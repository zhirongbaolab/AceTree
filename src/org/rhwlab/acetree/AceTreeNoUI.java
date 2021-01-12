package org.rhwlab.acetree;

import ij.IJ;
import ij.ImagePlus;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.rhwlab.help.AceTreeHelp;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.image.ImageWindowNoUI;
import org.rhwlab.snight.Config;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.NucleiMgrHeadless;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.tree.CellData;
import org.rhwlab.utils.EUtils;

public class AceTreeNoUI {
    private Hashtable   iNucleiMgrHash;

    private String      iConfigFileName;
    private String []   iImgSuffix;
    private Cell        iRoot;
    private Vector      iTempV;
    private String      iFilePath;
    private boolean     iRootEstablished;
    private int         iImageTime;
    private int         iImagePlane;
    public  ImageWindowNoUI 		iImgWin;
    private boolean     iImgWinSet;
    private NucleiMgr   iNucleiMgr;
    //private Parameters  iParameters;
    public AncesTree   iAncesTree;
    private String      iOrientation;
    private FileInputStream     iFstream;
    private BufferedReader      iBReader;

    private int         iTimeInc;
    private int         iPlaneInc;
    private Cell        iCurrentCell;
    private boolean     iCurrentCellPresent;
    private int         iCurrentCellXloc;
    private int         iCurrentCellYloc;
    private float       iCurrentCellZloc;
    //private String      iAxis;

    // key run parameters
    private String      iZipFileName; // a full path to the zip with nuclei and parameters
    private String      iZipNucDir;   // subdirectory in above zip for nuclei
    private String      iZipTifFilePath; // a full path to the zip file with tifs in it
    private String      iTifPrefix;      // leading part of image file names also parameters file
    private int         iStartingIndex;
    private int         iEndingIndex;
    private int         iNamingMethod;
    private Hashtable   iConfigHash;
    public Hashtable    iCellsByName;

    private double      iZPixRes;
    private int         iPlaneEnd;
    private int         iPlaneStart;

    private boolean     iShowAnnotations;
    private boolean     iShowAnnotationsSave;
    private boolean     iShowCentroids;
    public Integer      iTrackPosition;
    public Integer      iTrackPositionSave;
    private boolean     iIgnoreValueChanged;
    private boolean     iCallSaveImage;
    private int			iUseZip;
    private int         iColor;

    PrintWriter			iPrintWriter;

    public AceTreeNoUI(String configFileName) {
        iTempV = new Vector();
        iConfigFileName = configFileName;
        System.out.println("AceTreeNoUI constructor using config file: " + iConfigFileName);
        //NucUtils.setConfigFileName(iConfigFileName);
        iNucleiMgrHash = new Hashtable();
        iRootEstablished = false;
        iImageTime = 0;
        iImagePlane = 0;
        iTimeInc = 0;
        iPlaneInc = 0;
        iCurrentCell = null;
        iCurrentCellXloc = 0;
        iCurrentCellYloc = 0;
        setShowAnnotations(false);
        iShowCentroids = true;
        iRoot = new Cell(ROOTNAME);
        bringUpSeries(iConfigFileName);
        //bringUpSeriesData(iConfigFileName);

    }

    public AceTreeNoUI(Config config, PrintWriter pw) {
    	iPrintWriter = pw;
        iTempV = new Vector();
        iConfigFileName = config.iConfigFileName;
        //System.out.println("AceTree constructor using config file: " + iConfigFileName);
        //NucUtils.setConfigFileName(iConfigFileName);
        iNucleiMgrHash = new Hashtable();
        iRootEstablished = false;
        iImageTime = 0;
        iImagePlane = 0;
        iTimeInc = 0;
        iPlaneInc = 0;
        iCurrentCell = null;
        iCurrentCellXloc = 0;
        iCurrentCellYloc = 0;
        setShowAnnotations(false);
        iShowCentroids = true;
        iRoot = new Cell(ROOTNAME);
        bringUpSeries(config);

    }

    public NucleiMgr getNucleiMgr() {
        return iNucleiMgr;
    }

    @SuppressWarnings("unused")
	public void test() {
    	Enumeration e = iRoot.breadthFirstEnumeration();
    	while (e.hasMoreElements()) {
    		Cell c = (Cell)e.nextElement();
    	}
    	iImgWin.test();
    }

