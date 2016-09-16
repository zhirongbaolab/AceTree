/*
 * Created on Sep 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConfigFileList implements ActionListener {

    private JMenuItem   iConfigList;
    private JMenuItem   iChoose;
    private JMenuItem   iExit;
    private JMenu       iMenu;
    private Vector      iConfigFiles;
    private int         iItem;
    private int         iItems;
    private JPanel      iPanel;
    
    public ConfigFileList(JPanel jpanel) {
        iPanel = jpanel;
    }

    public void createMenu(JMenuBar menuBar) {
        //JMenu menu = new JMenu("File");
        //add(menu);
        iMenu = new JMenu("File");
        menuBar.add(iMenu);
        iConfigList = new JMenuItem(CONFIGS);
        iExit = new JMenuItem(EXIT);
        iMenu.add(iConfigList);
        iMenu.add(iExit);
        iConfigList.addActionListener(this);
        iExit.addActionListener(this);
        
        //add(iMenu);

    }

    private static final String 
         CONFIGS = "ConfigFiles"
        ,CHOOSE = "Choose"
        ,EXIT = "Exit"
        ;

    private void getFile() {
        JFileChooser fc = new JFileChooser(".");
        int returnVal = fc.showOpenDialog(iPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //String path = file.getPath();
            readConfigFileList(file);
        } else {
            System.out.println("Save command cancelled by user.");
        }
    }
    
    private void readConfigFileList(File f) {
        String sr = null;
        iConfigFiles = new Vector();
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            sr = br.readLine();
            while (sr != null && sr.length() > 2) {
                //System.out.println("config file: " + sr);
                iConfigFiles.add(sr);
                sr = br.readLine();
            }
            br.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        iItem = 0;
        iItems = iConfigFiles.size();

    }

    public Vector getConfigFiles() {
        return iConfigFiles;
    }
    
    public String getItem(int i) {
        return (String) iConfigFiles.elementAt(i);
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
	public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        String s = e.getActionCommand();
        if (s.equals(CONFIGS)) {
            getFile();
        } else if (s.equals(EXIT)) {
            ;
        }
    }

    // end of code for config file list
    public static void main(String[] args) {
    }

}
