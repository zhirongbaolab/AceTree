/*
 * Created on Aug 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.nucedit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Position;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;

/**
 * @author biowolp
 *
 */
public class EditTraverse implements ActionListener, ListSelectionListener {
    private JFrame              iFrame;
    private String              iTitle;
    private JPanel              iPanel;
    private JToolBar            iToolBar;
    private JTextField          iRootCell;
    private JList               iCellList;
    private DefaultListModel    iListModel;
    private JScrollPane         iScrollPane;
    private JButton             iTraverse;
    private JButton             iSaveAs;
    
    private AceTree             iAceTree;
    private NucleiMgr           iNucleiMgr;
    private AncesTree           iAncesTree;
    private Cell                iRoot;
    private Hashtable           iCellsByName;
    private Vector              nuclei_record;
    private boolean             iIgnoreEvents;
    private String              iCurrentCellName;
    private boolean             iLocalTest = false;
    
    public EditTraverse(Cell root) {
        //System.out.println("EditTraverse constructor entered"); 
        if(!iLocalTest) initialize();
        if (root != null) {
            iRoot = root;
        }
        
        iTitle = "EditTraverse";
        iFrame = new JFrame(iTitle);
        buildToolBar();
        //iCellList = new JList(iListModel);
        iIgnoreEvents = true;
        buildList();
        iPanel = new JPanel();
        iPanel.setLayout(new BoxLayout(iPanel, BoxLayout.Y_AXIS));
        iPanel.add(iToolBar);
        iPanel.add(iScrollPane);
        iFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        iFrame.setContentPane(iPanel);
        iFrame.pack();
        iFrame.setVisible(true);
        WinEventMgr wem = new WinEventMgr();
        iFrame.addWindowListener(wem);
        iFrame.addWindowFocusListener(wem);
        setKeyboardActions();
        iIgnoreEvents = true;
        iCellList.addListSelectionListener(this);
        iCellList.setSelectedIndex(1);
        iCellList.requestFocus();
        traverseAction();
        //System.out.println("EditTraverse constructor exiting"); 

    }
    
    public void initialize() {
        //System.out.println("initialize entered");
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        nuclei_record = iNucleiMgr.getNucleiRecord();
        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        //iIdentity = iNucleiMgr.getIdentity();
        //iRoot = iNucleiMgr.getRoot();
    }
    
