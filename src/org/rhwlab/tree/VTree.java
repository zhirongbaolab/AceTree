/*
 * Created on Jan 31, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.tree;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VTree implements ActionListener {
    private JFrame          iFrame;
    private JTextField []   iFields;
    private JLabel []       iLabels;
    private JButton         iColor;
    private JToolBar        iToolBar;
    private JButton         iShow;
    private JButton         iTestZ;
    private JButton         iPrint;
    private VTreeImpl       iVTreeImpl;
    private JRadioButton    iPng;
    private JRadioButton    iPs;

    private JCheckBox       iLabelRoot;
    private JCheckBox       iLabelLeaves;

    public VTree() {
        iFrame = new JFrame("VTree");
        JPanel pWhole = new JPanel();
        pWhole.setLayout(new BoxLayout(pWhole, BoxLayout.PAGE_AXIS));
        JScrollPane p = makePanel();
        pWhole.add(p);
        makeToolBar();
        pWhole.add(iToolBar);

        AceTree acetree = AceTree.getAceTree(null);
        NucleiMgr nucleiMgr = acetree.getNucleiMgr();
        JFrame.setDefaultLookAndFeelDecorated(false);
        String s = acetree.getConfig().getConfigFileName();
        File fs = new File(s);
        s = fs.getName();
        iFrame.setTitle(s);

        iFrame.getContentPane().add(pWhole);
        iFrame.pack();
        iFrame.setVisible(true);

    }

    private void makeToolBar() {
        iToolBar = new JToolBar();
        iShow = new JButton("show");
        iShow.addActionListener(this);
        iToolBar.add(iShow);
        iPrint = new JButton("print");
        iPrint.addActionListener(this);
        iToolBar.add(iPrint);
        //iTestZ = new JButton("testZ");
        //iTestZ.addActionListener(this);
        //iToolBar.add(iTestZ);
    }

    private JScrollPane makePanel() {
        JPanel pWhole = new JPanel();
        pWhole.setLayout(new BoxLayout(pWhole, BoxLayout.PAGE_AXIS));
        iFields = new JTextField[labels.length];
        iLabels = new JLabel[labels.length];

        for (int i=0; i < labels.length; i++) {
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(1,2));
            iLabels[i] = new JLabel(labels[i]);
            iFields[i] = new JTextField(initValues[i], 10);
            p.add(iLabels[i]);
            p.add(iFields[i]);
            pWhole.add(p);
        }
        iColor = new JButton("choose color");
        pWhole.add(iColor);
        iColor.addActionListener(this);

        //JPanel jp = new JPanel();
        //jp.setLayout(new GridLayout(0,2));
        iLabelRoot = new JCheckBox("label root", true);
        iLabelLeaves = new JCheckBox("label leaves", true);
        pWhole.add(iLabelRoot);
        pWhole.add(iLabelLeaves);

        JScrollPane sPane = new JScrollPane(pWhole);
        return sPane;
    }

    private String [] labels = {
             "rootCell"
            ,"endTime"
            ,"minRed"
            ,"maxRed"
            ,"yInc"
            ,"lineWidth"
            ,"hue"
            //,"fileName"
            //,"pagesBreaks"
    };

    private String [] initValues = {
             "E"
            ,"200"
            ,"-500"
            ,"5000"
            ,"10"
            ,"5"
            ,"0"
            //,"VTree.ps"
            //,"NONE"
    };


    public static void main(String[] args) {
        new VTree();
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == iShow) {
            //System.out.println("actionPerformed: show");
            if (iVTreeImpl == null) iVTreeImpl = new VTreeImpl();
            iVTreeImpl.showTree(iFields, iLabelRoot.isSelected(), iLabelLeaves.isSelected());
        } else if (o == iPrint) {
            //System.out.println("actionPerformed: show");
            JFileChooser iFC;
            iFC = new JFileChooser();
            int returnVal = iFC.showSaveDialog(iFrame);

            String dir = "";
            String name = "default.png";

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                dir = iFC.getCurrentDirectory().toString();
                name = iFC.getName(iFC.getSelectedFile());
                //String iTitle = dir + C.Fileseparator + name;
            }
            if (iVTreeImpl == null) iVTreeImpl = new VTreeImpl();
            iVTreeImpl.printTree(iFields, iLabelRoot.isSelected(), iLabelLeaves.isSelected(), name, dir);

        } else if (o == iTestZ) {
            VTreeImplZ vtz = new VTreeImplZ();
            vtz.printTree(iFields, "testZ.ps", "/home/biowolp/0tmp/errorBars/");
            vtz.showTree(iFields);
        } else if (o == iColor) {
            Color c = JColorChooser.showDialog(
                    null,
                    "Choose expression color",
                    Color.RED);
            if (c != null) {
                float [] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                iFields[VTreeImpl.HUELOC].setText(String.valueOf(hsb[0]));
                //println("actionPerformed, " + hsb[0] + CS + hsb[1] + CS + hsb[2]);
            }
        }

    }
    private static void println(String s) {System.out.println(s);}
    private static final String CS = ", ";
}
