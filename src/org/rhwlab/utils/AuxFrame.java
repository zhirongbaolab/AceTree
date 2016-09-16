/*
 * Created on Jun 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.tree.CanonicalTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.tree.SulstonTree;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AuxFrame extends JPanel implements ActionListener, 
        TreeSelectionListener {
    public String iTitle;
    private JFrame iFrame;
    private JToolBar iToolBar;
    private JTree iTree;
    private AceTree iAceTree;
    private Cell iRoot;
    private Cell iCurrentCell;
    private CanonicalTree iCanonicalTree;
    
    public AuxFrame(AceTree aceTree, String title, CanonicalTree canonicalTree) {
        iTitle = title;
        iAceTree = aceTree;
        iCanonicalTree = canonicalTree;
        iRoot = iCanonicalTree.getRoot();
        iTree = new JTree(iRoot);
        iCurrentCell = iRoot;
        iTree.addTreeSelectionListener(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,10));
        JButton jb1 = new JButton("Sulston style tree");
        jb1.addActionListener(this);
        iToolBar.add(jb1);
        add(iToolBar);
        displayTree();
        //expandTree();

        
        iFrame = new JFrame(iTitle);
        showMe();

    }
    
    private void displayTree() {
        iTree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setOpenIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setClosedIcon(null);
        ((DefaultTreeCellRenderer)(iTree.getCellRenderer())).setLeafIcon(null);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel treev = new JPanel();
        treev.setLayout(new BorderLayout());
        JScrollPane treeView = new JScrollPane(iTree);
        treeView.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        treeView.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        treev.add(treeView);
        add(treev);
    }
        
    public void expandTree() {
        Cell c = (Cell)iRoot.getFirstLeaf();
        while (c != null) {
            showTreeCell(c);
            c = (Cell)c.getNextLeaf();
        }
    }

    private void showTreeCell(Cell c) {
        TreeNode [] tna = c.getPath();
        TreePath tp = new TreePath(tna);
        iTree.makeVisible(tp);
        int row = iTree.getRowForPath(tp);
        iTree.setSelectionInterval(row,row);
        iTree.scrollRowToVisible(row);
    }

    public void showMe() {
        iFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        iFrame.setContentPane(this);
        iFrame.pack();
        iFrame.setVisible(true);
    }
    
    @Override
	public void actionPerformed(ActionEvent e) {
        int endingIndex = Cell.getEndingIndex();
        Cell.setEndingIndexS(Cell.LARGEENDTIME);
        Cell.setEndingIndexS(endingIndex);
        new SulstonTree(iCanonicalTree, iTitle, iCurrentCell, false, null);
        //println("AuxFrame.actionPerformed1: " + iCurrentCell + CS + endingIndex + CS + Cell.LARGEENDTIME);
        Cell.setEndingIndexS(endingIndex);
    }
    
    
    @Override
	public void valueChanged(TreeSelectionEvent e) {
        //System.out.println("treeSelectionChanged: " + e.getPath().getLastPathComponent());
        iCurrentCell = (Cell)e.getPath().getLastPathComponent();
    }

    private static final int
         WIDTH = 300
        ,HEIGHT = 400
        ;

     private static void println(String s) {System.out.println(s);}
     private static final String CS = ", ";

}