    private void breadthFirstEnumeration() {
        //System.out.println("breadthFirstEnumeration called");
        //new Throwable().printStackTrace();
        initialize();
        String cellName = iRootCell.getText();
        iCurrentCellName = cellName;
        Cell root = null;
        if (!cellName.equals(AceTree.ROOTNAME))
            root = (Cell)iCellsByName.get(cellName);
        else root = iAncesTree.getRoot();
        iIgnoreEvents = true;
        iListModel.clear();
        putHeaders();
        Enumeration e = root.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            String name = c.getName();
            char [] ca = new char[60];
            Arrays.fill(ca, ' ');
            StringBuffer sb = new StringBuffer(new String(ca));
            sb.replace(0, name.length() - 1, name);
            String fate = c.getFate();
            sb.replace(15, 15 + fate.length() - 1, fate);
            String lifetime = String.valueOf(c.getLifeTime());
            sb.replace(25, 25 + lifetime.length() - 1, lifetime);
            int count = iAncesTree.getCellCount(c.getEndTime());
            String scount = String.valueOf(count);
            sb.replace(35, 35 + scount.length() - 1, scount);
            String sEndTime = String.valueOf(c.getEndTime());
            sb.replace(42, 42 + sEndTime.length() - 1, sEndTime);
            String lineage = lineage(name);
            sb.replace(50, 50 + lineage.length() - 1, lineage);
            iListModel.addElement(sb.toString());
        }
        iIgnoreEvents = false;
        //System.out.println("breadthFirstEnumeration exiting");

    }

    
    private String lineage(String name) {
        if (name.indexOf("ABa") == 0) return "ABa";
        if (name.indexOf("ABp") == 0) return "ABp";
        if (name.indexOf("E") == 0) return "E";
        if (name.indexOf("MS") == 0) return "MS";
        if (name.indexOf("C") == 0) return "C";
        if (name.indexOf("D") == 0) return "D";
        return "P";
        
    }
    
    private void putHeaders() {
        //System.out.println("putHeaders entered");

        char [] ca = new char[60];
        Arrays.fill(ca, ' ');
        StringBuffer sb = new StringBuffer(new String(ca));
        String name = "cellName";
        sb.replace(0, name.length() - 1, name);
        String fate = "fate";
        sb.replace(15, 15 + fate.length() - 1, fate);
        String lifetime = "duration";
        sb.replace(25, 25 + lifetime.length() - 1, lifetime);
        //int count = iAncesTree.getCellCount(c.getEndTime());
        String scount = "cells";
        sb.replace(35, 35 + scount.length() - 1, scount);
        String sEndTime = "endTime";
        sb.replace(42, 42 + sEndTime.length() - 1, sEndTime);
        String sLineage = "lineage";
        sb.replace(50, 50 + sLineage.length() - 1, sLineage);
        iListModel.addElement(sb.toString());
        //System.out.println("putHeaders exiting");
        
    }
    
    private void buildList() {
        //System.out.println("buildList called");
        //Create the list and put it in a scroll pane.
        iListModel = new DefaultListModel();
        iCellList = new JList(iListModel);
        iCellList.setFont(new Font("courier", Font.PLAIN, 16));
        //putHeaders();
        breadthFirstEnumeration();
        //iCellList = new JList(iListModel);
        iCellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        iCellList.setSelectedIndex(0);
        iCellList.addListSelectionListener(this);
        iCellList.setVisibleRowCount(5);
        iScrollPane = new JScrollPane(iCellList);
        //if (iLocalTest) testKeyStrokeControl();
        //System.out.println("buildList exiting");

        
    }

    
    private void buildToolBar() {
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,10));
        iToolBar.setMaximumSize(new Dimension(500,20));
        iToolBar.add(new JLabel("cell:"));
        iRootCell = new JTextField();
        iRootCell.setColumns(15);
        if (iRoot != null) iRootCell.setText(iRoot.getName());
        else iRootCell.setText("ABa");
        iToolBar.add(iRootCell);
        iTraverse = new JButton("Traverse");
        iTraverse.addActionListener(this);
        iToolBar.add(iTraverse);
        iSaveAs = new JButton("SaveAs");
        iSaveAs.addActionListener(this);
        iToolBar.add(iSaveAs);
        
        
    }

    private class WinEventMgr extends WindowAdapter {
        @Override
		public void windowGainedFocus(WindowEvent e) {
            //System.out.println("windowGainedFocus: " + getTitle(e));
            //refreshDisplay(null);
        }
        @Override
		public void windowClosing(WindowEvent e) {
            iFrame.dispose();
            if (!iLocalTest) iAceTree.setEditTraverseNull();
        }
    }
    
    @SuppressWarnings("unused")
	public static void main(String[] args) {
        //System.out.println("EditTraverse main");
        EditTraverse et = new EditTraverse(null);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        //System.out.println("actionCommand: " + s);
        //int index = iCellList.getSelectedIndex();
        //iListModel.remove(index);
        if (s.equals("Traverse")) {
            //System.out.println("traverse action");
            traverseAction();
        } else if(s.equals("SaveAs")) {
            saveAs();
        }
        
    }
    
    private void traverseAction() {
        //System.out.println("traverseAction called");
        breadthFirstEnumeration();
        iCellList.setSelectedIndex(1);
        iCellList.ensureIndexIsVisible(0);
        iCellList.requestFocus();
        //System.out.println("traverseAction exiting");
    }


    private String extractNameFromListEntry(int k) {
        String name = (String)iListModel.elementAt(k);
        name = name.split("\\s+")[0];
        return name;
    }
    
    @SuppressWarnings("unused")
	private String getTrulySelectedCellName() {
        int index = iCellList.getSelectedIndex();
        int k = Math.max(index, 1);
        String name = (String)iListModel.elementAt(index);
        name = name.split("\\s+")[0];
        if (name.equals(AceTree.ROOTNAME)) {
            index++;
            iIgnoreEvents = true;
            iCellList.setSelectedIndex(index);
            iIgnoreEvents = false;
            name = (String)iListModel.elementAt(index);
            name = name.split("\\s+")[0];
        }
        if (index > 0) iCurrentCellName = (String)iListModel.elementAt(index - 1);
        else iCurrentCellName = name;
        iCurrentCellName = iCurrentCellName.split("\\s+")[0];
        return name;
    }
    
    
    private void bringUpImageWindow(String name) {
        //System.out.println("bringUpImageWindow entered: " + name);
        Cell c = (Cell)iCellsByName.get(name);
        //iCurrentCellName = s;
        int time = c.getEndTime() - 1;
        Vector v = new Vector();
        v.add("InputCtrl1");
        v.add(String.valueOf(time));
        v.add(name);
        iAceTree.forceTrackingOn();
        iAceTree.controlCallback(v);
        iAceTree.nextImage();
        iAceTree.setFocusHome();
        //System.out.println("bringUpImageWindow exiting");
        
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
	public void valueChanged(ListSelectionEvent e) {
        //System.out.println("valueChanged entered");
        if (iIgnoreEvents) return;
        //System.out.println("valueChanged passed ignore events: " + e);
        if (e.getValueIsAdjusting() == false) {
            String s = getTrulySelectedCellName();
            bringUpImageWindow(s);
        }
        //System.out.println("valueChanged exiting");
        
    }
    
    private int getIndexOfName(String name) {
        return iCellList.getNextMatch(name, 0, Position.Bias.Forward );
    }
    
    public void buildNotification() {
        //System.out.println("acetree rebuilt - saveName: " + iCurrentCellName);
        String saveName = iCurrentCellName;
        breadthFirstEnumeration();
        //if (iListModel.contains(saveName)) {
            //System.out.println("has name: " + saveName);
            //int j = iListModel.indexOf(saveName);
            int j = getIndexOfName(saveName);
            iCellList.setSelectedIndex(j);
            iCellList.ensureIndexIsVisible(j);
            //bringUpImageWindow(saveName);
        //}
    }
    
    @SuppressWarnings("unused")
	private void setKeyboardActions() {
        String s = null;
        String sl = "LEFT";
        Action left = new AbstractAction() {
            @Override
			public void actionPerformed(ActionEvent e) {
                //System.out.println("AbstractAction.actionPerformed: left");
                if(!iLocalTest) iAceTree.prevImage();
                //updateDisplay();
            }
        };        
        InputMap im = iCellList.getInputMap(JComponent.WHEN_FOCUSED);
//        KeyStroke ks = KeyStroke.getKeyStroke(sl);
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
        im.put(ks, sl);
//        iCellList.getActionMap().put(ks, left );
        iCellList.getActionMap().put(sl, left );

        String sr = "RIGHT";
        Action right = new AbstractAction() {
            @Override
			public void actionPerformed(ActionEvent e) {
                //System.out.println("AbstractAction.actionPerformed: right");
                if(!iLocalTest) iAceTree.nextImage();
            }
        };        
        im = iCellList.getInputMap(JComponent.WHEN_FOCUSED);
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        im.put(ks, sr);
        iCellList.getActionMap().put(sr, right );

        String sd = "DOWN";
        Action down = new AbstractAction() {
            @Override
			public void actionPerformed(ActionEvent e) {
                //System.out.println("AbstractAction.actionPerformed: down");
                iAceTree.imageDown();
            }
        };        
        im = iCellList.getInputMap(JComponent.WHEN_FOCUSED);
        //ks = KeyStroke.getKeyStroke(sr);
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK);
        im.put(ks, sd);
        iCellList.getActionMap().put(sd, down );
        
        String su = "UP";
        Action up = new AbstractAction() {
            @Override
			public void actionPerformed(ActionEvent e) {
                //System.out.println("AbstractAction.actionPerformed: up");
                iAceTree.imageUp();
            }
        };        
        im = iCellList.getInputMap(JComponent.WHEN_FOCUSED);
        //ks = KeyStroke.getKeyStroke(su);
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK);
        im.put(ks, su);
        iCellList.getActionMap().put(su, up );
        
    }
    
    @SuppressWarnings("unused")
	private void saveAs() {
        JFileChooser iFC = new JFileChooser();
        int returnVal = iFC.showSaveDialog(iFrame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String dir = iFC.getCurrentDirectory().toString();
            String name = iFC.getName(iFC.getSelectedFile());
            //append(dir);
            //append(name);
            
            try {
                PrintStream ps = new PrintStream(new FileOutputStream(iFC.getSelectedFile()));
                Enumeration e = iListModel.elements();
                while(e.hasMoreElements()) {
                    String s = (String)e.nextElement();
                    String [] sa = s.split("\\s+");
                    StringBuffer sb = new StringBuffer(sa[0]);
                    String CS = ",";
                    //sb.append(CS);
                    for(int i=1; i < sa.length; i++) {
                        sb.append(CS);
                        sb.append(sa[i]);
                    }
                    ps.println(sb.toString());
                }
                //ps.print("test");
                ps.flush();
                ps.close();
            
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        
        }    
    

    }
}
