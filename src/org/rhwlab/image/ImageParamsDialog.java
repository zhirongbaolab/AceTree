 /*
 * Created on Apr 28, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import org.rhwlab.image.ImageWindow.ColorSchemeUI;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ImageParamsDialog extends JDialog implements ActionListener {
    JPanel                          iPanel;
    //SublineageDisplayProperty []    iDispProps;
    ColorSchemeUI []                 iCSUI;
    ImageWindow                     iImgWin;
    JCheckBox                       iAcbTree;
    
    
    @SuppressWarnings("unused")
	public ImageParamsDialog(ImageWindow imgWin) {
        super(imgWin, "Image Parameters", false);
        iImgWin = imgWin;

        Border blackline = BorderFactory.createLineBorder(Color.black);
        //iDispProps = imgWin.getDisplayProps();
        iCSUI = new ColorSchemeUI[ImageWindow.iDispProps.length];
        iPanel = new JPanel();
        iPanel.setLayout(new BorderLayout());
        iPanel.setBorder(blackline);
        //iPanel.setLayout(new BoxLayout(iPanel, BoxLayout.PAGE_AXIS));
        
        JPanel lineagePanel = new JPanel();
        JPanel dummyPanel = new JPanel();
        JPanel topPart = new JPanel();
		// topPart.setLayout(new GridLayout(1,2));
		topPart.setLayout(new BoxLayout(topPart,BoxLayout.LINE_AXIS));
        lineagePanel.setLayout(new GridLayout(5,1));
        dummyPanel.setLayout(new GridLayout(5,1));
       
        //lineagePanel.setBorder(blackline);
        //lineagePanel.setMaximumSize(new Dimension(300,400));
        topPart.add(lineagePanel);
        topPart.add(dummyPanel);
        JPanel [] testPanel = new JPanel[ImageWindow.iDispProps.length];

        JTextField textField;
        JComboBox cb;
        JPanel labelPanel = new JPanel();
        JPanel labelPanel2 = new JPanel();

        JLabel sublineage = new JLabel("Item");
        JLabel color = new JLabel("Color");
        labelPanel.setLayout(new GridLayout(1,2));
        labelPanel.add(sublineage);
        labelPanel.add(color);
		labelPanel2.setLayout(new GridLayout(1,2));
		sublineage = new JLabel("Item");
        color = new JLabel("Color");
        labelPanel2.add(sublineage);
        labelPanel2.add(color);
        lineagePanel.add(labelPanel);
        
        // iAcbTree = new JCheckBox("AcbTree");
        //iAcbTree.addActionListener(this);
        //ImageWindow.cAcbTree = false;
        //dummyPanel.add(iAcbTree);
        //dummyPanel.setBorder(blackline);
     
		/*
		JPanel optionPanel = new JPanel();
	        for (int i=0; i < ImageWindow.iDispProps.length; i++) {
	            iCSUI[i] = imgWin.new ColorSchemeUI(i);
		    optionPanel.add(iCSUI[i].iPanel);
	        }
		*/

		// hack to avoid adding entry 3 and 6 to this menu
		// manually add them to dummymenu, no idea why this was written this way
		// or else last two entries end up on top of item-color on top right of display
        for (int i=0; i < ImageWindow.iDispProps.length; i++) {
            iCSUI[i] = imgWin.new ColorSchemeUI(i);
		    if (i!=2 & i!=5 & i!=6 & i!=7){
		    	lineagePanel.add(iCSUI[i].iPanel);
		    }
        }
	
		dummyPanel.add(labelPanel2);
		dummyPanel.add(iCSUI[2].iPanel);
		dummyPanel.add(iCSUI[5].iPanel);
		dummyPanel.add(iCSUI[6].iPanel);
		dummyPanel.add(iCSUI[7].iPanel);

		// lineagePanel.setMaximumSize(new Dimension(200, 200));
        iPanel.add(topPart, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        // buttonPanel.setLayout(new GridLayout(1,3));
        //buttonPanel.setMinimumSize(new Dimension(400, 100));
        
        JButton reset = new JButton("Reset");
        JButton apply = new JButton("Apply");
        JButton cancel = new JButton("Cancel");
        buttonPanel.add(apply);
        reset.addActionListener(this);
        apply.addActionListener(this);
        cancel.addActionListener(this);
        buttonPanel.add(reset);
        buttonPanel.add(apply);
        buttonPanel.add(cancel);
        JPanel botPart = new JPanel();
       // botPart.setLayout(new GridLayout(5,1));
        //botPart.add(new JPanel());
        botPart.add(buttonPanel);
      //  botPart.add(new JPanel());
       // botPart.add(new JPanel());
       // botPart.add(new JPanel());
        iPanel.add(botPart, BorderLayout.SOUTH);
        
        //iPanel.add(buttonPanel, BorderLayout.CENTER);
        //iPanel.add(new JPanel(), BorderLayout.CENTER);
        setContentPane(iPanel);
        setSize(new Dimension(570, 250));
        setLocationRelativeTo(imgWin);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        //println("actionPerformed: " + e);
        String command = e.getActionCommand();
        Object o = e.getSource();
        if (o == iAcbTree) {
            boolean b = iAcbTree.isSelected();
            ImageWindow.cAcbTree = b;
            println("ImageParamsDialog.actionPerformed: AcbTree: " + b);
            //println("actionPerformed:2 " + b);
        }
        if (command.equals("Reset")) {
            ImageWindow.iDispProps = iImgWin.getDisplayProps();                
            for (int i=0; i < ImageWindow.iDispProps.length; i++) {
                println("Reset: " + i + CS + ImageWindow.iDispProps[i].iName
                        + CS + ImageWindow.iDispProps[i].iLineageNum);
                iCSUI[i].iLabel.setText(ImageWindow.iDispProps[i].iName);
                iCSUI[i].iCB.setSelectedIndex(ImageWindow.iDispProps[i].iLineageNum);
            }
        } else if (command.equals("Apply")) {
            //println("ImageParamsDialog.actionPerformed: Apply");
            for (int i=0; i < ImageWindow.iDispProps.length; i++) {
                String name = iCSUI[i].iTF.getText();
                if (name.length() == 0) 
                	name = "-";
                int num = iCSUI[i].iCB.getSelectedIndex();
                ImageWindow.iDispProps[i].iName = name;
                ImageWindow.iDispProps[i].iLineageNum = num;
            }
            // Dispose of window on apply
            dispose();
        } else if (command.equals("Cancel")) {
        	dispose();
        }
        	
        //iAceTree.setDispProps3D(iDispProps);
        //updateDisplayedTab();
    }
    

    public static void main(String[] args) { }
    
    private static void println(String s) {
    	System.out.println(s);
	}
    
    private static final String CS = ", ";

}
