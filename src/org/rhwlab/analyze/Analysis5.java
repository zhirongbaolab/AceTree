/*
 * Created on Jul 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
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
import org.rhwlab.utils.ConfigFileList;

/**
 * Treat this as a template for a future Analysis class.
 * Any existing internals are obsolete as of Nov 30, 2005.
 *
 * @author biowolp
 *
 */
public class Analysis5 extends JPanel implements ActionListener, ListSelectionListener, Comparator {
    AceTree         iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    //Identity        iIdentity;
    private JFrame      iFrame;
    protected JToolBar iToolBar;
    protected JMenuBar     iMenuBar;
    private String      iTitle;
    private JFileChooser iFC;
    private JTextField iTextField;
    private JList               iCellList;
    private DefaultListModel    iListModel;
    private boolean         iNamesAvailable;
    private JScrollPane         iScrollPane;
    private int         iSampleIndex;
    private ConfigFileList  iConfigFileList;
    private Vector      iConfigFiles;
    private Vector      iNucleiMgrs;
    private Vector      iConfigsAvailable;
    private DefaultComboBoxModel iModel;
    private JComboBox   iCombo;
    private Hashtable   iNucleiMgrHash;
    private int			iDepthP0;

    private Vector		iLines;
    private Vector		iNames;
    private Vector 		iCellLines;
    private int         iKlast;
    private int			iTime;
    private Font        iFont;
    private String      iCellName;
    private int         iTest;

