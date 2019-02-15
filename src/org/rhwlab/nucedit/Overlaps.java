package org.rhwlab.nucedit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.acetree.NucUtils;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.tree.CellData;

public class Overlaps extends JPanel implements ActionListener, ListSelectionListener, Comparator {
    AceTree                     iAceTree;
    AncesTree                   iAncesTree;
    NucleiMgr                   iNucleiMgr;
    Vector                      nuclei_record;
    Cell                        iRoot;
    Hashtable                   iCellsByName;
    private JFrame              iFrame;
    protected JToolBar          iToolBar;
    protected JMenuBar          iMenuBar;
    private String              iTitle;
    private JTextField          iTextField;
    private JTextField          iTextField2;
    private JList               iCellList;
    private DefaultListModel    iListModel;
    private boolean             iNamesAvailable;
    private JScrollPane         iScrollPane;
    private JComboBox           iCombo;

    private int                 iTime;
    private String              iCellName;
    private int                 iTest;

    public Overlaps() {
        iTitle = "Overlaps";
        setPreferredSize(new Dimension(700, 300));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,0));
        add(iToolBar);
        buildOutToolBar();
        buildList();
        initialize();
        iFrame = new JFrame(iTitle);
        showMe();
        //help();

    }

    // Overlaps
    // makes a queryable list of cell deaths
    @SuppressWarnings("unused")
	private void test1() {
        println("test1 entered");
        initialize();
        iNamesAvailable = false;
        iTest = 1;
        NucUtils.setZPixRes(iAceTree.getConfig().getNucleiConfig().getZPixRes());
        String [] sa = iTextField.getText().split(":");
        int maxTime = Integer.parseInt(sa[0]);

        int t1 = Integer.parseInt(iTextField2.getText());
        int t2 = Integer.parseInt(iTextField.getText());


        String filter = "";
        if (sa.length > 1) filter = sa[1];

        t2 = maxTime;
        //println("start, end: " + t1 + CS + t2);
        ImageWindow imgWin = iAceTree.getImageWindow();
        //imgWin.clearAnnotations();
        iListModel.clear();
        iListModel.addElement("#list shows nuclei that are too close");
        iListModel.addElement("#where the separation is less than sum of radii - TOLERENCE ");

        for (int i = t1; i <= t2; i++) {
            findOverlaps(i);
        }
        iNamesAvailable = true;

    }


    private static int TOLERENCE = 15;
    public void findOverlaps(int now) {
        iNamesAvailable = false;
        iTest = 1;
        Vector nuclei = iNucleiMgr.getNucleiRecord().elementAt(now - 1);
        nuclei = cleanupAndSort(nuclei);
        for (int i=0; i < nuclei.size(); i++) {
            Nucleus n = ((Nuc)nuclei.get(i)).iNuc;
            for (int j=i + 1; j < nuclei.size(); j++) {
                Nucleus n2 = ((Nuc)nuclei.get(j)).iNuc;
                if (n2.status <  0) continue;
                int dist = NucUtils.distance(n, n2);
                double valid = (n.size + n2.size) / 2;
                if (dist > valid - TOLERENCE) continue;
                String plane = "above";
                if (n2.z > n.z) plane = "below";
                String s = now + CS + n.identity + CS + n2.identity + CS + plane + CS + DF1.format(dist) + CS + DF1.format(valid);
                iListModel.addElement(s);
                //println("findOverlaps, " + s);

            }
        }
        if (!this.isShowing()) this.showMe();
        iNamesAvailable = true;
    }

    private Vector cleanupAndSort(Vector nuclei) {
        Vector c = new Vector();
        for (int i=0; i < nuclei.size(); i++) {
            Nucleus n = (Nucleus)nuclei.get(i);
            if (n.status < 0) continue;
            c.add(new Nuc(n.identity, n));
        }
        Collections.sort(c, new Nuc());
        return c;

    }

    private class Nuc implements Comparator {
        String      iName;
        Nucleus     iNuc;

        public Nuc() {
        }

        public Nuc(String name, Nucleus n) {
            iName = name;
            iNuc = n;
        }

        @Override
		public int compare(Object o1, Object o2) {
            String s1 = ((Nuc)o1).iName;
            String s2 = ((Nuc)o2).iName;
            return s1.compareTo(s2);
        }


    }

    @SuppressWarnings("unused")
	private void showNucs() {
        initialize();
        iNamesAvailable = false;
        iTest = 1;
        NucUtils.setZPixRes(iAceTree.getConfig().getNucleiConfig().getZPixRes());
        String [] sa = iTextField.getText().split(":");
        int maxTime = Integer.parseInt(sa[0]);

        int t1 = Integer.parseInt(iTextField2.getText());
        int t2 = Integer.parseInt(iTextField.getText());


        String filter = "";
        if (sa.length > 1) filter = sa[1];

        t2 = maxTime;
        //println("start, end: " + t1 + CS + t2);
        ImageWindow imgWin = iAceTree.getImageWindow();
        //imgWin.clearAnnotations();
        iListModel.clear();
        iListModel.addElement("#list shows nucs and their plane");
        for (int i = t1; i <= t2; i++) {
            showNucs(i);
        }
        iNamesAvailable = true;


    }

    private void showNucs(int now) {
        Vector nuclei = iNucleiMgr.getNucleiRecord().elementAt(now - 1);
        nuclei = cleanupAndSort(nuclei);
        for (int i=0; i < nuclei.size(); i++) {
            Nucleus n = ((Nuc)nuclei.get(i)).iNuc;
            if (n.identity.indexOf("Nuc") == 0) {
                int plane = Math.round(n.z);
                String s = now + CS + n.identity + CS + plane;
                iListModel.addElement(s);
            }
        }

    }

    private void nucEm() {
        int [] remove = iCellList.getSelectedIndices();
        for (int i = remove.length - 1; i >= 0; i--) {
            String s = (String)iListModel.elementAt(remove[i]);
            String [] sa = s.split(CS);
            //println("unlink, " + sa[2] + CS + sa[3]);
            int time = Integer.parseInt(sa[0]);
            String cellName = sa[1];

            Vector nuclei = iNucleiMgr.getNucleiRecord().elementAt(time - 1);
            for (int j=0; j < nuclei.size(); j++) {
                Nucleus n = (Nucleus)nuclei.elementAt(j);
                if (!n.identity.equals(cellName)) continue;
                n.status = Nucleus.NILLI;
                n.identity = "";
                n.assignedID = "";
                println("nuking, " + cellName + CS + time);
            }


        }
        rebuildAndRename();

    }

    private void rebuildAndRename() {
        iAceTree.clearTree();
        iAceTree.buildTree(true);

        // update WormGUIDES data if it's open
        if (iAceTree.iAceMenuBar.view != null) {
            iAceTree.iAceMenuBar.view.rebuildData();
        }

        iNamesAvailable = false;
        iListModel.clear();
        initialize();
        //test1();

    }

    @SuppressWarnings("unused")
	private void setParms() {
        Parms myDialog = new Parms(iFrame, true);
}



    private static final double TOL = 0.7;

    private int jump(CellData cd0, CellData cd1) {
        double zPixRes = iAceTree.getConfig().getNucleiConfig().getZPixRes();
        Nucleus n0 = cd0.iNucleus;
        Nucleus n1 = cd1.iNucleus;
        int x = n0.x;
        int y = n0.y;
        int z = (int)(zPixRes * n0.z);
        int zz = (int)(zPixRes * n1.z);
        int dx = n1.x - x;
        int dy = n1.y - y;
        int dz = zz - z;
        int d2 = dx * dx + dy * dy + dz * dz;
        d2 = (int)Math.round(Math.sqrt(d2));
        return d2;
    }

    @Override
	public int compare(Object arg0, Object arg1) {
        int rtn = 1;
        String [] sa = ((String)arg0).split(CS);
        double d0 = Double.parseDouble(sa[2]);
        sa = ((String)arg1).split(CS);
        double d1 = Double.parseDouble(sa[2]);
        if (d0 > d1) rtn = -1;
        // TODO Auto-generated method stub
        return rtn;
    }


    private static final double FACTOR = .5;


    private String getConfigFileInfo(String longName) {
        String s = longName.substring(longName.lastIndexOf('/') + 1);
        s = s.substring(0, s.indexOf('.'));
        return s;
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        //append(s);
        if (e.getSource() == iCombo) {
            // note that we get an action event here when the first item
            // is added to iModel (in test1())
            // the hash is empty then so I trap out the event
            // once the hash has been populated no more events like that occur
            //append("got combo action event");
            String newSelection = (String)iCombo.getSelectedItem();
            iAceTree.restoreTree(newSelection);

        } else if (s.equals("Overlaps")) { //Deaths
            test1();
        } else if (s.equals("ShowNucs")) {
            showNucs();
            //showDivisionsAndDeaths(159);
        } else if (s.equals("NucEm")) { //Jumps
            nucEm();
            //test4();
        } else if (s.equals(CLEAR)) { //Clear
            iNamesAvailable = false;
            iListModel.clear();
        } else if (s.equals("setParms")) {
            setParms();
        }
    }


    private void buildOutToolBar() {
        iToolBar.setMaximumSize(new Dimension(700,20));
        iToolBar.add(new JLabel("start/end"));
        iTextField2 = new JTextField();
        iTextField2.setColumns(5);
        iTextField2.setText("100");
        iToolBar.add(iTextField2);
        iTextField = new JTextField();
        iTextField.setColumns(5);
        iTextField.setText("200");
        iToolBar.add(iTextField);
        JButton jb = null;
        jb = new JButton(CLEAR);
        addToolBarButton(jb);
        jb = new JButton("Overlaps");
        addToolBarButton(jb);
        jb = new JButton("ShowNucs");
        addToolBarButton(jb);
        jb = new JButton("NucEm");
        addToolBarButton(jb);
        jb = new JButton("setParms");
        addToolBarButton(jb);
    }

    protected void addToolBarButton(AbstractButton ab) {
        ab.addActionListener(this);
        iToolBar.add(ab);
    }

    protected void addComboBox(JComboBox jcb) {
        jcb.addActionListener(this);
        iToolBar.add(jcb);
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
        add(iScrollPane);
    }

    public void showMe() {
        //iFrame = new JFrame(iLog.iTitle);
        //iFrame.setTitle("Deaths and Adjacencies Dialog");
        iFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        iFrame.setContentPane(this);
        iFrame.pack();
        iFrame.setVisible(true);
    }

    public void initialize() {
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        nuclei_record = iNucleiMgr.getNucleiRecord();
        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        //iIdentity = iNucleiMgr.getIdentity();
        //iRoot = iNucleiMgr.getRoot();
        iRoot = iAncesTree.getRoot();
    }

    private static final String
    CLEAR = "Clear"
   ;



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
            String name = "";

            if (iTest == 1) {
                name = sa[1];
                iTime = Integer.parseInt(sa[0]);
            }

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


    private class Parms extends JDialog implements ActionListener {
        private JPanel  iPanel = null;
        private JButton iOKButton = null;
        private JButton iCancelButton = null;
        private JTextField      iTolerence;


        public Parms(JFrame frame, boolean modal) {
            super(frame, modal);
            iPanel = new JPanel();
            getContentPane().add(iPanel);
            iPanel.setLayout(new GridLayout(0,1));
            iTolerence = new JTextField(String.valueOf(TOLERENCE));
            iPanel.add(new JLabel("distance tolerence"));
            iPanel.add(iTolerence);
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
                TOLERENCE = Integer.parseInt(iTolerence.getText());
            }
            setVisible(false);
        }

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
    private static final DecimalFormat DF1 = new DecimalFormat("###.#");
    private static final DecimalFormat DF000 = new DecimalFormat("000");


}
