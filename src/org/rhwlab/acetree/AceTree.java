/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */
package org.rhwlab.acetree;

// import org.test.Display3D;
import org.rhwlab.volumeview.Display3D;

import javax.swing.UIManager.*;
import javax.swing.UIManager;
import javax.swing.BoxLayout;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.ImageIcon;
import java.awt.Color;

import org.rhwlab.acetree.ControlCallback;
import org.rhwlab.help.AceTreeHelp;
import org.rhwlab.help.Java3DError;
import org.rhwlab.help.GeneralStartupError;
import org.rhwlab.help.TestWindow;
import org.rhwlab.image.CellMovementImage;
//import org.rhwlab.image.EditImage;
//import org.rhwlab.image.EditImage3;
import org.rhwlab.image.Image3D;
import org.rhwlab.image.ImageAllCentroids;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.image.DepthViews;
import org.rhwlab.nucedit.EditLog;
import org.rhwlab.nucedit.KillCellsDialog;
//import org.rhwlab.nucedit.NucAddDialog;
import org.rhwlab.nucedit.AddOneDialog;
import org.rhwlab.nucedit.DeathsAdjacencies;
//import org.rhwlab.nucedit.EIDialog2;
import org.rhwlab.nucedit.EditTraverse;
import org.rhwlab.nucedit.Juvenesence;
import org.rhwlab.nucedit.KillDeepNucsDialog;
import org.rhwlab.nucedit.Lazarus;
import org.rhwlab.nucedit.NucEditDialog;
import org.rhwlab.nucedit.NucRelinkDialog;
import org.rhwlab.nucedit.UnifiedNucRelinkDialog;
import org.rhwlab.nucedit.Orientation;
import org.rhwlab.nucedit.SetEndTimeDialog;
import org.rhwlab.nucedit.Siamese;
import org.rhwlab.nucedit.Zafer1;
import org.rhwlab.snight.Config;
import org.rhwlab.snight.NucZipper;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.CanonicalTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.tree.Newick;
import org.rhwlab.tree.SulstonTree;
import org.rhwlab.tree.VTree;
import org.rhwlab.utils.AuxFrame;
import org.rhwlab.utils.C;
import org.rhwlab.utils.EUtils;
import org.rhwlab.utils.Log;
import java.awt.event.MouseAdapter;
import ij.ImagePlus;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
/**
 *
 * The main class for the embryo data and analysis user interface.
 * <p>
 * The major supporting classes of the application are:
 * <br><b>AncesTree</b> - builds the tree from the "nuclei" analysis files
 * <br><b>Cell</b> - the node cell of the tree from DefaultMutableTreeNode
 * <br><b>ZipImage</b> - handles display of the selected image
 * <br><b>ZipNuclei</b> - opens the zip data file and enables access to the analysis
 * files therein. Also supplies an instance of class ZipFile used elsewhere.
 * <br><b>NucUtils</b> - class holding many static utility functions closely related
 * </p><p>
 * The following additional supporting classes are involved:
 * <br>AnnotInfo - small data structure
 * <br>ControlCallback - interfaced defined so UI elements can be in a separate class
 * <br>EUtils - Designed to hold simple static utility functions not specific to this app.
 * <br>InputCtrl - a control now used to create on panel in the Our_Tree3 UI
 * <br>MouseHandler - captures mouse movements on the image window
 * to the app.
 * <br>SpringUtilities - class from java tutorial needed by InputCtrl
 *
 *
 * @author biowolp
 * @version 1.0 January 18, 2005
 */

public class AceTree extends JPanel
            implements /*TreeSelectionListener, PlugIn, */
            ActionListener, ControlCallback, Runnable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// public static String runningFrom=""; // hack for path stuff
    private Display3D display3D;

    protected static AceTree  iAceTree;

    private Hashtable<String, NucleiMgr>   iNucleiMgrHash;

    private JLabel 	iSeriesLabel;
    private String      iConfigFileName;
    private JTree       iTree;
//    private String []   iImgSuffix;
    private Cell        iRoot;
    private JTextPane   iText;
    private JTextPane   iText2;
    private JTextPane   iText3;
    protected JFrame      iMainFrame;
//    private Vector      iTempV;
//    private String      iFilePath;
//    private boolean     iRootEstablished;
    private int         iImageTime;
    private int         iImagePlane;
    public  ImageWindow iImgWin;
    private boolean     iImgWinSet;
    private NucleiMgr   iNucleiMgr;
    //private Parameters  iParameters;
    public AncesTree   iAncesTree;
    private String      iOrientation;

    public AceMenuBar  iAceMenuBar;
    private EditLog     iEditLog;
//    private Log         iDDLog;
    private Log         iDLog;
    //private boolean     iEditLogInitialized;

    protected WindowEventHandler  iWinEvtHandler;
//    private FileInputStream     iFstream;
//    private BufferedReader      iBReader;

    private JPanel iToolControls;
    private JButton     iCopy;
    private JButton     iShow;
    private JButton     iClear;
    private JButton     iNext;
    private JButton     iPrev;
    private JButton     iUp;
    private JButton     iDown;
    protected JButton     iHome;
    private JButton		iDefault;
    private JButton     iShowC;
    private JButton     iTrack;
    private JButton     iSister;
    private JButton     iColorToggle;
//    private JLabel      iLabel;
    private int         iTimeInc;
    private int         iPlaneInc;
    private Cell        iCurrentCell;
    private boolean     iCurrentCellPresent;
    private int         iCurrentCellXloc;
    private int         iCurrentCellYloc;
    private float       iCurrentCellZloc;
    protected InputCtrl   iInputCtrl;
    //private String      iAxis;

    // key run parameters
//    private String      iZipFileName; // a full path to the zip with nuclei and parameters
//    private String      iZipNucDir;   // subdirectory in above zip for nuclei
    private String      iZipTifFilePath; // a full path to the zip file with tifs in it
    private String      iTifPrefix;      // leading part of image file names also parameters file
    private int         iStartingIndex;
    private int         iEndingIndex;
    private int         iNamingMethod;
//    private Hashtable   iConfigHash;
    public Hashtable    iCellsByName;

    private double      iZPixRes;
    private int         iPlaneEnd;
    private int         iPlaneStart;

    private boolean     iShowAnnotations;
    private boolean     iShowAnnotationsSave;
    private boolean     iShowCentroids;
    //private Vector      iAnnotsShown;
    public Integer      iTrackPosition;
    public Integer      iTrackPositionSave;
    private boolean     iIgnoreValueChanged;

    protected Image3D    iImage3D;
    
    private Object      iDispProps3D;
    private Object      iDispProps3D2;
    private Object      iDispProps3D2Z;
//    private EditImage   iEditImage;
//    private EditImage   iEditImage2;
    //public EditImage3   iEditImage3;
    public boolean		iEditTools;
    private CellMovementImage iCellMovementImage;
    private boolean     iCallSaveImage;
    private int		iUseZip;
    private int		iUseStack;
    private int		iFileNameType;

    private CanonicalTree   iCanonicalTree;
    protected PlayerControl   iPlayerControl;
    private EditTraverse    iEditTraverse;

    private boolean     iDebugTest;
    private int         iColor;

    public	NucRelinkDialog			iNucRelinkDialog;
    public	AddOneDialog			iAddOneDialog;
    
    private BookmarkDialog		iBookmarkDialog;
    private JList				iBookmarkJList;
    private JButton				iAddActiveCell;
    
    private SulstonTree			iSulstonTree;
    
    private LinkedList<Integer>	iKeyQueue;

    private static boolean fullGUI = false;
    
    private int iStartTime;
    //private GeneralStartupError iGSE;
	
    /**
     * The only constructor defined for this class.
     * Instantiated in the main program.
     */
    protected AceTree() {
         this(null,false);
         System.out.println("AceTree empty constructor");
         //this("config.dat");
     }

    @SuppressWarnings("static-access")
	protected AceTree(String configFileName, boolean fullGUI) {
        super();
		AceTree.fullGUI=fullGUI;
//		System.out.println("string constructor");
        AceTree.iAceTree = this;
        this.iMainFrame = new JFrame(TITLE);

        // custom icon insert
        URL imageURL = PlayerControl.class.getResource("/images/icon2.gif");
        ImageIcon test=new ImageIcon(imageURL, "x");
        this.iMainFrame.setIconImage(test.getImage());


        this.iAceMenuBar = new AceMenuBar(this);
//        this.iTempV = new Vector();
        iConfigFileName = configFileName;
        //System.out.println("AceTree constructor using config file: " + iConfigFileName);
        //NucUtils.setConfigFileName(iConfigFileName);
        this.iNucleiMgrHash = new Hashtable<String, NucleiMgr>();
//        this.iRootEstablished = false;
        iImageTime = 0;
        iImagePlane = 0;
        iTimeInc = 0;
        iPlaneInc = 0;
        iCurrentCell = null;
        iCurrentCellXloc = 0;
        iCurrentCellYloc = 0;
        setShowAnnotations(false);
        iShowCentroids = false;
        iInputCtrl = null;
        iRoot = new Cell(ROOTNAME);
        iTree = new JTree(iRoot);
        iTree.addMouseListener(new TreeMouseAdapter());
        iEditLog = new EditLog("EditLog");
        iDLog = new Log("Debug Log");

        setKeyboardActions();
        displayTree();
        try {
	        iTrackPosition = ImageWindow.ANTERIOR;
	        iTrackPositionSave = iTrackPosition;
	        iDebugTest = false;
	        iCanonicalTree = CanonicalTree.getCanonicalTree();
	        iColor = 0;
	        iTree.addMouseListener(new TreeMouseAdapter());
	        if (iConfigFileName != null) {
		    //System.out.println("AceTree.java 350: " + iConfigFileName);
	            bringUpSeriesUI(iConfigFileName);
	        }
	        iTree.setCellRenderer(new AceTreeCellRenderer(this));
	        setDefaultKeyboardActions();
	        
	        iSulstonTree = null;
	        //iGSE = null;
        } catch (Throwable t) {
        	new GeneralStartupError(getMainFrame(), t);
        }
        
        iKeyQueue = new LinkedList<Integer>();
    }

    /* Function: transformTitle
     * Usage: transformTitle();
     ---
     * Transfroms current image into appropriate file name string for processing.
     */
    public String transformTitle() {
        String oldTitle = getImageTitle();
        int index = oldTitle.indexOf('t');
        System.out.println("Transforming title "+oldTitle);
        try {
	        int num = Integer.parseInt(oldTitle.substring(index + 1, index + 4));
	        return oldTitle.replace(oldTitle.substring(index-1),
	            "_t" + String.valueOf(num) + ".TIF");
        } catch (NumberFormatException nfe) {
        	//System.out.println("AceTree unable to transform image title.");
        }
        return "";
    }

    // The universe needs to be opened and closed once, in order
    // to enable setHandTool and setRectangleTool in crop menu -- haven't figured out yet
    // why closing permits the setting behavior (if the universe isn't closed once before,
    // setting hand/rectangle doesn't work.)

    private static int wasClosed = 0;
    
    public static int getWasClosed() {
        return wasClosed;
    }

    public static void setWasClosed(int i) {
        wasClosed = i;
    }

    /* Function: run3Dviewer
     * Usage: iAceTree.run3Dviewer();
     ---
     * Runs a new 3D viewer.
     */
    public void run3Dviewer() {
        try{
            if (display3D == null) {
                display3D = new Display3D();
            } else {
                if (display3D.getUniverse().getWindow() == null) {
                    display3D = null;
                    display3D = new Display3D();
                } else {
                    System.out.println("You cannot open up more than one main universe.");
                    return;
                }               
            }

            if (wasClosed == 0) { // horrible work-around for getting HandTool/RectangleTool to work, but it does the job
                // should fix to make more elegant in the future
                display3D.runThread("close");
                display3D = null;
                display3D = new Display3D();
            }

            System.out.println("Base directory: " + iZipTifFilePath.substring(0, iZipTifFilePath.length()-5));
            System.out.println("Image Title: " + transformTitle());
            display3D.setBaseDir(iZipTifFilePath.substring(0, iZipTifFilePath.length()-5));
            display3D.updateUniverse(transformTitle());
            display3D.runThread("");
        } catch(NullPointerException e) {
            display3D = null;
            System.out.println("Image stack is not loaded");
        }
    }

    /* Function: addNext3D;
     * Usage: addNext3D();
     ---
     * Updates content of new 3D viewer, if get Cell, next, or previous
     * button on AceTree is selected.
     */
    public void addNext3D() {
       if (display3D != null) {
            if (display3D.getUniverse().getWindow() == null) {
               display3D = null;
            } else {
               display3D.updateUniverse(transformTitle());
               display3D.addNext();
            }
        }      
    }

    public PlayerControl getPlayerControl() {
    	return iPlayerControl;
    }

//    private void testRoot() {
//        System.out.println("testRoot: " + iRoot.getLeafCount());
//    }
    public synchronized static AceTree getAceTree(String configFileName) {
		if (iAceTree == null) {
//		    System.out.println("AceTree.getAceTree making a new AceTree: " + configFileName);
		    if (configFileName != null) {
				if(configFileName.equals("-full")){
				    iAceTree = new AceTree(null,true);	   
				} 
				else{
				    iAceTree = new AceTree(configFileName,false);
				}	
		    }
		    else {
		    	iAceTree = new AceTree(null,false);
		    }
		} 
		return iAceTree;
    }
  

//     // public synchronized static AceTree getAceTree(String configFileName, String fullGUI) {
	