    public Analysis5(String title) {
        //super(title);
        setPreferredSize(new Dimension(700, 300));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        iFont = new Font("roman", Font.PLAIN, 16);
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,0));
        //JButton jb1 = new JButton("Save as");
        //jb1.addActionListener(this);
        //iToolBar.add(jb1);
        add(iToolBar);
        iConfigsAvailable = new Vector();
        iModel = new DefaultComboBoxModel(iConfigsAvailable);
        //showMe();
        buildOutToolBar();
        buildList();
        iNucleiMgrs = new Vector();
        iNucleiMgrHash = new Hashtable();
        initialize();
        iSampleIndex = 0;
        //iText.setFont(new Font("courier", Font.PLAIN, 12));
        //createMenuBar();
        //iConfigFileList = new ConfigFileList(this);
        //iConfigFileList.createMenu(iMenuBar);
        //JMenu choose = new JMenu("Choose");
        //JMenuItem chooseItem = new JMenuItem("ConfigFile");
        //choose.add(chooseItem);
        //iMenuBar.add(choose);
        //chooseItem.addActionListener(this);
        iFrame = new JFrame(iTitle);
        //iMenu = new JMenu("File");
        //iFrame.getContentPane().add(iMenu);
        //iFrame.setJMenuBar(createMenuBar());
        iFC = new JFileChooser(".");
        showMe();
        help();
    }

    // makes a queryable list of cell deaths
    @SuppressWarnings("unused")
	private void test1() {
        //println("test1 entered");
        iNamesAvailable = false;
        iTest = 1;
        int t1 = iNucleiMgr.getConfig().iStartingIndex;
        int t2 = iNucleiMgr.getConfig().iEndingIndex;
        t2 = Integer.parseInt(iTextField.getText());
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
                    StringBuffer sb = new StringBuffer();
                    sb.append(n.identity);
                    sb.append(CS + i);
                    iListModel.addElement(sb.toString());
                }
            }
        }
        iNamesAvailable = true;

    }

    @SuppressWarnings("unused")
	private void test3() {
        println("test3, the movie");
        //println("test1 entered");
        iNamesAvailable = false;
        iTest = 1;
        int t1 = iNucleiMgr.getConfig().iStartingIndex;
        int t2 = iNucleiMgr.getConfig().iEndingIndex;
        //println("start, end: " + t1 + CS + t2);
        ImageWindow imgWin = iAceTree.getImageWindow();
        //imgWin.clearAnnotations();
        iListModel.clear();
        iListModel.addElement("#list shows cells that died and the time");
        Vector bigJumps = new Vector();

        int checkCount = 0;
        Cell P = (Cell)iCellsByName.get("P");
        Enumeration e = P.children();
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            //println("test3, " + c.getName());
            Enumeration poe = c.preorderEnumeration();
            while (poe.hasMoreElements()) {
                Cell cc = (Cell)poe.nextElement();
                int baseTime = cc.getTime();
                Vector v = cc.getCellData();
                int size = v.size();
                if (size <= 1) continue;
                Vector jumps = new Vector();
                int sizeSum = 0;
                String name = cc.getName();
                CellData cd0 = (CellData)v.get(0);
                sizeSum += cd0.iNucleus.size;
                for (int i=1; i < size; i++) {
                    CellData cd1 = (CellData)v.get(i);
                    jumps.add(new Integer(jump(cd0, cd1)));
                    sizeSum += cd1.iNucleus.size;
                    cd0 = cd1;
                }

                // jumps is a Vector with an Integer object
                // whose value is the size of the move from timepoint to timepoint
                // there is one jump element per timepoint for this cell
                // if I decide the jump is big enough, I add something to
                // the listmodel with the cell name and time

                // John has asked instead for a list which is ordered by
                // by the size of the jump
                // to do that, I need to introduce a new stage in the analysis
                // at this point, I create a Vector of high jump size elements
                // where each element is an object containing the cell name,
                // time, and jump size

                // then I need to sort that list in big jump order

                // then I can take that Vector and produce the listmodel
                int maxTime = Integer.parseInt(iTextField.getText());
                int stdSize = sizeSum/size;
                for (int i=0; i < jumps.size(); i++) {
                    int time = baseTime + i;
                    if (time > maxTime) break;
                    double x = ((Integer)jumps.get(i)).intValue();
                    double xf = x/stdSize;
                    if (xf > TOL) {
                        String s = name + CS + time + CS + DF2.format(xf);
                        bigJumps.add(s);
                        //iListModel.addElement(s);
                        //println("     test3, " + name + CS + (cc.getTime() + i) + CS + x + CS + stdSize);
                        checkCount++;

                    }
                    //if (name.indexOf("Nuc") == 0) break;
                    //if(checkCount++ > 100) break;
                }
                //if (name.indexOf("Nuc") == 0) break;
                //if(checkCount++ > 100) break;


            }

            Collections.sort(bigJumps, this);
            for (int i=0; i < bigJumps.size(); i++) {
                String s = (String)bigJumps.get(i);
                iListModel.addElement(s);
            }


        }

        iNamesAvailable = true;

    }

    private static final double TOL = 0.7;

    private int jump(CellData cd0, CellData cd1) {
        double zPixRes = iNucleiMgr.getZPixRes();
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



    // works on the current cell
    // and it finds the distance from it to nearby cells
    // in the NEXT time point
    private void test3X() {
        // this fn will take a cell name at a time point
        // and present a list of cells in the next time point in order of
        // closeness to the named cell
        //iListModel.clear();
        iNamesAvailable = false;
        iTest = 3;
        //System.out.println("\nAnalysis5.test1 entered");
        iCellName = iAceTree.getCurrentCell().getName();
        //iTextField.setText(iCellName);
        iTime = iAceTree.getImageTime() + iAceTree.getTimeInc();
        //println("test1: " + iTime);
        Vector nuclei = (Vector)nuclei_record.get(iTime - 1);
        Vector nuclei2 = (Vector)nuclei_record.get(iTime);
        //iTime++; //point at the next time
        Nucleus nn = NucUtils.getCurrentCellData(nuclei, iCellName);
        double zPixRes = iNucleiMgr.getZPixRes();
        int x = nn.x;
        int y = nn.y;
        int z = (int)(zPixRes * nn.z);

        Vector v = new Vector();
        int k = 0;
        for (int i=0; i < nuclei2.size(); i++) {
            Nucleus n = (Nucleus)nuclei2.get(i);
            if (n.identity.length() > 0) {
                k++;
                int zz = (int)(zPixRes * n.z);
                int dx = n.x - x;
                int dy = n.y - y;
                int dz = zz - z;
                int d2 = dx * dx + dy * dy + dz * dz;
                d2 = (int)Math.round(Math.sqrt(d2));
                D u = new D(n.identity, d2, n.x, n.y, n.z, n, k, iTime + 1);
                v.add(u);
                //println(i + CS + n.identity + CS + d2);
            }

        }
        Collections.sort(v, new D());
        ImageWindow imgWin = iAceTree.getImageWindow();
        imgWin.clearAnnotations();
        iListModel.clear();
        iListModel.addElement("#list shows cells at time " + (iTime + 1) + " near " + iCellName + " at time " + iTime);
        int u = Math.min(v.size(), 10);
        for (int i=0; i < u; i++) {
            D dd = (D)v.get(i);
            imgWin.addAnnotation(dd.id, dd.x, dd.y);
            StringBuffer sb = new StringBuffer();
            //sb.append(i);
            sb.append(dd.id);
            sb.append(CS + dd.d);
            sb.append(CS + (iTime + 1));
            iListModel.addElement(sb.toString());

            //println(i + CS + dd.id + CS + dd.d + CS + dd.x + CS + dd.y + CS + dd.z);
        }
        imgWin.refreshDisplay();
        iNamesAvailable = true;
    }



    // given the current cell and current cell time t
    // list the cells close to it at that time
    // and then list the cells at time t = t-1 that are close to it
    private void test2() {
        //println("test2 entered$$");
        iTest = 2;
        iNamesAvailable = false;
        iCellName = iAceTree.getCurrentCell().getName();
        //iTextField.setText(iCellName);
        iTime = iAceTree.getImageTime() + iAceTree.getTimeInc();
        //println("test3: " + iTime);
        Vector nuclei = (Vector)nuclei_record.get(iTime - 1);
        Vector nuclei2 = null;
        if (iTime > 1) nuclei2 = (Vector)nuclei_record.get(iTime - 2);

        //println("iZPixRes: " + iNucleiMgr.getZPixRes());
        double zPixRes = iNucleiMgr.getZPixRes();
        Nucleus nn = NucUtils.getCurrentCellData(nuclei, iCellName);
        //println("nn: " + nn);

        //Nucleus p = (Nucleus)nuclei2.get(nn.predecessor - 1);
        //println("p: " + p);


        //c.showParameters();
        int x1 = nn.x;
        int y1 = nn.y;
        int z1 = (int)(zPixRes * nn.z);
        //int x2 = p.x;
        //int y2 = p.y;
        //int z2 = (int)(zPixRes * p.z);
        //println(x + CS + y + CS + z);
        Vector v = new Vector();
        Vector v2 = new Vector();
        int k = 0;
        D u = null;
        for (int i=0; i < nuclei.size(); i++) {
            Nucleus n = (Nucleus)nuclei.get(i);
            if (n.identity.length() > 0) {
                k++;
                int zz = (int)(zPixRes * n.z);
                int dx = n.x - x1;
                int dy = n.y - y1;
                int dz = zz - z1;
                int d2 = dx * dx + dy * dy + dz * dz;
                d2 = (int)Math.round(Math.sqrt(d2));
                u = new D(n.identity, d2, n.x, n.y, n.z, n, k, iTime);
                v.add(u);
            }

        }
        if (nuclei2 != null) {
            for (int i=0; i < nuclei2.size(); i++) {
                Nucleus n = (Nucleus)nuclei2.get(i); //earlier time nuclei
                if (n.identity.length() > 0) {
                    k++;
                    int zz = (int)(zPixRes * n.z);
                    int dx = n.x - x1;
                    int dy = n.y - y1;
                    int dz = zz - z1;
                    int d2 = dx * dx + dy * dy + dz * dz;
                    d2 = (int)Math.round(Math.sqrt(d2));
                    u = new D(n.identity, d2, n.x, n.y, n.z, n, k, iTime - 1);
                    v2.add(u);
                }
            }

        }
        Collections.sort(v, new D());
        if (nuclei2 != null) Collections.sort(v2, new D());
        ImageWindow imgWin = iAceTree.getImageWindow();
        imgWin.clearAnnotations();
        iListModel.clear();

        iListModel.addElement("#list shows: index, name, distance, time, plane");
        iListModel.addElement("#nuclei at time " + iTime + " near " + iCellName + " at time " + iTime);
        addItems(v, imgWin);
        if (nuclei2 != null) {
            iListModel.addElement("#nuclei at time " + (iTime - 1) + " near " + iCellName + " at time " + iTime);
            addItems(v2, imgWin);
            imgWin.refreshDisplay();
        }
        iNamesAvailable = true;
    }


    // seems to bring up a list of cells that are within a certain
    // distance of the current cell
    // I will not use this since test2() and test3() seem to give
    // essentially the same info in perhaps a better way
    private void test4() {
        println("test4 clicked");
        iNamesAvailable = false;
        iTest = 4;
        iTime = iAceTree.getImageTime() + iAceTree.getTimeInc();
        double zPixRes = iNucleiMgr.getZPixRes();
        //println("test1: " + iTime);
        Vector nuclei = (Vector)nuclei_record.get(iTime - 1);
        for (int i=0; i < nuclei.size(); i++) {
            Nucleus n1 = (Nucleus)nuclei.get(i);
            if (n1.status <= 0) continue;
            int margin = (int)Math.round(n1.size * FACTOR);
            for (int j = 0; j < nuclei.size(); j++) {
                if (j == i) continue;
                Nucleus n2 = (Nucleus)nuclei.get(j);
                if (n2.status <= 0) continue;
                int xd = Math.abs(n1.x - n2.x);
                if (xd > margin) continue;
                int yd = Math.abs(n1.y - n2.y);
                if (yd > margin) continue;
                int zd = (int)Math.round(zPixRes * Math.abs(n1.z - n2.z));
                if (zd > margin) continue;
                int d = (int)Math.round(Math.sqrt(xd*xd + yd*yd + zd*zd));
                StringBuffer sb = new StringBuffer();
                sb.append(d);
                sb.append(CS + n1.identity);
                sb.append(CS + n2.identity);
                sb.append(CS + n1.x + CS + n1.y + CS + n1.z);
                sb.append(CS + n2.x + CS + n2.y + CS + n2.z);

                iListModel.addElement(sb.toString());

            }
        }
        iNamesAvailable = true;
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

            //println(i + CS + dd.id + CS + dd.d + CS + dd.x + CS + dd.y + CS + dd.z);
        }

    }


    String [] shelp = {
            "#test1: makes a queryable list of cell deaths"
            ,"#test2: makes two queryable lists showing nearby cells at adjacent times"
            ,"#test3: makes a queryable list of cells near current cell at next time"
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


	private static final String
	 MOVETO = " m"
	,LINETO = " l"
	,RLINETO = " r"
	,NEWPATH = "n"
	,COLOR = " c"
   ,CLOSEPATH = "cp"
   ,FILL = "fill"
   ,STROKE = "s"
   ,SHOW = " show"
   ,COMMENT = " %"
   ,SP = " "
   ,LEFT = "("
   ,RITE = ")"
	;

	private static final int
		 TOP = 700
		,OFFSET = 100
		,DELTAY = 5
		,DELTAT = 1
        ,YINC = 20
        ,YOFFSET = 500
		;





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

        } else if (s.equals(TEST1)) {
            //append(TEST1);
            test1();
        } else if (s.equals(TEST2)) {
            //append(TEST2);
            test2();
        } else if (s.equals(TEST3)) {
            //append(TEST3);
            test3();
        } else if (s.equals(TEST4)) {
            //append(TEST3);
            //test4();
        } else if (s.equals(HELP)) {
            help();
            //append(TEST4);
            //test4();
        } else if (s.equals(CLEAR)) {
            iNamesAvailable = false;
            iListModel.clear();
            help();
        } else if (s.equals("jComboBoxChanged")) {
            //append("detected change");

        }
    }

    private void buildOutToolBar() {
        iToolBar.setMaximumSize(new Dimension(700,20));
        iToolBar.add(new JLabel("maxTime:"));
        iTextField = new JTextField();
        iTextField.setColumns(5);
        iTextField.setText("249");
        iToolBar.add(iTextField);
        JButton jb = null;
        jb = new JButton(CLEAR);
        addToolBarButton(jb);
        jb = new JButton(TEST1);
        addToolBarButton(jb);
        jb = new JButton(TEST2);
        addToolBarButton(jb);
        jb = new JButton(TEST3);
        addToolBarButton(jb);
        //jb = new JButton(TEST4);
        //addToolBarButton(jb);
        jb = new JButton(HELP);
        addToolBarButton(jb);
        //iCombo = new JComboBox(iModel);
        //addComboBox(iCombo);


    }

    /*
    protected JMenuBar createMenuBar() {
        iMenuBar = new JMenuBar();
        return iMenuBar;
    }
    */

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
        iCellList.setCellRenderer( new CellRenderer() );
        //putHeaders();
        //breadthFirstEnumeration();
        iCellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        iCellList.setSelectedIndex(0);
        iCellList.addListSelectionListener(this);
        iCellList.setVisibleRowCount(5);
        iScrollPane = new JScrollPane(iCellList);
        add(iScrollPane);
    }

    public void showMe() {
        //iFrame = new JFrame(iLog.iTitle);
        iFrame.setTitle("Deaths and Adjacencies Dialog");
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
        ,TEST1 = "Test1"
        ,TEST2 = "Test2"
        ,TEST3 = "Test3"
        ,TEST4 = "Test4"
        ,TEST5 = "Test5"
        ,HELP  = "Help"
        ;

    private static final String CS = ", ";
    private static void println(String s) {System.out.println(s);}
    private static final DecimalFormat DF2 = new DecimalFormat("###.##");

    public static void main(String[] args) {
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

    @SuppressWarnings("unused")
	private String getTrulySelectedCellName() {
        if (!iNamesAvailable) return null;
        int index = iCellList.getSelectedIndex();
        int k = Math.max(index, 1);
        String s = (String)iListModel.elementAt(index);
        String [] sa = s.split(", ");
        String name = sa[0];
        //iTime = Integer.parseInt(sa[1]) - 1;
        return name;
    }

    class CellRenderer extends JLabel implements ListCellRenderer {

        /* (non-Javadoc)
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        @Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            //println("getListCellRendererComponent: " + (String)value);
            String s = (String)value;

            setText(s);
            setFont(iFont);
            if (iCellName != null && s.indexOf(iCellName) >= 0) setFont( iFont.deriveFont(Font.BOLD) );
            return this;
        }

    }

/*
    // takes the cell name from the text box
    // goes to its end time
    // and lists the nearby cells to it at that time
    // note that its own distance comes up as zero
    private void test6() {
        System.out.println("\nAnalysis5.test2 entered");
        iTest = 2;
        String testName = iTextField.getText();
        Cell c = (Cell)iCellsByName.get(testName);
        int t = c.getEndTime();
        iTime = t;
        Vector nuclei = (Vector)nuclei_record.get(t - 1);
        Vector nuclei2 = (Vector)nuclei_record.get(t);
        Nucleus nn = NucUtils.getCurrentCellData(nuclei, testName);
        println("nn: " + nn);
        //c.showParameters();
        println("iZPixRes: " + iNucleiMgr.getZPixRes());
        double zPixRes = iNucleiMgr.getZPixRes();
        int x = nn.x;
        int y = nn.y;
        int z = (int)(zPixRes * nn.z);
        println(x + CS + y + CS + z);
        Vector v = new Vector();
        int k = 0;
        for (int i=0; i < nuclei.size(); i++) {
            Nucleus n = (Nucleus)nuclei.get(i);
            if (n.identity.length() > 0) {
                k++;
                int zz = (int)(zPixRes * n.z);
                int dx = n.x - x;
                int dy = n.y - y;
                int dz = zz - z;
                int d2 = dx * dx + dy * dy + dz * dz;
                d2 = (int)Math.round(Math.sqrt(d2));
                D u = new D(n.identity, d2, n.x, n.y, n.z, n, k, iTime);
                v.add(u);
                //println(i + CS + n.identity + CS + d2);
            }

        }
        Collections.sort(v, new D());
        ImageWindow imgWin = iAceTree.getImageWindow();
        imgWin.clearAnnotations();
        iListModel.clear();
        for (int i=0; i < 10; i++) {
            D dd = (D)v.get(i);
            imgWin.addAnnotation(dd.id, dd.x, dd.y);
            StringBuffer sb = new StringBuffer();
            sb.append(i);
            sb.append(CS + dd.id);
            sb.append(CS + dd.d);
            iListModel.addElement(sb.toString());

            println(i + CS + dd.id + CS + dd.d + CS + dd.x + CS + dd.y + CS + dd.z);


            if (dd.n.successor1 > 0) {
                Nucleus n2 = (Nucleus)nuclei2.get(dd.n.successor1 - 1);
                println("d1: " + n2);
            }
            if (dd.n.successor2 > 0) {
                Nucleus n2 = (Nucleus)nuclei2.get(dd.n.successor2 - 1);
                println("d2: " + n2);
            }



        }
        imgWin.refreshDisplay(null);
        iNamesAvailable = true;
    }

    // takes the cell name from the text box
    // goes to its end time
    // expects to find 2 daughters
    // makes 2 lists of cells at parent time that are
    // near each of the daughters
    private void test5() {
        println("test3 entered$$");
        iTest = 3;
        System.out.println("\nAnalysis5.test3 entered");
        println("iZPixRes: " + iNucleiMgr.getZPixRes());
        double zPixRes = iNucleiMgr.getZPixRes();
        String testName = iTextField.getText();
        Cell c = (Cell)iCellsByName.get(testName);
        int t = c.getEndTime();
        iTime = t;
        Vector nuclei = (Vector)nuclei_record.get(t - 1);
        Vector nuclei2 = (Vector)nuclei_record.get(t);
        Nucleus nn = NucUtils.getCurrentCellData(nuclei, testName);
        println("nn: " + nn);

        Nucleus dd1 = (Nucleus)nuclei2.get(nn.successor1 - 1);
        Nucleus dd2 = (Nucleus)nuclei2.get(nn.successor2 - 1);
        println("d1: " + dd1);
        println("d2: " + dd2);


        //c.showParameters();
        int x1 = dd1.x;
        int y1 = dd1.y;
        int z1 = (int)(zPixRes * dd1.z);
        int x2 = dd2.x;
        int y2 = dd2.y;
        int z2 = (int)(zPixRes * dd2.z);
        //println(x + CS + y + CS + z);
        Vector v = new Vector();
        Vector v2 = new Vector();
        int k = 0;
        D u = null;
        for (int i=0; i < nuclei.size(); i++) {
            Nucleus n = (Nucleus)nuclei.get(i);
            if (n.identity.length() > 0) {
                k++;
                int zz = (int)(zPixRes * n.z);
                int dx = n.x - x1;
                int dy = n.y - y1;
                int dz = zz - z1;
                int d2 = dx * dx + dy * dy + dz * dz;
                d2 = (int)Math.round(Math.sqrt(d2));
                u = new D(n.identity, d2, n.x, n.y, n.z, n, k, iTime - 1);
                v.add(u);
                zz = (int)(zPixRes * n.z);
                dx = n.x - x2;
                dy = n.y - y2;
                dz = zz - z2;
                d2 = dx * dx + dy * dy + dz * dz;
                d2 = (int)Math.round(Math.sqrt(d2));
                u = new D(n.identity, d2, n.x, n.y, n.z, n, k, iTime - 1);
                v2.add(u);
                //println(i + CS + n.identity + CS + d2);
            }

        }
        Collections.sort(v, new D());
        Collections.sort(v2, new D());
        ImageWindow imgWin = iAceTree.getImageWindow();
        imgWin.clearAnnotations();
        iListModel.clear();

        println("using: " + dd1);
        addItems(v, imgWin);
        println("using: " + dd2);
        addItems(v2, imgWin);
        imgWin.refreshDisplay(null);
        iNamesAvailable = true;
    }


*/
}
