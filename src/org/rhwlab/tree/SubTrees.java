package org.rhwlab.tree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;

public class SubTrees extends JPanel implements ActionListener {

    private JFrame              iFrame;
    JToolBar                    iToolBar;
    JTextField                  iTextField;
    JTextField                  iTextField2;
    JTextField                  iTextField3;
    int                         iLastTime;
    JRadioButton                iABala;
    JRadioButton                iABalp;
    private VTreeImpl           iVTreeImpl;
    ButtonGroup                 iButtonGroup;
    AceTree                     iAceTree;
    AncesTree                   iAncesTree;
    NucleiMgr                   iNucleiMgr;
    Vector                      nuclei_record;
    Cell                        iRoot;
    Hashtable                   iCellsByName;

    public SubTrees() {
        setPreferredSize(new Dimension(400, 200));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,0));
        add(iToolBar);
        initialize();
        buildOutToolBar();
        addButtons();
        iFrame = new JFrame("SubTrees");
        showMe();

    }

    public void initialize() {
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        nuclei_record = iNucleiMgr.getNucleiRecord();
        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        //iIdentity = iNucleiMgr.getIdentity();
        //iRoot = iNucleiMgr.getRoot();
        iRoot = iAceTree.getRoot();
        //makeSortedCellNames();
        iLastTime = estimate350CellStage();
    }


    @SuppressWarnings("unused")
	private int estimate350CellStage() {
        int stage = 355;
        int r = 0;
        int size = 0;
        int time = 0;
        int i = 0;
        for (; i < nuclei_record.size(); i++) {
            Vector nuclei = (Vector)nuclei_record.get(i);
            int t = nuclei.size();
            if (t > size) {
                size = t;
                time = i;
            }
            int count = 0;
            if (size > stage) {
                count = 0;
                for (int j=0; j < nuclei.size(); j++) {
                    Nucleus n = (Nucleus)nuclei.get(j);
                    if (n.status > 0 && (!n.identity.startsWith("N"))) count++;

                }
            }
            if (count > stage) break;

        }


        return (time + 1);
    }


    private static final String [] buttons = {
         "ABala"
        ,"ABalp"
        ,"ABara"
        ,"ABarp"
        ,"ABpla"
        ,"ABplp"
        ,"ABpra"
        ,"ABprp"
        ,"MSa"
        ,"MSp"
        ,"E"
        ,"C"
        ,"P3"
    };


    private void addButtons() {
        iButtonGroup = new ButtonGroup();
        Border blackBorder = BorderFactory.createLineBorder(Color.BLACK);
        JPanel jp = new JPanel(new GridLayout(1,0));
        jp.setBorder(blackBorder);
        for (int i=0; i < 4; i++) {
            JRadioButton rb = new JRadioButton(buttons[i]);
            rb.setBorder(blackBorder);
            iButtonGroup.add(rb);
            jp.add(rb);
            if (i == 0) rb.setSelected(true);
        }
        add(jp);
        jp = new JPanel(new GridLayout(1,0));
        jp.setBorder(blackBorder);
        for (int i=4; i < 8; i++) {
            JRadioButton rb = new JRadioButton(buttons[i]);
            rb.setBorder(blackBorder);
            iButtonGroup.add(rb);
            jp.add(rb);
        }
        add(jp);
        jp = new JPanel(new GridLayout(1,0));
        jp.setBorder(blackBorder);
        for (int i=8; i < buttons.length; i++) {
            JRadioButton rb = new JRadioButton(buttons[i]);
            rb.setBorder(blackBorder);
            iButtonGroup.add(rb);
            jp.add(rb);
        }
        add(jp);

    }

    private void buildOutToolBar() {
        iToolBar.setMaximumSize(new Dimension(700,20));
        iToolBar.add(new JLabel("end"));
        iTextField = new JTextField();
        iTextField.setColumns(5);
        iTextField.setText(String.valueOf(iLastTime));
        iToolBar.add(iTextField);
        iToolBar.add(new JLabel("minRed"));
        iTextField2 = new JTextField();
        iTextField2.setColumns(6);
        iTextField2.setText(String.valueOf(-500));
        iToolBar.add(iTextField2);
        iToolBar.add(new JLabel("maxRed"));
        iTextField3 = new JTextField();
        iTextField3.setColumns(5);
        iTextField3.setText(String.valueOf(5000));
        iToolBar.add(iTextField3);
        JButton jb = null;
        jb = new JButton("Show");
        addToolBarButton(jb);
        //jb = new JButton("Report");
        //addToolBarButton(jb);
        //jb = new JButton("SetParms");
        //addToolBarButton(jb);

    }

    protected void addToolBarButton(AbstractButton ab) {
        ab.addActionListener(this);
        iToolBar.add(ab);
    }


    public void showMe() {
        //iFrame = new JFrame(iLog.iTitle);
        //iFrame.setTitle("Deaths and Adjacencies Dialog");
        iFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        iFrame.setContentPane(this);
        iFrame.pack();
        iFrame.setVisible(true);
    }


    @Override
	public void actionPerformed(ActionEvent e) {
        String root = getRoot();
        if (iVTreeImpl == null) iVTreeImpl = new VTreeImpl();
        int last = Integer.parseInt(iTextField.getText());
        int minRed = Integer.parseInt(iTextField2.getText());
        int maxRed = Integer.parseInt(iTextField3.getText());
        iVTreeImpl.showTree(root, last, minRed, maxRed);

    }

    private String getRoot() {
        String root = "E";
        Enumeration e = iButtonGroup.getElements();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            JRadioButton jb = (JRadioButton)o;
            root = jb.getText();
            boolean b = jb.isSelected();
            if (b) break;

        }
        return root;
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    private static final String CS = ", ";
    private static void println(String s) {System.out.println(s);}
    private static final DecimalFormat DF2 = new DecimalFormat("###.##");
    private static final DecimalFormat DF000 = new DecimalFormat("000");
    private static String fmt4(double d) {return new DecimalFormat("####.####").format(d);}

}
