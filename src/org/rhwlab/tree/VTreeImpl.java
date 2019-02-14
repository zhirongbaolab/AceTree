/*
 * Created on Jan 31, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.tree;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ScrollPane;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;
import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.utils.C;

import flanagan.plot.PlotGraph;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VTreeImpl {

    AceTree     iAceTree;
    NucleiMgr   iNucleiMgr;
    Vector      nuclei_record;
    AncesTree   iAncesTree;
    Hashtable   iCellsByName;
    Cell        iRoot;
    String      iShortName;
    String      iRootCell;

    int         iEndTime;
    boolean     iLeafNames;
    boolean     iRootName;
    int         iRootK;

    Vector      iCellLines;
    Vector      iLines;
    Vector      iNames;
    int         iKlast;
    int         iKincInit;
    String      iPSFileName;
    int         iLineWidth;
    int         iLineInc;
    int         iFontSize;
    float       iHue;

    Hashtable   iDaughters;

    TestCanvas  iTestCanvas;

    double []   iLimits;
    //int         iTop;
    double      iYScale;
    int         iYTranslate;


    public VTreeImpl() {
        initialize();
        iKincInit = YINC;
        iFontSize = MINFONTSIZE;
    }

    public void initialize() {
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        nuclei_record = iNucleiMgr.getNucleiRecord();
        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        //iIdentity = iNucleiMgr.getIdentity();
        iRoot = iAncesTree.getRoot();
        //iRoot = iNucleiMgr.getRoot(); //causes JTree selection problem!!!
        iRootName = true;
        iLeafNames = true;
        iEndTime = 200;
    }


    private String setParams(JTextField [] params) {
        String rootName = params[ROOTNAME].getText();
        String endTime = params[ENDTIME].getText();
        String minRed = params[MINRED].getText();
        String maxRed = params[MAXRED].getText();
        String yInc = params[YINCLOC].getText();
        String sHue = params[HUELOC].getText();
        Cell.setMinRed(Integer.parseInt(minRed));
        Cell.setMaxRed(Integer.parseInt(maxRed));
        iEndTime = Integer.parseInt(endTime);
        iKincInit = Integer.parseInt(yInc);
        iLineWidth = Integer.parseInt(params[LINEWIDTH].getText());
        iLineInc = Integer.parseInt(yInc);
        iHue = Float.parseFloat(sHue);
        return rootName;
    }

    private void prepareIt(String rootName) {
        iCellLines = new Vector();
        iLines = new Vector();
        iNames = new Vector();
        prepare(rootName);
    }


    public void showTree(String rootName, int endTime, int minRed, int maxRed) {
        iRootCell = rootName;
        iRootName = true;
        iLeafNames = true;
        Cell.setMinRed(minRed);
        Cell.setMaxRed(maxRed);
        iEndTime = endTime;
        iKincInit = 10;
        iLineWidth = 2;
        iLineInc = 5;
        iHue = 0;
        prepareIt(rootName);
        if (iTestCanvas != null) {
            iTestCanvas.dispose();
            iTestCanvas = null;
        }
        TestCanvas tc = new TestCanvas(rootName);
        tc.showIt();
        iTestCanvas = tc;

    }


    public void showTree(JTextField [] params, boolean labelRoot, boolean labelLeaves) {
        String rootName = setParams(params);
        iRootCell = rootName;
        iRootName = labelRoot;
        iLeafNames = labelLeaves;
        prepareIt(rootName);
        if (iTestCanvas != null) {
            iTestCanvas.dispose();
            iTestCanvas = null;
        }
        TestCanvas tc = new TestCanvas(rootName);
        tc.showIt();
        iTestCanvas = tc;
    }

    public void printTree(JTextField [] params, boolean labelRoot, boolean labelLeaves, String fileName, String dir) {
        String rootName = setParams(params);
        iRootName = labelRoot;
        iLeafNames = labelLeaves;
        iPSFileName = dir + C.Fileseparator + fileName; //params[PSFILE].getText();

        if (fileName.indexOf("png") >= 0) {
            iTestCanvas.captureImage(fileName, dir);
            return;
        }
        // else make a postscript file
        //Vector vSep = getSeparators(params[SEPARATORS].getText());
        Vector vSep = getSeparators("NONE");
        prepareIt(rootName);
        Vector v = packageIt();
        //String [] sa = {""};
        v = segregate(v, vSep);

        printIt(v);
    }

    private Vector getSeparators(String ss) {
        Vector v = new Vector();
        String [] sa = null;
        String [] sb = null;
        sb = new String[1];
        sb[0] = "";
        v.add(sb);
        if (ss.indexOf("NONE") == 0) {
            ;
        } else {
            sa = ss.split(",");
            for (int i=0; i < sa.length; i++) {
                if (sa[i].equals("P1")) {
                    v.add(0, SUBLINEAGES[0]);
                } else if(sa[i].equals("P2")) {
                    v.add(0, SUBLINEAGES[1]);
                } else if(sa[i].equals("P3")) {
                    v.add(0, SUBLINEAGES[2]);
                } else if(sa[i].equals("P2")) {
                    v.add(0, SUBLINEAGES[3]);
                } else if(sa[i].equals("EMS")) {
                    v.add(0, SUBLINEAGES[4]);
                } else {
                    sb = new String[1];
                    sb[0] = sa[i];
                    v.add(0, sb);
                }

            }
        }
        return v;
    }

    private static final String [][]  SUBLINEAGES = {
             {"P1", "EMS", "MS", "E", "P2", "C", "P3", "D", "P4", "Z"}
            ,{"P2", "C", "P3", "D", "P4", "Z"}
            ,{"P3", "D", "P4", "Z"}
            ,{"P4", "Z"}
            ,{"EMS", "MS", "E"}

    };

    @SuppressWarnings("unused")
	private Vector segregate(Vector vIn, Vector vSep) {
        Vector va = new Vector();
        Vector v = null;
        String pattern = null;
        Object patternObj;
        Vector patternVec = null;
        Vector vKill = null;
        for (int j=0; j < vSep.size(); j++) {
            println("segregate j=" + j);
            String [] sa = (String [])vSep.get(j);
            println("segregate sa=" + sa);
            v = new Vector();
            vKill = new Vector();
            Enumeration e = vIn.elements();
            while(e.hasMoreElements()) {
                String s = (String)e.nextElement();
                println("segregate s=" + s);
                for (int i=0; i < sa.length; i++) {
                    pattern = sa[i];
                    if (s.indexOf(COMMENT + pattern) >= 0) {
                        v.add(s);
                        println("segregate: " + s);
                        vKill.add(s);
                    } else {
                        println("segregate not using: " + s);
                    }
                }
            }
            Enumeration ee = vKill.elements();
            while (ee.hasMoreElements()) {
                vIn.remove(ee.nextElement());

            }
            v.add("showpage");
            va.add(v);
        }
        println("segregate va.size: " + va.size());
        v = (Vector)va.get(0);
        println("segregate v.size: " + v.size());
        return va;

    }


    public Vector packageIt() {
        iLimits = findYLimits();
        double scale = 700./(iLimits[0] - iLimits[1]);
        iYScale = Math.ceil(scale*100)/100.;
        iYScale = Math.min(iYScale, 1.);
        //iTop = (int)(iYScale*iLimits[0]);
        //iYScale = 1;
        //iTop = TOP;
        iYTranslate = (int)iLimits[0] - TOP + 100; //(int)Math.ceil(iYScale * iLimits[1]);
        //if (iYScale == 1.) iYTranslate = 0;
        Vector v = new Vector();
        int offset = 100;
        for(int i=0; i < iCellLines.size(); i++) {
            Object o = iCellLines.get(i);
            //println("packageIt: " + i + CS + o);
            if (o instanceof CellLine) {
                CellLine p = (CellLine)o;
                int x1 = p.c.getTime() + offset;
                int x2 = x1;
                if (p.y1 == p.y2) x2 = p.c.getEnd() + offset;
                v.add(prepareLine(x1, p.y1, x2, p.y2, p.c));
                //outputVector(v);
            } else {
                Name p = (Name)o;
                v.add(prepareString(p.c, p.name, p.x, p.y));
                //outputVector(v);

            }
        }
        //println("packageIt v.size: " + v.size());
        return v;
    }

    @SuppressWarnings("unused")
	public double [] findYLimits() {
        int ymax = 0;
        int ymin = Integer.MAX_VALUE;
        int offset = 100;
        for(int i=0; i < iCellLines.size(); i++) {
            Object o = iCellLines.get(i);
            //println("packageIt: " + i + CS + o);
            if (o instanceof CellLine) {
                CellLine p = (CellLine)o;
                int y = Math.max(p.y1, p.y2);
                ymax = Math.max(ymax, y);
                y = Math.min(p.y1, p.y2);
                ymin = Math.min(ymin, y);
            } else {
                Name p = (Name)o;
                ymax = Math.max(ymax, p.y);
                ymin = Math.min(ymin, p.y);
            }
        }
        double [] rtn = new double[2];
        rtn[0] = ymax;
        rtn[1] = ymin;

        System.out.println("findYLimits, " + rtn[0] + CS + rtn[1]);
        return rtn;
    }

    public void printIt(Vector vIn) {
        PrintWriter pw = null;
        try {
            FileOutputStream fs = new FileOutputStream(iPSFileName);
            pw = new PrintWriter(fs);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return;
        }
        Vector v = null;
        String s = null;
        for(int i=0; i < PSHEADER.length; i++) pw.println(PSHEADER[i]);
        pw.println(iLineInc + " scalefont setfont");
        pw.println(iLineWidth + " setlinewidth");
        //System.out.println("printIt, " + iLimits[0] + ", " + iLimits[1]);
        System.out.println("printIt, " + iYScale + ", " + iYTranslate);
        pw.println(1 + " " + iYScale + " scale");
        pw.println(0 + " " + iYTranslate + " translate");

        for (int i=0; i < vIn.size(); i++) {
            v = (Vector)vIn.get(i);
            for (int j=0; j < v.size(); j++) {
                s = (String)v.get(j);
                pw.println(s);
                //System.out.println("printIt: " + s);
            }
            //pw.println(COMMENT + "\n\n\n" + COMMENT);
        }
        pw.flush();
        pw.close();

    }

    private static final String [] PSHEADER = {
    "%!"
    ,"/n {newpath} bind def"
    ,"/m {moveto} bind def"
    ,"/l {lineto} bind def"
    ,"/r {rlineto} bind def"
    ,"/c {setrgbcolor} bind def"
    ,"/cp {closepath} bind def"
    ,"/s {stroke} bind def"
    ,"/x {moveto setrgbcolor lineto closepath stroke} bind def"
    ,"/sg {setgray} bind def"
    ,"/Courier findfont "
//    ,"/Courier findfont 1 scalefont setfont"
    };


    private String prepareLine(int x1, int y1, int x2, int y2, Cell c) {
        Vector cellData = c.getCellData();
        //System.out.println("prepareLine: " + c.getName() + CS + cf[0] + CS + cf[1] + CS + cf[2]);
        DecimalFormat df = new DecimalFormat("#.##");
        int top = TOP;
        String s = "";
        StringBuffer sb = new StringBuffer();
        //s = x1 + SP + (top - y1) + MOVETO;
        //sb.append(SP + s);
        int y = top - y1;

        if (x1 == x2) {
            sb = new StringBuffer(NEWPATH);
            s = SP + x1 + SP + (top - y1) + MOVETO;
            sb.append(SP + s);
            Color cc = null;
            try {
                //cc = c.getColor(0);
                cc = getColor(cellData, 0);
                //g.setColor(cc);
            } catch(Exception e) {
                cc = Color.BLACK;
                //g.setColor(Color.BLACK);
            }
            //Color cc = c.getColor(x - 1);
            float [] cf = cc.getColorComponents(null);
            s = SP + df.format(cf[0]) + SP + df.format(cf[1]) + SP + df.format(cf[2]);
            s = s + COLOR;
            s = s + SP + 0 + SP + (y1 - y2) + RLINETO;
            sb.append(SP + s);
            sb.append(SP + CLOSEPATH + SP + STROKE + COMMENT + c.getName());
            //System.out.println("prepareLine1: " + sb.toString());
            return sb.toString();
        } else {

            for (int x=x1; x <= x2; x++) {
                Color cc = null;
                try {
                    //cc = c.getColor(x - x1);
                    cc = getColor(cellData, x - x1);
                    //g.setColor(cc);
                } catch(Exception e) {
                    cc = Color.BLACK;
                    //g.setColor(Color.BLACK);
                }
                //Color cc = c.getColor(x - 1);
                float [] cf = cc.getColorComponents(null);
                s = NEWPATH;
                s = s + SP + x + SP + y;
                s = s + SP + df.format(cf[0]) + SP + df.format(cf[1]) + SP + df.format(cf[2]);
                s = s + SP + (x + 1) + SP + y + SP + X;
                sb.append(SP + s);
            }
        }
        sb.append(COMMENT + c.getName());
        //System.out.println("prepareLine2: " + sb.toString());
        return sb.toString();
    }

    private String prepareLineX(int x1, int y1, int x2, int y2, Cell c) {

        //System.out.println("prepareLine: " + c.getName() + CS + cf[0] + CS + cf[1] + CS + cf[2]);
        DecimalFormat df = new DecimalFormat("#.##");
        int top = TOP;
        String s = "";
        StringBuffer sb = new StringBuffer(NEWPATH);
        s = x1 + SP + (top - y1) + MOVETO;
        sb.append(SP + s);
        int y = y1 - y2;

        if (x1 == x2) {
            s = 0 + SP + y + RLINETO;
            sb.append(SP + s);
        } else {

            for (int x=1; x <= x2 - x1; x++) {
                Color cc = null;
                try {
                    cc = c.getColor(x - 1);
                    //g.setColor(cc);
                } catch(Exception e) {
                    cc = Color.BLACK;
                    //g.setColor(Color.BLACK);
                }
                //Color cc = c.getColor(x - 1);
                float [] cf = cc.getColorComponents(null);
                s = df.format(cf[0]) + SP + df.format(cf[1]) + SP + df.format(cf[2]) + COLOR;
                s = s + SP + 1 + SP + y + SP + RLINETO;
                sb.append(SP + s);
            }
        }
        sb.append(SP + CLOSEPATH + SP + STROKE + COMMENT + c.getName());
        return sb.toString();
    }

    private String prepareString(Cell c, String n, int x, int y) {
        Vector cellData = c.getCellData();
        int top = TOP;
        DecimalFormat df = new DecimalFormat("#.##");
        String s = null;
        //Color cc = c.getLastColor();
        Color cc = getColor(cellData, cellData.size() - 1);
        float [] cf = cc.getColorComponents(null);
        s = df.format(cf[0]) + SP + df.format(cf[1]) + SP + df.format(cf[2]) + COLOR;
        s += SP + x + SP + (top - y) + MOVETO + SP + LEFT + n + RITE + SHOW + COMMENT + n;
        return s;
    }



    private static final String
         MOVETO = " m"
        ,LINETO = " l"
        ,RLINETO = " r"
        ,NEWPATH = "n"
        ,COLOR = " c"
        ,CLOSEPATH = "cp"
        ,X = "x"
        ,FILL = "fill"
        ,STROKE = "s"
        ,SHOW = " show"
        ,COMMENT = " %"
        ,SP = " "
        ,LEFT = "("
        ,RITE = ")"
        ;

    public static final int
         ROOTNAME = 0
	,ENDTIME = 1
        ,MINRED = 2
        ,MAXRED = 3
        ,YINCLOC = 4
        ,LINEWIDTH = 5
        ,HUELOC = 6
        //,PSFILE = 6
        //,SEPARATORS = 6
        ;


    private Cell getNewTree() {
        AncesTree ances = new AncesTree(null, iNucleiMgr, iAceTree.getConfig().getImageConfig().getStartingIndex(), iEndTime);
        //AncesTree ances = new AncesTree(null, iNucleiMgr, 1, iEndTime);
        iCellsByName = ances.getCellsByName();
        return ances.getRoot();
    }

    @SuppressWarnings({ "unused", "null" })
	public void prepare(String rootName) {
        println("Analysis5.prepare: " + rootName);
        Hashtable doneHash = new Hashtable();
        iDaughters = new Hashtable();
        String s = "";
        int kStart = 50;
        iKlast = 0;
        int k = kStart;
        //int kIncInit = YINC;
        int kInc = iKincInit;
        int nameInterval = Math.max(iFontSize/kInc, 1);
        int xOffset = 100;
        getNewTree();
        Cell root = (Cell)iCellsByName.get(rootName);


        if (root == null) {
            System.out.println("rootName not found: " + rootName);
            return;
        }
        int rootLevel = root.getLevel();
        int rootDepth = root.getDepth();
        println("depthOf: " + rootName + CS + rootLevel + CS + rootDepth);
        Cell x = null;
        Cell y = null;
        Cell parent = null;
        Vector v = getLeaves(rootName);
        //println("prepare, num leaves = " + v.size());
        //for (int i=0; i < v.size(); i++) {
        //    println("prepare, " + i + CS + ((Cell)v.get(i)).getName());
        //}
        boolean leafPass = true;
        Hashtable allCells = getAllCells(rootName);
        Vector p = new Vector();
        for(int i=0; i <= root.getDepth(); i++) {
            println("\nfor i: " + i);
            Vector readds = new Vector();
            if (i > 0) {
                v = (Vector)p.clone();
                p = new Vector();
                leafPass = false;
            }
            s  = "v size: " + v.size(); println(s);
            //showParents(v);
            //Enumeration e = v.elements();
            //while (e.hasMoreElements()) {
            for (int j=0; j < v.size(); j++) {
                //println("\nfor j: " + j + CS + i);
                //x = (Cell)e.nextElement();
                x = (Cell)v.get(j);
                if (allCells.get(x) == null) continue;
                if (x == iRoot) continue;
                println("\nNOW CONSIDERING: " + x.getName());
                //if (j < v.size() - 1) y = (Cell)v.get(j + 1);
                parent = (Cell)x.getParent();
                int xStart = x.getTime() + xOffset;
                if (parent == null) parent = x;
                //println("parent: " + parent.getName() + CS + iRoot.getName());
                //if (parent == iRoot) continue;
                //println(parent.showStuff());
                if (parent.getName() != "P") {
                    // need to know that the sister of this cell is also in the list
                    boolean itsInThere = false;
                    Cell sister = null;
                    int childCount = parent.getChildCount();
                    if (childCount == 1) {
                        System.out.println("prepare, debug cell=" + x.getName());
                        itsInThere = false;
                    } else {
                        sister = (Cell)parent.getChildAt(1);
                        if (x == sister) sister = (Cell)parent.getChildAt(0);
                        itsInThere = v.contains(sister);
                        println("sister: " + sister.getName() + CS + itsInThere + CS + (i > 0));
                    }
                    if (!itsInThere && i > 0) {
                        // since the sister is not present, I will not be able to draw the line to the pair
                        // so put the cell into the parent vector
                        //println("not in there: " + sister.getName() + " so add: " + x.getName());
                        println("add parent: " + x.getName() + " because sister not present " ); //+ sister.getName() );
                        p.add(x);
                        //println("add parent: " + parent + " because sister not present " + sister.getName() );
                        //p.add(parent);
                        //continue;
                    }

                    //boolean firstOfPair = parent.getChildAt(0) == x;
                    //firstOfPair = firstOfPair && parent.getChildCount() > 1;
                    //boolean soloLeaf = (i == 0) && ((x.getLevel() - rootLevel) < rootDepth);
                    boolean soloLeaf = false; // = (i == 0) && (x.getLevel() != y.getLevel());
                    if (i == 0 && itsInThere) {
                        if (x.getDepth() < sister.getDepth()) soloLeaf = true;
                    }
                    //if (i == 0) println("siblings: " + x.getName() + CS + (x.getLevel() - rootLevel) + CS + soloLeaf + CS + rootLevel);
                    if (soloLeaf) {
                        //println("add parent: " + x.getName() + " because of soloLeaf ");
                        //p.add(x);
                        println("add parent: " + parent + " because of soloLeaf " + x.getName());
                        p.add(parent);
                        //Daughter d3 = new Daughter(x, xStart, k);
                        //Daughters da3 = new Daughters(d3, null);
                        //iDaughters.put(x, da3);
                        //println("soloLeaf creating daughters at " + x.getName() + " with daughter " + x.getName());

                    }
                }
                Daughters myD = null;
                Daughter da = null;
                Daughter db = null;
                if (i > 0) {
                    try {
                        myD = (Daughters)iDaughters.get(x);
                        da = myD.da[0];
                        db = myD.da[1];
                        println("available daughters: " + da + CS + db);
                        if (da != null && db != null) {
                            k = (da.y + db.y)/2;
                            if (doneHash.get(x) == null) {
                                doneHash.put(x, x);
                                Cell o = (Cell)allCells.remove(x);
                                if (o == null) {
                                    println("REMOVING NON EXISTANT CELL: " + o.getName());
                                }
                                println("606 drawing from " + x.getName() + CS + da + CS + db + CS + k + CS + iKlast);
                                handleCellLine(x, k, k);
                                if (x.getName().equals(root.getName())) iRootK = k;
                                iKlast = Math.max(k, iKlast);
                            }

                        }
                        else if (da == null || db == null) {
                            if (!readds.contains(x)) {
                                readds.add(x);
                                v.add(x); //readd it to the vector for later consideration
                                println("readded: " + x.getName());
                            } else {
                                p.add(x);
                                println("add parent: " + x.getName() + " because I cannot process it this time");
                            }
                            continue;
                        }
                        //else if (db != null) k = db.y;
                        //else k = da.y;
                    } catch(NullPointerException npe) {
                        println("NULLPOINTER: " + x.getName() + CS + myD);

                    }
                    //println("parent&daughters: " + x.getName() + CS + da + CS + db);
                } else {
                    if (doneHash.get(x) == null) {
                        println("drawing from " + x.getName() + CS + da + CS + db + CS + k);
                        handleCellLine(x, k, k);
                        iKlast = Math.max(k, iKlast);
                        doneHash.put(x, x);
                        Cell o = (Cell)allCells.remove(x);
                        if (o == null) {
                            println("REMOVING NON EXISTANT CELL: " + o.getName());
                        }
                    }
                }
                Daughter d1 = new Daughter(x, xStart, k);
                Daughters ds = (Daughters)iDaughters.get(parent);
                if (ds == null) {
                    ds = new Daughters(d1, null);
                    iDaughters.put(parent, ds);
                    p.add(parent);
                    println("add parent: " + parent.getName() + " create daughters with daughter " + d1.d.getName());
                } else {
                        ds.addSecond(d1);
                        println("adding to daughters of parent " + parent.getName() + " with daughter " + d1.d.getName());
                        Daughter d2 = ds.getFirst();
                        if (d2 != null) {
                            println("drawing connector between " + x.getName() + CS + d2.d.getName());
                            //handleLine(xStart, k, xStart, d2.y);
                            handleCellLine(x, k, d2.y);
                        }
                }
                if (i == 0 && j % nameInterval  == 0 && iLeafNames) {
                    handleString(x, iEndTime + xOffset + 10, k + 3);
                    //handleString(x, iNucleiMgr.getEndingIndex() + xOffset + 10, k);
                }
                //if (i == 0) handleString(x, x.getEnd() + xOffset + 10, k);
                k += iKincInit * (parent.getLeafCount() - 1);
            }
            s = "parentSize: " + p.size(); println(s);
            //showParents(p);
        }
        if (iRootName) {
            String ss = root.getName();
            println("prepare, rootName, " + root.getTime() + CS + xOffset);
            handleString(root, xOffset + root.getTime() - ss.length() * 7, iRootK + 3);
        }

        println("check all cells: " + allCells.size());
        println("iKlast: " + iKlast);
        //iWasDrawn = true;
    }

    private void println(String s) {
        //System.out.println(s);
    }

    private void handleCellLine(Cell c, int y1, int y2) {
        println("handleCellLine, " + c.getName() + CS + y1 + CS + y2);

        CellLine cLine = new CellLine(c, y1, y2);
        iCellLines.add(cLine);

    }

    private void handleString(Cell c, int x, int y) {
        println("handleString, " + c.getName() + CS + x + CS + y);
        Name name = new Name(c, x, y);
        iCellLines.add(name);

    }

    private Hashtable getAllCells(String rootName) {
        //System.out.println("getAllCells: " + rootName);
        Hashtable h = new Hashtable();
        Cell x = (Cell)iCellsByName.get(rootName);
        Enumeration e = x.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            Cell cn = (Cell)e.nextElement();
            //System.out.println("getAllCells next: " + cn.getName());
            h.put(cn, cn);
        }
        return h;
    }


    @SuppressWarnings("unused")
	private Vector getLeaves(String rootName) {
        //if (1 == 1) return fakeLeaves(rootName);
        Vector v = new Vector();
        Cell d = (Cell)iCellsByName.get(rootName);
        int last = d.getLeafCount();
        int k = 0;
        Cell x = (Cell)d.getFirstLeaf();
        for (int i=0; i < last; i++) {
            //System.out.println("getLeaves: " + x.getName());
	    int endTime = x.getEndTime();
            v.add(0, x);
            x = (Cell)x.getNextLeaf();
            if (x == null) break;
        }
        return v;
    }

    private Vector fakeLeaves(String rootName) {
        Vector v = new Vector();
        Cell c = null;
        c = (Cell)iCellsByName.get("Epr");
        v.add(0, c);
        c = (Cell)iCellsByName.get("Epl");
        v.add(0, c);
        c = (Cell)iCellsByName.get("Ear");
        v.add(0, c);
        c = (Cell)iCellsByName.get("Eal");
        v.add(0, c);
        return v;
    }


    private Color getColor(Vector cellData, int k) {
        if (k < 0 || k > cellData.size()) return Color.black;
        CellData cd = (CellData)cellData.elementAt(k);
        int red = cd.iNucleus.rweight;
        float frac = ((float)red - (float)Cell.cMin)/((float)Cell.cMax - (float)Cell.cMin);
        frac = Math.min(frac, 1f);
        frac = Math.max(frac, 0f);
        float hue = iHue;
        float sat = 1;
        int col = Color.HSBtoRGB(hue, sat, frac);
        //println("getColor, " + c.getName() + CS + k + CS + red + CS + frac);
        return new Color(col);
    }



    class TestCanvas extends Canvas {
        private JFrame  iFrame;
        private boolean iWasDrawn;
        private String rootName;
        int height;
        int width;
        public TestCanvas(String rName) {
            rootName = rName;
            setBackground(Color.white);
            int height = iKlast + 100;
            width = 500;
            setSize(new Dimension(width, height));
            MouseHandler mh = new MouseHandler(this);
            addMouseMotionListener(mh);
            addMouseListener(mh);
        }

        public void dispose() {
            iFrame.dispose();
            iFrame = null;
        }

        private void println(String s) {
            //if (!iWasDrawn) System.out.println(s);
        }

        @Override
		@SuppressWarnings("unused")
		public void paint(Graphics g) {
            g.setColor(Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());
            int offset = OFFSET;
            Graphics2D g2 = (Graphics2D)g;
            int width = LINEWIDTH;
            g2.setStroke(new BasicStroke(iLineWidth));
            for(int i=0; i < iCellLines.size(); i++) {
                Object o = iCellLines.get(i);
                if (o instanceof CellLine) {
                    CellLine p = (CellLine)o;
                    int x1 = p.c.getTime() + offset;
                    int x2 = x1;
                    if (p.y1 == p.y2) x2 = p.c.getEnd() + offset;
                    drawLine(g2, x1, p.y1, x2, p.y2, p.c);
                    //g.drawLine(x1, p.y1, x2, p.y2);
                } else {
                    Name p = (Name)o;
                    //g.drawString(p.name, p.x, p.y);
                    drawString(g2, p.x, p.y, p.c);
                }
            }
        }

        public void captureImage(String fileName, String dir) {
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            paint(g);
            File f = new File(dir + "/" + fileName);
            try {
                ImageIO.write(image, "png", f);
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
            makeWebPage(fileName, dir);
        }

        @SuppressWarnings("resource")
		public void makeWebPage(String fileName, String dir) {
            String s = fileName.substring(0, fileName.length() - 4);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(dir + "/" + s + ".html", false);
            } catch(FileNotFoundException fnfe) {
                fnfe.printStackTrace();
                return;
            }
            PrintWriter pw = new PrintWriter(fos, true);
            pw.println("<html>");
            pw.println("<head><title>" + s + "</title></head>");
            pw.println("<body>");
            pw.println("<img src=\"" + s + ".png\">");
            pw.println("</body>");
            pw.println("</html>");

        }



        public void showIt() {
            iFrame = new JFrame();
            JFrame jf = iFrame;
            JFrame.setDefaultLookAndFeelDecorated(false);
            String s = iAceTree.getConfig().getConfigFileName();
            File cf = new File(s);
            s = cf.getName();
            iShortName = s;
            jf.setTitle(s);
            ScrollPane sp = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
            //JScrollPane sp = new JScrollPane();
            //sp.setToolTipText("tool tip text");
            sp.add(this);
            jf.getContentPane().add(sp, BorderLayout.CENTER);
            jf.setSize(new Dimension(600, 500));
            jf.setVisible(true);

        }

        private Cell findIt(int xx, int yy) {
            CellLine cL = null;
            Cell c = null;
            int t = xx - OFFSET;
            for (int i=0; i < iCellLines.size(); i++) {
                Object o = iCellLines.get(i);
                if (!(o instanceof CellLine)) continue;
                cL = (CellLine)o;
                if (yy < cL.y1 - DELTAY || yy > cL.y2 + DELTAY) continue;
                c = cL.c;
                if (t < c.getTime() - DELTAT || t > c.getEnd() + DELTAT) continue;
                break;
            }
            return c;
        }

        private void drawLine(Graphics g, int x1, int y1, int x2, int y2, Cell c) {
            //println("Analysis5.drawLine: " + c.getName() + CS + x1 + CS + y1 + CS + x2 + CS + y2);
            Vector cellData = c.getCellData();
            if (x1 == x2) {
                try {
                    Color cc = getColor(cellData, 0);
                    g.setColor(cc);
                } catch(Exception e) {
                    g.setColor(Color.BLACK);
                }
                //g.setColor(cc);
                g.drawLine(x1, y1, x2, y2);
            } else {
                int k = 0;
                for (int x=x1; x < x2; x++) {
                    try {
                        Color cc = getColor(cellData, k++);
                        //System.out.println("drawLine: " + c.getName() + CS + cc);
                        g.setColor(cc);
                    } catch(Exception e) {
                        g.setColor(Color.BLACK);
                    }
                    g.drawLine(x, y1, x + 1, y2);
                }
            }
        }





        private void drawLine(Graphics g, int x1, int y1, int x2, int y2, Cell c, boolean bogus) {
            //System.out.println("Analysis5.drawLine: " + c.getName() + CS + x1 + CS + y1 + CS + x2 + CS + y2);
            if (x1 == x2) {
                try {
                    Color cc = c.getColor(0);
                    g.setColor(cc);
                } catch(Exception e) {
                    g.setColor(Color.BLACK);
                }
                //g.setColor(cc);
                g.drawLine(x1, y1, x2, y2);
            } else {
                int k = 0;
                for (int x=x1; x < x2; x++) {
                    try {
                        Color cc = c.getColor(k++);
                        //System.out.println("drawLine: " + c.getName() + CS + cc);
                        g.setColor(cc);
                    } catch(Exception e) {
                        g.setColor(Color.BLACK);
                    }
                    g.drawLine(x, y1, x + 1, y2);
                }
            }
        }

        private void drawString(Graphics g, int x, int y, Cell c) {
            //System.out.println("drawString: " + c + CS + x + CS + y);
            Vector cellData = c.getCellData();
            Color cc = getColor(cellData, cellData.size() - 1);
            //Color cc = c.getLastColor();
            g.setColor(cc);
            Font f = new Font("Courier", 0, 10 /*iLineInc*/);
            g.setFont(f);
            g.drawString(c.getName(), x, y);
        }

        private void drawString(Graphics g, int x, int y, Cell c, boolean bogus) {
            //System.out.println("drawString: " + c + CS + x + CS + y);
            Color cc = c.getLastColor();
            g.setColor(cc);
            Font f = new Font("Courier", 0, 10 /*iLineInc*/);
            g.setFont(f);
            g.drawString(c.getName(), x, y);
        }



        class MouseHandler extends MouseInputAdapter {

            public MouseHandler(Object o) {
                super();
            }

            @Override
			public void mouseMoved(MouseEvent e) {
            }

            public void notifyAceTree(Cell c, int time) {
                Vector v = new Vector();
                v.add("InputCtrl1");
                v.add(String.valueOf(time));
                v.add(c.getName());
                iAceTree.controlCallback(v);
            }

            @Override
			public void mouseClicked(MouseEvent e) {
                //System.out.println("TreeCanvas2.mouseClicked: " + e.getX() + CS + e.getY() + CS);
                Cell cs = findIt(e.getX(), e.getY());
                //System.out.println("clicked: " + cs);
                if (cs != null) {
                    int intTime = e.getX() - OFFSET;
                    int button = e.getButton();



                    if (button == MouseEvent.BUTTON1) {
                        iAceTree.restoreTree(iShortName);
                        notifyAceTree(cs, intTime);
                    }
                    else if (button == MouseEvent.BUTTON3) {
                        iAceTree.restoreTree(iShortName);
                        notifyAceTree(cs, cs.getEndTime());
                    }
                    else if (button == MouseEvent.BUTTON2) {
                        iAceTree.restoreTree(iShortName);
                        notifyAceTree(cs, cs.getEndTime());
                        plottest(cs);

                    }
                }
            }
        }


    }

    private void plottest(Cell c) {
        // build up Vector of Cells from root to this cell
        Vector path = new Vector();
        path.add(0, c);
        Cell parent = c;
        while (!parent.getName().equals(iRootCell)) {
            parent = (Cell)parent.getParent();
            //System.out.println("plottest, " + parent.getName() + CS + iRoot.getName());
            path.add(0, parent);
        }
        Vector yVec = new Vector();
        for (int j=0; j < path.size(); j++) {
            Cell cc = (Cell)path.get(j);
            Vector cellData = cc.getCellData();
            for (int n=0; n < cellData.size(); n++) {
                CellData cd = (CellData)cellData.get(n);
                yVec.add(new Double(cd.iNucleus.rweight));
            }
        }
        double [] xxdata = new double[yVec.size()];
        double [] yydata = new double[yVec.size()];
        for (int i=0; i < yVec.size(); i++) {
            //CellData cd = (CellData)cellData.get(i);
            xxdata[i] = i;
            yydata[i] = ((Double)yVec.get(i)).doubleValue();
        }

        PlotGraph pg = new PlotGraph(xxdata, yydata);
        pg.setCloseChoice(2);
        pg.rescaleY(.5);
        pg.rescaleX(.5);
        pg.setPoint(0);
        pg.setGraphTitle(c.getName() + CS + iShortName);

        pg.plot();
    }

    private class CellLine {
        Cell c;
        int y1;
        int y2;

        private CellLine(Cell cc, int yy1, int yy2) {
            c = cc;
            y1 = yy1;
            y2 = yy2;
        }
    }

    private class Name {
        Cell c;
        String name;
        int x;
        int y;

        private Name(Cell cc, int xx, int yy) {
            c = cc;
            name = c.getName();
            x = xx;
            y = yy;
        }
    }

    private class Daughter {
        Cell d;
        int  x;
        int  y;
        private Daughter(Cell c, int xx, int yy) {
            d = c;
            x = xx;
            y = yy;
        }

        @Override
		public String toString() {
            String s = "";
            s += d.getName();
            s += CS + x + CS + y;
            return s;
        }
    }
    private class Daughters {
        Daughter [] da;

        private Daughters(Daughter d1, Daughter d2) {
            da = new Daughter[2];
            da[0] = d1;
            da[1] = d2;
        }

        private boolean addSecond(Daughter d2) {
            if (da[0].d.getName().equals(d2.d.getName())) return false;
            da[1] = d2;
            return true;
        }

        private Daughter getFirst() {
            return da[0];
        }
    }


    private static final int
         //TOP = 1400
         TOP = 8000
        ,OFFSET = 100
        ,DELTAY = 5
        ,DELTAT = 1
        ,YINC = 20
        ,YOFFSET = 500
        ,MINFONTSIZE = 10
    ;


    private static final String CS = ", ";

    //public static void main(String[] args) {
    //}


}
