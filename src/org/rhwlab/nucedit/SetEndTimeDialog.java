/*
 * Created on Mar 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.nucedit;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetEndTimeDialog extends JDialog implements ActionListener {

    private AceTree iAceTree;
    private NucleiMgr iNucleiMgr;
    private JTextField iEndTime;
    private JButton iDoit;
    
    public SetEndTimeDialog(AceTree aceTree, Frame owner, boolean modal) {
        super(owner, TITLE, modal);
        iAceTree = aceTree;
        iNucleiMgr = iAceTree.getNucleiMgr();
        setTitle(TITLE);
        
            JDialog dialog = this;
            JPanel p = new JPanel();
            p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
            JPanel s = new JPanel();
            s.setLayout(new FlowLayout());
            
            JLabel label = new JLabel(ADDTIME);
            s.add(label);
            iEndTime = new JTextField();
            iEndTime.setColumns(5);
            iEndTime.setText(String.valueOf(iAceTree.getConfig().getNucleiConfig().getEndingIndex()));
            s.add(iEndTime);
            p.add(s);

            s = new JPanel();
            s.setLayout(new FlowLayout());
            iDoit = new JButton(DOIT);
            iDoit.addActionListener(this);
            s.add(iDoit);
            p.add(s);

            p.setOpaque(true); //content panes must be opaque
            dialog.setContentPane(p);
            dialog.setSize(new Dimension(200, 100));
            dialog.setLocationRelativeTo(owner);
            dialog.setVisible(true);

    }

    @Override
	public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == iDoit) {
            int endTime = Integer.parseInt(iEndTime.getText());
            iAceTree.getConfig().getNucleiConfig().setEndingIndex(endTime);
        }
        dispose();

    }

    private final static String
         TITLE = "Nuclei Editor"
        ,ADDTIME = "end time"
        ,DOIT = "Apply"
        ;

}
