package org.rhwlab.acetree;

import java.util.Arrays;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BookmarkDialog extends JFrame implements ActionListener{
	
	protected JMenuBar 	iMenuBar;
	protected JMenu		iFileMenu;
	protected JMenuItem	iLoad;
	protected JMenuItem iAppend;
	protected JMenuItem iSave;
	protected JMenuItem iClose;
	
	protected JButton 	iAdd;
	protected JButton 	iDelete;
	protected JButton 	iClear;
	
	protected JPanel	iContainerPanel;
	
	// Path to directory of the view config file
	// (used for saving and loading)
	protected String	iPath;
	
	// JList<String> used in Java 1.7 or higher
	//protected JList<String>		iJList;
	protected JList				iJList;
	protected DefaultListModel	iListModel;
	
	public BookmarkDialog(String path) {
		super("Bookmarked Cells");
		fillWindow();
		setLocationRelativeTo(null);
		setSize(new Dimension(260, 540));
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setVisible(true);
	}
	
	// Fills main window with top menu, edit buttons, and empty cell list
	public void fillWindow() {
		iContainerPanel = new JPanel();
		// Lay out components from top to bottom
		iContainerPanel.setLayout(new BoxLayout(iContainerPanel, BoxLayout.Y_AXIS));
		addMenu();
		addList();
		// Add filled panel to frame
		add(iContainerPanel);
	}
	
	// Creates menu bar and edit buttons and adds them to main panel
	public void addMenu() {
		// Create menu bar and menu items
		iMenuBar = new JMenuBar();
		iFileMenu = new JMenu("File");
		iLoad = new JMenuItem("Load From File");
		iLoad.addActionListener(this);
		iFileMenu.add(iLoad);
		iAppend = new JMenuItem("Append From File");
		iAppend.addActionListener(this);
		iFileMenu.add(iAppend);
		iSave = new JMenuItem("Save To File");
		iSave.addActionListener(this);
		iFileMenu.add(iSave);
		iClose = new JMenuItem("Close");
		iClose.addActionListener(this);
		iFileMenu.addSeparator();
		iFileMenu.add(iClose);
		iMenuBar.add(iFileMenu);
		setJMenuBar(iMenuBar);
		
		// Create Edit menu
		iAdd = new JButton("Add Active Cell");
		// Action listener for iAdd button is implemented in AceTree
		iAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
		iContainerPanel.add(iAdd);
		
		iDelete = new JButton("Delete Selected Cell");
		iDelete.addActionListener(this);
		iDelete.setAlignmentX(Component.CENTER_ALIGNMENT);
		iContainerPanel.add(iDelete);
		
		iClear = new JButton("Clear List");
		iClear.addActionListener(this);
		iClear.setAlignmentX(Component.CENTER_ALIGNMENT);
		iContainerPanel.add(iClear);
	}
	
	// Instantiates empty list of cells and adds it to the panel
	public void addList() {
		JLabel test = new JLabel("Bookmarked Cells List");
		test.setAlignmentX(Component.CENTER_ALIGNMENT);
		iContainerPanel.add(test);
		
		iListModel = new DefaultListModel();
		iJList = new JList (iListModel);
		iJList.setLayoutOrientation(JList.VERTICAL);
		iJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane scrollList = new JScrollPane(iJList);
		scrollList.setAlignmentX(Component.CENTER_ALIGNMENT);
		iContainerPanel.add(scrollList);
	}
	
	// Sets path to where xml files are loaded and saved
	public void setPath(String path) {
		iPath = path;
	}
	
	// Add cell to current array list if is isn't there already
	// and sorts list
	public void addCell(String name) {
		if (!iListModel.contains(name)) {
			iListModel.addElement(name);
			sortList();
		}
	}
	
	// Removes cell at specified index from list
	public void deleteCell(int index) {
		iListModel.remove(index);
	}
	
	// Clears cell list
	public void clearList() {
		iListModel.clear();
	}
	
	// Menu item handler
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o == iClose)
			hideWindow();
		else if (o == iClear) {
			if (!iListModel.isEmpty()) {
				int confirm = JOptionPane.showConfirmDialog(this,
						"Are you sure you want\nto clear this list?\n",
						"Confirm",
						JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION)
					clearList();
			}
		}
		else if (o == iDelete) {
			if (iJList.getSelectedIndex() != -1)
				deleteCell(iJList.getSelectedIndex());
		}
		else if (o == iLoad) {
			loadFromFile(false);
		}
		else if (o == iAppend) {
			loadFromFile(true);
		}
		else if (o == iSave) {
			saveToFile();
		}
		
		// iAdd action listener implemented in AceTree
	}
	
	public void windowGainedFocus(WindowEvent e) { }

	public void windowLostFocus(WindowEvent e) { }
	
	// Shows window
	public void displayWindow() {
		setVisible(true);
	}
	
	// Hides window
	public void hideWindow() {
		setVisible(false);
	}
	
	// Sorts list model alphabetically
	public void sortList() {
		Object[] array = iListModel.toArray();
		Arrays.sort(array);
		clearList();
		for (Object name : array)
			iListModel.addElement(name);
	}
	
	// Loads xml file to specified by user in the file chooser dialog
	// Boolean input specifies whether to append cells from a file or load and overwrite the list
	public void loadFromFile(boolean toAppend) {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
		if (iPath != null)
			fileChooser.setSelectedFile(new File(iPath));
		else 
			fileChooser.setSelectedFile(new File("."));
        fileChooser.setDialogTitle("Load xml File");
        fileChooser.setFileFilter(xmlFilter);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
            	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            	Document doc = dBuilder.parse(file);
            	NodeList nodeList = doc.getElementsByTagName("cell");
            	if (!toAppend)
            		clearList();
        		populateListModel(nodeList);
            } catch(FileNotFoundException fnfe) {
                System.out.println("File not found.");
                return;
            } catch(Exception e) {
                e.printStackTrace();
                return;
            }
            System.out.println("Loaded bookmarked cells from: " + file);
        }
	}
	
	// Populates list model given a list of nodes loaded from xml file
	public void populateListModel(NodeList nodeList) {
		if (nodeList == null || nodeList.getLength() < 1) {
			return;
		}
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			Element nodeElem = (Element)node;
			String name = nodeElem.getAttribute("name");
			iListModel.addElement(name);
    	}
		sortList();
	}
	
	// Saves xml file to path specified by user in the file chooser dialog
	@SuppressWarnings("resource")
	public void saveToFile() {
		JFileChooser fileChooser = new JFileChooser();
		if (iPath != null)
			fileChooser.setSelectedFile(new File(iPath));
		else
			fileChooser.setSelectedFile(new File("."));
		fileChooser.setDialogTitle("Save xml File");
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
	        File file = new File(fileChooser.getSelectedFile()+"");
	        String path = file.getPath();
	        // Check for xml extension
	        if (path.lastIndexOf(".") != -1) {
	            String extension = path.substring(path.lastIndexOf("."),path.length());
	            if (!extension.equals(".xml"))
	            	file = new File(fileChooser.getSelectedFile()+".xml");
	        }
	        else {
	        	file = new File(fileChooser.getSelectedFile()+".xml");
	        }
	        path = file.getPath();
	        
	        try {
	            PrintWriter pw = new PrintWriter(new FileOutputStream(path), true);
	            pw.println("<?xml version='1.0' encoding='utf-8'?>");
	            pw.println();
	            pw.println("<cells>");
	            for (int i = 0; i < iListModel.size(); i++) {
	                StringBuffer sb = new StringBuffer();
	                sb.append("<cell ");
	                sb.append("name=\"" + (String)(iListModel.get(i)) + "\"/>");
	                pw.println(sb.toString());
	            }
	            pw.println("</cells>");	            
	        } catch(IOException ioe) {
	            ioe.printStackTrace();
	            return;
	        }
	        System.out.println("Saved bookmarked cells to: " + file);
        }
	}
	
	// Returns JList of cell names
	public JList getJList() {
		return iJList;
	}
	
	// Returns reference to the "Add Active Cell" button
	public JButton getAddButton() {
		return iAdd;
	}
	

}