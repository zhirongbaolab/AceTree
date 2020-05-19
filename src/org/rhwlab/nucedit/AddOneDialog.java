package org.rhwlab.nucedit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Vector;

import javax.swing.*;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.acetree.NucUtils;
//import org.rhwlab.image.EditImage3;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.Cell;
/*
 * name is historical currently serves as launcher for kill deep/kill windows and
 * for adjusting cell position name etc.
 */
public class AddOneDialog extends JDialog implements ActionListener, WindowFocusListener {
    ImageWindow             iParent;
    private AceTree         iAceTree;
    int                     iImageTime;
    int                     iTimeInc;
    int                     iImagePlane;
    int                     iPlaneInc;
    int                     iPrevTime;
    Cell                    iCurrentCell;
    Nucleus                 iNucleus;
    protected int           iNucSize;
    private Cell            iCurrentCellSave;
    private Nucleus         iNucleusCopy;
    private Nucleus         iNucleusActive;
    private boolean         iTimeChanged;
    private int             iTimeSave;

    //  private JRadioButton    iAdjust;
    // private JRadioButton    iAdd;
    private JButton    		iAddSeries;
    private JButton         iRelink;
    private JButton         iKillCells;
    private JButton         iKillDeep;
    private JButton         iRebuildAndRename;
    private JButton         iRebuildOnly;
    private JButton         iUp;
    private JButton         iDown;
    private JButton         iLeft;
    private JButton         iRight;
    private JButton         iBig;
    private JButton         iSmall;
    private JButton         iIncZ;
    private JButton         iDecZ;
    private JButton         iUndo;
    private JButton         iTest;
    //private JTextField      iName;
    private JLabel      	iName;
    private JTextField      iForceName;
    private JTextField      iX;
    private JTextField      iY;
    private JTextField      iZ;
    private JTextField      iD;
    private JButton         iSetN;
    private JButton         iForce;
    private JButton         iSetX;
    private JButton         iSetY;
    private JButton         iSetZ;
    private JButton         iSetD;
    private JButton			iDefault;

    private JTextField      iStartTime;
    private JTextField      iEndTime;
    private JButton         iPropX;
    private JButton         iPropY;
    private JButton         iPropZ;
    private JButton         iPropD;

    public AddOneDialog(AceTree aceTree, Frame owner, boolean modal, Cell cell, int time) {
        super(owner, modal);
        addWindowListener(new WindowEventHandler());
        //println("AddOneDialog, " + cell.getName() + CS + time);
        setTitle("Adjust or Delete Cells");
        iAceTree = aceTree;
        iNucSize = 50;
        JDialog dialog = this;
        JPanel pWhole = new JPanel();
        iParent = (ImageWindow)owner;
        pWhole.setOpaque(true); //content panes must be opaque
        pWhole.setLayout(new BoxLayout(pWhole,BoxLayout.PAGE_AXIS));
        fillControlPanel(pWhole);

        iTimeSave = -1;
        updateCurrentInfo(true);
        updateTextFields();
        //iCurrentCellSave = iCurrentCell;
        //System.out.println("EditImage: " + iNucleus);
        if (iNucleus == null)
        	iNucleus = new Nucleus();
        iNucleusCopy = iNucleus.copy();
        iNucleusActive = iNucleus;

        //iTimeSave = iImageTime + iTimeInc;
        iTimeSave = iImageTime;
        iCurrentCellSave = iCurrentCell;
        
        JScrollPane scrollPane = new JScrollPane(pWhole);
        dialog.setContentPane(scrollPane);
       
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        setKeyboardActions();

        addWindowFocusListener(this);
        dialog.setSize(new Dimension(370, 550));
    }

    private void setKeyboardActions() {
        Action home = new AbstractAction() {
            @Override
			public void actionPerformed(ActionEvent e) {
            	println("setKeyboardActions");
            	iAceTree.requestFocus();
            }
        };
        KeyStroke key = null;
        key = KeyStroke.getKeyStroke("F2");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false);

