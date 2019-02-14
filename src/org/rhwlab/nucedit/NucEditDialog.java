/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */
package org.rhwlab.nucedit;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.text.MaskFormatter;

import org.rhwlab.acetree.AceTree;

/**
 * This is the editing dialog called from the Edit menu of AceTree
 * It uses JTable objects to do the substantive work
 * @author biowolp
 * @version 1.0 Feb 9, 2005
 */

public final class NucEditDialog extends JDialog implements ActionListener {
    NucEditModel iNucEditModel;
    //NucleiTableModel [] iNucleiTableModel;

    //JMenuItem iTest1;
    //JMenuItem iTest2;
    //JMenuItem iAdd1;
    //JMenuItem iAdd2;
    //JMenuItem iCommit;
    JMenuItem iEnd;
    JPanel newContentPane;
    JFormattedTextField iTimeField;
    JButton iButton;
    JButton iNextButton;
    JButton iPrevButton;
    //JButton iFixxButton;
    int iTime;
    JLabel iLabel;

    /**
     * @param nucEdit an AceTree object used to access a NucleiMgr object
     * @param owner Frame the main frame of the application 
     * @param modal boolean (set false in this usage)
     */
    @SuppressWarnings("unused")
	public NucEditDialog(AceTree acetree, Frame owner, boolean modal)  {
        super(owner, modal);
        setTitle(TITLE);
        iNucEditModel = new NucEditModel(acetree);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu menu = new JMenu(FILE);
        menuBar.add(menu);
        
        //iAdd1 = new JMenuItem(ADD1);
        //iAdd1.addActionListener(this);
        //menu.add(iAdd1);
        //iAdd2 = new JMenuItem(ADD2);
        //iAdd2.addActionListener(this);
       //menu.add(iAdd2);
        //iCommit = new JMenuItem(COMMIT);
        //iCommit.addActionListener(this);
        //menu.add(iCommit);
        iEnd = new JMenuItem(END);
        iEnd.addActionListener(this);
        menu.add(iEnd);
        setItemsEnabled(false);
        
        JDialog dialog = this;
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        JPanel s = new JPanel();
        s.setLayout(new FlowLayout());
        JLabel label2 = new JLabel(TIME);
        s.add(label2);
        MaskFormatter mf = createFormatter("###");
        iTimeField = new JFormattedTextField(mf);
        iTimeField.setFocusLostBehavior(JFormattedTextField.PERSIST);
        iTimeField.setColumns(5);
        //iTime = 1;
        iTime = acetree.getImageManager().getCurrImageTime();
        iTimeField.setValue(String.valueOf(iTime));
        s.add(iTimeField);
        iButton = new JButton(READ);
        iButton.addActionListener(this);
        s.add(iButton);
        //iFixxButton = new JButton(FIXX);
        //iFixxButton.addActionListener(this);
        //s.add(iFixxButton);
        iNextButton = new JButton(NEXT);
        iNextButton.addActionListener(this);
        s.add(iNextButton);
        iPrevButton = new JButton(PREV);
        iPrevButton.addActionListener(this);
        s.add(iPrevButton);
        s.setMaximumSize(new Dimension(1000, 20));
        p.add(s);
        
        // the two JTable objects are the heart of this dialog
        JTable table = new JTable(iNucEditModel.getNucleiTableModel(0));
        table.setPreferredScrollableViewportSize(new Dimension(1000, 70));
        JScrollPane scrollPane = new JScrollPane(table);
        p.add(scrollPane);
        
        // provide a label in between to show the time corresponding
        // to the lower JTable
        s = new JPanel();
        s.setLayout(new FlowLayout());
        iLabel = new JLabel(TIME + String.valueOf(iTime + 1));
        s.add(iLabel);
        s.setMaximumSize(new Dimension(500, 20));
        p.add(s);
        
        JTable table2 = new JTable(iNucEditModel.getNucleiTableModel(1));
        table2.setPreferredScrollableViewportSize(new Dimension(1000, 70));
        JScrollPane scrollPane2 = new JScrollPane(table2);
        p.add(scrollPane2);
        
        //GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //String[] familynames = env.getAvailableFontFamilyNames();
        //for(int i=0; i < familynames.length; i++) {
        //    System.out.println("NucEditDialog: " + i + C.CS + familynames[i]);
        //}
        Font f = table.getFont();
        //System.out.println("NucEditDialog font family: " + f.getFamily());
        //f = new Font(familynames[14], Font.BOLD, 14);
        f = f.deriveFont(Font.BOLD);
        f = f.deriveFont(14.0f);
        table.setFont(f);
        table2.setFont(f);
      
        newContentPane = p;
        newContentPane.setOpaque(true); //content panes must be opaque
        dialog.setContentPane(newContentPane);

        //      Show it.
        dialog.setSize(new Dimension(400, 600));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
        read();
        int tcount = table.getColumnCount();
        int width = 100;
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setMinWidth(width);
        }
        if (table2.getColumnModel().getColumnCount() > 0) {
            table2.getColumnModel().getColumn(0).setMinWidth(width);
        }
    }

    //A convenience method for creating a MaskFormatter.
    protected MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        String time = iTimeField.getText().trim();
        String command = e.getActionCommand();
        /*
        if (o == iCommit || o == iFixxButton) {
            //String time = (String)iTimeField.getText().trim();
            int i = Integer.parseInt(time) - 1;
            iNucEditModel.commitAllChanges(i);
        } else if (o == iAdd1) {
            iNucEditModel.getNucleiTableModel(0).addRow();
        } else if (o == iAdd2) {
            iNucEditModel.getNucleiTableModel(1).addRow();
        */
        if (o == iEnd) {
            setVisible(false);
            dispose();
        } else if (command.equals(READ)) {
            read();
            //String time = (String)iTimeField.getText().trim();
            //iTime = Integer.parseInt(time);
            //iNucEditModel.updateNucleiTableModel(iTime - 1);
            //iLabel.setText(TIME + String.valueOf(iTime + 1));
            //setItemsEnabled(true);
        } else if (command.equals(NEXT)) {
            iTime = Integer.parseInt(time) + 1;
            iNucEditModel.updateNucleiTableModel(iTime - 1);
            iLabel.setText(TIME + String.valueOf(iTime + 1));
            iTimeField.setText(String.valueOf(iTime));
            setItemsEnabled(true);
        } else if (command.equals(PREV)) {
            iTime = Integer.parseInt(time) - 1;
            if (iTime < 1) return;
            iNucEditModel.updateNucleiTableModel(iTime - 1);
            iLabel.setText(TIME + String.valueOf(iTime + 1));
            iTimeField.setText(String.valueOf(iTime));
            setItemsEnabled(true);
        }
        
    }
    
    private void read() {
        String time = iTimeField.getText().trim();
        iTime = Integer.parseInt(time);
        iNucEditModel.updateNucleiTableModel(iTime - 1);
        iLabel.setText(TIME + String.valueOf(iTime + 1));
        setItemsEnabled(true);
    }

    private void setItemsEnabled(boolean enabled) {
        //iAdd1.setEnabled(enabled);
        //iAdd2.setEnabled(enabled);
        //iCommit.setEnabled(enabled);
        iEnd.setEnabled(enabled);
        
    }
    
    private final static String
         TITLE = "Nuclei Viewer"
        ,TEST = "Test"
        ,TEST1 = "test1"
        ,TEST2 = "test2"
        ,READ = "Read"
        ,NEXT = "Next"
        ,PREV = "Prev"
        ,FIXX = "Fixx"
        ,TIME = "Time: "
        ,ADD  = "Add"
        ,ADD1 = "Add to first time"
        ,ADD2 = "Add to second time"
        ,EDIT = "Edit"
        ,COMMIT = "Commit changes"
        ,FILE = "File"
        ,QUIT = "Quit"
        ,END = "Exit"
        ;
    
    /**
     * unused main function
     * @param args String []
     */
    public static void main(String[] args) {
    }
}