//     //if (iAceTree == null) {
// 	    System.out.println("AceTree.getAceTree making a new AceTree: " + configFileName);
// 	    if (configFileName != null) {
//  		if (fullGUI!=null){
// 		    iAceTree = new AceTree(configFileName,true);
// 		}
//  		else{
// 		    iAceTree = new AceTree(configFileName,false);
// 		}
// 	    }
// 	    else {
// 		iAceTree = new AceTree(null,fullGUI);
// 	    }
// 	} 
// 	return iAceTree;
//     }
	 
    public synchronized static AceTree getAceTree(Object configFileName) {
	System.out.println("array factory");
        if (iAceTree == null) {
	    if (configFileName != null) {
		String [] configs = ((String [])configFileName);
		if (configs.length == 1) {
		    if(configs[0].equals("-full")){
			System.out.println("about to call null boolean con");
		       
			iAceTree = new AceTree(null,true);
		    }
		    else{
			System.out.println("about to call string boolean con");
			iAceTree = new AceTree(configs[0],false);
		    }
		} else {
		    // for now have disabled the abiilty to open multiple series from command line inorder to simply process a second flag to turn on complicated gui
		    // no idea what it was for anyway
		    System.out.println("about to call string boolean con");
		    iAceTree = new AceTree(configs[0],true);
                    //iAceTree = new AceTree(configs[1]);
                    //for (int i=2; i < configs.length; i++) {
		    //   iAceTree.setConfigFileName(configs[i]);
		    //  iAceTree.bringUpSeriesUI(configs[i]);
		}
		iAceTree.setConfigFileName(configs[0]);
		iAceTree.bringUpSeriesUI(configs[0]);
		
	    }
	    
	    else{
		System.out.println("about to call empty con");
		iAceTree = new AceTree(null,false);
	    }

	}

	return iAceTree;
    }

    private class AceTreeCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 1L;
		AceTree iAceTree;
    	public AceTreeCellRenderer(AceTree aceTree) {
    		super();
    		iAceTree = aceTree;
    	}

    	@Override
		public Font getFont() {
    		Font f = super.getFont();
    		if (iAceTree != null) {
    			Cell x = iAceTree.getCellByName(getText());
    			if (x == null) {
    				f = f.deriveFont(Font.PLAIN);
    			} else if (x.getTime() < 1) {
    		    	f = f.deriveFont(Font.ITALIC);
    		    } else {
    			    f = f.deriveFont(Font.BOLD);
    		    }
    		}
    		return f;
    	}



    	@Override
		public Icon getLeafIcon() {
    		return null;
    	}

    	@Override
		public Icon getOpenIcon() {
    		return null;
    	}

    	@Override
		public Icon getClosedIcon() {
    		return null;
    	}

    }


    public void bringUpSeriesUI(String configFileName) {
    	try {
	    	// Reset ImageWindow use stack flag
    		newLine();
	        System.out.println("ImageWindow stack flag reset to 0 in AceTree.");
	        ImageWindow.setUseStack(0);

	        System.out.println("bringUpSeriesUI: " + configFileName);
	        System.gc();
	        // check to see if the series is already in the hash
	        String shortName = Config.getShortName(configFileName);
	        NucleiMgr nucMgr = iNucleiMgrHash.get(shortName);
	        if (nucMgr == null) {
	            // if not in hash then make sure there is such a file before proceeding
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
	        iNucleiMgr = iNucleiMgrHash.get(shortName);
	        if (iNucleiMgr == null) {
	            System.out.println(HELPMSG + configFileName);
	            System.exit(1);
	        }
	        iEditLog = iNucleiMgr.getEditLog();
	        iNucleiMgr.sendStaticParametersToImageWindow();
	        ImageWindow.setNucleiMgr(iNucleiMgr);
	        
	        //clearTree();
	        setConfigFileName(configFileName);
	        grabConfigStuff();
	        iPlaneEnd = iNucleiMgr.getPlaneEnd();
	        iPlaneStart = iNucleiMgr.getPlaneStart();
	        iZPixRes=iNucleiMgr.getZPixRes();
	        //clearTree();
	        //iTree.updateUI();
	
	        buildTree(false);
	        setShowAnnotations(true);
	       
	
	        // Show Java 3D warning message
	        try {
	    		Class.forName("javax.media.j3d.VirtualUniverse");
	    	} catch (ClassNotFoundException e) {
	    		new Java3DError(iMainFrame);
	    	}
    	} catch (Throwable t) {
			new GeneralStartupError(getMainFrame(), t);
    	}
        
    }

    public void bringUpSeriesUI(Config config) {
    	// Reset ImageWindow use stack flag
    	newLine();
        System.out.println("ImageWindow stack flag reset to 0 in AceTree.");
        ImageWindow.setUseStack(0);
        
    	String configFileName = config.iConfigFileName;
        System.out.println("bringUpSeriesUI: " + configFileName);
        System.gc();
        // check to see if the series is already in the hash
        String shortName = Config.getShortName(configFileName);
        NucleiMgr nucMgr = iNucleiMgrHash.get(shortName);
        if (nucMgr == null) {
            // if not in hash then make sure there is such a file before proceeding

            int k = bringUpSeriesData(config);
            if (k != 0) return; //problem finding the zipNuclei
        }
        iNucleiMgr = iNucleiMgrHash.get(shortName);
        if (iNucleiMgr == null) {
            System.out.println(HELPMSG + configFileName);
            System.exit(1);
        }
        iEditLog = iNucleiMgr.getEditLog();
        iNucleiMgr.sendStaticParametersToImageWindow();
        ImageWindow.setNucleiMgr(iNucleiMgr);
        
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

    /**
     * Set up the series data. Process the nuclei via the NucleiMgr and build the AncesTree
     * 
     * @param configFileName - the file path of the config file
     * @ return int indicating success or failure
     */
    public int bringUpSeriesData(String configFileName) {
        System.out.println("bringUpSeriesData: " + configFileName);
        File fx = new File(configFileName);
		//String ss = TITLE + ": " + fx.getName();
        //iMainFrame.setTitle(ss);
       // if(iSeriesLabel==null){
       // 	iSeriesLabel=new JLabel("AceTree");
       // 	iSeriesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        	//iSeriesLabel.setHorizontalAlignment(SwingConstants.LEFT);
        	//iSeriesLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        //}
        iSeriesLabel.setText(fx.getName());
        iMainFrame.setTitle(TITLE);
        
        // this is the only place where we construct a NucleiMgr
        NucleiMgr nucMgr = new NucleiMgr(configFileName);
        ImageWindow.setUseStack(iUseStack);
        //System.out.println("ImageWindow static stack set: "+iUseStack);
        if (!nucMgr.iGoodNucleiMgr) {
            return -1;
        }
        
        nucMgr.processNuclei(true, nucMgr.getConfig().iNamingMethod);
        //nucMgr.processNuclei(false, nucMgr.getConfig().iNamingMethod);
        String config = nucMgr.getConfig().getShortName();
        println("bringUpSeriesData, " + config);
        if (!iNucleiMgrHash.containsKey(config)) {
            iNucleiMgrHash.put(config, nucMgr);
		    if(fullGUI)
		    	iAceMenuBar.addToRecent(config);
        }
        
        System.gc();
        return 0;
    }

    public int bringUpSeriesData(Config config) {
    	String configFileName = config.iConfigFileName;
        System.out.println("bringUpSeriesData: " + configFileName);
        File fx = new File(configFileName);
        String ss = TITLE + ": " + fx.getName();
        iMainFrame.setTitle(ss);

        // this is the only place where we construct a NucleiMgr
        NucleiMgr nucMgr = new NucleiMgr(config);
        ImageWindow.setUseStack(iUseStack);
        //System.out.println("ImageWindow static stack set: "+iUseStack);
        if (!nucMgr.iGoodNucleiMgr) {
            return -1;
        }
        //nucMgr.processNuclei(false, nucMgr.getConfig().iNamingMethod);
        nucMgr.processNuclei(true, nucMgr.getConfig().iNamingMethod);
        String configName = nucMgr.getConfig().getShortName();
        if (!iNucleiMgrHash.containsKey(configName)) {
            iNucleiMgrHash.put(configName, nucMgr);
            if(fullGUI)
            	iAceMenuBar.addToRecent(configName);
        }
        
        System.gc();
        return 0;
    }


    public void openSeveralConfigs(String configList) {
        String sr = null;
        boolean first = true;
        //iConfigFiles = new Vector();
        try {
            FileInputStream fis = new FileInputStream(configList);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            sr = br.readLine();
            while (sr != null && sr.length() > 2) {
                if (sr.indexOf("#") != 0) {
                    String [] sa = sr.split(" ");
                    sr = sa[0];
                    System.out.println("\n\n***config file: " + sr);
                    bringUpSeriesData(sr);
                    if (first) {
                        bringUpSeriesUI(sr);
                        first = false;
                    }
                }
                sr = br.readLine();
            }
            br.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }


    }

    public void removeRecent(String item) {
        iNucleiMgrHash.remove(item);
        System.gc();
    }

    @SuppressWarnings("unused")
	public void clearAll() {
        Enumeration<String> e = iNucleiMgrHash.keys();
        while (e.hasMoreElements()) {
            NucleiMgr nm = iNucleiMgrHash.get(e.nextElement());
            nm = null;
            System.gc();
        }
        iNucleiMgrHash = new Hashtable<String, NucleiMgr>();
        System.gc();
    }

    private void grabConfigStuff() {
        Config c = iNucleiMgr.getConfig();
        iTifPrefix = c.iTifPrefix;
        iStartingIndex = c.iStartingIndex;
        iEndingIndex = c.iEndingIndex;
        iUseZip = c.iUseZip;
        iUseStack = c.iUseStack;
        iZipTifFilePath = c.iZipTifFilePath;
        
        // Get start time
        iStartTime = c.getStartTime();
    }


    // Removes all leaves from working tree
    public void clearTree() {
        //new Throwable().printStackTrace();
        if (iAncesTree == null) {
            return;
        }
        Cell root = iRoot;
        if (root == null) {
        	println("Root is null. Acetree cannot cleartree().");
        	return;
        }
        	
        int m = 0;
        int count = 0;
        Cell cc = null;
        while (iRoot.getChildCount() > 1) {
            Cell c = (Cell)iRoot.getFirstLeaf();
            while ((cc = (Cell)c.getNextLeaf()) != null) {
                c.removeFromParent();
                c = null;
                c = cc;
                count++;
            }
            m++;
        }
        println("clearTree: removed: " + count + CS + m);
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        println("clearTree: memory: " + runtime.freeMemory() + CS + runtime.totalMemory() + CS + runtime.maxMemory());
        if (root != null)
        	root.removeAllChildren();
        
        /*
         * TODO
         * figure out parameters from tree/AncesTree.java
         */
        Hashtable x = iAncesTree.getCells();
        if (x != null)
        	x.clear();
        iTree.updateUI();
    }

//    private void clearTreeTest() {
//        if (iAncesTree == null) {
//            return;
//        }
//        Cell root = iAncesTree.getRoot();
//        root = iRoot;
//        System.out.println("\nAceTree.clearTree entered with root=" + root);
//        int count = 0;
//        /*
//        println("clearTreeTest: leafcount: " + iRoot.getLeafCount());
//        for (int i=0; i < 3000; i++) {
//            int k = iRoot.getLeafCount();
//            //println("clearTreeTest: " + i + CS + k);
//            if (k == 1) break;
//            Cell c = (Cell)iRoot.getFirstLeaf();
//            //println("clearTreeTest: " + i + CS + k + CS + c.getName());
//            c.removeFromParent();
//            //c = null;
//        }
//        */
//        int m = 0;
//        Cell cc = null;
//        while (iRoot.getChildCount() > 1) {
//            //int k = iRoot.getChildCount();
//            //println("clearTreeTest: childcount: " + k);
//            Cell c = (Cell)iRoot.getFirstLeaf();
//            while ((cc = (Cell)c.getNextLeaf()) != null) {
//                c.removeFromParent();
//                c = null;
//                c = cc;
//                count++;
//            }
//            m++;
//        }
//        println("clearTreeTest: removed: " + count + CS + m);
//
//
//        if (root != null) root.removeAllChildren();
//        Hashtable x = iAncesTree.getCells();
//        if (x != null) x.clear();
//        //iNucleiMgr.clearAllHashkeys(); //************ BOGUS
//        iTree.updateUI();
//    }

    @SuppressWarnings("unused")
	private void reviewNuclei() {
    	/*
    	 * TODO
    	 * figure out vector type from NucleiMgr
    	 */
    	Vector nr = iNucleiMgr.getNucleiRecord();
    	for (int i=189; i < 195; i++) {
    		Vector nuclei = (Vector)nr.get(i);
    		for (int j=0; j < nuclei.size(); j++) {
    			Nucleus n = (Nucleus)nr.get(j);
    			println("reviewNuclei, " + i + CS + j );
    		}
    	}
    }

    // 
    @SuppressWarnings("unused")
	public void buildTree(boolean doIdentity) {
        iShowAnnotationsSave = iShowAnnotations;
        setShowAnnotations(false);
        iShowCentroids = false;
        iShowC.setText(SHOWC);
        if (doIdentity) 
        	iNucleiMgr.processNuclei(doIdentity, iNamingMethod);

        if (iEditLog != null) {
            //iEditLog.append(new GregorianCalendar().getTime().toString());
            iEditLog.append("buildTree(" + doIdentity +
                ") start = " + iStartingIndex + " end = " + iEndingIndex
                + iEditLog.getTime());
        }

        grabConfigStuff();
        System.out.println("StartingIndex: " + iStartingIndex);
        System.out.println("EndingIndexX: " + iEndingIndex);
        Cell.setEndingIndexS(iEndingIndex);
        iAncesTree = iNucleiMgr.getAncesTree();
        
        // AncesTree.getCellsByName() returns a HashTable of key cellname and value cell
        iCellsByName = iAncesTree.getCellsByName();
        //Cell P = (Cell)iCellsByName.get("P");
        //int kkk = P.getChildCount();
        //println("AceTree.buildTree, 1, " + kkk + CS + P.getName());

        updateRoot(iAncesTree.getRootCells());

        iCellsByName = iAncesTree.getCellsByName();
        //Cell PP = (Cell)iCellsByName.get("P"); 
        //int kk = PP.getChildCount();
        //println("AceTree.buildTree, 2, " + kk + CS + PP.getName());
        //iAxis = getAxis();

        //System.out.println("buildTree: " + iRoot + CS + iRoot.getChildCount());
        int k = 0;
        Cell c = walkUpToAGoodCell();

        iAceMenuBar.setEditEnabled(true);
        iAceMenuBar.setEnabled(true);

        // 20050808 added in response to detected bug related to killCells
        iTree.updateUI();
        setTreeSelectionMode();
        setStartingCell(c, iStartTime);
        iTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setOpenIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setClosedIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setLeafIcon(null);

        if (iEditTraverse != null) iEditTraverse.buildNotification();
        setShowAnnotations(iShowAnnotationsSave);

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

    public void restoreTree(String shortName) {
        System.out.println("\n\nAceTree.restoreTree called: " + shortName);
        iMainFrame.setTitle(TITLE + ": " + shortName);
        //new Throwable().printStackTrace();
        NucleiMgr nucMgr = iNucleiMgrHash.get(shortName);
        if (nucMgr == null) {
            System.out.println("SORRY: " + shortName + " is not hashed");
            return;
        }
        iNucleiMgr = nucMgr;
        iEditLog = iNucleiMgr.getEditLog();
        grabConfigStuff();
        iPlaneEnd = iNucleiMgr.getPlaneEnd();
        iPlaneStart = iNucleiMgr.getPlaneStart();
        //NucUtils.setNucleiMgr(iNucleiMgr);
        ImageWindow.setNucleiMgr(iNucleiMgr);
        ImageWindow.setStaticParameters(iNucleiMgr.getConfig().iZipTifFilePath,
                iNucleiMgr.getConfig().iTifPrefix, iNucleiMgr.getConfig().iUseZip,
                iNucleiMgr.getConfig().iSplitChannelImage);

        System.out.println("StartingIndex: " + iStartingIndex);
        System.out.println("EndingIndex: " + iEndingIndex);
        Cell.setEndingIndexS(iEndingIndex);
        iAncesTree = iNucleiMgr.getAncesTree();
        updateRoot(iAncesTree.getRootCells());
        iCellsByName = iAncesTree.getCellsByName();
        setShowAnnotations(false);
        iShow.setText(SHOW);
        iShowCentroids = false;
        iShowC.setText(SHOWC);
        Cell.setEndingIndexS(iEndingIndex); // what does this do?

        Cell c = walkUpToAGoodCell();
        iTree.updateUI();
        setTreeSelectionMode();
        setStartingCell(c, iStartTime);
        iTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setOpenIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setClosedIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setLeafIcon(null);
        //iAxis = getAxis();
    }


    /*
     * TODO
     * figure out types --> getCells() from AncesTree.java
     */
    @SuppressWarnings("unused")
	private void updateRoot(Vector rootCells) {
        Cell PP = (Cell)iCellsByName.get("P");
        int kk = PP.getChildCount();
        //println("AceTree.updateRoot, 1, " + kk + CS + PP.getName());
        //System.out.println("\n#######updateRoot in: " + iRoot.showStuff());
        iRoot.removeAllChildren();

        //PP = (Cell)iCellsByName.get("P");
        //kk = PP.getChildCount();
       // println("AceTree.updateRoot, 2, " + kk + CS + PP.getName());


        // struggled with what must be a bug in DefaultMutableTreeNode
        // until I broke out the collecting of children
        // from the adding of them to a different root
        Vector v = new Vector();
        Enumeration e = rootCells.elements();
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            v.add(c);
        }
        //PP = (Cell)iCellsByName.get("P");
        //kk = PP.getChildCount();
        //println("AceTree.updateRoot, 3, " + kk + CS + PP.getName() + CS + iCellsByName.size());

        for (int i=0; i < v.size(); i++) {
            Cell cc = (Cell)v.elementAt(i);
            //println("AceTree.updateRoot, " + i + CS + cc.getName() + CS + ((Cell)cc.getParent()).getName());
            cc.removeFromParent();
            iRoot.add(cc);
        }
        //iCellsByName.put("P", PP);
        iCellsByName.remove("P");
        iCellsByName.put("P", iRoot);


        //PP = (Cell)iCellsByName.get("P");
        //kk = PP.getChildCount();
        //println("AceTree.updateRoot, 4, " + kk + CS + PP.getName() + CS + iCellsByName.size());

		iRoot.setEndTime(1);
		//System.out.println("\n#######updateRoot out: " + iRoot.showStuff());
		//Cell xx = (Cell)iAncesTree.getCellsByName().put(iRoot.getName(), iRoot);
		//System.out.println("\n#######updateRoot out2: " + xx.showStuff());
    }

    public void setStartingCell(Cell c, int time) {
        // seem to need to exercise iAncesTree to start things off well
        System.out.println("setStartingCell, cell, time: " + c + CS + time);

	    //new Throwable().printStackTrace();
        if (c != iRoot) {
            if (c == null)
            	c = (Cell)iRoot.getChildAt(0);
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
            showTreeCell(iCurrentCell);
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
            iShowC.setText(HIDEC);
            addMainAnnotation();
        }
        iAceMenuBar.setClearEnabled(true);
        //System.out.println("setStartingCell -iImgWin: " + iImgWin);
		if(iImgWin!=null)
	        iImgWin.refreshDisplay(null);
	
    }

    @Override
	public void run() {
        println("AceTree.run: entered");
        try {
            Thread.sleep(5);
        } catch(Exception e) {

        }
        nextImage();
        try {
            Thread.sleep(5);
        } catch(Exception e) {

        }
        prevTime();
    }

    public void expandTree() {
        Cell c = (Cell)iRoot.getFirstLeaf();
        while (c != null) {
            showTreeCell(c);
            //System.out.println(c);
            //TreeNode [] tna = c.getPath();
            //TreePath tp = new TreePath(tna);
            //iTree.makeVisible(tp);
            c = (Cell)c.getNextLeaf();
        }
    }

    private void setTreeSelectionMode() {
        iTree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setOpenIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setClosedIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setLeafIcon(null);
    }

    private void displayTree() {
        iTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setOpenIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setClosedIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setLeafIcon(null);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        if(iSeriesLabel==null){
        	iSeriesLabel=new JLabel("AceTree");
        	iSeriesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        	//iSeriesLabel.setHorizontalAlignment(SwingConstants.LEFT);
        	//iSeriesLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        }
       
        add(iSeriesLabel);
        //TREE
        JPanel treev = new JPanel();
        treev.setLayout(new BorderLayout());
        JScrollPane treeView = new JScrollPane(iTree);
        treeView.setPreferredSize(new Dimension(WIDTH,500));//-as made it prefer big
        treeView.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        treev.add(treeView);
        add(treev);

        //INFO PANE
        JPanel textv = new JPanel();
        textv.setLayout(new BorderLayout());
        textv.setMaximumSize(new Dimension(Integer.MAX_VALUE,150));
        textv.setPreferredSize(new Dimension(WIDTH,HEIGHT100)); 
        iText = new JTextPane();
        iText.setEditable(false);
        JScrollPane textView = new JScrollPane(iText);
        textView.setPreferredSize(new Dimension(WIDTH,HEIGHT100));
        textv.add(textView);
        add(textv);
        // CELL TIME CHOOSER
        iInputCtrl = new InputCtrl(this);
        add(iInputCtrl);

        //create panel for tools
        iToolControls=new JPanel();
	 	iToolControls.setLayout(new BoxLayout(iToolControls,BoxLayout.PAGE_AXIS));
	 	// PLAYER CONTROL
        iPlayerControl = new PlayerControl(this);
        iToolControls.add(iPlayerControl);
        //KEYPAD
        JPanel pad = createPad();
        iToolControls.add(pad);
        //iTree.addTreeSelectionListener(this);

        JPanel textv2 = new JPanel();
        textv2.setLayout(new BorderLayout());
        //changed this to half size to reflect absence of cell name portion
		textv2.setPreferredSize(new Dimension(WIDTH/2,HEIGHT30));
		textv2.setMaximumSize(new Dimension(WIDTH,HEIGHT30));
        iText2 = new JTextPane();
        iText2.setPreferredSize(new Dimension(WIDTH/2, HEIGHT30));
        iText2.setEditable(false);
        textv2.add(iText2, BorderLayout.WEST);
        iText3 = new JTextPane();
        iText3.setPreferredSize(new Dimension(WIDTH/2, HEIGHT30));
        iText3.setEditable(false);
       // textv2.add(iText3, BorderLayout.EAST);

	iPlayerControl.addToToolbar(textv2);
	// iToolControls.add(textv2);

	//add panel with tools to move to main control window for now
	//add(iToolControls);
    }

    private JPanel createPad() {
        JPanel p = new JPanel();
        iNext = new JButton(NEXTT);
        iPrev = new JButton(PREV);
        iUp = new JButton(UP);
        iDown = new JButton(DOWN);
        iHome = new JButton(HOME);
        iDefault = iHome;
    	iMainFrame.getRootPane().setDefaultButton(iDefault);

        //iShowC = new JButton(SHOWC);
        iNext.addActionListener(this);
        iPrev.addActionListener(this);
        iUp.addActionListener(this);
        iDown.addActionListener(this);
        iHome.addActionListener(this);
        //p.setLayout(new GridLayout(1,7));
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
        iShow = new JButton(SHOW);
        iShowC = new JButton(SHOWC);
        iClear = new JButton(CLEAR);
        iShow.addActionListener(this);
        iShowC.addActionListener(this);
        iClear.addActionListener(this);
        //JButton x1 = new JButton("");
        //JButton x3 = new JButton("");

		JButton iDepthViews=new JButton(DEPTHVIEWS);
		iDepthViews.addActionListener(this);
	

        iCopy = new JButton(COPY);
        iCopy.addActionListener(this);
        iTrack = new JButton(TRACK);
        iTrack.addActionListener(this);
        iSister = new JButton(SISTER);
        iSister.addActionListener(this);
        iColorToggle = new JButton(COLORTOGGLE);
        iColorToggle.addActionListener(this);
        //iEdit = new JButton(EDIT);
        //iEdit.addActionListener(this);

	// p.setPreferredSize(new Dimension(WIDTH, HEIGHT75));
	//	 p.setMaximumSize(new Dimension(WIDTH, HEIGHT75));
	// old version of button panel
       //  p.add(iShow);
//         p.add(iUp);
//         p.add(iClear);
//         p.add(iPrev);
//         p.add(iHome);
//         p.add(iNext);
//         p.add(iShowC);
//         p.add(iDown);
//         p.add(iCopy);
//         p.add(iTrack);
//         p.add(iSister);
//         p.add(iColorToggle);

        p.add(iShow);
	//p.add(iColorToggle);
	//p.add(iUp);
	
        p.add(iClear);

	p.add(iShowC);
	p.add(iDepthViews);
	//p.add(new JSeparator(SwingConstants.VERTICAL));
	//p.add(iDown);
	

	p.add(iTrack);
	 p.add(iSister);
	// p.add(iPrev);
	p.add(iHome);
	// p.add(iNext);
	
	
	// p.add(iCopy);

        //p.add(iEdit);
        return p;
    }


    @SuppressWarnings("unused")
	private void setSpecialKeyboardActions() {
    	KeyStroke key = null;
    	String xxx = null;
    	final AceTree aceTree = this;

    	xxx = "ctrl_left";
    	Action ctrl_left = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, ctrl_left");
    			if (iAddOneDialog != null) {
    				ActionEvent ae = new ActionEvent(aceTree, 1, "LEFT");
    				iAddOneDialog.actionPerformed(ae);
    			}
    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, ctrl_left );

    	xxx = "ctrl_left_a";
    	Action ctrl_left_a = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, ctrl_left");
    			if (iAddOneDialog != null) {
    				ActionEvent ae = new ActionEvent(aceTree, 1, "LEFT");
    				iAddOneDialog.actionPerformed(ae);
    			}
    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, ctrl_left );

    	xxx = "ctrl_right";
        Action ctrl_right = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, ctrl_right");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "RIGHT");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, ctrl_right );

    	xxx = "ctrl_right_d";
        Action ctrl_right_d = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, ctrl_right");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "RIGHT");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, ctrl_right );

        xxx = "ctrl_up";
    	Action ctrl_up = new AbstractAction() {
    		private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, ctrl_up");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "UP");
				//ActionEvent ae = new ActionEvent(aceTree, 1, "DOWN");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, ctrl_up );

        xxx = "ctrl_up_w";
    	Action ctrl_up_w = new AbstractAction() {
    		private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, ctrl_up");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "UP");
				//ActionEvent ae = new ActionEvent(aceTree, 1, "DOWN");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, ctrl_up );

    	xxx = "ctrl_down";
        Action ctrl_down = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, ctrl_down");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "DOWN");
				//ActionEvent ae = new ActionEvent(aceTree, 1, "UP");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, ctrl_down );

    	xxx = "ctrl_down_s";
        Action ctrl_down_s = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, ctrl_down");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "DOWN");
				//ActionEvent ae = new ActionEvent(aceTree, 1, "UP");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, ctrl_down );

    	xxx = "shift_left";
    	Action shift_left = new AbstractAction() {
    		private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_left");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "BIG");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, shift_left );

    	xxx = "shift_left_a";
    	Action shift_left_a = new AbstractAction() {
    		private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_left");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "BIG");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, shift_left );

    	xxx = "shift_right";
        Action shift_right = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_right");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "SMALL");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, shift_right );

    	xxx = "shift_right_d";
        Action shift_right_d = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_right");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "SMALL");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, shift_right );

        xxx = "shift_up";
    	Action shift_up = new AbstractAction() {
    		private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_up");
    			if (iAddOneDialog == null) return;
				//ActionEvent ae = new ActionEvent(aceTree, 1, "INCZ");
				ActionEvent ae = new ActionEvent(aceTree, 1, "DECZ");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, shift_up );

        xxx = "shift_up_w";
    	Action shift_up_w = new AbstractAction() {
    		private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_up");
    			if (iAddOneDialog == null) return;
				//ActionEvent ae = new ActionEvent(aceTree, 1, "INCZ");
				ActionEvent ae = new ActionEvent(aceTree, 1, "DECZ");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, shift_up );

    	xxx = "shift_down";
        Action shift_down = new AbstractAction() { 
        	private static final long serialVersionUID = 1L;
        	@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_down");
    			if (iAddOneDialog == null) return;
				//ActionEvent ae = new ActionEvent(aceTree, 1, "DECZ");
				ActionEvent ae = new ActionEvent(aceTree, 1, "INCZ");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, shift_down );

    	xxx = "shift_down_s";
        Action shift_down_s = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_down");
    			if (iAddOneDialog == null) return;
				//ActionEvent ae = new ActionEvent(aceTree, 1, "DECZ");
				ActionEvent ae = new ActionEvent(aceTree, 1, "INCZ");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, shift_down );

        // this one is a delete cell special
    	xxx = "DELETE";
        Action DELETE = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, DELETE");
    			if (iAddOneDialog == null) return;
    			killCell(0);
    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, DELETE );

        // this one is a delete cell special
    	xxx = "BACKSPACE";
        Action BACKSPACE = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, BACKSPACE");
    			if (iAddOneDialog == null) return;
    			killCell(0);
    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, BACKSPACE);


        xxx = "F5";
        Action F5 = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, F5");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, AddOneDialog.REBUILDANDRENAME);
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, F5 );

        // these go to the NucRelinkDialog

        xxx = "F1";
        Action F1 = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, F1");
    			if (iNucRelinkDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, NucRelinkDialog.SETEARLYCELL);
				iNucRelinkDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, F1 );

    	xxx = "F2";
        Action F2 = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, F2");
    			if (iNucRelinkDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, NucRelinkDialog.SETLATECELL);
				iNucRelinkDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, F2 );

    	xxx = "F3";
        Action F3 = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, F3");
    			if (iNucRelinkDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, NucRelinkDialog.APPLYONLY);
				iNucRelinkDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, F3 );

    	xxx = "F4";
        Action F4 = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, F4");
    			if (iNucRelinkDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, NucRelinkDialog.APPLYANDREBUILD);
				iNucRelinkDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, false);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, xxx);
        getActionMap().put(xxx, F4 );
    }

    private void setKeyboardActions() {

    	setSpecialKeyboardActions();

    	String actionKey = "";
    	KeyStroke stroke = null;
    	InputMap inputMap = null;
    	ActionMap actionMap = null;


        String s = "PAGE_UP";
        Action PageUp = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
            @Override
			public void actionPerformed(ActionEvent e) {
                if (iShow.getText().equals(HIDE)) {
                    setShowAnnotations(false);
                } else {
                    setShowAnnotations(true);
                }
                updateDisplay();
            }
        };
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("PAGE_UP"), "PAGE_UP");
        getActionMap().put(s, PageUp );

        s = "PAGE_DOWN";
        Action PageDn = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
            @Override
			public void actionPerformed(ActionEvent e) {
                if (iShowC.getText().equals(HIDEC)) {
                    iShowCentroids = false;
                    iShowC.setText(SHOWC);
                } else {
                    iShowCentroids = true;
                    iShowC.setText(HIDEC);
                }
                updateDisplay();
            }
        };
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
            put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, PageDn );

        s = "END";
        Action end = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
        	@Override
			public void actionPerformed(ActionEvent e) {
                copyImage();
            }
        };
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, end );

        s = "HOME";
        Action home = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
            @Override
			public void actionPerformed(ActionEvent e) {
                iTimeInc = iPlaneInc = 0;
                updateDisplay();
            }
        };
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, home );

        s = "UP";
        Action up = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
            @Override
			public void actionPerformed(ActionEvent e) {
                //System.out.println("up key pressed");
                incPlane(-1);
                iTrackPosition = ImageWindow.NONE;
                updateDisplay();
            }
        };
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, up );

        actionKey = "w_up";
        stroke = KeyStroke.getKeyStroke("typed w");
        inputMap = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(stroke, actionKey);
        getActionMap().put(actionKey, up);
        
        // Fast (skipping a few planes) UP using CTRL
        s = "shift UP";
        Action shift_up = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
        	@Override
			public void actionPerformed(ActionEvent e) {
        		//System.out.println("shift-up key pressed--skipping planes");
        		incPlane(-5);
        		iTrackPosition = ImageWindow.NONE;
        		updateDisplay();
        	}
        };
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, shift_up);

        s = "DOWN";
        Action down = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
            @Override
			public void actionPerformed(ActionEvent e) {
                //System.out.println("down key pressed");
                incPlane(1);
                iTrackPosition = ImageWindow.NONE;
                updateDisplay();
            }
        };
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, down );

        actionKey = "s_down";
        stroke = KeyStroke.getKeyStroke("typed s");
        inputMap = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(stroke, actionKey);
        actionMap = this.getActionMap();
        actionMap.put(actionKey, down);
        
        // Fast (skipping a few planes) DOWN using CTRL
        s = "shift DOWN";
        Action shift_down = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
        	@Override
			public void actionPerformed(ActionEvent e) {
        		//System.out.println("shift-down key pressed--skipping planes");
        		incPlane(5);
        		iTrackPosition = ImageWindow.NONE;
        		updateDisplay();
        	}
        };
    	getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
    	getActionMap().put(s, shift_down);


        s = "LEFT";
        Action left = new AbstractAction("LEFT") {
        	private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
            	//System.out.println("LEFT pressed.");
                prevImage();
            }
        };
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, left );

        actionKey = "a_left";
        stroke = KeyStroke.getKeyStroke("typed a");
        inputMap = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(stroke, actionKey);
        actionMap = this.getActionMap();
        actionMap.put(actionKey, left);
        
        // Fast (skipping a few planes) LEFT using CTRL
        s = "shift LEFT";
        Action shift_left = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
        	@Override
			public void actionPerformed(ActionEvent e) {
        		//System.out.println("shift-left key pressed--skipping times");
        		prevImageFast();
        	}
        };
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, shift_left);


        s = "RIGHT";
        Action right = new AbstractAction(s) {
        	private static final long serialVersionUID = 1L;
            @Override
			public void actionPerformed(ActionEvent e) {
            	//System.out.println("RIGHT pressed.");
            	nextImage();
            }
        };

        //AceTreeActions right = new AceTreeActions("RIGHT", 12345);
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, right );

        actionKey = "d_right";
        stroke = KeyStroke.getKeyStroke("typed d");
        inputMap = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(stroke, actionKey);
        actionMap = this.getActionMap();
        actionMap.put(actionKey, right);
        
        // Fast (skipping a few planes) UP using CTRL
        s = "shift RIGHT";
        Action shift_right = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
        	@Override
			public void actionPerformed(ActionEvent e) {
        		//System.out.println("shift-right key pressed--skipping times");
        		nextImageFast();
        	}
        };
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, shift_right);

        s = "ENTER";
        Action get = new AbstractAction(s) {
        	private static final long serialVersionUID = 1L;
            @Override
			public void actionPerformed(ActionEvent e) {
                iInputCtrl.getIt();
                updateDisplay();
            }
        };
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
            put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, get );

    }

