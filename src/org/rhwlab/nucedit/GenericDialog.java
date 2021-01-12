/*
 * Created on Apr 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.nucedit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GenericDialog extends JDialog implements ActionListener {

    protected AceTree iAceTree;
    protected NucleiMgr iNucleiMgr;
    protected JButton iApplyAndRebuild;
    private JButton iApplyOnly;
    protected JPanel iContentPanel;
    protected JPanel iMainPanel;
    protected Border iBlackLine;
    protected Border iTopBorder;
    protected Border iBotBorder;
    protected Border iTopBotBorder;
    

    @SuppressWarnings("unused")
	public GenericDialog(AceTree aceTree, Frame owner, boolean modal)  {
        super(owner, modal);
        iAceTree = aceTree;
        if (iAceTree != null)iNucleiMgr = iAceTree.getNucleiMgr();
        setTitle(TITLE);
        iBlackLine = BorderFactory.createLineBorder(Color.black);
        //Border empty = BorderFactory.createEmptyBorder();
        iTopBorder = BorderFactory.createEmptyBorder(10,0,0,0);
        iBotBorder = BorderFactory.createEmptyBorder(0,0,10,0);
        iTopBotBorder = BorderFactory.createEmptyBorder(10,0,10,0);

        JDialog dialog = this;
        iMainPanel = new JPanel();
        iMainPanel.setLayout(new BoxLayout(iMainPanel, BoxLayout.PAGE_AXIS));
        iContentPanel = new JPanel();
       // iContentPanel.setBorder(iBlackLine);
        iMainPanel.add(iContentPanel);

        JPanel xp = new JPanel();
      //  xp.setBorder(iBlackLine);
        xp.setLayout(new BoxLayout(xp, BoxLayout.PAGE_AXIS));
        //Border b = BorderFactory.createEmptyBorder(10,0,10,0);
        JPanel s;
        s = new JPanel();
        //s.setBorder(iBlackLine);
        //s = new JPanel();
	// s.setLayout(new GridLayout(2,1));
        iApplyAndRebuild = new JButton(APPLYANDREBUILD);
        iApplyAndRebuild.addActionListener(this);
        s.add(iApplyAndRebuild);
        //iApplyOnly = new JButton(APPLYONLY);
        //iApplyOnly.addActionListener(this);
	// s.add(iApplyOnly);
        xp.add(s);
        iMainPanel.add(xp);
        iMainPanel.setOpaque(true); //content panes must be opaque
        setContentPane(iMainPanel);

        //setSize(new Dimension(WIDTH, HEIGHT));
        setLocationRelativeTo(owner);
        //setVisible(true);


    }

    @Override
	public void actionPerformed(ActionEvent e) {
        //System.out.println("GenericDialog.actionPerformed");
        Object o = e.getSource();
        if (o == iApplyAndRebuild || o == iApplyOnly) {
	    // if (o == iApplyAndRebuild) {
                //System.out.println("GenericDialog.actionPerformed rebuilding");
                iAceTree.clearTree();
                iAceTree.buildTree(true);

                // update WormGUIDES data if it's open
                if (iAceTree.iAceMenuBar.view != null) {
                    iAceTree.iAceMenuBar.view.rebuildData();
                }

                iAceTree.updateDisplay();
		// }
            dispose();
        }

    }

    public final static String
         TITLE = "Generic Editor"
        ,CS = ", "
        ,APPLYANDREBUILD = "Apply Changes"
        ,APPLYONLY = "old apply only"
        ;

    private final static int
         WIDTH = 200
        ,HEIGHT = 100
        ;
    
    public static void main(String[] args) {
        GenericDialog gd = new GenericDialog(null, null, true);
        gd.setSize(new Dimension(WIDTH, HEIGHT));
        //gd.setLocationRelativeTo(owner);
        gd.setVisible(true);
    }
}
