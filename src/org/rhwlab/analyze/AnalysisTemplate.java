/*
 * Created on Nov 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
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
public class AnalysisTemplate extends Log implements Runnable {
    public JTextField iTextField1;
    public JTextField iTextField2;
    public JTextField iTextField3;
    //private JCheckBox iCheckBox;
    public AceTree         iAceTree;
    public AncesTree       iAncesTree;
    public NucleiMgr       iNucleiMgr;
    public Vector          nuclei_record;
    public Cell            iRoot;
    public Hashtable       iCellsByName;
    //public Identity        iIdentity;

    public AnalysisTemplate() {
        super("Plugin");
    }

    @Override
	public void run() {
        //System.out.println("AnalysisTemplate.run() entered");
        //System.out.println("TestPlugin.run() entered");
        showMe();
        buildOutToolBar();

        append("what's up doc?");

    }

    public void test1() {

    }

    public void test2() {

    }

    public void initialize() {
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        nuclei_record = iNucleiMgr.getNucleiRecord();
        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        //iIdentity = iNucleiMgr.getIdentity();
        //iRoot = iNucleiMgr.getRoot();
    }



    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals(TEST1)) {
            test1();
        } else if (s.equals(TEST2)) {
            test2();
        } else if (s.equals(CLEAR)) {
            append("clear");
            iText.setText("");
        } else super.actionPerformed(e);
    }


    public void buildOutToolBar() {
        iToolBar.setMaximumSize(new Dimension(700,20));
        iToolBar.add(new JLabel("time:"));
        iTextField1 = new JTextField();
        iTextField1.setColumns(15);
        iTextField1.setText("1");
        iToolBar.add(iTextField1);
        iTextField2 = new JTextField();
        iTextField2.setColumns(15);
        iTextField2.setText("100");
        iToolBar.add(iTextField2);
        iTextField3 = new JTextField();
        iTextField3.setColumns(15);
        iTextField3.setText("MS");
        iToolBar.add(iTextField3);
        //iCheckBox = new JCheckBox("show header");
        //iToolBar.add(iCheckBox);
        JButton jb = null;
        jb = new JButton(CLEAR);
        addToolBarButton(jb);
        jb = new JButton(TEST1);
        addToolBarButton(jb);
        jb = new JButton(TEST2);
        addToolBarButton(jb);
        //jb = new JButton(TEST3);
        //addToolBarButton(jb);


    }

    private static final String
    CLEAR = "Clear"
   ,TEST1 = "Test1"
   ,TEST2 = "Test2"
   ,TEST3 = "Test3"
   ;


    public static void main(String[] args) {
    }
}
