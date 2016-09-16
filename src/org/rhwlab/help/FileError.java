package org.rhwlab.help;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class FileError extends JFrame{
	
	private static final String
		IMAGE_ERROR_TITLE = "Image File Not Found",
		ZIP_ERROR_TITLE = "Zip File Not Found",
		TITLE = "File Not Found";
	
	private static final int
		IMAGE_TYPE = 1,
		ZIP_TYPE = 2;
	
	private static final String
		IMAGE_STRING = " image ",
		ZIP_STRING = " zip ";
	
	private boolean image_type, zip_type;
	
	public FileError(String filepath, int type) {
		super(TITLE);
		
		image_type = (type == IMAGE_TYPE);
		zip_type = (type == ZIP_TYPE);
		if (image_type)
			setTitle(IMAGE_ERROR_TITLE);
		else if (zip_type)
			setTitle(ZIP_ERROR_TITLE);
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JTextArea txtBox = new JTextArea("\n\nFailed to open", 11, 200);
        if (image_type)
        	txtBox.append(IMAGE_STRING);
        else if (zip_type)
        	txtBox.append(ZIP_STRING);
        txtBox.append("file\n\n");
        txtBox.append(filepath+"\n\n");
        
        txtBox.setLineWrap(true);
        txtBox.setEditable(false);
        
        contentPanel.add(txtBox);
        add(contentPanel);
        setPreferredSize(new Dimension(350, 200));
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        System.out.println("Cannot open image file "+filepath);
	}

}