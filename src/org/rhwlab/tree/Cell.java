/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */

package org.rhwlab.tree;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

import org.rhwlab.acetree.NucUtils;
import org.rhwlab.snight.Nucleus;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 * Representation of a cell or nucleus in the embryo
 *
 * @author biowolp
 * @version 1.0 January 3, 2005
 */

/* 
 * For drawing bookmarked cells,
 * give referene to bookmark listmode to each cell
 * cell checks to see if it is bookmarked
 * draws accordingly
 * 
 * */

public class Cell extends DefaultMutableTreeNode {
    private String iName;
    private int iTimeIndex;
    private int iPlane;
    private int iX;
    private int iY;
    private double iDia;
    private int iEndTime;
    private int iEndFate;
    private String iHashKey;
    private Hashtable iCellXHash;
    public int iEndingIndex;
    private int iLateTime;
    private Vector iCellData;

    // drawing related variables and static functions
    public double ysc;
    public int yStartUse;
    public int xUse;
    private int iXmax;
    private Vector iCellsDrawn;
    
    // Reference to bookmark list
    private DefaultListModel iBookmarkListModel;
    
    public void setBookmarkListModel(ListModel listModel) {
    	iBookmarkListModel = (DefaultListModel)listModel;
    }

    public static int cMin = 25000;
    public static int cMax = 35000;
    private static double cScale;
    private static int iEndingIndexS;
    public static void setEndingIndexS(int endingIndex) {
        iEndingIndexS = endingIndex;
    }
    public static int getEndingIndex() {
        return iEndingIndexS;
    }
    public static int xsc;
    public static void setXScale(int xScale) {
        xsc = xScale;
    }

    public void setLateTime(int time) {
        iLateTime = time;// + iTimeIndex % 10;
    }

    public static void setMinRed(int min) {
        cMin = min;
        setRedScale();
    }

    public static void setMaxRed(int max) {
        cMax = max;
        setRedScale();
    }

    private static void setRedScale() {
        cScale = CMAP.length/((double)(cMax - cMin));
    }

    public static void setHeight(double height) {
    }

    /**
     * @param name String name of Cell
     * also stored in the parent as userObject
     */
    public Cell(String  name) {
        super(name);
        iName = (String)userObject;
        iEndingIndex = iEndingIndexS;
        iLateTime = iEndingIndex;
        iCellData = new Vector();
        cScale = CMAP.length/((double)(cMax - cMin));
    }

    public Cell(Cell c) {
        iName = c.iName;
        iEndingIndex = c.iEndingIndex;
        iLateTime = c.iLateTime;
        iCellData = c.iCellData;
        iTimeIndex = c.iTimeIndex;
        iPlane = c.iPlane;
        iX = c.iX;
        iY = c.iY;
        iDia = c.iDia;
        iEndTime = c.iEndTime;
        iEndFate = c.iEndFate;
        iHashKey = c.iHashKey;
        iCellXHash = c.iCellXHash;
        Cell p = (Cell)c.getParent();
        setParent(p);
        Cell d1 = (Cell)c.getChildAt(0);
        insert(d1, 0);

        println("Cell, " + this);
    }

    public Cell(String name, int endingIndex) {
        this(name);
        iEndingIndex = endingIndex;
    }

    public Cell(String name, int endingIndex, int startTime) {
    	this(name, endingIndex);
    	iTimeIndex = startTime;
    }

    public Color getColor(int i) {
        //System.out.println("Cell.getColor: " + getName() + CS + i);
        CellData cd = (CellData)iCellData.elementAt(i);
        int red = cd.iNucleus.rweight;
        return getTheColor(getDiscrete(red));
    }

    public Color getLastColor() {
        CellData cd = (CellData)iCellData.lastElement();
        int red = cd.iNucleus.rweight;
        return getTheColor(getDiscrete(red));
    }

