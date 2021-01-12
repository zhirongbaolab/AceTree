// Created 18 March, 2015

package org.rhwlab.image;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ImageContrastTool extends JDialog implements ActionListener, ChangeListener {
	
	private JSlider iSlider1min, iSlider1max, iSlider2min, iSlider2max, iSlider3min, iSlider3max;
	private JTextField iText1min, iText1max, iText2min, iText2max, iText3min, iText3max;
	private double min1, max1, min2, max2, min3, max3;
	private JButton iReset, iCancel;
	private int iUseStack;
	
	public ImageContrastTool(JFrame parent, int useStack) {
		super(parent, "Adjust Contrast", false);

		iUseStack = useStack;
		if (iUseStack == 1)
			setTitle(getTitle() + " 16-bit stack mode");
		else
			setTitle(getTitle() + " 8-bit non-stack mode");
		
		addDialogComponents();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		pack();
		setVisible(true);
		
		min1 = min2 = 0;
		if (iUseStack == 1)
			max1 = max2 = max3 = MAX16BIT;
		else
			max1 = max2 = max3 = MAX8BIT;
	}
	
	// Add sliders, textfields, and buttons to main dialog window
	public void addDialogComponents() {
		// Document listener for text fields
		DocumentListener docListener = new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				readText(e);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				readText(e);
			}
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				readText(e);
			}
			
			private void readText(DocumentEvent e) {
				Object source = e.getDocument().getProperty("owner");
				if (source instanceof JTextField) {
					try {
						double val = Double.parseDouble(((JTextField)source).getText());
						int sliderVal = (int)val;
						int validMax = MAX8BIT;
						if (iUseStack == 1)
							validMax = MAX16BIT;
						boolean valid = (val >= 0. && val <= validMax);
						if (valid) {
							if (iText1min == source) 
								iSlider1min.setValue(sliderVal);
							else if (iText1max == source)
								iSlider1max.setValue(sliderVal);
							else if (iText2min == source)
								iSlider2min.setValue(sliderVal);
							else if (iText2max == source) 
								iSlider2max.setValue(sliderVal);
							else if (iText3min == source)
								iSlider3min.setValue(sliderVal);
							else if (iText3max == source)
								iSlider3max.setValue(sliderVal);
						}
					}
					catch (Exception e2) {
						return;
					}
				}
			}
		};
		
		JPanel jpWhole = new JPanel();
		jpWhole.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		int sliderMaxVal = MAX8BIT;
		if (iUseStack == 1)
			sliderMaxVal = MAX16BIT;

		// Add four sliders and four text fields
		JLabel jl = new JLabel("Red Channel Min");
		iSlider1min = new JSlider(0, sliderMaxVal);
		iSlider1min.setValue(0);
		setTicks(iSlider1min);
		iSlider1min.addChangeListener(this);
		iText1min = new JTextField("0", 7);
		iText1min.getDocument().addDocumentListener(docListener);
		setOwner(iText1min);
		c.ipady = 20;
		c.gridx = 0;
		c.gridy = 0;
		jpWhole.add(jl, c);
		c.gridx = 1;
		c.gridwidth = 3;
		jpWhole.add(iSlider1min, c);
		c.gridx = 4;
		c.gridwidth = 1;
		jpWhole.add(iText1min, c);
		
		jl = new JLabel("Red Channel Max");
		iSlider1max = new JSlider(0, sliderMaxVal);
		iSlider1max.setValue(sliderMaxVal);
		setTicks(iSlider1max);
		iSlider1max.addChangeListener(this);
		iText1max = new JTextField(""+sliderMaxVal, 7);
		iText1max.getDocument().addDocumentListener(docListener);
		setOwner(iText1max);
		c.gridx = 0;
		c.gridy = 1;
		jpWhole.add(jl, c);
		c.gridx = 1;
		c.gridwidth = 3;
		jpWhole.add(iSlider1max, c);
		c.gridx = 4;
		c.gridwidth = 1;
		jpWhole.add(iText1max, c);
		
		jl = new JLabel("Green Channel Min");
		iSlider2min = new JSlider(0, sliderMaxVal);
		iSlider2min.setValue(0);
		setTicks(iSlider2min);
		iSlider2min.addChangeListener(this);
		iText2min = new JTextField("0", 7);
		iText2min.getDocument().addDocumentListener(docListener);
		setOwner(iText2min);
		c.gridx = 0;
		c.gridy = 2;
		jpWhole.add(jl, c);
		c.gridx = 1;
		c.gridwidth = 3;
		jpWhole.add(iSlider2min, c);
		c.gridx = 4;
		c.gridwidth = 1;
		jpWhole.add(iText2min, c);
		
		jl = new JLabel("Green Channel Max");
		iSlider2max = new JSlider(0, sliderMaxVal);
		iSlider2max.setValue(65535);
		setTicks(iSlider2max);
		iSlider2max.addChangeListener(this);
		iText2max = new JTextField(""+sliderMaxVal, 7);
		iText2max.getDocument().addDocumentListener(docListener);
		setOwner(iText2max);
		c.gridx = 0;
		c.gridy = 3;
		jpWhole.add(jl, c);
		c.gridx = 1;
		c.gridwidth = 3;
		jpWhole.add(iSlider2max, c);
		c.gridx = 4;
		c.gridwidth = 1;
		jpWhole.add(iText2max, c);

		jl = new JLabel("Blue Channel Min");
		iSlider3min = new JSlider(0, sliderMaxVal);
		iSlider3min.setValue(0);
		setTicks(iSlider3min);
		iSlider3min.addChangeListener(this);
		iText3min = new JTextField("0", 7);
		iText3min.getDocument().addDocumentListener(docListener);
		setOwner(iText3min);
		c.gridx = 0;
		c.gridy = 4;
		jpWhole.add(jl, c);
		c.gridx = 1;
		c.gridwidth = 3;
		jpWhole.add(iSlider3min, c);
		c.gridx = 4;
		c.gridwidth = 1;
		jpWhole.add(iText3min, c);

		jl = new JLabel("Blue Channel Max");
		iSlider3max = new JSlider(0, sliderMaxVal);
		iSlider3max.setValue(65535);
		setTicks(iSlider3max);
		iSlider3max.addChangeListener(this);
		iText3max = new JTextField(""+sliderMaxVal, 7);
		iText3max.getDocument().addDocumentListener(docListener);
		setOwner(iText3max);
		c.gridx = 0;
		c.gridy = 5;
		jpWhole.add(jl, c);
		c.gridx = 1;
		c.gridwidth = 3;
		jpWhole.add(iSlider3max, c);
		c.gridx = 4;
		c.gridwidth = 1;
		jpWhole.add(iText3max, c);

		// Add buttons
		iReset = new JButton("Reset");
		iReset.addActionListener(this);
		iCancel = new JButton("Cancel");
		iCancel.addActionListener(this);
		c.gridy = 6;
		c.gridx = 1;
		jpWhole.add(iReset, c);
		c.gridx = 2;
		jpWhole.add(iCancel, c);
		
		add(jpWhole);
	}
	
	// sets "owner" property of the Document of jtf parameter
	public void setOwner(JTextField jtf) {
		jtf.getDocument().putProperty("owner", jtf);
	}
	
	// Elongate and set tick marks for slider
	public void setTicks(JSlider js) {
		Dimension d = js.getPreferredSize();
		js.setPreferredSize(new Dimension(d.width+120, d.height));
		if (iUseStack == 1) {
			js.setMinorTickSpacing((MAX16BIT+1)/16);
		    js.setMajorTickSpacing((MAX16BIT+1)/8);
		}
		else {
			js.setMinorTickSpacing((MAX8BIT+1)/16);
		    js.setMajorTickSpacing((MAX8BIT+1)/8);
		}
	    js.setPaintTicks(true);
	    js.setSnapToTicks(false);
		
	    Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
	    table.put(new Integer(0), new JLabel("0"));
	    if (iUseStack == 1) {
		    table.put(new Integer(16384), new JLabel(""+(MAX16BIT/4)));
		    table.put(new Integer(32768), new JLabel(""+(MAX16BIT/2)));
		    table.put(new Integer(49152), new JLabel(""+(3*MAX16BIT/4)));
		    table.put(new Integer(65535), new JLabel(""+MAX16BIT));
			table.put(new Integer(49152), new JLabel(""+(3*MAX16BIT/4))); // correct for blue?
			table.put(new Integer(65535), new JLabel(""+MAX16BIT)); // correct for blue?
	    }
	    else {
	    	table.put(new Integer(64), new JLabel(""+(MAX8BIT/4)));
		    table.put(new Integer(128), new JLabel(""+(MAX8BIT/2)));
		    table.put(new Integer(192), new JLabel(""+(3*MAX8BIT/4)));
		    table.put(new Integer(255), new JLabel(""+MAX8BIT));
			table.put(new Integer(192), new JLabel(""+(3*MAX8BIT/4))); // correct for blue?
			table.put(new Integer(255), new JLabel(""+MAX8BIT)); // correct for blue?
	    }
	    js.setLabelTable(table);
	    js.setPaintLabels(true);
	}
	
	// Methods to set slider values
	public void setSlider1min(int val) {
		iSlider1min.setValue(val);
	}
	
	public void setSlider1max(int val) {
		iSlider1max.setValue(val);
	}
	
	public void setSlider2min(int val) {
		iSlider2min.setValue(val);
	}
	
	public void setSlider2max(int val) {
		iSlider2max.setValue(val);
	}

	public void setSlider3min(int val) {
		iSlider3min.setValue(val);
	}

	public void setSlider3max(int val) {
		iSlider3max.setValue(val);
	}
	
	// Methods to retrieve references to each slider
	public JSlider getSlider1min() {
		return iSlider1min;
	}
	
	public JSlider getSlider1max() {
		return iSlider1max;
	}
	
	public JSlider getSlider2min() {
		return iSlider2min;
	}
	
	public JSlider getSlider2max() {
		return iSlider2max;
	}

	public JSlider getSlider3min() {
		return iSlider3min;
	}

	public JSlider getSlider3max() {
		return iSlider3max;
	}
	
	// Action listeners for buttons
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (iCancel == source) {
			setVisible(false);
		}
		else if (iReset == source) {
			resetSliders();
		}
	}
	
	public void resetSliders() {
		iSlider1min.setValue(0);
		iSlider2min.setValue(0);
		iSlider3min.setValue(0);
		if (iUseStack == 1) {
			iSlider1max.setValue(MAX16BIT);
			iSlider2max.setValue(MAX16BIT);
			iSlider3max.setValue(MAX16BIT);
		}
		else {
			iSlider1max.setValue(MAX8BIT);
			iSlider2max.setValue(MAX8BIT);
			iSlider3max.setValue(MAX8BIT);
		}
	}
	
	// Action listeners for sliders
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
			int val = source.getValue();
			String txt = ""+val;
            if (iSlider1min == source) {
            	min1 = val;
            	iText1min.setText(txt);
            }
            else if (iSlider1max == source) {
            	max1 = val;
            	iText1max.setText(txt);
            }
            else if (iSlider2min == source) {
            	min2 = val;
            	iText2min.setText(txt);
            }
            else if (iSlider2max == source) {
            	max2 = val;
            	iText2max.setText(txt);
            } else if (iSlider3min == source) {
				min3 = val;
				iText3min.setText(txt);
			}
			else if (iSlider3max == source) {
				max3 = val;
				iText3max.setText(txt);
			}
		}
	}
	
	private static final int
		MAX8BIT = 255,
		MAX16BIT = 65535;
}