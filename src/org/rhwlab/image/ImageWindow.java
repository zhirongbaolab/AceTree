/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */
package org.rhwlab.image;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.IllegalArgumentException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipEntry;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.WindowConstants;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.net.URL;





import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.OvalRoi;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.io.Opener;
import ij.io.TiffDecoder;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.ImageIcon;





import net.sf.ij.jaiio.BufferedImageCreator;

import org.rhwlab.acetree.PlayerControl;
import org.rhwlab.acetree.AceTree;
import org.rhwlab.acetree.AnnotInfo;
//import org.rhwlab.image.Image3D.SublineageDisplayProperty;
//import org.rhwlab.image.Image3D.PropertiesTab.SublineageUI;
import org.rhwlab.image.ParsingLogic.ImageNameLogic;
import org.rhwlab.image.management.ImageManager;
import org.rhwlab.nucedit.AddOneDialog;
import org.rhwlab.nucedit.NucRelinkDialog;
import org.rhwlab.nucedit.UnifiedNucRelinkDialog;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.C;
import org.rhwlab.utils.EUtils;
import org.rhwlab.acetree.PartsList;

/**
 * Provides a JFrame window to contain the ImageJ ImagePlus object
 *
 * @author biowolp
 * @version 1.0 January 25, 2005
 */
public class  ImageWindow extends JFrame implements  KeyListener, Runnable {
	// variables to stay after revisions
    public ImageCanvas      iImgCanvas;
    static ImagePlus        iImgPlus;
    String                  iTitle;

    static Object []        iSpecialEffect;

    WinEventMgr 			wem;
    boolean                 iIsMainImgWindow;

    MouseHandler            iMouseHandler;
    boolean                 iIsRightMouseButton;

    // contrast controls
    protected ImageContrastTool ict;
    protected JButton			ictApplyButton;
    protected JSlider			iSlider1min;
    protected JSlider			iSlider1max;
    protected JSlider			iSlider2min;
    protected JSlider			iSlider2max;
    protected JSlider           iSlider3min;
    protected JSlider           iSlider3max;

    public static ColorSchemeDisplayProperty []     iDispProps;
    protected JToolBar      iToolBar;

    ImageZoomerFrame		iImageZoomerFrame;
    ImageZoomerPanel 		iImageZoomerPanel;


    // ****************************************************************************************************************
    // variables to be removed for revisions
    AceTree                 iAceTree;

    /* these should go into ImageManager */
    int                     iTimeInc;
    int                     iPlaneInc;
    public static int 	    imagewindowPlaneNumber;//unlike iImagePlane this includes increment number used only for new image access
    public static String        cCurrentImagePart;
    public static String        cZipTifFilePath;
    public static String        cTifPrefix;
    public static String        cTifPrefixR;
    // ***********************************

    /* these should go into ImageSavingManager */
    boolean                 iSaveImage;
    boolean                 iSaveInProcess;
    String                  iSaveImageDirectory;
    // *****************************************

    /* this should go into ImageAnnotationManager */
    Vector                  iAnnotsShown;
    public static NucleiMgr     cNucleiMgr;
    public static int           cLineWidth;
    PartsList iPartsList; // likely used to show systematic names
    // *****************************************

    /* these probably belong in ImageConversionManager */
    static byte []          iRpix;
    static byte []          iGpix;
    static byte []          iBpix;
    // **************************************************

    /* moved into ImageAnnotationManager */
    protected DefaultListModel	iBookmarkListModel;
    // *************************************

    // NOT SURE WHAT THESE ARE OR WHAT THEY'RE USED FOR
    boolean                 iUseRobot;
    boolean                 iNewConstruction;
    static boolean         	cAcbTree = false;


    // TODO this is only temporarily here to get new functionality working --> porting the code that has dependencies after
    private ImageManager imageManager;

    /**
     * Revised ImageWindow constructor
     * @author Braden Katzman
     * Date revised: 10/2018
     *
     * This constructor has significantly less responsibility since its components have been modularized
     *
     * @param title
     * @param imgPlus
     * @param playercontrol
     */
    public ImageWindow(String title, ImagePlus imgPlus, PlayerControl playercontrol, ImageManager imageManager) {
        super(title);
        iTitle = title;
        iImgPlus = imgPlus;
        iImgCanvas = new ImageCanvas(imgPlus);
        iDispProps = getDisplayProps();

        //custom icon
        URL imageURL = ImageWindow.class.getResource("/images/icon2.gif");
        ImageIcon test=new ImageIcon(imageURL, "x");
        this.setIconImage(test.getImage());

        Container c = getContentPane();
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());

        BufferedImage image = BufferedImageCreator.create((ColorProcessor)iImgPlus.getProcessor());
        iImageZoomerPanel= new ImageZoomerPanel(this, image, 10.0, title, playercontrol);
        jp.add(iImageZoomerPanel);
        c.add(jp);

        pack();

        // bring up the image
        setVisible(true);

        setFocusable(true);

        // this keeps the ImageWindow from being destroyed when it is closed, since it may be reopened at another point during program execution
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        wem = new WinEventMgr();
        addWindowFocusListener(wem);
        addWindowListener(wem);
        iMouseHandler = new MouseHandler(this);


        iImageZoomerPanel.getImage().addMouseMotionListener(iMouseHandler);
        iImageZoomerPanel.getImage().addMouseListener(iMouseHandler);

        //setImageTimeAndPlaneFromTitle();

        iIsMainImgWindow = iTitle.indexOf(RANDOMT) == -1 || iTitle.indexOf(RANDOMF) == -1;
        iIsRightMouseButton = false;
        iSaveImage = false;
        iSaveImageDirectory = null;
        iUseRobot = false;

        iImgCanvas.addKeyListener(this);

        ict = null;
        ictApplyButton = null;
        iSlider1min = iSlider1max = iSlider2min = iSlider2max = iSlider3min = iSlider3max = null;

        iPartsList = new PartsList();

        // TODO - these are just temporary workarounds
        this.imageManager = imageManager;
        iPlaneInc = iTimeInc = 0;
        iAnnotsShown = new Vector();