    private Color getColor(int i, Vector v) {
        // next line is patch attempt for "canonical trees"
        if (v.size() == 0) 
        	return getTheColor(getDiscrete(0));
        CellData cd = (CellData)v.elementAt(i);
        int red = cd.iNucleus.rweight;
        return getTheColor(getDiscrete(red));
    }

    public void paintLine(Graphics g, int x1, int y1, int x2, int y2){
        Graphics2D g2d = (Graphics2D)g;
        int width = LINEWIDTH;
        g2d.setStroke(new BasicStroke(width));
        g2d.drawLine(x1, y1, x2, y2);
    }

    private void drawColoredLine(Graphics g, Cell c, int x1, int y1, int x2, int y2) {
        //System.out.println("\ndrawColoredLine entered: " + iName + CS + c.getName() + CS + x1
        //       + CS + y1 + CS + x2 + CS + y2 + CS + ysc + CS + (y2 - y1)/ysc);
        Vector use;
        if (!iName.equals(c.getName())) {
            use = c.iCellData;
        } else {
            use = iCellData;

        }
        int useSize = use.size();
        // If drawing a horizontal line
        if (x1 != x2) {
            c.getParent();
            // Retrieves green color
            Color color = getColor(use.size() - 1, use);
            g.setColor(color);
            g.drawLine(x1, y1, x2, y2);
        }
        // If drawing a vertical line
        else {
            int range = (int)Math.round((y2 - y1)/ysc);
            int k = Math.min(range, useSize);
            // attempt a patch to handle canonical trees without
            // having them create iCellData
            if (k == 0) {
                k = range;
                useSize = k;
            }
            // end of patch attempt
            if (k > 0) {
                int y2i = y1;
                int y10 = y1;
                for (int i=0; i < k; i++) {
                	Color color = c.getColor(i, use);
                	// Use another color for bookmarked cells
                	if (iBookmarkListModel != null) {
                		if (iBookmarkListModel.contains(c.getName())) {
                			//System.out.println(c.getName()+" bookmarked.");
                			color = Color.MAGENTA;
                		}
                	}
                    //System.out.println("drawColoredLine: " + i + CS + k + CS +c.iCellData.size() + CS + c.getName() + CS + useSize + CS + use.elementAt(i));
                    g.setColor(color);
                    y2i = y10 + (y2 - y10) * (i + 1) / k;
                    paintLine(g, x1, y1, x2, y2);
                    y1 = y2i;
                }
            }
        }
        g.setColor(Color.BLACK);
    }


    // Used to draw root of tree with the yellow segment
    public void draw(Graphics g, int w, int h, int frameWidth, Hashtable cHash) {
        //System.out.println("draw1 " + this.getName());
        iCellsDrawn = new Vector();
        iCellXHash = cHash;
        int rootStart = iTimeIndex;
        double height = (iLateTime - iTimeIndex);
        //double height = 500. - iTimeIndex;
        //double height = iEndingIndex - iTimeIndex;
        //System.out.println("Cell.draw: " + iTimeIndex + CS + iEndingIndex + CS + iLateTime);
        //height = 800;
        //System.out.println("draw cHeight=" + cHeight);
        ysc = (h - START1 - BORDERS)/height; //*(cHeight-100)/600;
        iXmax = xsc;
        yStartUse = START1;
        
        // Draws yellow root segment of lineage tree
        xUse = draw(g, h, xsc + 20, yStartUse, this, cHash, rootStart);
        g.fillOval(xUse-2, START1-2, 4, 4);
        g.drawString(this.toString(), xUse + 5, START1);
        fillInHash(this, cHash);
        g.setColor(Color.YELLOW);
        g.drawLine(xUse, START0, xUse, START1);
        g.setColor(Color.BLACK);
        showScale(g, h - START1 - BORDERS, frameWidth);
        //drawCellNames(g);
        iLateTime = iEndingIndex;
    }

