/*
 * Created on Sep 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rhwlab.acetree.AceTree;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Series extends JDialog implements ActionListener {
    
    public String iCurrentPattern;
    
    @SuppressWarnings("unused")
	public Series(JFrame frame, Vector filenames) {
        super(frame, true);
        System.out.println("Series constructor called");
        //super(AceTree.getAceTree(null).getMainFrame(), true);
        if (filenames == null) return;
        JFrame owner = AceTree.getAceTree(null).getMainFrame();
        JDialog dialog = this;
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        //p.setPreferredSize(new Dimension(200,300));
        p.setOpaque(true); //content panes must be opaque
        
        String [] patternExamples = new String[filenames.size()];
        for (int i=0; i < patternExamples.length; i++) {
            patternExamples[i] = (String)filenames.elementAt(i);
        }
        
        /*
        String[] patternExamples = {
                "dd MMMMM yyyy",
                "dd.MM.yy",
                "MM/dd/yy",
                "yyyy.MM.dd G 'at' hh:mm:ss z",
                "EEE, MMM d, ''yy",
                "h:mm a",
                "H:mm:ss:SSS",
                "K:mm a,z",
                "yyyy.MMMMM.dd GGG hh:mm aaa"
                };
        */
        iCurrentPattern = patternExamples[0];

        //Set up the UI for selecting a pattern.
        JLabel patternLabel2 = new JLabel("select config from the list:");

        JComboBox patternList = new JComboBox(patternExamples);
        patternList.setEditable(false);
        patternList.addActionListener(this);

        JPanel patternPanel = new JPanel();
        patternPanel.setLayout(new BoxLayout(patternPanel,
                               BoxLayout.PAGE_AXIS));
        //patternPanel.add(patternLabel1);
        patternPanel.add(patternLabel2);
        patternList.setAlignmentX(Component.LEFT_ALIGNMENT);
        patternPanel.add(patternList);
        patternPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(patternPanel);

        dialog.setContentPane(p);
        dialog.pack();

        //dialog.setSize(new Dimension(200, 300));
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
        dialog.setModal(false);
        System.out.println("Series constructor called");

    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
	public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        JComboBox cb = (JComboBox)e.getSource();
        String newSelection = (String)cb.getSelectedItem();
        iCurrentPattern = newSelection;
        System.out.println("selection: " + iCurrentPattern);
        dispose();
    }

    public static void main(String[] args) {
    }

}
