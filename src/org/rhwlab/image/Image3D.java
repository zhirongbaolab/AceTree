/*
 * Created on Nov 4, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.image;

//import gov.noaa.pmel.sgt.LineAttribute;
import java.net.URL;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.text.*;

import javax.media.j3d.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.Border;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.acetree.PlayerControlAceAtlas;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import qdxml.DocHandler;
import qdxml.QDParser;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
/**
 * @author biowolp
 *
 */

public class Image3D extends MouseAdapter
    implements ActionListener, Runnable, DocHandler, ChangeListener, ItemListener {

    protected PickCanvas      iPickCanvas;
    protected AceTree         iAceTree;
    protected NucleiMgr       iNucleiMgr;
    public BranchGroup        iBG2; 
    private Background        iBackground;
    protected JFrame          iFrame;
    protected SimpleUniverse  iUniverse;
    protected Canvas3D        iCanvas;

    protected Canvas3D			iOfflineCanvas;
    protected SimpleUniverse	iOfflineUniverse;
    
    //////////////////// START COLOR CONTROL PANEL //////////////////// 
    private     JPanel              iColorControlPanel;
    private     SublineageUI []     iSubUI;
    private     JTextField          iMinRedField;
    private     JTextField          iMaxRedField;
    private     JCheckBox           iUseExprBox;
    private     JRadioButton        iUseExprColors;
    private     JCheckBox           iShowNonExpressingChkBox;
    
    private     JPanel              trackingPanel;
    private     JButton             customTailColor;
    private     JSlider             timePts;
    
    private     JSlider             overlayMinXSlider;
    private     JSlider             overlayMinYSlider;
    private     JSlider             overlayMinZSlider;
    private     JSlider             overlayMaxXSlider;
    private     JSlider             overlayMaxYSlider;
    private     JSlider             overlayMaxZSlider;
    private     JSlider             overlaySubsampleSlider;
    private     JCheckBox           overlayRedChannel;
    private     JCheckBox           overlayGreenChannel;
    private     JCheckBox           overlayBlueChannel;
    private     JCheckBox           overlayAutoROI;
    //////////////////// END COLOR CONTROL PANEL //////////////////// 

    private     JButton             showOverlay;
    private     JButton             showTails;

    protected String        iTitle;
    boolean                 iNewConstruction;
    Thread                  iThread;
    boolean                 iSaveInProcess;
    static boolean          iSaveImage;         // is AceTree in the process of saving an image?
    boolean                 fakeit1;
    boolean                 fakeit2;
    JTextField              iAngle;
    JTextField              iScale;
    JLabel                  iPick;
    private int             iXA;
    protected int           iYA;
    protected float         iZA;

    protected  Transform3D     iRotate;
    protected TransformGroup  iRotGroup;
    protected TransformGroup  iTranslateGroup;
    protected Matrix4d        iMatrix;

    protected JPanel          	iImagePanel;
    //protected JPanel          iControlPanel;
    protected  JTabbedPane     	iTabbedPane;
    //public PropertiesTab2   	iPT2;

    protected JTextField  iAngX;
    protected JTextField  iAngXInc;
    protected JButton     iXUp;
    protected JButton     iXDn;

    protected JTextField  iAngY;
    protected JTextField  iAngYInc;
    protected JButton     iYUp;
    protected  JButton     iYDn;

    protected JTextField  iAngZ;
    protected JTextField  iAngZInc;
    protected JButton     iZUp;
    protected JButton     iZDn;

    protected JTextField  iPosIncr;
    protected JTextField  iPos;
    protected JButton     iPIn;
    protected JButton     iPOut;

    protected JButton     iRestore;
    protected  JButton    iUndoButton;
    protected Vector      iUndo;

    protected JButton     iSaveMovie;
    protected JButton 	  iShowSisters;
    
    protected JButton     iLoadButton;
    protected JButton     iSaveButton;
    //protected String      iCurrentRotDir;

    protected JButton     iSaveImageButton;
    protected String      iSaveImageAsDir;
    protected String      iLastSaveAsName;

    protected String iSaveMovieDir;
    
    protected JCheckBox		iSameSizeSpheres;
    protected JSlider		iSphereScale;

    BranchGroup iBGT;
    Indicator3D iIndicator;
    
    private BranchGroup iNucBG;
    protected   Image3DGeometryManager  geometryManager;
    protected   Image3DViewConfig       viewConfig;
    private     Image3DOverlayGenerator overlayGenerator;
    
    // For 16-bit images
    protected int image3DUseStack;

    private static final String
		CS = ", "
        ,IMAGETYPE = "jpeg"
    ;

    private Color3f [] BACKGROUNDS = {
		new Color3f(1.f, 1.f, 1.f) // lite gray
		,new Color3f(0.3f, 0.3f, 0.3f) // gray
		,new Color3f(0.1f, 0.1f, 0.1f) // dark gray
    };

    public Image3D(){}

    public Image3D(AceTree aceTree, String title) {
    	try {
    		Class.forName("javax.media.j3d.VirtualUniverse");
    	} catch (ClassNotFoundException e) {
    		return;
    	}
        iAceTree = aceTree;
        iNucleiMgr = iAceTree.getNucleiMgr();
        iTitle = title;
        viewConfig = Image3DViewConfig.getInstance(); 
        viewConfig.setTitle(iTitle);
        geometryManager = new Image3DGeometryManager(this);
        overlayGenerator = new Image3DOverlayGenerator(this);
        overlayGenerator.start();
        System.out.println("Overlay generator now running...");

        iFrame = new JFrame(title);
        //iFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        WinEventMgr wem = new WinEventMgr();
        iFrame.addWindowListener(wem);
        
        System.out.println("triggered by WormGUIDES button");
        
        // Add functionality to close window event
        /*
        iFrame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		geometryManager.setOverlayReady(false);
        		geometryManager.setOverlay(null);
        		viewConfig.setShowingOverlay(false);
        		//iFrame.dispose();
        		iFrame.setVisible(false);
        	}
        });
        */

        iNewConstruction = true;

        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        template.setSceneAntialiasing(GraphicsConfigTemplate.PREFERRED);

        GraphicsConfiguration config =
                    GraphicsEnvironment.getLocalGraphicsEnvironment().
                     getDefaultScreenDevice().getBestConfiguration(template);
        
        iCanvas = new Canvas3D(config);
        iCanvas.setSize(ImageWindow.cImageWidth, ImageWindow.cImageHeight);

        iUniverse = new SimpleUniverse(iCanvas);
        
        if (iUniverse == null || iUniverse.getViewer() == null)
        	System.out.println("iUniverse or viewer is null...");
        
        iUniverse.getViewer().getView().setSceneAntialiasingEnable(false);
        iUniverse.getViewingPlatform().setNominalViewingTransform();

        iOfflineCanvas = new Canvas3D(config, true);
        iOfflineCanvas.setSize(ImageWindow.cImageWidth, ImageWindow.cImageHeight);

        iOfflineUniverse = new SimpleUniverse(iOfflineCanvas);
        iOfflineUniverse.getViewingPlatform().setNominalViewingTransform();


        ViewingPlatform viewingPlatform = iUniverse.getViewingPlatform( );
        iTranslateGroup = viewingPlatform.getViewPlatformTransform( );

        iMatrix  = new Matrix4d( );
        Transform3D t3d = new Transform3D();
        iTranslateGroup.getTransform(t3d);

        buildOutImageUI();
        buildColorControlGUI();
        
        // Make color management panel scrollable
        JScrollPane iColorControlPanelScroll = new JScrollPane(iColorControlPanel);

        iTabbedPane = new JTabbedPane();
        iTabbedPane.addTab("Image", null, iImagePanel, "View 3D image");

        Object dispProps = iAceTree.getDispProps3D2();
        if (dispProps == null) iAceTree.setDispProps3D2( viewConfig.getDispProps() );        

        //iPT2 = new PropertiesTab2(this);
        iTabbedPane.addTab("Color Controls", null, iColorControlPanelScroll, "Set color scheme");
        
        URL imageURL = Image3D.class.getResource("/images/icon2.gif");
        ImageIcon test=new ImageIcon(imageURL, "x");	
        iFrame.setIconImage(test.getImage());
        
        iFrame.getContentPane().add(iTabbedPane);

        iCanvas.addMouseListener(this);
        iFrame.pack();
        iFrame.setVisible(true);

        iUndo = new Vector();
        insertContent(iTitle);
        iSaveImageAsDir = "";
        iLastSaveAsName = "";

        setKeyboardActions();//attempt to add keyboard controls to this
        
        // For 16-bit images
        image3DUseStack = iAceTree.getUseStack();
    }

    public AceTree getAceTree() {
        return iAceTree;
    }

    // set keyboard actions
    protected void setKeyboardActions() {
    	String actionKey = "";
    	KeyStroke stroke = null;
    	InputMap inputMap = null;
    	ActionMap actionMap = null;

        String s = "LEFT";
        Action left = new AbstractAction("LEFT") {
            @Override
			public void actionPerformed(ActionEvent e) {
                iAceTree.prevImage();
            }
        };
        iTabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        iTabbedPane.getActionMap().put(s, left );

        actionKey = "a_left";
        stroke = KeyStroke.getKeyStroke("typed a");
        inputMap = iTabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(stroke, actionKey);
        actionMap = iTabbedPane.getActionMap();
        actionMap.put(actionKey, left);
        
        // Fast (skipping a few planes) LEFT using CTRL
        s = "shift LEFT";
        Action shift_left = new AbstractAction() {
        	@Override
			public void actionPerformed(ActionEvent e) {
        		iAceTree.prevImageFast();
        	}
        };
        iTabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        iTabbedPane.getActionMap().put(s, shift_left);

        s = "RIGHT";
        Action right = new AbstractAction(s) {
            @Override
			public void actionPerformed(ActionEvent e) {
                //System.out.println("right key pressed");
                iAceTree.nextImage();
            }
        };
        //AceTreeActions right = new AceTreeActions("RIGHT", 12345);
        iTabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        iTabbedPane.getActionMap().put(s, right );
        
        // Fast (skipping a few planes) UP using CTRL
        s = "shift RIGHT";
        Action shift_right = new AbstractAction() {
        	@Override
			public void actionPerformed(ActionEvent e) {
        		iAceTree.nextImageFast();
        	}
        };
        iTabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(s), s);
        iTabbedPane.getActionMap().put(s, shift_right);

        actionKey = "d_right";
        stroke = KeyStroke.getKeyStroke("typed d");
        inputMap = iTabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(stroke, actionKey);
        actionMap = iTabbedPane.getActionMap();
        actionMap.put(actionKey, right);
    }
    //end keyboard actions
    
    protected void buildOutImageUI() {
        iPick = new JLabel("pick");

        iImagePanel = new JPanel();
        iImagePanel.setLayout(new BorderLayout());
        iImagePanel.add(iCanvas, "Center");

        JPanel secondPanel = new JPanel(new BorderLayout());
        //panel to hold full width elements below scene
        JPanel belowScene=new JPanel(new GridLayout(0,1));
        
        secondPanel.add(belowScene,"North");

        belowScene.add(new PlayerControlAceAtlas(iAceTree));
        belowScene.add(iPick);
 
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.PAGE_AXIS));
       
        JPanel rotatePanels = new JPanel();
        rotatePanels.setLayout(new GridLayout(0, 1));
        JPanel rotatePanel = new JPanel();
        rotatePanel.setLayout(new GridLayout(1, 0));

        rotatePanel.add(new JLabel("angX"));
        iAngXInc = new JTextField("30", 5);
        iAngX = new JTextField("0", 10);
        iXUp = new JButton("up");
        iXDn = new JButton("down");
        rotatePanel.add(iAngXInc);
        rotatePanel.add(iAngX);
        rotatePanel.add(iXUp);
        rotatePanel.add(iXDn);
        iXUp.addActionListener(this);
        iXDn.addActionListener(this);
        rotatePanels.add(rotatePanel);

        rotatePanel = new JPanel();
        rotatePanel.setLayout(new GridLayout(1, 0));
        rotatePanel.add(new JLabel("angY"));
        iAngYInc = new JTextField("30", 5);
        iAngY = new JTextField("0", 10);
        iYUp = new JButton("up");
        iYDn = new JButton("down");
        rotatePanel.add(iAngYInc);
        rotatePanel.add(iAngY);
        rotatePanel.add(iYUp);
        rotatePanel.add(iYDn);
        iYUp.addActionListener(this);
        iYDn.addActionListener(this);
        rotatePanels.add(rotatePanel);

        rotatePanel = new JPanel();
        rotatePanel.setLayout(new GridLayout(1, 0));
        rotatePanel.add(new JLabel("angZ"));
        iAngZInc = new JTextField("30", 5);
        iAngZ = new JTextField("0", 10);
        iZUp = new JButton("up");
        iZDn = new JButton("down");
        rotatePanel.add(iAngZInc);
        rotatePanel.add(iAngZ);
        rotatePanel.add(iZUp);
        rotatePanel.add(iZDn);
        iZUp.addActionListener(this);
        iZDn.addActionListener(this);
        rotatePanels.add(rotatePanel);

        rotatePanel = new JPanel();
        rotatePanel.setLayout(new GridLayout(1, 0));
        rotatePanel.add(new JLabel("Pos"));

        Transform3D t3d = new Transform3D();
        iTranslateGroup.getTransform(t3d);
        t3d.get(iMatrix);

        iPosIncr = new JTextField("0.2", 5);
        iPos = new JTextField(fmt1(iMatrix.m23), 10);
        iPIn = new JButton("in");
        iPOut = new JButton("out");
        rotatePanel.add(iPosIncr);
        rotatePanel.add(iPos);
        rotatePanel.add(iPIn);
        rotatePanel.add(iPOut);
        iPIn.addActionListener(this);
        iPOut.addActionListener(this);
        rotatePanels.add(rotatePanel);

        rotatePanel = new JPanel();
        //rotatePanel.setLayout(new GridLayout(1, 0));
       

        rotatePanel.setLayout(new GridLayout(1, 0));
        iLoadButton = new JButton("Load Orientation");
        rotatePanel.add(iLoadButton);
        iLoadButton.addActionListener(this);
        iSaveButton = new JButton("Save Orientation");
        iSaveButton.addActionListener(this);
        rotatePanel.add(iSaveButton);
        //iCurrentRotDir = ".";
        rotatePanels.add(rotatePanel);
        
      
         rotatePanel = new JPanel();
         rotatePanel.setLayout(new GridLayout(1, 0));
        iRestore = new JButton("Default Orientation");
        iRestore.addActionListener(this);
        rotatePanel.add(iRestore);
        iShowSisters=new JButton("Show Sisters");
        iShowSisters.addActionListener(this);

        showTails = new JButton("Show Tails");
        showTails.addActionListener(this);
        rotatePanel.add(showTails);
        rotatePanel.add(iShowSisters);

        showOverlay = new JButton("Show Overlay");
        showOverlay.addActionListener(this);
        rotatePanel.add(showOverlay);
        
         rotatePanels.add(rotatePanel);

         rotatePanel = new JPanel();
         rotatePanel.setLayout(new GridLayout(1, 0));
        iSaveMovie = new JButton("Save Movie");
        rotatePanel.add(iSaveMovie);
        iSaveMovie.addActionListener(this);
        iSaveImageButton = new JButton("Save Still Image");
        rotatePanel.add(iSaveImageButton);
        iSaveImageButton.addActionListener(this);
        rotatePanel.add(iSaveImageButton);
       
        rotatePanels.add(rotatePanel);
     
        newPanel.add(rotatePanels);

        //secondPanel.setLayout(new BoxLayout(secondPanel,BoxLayout.LINE_AXIS));
        secondPanel.add(newPanel, "West");
        iIndicator = new Indicator3D();
        iIndicator.setAlignmentY(Component.CENTER_ALIGNMENT);
        secondPanel.add(iIndicator, "East");
       
        iImagePanel.add(secondPanel, "South");

    }


    public void updateDisplayedTab() {
        iTabbedPane.setSelectedIndex(0);
        insertContent(iTitle);
    }

    private void applyTrans(double incr, char axis) {
        int angle = 0;
        Transform3D t3d = new Transform3D();
        switch(axis) {
	case 'x':
	    t3d.rotX(incr);
	    iUndo.add(new Trans(t3d, incr, 'x'));
	    angle = Integer.parseInt(iAngX.getText());
	    angle += Math.round(Math.toDegrees(incr));
	    angle = angle % 360;
	    iAngX.setText(String.valueOf(angle));
	    break;
	case 'y':
	    t3d.rotY(incr);
	    iUndo.add(new Trans(t3d, incr, 'y'));
	    angle = Integer.parseInt(iAngY.getText());
	    angle += Math.round(Math.toDegrees(incr));
	    angle = angle % 360;
	    iAngY.setText(String.valueOf(angle));
	    break;
	case 'z':
	    t3d.rotZ(incr);
	    iUndo.add(new Trans(t3d, incr, 'z'));
	    angle = Integer.parseInt(iAngZ.getText());
	    angle += Math.round(Math.toDegrees(incr));
	    angle = angle % 360;
	    iAngZ.setText(String.valueOf(angle));
	    break;
        }
        iIndicator.apply(t3d);
        iRotate.mul(t3d);
        iRotGroup.setTransform(iRotate);
    }


    protected void handleRotatePanel(Object o) {
        int angle = 0;
        Transform3D t3d = new Transform3D();

        double incrDeg = 30;
        double incr = Math.toRadians(incrDeg);
        if (o == iXUp || o == iXDn) {
            incrDeg = Integer.parseInt(iAngXInc.getText());
            incr = Math.toRadians(incrDeg);
            if (o == iXDn) {
                incr *= -1;
            }
            applyTrans(incr, 'x');
            return;
        }
        if (o == iYUp || o == iYDn) {
            incrDeg = Integer.parseInt(iAngYInc.getText());
            incr = Math.toRadians(incrDeg);
            if (o == iYDn) {
                incr *= -1;
            }
            applyTrans(incr, 'y');
            return;
        }

        if (o == iZUp || o == iZDn) {
            incrDeg = Integer.parseInt(iAngZInc.getText());
            incr = Math.toRadians(incrDeg);
            if (o == iZDn) {
                incr *= -1;
            }
            applyTrans(incr, 'z');
            return;
        }

        if (o == iPIn || o == iPOut) {
            double pos = Double.parseDouble(iPos.getText());
            double posInc = Double.parseDouble(iPosIncr.getText());
            if (o == iPIn) posInc *= -1;
            pos += posInc;
            iPos.setText(fmt1(pos));

            iTranslateGroup.getTransform(t3d);
            Matrix4d m4d = new Matrix4d();
            t3d.get(m4d);
            Matrix4d mincr = new Matrix4d();
            mincr.m23 = posInc;
            m4d.add(mincr);
            // m03 is x
            // m13 is y
            // m23 is z
            t3d.set(m4d);
            iTranslateGroup.setTransform(t3d);
            t3d.set(mincr);
            iUndo.add(new Trans(new Transform3D(), posInc, 'p'));
        }

        if (o == iRestore) {
            iRotate.mulInverse(iRotate);
            iRotGroup.setTransform(iRotate);
            iUndo.clear();
            iIndicator.restore();
            iAngX.setText("0");
            iAngY.setText("0");
            iAngZ.setText("0");

            t3d.set(iMatrix);
            iTranslateGroup.setTransform(t3d);
            iPos.setText(fmt1(iMatrix.m23));
            return;
        }

        if (o == iUndoButton && iUndo.size() > 0) {
            Trans t = (Trans)iUndo.remove(iUndo.size() - 1);
            Transform3D t3 = t.getT3D();
            if (t3 != null) {
                double angInc = Math.toDegrees(t.getAngInc());
                switch(t.getAxis()) {
                case 'x':
                    angle = Integer.parseInt(iAngX.getText());
                    angle -= angInc;
                    iAngX.setText(String.valueOf(angle));
                    handleRotateUndo(t3);
                    break;
                case 'y':
                    angle = Integer.parseInt(iAngY.getText());
                    angle -= angInc;
                    iAngY.setText(String.valueOf(angle));
                    handleRotateUndo(t3);
                    break;
                case 'z':
                    angle = Integer.parseInt(iAngZ.getText());
                    angle -= angInc;
                    iAngZ.setText(String.valueOf(angle));
                    handleRotateUndo(t3);
                    break;
                case 'p':
                    //println("case p code");
                    iTranslateGroup.getTransform(t3d);
                    Matrix4d m4d = new Matrix4d();
                    t3d.get(m4d);
                    m4d.m23 -= t.getAngInc();
                    t3d.set(m4d);
                    iTranslateGroup.setTransform(t3d);
                    double pos = Double.parseDouble(iPos.getText());
                    pos -= t.getAngInc();
                    iPos.setText(fmt1(pos));
                    break;
                }

            }
            iRotGroup.setTransform(iRotate);
        }
    }

    private void handleRotateUndo(Transform3D t3) {
        t3.invert();
        iRotate.mul(t3);
        iIndicator.apply(t3);

    }

    @SuppressWarnings("resource")
	protected void saveRotations() {
        File file = null;
        JFileChooser fileChooser = new JFileChooser(viewConfig.getCurrentRotDir());
        int returnVal = fileChooser.showSaveDialog(iAceTree);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        } else {
            System.out.println("Save command cancelled by user.");
            return;
        }
        viewConfig.setCurrentRotDir(file.getParent());

        PrintWriter pw = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            pw = new PrintWriter(fos, true);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }
        pw.println("<?xml version='1.0' encoding='utf-8'?>");
        pw.println();
        pw.println("<rotations>");
        for (int i=0; i < iUndo.size(); i++) {
            Trans t = (Trans)iUndo.get(i);
            StringBuffer sb = new StringBuffer();
            sb.append("<rotation ");
            sb.append("radians=\"" + t.getAngInc() + "\" ");
            sb.append("axis=\"" + t.getAxis() + "\"/>");
            pw.println(sb.toString());
        }
        pw.println("</rotations>");
    }

    protected void loadRotations() {
        File file = null;
        JFileChooser fileChooser = new JFileChooser(viewConfig.getCurrentRotDir());
        int returnVal = fileChooser.showOpenDialog(iAceTree);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        } else {
            System.out.println("Save command cancelled by user.");
            return;
        }
        viewConfig.setCurrentRotDir(file.getParent());

        try {
            FileReader fr = new FileReader(file);
            QDParser.parse(this, fr);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    // this function is shared between Image3D and PropertiesTab2
    // neesd to be overridden to implement doc handler
    @Override
	public void startElement(String tag, Hashtable h) throws Exception {
        if(tag.equals("rotation")) {
            String incrs = (String)h.get("radians");
            String axiss = (String)h.get("axis");
            double incr = Double.parseDouble(incrs);
            char axis = axiss.charAt(0);
            applyTrans(incr, axis);
        } else if(tag.equals("lineage")) {
            String name = (String)h.get("name");
            String color = (String)h.get("color");
            int lineageCount = viewConfig.getLineageCount();
            viewConfig.getDispProp(lineageCount).setName(name);
            viewConfig.getDispProp(lineageCount).setLineageNum(Image3DViewConfig.getColorIndex(color));
            viewConfig.setLineageCount(lineageCount++);
        }

    }

    // prepares environment to save movie of geometric 3D animation
    // called by actionPerformed when user clicks save movie/stop saving movie button
    // User must wait for generateOverlayInfo() and conversion to Shape3D to finish before pressing "Save Movie"
    //		or else two save windows pop up
    private void saveMovie() {
        // if AceTree had not been in the process of saving a movie, start saving movie now 
        if(!iSaveImage){
            System.out.println("in setting save");
            iSaveImage = true;
            if(!iAceTree.hasActiveImage3D()) {
                System.out.println("no active image3d");
                iAceTree.threeDview();
            } else {
                String name = iAceTree.iImgWin.getTitle();
                name = name.substring(0, name.length() - 8);
                name = name.substring(4, name.length());
                String path = iAceTree.iImgWin.getSaveImageDirectory();
                if (path != null) {
                	path += "/" + name + ".jpeg";
                	System.out.println("Path is not null, rendering off screen at path: "+path);
                	offScreenRendering(path);
                }
                else {
                	iSaveImage = false;
                	return;
                }
                iSaveMovie.setText("Stop Save Movie");
            }
        }else {
        // if AceTree had been in process of saving movie, and the user clicked the button
        // then AceTree is no longer saving, but the button should now read 'Save Movie'
        // so user can start saving new movie
                System.out.println("In stopping save.");
                iSaveImage = false;
                iSaveMovie.setText("Save Movie");
        }
        updateDisplayedTab();
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        Object o = e.getSource();

        //////////////////// FOR COLOR CONTROL TAB ////////////////////  
        if (command.equals("Cancel Changes")) {
            updateDisplayedTab();
        } else if (command.equals("Reset Colors")) {
        	//System.out.println("Reset colors.");
        	SublineageDisplayProperty[] sdp = Image3DViewConfig.getDefaultDispProp();
        	for (int i = 0; i < sdp.length; i++) {
        		//System.out.println(sdp[i].getName()+" "+sdp[i].getLineageNum());
	        	viewConfig.getDispProp(i).setName(sdp[i].getName());
				viewConfig.getDispProp(i).setLineageNum(sdp[i].getLineageNum());
        	}
        	update();
        } else if (command.equals("Apply Changes")) {
            for (int i = 0; i < viewConfig.getNumDispProps(); i++) {
                String name = iSubUI[i].getText();
                // Make name case insensitive
                //String nameLower = name.toLowerCase();
                
                if (!isSulston(name))
                	name = name.toLowerCase();
            	
                //System.out.println(name);
                if (name.length() == 0)
                	name = "";
                int lineageNum = iSubUI[i].getSelectedIndex();
                viewConfig.getDispProp(i).setName(name);
                viewConfig.getDispProp(i).setLineageNum(lineageNum);
            }

            viewConfig.setMinRed(Integer.parseInt(iMinRedField.getText()));
            viewConfig.setMaxRed(Integer.parseInt(iMinRedField.getText()));
            viewConfig.setUseExpression(iUseExprBox.isSelected());
            viewConfig.setUseExpressionColors(iUseExprColors.isSelected());
            viewConfig.setShowNonExpressing(iShowNonExpressingChkBox.isSelected());

            viewConfig.setUseOverlayAutoROI(overlayAutoROI.isSelected());
            viewConfig.setUseOverlayRedChannel(overlayRedChannel.isSelected());
            viewConfig.setUseOverlayGreenChannel(overlayGreenChannel.isSelected());
            viewConfig.setUseOverlayBlueChannel(overlayBlueChannel.isSelected());

            viewConfig.setOverlaySubsample(overlaySubsampleSlider.getValue());

            if(viewConfig.isOverlayXYZChanged()) {
                if(overlayMinXSlider.getValue() <= overlayMaxXSlider.getValue()) {
                    viewConfig.setOverlayMinX( overlayMinXSlider.getValue() );
                    viewConfig.setOverlayMaxX( overlayMaxXSlider.getValue() );
                } else {
                    overlayMinXSlider.setValue(Image3DViewConfig.DEFAULT_OVERLAY_MIN_X);
                    overlayMaxXSlider.setValue(Image3DViewConfig.DEFAULT_OVERLAY_MAX_X);
                }

                if(overlayMinYSlider.getValue() <= overlayMaxXSlider.getValue()) {
                    viewConfig.setOverlayMinY( overlayMinYSlider.getValue() );
                    viewConfig.setOverlayMaxY( overlayMaxYSlider.getValue() );
                } else {
                    overlayMinYSlider.setValue(Image3DViewConfig.DEFAULT_OVERLAY_MIN_Y);
                    overlayMaxYSlider.setValue(Image3DViewConfig.DEFAULT_OVERLAY_MAX_Y);
                }

                if(overlayMinZSlider.getValue() <= overlayMaxXSlider.getValue()) {
                    viewConfig.setOverlayMinZ( overlayMinZSlider.getValue() );
                    viewConfig.setOverlayMaxZ( overlayMaxZSlider.getValue() );
                } else {
                    overlayMinZSlider.setValue(Image3DViewConfig.DEFAULT_OVERLAY_MIN_Z);
                    overlayMaxZSlider.setValue(iNucleiMgr.getPlaneEnd());
                }
            }

            //viewConfig.setTailOpacity(opacitySlider.getValue());
            viewConfig.setTailTimePoints(timePts.getValue());
            //customTailColor.setBackground(viewConfig.getCustomTailColor());
            updateDisplayedTab();

        } else if (command.equals("Load Color Scheme")) {
            System.out.println("Load from file");
            loadFromFile();
        } else if (command.equals("Save Color Scheme")) {
            System.out.println("Save to file");
            saveToFile();
        } 
        // Implement same size spheres check box
        if (o == iSameSizeSpheres)
    		geometryManager.setShowSameSizeSpheres(iSameSizeSpheres.getModel().isSelected());
        if(o == customTailColor) {
            Color c = viewConfig.getCustomTailColor();
            viewConfig.setCustomTailColor( JColorChooser.showDialog(iFrame, 
                "Select a Custom Tail Color", c) );
            //customTailColor.setOpaque(true);
            //customTailColor.setBackground(viewConfig.getCustomTailColor());
        }


        //////////////////// FOR IMAGE TAB ////////////////////  
        if (   o == iXUp || o == iXDn
	       || o == iYUp || o == iYDn
	       || o == iZUp || o == iZDn
	       || o == iPIn || o == iPOut
	       || o == iRestore || o == iUndoButton) {
            handleRotatePanel(o);
            return;
        }
        // if user presses "Save Movie/Stop Saving Movie" on geometric 3D window...
        if(o==iSaveMovie){
            saveMovie();
        }
        if(o==iShowSisters){
        	if(viewConfig.areSistersVisible()){
                    viewConfig.setSistersVisible(false);
                    iShowSisters.setText("Show Sisters");
        	} else {
                    viewConfig.setSistersVisible(true);
                    iShowSisters.setText("Hide Sisters");
        	}
    		updateDisplayedTab();
        }
        if(o == showTails) {
            if(viewConfig.isShowingTails()){
                viewConfig.setShowingTails(false);
                showTails.setText("Show Tails");
            } else {
                viewConfig.setShowingTails(true);
                showTails.setText("Hide Tails");
            }
            updateDisplayedTab();
        }

        if(o == showOverlay) {
            if(viewConfig.isShowingOverlay()) {
                viewConfig.setShowingOverlay(false);
                showOverlay.setText("Show Overlay");
            } else {
                viewConfig.setShowingOverlay(true);
                viewConfig.setOverlayXYZChanged(true);
                showOverlay.setText("Hide Overlay");
            }
            updateDisplayedTab();
        }


        if ( o == iLoadButton) {
            loadRotations();
        } else if (o == iSaveButton) {
            saveRotations();
        } else if (o == iSaveImageButton) {
            saveImageAs();
        }

    }
    
    // Check to see if a String marks the beginning of a sulstonname
    public boolean isSulston(String name) {
    	name = name.toLowerCase();
    	String prefixes[] = {"ab", "p0", "p1", "p2", "p3", "p4", "polar", "p", "ems", "e", "ms", "c", "d", "z1", "z2"};
    	for (int i = 0; i < prefixes.length; i++) {
    		if (name.startsWith(prefixes[i]))
    			return true;
    	}
    	return false;
    }


    public synchronized void insertContent(String title) {
    	//System.out.println("Inserting content with title "+title+"...");
		if (title.equals("null"))
		    title=iTitle;
		
		if (iSaveImage) {
	        iUniverse.getViewer().getView().stopView();
	        String path = iAceTree.iImgWin.getSaveImageDirectory() + "/" + title + "." + IMAGETYPE; 
	        offScreenRendering(path);
	        iUniverse.getViewer().getView().startView();
	    }
		
        while (iSaveInProcess) ;

        iTitle = title;
        //System.out.println("iTitle: "+iTitle);
        viewConfig.setTitle(iTitle);
        iFrame.setTitle(iTitle);
        
        /*
        try {
        	Viewer ver = iUniverse.getViewer();
        } catch (NullPointerException npe) {
        	System.out.println("Universe.getViewer() is null...");
        	npe.printStackTrace();
        }
        try {
        	View v = iUniverse.getViewer().getView();
        } catch (NullPointerException npe) {
        	System.out.println("Universe.getViewer().getView() is null...");
        	npe.printStackTrace();
        }
        */

        // Throw NullPointerException on Mac
        try {
        	iUniverse.getViewer().getView().stopView();
        } catch (NullPointerException npe) {
        	System.out.println("Failed to get SimpleUniverse view.");
        	npe.printStackTrace();
        }

        // build new scene
        iBGT = createSceneGraph();
        if (iBGT == null) {
        	System.out.println("Null branch group returned to Image3D.insertContent().");
            iAceTree.getPlayerControl().stop();
            return;
        }
        
        //Locale l = iUniverse.getLocale();
        //add new scene
        try {
        	iUniverse.addBranchGraph(iBGT);
        } catch (NullPointerException npe) {
        	System.out.println("Failed to add BranchGroup to Universe.");
        	//npe.printStackTrace();
        }
        //detach old scene
        if (iBG2 != null)
        	iBG2.detach();
     
        // Throw NullPointerException on Mac
        try {
        	iUniverse.getViewer().getView().startView();
        } catch (NullPointerException npe) {
        	System.out.println("Failed to get SimpleUniverse view.");
        	npe.printStackTrace();
        }

        // if(iBG2!=null)
        //	l.replaceBranchGraph(iBG2,iBGT);
        //  else
        //	if(iBGT!=null)
        //	    iUniverse.addBranchGraph(iBGT);
        iBG2 = iBGT;
	    
        iPickCanvas = new PickCanvas(iCanvas, iBG2);
        iPickCanvas.setMode(PickTool.BOUNDS);
    }
  

    // For some reason right now the image disappears after save
    public void offScreenRendering(String path) {
    	if (path == null || path.equals(""))
    		return;
    	
    	println("offScreenRendering, ");
    	System.gc();
        int width = 700;
        int height = 500;

        SimpleUniverse.getPreferredConfiguration();

        Canvas3D c = iOfflineCanvas;
        c.getScreen3D().setSize(width,height);
        c.getScreen3D().setPhysicalScreenWidth(0.0254/90.0 * width);
        c.getScreen3D().setPhysicalScreenHeight(0.0254/90.0 * height);

        if (iBG2 != null)
        	iBG2.detach();
    	iBG2 = createSceneGraph();
    	if (iBG2 == null) {
    		//System.out.println("Null branch group returned to insertContent().");
		    iSaveImage = false;
		    iAceTree.getPlayerControl().stop();
		    return;
        }

        SimpleUniverse su = iOfflineUniverse;
        su.addBranchGraph(iBG2);
        su.getViewingPlatform().setNominalViewingTransform();

        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGB, bImage);

        System.out.println("Rendering...");
        try {
	        c.setOffScreenBuffer(buffer);
	        c.renderOffScreenBuffer();
	        c.waitForOffScreenRendering();
	        bImage = c.getOffScreenBuffer().getImage();

	        try {
		        System.out.println("Saving..");
		        //String saveDir = iAceTree.iImgWin.getSaveImageDirectory();
		        DataOutputStream output = new DataOutputStream(new FileOutputStream(path));
	            //saveDir + "/" + title + ".jpg"));
	            ImageIO.write(bImage,"JPEG",output);
	            output.close();
	            
	        } catch (IOException ioe) {
	        	System.out.println("Exception caught in Saving.");
	        	return;
	        }
	        
        } catch (Exception e) {
        	System.out.println("Exception caught in Rendering.");
        	e.printStackTrace();
        	return;
        }
        
        System.out.println("Save success.");
        iSaveInProcess = false;
        System.out.println(path);
    }

    public BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();
        root.setCapability(BranchGroup.ALLOW_DETACH);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
        
        //Color3f bgColor   = new Color3f(0.3f, 0.3f, 0.3f);
        Color3f lColor1   = new Color3f(1f, 1f, 1f);
        Vector3d lPos1 =  new Vector3d(0.0, 0.5, 2.0);
        Vector3f lDirect1 = new Vector3f(lPos1);
        lDirect1.negate();
        Light lgt1 = new DirectionalLight(lColor1, lDirect1);
        lgt1.setInfluencingBounds(bounds);
        root.addChild(lgt1);
        
        // Check for and change background color
        int m = viewConfig.getDispProp(viewConfig.getNumDispProps() - 1).getLineageNum();
        Color3f bgColor = ColorConstants.darkgray;
        switch(m) {
        case 0:
            bgColor = ColorConstants.white;
            break;
        case 1:
            bgColor = ColorConstants.lightgray;
            break;
        default:
            break;
        }
        
        iBackground = new Background(bgColor);
        iBackground.setApplicationBounds(bounds);
        root.addChild(iBackground);

        TransformGroup objRotate = new TransformGroup();
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objRotate.setCapability(Group.ALLOW_CHILDREN_WRITE);
        
        if(viewConfig.isShowingOverlay()) {
            overlayGenerator.interrupt();
        }

        iNucBG = geometryManager.createNuclearBranchGroup(iXA, iYA, iZA);
        if (geometryManager.empty()) {
        	viewConfig.setShowingOverlay(false);
        	
        	// Not sure if this is necessary, but added just in case
        	geometryManager.setOverlay(null);
        	geometryManager.setOverlayReady(false);
        	
        	showOverlay.setText("Show Overlay");
        	return null;
        }

        iNucBG.compile();
        objRotate.addChild(iNucBG);

        TransformGroup initRotGroup = new TransformGroup();
        Transform3D initRotate = new Transform3D();
        NucleiMgr nucMgr = iAceTree.getNucleiMgr();
        int ap = nucMgr.getParameters().apInit;
        int dv = nucMgr.getParameters().dvInit;
        int lr = nucMgr.getParameters().lrInit;

        if (ap == -1) {
            Transform3D apt = new Transform3D();
            apt.rotZ(Math.PI);
            initRotate.mul(apt);
            ap = -ap;
            dv = -dv;
        }
        if (dv == -1) {
            Transform3D dvt = new Transform3D();
            dvt.rotX(Math.PI);
            initRotate.mul(dvt);
            dv = -dv;
            lr = -lr;
        }

        initRotGroup.setTransform(initRotate);
        initRotGroup.addChild(objRotate);

        if (iRotate == null)
        	iRotate = new Transform3D();
        
        iRotGroup = new TransformGroup(iRotate);
        iRotGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        iRotGroup.addChild(initRotGroup);

        root.addChild(iRotGroup);

        MouseRotate myMouseRotate = new MouseRotate();
        myMouseRotate.setTransformGroup(objRotate);
        myMouseRotate.setSchedulingBounds(new BoundingSphere());
        root.addChild(myMouseRotate);
               
        if (root == null)
        	System.out.println("Null branch group...");
        
        try {
        	root.compile();
        } catch (Exception e) {
        	System.out.println("Problem in compiling branch group for scene graph.");
        	return null;
        }
        
        System.out.println("Finished createSceneGraph().");
        return root;
    }

    @Override
	public void mouseClicked(MouseEvent e) {
        iPickCanvas.setShapeLocation(e);
        PickResult [] results = iPickCanvas.pickAll();
        String name = getPickedNucleusNames(results);
        //System.out.println("You picked: " + name);
        iPick.setText("You picked: " + name);
    }

    private String getPickedNucleusNames(PickResult [] results) {
        String s = "none";
        Vector v = new Vector();
        //v.add(0, s);
        if (results != null) {
            for (int i= (results.length - 1); i >= 0; i--) {
                Primitive p = (Primitive)results[i].getNode(PickResult.PRIMITIVE);

                if (p != null) {
                    String pname = p.getClass().getName();
                    if (pname.indexOf("NamedSphere") >= 0) {
                        s = ((NamedSphere)p).getName();
                        v.add(0, s);
                    }
                }
            }
        }
        if (v.size() == 0) return "none";
        Enumeration e = v.elements();
        s = "";
        while (e.hasMoreElements()) {
            if (s.length() > 0) s += CS;
            s += (String)e.nextElement();
        }
        return s;
    }

    @Override
	public void run() {
    	println("Image3D.run, ");
    	new Exception().printStackTrace();
        iSaveInProcess = true;
        int k = 1000;
        if (iNewConstruction) {
            k = 5000; // long delay needed on new open
            iNewConstruction = false;
        }
        while (iSaveInProcess) {
	    try {
		Thread.sleep(k);
	    } catch(InterruptedException ie) {
		ie.printStackTrace();
	    }
        }
        //saveImage();
        //offScreenRendering();
    }

    // called by AceTree image3DSave()
    // argument is TRUE if AceTree should START saving movie/is in process of saving movie
    // argument is FALSE if AceTree should STOP saving movie/is no longer in process of saving movie
    public static void setSaveImageState(boolean saveIt) {
        iSaveImage = saveIt;
        println("Image3D.setSaveImageState, ");
    }

    // called when a user wants to save a still image
    public void saveImageAs() {
    	println("Image3D.saveImageAs, ");

        // open up a file chooser so that user can pick directory to save in
        // and name of file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setPreferredSize(new Dimension(600, 400));
        fileChooser.setCurrentDirectory(new File(iSaveImageAsDir));
        fileChooser.setSelectedFile(new File(iLastSaveAsName));

        String path = "";

        int returnVal = fileChooser.showSaveDialog(iAceTree);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            path = file.getPath();
            iSaveImageAsDir = file.getParent();
            iLastSaveAsName = file.getName() + "x";
            //takeScreenshot(path);
            String extension = "." + IMAGETYPE;
            if(!path.substring(path.length() - extension.length()).toLowerCase().equals(extension))
                path += ".jpeg";
            offScreenRendering(path);
        } else {
            System.out.println("Save command cancelled by user.");
        }
        updateDisplayedTab();
    } 

    // take a screenshot and saves it to the given path
    // the screenshot is of the entire geometric 3D window
    // v. offScreenRendering will make a jpeg of the animation only
    private void takeScreenshot(String path) {
        Rectangle screenRect = iFrame.getBounds();

        int topAdjust = 23;
        screenRect.y += topAdjust;
        screenRect.height -= topAdjust;

        Robot robot = null;
        try {
            robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRect);
            ImageIO.write(image, IMAGETYPE, new File(path + "." + IMAGETYPE));
        } catch(Exception e) {
            e.printStackTrace();
        }
        iSaveInProcess = false;

        System.out.println("file: " + path + " written");
    }
    public static void main(String[] args) {
    }

    private static void println(String s) {System.out.println(s);}
    private static final DecimalFormat DF1 = new DecimalFormat("####.##");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    protected static String fmt1(double x) {return DF1.format(x);}

    @Override
	public void startDocument() throws Exception {
    }

    @Override
	public void endDocument() throws Exception {
    }

    @Override
	public void text(String str) throws Exception {
    }

    @Override
	public void endElement(String tag) throws Exception {
    }

    private void buildColorControlGUI() {
        //println("PropertiesTab2, " + iDispProps2);
        Border blackline = BorderFactory.createLineBorder(Color.black);

        iColorControlPanel = new JPanel();
        iColorControlPanel.setBorder(blackline);
        iColorControlPanel.setLayout(new BoxLayout(iColorControlPanel, BoxLayout.LINE_AXIS));

        ////////////////////////////////////////////////////////////
        // Start of Lineage Color Controls 
        ////////////////////////////////////////////////////////////
        JPanel lineagePanel = new JPanel();
        lineagePanel.setLayout(new GridLayout(0,1));
        lineagePanel.setBorder(blackline);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1,2));
        JLabel sublineage = new JLabel("sublineage");
        JLabel color = new JLabel("color");
        labelPanel.add(sublineage);
        labelPanel.add(color);

        lineagePanel.add( new JLabel("Lineage Color Controls"));
        lineagePanel.add( new JLabel(" "));
        lineagePanel.add(labelPanel);

        new JPanel();

        JPanel topPart = new JPanel();
        topPart.add(lineagePanel);
        
        iSubUI = new SublineageUI[viewConfig.getNumDispProps()];
        
        for(int i = 0; i < viewConfig.getNumDispProps(); i++) {
            SublineageDisplayProperty tempDispProp = viewConfig.getDispProp(i);
            if (i == viewConfig.getNumDispProps()-2) {
            	iSubUI[i] = new SublineageUI(tempDispProp, 2);
            }
            else if (i == viewConfig.getNumDispProps()-1) {
            	iSubUI[i] = new SublineageUI(tempDispProp, 1);
            }
            else {
            	iSubUI[i] = new SublineageUI(tempDispProp, 0);
            }
            lineagePanel.add(iSubUI[i].getPanel());
        }

        lineagePanel.setMaximumSize(new Dimension(200, 200));
        iColorControlPanel.add(topPart);
        ////////////////////////////////////////////////////////////
        // End of Lineage Color Controls  
        ////////////////////////////////////////////////////////////

        JPanel botPart = new JPanel();
        botPart.setLayout(new BoxLayout(botPart,BoxLayout.Y_AXIS));
        iColorControlPanel.add(botPart);
        
		////////////////////////////////////////////////////////////
		// Start of Sphere Size Option
		////////////////////////////////////////////////////////////
        
		JPanel sizePanel = new JPanel();
		sizePanel.setAlignmentX(Component.CENTER_ALIGNMENT);	
		sizePanel.setLayout(new BoxLayout(sizePanel,BoxLayout.Y_AXIS));
		sizePanel.setBorder(blackline);
		JLabel sizeLabel = new JLabel("Adjust Sphere Size");
		sizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		sizePanel.add(sizeLabel);
		
		// Sphere size tick box
        iSameSizeSpheres = new JCheckBox("Same Size Spheres", false);
        iSameSizeSpheres.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Size scale slider
        iSphereScale = new JSlider(10, 100);
        iSphereScale.setValue(55);
        geometryManager.setSphereScale(iSphereScale.getValue()/100.);
        scaleSizeSlider(iSphereScale);
        
        sizePanel.add(iSameSizeSpheres);
        sizePanel.add(iSphereScale);
        iSameSizeSpheres.addActionListener(this);
        iSphereScale.addChangeListener(this);
        
		////////////////////////////////////////////////////////////
		// End of Sphere Size Option
		////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////
        // Start of Tracking Options 
        ////////////////////////////////////////////////////////////

        trackingPanel = new JPanel();
        trackingPanel.setAlignmentX(Component.CENTER_ALIGNMENT);	
        trackingPanel.setLayout(new BoxLayout(trackingPanel,BoxLayout.Y_AXIS));
        trackingPanel.setBorder(blackline);

        // slider for N time points
        JPanel timePtSliderPanel = new JPanel();
        timePtSliderPanel.setLayout(new GridLayout(2,1));
        JLabel timePtsLabel = new JLabel("Number of Time Points Tracked", SwingConstants.CENTER);
        timePtsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timePts = new JSlider(SwingConstants.HORIZONTAL, Image3DViewConfig.MIN_TAIL_TIME_PTS, 
                Image3DViewConfig.MAX_TAIL_TIME_PTS, Image3DViewConfig.INIT_TAIL_TIME_PTS);
        timePts.addChangeListener(this);
        timePts.setMajorTickSpacing(5);
        timePts.setMinorTickSpacing(1);
        timePts.setPaintTicks(true);
        timePts.setPaintLabels(true);
        timePts.setSnapToTicks(true);

        trackingPanel.add(timePtsLabel);
        trackingPanel.add(timePts);
        //timePtSliderPanel.add(timePtsLabel);
        //timePtSliderPanel.add(timePts);

        /*
        // radio buttons for using lineage/custom colors
        JPanel trackingColorPanel = new JPanel();
        JRadioButton useLineageColors = new JRadioButton("Use Lineage Colors");
        JRadioButton useCustomColors = new JRadioButton("Use Custom Colors");

        useLineageColors.addActionListener(this);
        useCustomColors.addActionListener(this);

        ButtonGroup group = new ButtonGroup();
        group.add(useLineageColors);
        group.add(useCustomColors);

        useLineageColors.setSelected(true);

        trackingColorPanel.add(useLineageColors);
        trackingColorPanel.add(useCustomColors);
        */

        customTailColor = new JButton();
        customTailColor.setAlignmentX(Component.CENTER_ALIGNMENT);
        customTailColor.setText("Track Color");
        //customTailColor.setBackground(viewConfig.DEFAULT_CUSTOM_TAIL_COLOR);
        //customTailColor.setEnabled(false);
        customTailColor.setToolTipText("Click here to change color");
        customTailColor.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                viewConfig.setCustomTailColor( JColorChooser.showDialog(iFrame, 
                    "Select a Custom Tail Color", Image3DViewConfig.DEFAULT_CUSTOM_TAIL_COLOR) );
            }
        });

        /*
        // slider for opacity
        JPanel opacityPanel = new JPanel();
        JLabel opacityLabel = new JLabel("Tail Opacity", JLabel.CENTER);
        opacityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        opacitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50); //min, max, default
        
        opacitySlider.addChangeListener(this);
        opacitySlider.setMajorTickSpacing(20);
        opacitySlider.setMinorTickSpacing(5);
        opacitySlider.setPaintTicks(true);
        opacitySlider.setPaintLabels(true);
        opacitySlider.setSnapToTicks(true);

        opacityPanel.add(opacityLabel);
        opacityPanel.add(opacitySlider);
        */

        // add subpanels to tracking panel
        //trackingPanel.add(timePtSliderPanel);
        trackingPanel.add(customTailColor);
        //trackingPanel.add(trackingColorPanel);
        //trackingPanel.add(opacityPanel);

        ////////////////////////////////////////////////////////////
        // End of Tracking Options 
        ////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////
        // Start of Right Side Buttons 
        ////////////////////////////////////////////////////////////
       
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel,BoxLayout.Y_AXIS));
        filePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        //filePanel.setLayout(new GridLayout(1,2));

        JButton load = new JButton("Load Color Scheme");
        JButton save = new JButton("Save Color Scheme");
        JButton reset = new JButton("Reset Colors");
        JButton apply = new JButton("Apply Changes");
        //showTails = new JCheckBox("Show Tails", false);
        // JButton cancel = new JButton("Cancel Changes");

        
        load.setAlignmentX(Component.CENTER_ALIGNMENT);
        save.setAlignmentX(Component.CENTER_ALIGNMENT);	
        reset.setAlignmentX(Component.CENTER_ALIGNMENT);	
        apply.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
        //showTails.setAlignmentX(Component.CENTER_ALIGNMENT);

        load.addActionListener(this);
        save.addActionListener(this);
        reset.addActionListener(this);
        apply.addActionListener(this);
        
        /*
        showTails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableTailComponents(trackingPanel, showTails.isSelected());
            }
        }); 
        */

        botPart.add(new JLabel(" "));
        filePanel.add(load);
        filePanel.add(save);
        filePanel.add(reset);
        filePanel.add(apply);
        //filePanel.add(showTails);
        //filePanel.add(cancel);
        botPart.add(filePanel);
        botPart.add(Box.createVerticalGlue());
        botPart.add(sizePanel);
        botPart.add(Box.createVerticalGlue());
        botPart.add(trackingPanel);
        botPart.add(Box.createVerticalGlue());


        ////////////////////////////////////////////////////////////
        // End of Right Side Buttons
        ////////////////////////////////////////////////////////////
        
        // add trackign panel to botpanel
        ////////////////////////////////////////////////////////////
        // Start of Right Side Overlay Controls
        ////////////////////////////////////////////////////////////
        
        JPanel outerOverlayPanel = new JPanel();
        outerOverlayPanel.setLayout(new BoxLayout(outerOverlayPanel, BoxLayout.Y_AXIS));
            outerOverlayPanel.setBorder(blackline);

        JLabel overlayLabel = new JLabel("Overlay Options", SwingConstants.CENTER);
        JPanel overlayPanel = new JPanel();
        overlayPanel.setAlignmentX(Component.CENTER_ALIGNMENT);	
        //overlayPanel.setLayout(new BoxLayout(overlayPanel,BoxLayout.PAGE_AXIS));
        overlayPanel.setLayout(new GridLayout(1,2));


        JLabel overlayMinXLabel = new JLabel("Overlay Min X", SwingConstants.CENTER);
        overlayMinXLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        overlayMinXSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 512, Image3DViewConfig.DEFAULT_OVERLAY_MIN_X);
        overlayMinXSlider.addChangeListener(this);
        overlayMinXSlider.setMajorTickSpacing(128);
        overlayMinXSlider.setMinorTickSpacing(32);
        overlayMinXSlider.setPaintTicks(true);
        overlayMinXSlider.setPaintLabels(true);
        overlayMinXSlider.setSnapToTicks(true);
        JPanel overlayMinXPanel = new JPanel();
        overlayMinXPanel.setLayout(new BoxLayout(overlayMinXPanel, BoxLayout.Y_AXIS));
        overlayMinXPanel.add(overlayMinXLabel);
        overlayMinXPanel.add(overlayMinXSlider);

        JLabel overlayMaxXLabel = new JLabel("Overlay Max X", SwingConstants.CENTER);
        overlayMaxXLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        overlayMaxXSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 512, Image3DViewConfig.DEFAULT_OVERLAY_MAX_X);
        overlayMaxXSlider.addChangeListener(this);
        overlayMaxXSlider.setMajorTickSpacing(128);
        overlayMaxXSlider.setMinorTickSpacing(32);
        overlayMaxXSlider.setPaintTicks(true);
        overlayMaxXSlider.setPaintLabels(true);
        overlayMaxXSlider.setSnapToTicks(true);
        JPanel overlayMaxXPanel = new JPanel();
        overlayMaxXPanel.setLayout(new BoxLayout(overlayMaxXPanel, BoxLayout.Y_AXIS));
        overlayMaxXPanel.add(overlayMaxXLabel);
        overlayMaxXPanel.add(overlayMaxXSlider);

        JLabel overlayMinYLabel = new JLabel("Overlay Min Y", SwingConstants.CENTER);
        overlayMinYLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        overlayMinYSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 512, Image3DViewConfig.DEFAULT_OVERLAY_MIN_Y);
        overlayMinYSlider.addChangeListener(this);
        overlayMinYSlider.setMajorTickSpacing(128);
        overlayMinYSlider.setMinorTickSpacing(32);
        overlayMinYSlider.setPaintTicks(true);
        overlayMinYSlider.setPaintLabels(true);
        overlayMinYSlider.setSnapToTicks(true);
        JPanel overlayMinYPanel = new JPanel();
        overlayMinYPanel.setLayout(new BoxLayout(overlayMinYPanel, BoxLayout.Y_AXIS));
        overlayMinYPanel.add(overlayMinYLabel);
        overlayMinYPanel.add(overlayMinYSlider);

        JLabel overlayMaxYLabel = new JLabel("Overlay Max Y", SwingConstants.CENTER);
        overlayMaxYLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        overlayMaxYSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 512, Image3DViewConfig.DEFAULT_OVERLAY_MAX_Y);
        overlayMaxYSlider.addChangeListener(this);
        overlayMaxYSlider.setMajorTickSpacing(128);
        overlayMaxYSlider.setMinorTickSpacing(32);
        overlayMaxYSlider.setPaintTicks(true);
        overlayMaxYSlider.setPaintLabels(true);
        overlayMaxYSlider.setSnapToTicks(true);
        JPanel overlayMaxYPanel = new JPanel();
        overlayMaxYPanel.setLayout(new BoxLayout(overlayMaxYPanel, BoxLayout.Y_AXIS));
        overlayMaxYPanel.add(overlayMaxYLabel);
        overlayMaxYPanel.add(overlayMaxYSlider);

        JLabel overlayMinZLabel = new JLabel("Overlay Min Z", SwingConstants.CENTER);
        overlayMinZLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        overlayMinZSlider = new JSlider(SwingConstants.HORIZONTAL, 0, iNucleiMgr.getPlaneEnd(), Image3DViewConfig.DEFAULT_OVERLAY_MIN_Z);
        overlayMinZSlider.addChangeListener(this);
        overlayMinZSlider.setMajorTickSpacing(10);
        overlayMinZSlider.setMinorTickSpacing(2);
        overlayMinZSlider.setPaintTicks(true);
        overlayMinZSlider.setPaintLabels(true);
        overlayMinZSlider.setSnapToTicks(true);
        JPanel overlayMinZPanel = new JPanel();
        overlayMinZPanel.setLayout(new BoxLayout(overlayMinZPanel, BoxLayout.Y_AXIS));
        overlayMinZPanel.add(overlayMinZLabel);
        overlayMinZPanel.add(overlayMinZSlider);

        JLabel overlayMaxZLabel = new JLabel("Overlay Max Z", SwingConstants.CENTER);
        overlayMaxZLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        overlayMaxZSlider = new JSlider(SwingConstants.HORIZONTAL, 0, iNucleiMgr.getPlaneEnd(), iNucleiMgr.getPlaneEnd());
        overlayMaxZSlider.addChangeListener(this);
        overlayMaxZSlider.setMajorTickSpacing(10);
        overlayMaxZSlider.setMinorTickSpacing(2);
        overlayMaxZSlider.setPaintTicks(true);
        overlayMaxZSlider.setPaintLabels(true);
        overlayMaxZSlider.setSnapToTicks(true);
        JPanel overlayMaxZPanel = new JPanel();
        overlayMaxZPanel.setLayout(new BoxLayout(overlayMaxZPanel, BoxLayout.Y_AXIS));
        overlayMaxZPanel.add(overlayMaxZLabel);
        overlayMaxZPanel.add(overlayMaxZSlider);

        JPanel overlayMinPanel = new JPanel();
        overlayMinPanel.setLayout(new BoxLayout(overlayMinPanel, BoxLayout.Y_AXIS));
        overlayMinPanel.add(overlayMinXPanel);
        overlayMinPanel.add(overlayMinYPanel);
        overlayMinPanel.add(overlayMinZPanel);

        JPanel overlayMaxPanel = new JPanel();
        overlayMaxPanel.setLayout(new BoxLayout(overlayMaxPanel, BoxLayout.Y_AXIS));
        overlayMaxPanel.add(overlayMaxXPanel);
        overlayMaxPanel.add(overlayMaxYPanel);
        overlayMaxPanel.add(overlayMaxZPanel);

        //overlayPanel.add(overlayLabel);
        overlayPanel.add(overlayMinPanel);
        //overlayPanel.add(Box.createRigidArea(new Dimension(5,0)));
        overlayPanel.add(overlayMaxPanel);

        overlayAutoROI = new JCheckBox("Auto Overlay ROI", viewConfig.useOverlayAutoROI());
        overlayAutoROI.addItemListener(this);

        overlayRedChannel = new JCheckBox("Show Red Channel", viewConfig.useOverlayRedChannel());
        overlayRedChannel.addItemListener(this);
        overlayGreenChannel = new JCheckBox("Show Green Channel", viewConfig.useOverlayGreenChannel());
        overlayGreenChannel.addItemListener(this);
        overlayBlueChannel = new JCheckBox("Show Blue Channel", viewConfig.useOverlayBlueChannel());
        overlayBlueChannel.addItemListener(this);

        JPanel overlaySubsamplePanel = new JPanel();
        overlaySubsamplePanel.setLayout(new BoxLayout(overlaySubsamplePanel, BoxLayout.Y_AXIS));
        JLabel overlaySubsampleLabel = new JLabel("Subsample size: ", SwingConstants.CENTER);
        overlaySubsampleSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 9, Image3DViewConfig.DEFAULT_OVERLAY_SUBSAMPLE);
        overlaySubsampleSlider.addChangeListener(this);
        overlaySubsampleSlider.setMajorTickSpacing(2);
        overlaySubsampleSlider.setMinorTickSpacing(1);
        overlaySubsampleSlider.setPaintTicks(true);
        overlaySubsampleSlider.setPaintLabels(true);
        overlaySubsampleSlider.setSnapToTicks(true);
        overlaySubsamplePanel.add(overlaySubsampleLabel);
        overlaySubsamplePanel.add(overlaySubsampleSlider);

        outerOverlayPanel.add(overlayLabel);
        outerOverlayPanel.add(overlayPanel);
        outerOverlayPanel.add(overlaySubsamplePanel);
        outerOverlayPanel.add(overlayAutoROI);
        outerOverlayPanel.add(overlayRedChannel);
        outerOverlayPanel.add(overlayGreenChannel);
        //outerOverlayPanel.add(overlayBlueChannel);


        ////////////////////////////////////////////////////////////
        // End of Right Side Overlay Controls
        ////////////////////////////////////////////////////////////

        botPart.add(outerOverlayPanel);
        //botPart.add(new JLabel(" "));
        //botPart.add(Box.createVerticalGlue());

        ////////////////////////////////////////////////////////////
        // Start of Right Side Expression Controls
        ////////////////////////////////////////////////////////////

        JPanel jp = new JPanel();
        jp.setAlignmentX(Component.CENTER_ALIGNMENT);	
        jp.setLayout(new BoxLayout(jp,BoxLayout.Y_AXIS));
        jp.setBorder(blackline);

        iMinRedField = new JTextField(String.valueOf(viewConfig.getMinRed()), 7);
        iMaxRedField = new JTextField(String.valueOf(viewConfig.getMaxRed()), 7);
        iMinRedField.setAlignmentX(Component.CENTER_ALIGNMENT);
        iMaxRedField.setAlignmentX(Component.CENTER_ALIGNMENT);

        iMaxRedField.setMaximumSize(new Dimension(150,20));
        iMinRedField.setMaximumSize(new Dimension(150,20));

        iUseExprBox = new JCheckBox("Use Expression", viewConfig.isUsingExpression());
        iUseExprBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        jp.add(new JLabel("Expression Color Controls"));
        jp.add(new JLabel(" "));
        jp.add((new JSeparator(SwingConstants.HORIZONTAL))); 

        jp.add(iUseExprBox);

        jp.add(new JLabel(" "));
        jp.add((new JSeparator(SwingConstants.HORIZONTAL))); 
        jp.add(new JLabel("Expression Range:"));
        jp.add(new JLabel(" "));
        jp.add(new JLabel("Min Expression (Black)"));
        jp.add(iMinRedField);
        jp.add(new JLabel("Max Expression (Red)"));
        jp.add(iMaxRedField);

        jp.add(new JLabel(" ")); 
        jp.add((new JSeparator(SwingConstants.HORIZONTAL))); 

        iUseExprColors = new JRadioButton("Expression Level");
        iUseExprColors.setSelected(true);

        JRadioButton lineage = new JRadioButton("Lineage Identity");

        ButtonGroup bg = new ButtonGroup();
        bg.add(iUseExprColors);
        bg.add(lineage);

        jp.add(new JLabel("Color Expressing Cells Via: "));
        jp.add(iUseExprColors);
        jp.add(lineage);

        jp.add(new JLabel(" ")); 
        jp.add((new JSeparator(SwingConstants.HORIZONTAL))); 

        iShowNonExpressingChkBox = new JCheckBox("Show non-expressing", 
                viewConfig.isShowingNonExpressing());

        jp.add(iShowNonExpressingChkBox);

        ////////////////////////////////////////////////////////////
        // End of Right Side Expression Controls
        ////////////////////////////////////////////////////////////
      
        //jp.setBorder(blackline);
       
        botPart.add(Box.createVerticalGlue());
        //JPanel dummy2=new JPanel();
        //dummy2.setPreferredSize(new Dimension(400,300));
        //botPart.add(dummy2); 
  
        botPart.add(jp);
    }

    // Saves file automatically with .xml extension
    @SuppressWarnings("resource")
	private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser(viewConfig.getCurrentRotDir());
        int returnVal = fileChooser.showSaveDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) return;
        
        File file = new File(fileChooser.getSelectedFile()+"");
        String path = file.getPath();
        if (path.lastIndexOf(".") != -1) {
            String extension = path.substring(path.lastIndexOf("."),path.length());
            if (!extension.equals(".xml"))
            	file = new File(fileChooser.getSelectedFile()+".xml");
        }
        else {
        	file = new File(fileChooser.getSelectedFile()+".xml");
        }
        path = file.getPath();
        
        viewConfig.setCurrentRotDir(file.getParent());
        System.out.println("saveToFile: " + file);
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(path), true);
            pw.println("<?xml version='1.0' encoding='utf-8'?>");
            pw.println();
            pw.println("<lineages>");
            for (int i=0; i < viewConfig.getNumDispProps()-2; i++) {
                //pw.println(iDispProps2[i].iName + CS + COLORS[iDispProps2[i].iLineageNum]);
                StringBuffer sb = new StringBuffer();
                sb.append("<lineage ");
                sb.append("name=\"" + viewConfig.getDispProp(i).getName() + "\" ");
                sb.append("color=\"" + viewConfig.getDispProp(i).getColor() + "\"/>");
                pw.println(sb.toString());
            }
            pw.println("</lineages>");

        } catch(IOException ioe) {
            ioe.printStackTrace();
            return;
        }
    }

    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser(viewConfig.getCurrentRotDir());
        int returnVal = fileChooser.showOpenDialog(null);

        if (returnVal != JFileChooser.APPROVE_OPTION) return;
        File file = fileChooser.getSelectedFile();
        viewConfig.setCurrentRotDir(file.getParent());
        viewConfig.setLineageCount(0);

        try {
        	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        	Document doc = dBuilder.parse(file);
        	NodeList nodeList = doc.getElementsByTagName("lineage");
        	
        	for (int i = 0; i < nodeList.getLength(); i++) {
        		if (i < viewConfig.getNumDispProps()-2) {
        			Node node = nodeList.item(i);
        			Element nodeElem = (Element)node;
        			String name = nodeElem.getAttribute("name");
        			// Older save files have empty name fields saved as "-"
        			// Insert "" into name field instead
        			if (!name.equals("-"))
        				viewConfig.getDispProp(i).setName(name);
    				viewConfig.getDispProp(i).setLineageNum(Image3DViewConfig.getColorIndex(nodeElem.getAttribute("color")));
        		}
        	}
        	
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        update();
    }

    public void scaleSizeSlider(JSlider js) {
    	Dimension d = js.getPreferredSize();
		js.setPreferredSize(new Dimension(d.width+20, d.height+40));
		js.setMinorTickSpacing(9);
	    js.setPaintTicks(true);
	    js.setSnapToTicks(false);
    	
    	Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
	    table.put(new Integer(10), new JLabel("Small"));
	    table.put(new Integer(55), new JLabel("Medium"));
	    table.put(new Integer(100), new JLabel("Large"));
	    js.setLabelTable(table);
	    js.setPaintLabels(true);
    }
    
    public void update() {
        for (int i=0; i < viewConfig.getNumDispProps()-2; i++) {
            iSubUI[i].setText(viewConfig.getDispProp(i).getName());
            iSubUI[i].setSelectedIndex(viewConfig.getDispProp(i).getLineageNum());
        }
    }

    public void enableTailComponents(Container container, boolean enable) {
       Component[] components = container.getComponents();
       for(Component component : components) {
         component.setEnabled(enable);
         if(component instanceof Container) {
             enableTailComponents( (Container) component, enable);
         }
       }
    }

    @Override
	public void stateChanged(ChangeEvent e) {
        JSlider o = (JSlider) e.getSource();
        if(   o == overlayMinXSlider || o == overlayMinYSlider || o == overlayMinZSlider 
           || o == overlayMaxXSlider || o == overlayMaxYSlider || o == overlayMaxZSlider
           || o == overlaySubsampleSlider )
           viewConfig.setOverlayXYZChanged(true); 
        else if (o == iSphereScale) 
        	geometryManager.setSphereScale(iSphereScale.getValue()/100.);
    }

    @Override
	public void itemStateChanged(ItemEvent e) {
        Object o = e.getItemSelectable();
        if(o == overlayAutoROI) {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                viewConfig.setUseOverlayAutoROI(overlayAutoROI.isSelected());
            } else {
                viewConfig.resetOverlayDefaults();
                
            }
        } 

        if(o == overlayRedChannel || o == overlayGreenChannel || o == overlayBlueChannel)
            viewConfig.setChangeOverlayChannel(true);
    }
    
    public JFrame getImage3DFrame() {
    	return iFrame;
    }

    public Image3DGeometryManager getGeoManager() { return this.geometryManager; }
    public Image3DViewConfig getViewConfig() { return this.viewConfig; }

    public class WinEventMgr extends WindowAdapter {
        // Closes AceTree's Geometric 3D window.
        @Override
		public void windowClosing(WindowEvent e) {
        	geometryManager.setOverlayReady(false);
    		geometryManager.setOverlay(null);
    		viewConfig.setShowingOverlay(false);
            //System.out.println("Image3D windowClosing: ");
            iFrame.setVisible(false);
            //iAceTree.image3DOff();
        }
    }
}
