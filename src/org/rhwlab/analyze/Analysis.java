/*
 * Created on May 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.Identity3;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.CanonicalTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.CleanString;
import org.rhwlab.utils.EUtils;
import org.rhwlab.utils.Log;


/**
 * Contains a UI and three active functions to report on a lineage:
 * Overview
 * Identity check
 * Fate check
 *
 * @author biowolp
 * @version 1.1 November 30, 2005
 *
 */
public class Analysis extends Log {
    AceTree         iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    Vector          iDied;
    Vector          iDividedTooSoon;
    Vector          iAliveTooOld;
    Vector          iAllCells;


    public Analysis(AceTree aceTree, String title) {
        super(title);
        iAceTree = aceTree;
        //System.out.println("Analysis constructor");
        showMe();
        getFrame().setJMenuBar(createMenuBar());
        JButton jb = null;
        jb = new JButton(CLEAR);
        addToolBarButton(jb);
        /*
        jb = new JButton(TEST1);
        addToolBarButton(jb);
        jb = new JButton(TEST2);
        addToolBarButton(jb);
        jb = new JButton(TEST3);
        addToolBarButton(jb);
        jb = new JButton(TEST4);
        addToolBarButton(jb);
        jb = new JButton(TEST5);
        addToolBarButton(jb);
        jb = new JButton(TEST6);
        addToolBarButton(jb);
        */
        initialize();

    }

    public Analysis() {
        super("");

    }

    public void initialize() {
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        nuclei_record = iNucleiMgr.getNucleiRecord();
        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        //iIdentity = iNucleiMgr.getIdentity();
        //iRoot = iNucleiMgr.getRoot();
        //iRoot = iAncesTree.getRootCells();
        iRoot = iAncesTree.getRoot();
    }

    // overview
    public void test1() {
        initialize();
        append("config file: " + iNucleiMgr.getConfig().iConfigFileName);
        append("nuclei_record length: " + nuclei_record.size());
        append("series ending index: " + iNucleiMgr.getEndingIndex());
        append("cells hash size: " + iCellsByName.size());
        append("tree leaves: " + iRoot.getLeafCount());
        append("tree leaves: " + iAceTree.getRoot().getLeafCount());
        append("live cells at end: " + countLiveCells(iNucleiMgr.getEndingIndex()));
        showLeaves();
    }

    private int countLiveCells(int time) {
        int count = 0;
        Vector nuclei = (Vector)nuclei_record.elementAt(time - 1);
        for (int j=0; j < nuclei.size(); j++) {
            Nucleus n = (Nucleus)nuclei.elementAt(j);
            if (n.status >= 0) {
                count++;
                if (time == nuclei_record.size()) {
                    Cell c = (Cell)iCellsByName.get(n.identity);
                    //System.out.println("countLiveCells: " + c + CS + n.identity + CS + time);
                    if (c.getFateInt() != Cell.ALIVE) append(c + CS + c.getFate());
                }
            }
        }
        return count;
    }

    @SuppressWarnings("unused")
	private void showLeaves() {
        Cell c = (Cell)iRoot.getFirstLeaf();
        int fate = -1;
        int deaths = 0;
        int alive = 0;
        int dividing = 0;
        int i = 0;
        do {
            i++;
            fate = c.getFateInt();
            if (fate == Cell.ALIVE || fate == Cell.DIVIDED) alive++;
            else if (fate == Cell.DIED) deaths++;
            //append(i + CS + c + CS + c.fates[fate]);
        } while ((c = (Cell)c.getNextLeaf()) != null);
        append("summary, alive=" + alive + ", died=" + deaths);
    }

