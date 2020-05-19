package org.rhwlab.nucedit;

import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import org.rhwlab.acetree.NucUtils;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.acetree.AceTree;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.image.ImageWindow;

import javax.swing.border.Border;

/**
 * @author Santella
 *
 * subclass of nucrelinkdialog that implements middle keyframe button to unify
 * all nucleus adding relinking in one dialog
 */
public class UnifiedNucRelinkDialog extends NucRelinkDialog{
    private JButton 		       iAddKeyframe;
    private JButton  iInactivate;
    private JCheckBox iWarned;
    private boolean addKeyframeActive=false;
    private JLabel iStatus;


    @SuppressWarnings("unused")
	public UnifiedNucRelinkDialog(AceTree aceTree, Frame owner, boolean modal, Cell cell, int time) {
    	super(owner, modal);
    	Border blackline = BorderFactory.createLineBorder(Color.black);
    	
    	iAceTree = aceTree;
    	iNucleiMgr = iAceTree.getNucleiMgr();
    	iEditLog = iAceTree.getEditLog();
    	//iDLog = iAceTree.getDebugLog();
    	setTitle(TITLE);

    	JDialog dialog = this;
    	//dialog.setFocusable(false);
    	addWindowListener(new WindowEventHandler());
    	dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    	JPanel pWhole = new JPanel();
    	//pWhole.setLayout(new BoxLayout(pWhole, BoxLayout.PAGE_AXIS));
    	createEarlyPanel(pWhole);
    	//pWhole.add((new JSeparator(SwingConstants.VERTICAL)));
    	
    	//create and add middle
    	JPanel middle=new JPanel();
    	middle.setLayout(new BoxLayout(middle, BoxLayout.PAGE_AXIS));	
    	middle.setAlignmentX(Component.CENTER_ALIGNMENT);
    	middle.setBackground(Color.white);
    	middle.setForeground(Color.white);
    	//middle.setBorder(blackline);
    	iAddKeyframe=new JButton(KEYFRAME);
    	iAddKeyframe.addActionListener(this);
    	JLabel label=new JLabel(KEYFRAMEHEADER,SwingConstants.CENTER);
    	label.setAlignmentX(Component.CENTER_ALIGNMENT);
    	middle.add(label);
    	
    	JPanel warngroup=new JPanel();
    	warngroup.setBackground(Color.white);    
    	label=new JLabel(WARNING);
    	label.setAlignmentX(Component.CENTER_ALIGNMENT);
    	warngroup.add(label);
    	iWarned=new JCheckBox();
    	warngroup.add(iWarned);
    	warngroup.setAlignmentX(Component.CENTER_ALIGNMENT);
        
    	middle.add(warngroup);
    
    	middle.add(iAddKeyframe);
    	iAddKeyframe.setAlignmentX(Component.CENTER_ALIGNMENT);
    	iStatus=new JLabel("Inactive");
    	iStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
    	middle.add(iStatus);
    	middle.add(Box.createVerticalGlue());
    	middle.add(new JPanel());
    	//iInactivate=new JButton(DEACTIVATE);
    	//iInactivate.addActionListener(this);
    	//iInactivate.setEnabled(false);
    	//iInactivate.setAlignmentX(Component.CENTER_ALIGNMENT);
    	//middle.add(iInactivate);
    	//middle.setPreferredSize(new Dimension(WIDTH,100));
    	
        pWhole.add((new JSeparator(SwingConstants.HORIZONTAL))); 
        pWhole.add((new JSeparator(SwingConstants.HORIZONTAL))); 
        
    	pWhole.add(middle);

        pWhole.add((new JSeparator(SwingConstants.HORIZONTAL))); 
        pWhole.add((new JSeparator(SwingConstants.HORIZONTAL))); 
        
    	//pWhole.add((new JSeparator(SwingConstants.VERTICAL)));
    	createLatePanel(pWhole, owner);    
    	this.setSize(new Dimension(WIDTH, HEIGHT+60));
    }

