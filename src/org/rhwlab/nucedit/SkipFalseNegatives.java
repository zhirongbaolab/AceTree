/*
 * Created on Jul 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.nucedit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.Log;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SkipFalseNegatives extends JDialog implements ActionListener {

    private AceTree iAceTree;
    private NucleiMgr iNucleiMgr;
    private JTextField iRelinkTime;
    private JTextField iRelinkNuc;
    private JTextField iLinkTime;
    private JTextField iLinkNuc;
    private JButton iDoit;
    private JButton iApplyAndRebuild;
    private JButton iApplyOnly;
    //private JButton iRelinkButton;
    private JButton iLinkButton;
    private JButton iSkipFalseNegativesButton;
    private JButton iContinueButton;
    private EditLog iEditLog;
    private Log     iDLog;
    private int     iStrTime; // state variable for skip..continue functionality
    
    @SuppressWarnings("unused")
	public SkipFalseNegatives () {
            super(AceTree.getAceTree(null).getMainFrame(), false);
            iAceTree = AceTree.getAceTree(null);
            iNucleiMgr = iAceTree.getNucleiMgr();
            iEditLog = iAceTree.getEditLog();
            iDLog = iAceTree.getDebugLog();
            setTitle(TITLE);
            
            JDialog dialog = this;
            JPanel pWhole = new JPanel();
            pWhole.setLayout(new BoxLayout(pWhole, BoxLayout.PAGE_AXIS));
            Border blackline = BorderFactory.createLineBorder(Color.black);
            //Border empty = BorderFactory.createEmptyBorder();
            Border topBorder = BorderFactory.createEmptyBorder(10,0,0,0);
            Border botBorder = BorderFactory.createEmptyBorder(0,0,10,0);
            
            JPanel labelAtTop = new JPanel();
            labelAtTop.setLayout(new GridLayout(2,1)); //labelAtTop, BoxLayout.PAGE_AXIS));
            labelAtTop.setBorder(blackline);
            JLabel topLab = new JLabel(EARLIER);
            Font f = topLab.getFont();
            //iDLog.append("NucRelinkDialog: " + f.getName() + CS + f.getStyle() + CS + f.getSize());
            int size = (int)(f.getSize() * 1.3);
            //iDLog.append("NucRelinkDialog: " + size);
            
            Font f2 = new Font(f.getName(), f.getStyle(), size);
            topLab.setFont(f2);
            labelAtTop.add(topLab);
            JLabel botLab = new JLabel(LATER);
            botLab.setFont(f2);
            labelAtTop.add(botLab);
            pWhole.add(labelAtTop);
            
            
            //earlier time
            JPanel pStr = new JPanel();
            pStr.setLayout(new BoxLayout(pStr, BoxLayout.PAGE_AXIS));
            pStr.setBorder(blackline);
            JPanel s = new JPanel();
            s.setLayout(new FlowLayout());
            JLabel label = new JLabel(LINKTIME);
            s.add(label);
            iLinkTime = new JTextField();
            iLinkTime.setColumns(5);
            s.add(iLinkTime);
            s.setBorder(topBorder);
            pStr.add(s);

            s = new JPanel();
            s.setLayout(new FlowLayout());
            label = new JLabel(LINKNUC);
            s.add(label);
            iLinkNuc = new JTextField();
            iLinkNuc.setColumns(12);
            s.add(iLinkNuc);
            s.setBorder(botBorder);
            pStr.add(s);
            iLinkButton = new JButton(SETCURRENTCELL);
            iLinkButton.addActionListener(this);
            s = new JPanel();
            s.setLayout(new GridLayout(1,1));
            s.setBorder(topBorder);
            s.add(iLinkButton);
            pStr.add(s);
            pWhole.add(pStr);
            
            // later time
            JPanel pEnd = new JPanel();
            pEnd.setLayout(new BoxLayout(pEnd, BoxLayout.PAGE_AXIS));
            pEnd.setBorder(blackline);
            s = new JPanel();
            s.setLayout(new FlowLayout());
            label = new JLabel(RELINKTIME);
            s.add(label);
            iRelinkTime = new JTextField();
            iRelinkTime.setColumns(5);
            //iRelinkTime.setText(String.valueOf(time));
            s.setBorder(topBorder);
            s.add(iRelinkTime);
            pEnd.add(s);

            s = new JPanel();
            s.setLayout(new FlowLayout());
            label = new JLabel(RELINKNUC);
            s.add(label);
            iRelinkNuc = new JTextField();
            iRelinkNuc.setColumns(12);
            //iRelinkNuc.setText(cell.getName());
            s.add(iRelinkNuc);
            s.setBorder(botBorder);
            pEnd.add(s);
            //iRelinkButton = new JButton(SETCURRENTCELL);
            //iRelinkButton.addActionListener(this);
            iSkipFalseNegativesButton = new JButton(SKIPFALSENEGS);
            iSkipFalseNegativesButton.addActionListener(this);
            iContinueButton = new JButton(CONTINUESKIPPING);
            iContinueButton.addActionListener(this);
            s = new JPanel();
            s.setLayout(new GridLayout(0,1));
            s.setBorder(topBorder);
            //s.add(iRelinkButton);
            s.add(iSkipFalseNegativesButton);
            s.add(iContinueButton);
            pEnd.add(s);
            pWhole.add(pEnd);
            
            /*
            s = new JPanel();
            s.setLayout(new FlowLayout());
            iDoit = new JButton(DOIT);
            iDoit.addActionListener(this);
            s.add(iDoit);
            p.add(s);
            */

            JPanel xp = new JPanel();
            xp.setBorder(blackline);
            xp.setLayout(new BoxLayout(xp, BoxLayout.PAGE_AXIS));
            Border b = BorderFactory.createEmptyBorder(10,0,10,0);
            s = new JPanel();
            //s.setBorder(blackline);
            s.setLayout(new GridLayout(3,1));
            s.add(new JLabel(""));
            iApplyAndRebuild = new JButton(APPLYANDREBUILD);
            iApplyAndRebuild.addActionListener(this);
            s.add(iApplyAndRebuild);
            iApplyOnly = new JButton(APPLYONLY);
            iApplyOnly.addActionListener(this);
            s.add(iApplyOnly);
            //xp.add(s);
            pWhole.add(s);

            pWhole.setOpaque(true); //content panes must be opaque
            dialog.setContentPane(pWhole);
            dialog.setSize(new Dimension(220, 400));
            dialog.setLocationRelativeTo(AceTree.getAceTree(null).getMainFrame());
            dialog.setVisible(true);

        
        }
        
    
    
    // fresh = true when we start skipping
    // fresh = false if we continue past a proposed end point
    private void skipFalseNegatives(boolean fresh) {
        int strTime;
        int strIncrement = 0;
        
        if (fresh) {
            try {
                iStrTime = Integer.parseInt(iLinkTime.getText());
            } catch(NumberFormatException nfe) {
                showMessage("invalid link time, aborting");
                return;
            }
        } else {
            try {
                strIncrement = Integer.parseInt(iRelinkTime.getText()) + 1 - iStrTime;
            } catch(NumberFormatException nfe) {
                showMessage("invalid relink time, aborting");
                return;
            }
        }
        strTime = iStrTime;
        String strCellName = iLinkNuc.getText();
        if (fresh) {
            Cell c = iAceTree.getCellByName(strCellName);
            strTime = c.getEndTime();
        }
        iLinkTime.setText(String.valueOf(strTime));
        if (fresh) {
            boolean b = checkStartingCellValidity(strCellName, strTime);
            if (!b) return;
        }
        
        Nucleus n = iNucleiMgr.getCurrentCellData(strCellName, strTime);
        if (n == null) return;
        int endingIndex = iAceTree.getConfig().getNucleiConfig().getEndingIndex();
        Nucleus nc = null;
        int i;
        for (i = strTime + 1 + strIncrement; i <= endingIndex; i++) {
            nc = iNucleiMgr.findClosestNucleusXYZ(n.x, n.y, n.z, i);
            if (nc != null) break;
        }
        if (nc == null) {
            showMessage("nothing found");
            return;
        }
        
        
        Cell cc = (Cell)iAceTree.getAncesTree().getCellsByName().get(nc.identity);
        iAceTree.setCurrentCell(cc, i, AceTree.CONTROLCALLBACK);
        iAceTree.updateDisplay();

        iRelinkTime.setText(String.valueOf(i));
        iRelinkNuc.setText(nc.identity);
    
    }
    
    @Override
	public void actionPerformed(ActionEvent e) {
        //System.out.println("NucRelinkDialog.actionPerformed");
        Object o = e.getSource();
        if (o == iLinkButton) {
            //iAceTree.forceTrackingOn();
            int time = iAceTree.getImageManager().getCurrImageTime();
            iLinkTime.setText(String.valueOf(time));
            iLinkNuc.setText(iAceTree.getCurrentCell().getName());
        } else if (o == iSkipFalseNegativesButton) {
            skipFalseNegatives(true);
        } else if (o == iContinueButton) {
            skipFalseNegatives(false);
        } else if (o == iApplyAndRebuild || o == iApplyOnly) {
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
            
            if (endTime <= strTime) {
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
            System.out.println(sb.toString());
            iEditLog.append(sb.toString());
            
            iNucleiMgr.makeBackupNucleiRecord();
            createAndAddCells(endCellName, endTime, strCellName, strTime);
            if (o == iApplyAndRebuild) {
                iAceTree.clearTree();
                iAceTree.buildTree(true);

                // update WormGUIDES data if it's open
                if (iAceTree.iAceMenuBar.view != null) {
                    iAceTree.iAceMenuBar.view.rebuildData();
                }

                AncesTree ances = iAceTree.getAncesTree();
                Hashtable h = ances.getCellsByName();
                Cell c = (Cell)h.get(strCellName);
                iAceTree.setStartingCell(c, LARGETIME);
                iEditLog.setModified(true);
                iAceTree.updateDisplay();
            }
        }
    }
            
    @SuppressWarnings("unused")
	private boolean checkStartingCellValidity(String strCellName, int strTime) {
        Nucleus nStr = iNucleiMgr.getCurrentCellData(strCellName, strTime);
        if (nStr == null) {
            String s  = "";
            String s0 = "";
            String s1 = "";
            //s0 = "invalid cell: ";
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
    
    @SuppressWarnings("unused")
	private boolean checkCellValidities(String endCellName, int endTime, String strCellName, int strTime) {
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
    
    private void createAndAddCells(String endCellName, int endTime, String strCellName, int strTime) {
        // access nucleus record of end and start cells
        //System.out.println("createAndAddCells");
        Nucleus nEnd = getNucleus(endCellName, endTime);
        //System.out.println("endCell: " + endCellName + CS + endTime);
        //System.out.println("nEnd: " + nEnd);
        //System.out.println("startCell: " + strCellName + CS + strTime);
        Nucleus nStr = getNucleus(strCellName, strTime);
        //System.out.println("nStr: " + nStr);
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
            iEditLog.append("adding: " + n.toString());
            nucleiAdd.add(n);
        }
        nEnd.predecessor = n.index;
        //System.out.print("nEnd: " + nEnd);
    }
    
    private Nucleus getNucleus(String name, int time) {
        //System.out,println("seeming: " + name + CS + time);
        Nucleus n = null;
        Vector nuclei_record = iNucleiMgr.getNucleiRecord();
        Vector nuclei = (Vector)nuclei_record.elementAt(time - 1);
        for (int j=0; j < nuclei.size(); j++) {
            n = (Nucleus)nuclei.elementAt(j);
            
            if (n.identity.equals(name)) break;
        }
        return n;
    }
    
    private Nucleus interpolateNucleus(Nucleus nEnd, Nucleus nStr, int endTime, int strTime, int midTime) {
        Nucleus n = nStr.copy();
        int deltaT = endTime - strTime;
        int deltaM = midTime - strTime;
        n.x = (nEnd.x - nStr.x)*deltaM/deltaT + nStr.x;
        n.y = (nEnd.y - nStr.y)*deltaM/deltaT + nStr.y;
        n.z = (nEnd.z - nStr.z)*deltaM/deltaT + nStr.z;
        n.size = (nEnd.size - nStr.size)*deltaM/deltaT + nStr.size;
        return n;
    }
    
    private final static String
    TITLE = "Skip False Negatives"
   ,RELINKTIME = "Later time"
   ,RELINKNUC = "Cell name"
   ,LINKTIME = "Earlier time"
   ,LINKNUC = "Cell name"
   ,DOIT = "Apply"
   ,CS = ", "
   ,NL = "\n"
   ,APPLYANDREBUILD = "apply/rebuild"
   ,APPLYONLY = "apply only"
   ,SETCURRENTCELL = "set current cell"
   ,SKIPFALSENEGS = "skip false negatives"
   ,CONTINUESKIPPING = "continue skipping"
   ,LATER = "cell at earlier time"
   ,EARLIER = "search for ancesters of";
   ;

   private static final int
        LARGETIME = 1000
       ;
            
}
