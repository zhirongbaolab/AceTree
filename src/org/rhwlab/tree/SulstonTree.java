package org.rhwlab.tree;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JCheckBox;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.C;
/*
 * Created on Apr 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SulstonTree extends JFrame implements ActionListener, WindowFocusListener {
    String              title;
    TreeCanvas          tc;
    int                 width;
    int                 height;
    public AceTree      iAceTree;
    private CanonicalTree   iCanonicalTree;
    private JToolBar    iToolBar;
    private JTextField  iRootCell;
    private JTextField  iLateTimeField;
    private JTextField  iMinRedField;
    private JTextField  iMaxRedField;
    private JButton     iRefresh;
    private Cell        iCurrentCell;
    private TreeCanvas  iTreeCanvas;
    private TreePanel   iTreePanel;
    private TreePanel   tp;
    private boolean     iCanInterrogate;
    
    private JCheckBox	iBookmarked;
    private DefaultListModel iBookmarkListModel;
    //true if this is an ancestral tree
    //false if it is a sulston (canonical) tree

    public SulstonTree(Object tree, String title, Cell c, boolean canInterrogate, ListModel bookmarkListModel) {
        super(title);
        //System.out.println("SulstonTree constructor");
        this.title = title;
        width = WIDTH;
        height = HEIGHT;
        setResizable(true);
        if (canInterrogate)iAceTree = (AceTree)tree;
        else iCanonicalTree = (CanonicalTree)tree;
        iCurrentCell = c;
        iCanInterrogate = canInterrogate;
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,10));
        JPanel jp = new JPanel(new FlowLayout());
        JLabel rootCell = new JLabel("root");
        jp.add(rootCell);
        iRootCell = new JTextField(c.getName(), 6);
        jp.add(iRootCell);
        iToolBar.add(jp);


        jp = new JPanel(new FlowLayout());
        JLabel endTime = new JLabel("End time");
        jp.add(endTime);
        //iToolBar.add(endTime);
        int endTimeData = iCurrentCell.getEndTime();
        iLateTimeField = new JTextField(String.valueOf(endTimeData), 6);
        jp.add(iLateTimeField);
        //iToolBar.add(iLateTimeField);
        iToolBar.add(jp);

        jp = new JPanel(new FlowLayout());
        iMinRedField = new JTextField("-500", 6);
        iMaxRedField = new JTextField("5000", 6);
        jp.add(new JLabel("minRed"));
        jp.add(iMinRedField);
        iToolBar.add(jp);
        jp = new JPanel(new FlowLayout());
        jp.add(new JLabel("maxRed"));
        jp.add(iMaxRedField);
        iToolBar.add(jp);

        // Add checkbox for bookmarked cells
        jp = new JPanel(new FlowLayout());
        // Make checkbox unchecked by default
        iBookmarked = new JCheckBox("Show Bookmarked", false);
        jp.add(iBookmarked);
        iToolBar.add(jp);

        jp = new JPanel(new FlowLayout());
        iRefresh = new JButton("Refresh");
        iRefresh.addActionListener(this);
        //iToolBar.add(iRefresh);
        jp.add(iRefresh);
        JButton jb1 = new JButton("Print");
        jb1.addActionListener(this);
        //iToolBar.add(jb1);
        jp.add(jb1);
        iToolBar.add(jp);

        getContentPane().add(iToolBar, "North");
        //ScrollPane sp = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
        //ScrollPane sp = new ScrollPane();
        //getContentPane().add(sp, "Center");
        iCurrentCell.setLateTime(Integer.parseInt(iLateTimeField.getText()));
        //sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


        //tc = new TreeCanvas(c, this, canInterrogate);
        //sp.add(tc);
        ////tc.setSize(600, 600);
        //iTreeCanvas = tc;

        tp = new TreePanel(c, this, canInterrogate);
        JScrollPane sp = new JScrollPane(tp,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        getContentPane().add(sp, "Center");
        //sp.add(tp);
        //tp.setSize(600, 600);
        iTreePanel = tp;

        refreshTree();
        //iTreeCanvas.setLateTime(Integer.parseInt(iLateTimeField.getText()));
        pack();
        setVisible(true);
        addWindowFocusListener(this);
        
        // Add ListModel reference to bookmarked cells
        setBookmarkList(bookmarkListModel);
    }
    
    @Override
	public Dimension getPreferredSize(){ 
    	return new Dimension(width, height); 
	}

    @Override
	public Dimension getMinimumSize(){ 
    	return new Dimension(MINWIDTH, MINHEIGHT); 
	}



    private void refreshTree(boolean b) {
        if (iCanInterrogate) {
            iCurrentCell = iAceTree.getCellByName(iRootCell.getText());
            iTreeCanvas.setCell(iCurrentCell);
        }
        else {
            iCurrentCell = (Cell)((iCanonicalTree.getCellsHash()).get(iRootCell.getText()));
            iTreeCanvas.setCell(iCurrentCell);
        }
        iTreeCanvas.setLateTime(Integer.parseInt(iLateTimeField.getText()));
        iTreeCanvas.setMinRed(Integer.parseInt(iMinRedField.getText()));
        iTreeCanvas.setMaxRed(Integer.parseInt(iMaxRedField.getText()));
        iTreeCanvas.repaint();
    }

    private void refreshTree() {
        if (iCanInterrogate) {
            iCurrentCell = iAceTree.getCellByName(iRootCell.getText());
            iTreePanel.setCell(iCurrentCell);
        } else {
            iCurrentCell = (Cell)((iCanonicalTree.getCellsHash()).get(iRootCell.getText()));
            iTreePanel.setCell(iCurrentCell);
        }
        iTreePanel.setLateTime(Integer.parseInt(iLateTimeField.getText()));
        iTreePanel.setMinRed(Integer.parseInt(iMinRedField.getText()));
        iTreePanel.setMaxRed(Integer.parseInt(iMaxRedField.getText()));
        
        // Handle color bookmarked cells checkbox
        if (iBookmarked.isSelected() && iBookmarkListModel != null) {
        	//System.out.println("Setting tree panel bookmark list...");
        	iTreePanel.setBookmarkListModel(iBookmarkListModel);
        }
        // If bookmark checkbox is not ticked have TreePanel lose its reference to the bookmark list
        // so that bookmarked cells don't get drawn differntly
        else {
        	//System.out.println("Dereferencing bookmark in TreePanel...");
        	iTreePanel.setBookmarkListModel(null);
        }
        
        iTreePanel.repaint();
        //System.out.println("refreshTree: ");
        //iTreePanel.paintComponent(iTreePanel.getGraphics());
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Print")) {
            System.out.println("print requested");
            saveImage();
            //printIt(); // seems to fail due to rotated text
        } else if (s.equals("Refresh")) {
            //System.out.println("SulstonTree.Refresh");
            // 20050824 we must refresh the cell in case the tree has been rebuilt
            // apparently otherwise the cell reference we have refers to the previous tree
            // conditional expression prevents this update when this is a canonical tree
            refreshTree();
            //if (iCanInterrogate) {
            //    iCurrentCell = iAceTree.getCellByName(iCurrentCell.getName());
            //    iTreeCanvas.setCell(iCurrentCell);
            //}
            //iTreeCanvas.setLateTime(Integer.parseInt(iLateTimeField.getText()));
            //iTreeCanvas.repaint();

        }

    }


    public void printIt() {
        Toolkit t = getToolkit();
        PrintJob pj = t.getPrintJob(SulstonTree.this, "Printing " + title, null);
        if (pj != null) {
            Graphics pg = pj.getGraphics();
            //Graphics2D pg2d = (Graphics2D)pg;
            printAll(pg);
            pg.dispose();
            pj.end();
        }
    }

    private void listWriters() {
        String [] sa = ImageIO.getWriterMIMETypes();
        for (int i=0; i < sa.length; i++) {
            System.out.println(sa[i]);
        }
    }
    
    // Sets ListModel of bookmarked cells
    // Called by AceTree
    public void setBookmarkList(ListModel list) {
    	iBookmarkListModel = (DefaultListModel)list;
    	//System.out.println("Bookmark list set.");
    }


    @SuppressWarnings("unused")
	public void saveImage() {
        listWriters();
        JFileChooser iFC;
        iFC = new JFileChooser();
        int returnVal = iFC.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String dir = iFC.getCurrentDirectory().toString();
            String name = iFC.getName(iFC.getSelectedFile());
            String iTitle = dir + C.Fileseparator + name;
            //iTitle += "." +IMAGETYPE;
            //append(dir);
            //append(name);
            //println("SulstonTree.saveImage, " + iTreeCanvas + CS + iTitle);
            iTreePanel.captureImage(name, dir);
            /*
            Rectangle screenRect = this.getBounds();
            int topAdjust = 58;
            int y = screenRect.y;
            screenRect.y += topAdjust;
            int height = screenRect.height;
            screenRect.height -= topAdjust;
            // create screen shot
            Robot robot = null;
            try {
                robot = new Robot();
                BufferedImage image = robot.createScreenCapture(screenRect);
                ImageIO.write(image, IMAGETYPE, new File(iTitle + "." + IMAGETYPE));
            } catch(AWTException awtex) {
                awtex.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
            System.out.println("file: " + iTitle + "." + IMAGETYPE + " written");
            //iSaveInProcess = false;

             */
        }
    }

    private static final String
    CS = ", "
   ,IMAGETYPE = "png"
   ;

    private static final int
         WIDTH = 1000
        ,HEIGHT = 350
        ,MINWIDTH = 550
        ,MINHEIGHT = 200;
    ;

    public static void main(String[] args) {
    }
    /* (non-Javadoc)
     * @see java.awt.event.WindowFocusListener#windowGainedFocus(java.awt.event.WindowEvent)
     */
    @Override
	public void windowGainedFocus(WindowEvent e) {
        refreshTree();
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.WindowFocusListener#windowLostFocus(java.awt.event.WindowEvent)
     */
    @Override
	public void windowLostFocus(WindowEvent e) {

    }
    private static void println(String s) {System.out.println(s);}
    //private static final String CS = ", ";
    private static final DecimalFormat DF1 = new DecimalFormat("####.##");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
}


