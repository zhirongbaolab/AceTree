package org.rhwlab.nucedit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Comparator;
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
import org.rhwlab.image.ImageWindow;

import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;


public class Lazarus extends JDialog  implements ActionListener, ListSelectionListener, Comparator {

    AceTree         iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    private JFrame      iFrame;
    protected JToolBar iToolBar;
    private String      iTitle;
    private JTextField iTextField;
    private Font        iFont;
    private DefaultListModel    iListModel;
    private JList               iCellList;
    private boolean         iNamesAvailable;
    private JScrollPane         iScrollPane;
    private int         iTime;
    Hashtable                  iLazers;
    int                     iDelT;
    int                     iDelD;
    int                     iDelJ;
    int                     iDelJD;

    public Lazarus() {

        iTitle = "Lazarus";
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
        iFont = new Font("roman", Font.PLAIN, 16);
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,0));
        add(iToolBar);
        buildOutToolBar();
        buildList();
        initialize();
        iTitle = "Lasarus";
        iFrame = new JFrame(iTitle);
        showMe();
        */

        help();
        iDelT = 4;
        iDelD = 30;
        iDelJ = 30;
        iDelJD = 20;

    }

    @SuppressWarnings("unused")
	private void makeInitialList() {
        //println("test1 entered");
        println("makeInitialList, " + iDelT + CS + iDelD + CS + iDelJ + CS + iDelJD);
        iLazers = new Hashtable();
        iNamesAvailable = false;
        //iTest = 1;
        int t1 = iAceTree.getConfig().getImageConfig().getStartingIndex();
        int t2 = iAceTree.getConfig().getImageConfig().getEndingIndex();
        String [] sa = iTextField.getText().split(":");
        int maxTime = Integer.parseInt(sa[0]);
        String filter = "";
        if (sa.length > 1) filter = sa[1];

        t2 = maxTime;
        //println("start, end: " + t1 + CS + t2);
        ImageWindow imgWin = iAceTree.getImageWindow();
        //imgWin.clearAnnotations();
        iListModel.clear();
        iListModel.addElement("#list shows cells that died and the time");
        for (int i = t1; i < t2; i++) {
            Vector nuclei = (Vector)nuclei_record.get(i - 1);
            for (int j = 0; j < nuclei.size(); j++) {
                Nucleus n = (Nucleus)nuclei.get(j);
                if (n.status < 1) continue;
                if (n.successor1 < 0 && n.successor2 < 0) {
                    //println("died: " + n.identity + CS + i);
                    //StringBuffer sb = new StringBuffer();
                    //sb.append(n.identity);
                    //sb.append(CS + i);
                    //if (sb.toString().indexOf(filter) == 0) {
                    //    iListModel.addElement(sb.toString());
                    //}
                    Laz laz = new Laz(n, i);
                    iLazers.put(n.identity, laz);
                }
            }
        }
        iNamesAvailable = true;
        //*****
        iListModel.clear();
        iListModel.addElement("####");
        Enumeration cells = iLazers.keys();
        while (cells.hasMoreElements()) {
        //for (int i=0; i < iLazers.size(); i++) {
            String name = (String)cells.nextElement();
            Laz laz = (Laz)iLazers.get(name);
            //println("makeInitialList, " + i + laz.iDyer.identity + CS + laz.iDTime);
            int tend = laz.iDTime + iDelT;
            Vector nuclei = null;
            for (int j = laz.iDTime + 1; j < tend; j++) {
                nuclei = (Vector)nuclei_record.get(j - 1);
                boolean matchFound = false;
                for (int m = 0; m < nuclei.size(); m++) {
                    Nucleus n = (Nucleus)nuclei.get(m);
                    if (n.status < 0) continue;
                    int d = laz.distance(n);
                    if (d < iDelD) {
                        Laz lazx = getJump(j-2, n);
                        if (lazx.iMatch) {
                            laz.setMatch(n, j);
                            matchFound = true;
                            break;
                        }
                    }
                }
                if (matchFound) break;
           }
           println("" + laz);
           if (laz.iMatch) iListModel.addElement(laz.toString());
        }
        //*****/


    }

    private Laz getJump(int k, Nucleus n) {
        Laz laz = new Laz(n, k);
        Vector nuclei = (Vector)nuclei_record.get(k);
        int index = n.predecessor;
        if (index < 0) {
            // a new born cell is in the right spot
            laz.iMatch = true;
            return laz;
        }
        Nucleus np = (Nucleus)nuclei.get(index - 1);
        int dist = laz.distance(np);
        if (np.successor2 > 0) {
            // this is a division case
            if (dist > iDelJD) laz.iMatch = true;

        } else {
            if (dist > iDelJ) laz.iMatch = true;
        }
        return laz;
    }

    /*
    private int getJumpX(int k, Nucleus n) {
        Laz laz = new Laz(n, k);
        Vector nuclei = (Vector)nuclei_record.get(k);
        for (int i=0; i < nuclei.size(); i++) {
            Nucleus np = (Nucleus)nuclei.get(i);
            if (np.identity.equals(n.identity)) {
                return laz.distance(np);
            }
        }
        return 0;
    }
    */

    private void linkEm() {
        NucRelinkDialog.iNucleiMgr = iNucleiMgr;
        Enumeration cells = iLazers.keys();
        while (cells.hasMoreElements()) {
        //for (int i=0; i < iLazers.size(); i++) {
            String name = (String)cells.nextElement();
            Laz laz = (Laz)iLazers.get(name);
            if (!laz.iMatch) continue;

            NucRelinkDialog.createAndAddCells(laz.iLifer.identity, laz.iLTime, laz.iDyer.identity, laz.iDTime);
        }
    }

    private void rebuildAndRename() {
        //updateCurrentInfo(false);
        //int time = iImageTime + iTimeInc;
        //Cell c = iCurrentCell;
        iAceTree.clearTree();
        iAceTree.buildTree(true);

        // update WormGUIDES data if it's open
        if (iAceTree.iAceMenuBar.view != null) {
            iAceTree.iAceMenuBar.view.rebuildData();
        }

        iAceTree.updateDisplay();
        //if (c != null) iAceTree.setStartingCell(c, time);
        makeInitialList();

    }

    @SuppressWarnings("unused")
	private void setParms() {
            Parms myDialog = new Parms(iFrame, true);
    }

    private void remove() {
        int [] remove = iCellList.getSelectedIndices();
        for (int i = remove.length - 1; i >= 0; i--) {
            String s = (String)iListModel.elementAt(remove[i]);
            String [] sa = s.split(CS);
            iLazers.remove(sa[0]);
            println("remove, " + s);
            iListModel.remove(remove[i]);
        }
    }



    private void buildList() {
        //Create the list and put it in a scroll pane.
        iListModel = new DefaultListModel();
        iCellList = new JList(iListModel);
        iCellList.setFont(new Font("courier", Font.PLAIN, 16));
        iCellList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        iCellList.setSelectedIndex(0);
        iCellList.addListSelectionListener(this);
        //iCellList.setVisibleRowCount(5);
        iScrollPane = new JScrollPane(iCellList);
        //add(iScrollPane);
    }

    private void buildOutToolBar() {
        iToolBar.setMaximumSize(new Dimension(700,20));
        iToolBar.add(new JLabel("maxTime:"));
        iTextField = new JTextField();
        iTextField.setColumns(5);
        iTextField.setText("200");
        iToolBar.add(iTextField);
        JButton jb = null;
        jb = new JButton(CLEAR);
        addToolBarButton(jb);
        jb = new JButton(TEST1);
        addToolBarButton(jb);
        jb = new JButton("linkEm");
        addToolBarButton(jb);
        jb = new JButton("rebuild");
        addToolBarButton(jb);
        jb = new JButton("setParms");
        addToolBarButton(jb);
        jb = new JButton("remove");
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

    String [] shelp = {
            "# maxTime field accepts forms like 200:ABalaa"
            ,"# if the colon and cell are there then output will be filtered to that"
            ,"# and its decendents"
            ,"# Deaths: makes a queryable list of cell deaths"
            ,"# Adjacent backward: makes two queryable lists showing nearby cells at this ane previous time."
            ,"#A djacent forward: makes a queryable list of cells near current cell at next time"
    };

    private void help() {
        iListModel.addElement("####");
        //for (int i=0; i < shelp.length; i++) {
        //    iListModel.addElement(shelp[i]);
        //}

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



    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
    private static final String CS = ", ";
    private static void println(String s) {System.out.println(s);}
    private static final DecimalFormat DF2 = new DecimalFormat("###.##");

    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals(TEST1)) {
            makeInitialList();
        } else if (s.equals(CLEAR)) {
            iNamesAvailable = false;
            iListModel.clear();
            help();
        } else if (s.equals("linkEm")) {
            linkEm();
        } else if (s.equals("rebuild")) {
            rebuildAndRename();
        } else if (s.equals("setParms")) {
            setParms();
        } else if (s.equals("remove")) {
            remove();
        }
    }

    @Override
	public void valueChanged(ListSelectionEvent e) {
        if (!iNamesAvailable) return;
        if (e.getValueIsAdjusting() == false) {
            int index = iCellList.getSelectedIndex();
            int k = Math.max(index, 0);
            String s = (String)iListModel.elementAt(k);
            if (s == null) return;
            if (s.charAt(0) == '#') return;
            String [] sa = s.split(CS);
            String name = "";

            name = sa[0];
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


    @Override
	public int compare(Object arg0, Object arg1) {
        // TODO Auto-generated method stub
        return 0;
    }

private class Laz {
    boolean     iMatch;
    Nucleus     iDyer;
    int         iDTime;
    Nucleus     iLifer;
    int         iLTime;
    int         iDist; // used transiently only
    public Laz(Nucleus n, int time) {
        iDyer = n;
        iDTime = time;

    }

    public void setMatch(Nucleus n, int time) {
        iMatch = true;
        iLifer = n;
        iLTime = time;
    }

    public void linkIt() {
        // do something
    }

    public int distance(Nucleus n) {
        double x = iDyer.x - n.x;
        double d = x * x;
        x = iDyer.y - n.y;
        d += x * x;
        x = 11.1 * (iDyer.z - n.z);
        d += x * x;

        iDist = (int)Math.sqrt(d);
        return iDist;
    }

    @Override
	public String toString() {
        StringBuffer sb = new StringBuffer(iDyer.identity);
        sb.append(CS + iDTime);
        sb.append(CS + iMatch);
        if (iMatch) {
            sb.append(CS + iLifer.identity);
            sb.append(CS + iLTime);
        }
        return sb.toString();
    }

}

    private class Parms extends JDialog implements ActionListener {
        private JPanel  iPanel = null;
        private JButton iOKButton = null;
        private JButton iCancelButton = null;
        private JTextField      iTime;
        private JTextField      iDistance;
        private JTextField      iJump;
        private JTextField      iJumpD;


        public Parms(JFrame frame, boolean modal) {
            super(frame, modal);
            iPanel = new JPanel();
            getContentPane().add(iPanel);
            iPanel.setLayout(new GridLayout(0,1));
            iTime = new JTextField(String.valueOf(iDelT));
            iDistance = new JTextField(String.valueOf(iDelD));
            iJump = new JTextField(String.valueOf(iDelJ));
            iJumpD= new JTextField(String.valueOf(iDelJD));
            iPanel.add(new JLabel("time"));
            iPanel.add(iTime);
            iPanel.add(new JLabel("distance"));
            iPanel.add(iDistance);
            iPanel.add(new JLabel("jump"));
            iPanel.add(iJump);
            iPanel.add(new JLabel("division"));
            iPanel.add(iJumpD);
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
                iDelT = Integer.parseInt(iTime.getText());
                iDelD = Integer.parseInt(iDistance.getText());
                iDelJ = Integer.parseInt(iJump.getText());
                iDelJD = Integer.parseInt(iJumpD.getText());
            }
            setVisible(false);
        }

    }

}
