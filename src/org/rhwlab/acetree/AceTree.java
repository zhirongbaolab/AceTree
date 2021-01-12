/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */
package org.rhwlab.acetree;

// import org.test.Display3D;

import javax.swing.UIManager.*;
import javax.swing.UIManager;
import javax.swing.BoxLayout;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.ImageIcon;
import java.awt.Color;

import org.rhwlab.acetree.ControlCallback;
import org.rhwlab.help.AceTreeHelp;
import org.rhwlab.help.GeneralStartupError;
import org.rhwlab.help.TestWindow;
import org.rhwlab.image.*;
//import org.rhwlab.image.EditImage;
//import org.rhwlab.image.EditImage3;

import org.rhwlab.image.management.ImageConversionManager;
import org.rhwlab.image.management.ImageManager;
import org.rhwlab.image.management.ImageWindowDelegate;
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
import javax.swing.event.TreeSelectionListener;
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
            implements ActionListener, ControlCallback, Runnable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    protected static AceTree  iAceTree;

    private Hashtable<String, NucleiMgr>   iNucleiMgrHash;

    private JLabel 	iSeriesLabel;
    private String      iConfigFileName;
    private JTree       iTree;
    private Cell        iRoot;
    private JTextPane   iText;
    private JTextPane   iText2;
    private JTextPane   iText3;
    public JFrame      iMainFrame;

    private boolean     iImgWinSet;
    public AncesTree   iAncesTree;
    private String      iOrientation;

    public AceMenuBar  iAceMenuBar;
    private EditLog     iEditLog;
    private Log         iDLog;

    protected WindowEventHandler  iWinEvtHandler;

    public JPanel iToolControls;
    private JButton     iCopy;
    private JButton     iShow;
    private JButton     iClear;
    private JButton     iNext;
    private JButton     iPrev;
    private JButton     iUp;
    private JButton     iDown;
    public JButton     iHome;
    private JButton		iDefault;
    private JButton     iShowC;
    private JButton     iTrack;
    private JButton     iSister;
    private JButton     iColorToggle;
    private JButton     maximumIntensityProjectionToggle;

    // increment values
    private int         iTimeInc;
    private int         iPlaneInc;

    private Cell        iCurrentCell;
    private boolean     iCurrentCellPresent;
    private int         iCurrentCellXloc;
    private int         iCurrentCellYloc;
    private float       iCurrentCellZloc;
    protected InputCtrl   iInputCtrl;

    public Hashtable    iCellsByName;

    private boolean     iShowAnnotations;
    private boolean     iShowSulstonAnnotations;
    private boolean     iShowAnnotationsSave;
    private boolean     iShowCentroids;
    public Integer      iTrackPosition;
    public Integer      iTrackPositionSave;
    private boolean     iIgnoreValueChanged;

    private Object      iDispProps3D;
    private Object      iDispProps3D2;
    private Object      iDispProps3D2Z;
    public boolean		iEditTools;
    private boolean     iCallSaveImage;

    private CellMovementImage iCellMovementImage;

    private int		iFileNameType;

    private CanonicalTree   iCanonicalTree;
    protected PlayerControl   iPlayerControl;
    private EditTraverse    iEditTraverse;

    private int         iColor;

    public	NucRelinkDialog			iNucRelinkDialog;
    public	AddOneDialog			iAddOneDialog;
    
    private BookmarkDialog		iBookmarkDialog;
    private JList				iBookmarkJList;
    private JButton				iAddActiveCell;
    
    private SulstonTree			iSulstonTree;
    
    private LinkedList<Integer>	iKeyQueue;

    //semaphores  merge from shooting_star_both_as AceTree source code
    public boolean             iATNucleiMgrLock;
    public boolean             iSNNucleiMgrLock;

    private static boolean fullGUI = false;

    // booleans to control tree selection changes and determine their origins
    boolean treeValueChangedFromMouseClick;
    boolean treeValueChangedFromImageChange;
    boolean treeValueChangedFromStartup;
    public boolean treeValueChangedFromEdit;

    /*
     * Revisions 10/2018 to image loading pipeline - grouping these variables together
     * because they figure heavily in the revised pipeline
     */
    public  ImageWindow iImgWin;
    private ImageWindowDelegate imageWindowDelegate;

    private Config configManager;
    private ImageManager imageManager;
    private NucleiMgr   iNucleiMgr;
    // ********************************************************************************

    protected AceTree() {
         this(null,false);
         System.out.println("AceTree empty constructor");
         //this("config.dat");
     }

    @SuppressWarnings("static-access")
	protected AceTree(String configFileName, boolean fullGUI) {
        super();

        //semaphores  merge from shooting_star_both_as AceTree source code
        System.out.println("Initializing NM locks");
        boolean success = SNLockNucleiMgr(false);
        if(success){
            System.out.println("SN unlocked");
        }
        success = ATLockNucleiMgr(false);

		AceTree.fullGUI=fullGUI;

        AceTree.iAceTree = this;
        this.iMainFrame = new JFrame(TITLE);

        // custom icon insert
        URL imageURL = PlayerControl.class.getResource("/images/icon2.gif");
        ImageIcon test=new ImageIcon(imageURL, "x");
        this.iMainFrame.setIconImage(test.getImage());


        this.iAceMenuBar = new AceMenuBar(this);

        iConfigFileName = configFileName;

        this.iNucleiMgrHash = new Hashtable<String, NucleiMgr>();

        iCurrentCell = null;
        iCurrentCellXloc = 0;
        iCurrentCellYloc = 0;

        setShowAnnotations(false);
        setShowSulstonAnnotations(true);
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
	        iCanonicalTree = CanonicalTree.getCanonicalTree();
	        iColor = 1;
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

        this.treeValueChangedFromMouseClick = false;
        this.treeValueChangedFromImageChange = false;
        this.treeValueChangedFromStartup = true;
        this.treeValueChangedFromEdit = false;
    }

    //semaphores  merge from shooting_star_both_as AceTree source code
    public boolean ATLockNucleiMgr(boolean lock){
        //Handles our semaphore for locking/unlocking NM
        System.out.println("AT is Locking or unlocking NucleiMgr");
        boolean success = false;
        if(lock){
            if(!iATNucleiMgrLock){
                //NM is not locked, lock it and return 1
                iATNucleiMgrLock = true;
                success = true;
            }
        }
        else{
            //If NM is to be unlocked, just unlock it and return 1
            iATNucleiMgrLock = false;
            success = true;
        }
        //If NM was already locked, return 0
        return success;
    }

    public boolean SNLockNucleiMgr(boolean lock){
        //Handles our semaphore for locking/unlocking NM
        System.out.println("SN is Locking or unlocking NucleiMgr");
        boolean success = false;
        if(lock){
            System.out.println("SN Locking NM");
            if(!iSNNucleiMgrLock){
                //NM is not locked, lock it and return 1
                iSNNucleiMgrLock = true;
                success = true;
            }
        }
        else{
            System.out.println("SN Unlocking NM");
            //If NM is to be unlocked, just unlock it and return 1
            iSNNucleiMgrLock = false;
            success = true;
        }
        //If NM was already locked, return 0
        return success;
    }

    public boolean getSNLock(){
        return iSNNucleiMgrLock;
    }

    public boolean getATLock(){
        return iATNucleiMgrLock;
    }

    /* Function: transformTitle
     * Usage: transformTitle();
     ---
     * Transfroms current image into appropriate file name string for processing.
     */
    public String transformTitle() {
        String oldTitle = getImageTitle();
        int index = oldTitle.indexOf('t');
        //System.out.println("Transforming title "+oldTitle);
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

    public PlayerControl getPlayerControl() {
    	return iPlayerControl;
    }

    public synchronized static AceTree getAceTree(String configFileName) {
		if (iAceTree == null) {
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


    /**
     * Revised 10/2018
     * @author Braden Katzman
     *
     * Builds modularized components:
     * - ConfigManager (contains NucleiConfig and ImageConfig - built in bringUpSeriesData)
     * - NucleiMgr (built in bringUpSeriesData)
     * - ImageManager (contains ImageConfig)
     * - ImageWindow
     *
     * @param configFileName
     */
    public void bringUpSeriesUI(String configFileName) {
        System.out.println("Bringing up series UI using file name: " + configFileName);
    	try {
    		newLine();

	        // garbage collection call - just to keep things tidy and running with enough memory - doesn't hurt
	        //  System.gc();

            System.out.println("*** Starting Nuclei configuration including: building NucConfig, NucManager, processing nuclei and assigning names ***");

	        // check to see if the series is already in the hash (this is an optimization to support faster loading of multiple datasets in a single runtime)
//	        String shortName = Config.getShortName(configFileName);
//	        NucleiMgr nucMgr = iNucleiMgrHash.get(shortName);

	        // in most cases, the user will only open a single dataset during a program execution, which means there will be no
            // no NucleiMgr in the hash. In those instances, we proceed by checking if the configuration file to build the NucMgr
            // exists, and then build it via bringUpSeriesUI
//	        if (nucMgr == null) {
	            // if not in hash then make sure there is such a file before proceeding

	            try {
	                FileInputStream fis = new FileInputStream(configFileName);
	                fis.close();
	            } catch(Exception fnfe) {
	                new AceTreeHelp("/org/rhwlab/help/messages/ConfigError.html", 200, 200);
	                return;
	            }

	            // if we've reached here, then the file exists, so open it up
	            int k = bringUpSeriesData(configFileName);

	            // if the return value from bringUpSeriesData wasn't 0, then a problem occurred opening the data, return
	            if (k != 0)  {
	                System.out.println("Coulnd't build NucleiMgr, not bringing up image series");
	                return; //problem finding the zipNuclei
                }
	        //}



            String shortName = Config.getShortName(configFileName);
	        iNucleiMgr = iNucleiMgrHash.get(shortName);

	        // if it's null, we've got problems
	        if (iNucleiMgr == null) {
	            System.out.println(HELPMSG + configFileName);
	            System.exit(1);
	        }

	        System.out.println("*** Nuclei configuration complete ***");
	        newLine();
	        System.out.println("*** Starting Image configuration including: building ImageWindow and its components, and configuring the image data for view ***");

            // build an ImageManager (file parsing, name logic, image related runtime variables
            if (configManager != null) {
                imageManager = new ImageManager(configManager.getImageConfig());
            } else {
                System.out.println("Can't build ImageManager in AceTree.bringUpSeriesUI() - Config didn't successfully build an ImageConfig\n\n\n");
            }

            iEditLog = iNucleiMgr.getEditLog();

            // in the revised loading pipeline, build tree will no longer bring up the image series as it did before
            // Therefore, once the buildTree operation is done, we are in the clear for bringing up the images
            buildTree(false);

            // ***** bring up the image series ************

            // first, let's build an ImageWindow with the first processed image
            if (this.iImgWin != null) {
                // close the existing image window if there is already one open
                this.iImgWin.setVisible(false);
                this.iImgWin.dispose();
            }
            this.iImgWin = new ImageWindow("",
                                            this.imageManager.bringUpImageSeries(),
                                            this.iPlayerControl,
                                            this.imageManager);
            // now that the image series has been processed by imageManager.bringUpImageSeries(), make a title for the window
            this.iImgWin.setTitle(this.imageManager.makeImageNameForTitle());

            // give the ImageWindow access to AceTree and NucleiMgr (Note: awful code practice, but remains because of legacy implementation - should be heavily refactored)
            this.iImgWin.setAceTree(this);
            this.iImgWin.setNucleiMgr(this.iNucleiMgr);

            // call showCurrentCell in case the starting time (the time point listed in the XML config) is different than
            // that found for the current cell when building the lineage tree
            if (this.iCurrentCell != null) {
                Vector v = new Vector();
                v.add("InputCtrl1");
                v.add(Integer.toString(this.imageManager.getCurrImageTime()));
                v.add("");
                controlCallback(v);
            }


            // update the PlayerControl tab with an color channel toggle that matches the config of this image series
            this.getPlayerControl().updateColorChannelToggleButton();

            // set the starting color toggle
            this.iColor = this.imageManager.getNextValidColorToggleIndex(-1);

            // CHECK HERE FOR THE RARE CASE OF A ZERO INDEXED IMAGE SERIES AND UPDATE THE TIMEINC VARIABLE IF NECESSARY
            if (this.imageManager.getCurrImageTime() == 0) {
                iTimeInc = 1;
            }

            // next, we'll build an ImageWindowDelegate with the ImageWindow just created so that it can facilitate annotating and saving
            this.imageWindowDelegate = new ImageWindowDelegate(this.iImgWin, this.imageManager, this.iNucleiMgr);


            // add the toolbar to the image window
            if (iBookmarkJList != null) {
                iImgWin.setBookmarkList(iBookmarkJList.getModel());
            }


            iImgWin.add(iToolControls,BorderLayout.SOUTH);
            iImgWin.pack();
            iImgWinSet = true;

            if (iCurrentCell != null && !iCurrentCell.getName().equals("P") && iRoot.getChildCount() > 0) {
                addMainAnnotation();
            }

            iShowCentroids = true;
            iShowC.setText(HIDEC);

            iAceMenuBar.setClearEnabled(true);

	        setShowAnnotations(true);
	        setShowSulstonAnnotations(true);
	        updateDisplay();
    	} catch (Throwable t) {
			new GeneralStartupError(getMainFrame(), t);
    	}

    	// System.gc();
    }

    public void bringUpSeriesUI(Config config) {
    	// Reset ImageWindow use stack flag
    	newLine();
        
    	String configFileName = config.iConfigFileName;
        System.out.println("bringUpSeriesUI: " + configFileName);
        // System.gc();
        // check to see if the series is already in the hash
        String shortName = Config.getShortName(configFileName);
        NucleiMgr nucMgr = iNucleiMgrHash.get(shortName); // usually null - nucMgr created below in bringUpSeriesData()
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
        iImgWin.setNucleiMgr(iNucleiMgr);
        
        //clearTree();
        setConfigFileName(configFileName);

        //clearTree();
        //iTree.updateUI();
        buildTree(false);
        setShowAnnotations(true);
        setShowSulstonAnnotations(true);

    }

    /**
     * Set up the series data. Process the nuclei via the NucleiMgr and build the AncesTree
     *
     * Revised 10/2018
     * @author Braden Katzman
     * 
     * @param configFileName - the file path of the config file
     * @ return int indicating success or failure
     */
    public int bringUpSeriesData(String configFileName) {
        System.out.println("accessing the data from nuc.zip in bringUpSeriesData");
        File fx = new File(configFileName);

        // UI stuff
        iSeriesLabel.setText(fx.getName());
        iMainFrame.setTitle(TITLE);

        // Under the revisions, we want to create a top level Config class which will build separate ImageConfig and NucleiConfig objects

        //System.out.println("building a config manager using file name" + configFileName);
        this.configManager = new Config(configFileName);
        // now we have respective NucleiConfig and ImageConfig through the reference to configManager

        // Let's build a NucleiMgr, then we'll move on the putting the images together (it will be a local copy that we then place in the NucleiMgr hash)
        NucleiMgr nucMgr = new NucleiMgr(configManager.getNucleiConfig()); // post 10/2018 revisions
        // at this point, the nuclei have been read into the system

        if (!nucMgr.iGoodNucleiMgr) {
            return -1;
        }

        // if we've reached here, the NucMgr is good to go, so we can process the nuclei (set the successors and build the AncesTree object)
        nucMgr.processNuclei(true); // post 10/2018 revisions

        String config = configManager.getShortName();
        println("bringUpSeriesData, " + config);

        // Previously, there was a way of saving already built NucleiMgr's so that they could be reaccessed
        // multiple times in a program instance (if other embryos were opened and then returned to). The messiness
        // involved in maintaining does not outweigh the time spent to just rebuild the nucmgr when an dataset is opened.
        // Here, we keep some of the hooks that were used to maintain the nucmgr just for ease of implementation. If
        // the nucmgr is already here, remove it and add the new one (who knows, maybe it was changed?)
        if (!iNucleiMgrHash.containsKey(config)) {
            iNucleiMgrHash.remove(config);
		    if(fullGUI)
		    	iAceMenuBar.addToRecent(config);
        }
        // always put the newly built nucmgr in the hash
        iNucleiMgrHash.put(config, nucMgr);
        
        // System.gc();
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
        
        // System.gc();
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
        // System.gc();
    }

    @SuppressWarnings("unused")
	public void clearAll() {
        Enumeration<String> e = iNucleiMgrHash.keys();
        while (e.hasMoreElements()) {
            NucleiMgr nm = iNucleiMgrHash.get(e.nextElement());
            nm = null;
            // System.gc();
        }
        iNucleiMgrHash = new Hashtable<String, NucleiMgr>();
        // System.gc();
    }

    // Removes all leaves from working tree
    public void clearTree() {
        //new Throwable().printStackTrace();
        if (iAncesTree == null) {
            return;
        }
        if (iRoot == null) {
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
                c = cc;
                count++;
            }
            m++;
        }
        println("clearTree: removed: " + count + CS + m);
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        println("clearTree: memory: " + runtime.freeMemory() + CS + runtime.totalMemory() + CS + runtime.maxMemory());
        if (iRoot != null)
        	iRoot.removeAllChildren();

        Hashtable x = iAncesTree.getCells();
        if (x != null) {
            x.clear();
            System.out.println("AncesTree cells size: " + iAncesTree.getCells().size());
        }


        iTree.updateUI();
        // System.gc();
    }

    @SuppressWarnings("unused")
	private void reviewNuclei() {
    	Vector nr = iNucleiMgr.getNucleiRecord();
    	for (int i=189; i < 195; i++) {
    		Vector nuclei = (Vector)nr.get(i);
    		for (int j=0; j < nuclei.size(); j++) {
    			Nucleus n = (Nucleus)nr.get(j);
    			println("reviewNuclei, " + i + CS + j );
    		}
    	}
    }


	public void buildTree(boolean doIdentity) {
        System.out.println("Building lineage tree..");

        iShowAnnotationsSave = iShowAnnotations;
        setShowAnnotations(false);

        if (doIdentity) {
            iNucleiMgr.processNuclei(doIdentity, this.configManager.getNucleiConfig().getNamingMethod());
        }

        if (iEditLog != null) {
            iEditLog.append("buildTree(" + doIdentity +
                ") start = " + this.configManager.getImageConfig().getStartingIndex() + " end = " + this.configManager.getImageConfig().getEndingIndex()
                + iEditLog.getTime());
        }

        Cell.setEndingIndexS(configManager.getNucleiConfig().getEndingIndex());


        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();

        // this should take the nuclei data that has been loaded into the AncesTree from the NucleiMgr and make a tree rooted at the iRoot cell (tree node)
        updateRoot(iAncesTree.getRootCells());
        iCellsByName = iAncesTree.getCellsByName();

        iAceMenuBar.setEditEnabled(true);
        iAceMenuBar.setEnabled(true);

        iTree.updateUI();
        setTreeSelectionMode();
        setTreeSelectionListener();

        // assume that P0 is the root, and look for the first child present in the nuclei
        Cell c = walkUpToAGoodCell();

        this.treeValueChangedFromEdit = true;
        setStartingCell(c, configManager.getNucleiConfig().getStartingIndex());

        // set up the UI properties for the tree shown in the main AceTree tab so that cells in the tree can be selected and trigger a change in the ImageWindow
        iTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setOpenIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setClosedIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setLeafIcon(null);

        if (iEditTraverse != null)  {
            iEditTraverse.buildNotification();
        }

        setShowAnnotations(iShowAnnotationsSave);
        // System.gc();
    }

    private Cell walkUpToAGoodCell() {
        Cell c = null;
        //System.out.println("Looking for a starting cell beginning at root: " + iRoot.getName());
        if (iRoot.getChildCount() <= 1) {
            return iRoot;
        }

        // assume the first child is P0
        // look for a real cell off of P0
        c = (Cell)iRoot.getChildAt(0);

        while (c.getTime() < 0 && c.getChildCount() > 0) {
            c = (Cell)c.getChildAt(0);
        }

        // if you don't find one, go back to the root and look
        // for a Nuc or something
        if (c.getTime() < 0) {
            for (int i=1; i < iRoot.getChildCount(); i++) {
                c = (Cell)iRoot.getChildAt(i);
                if (c.getTime() > 0) break;
            }

        }


        //System.out.println("returning: " + c.getName());
        return c;
    }

    public void restoreTree(String shortName) {
        //System.out.println("\n\nAceTree.restoreTree called: " + shortName);
        iMainFrame.setTitle(TITLE + ": " + shortName);
        //new Throwable().printStackTrace();
        NucleiMgr nucMgr = iNucleiMgrHash.get(shortName);
        if (nucMgr == null) {
            System.out.println("SORRY: " + shortName + " is not hashed");
            return;
        }
        iNucleiMgr = nucMgr;
        iEditLog = iNucleiMgr.getEditLog();
        //NucUtils.setNucleiMgr(iNucleiMgr);
        iImgWin.setNucleiMgr(iNucleiMgr);

        Cell.setEndingIndexS(this.configManager.getImageConfig().getEndingIndex());
        iAncesTree = iNucleiMgr.getAncesTree();
        updateRoot(iAncesTree.getRootCells());
        iCellsByName = iAncesTree.getCellsByName();
        setShowAnnotations(false);
        setShowSulstonAnnotations(true);
        iShow.setText(SHOWSUL);
        //iShowCentroids = false;
        //iShowC.setText(SHOWC);
        Cell.setEndingIndexS(this.configManager.getImageConfig().getEndingIndex()); // what does this do?

        Cell c = walkUpToAGoodCell();
        iTree.updateUI();
        setTreeSelectionMode();
        setStartingCell(c, this.configManager.getImageConfig().getStartingIndex());
        iTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setOpenIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setClosedIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setLeafIcon(null);
    }


    @SuppressWarnings("unused")
	private void updateRoot(Vector rootCells) {
        Cell PP = (Cell)iCellsByName.get("P");
        int kk = PP.getChildCount();
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
        //iCellsByName.put("P", PP);
        iCellsByName.remove("P");
        iCellsByName.put("P", iRoot);


		iRoot.setEndTime(1);
    }

    /**
     * Revised 10/18
     * @author Braden Katzman
     *
     * Previously, this method was the main trigger for bringing up the ImageWindow
     *
     * In the revised version, in an effort to tidy things up and group functionality more logically, this
     * will set up the starting cell and time as variables that ImageManager will use to bring up the image series
     * based on this information
     *
     * @param c
     * @param time
     */
    public void setStartingCell(Cell c, int time) {
        //System.out.println("setStartingCell, cell, time: " + c + CS + time);

        // if the nuclei passed isn't the root of the tree
        if (c != iRoot) {
            if (c == null)
            	c = (Cell)iRoot.getChildAt(0);

            while (c.getChildCount() > 0 && c.getTime() < 1) {
                c = (Cell)c.getChildAt(0);
            }


            time = Math.max(time, c.getTime());
            time = Math.min(time, c.getEndTime());


            getTimeAndPlane(c);

            iTimeInc = 0;
            iPlaneInc = 0;
            iCurrentCell = c;

            getCurrentCellParameters();
            this.treeValueChangedFromEdit = true;
            showTreeCell(iCurrentCell);
        } else { // the nuclei passed is the root of the tree -- use default viewing parameters
            imageManager.setCurrImageTime(1);
            imageManager.setCurrImagePlane(15);
            System.out.println("Setting default image time and plane: 1, 15. No starting cell");
        }

        /**
         * In the revised configuration and loading method 10/2018, we want to keep setStartingCell limited
         * strictly to query and setting the starting cell, and not also giving it the responsibility of bringing
         * up the image series. The code left here below is done for legacy support purposes. In the revised pipeline,
         * bring up series
         *
         * HandleCellSelection should still be used as a way to update the image window during runtime as it is a logical
         * progression from user interaction to window update, but it should be deconvolved from the actual loading and
         * initialization of the image window
         */
//        if (configManager == null) {
//            handleCellSelectionChange(c, time - iImageTime); // this will bring up an image
//            if (!c.getName().equals("P") && iRoot.getChildCount() > 0) {
//                //setShowAnnotations(true);
//                iShowCentroids = true;
//                iShowC.setText(HIDEC);
//                addMainAnnotation();
//            }
//            iAceMenuBar.setClearEnabled(true);
//
//
//            if(iImgWin!=null) {
//                System.out.println("REFRESH DISPLAY CALLED FROM SET STARTING CELL");
//                iImgWin.refreshDisplay(null);
//            }
//        }
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
            //this.treeValueChangedFromEdit = true;
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

    private void setTreeSelectionListener() {
        iTree.addTreeSelectionListener((TreeSelectionEvent tse) -> {
            if (this.treeValueChangedFromMouseClick) {
                //System.out.println("Tree value changed listener detects value was changed from mouse click on tree. Setting that flag to false and returning");
                this.treeValueChangedFromMouseClick = false;
                return;
            } else if (this.treeValueChangedFromImageChange) {
                //System.out.println("Tree value changed listener detects value was changed from image change. Setting that flag to false and returning");
                this.treeValueChangedFromImageChange = false;
                return;
            } else if(this.treeValueChangedFromStartup) {
                this.treeValueChangedFromStartup = false;
                return;
            } else if (this.treeValueChangedFromEdit) { // capture the case when the tree is updated from edits being made
                //System.out.println("Tree value changed from edit, not updating view or tree");
                this.treeValueChangedFromEdit = false;
                return;
            } else {
                Cell c = (Cell) iTree.getLastSelectedPathComponent();

                if (c != null) {
                    if (c == iCurrentCell) return; // this cell is already selected

                    if (c.getTime() < 0) {
                        if (iCurrentCell != null)
                            c = iCurrentCell;
                        else return;
                    }
                    //System.out.println("Tree value change listener detects value was changed from arrow keys on tree. Updating current cell in image window to tree selection");

                    int time = c.getTime();
                    setCurrentCell(c, time, LEFTCLICKONTREE); // just use LEFTCLICKONTREE because it accomplishes what is needed
                } else {
                    System.out.println("Tree item is null");
                }

                // turn off all flags in case they've somehow been turned on
                this.treeValueChangedFromMouseClick = false;
                this.treeValueChangedFromImageChange = false;

                return;
            }
        });
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
        iShow = new JButton(SHOWSUL);
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
        maximumIntensityProjectionToggle = new JButton(MAXPROJ);
        maximumIntensityProjectionToggle.addActionListener(this);

        p.add(iShow);
	
        p.add(iClear);

	p.add(iShowC);
	p.add(iDepthViews);

	p.add(iTrack);
	 p.add(iSister);
	p.add(iHome);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
        getActionMap().put(xxx, ctrl_left );

    	xxx = "ctrl_left_a";
    	Action ctrl_left_a = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, ctrl_left_a");
    			if (iAddOneDialog != null) {
    				ActionEvent ae = new ActionEvent(aceTree, 1, "LEFT");
    				iAddOneDialog.actionPerformed(ae);
    			}
    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK, false);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
        getActionMap().put(xxx, ctrl_right );

    	xxx = "ctrl_right_d";
        Action ctrl_right_d = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, ctrl_right_d");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "RIGHT");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK, false);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
        getActionMap().put(xxx, ctrl_up );

        xxx = "ctrl_up_w";
    	Action ctrl_up_w = new AbstractAction() {
    		private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, ctrl_up_w");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "UP");
				//ActionEvent ae = new ActionEvent(aceTree, 1, "DOWN");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK, false);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
        getActionMap().put(xxx, ctrl_down );

    	xxx = "ctrl_down_s";
        Action ctrl_down_s = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, ctrl_down_s");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "DOWN");
				//ActionEvent ae = new ActionEvent(aceTree, 1, "UP");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK, false);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
        getActionMap().put(xxx, ctrl_down );

    	xxx = "shift_left";
    	Action shift_left = new AbstractAction() {
    		private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_left");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "SMALL");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
        getActionMap().put(xxx, shift_left );

    	xxx = "shift_left_a";
    	Action shift_left_a = new AbstractAction() {
    		private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_left_a");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "SMALL");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
        getActionMap().put(xxx, shift_left );

    	xxx = "shift_right";
        Action shift_right = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_right");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "BIG");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
        getActionMap().put(xxx, shift_right );

    	xxx = "shift_right_d";
        Action shift_right_d = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_right_d");
    			if (iAddOneDialog == null) return;
				ActionEvent ae = new ActionEvent(aceTree, 1, "BIG");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
        getActionMap().put(xxx, shift_right );

        xxx = "shift_up";
    	Action shift_up = new AbstractAction() {
    		private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_up");
    			if (iAddOneDialog == null) return;
				//ActionEvent ae = new ActionEvent(aceTree, 1, "INCZ");
				ActionEvent ae = new ActionEvent(aceTree, 1, "INC Z");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
        getActionMap().put(xxx, shift_up );

        xxx = "shift_up_w";
    	Action shift_up_w = new AbstractAction() {
    		private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_up_w");
    			if (iAddOneDialog == null) return;
				//ActionEvent ae = new ActionEvent(aceTree, 1, "INCZ");
				ActionEvent ae = new ActionEvent(aceTree, 1, "INC Z");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
        getActionMap().put(xxx, shift_up );

    	xxx = "shift_down";
        Action shift_down = new AbstractAction() { 
        	private static final long serialVersionUID = 1L;
        	@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_down");
    			if (iAddOneDialog == null) return;
				//ActionEvent ae = new ActionEvent(aceTree, 1, "DECZ");
				ActionEvent ae = new ActionEvent(aceTree, 1, "DEC Z");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
        getActionMap().put(xxx, shift_down );

    	xxx = "shift_down_s";
        Action shift_down_s = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
    		@Override
			public void actionPerformed(ActionEvent e) {
    			//println("AceTree.setSpecialKeyBoardActions, shift_down_s");
    			if (iAddOneDialog == null) return;
				//ActionEvent ae = new ActionEvent(aceTree, 1, "DECZ");
				ActionEvent ae = new ActionEvent(aceTree, 1, "DEC Z");
				iAddOneDialog.actionPerformed(ae);

    		}
    	};
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_MASK, false);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, xxx);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("PAGE_UP"), "PAGE_UP");
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
            put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, PageDn );

        s = "END";
        Action end = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
        	@Override
			public void actionPerformed(ActionEvent e) {
                //copyImage();
            }
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), s);
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
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, home );

        s = "UP";
        Action up = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
            @Override
			public void actionPerformed(ActionEvent e) {
                imageUp();
            }
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, up );

        actionKey = "w_up";
        stroke = KeyStroke.getKeyStroke("typed w");
        inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, actionKey);
        getActionMap().put(actionKey, up);
        
        // Fast (skipping a few planes) UP using CTRL + SHIFT
        s = "ctrl shift UP";
        Action ctrl_shift_up = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
        	@Override
			public void actionPerformed(ActionEvent e) {
        		//System.out.println("shift-up key pressed--skipping planes");
        		imageManager.incrementImagePlaneNumber(5);
        		//System.out.println("Stopped tracking from shift UP");
        		iTrackPosition = ImageWindow.NONE;
        		updateDisplay();
        	}
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, ctrl_shift_up);

        s = "DOWN";
        Action down = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
            @Override
			public void actionPerformed(ActionEvent e) {
                imageDown();
            }
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, down );

        actionKey = "s_down";
        stroke = KeyStroke.getKeyStroke("typed s");
        inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, actionKey);
        actionMap = this.getActionMap();
        actionMap.put(actionKey, down);
        
        // Fast (skipping a few planes) DOWN using CTRL + SHIFT
        s = "ctrl shift DOWN";
        Action ctrl_shift_down = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
        	@Override
			public void actionPerformed(ActionEvent e) {
        		//System.out.println("shift-down key pressed--skipping planes");
        		imageManager.incrementImagePlaneNumber(-5);
        		//System.out.println("Stopped tracking from shift DOWN");
        		iTrackPosition = ImageWindow.NONE;
        		updateDisplay();
        	}
        };
    	getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), s);
    	getActionMap().put(s, ctrl_shift_down);


        s = "LEFT";
        Action left = new AbstractAction(s) {
        	private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
                prevImage();
            }
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, left );

        actionKey = "a_left";
        stroke = KeyStroke.getKeyStroke("typed a");
        inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, actionKey);
        actionMap = this.getActionMap();
        actionMap.put(actionKey, left);
        
        // Fast (skipping a few planes) LEFT using CTRL + SHIFT
        s = "ctrl shift LEFT";
        Action ctrl_shift_left = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
        	@Override
			public void actionPerformed(ActionEvent e) {
        		prevImageFast();
        	}
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, ctrl_shift_left);

        s = "RIGHT";
        Action right = new AbstractAction(s) {
        	private static final long serialVersionUID = 1L;
            @Override
			public void actionPerformed(ActionEvent e) {
            	nextImage();
            }
        };

        //AceTreeActions right = new AceTreeActions("RIGHT", 12345);
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, right );

        actionKey = "d_right";
        stroke = KeyStroke.getKeyStroke("typed d");
        inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, actionKey);
        actionMap = this.getActionMap();
        actionMap.put(actionKey, right);
        
        // Fast (skipping a few planes) UP using CTRL + SHIFT
        s = "ctrl shift RIGHT";
        Action ctrl_shift_right = new AbstractAction() {
        	private static final long serialVersionUID = 1L;
        	@Override
			public void actionPerformed(ActionEvent e) {
        		//System.out.println("shift-right key pressed--skipping times");
        		nextImageFast();
        	}
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, ctrl_shift_right);

        s = "ENTER";
        Action get = new AbstractAction(s) {
        	private static final long serialVersionUID = 1L;
            @Override
			public void actionPerformed(ActionEvent e) {
                iInputCtrl.getIt();
                updateDisplay();
            }
        };
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
            put(KeyStroke.getKeyStroke(s), s);
        getActionMap().put(s, get );

    }

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
            		//println("its a button");
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
        if (c == null) return;
        //iAnnotsShown.clear();
        getTimeAndPlane(c);
        iTimeInc = timeInc;
        if (this.imageManager.getCurrImageTime() < 1 || this.imageManager.getCurrImagePlane() < 1) return;
        updateDisplay();
    }

    /**
     * Deprecated under the configuration and loading revisions 10/2018 but left here for failure mode in which AceTree returns to previous method
     * @param c
     */
    private void getTimeAndPlane(Cell c) {
        if (c == null) return;
        if (c == iRoot) {
            //this.imageManager.setCurrImageTime(1);
            this.imageManager.setCurrImagePlane(15);
        } else {
            //System.out.println("Setting current image plane: " + (int)((double)c.getPlane() + HALFROUND));
            this.imageManager.setCurrImagePlane((int)((double)c.getPlane() + HALFROUND));

        }
        //iTimeInc = 0;
        iPlaneInc = 0;
        iCurrentCell = c;
    }


    /**
     * Revised 10/2018
     * @author Braden Katzman
     *
     * This method is the main driver for updating the image window. Its progression is:
     * - Based on the user input, query the ImageManager for the appropriate image
     * - Feed the image through ImageWindowDelegate to make any necessary updates
     * - pass the fully processed image to ImageWindow.refreshDisply() for display
     *
     */
    public void updateDisplay() {
        if (this.imageManager.getCurrImageTime() < 1) return;
        
        if ((this.imageManager.getCurrImagePlane() + iPlaneInc) <= 0) {
            iPlaneInc = (-1 * this.imageManager.getCurrImagePlane() + 1);
        }
        
        getCurrentCellParameters();

        if (iImgWin != null) {
            //System.out.println("Refreshing image window with new image, time: " + this.imageManager.getCurrImageTime() + ", plane: " + this.imageManager.getCurrImagePlane());
            // refresh the ImageWindow by building the desired image in the series with ImageManager and passing it along
            if (this.imageManager.isCurrImageMIP()) {
                // rebuild a maximum intensity projection (most likely the contrast slider was updated and ImageWindow has called back to updateDisplay())
                maximumIntensityProjection(false); // will pass the max projection to ImageWindow
            } else {
                int planeNum = -1;
                if (this.configManager.getImageConfig().getUseStack() == 1) { planeNum = this.imageManager.getCurrImagePlane(); }

                iImgWin.refreshDisplay(this.imageManager.makeImageNameForTitle(), this.imageManager.extractColorChannelFromImagePlus(this.imageManager.makeImage(), this.iColor), planeNum);
            }
        }

        String s = makeDisplayText();
        iText.setText(s);
        
        if (iCallSaveImage) {
            iCallSaveImage = false;
		    if(iImgWin!=null)
		    	iImgWin.saveImageIfEnabled();
        }

        
        if(iAddOneDialog!=null)
        	iAddOneDialog.updateCellInfo();
    }

    @SuppressWarnings("static-access")
