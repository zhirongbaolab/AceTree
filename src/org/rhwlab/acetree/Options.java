/*
 * Created on Jul 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.acetree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;

import org.rhwlab.image.ImageWindow;
import org.rhwlab.snight.Config;
import org.rhwlab.snight.Identity3;
import org.rhwlab.snight.NucleiMgr;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Options extends JDialog implements ActionListener {

    private AceTree         iAceTree;
    private NucleiMgr       iNucleiMgr;
    private JSpinner        iLineWidth;
    private JSpinner        iDivisor;
    private JSpinner        iMinCutoff;
    private JRadioButton    iStandard;
    private JRadioButton    iCanonical;
    private JRadioButton    iNewCanonical;
    private JRadioButton    iManual;
    private JCheckBox		iPermanentChoice;
    private JButton         iApply;
    private JButton         iCancel;
    private boolean         iCanonicalInUse;
    private int             iNamingMethod;
    private int             iLineWidthInUse;
    private int             iDivisorInUse;
    private int             iMinCutoffInUse;

    private JComboBox       iAxis;
    private JCheckBox       iUseAxis;
    private String          iAxisInUse;

    // for background compensation
    JRadioButton            iRCNone;
    JRadioButton            iRCGlobal;
    JRadioButton            iRCLocal;
    JRadioButton            iRCBlot;
    JRadioButton            iRCCross;
    JRadioButton []         iRCChoice;
    JRadioButton []         iZipChoice;


    public Options() {
        super(AceTree.getAceTree(null).getMainFrame(), false);
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        JFrame owner = iAceTree.getMainFrame();
        setTitle(TITLE);
        iNamingMethod = 2;
        iDivisorInUse = 8;
        iMinCutoffInUse = 5;
        iLineWidthInUse = 2;
        iAxisInUse = "";

        if (iNucleiMgr != null) {
            Identity3 identity = iNucleiMgr.getIdentity();
            //iCanonicalInUse = Identity.getIdentity().getCanonical();
            iNamingMethod = identity.getNamingMethod();
            //iDivisorInUse = identity.getDivisor();
            //iMinCutoffInUse = identity.getMinCutoff();
            //System.out.println("Options: " + iDivisorInUse + C.CS + iMinCutoffInUse);
            iLineWidthInUse = ImageWindow.cLineWidth;

        }
        JDialog dialog = this;
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        p.setPreferredSize(new Dimension(200,600));
        addCentroidStyle(p);

        addIdentityChoices(p);
        addAxisSelection(p);
        addIdentitySettings(p);
        addRedCorrectionSelection(p);
        addUseZipSelection(p);
        addButtons(p);

        p.setOpaque(true); //content panes must be opaque
        dialog.setContentPane(p);
        dialog.pack();

        //dialog.setSize(new Dimension(200, 300));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
        dialog.setModal(false);

    }

    private void addCentroidStyle(JPanel mp) {
        JPanel s = new JPanel();
        s.setBorder(BorderFactory.createLineBorder(Color.black));
        s.setLayout(new GridLayout(0,1));
        s.add(new JLabel("Centroid style"));
        JPanel x = new JPanel();
        x.setLayout(new GridLayout(1,0));
        JLabel sl = new JLabel("Line width");
        x.add(sl);
        iLineWidth = new JSpinner(new SpinnerNumberModel(iLineWidthInUse,1,5,1));
        x.add(iLineWidth);
        s.add(x);
        mp.add(s);


    }

    private void addIdentityChoices(JPanel mp) {
        iStandard = new JRadioButton(STANDARD);
        //iCanonical = new JRadioButton(CANONICAL);
        iNewCanonical = new JRadioButton(NEWCANONICAL);
        iManual = new JRadioButton(MANUAL);
        ButtonGroup bg = new ButtonGroup();
        //JPanel rp = new JPanel(new GridLayout(0, 1));
        JPanel rp = new JPanel();
        Border blackline = BorderFactory.createLineBorder(Color.black);
        rp.setBorder(blackline);
        rp.setLayout(new GridLayout(0,1));
        bg.add(iStandard);
        //bg.add(iCanonical);
        bg.add(iNewCanonical);
        bg.add(iManual);
        //System.out.println("Options2: " + iNamingMethod);
        iStandard.setSelected(iNamingMethod == Identity3.STANDARD);
        //iCanonical.setSelected(iNamingMethod == Identity3.CANONICAL);
        iNewCanonical.setSelected(iNamingMethod == Identity3.NEWCANONICAL);
        iManual.setSelected(iNamingMethod == Identity3.MANUAL);
        rp.add(new JLabel("Identification mode"));
        rp.add(iStandard);
        //rp.add(iCanonical);
        rp.add(iNewCanonical);
        rp.add(iManual);
        mp.add(rp);

        //JPanel p = new JPanel();
        //iPermanentChoice = new JCheckBox("use for all opens ", false);
        //p.add(iPermanentChoice);
        //mp.add(p);


    }

    private void addIdentitySettings(JPanel mp) {
        JPanel s = new JPanel();
        s.setBorder(BorderFactory.createLineBorder(Color.black));
        s.setLayout(new GridLayout(0,1));
        s.add(new JLabel("Centroid style"));
        JPanel x = new JPanel();
        x.setLayout(new GridLayout(2,0));
        //JLabel sl = new JLabel("Line width");
        x.add(new JLabel("Divisor"));
        iDivisor = new JSpinner(new SpinnerNumberModel(iDivisorInUse,1,20,1));
        x.add(iDivisor);
        x.add(new JLabel("MinCutoff"));
        iMinCutoff = new JSpinner(new SpinnerNumberModel(iMinCutoffInUse,1,20,1));
        x.add(iMinCutoff);
        s.add(x);
        mp.add(s);

    }

    private void addAxisSelection(JPanel mp) {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createLineBorder(Color.black));
        p.setLayout(new GridLayout(0,1));
        iUseAxis = new JCheckBox("set axis ", false);
        p.add(iUseAxis);
        iAxis = new JComboBox(AXES);
        iAxis.setEditable(true);
        p.add(iAxis);
        if (iNucleiMgr != null) {
//            String s = (iNucleiMgr.getConfig()).iAxisGiven;
        	String s = "";
            if (s.length() == 0) {
                iAxis.setSelectedItem(AXES[0]);
                iUseAxis.setSelected(false);
            } else {
                iAxis.setSelectedItem(s);
                iUseAxis.setSelected(true);
            }
            iAxisInUse = s;
        }
        mp.add(p);
    }

    private void addRedCorrectionSelection(JPanel mp) {
        iRCChoice = new JRadioButton[5];
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createLineBorder(Color.black));
        p.setLayout(new GridLayout(0,1));
        iRCChoice[0] = new JRadioButton(Config.REDCHOICE[0]);
        iRCChoice[1] = new JRadioButton(Config.REDCHOICE[1]);
        iRCChoice[2] = new JRadioButton(Config.REDCHOICE[2]);
        iRCChoice[3] = new JRadioButton(Config.REDCHOICE[3]);
        iRCChoice[4] = new JRadioButton(Config.REDCHOICE[4]);
        ButtonGroup bg = new ButtonGroup();
        p.add(new JLabel("Red background compensation"));
        for (int i=0; i < 5; i++) {
            iRCChoice[i] = new JRadioButton(Config.REDCHOICE[i]);
            bg.add(iRCChoice[i]);
            p.add(iRCChoice[i]);
        }
        int c = iNucleiMgr.getConfig().getRedChoiceNumber();
        iRCChoice[c].setSelected(true);
        mp.add(p);
    }

    static final String [] ZIPCHOICE = {
    	 "tif", "zip1", "zip2", "jpg"
    };

    private void addUseZipSelection(JPanel mp) {
    	iZipChoice = new JRadioButton[4];
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createLineBorder(Color.black));
        p.setLayout(new GridLayout(0,1));
        //iZipChoice[0] = new JRadioButton(ZIPCHOICE[0]);
        //iZipChoice[1] = new JRadioButton(ZIPCHOICE[1]);
        //iZipChoice[2] = new JRadioButton(ZIPCHOICE[2]);
        //iZipChoice[3] = new JRadioButton(ZIPCHOICE[3]);
        ButtonGroup bg = new ButtonGroup();
        p.add(new JLabel("Image type (\"usezip\")"));
        for (int i=0; i < 4; i++) {
            iZipChoice[i] = new JRadioButton(ZIPCHOICE[i]);
            bg.add(iZipChoice[i]);
            p.add(iZipChoice[i]);
        }
        int c = iNucleiMgr.getConfig().iUseZip;
        iZipChoice[c].setSelected(true);
        mp.add(p);
    }

    private int getRedChoice() {
        int i = 0;
        for (i=0; i < iRCChoice.length; i++) {
            if (iRCChoice[i].isSelected()) break;
        }
        return i;
    }

    private void addButtons(JPanel mp) {
        iApply = new JButton(APPLY);
        iCancel = new JButton(CANCEL);
        iApply.addActionListener(this);
        iCancel.addActionListener(this);
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createLineBorder(Color.black));
        p.add(iApply);
        p.add(iCancel);
        mp.add(p);
    }


    @Override
	public void actionPerformed(ActionEvent e) {
        Object x = e.getSource();
        if (x != iCancel) {
            //System.out.println("not equal cancel");
            int divisor = ((Integer)iDivisor.getValue()).intValue();
            int minCutoff = ((Integer)iMinCutoff.getValue()).intValue();
            int m = ((Integer)iLineWidth.getValue()).intValue();
            if (m != iLineWidthInUse) ImageWindow.cLineWidth = m;
            int namingMethod = Identity3.MANUAL;
            if (iStandard.isSelected()) namingMethod = Identity3.STANDARD;
            //else if (iCanonical.isSelected()) namingMethod = Identity3.CANONICAL;
            else if (iNewCanonical.isSelected()) namingMethod = Identity3.NEWCANONICAL;
            if (namingMethod != iNamingMethod) {
                iNucleiMgr.getConfig().iNamingMethod = namingMethod;
            }

            // handle permanent change in naming
            //Config.cDefaultNaming = namingMethod;

            String axis = (String)iAxis.getSelectedItem();
            boolean useAxis = !axis.equals(iAxisInUse)
                            && iNewCanonical.isSelected()
                            && iUseAxis.isSelected();
//            if (useAxis) iNucleiMgr.getConfig().iAxisGiven = axis;
            //println("Options.actionPerformed: " + axis + CS + useAxis);
            if (namingMethod != iNamingMethod
                    || divisor != iDivisorInUse
                    || minCutoff != iMinCutoffInUse
                    || useAxis) {

                //Identity identity = iNucleiMgr.getIdentity();
                //identity.setNamingMethod(namingMethod);
                //identity.setDivisor(divisor);
                //identity.setMinCutoff(minCutoff);
                //identity.identityAssignment();
                iNucleiMgr.getConfig().iNamingMethod = namingMethod;
                //iNucleiMgr.processNuclei(true, namingMethod);
                iAceTree.clearTree();
                iAceTree.buildTree(true);
                //iAceTree.buildTree(true);
            }
            // handle the red bkg comp results
            // first find what is now selected
            int k = getRedChoice();
            if (k != iNucleiMgr.getConfig().getRedChoiceNumber()) {
                iNucleiMgr.getConfig().iExprCorr = Config.REDCHOICE[k];
                iNucleiMgr.computeRWeights();
            }

        }
        dispose();

    }

    private static final String [] AXES = {
            "auto"
           ,"adl"
           ,"avr"
           ,"pdr"
           ,"pvl"

       };

    private static final String
         TITLE = "Options"
        ,STANDARD = "Standard"
        //,CANONICAL = "Canonical"
	    ,NEWCANONICAL = "New canonical"
        ,MANUAL = "Manual"
        ,APPLY = "Apply"
        ,CANCEL = "Cancel"
        ;

    public static void main(String[] args) {
        new Options();
    }
    private static void println(String s) {System.out.println(s);}
    private static final String CS = ", ";
}