//   private class AceTreeActions extends AbstractAction {
//    	int iID;
//
//    	public AceTreeActions(String name, int id) {
//    		super(name);
//    		iID = id;
//
//    	}
//
//		public void actionPerformed(ActionEvent e) {
//			println("AceTreeActions.actionPerformed, " + e);
//
//		}
//
//    }

    private void setDefaultKeyboardActions() {
        String s = "F2";
        Action home = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
            @Override
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
            	println("setDefaultKeyboardActions, ");
            	//iAceTree.requestFocus();
            	Component compFocusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            	Window windowFocusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
            	if (compFocusOwner instanceof JButton) {
            		println("its a button");
            		//((JButton)compFocusOwner).doClick();
            	}
            	println("setKeyboardActions, " + compFocusOwner);
            }
        };
        iDefault.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
            put(KeyStroke.getKeyStroke(s), s);
        iDefault.getActionMap().put(s, home );

    }


    /////////////////////////////////////////////////////////////

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


    public void updateDisplay() {
	//	println("updateDisplay:1 " + System.currentTimeMillis());
        if (iDebugTest) 
        	println("updateDisplay:1 " + System.currentTimeMillis());
        if ((iImageTime + iTimeInc) < iStartingIndex) 
        	return;
        
        if ((iImagePlane + iPlaneInc) <= 0)
        	iPlaneInc = (-1*iImagePlane + 1);
        
        getCurrentCellParameters();
        //System.out.println("AceTree using stack: "+iUseStack);
        handleImage();
        
        if (iCallSaveImage) {
            iCallSaveImage = false;
		    if(iImgWin!=null)
		    	iImgWin.saveImageIfEnabled();
        }
        
        String s = makeDisplayText();
        iText.setText(s);
        if (iDebugTest) {
            debugTest(false);
            println("updateDisplay:2 " + System.currentTimeMillis());
        }
        
        if(iAddOneDialog!=null)
        	iAddOneDialog.updateCellInfo();
    }

    @SuppressWarnings("static-access")
	public void handleImage() {
    	//	System.out.println("handle image");
        String cfile = makeImageName();
        ImagePlus ip = null;
        ImageWindow.setUseStack(iUseStack);
        if (cfile == null) {
            //IJ.error("no image available");
        	//System.out.println("AceTree calling ImageWindow.makeImage(null)...");
            ImageWindow.makeImage(null);
            return;
        } else {
            if (iImgWin != null) {
                try {
                    ip = iImgWin.refreshDisplay(iTifPrefix + cfile);
                } catch(Exception e) {
                    System.out.println("handleImage -- no image available: " + iTifPrefix + cfile);
                    System.out.println(e);
                    e.printStackTrace();
                    iPlayerControl.pause();
                }
            } else {
                System.out.println("\nhandleImage making new one: " + ip + CS + iTifPrefix + CS + cfile);
				// ip = ImageWindow.makeImage(iTifPrefix + cfile);
                //System.out.println("AceTree calling ImageWindow.makeImage2...");
				ip = ImageWindow.makeImage2(iTifPrefix + cfile, getImagePlane(), getUseStack());
				 //iImgWin = new ImageWindow( cfile, ip);
				try {
					iImgWin = new ImageWindow(iTifPrefix + cfile, ip, iPlayerControl);
					//System.out.println("AceTree passing to ImageWindow title: "+iTifPrefix + cfile);
	                iImgWin.setAceTree(this);
	                // Pass list of bookmarked cells to ImageWindow
	                if (iBookmarkJList != null)
	                	iImgWin.setBookmarkList(iBookmarkJList.getModel());
	                //iImgWin.refreshDisplay(iTifPrefix + makeImageName(iCurrentCell);
	                iImgWin.add(iToolControls,BorderLayout.SOUTH);
	                iImgWin.pack();
	                iImgWinSet = true;
				} catch (Throwable t) {
					new GeneralStartupError(getMainFrame(), t);
				}
            }
        }

        /*
        if (iEditImage != null) {
            try {
                ip = iEditImage.refreshDisplay(iTifPrefix + cfile);
            } catch(Exception e) {
                System.out.println("handleImage -- no image available: " + iTifPrefix + cfile);
                System.out.println(e);
                e.printStackTrace();
                //iPlayerControl.pause();
            }
        }
        */

        /*
        if (iEditImage3 != null) {
            try {
                ip = iEditImage3.refreshDisplay(iTifPrefix + cfile);
            } catch(Exception e) {
                System.out.println("handleImage -- no image available: " + iTifPrefix + cfile);
                System.out.println(e);
                e.printStackTrace();
                //iPlayerControl.pause();
            }
        }
        //iImgWin.requestFocus();
         * */

    }

    public void addMainAnnotation() {
        //System.out.println("addMainAnnotation: " + iCurrentCellXloc + CS + iCurrentCellYloc);
        if (iCurrentCellXloc <= 0) return;
	if(iImgWin!=null)
	    iImgWin.addAnnotation(iCurrentCellXloc, iCurrentCellYloc, true);
        //if (iEditImage != null) iEditImage.addAnnotation(iCurrentCellXloc, iCurrentCellYloc, true);
        //if (iEditImage3 != null) iEditImage3.addAnnotation(iCurrentCellXloc, iCurrentCellYloc, true);
    }

    public String makeImageName()
    {
    	return imageNameHandler(iImageTime + iTimeInc, iImagePlane + iPlaneInc);
    }

    public String makeImageName(int time, int plane)
    {
    	return imageNameHandler(time, plane);
    }

    public boolean checkExists(File f)
    {
    	return f.exists();
    }
    
    public String imageNameHandler(int time, int plane)
    {
    	StringBuffer namebuf = new StringBuffer("t");
        namebuf.append(EUtils.makePaddedInt(time));
        /*
        if(iUseStack == 0)
        {
        	namebuf.append("-p");
        	String p = EUtils.makePaddedInt(plane, 2);
        	namebuf.append(p);
        }
        */
        namebuf.append("-p");
    	String p = EUtils.makePaddedInt(plane, 2);
    	namebuf.append(p);
    	
        String original_name = namebuf.toString();
      	StringBuffer namebuf2 = new StringBuffer("t");
        namebuf2.append(String.valueOf(time));
        String new_name = namebuf2.toString();
        
        //System.out.println("AceTree.imageNameHandler: " + iZipTifFilePath + C.Fileseparator + iTifPrefix + original_name + ".tif");
        //System.out.println("AceTree.imageNameHandler: " + iZipTifFilePath + C.Fileseparator + iTifPrefix + new_name + ".tif"); 
        
        if(iFileNameType == 0)
        {
        	switch(1)
        	{
        		case 0:
        		default:
        			if(checkExists(new File(iZipTifFilePath + C.Fileseparator + iTifPrefix + original_name + ".tif"))) {
        				iFileNameType = 1;
        				break;
    				}
        			if(checkExists(new File(iZipTifFilePath + C.Fileseparator + iTifPrefix + new_name + ".TIF"))) {
        				iFileNameType = 8;
        				break;
    				}
        			if(checkExists(new File(iZipTifFilePath + C.Fileseparator + iTifPrefix + original_name + ".TIF"))) {
        				iFileNameType = 2;
        				break;
    				}
        			if(checkExists(new File(iZipTifFilePath + C.Fileseparator + iTifPrefix + original_name + ".tiff"))) {
        				iFileNameType = 3;
        				break;
    				}
					if(checkExists(new File(iZipTifFilePath + C.Fileseparator + iTifPrefix + original_name + ".TIFF"))) {
						iFileNameType = 4;
						break;
					}
					if(checkExists(new File(iZipTifFilePath + C.Fileseparator + iTifPrefix + original_name + ".zip"))) {
						iFileNameType = 5;
						break;
					}
					if(checkExists(new File(iZipTifFilePath + C.Fileseparator + iTifPrefix + original_name + ".ZIP"))) {
						iFileNameType = 6;
						break;
					}
					if(checkExists(new File(iZipTifFilePath + C.Fileseparator + iTifPrefix + new_name + ".tif"))) {
						iFileNameType = 7;
						break;
					}
					if(checkExists(new File(iZipTifFilePath + C.Fileseparator + iTifPrefix + new_name + ".tiff"))) {
						iFileNameType = 9;
						break;
					}
					if(checkExists(new File(iZipTifFilePath + C.Fileseparator + iTifPrefix + new_name + ".TIFF"))) {
						iFileNameType = 10;
						break;
					}
					if(checkExists(new File(iZipTifFilePath + C.Fileseparator + iTifPrefix + new_name + ".zip"))) {
						iFileNameType = 11;
						break;
					}
					if(checkExists(new File(iZipTifFilePath + C.Fileseparator + iTifPrefix + new_name + ".ZIP"))) {
						iFileNameType = 12;
						break;
					}
        	}
        }
        
        //System.out.println("AceTree.imagNameHandler: " + iFileNameType); 
        
        
        switch(iFileNameType)
        {
        	case 1:
        		return(original_name + ".tif");
        	case 8:
        		return(new_name + ".TIF");
        	case 2:
        		return(original_name + ".TIF");
        	case 3:
        		return(original_name + ".tiff");
        	case 4:
        		return(original_name + ".TIFF");
        	case 5:
        		return(original_name + ".zip");
        	case 6:
        		return(original_name + ".ZIP");
        	case 7:
        		return(new_name + ".tif");
        	case 9:
        		return(new_name + ".tiff");
        	case 10:
        		return(new_name + ".TIFF");
        	case 11:
        		return(new_name + ".zip");
        	case 12:
        		return(new_name + ".ZIP");
       		default:
       			return(null);
        }
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


    private void getCurrentCellParameters() {
    	//System.out.println("getCurrentCellParameters: " + iImageTime + CS + iTimeInc);
        if (iCurrentCell == null) 
        	return;
        int time = iImageTime + iTimeInc;
        if (time == 0) {
            time = 1;
            iImageTime = 1;
        }
        //Vector nuclei = iNucleiMgr.getNucleiRecord()[time - 1];
        Nucleus n = null;
        try {
            //Vector nuclei = (Vector)iNucleiMgr.getNucleiRecord().elementAt(time - 1);
        	/*
        	 * TODO
        	 * figure out types from NucleiMgr
        	 */
        	Vector nuclei = iNucleiMgr.getElementAt(time - 1);
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

//    private String getRedDataFromCell(int time) {
//        Vector vcd = iCurrentCell.getCellData();
//        int item = time - iCurrentCell.getTime();
//        CellData cd = (CellData)vcd.elementAt(item);
//        String s = ", rweight: " + cd.iNucleus.rweight;
//        return s;
//    }

    /*
     * TODO
     * figure out types from NucleiMgr
     */
    private String makeDisplayText() {
        int time = iImageTime + iTimeInc;
        //Vector nuclei = (Vector)iNucleiMgr.getNucleiRecord().elementAt(time - 1);
        Vector nuclei = iNucleiMgr.getElementAt(time - 1);

        String name = "";
        if (iCurrentCell != null)
        	name = iCurrentCell.getName();
        StringBuffer sb2 = new StringBuffer();
        //System.out.println("makeDisplayText: " + name);
        if (iCurrentCell == null)
        	iCurrentCellPresent = false;
        if (iCurrentCellPresent) {
            sb2.append(name + " is one of ");
            sb2.append(NucUtils.countLiveCells(nuclei) + " cells at time " + (iImageTime + iTimeInc));
            Nucleus n = NucUtils.getCurrentCellNucleus(nuclei, iCurrentCell);
	        if (n != null) {
            sb2.append("\nlocation: " + iCurrentCellXloc + ", " + iCurrentCellYloc + ", " + n.z);
	        //sb2.append(CS + iAxis);
            double d = iNucleiMgr.nucDiameter(n,
                    iImagePlane + iPlaneInc);
            String sd = (new DecimalFormat("###.#")).format(d);
            sb2.append("\nsize: " + n.size + " displayed diameter: " + sd);
            sb2.append("\ncurrent index: " + n.index);
            //sb2.append(getRedDataFromCell(time));
            sb2.append(" weightg/r: " + n.weight);
            sb2.append(", " + n.rweight);
            sb2.append("\nstart=" + iCurrentCell.getTime());
            sb2.append(", end=" + iCurrentCell.getEnd());
            sb2.append(", fate=" + iCurrentCell.getFate());
            String track;
            switch(iTrackPosition.intValue()) {
                case 1:
                    track = "\ntrack anterior";
                    break;
                case 2:
                    track = "\ntrack posterior";
                    break;
                default:
                    track = "\nnot tracking";
                    break;

            }
            //if (iTrackPosition != ImageWindow.NONE) track = "\ntracking";
            sb2.append(track);
            }
        } else
        	sb2.append(name + " not present");
        //System.out.println("makeDisplayText: " + iCurrentCell.getRedDataString());
        return sb2.toString();
    }

    /**
     * Called from AceMenuBar on file open action
     * @param name the name of the config file we opened
     */
    public void setConfigFileName(String name) {
        //System.out.println("AceTree.setConfigFileName: " + name);
        //new Throwable().printStackTrace();
        iConfigFileName = name;
        //NucUtils.setConfigFileName(name);
        
        // If the bookmark dialog is instantiated, change its
        // path to where xml are loaded and saved
        if (iBookmarkDialog != null)
        	iBookmarkDialog.setPath(iConfigFileName);
    }

    /**
     * Called from AceMenuBar on quickopen action
     * @param
     */
    public void quickOpen() {
        println("AceTree.quickOpen");
        new QuickOpen();
    }


    public String getConfigFileName() {
        //System.out.println("AceTree.getConfigFileName: " + iConfigFileName);
        return iConfigFileName;
    }
////////////////////////////////////////////////////////////////////
////////////  image handling ///////////////////////////////////////
////////////////////////////////////////////////////////////////////


    private void copyImage() {
        ImagePlus ip = iImgWin.getImagePlus();
        String s = iTifPrefix + makeImageName() + Math.random();
        ImageWindow iw = new ImageWindow(s, ip,iPlayerControl);
        iw.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        iw.setAceTree(this);
        //iw.refreshDisplay(s);
        iw.setLocation(iw.getX() + XINC, iw.getY() + YINC);
        iw.removeHandlers();
        iw.setAceTree(null);
        iw = null;
    }

/*
    public void editImage() {
        ImagePlus ip = iImgWin.getImagePlus();
        String s1 = iTifPrefix + makeImageName();
        String s = s1 + Math.random();
        //System.out.println("editImage: " + s);
        iEditImage = new EditImage(s, ip);
        iEditImage.setLocation(iEditImage.getX() + XINC, iEditImage.getY() + YINC);
        if (iEditImage != null) {
            try {
                ip = iEditImage.refreshDisplay(s1);
            } catch(Exception e) {
                System.out.println("editImage -- no image available: " + s1);
                System.out.println(e);
                e.printStackTrace();
                //iPlayerControl.pause();
            }
        }
    }
*/
    public void editImage3() {
    	/*
        ImagePlus ip = iImgWin.getImagePlus();
        String s1 = iTifPrefix + makeImageName();
        String s = s1 + Math.random();
        //System.out.println("editImage: " + s);
        iEditImage3 = new EditImage3(s, ip);
        iEditImage3.setLocation(iImgWin.getX() + XINC, iImgWin.getY() + YINC);
        if (iEditImage3 != null) {
            try {
                ip = iEditImage3.refreshDisplay(s1);
            } catch(Exception e) {
                System.out.println("editImage -- no image available: " + s1);
                System.out.println(e);
                e.printStackTrace();
                //iPlayerControl.pause();
            }
        }
        */
    }

    public void editTools() {
    	//brings up the NucRelinkDialog
    	//println("AceTree.editTools, ");
    	relinkNucleus();
    	
    	if (iAddOneDialog != null)
    		return;
    	iEditTools = true;
    	iAddOneDialog = new AddOneDialog(this, iImgWin, false, iCurrentCell, iImageTime);
    	//iAddOneDialog = new AddOneDialog(this, iEditImage3, false, iCurrentCell, iImageTime);
    	iImgWin.iDialog = iAddOneDialog;
    	//iImgWin.setDialogsEnabled(true);
    	//iEditImage3.iDialog = iAddOneDialog;
    }
    
    //private int selectedListIndex = 0;
    // Add new dialog for bookmarks window
    public synchronized void bookmarkTool() {
    	// User has already opened the bookmark dialog before
    	if (iBookmarkDialog != null) {
    		iBookmarkDialog.displayWindow();
    	}
    	else {
    		iBookmarkDialog = new BookmarkDialog(iConfigFileName);

    		// Retrieve cell list and add button from bookmark dialog
    		// and add listeners for their events
    		iBookmarkJList = iBookmarkDialog.getJList();
    		//iBookmarkJList.addListSelectionListener(new ListSelectionHandler());
    		
    		// Add mouse listener to bookmark list
    		iBookmarkJList.addMouseListener(new MouseAdapter() {
    			@Override
				public void mouseClicked(MouseEvent me) {
    				//int clickedListIndex = iBookmarkJList.locationToIndex(me.getPoint());
					String selected = (String)iBookmarkJList.getSelectedValue();
	 				// Handle setting active cell
	 				if (selected != null) {
		 				try {
			 				Cell c = (Cell)iAncesTree.getCellsByName().get(selected);
			 				if (c != null) {
			 					System.out.println("Setting active cell to: "+selected);
				 				showTreeCell(c);
				                if (c.getTime() < 0) {
				                    if (iCurrentCell != null) 
				                    	c = iCurrentCell;
				                    else 
				                    	return;
				                }
				                int time = c.getTime();
				                setCurrentCell(c, time, LEFTCLICKONTREE);
			 				}
		 				}
		 				catch (NullPointerException npe) {
		 					return;
		 				}
	 				}
    			}
    		});
    		
    		// Pass bookmarked list of cells to ImageWindow for rendering in another color
    		if (iImgWin != null)
    			iImgWin.setBookmarkList(iBookmarkJList.getModel());
    		iAddActiveCell = iBookmarkDialog.getAddButton();
    		iAddActiveCell.addActionListener(this);
    		
    		// Pass bmarked list to SulstonTree for rendering in another color
    		// Used for interactive lineage
    		if (iSulstonTree != null)
    			iSulstonTree.setBookmarkList(iBookmarkJList.getModel());
    	}
    }
    
    // Event handler for list selection
    /*
 	private class ListSelectionHandler implements ListSelectionListener {
 		// Handler for list selection
 		public synchronized void valueChanged(ListSelectionEvent e) {
 			JList list = (JList)e.getSource();
 			boolean isAdjusting = e.getValueIsAdjusting();
 			// Probably don't need to test if the selection is adjusting because only one
 			// selection can be made at a time anyway
 			//boolean isAdjusting = false;
 			if (!isAdjusting) {
 				String selected = (String)list.getSelectedValue();
 				selectedListIndex = list.getSelectedIndex();
 				// Handle setting active cell
 				if (selected != null) {
	 				try {
		 				Cell c = (Cell)iAncesTree.getCellsByName().get(selected);
		 				if (c != null) {
		 					System.out.println("Setting active cell to: "+selected);
			 				showTreeCell(c);
			                if (c.getTime() < 0) {
			                    if (iCurrentCell != null) 
			                    	c = iCurrentCell;
			                    else 
			                    	return;
			                }
			                int time = c.getTime();
			                setCurrentCell(c, time, LEFTCLICKONTREE);
		 				}
	 				}
	 				catch (NullPointerException npe) {
	 					return;
	 				}
 				}
 			}
 		}
 	}
 	*/

    public void cellMovementImage() {
        ImagePlus ip = iImgWin.getImagePlus();
        String s1 = iTifPrefix + makeImageName();
        String s = s1;
        //String s1 = iTifPrefix + makeImageName();
        //String s = s1 + Math.random();
        //System.out.println("cellMovementImage: " + s);
        iCellMovementImage = new CellMovementImage(s, ip);
        /*
        if (iEditImage != null) {
            try {
                ip = iCellMovementImage.refreshDisplay(s1);
            } catch(Exception e) {
                System.out.println("cellMovememtImage -- no image available: " + s1);
                System.out.println(e);
                e.printStackTrace();
                //iPlayerControl.pause();
            }
        }
        */

    }


    public void setEditImageNull(int which) {
        //println("setEditImageNull:");
        switch(which) {
            case 1:
                //iEditImage = null;
                break;
            case 3:
                //iEditImage3 = null;
                break;
            case 4:
                iCellMovementImage = null;
                break;

            default:
        }
    }

////////////////////////////////////////////////////////////////////
////////////image handling end ///////////////////////////////////
////////////////////////////////////////////////////////////////////

// introduced to permit right click on tree to select end time of cell
    private class TreeMouseAdapter extends MouseInputAdapter {
        @Override
		@SuppressWarnings({ "unused", "static-access" })
		public void mouseClicked(MouseEvent e) {
            int button = e.getButton();
            //System.out.println("TreeMouseAdapter.mouseClicked: " + button);
            Cell c = null;
            if (button == 2)
            	return;
            else {
                int selRow = iTree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = iTree.getPathForLocation(e.getX(), e.getY());
                if (selPath == null)
                	return;
                c = (Cell)selPath.getLastPathComponent();
                //if (c.getName().startsWith("AB")) {
                //	println("TreeMouseAdapter.mouseClicked: debug");
                //}
            }

            if (button == 1) {
                //int selRow = iTree.getRowForLocation(e.getX(), e.getY());
                //TreePath selPath = iTree.getPathForLocation(e.getX(), e.getY());
                //if (selPath == null) return;
                //Cell c = (Cell)selPath.getLastPathComponent();
                //Cell c = (Cell) iTree.getLastSelectedPathComponent();
                showTreeCell(c);
                if (c.getTime() < 0) {
                    if (iCurrentCell != null)
                    	c = iCurrentCell;
                    else return;
                }
                int time = c.getTime();
                //println("TreeMouseAdapter.mouseClicked: " + selPath + CS + c + CS + time);
                setCurrentCell(c, time, LEFTCLICKONTREE);
            }

            else if (button == MouseEvent.BUTTON3|e.isControlDown()) {
                //iIgnoreValueChanged = true;
                //int selRow = iTree.getRowForLocation(e.getX(), e.getY());
                //TreePath selPath = iTree.getPathForLocation(e.getX(), e.getY());
                //if (selPath == null) return;
                //Cell c = (Cell)selPath.getLastPathComponent();
                //Cell c = (Cell) iTree.getLastSelectedPathComponent();
                showTreeCell(c);
                if (c.getTime() < 0) {
                    if (iCurrentCell != null) c = iCurrentCell;
                    else return;

                }
                int time = c.getEnd();
                setCurrentCell(c, time, RIGHTCLICKONTREE);
            }
        }
    }


    /** Required by TreeSelectionListener interface.
     * */
    public void valueChanged(TreeSelectionEvent e) {
        // events are ignored if they are created programmatically
        // as part of the tracking code
        //System.out.println("valueChanged: " + iIgnoreValueChanged);
        /*
        if (iIgnoreValueChanged) {
            iIgnoreValueChanged = false;
            return;
        }
        int now = iImageTime + iTimeInc;
        Cell c = (Cell) iTree.getLastSelectedPathComponent();
        setCurrentCell(c, now, LEFTCLICKONTREE);
        */
    }


    public void imageUp() {
        incPlane(-1);
        iTrackPosition = ImageWindow.NONE;
        iCallSaveImage = true;
        updateDisplay();
    }

    public void imageDown() {
        incPlane(1);
        iTrackPosition = ImageWindow.NONE;
        iCallSaveImage = true;
        updateDisplay();
    }


    @Override
	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == iAddActiveCell) {
    		try {
	        	if (iCurrentCell != null || !iCurrentCell.equals("")) {
	        		String name = iCurrentCell.getName();
	        		//System.out.println("Adding cell: "+name);
	        		iBookmarkDialog.addCell(name);
	        	}
    		}
    		catch (NullPointerException npe) {
    			return;
    		}
    		return;
        }
    	println("AceTree.actionPerformed, " + e);
        boolean doUpdate = true;
        if (!iImgWinSet) 
        	return;
        iImgWin.setSpecialEffect(null);
        String cmd = e.getActionCommand();
        int inc = 0;
        if (e.getActionCommand().equals(NEXTT)) {
            doUpdate = nextTime();
        }
        else if (cmd.equals("F2"))
        	println("AceTree.actionPerformed, F2");
        else if (e.getActionCommand().equals(PREV))
        		prevImage();
        else if (e.getActionCommand().equals(UP)) {
            imageUp();
            return;
        }
        else if (e.getActionCommand().equals(DOWN)) {
            imageDown();
            return;
        }
        else if (e.getActionCommand().equals(HOME)) {
            getTimeAndPlane(iCurrentCell);
            if (iCurrentCell.isAnterior()) iTrackPosition = ImageWindow.ANTERIOR;
            else iTrackPosition = ImageWindow.POSTERIOR;
            //setTrack();
        }
        else if (e.getActionCommand().equals(SHOW)) {
            setShowAnnotations(true);
        }
        else if (e.getActionCommand().equals(HIDE)) {
            setShowAnnotations(false);
        }
        else if (e.getActionCommand().equals(SHOWC)) {
            iShowCentroids = true;
            iShowC.setText(HIDEC);
        }
        else if (e.getActionCommand().equals(HIDEC)) {
            iShowCentroids = false;
            iShowC.setText(SHOWC);
        }
        else if (e.getActionCommand().equals(CLEAR)) {
            setShowAnnotations(false);
            iImgWin.clearAnnotations();
            //if (iEditImage3 != null) iEditImage3.clearAnnotations();
        }
        else if (e.getActionCommand().equals(COPY)) {
            //copyImage();
            //John requested that we trashcan this
            //for (int i=0; i < 10; i++) {
            //    clearTree();
            //    buildTree(true);
            //}
            //debugTest(true);
            //nextTime();
        }
	else if (e.getActionCommand().equals(DEPTHVIEWS)){
		 new DepthViews("");
	    }

        else if (e.getActionCommand().equals(EDIT)) {
            editImage3();
        }

        else if (e.getSource() == iTrack) {
            setTrack();
        }
        else if (e.getSource() == iSister) {
            handleSisterRequest();
        }
        else if (e.getSource() == iColorToggle) {
            toggleColor();
        }
        if (doUpdate)
        	updateDisplay();
    }

    public void toggleColor() {
        iColor = (iColor + 1) % 4;
    }

    public int getColor() {
        return iColor;
    }

    // handle track/no track button action
    private void setTrack() {
        if (iTrackPosition != ImageWindow.NONE) {
            iTrackPositionSave = iTrackPosition;
            iTrackPosition = ImageWindow.NONE;
        } else {
            iTrackPosition = iTrackPositionSave;
        }
    }

    public void forceTrackingOn() {
        iTrackPosition = ImageWindow.ANTERIOR;
        iTrackPositionSave = ImageWindow.POSTERIOR;
    }

    /*
     * TODO
     * figure out types
     */
    @Override
	@SuppressWarnings("unused")
	public void controlCallback(Vector v) {
    	if(iImgWin == null)
    		return;
    	
        iImgWin.setSpecialEffect(null);
        String ctrl = (String)v.elementAt(0);
        boolean haveTime = false;
        boolean haveCellName = false;
        boolean haveCellIndex = false;
        // Command to "get cell"
        if (ctrl.equals("InputCtrl1")) {
            //println("controlCallback: ");
        	//requestFocus();
            String time = ((String)v.elementAt(1)).trim();

            int requestedTime = -1;
            Vector v2 = null;
            try {
                requestedTime = Integer.parseInt(time);
                //v2 = (Vector)iNucleiMgr.getNucleiRecord().elementAt(requestedTime - 1);
                v2 = iNucleiMgr.getElementAt(requestedTime-1);
                haveTime = true;

            } catch(Exception e) {
                //System.out.println("bad image time: " + time);
                //return;
            }
            String cell = ((String)v.elementAt(2)).trim();
            String target = cell.toLowerCase();
		    String cellproper = PartsList.lookupProper(cell);
		    System.out.println("ControlCallback looked up cell, proper: "+cell+CS+cellproper);
		    if (cellproper != null) {
		    	target = cellproper.toLowerCase();
	    	}
	
		    //System.out.println("controlCallback: " + cell + CS + time);
            boolean numeric = false;
            if (cell.length() > 0) {
                numeric = Character.isDigit(cell.charAt(0));
                if (numeric)
                	haveCellIndex = true;
                else
                	haveCellName = true;
            }
            
            //System.out.println("controlCallback: " + time + CS + cell + CS + numeric);
            boolean valid = haveCellName || haveTime;
            if (!valid) {
                println("controlCallback: invalid choice");
                return;
            }

            Cell c = null;
            if (numeric) {
                int index = Integer.parseInt(cell);
                try {
                    cell = ((Nucleus)v2.elementAt(index - 1)).identity;
                    c = (Cell)iAncesTree.getCellsByName().get(cell);
                    c = null;
                } catch(ArrayIndexOutOfBoundsException aiob) {
                    System.out.println("bad cell index: " + cell);
                    return;
                } catch(Exception e) {
                    System.out.println("ControlCallback: " + e);
                    return;
                }
            }
            else if (target.length() > 0) {
            	//System.out.println("Length of cell name requested: "+cell.length());
            	//System.out.println("Need to look for: ("+target+")");
            	//iAncesTree.printCellHashLowerCase();
                //c = (Cell)iAncesTree.getCellsByNameLowerCase().get(cell);
            	//iAncesTree.printCellHash();
                //c = (Cell)iAncesTree.getCellsByName().get(cell);
            	c = (Cell)iAncesTree.getCellsByNameLowerCase().get(target);
            	if (c == null)
            		System.out.println("Couldn't get cell from hash");
            }
            else
            	c = null;

            Cell csave = null;
            if (c != null) {
                if (!haveTime)
                	requestedTime = c.getEnd();
            }
            else if (haveTime) {
                // try for a cell near the middle of the embryo
                for (int i=0; i < v2.size(); i++) {
                    Nucleus n = (Nucleus)v2.get(i);
                    c = null;
                    if (n.status > 0) {
                        c = (Cell)iAncesTree.getCellsByName().get(n.identity);
                        if (csave == null) csave = c;
                        if (n.z > 15 && n.z < 18) break;
                    }
                }
                if (c == null)
                	c = csave;
            }

        	setCurrentCell(c, requestedTime, CONTROLCALLBACK);

            System.out.println(transformTitle());
            
            // New 3D Viewer Code, to update upon "getting cell"
            addNext3D();
        }
    }

    /*
     * TODO
     * figure out types
     */
    public void controlCallback(Vector v, int oldversion) {
        iImgWin.setSpecialEffect(null);
        String ctrl = (String)v.elementAt(0);
        if (ctrl.equals("InputCtrl1")) {
            String time = ((String)v.elementAt(1)).trim();

            int requestedTime = -1;
            Vector v2 = null;
            try {
                requestedTime = Integer.parseInt(time);
                //v2 = (Vector)iNucleiMgr.getNucleiRecord()[requestedTime - 1];
                //v2 = (Vector)iNucleiMgr.getNucleiRecord().elementAt(requestedTime - 1);
                v2 = iNucleiMgr.getElementAt(requestedTime-1);
            } catch(Exception e) {
                System.out.println("bad image time: " + time);
                return;
            }
            String cell = ((String)v.elementAt(2)).trim();
            //System.out.println("controlCallback: " + time + CS + cell);
            boolean numeric = Character.isDigit(cell.charAt(0));
            Cell c = null;
            if (numeric) {
                int index = Integer.parseInt(cell);
                try {
                    cell = ((Nucleus)v2.elementAt(index - 1)).identity;
                    c = (Cell)iAncesTree.getCellsByName().get(cell);
                } catch(ArrayIndexOutOfBoundsException aiob) {
                    System.out.println("bad cell index: " + cell);
                    return;
                } catch(Exception e) {
                    System.out.println("ControlCallback: " + e);
                    return;
                }
            } else {
                c = (Cell)iAncesTree.getCellsByName().get(cell);
            }
            if (c == null) {
                System.out.println("bad cell name: " + cell);
                return;
            }
            //System.out.println("controlCallback: " + c.getName() + CS + requestedTime);
            setCurrentCell(c, requestedTime, CONTROLCALLBACK);
        }
    }

    public Cell getCellByName(String name) {
        if (iAncesTree == null)
        	return null;
    	else 
    		return (Cell)iAncesTree.getCellsByName().get(name);
    }

    public Cell getCellByHash(String hashKey) {
        return (Cell)iAncesTree.getCells().get(hashKey);
    }

    public AncesTree getAncesTree() {
        return iAncesTree;
    }

    public JTree getJTree() {
        return iTree;
    }

    public NucleiMgr getNucleiMgr() {
        return iNucleiMgr;
    }
    
    public boolean nextTime() {
	   //System.out.println("AceTree.nextTime: " + iImageTime + CS + iTimeInc + CS + iEndingIndex);
       //new Throwable().printStackTrace();
	   //System.out.println("iImageTime: "+iImageTime+CS+"iEndingIndex: "+iEndingIndex);
	   if (iImageTime + iTimeInc == iEndingIndex)
        	return false;
        
        iTimeInc++;

        iCallSaveImage = true;
        int now = iImageTime + iTimeInc;
        int end = 9999;
        if (iCurrentCell != null) 
        	end = iCurrentCell.getEnd();
        if (now <= end) 
        	return true; // we will call updateDisplay next
        if (iCurrentCell.getFateInt() == Cell.DIED) {
            iCurrentCellPresent = false;
            //System.out.println("cell death -- tracking cannot continue on this cell: " + iCurrentCell);
            //System.out.println("nextTime turning tracking off");
            iImageTime += iTimeInc;
            iTimeInc = 0;
            iTrackPosition = ImageWindow.NONE;
            return true;
        }
        // at this point we know that a cell division occurred in this transition
        // iCurrentCell will change as a side effect of doDaughterDisplayWork
        setCurrentCell(iCurrentCell, now, NEXTTIME);
        /*
        */
        return true;
    }

    public boolean prevTime() {
        if (iImageTime + iTimeInc <= iStartingIndex)
        	return false;
        iTimeInc--;
        
        iCallSaveImage = true;
        int now = iImageTime + iTimeInc;
        int start = 0;
        if (iCurrentCell != null) start = iCurrentCell.getTime();
        if (now >= start)
        	return true;
        // a cell change occurs as we move to parent here
        //println("prevTime: " + iCurrentCell.getName() + CS + now);
        setCurrentCell(iCurrentCell, now, PREVTIME);
        return true;
    }


    private void showTreeCell(Cell c) {
        iIgnoreValueChanged = true;
        TreeNode [] tna = c.getPath();
        TreePath tp = new TreePath(tna);
        //iTree.expandPath(tp);
        iTree.makeVisible(tp);
        int row = iTree.getRowForPath(tp);
        iTree.setSelectionInterval(row,row);
        iTree.scrollRowToVisible(row);
        iTree.makeVisible(tp);
        //System.out.println("Showing cell: "+c.getName());
        //iIgnoreValueChanged = false;
    }

//    private void makeDaughterDisplay(Cell c) {
//        iTimeInc = 0;
//        iPlaneInc = 0;
//        getTimeAndPlane(c);
//        if (iImageTime < 1 || iImagePlane < 1) return;
//        doDaughterDisplayWork((Cell)c.getParent(), c);
//    }

    @SuppressWarnings("unused")
	private void doDaughterDisplayWork(Cell parent, Cell selectedDaughter) {
        //Cell parent = (Cell)c.getParent();
        //System.out.println("doDaughterDisplayWork: " + parent + CS + selectedDaughter);
        if (parent == null) System.out.println("*******NULL PARENT");
        if (!isTracking()) return;
        if (parent.getName() == ROOTNAME) return;
        if (iTimeInc != 0) return;
        int k = parent.getChildCount();
        if (k <= 1) return;
        Cell anteriorCell = (Cell)parent.getChildAt(0);
        Cell posteriorCell = (Cell)parent.getChildAt(1);
        //System.out.println("makeDaughterDisplay: " + anteriorCell + CS + posteriorCell);
        if (selectedDaughter != null) {
            if (selectedDaughter == anteriorCell) iTrackPosition = ImageWindow.ANTERIOR;
            else iTrackPosition = ImageWindow.POSTERIOR;
        }
        Cell save = iCurrentCell;
        if (iTrackPosition == ImageWindow.ANTERIOR) 
        	iCurrentCell = anteriorCell;
        else iCurrentCell = posteriorCell;
        if (iCurrentCell == null) {
            iCurrentCell = save;
            return;
        }

        //iNoTrack.setText(NOTRACK);

        //Vector nuclei = iNucleiMgr.getNucleiRecord()[iImageTime + iTimeInc - 1];
        //Vector nuclei = (Vector)iNucleiMgr.getNucleiRecord().elementAt(iImageTime + iTimeInc - 1);
        /*
         * TODO
         * types from NucleiMgr
         */
        Vector nuclei = iNucleiMgr.getElementAt(iImageTime + iTimeInc - 1);
        String currentName = parent.getName();
        StringBuffer dummy = new StringBuffer();
        Nucleus anterior = NucUtils.getCurrentCellData(nuclei, anteriorCell.getName());
        Nucleus posterior = NucUtils.getCurrentCellData(nuclei, posteriorCell.getName());
        //System.out.println("makeDaughterDisplay: anterior: " + anterior);
        //System.out.println("makeDaughterDisplay: posterior: " + posterior);
        if (anterior != null && posterior != null) {
            makeAndSetSpecialEffects(anterior, posterior);
        }
    }

    private void makeAndSetSpecialEffects(Nucleus anterior, Nucleus posterior) {
        //System.out.println("makeAndSetSpecialEffects1: " + anterior);
        //new Throwable().printStackTrace();
        //System.out.println("makeAndSetSpecialEffects1: " + posterior);
        Object [] se = new Object[8];
        if (iTrackPosition == ImageWindow.ANTERIOR) {
            se[0] = new Integer(anterior.x);
            se[1] = new Integer(anterior.y);
            se[2] = new Integer((int)(anterior.z + HALFROUND));
            se[3] = new Integer(posterior.x);
            se[4] = new Integer(posterior.y);
            se[5] = new Integer(posterior.size/2);
            se[6] = new Integer((int)(posterior.z + HALFROUND));
            se[7] = posterior.identity;
        } else {
            se[0] = new Integer(posterior.x);
            se[1] = new Integer(posterior.y);
            se[2] = new Integer((int)(posterior.z + HALFROUND));
            se[3] = new Integer(anterior.x);
            se[4] = new Integer(anterior.y);
            se[5] = new Integer(anterior.size/2);
            se[6] = new Integer((int)(anterior.z + HALFROUND));
            se[7] = anterior.identity;
        }
		if (iImgWin!=null)
		    iImgWin.setSpecialEffect(se);

    }


    private void trackingActionsOnCurrentCellChange() {
        //System.out.println("trackingActionsOnCurrentCellChange");
        // set iImageTime and iTimeInc cleanly
        // set iImagePlane and iPlaneInc cleanly
        // assume initially that the transition was to a previous time
        int time = iImageTime + iTimeInc;
        iImageTime = iCurrentCell.getTime();
        iTimeInc = time - iImageTime;
        int plane = iImagePlane + iPlaneInc;
        iImagePlane = iCurrentCell.getPlane();
        iPlaneInc = plane - iImagePlane;
    }

    public void setCurrentCell(Cell c, int time, int source) {
    	if(iCellsByName == null)
    		return;
    	
        //println("AceTree.setCurrentCell: " + c + CS + time + CS + source);
        if (c == null) {
        	if (source == CONTROLCALLBACK) {
        		//System.out.println("AceTree.setCurrentCell CONTROLCALLBACK.");
        		showSelectedCell(c, time);
        	}
        	return;
        }
        //println("AceTree.setCurrentCell: " + c.getName() + CS + time + CS + source);
        if(iImgWin != null)
        	iImgWin.setSpecialEffect(null);
        //System.out.println("setCurrentCell: " + c + CS + time + CS + source);
        if (source != RIGHTCLICKONEDITIMAGE && !iCellsByName.containsKey(c.getName())) {
            //System.out.println("setCurrentCell:2 " + c + CS + time + CS + source);
            return;
        }
        
        // Look at this for making force named cell active
        if (source == RIGHTCLICKONIMAGE) {
        	//System.out.println("AceTree.setCurrentCell RIGHTCLICKONIMAGE.");
            Cell old = iCurrentCell;
            iCurrentCell = c; //(Cell)iAncesTree.getCellsByName().get(cellName);
            trackingActionsOnCurrentCellChange();
            iAceTree.forceTrackingOn();
            //System.out.println("Right click on image cell: "+c.getName());
            showTreeCell(iCurrentCell);
            String s = makeDisplayText();
            iText.setText(s);
            //println("setCurrentCell:3 " + iCurrentCell + CS + old);
		    if(iImgWin != null)
		    	iImgWin.updateCurrentCellAnnotation(iCurrentCell, old, -1);
            Cell parent = (Cell)c.getParent();
            if (iTimeInc == 0 && !parent.getName().equals("P0")) 
            	doDaughterDisplayWork((Cell)c.getParent(), c);
            else {
                if (iCurrentCell.isAnterior()) iTrackPosition = ImageWindow.ANTERIOR;
                else iTrackPosition = ImageWindow.POSTERIOR;
            }
            updateDisplay();
        } 
        else if (source == RIGHTCLICKONEDITIMAGE) {
        	//System.out.println("AceTree.setCurrentCell RIGHTCLICKONEDITIMAGE.");
            //println("setCurrentCell:4 ");
            Cell old = iCurrentCell;
            iCurrentCell = c;
            trackingActionsOnCurrentCellChange();
            iAceTree.forceTrackingOn();
            showTreeCell(iCurrentCell);
            String s = "added cell in progress";
	    if(iImgWin != null)
	    	iImgWin.updateCurrentCellAnnotation(iCurrentCell, old, -1);
            iText.setText(s);
            updateDisplay();

        } else if (source == LEFTCLICKONTREE) {
            showSelectedCell(c, time);
            updateDisplay();
            //if (iImage3D != null) iImage3D.insertContent(getImageTitle());

        } else if (source == RIGHTCLICKONTREE) {
            //System.out.println("setCurrentCell RIGHTCLICKONTREE: " + c.getName() + CS + time);
            if (c.isAnterior())
            	iTrackPosition = ImageWindow.ANTERIOR;
            else iTrackPosition = ImageWindow.POSTERIOR;
            showSelectedCell(c, time);
            //if (iImage3D != null) iImage3D.insertContent(getImageTitle());
        } else if (source == CONTROLCALLBACK) {
            showSelectedCell(c, time);
        } else if (source == NEXTTIME) {
            iImageTime = time;
            iTimeInc = 0;
            Cell currentCellSave = iCurrentCell;
            doDaughterDisplayWork(iCurrentCell, null);
            if (currentCellSave != iCurrentCell) {
                trackingActionsOnCurrentCellChange();
		if(iImgWin!=null)
		    iImgWin.updateCurrentCellAnnotation(iCurrentCell, currentCellSave, time);
            }
            showTreeCell(iCurrentCell);
        } else if (source == PREVTIME) {
            //Vector nuclei1 = (Vector)iNucleiMgr.getNucleiRecord().elementAt(iImageTime + iTimeInc);
            //Vector nuclei0 = (Vector)iNucleiMgr.getNucleiRecord().elementAt(iImageTime + iTimeInc - 1);
        	/*
        	 * TODO
        	 * NucleiMgr types
        	 */
        	Vector nuclei1 = iNucleiMgr.getElementAt(iImageTime + iTimeInc);
            Vector nuclei0 = iNucleiMgr.getElementAt(iImageTime + iTimeInc - 1);
        	
        	Nucleus n = NucUtils.getParent(nuclei0, nuclei1, iCurrentCell.getName());
            Cell currentCellSave = iCurrentCell;
            if (n != null) {
                iCurrentCell = (Cell)iAncesTree.getCellsByName().get(n.identity);
                if (iCurrentCell == null) {
                	
                    iCurrentCell = currentCellSave;
                    return;
                }
                if (currentCellSave != iCurrentCell) {
                    trackingActionsOnCurrentCellChange();
				    if(iImgWin!=null)
				    	iImgWin.updateCurrentCellAnnotation(iCurrentCell, currentCellSave, time);
                }
                showTreeCell(iCurrentCell);
            } else {
                iTrackPosition = ImageWindow.NONE;
                iCurrentCell = null;
                showTreeCell(iRoot);
            }

        }
    }

    /**
     * does the work required by the cell selection control
     * @param c Cell the cell desired
     * @param requestedTime int the time index where it is to be shown
     * @param v2 vector of nuclei at this time point
     */
    @SuppressWarnings("unused")
	private void showSelectedCell(Cell c, int requestedTime) {
    	if (iImgWin == null)
    		return;
    	
        if (c == null) {
            iImageTime = requestedTime;
            iTimeInc = 0;
            iImagePlane = 15;
            iPlaneInc = 0;
            iCurrentCell = iRoot;
            showTreeCell(iCurrentCell);
            updateDisplay();

        	return;
        }

        String name = c.getName();
        //System.out.println("Selected name: "+name);
        Nucleus n = iNucleiMgr.getCurrentCellData(name, requestedTime);
        //if (n == null) {
        //    System.out.println("cell " + c + " not present at time " + requestedTime);
        //    return;
        //}
        if (n != null) {
            Cell old = iCurrentCell;
            iImageTime = c.getTime();
            iTimeInc = requestedTime - iImageTime;
            iImagePlane = (int)(n.z + HALFROUND);
            iPlaneInc = 0;
            iCurrentCell = c;
            //System.out.println("showSelectedCell: " + iCurrentCell + CS + c + CS + iImagePlane + CS + iPlaneInc);
            //if (iImageTime < 1 || iImagePlane < 1) return;
            if (iImageTime < 1)
            	return;
            iCurrentCellPresent = true;
            if (iCurrentCell.isAnterior())
            	iTrackPosition = ImageWindow.ANTERIOR;
            else iTrackPosition = ImageWindow.POSTERIOR;
            showTreeCell(iCurrentCell);

            //if (iTimeInc == 0) makeDaughterDisplay(iCurrentCell);

            int baseTime = c.getTime(); //Integer.parseInt(sa[0]);
            iImgWin.updateCurrentCellAnnotation(iCurrentCell, old, -1);
            updateDisplay();
        }
        else {
            iImageTime = requestedTime;
            iTimeInc = 0;
            iImagePlane = 15;
            iPlaneInc = 0;
            iCurrentCell = c;
            showTreeCell(iCurrentCell);
            updateDisplay();
        }

    }

    // NOT USED
    /*
    private void incTime(int inc) {
        if (inc > 0) {
            if (iImageTime + iTimeInc < iEndingIndex)
            	iTimeInc++;
        }
        else if (iImageTime + iTimeInc > iStartingIndex)
        	iTimeInc--;
    }
    */

    private void incPlane(int inc) {
        if (inc > 0) {
        	if (iImagePlane + iPlaneInc < iPlaneEnd)
        		iPlaneInc += inc;
        }
        else if (inc < 0) {
        	if (iImagePlane + iPlaneInc > 1)
        		iPlaneInc += inc;
        }
        	
    }

    private void handleSisterRequest() {
        //if (iImgWin.getSpecialEffect() == null)
        int k = iCurrentCell.getSiblingCount();
        if (k != 2) return;
        Cell parent = (Cell)iCurrentCell.getParent();
        Cell anteriorCell = (Cell)parent.getChildAt(0);
        Cell posteriorCell = (Cell)parent.getChildAt(1);
        int now = iImageTime + iTimeInc;
        Nucleus anterior = iNucleiMgr.getCurrentCellData(anteriorCell.getName(), now);
        Nucleus posterior = iNucleiMgr.getCurrentCellData(posteriorCell.getName(), now);
        if (anterior == null || posterior == null) {
            System.out.println("sister not present");
            return;
        }
        makeAndSetSpecialEffects(anterior, posterior);
    }

    /**
     * called from ImageWindow when user clicks a nucleus
     * @param e MouseEvent detected in ImageWindow
     */
    public void mouseMoved(MouseEvent e) {
        String s = POSITION + e.getX() + ", " + e.getY();
       iText2.setText(s);
    }

    public void cellAnnotated(String name) {
        iText3.setText(name);
    }
    ///////////////////// editing ///////////////////////////////////

    @SuppressWarnings("unused")
	public void saveNuclei(File file) {
    	// File name already has .zip extension
    	// Done in AceMenuBar
        System.out.println("saveNuclei: " + file);
        //iEditLog.showMe();
        NucZipper nz = new NucZipper(file, iNucleiMgr);
        nz = null;
        //iEditLog.setModified(false);
        System.out.println("Finished saving nuclei.");
    }

    public void viewNuclei() {
        new NucEditDialog(this, iMainFrame, false);
    }

    //public void addNucleus() {
        //new NucAddDialog(false);
    //    new AddNucToRoot(false);
    //}

    public void relinkNucleus() {
        int time = iImageTime + iTimeInc;
        if (iNucRelinkDialog == null) {
	        iNucRelinkDialog = new UnifiedNucRelinkDialog(this, iMainFrame, false, iCurrentCell, time);
			// this var is now always set to inucrelink or null
			//ass old addone, now adjust dialog does not
			iImgWin.iDialog2 = iNucRelinkDialog;
        }
        else
        	iNucRelinkDialog.setVisible(true);
    }

    public void killCell(int x) {
    	println("killCell, ");
        //if (iTimeInc != 0 && iPlaneInc != 0) return;
        //Vector nuclei = iNucleiMgr.getNucleiRecord()[iImageTime + iTimeInc - 1];
    	int currenttime=iImageTime + iTimeInc - 1;
        //Vector nuclei = (Vector)iNucleiMgr.getNucleiRecord().elementAt(currenttime);
    	Vector nuclei = iNucleiMgr.getElementAt(currenttime);
    	
    	String name = iCurrentCell.getName();
        Nucleus n = null;
        for (int j = 0; j < nuclei.size(); j++) {
            n = (Nucleus)nuclei.elementAt(j);
            if (!n.identity.equals(name))
            	continue;
            n.status = Nucleus.NILLI;
            break;
        }
        prevImage();

        // added rebuild code
        clearTree();
        buildTree(true);
        // add find self at previous time code from relink
        AncesTree ances = getAncesTree();
		Hashtable h = ances.getCellsByName();
		Cell c = (Cell)h.get(name);
		
		//set active cell to start time to aid review
		if(c!=null){
			iAceTree.setStartingCell(c, currenttime);
			System.out.println("Setting starting in delete key"+c);
		}
    }

    public void killDeepNucs() {
    	new KillDeepNucsDialog(this, iMainFrame, true);
    }

    public void killDeepNucs(int zLim) {
        Vector nucRec = iNucleiMgr.getNucleiRecord();
        for (int i=0; i < nucRec.size(); i++) {
        	Vector nuclei = (Vector)nucRec.get(i);
        	for (int j=0; j < nuclei.size(); j++) {
        		Nucleus n = (Nucleus)nuclei.get(j);
        		if (n.status == Nucleus.NILLI) continue;
        		if (n.z < zLim) continue;
        		println("killDeepNucs, " + i + CS + n);
        		n.status = Nucleus.NILLI;
        	}
        }
        clearTree();
        buildTree(true);

    }

    public void testWindow() {
    	new TestWindow(this, iMainFrame, false);
    }

    public void killCells() {
        int time = iImageTime + iTimeInc;
        new KillCellsDialog(this, iMainFrame,true, iCurrentCell, time, iEditLog);
    }

    public void pausePlayerControl() {
        iPlayerControl.pause();
    }

    public void setEndTime() {
        new SetEndTimeDialog(this, iMainFrame, true);
    }

    public void incrementEndTime() {
        setEndingIndex(++iEndingIndex);
    }

    @SuppressWarnings("unused")
	public void setEndingIndex(int endTime) {
        iEndingIndex = endTime;
        iNucleiMgr.setEndingIndex(endTime);
        clearTree();
        Hashtable oldHash = iAncesTree.getCellsByName();
        buildTree(true);
        Hashtable newHash = iAncesTree.getCellsByName();
        String name = null;
        Cell c = null;
        Enumeration newKeys = newHash.keys();
        Vector newNames = new Vector();
        while(newKeys.hasMoreElements()) {
            name = (String)newKeys.nextElement();
            if(oldHash.containsKey(name)) continue;
            c = (Cell)newHash.get(name);
            newNames.add(name);
        }
        Collections.sort(newNames);
        for (int i=0; i < newNames.size(); i++) {
            c = (Cell)newHash.get(newNames.elementAt(i));
            //System.out.println(c.toString(0));
        }
    }

    public void undo() {
        iEditLog.append("UNDO");
        iNucleiMgr.restoreNucleiRecord();
        iNucleiMgr.clearAllHashkeys();
        clearTree();
        buildTree(true);
        setStartingCell((Cell)iRoot.getFirstChild(), iStartTime);
        iEditLog.setModified(true);

    }

    ///////////////////// editing end ///////////////////////////////////

    public void exit() {

       //JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE),
        //         JOptionPane.OK_CANCEL_OPTION);
        //pane.set.Xxxx(...); // Configure
        if (iEditLog.getModified()) {
            Object[] options = { "OK", "CANCEL" };
            String msg = "Warning, you have unsaved edits.\nClick OK to continue.";
            int choice = JOptionPane.showOptionDialog(null, msg, "Warning",
                     JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                             null, options, options[0]);
            if (choice == 0) iWinEvtHandler.windowClosing(null);
        } else {
            iWinEvtHandler.windowClosing(null);
        }
    }

    public void editTraverse() {
        iEditTraverse = new EditTraverse(iCurrentCell);
    }

    public void setEditTraverseNull() {
        iEditTraverse = null;
    }

    public void setFocusHome() {
        iHome.grabFocus();
    }

    public void ancestral() {
        //iCurrentCell.setLateTime(250);
    	if (iBookmarkDialog != null)
    		iSulstonTree = new SulstonTree(this, "Ancestral Tree", iCurrentCell, true, iBookmarkJList.getModel());
        //iShowTriangle = !iShowTriangle;
    	else
    		iSulstonTree = new SulstonTree(this, "Ancestral Tree", iCurrentCell, true, null);
    }

    public void canonical() {
        //System.out.println("AceTree.test");
        if (iCanonicalTree == null) iCanonicalTree = CanonicalTree.getCanonicalTree();
        new AuxFrame(this, "Sulston Tree", iCanonicalTree);
    }

    public void vtree() {
	    new VTree();
    }

    public void test() {
        //System.out.println("AceTree.test");
        new AceTreeHelp("/org/rhwlab/help/messages/J3Derror.html", 600, 300);

    }

    public CanonicalTree getCanonicalTree() {
        return iCanonicalTree;
    }

    public void threeDview() {
        String s = getImageTitle();
        if (iUseStack == 1)
        	s = getTifPathWithPrefix();
        System.out.println("AceTree passing to Image3D title: "+s);
        try {
    		iImage3D = new Image3D(this, s);
        } catch(NoClassDefFoundError ee) {
        	new Java3DError(iMainFrame);
        }

    }

    public String getTifPathWithPrefix() {
    	return iTifPrefix+makeImageName();
    }

    public void allCentroidsView() {
        String s = getImageTitle();
        new ImageAllCentroids(this, s);

    }
    /*
    public String getImageTitle() {
        String s = makeImageName();
        int k = s.lastIndexOf("-");
        s = s.substring(0, k);
        String s2 = iTifPrefix;
        int j = s2.lastIndexOf(C.Fileseparator);
        if (j > 0) s2 = s2.substring(j + 1);
        return s2 + s;
    }
    */

	  public String getImageTitle() {
			String s = makeImageName();
			if (s!=null) {
			    if(iUseStack == 0)
				{
				    int k = s.lastIndexOf("-");
				    if (k != -1)
				    	s = s.substring(0, k);
				}
			}
			//System.out.println("AceTree.java 2692: " + s);
			String s2 = iTifPrefix;
			//System.out.println("AceTree.java 3243: " + iTifPrefix);
			int j = s2.lastIndexOf(C.Fileseparator);
			if (j > 0)
				s2 = s2.substring(j + 1);
			
			//System.out.println("AceTree.java 2695: " + s2 + s);
			return s2 + s;
    }

  	/*
    public void image3DOff() {
        iImage3D = null;
    }
    */

  	/*
    public void image3DSave(boolean saveIt) {
        Image3D.setSaveImageState(saveIt);
        if (!saveIt) return;
        if (iImage3D == null) {
            System.out.println("no active image3d");
            threeDview();
            //return;
        } else {
            iImage3D.saveImage();
        }
        //image3DSave(saveIt);
    }
    */

    // called by Image3D actionPerformed() 
    // argument will be TRUE if AceTree should start saving movie/rendering images
    // argument will be FALSE if AceTree should stop    "       "
    public void image3DSave(boolean saveIt) {
        Image3D.setSaveImageState(saveIt);
        if (!saveIt) return;
        if (iImage3D == null) {
            System.out.println("no active image3d");
            threeDview();
            //return;
        } else {
            //iImage3D.saveImage();
        	//println("image3DSave, " + iImgWin.getTitle());
        	//println("image3DSave, " + iImgWin.getName());
        	String name = iImgWin.getTitle();
        	name = name.substring(0, name.length() - 8);
        	name = name.substring(4, name.length());
            iImage3D.offScreenRendering(name);
        }
    }

    public boolean hasActiveImage3D() {
        return iImage3D != null;
    }


    public void image2DSave(boolean saveIt) {
        iImgWin.setSaveImageState(saveIt);
        iImgWin.saveImageIfEnabled();
    }

    public void zoomView() {
    	iImgWin.zoomView();
    }


    private void delay(int n) {
        long t = System.currentTimeMillis();
        long e = t + n;
        while (t < e) t = System.currentTimeMillis();

    }

    public boolean nextImage() {
		if(iImgWin!=null)
		    iImgWin.setSpecialEffect(null);
        boolean b = nextTime();
        
        if (iImage3D != null && iImage3D.getImage3DFrame().isVisible()) {
        	iImage3D.insertContent(getImageTitle());
        	// New 3D Viewer Code, to update upon clicking next
            addNext3D();
        }
        
        updateDisplay();
        return b;
    }
    
    public boolean nextImageFast() {
    	if(iImgWin!=null)
		    iImgWin.setSpecialEffect(null);
        boolean b = nextTime();
        for (int i = 0; i < 4 & b; i++)
        	b = nextTime();
        
        if (iImage3D != null && iImage3D.getImage3DFrame().isVisible()) {
        	iImage3D.insertContent(getImageTitle());
        	// New 3D Viewer Code, to update upon clicking next
            addNext3D();
        }
        
        updateDisplay();
        return b;
    }

    public boolean prevImage() {
        boolean b = prevTime();
        
        if (iImage3D != null) {
        	iImage3D.insertContent(getImageTitle());
        }

        // New 3D Viewer Code, to update upon clicking previous
        addNext3D();
        
        updateDisplay();
        return b;
    }
    
    public boolean prevImageFast() {
    	if(iImgWin!=null)
		    iImgWin.setSpecialEffect(null);
        boolean b = prevTime();
        for (int i = 0; i < 4 & b; i++)
        	b = prevTime();
        
        if (iImage3D != null) {
        	iImage3D.insertContent(getImageTitle());
        }

        // New 3D Viewer Code, to update upon clicking previous
        addNext3D();
        
        updateDisplay();
        return b;
    }

    public void updateUseZip(int useZip) {
    	iUseZip = useZip;
    	println("updateUseZip, " + useZip);
    	ImageWindow.cUseZip = useZip;
    	iNucleiMgr.getConfig().iUseZip = useZip;
    }

    @SuppressWarnings("unused")
	public void exportNewick() {
        try {
            Newick newick = new Newick(iRoot);
        } catch(Exception e) {
            System.out.println("ATVapp unavailable");
        } catch(NoClassDefFoundError ee) {
            System.out.println("you need to get ATVTree.jar");
            new AceTreeHelp("/org/rhwlab/help/messages/ATVerror.html");
        }
    }
/*
    public Parameters getParameters() {
        //System.out.println("getParameters: " + iParameters);
        return iParameters;
    }
*/
    public EditLog getEditLog() {
        return iEditLog;
    }

    public Log getDebugLog() {
        return iDLog;
    }

    public int getTimeInc() {
        return iTimeInc;
    }

    public int getPlaneInc() {
        return iPlaneInc;
    }

    public int getUseStack() {
        return iUseStack;
    }
    

    public int getImageTime() {
        return iImageTime;
    }

    public int getImagePlane() {
        return iImagePlane;
    }

    public boolean getShowCentroids() {
        return iShowCentroids;
    }

    public void setShowCentroids(boolean show) {
        iShowCentroids = show;
    }

    public boolean getShowAnnotations() {
        return iShowAnnotations;
    }

    public void setShowAnnotations(boolean show) {
        //println("setShowAnnotations: " + show);
        //new Throwable().printStackTrace();
        iShowAnnotations = show;
        if (iShow != null) {
            if (show) iShow.setText(HIDE);
            else iShow.setText(SHOW);
        }
    }

    public Cell getCurrentCell() {
        return iCurrentCell;
    }

    public JFrame getMainFrame() {
        return iMainFrame;
    }

    public Object getDispProps3D() {
        return iDispProps3D;
    }

    public Object getDispProps3D2() {
        return iDispProps3D2;
    }

    public Object getDispProps3D2Z() {
        return iDispProps3D2Z;
    }

    public void setDispProps3D(Object obj) {
        iDispProps3D = obj;
    }

    public void setDispProps3D2(Object obj) {
        iDispProps3D2 = obj;
    }

    public void setDispProps3D2Z(Object obj) {
        iDispProps3D2Z = obj;
    }

    public Cell getRoot() {
        return iRoot;
    }

    public boolean getFullGUI(){
	return fullGUI;
    }
    public double getiZPixRes(){
	return iZPixRes;
    }

    public boolean isTracking() {
        //System.out.println("isTracking: " + iTrackPosition + CS + (iTrackPosition != ImageWindow.NONE));
        return iTrackPosition != ImageWindow.NONE;
    }

    public void setOrientation(String orientation) {
        iOrientation = orientation;
        System.out.println("setOrientation: " + iOrientation);
    }

    public String getOrientation() {
        System.out.println("getOrientation: " + iOrientation);
        return iOrientation;
    }

    public Hashtable getNucleiMgrHash() {
        return iNucleiMgrHash;
    }

    public ImageWindow getImageWindow() {
    	//System.out.println("ImageWindow requested.");
        return iImgWin;
    }
    
    public void showImageWindow() {
    	if (iImgWin != null)
    		iImgWin.setVisible(true);
    }

    private void debugShow(int testTime) {
        System.out.println();
        System.out.println("setup for edit");
        Vector nuclei = null;
        //nuclei = (Vector)iNucleiMgr.getNucleiRecord().elementAt(testTime - 1);
        nuclei = iNucleiMgr.getElementAt(testTime - 1);
        
        Enumeration e = nuclei.elements();
        System.out.println("time: " + (testTime - 1));
        while (e.hasMoreElements()) {
            System.out.println(e.nextElement());
        }

        //nuclei = (Vector)iNucleiMgr.getNucleiRecord().elementAt(testTime);
        nuclei = iNucleiMgr.getElementAt(testTime);
        
        e = nuclei.elements();
        System.out.println("time: " + (testTime));
        while (e.hasMoreElements()) {
            System.out.println(e.nextElement());
        }

    }
    public String getiTifPrefix(){
    	return iTifPrefix;
    }


    public final static int
     LEFTCLICKONTREE = 1
    ,RIGHTCLICKONTREE = 2
    ,RIGHTCLICKONIMAGE = 3
    ,CONTROLCALLBACK = 4
    ,NEXTTIME = 5
    ,PREVTIME = 6
    ,RIGHTCLICKONEDITIMAGE = 7
    ;

    private final static int
     WIDTH = 330
    ,HEIGHT200 = 200
    ,HEIGHT100 = 100
    ,HEIGHT75 = 75
    ,HEIGHT30 = 30
    ,XINC = 8
    ,YINC = 12
    ;


    final public static String
     PARAMETERS = "parameters"
    ,POSITION = "Mouse position: "
    ,SPACES15 = "               "
    ,TITLE = "AceTree"
    ,HELPMSG = "you must provide file: "
    ,SEP = ", "
    ,ROOTNAME = "ROOT"
    ;

    final public static String
    NEXTT = "Next"
   ,PREV = "Prev"
   ,UP   = "Up Z"
   ,DOWN = "Down Z"
   ,HOME = "Cell Birth"
   ,SHOW = "Show Names"
   ,SHOWC = "Show Cells"
   ,HIDE = "Hide Names"
   ,HIDEC = "Hide Cells"
   ,CLEAR = "Clear Names"
   ,COPY = "Copy"
   ,EDIT = "Edit"
   ,NOTRACK = "No Track"
   ,TRACK = "Track"
   ,SISTER = "Sister"
   ,COLORTOGGLE = "Channel"
   ,CS = ", "
   ,DEPTHVIEWS="Depth View"
   ;

    /*
    private static final String [] configParams = {
            "zipFileName"
           ,"tif directory"
           ,"tifPrefix"
           ,"nuclei directory"
           ,"root name"
           ,"starting index"
           ,"ending index"
           ,"use zip"
           ,"namingMethod"
    };
    */

    protected static final int
         ZIPFILENAME = 0
        ,TIFDIRECTORY = 1
        ,TIFPREFIX = 2
        ,NUCLEIDIRECTORY = 3
        ,ROOTNAMEI = 4
        ,STARTINGINDEX = 5
        ,ENDINGINDEX = 6
        ,USEZIP = 7
        ,NAMINGMETHOD = 8
        ;

    private static final float
         HALFROUND = 0.5f
        ;

    public void debugTest(boolean b) {
        iDebugTest = b;
    }

    protected void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        //iMainFrame = new JFrame(TITLE);
        iMainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        //JPanel newContentPane = this;
        iMainFrame.getContentPane().setLayout(new BorderLayout());
        //int hInput = iInputCtrl.getY();
        //System.out.println("hInput="+hInput);
        int height = HEIGHT200 + HEIGHT100 + HEIGHT100 + HEIGHT100 + 2*HEIGHT30;
        this.setMinimumSize(new Dimension(WIDTH, height));
        this.setOpaque(true); //content panes must be opaque

        // make this AceTree instance the content pane
        //iAceMenuBar = new AceMenuBar(this);
        iMainFrame.setJMenuBar(iAceMenuBar);
		// iMainFrame.getContentPane().add(this, BorderLayout.NORTH);
		iMainFrame.getContentPane().add(this, BorderLayout.CENTER);
        iMainFrame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = iMainFrame.getSize();
        //System.out.println("windowSize: " + windowSize);
        iMainFrame.setLocation(Math.max(0,(screenSize.width -windowSize.width)/2),
                Math.max(0,(screenSize.height-windowSize.height)/2));
        iWinEvtHandler = new WindowEventHandler();
        iMainFrame.addWindowListener(iWinEvtHandler);

        iMainFrame.setFocusTraversalPolicy(
                new FocusControl(iHome
                        , iInputCtrl.getTimeField()
                        , iInputCtrl.getNameField()
                        ));
        iMainFrame.setVisible(true);
    }

    public void run(String arg0) {
        createAndShowGUI();
    }

    Orientation		iOrientationPanel;
    void showOrientation() {
    	iOrientationPanel = new Orientation();
    }

    Zafer1			iZafer1Panel;
    void showZafer1() {
    	iZafer1Panel = new Zafer1();
    }

    Juvenesence		iJuvenesencePanel;
    void showJuvenesence() {
    	iJuvenesencePanel = new Juvenesence();
    }

    Lazarus			iLazarusPanel;
    void showLazarus() {
    	iLazarusPanel = new Lazarus();
    }

    Siamese			iSiamesePanel;
    void showSiamese() {
    	iSiamesePanel = new Siamese();
    }

    DeathsAdjacencies 	iDeathsAdjacenciesPanel;
    void showDeathsAdjacencies() {
    	iDeathsAdjacenciesPanel = new DeathsAdjacencies();
    }

