package org.rhwlab.nucedit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
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

public class Siamese extends JDialog implements ActionListener, ListSelectionListener, Comparator {

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

    int                     iDelT;
    int                     iDelD;
    int                     iDelJ;
    int                     iDelJD;

    public Siamese() {
        //super(title);
        iTitle = "Siamese";
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
        buildOutToolBar();
        buildList();
        initialize();
        iFrame = new JFrame(iTitle);
        showMe();
        //help();
         */

    }

    // makes a queryable list of scored divisions
    @SuppressWarnings("unused")
	private void test1() {
        //println("test1 entered");
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
        iListModel.addElement("#list shows parent, division time, daughters 1 & 2, and score");
        for (int i = t1; i <= t2; i++) {
            showDivisions(i);
        }
        iNamesAvailable = true;

    }



    public void showDivisions(int now) {
        //println("showDivisions, " + now);
        iNamesAvailable = false;
        iTest = 1;
        Vector nucleiNow = iNucleiMgr.getNucleiRecord().elementAt(now - 1);
        Nucleus nnow = null;
        for (int j=0; j < nucleiNow.size(); j++) {
            nnow = (Nucleus)nucleiNow.elementAt(j);
            if (nnow.status <= 0) continue;
            if (nnow.successor1 > 0  && nnow.successor2 > 0) {
                Vector nucleiNext = iNucleiMgr.getNucleiRecord().elementAt(now);
                Nucleus a = (Nucleus)nucleiNext.elementAt(nnow.successor1 - 1);
                Nucleus p = (Nucleus)nucleiNext.elementAt(nnow.successor2 - 1);
                int score = evaluateDivision(nnow, a, p, now);
                if (score == 0) continue;
                iListModel.addElement(division(nnow.identity, a.identity, p.identity, now, score));
                if (score < 100) continue;
                // this is a case of a short division so we look (again) at the parent division
                Cell cnow = (Cell)iCellsByName.get(nnow.identity);
                Cell parent = (Cell)cnow.getParent();
                //if (parent == null) {
                //    println("showDivisions, null parent, " + cnow.getName() + CS + nnow);
                //}

                int endTime = parent.getEndTime();
                Vector cellData = parent.getCellData();
                if (cellData.size() == 0) break;
                CellData cd = (CellData)cellData.get(cellData.size() - 1);
                Nucleus parentN = cd.iNucleus;
                //println("showDivisions, " + nnow.identity + CS + endTime + CS + parentN);

                nucleiNext = iNucleiMgr.getNucleiRecord().elementAt(endTime);
                a = (Nucleus)nucleiNext.elementAt(parentN.successor1 - 1);
                p = (Nucleus)nucleiNext.elementAt(parentN.successor2 - 1);
                score = evaluateDivision(parentN, a, p, endTime);
                iListModel.addElement(division(parentN.identity, a.identity, p.identity, endTime, score) + CS + "PARENT");



            }
        }
        if (!this.isShowing()) this.showMe();
        iNamesAvailable = true;
    }

    private static int
         MINLIFETIME = 15
        ,MAXCGMOVEMENT = 20
        ,MAXMOVEMENT = 30
        ,MINMOVEMENT = 10
        ;

    private int evaluateDivision(Nucleus parent, Nucleus dau1, Nucleus dau2, int now) {
        int ddau1 = NucUtils.distance(parent, dau1);
        int ddau2 = NucUtils.distance(parent, dau2);
        int dmax = Math.max(ddau1, ddau2);
        int dmin = Math.min(ddau1, ddau2);

        Cell pcell = (Cell)iCellsByName.get(parent.identity);
        int lifeTime = 2 * MINLIFETIME;
        if (pcell != null) lifeTime = pcell.getLifeTime();
        int cgdist = NucUtils.distance(parent, NucUtils.meanPos(dau1, dau2));
        int score = 1000;
        //if (ddau2 < MINMOVEMENT) score += 1;
        //if (ddau1 < MINMOVEMENT) score += 10;
        if (dmax > MAXMOVEMENT && dmin < MINMOVEMENT) score += 1;
        if (cgdist > MAXCGMOVEMENT) score += 10;
        if (lifeTime < MINLIFETIME) score += 100;
        score -= 1000;
        //boolean good = ddau1 > MINMOVEMENT;
        //good = good && ddau2 > MINMOVEMENT;
        //good = good && lifeTime > MINLIFETIME;
        //if (!good) println("bogus, " + parent.identity + CS + now + CS + ddau1 + CS + ddau2 + CS + lifeTime);
        //println("evaluateDivision, " + parent.identity + CS + now + CS + lifeTime + CS + cgdist + CS + ddau1 + CS + ddau2 + CS + score);
        return score;
    }

    public String division(String par, String dau, String dau2, int now, int score) {
        StringBuffer sb = new StringBuffer(par);
        sb.append(CS + now);
        sb.append(CS + dau);
        sb.append(CS + dau2);
        sb.append(CS + DF000.format(score));
        return sb.toString();
    }

