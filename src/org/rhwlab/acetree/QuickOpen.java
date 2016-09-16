/*
 * Created on May 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.acetree;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.rhwlab.snight.NucleiMgr;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QuickOpen extends JDialog implements ActionListener {

    JTextField  iTypicalImage;
    JButton     iBrowseForImage;
    JTextField  iNucleiZipFile;
    JButton     iBrowseForNucleiZip;
    JButton     iOpen;
    String      iImageDir;
    AceTree     iAceTree;
    
    JComboBox   iAxis;
    
    public QuickOpen() {
        super();
        setTitle(TITLE);
        setSize(new Dimension(400, 200));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        iImageDir = ".";
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        setContentPane(p);
        JPanel pp = new JPanel();
        pp.setLayout(new FlowLayout());
        pp.add(new JLabel("typical image:"));
        iTypicalImage = new JTextField(15);
        pp.add(iTypicalImage);
        iBrowseForImage = new JButton("browse");
        pp.add(iBrowseForImage);
        iBrowseForImage.addActionListener(this);
        p.add(pp);
        
        pp = new JPanel();
        pp.setLayout(new FlowLayout());
        pp.add(new JLabel("nuclei zip file:"));
        iNucleiZipFile = new JTextField("NULL", 15);
        pp.add(iNucleiZipFile);
        iBrowseForNucleiZip = new JButton("browse");
        pp.add(iBrowseForNucleiZip);
        iBrowseForNucleiZip.addActionListener(this);
        p.add(pp);
        
        pp = new JPanel();
        pp.setLayout(new FlowLayout());
        pp.add(new JLabel("set axis:"));
        iAxis = new JComboBox(AXES);
        iAxis.setEditable(true);
        pp.add(iAxis);
        p.add(pp);
        
        pp = new JPanel();
        pp.setLayout(new FlowLayout());
        iOpen = new JButton("open");
        pp.add(iOpen);
        iOpen.addActionListener(this);
        p.add(pp);
        

        
        
        
        
        setVisible(true);

    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
	public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == iBrowseForImage) {
            println("actionPerformed: browseForImage");
            JFileChooser fileChooser = new JFileChooser(iImageDir);
            fileChooser.setCurrentDirectory(new File(iImageDir));
            fileChooser.setSelectedFile(new File(""));
            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String path = file.getPath();
                iImageDir = file.getParent();
                iTypicalImage.setText(path);
            } else {
                    System.out.println("Save command cancelled by user.");
            }
            
        } else if (o == iBrowseForNucleiZip) {
            println("actionPerformed: browseForNucleiZip");
            JFileChooser fileChooser = new JFileChooser(iImageDir);
            fileChooser.setCurrentDirectory(new File(iImageDir));
            fileChooser.setSelectedFile(new File(""));
            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String path = file.getPath();
                iImageDir = path;
                iNucleiZipFile.setText(path);
            } else {
                    System.out.println("Save command cancelled by user.");
            }
            
        } else if (o == iOpen) {
            println("actionPerformed: open");
            String image = iTypicalImage.getText();
            // assume the images are in a directory like tif
            // and that I can create a new directory dats at that level
            // or maybe it is already there
            String parent = new File(image).getParent();
            parent = new File(parent).getParent();
            println("actionPerformed: parent of parent: " + parent);
            String dats = parent + "/dats";
            File fdats = new File(dats);
            println("actionPerformed: dats: " + dats);
            if (!fdats.exists()) {
                println("actionPerformed: making directory: " + fdats);
                fdats.mkdir();
                try {
                    //Runtime.getRuntime().exec("chgrp waterstonlab " + fdats);
                    Runtime.getRuntime().exec("chmod 777 " + fdats);
                } catch(Exception ee) {
                    ee.printStackTrace();
                    System.out.println("exiting -- unable to set directory permissions");
                    System.exit(11);
                }

            }
            String nuclei = iNucleiZipFile.getText();
            
            if (image.length() == 0) return;
            if (nuclei.length() == 0) return;
            
            // now construct a config file path
            String configName = new File(image).getName();
            int m = configName.lastIndexOf('.');
            configName = configName.substring(0, m);
            
            String configPath = dats + "/" + configName + ".dat";
            println("actionPerformed: configPath: " + configPath);
            
            //if (1 == 1) System.exit(0);
            String axis = (String)iAxis.getSelectedItem();
            boolean useAxis = !axis.equals(AXES[0]); 
 
            //println("actionPerformed: " + name);
            //int k = name.lastIndexOf(".");
            //name = name.substring(0, k);
            //println("actionPerformed: " + name);
            File f = new File(configPath);
            FileOutputStream fos = null;
            try {   
                fos = new FileOutputStream(f);
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
            PrintWriter pw = new PrintWriter(fos);
            pw.println("typical image, " + image);
            pw.println("zipFileName, " + nuclei);
            if (useAxis) pw.println("axis, " + axis);
            if (nuclei.equals("NULL")) {
                pw.println("ending index, " + NucleiMgr.LAST);
            }
            pw.close();
            dispose();
            if (iAceTree == null) iAceTree = AceTree.getAceTree("");
            iAceTree.setConfigFileName(configPath);
            iAceTree.bringUpSeriesUI(configPath);
        }

    }

    private static final String [] AXES = {
         "auto"
        ,"adl"
        ,"avr"  
    };
        
    
    private static final String
         TITLE = "QuickOpen"
        ;
    
	public static void main(String[] args) {
        println("QuickOpen.main");
        new QuickOpen();
    }

    private static void println(String s) {System.out.println(s);}
    private static final String CS = ", ";

}
