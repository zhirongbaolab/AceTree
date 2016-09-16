package org.rhwlab.nucedit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;


public class Juvenesence extends JDialog implements ActionListener, ListSelectionListener{

    private JFrame              iFrame;
    protected JToolBar          iToolBar;
    private JTextField          iTextField;
    AceTree                     iAceTree;
    AncesTree                   iAncesTree;
    NucleiMgr                   iNucleiMgr;
    Vector                      nuclei_record;
    Cell                        iRoot;
    Hashtable                   iCellsByName;
    Vector                      iSortedCellNames;
    int                         iLastTime;
    private JList               iCellList;
    private DefaultListModel    iListModel;
    private boolean             iNamesAvailable;
    private JScrollPane         iScrollPane;
    int                         iTime;
    String						iTitle;

    public Juvenesence() {
        iTitle = "Juvenesence";
        setTitle(iTitle);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        setContentPane(p);
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,0));
        p.add(iToolBar);
        buildOutToolBar();
        buildList();
        iCellList.setVisibleRowCount(10);
        p.add(iScrollPane);
        initialize();
        setVisible(true);
        pack();

    	/*
    	setPreferredSize(new Dimension(700, 300));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,0));
        add(iToolBar);
        initialize();
        buildOutToolBar();
        buildList();
        iFrame = new JFrame("Juvenesence");
        showMe();
        */

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
        makeSortedCellNames();
        iLastTime = estimate350CellStage();
    }

    @SuppressWarnings("unused")
	public void lifetimesTest(double tolL, double tolH) {
        println("lifetimesTest, " + tolL + CS + tolH);
        iLastTime = Integer.parseInt(iTextField.getText());
        //Embryo a = Embryo.getEmbryoFromSeries(series);
        Vector v = findQualifyingCellNames();
        AncesTree ances = iAncesTree;
        //Cell root = ances.getRoot();
        Enumeration e = iRoot.breadthFirstEnumeration();
        Vector base = new Vector();
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            String name = c.getName();
            if (v.contains(name)) base.add(name);
        }
        Hashtable lifetimesHash = new Hashtable();
        for (int i=base.size() - 1; i >= 0; i--) {
            String name = (String)base.get(i);
            if (name.equals("Cpppa")) {
                int kkk=0;
            }
            Cell c = (Cell)iCellsByName.get(name);
            if (c.getEndTime() >= iLastTime) continue;
            double lifetime = (c.getEndTime() - c.getTime() + 1);
            lifetimesHash.put(name, new Double(lifetime));
        }
        for (int i=base.size() - 1; i >= 0; i--) {
            String name = (String)base.get(i);
            //println("test, " + name);
            //if (name.equals("P3")) {
            //    String x = "x";
            //}
            Cell c = (Cell)iCellsByName.get(name);
            Cell p = (Cell)c.getParent();
            if (p == null) continue;
            String pname = p.getName();
            Object obj = lifetimesHash.get(pname);
            if (obj == null) continue; //parent in but we dont have its lifetime
            double pLifetime = ((Double)obj).doubleValue();
            obj = lifetimesHash.get(name);
            if (obj == null) continue; //parent in but we dont have its lifetime
            double lifetime = ((Double)lifetimesHash.get(name)).doubleValue();
            double ratio = lifetime/pLifetime;
            if (ratio < tolL || ratio > tolH) {

                String s = name + CS + c.getEndTime() + CS + fmt4(ratio) + CS + fmt4(lifetime) + CS + fmt4(pLifetime);
                println(s);
                iListModel.addElement(s);

            }

        }
        println("lifetimesTest, exiting");
    }

    private  Vector findQualifyingCellNames() {
        Vector ans = new Vector();
        for (int i=0; i < iSortedCellNames.size(); i++) {
            String name = (String)iSortedCellNames.get(i);
            if (name.length() == 0) continue;
            //if (!(name.startsWith(lineage))) continue;
            Cell c = (Cell)iCellsByName.get(name);
            if (c.getFateInt() != Cell.DIVIDED) continue;
            Cell cp = (Cell)c.getParent();
            if (cp == null) {
                println("$$$$$$$$$$$");
                println("##### findComparableCellNames, missing parent, " + name);
                println("***********");
            }
            if (cp.getFateInt() != Cell.DIVIDED) continue;
            int endT = c.getEndTime();
            if (endT > iLastTime) continue;

            ans.add(name);
        }
        return ans;
    }



    @SuppressWarnings("unused")
	private void makeSortedCellNames() {
        iSortedCellNames = new Vector();
        Enumeration e = iCellsByName.keys();
        int i = 0;
        while (e.hasMoreElements()) {
            iSortedCellNames.add (e.nextElement());
        }
        Collections.sort(iSortedCellNames);
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



    private void buildList() {
        //Create the list and put it in a scroll pane.
        iListModel = new DefaultListModel();
        iCellList = new JList(iListModel);
        iCellList.setFont(new Font("courier", Font.PLAIN, 16));
        //iCellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        iCellList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        iCellList.setSelectedIndex(0);
        iCellList.addListSelectionListener(this);
        iCellList.setVisibleRowCount(5);
        iScrollPane = new JScrollPane(iCellList);
        //add(iScrollPane);
        iListModel.addElement(help());
    }



    private void buildOutToolBar() {
        iToolBar.setMaximumSize(new Dimension(700,20));
        iToolBar.add(new JLabel("end"));
        iTextField = new JTextField();
        iTextField.setColumns(5);
        iTextField.setText(String.valueOf(iLastTime));
        iToolBar.add(iTextField);
        JButton jb = null;
        jb = new JButton("Clear");
        addToolBarButton(jb);
        jb = new JButton("Report");
        addToolBarButton(jb);
        jb = new JButton("SetParms");
        addToolBarButton(jb);

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
        String s = e.getActionCommand();
        if (s.equals("SetParms")) {
            setParms();
        } else if (s.equals("Report")) {
            iNamesAvailable = false;
            lifetimesTest(MINRATIO, MAXRATIO);
            iNamesAvailable = true;
        } else if (s.equals("Clear")) { //Clear
            iNamesAvailable = false;
            iListModel.clear();
            iListModel.addElement(help());
        }

    }

    private String help() {
        return "### dividing cell, time, ratio, lifetime, parent lifetime";
    }



    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
	public void valueChanged(ListSelectionEvent e) {
        if (!iNamesAvailable) return;
        if (e.getValueIsAdjusting() == false) {
            int index = iCellList.getSelectedIndex();
            int k = Math.max(index, 1);
            String s = (String)iListModel.elementAt(k);
            if (s == null) return;
            if (s.charAt(0) == '#') return;
            String [] sa = s.split(CS);
            String name = sa[0];
            iTime = Integer.parseInt(sa[1]);

            bringUpImageWindow(name);
        }

    }

    private void bringUpImageWindow(String name) {
        Vector v = new Vector();
        v.add("InputCtrl1");
        v.add(String.valueOf(iTime));
        v.add(name);
        iAceTree.forceTrackingOn();
        iAceTree.controlCallback(v);
        iAceTree.setFocusHome();
    }

    @SuppressWarnings("unused")
	private void setParms() {
        Parms myDialog = new Parms(iFrame, true);
    }

    private class Parms extends JDialog implements ActionListener {
        private JPanel  iPanel = null;
        private JButton iOKButton = null;
        private JButton iCancelButton = null;
        private JTextField      iMaxRatio;
        private JTextField      iMinRatio;


        public Parms(JFrame frame, boolean modal) {
            super(frame, modal);
            iPanel = new JPanel();
            getContentPane().add(iPanel);
            iPanel.setLayout(new GridLayout(0,1));
            iMinRatio = new JTextField(String.valueOf(MINRATIO));
            iMaxRatio = new JTextField(String.valueOf(MAXRATIO));
            iPanel.add(new JLabel("min ratio"));
            iPanel.add(iMinRatio);
            iPanel.add(new JLabel("max ratio"));
            iPanel.add(iMaxRatio);
            iOKButton = new JButton("OK");
            iOKButton.addActionListener(this);
            iPanel.add(iOKButton);
            iCancelButton = new JButton("Cancel");
            iCancelButton.addActionListener(this);
            iPanel.add(iCancelButton);
            pack();
            setLocationRelativeTo(frame);
            setVisible(true);
        }

        @Override
		public void actionPerformed(ActionEvent e) {
            if(iOKButton == e.getSource()) {
                MINRATIO = Double.parseDouble(iMinRatio.getText());
                MAXRATIO = Double.parseDouble(iMaxRatio.getText());
            }
            setVisible(false);
        }


    }

    private static double
    MINRATIO = 0.8
   ,MAXRATIO = 2.5
   ;


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