    // Seems useless
    private void drawCellNames(Graphics g) {
        g.setColor(Color.BLACK);
        Enumeration e = iCellsDrawn.elements();
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            boolean b = isLeaf(c);
            if (!b && c != this) {
                    //drawRotatedText(g, c.getName(), c.xUse, c.yStartUse - 5, -Math.PI/8);
            }
        }

    }

    private boolean isLeaf(Cell c) {
        boolean rtn = false;
        if (c.isLeaf()) 
        	rtn = true;
        else if (c.getEndTime() > iLateTime) 
        	rtn = true;
        return rtn;
    }



    private int draw(Graphics g, int h, int x, int ystart, Cell c, Hashtable cHash, int rootStart) {
        iCellsDrawn.add(c);
        boolean done = false;
        int lastTime = c.iEndTime;
        int lateTime = iLateTime;
        //System.out.println("draw2 " + c.getName() + CS + this.getName() + CS + lastTime + CS + lateTime);
        if (c.iEndTime > lateTime) {
            done = true;
            lastTime = lateTime;
        }

        int length = (int)((lastTime - c.iTimeIndex) * ysc + .5);
        c.yStartUse = (int)((c.iTimeIndex - iTimeIndex) * ysc) + START1;
        //System.out.println("draw2: " + c + CS + iTimeIndex + CS + c.iTimeIndex + CS + c.iEndTime);

        if (c.getChildCount() == 0 || done) {
            if (x < iXmax) x = iXmax + xsc;

            //g.setColor(getDebugColor(c));
            //g.drawLine(x, c.yStartUse, x, c.yStartUse + length);
        	//System.out.println("draw2 " + c.getName());
            drawColoredLine(g, c, x, c.yStartUse, x, c.yStartUse + length);
            //System.out.println("draw1: " + iName + CS + c.getName() + CS + done + CS + g.getColor());
            g.setColor(Color.BLACK);
            //System.out.println("draw: " + c.getName() + CS + yStartUse + CS + length);
            drawRotatedText(g, c.getName(), x, c.yStartUse + length + 5, Math.PI/2);
            if (x > iXmax) iXmax = x;
            c.xUse = x;
            fillInHash(c, cHash);
            g.fillOval(c.xUse-2, c.yStartUse-2, 4, 4);
            return x;
        } else {
            Cell cLeft = (Cell)c.getChildAt(0);
            Cell cRite = (Cell)c.getChildAt(1);
            int nl = cLeft.getChildCount()/2;
            if (nl == 0) 
            	nl = 1;
            //g.setColor(getDebugColor(cLeft));
            //System.out.println("draw2 " + cLeft.getName());
            int x1 = draw(g, h, x, yStartUse + length, cLeft, cHash, rootStart);
            //g.setColor(Color.black);
            cLeft.xUse = x1;
            if (!isLeaf(cLeft)) {
            //if (x1 != x && !cLeft.isLeaf()) {
                g.fillOval(cLeft.xUse - 2, cLeft.yStartUse - 2, 4, 4);
                fillInHash(cLeft, cHash);
                drawRotatedText(g, cLeft.getName(), cLeft.xUse, cLeft.yStartUse - 5, -Math.PI/8);
                //System.out.println("drawRotated: " + cLeft.getName() + CS + cLeft.isLeaf() + CS + x1 + CS + x);
            }
            int xx = x1 + xsc * nl;
            //System.out.println("draw2 " + cRite.getName());
            int x2 = draw(g, h, xx, yStartUse + length, cRite, cHash, rootStart);
            cRite.xUse = x2;
            if (!isLeaf(cRite)) {
            //if (x2 != xx && !cRite.isLeaf()) {
                //g.fillOval(cRite.xUse - 4, cRite.yStartUse - 4, 8, 8);
                g.fillOval(cRite.xUse - 2, cRite.yStartUse - 2, 4, 4);
                fillInHash(cRite, cHash);
                drawRotatedText(g, cRite.getName(), cRite.xUse, cRite.yStartUse - 5, -Math.PI/8);
            }
            //g.setColor(getDebugColor(c));;
            //g.drawLine(cLeft.xUse, cLeft.yStartUse, cRite.xUse, cRite.yStartUse);
            
            // Draw horizontal line 
            drawColoredLine(g, c, cLeft.xUse, cLeft.yStartUse, cRite.xUse, cRite.yStartUse);
            
            //System.out.println("draw2: " + iName + CS + c.getName() + CS + done + CS + g.getColor());
            x = (x1 + x2)/2;
            //g.drawLine(x, c.yStartUse, x, cLeft.yStartUse);
            
            // Draw vertical line
            drawColoredLine(g, c, x, c.yStartUse, x, cLeft.yStartUse);

            return x;
        }
    }


    private void fillInHash(Cell c, Hashtable cHash) {
        int k = c.xUse * 10000 + c.yStartUse;
        cHash.put(new Integer(k), c);
    }


    private void showScale(Graphics g, int y, int frameWidth) {
        // tick marks every 10 minutes
        int lateTime = iLateTime;
        int x = 5; //frameWidth/2;
        Color colorNow = g.getColor();
        g.setColor(Color.blue);
        g.drawLine(x, START1, x, y + START1);

        int k = iLateTime - iTimeIndex;
        double fy = y;
        double fk = k;
        double incOne = fy/fk;
        double incTen = 10*incOne;

        k = (k - (k%10))/10;
        int inc = 5;
        for (int i=0; i <= k; i++) {
            int y0 = START1 + (int)Math.round(incTen * i); // + 0.5);
            g.drawLine(x, y0, x + inc, y0);
        }
        g.drawString(String.valueOf(iTimeIndex), x + inc, START1);
        g.drawString(String.valueOf(lateTime), x + inc, START1 + y + 15);
        g.setColor(colorNow);
    }

    private void drawRotatedText(Graphics g, String s, int x, int y, double angle) {
        Point p1 = new Point(x,y);
        //System.out.println("drawRotatedText1: " + s + CS + p1.getX() + CS + p1.getY() + CS + angle);

        Graphics2D g2d = (Graphics2D)g;
        g2d.rotate(angle);
        try {
            g2d.getTransform().inverseTransform(p1, p1);
        } catch(Exception e) {
            e.printStackTrace();
        }
        //System.out.println("drawRotatedText2: " + s + CS + p1.getX() + CS + p1.getY());


        if (angle > 1.5) g2d.drawString(s, y, -x);
        else {
            Point p = myRotate(x, y);
            g2d.drawString(s, (int)p.getX(), (int)p.getY());

        }

        myRotate(x, y);

        g2d.rotate(-angle);

    }

    private Point myRotate(int x, int y) {
        double h = Math.sqrt(x*x + y*y);
        double a = Math.atan((double)y/(double)x);
        double b = .375*Math.PI - a;
        //System.out.println("myRotate: " + h + CS + a + CS + b);
        int yy = (int)Math.round(h * Math.cos(b));
        int xx = (int)Math.round(h * Math.sin(b));
        return new Point(xx, yy);
    }


    public void updateCellData(Nucleus n) {
        iCellData.add(new CellData(n));
    }

    public Vector getCellData() {
        return iCellData;
    }

    public void setCellData(Vector cd) {
        iCellData = cd;
    }

    public Vector getCellData(int start, int end) {
        //System.out.println("getCellData: " + iName + CS + start + CS + end + CS + iTimeIndex + CS + iEndTime);
        if (start <= iTimeIndex && end > iEndTime) return iCellData;
        if (start > iEndTime) return null;
        Vector v = new Vector();
        //System.out.println("adding some");
        start = Math.max(iTimeIndex, start);
        int last = iTimeIndex + iCellData.size() - 1;
        end = Math.min(last, end);
        //System.out.println("getCellData: " + start + CS + last + CS + end);
        for (int i = start; i <= end; i++) {
            v.add(iCellData.elementAt(i - iTimeIndex));
        }
        return v;
    }


    public Vector getAllCellData(int start, int end) {
        Vector rtn = new Vector();
        Cell p = (Cell)getParent();
        //System.out.print("getAllCellData: " + iName);
        //if (p != null) System.out.println(CS + p.getName());
        //else System.out.println();
        while (p != null) {
            Vector parentCellData = p.getCellData(start, end);
            if (parentCellData == null) break;
            rtn.addAll(0, parentCellData);
            p = (Cell)p.getParent();
        }
        Vector v = getCellData(start, end);
        if (v != null) rtn.addAll(v);
        return rtn;
    }

    public String getRedDataString(int first, int last, int separator, int [] count) {
        int k = 0;
        String sep = SEPARATORS[separator];
        String s = "";
        iCellData.elements();
        //while (e.hasMoreElements()) {
        int i = 0;
        for(i=0; i < iCellData.size(); i++) {
            int time = i + iTimeIndex;
            if (time < first) break;
            if (time > last) continue;
            CellData cd = (CellData)iCellData.elementAt(i);
            //CellData cd = (CellData)e.nextElement();
            //double d = cd.iRweight - 35000;
            double d = cd.iNucleus.rweight - 35000;
            //System.out.println("getRedDataString: " + iName + CS + cd.iRweight + CS + k);
            if (k == 0) s += sep + ONEDEC.format(d + 0.1);
            else s += sep + NODEC.format(d);

            //s += CS + cd.iRweight;
            k++;
            //if (k + iTimeIndex < first) {
            //    s = "";
            //    continue;
            //}
            //if (k + iTimeIndex >= last) break;
        }
        System.out.println("Cell " + iName + CS + k + CS + i);
        count[0] = k;
        return s;
    }

    private String [] blanks = {
         ""
        ," "
        ,"  "
        ,"   "
        ,"    "
        ,"     "
        ,"      "
        ,"       "
        ,"        "
        ,"         "
        ,"          "
    };

    private static final String[] SEPARATORS = {
            ", "
           ,"\t"
    };

    private StringBuffer iRedHeader;

    public String getReverseRedDataString(int first, int last, int separator, int [] count) {
        //System.out.println("getReverseRedDataString: " + iName + CS + first + CS + last);
        String sep = SEPARATORS[separator];
        int k = 0;
        String s = "";
        String s1 = "";
        StringBuffer sb = new StringBuffer();
        //System.out.println("getReverse... size= " + iCellData.size());
        for (int i=iCellData.size() - 1; i >= 0; i--){
            int time = i + iTimeIndex;
            if (time < first) break;
            if (time > last) continue;
            CellData cd = (CellData)iCellData.elementAt(i);
            //System.out.println("getReverse...: " + i + CS + k);
            double d = cd.iNucleus.rweight - 35000;
            if (k == 0) {

                s1 = ONEDEC.format(d + 0.1);
                s += sep + s1;
                sb.append(makeHeaderName(s1));
                //sb.append(CS + iName + blanks[s1.length() - iName.length()]);
            }
            else {
                s1 = NODEC.format(d);
                //System.out.println("NODEC: " + s1);
                s += sep + s1;
                sb.append(makeHeaderName(s1));
                //sb.append(CS + iName + blanks[s1.length() - iName.length()]);
            }
            //System.out.println(iName + CS + i +CS + k + CS + (i + iTimeIndex) + CS + last + CS + d);
            k++;
        }
        System.out.println("Cell: " + iName + CS + k);
        iRedHeader = sb;
        count[0] = k;
        return s;
    }

    private String makeHeaderName(String num) {
        String s = "";
        int n = num.length();
        int m = iName.length();
        if (n > m) s = iName + blanks[n - m];
        else {
            int k = m - n;
            s = iName.substring(k);
        }
        return CS + s;
    }


    public String getReverseRedDataHeaderString() {
        return iRedHeader.toString();
    }

    private static final DecimalFormat
         NODEC = new DecimalFormat("#######")
        ,ONEDEC = new DecimalFormat("#######.#")
        ;

    /**
     * this is where the real parameters of a cell are set
     * @param time birth time of cell
     * @param n Nucleus object for this cell
     */
    public void setParameters(int time, int endTime, Nucleus n) {
        //System.out.println("Cell.setParameters: " + iName + CS + time + CS + endTime);
        iTimeIndex = time;
        iPlane = (int)(n.z + NucUtils.HALFROUND);
        iX = n.x;
        iY = n.y;
        iDia = n.size;
        iEndTime = endTime; //Parameters.getMovie().time_end;
        iEndFate = ALIVE;
        //iHashKey = String.valueOf(iTimeIndex * 1000 + n.index);
        iCellData.add(new CellData(n));
    }

    /**
     * used only by Canonical Tree
     * @param time is the start time from file lineage2.gtr
     */
    public void setStartTime(int time) {
        iTimeIndex = time;
        iEndTime = 0;
        iEndFate = ALIVE;
    }

    public void setTime(int time){
        iTimeIndex = time;
    }

    /**
     * access function for cell name
     * @return String cell name
     */
    public String getName() {
        return iName;
    }

    public void setName(String newName) {
        iName = newName;
    }

    public String getHashKey() {
        return iHashKey;
    }

    public void setHashKey(String hashKey) {
        iHashKey = hashKey;
    }

    /**
     * access function for cell start time
     *
     * @return int time
     */
    public int getTime() {
        return iTimeIndex;
    }

    public int getEndTime() {
        return iEndTime;
    }

    /**
     * access function for cell end time
     *
     * @return int end time
     */
    public int getEnd() {
        return iEndTime;
    }

    /**
     * access function for cell fate
     *
     * @return int iEndFate
     */
    public String getFate() {
        return fates[iEndFate];
    }

    public int getFateInt() {
        return iEndFate;
    }

    /**
     * access function for plane where cell identified rounded to an int
     * @return int nearest image plane for cell birth
     */
    public int getPlane() {
        return iPlane;
    }


    /**
     * access function for cell X position at birth
     * @return int x position
     */
    public int getX() {
        return iX;
    }

    /**
     * access function for cell Y position at birth
     * @return int y position
     */
    public int getY() {
        return iY;
    }
    public double getDiam() {
	return iDia;
    }

    /**
     * provide String for outputting the cell object
     * @return String name of cell
     */
    @Override
	public String toString() {
        return iName;
    }

    public String toString(int x) {
        StringBuffer sb = new StringBuffer(iName);
        sb.append(CS + iTimeIndex);
        sb.append(CS + iPlane);
        return sb.toString();
    }

    /**
     * access function for setting end time (division or death) of cell
     * @param time int time of death
     */
    public void setEndTime(int time) {
        //System.out.println("#######Cell.setEndTime: " + time);
        //new Throwable().printStackTrace();
        iEndTime = time;
    }

    public void setEndingIndex(int time) {
        iEndingIndex = time;
    }

    /**
     * access function for setting the ultimate fate of the cell
     * @param fate int from: 0 -> ALIVE; 1 -> DIVIDED; 2 -> DIED
     */
    public void setEndFate(int fate) {
        iEndFate = fate;
    }

    /**
     * lifetime is difference between end time and birth time
     * @return int difference
     */
    public int getLifeTime() {
        return iEndTime - iTimeIndex + 1;
    }

    public boolean isAnterior() {
        Cell x = (Cell)getParent().getChildAt(0);
        return x == this;
    }

    /**
     * debugging function
     *
     */
    public void showParameters() {
    }

    public String showStuff() {
        StringBuffer sb = new StringBuffer();
        sb.append(iName + CS);
        sb.append(iTimeIndex + CS);
        sb.append(iEndTime + CS);
        sb.append(iEndingIndex);
        return sb.toString();
    }

    public static int getDiscrete(int r) {
        int k = 0;
        k = (int)((r - cMin) * cScale);
        return k;
    }

    private static final Color GRAYCOLOR = new Color(128, 128, 128);
    
    public static Color getTheColor(int index) {
        if (index < 0) {
            index = 0;
            return GRAYCOLOR;
        }
        if (index >= CMAP2.length) 
        	index = CMAP2.length - 1;
        return CMAP2[index];
    }

    private static final Color [] CMAP = {
            new Color(000,   30 , 255             )
           ,new Color(000,   55 , 230               )
           ,new Color(000,   80 , 205            )
           ,new Color(000,  105 , 180              )
           ,new Color(000,  130 , 155            )
           ,new Color(000,  155 , 130            )
           ,new Color(000,  180 , 105            )
           ,new Color(000,  205 , 80            )
           ,new Color(000,  230 , 55            )
           ,new Color(000,  255 , 30            )
           ,new Color(30,  230 , 000            )
           ,new Color(55,  205 , 000            )
           ,new Color(85,  180 , 000            )
           ,new Color(105,  155 , 000            )
           ,new Color(130,  130 , 000            )
           ,new Color(155,  105 , 000            )
           ,new Color(180,   80 , 000            )
           ,new Color(205,   55 , 000            )
           ,new Color(230,   30 , 000            )
           ,new Color(255,  000 , 000            )
    };

    private static final Color [] CMAP3 = {
            new Color(000,  000,    255)
           ,new Color(000,  051,    255)
           ,new Color(000,  103,    255)
           ,new Color(000,  153,    255)
           ,new Color(000,  204,    255)
           ,new Color(000,  255,    255)
           ,new Color(000,  255,    204)
           ,new Color(000,  255,    153)
           ,new Color(000,  255,    103)
           ,new Color(000,  255,    051)
           ,new Color(000,  255,    000)
           ,new Color(051,  255,    000)
           ,new Color(153,  255,    000)
           ,new Color(204,  255,    000)
           ,new Color(255,  255,    000)
           ,new Color(255,  204,    000)
           ,new Color(255,  153,    000)
           ,new Color(255,  102,    000)
           ,new Color(255,  051,    000)
           ,new Color(255,  000,    000)
    };

    private static final Color [] CMAP2 = {
            new Color(000, 255, 0)
           ,new Color(000, 230, 0)
           ,new Color(000, 205, 0)
           ,new Color(000, 180, 0)
           ,new Color(000, 155, 0)
           ,new Color(000, 130, 0)
           ,new Color(000, 105, 0)
           ,new Color(000, 80, 0)
           ,new Color(000, 55, 0)
           ,new Color(000, 30, 0)
           ,new Color(30,  000, 0)
           ,new Color(55,  000, 0)
           ,new Color(85,  000, 0)
           ,new Color(105, 000, 0)
           ,new Color(130, 000, 0)
           ,new Color(155, 000, 0)
           ,new Color(180, 000, 0)
           ,new Color(205, 000, 0)
           ,new Color(230, 000, 0)
           ,new Color(255, 000, 0)
    };

    public final static int
     NAME = 4
    ,TIME = 0
    ,PLANE = 3
    ,X = 1
    ,Y = 2
    ,DIA = 5
    ,PREV = 12 // index for this cell in the previous nuclei file
    ,START0 = 10 // y location where tree drawing starts
    ,START1 = 20 // y location where root cell is placed
    //,BORDERS = 60 // combined unused space at top and bottom (I think)
    ,BORDERS = 90 // combined unused space at top and bottom (I think)
    ,LINEWIDTH = 5
    ;

    public final static int
         ALIVE = 0
        ,DIVIDED = 1
        ,DIED = 2
        ;

    public final static int
         LARGEENDTIME = 500
        ;

    public final static String [] fates = {
            "alive"
           ,"divided"
           ,"died"
    };

    private final static String
         CS = ", "
        ;

    private static void println(String s) {System.out.println(s);}

}
