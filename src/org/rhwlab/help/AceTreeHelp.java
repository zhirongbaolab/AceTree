/*
 * Created on May 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.help;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;

import org.rhwlab.acetree.AceTree;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AceTreeHelp extends JPanel {
    JFrame  iFrame;
    String  iFileName;
    																																																							
    public AceTreeHelp(String fileName) {
        this(fileName, 400, 500);
        iFileName = fileName;
    }
    
    public AceTreeHelp(String fileName, int width, int height) {
        setLayout(new BorderLayout());
        //Create an editor pane.
        iFileName = fileName;
        JEditorPane editorPane = createEditorPane();
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        //editorScrollPane.setVerticalScrollBarPolicy(
        //                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setPreferredSize(new Dimension(width, height));
        editorScrollPane.setMinimumSize(new Dimension(width, height));
        add(editorScrollPane);
        int k = fileName.lastIndexOf("/") + 1;
        int m = fileName.lastIndexOf(".");
        String title = fileName.substring(k, m);
        iFrame = new JFrame(title);
        showMe();
    }
    
    private JEditorPane createEditorPane() {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        java.net.URL url = null;
        if (iFileName.indexOf("http://") == 0) {
            try {
            	System.out.println("Http:// found.");
                url = new URL(iFileName);
            } catch(Exception e) {
                System.out.println("bad url: " + iFileName);
                return null;
            }
        } else {
            url = AceTree.class.getResource(iFileName);
            System.out.println("Using AceTree class getResource for file name.");
        }
        if (url != null) {
            try {
                editorPane.setPage(url);
            } catch (IOException e) {
                System.err.println("Attempted to read a bad URL: " + url);
            }
        } else {
            System.err.println("Couldn't find html file: " + iFileName);
        }

        return editorPane;
    }


    public void showMe() {
        //iFrame = new JFrame(iLog.iTitle);
        iFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //UITest newContentPane = new UITest();
        //newContentPane.setOpaque(true); //content panes must be opaque
        iFrame.setContentPane(this);
        iFrame.pack();
        iFrame.setVisible(true);
    }
    

    public static void main(String[] args) {
    }
}