//	public void handleImage() {
//        String cfile = makeImageName();
//        System.out.println(cfile + " - " + iZipTifFilePath + ", " + iTifPrefix);
//        ImagePlus ip = null;
//        ImageWindow.setUseStack(iUseStack);
//        ImageWindow.setSplitMode(iSplit);
//        if (cfile == null) {
//            //IJ.error("no image available");
//        	//System.out.println("AceTree calling ImageWindow.makeImage(null)...");
//            ImageWindow.makeImage(null);
//            return;
//        } else {
//            if (iImgWin != null) {
//                try {
//                    ip = iImgWin.refreshDisplay(iTifPrefix + cfile);
//                } catch(Exception e) {
//                    System.out.println("handleImage -- no image available: " + iTifPrefix + cfile);
//                    System.out.println(e);
//                    e.printStackTrace();
//                    iPlayerControl.pause();
//                }
//            } else {
//                System.out.println("\nhandleImage making new one: " + ip + CS + iTifPrefix + CS + cfile);
//
//				ip = ImageWindow.makeImage2(iTifPrefix + cfile, getImagePlane(), getUseStack(), getiSplit());
//				 //iImgWin = new ImageWindow( cfile, ip);
//				try {
//					iImgWin = new ImageWindow(iTifPrefix + cfile, ip, iPlayerControl, true);
//
//	                iImgWin.setAceTree(this);
//
//	                // Pass list of bookmarked cells to ImageWindow
//	                if (iBookmarkJList != null) {
//                        iImgWin.setBookmarkList(iBookmarkJList.getModel());
//                    }
//
//
//	                iImgWin.add(iToolControls,BorderLayout.SOUTH);
//	                iImgWin.pack();
//	                iImgWinSet = true;
//				} catch (Throwable t) {
//					new GeneralStartupError(getMainFrame(), t);
//				}
//            }
//        }
//    }

    /**
     * Called by:
     * - bringUpSeriesUI() to add annotation for the selecting cell at start time
     */
    public void addMainAnnotation() {
        if (iCurrentCellXloc <= 0) {
            return;
        }

        if (iImgWin != null) {
            iImgWin.addAnnotation(iCurrentCellXloc, iCurrentCellYloc, true);
        }
    }

    private int trackCellPlane() {
        if (iTrackPosition != ImageWindow.NONE) {
            iPlaneInc = 0;

            // return the cell's z plane (a 0-indexed field) + the offset to get a 1-indexed field corresponding to image plane
            return (int)(iCurrentCellZloc + NUCZINDEXOFFSET);
        }
        else {
            return this.imageManager.getCurrImagePlane();
        }
    }

    /**
     * Called by setStartingCell in bringing up the image series
     */
    private void getCurrentCellParameters() {
    	//System.out.println("getCurrentCellParameters: " + this.imageManager.getCurrImageTime() + CS + iTimeInc);
        if (iCurrentCell == null) 
        	return;
        //System.out.println("Current image time is: " + this.imageManager.getCurrImageTime());
        int time = this.imageManager.getCurrImageTime();

        Nucleus n = null;
        try {
            int t = time - 1;
            //System.out.println("Looking for nuc " + iCurrentCell.getName() + " at time: " + t);
        	Vector nuclei = iNucleiMgr.getElementAt(time - 1);
            n = NucUtils.getCurrentCellNucleus(nuclei, iCurrentCell);
        } catch(Exception e) {
            System.out.println("AceTree.getCurrentCellParameters error at time=" + time);
        }

        iCurrentCellXloc = -1;
        iCurrentCellYloc = -1;
        iCurrentCellZloc = -1;
        iCurrentCellPresent = false;
        if (n != null) {
            iCurrentCellXloc = n.x;
            iCurrentCellYloc = n.y;
            iCurrentCellZloc = n.z;
            this.imageManager.setCurrImagePlane(trackCellPlane());
            iCurrentCellPresent = true;
        }
    }


    private String makeDisplayText() {
        int time = this.imageManager.getCurrImageTime();
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
            sb2.append(NucUtils.countLiveCells(nuclei) + " cells at time " + (this.imageManager.getCurrImageTime()));
            Nucleus n = NucUtils.getCurrentCellNucleus(nuclei, iCurrentCell);
	        if (n != null) {
            sb2.append("\nlocation: " + iCurrentCellXloc + ", " + iCurrentCellYloc + ", " + n.z);
	        //sb2.append(CS + iAxis);
            double d = iNucleiMgr.nucDiameter(n,
                    this.imageManager.getCurrImagePlane() + iPlaneInc);
            String sd = (new DecimalFormat("###.#")).format(d);
            sb2.append("\nsize: " + n.size + " displayed diameter: " + sd);
            sb2.append("\ncurrent index: " + n.index);
            //sb2.append(getRedDataFromCell(time));
            sb2.append("\nexpression histone/label: " + n.weight);
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

////////////////////////////////////////////////////////////////////
////////////  image handling ///////////////////////////////////////
////////////////////////////////////////////////////////////////////


//    private void copyImage() {
//        ImagePlus ip = iImgWin.getImagePlus();
//        String s = iTifPrefix + makeImageName() + Math.random();
//        ImageWindow iw = new ImageWindow(s, ip,iPlayerControl, true);
//        iw.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//        iw.setAceTree(this);
//        //iw.refreshDisplay(s);
//        iw.setLocation(iw.getX() + XINC, iw.getY() + YINC);
//        iw.removeHandlers();
//        iw.setAceTree(null);
//        iw = null;
//    }

    public void editImage3() {
    }

    public void editTools() {
    	relinkNucleus();
    	
    	if (iAddOneDialog != null)
    		return;
    	iEditTools = true;
    	iAddOneDialog = new AddOneDialog(this, iImgWin, false, iCurrentCell, this.imageManager.getCurrImageTime());
    	iImgWin.iDialog = iAddOneDialog;
    }

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
			 					//System.out.println("Setting active cell to: "+selected);
                                treeValueChangedFromEdit = true;
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


    public void cellMovementImage() {
        ImagePlus ip = this.imageManager.makeImage(this.imageManager.getCurrImageTime(), this.imageManager.getCurrImagePlane());

        iCellMovementImage = new CellMovementImage(ip.getFileInfo().fileName, ip);

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
            //System.out.println("TreeMouseAdapter.mouseClicked: " + button + ". Setting treeValueChangedFromMouseClick to true");
            treeValueChangedFromMouseClick = true;
            Cell c = null;
            if (button == 2)
            	return;
            else {
                int selRow = iTree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = iTree.getPathForLocation(e.getX(), e.getY());
                if (selPath == null)
                	return;
                c = (Cell)selPath.getLastPathComponent();
            }

            if (button == 1) {

                showTreeCell(c);
                if (c.getTime() < 0) {
                    if (iCurrentCell != null)
                    	c = iCurrentCell;
                    else return;
                }
                int time = c.getTime();
                //println("TreeMouseAdapter.mouseClicked: " + c + CS + time);
                setCurrentCell(c, time, LEFTCLICKONTREE);
            }

            else if (button == MouseEvent.BUTTON3|e.isControlDown()) {
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


    public void imageUp() {
        this.imageManager.incrementImagePlaneNumber(1);

        //System.out.println("Stopped tracking from imageUP");
        iTrackPosition = ImageWindow.NONE;
        iCallSaveImage = true;

        // flip the MIP flag if AceTree is showing a MIP right now
        //this.imageManager.setCurrImageMIP(false);

        updateDisplay();
    }

    public void imageDown() {
        this.imageManager.incrementImagePlaneNumber(-1);
        //incPlane(1);
        //System.out.println("Stopped tracking from imageDOWN");
        iTrackPosition = ImageWindow.NONE;
        iCallSaveImage = true;

        // flip the MIP flag if AceTree is showing a MIP right now
        //this.imageManager.setCurrImageMIP(false);

        updateDisplay();
    }


    @Override
	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == iAddActiveCell) {
    		try {
	        	if (iCurrentCell != null || !iCurrentCell.equals("")) {
	        		String name = iCurrentCell.getName();
	        		iBookmarkDialog.addCell(name);
	        	}
    		}
    		catch (NullPointerException npe) {
    			return;
    		}
    		return;
        }

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
        }
        else if (e.getActionCommand().equals(SHOWSUL)) {
            setShowAnnotations(true);
            setShowSulstonAnnotations(true);
        }
        else if (e.getActionCommand().equals(SHOWTER)) {
            setShowAnnotations(true);
            setShowSulstonAnnotations(false);
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
        } else if (e.getSource() == maximumIntensityProjectionToggle) {
            maximumIntensityProjection(true);

            doUpdate = false;
        }
        if (doUpdate)
        	updateDisplay();
    }

    public void maximumIntensityProjection(boolean calledFromClick) {
        if (this.imageManager == null) return;

        // check if we're already in MIP mode and this method is being trigged because of a button click, indicating we should go back to normal image mode
        if (this.imageManager.isCurrImageMIP() && calledFromClick) {
            this.imageManager.setCurrImageMIP(false);
            updateDisplay();
            return;
        } else {
            // assume that we want to show centroids
            this.iShowCentroids = true;

            // update the centroids flag if the button has been toggled
            if (iShowC.getText().equals(SHOWC)) {
                this.iShowCentroids = false;
            }

            this.iImgWin.refreshDisplay(this.imageManager.getCurrentImageName(),
                    this.imageManager.extractColorChannelFromImagePlus(this.imageManager.makeMaxProjection(), this.iColor),
                    Integer.MAX_VALUE);
        }
    }

    public void toggleColor() {
        this.iColor = this.imageManager.getNextValidColorToggleIndex(this.iColor);

        // console logging
        switch(iColor) {
            case 1:
                System.out.println("*** RED channel mode ***");
                break;
            case 2:
                System.out.println("*** GREEN channel mode ***");
                break;
            case 3:
                System.out.println("*** BLUE channel mode ***");
                break;
            case 4:
                System.out.println("*** RED/GREEN channel mode ***");
                break;
            case 5:
                System.out.println("*** GREEN/BLUE channel mode ***");
                break;
            case 6:
                System.out.println("*** RED/BLUE channel mode ***");
                break;
            case 7:
                System.out.println("*** RED/GREEN/BLUE channel mode ***");
                break;
            default:
        }
    }

    public int getColor() {
        return iColor;
    }

    // handle track/no track button action
    private void setTrack() {
        if (iTrackPosition != ImageWindow.NONE) {
            //System.out.println("Stopped tracking from setTrack");
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

    /**
     * Handling code for bottom component of main AceTree window. Queries image time and/or cell
     * @param v
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


        if (ctrl.equals("InputCtrl1")) {
            String time = ((String)v.elementAt(1)).trim();

            // IMAGE TIME text field processing
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


            // INDEX/CELL NAME text field processing
            String cell = ((String)v.elementAt(2)).trim();
            String target = cell.toLowerCase();
		    String cellproper = PartsList.lookupProper(cell);
		    //System.out.println("ControlCallback looked up cell, proper: "+cell+CS+cellproper);
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

            //System.out.println(transformTitle());
        }
    }

    public void controlCallback(Vector v, int oldversion) {
        iImgWin.setSpecialEffect(null);
        String ctrl = (String)v.elementAt(0);
        if (ctrl.equals("InputCtrl1")) {
            String time = ((String)v.elementAt(1)).trim();

            int requestedTime = -1;
            Vector v2 = null;
            try {
                requestedTime = Integer.parseInt(time);
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

    public Config getConfig() { return configManager; }
    
    public boolean nextTime() {
	   this.imageManager.incrementImageTimeNumber(1);

	   if (iTimeInc != 0) {
           iTimeInc++;
       }

        iCallSaveImage = true;
        //int now = this.imageManager.getCurrImageTime() + iTimeInc;
        int now = this.imageManager.getCurrImageTime();
        int end = 9999;
        if (iCurrentCell != null) 
        	end = iCurrentCell.getEnd();
        if (now <= end) {
            return true; // we will call updateDisplay next
        }


        if (iCurrentCell.getFateInt() == Cell.DIED) {
            //System.out.println("Cell died, turning tracking off");
            iCurrentCellPresent = false;
            //this.imageManager.setCurrImageTime(this.imageManager.getCurrImageTime() + iTimeInc);
            //this.imageManager.setCurrImageTime(this.imageManager.getCurrImageTime());
            iTimeInc = 0;
            iTrackPosition = ImageWindow.NONE;
            return true;
        }

        // at this point we know that a cell division occurred in this transition
        // iCurrentCell will change as a side effect of doDaughterDisplayWork
        iTimeInc = 0;
        setCurrentCell(iCurrentCell, now, NEXTTIME);

        return true;
    }

    public boolean prevTime() {
        this.imageManager.incrementImageTimeNumber(-1);

        if (iTimeInc != 0) {
            iTimeInc--;
        }

        // flip the MIP flag if AceTree is showing a MIP right now
        //this.imageManager.setCurrImageMIP(false);

        // check if we're still moving through the lifetime of the currently selected cell.
        iCallSaveImage = true;
        int now = this.imageManager.getCurrImageTime();
        int start = 0;
        if (iCurrentCell != null) start = iCurrentCell.getTime();
        if (now >= start)
        	return true;
        // if we've made it here, the cell that's being tracked has disappeared so we need to find a new cell to set


        // a cell change occurs as we move to parent here
        //println("prevTime: " + iCurrentCell.getName() + CS + now);
        iTimeInc = 0;
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
        if (row != -1) {
            try {
                iTree.setSelectionInterval(row, row);
                iTree.scrollRowToVisible(row);
                iTree.makeVisible(tp);

                this.treeValueChangedFromImageChange = true;
            } catch (NullPointerException npe) {

            }
        }
    }

    @SuppressWarnings("unused")
	private void doDaughterDisplayWork(Cell parent, Cell selectedDaughter) {
        //Cell parent = (Cell)c.getParent();
        //System.out.println("doDaughterDisplayWork: " + parent + CS + selectedDaughter);
        if (parent == null) System.out.println("*******NULL PARENT");
        if (!isTracking()) {
            return;
        }
        if (parent.getName() == ROOTNAME) return;

        if (iTimeInc != 0) {
            return;
        }

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
        else {
            iCurrentCell = posteriorCell;
        }

        if (iCurrentCell == null) {
            iCurrentCell = save;
            return;
        }

        Vector nuclei = iNucleiMgr.getElementAt(this.imageManager.getCurrImageTime() - 1);
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
        // set iImageTime and iTimeInc cleanly
        // set iImagePlane and iPlaneInc cleanly
        // assume initially that the transition was to a previous time
        int imageTime = this.imageManager.getCurrImageTime();
        iTimeInc = imageTime - iCurrentCell.getTime();
        this.imageManager.setCurrImageTime(iCurrentCell.getTime() + iTimeInc);

        //System.out.println("Current image time being updated from: " + imageTime
        //        + " to (currentCell.getTime() + iTimeInc: " + iCurrentCell.getTime() +
        //        ", " +  iTimeInc + " = " + (iCurrentCell.getTime() + iTimeInc));

        int imagePlane = this.imageManager.getCurrImagePlane();
        iPlaneInc = imagePlane - iCurrentCell.getPlane();
        this.imageManager.setCurrImagePlane(iCurrentCell.getPlane() + iPlaneInc);

        //System.out.println("Current image plane being updated from: " + imagePlane
        //        + " to currentCell.getPlane() = " + iCurrentCell.getPlane() +
        //        ", with iPlaneInc =" +  iPlaneInc);

        //System.out.println(isTracking());

    }

    public void setCurrentCell(Cell c, int time, int source) {
        //System.out.println("Set current cell for: " + c.getName() + " at time: " + time + " from source: " + source);
    	if(iCellsByName == null)
    		return;

        if (c == null) {
        	if (source == CONTROLCALLBACK) {
        	    System.out.println("Control callback showing time: " + time);
        		showSelectedCell(c, time);
        	}
        	return;
        }
        //System.out.println("setCurrentCell called on: " + c.getName() + ", at time: " + time);

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

            this.treeValueChangedFromEdit = true;
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
            //updateDisplay();

        } 
        else if (source == RIGHTCLICKONEDITIMAGE) {
        	//System.out.println("AceTree.setCurrentCell RIGHTCLICKONEDITIMAGE.");
            //println("setCurrentCell:4 ");
            Cell old = iCurrentCell;
            iCurrentCell = c;
            trackingActionsOnCurrentCellChange();
            iAceTree.forceTrackingOn();

            this.treeValueChangedFromEdit = true;
            showTreeCell(iCurrentCell);

            String s = "Add Cell in progress";

            if(iImgWin != null)
                iImgWin.updateCurrentCellAnnotation(iCurrentCell, old, -1);
                iText.setText(s);
                //updateDisplay();

        } else if (source == LEFTCLICKONTREE) {
            showSelectedCell(c, time);
            //updateDisplay();
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
            //System.out.println("Source is nexttime, setting image time to: " + time);
            this.imageManager.setCurrImageTime(time);
            iTimeInc = 0;
            Cell currentCellSave = iCurrentCell;
            doDaughterDisplayWork(iCurrentCell, null);

            if (currentCellSave != iCurrentCell) {
                trackingActionsOnCurrentCellChange();

                if(iImgWin != null) {
                    iImgWin.updateCurrentCellAnnotation(iCurrentCell, currentCellSave, time);
                }
            }

            this.treeValueChangedFromEdit = true;
            showTreeCell(iCurrentCell);
        } else if (source == PREVTIME) {
            //System.out.println("Prevtime called at: " + this.imageManager.getCurrImageTime());
        	//Vector nuclei1 = iNucleiMgr.getElementAt(this.imageManager.getCurrImageTime() + iTimeInc);
            //Vector nuclei0 = iNucleiMgr.getElementAt(this.imageManager.getCurrImageTime() + iTimeInc - 1);
            Vector nuclei1 = iNucleiMgr.getElementAt(this.imageManager.getCurrImageTime());
            Vector nuclei0 = iNucleiMgr.getElementAt(this.imageManager.getCurrImageTime() - 1);

            //System.out.println("Queried nucs at times: " + (this.imageManager.getCurrImageTime() + iTimeInc) + ", " + (this.imageManager.getCurrImageTime() + iTimeInc - 1));
            //System.out.println("Looking for parent of " + iCurrentCell.getName() + " at those times");
        	Nucleus n = NucUtils.getParent(nuclei0, nuclei1, iCurrentCell.getName());
            Cell currentCellSave = iCurrentCell;
            if (n != null) {
                //System.out.println("Parent: " + n.identity + " of: " + iCurrentCell.getName());
                iCurrentCell = (Cell)iAncesTree.getCellsByName().get(n.identity);
                //System.out.println("Current cell is now: " + iCurrentCell);
                if (iCurrentCell == null) {
                	
                    iCurrentCell = currentCellSave;
                    return;
                }
                if (currentCellSave != iCurrentCell) {
                    //System.out.println("Prev cell and new one found (parent) are different. Doing tracking actions of current cell change");
                    trackingActionsOnCurrentCellChange();
				    if(iImgWin!=null)
				    	iImgWin.updateCurrentCellAnnotation(iCurrentCell, currentCellSave, time);
                }
                this.treeValueChangedFromEdit = true;
                showTreeCell(iCurrentCell);
            } else {
                //System.out.println("Couldn't find parent to: " + iCurrentCell.getName() + ". Turning tracking off and showing tree root");
                iTrackPosition = ImageWindow.NONE;
                iCurrentCell = null;

                //this.treeValueChangedFromEdit = true;
                showTreeCell(iRoot);
            }
        }

        // if the wormguides window is open, update the currently selected cell there
//        if (iAceMenuBar.view != null && !iAceMenuBar.view.isClosed()) {
//            //System.out.println("Passing name: " + iCurrentCell.getName() + " to WG for window update");
//            iAceMenuBar.view.updateSelectedEntity(iCurrentCell.getName());
//        }
    }

    /**
     * Called from WormGUIDES to enable cell selection via WormGUIDES
     * @param identity
     */
    public void showSelectedCell(String identity) {
         showSelectedCell((Cell)iAncesTree.getCellsByName().get(identity), this.imageManager.getCurrImageTime());
    }

    /**
     * does the work required by the cell selection control
     * @param c Cell the cell desired
     * @param requestedTime int the time index where it is to be shown
     */
    @SuppressWarnings("unused")
	public void showSelectedCell(Cell c, int requestedTime) {
        //System.out.println("Showing selected cell: " + c + ", " + " at time: " + requestedTime);
    	if (iImgWin == null)
    		return;
    	
        if (c == null) {
            this.imageManager.setCurrImageTime(requestedTime);
            this.imageManager.setCurrImagePlane(15);
            iTimeInc = 0;
            iPlaneInc = 0;

            iCurrentCell = iRoot;

            //this.treeValueChangedFromEdit = true;
            showTreeCell(iCurrentCell);
            updateDisplay();

        	return;
        }

        String name = c.getName();
        //System.out.println("Selected name: "+name);
        Nucleus n = iNucleiMgr.getCurrentCellData(name, requestedTime);

        if (n != null) {
            Cell old = iCurrentCell;

            //System.out.println("setting current image time to: " + requestedTime);
            //this.imageManager.setCurrImageTime(c.getTime());
            this.imageManager.setCurrImageTime(requestedTime);


            iTimeInc = requestedTime - this.imageManager.getCurrImageTime();

            this.imageManager.setCurrImagePlane((int)(n.z + HALFROUND));

            iPlaneInc = 0;
            iCurrentCell = c;

            if (this.imageManager.getCurrImageTime() < 1)
            	return;

            iCurrentCellPresent = true;
            if (iCurrentCell.isAnterior())
            	iTrackPosition = ImageWindow.ANTERIOR;
            else iTrackPosition = ImageWindow.POSTERIOR;

            //this.treeValueChangedFromEdit = true;
            showTreeCell(iCurrentCell);

            //int baseTime = c.getTime(); //Integer.parseInt(sa[0]);
            iImgWin.updateCurrentCellAnnotation(iCurrentCell, old, -1);
            updateDisplay();
        }
        else {
            this.imageManager.setCurrImageTime(requestedTime);
            this.imageManager.setCurrImagePlane(15);
            iTimeInc = 0;
            iPlaneInc = 0;
            iCurrentCell = c;

            //this.treeValueChangedFromEdit = true;
            showTreeCell(iCurrentCell);
            updateDisplay();
        }

    }

    private void incPlane(int inc) {
        if (inc > 0) {
        	if (this.imageManager.getCurrImagePlane() + iPlaneInc < this.configManager.getImageConfig().getPlaneEnd())
        		iPlaneInc += inc;
        }
        else if (inc < 0) {
        	if (this.imageManager.getCurrImagePlane() + iPlaneInc > 1)
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
        //int now = this.imageManager.getCurrImageTime() + iTimeInc;
        int now = this.imageManager.getCurrImageTime();
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
        NucZipper nz = new NucZipper(file, iNucleiMgr, configManager);

        //iEditLog.setModified(false);
        System.out.println("Finished saving nuclei.");
    }

    public void viewNuclei() {
        new NucEditDialog(this, iMainFrame, false);
    }


    public void relinkNucleus() {
        int time = this.imageManager.getCurrImageTime();
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
        //semaphores  merge from shooting_star_both_as AceTree source code
        boolean SNLock = iAceTree.getSNLock();
        if(SNLock){
            JOptionPane.showMessageDialog(null,"Waiting for StarryNite to finish writing nuclei data");
            //SN has locked NM, wait for it to be unlocked
            //Pop up a dialog indicating that we're waiting
            while(SNLock){
                try{
                    Thread.sleep(100);
                    SNLock = iAceTree.getSNLock();
                }
                catch(InterruptedException ex){

                }
            }
            JOptionPane.showMessageDialog(null,"StarryNite is done, taking over");
        }
        boolean success = iAceTree.ATLockNucleiMgr(true);

        //killcell
    	println("\n\nkillCell");
        this.treeValueChangedFromEdit = true;

    	//int currenttimeNuclei = this.imageManager.getCurrImageTime() + iTimeInc - 1;
        int currenttimeNuclei = this.imageManager.getCurrImageTime() - 1;
    	Vector nuclei = iNucleiMgr.getElementAt(currenttimeNuclei);
    	
    	String name = iCurrentCell.getName();
    	System.out.println("Looking for cell: " + name + " to kill");
        Nucleus n = null;
        for (int j = 0; j < nuclei.size(); j++) {
            n = (Nucleus)nuclei.elementAt(j);
            if (!n.identity.equals(name))
            	continue;

            System.out.println("Nullifying status of: " + n.identity + " at " + (currenttimeNuclei + 1));
            n.status = Nucleus.NILLI;
            break;
        }

        // added rebuild code
        clearTree();
        buildTree(true);

        // update WormGUIDES data if it's open
        if (iAceMenuBar.view != null && !iAceMenuBar.view.isClosed()) {
            iAceMenuBar.view.rebuildData();
        }

        // add find self at previous time code from relink
        AncesTree ances = getAncesTree();
		Hashtable h = ances.getCellsByName();

		Cell c = (Cell)h.get(name);
		
		// set active cell to start time to aid review
		if(c != null) {
            //System.out.println("Setting starting cell c: " + c + " at time: " + currenttimeNuclei);
			this.treeValueChangedFromEdit = true;
            setStartingCell(c, currenttimeNuclei);
		}

        prevImage();

		// System.gc();

        success = iAceTree.ATLockNucleiMgr(false);
    }

    public void killDeepNucs() {
    	new KillDeepNucsDialog(this, iMainFrame, true);
    }

    //merge from shooting_star_both_as AceTree source code
    public void killDeepNucs(int zLim) {
        boolean SNLock = iAceTree.getSNLock();
        if(SNLock){
            JOptionPane.showMessageDialog(null,"Waiting for StarryNite to finish writing nuclei data");
            //SN has locked NM, wait for it to be unlocked
            //Pop up a dialog indicating that we're waiting
            while(SNLock){
                try{
                    Thread.sleep(100);
                    SNLock = iAceTree.getSNLock();
                }
                catch(InterruptedException ex){

                }
            }
            JOptionPane.showMessageDialog(null,"StarryNite is done, taking over");
        }
        boolean success = iAceTree.ATLockNucleiMgr(true);

        Vector nucRec = (Vector)iNucleiMgr.getNucleiRecord();
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

        success = iAceTree.ATLockNucleiMgr(false);
    }

    public void testWindow() {
    	new TestWindow(this, iMainFrame, false);
    }

    public void killCells() {
        //int time = this.imageManager.getCurrImageTime() + iTimeInc;
        new KillCellsDialog(this, iMainFrame,true, iCurrentCell, this.imageManager.getCurrImageTime(), iEditLog);
    }

    public void pausePlayerControl() {
        iPlayerControl.pause();
    }

    public void setEndTime() {
        new SetEndTimeDialog(this, iMainFrame, true);
    }

    public void incrementEndTime() {
        setEndingIndex(this.configManager.getImageConfig().getEndingIndex() + 1);
    }

    @SuppressWarnings("unused")
	public void setEndingIndex(int endTime) {
        this.configManager.getImageConfig().setEndingIndex(endTime);
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

        // update WormGUIDES data if it's open
        if (iAceMenuBar.view != null && !iAceMenuBar.view.isClosed()) {
            iAceMenuBar.view.rebuildData();
        }

        this.treeValueChangedFromEdit = true;
        setStartingCell((Cell)iRoot.getFirstChild(), this.configManager.getImageConfig().getStartingIndex());
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
        iSulstonTree = new SulstonTree(iCanonicalTree, "Canonical Sulston Tree", iCurrentCell, false, null);
        //new AuxFrame(this, "Sulston Tree", iCanonicalTree);
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

    public void allCentroidsView() {
        String s = getImageTitle();
        new ImageAllCentroids(this, s);

    }

	  public String getImageTitle() {
			String s = this.imageManager.getCurrentImage().getFileInfo().fileName;
			if (s!=null) {
			    if(this.configManager.getImageConfig().getUseStack() == 0)
				{
				    int k = s.lastIndexOf("-");
				    if (k != -1)
				    	s = s.substring(0, k);
				}
			}
			//System.out.println("AceTree.java 2692: " + s);
			String s2 = this.configManager.getImageConfig().getImagePrefixes()[0];
			//System.out.println("AceTree.java 3243: " + iTifPrefix);
			int j = s2.lastIndexOf(C.Fileseparator);
			if (j > 0)
				s2 = s2.substring(j + 1);
			
			//System.out.println("AceTree.java 2695: " + s2 + s);
			return s2 + s;
    }


    public void image2DSave(boolean saveIt) {
        iImgWin.setSaveImageState(saveIt);
        iImgWin.saveImageIfEnabled();
    }


    private void delay(int n) {
        long t = System.currentTimeMillis();
        long e = t + n;
        while (t < e) t = System.currentTimeMillis();

    }

    public boolean nextImage() {
        //System.out.println("CALLED NEXTIMAGE");
		if(iImgWin != null) {
            iImgWin.setSpecialEffect(null);
        }

        boolean b = nextTime();

		updateDisplay();

		return b;
    }
    
    public boolean nextImageFast() {
    	if(iImgWin!=null)
		    iImgWin.setSpecialEffect(null);
        boolean b = nextTime();
        for (int i = 0; i < 4 & b; i++)
        	b = nextTime();
        
        updateDisplay();
        return b;
    }

    public boolean prevImage() {
        boolean b = prevTime();
        
        updateDisplay();
        return b;
    }
    
    public boolean prevImageFast() {
    	if(iImgWin!=null)
		    iImgWin.setSpecialEffect(null);
        boolean b = prevTime();
        for (int i = 0; i < 4 & b; i++)
        	b = prevTime();
        
        updateDisplay();
        return b;
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
        return this.configManager.getImageConfig().getUseStack();
    }

    public int getImageTime() {
        return this.imageManager.getCurrImageTime();
    }

    public int getImagePlane() {
        return this.imageManager.getCurrImagePlane();
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
        iShowAnnotations = show;
        setShowButton();
    }

    public boolean getShowSulstonAnnotations() { return iShowSulstonAnnotations; }

    public void setShowSulstonAnnotations(boolean suls) {
        iShowSulstonAnnotations = suls;
        setShowButton();
    }

    private void setShowButton() {
        if (iShow != null) {
            if (iShowAnnotations && !iShowSulstonAnnotations) iShow.setText(HIDE);
            else if (iShowAnnotations && iShowSulstonAnnotations) iShow.setText(SHOWTER);
            else iShow.setText(SHOWSUL);
        }
    }


    public Cell getCurrentCell() {
        return iCurrentCell;
    }

    public JFrame getMainFrame() {
        return iMainFrame;
    }

    public Cell getRoot() {
        return iRoot;
    }

    public boolean getFullGUI(){
	return fullGUI;
    }
    public double getiZPixRes(){
	return this.configManager.getNucleiConfig().getZPixRes();
    }

    public boolean isTracking() {
        //System.out.println("isTracking: " + iTrackPosition + CS + (iTrackPosition != ImageWindow.NONE));
        return iTrackPosition != ImageWindow.NONE;
    }

    public void setIsTracking(Integer value) {
        // only set tracking flag is the value is legitimate tracking value
        if (value == ImageWindow.NONE || value == ImageWindow.ANTERIOR || value == ImageWindow.POSTERIOR) {
            iTrackPosition = value;
        }
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


    public ImageManager getImageManager() {
        return this.imageManager;
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
   ,SHOWSUL = "Sulston Names"
   ,SHOWTER = "Terminal Names"
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
            ,MAXPROJ = "Maximum Intensity Projection"
   ,CS = ", "
   ,DEPTHVIEWS="Depth View"
   ;

    private static final float
         HALFROUND = 0.5f
        ;

    private void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        iMainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        iMainFrame.getContentPane().setLayout(new BorderLayout());
        int height = HEIGHT200 + HEIGHT100 + HEIGHT100 + HEIGHT100 + 2*HEIGHT30;
        this.setMinimumSize(new Dimension(WIDTH, height));
        this.setOpaque(true); //content panes must be opaque

        // make this AceTree instance the content pane
        iMainFrame.setJMenuBar(iAceMenuBar);
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
        ot.iCmdLineRun = true;
    }

    private static void println(String s) {System.out.println(s);}
    private void newLine() {System.out.println(""); }
    private static final double NUCZINDEXOFFSET = 0.5; // nuclei z values are 0 indexed and image planes are 1 indexed so we use the offset to equate the two
}