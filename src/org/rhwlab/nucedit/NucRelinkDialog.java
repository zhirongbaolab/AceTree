/*
 * Created on Mar 29, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.nucedit;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.JSeparator;
import javax.swing.Box;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NucRelinkDialog extends JDialog implements ActionListener {
	protected AceTree 			iAceTree;
	protected static NucleiMgr 	iNucleiMgr;
	protected JLabel			iRelinkTime;
	protected JLabel 			iRelinkNuc;
	protected JLabel 			iLinkTime;
	protected JLabel 			iLinkNuc;
	//private JButton 			iDoit;
	private JButton 			iApplyAndRebuild;
	private JButton 			iApplyOnly;
	private JButton 			iRelinkButton;
	private JButton 			iLinkButton;
	private JButton 			iLinkRootButton;
	protected EditLog 			iEditLog;
	//private Log     iDLog;

	protected boolean iStartArmed=false;

	// constructor for subclass use is this even the right way to do this?
	protected NucRelinkDialog( Frame owner, boolean modal){
		super(owner,modal);
	}

	public NucRelinkDialog(AceTree aceTree, Frame owner, boolean modal, Cell cell, int time) {
		super(owner, modal);
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
		//fill UI in two chunks
		createEarlyPanel(pWhole);
		createLatePanel(pWhole,owner);
	}

	@SuppressWarnings("unused")
	protected void createLatePanel(JPanel pWhole, Frame owner){
		// later time
		Border blackline = BorderFactory.createLineBorder(Color.black);
		Border bothBorder = BorderFactory.createEmptyBorder(0,10,0,10);

		JPanel pEnd = new JPanel();
		pEnd.setLayout(new BoxLayout(pEnd, BoxLayout.PAGE_AXIS));
		pEnd.setAlignmentX(Component.CENTER_ALIGNMENT);

		iRelinkButton = new JButton(SETLATECELL);
		iRelinkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		iRelinkButton.addActionListener(this);

		JLabel label=new JLabel(LATEHEADER,SwingConstants.CENTER);
		label.setAlignmentX(CENTER_ALIGNMENT);
		pEnd.add(label);

		pEnd.add(iRelinkButton);
		  pEnd.add(Box.createVerticalGlue());

		JPanel bothl = new JPanel();
		bothl.setLayout(new BoxLayout(bothl,BoxLayout.LINE_AXIS));

		bothl.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel s = new JPanel();
		s.setLayout(new BoxLayout(s,BoxLayout.PAGE_AXIS));

		label = new JLabel(RELINKTIME);
		s.add(label);
		label = new JLabel(RELINKNUC);
		s.add(label);
	
		bothl.add(s);
		
		s = new JPanel();
		s.setLayout(new BoxLayout(s,BoxLayout.PAGE_AXIS));

		iRelinkTime = new JLabel(FIVE);

		s.add(iRelinkTime);
		iRelinkNuc = new JLabel(TWELVE);
		s.add(iRelinkNuc);

		bothl.add(s);
		pEnd.add(bothl);
		  pEnd.add(Box.createVerticalGlue());

		 pWhole.add(Box.createVerticalGlue());
		pWhole.add(pEnd);

		iApplyAndRebuild = new JButton(APPLYANDREBUILD);
		iApplyAndRebuild.setAlignmentX(Component.CENTER_ALIGNMENT);
		iApplyAndRebuild.addActionListener(this);
		  pWhole.add((new JSeparator(SwingConstants.HORIZONTAL))); 
		  pWhole.add(Box.createVerticalGlue());
				     
		pWhole.add(iApplyAndRebuild);

		//pWhole.add(s);

		pWhole.setOpaque(true); //content panes must be opaque
		this.setContentPane(pWhole);

		this.setLocationRelativeTo(owner);
		this.setVisible(true);

		//addKeyListener(new MyKeyListener());
		//setFocusableFalse();
		//setDefaultButtonBehavior(iLinkButton, "SPACE");
		//JButton [] jba = new JButton[4];
		//jba[0] = iLinkButton;
		//jba[1] = iRelinkButton;
		//jba[2] = iApplyOnly;
		//jba[3] = iApplyAndRebuild;
		//setKeyBehavior(jba, "ENTER");
		//iLinkButton.requestFocus();
		this.setSize(new Dimension(WIDTH, HEIGHT));
	}
	
	@SuppressWarnings("unused")
	protected void createEarlyPanel(JPanel pWhole){

		
		pWhole.setLayout(new BoxLayout(pWhole, BoxLayout.PAGE_AXIS));
		Border blackline = BorderFactory.createLineBorder(Color.black);
		
		//Border empty = BorderFactory.createEmptyBorder();
		Border bothBorder = BorderFactory.createEmptyBorder(0,10,0,10);
		//Border botBorder = BorderFactory.createEmptyBorder(0,0,10,0);

		//earlier time
		JPanel pStr = new JPanel();
		pStr.setLayout(new BoxLayout(pStr, BoxLayout.PAGE_AXIS));
		pStr.setAlignmentX(Component.CENTER_ALIGNMENT);
		//pStr.setBorder(blackline);

		iLinkButton = new JButton(SETEARLYCELL);
		iLinkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		iLinkButton.addActionListener(this);
		iLinkRootButton = new JButton(SETROOTCELL);
		iLinkRootButton.addActionListener(this);
		iLinkRootButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//JPanel s = new JPanel();
		//s.setLayout(new GridLayout(0,1));
		JLabel header=new JLabel(EARLYHEADER);
		header.setAlignmentX(Component.CENTER_ALIGNMENT);
		pStr.add(header);
		
		//s.setBorder(topBorder);
		pStr.add(iLinkButton);
		pStr.add(new JLabel("or"));
		pStr.add(iLinkRootButton);
		
		pStr.add(new JLabel(""));
		//pStr.add(s);
	  pStr.add(Box.createVerticalGlue());
	  
	  
		// name and time fields
		JPanel bothl=new JPanel();
		//bothl.setLayout(new GridLayout(2,2));
		bothl.setLayout(new BoxLayout(bothl,BoxLayout.LINE_AXIS));
		//bothl.setBorder(bothBorder);
		bothl.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel s = new JPanel();
		s.setLayout(new BoxLayout(s,BoxLayout.PAGE_AXIS));
		s.setBorder(bothBorder);
//		s.setAlignmentX(Component.CENTER_ALIGNMENT);
//		s.setLayout(new FlowLayout());
		JLabel label = new JLabel(LINKTIME);
	//	s.add(label);
		iLinkTime = new JLabel(FIVE);
		//s.add(iLinkTime);
			//bothl.add(s);
		//s = new JPanel();
		//s.setAlignmentX(Component.CENTER_ALIGNMENT);
		//s.setLayout(new FlowLayout());
		JLabel label2 = new JLabel(LINKNUC);
		iLinkNuc = new JLabel(TWELVE);
		//s.add(label);
		s.add(label);
		s.add(label2);
		bothl.add(s);
		s=new JPanel();
		s.setLayout(new BoxLayout(s,BoxLayout.PAGE_AXIS));
		s.add(iLinkTime);
		s.add(iLinkNuc);
		bothl.add(s);
		//bothl.add(s);
		pStr.add(bothl);
		
	 pStr.add(Box.createVerticalGlue());
		pStr.add(new JLabel(""));
		
		//pStr.setBorder(botBorder);
		//pStr.setPreferredSize(new Dimension(WIDTH,140));
		pWhole.add(pStr);
		pWhole.add(Box.createVerticalGlue());
	}

	private void setFocusableFalse() {
		iLinkTime.setFocusable(false);
		iLinkNuc.setFocusable(false);
		iLinkButton.setFocusable(false);
		iLinkRootButton.setFocusable(false);
		iRelinkTime.setFocusable(false);
		iRelinkNuc.setFocusable(false);
		iRelinkButton.setFocusable(false);
		iApplyAndRebuild.setFocusable(false);
		iApplyOnly.setFocusable(false);

	}

	@SuppressWarnings("unused")
	void setDefaultButtonBehavior(JButton jb, String key) {
		String s = key;
		Action home = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				println("focus to AceTree");
				iAceTree.requestFocus();
			}
		};
		//jb.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), "pressed");
		jb.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0, false), "pressed");
		jb.getActionMap().put("pressed", home );


	}

	void setKeyBehavior(JButton [] jba, String key) {
		String s = key;
		Action home = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Component compFocusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				if (compFocusOwner instanceof JButton) {
					println("its a button");
					((JButton)compFocusOwner).doClick();
				}
			}
		};
		for (int i=0; i < jba.length; i++) {
			jba[i].getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(s), s);
			jba[i].getActionMap().put(s, home );
		}


	}


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

		//System.out.println("NucRelinkDialog.actionPerformed");
		Object o = e.getSource();
		String cmd = e.getActionCommand();
		if (cmd.equals(SETEARLYCELL)) {
			int time = iAceTree.getImageTime();
			//System.out.println("Early cell time is: " + time);
			iLinkTime.setText(String.valueOf(time));
			iLinkNuc.setText(iAceTree.getCurrentCell().getName());
			iStartArmed=true;
		} else if (o == iLinkRootButton) {
			iLinkTime.setText(String.valueOf((iAceTree.getNucleiMgr()).getStartingIndex()));
			iLinkNuc.setText(AceTree.ROOTNAME);
		} else if (cmd.equals(SETLATECELL)) {
			int time = iAceTree.getImageTime();
			//System.out.println("Late cell time is: " + time);
			iRelinkTime.setText(String.valueOf(time));
			iRelinkNuc.setText(iAceTree.getCurrentCell().getName());

		} else if (cmd.equals(APPLYANDREBUILD) || cmd.equals(APPLYONLY)) {
			relinkAndRebuild();
		}
		iAceTree.requestFocus();
		// System.gc();

		success = iAceTree.ATLockNucleiMgr(false);

	}
	protected void relinkAndRebuild(){
		int endTime;
		try {
			endTime = Integer.parseInt(iRelinkTime.getText());
		} catch(NumberFormatException nfe) {
			showMessage("invalid relink time, aborting");
			return;
		}
		int strTime;
		try {
			strTime = Integer.parseInt(iLinkTime.getText());
		} catch(NumberFormatException nfe) {
			showMessage("invalid link time, aborting");
			return;
		}

		if (strTime > 1 && endTime <= strTime) {
			showMessage("endTime is not greater than start time, aborting");
			return;
		}

		String endCellName = iRelinkNuc.getText();
		String strCellName = iLinkNuc.getText();
		boolean b = checkCellValidities(endCellName, endTime, strCellName, strTime);

		if (!b) return;

		StringBuffer sb = new StringBuffer("RELINKING: ");
		sb.append(endTime);
		sb.append(CS + endCellName);
		sb.append(iNucleiMgr.getIndex(endCellName, endTime));
		sb.append(CS + strTime);
		sb.append(CS + strCellName);
		sb.append(iNucleiMgr.getIndex(strCellName, strTime));
		//System.out.println(sb.toString());
		iEditLog.append(sb.toString());
		
		//actual generation of interp
		createAndAddCells(endCellName, endTime, strCellName, strTime);
		
		//System.out.println("returned from createAndAddCells");
		// if (cmd.equals(APPLYANDREBUILD)) {
		//println("\n\nNucRelinkDialog.actionPerformed: applyAndRebuild");
		iAceTree.treeValueChangedFromEdit = true;
		iAceTree.clearTree();
		iAceTree.buildTree(true);

		// update WormGUIDES data if it's open
		if (iAceTree.iAceMenuBar.view != null) {
			iAceTree.iAceMenuBar.view.rebuildData();
		}

		AncesTree ances = iAceTree.getAncesTree();
		Hashtable h = ances.getCellsByName();


		Cell c = (Cell)h.get(strCellName);
		
		//set active cell to start time to aid review
		if(c != null) {
            System.out.println("Setting starting cell in relink " + c + " at startTime: " + strTime);
			iAceTree.treeValueChangedFromEdit = true;

            // make a call to the WormGUIDES window to rebuild it's scene. Sometimes, relinking throws WG into an error
			// so we'll rebuild this time
            if (iAceTree.iAceMenuBar.view != null) {
            	iAceTree.iAceMenuBar.view.buildScene();
			}

			/**
			 * the convention for relinking is to select the later, unnamed cell and then link it back to the earlier time.
			 * In this case, the time doesn't change because the first linked point is supposed to be shown when relinking.
			 * However, users can also relink in the forward direction i.e. select the earlier named cell first and walk
			 * forward to the unnamed cell. In this case, we end up viewing the later frame when applying the relink, so
			 * we want to show the first frame after that relink is made
			 */
			if (iAceTree.getImageManager().getCurrImageTime() != strTime) {
				iAceTree.getImageManager().setCurrImageTime(strTime);

				// also update the annotation
				iAceTree.iImgWin.updateCurrentCellAnnotation(c, new Cell(""), strTime);
			}
			iAceTree.treeValueChangedFromEdit = true;
			iAceTree.showSelectedCell(c, strTime);
			//iAceTree.updateDisplay();
		}
		iEditLog.setModified(true);
		//dispose();
		iRelinkNuc.setText(FIVE);
		iRelinkTime.setText(TWELVE);
		iLinkNuc.setText(strCellName);
		char x = endCellName.charAt(0);
		if (x != '_' && x != 'N') iLinkNuc.setText(endCellName);
		iLinkTime.setText(String.valueOf(endTime));
		// }
	}

	@SuppressWarnings("unused")
	private boolean checkCellValidities(String endCellName, int endTime, String strCellName, int strTime) {
		if (strCellName.equals(AceTree.ROOTNAME)) return true;
		Nucleus nEnd = iNucleiMgr.getCurrentCellData(endCellName, endTime);
		Nucleus nStr = iNucleiMgr.getCurrentCellData(strCellName, strTime);
		if (nEnd == null || nStr == null) {
			String s  = "";
			String s0 = "";
			String s1 = "";
			//s0 = "invalid cell: ";
			if (nEnd == null) s0 = "invalid cell: " + endCellName + CS + endTime + NL;
			if (nStr == null) s1 = "invalid cell: " + strCellName + CS + strTime + NL;
			showMessage(s0 + s1);
			return false;
		}
		if (nStr.successor2 > 0) {
			String s = "Cell " + strCellName + " already has 2 successors\n";
			s = s + "cannot complete relink.";
			showMessage(s);
			return false;
		}
		return true;
	}

	private void showMessage(String s) {
		JOptionPane pane = new JOptionPane(s);
		JDialog dialog = pane.createDialog(iAceTree, "About AceTree");
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	public static void createAndAddCells(String endCellName, int endTime, String strCellName, int strTime) {
		// access nucleus record of end and start cells
		println("createAndAddCells, " + endCellName + CS + endTime + CS + strCellName + CS + strTime);
		Nucleus nEnd = getNucleus(endCellName, endTime);
		if (strCellName.equals(AceTree.ROOTNAME)) {
			nEnd.predecessor = Nucleus.NILLI;
			return;
		}
		//System.out.println("endCell: " + endCellName + CS + endTime);
		//System.out.println("nEnd: " + nEnd);
		//System.out.println("startCell: " + strCellName + CS + strTime);
		Nucleus nStr = getNucleus(strCellName, strTime);
		//System.out.println("actionPerformed: nStr: " + nStr);
		
		// nuclei_record accessed and modified here
		Vector nuclei_record = iNucleiMgr.getNucleiRecord();
		Vector nucleiAdd = null;
		Nucleus n = nStr;
		int predecessor = nStr.index;
		for (int k = strTime + 1; k < endTime; k++) {
			nucleiAdd = (Vector)nuclei_record.elementAt(k - 1);
			n = interpolateNucleus(nEnd, nStr, endTime, strTime, k);
			n.index = nucleiAdd.size() + 1;
			//n.snindex = n.index;
			n.predecessor = predecessor;
			predecessor = n.index;
			//System.out.println("adding: " + n);
			//iEditLog.append("adding: " + n.toString());
			nucleiAdd.add(n);
		}
		nEnd.predecessor = n.index;
		nEnd.rwraw = 1;
		n.rwraw = 1;
		//System.out.print("nEnd: " + nEnd);
	}

	private static Nucleus getNucleus(String name, int time) {
		System.out.println("getNucleus, seeming: " + name + CS + time);
		Nucleus nRtn = null;
		Nucleus n = null;
		Vector nuclei_record = iNucleiMgr.getNucleiRecord();
		Vector nuclei = (Vector)nuclei_record.elementAt(time - 1);
		for (int j=0; j < nuclei.size(); j++) {
			n = (Nucleus)nuclei.elementAt(j);

			if (n.status > 0 && n.identity.equals(name)) {
				nRtn = n;
				break;
			}
		}
		return nRtn;
	}

	private static Nucleus interpolateNucleus(Nucleus nEnd, Nucleus nStr, int endTime, int strTime, int midTime) {
		Nucleus n = nStr.copy();
		int deltaT = endTime - strTime;
		int deltaM = midTime - strTime;
		n.x = (nEnd.x - nStr.x)*deltaM/deltaT + nStr.x;
		n.y = (nEnd.y - nStr.y)*deltaM/deltaT + nStr.y;
		n.z = (nEnd.z - nStr.z)*deltaM/deltaT + nStr.z;
		n.size = (nEnd.size - nStr.size)*deltaM/deltaT + nStr.size;
		return n;
	}

	public class MyKeyListener extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent ke){
			char i = ke.getKeyChar();
			String str = Character.toString(i);
			println("MyKeyListener, " + str);
			//iAceTree.requestFocus();
		}
	}

	protected class WindowEventHandler extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			iAceTree.iNucRelinkDialog = null;
			println("NucRelinkDialog.windowclosing");
		}

	}
	public final static int
	WIDTH=230
	,HEIGHT=400;

	public final static String
	TITLE = "Edit Track"
		,RELINKTIME = "Timepoint "
			,RELINKNUC = "Cell Name  "
				,LINKTIME = "Timepoint"
					,LINKNUC = "Cell Name"
						,DOIT = "Apply"
							,CS = ", "
								,NL = "\n"
									,APPLYANDREBUILD = "Apply"
										,APPLYONLY = "old apply only"
											,SETCURRENTCELL = "set current cell"
												,SETEARLYCELL = "Use Active Cell"
													,SETLATECELL = "Use Active Cell "
														,SETROOTCELL = "Use ROOT Cell"
															,LATER = "Link Cell at Later Time"
																,EARLIER = "to Cell at Earlier Time"
																	,FIVE = "     "
																		,TWELVE = "            "
																			,LATEHEADER="Choose Cell as Late Timepoint"
																				,EARLYHEADER="Choose Cell as Early Timepoint"
																					;

	private static void println(String s) {System.out.println(s);}
	private static void print(String s) {System.out.print(s);}
	private static final String TAB = "\t";
	private static final DecimalFormat DF0 = new DecimalFormat("####");
	private static final DecimalFormat DF1 = new DecimalFormat("####.#");
	private static final DecimalFormat DF4 = new DecimalFormat("####.####");
	private static String fmt4(double d) {return DF4.format(d);}
	private static String fmt1(double d) {return DF1.format(d);}
	private static String fmt0(double d) {return DF0.format(d);}
}
