/*
 * Created on Apr 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.nucedit;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.*;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class KillCellsDialog extends GenericDialog {
    JTextField 			iCellToKill;
    JTextField 			iKillTime;
    JRadioButton 		iOneCell;
    JRadioButton 		iSeveralCells;
    JLabel 				iCellCount;
    //JSpinner       		iCellsToKill;
   JTextField    		iCellsToKill;
    Vector 				iCandidateCells;
    int 				iTime;
    String 				iCellName;
    EditLog 			iEditLog;
    int					iNumCellsToEnd;
    JButton				iUseAll;
    //SpinnerNumberModel	iSNModel;
    //NucleiMgr iNucleiMgr;


    /**
     * @param aceTree
     * @param owner
     * @param modal
     */
    public KillCellsDialog(AceTree aceTree, Frame owner, boolean modal,
            Cell cell, int time, EditLog editLog) {
        super(aceTree, owner, modal);
        
     	if (cell!=null)
                iCellName = cell.getName();
     	else
                iCellName="";

     	System.out.println("Attempting to kill cell: " + iCellName);
     	
        //Border blackline = BorderFactory.createLineBorder(Color.black);
        iEditLog = iNucleiMgr.getEditLog();
        iCandidateCells = new Vector();
        iTime = time;
       
        setTitle(TITLE);
        iContentPanel.setLayout(new BoxLayout(iContentPanel, BoxLayout.PAGE_AXIS));
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(1,0));
       // p.setBorder(blackline);
        JLabel test = new JLabel(CELLTOKILL);
        p.add(test);
        iCellToKill = new JTextField();
        iCellToKill.setColumns(12);
        //iCellToKill.setMaximumSize(new Dimension(20, 200));
        iCellToKill.setText(iCellName);
        p.add(iCellToKill);
        iContentPanel.add(p);
        p = new JPanel();
        p.setLayout(new GridLayout(1,0));
        //p.setBorder(blackline);
        test = new JLabel(KILLTIME);
        //iContentPanel.add(test);
        iKillTime = new JTextField();
        iKillTime.setColumns(12);
        iKillTime.setText(String.valueOf(time));
        //iContentPanel.add(iKillTime);
        p.add(test);
        p.add(iKillTime);
        iContentPanel.add(p);

        JPanel choices = new JPanel();
        choices.setLayout(new GridLayout(1,0));
        //choices.setBorder(blackline);
      
        // put starting cell on the list and then look for more
        // up to the point where the name changes
        System.out.println("KillCellsDialog: " + iNucleiMgr + CS + iAceTree.getImageManager().getCurrImageTime());
        Vector nuclei = iNucleiMgr.getNucleiRecord().elementAt(iAceTree.getImageManager().getCurrImageTime() - 1);
        iCandidateCells = new Vector();
        iCandidateCells.add(nuclei);
        int nmax; 
        if(cell!=null){
        	collectCandidateCells();
        	nmax= iCandidateCells.size();}
        else
        	nmax=0;
        
        iNumCellsToEnd = nmax;
        //System.out.println("at time: " + iTime);
        //System.out.println("killable: " + nmax);
        iCellCount = new JLabel(NUMBER);
        choices.add(iCellCount);
        //iSNModel = new SpinnerNumberModel(1, 0, nmax, 1);
        //iCellsToKill = new JSpinner(iSNModel);
        iCellsToKill=new JTextField();
        iCellsToKill.setColumns(12);
        iCellsToKill.setText(String.valueOf(time));
        choices.add(iCellsToKill);

        iUseAll = new JButton("Use All");
        iUseAll.setAlignmentX(Component.CENTER_ALIGNMENT);
        //iUseAll.setPrefferedWidth(WIDTH);
        iUseAll.addActionListener(this);
        //choices.add(iUseAll);

        iContentPanel.add(choices);
        iContentPanel.add(iUseAll);
        this.pack();
        //setSize(new Dimension(WIDTH, HEIGHT));
        setVisible(true);

    }

    private void collectCandidateCells() {
        int k = iTime; // the first time is already in there
        int count = 1;
        boolean found = true;
        String name = iCellToKill.getText();
        Nucleus n = null;
        Vector nuclei = null;
        Vector nuclei_record = iNucleiMgr.getNucleiRecord();
        while (found && (count < MAX_KILLS) && k < nuclei_record.size()) {
            nuclei = (Vector)nuclei_record.elementAt(k++);
            found = false;
            for (int j=0; j < nuclei.size(); j++) {
                n = (Nucleus)nuclei.elementAt(j);
                if (!n.identity.equals(name)) continue;
                if (n.status <= 0) break;
                iCandidateCells.add(nuclei);
                //n.status = Nucleus.NILLI;
                found = true;
            }
            count++;
        }

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

    	//reparse candidates at beginning to double check if user has modified text fields
    	// for cell name or start time
    	iCellName=iCellToKill.getText();
    	iTime=Integer.parseInt(iKillTime.getText());
    	Vector nuclei = iNucleiMgr.getNucleiRecord().elementAt(iAceTree.getImageManager().getCurrImageTime() - 1);
    	iCandidateCells = new Vector();
    	iCandidateCells.add(nuclei);
       	collectCandidateCells();
    	iNumCellsToEnd= iCandidateCells.size();
    	System.out.println("Parsed successors for "+ iCellName +" at time "+ iTime +" found "+ iNumCellsToEnd);
    	
    	if(iNucleiMgr.getCurrentCellData(iCellName, iTime)==null){
    		System.out.println("Attempt to delete nonexistent cell quitting");
    		return;
    	}
    		
    	
    	
        Object o = e.getSource();
        if (o == iUseAll) {
        	//Object oo = iSNModel.getValue();
        	//println("actionPerformed, " + oo);
        	//iSNModel.setValue(new Integer(iNumCellsToEnd));
        	//start time
        	Integer xx2=Integer.valueOf(iKillTime.getText());
        	
        	iCellsToKill.setText(String.valueOf(xx2.intValue()+ iNumCellsToEnd-1));
        	//println("actionPerformed, " + oo);
        	return;
        }

       // Integer xx = (Integer)iCellsToKill.getValue();
        Integer xx = Integer.valueOf(iCellsToKill.getText());
        Integer xx2 = Integer.valueOf(iKillTime.getText());
        // change k to be end minus start
        int k = xx.intValue()-xx2.intValue()+1;
        if (k == 0||k>iNumCellsToEnd) {
        	System.out.println("Out of range values provided for cell deletion");
        	return;
        }
        //System.out.println("actionPerformed: " + x);
        // locate the cell to start at after a rebuild
        String indexString = iNucleiMgr.getIndex(iCellName, iTime);
        //String indexString = " (" + index + ") ";
        iEditLog.appendx("KILLING " + iCellName + indexString + " at time " + iTime);
        //int k = iCandidateCells.size();
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
        iNucleiMgr.makeBackupNucleiRecord();
        Nucleus predecessorNuc = null;
        if (iTime > 1) {
            Nucleus nc = iNucleiMgr.getCurrentCellData(iCellName, iTime);
            Vector predNuclei = iNucleiMgr.getNucleiRecord().elementAt(iTime - 2);
            if (nc.predecessor > 0) {
                predecessorNuc = (Nucleus)predNuclei.elementAt(nc.predecessor - 1);
            }
        }
        int namingMethod = AceTree.getAceTree(null).getConfig().getNucleiConfig().getNamingMethod();
        for (int i=0; i < k; i++) {
            nuclei = (Vector)iCandidateCells.elementAt(i);
            Nucleus n = null;
            //Vector indices = new Vector();
            for (int j=0; j < nuclei.size(); j++) {
                n = (Nucleus)nuclei.elementAt(j);
                if (!n.identity.equals(iCellName)) 
                	continue;
                n.status = Nucleus.NILLI;
                n.rwraw = 1;
                n.identity = "";
                n.assignedID = "";
                //indices.add(new Integer(j));
            }
        }
        Cell c = null;
        int strTime = iTime - 1;
        if (predecessorNuc != null) {
            AncesTree ances = iAceTree.getAncesTree();
            Hashtable h = ances.getCellsByName();
            c = (Cell)h.get(predecessorNuc.identity);
        }

        if (o == iApplyAndRebuild) {

            iAceTree.treeValueChangedFromEdit = true;
            iAceTree.clearTree();
            iAceTree.buildTree(true);
            iEditLog.setModified(true);

            if (iAceTree.iAceMenuBar.view != null) {
                iAceTree.iAceMenuBar.view.rebuildData();
            }

            AncesTree ances = iAceTree.getAncesTree();
            Hashtable h = ances.getCellsByName();

            if (c != null)
            	c = (Cell)h.get(c.getName());

            System.out.println("killCellsDialog.actionPerformed: " + c + CS + strTime);

            if (c == null) {
	            // Try to get any existing cell at strTime
	            Vector newtime = iNucleiMgr.getNuclei(strTime-1);
	            // Just use the first nucleus at that timepoint
	            Nucleus newnuc = (Nucleus)newtime.get(0);
	            c = (Cell)iAceTree.getAncesTree().getCells().get(newnuc.hashKey);
            }
            iAceTree.treeValueChangedFromEdit = true;
            iAceTree.setStartingCell(c, strTime);



            iAceTree.updateDisplay();
            dispose();
        } else super.actionPerformed(e);

        success = iAceTree.ATLockNucleiMgr(false);

    }

    private final static int
    WIDTH = 200
   ,HEIGHT = 225
   ,MAX_KILLS = 999
   ;

    public final static String
         TITLE = "Kill Cell"
        ,ONECELL = "one cell"
        ,SEVERAL = "several cells"
        ,KILLABLE = " are killable"
        ,CELLTOKILL = "Cell"
        ,KILLTIME = "Start Time"
        ,NUMBER = "End Time"
        ,SPACER = "     "
        ;

    public static void main(String[] args) {}
    private static void println(String s) {System.out.println(s);}
    private static void print(String s) {System.out.print(s);}
    private static final String CS = ", ";
    private static final String TAB = "\t";
    private static final DecimalFormat DF0 = new DecimalFormat("####");
    private static final DecimalFormat DF1 = new DecimalFormat("####.#");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt4(double d) {return DF4.format(d);}
    private static String fmt1(double d) {return DF1.format(d);}
    private static String fmt0(double d) {return DF0.format(d);}
}
