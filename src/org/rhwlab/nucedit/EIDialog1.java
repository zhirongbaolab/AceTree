/*
 * Created on Apr 27, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.nucedit;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import org.rhwlab.acetree.AceTree;
//import org.rhwlab.image.EditImage3;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;

/**
 * @author biowolp
 *
 * ADD SERIES DIALOG
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EIDialog1 extends JDialog implements ActionListener, WindowFocusListener {

    public ImageWindow     iParent;
    public JTextField      iName;
    public JTextField      iTime;
    public JRadioButton    iSetStart;
 
    public JRadioButton    iLink;
    public JRadioButton    iNothing;

       public JRadioButton    iAddSeries;
    private JButton         iUp;
    private JButton         iDown;
    private JButton         iLeft;
    private JButton         iRight;
    private JButton         iBig;
    private JButton         iSmall;
    private JButton         iUndo;
    private JButton         iTest;

    private JButton         iRebuildAndRename;

    private AceTree         iAceTree;
    int                     iImageTime;
    int                     iTimeInc;
    int                     iImagePlane;
    int                     iPlaneInc;
    int                     iPrevTime;
    Cell                    iCurrentCell;
    Nucleus                 iNucleus;
    Nucleus                 iLinkNucleus;



    public EIDialog1(AceTree aceTree, Frame owner, boolean modal,
            Cell cell, int time) {
        super(owner, modal);
        setTitle("Add series of nuclei");
        iAceTree = aceTree;
        JDialog dialog = this;
        JPanel pWhole = new JPanel();
        iParent = (ImageWindow)owner;
        pWhole.setOpaque(true); //content panes must be opaque
        addControls(pWhole);
        dialog.setContentPane(pWhole);
        dialog.setSize(new Dimension(310, 400));
        dialog.setLocationRelativeTo(owner);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);

        addWindowFocusListener(this);

    }


    protected void addControls(JPanel pp) {
        pp.setLayout(new BoxLayout(pp, BoxLayout.PAGE_AXIS));
        pp.add(new JLabel("Starting cell"));
        JPanel jp = new JPanel();
        jp.setLayout(new FlowLayout());
        jp.add(new JLabel("Name:"));
        iName = new JTextField("", 15);
        jp.add(iName);
        jp.setMaximumSize(new Dimension(300, 20));
        pp.add(jp);

        jp = new JPanel();
        jp.setLayout(new FlowLayout());
        jp.add(new JLabel("Time: "));
        iTime = new JTextField("", 15);
        jp.add(iTime);
        jp.setMaximumSize(new Dimension(300, 20));
        pp.add(jp);




        JPanel ppx = new JPanel();
        Border blackline = BorderFactory.createLineBorder(Color.black);

        //ppx.setLayout(new BoxLayout(ppx, BoxLayout.PAGE_AXIS));
        ppx.setLayout(new GridLayout(0,1));
        ButtonGroup bg = new ButtonGroup();
        iSetStart = new JRadioButton("Set start (with right click)");
        ppx.add(iSetStart);
        iAddSeries = new JRadioButton("Add series (with left click)");
        ppx.add(iAddSeries);
        iLink = new JRadioButton("Link to existing cell (with right click)");
        ppx.add(iLink);
        iNothing = new JRadioButton("Nothing (safe haven for clicks)");
        ppx.add(iNothing);
        bg.add(iSetStart);
        bg.add(iAddSeries);
        bg.add(iLink);
        bg.add(iNothing);
        iSetStart.setSelected(true);
        ppx.setBorder(blackline);
        pp.add(ppx);

        ppx = new JPanel();
        addKeypad(ppx);
        pp.add(ppx);

        ppx = new JPanel();
        iRebuildAndRename= new JButton(REBUILDANDRENAME);
        iRebuildAndRename.addActionListener(this);
        //bg.add(iRebuildAndRename);
        ppx.add(iRebuildAndRename);

        pp.add(ppx);

    }

    protected void addKeypad(JPanel mp) {
        JPanel p = new JPanel();
        iLeft = new JButton(LEFT);
        iRight = new JButton(RIGHT);
        iUp = new JButton(UP);
        iDown = new JButton(DOWN);
        //iUndo = new JButton(UNDO);
        iTest = new JButton(TEST);
        iBig = new JButton(BIG);
        iSmall = new JButton(SMALL);

        //iShowC = new JButton(SHOWC);
        iLeft.addActionListener(this);
        iRight.addActionListener(this);
        iUp.addActionListener(this);
        iDown.addActionListener(this);
        //iUndo.addActionListener(this);
        iTest.addActionListener(this);
        iBig.addActionListener(this);
        iSmall.addActionListener(this);
        //iHome.addActionListener(this);
        p.setLayout(new GridLayout(3,3));
        p.setBorder(BorderFactory.createLineBorder(Color.white));
        p.add(iBig);
        p.add(iUp);
        p.add(iSmall);
        p.add(iLeft);
        p.add(new JButton());
        p.add(iRight);
        p.add(new JButton());
        p.add(iDown);
        //p.add(iUndo);
        p.add(new JButton());
        mp.add(p);
        //setKeypadEnabled(false);

    }


    @SuppressWarnings("unused")
	protected void addCell(int x, int y, boolean continuation) {
    	println("EIDialog.addCell, " + x + CS + y + CS + continuation);
        if (!continuation) {

        } else {
            AncesTree ances = iAceTree.getAncesTree();
            Hashtable h = ances.getCellsByName();
            Cell c = (Cell)h.get(iNucleus.identity);
            Cell parent = (Cell)c.getParent();
            updateCurrentInfo(false);
            Vector nuclei_record = iAceTree.getNucleiMgr().getNucleiRecord();
            Vector nucleiAdd = null;
            int deltaT = iAceTree.getImageManager().getCurrImageTime() - iPrevTime;
            int startx = iNucleus.x;
            int starty = iNucleus.y;
            float startz = iNucleus.z;
            float z = iAceTree.getImageManager().getCurrImagePlane();
            int prevTime = iPrevTime;
            Nucleus n = null;
            for (int k=prevTime + 1; k <= iAceTree.getImageManager().getCurrImageTime(); k++) {
                nucleiAdd = (Vector)nuclei_record.elementAt(k - 1);

                if (k < iAceTree.getImageManager().getCurrImageTime() || iLinkNucleus == null) {
                    n = iNucleus.copy();
                    int deltaM = k - prevTime;
                    n.x = (x - startx)*deltaM/deltaT + startx;
                    n.y = (y - starty)*deltaM/deltaT + starty;
                    n.z = (z - startz)*deltaM/deltaT + startz;
                    n.index = nucleiAdd.size() + 1;
                    iNucleus.successor1 = n.index;
                    n.predecessor = iNucleus.index;
                    nucleiAdd.add(n);
                } else {
                    n = iLinkNucleus.copy();
                    n.identity = iNucleus.identity;
                    n.hashKey = iNucleus.hashKey;
                    iNucleus.successor1 = n.index;
                    n.predecessor = iNucleus.index;
                    nucleiAdd.setElementAt(n, iLinkNucleus.index - 1);
                    println("linknucleuspath: " + n);
                }
                iNucleus = n;

                c = new Cell(n.identity, iAceTree.getImageManager().getCurrImageTime());
                c.setHashKey(iNucleus.hashKey);
                c.setParameters(iAceTree.getImageManager().getCurrImageTime(), iAceTree.getImageManager().getCurrImageTime(), n);
                Cell root = iAceTree.getAncesTree().getRoot();
                c.setParent(parent);

                if (iAceTree.iAceMenuBar.view != null) {
                    iAceTree.iAceMenuBar.view.rebuildData();
                }

                iAceTree.setCurrentCell(c, iAceTree.getImageManager().getCurrImageTime(), AceTree.RIGHTCLICKONEDITIMAGE);
                iParent.addAnnotation(x, y, true);
                iName.setText(iNucleus.identity);
                iTime.setText(String.valueOf(k));
                iPrevTime = k;
                iAceTree.updateDisplay();

            }
        }
    }

    protected void updateCurrentInfo(boolean detectChange) {
        System.out.println("EditImage.updateCurrentInfo called: " + new GregorianCalendar().getTime());
        iImageTime = iAceTree.getImageManager().getCurrImageTime();
        iImagePlane = iAceTree.getImageManager().getCurrImagePlane();
        iTimeInc = iAceTree.getTimeInc();
        iPlaneInc = iAceTree.getPlaneInc();
        iCurrentCell = iAceTree.getCurrentCell();
        if (!detectChange) return;
        //System.out.println("updateCurrentInfo: detect change steps implemented");
    }

    // mouse events are trapped in editImage3 and passed into this fn if its window if is up
    // only left mouse button events come to EIDialog1
    @Override
	public void processMouseEvent(MouseEvent e) {

        int button = e.getButton();
        println("processMouseEvent, " + button);
        if (button == 3) {
            updateCurrentInfo(false);
            Nucleus n = iAceTree.getNucleiMgr().findClosestNucleus(e.getX(), e.getY(), iAceTree.getImageManager().getCurrImagePlane(), iAceTree.getImageManager().getCurrImageTime());
            if (n == null) {
                System.out.println("cant find closest nucleus");
                return;
            }
            Cell c = (Cell)iAceTree.getAncesTree().getCells().get(n.hashKey);

            //System.out.println("mouseClicked1: " + c + C.CS + iCurrentCell
            //        + C.CS + iImagePlane + C.CS + iPlaneInc);
            iAceTree.setCurrentCell(c, iAceTree.getImageManager().getCurrImageTime(), AceTree.RIGHTCLICKONEDITIMAGE);

            iAceTree.updateDisplay();
            //System.out.println("mouseClicked2: " + c + C.CS + iCurrentCell);
            if (iAddSeries.isSelected()) {
                println("right click ignored with Add series selected");
            } else if (iSetStart.isSelected()) {
                iName.setText(n.identity);
                iPrevTime = iImageTime;
                iTime.setText(String.valueOf(iPrevTime));

                iNucleus = n;
                iAddSeries.setSelected(true);
            } else if (iLink.isSelected()) {
                if (iNucleus.successor2 != Nucleus.NILLI) {
                    println("Link error re: "  + iNucleus.identity);
                    return;
                } else {
                    iLinkNucleus = n;
                    addCell(e.getX(), e.getY(), true);
                    //iNucleus.successor1 = n.index;
                    //n.predecessor = iNucleus.index;
                    println("recommending a rebuild:");
                }
            }
            //iAceTree.updateDisplay();


        } else if (button == 1) {
            if (iSetStart.isSelected()) {
                iLinkNucleus = null;
                addCell(e.getX(), e.getY(), false);
            }
            if (iAddSeries.isSelected()) {
                iLinkNucleus = null;
                addCell(e.getX(), e.getY(), true);
            }

        }

    }

    @Override
	public void processWindowEvent(WindowEvent e){
        //println("processWindowEvent: " + e);
        int id = e.getID();
        if (id == WindowEvent.WINDOW_CLOSING) {
        	//iParent.addSeriesClosing();
            //iParent.parentNotifyDialogClosing(this);
            dispose();
        }

    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
	public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == iRebuildAndRename) {
            updateCurrentInfo(false);
            int time = iAceTree.getImageManager().getCurrImageTime();
            Cell c = iCurrentCell;
            iAceTree.clearTree();
            iAceTree.buildTree(true);

            if (iAceTree.iAceMenuBar.view != null) {
                iAceTree.iAceMenuBar.view.rebuildData();
            }

            if (c != null) iAceTree.setStartingCell(c, time);
            iAceTree.updateDisplay();
        }
        Nucleus n = iAceTree.getNucleiMgr().getNucleusFromHashkey(iCurrentCell.getHashKey(), iAceTree.getImageManager().getCurrImageTime());
        if (o == iUp) {
            n.y--;
            iAceTree.updateDisplay();

        }
        else if (o == iDown) {
            n.y++;
            iAceTree.updateDisplay();
        }
        else if (o == iLeft) {
            n.x--;
            iAceTree.updateDisplay();
        }
        else if (o == iRight) {
            n.x++;
            iAceTree.updateDisplay();
        }
        else if (o == iBig) {
            n.size += 2;
            iAceTree.updateDisplay();
        }
        else if (o == iSmall) {
            n.size -= 2;
            iAceTree.updateDisplay();
        }

    }

    protected static final String
    UP = "UP"
   ,DOWN = "DOWN"
   ,LEFT = "LEFT"
   ,RIGHT = "RIGHT"
   ,TEST = "TEST"
   ,UNDO = "UNDO"
   ,BIG = "BIG"
   ,SMALL = "SMALL"
   ,ADD = "addCell"
   ,REBUILDANDRENAME =  "Apply Changes"
   ;

    public static void main(String[] args) {
    }

    protected static void println(String s) {System.out.println(s);}
    protected static final String CS = ", ";



	@Override
	public void windowGainedFocus(WindowEvent e) {
		println("EIDialog.windowGainedFocus, ");
		iParent.iDialog = this;

	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		println("EIDialog.windowLostFocus, ");

	}


}