    private void bringUpSeries(String configFileName) {
        String shortName = Config.getShortName(configFileName);
        NucleiMgr nucMgr = (NucleiMgr)iNucleiMgrHash.get(shortName);
        if (nucMgr == null) {
            try {
                FileInputStream fis = new FileInputStream(configFileName);
                fis.close();
            } catch(Exception fnfe) {
                new AceTreeHelp("/org/rhwlab/help/messages/ConfigError.html", 200, 200);
                return;
            }

            int k = bringUpSeriesData(configFileName);
            if (k != 0) return; //problem finding the zipNuclei
        }
        iNucleiMgr = (NucleiMgr)iNucleiMgrHash.get(shortName);
        if (iNucleiMgr == null) {
            System.out.println(HELPMSG + configFileName);
            System.exit(1);
        }
        //iNucleiMgr.sendStaticParametersToImageWindow();
        ImageWindowNoUI.setStaticParameters(iNucleiMgr.getConfig().iZipTifFilePath,
                iNucleiMgr.getConfig().iTifPrefix, iNucleiMgr.getConfig().iUseZip);
        ImageWindowNoUI.setNucleiMgr(iNucleiMgr);
        //clearTree();
        setConfigFileName(configFileName);
        grabConfigStuff();
        iPlaneEnd = iNucleiMgr.getPlaneEnd();
        iPlaneStart = iNucleiMgr.getPlaneStart();

        //clearTree();
        //iTree.updateUI();
        buildTree(false);
        setShowAnnotations(true);

    }

    private void bringUpSeries(Config config) {
        int k = bringUpSeriesData(config);
        if (k != 0) return; //problem finding the zipNuclei
        ImageWindowNoUI.setStaticParameters(iNucleiMgr.getConfig().iZipTifFilePath,
                iNucleiMgr.getConfig().iTifPrefix, iNucleiMgr.getConfig().iUseZip);
        ImageWindowNoUI.setNucleiMgr(iNucleiMgr);
        //clearTree();
        //setConfigFileName(iConfig.iConfigFileName);
        grabConfigStuff();
        iPlaneEnd = iNucleiMgr.getPlaneEnd();
        iPlaneStart = iNucleiMgr.getPlaneStart();

        //clearTree();
        //iTree.updateUI();
        buildTree(false);
        setShowAnnotations(true);

    }


    @SuppressWarnings("unused")
	public int bringUpSeriesData(Config config) {
    	String configFileName = config.iConfigFileName;
        File fx = new File(configFileName);
        String ss = TITLE + ": " + fx.getName();
        //iMainFrame.setTitle(ss);

        // this is the only place where we construct a NucleiMgr
        iNucleiMgr = new NucleiMgrHeadless(config, iPrintWriter);
        if (!iNucleiMgr.iGoodNucleiMgr) {
            return -1;
        }
        iNucleiMgr.processNuclei(true, iNucleiMgr.getConfig().iNamingMethod);
        /*
        String configName = nucMgr.getConfig().getShortName();
        if (!iNucleiMgrHash.containsKey(configName)) {
            iNucleiMgrHash.put(configName, nucMgr);
            iAceMenuBar.addToRecent(configName);
        }
        */
        // System.gc();
        return 0;
    }



    @SuppressWarnings("unused")
	public int bringUpSeriesData(String configFileName) {
        File fx = new File(configFileName);
        String ss = TITLE + ": " + fx.getName();

        // this is the only place where we construct a NucleiMgr
        NucleiMgr nucMgr = new NucleiMgrHeadless(configFileName);
        if (!nucMgr.iGoodNucleiMgr) {
            return -1;
        }
        nucMgr.processNuclei(true, nucMgr.getConfig().iNamingMethod);
        String config = nucMgr.getConfig().getShortName();
        if (!iNucleiMgrHash.containsKey(config)) {
            iNucleiMgrHash.put(config, nucMgr);
        }
        return 0;
    }

    @SuppressWarnings("unused")
	public void buildTree(boolean doIdentity) {
        iShowAnnotationsSave = iShowAnnotations;
        setShowAnnotations(false);
        iShowCentroids = true;
        if (doIdentity) iNucleiMgr.processNuclei(doIdentity, iNamingMethod);

        grabConfigStuff();
        //System.out.println("StartingIndex: " + iStartingIndex);
        //System.out.println("EndingIndexX: " + iEndingIndex);
        Cell.setEndingIndexS(iEndingIndex);
        iAncesTree = iNucleiMgr.getAncesTree();
        updateRoot(iAncesTree.getRootCells());
        iCellsByName = iAncesTree.getCellsByName();
        //iAxis = getAxis();

        //System.out.println("buildTree: " + iRoot + CS + iRoot.getChildCount());
        int k = 0;
        Cell c = walkUpToAGoodCell();


        // 20050808 added in response to detected bug related to killCells
        //setStartingCell(c, 1);
        //setShowAnnotations(iShowAnnotationsSave);

    }