    @SuppressWarnings("unused")
	private void breadthFirstEnumeration() {
        Enumeration e = iRoot.breadthFirstEnumeration();
        //Enumeration e = iRoot.preorderEnumeration();
        //Enumeration e = iRoot.postorderEnumeration();
        int i = 0;
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            categorize(c);
            //append(i++ + CS + c + CS + c.getTime() + CS + c.getLifeTime() +
            //        CS + c.getFate());
        }
    }

    private void categorizeCells() {
        Enumeration e = iCellsByName.elements();
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            categorize(c);
        }
    }

    private void cellCountVersesTime() {
        for (int time = 1; time <= nuclei_record.size(); time++) {
            append(time + CS + countLiveCells(time));
        }
    }

    private void categorize(Cell c) {
        String name = c.getName();
        int fate = c.getFateInt();
        if (fate == Cell.DIED) {
            iDied.add(name);
            return;
        }
        int lifeTime = c.getLifeTime();
        if (fate == Cell.DIVIDED) {
            if (lifeTime < SHORTLIFE) {
                iDividedTooSoon.add(name);
                return;
            }
        } else if (lifeTime > LONGLIFE) {
            iAliveTooOld.add(name);
        }
    }

    // 20051116 not called
    private void test3() {
        int found = 0;
        int notFound = 0;
        CanonicalTree ct = iAceTree.getCanonicalTree();
        Vector sortedCellNames = ct.getSortedCellNames();
        Hashtable ourCells = iAncesTree.getCellsByName();
        /*
        Enumeration e2 = ourCells.keys();
        while (e2.hasMoreElements()) {
            append(e2.nextElement().toString());
        }
        */
        Enumeration e = sortedCellNames.elements();
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            if (!ourCells.containsKey(name)) {
                append(name);
                notFound++;
            } else found++;
        }
        append("found: " + found + CS + "notFound: " + notFound);

    }

    // identity check (are names canonical?)
    @SuppressWarnings("unused")
	private void test4() {
        // reports names we have that are non canonical
        //System.out.println("test4");
        initialize();
        //iCellsByName = iAncesTree.getCellsByName();
        iAllCells = new Vector();
        int found = 0;
        int notFound = 0;
        CanonicalTree ct = iAceTree.getCanonicalTree();
        Hashtable canonicalCellsHash = ct.getCellsHash();
        Hashtable ourCells = iAncesTree.getCellsByName();
        Enumeration e = iCellsByName.elements();
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            iAllCells.add(c.getName());
        }
        //System.out.println("iAllCells.size: " + iAllCells.size());
        Collections.sort(iAllCells);
        e = iAllCells.elements();
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            if (name.indexOf("polar") == 0) continue;
            if (!canonicalCellsHash.containsKey(name)) {
                String parentName = name.substring(0, name.length() - 1);
                //append("name, parentName: " + name + CS + parentName);
                Cell parent = (Cell)canonicalCellsHash.get(parentName);
                String report = "";
                if (parent == null) report = " error is earlier in lineage";
                else {
                    String dau0 = "";
                    String dau1 = "";
                    int children = parent.getChildCount();
                    if (children > 0) {
                        dau0 = ((Cell)parent.getChildAt(0)).getName();
                        if (children > 1) {
                            dau1 = ((Cell)parent.getChildAt(1)).getName();
                        }
                    }
                    report = " vs parent " + parentName + " with " + children + " children: " + dau0 + CS + dau1;
                }
                if (report.length() > 0) {
                    append(name + report);
                    notFound++;
                }
            } else found++;
        }
        append("found: " + found + CS + "notFound: " + notFound);


    }

    // 20051116 not called
    @SuppressWarnings("unused")
	private void test5() {
        append("\ncollect data for new sister naming approach");
        append("fields: cellname, birth, end, cellCountAtBirth,");
        append("fields: x1, y1, z1, d1, x2, y2, z2, d2, x3, y3, z3, d3");
        append("where x1, ... denotes position and size at birth");
        append("where x2, .,. denotes position and size at birth + 2");
        append("where x3, ... denotes position and size at end");
        iAllCells = new Vector();
        int found = 0;
        int notFound = 0;
        CanonicalTree ct = iAceTree.getCanonicalTree();
        Hashtable canonicalCellsHash = ct.getCellsHash();
        Hashtable ourCells = iAncesTree.getCellsByName();
        Enumeration e = ourCells.elements();
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            iAllCells.add(c.getName());
        }
        Collections.sort(iAllCells);
        e = iAllCells.elements();
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            Cell c = (Cell)ourCells.get(name);
            CellData cd = new CellData();
            cd.name = name;
            cd.birth = c.getTime();
            cd.end = c.getEndTime();
            cd.cellCount = countCells(cd.birth);
            Nucleus n;
            n = iNucleiMgr.getCurrentCellData(name, cd.birth);
            cd.x1 = n.x;
            cd.y1 = n.y;
            cd.z1 = (int)(n.z * iNucleiMgr.getZPixRes());
            cd.d1 = n.size;
            n = iNucleiMgr.getCurrentCellData(name, cd.birth + 2);
            cd.x2 = n.x;
            cd.y2 = n.y;
            cd.z2 = (int)(n.z * iNucleiMgr.getZPixRes());
            cd.d2 = n.size;
            n = iNucleiMgr.getCurrentCellData(name, cd.end);
            cd.x3 = n.x;
            cd.y3 = n.y;
            cd.z3 = (int)(n.z * iNucleiMgr.getZPixRes());
            cd.d3 = n.size;
            append(cd.toString());


        }

    }

    /*
    // 20051116 obsolete and not called -- no entries in relativePositionHash
    private void test6() {
        Hashtable relPosHash = iNucleiMgr.getIdentity().getRelativePositionHash();
        Enumeration e = relPosHash.keys();
        append("relPosHash has " + relPosHash.size() + " entries.");
        Vector ones = new Vector();
        Vector twos = new Vector();
        Vector threes = new Vector();
        while(e.hasMoreElements()) {
            String s = (String)e.nextElement();
            Integer kI = (Integer)relPosHash.get(s);
            int k = kI.intValue();
            switch(k) {
            case 1:
            case -1:
                ones.add(s); break;
            case 2:
            case -2:
                twos.add(s); break;
            case 3:
            default:
                threes.add(s);
            }
            //if (Math.abs(k) != 1) append(s + C.CS + k);
        }
        append(ones.size() + " cells named by x axis relative position.");
        append(twos.size() + " cells named by y axis relative position");
        e = twos.elements();
        while (e.hasMoreElements()) append((String)e.nextElement());
        append(threes.size() + " cells named by z axis relative position");
        e = threes.elements();
        while (e.hasMoreElements()) append((String)e.nextElement());
    }
    */

    @SuppressWarnings("unused")
	private void examineDivisions() {
        int count = 0;
        int diffs = 0;
        int comps = 0;
        Hashtable h = new Hashtable();
        Identity3 identity = iNucleiMgr.getIdentity();
        Hashtable cells = CanonicalTree.getCanonicalTree().getCellsHash();
        Enumeration e = iCellsByName.keys();
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            String sis = identity.makeSisterName(name);
            Cell canon = (Cell)cells.get(name);
            Cell parent = (Cell)canon.getParent();
            if (parent == null) continue;

            if(h.containsKey(sis)) continue;
            h.put(sis, name);
            Cell ours = (Cell)iCellsByName.get(name);
            CleanString cs = new CleanString();
            cs.insertText(name);
            cs.setPosition(15);
            cs.insertX(canon.getTime());
            cs.insertX(canon.getEndTime());
            cs.insertX(ours.getTime());
            cs.insertX(ours.getEndTime(), false);

        }

    }

    private int countCells(int time) {
        Vector nuclei = iNucleiMgr.getNuclei(time);
        Enumeration e = nuclei.elements();
        int count = 0;
        while (e.hasMoreElements()) {
            Nucleus n = (Nucleus)e.nextElement();
            if (n.status > 0) count++;
        }
        return count;
    }

    public class CellData {
        StringBuffer iSB;
        int iPos;

        String name;
        int birth;
        int end;
        int cellCount;
        int x1, y1, z1, d1;
        int x2, y2, z2, d2;
        int x3, y3, z3, d3;

        public CellData() {
            char[] chars = new char[50];
            Arrays.fill(chars, ' ');
            iSB = new StringBuffer(new String(chars));
            iPos = 0;
        }

        @Override
		public String toString() {
            insertText(name);
            iPos = 15;
            insertX(birth);
            insertX(end);
            insertX(cellCount);
            insertX(x1);
            insertX(y1);
            insertX(z1);
            insertX(d1);
            insertX(x2);
            insertX(y2);
            insertX(z2);
            insertX(d2);
            insertX(x3);
            insertX(y3);
            insertX(z3);
            insertX(d3, false);
            //System.out.println("buffer length: " + iSB.length());

            return iSB.toString();
        }



        private void insertText(String text) {
            int k = iPos + text.length();
            iSB.replace(iPos, k, text);
            iPos = k;
            k = iPos + CS.length();
            iSB.replace(iPos, k, CS);
            iPos = k;
        }


        private void insertX(int x) {
            insertX(x, true);
        }

        private void insertX(int x, boolean putComma) {
            String s = EUtils.makePaddedInt(x);
            int k = iPos + s.length();
            iSB.replace(iPos, k, s);
            if (putComma) {
                iPos = k;
                addComma();
            }
        }

        private void addComma() {
            int k = iPos + CS.length();
            iSB.replace(iPos, k, CS);
            iPos = k;
        }
    }


    // fate check
    private void test2() {
        append("\nTest2 " + new GregorianCalendar().getTime().toString());
        iAncesTree = iAceTree.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        iDied = new Vector();
        iDividedTooSoon = new Vector();
        iAliveTooOld = new Vector();
        //breadthFirstEnumeration();
        categorizeCells();
        Collections.sort(iDied);
        int nDied = iDied.size();
        append("died: " + nDied);
        for (int i=0; i < nDied; i++) {
            String c = (String)iDied.elementAt(i);
            append(c);
        }
        Collections.sort(iDividedTooSoon);
        int nDividedTooSoon = iDividedTooSoon.size();
        append("divided early: " + nDividedTooSoon + " (lived < " + SHORTLIFE + ")");
        for (int i=0; i < nDividedTooSoon; i++) {
            String c = (String)iDividedTooSoon.elementAt(i);
            append(c);
        }
        int nAliveTooOld = iAliveTooOld.size();
        Collections.sort(iAliveTooOld);
        append("alive too long: " + nAliveTooOld + " (lived > " + LONGLIFE + ")");
        for (int i=0; i < nAliveTooOld; i++) {
            String c = (String)iAliveTooOld.elementAt(i);
            append(c);
        }



    }

    @Override
	protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        //JMenu menu = new JMenu(FILE);
        //menuBar.add(menu);
        //JMenuItem test = new JMenuItem(SAVEAS);
        //menu.add(test);
        //test.addActionListener(this);
        JMenu menu = null;
        JMenuItem test = null;

        menu = new JMenu(ACTIONS);
        menuBar.add(menu);
        test = new JMenuItem(OVERVIEW);
        menu.add(test);
        test.addActionListener(this);
        test = new JMenuItem(IDENTITYCHECK);
        menu.add(test);
        test.addActionListener(this);
        test = new JMenuItem(FATECHECK);
        menu.add(test);
        test.addActionListener(this);
        //test = new JMenuItem(DIVISIONCHECK);
        //menu.add(test);
        //test.addActionListener(this);


        return menuBar;
    }



    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals(OVERVIEW)) {
            append(NL + OVERVIEW);
            test1();
        }
        else if (s.equals(IDENTITYCHECK)) {
            append(NL + IDENTITYCHECK);
            test4();
        }
        else if (s.equals(FATECHECK)) {
            append(NL + FATECHECK);
            test2();
        }
        else if (s.equals(DIVISIONCHECK)) {
            append(NL + DIVISIONCHECK);
            //test6();
        }
        else if (s.equals(TEST1)) {
            append(TEST1);
            test1();
        } else if (s.equals(CLEAR)) {
            iText.setText("");
        } else if (s.equals(TEST2)) {
            test2();
        } else if (s.equals(TEST3)) {
            test3();

        } else if (s.equals(TEST4)) {
            test4();
        } else if (s.equals(TEST5)) {
            test5();
        } else if (s.equals(TEST6)) {
            //test6();
        } else {
            super.actionPerformed(e);
        }
    }

    private static final String
         TEST1 = "Test1"
        ,TEST2 = "Test2"
        ,TEST3 = "Test3"
        ,TEST4 = "Test4"
        ,TEST5 = "Test5"
        ,TEST6 = "Test6"
        ,CLEAR = "Clear"
        ,SAVEAS = "Save as"
        ,OVERVIEW = "Overview"
        ,IDENTITYCHECK = "IdentityCheck"
        ,FATECHECK = "FateCheck"
        ,DIVISIONCHECK = "DivisionCheck"
        ,ACTIONS = "Actions"
        ,FILE = "File"
        ,CS = ", "
        ,NL = "\n"
        ;

    private static final int
         SHORTLIFE = 15
        ,LONGLIFE = 50
        ;

    public static void main(String[] args) {
        Analysis a = new Analysis();
        CellData cd = a.new CellData();
        cd.name = "ABalp";
        cd.birth = 20;
        cd.end = 40;
        System.out.println(cd);
        cd = a.new CellData();
        cd.name = "ABala";
        cd.birth = 20;
        cd.end = 42;
        System.out.println(cd);

    }
}
