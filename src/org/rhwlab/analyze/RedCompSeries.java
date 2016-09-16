/*
 * Created on Jan 3, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.Log;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RedCompSeries extends Log {

    AceTree         iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    Hashtable       iNucleiMgrHash;
    //Identity        iIdentity;

    private JTextField iTextField;
    private JMenuItem   iConfigList;
    private JMenuItem   iExit;
    private JMenu       iMenu;

    public RedCompSeries(String title) {
        super(title);
        showMe();
        buildOutToolBar();
        createMenu();
        initialize();
    }

    private void test1() {
        println("test1");
        Enumeration keys = iNucleiMgrHash.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            println("test1, " + key);
            iNucleiMgr = (NucleiMgr)iNucleiMgrHash.get(key);
            println("test1, " + iNucleiMgr);

        }

    }

    private void test2() {
        println("test2");
    }

    private void test3() {
        println("test3");
    }


    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals(TEST1)) {
            append(TEST1);
            test1();
        } else if (s.equals(TEST2)) {
            //append(TEST2);
            test2();
        } else if (s.equals(TEST3)) {
            //append(TEST3);
            test3();
        } else if (s.equals(CLEAR)) {
            iText.setText("");
        } else if (s.equals(EXIT)) {
            iFrame.dispose();
        } else super.actionPerformed(e);
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
        iNucleiMgrHash = iAceTree.getNucleiMgrHash();
    }

    private void createMenu() {
        //JMenu menu = new JMenu("File");
        //add(menu);
        iMenu = new JMenu("File");
        iMenuBar.add(iMenu);
        iConfigList = new JMenuItem(CONFIGS);
        iExit = new JMenuItem(EXIT);
        iMenu.add(iConfigList);
        iMenu.add(iExit);
        iConfigList.addActionListener(this);
        iExit.addActionListener(this);

        //add(iMenu);

    }

    private void buildOutToolBar() {
        iToolBar.setMaximumSize(new Dimension(500,20));
        iToolBar.add(new JLabel("prefix:"));
        iTextField = new JTextField();
        iTextField.setColumns(15);
        iTextField.setText("55");
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
    }

    private static final String
    CLEAR = "Clear"
   ,LINE  = "                                        "
   ,TEST1 = "Test1"
   ,TEST2 = "Test2"
   ,TEST3 = "Test3"
   ,EXIT  = "Exit"
   ,CONFIGS = "ConfigFiles"
   ;
    public static void main(String[] args) {
    }
    private static void println(String s) {System.out.println(s);}
    private static final String CS = ", ", CNS = ",";
}
