package org.rhwlab.help;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import org.rhwlab.acetree.AceTree;

public class TestWindow extends JDialog implements ActionListener {

	AceTree			iAceTree;
	JDialog			iJDialog;
	JPanel			iPanel;
	JButton			iButton;
	JButton			iDefault;

	@SuppressWarnings("unused")
	public TestWindow(AceTree aceTree, Frame owner, boolean modal) {
        super(owner, modal);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        iJDialog = this;
        iAceTree = aceTree;
        setTitle("TestWindow");
        JPanel pWhole = new JPanel();
        iPanel = pWhole;

        pWhole.setLayout(new BoxLayout(pWhole, BoxLayout.PAGE_AXIS));
        Border blackline = BorderFactory.createLineBorder(Color.black);

        JTextField text = new JTextField("text goes here");
        pWhole.add(text);
        //text.setKeymap(null);


        JButton jb = new JButton("test");
        pWhole.add(jb);
        jb.addActionListener(this);
        iButton = jb;

        iDefault = new JButton(".");
        pWhole.add(iDefault);
        iDefault.addActionListener(this);
        getRootPane().setDefaultButton(iDefault);
        iDefault.setSize(new Dimension(1,1));



        setKeyboardActions();
        pWhole.setOpaque(true); //content panes must be opaque
        setContentPane(pWhole);
        //dialog.setSize(new Dimension(200, 400));
        setLocationRelativeTo(owner);
        pack();
        setSize(new Dimension(200, 200));
        setVisible(true);
        iButton.requestFocus();



	}

    @SuppressWarnings("unused")
	private void setKeyboardActions() {
        String s = "F1";
        Action home = new AbstractAction() {
            @Override
			public void actionPerformed(ActionEvent e) {
            	println("setKeyboardActions");
            	iAceTree.iImgWin.requestFocus();
            	//iAceTree.requestFocus();
            	/*
            	Component compFocusOwner =
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            	Window windowFocusOwner =
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
            	if (compFocusOwner instanceof JButton) {
            		println("its a button");
            		((JButton)compFocusOwner).doClick();
            	}
            	println("setKeyboardActions, " + compFocusOwner);
            	*/
            }
        };
        KeyStroke key = null;
        key = KeyStroke.getKeyStroke("F2");
        key = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK, false);

        iDefault.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, "pressed");
        iDefault.getActionMap().put("pressed", home );

    }


	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
		println("actionPerformed, " + c);
		if (c.equals("test")) {
			println("TestWindow.actionPerformed, ");
		} else if (c.equals(".")) println("default");

	}

    @Override
	public void processMouseEvent(MouseEvent e) {

        println("processMouseEvent, " + e);
        int button = e.getButton();
        println("processMouseEvent, " + button);
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

    private static void println(String s) {System.out.println(s);}
    private static void print(String s) {System.out.print(s);}
    private static final String CS = ", ";
    private static final String TAB = "\t";
    private static final DecimalFormat DF0 = new DecimalFormat("####");
    private static final DecimalFormat DF1 = new DecimalFormat("####.#");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt4(double d) {return DF4.format(d);}
    private static String fmt1(double d) {return DF1.format(d);}
    private static String fmt0(double d) {return DF0.format(d);}

}
