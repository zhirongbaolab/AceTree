/*
 * SublineageUI.java
 *
 * Represents one of the rows with a text field and combo box
 * in the geometric 3D (Image3D2's) Color Controls tab, 
 * Lineage Controls
 */

package org.rhwlab.image;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class SublineageUI {
    private JPanel       iPanel;
    private JTextField   iTF;
    private JComboBox    iCB;

    private static final int WIDTH = 15;

    /*
     * Second integer parameter "type" specifies which dropdown menu is involked
     * 0: default colors
     * 1: background grays
     * 2: "other" colors
	*/
    public SublineageUI(SublineageDisplayProperty dispProp, int type) {
        iPanel = new JPanel();
        iPanel.setLayout(new GridLayout(1,2));

        iTF = new JTextField(dispProp.getName(), WIDTH);
        
        switch(type){
        	case 1:
        		iCB = new JComboBox(Image3DViewConfig.GRAYDEPTH);
        		break;
        	case 2:
        		iCB = new JComboBox(Image3DViewConfig.TRANSPROPS);
        		break;
        	default:
        		iCB = new JComboBox(Image3DViewConfig.LINEAGE_COLORS);
        		break;
        }
        iCB.setSelectedIndex(dispProp.getLineageNum());

        iPanel.add(iTF);
        iPanel.add(iCB);
        iPanel.setMaximumSize(new Dimension(200,10));
    }

    public String getText() {
        return iTF.getText();
    }

    public void setText(String s) {
        iTF.setText(s);
    }

    public int getSelectedIndex() {
        return iCB.getSelectedIndex();
    }

    public void setSelectedIndex(int i) {
        iCB.setSelectedIndex(i);
    }

    public JPanel getPanel() {
        return iPanel;
    }
}

