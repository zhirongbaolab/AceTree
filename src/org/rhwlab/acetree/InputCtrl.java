package org.rhwlab.acetree;

import org.rhwlab.utils.SpringUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.MaskFormatter;
/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */

/**
 * control panel for selecting a cell by typing the time index
 * and the cell name or index in nuclei file for that time
 * 
 * @version 1.0 January 24, 2005
 * @author biowolp
 */
public class InputCtrl extends JPanel implements ActionListener {
    private JFormattedTextField iTimeField;
    private JTextField iNameField;
    private ControlCallback iCC;
    
    final static int GAP = 10;

    /**
     * 
     */
    public InputCtrl(ControlCallback cc) {
        super();
        iCC = cc;
        createPanes();
    }

    @SuppressWarnings("unused")
	private void createPanes() {
        //JPanel p = new JPanel();
        //p.setPreferredSize(new Dimension(WIDTH,200));
        JPanel p = new JPanel() {
            //Don't allow us to stretch vertically.
            @Override
			@SuppressWarnings("unused")
			public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                //return new Dimension(Integer.MAX_VALUE,
                //        pref.height);
                return new Dimension(Integer.MAX_VALUE,
                        Integer.MAX_VALUE);
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        Border paneEdge = BorderFactory.createEmptyBorder(10,10,10,10);
        Border blackline = BorderFactory.createLineBorder(Color.black);

        setBorder(blackline);
        p.add(createEntryFields());
        p.add(createButtons());
        add(p);
    }
    
    protected JComponent createEntryFields() {
        JPanel panel = new JPanel(new SpringLayout());

        String[] labelStrings = {
            "image time: ",
            "index/cell name: "
        };

        JLabel[] labels = new JLabel[labelStrings.length];
        JComponent[] fields = new JComponent[labelStrings.length];

        int fieldNum = 0;

        MaskFormatter mf = createFormatter("#######");
        iTimeField = new JFormattedTextField(mf);
        iTimeField.setFocusLostBehavior(JFormattedTextField.PERSIST);
        iTimeField.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                iTimeField.setCaretPosition(0);
            }
            public void focusLost(FocusEvent e) {}

        });

        iNameField = new JTextField();
        iNameField.setColumns(15);

        fields[fieldNum++] = iTimeField;
        fields[fieldNum++] = iNameField;

        //Associate label/field pairs, add everything,
        //and lay it out.
        for (int i = 0; i < labelStrings.length; i++) {
            labels[i] = new JLabel(labelStrings[i],
                                   SwingConstants.TRAILING);
            labels[i].setLabelFor(fields[i]);
            panel.add(labels[i]);
            panel.add(fields[i]);
        }
        SpringUtilities.makeCompactGrid(panel,
                                        labelStrings.length, 2,
                                        GAP, GAP, //init x,y
                                        GAP, GAP/2); //xpad, ypad
        return panel;
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

    protected JComponent createButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        panel.setMinimumSize(new Dimension(WIDTH, HEIGHT));

        JButton button = new JButton("Get cell");
        button.addActionListener(this);
        panel.add(button);

        button = new JButton("Clear");
        button.addActionListener(this);
        button.setActionCommand("clear");
        panel.add(button);

        //Match the SpringLayout's gap, subtracting 5 to make
        //up for the default gap FlowLayout provides.
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0,
                                                GAP-5, GAP-5));
        return panel;
    }

    public void getIt() {
        Vector v = new Vector();
        v.add("InputCtrl1");
        v.add(iTimeField.getText());
        v.add(iNameField.getText());
        iCC.controlCallback(v);
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        if ("clear".equals(e.getActionCommand())) {
            iTimeField.setValue(null);
            iNameField.setText("");
        } else {
            getIt();
        }
    }

    public Component getTimeField() {
        return iTimeField;
    }
    
    public Component getNameField() {
        return iNameField;
    }
    
    final public static int 
         WIDTH = 200
        ,HEIGHT = 100
        ;
    
    public static void main(String[] args) {
    }
}
