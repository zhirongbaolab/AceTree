package org.rhwlab.nucedit;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.nucedit.KillSublineage;
/*
 * hijacked to hold both its own functionality and kill sublineage
 */
public class KillDeepNucsDialog  extends JDialog implements ActionListener {

	AceTree			iAceTree;
	NucleiMgr		iNucleiMgr;
	int			iZLim;
	int			iCount;
	JLabel			iEstimatedCount;
	JLabel			iZLimLabel;
	JRadioButton		iFlippedImages;

        @SuppressWarnings("unused")
		public KillDeepNucsDialog(AceTree aceTree, Frame owner, boolean modal) {

        super(owner, modal);
        iAceTree = aceTree;
        iNucleiMgr = iAceTree.getNucleiMgr();
        setTitle("Kill Group");

        JDialog dialog = this;
        iZLim = 27;
        //estimateNucs(false);
        //iEstimatedCount = new JLabel(String.valueOf(iCount));
        JPanel bothTools=new JPanel();
    		bothTools.setLayout(new BoxLayout(bothTools,BoxLayout.PAGE_AXIS));
            JPanel pWhole = new JPanel();
       JLabel l=new JLabel("Kill Based On Z");
        l.setAlignmentX(CENTER_ALIGNMENT);	
        pWhole.add(l);
        pWhole.add(new JLabel(" "));
        pWhole.setLayout(new BoxLayout(pWhole, BoxLayout.PAGE_AXIS));
        Border blackline = BorderFactory.createLineBorder(Color.black);
        //Border empty = BorderFactory.createEmptyBorder();
        Border topBorder = BorderFactory.createEmptyBorder(10,0,0,0);
        Border botBorder = BorderFactory.createEmptyBorder(0,0,10,0);
       // pWhole.setBorder(blackline);
        JPanel p = new JPanel();
    
       //add zlimit setting controls
        p.setLayout(new GridLayout(1,0));
        p.add(new JLabel("Z Limit:"));
        iZLimLabel = new JLabel(String.valueOf(iZLim));
        p.add(iZLimLabel);
        JButton plus = new JButton("+");
        JButton minus = new JButton("-");
        plus.addActionListener(this);
        minus.addActionListener(this);
        p.add(plus);
        p.add(minus);
        pWhole.add(p);
        
        
        //add above or below
        /*
        p = new JPanel();
        iFlippedImages = new JCheckBox("Kill Low Z Values?");
        p.setLayout(new GridLayout(1,0));
        p.add(iFlippedImages);
        pWhole.add(p);
        estimateNucs(false);
        iEstimatedCount = new JLabel(String.valueOf(iCount));
        */
        p = new JPanel();
        ButtonGroup zDir=new ButtonGroup();
        JRadioButton zhigh = new JRadioButton("Above",true);
        iFlippedImages = new  JRadioButton("Below");//JCheckBox("Kill Low Z Values?");
        zDir.add(zhigh);
        zDir.add(iFlippedImages);

        p.setLayout(new GridLayout(1,0));
        p.add(new JLabel("Kill Cells:"));
        
        p.add(zhigh);
        p.add(iFlippedImages);
        pWhole.add(p);
        estimateNucs(false);
        iEstimatedCount = new JLabel(String.valueOf(iCount));

        
        //add estimate stuff
        p = new JPanel();
        p.setLayout(new GridLayout(1,0));
        JButton estimate = new JButton("Estimate");
        p.add(estimate);
        estimate.addActionListener(this);
        pWhole.add(p);


        p = new JPanel();
        p.setLayout(new GridLayout(1,0));
        p.add(new JLabel("Estimated count: "));
        p.add(iEstimatedCount);
        pWhole.add(p);

        //add kill button
        p = new JPanel();
        p.setLayout(new GridLayout(1,0));
        JButton killem = new JButton("Kill Cells");
        killem.addActionListener(this);
        p.add(killem);
        pWhole.add(p);

               
       bothTools.setOpaque(true); //content panes must be opaque
       JPanel group=new JPanel();
       group.setLayout(new BoxLayout(group,BoxLayout.LINE_AXIS));
        group.add(pWhole);
  	  group.add(Box.createHorizontalGlue());
        group.add((new JSeparator(SwingConstants.VERTICAL)));     
        group.add((new JSeparator(SwingConstants.VERTICAL))); 
        group.add(Box.createHorizontalGlue());
        //add kill sublineage tool
       group.add(new KillSublineage(aceTree));
      // JPanel dummy=new JPanel();
     //  dummy.setPreferredSize(new Dimension(50,10));
     //  group.add(dummy);
       bothTools.add(group);
       bothTools.add((new JSeparator(SwingConstants.HORIZONTAL))); 
       bothTools.add(new JLabel(" "));
       l=new JLabel("Warning: Kill Group tools are only for cleanup.");
		l.setAlignmentX(CENTER_ALIGNMENT);	
		bothTools.add(l);
		 l=new JLabel("Not recommended for editing.");
			l.setAlignmentX(CENTER_ALIGNMENT);	
			bothTools.add(l);
			
        dialog.setContentPane(bothTools);
        //dialog.setSize(new Dimension(200, 400));
        dialog.setLocationRelativeTo(owner);
        dialog.pack();
        dialog.setVisible(true);


	}

	private void estimateNucs(boolean implement) {
		println("estimateNucs, " + iZLim + CS + iCount + CS + implement);
		iCount = 0;
        Vector nucRec = iNucleiMgr.getNucleiRecord();
        for (int i=0; i < nucRec.size(); i++) {
        	Vector nuclei = (Vector)nucRec.get(i);
        	for (int j=0; j < nuclei.size(); j++) {
        		Nucleus n = (Nucleus)nuclei.get(j);
        		if (n.status == Nucleus.NILLI) continue;
        		if (n.z < iZLim && !iFlippedImages.isSelected()) continue;
        		if (n.z > iZLim && iFlippedImages.isSelected()) continue;
        		//println("killDeepNucs, " + i + CS + n);
        		if (implement) {
        			n.status = Nucleus.NILLI;
        		} else iCount++;
        	}
        }
        if (implement) {
            iAceTree.clearTree();
            iAceTree.buildTree(true);

            if (iAceTree.iAceMenuBar.view != null) {
                iAceTree.iAceMenuBar.view.rebuildData();
            }

            iAceTree.updateDisplay();

        }
		//println("estimateNucs, " + iZLim + CS + iCount);
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

		String c = e.getActionCommand();
		if (c.equals("+")) iZLim++;
		else if (c.equals("-")) iZLim--;
		else if (c.equals("Estimate")) {
			estimateNucs(false);
			iEstimatedCount.setText(String.valueOf(iCount));
		} else if (c.equals("Kill Cells")) {
			estimateNucs(true);
			iEstimatedCount.setText(String.valueOf(iCount));
		}

		iZLimLabel.setText(String.valueOf(iZLim));

        success = iAceTree.ATLockNucleiMgr(false);

	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

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