///////////////////////////////////////////////////////////////////////////////
///// stuff that gets modified for the AceTree object in EmbryoDB ///////////////////////
/////////////////////////////////////////////////////////////////////////////
    private boolean				iCmdLineRun;

    protected class WindowEventHandler extends WindowAdapter {
        @Override
		public void windowActivated(WindowEvent e) {
            iHome.requestFocusInWindow();
        }
        @Override
		public void windowClosing(WindowEvent e) {
            System.out.println("AceTree shutdown " + new GregorianCalendar().getTime() + CS + iCmdLineRun);
            iMainFrame.dispose();
            if (iImgWin != null) 
            	iImgWin.dispose();
            if (iCmdLineRun) 
            	System.exit(0);
        }

    }

///////////////////////////////////////////////////////////////////////////////
///// end of stuff that gets modified for the AceTree object in EmbryoDB ///////////////////////
/////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
        System.out.println("AceTree launched: " + new GregorianCalendar().getTime());
        boolean setui=false;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) { // if not on mac   
            ;
		} else { 
            try {
                for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        UIManager.put("control", new Color(240,240,255));
                        setui=true;
                        break;
                    }
                }
                if(!setui){
                    UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
                }
        	
            } catch (UnsupportedLookAndFeelException e) {
                // handle exception
            } catch (ClassNotFoundException e) {
                // handle exception
            } catch (InstantiationException e) {
                // handle exception
            } catch (IllegalAccessException e) {
                // handle exception
            }
        }

		//I removed this but not sure what its for -as 2013
		//ManifestX.reportAndUpdateManifest();
        String config = null;

        AceTree ot;
        if (args.length > 0) {
            System.out.println("AceTree args[0]: " + args[0]);
            ot = getAceTree(args); //new AceTree(args[0]);
        }
        else ot = getAceTree(null);
        ot.run("");
        ot.debugTest(false);
        ot.iCmdLineRun = true;
        System.out.println("main exiting");
    }

    private static void println(String s) {System.out.println(s);}
    private void newLine() {System.out.println(""); }
}