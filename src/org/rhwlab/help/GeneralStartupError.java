package org.rhwlab.help;

import java.awt.Dimension;
import java.lang.Throwable;
import java.lang.StackTraceElement;
import java.lang.Math;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;


public class GeneralStartupError extends JDialog{
	
	public GeneralStartupError(JFrame frame, Throwable t) {
		super(frame, "Startup Error");

        JTextArea txtBox = new JTextArea("\nThere was an error or exception in initializing AceTree.");
        txtBox.append("\n\n");
        txtBox.append(t.getClass().getName());
        txtBox.append("\n\n");
        txtBox.append(t.getMessage());
        txtBox.append("\n\nJava Version:\n");
        txtBox.append(System.getProperty("java.version"));
        txtBox.append("\n\nStack Trace:\n");
        txtBox.append(getStackTrace(t));
        txtBox.setEditable(false);

        JScrollPane scroll = new JScrollPane(txtBox);
        scroll.setPreferredSize(new Dimension(370, 180));
        
        add(scroll);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        System.out.println("Problem in initializing AceTree");
	}
	
	public String getStackTrace(Throwable t) {
		String stacktrace = "";
        StackTraceElement[] steArray = t.getStackTrace();
        int numElements = Math.min(STACKELEMENTSTOPRINT, steArray.length);
        // Print first (defined number) elements from stack trace
        for (int i = 0; i < numElements; i++) {
        	stacktrace += steArray[i].toString() + "\n";
        }
        return stacktrace;
    }
	
	private static final int STACKELEMENTSTOPRINT = 20;
}