        iDefault.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, "pressed");
        iDefault.getActionMap().put("pressed", home );
        iDefault.requestFocus();

    }

    private class WindowEventHandler extends WindowAdapter {
        @Override
		public void windowClosing(WindowEvent e) {
        	iAceTree.iAddOneDialog = null;
            println("AddOneDialog.windowclosing");
        }
    }



    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
	public void actionPerformed(ActionEvent e) {
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

        Object o = e.getSource();
        String cmd = e.getActionCommand();
        //println("EIDialog2.actionPerformed: " + o);
	//  if (o == iAdd) return;
        if (o == iRelink) {
            iAceTree.relinkNucleus();
            //iAdjust.setSelected(true);
            iAceTree.updateDisplay();
        } else if (o == iKillCells) {
                iAceTree.killCells();
		//    iAdjust.setSelected(true);
            iAceTree.updateDisplay();
		//        } 
// 	else if( o== iAddSeries){
// 	    //added -as
// 	    // setDialogsEnabled(false);
// 	     Cell c = iAceTree.getCurrentCell();
// 	     int time = iImageTime + iTimeInc;
// 	     JDialog iDialog = new EIDialog1(iAceTree, iAceTree.getImageWindow(), false, c, time);
	}else if( o==iKillDeep){
	    iAceTree.killDeepNucs();
	    
	}else if (cmd.equals(REBUILDANDRENAME)) {
	    updateCurrentInfo(false);
	    //int time = iImageTime + iTimeInc
            int time = iImageTime;
	    Cell c = iCurrentCell;
	    iAceTree.clearTree();
	    iAceTree.buildTree(true);

            // update WormGUIDES data if it's open
            if (iAceTree.iAceMenuBar.view != null) {
                iAceTree.iAceMenuBar.view.rebuildData();
            }

	    iAceTree.updateDisplay();
	    //iEditLog.setModified(true);
	    //System.out.println("actionPerformed: " + c + C.CS + time);
	    if (c != null)
	    	iAceTree.setStartingCell(c, time);
            iAceTree.updateDisplay();
	    // iAdjust.setSelected(true);

//         } else if (o == iRebuildOnly) {
//                 updateCurrentInfo(false);
//                 int time = iImageTime + iTimeInc;
//                 Cell c = iCurrentCell;
//                 iAceTree.clearTree();
//                 iAceTree.buildTree(false);
// 		//   iEditLog.setModified(true);
//                 System.out.println("actionPerformed: " + c + C.CS + time);
//                 if (c != null) iAceTree.setStartingCell(c, time);
//                 iAdjust.setSelected(true);

        } else {
            //println("AddOneDialog.actionPerformed:2 iCurrentCell: " + iCurrentCell);
            updateCurrentInfo(false);
            setKeypadEnabled(true);
            //println("EIDialog2.actionPerformed:3 iCurrentCell: " + iCurrentCell);
            Nucleus n = iAceTree.getNucleiMgr().getNucleusFromHashkey(iCurrentCell.getHashKey(), iAceTree.getImageManager().getCurrImageTime());
            if(n==null)
            	return;
            
            if (cmd.equals(UP)) n.y--;
            else if (cmd.equals(DOWN)) n.y++;
            else if (cmd.equals(LEFT)) n.x--;
            else if (cmd.equals(RIGHT)) n.x++;
            else if (cmd.equals(BIG)) {
                n.size += 2;
                iNucSize = n.size;
            }
            else if (cmd.equals(SMALL)) {
                n.size -= 2;
                iNucSize = n.size;
            } else if (cmd.equals(INCZ)) {
            	n.z += ZINCREMENT;
            	iZ.setText(String.valueOf(n.z));
            } else if (cmd.equals(DECZ)) {
            	n.z -= ZINCREMENT;
            	iZ.setText(String.valueOf(n.z));
            }
            else if (o == iX || o == iSetX)
            	n.x = Integer.parseInt(iX.getText());
            else if (o == iY || o == iSetY)
            	n.y = Integer.parseInt(iY.getText());
            else if (o == iZ || o == iSetZ)
            	n.z = Float.parseFloat(iZ.getText());
            else if (o == iD || o == iSetD)
            	n.size = Integer.parseInt(iD.getText());
            else if (o == iName) {
                String oldName = n.identity;
                n.identity = iName.getText();
                iCurrentCell.setName(n.identity);
                iParent.updateCellAnnotation(iCurrentCell, oldName, iImageTime);
                iAceTree.updateDisplay();
            }
            
            // Force name button handled here
            else if (o == iForce) {
                n.assignedID = iForceName.getText();
                n.identity = n.assignedID;
                iName.setText(n.assignedID);
                iForceName.setText("");
                // we need to change the hashkey in the nucleus object and cell object
                // assume we know the iCurrentCell at this point
                //int time = iImageTime + iTimeInc;
                int time = iImageTime;
                
                String hashKey = NucUtils.makeHashKey(time, n);
                //System.out.println("addCell: " + hashKey);
                n.setHashKey(hashKey);
                iCurrentCell.setHashKey(hashKey);
                iCurrentCell.setName(n.identity);
                System.out.println("Cell hashkey, name: "+hashKey+", "+n.identity);
                
                // Try this -DT
                /*
                updateCurrentInfo(false);
                Cell c = iCurrentCell;
	            iAceTree.clearTree();
	            iAceTree.buildTree(false);
	            iAceTree.setCurrentCell(c, time, iAceTree.RIGHTCLICKONEDITIMAGE);
	            System.out.println("actionPerformed: " + c + C.CS + time);
	            if (c != null)
	            	iAceTree.setStartingCell(c, time);
	            
	            //iAceTree.relinkNucleus();
	            iParent.refreshDisplay(null);
	            
	            // If relinkNucleus() doesn't work, import AncesTree and manipulate the hashtable
	            // with iAceTree.getAncesTree()...
	            */

            }else if(o==iPropX){
            	//propogate x between start time and end time
            	checkTimesAndPropogateValue("X");
            	
            }else if(o==iPropY){
            	//propogate x between start time and end time
            	checkTimesAndPropogateValue("Y");
            	
            }else if(o==iPropZ){
            	//propogate x between start time and end time
            	checkTimesAndPropogateValue("Z");
            	
            }else if(o==iPropD){
            	//propogate x between start time and end time
            	checkTimesAndPropogateValue("D");
            	
            }
            iAceTree.updateDisplay();
        }

        success = iAceTree.ATLockNucleiMgr(false);

    }

    private void checkTimesAndPropogateValue(String field){

    	System.out.println("Propogate "+field);
    	int starttime;
    	int endtime;
    	try{
    		starttime=Integer.parseInt(iStartTime.getText());	
    		endtime=Integer.parseInt(iEndTime.getText());
    	}catch( NumberFormatException e){
    		System.out.println("Non Number Entry");
    		return;
    	}
    	boolean validrange=true;
    	Nucleus n;
    	// perform range check on  start end time
    	for (int i=starttime;i<=endtime;i++){
    		n = iAceTree.getNucleiMgr().getNucleusFromHashkey(iCurrentCell.getHashKey(), i);
    		if (n==null) validrange=false;
    	}
    	//if exists in range iterate over range
    	//adjust property
    	if(validrange){

    		for (int i=starttime;i<=endtime;i++){
    			n = iAceTree.getNucleiMgr().getNucleusFromHashkey(iCurrentCell.getHashKey(), i);
    			if(field.equals("X")){
    				n.x = Integer.parseInt(iX.getText());
    				//System.out.println("Valid Range X");
    			}
    			else if (field.equals("Y")){ 
    				n.y = Integer.parseInt(iY.getText());
    				//System.out.println("Valid Range Y");
    			}
    			else if (field.equals("Z")){ 
    				n.z = Float.parseFloat(iZ.getText());
    				//System.out.println("Valid Range Z");
    			} 
    			else if (field.equals("D")){ 
    				n.size = Integer.parseInt(iD.getText());
    				//System.out.println("Valid Range D");
    			}
    			//else
    				//System.out.println("Failure of Field");

    		}//end loop
    	}//end if valid
    	else{System.out.println("Invalid Time Range for Cell "+iNucleus);}
    }

    @Override
	public void processMouseEvent(MouseEvent e) {
        //println("AddOneDialog.processMouseEvent: " + e);
        int button = e.getButton();
       //  if (button == 1) {
//             if (iAdd.isSelected()) {
//                 addCell(e.getX(), e.getY(), false);
//             }
//         } else 
	    if (button == 3) {
            updateCurrentInfo(false);
            //Nucleus n = ImageWindow.cNucleiMgr.findClosestNucleus(e.getX(), e.getY(), iImagePlane + iPlaneInc, iImageTime + iTimeInc);
            Nucleus n = iAceTree.getNucleiMgr().findClosestNucleus(e.getX(), e.getY(), iAceTree.getImageManager().getCurrImagePlane(), iAceTree.getImageManager().getCurrImageTime());
            if (n == null) {
                System.out.println("cant find closest nucleus");
                return;
            }
            Cell c = (Cell)iAceTree.getAncesTree().getCells().get(n.hashKey);

            //System.out.println("mouseClicked1: " + c + C.CS + iCurrentCell
            //        + C.CS + iImagePlane + C.CS + iPlaneInc);
            //iAceTree.setCurrentCell(c, iImageTime + iTimeInc, AceTree.RIGHTCLICKONEDITIMAGE);
            iAceTree.setCurrentCell(c, iAceTree.getImageManager().getCurrImageTime(), AceTree.RIGHTCLICKONEDITIMAGE);
            iNucleus = n;
            updateTextFields();
            iAceTree.updateDisplay();

        } // else if (button == 2) {
//             addCell(e.getX(), e.getY(), false);

//         }
    }

    protected void addCell(int x, int y, boolean continuation) {
        //System.out.println("addCell: " + x + C.CS + y);
        updateCurrentInfo(false);
        //int time = iImageTime + iTimeInc;
        int time = iAceTree.getImageManager().getCurrImageTime();
        Vector nuclei = iAceTree.getNucleiMgr().getNucleiRecord().elementAt(time - 1);
        Nucleus n = new Nucleus();
        n.index = nuclei.size() + 1;
        String hashKey = NucUtils.makeHashKey(time, n);
        //System.out.println("addCell: " + hashKey);
        n.setHashKey(hashKey);
        n.status = 1;
        n.x = x;
        n.y = y;
        n.z = iAceTree.getImageManager().getCurrImagePlane();
        n.size = iNucSize;
        n.identity = "_" + hashKey;
        n.predecessor = -1;
        n.successor1 = -1;
        n.successor2 = -1;
        nuclei.add(n);
        iNucleus = n;

        Cell c = new Cell(n.identity, time);
        c.setHashKey(hashKey);
        iAceTree.getAncesTree().getCells().put(hashKey, c);
        iAceTree.setShowCentroids(true);
        iAceTree.setShowAnnotations(true);

        c.setParameters(time, time, n);
        Cell root = iAceTree.getAncesTree().getRoot();
        c.setParent(root);
        //iAceTree.setCurrentCell(c, time, AceTree.CONTROLCALLBACK);
//        iAceTree.setCurrentCell(c, iImageTime + iTimeInc, AceTree.RIGHTCLICKONEDITIMAGE);


        iAceTree.setCurrentCell(c, time, AceTree.RIGHTCLICKONEDITIMAGE);
        iParent.addAnnotation(x, y, true);




	//        iAdjust.setSelected(true);
        setKeypadEnabled(true);
        iName.setText(n.identity);

        iAceTree.treeValueChangedFromEdit = true;
        iAceTree.updateDisplay();

        //System.out.println("addCell: " + iCurrentCell);
        //iAceTree.clearTree();
        //iAceTree.buildTree(true);
    }

    /*
     * I added a public update function for acetree to call to keep display up to date
     * previously was done only in event handlers, but addone doesnt get events
     * for moving in time
     */
    public void updateCellInfo(){
    	updateCurrentInfo(false);
    	updateTextFields();
    }

    protected void updateCurrentInfo(boolean detectChange) {
        //System.out.println("EditImage.updateCurrentInfo called: " + new GregorianCalendar().getTime());
        iImageTime = iAceTree.getImageManager().getCurrImageTime();
        iImagePlane = iAceTree.getImagePlane();
        iTimeInc = iAceTree.getTimeInc();
        iPlaneInc = iAceTree.getPlaneInc();
        iCurrentCell = iAceTree.getCurrentCell();
        //Vector nuclei = ImageWindow.cNucleiMgr.getNuclei(iImageTime + iTimeInc - 1);
        Vector nuclei = iAceTree.getNucleiMgr().getNuclei(iImageTime - 1);
        iNucleus = NucUtils.getCurrentCellNucleus(nuclei, iCurrentCell);
        
        // Force named cell does not get set to iCurrentCell -DT
        //println("updateCurrentInfo: " + iCurrentCell + CS + iNucleus);
        //if (!detectChange) return;
        //System.out.println("updateCurrentInfo: detect change steps implemented");
    }

    protected void updateTextFields() {
        updateCurrentInfo(false);
        //System.out.println("updateTextFields: " + iName + C.CS + iNucleus);
        if (iNucleus == null) { //workaround
        	iName.setText("");
			//  iForceName.setText(iNucleus.assignedID);
			iX.setText("");
			iY.setText("");
			iZ.setText("");
			iD.setText("");
			iStartTime.setText("");
        }else{
	        iName.setText(iNucleus.identity);
		//  iForceName.setText(iNucleus.assignedID);
	        iX.setText(String.valueOf(iNucleus.x));
	        iY.setText(String.valueOf(iNucleus.y));
	        iZ.setText(String.valueOf(iNucleus.z));
	        iD.setText(String.valueOf(iNucleus.size));
	        iStartTime.setText(String.valueOf(iImageTime));
        }
    }

    protected void fillControlPanel(JPanel pp) {
        //buttons for deletion
    	addChoices(pp);
    	
        pp.add((new JSeparator(SwingConstants.HORIZONTAL))); 
        
        //adjust section
        JLabel l=new JLabel("Adjust Cell Position");
        l.setAlignmentX(Component.CENTER_ALIGNMENT);	
        pp.add(l);
        
        //add name field
        iName = new JLabel(NAME);
        l = new JLabel(NAME+":");
        JPanel grouper=new JPanel();
        grouper.add(l);
        grouper.add(iName);
        pp.add(grouper);
        
        // Add force name option
        l = new JLabel(FORCENAME);
        iForceName = new JTextField("", 8);
        iForce = new JButton(FORCENAME);
        iForce.addActionListener(this);
        grouper = new JPanel();
        grouper.add(l);
        grouper.add(iForceName);
        grouper.add(iForce);
        pp.add(grouper);
        pp.add((new JSeparator(SwingConstants.HORIZONTAL))); 
        
        addKeypad(pp);
        pp.add((new JSeparator(SwingConstants.HORIZONTAL)));
        pp.add(new JLabel(" "));
        addTextFields(pp);
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
        iIncZ = new JButton(INCZ);
        iDecZ = new JButton(DECZ);

        //iShowC = new JButton(SHOWC);
        iLeft.addActionListener(this);
        iRight.addActionListener(this);
        iUp.addActionListener(this);
        iDown.addActionListener(this);
        //iUndo.addActionListener(this);
        iTest.addActionListener(this);
        iBig.addActionListener(this);
        iSmall.addActionListener(this);
        iIncZ.addActionListener(this);
        iDecZ.addActionListener(this);
        //iHome.addActionListener(this);
        p.setLayout(new GridLayout(3,3));
        p.setBorder(BorderFactory.createLineBorder(Color.white));
        p.add(iBig);
        p.add(iUp);
        p.add(iSmall);
        p.add(iLeft);
        p.add(new JButton());
        p.add(iRight);
        p.add(iIncZ);
        p.add(iDown);
        //p.add(iUndo);
        p.add(iDecZ);
        mp.add(p);
        //setKeypadEnabled(false);
        iDefault = iSmall;

    }

    protected void setKeypadEnabled(boolean b) {
        iUp.setEnabled(b);
        iDown.setEnabled(b);
        iLeft.setEnabled(b);
        iRight.setEnabled(b);
        iBig.setEnabled(b);
        iSmall.setEnabled(b);
        iName.setEnabled(b);
        iX.setEnabled(b);
        iY.setEnabled(b);
        iZ.setEnabled(b);
        iD.setEnabled(b);
    }

    @SuppressWarnings("unused")
	protected void addChoices(JPanel mp) {
    	JRadioButton rb = null;
    	ButtonGroup bg = new ButtonGroup();
    	JPanel rp = new JPanel();
    	rp.setLayout(new BoxLayout(rp,BoxLayout.LINE_AXIS));

    	rp.setBorder(BorderFactory.createLineBorder(Color.white));
    	JLabel l=new JLabel("Delete Tools");
    	l.setAlignmentX(Component.CENTER_ALIGNMENT);
    	mp.add(l);
    	JPanel group=new JPanel();
    	iKillDeep = new JButton(KILLGROUP);
    	iKillDeep.addActionListener(this);
    	bg.add(iKillDeep);
    	group.add(iKillDeep);

    	iKillCells= new JButton(KILLCELLS);
    	iKillCells.addActionListener(this);      
    	group.add(iKillCells);
    	rp.add(group);
    	mp.add(rp);
    }

    protected void addTextFields(JPanel mp) {
        JPanel p = new JPanel();
        //p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setLayout(new GridLayout(0,3));
        //iName = new JTextField(NAME, 5);
       
        
	//  iForceName = new JTextField("");
        iX = new JTextField(X,5);
        iY = new JTextField(Y,5);
        iZ = new JTextField(Z,5);
        iD = new JTextField(D,5);
        //iName.addActionListener(this);
        iX.addActionListener(this);
        iY.addActionListener(this);
        iZ.addActionListener(this);
        iD.addActionListener(this);
        
        
      
       // iSetN = new JButton(SETN);
       // iSetN.addActionListener(this);
       // p.add(iSetN);
        //p.add(new JLabel(""));
        
     
	// l = new JLabel(FORCENAME);
	// p.add(l);
	// p.add(iForceName);
	//  iForce = new JButton(FORCE);
	// iForce.addActionListener(this);
	// p.add(iForce);
        JLabel l = new JLabel(X);
        
       JPanel grouper=new JPanel();
        grouper.add(l);
        grouper.add(iX);
        p.add(grouper);
        
//        p.add(l);
 //       p.add(iX);
        iSetX = new JButton(SETX);
        iSetX.addActionListener(this);
        
        p.add(iSetX);
        
        iPropX = new JButton(PROPX);
        iPropX.addActionListener(this);
        p.add(iPropX);
        
        l = new JLabel(Y);
        
        grouper=new JPanel();
        grouper.add(l);
        grouper.add(iY);
        p.add(grouper);
        
//        p.add(l);
 //       p.add(iY);
        iSetY = new JButton(SETY);
        iSetY.addActionListener(this);
        p.add(iSetY);
        
        iPropY = new JButton(PROPY);
        iPropY.addActionListener(this);
        p.add(iPropY);
        
        l = new JLabel(Z);
//        p.add(l);
 //       p.add(iZ);
        grouper=new JPanel();
        grouper.add(l);
        grouper.add(iZ);
        p.add(grouper);
        
        iSetZ = new JButton(SETZ);
        iSetZ.addActionListener(this);
        p.add(iSetZ);
        
        iPropZ = new JButton(PROPZ);
        iPropZ.addActionListener(this);
        p.add(iPropZ);
        
        l = new JLabel(D);
       // p.add(l);
        //p.add(iD);
        grouper=new JPanel();
        grouper.add(l);
        grouper.add(iD);
        p.add(grouper);
        
        iSetD = new JButton(SETD);
        iSetD.addActionListener(this);
        p.add(iSetD);
        
        iPropD = new JButton(PROPD);
        iPropD.addActionListener(this);
        p.add(iPropD);
        
        mp.add(p);
        
        //add times and propotagion
        iStartTime = new JTextField(" ",4);
        iEndTime = new JTextField(" ",4);
        
       l=new JLabel("Time Range For Propogation");
    	l.setAlignmentX(Component.CENTER_ALIGNMENT);
    	  mp.add(l);
    	  grouper=new JPanel();
    	  grouper.add(new JLabel("Start Time:"));
    	  grouper.add(iStartTime);
    	  //mp.add(grouper);
    	  // grouper=new JPanel();
    	  grouper.add(new JLabel("End Time:"));
    	  grouper.add(iEndTime);
    	  mp.add(grouper);
    }

    @Override
	public void processWindowEvent(WindowEvent e){
        //println("processWindowEvent: " + e);
        int id = e.getID();
        if (id == WindowEvent.WINDOW_CLOSING) {
	    //   iParent.parentNotifyDialogClosing(this);
            iAceTree.iAddOneDialog = null;
            dispose();
            //System.exit(0);
        }

    }



    public static final String
	    UP = "UP"
	   ,DOWN = "DOWN"
	   ,LEFT = "LEFT"
	   ,RIGHT = "RIGHT"
	   ,TEST = "TEST"
	   ,UNDO = "UNDO"
	   ,BIG = "BIG"
	   ,SMALL = "SMALL"
	   ,INCZ = "INC Z"
	   ,DECZ = "DEC Z"
	   ,KILLDEEP="Kill Deep Nuclei"
	   ,KILLGROUP="Kill Group"
	   ,ADJUST = "Adjust Cell"
	   ,ADD = "Add Cell"
	   ,ADDSERIES = "Add Cell Series"
	   ,REBUILDANDRENAME =  "rebuildAndRename"
	   ,RELINK = "Relink"
	   ,KILLCELLS = "Kill Cell"
	   ,REBUILDONLY = "rebuildOnly"
	   ,NAME = "Name"
	   ,FORCENAME = "Force Name"
	   ,X = "X"
	   ,Y = "Y"
	   ,Z = "Z"
	   ,D = "D"
	   ,FORCE = "Force"
	   ,SETN = "Set N"
	   ,SETX = "Set X"
	   ,SETY = "Set Y"
	   ,SETZ = "Set Z"
	   ,SETD = "Set D"
	   ,STARTTIME="Start Time"
	   ,ENDTIME="End Time"
	   ,PROPX = "Propogate X"
	   ,PROPY = "Propogate Y"
	   ,PROPZ = "Propogate Z"
	   ,PROPD = "Propogate D"
   ;

    static final double ZINCREMENT = 0.5;



    public static void main(String[] args) {
        //EIDialog2 eid2 = new EIDialog2(null, null, false, null, 0);

    }
    protected static void println(String s) {System.out.println(s);}
    protected static final String CS = ", ";


	@Override
	public void windowGainedFocus(WindowEvent e) {
		//println("AddOneDialog.windowGainedFocus, ");
		iParent.iDialog = this;

	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		//println("AddOneDialog.windowLostFocus, ");

	}
}