    private void unDivide() {
        int [] remove = iCellList.getSelectedIndices();
        for (int i = remove.length - 1; i >= 0; i--) {
            String s = (String)iListModel.elementAt(remove[i]);
            String [] sa = s.split(CS);
            println("unlink, " + sa[2] + CS + sa[3]);
            int time = Integer.parseInt(sa[1]) + 1;
            NucRelinkDialog.iNucleiMgr = iNucleiMgr;
            NucRelinkDialog.createAndAddCells(sa[2], time, AceTree.ROOTNAME, 1);
            NucRelinkDialog.createAndAddCells(sa[3], time, AceTree.ROOTNAME, 1);
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
        test1();

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

    private void addItems(Vector vv, ImageWindow imgWin) {
        int last = Math.min(vv.size(), 10);
        for (int i=0; i < last; i++) {
            D dd = (D)vv.get(i);
            imgWin.addAnnotation(dd.id, dd.x, dd.y);
            StringBuffer sb = new StringBuffer();
            sb.append(i);
            sb.append(CS + dd.id);
            sb.append(CS + dd.d);
            sb.append(CS + dd.time);
            sb.append(CS + dd.n.z);
            iListModel.addElement(sb.toString());
        }

    }


    String [] shelp = {
            "# maxTime field accepts forms like 200:ABalaa"
            ,"# if the colon and cell are there then output will be filtered to that"
            ,"# and its decendents"
            ,"# Deaths: makes a queryable list of cell deaths"
            ,"# Adjacent backward: makes two queryable lists showing nearby cells at this ane previous time."
            ,"#A djacent forward: makes a queryable list of cells near current cell at next time"
    };

    private void help() {
        for (int i=0; i < shelp.length; i++) {
            iListModel.addElement(shelp[i]);
        }

    }


    private class D implements Comparator {
        String id;
        int     d;
        int     x;
        int     y;
        float   z;
        Nucleus n;
        int     k;
        int     time;

        public D() {}

        public D(String s, int dd, int xx, int yy, float zz, Nucleus nn, int kk, int t) {
            id = s;
            d = dd;
            x = xx;
            y = yy;
            z = zz;
            n = nn;
            k = kk;
            time = t;

        }
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
		public int compare(Object o1, Object o2) {
            D d1 = (D)o1;
            D d2 = (D)o2;
            int rtn = 0;
            if (d1.d < d2.d) rtn = -1;
            else if (d1.d > d2.d) rtn = 1;
            return rtn;
        }
    }





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

        } else if (s.equals("Divisions")) { //Deaths
            test1();
            //showDivisionsAndDeaths(159);
        } else if (s.equals("UnDivide")) { //Jumps
            unDivide();
            //test4();
        } else if (s.equals(CLEAR)) { //Clear
            iNamesAvailable = false;
            iListModel.clear();
            help();
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
        jb = new JButton("Divisions");
        addToolBarButton(jb);
        jb = new JButton("UnDivide");
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
        //iCellList.setVisibleRowCount(5);
        iScrollPane = new JScrollPane(iCellList);
        //add(iScrollPane);
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
   ,LINE  = "                                        "
   ,ANGLE = "Angle"
   ,TEST1 = "Deaths"
   ,TEST2 = "Adj back"
   ,TEST3 = "Adj forward"
   ,TEST4 = "Jumps"
   ,TEST5 = "Test5"
   ,HELP  = "Help"
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

            if (iTest == 2) {
                name = sa[1];
                iTime = Integer.parseInt(sa[3]);
            } else if (iTest == 1) {
                name = sa[0];
                iTime = Integer.parseInt(sa[1]);
            } else if (iTest == 3) {
                name = sa[0];
                iTime = Integer.parseInt(sa[2]);
            } else if (iTest == 4) {
                name = sa[1];
                //iTime = Integer.parseInt(sa[2]);
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
        private JTextField      iMinDauMove;
        private JTextField      iMaxDauMove;
        private JTextField      iMaxCGMove;
        private JTextField      iMinLifeTime;


        public Parms(JFrame frame, boolean modal) {
            super(frame, modal);
            iPanel = new JPanel();
            getContentPane().add(iPanel);
            iPanel.setLayout(new GridLayout(0,1));
            iMinLifeTime = new JTextField(String.valueOf(MINLIFETIME));
            iMaxCGMove = new JTextField(String.valueOf(MAXCGMOVEMENT));
            iMaxDauMove = new JTextField(String.valueOf(MAXMOVEMENT));
            iMinDauMove = new JTextField(String.valueOf(MINMOVEMENT));
            iPanel.add(new JLabel("min lifetime"));
            iPanel.add(iMinLifeTime);
            iPanel.add(new JLabel("max CG move"));
            iPanel.add(iMaxCGMove);
            iPanel.add(new JLabel("max dau move"));
            iPanel.add(iMaxDauMove);
            iPanel.add(new JLabel("min dau move"));
            iPanel.add(iMinDauMove);
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
                MINLIFETIME = Integer.parseInt(iMinLifeTime.getText());
                MAXCGMOVEMENT = Integer.parseInt(iMaxCGMove.getText());
                MINMOVEMENT = Integer.parseInt(iMinDauMove.getText());
                MAXMOVEMENT = Integer.parseInt(iMaxDauMove.getText());
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
    private static final DecimalFormat DF000 = new DecimalFormat("000");


}
