/*
 * Created on Apr 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.nucedit;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;

/**
 * @author santella
 *
 * based on killcellsdialog this is a panel that implements killing an entire sublineage.
 */
public class KillSublineage extends JPanel implements ActionListener {
	Vector 				iCandidateCells;
	int 				iTime;
	String 				iCellName;
	Cell iCellToKill;
	AceTree iAceTree;
	EditLog iEditLog;
	NucleiMgr iNucleiMgr;
	/**
	 * @param aceTree
	 * //@param owner
	 * //@param modal
	 */
	@SuppressWarnings("unused")
	public KillSublineage(AceTree aceTree) {
		iAceTree=aceTree;
		this.setAlignmentX(CENTER_ALIGNMENT);	
		iNucleiMgr = aceTree.getNucleiMgr();
    	iEditLog = aceTree.getEditLog();
    	int time =  iAceTree.getImageManager().getCurrImageTime();

        Cell cell = iAceTree.getCurrentCell();
        iCellToKill=cell;
       
		if (cell!=null)
			iCellName = cell.getName();
		else
			iCellName="";
		iEditLog = iNucleiMgr.getEditLog();
		iCandidateCells = new Vector();
		iTime = time;
	     Border blackline = BorderFactory.createLineBorder(Color.black);
	   //  this.setBorder(blackline);

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		JLabel l=new JLabel("Kill Sublineage");
		l.setAlignmentX(CENTER_ALIGNMENT);	
		this.add(l);
		
		l=new JLabel(" ");
		l.setAlignmentX(CENTER_ALIGNMENT);	
		this.add(l);
		
		l = new JLabel("current Cell: "+iCellName);
		l.setAlignmentX(CENTER_ALIGNMENT);	
		this.add(l);

		//int count=estimateSublineage();
		JLabel lestimate = new JLabel("Estimated Count: "+"0");
		lestimate.setAlignmentX(CENTER_ALIGNMENT);	
		this.add(lestimate);
	
		
		JButton killem = new JButton("Kill Sublineage");
		killem.setAlignmentX(CENTER_ALIGNMENT);	
		killem.addActionListener(this);
		this.add(Box.createVerticalGlue());   // allocate space between estimate and button
		this.add(killem);



		// put starting cell on the list and then look for more
		// up to the point where the name changes
		//System.out.println("KillCellsDialog: " + iNucleiMgr + CS + iTime);
		Vector nuclei = iNucleiMgr.getNucleiRecord().elementAt(iTime - 1);
		iCandidateCells = new Vector();
		iCandidateCells.add(nuclei);
		int nmax; 
		int estimate=0;
		if(cell!=null){
			estimate=collectCandidateCells();
			nmax= iCandidateCells.size();}
		else
			nmax=0;

		lestimate.setText("Estimate: "+estimate);

	}

	@SuppressWarnings("unused")
	private int collectCandidateCells() {
		int k = iTime; // the first time is already in there
		int count = 1;
		int estimate=1;
		boolean found = true;
		String name = iCellToKill.getName();
		Nucleus n = null;
		Vector nuclei = null;
		Vector nuclei_record = iNucleiMgr.getNucleiRecord();
		while (found && k < nuclei_record.size()) {
			nuclei = (Vector)nuclei_record.elementAt(k++);
			found = false;
			boolean foundonce=false;
			for (int j=0; j < nuclei.size(); j++) {
				n = (Nucleus)nuclei.elementAt(j);
				if (!n.identity.contains(name)) {
					estimate++;
					if(!foundonce)
						iCandidateCells.add(nuclei);
					foundonce=true;
					found = true;
					}
			}
			count++;
		}
		return estimate;
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

		if(iNucleiMgr.getCurrentCellData(iCellName, iTime)==null){
			System.out.println("Attempt to delete nonexistent cell quitting");
			return;
		}



		Object o = e.getSource();
		
//bunch of stuff for edit log
	
		String indexString = iNucleiMgr.getIndex(iCellName, iTime);
		//String indexString = " (" + index + ") ";
		iEditLog.appendx("KILLING Sublineage" + iCellName + indexString + " at time " + iTime);
		int k = iCandidateCells.size();
		/*
		//
		if (k == 1) iEditLog.append(".");
		else {
			iEditLog.appendx(" and following ");
			if (k == 2) iEditLog.append("time." + iNucleiMgr.getIndex(iCellName, iTime + 1));
			else {
				int km = k - 1;
				String s = km + " times ";
				for (int i = 0; i < km; i++) {
					s += iNucleiMgr.getIndex(iCellName, iTime + i + 1);

				}
				iEditLog.append(s);
			}
		}
		*/
		//finding its predecessor
		iNucleiMgr.makeBackupNucleiRecord();
		Nucleus predecessorNuc = null;
		if (iTime > 1) {
			Nucleus nc = iNucleiMgr.getCurrentCellData(iCellName, iTime);
			Vector predNuclei = iNucleiMgr.getNucleiRecord().elementAt(iTime - 2);
			if (nc.predecessor > 0) {
				predecessorNuc = (Nucleus)predNuclei.elementAt(nc.predecessor - 1);
			}
		}
		
		//int namingMethod = AceTree.getAceTree(null).getNucleiMgr().getIdentity().getNamingMethod();
		//actual deletion
		for (int i=0; i < k; i++) {
			Vector nuclei = (Vector)iCandidateCells.elementAt(i);
			Nucleus n = null;

			for (int j=0; j < nuclei.size(); j++) {
				n = (Nucleus)nuclei.elementAt(j);
				if (!n.identity.contains(iCellName)) continue;
				n.status = Nucleus.NILLI;
				n.identity = "";
				n.assignedID = "";

			}

		}
		Cell c = null;
		int strTime = iTime - 1;
		if (predecessorNuc != null) {
			AncesTree ances = iAceTree.getAncesTree();
			Hashtable h = ances.getCellsByName();
			c = (Cell)h.get(predecessorNuc.identity);
		}

				iAceTree.clearTree();
			iAceTree.buildTree(true);

			// update WormGUIDES data if it's open
			if (iAceTree.iAceMenuBar.view != null) {
				iAceTree.iAceMenuBar.view.rebuildData();
			}

			iEditLog.setModified(true);
			AncesTree ances = iAceTree.getAncesTree();
			Hashtable h = ances.getCellsByName();
			if (c != null) c = (Cell)h.get(c.getName());
			System.out.println("killSublineage.actionPerformed: " + c + CS + strTime);
			if (c != null) {
				iAceTree.setStartingCell(c, strTime);
				iAceTree.updateDisplay();
			}

			success = iAceTree.ATLockNucleiMgr(false);

    }

		   private static final String CS = ", ";
		    private static final String TAB = "\t";


}