    @Override
	@SuppressWarnings("unused")
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
    	if (o==iAddKeyframe || cmd.equals(SHORTCUTTRIGGER)) {
    		if(!addKeyframeActive) {
    			if(iWarned.isSelected()) {
	    			//add cell if no start specified
	    			// relink and set end as start if start specified
	    			addKeyframeActive=true;
	    			iAddKeyframe.setText("Stop Adding");
	    		
	    			iStatus.setText("Active");
	    			iStatus.setForeground(Color.red);
    			} else {
    				iStatus.setText("Warning Checkbox Not Selected!");
    				return;
    			}
    			//iInactivate.setEnabled(true);
    		} else {
    				//	System.out.println("deactivate");
    				addKeyframeActive=false;
    				iAddKeyframe.setText(KEYFRAME);
    				iStatus.setText("Inactive");
    				iStatus.setForeground(Color.black);
    				//iInactivate.setEnabled(false);
    				iWarned.setSelected(false);
    		}
		} else {
    		//only handles new button event old ones unchanged
    		// but deactivate whenever anything done with old buttons
    		super.actionPerformed(e);
    		addKeyframeActive=false;
    		iAddKeyframe.setText(KEYFRAME);	
    		iStatus.setText("Inactive");
    		iStatus.setForeground(Color.black);
    		//iInactivate.setEnabled(false);
    		iWarned.setSelected(false);
    	}
		success = iAceTree.ATLockNucleiMgr(false);
    }
    // here is the main new logic extending this class to
    // have functionality similar to addseries 
    @Override
	@SuppressWarnings("unused")
	public void processMouseEvent(MouseEvent e){
    	// filter out right clicks so can tracked active in main window
    	if(e.getButton() != MouseEvent.BUTTON1)
    		return;
    	// System.out.println("process mouse event in unifiednucrelink"+addKeyframeActive);
    	//boolean showNames = iAceTree.getShowAnnotations();
    	if(addKeyframeActive & iWarned.isSelected()) {
    		//System.out.println("Good for adding nucleus.");
    		String strCellName = iLinkNuc.getText();
    		int x = e.getX();
    		int y = e.getY();
    		int z = iAceTree.getImageManager().getCurrImagePlane();
    		int time = iAceTree.getImageManager().getCurrImageTime();
		
    		if(!iStartArmed || strCellName.equals(AceTree.ROOTNAME)) {
				String ID = addCell(x,y);
    			//rebuild and rename
				//System.out.println("Rebuild tree and rename nucleus.");
    			iAceTree.clearTree();
    			iAceTree.buildTree(true);


    		} else {
    			int startTime;
    			try {
    				 startTime = Integer.parseInt(iLinkTime.getText());
    			} catch(NumberFormatException nfe) {
    				System.out.println("invalid relink time, aborting");
    				return;
    			}
    			if (startTime < time){
    				//System.out.println("Relinking cell...");
    				relinkIntermediateCell(x, y);
				}
    			else {
    				System.out.println("Start: "+startTime+" current "+time+" cannot extend before end of track");
    				return;
				}

    		}

			// update WormGUIDES data if it's open
			if (iAceTree.iAceMenuBar.view != null) {
				iAceTree.iAceMenuBar.view.updateData(time);
			}
    		
    		//find ref to what we just created now that its renamed
			Nucleus itself = iAceTree.getNucleiMgr().findClosestNucleusXYZ(x, y, z, time);
			//System.out.println("found nucleus"+itself);
			Cell itselfcell = (Cell)(iAceTree.getAncesTree().getCellsByName().get(itself.identity));
			//set it as active cell for actree
			iAceTree.setStartingCell(itselfcell,time);

			// we need to call an update now
			iAceTree.getImageManager().setCurrImageTime(time);
			iAceTree.iImgWin.addAnnotation(x, y, true);

			iAceTree.updateDisplay();

			//set it as current early cell
			iLinkTime.setText(Integer.toString(time));
			iLinkNuc.setText(itself.identity);
			iStartArmed=true;// is set so will be interpreted as such
    	//	addKeyframeActive=false;
    	//	iStatus.setText("Inactive");
    	//	iStatus.setForeground(Color.black);
    	}
    }


    /*
      //transplant of addsingle add cell function executed when root is
	  //pred or none chosen
     */
    protected String addCell(int x, int y) {
    	int time = iAceTree.getImageManager().getCurrImageTime();
        Vector nuclei = iAceTree.getNucleiMgr().getNucleiRecord().elementAt(time - 1);
        Nucleus n = new Nucleus();
        n.index = nuclei.size() + 1;
        String hashKey = NucUtils.makeHashKey(time, n);
        n.setHashKey(hashKey);
        n.status = 1;
        n.x = x;
        n.y = y;
        int plane= iAceTree.getImageManager().getCurrImagePlane();
        n.z = plane;
		System.out.println("make nucleus "+x+" "+y+" "+plane+" "+time);
		//	Nucleus nclose = ImageWindow.cNucleiMgr.findClosestNucleus(x,y,plane,time);
		Cell ccur=iAceTree.getCurrentCell();
		if (ccur != null) {
			int diameter = (int)ccur.getDiam();
			//System.out.println("UnifiedNucRelinkDialog got current cell diameter: "+diameter);
		    if (diameter <= 0)
		    	diameter = 5;
		    n.size = diameter;
		}
		else
		    n.size = 20;

        n.identity = "_" + hashKey;
        n.predecessor = -1;
        n.successor1 = -1;
        n.successor2 = -1;
        nuclei.add(n);
     

        Cell c = new Cell(n.identity, time);
        c.setHashKey(hashKey);
        iAceTree.getAncesTree().getCells().put(hashKey, c);
        iAceTree.setShowCentroids(true);
        //iAceTree.setShowAnnotations(true);

        c.setParameters(time, time, n);
        Cell root = iAceTree.getAncesTree().getRoot();
        c.setParent(root);
		
		iAceTree.setCurrentCell(c, time, AceTree.RIGHTCLICKONEDITIMAGE);
		iAceTree.iImgWin.addAnnotation(x, y, true);
		//iAceTree.updateDisplay();
		return(n.identity);
    }
    
    
    protected void relinkIntermediateCell(int x,int y){
		System.out.println("adding tween");
		String ID=addCell(x,y);
		//set to late and rebuild
		int time = iAceTree.getImageManager().getCurrImageTime();
		iRelinkTime.setText(Integer.toString(time));
		iRelinkNuc.setText(ID);
		relinkAndRebuild();

		// try and set active timepoint to intermediate point at end of interpolation
		//name will not be right if addition has created a bifurcation
		// so search for C at time should fail returning null
		//and update to current cell will not occur
		AncesTree ances = iAceTree.getAncesTree();
		Hashtable h = ances.getCellsByName();
		Cell c = (Cell)h.get(iLinkNuc);	
		//set active cell to end time to aid review
		if(c!=null) {
			System.out.println("not null so setting current cell "+c);
			iAceTree.setStartingCell(c,time);
		}

    }

    public boolean getAddKeyframeActive() {
    	return addKeyframeActive;
	}

	public void setiWarned(boolean b) {
    	iWarned.setSelected(b);
	}


 public final static String
       KEYFRAME = "Add Intermediate Cell"
      ,KEYFRAMEHEADER = "Add Cell as Intermediate (Optional)"
	  ,WARNING="Warning:Is early set correctly?"
	  ,DEACTIVATE="Deactivate"
	  ,SHORTCUTTRIGGER ="Short cut trigger";
}