        // we need to check for the case here where the images are zero indexed, which is rare, but happens in the case of some diSPIM data.
        // If this is the case, we need to deal with it so that we don't index improperly into the NucleiMgr
        if (this.imageManager.getCurrImageTime() == 0) {
            iTimeInc = 1;
        }
    }

    public void removeHandlers() {
        // Remove mouse handler
        iImageZoomerPanel.getImage().removeMouseListener(iMouseHandler);
        iImageZoomerPanel.getImage().removeMouseListener(iMouseHandler);
        iMouseHandler = null;

        // Remove window event manager
        removeWindowFocusListener(wem);
        removeWindowListener(wem);
        wem = null;
    }

    public void setBookmarkList(ListModel list) {
    	iBookmarkListModel = (DefaultListModel)list;
    }
    
    public MouseHandler getMouseHandler() {
    	return iMouseHandler;
    }

    public void setNucleiMgr(NucleiMgr nucleiMgr) {
        cNucleiMgr = nucleiMgr;
    }

    @SuppressWarnings("unused")
	public static ImagePlus readData(FileInputStream fis, boolean bogus) {
        if (fis == null)
        	return null;
        int byteCount;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        try {
            InputStream is = fis;
            byte data[] = new byte[DATA_BLOCK_SIZE];

            //  4. read source zipped data and write to uncompressed stream
            while ( (byteCount = is.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
                out.write(data, 0, byteCount);
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return openTiff(new ByteArrayInputStream(out.toByteArray()), true);

    }

    public static ImagePlus readData(FileInputStream fis) {
        if (fis == null) return null;
        byte [] ba = readByteArray(fis);
        return openTiff(new ByteArrayInputStream(ba), true);
    }

    @SuppressWarnings("unused")
	public static byte[] readByteArray(FileInputStream fis) {
        if (fis == null) return null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int byteCount;
        byte[] buf = new byte[4096];
        try {
            InputStream is = fis;
            byte data[] = new byte[DATA_BLOCK_SIZE];

            //  4. read source zipped data and write to uncompressed stream
            while ( (byteCount = is.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
                out.write(data, 0, byteCount);
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return out.toByteArray();

    }
 

    /** Attempts to open the specified inputStream as a
    TIFF, returning an ImagePlus object if successful. */
    public static ImagePlus openTiff(InputStream in, boolean convertToRGB) {
        //System.out.println("openTiff entered");
        if (in == null) return null;
        FileInfo[] info = null;
        try {
            TiffDecoder td = new TiffDecoder(in, null);
            info = td.getTiffInfo();
        } catch (FileNotFoundException e) {
            IJ.error("TiffDecoder", "File not found: "+e.getMessage());
            return null;
        } catch (Exception e) {
            IJ.error("TiffDecoder", ""+e);
            return null;
        }
        ImagePlus imp = null;
        if (IJ.debugMode) // dump tiff tags
            IJ.log(info[0].info);
        FileOpener fo = new FileOpener(info[0]);
        imp = fo.open(false);
        // detect 8 bit or RGB from the FileInfo object info[0]
	//  if (info[0].getBytesPerPixel() == 1 && convertToRGB) {
	//      imp = convertToRGB(imp);
	//  }
        //IJ.showStatus("");
        return imp;
    }


    protected static ImagePlus makeRedImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.setRGB(iRpix, new byte[iRpix.length], new byte[iRpix.length]);
        ip.setProcessor("test", iproc3);
        return ip;
    }

    protected static ImagePlus makeGreenImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        //System.out.println("makeGreenImagePlus: " + iproc);
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        //System.out.println("makeGreenImagePlus2: " + iproc + CS + iGpix  + CS + iRpix);
        iproc3.setRGB(new byte[iRpix.length], iGpix, new byte[iRpix.length]);
        ip.setProcessor("test", iproc3);
        return ip;
    }

    protected static ImagePlus makePlainImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        if (cAcbTree) {
            //byte [] added = new byte[iRpix.length];
            //for (int i=0; i < iRpix.length; i++) {
             //   added[i] = (byte)(iRpix[i] + iGpix[i]);
            //}
            iproc3.setRGB(iRpix, iRpix, iRpix);
        } else {
            iproc3.setRGB(new byte[iRpix.length], new byte[iRpix.length], new byte[iRpix.length]);

        }
        ip.setProcessor("test", iproc3);
        return ip;
    }


    // end of static stuff

    public ImageWindow() {

    }

    public class ColorSchemeDisplayProperty {
        public String iName;
        public int    iLineageNum;

        public ColorSchemeDisplayProperty(String name, int lineageNum) {
            iName = name;
            iLineageNum = lineageNum;
        }
    }

    public class ColorSchemeUI {
        public JPanel       iPanel;
        public JTextField   iTF;
        public JComboBox    iCB;
        public JLabel       iLabel;

        public ColorSchemeUI(int i) {
            iPanel = new JPanel();
            iPanel.setLayout(new GridLayout(1,2));
            iTF = new JTextField(iDispProps[i].iName, WIDTH);
            iLabel = new JLabel(iDispProps[i].iName);
            String [] list;
            list = COLORS;
            if (i == 5) 
            	list = SIZES;
            if (i == 6) 
            	list = SHAPES;
            iCB = new JComboBox(list);
            iCB.setSelectedIndex(iDispProps[i].iLineageNum);
            //iPanel.add(iTF);
            iPanel.add(iLabel);
            iPanel.add(iCB);
            iPanel.setMaximumSize(new Dimension(200,10));
        }

        private String [] COLORS = {
                "red"
                ,"blue"
                ,"green"
                ,"yellow"
                ,"cyan"
                ,"magenta"
                ,"pink"
                ,"gray"
                ,"white"

        };

	private String[] SHAPES = { "circle", "dot" };

	// increase line sizes to choos from when determining circle and dot line thickness
        private String [] SIZES = {"1", "2", "3", "4", "5","6","7","8","9","10"};

    }


    public class PropertiesTab implements ActionListener {
        JPanel                          iPanel;
        //SublineageDisplayProperty []    iDispProps;
        ColorSchemeUI []                 iCSUI;

        @SuppressWarnings("unused")
		public PropertiesTab() {
            Border blackline = BorderFactory.createLineBorder(Color.black);
            iDispProps = getDisplayProps();
            iCSUI = new ColorSchemeUI[iDispProps.length];
            iPanel = new JPanel();
            iPanel.setLayout(new BorderLayout());
            iPanel.setBorder(blackline);
            //iPanel.setLayout(new BoxLayout(iPanel, BoxLayout.PAGE_AXIS));
            JPanel lineagePanel = new JPanel();
            JPanel dummyPanel = new JPanel();
            JPanel topPart = new JPanel();
            topPart.setLayout(new GridLayout(1,2));
            lineagePanel.setLayout(new GridLayout(0,1));
            lineagePanel.setBorder(blackline);
            //lineagePanel.setMaximumSize(new Dimension(300,400));
            topPart.add(lineagePanel);
            topPart.add(dummyPanel);
            JPanel [] testPanel = new JPanel[iDispProps.length];
            JTextField textField;
            JComboBox cb;
            JPanel labelPanel = new JPanel();
            JLabel sublineage = new JLabel("item");
            JLabel color = new JLabel("color");
            labelPanel.setLayout(new GridLayout(1,2));
            labelPanel.add(sublineage);
            labelPanel.add(color);
            lineagePanel.add(labelPanel);

            for (int i=0; i < iDispProps.length; i++) {
                iCSUI[i] = new ColorSchemeUI(i);
                lineagePanel.add(iCSUI[i].iPanel);
            }
            lineagePanel.setMaximumSize(new Dimension(200, 200));
            iPanel.add(topPart, BorderLayout.NORTH);
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(1,3));
            //buttonPanel.setMinimumSize(new Dimension(400, 100));

            JButton reset = new JButton("Reset");
            JButton apply = new JButton("Apply");
            JButton cancel = new JButton("Cancel");
            buttonPanel.add(apply);
            reset.addActionListener(this);
            apply.addActionListener(this);
            cancel.addActionListener(this);
            buttonPanel.add(reset);
            buttonPanel.add(apply);
            buttonPanel.add(cancel);
            JPanel botPart = new JPanel();
            botPart.setLayout(new GridLayout(5,1));
            botPart.add(new JPanel());
            botPart.add(buttonPanel);
            botPart.add(new JPanel());
            botPart.add(new JPanel());
            botPart.add(new JPanel());
            iPanel.add(botPart, BorderLayout.CENTER);

            //iPanel.add(buttonPanel, BorderLayout.CENTER);
            //iPanel.add(new JPanel(), BorderLayout.CENTER);

        }

        // I do not think that this action listener is being used -DT
        @Override
		public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("Reset")) {
                iDispProps = getDisplayProps();
                for (int i=0; i < iDispProps.length; i++) {
                    iCSUI[i].iLabel.setText(iDispProps[i].iName);
                    iCSUI[i].iCB.setSelectedIndex(iDispProps[i].iLineageNum);
                }


            } else if (command.equals("Apply")) {
                for (int i=0; i < iDispProps.length; i++) {
                    String name = iCSUI[i].iTF.getText();
                    if (name.length() == 0) 
                    	name = "-";
                    int num = iCSUI[i].iCB.getSelectedIndex();
                    iDispProps[i].iName = name;
                    iDispProps[i].iLineageNum = num;
                }

            }
        }

        public JPanel getPanel() {
            return iPanel;
        }

        private String [] COLORS = {
            "red"
            ,"blue"
            ,"green"
            ,"yellow"
            ,"cyan"
            ,"magenta"
            ,"pink"
            ,"gray"
            ,"white"
        };

        private String [] SIZES = {"1", "2", "3"};

        private static final int WIDTH = 15;

    }
    // END class ColorSchemeUI

    public ColorSchemeDisplayProperty [] getDisplayProps() {
        ColorSchemeDisplayProperty [] dispProps = {
		    new ColorSchemeDisplayProperty("normal centroid", 1)
		    ,new ColorSchemeDisplayProperty("selected centroid", 8)
		    ,new ColorSchemeDisplayProperty("annotations", 8)
		    ,new ColorSchemeDisplayProperty("upper sister", 4)
		    ,new ColorSchemeDisplayProperty("lower sister", 5)
		    ,new ColorSchemeDisplayProperty("line size" , 0)
		    ,new ColorSchemeDisplayProperty("nucleus marker", 0)
		    ,new ColorSchemeDisplayProperty("bookmarked centroid", 1)
        };
        return dispProps;
    }



    public void setAceTree(AceTree aceTree) {
        iAceTree = aceTree;
    }

    public AceTree getAceTree() {
        return iAceTree;
    }

    /**
     * Revised refresh method that is given a fully processed ImagePlus from the management classes
     *
     * @param imageName
     * @param ip
     * @param planeNumber
     */
    public void refreshDisplay(String imageName, ImagePlus ip, int planeNumber) {
        // not necessary to show stdout on null image plus - it is expected behavior and we just show annotations if present
        if (ip == null) {
//            System.out.println("Null image attempting to load: " + iAceTree.getImageManager().getCurrentImageName());
            // make a blank image that is the same dimension as the valid images in the dataset and we'll draw annotations on it
            ip = new ImagePlus();
            ImageProcessor iproc = new ColorProcessor(iImgPlus.getWidth(), iImgPlus.getHeight());
            ip.setProcessor(imageName, iproc);
        }

        boolean inMaxProjectionMode = false;
        String directoryDelimiter = ImageNameLogic.getDirectoryDelimiter(imageName);
        if (planeNumber == -1) {
            setTitle(imageName.substring(imageName.lastIndexOf(directoryDelimiter)));
        } else if (planeNumber == Integer.MAX_VALUE) {
            // this will be the hook used to identify that a MAXIMUM INTESITY PROJECTION is being used
            setTitle(imageName.substring(imageName.lastIndexOf(directoryDelimiter)) + " - Maximum Intensity Projection");
            inMaxProjectionMode = true;
        } else {

            setTitle(imageName.substring(imageName.lastIndexOf(directoryDelimiter)) + " (plane " + planeNumber + ")");
        }

        if (ip != null)
            iImgPlus.setProcessor(imageName, ip.getProcessor());
        if (iIsMainImgWindow && iAceTree.isTracking()) {
            //System.out.println("In imagewindow, acetree is tracking so adding main annotation");
            iAceTree.addMainAnnotation();
        }
        if (iAceTree.getShowCentroids())
            showCentroids(inMaxProjectionMode);
        if (iAceTree.getShowAnnotations())
            showAnnotations(inMaxProjectionMode);
        if (iSpecialEffect != null)
            showSpecialEffect();

        iImgCanvas.repaint();

        if(iImageZoomerPanel!=null){
            BufferedImage image = BufferedImageCreator.create((ColorProcessor)iImgPlus.getProcessor());
            iImageZoomerPanel.updateImage(image);
        }
        if (iImageZoomerFrame != null) {
            BufferedImage image = BufferedImageCreator.create((ColorProcessor)iImgPlus.getProcessor());
            iImageZoomerFrame.updateImage(image);
        }
    }

    /**
     * Revised refreshDisplay() method that is called by other classes and refers back to the ImageManager in AceTree
     * to update the view without being passed parameters explicitly i.e. this call assumes that the image manager has
     * been properly update prior to this call
     */
    public void refreshDisplay() {
        refreshDisplay(iAceTree.getImageManager().getCurrentImageName(),
                iAceTree.getImageManager().makeImage(),
                iAceTree.getImageManager().getCurrImagePlane());
    }

    /**
     * Commented out on 1/3/2019
     *
     */