    @SuppressWarnings("unused")
	private void updateRoot(Vector rootCells) {
		//System.out.println("\n#######updateRoot in: " + iRoot.showStuff());
        iRoot.removeAllChildren();
        // struggled with what must be a bug in DefaultMutableTreeNode
        // until I broke out the collecting of children
        // from the adding of them to a different root
        Vector v = new Vector();
        Enumeration e = rootCells.elements();
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            v.add(c);
        }
        for (int i=0; i < v.size(); i++) {
            Cell cc = (Cell)v.elementAt(i);
            cc.removeFromParent();
            iRoot.add(cc);
        }
		iRoot.setEndTime(1);
		//System.out.println("\n#######updateRoot out: " + iRoot.showStuff());
		Cell xx = (Cell)iAncesTree.getCellsByName().put(iRoot.getName(), iRoot);
		//System.out.println("\n#######updateRoot out2: " + xx.showStuff());
    }

    private Cell walkUpToAGoodCell() {
        Cell c = null;
        if (iRoot.getChildCount() <= 1) return iRoot;
        // assume the first child is P0
        // look for a real cell off of P0
        c = (Cell)iRoot.getChildAt(0);
        while (c.getTime() < 0 && c.getChildCount() > 0) {
            //System.out.println("buildTree: " + c + CS + c.getChildCount() + CS + k);
            //c = (Cell)iRoot.getChildAt(++k);
            c = (Cell)c.getChildAt(0);
            //System.out.println("buildTree: " + c);
        }
        // if you don't find one, go back to the root and look
        // for a Nuc or something
        if (c.getTime() < 0) {
            for (int i=1; i < iRoot.getChildCount(); i++) {
                c = (Cell)iRoot.getChildAt(i);
                if (c.getTime() > 0) break;
            }

        }
        return c;

    }

    /*
    private String getAxis() {
        String axis = "adl";
        Identity id = iNucleiMgr.getIdentity();
        if (id.getParameters().dvInit < 0) axis = "avr";
        println("\ngetAxis: " + axis);
        //return axis;
        return "sam";
    }
    */



    private void grabConfigStuff() {
        Config c = iNucleiMgr.getConfig();
        iTifPrefix = c.iTifPrefix;
        iStartingIndex = c.iStartingIndex;
        iEndingIndex = c.iEndingIndex;
        iUseZip = c.iUseZip;

    }

    public void setStartingCell(Cell c, int time) {
        // seem to need to exercise iAncesTree to start things off well
        //System.out.println("setStartingCell, cell, time: " + c + CS + time);


	    //new Throwable().printStackTrace();
        if (c != iRoot) {
            if (c == null) c = (Cell)iRoot.getChildAt(0);
            while (c.getChildCount() > 0 && c.getTime() < 1) {
                //println("setStartingCell while loop: " + c + CS + c.getTime());
                c = (Cell)c.getChildAt(0);
            }


            //c.showParameters();
            time = Math.max(time, c.getTime());
            time = Math.min(time, c.getEndTime());
            iImageTime = time;
            iTimeInc = 0;
            getTimeAndPlane(c);
            getCurrentCellParameters();
            //showTreeCell(iCurrentCell);
        } else {
            iImageTime = 1;
            iTimeInc = 0;
            iImagePlane = 15;
            iPlaneInc = 0;
        }
        handleCellSelectionChange(c, time - iImageTime); // this will bring up an image
        if (!c.getName().equals("P") && iRoot.getChildCount() > 0) {
            //setShowAnnotations(true);
            iShowCentroids = true;
            addMainAnnotation();
        }
        //System.out.println("setStartingCell -iImgWin: " + iImgWin);
        iImgWin.refreshDisplay(null);
    }

    private void getTimeAndPlane(Cell c) {
        if (c == null) return;
        if (c == iRoot) {
            iImageTime = 1;
            iImagePlane = 15;
        } else {
            iImageTime = c.getTime();
            iImagePlane = (int)((double)c.getPlane() + HALFROUND);
        }
        iTimeInc = 0;
        iPlaneInc = 0;
        iCurrentCell = c;
    }

    private void getCurrentCellParameters() {
    	//System.out.println("getCurrentCellParameters: " + iImageTime + CS + iTimeInc);
        if (iCurrentCell == null) return;
        int time = iImageTime + iTimeInc;
        if (time == 0) {
            time = 1;
            iImageTime = 1;
        }
        //Vector nuclei = iNucleiMgr.getNucleiRecord()[time - 1];
        Nucleus n = null;
        try {
            Vector nuclei = iNucleiMgr.getNucleiRecord().elementAt(time - 1);
            n = NucUtils.getCurrentCellNucleus(nuclei, iCurrentCell);
        } catch(Exception e) {
            System.out.println("AceTree.getCurrentCellParameters error at time=" + time);
        }
        //System.out.println("getCurrentCellParameters: " + time + CS + iCurrentCell + CS + n);
        iCurrentCellXloc = -1;
        iCurrentCellYloc = -1;
        iCurrentCellZloc = -1;
        iCurrentCellPresent = false;
        if (n != null) {
            iCurrentCellXloc = n.x;
            iCurrentCellYloc = n.y;
            iCurrentCellZloc = n.z;
            iImagePlane = trackCellPlane();
            iCurrentCellPresent = true;
        }
    }

    private void handleCellSelectionChange(Cell c, int timeInc) {
        //System.out.println("handleCellSelectionChange: " + c + CS + timeInc);
        if (c == null) return;
        //iAnnotsShown.clear();
        getTimeAndPlane(c);
        iTimeInc = timeInc;
        iPlaneInc = 0;
        //println("handleCellSelectionChange:2 " + iImageTime + CS + iImagePlane);
        if (iImageTime < 1 || iImagePlane < 1) return;
        updateDisplay();
    }

    private int trackCellPlane() {
        if (iTrackPosition != ImageWindow.NONE) {
            iPlaneInc = 0;
            return (int)(iCurrentCellZloc + 0.5);
        }
        else {
            return iImagePlane;
        }
    }

    public void updateDisplay() {
        if ((iImageTime + iTimeInc) < iStartingIndex) return;
        if ((iImagePlane + iPlaneInc) <= 0) return;
        getCurrentCellParameters();
        handleImage();
    }

    public void updateDisplay(int time, int plane, int color) {
    	iImageTime = time;
    	iImagePlane = plane;
    	iColor = color;
    	updateDisplay();
    }

    public void handleImage() {
        String cfile = makeImageName();
        ImagePlus ip = null;
        if (cfile == null) {
            IJ.error("no image available");
            ImageWindowNoUI.makeImage(null);
            return;
        } else {
            if (iImgWin != null) {
                try {

                    ip = iImgWin.refreshDisplay(iTifPrefix + cfile);
                } catch(Exception e) {
                    System.out.println("handleImage -- no image available: " + iTifPrefix + cfile);
                    //System.out.println(e);
                    e.printStackTrace();
                }
            } else {
                ip = ImageWindowNoUI.makeImage(iTifPrefix + cfile);
                iImgWin = new ImageWindowNoUI(iTifPrefix + cfile, ip);
                iImgWin.setAceTree(this);
                ip = iImgWin.refreshDisplay(iTifPrefix + cfile);
                //iImgWin.refreshDisplay(iTifPrefix + makeImageName(iCurrentCell);
                iImgWinSet = true;
            }
        }
    }

    private String makeImageName() {
        // typical name: t001-p15.tif
        // to be augmented later to something like: images/050405-t001-p15.tif
        // which specifies a path and prefix for the set
        StringBuffer name = new StringBuffer("t");
        name.append(EUtils.makePaddedInt(iImageTime + iTimeInc));
        name.append("-p");
        String p = EUtils.makePaddedInt(iImagePlane + iPlaneInc, 2);
        name.append(p);
        switch(iUseZip) {
        case 0:
        case 1:
            name.append(".tif");
            break;
        default:
            name.append(".zip");
        }
        return(name.toString());
    }

    public void addMainAnnotation() {
        if (iCurrentCellXloc <= 0) return;
        iImgWin.addAnnotation(iCurrentCellXloc, iCurrentCellYloc, true);
    }

    public void setShowAnnotations(boolean show) {
        iShowAnnotations = show;
    }

    public void setConfigFileName(String name) {
        iConfigFileName = name;
    }

    public Cell getRoot() {
    	return iRoot;
    }

    public int getTimeInc() {
        return iTimeInc;
    }

    public int getPlaneInc() {
        return iPlaneInc;
    }

    public int getImageTime() {
        return iImageTime;
    }

    public int getImagePlane() {
        return iImagePlane;
    }

    public int getColor() {
        return iColor;
    }

    public boolean isTracking() {
        return iTrackPosition != ImageWindow.NONE;
    }

    public boolean getShowAnnotations() {
        return iShowAnnotations;
    }

    public boolean getShowCentroids() {
        return iShowCentroids;
    }

    public ImageWindowNoUI getImageWindowNoUI() {
    	return iImgWin;
    }

    final public static String
    PARAMETERS = "parameters"
   ,POSITION = "Mouse position: "
   ,SPACES15 = "               "
   ,TITLE = "AceTree"
   ,HELPMSG = "you must provide file: "
   ,SEP = ", "
   ,ROOTNAME = "P"
   ;

    private static final float
    HALFROUND = 0.5f
   ;

    public static void processUsingEditedPoints(String s) {
    	s = "081505";
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String configFileName = "/nfs/waterston1/annots/murray/102405_pha4red/dats/102405_pha4red.xml";
		configFileName = "/nfs/waterston1/annots/murray/010306_pha4red/dats/010306_pha4red.xml";
		configFileName = "/nfs/waterston1/annots/murray/20070719_lin-39_10/dats/20070719_lin-39_10.xml";
		//AceTreeNoUI acenui = new AceTreeNoUI(configFileName);
		Config config = Config.createConfigFromXMLFile(configFileName);
		config.setEndingIndex(200);
		//AceTreeNoUI acenui = new AceTreeNoUI(configFileName);
		//AceTreeNoUI acenui = new AceTreeNoUI(config, null);
		//Orientation orientation = new Orientation(acenui);
		//Vector v = new Vector();
		//orientation.showDivisions(150, v);
		//for (int i=0; i < v.size(); i++) {
		//	String s = (String)v.get(i);
		//	println("AceTreeNoUI.main, " + i + CS + s);
		//}

		AceTreeNoUI acenui = new AceTreeNoUI(configFileName);
		Hashtable h = acenui.iAncesTree.getCellsByName();
		Cell c = (Cell)h.get("ABalaaaala");
		Vector v = c.getCellData();
		int tstart = c.getTime();
		int tend = c.getEndTime();
		int length = v.size();
		/*
		for (int i=0; i < v.size(); i++) {
			CellData cd = (CellData)v.get(i);
			Nucleus n = cd.iNucleus;
			println("main, " + n.identity + CS + n.z + CS + (tstart + i));

		}
		*/
		println("main, " + tstart + CS + tend + CS + length);
		CellData cd = (CellData)v.get(v.size() - 1);
		int plane = Math.round(cd.iNucleus.z);

		/*
		Enumeration e = h.keys();
		while (e.hasMoreElements()) {
			String cname = (String)e.nextElement();
			Cell c = (Cell)h.get(cname);
			Vector v = c.getCellData();
			println("main, " + cname + CS + c + CS + v.size());
		}
	    */


		//acenui.updateDisplay(200, 15, 2);

		boolean showAllCentroids = false;
		boolean showAllAnnotations = true;
		boolean showSelectedCell = true;
		acenui.updateDisplay(tend, plane, 0);
		ImageWindowNoUI imgwin = acenui.getImageWindowNoUI();
		if (showAllCentroids) imgwin.showCentroids();
		if (showSelectedCell) {
			imgwin.showSingleCentroid(cd.iNucleus);
		//	imgwin.showSingleAnnotation(cd.iNucleus);
		}
        BufferedImage bi = imgwin.createBufferedImage();
        Graphics g = bi.getGraphics();
		if (showSelectedCell) {
			imgwin.showSingleCentroid(cd.iNucleus);
			imgwin.showSingleAnnotation(cd.iNucleus, g);
		}
		if (showAllAnnotations) imgwin.showAnnotations(g);
		g.dispose();
        /*
		if (showSelectedCell) {
        	Nucleus n = cd.iNucleus;
            Graphics g = bi.getGraphics();
            g.setColor(Color.WHITE);
            g.drawString(n.identity, n.x, n.y);
            g.dispose();

        }
        */
        //Graphics g = bi.getGraphics();
        //g.drawString("this is a test", 100, 100);

        JFrame frame = new JFrame("LabelDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,1000);
        frame.setTitle("test");

        ImageIcon ii = new ImageIcon(bi);
        JLabel label = new JLabel(ii);
        JScrollPane pane = new JScrollPane(label);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        frame.setContentPane(pane);
        frame.pack();
        frame.setVisible(true);
		println("main, ending");


	}

    private static void println(String s) {System.out.println(s);}
    private static void print(String s) {System.out.print(s);}
    private static final String CS = ", ";
    private static final String TAB = "\t";
    private static final DecimalFormat DF0 = new DecimalFormat("####");
    private static final DecimalFormat DF1 = new DecimalFormat("####.#");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt4(double d) {return DF4.format(d);}
    private static String fmt1(double d) {return DF1.format(d);}
    private static String fmt0(double d) {return DF0.format(d);}

}
