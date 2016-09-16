package org.rhwlab.help;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;


public class Java3DError extends JDialog{
	
	public Java3DError(JFrame frame) {
		super(frame, "Java 3D Not Found");

        JTextArea txtBox = new JTextArea("\nJava 3D is not found. Some features cannot be used.");
        txtBox.append("\n\nJava Version:\n");
        txtBox.append(getJavaVersion());
        txtBox.append("\n\nClasspath:\n");
        txtBox.append(getClassPath());
        txtBox.setEditable(false);
        
        JScrollPane scroll = new JScrollPane(txtBox);
        scroll.setPreferredSize(new Dimension(370, 180));
        
        add(scroll);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        System.out.println("Java 3D Not Found.");
	}
	
	public String getJavaVersion () {
        return System.getProperty("java.version");
    }
    
    static String getClassPath() {
    	String path = System.getProperty("java.class.path");
    	String paths[] = path.split(":");
    	String mainPath = paths[0];
    	try {
	    	mainPath = mainPath.substring(0, mainPath.lastIndexOf("/"));
    	}
    	catch (StringIndexOutOfBoundsException oobe) {
    		
    	}
    	return mainPath;
    }
}