//    public ImagePlus refreshDisplay() {
//        String imageName = iAceTree.getImageManager().getCurrentImageName();
//        iTitle = imageName;
//
//        setTitle(imageName.substring(4));
//
//        if (iIsMainImgWindow) {
//            iTimeInc = iAceTree.getTimeInc();
//            iPlaneInc = iAceTree.getPlaneInc();
//            imagewindowPlaneNumber = iAceTree.getImageManager().getCurrImagePlane()+iPlaneInc;
//        } else {
//            iTimeInc = 0;
//            iPlaneInc = 0;
//            //setImageTimeAndPlaneFromTitle();
//        }
//
//        // Append plane number to ImageWindow title in stack mode
//        if (iAceTree.getUseStack() == 1) {
//        	setTitle(imageName.substring(4) + " (plane "+imagewindowPlaneNumber+")");
//        }
//
//
//        ImagePlus ip = iAceTree.getImageManager().makeImage();
//        iAceTree.getImageManager().setCurrImage(ip);
//
//        if (ip == null) {
//            iAceTree.pausePlayerControl();
//            System.out.println("no ImagePlus for: " + iTitle);
//        }
//
//        if (iAceTree == null)
//        	return null;
//
//        switch (iAceTree.getColor()) {
//            case 1:
//                ip = makeGreenImagePlus(ip);
//                break;
//            case 2:
//                ip = makeRedImagePlus(ip);
//                break;
//            case 3:
//                ip = makePlainImagePlus(ip);
//                break;
//            default:
//        }
//        //ip = makeGreenImagePlus(ip);
//        // Set contrast values for red/green channels
//        if (ip != null && iAceTree.getUseStack() != 1) {
//        	// red channel
//        	ip.setDisplayRange(iAceTree.getImageManager().getContrastMin1(), iAceTree.getImageManager().getContrastMax1(), 4);
//        	// green channel
//        	ip.setDisplayRange(iAceTree.getImageManager().getContrastMin2(), iAceTree.getImageManager().getContrastMax2(), 2);
//        }
//
//        if (ip != null)
//        	iImgPlus.setProcessor(imageName, ip.getProcessor());
//        if (iIsMainImgWindow && iAceTree.isTracking())
//        	iAceTree.addMainAnnotation();
//        if (iAceTree.getShowCentroids())
//        	//showCentroids();
//        if (iAceTree.getShowAnnotations())
//        	//showAnnotations();
//        if (iSpecialEffect != null)
//        	showSpecialEffect();
//
//        //iSpecialEffect = null;
//        iImgCanvas.repaint();
//
//		if(iImageZoomerPanel!=null){
//			BufferedImage image = BufferedImageCreator.create((ColorProcessor)iImgPlus.getProcessor());
//			iImageZoomerPanel.updateImage(image);
//		}
//		if (iImageZoomerFrame != null) {
//	    	BufferedImage image = BufferedImageCreator.create((ColorProcessor)iImgPlus.getProcessor());
//	    	iImageZoomerFrame.updateImage(image);
//	    }
//
//	    return iImgPlus;
//    }


    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
	public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        int mods = e.getModifiers();
        boolean shift = (mods & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
        boolean ctrl = (mods & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
        //println("ImageWindow.keyPressed, " + code + CS + shift + CS + ctrl + CS + e);
        if (shift || ctrl) sendToEIDialog2(code, shift, ctrl);
        else {
            switch(code) {
                case KeyEvent.VK_UP:
                    iAceTree.actionPerformed(new ActionEvent(this, 0, AceTree.UP));
                    break;
                case KeyEvent.VK_DOWN:
                    iAceTree.actionPerformed(new ActionEvent(this, 0, AceTree.DOWN));
                    break;
                case KeyEvent.VK_LEFT:
                    iAceTree.actionPerformed(new ActionEvent(this, 0, AceTree.PREV));
                    break;
                case KeyEvent.VK_RIGHT:
                    iAceTree.actionPerformed(new ActionEvent(this, 0, AceTree.NEXTT));
                    break;
                case KeyEvent.VK_F2:
                    iAceTree.actionPerformed(new ActionEvent(this, 0, "F2"));
                    break;
                default:
                    return;
            }
        }
    }

    @SuppressWarnings("unused")
	private void sendToEIDialog2(int keycode, boolean alt, boolean ctrl) {
    	println("sendToEIDialog2, ");
    	ActionEvent a = null;
        switch(keycode) {
	        case KeyEvent.VK_UP:
	            iAceTree.actionPerformed(new ActionEvent(this, 0, AceTree.UP));
	            break;
	        case KeyEvent.VK_DOWN:
	            iAceTree.actionPerformed(new ActionEvent(this, 0, AceTree.DOWN));
	            break;
	        case KeyEvent.VK_LEFT:
	        	//if (ctrl) a = new ActionEvent(this, 0, EIDialog2.LEFT);
	        	//else a = new ActionEvent(this, 0, EIDialog2.BIG);
	        	AddOneDialog addOne = iAceTree.iAddOneDialog;
	            if (addOne != null) addOne.actionPerformed(new ActionEvent(this, 0, AddOneDialog.LEFT));
	            break;
	        case KeyEvent.VK_RIGHT:
	            iAceTree.actionPerformed(new ActionEvent(this, 0, AceTree.NEXTT));
	            break;
	        case KeyEvent.VK_F2:
	            iAceTree.actionPerformed(new ActionEvent(this, 0, "F2"));
	            break;
	        default:
	            return;
        }
    }

    public void setSpecialEffect(Object [] specialEffect) {
        iSpecialEffect = specialEffect;
    }

    protected void showSpecialEffect() {
        if (!iAceTree.isTracking()) 
        	return;
        int x1 = ((Integer)iSpecialEffect[0]).intValue();
        int y1 = ((Integer)iSpecialEffect[1]).intValue();
        int z1 = ((Integer)iSpecialEffect[2]).intValue();
        int x2 = ((Integer)iSpecialEffect[3]).intValue();
        int y2 = ((Integer)iSpecialEffect[4]).intValue();
        int r2 = ((Integer)iSpecialEffect[5]).intValue();
        int z2 = ((Integer)iSpecialEffect[6]).intValue();
        String s = (String)iSpecialEffect[7];
        int offset = r2 + 4;
        if (y2 < y1) offset = -offset;


        ImageProcessor iproc = getImagePlus().getProcessor();
        //iproc.setColor(Color.magenta);
        iproc.setColor(COLOR[iDispProps[LOWERSIS].iLineageNum]);
        if (z2 <= z1) 
        	iproc.setColor(COLOR[iDispProps[UPPERSIS].iLineageNum]);
        //if (z2 <= z1) iproc.setColor(Color.cyan);

        iproc.setLineWidth(cLineWidth);
        //iproc.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
        iproc.drawLine(x1, y1, x2, y2);
        iproc.drawPolygon(EUtils.pCircle(x2, y2, r2));
        iproc.drawString("    " + s + "(" + z2 + ")", x2, y2 + offset);
    }

//    protected void setImageTimeAndPlaneFromTitle() {
//        int k = iTitle.lastIndexOf(DASHT) + DASHT.length();
//        if (k <= 1) {
//            iAceTree.getImageManager().setCurrImageTime(1);
//            iAceTree.getImageManager().setCurrImagePlane(15);
//            iTimeInc = 0;
//            iPlaneInc = 0;
//            String random = RANDOMT;
//            iIsMainImgWindow = iTitle.indexOf(random) == -1;
//            return;
//        }
//        System.out.println("setImage..: " + k);
//        String time = iTitle.substring(k, k + 3);
//        iAceTree.getImageManager().setCurrImageTime(Integer.parseInt(time));
//        String s = iTitle.substring(k);
//        k = s.indexOf(DASHP) + DASHP.length();
//        String plane = s.substring(k, k + 2);
//        iImagePlane = Integer.parseInt(plane);
//        iTimeInc = 0;
//        iPlaneInc = 0;
//        String random = RANDOMT;
//        if (cUseZip > 0) random = RANDOMF;
//        iIsMainImgWindow = iTitle.indexOf(random) == -1;
//    }


    public ImageCanvas getCanvas() {
        return iImgCanvas;
    }

    public ImagePlus getImagePlus() {
        return iImgPlus;
    }

    ////////////////////////////////////////

    @SuppressWarnings("unused")
	public void addAnnotation(int mx, int my, boolean dontRemove) {
        //System.out.println("Add annotation in ImageWindow");
        double x, y, r;
        boolean g;

        // if in max projection mode, we'll find the closest nuc by iterating through the stack.
        // if not in max projection mode, we'll use the current plane info to find the closest nucleus to the click point
        Nucleus n;
        if (iAceTree.getImageManager().isCurrImageMIP()) {
            n = cNucleiMgr.findClosestNucleus(mx, my, iAceTree.getImageManager().getCurrImageTime());
        } else {
            n = cNucleiMgr.findClosestNucleus(mx, my, iAceTree.getImageManager().getCurrImagePlane(), iAceTree.getImageManager().getCurrImageTime());
        }


        if (n != null) {
            if (cNucleiMgr.hasCircle(n, iAceTree.getImageManager().getCurrImagePlane()) || iAceTree.getImageManager().isCurrImageMIP()) {
                //System.out.println("Adding tag for: " + n.identity);
                String propername = PartsList.lookupSulston(n.identity);
                String label = n.identity;
                if (propername != null) {
                    label = label + " " + propername;
                }
                AnnotInfo ai = new AnnotInfo(label, n.x, n.y);
                // now, if this one is not in the vector add it
                // otherwise remove it
                boolean itemRemoved = false;
                boolean itemAlreadyPresent = false;
                String test = label;//n.identity;
                AnnotInfo aiTest = null;
                for (int k = 0; k < iAnnotsShown.size(); k++) {
                    aiTest = (AnnotInfo) iAnnotsShown.elementAt(k);
                    if (aiTest.iName.equals(test)) {
                        itemAlreadyPresent = true;
                        if (!dontRemove) {
                            iAnnotsShown.remove(k);
                            itemRemoved = true;
                        }
                        break;
                    }

                }

                if (!itemRemoved && !itemAlreadyPresent) {
                    iAnnotsShown.add(ai);
                }
                // if this was a button 3 mouse click
                // and this is the main window
                // we will make this the current cell and makeDisplayText agree
                if (iIsRightMouseButton && iIsMainImgWindow) {
                    iIsRightMouseButton = false;
                }
            }
        }
	}


    public static final int [] WIDTHS = {1,2,3,4,5,6,7,8,9,10};
    
    @SuppressWarnings("unused")
	protected void showCentroids(boolean isInMaxProjectionMode) {
        int time = iAceTree.getImageManager().getCurrImageTime();
        if (time < 0) {
            iAceTree.getImageManager().setCurrImageTime(1);
            iTimeInc = 0;
        }
        
        Vector v = cNucleiMgr.getElementAt(iAceTree.getImageManager().getCurrImageTime() - 1);
        
        ImageProcessor iproc = getImagePlus().getProcessor();
        iproc.setColor(COLOR[iDispProps[NCENTROID].iLineageNum]);
        iproc.setLineWidth(WIDTHS[iDispProps[LINEWIDTH].iLineageNum]);
        Polygon p = null;
        
        Enumeration e = v.elements();
        Cell currentCell = iAceTree.getCurrentCell();
        while(e.hasMoreElements()) {
            Nucleus n = (Nucleus)e.nextElement();
            if (n.status < 0) 
            	continue;

            double x;
            if (isInMaxProjectionMode) {
                // instead of passing an image plane, we'll pass the z component of the nucleus itself which in effect gives us the actual diameter of the cell
                x = cNucleiMgr.nucDiameter(n, n.z);
            } else {
                x = cNucleiMgr.nucDiameter(n,
                        iAceTree.getImageManager().getCurrImagePlane());
            }
            if (x > 0) {
            	// Manage bookmarked cells
                if (iBookmarkListModel != null && !iBookmarkListModel.isEmpty()) {
                	String name = n.identity;
                	if (iBookmarkListModel.contains(name))
                		iproc.setColor(COLOR[iDispProps[BMCENTROID].iLineageNum]);
                }
                if (currentCell != null && n.hashKey != null && n.hashKey.equals(currentCell.getHashKey()) && iAceTree.isTracking()) {
                    iproc.setColor(COLOR[iDispProps[SCENTROID].iLineageNum]);
                }
				int TOGGLE_OPTION = 6; // toggle option colorscheme display property is 6th element in iDispProp
				if(iDispProps[TOGGLE_OPTION].iLineageNum == 0) { // don't toggle, default to empty circles
				    iproc.drawPolygon(EUtils.pCircle(n.x, n.y, (int)(x/2.)));
				} else {
				    iproc.drawDot(n.x, n.y);
				}
                iproc.setColor(COLOR[iDispProps[NCENTROID].iLineageNum]);
            }
        }
    }

    @SuppressWarnings("unused")
	private void drawRoi(int plane, Nucleus c, ImageProcessor iproc) {
        double d = cNucleiMgr.nucDiameter(c, plane);
        float fxx = c.x;
        float fyy = c.y;
        fxx -= d/2;
        fyy -= d/2;
        int xx = (int)(fxx + 0.5);
        int yy = (int)(fyy + 0.5);
        int dd = (int)(d + 0.5);

        //int d = (int)(c.d + 0.5);
        //System.out.println("processImage, d=" + d + C.CS + c.d);
        //int xx = c.x - d/2;
        //int yy = c.y - d/2;
        OvalRoi oRoi = new OvalRoi(xx, yy, dd, dd);
        //Color csave = iproc.getColor();
        iproc.setColor(new Color(0, 0, 255));
        oRoi.drawPixels(iproc);
        Rectangle r = oRoi.getBounds();
        int width = iproc.getWidth();
        int offset, i;
        for (int y=r.y; y < (r.y + r.height); y++) {
            offset = y * width;
            for (int x = r.x; x <= (r.x + r.width); x++) {
                i = offset + x;
                if (oRoi.contains(x, y)) {
                    //iproc.drawPixel(x,y);
                    int k = iproc.getPixel(x, y);
                    int m = k & -16711936;
                    //System.out.println("drawRoi: " + k + C.CS + m);
                    //redSum += Math.abs(redPixels[i]);
                }
            }
        }



    }

    @SuppressWarnings("unused")
	protected void showAnnotations(boolean inMaxProjectionMode) {
        Vector v = cNucleiMgr.getNucleiRecord().elementAt(iAceTree.getImageManager().getCurrImageTime()  - 1);
        int size = v.size();
        int [] x = new int[size];
        int [] y = new int[size];
        Vector annots = new Vector();
        Enumeration e = v.elements();
        while(e.hasMoreElements()) {
            AnnotInfo ai = null;
            Nucleus n = (Nucleus)e.nextElement();
            String propername = PartsList.lookupSulston(n.identity);
			String label = n.identity;
			if(propername != null){
			    label = label + " " + propername;
			}
         
            if (n.status >= 0 && (isInList(label) != null)) {
                ai = new AnnotInfo(label, n.x, n.y);

                // if we're in max projection mode, then we want to show all annotations so we'll add all annotations
                if (inMaxProjectionMode) {
                    annots.add(ai);
                } else if (cNucleiMgr.hasCircle(n, iAceTree.getImageManager().getCurrImagePlane())) {
                    annots.add(ai);
                }
            }
        }
        drawStrings(annots, this);
        //NucUtils.drawStrings(annots, this);
        //iShow.setText(HIDE);
    }

    private void drawStrings(Vector annots, ImageWindow imgWin) {
        ImagePlus imgPlus = imgWin.getImagePlus();
        ImageProcessor imgProc = imgPlus.getProcessor();
        ImageCanvas imgCan = imgWin.getCanvas();
        //imgProc.setColor(Color.yellow);
        //System.out.println("iDispProps: " + iDispProps);
        imgProc.setColor(COLOR[iDispProps[ANNOTATIONS].iLineageNum]);
        imgProc.setFont(new Font("SansSerif", Font.BOLD, 13));
        Enumeration e = annots.elements();
        while (e.hasMoreElements()) {
            AnnotInfo ai = (AnnotInfo)e.nextElement();
            imgProc.moveTo(imgCan.offScreenX(ai.iX),imgCan.offScreenY(ai.iY));
            
            // If there is a proper name appended, shows Sulston or terminal name base on user choice
            String name = ai.iName.trim(); //to avoid leading space
            int i = name.indexOf(" ");
            if (i > 0)
                if (iAceTree.getShowSulstonAnnotations()) {
                    name = name.substring(0, i+1);
                } else {
                    name = name.substring(i+1, name.length()).toUpperCase();
                }
            imgProc.drawString(name);
        }
        imgPlus.updateAndDraw();
    }

    public void updateCurrentCellAnnotation(Cell newCell, Cell old, int time) {
        //new Throwable().printStackTrace();
        //println("updateCurrentCellAnnotation: " + newCell.getName() + C + updateCurrent + old.getName() + CS + time);
        AnnotInfo ai = null;
        if (old != null) ai = isInList(old.getName());
        if (ai != null) iAnnotsShown.remove(ai);
        if (time == -1) time = newCell.getTime();
        String s = newCell.getHashKey();
        Nucleus n = null;
        //println("updateCurrentCellAnnotation:2 " + s);
        if (s != null) {
            n = cNucleiMgr.getNucleusFromHashkey(newCell.getHashKey(), time);
            //println("updateCurrentCellAnnotation:3 " + n);
        }
        if ((n != null) && (isInList(newCell.getName()) == null)) {

            String propername = PartsList.lookupSulston(n.identity);
            String label = n.identity;
            if (propername != null) {
                label = label + " " + propername;
            }
            ai = new AnnotInfo(label, n.x, n.y);

            iAnnotsShown.add(ai);
        }
    }

    public void clearAnnotations() {
        iAnnotsShown.clear();
    }

    public void addAnnotation(String name, int x, int y) {
        AnnotInfo ai = new AnnotInfo(name, x, y);
        iAnnotsShown.add(ai);
    }

    protected AnnotInfo isInList(String name) {
        //System.out.println("isInList: " + name + CS + iAnnotsShown.size());
        AnnotInfo aiFound = null;
        Enumeration e = iAnnotsShown.elements();
        while(e.hasMoreElements()) {
            AnnotInfo ai = (AnnotInfo)e.nextElement();
            boolean is = ai.iName.equals(name);
            if (is) {
                aiFound = ai;
                break;
            }
        }
        return aiFound;
    }

    public void saveImageIfEnabled() {
        if (iSaveImage) {
            while(iSaveInProcess);
            new Thread(this).start();
        }
    }

    @Override
	public void run() {
        iSaveInProcess = true;
        int k = 1000;
        if (iNewConstruction) {
            k = 5000; // long delay needed on new open
            iNewConstruction = false;
        }
        try {
            Thread.sleep(k);
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
        saveImage();

    }

	void saveJpeg(BufferedImage bi, String outFileName, int quality) {
		Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
		javax.imageio.ImageWriter writer = (javax.imageio.ImageWriter)iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(1);   // an integer between 0 and 1
		//println("otherSaveJpeg, " + iwp.canWriteCompressed() + CS + iwp.getCompressionQuality());
		//outFileName = "jpg" + quality + ".jpg";
		File file = new File(outFileName);
		if (file.exists()) file.delete();
		file = new File(outFileName);
		try {
			FileImageOutputStream output = new FileImageOutputStream(file);
			writer.setOutput(output);
			IIOImage image = new IIOImage(bi, null, null);
			writer.write(null, image, iwp);
			writer.dispose();
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void zoomView() {
        String title = "zoom";
        BufferedImage image = BufferedImageCreator.create((ColorProcessor)iImgPlus.getProcessor());
    	ImageZoomerFrame izf = new ImageZoomerFrame(this, image, 10.0, title);
    	izf.addKeyListener(this); //added to make zoom window respond to key events -AS 11/23/11
    	iImageZoomerFrame = izf;
	}

    @SuppressWarnings("unused")
	public void saveImage() {
        String title = makeTitle();
        if (title == null) {
            cancelSaveOperations();
            return;
        }
        Rectangle screenRect = this.getBounds();
        int topAdjust = 23;
        int y = screenRect.y;
        screenRect.y += topAdjust;
        int height = screenRect.height;
        screenRect.height -= topAdjust;
        // create screen shot
        //File f = new File("ij-ImageIO_.jar");
        //if (!f.exists()) {
        //    println("CANNOT SAVE FILES -- MISSING ij-ImageIO_.jar");
        //    return;
        //}

        Robot robot = null;
        BufferedImage image = null;
        if (iUseRobot) {
            try {
                robot = new Robot();
            } catch(AWTException e) {
                println("EXCEPTION -- NO ROBOT -- NOT SAVING");
                iSaveInProcess = false;
                iSaveImage = false;
                iAceTree.iAceMenuBar.resetSaveState();
                return;
            }
            image = robot.createScreenCapture(screenRect);
        } else {
            image = BufferedImageCreator.create((ColorProcessor)iImgPlus.getProcessor());
        }

        saveJpeg(image, title, 20);

        System.out.println("file: " + title + " written");
        iSaveInProcess = false;
    }

    public void cancelSaveOperations() {
        println("WARNING: NO IMAGE SAVE PATH -- NOT SAVING!");
        iSaveInProcess = false;
        iSaveImage = false;
        iAceTree.iAceMenuBar.resetSaveState();
        return;

    }

    public String getSaveImageDirectory() {
        if (iSaveImageDirectory != null) return iSaveImageDirectory;
        try {
            Class.forName("net.sf.ij.jaiio.BufferedImageCreator");
        } catch(ClassNotFoundException e) {
            iUseRobot = true;
            println("USING ROBOT FOR IMAGE2D SAVING");
        }

        try {
            JFileChooser fc = new JFileChooser("");
            fc.setDialogTitle("Save images to: ");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showOpenDialog(null);
            String path = null;
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                path = fc.getSelectedFile().getPath();
                iSaveImageDirectory = path;
                System.out.println("Saving images to: " + path);
                return path;
            }
            else {
            	System.out.println("Save cancelled by user.");
            	return null;
            }
        } catch (Exception e){
        	System.out.println("Failed to get save image directory.");
        	e.printStackTrace();
        	return null;
        }

    }

    private String makeTitle() {
        if (iSaveImageDirectory == null) {
            String dir = getSaveImageDirectory();
            iSaveImageDirectory = dir;
            if (dir == null) return null;
        }
        String s = iTitle;
        int j = s.lastIndexOf(C.Fileseparator) + 1;
        int k = s.lastIndexOf(".");
        s = s.substring(j, k) + ".jpeg";
        //s = s.substring(j, k) + ".png";
        s = iSaveImageDirectory + "/" + s;
        return s;
    }

    public void setSaveImageState(boolean saveIt) {
        iSaveImage = saveIt;
    }

    public Object [] getSpecialEffect() {
        return iSpecialEffect;
    }

    /**
     * Accessor method used by ImageAnnotationManager to determine the state of the user interaction
     *
     * Part of revisions: 10/2018
     * @author Braden Katzman
     * @return
     */
    public boolean isRightMouseButtonDown() {
        return this.iIsRightMouseButton;
    }

    /**
     * Mutator method used by ImageAnnotationManager to update the flag that checks for right click
     *
     * Part of revisions: 10/2018
     * @author Braden Katzman
     * @param value
     */
    public void setiIsRightMouseButton(boolean value) {
        this.iIsRightMouseButton = value;
    }

    /**
     * Accessor method used by ImageAnnotationManager to determine the state of the ImageWindow
     *
     * Part of revisions: 10/2018
     * @author Braden Katzman
     * @return
     */
    public boolean isMainImgWindow() {
        System.out.println("------ method used --------");
        return this.iIsMainImgWindow;
    }

    private class WinEventMgr extends WindowAdapter {
        @Override
		public void windowGainedFocus(WindowEvent e) {
        	iAceTree.requestFocus();
        }
        
        @Override
		public void windowClosing(WindowEvent e) {
            //System.out.println("windowClosing: " + iIsMainImgWindow);
            if (iIsMainImgWindow) 
            	dispose();
        }
    }

    class MouseHandler extends MouseInputAdapter {
    	ImageWindow iw;
        public MouseHandler(ImageWindow iw) {
            super();

            this.iw=iw;
        }

        @Override
		public void mouseMoved(MouseEvent e) {
		    //handle zoom view transform to original coordinate system
	        int x = e.getX();
	    	int y = e.getY();
	    	int x2 = iImageZoomerPanel.transform(x);
	    	//(int)Math.round(x * 100./ (double)m_imagePanel.getZoomedTo());
	    	int y2 = iImageZoomerPanel.transform(y);
	    	// (int)Math.round(y * 100./ (double)m_imagePanel.getZoomedTo());
	    	MouseEvent e2 = new MouseEvent(iw, 0, 0, 0, x2, y2, 0, false, e.getButton());
	    	try {
	    		iAceTree.mouseMoved(e2);
	    	}
	    	catch (NullPointerException npe) {
	    		// If the image is copiedw ith the END hotkey, it will not have a reference to AceTree
	    		// AceTree will be null in this case
	    		return;
	    	}
        }

        @Override
		public void mouseClicked(MouseEvent e) {
        	int x = e.getX();
	    	int y = e.getY();
	    	int x2 = iImageZoomerPanel.transform(x);
	    	//(int)Math.round(x * 100./ (double)m_imagePanel.getZoomedTo());
	    	int y2 = iImageZoomerPanel.transform(y);
	        	//println("ImageWindow.mouseClicked, " + e.getX() + CS + e.getY());
            int button = e.getButton();
            
            // e.BUTTON3 ie right click -DT
            if (button == MouseEvent.BUTTON3|e.isControlDown()) {
                iIsRightMouseButton = true;
            } else {
                iIsRightMouseButton = false;
            }
            if (button == MouseEvent.BUTTON3|e.isControlDown()) {
                Nucleus n;
                if (iAceTree.getImageManager().isCurrImageMIP()) {
                    n = cNucleiMgr.findClosestNucleus(x2,y2, iAceTree.getImageManager().getCurrImageTime());
                } else {
                    n = cNucleiMgr.findClosestNucleus(x2,y2, iAceTree.getImageManager().getCurrImagePlane(), iAceTree.getImageManager().getCurrImageTime());
                }

                if (n == null) {
                	//System.out.println("No nucleus selected to be active, cannot set current cell.");
                	return;
                }
                Cell c = iAceTree.getCellByName(n.identity);
                if (c != null) {
	                iAceTree.setCurrentCell(c, iAceTree.getImageManager().getCurrImageTime(), AceTree.RIGHTCLICKONIMAGE);
	                //System.out.println("Current cell set to "+n.identity);
                }
            } 
            else if (button == MouseEvent.BUTTON1) {
                //System.out.println("mouseClicked " + e.getX());
                addAnnotation(x2, y2, false);

                //iAceTree.updateDisplay();
            }
            //middle click turn on add intermediate cell while in edit mode
            else if (button == MouseEvent.BUTTON2) {
                if (iAceTree.iNucRelinkDialog == null ) return;
                UnifiedNucRelinkDialog unrd = null;
                try {
                    unrd = (UnifiedNucRelinkDialog)iAceTree.iNucRelinkDialog;
                } catch (ClassCastException cce) {
                    return;
                }
                if (!unrd.getAddKeyframeActive()) {
                    unrd.setiWarned(true);
                } else {
                    unrd.setiWarned(false);
                }
                ActionEvent ae = new ActionEvent(iAceTree, 1, UnifiedNucRelinkDialog.SHORTCUTTRIGGER);
                unrd.actionPerformed(ae);
                return;
            }
            
            iAceTree.cellAnnotated(getClickedCellName(x2, y2));
            iAceTree.updateDisplay();
            MouseEvent e2 = new MouseEvent(iw, 0, 0, 0, x2, y2, 0, false, e.getButton());
            processEditMouseEvent(e2);
        }

    }

    @SuppressWarnings("unused")
	private String getClickedCellName(int x, int y) {
        int timeInc = 0;
        int planeInc = 0;
        if (iIsMainImgWindow) {
            timeInc = iAceTree.getImageManager().getCurrImageTime();
            planeInc = iAceTree.getImageManager().getCurrImagePlane();
        }
        String name = "";
        Nucleus n;
        if (iAceTree.getImageManager().isCurrImageMIP()) {
            n = cNucleiMgr.findClosestNucleus(x,y, iAceTree.getImageManager().getCurrImageTime());
        } else {
            n = cNucleiMgr.findClosestNucleus(x,y, iAceTree.getImageManager().getCurrImagePlane(), iAceTree.getImageManager().getCurrImageTime());
        }
        if (n != null) {
            if (iAceTree.getImageManager().isCurrImageMIP()) {
                //System.out.println("setting name: " + n.identity);
              name = n.identity;
            } else if (cNucleiMgr.hasCircle(n, iAceTree.getImageManager().getCurrImagePlane())) {
                name = n.identity;
            }
        }
        return name;
    }

    protected static final String
    RANDOMF = ".zip0"
   ,RANDOMT = ".tif0"
   ,DASHT = "-t"
   ,DASHP = "-p"
   ;

    private static final int
    	GREENCHANNEL = 2,
    	REDCHANNEL = 4,
    	CHANNELMAX = 255,
    	CHANNELMIN = 0
	;
    
    public static final Integer
         ANTERIOR = new Integer(1)
        ,POSTERIOR = new Integer(2)
        ,NONE = new Integer(0)
        ;

    private static final String
         CS = ", "
        ;

    private static final int
    DATA_BLOCK_SIZE  = 2048
   //,LINEWIDTH = 1
   ;

    public static final int
         NCENTROID = 0
        ,SCENTROID = 1
        ,ANNOTATIONS = 2
        ,UPPERSIS = 3
        ,LOWERSIS = 4
        ,LINEWIDTH = 5
        ,BMCENTROID = 7
        ;

    public static final Color [] COLOR = {
        Color.RED
        ,new Color(140,70,255)
        ,Color.GREEN
        ,Color.YELLOW
        ,Color.CYAN
        ,Color.MAGENTA
        ,Color.PINK
        ,Color.LIGHT_GRAY
        ,Color.WHITE
    };


    private static void println(String s) {
    	System.out.println(s);
	}


    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
	public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }


    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
	public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

	//     private JButton     iAddSeries;
	//     private JButton     iAddOne;
	public JDialog 		iDialog;
	public JDialog 		iDialog2;


    public void launchImageParamsDialog(){
    	new ImageParamsDialog(this);
    }
    
    // Display channels contrast tool for 16-bit to 8-bit images
    public void launchContrastTool() {
    	if (ict == null) {
    		ict = new ImageContrastTool(this, iAceTree.getUseStack());
    		iSlider1min = ict.getSlider1min();
    		iSlider1max = ict.getSlider1max();
    		iSlider2min = ict.getSlider2min();
    		iSlider2max = ict.getSlider2max();
    		iSlider3min = ict.getSlider3min();
    		iSlider3max = ict.getSlider3max();

    		
    		ict.setSlider1min(iAceTree.getImageManager().getContrastMin1());
        	ict.setSlider1max(iAceTree.getImageManager().getContrastMax1());
        	ict.setSlider2min(iAceTree.getImageManager().getContrastMin2());
        	ict.setSlider2max(iAceTree.getImageManager().getContrastMax2());
        	ict.setSlider3min(iAceTree.getImageManager().getContrastMin3());
        	ict.setSlider3max(iAceTree.getImageManager().getContrastMax3());
    		
    		SliderListener sl = new SliderListener();
        	iSlider1min.addChangeListener(sl);
        	iSlider1max.addChangeListener(sl);
        	iSlider2min.addChangeListener(sl);
        	iSlider2max.addChangeListener(sl);
        	iSlider3min.addChangeListener(sl);
        	iSlider3max.addChangeListener(sl);
    	}
    	else {
    		ict.setSlider1min(iAceTree.getImageManager().getContrastMin1());
        	ict.setSlider1max(iAceTree.getImageManager().getContrastMax1());
        	ict.setSlider2min(iAceTree.getImageManager().getContrastMin2());
        	ict.setSlider2max(iAceTree.getImageManager().getContrastMax2());
        	ict.setSlider3min(iAceTree.getImageManager().getContrastMin3());
        	ict.setSlider3max(iAceTree.getImageManager().getContrastMax3());
        	
        	ict.setVisible(true);
    	}
    }
    
    // Change event listener implementation for sliders

    /**
     * TODO - revise so that callback to AceTree.updateDisplay isn't used in this way
     */
    public class SliderListener implements ChangeListener {
    	@Override
		public void stateChanged(ChangeEvent e) {
    		JSlider source = (JSlider)e.getSource();
    		if (!source.getValueIsAdjusting()) {
    			int min1 = iSlider1min.getValue();
				int max1 = iSlider1max.getValue();
    			if (min1 <= max1) {
    				//System.out.println("ImageWindow set Red min, max: "+min1+CS+max1);
                    iAceTree.getImageManager().setContrastMin1(min1);
                    iAceTree.getImageManager().setContrastMax1(max1);
    			}
    			int min2 = iSlider2min.getValue();
    			int max2 = iSlider2max.getValue();
    			if (min2 <= max2) {
    				//System.out.println("ImageWindow set Green min, max: "+min2+CS+max2);
                    iAceTree.getImageManager().setContrastMin2(min2);
                    iAceTree.getImageManager().setContrastMax2(max2);
    			}
    			int min3 = iSlider3min.getValue();
    			int max3 = iSlider3max.getValue();
    			if (min3 <= max3) {
    			    iAceTree.getImageManager().setContrastMin3(min3);
    			    iAceTree.getImageManager().setContrastMax3(max3);
                }

    			iAceTree.updateDisplay();
    		}
    	}
    }

    public void updateCellAnnotation(Cell newCell, String oldName, int time) {
        AnnotInfo ai = isInList(oldName);
        if (ai != null) {
            iAnnotsShown.remove(ai);
            //if (time == -1) time = newCell.getTime();
            Nucleus n = ImageWindow.cNucleiMgr.getNucleusFromHashkey(newCell.getHashKey(), time);
            String name = newCell.getName();
            if (isInList(name) == null) {
            	int space = name.indexOf(" ");
            	if (space >= 0)
            		name = name.substring(space+1, name.length());
                ai = new AnnotInfo(name, n.x, n.y);
                iAnnotsShown.add(ai);
            }
        }
    }

    public void processEditMouseEvent(MouseEvent e) {
		if (iDialog == null & iDialog2==null) 
			return;
		if(iDialog2 !=null){
		    UnifiedNucRelinkDialog relink=(UnifiedNucRelinkDialog)iDialog2;
		    relink.processMouseEvent(e);
		}
		if (iDialog !=null){
			AddOneDialog addOne = (AddOneDialog)iDialog;
			addOne.processMouseEvent(e);
	    }
    }
}
