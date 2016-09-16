package org.rhwlab.volumeview;

import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import ij.ImagePlus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Ace3DCropMenu extends JMenu implements ActionListener {
	private JMenuBar menuBar;
	private JMenuItem newCrop;
	private JMenuItem setCrop;
	private JMenuItem reloadCrop;
	private JMenuItem setTransform;
	private JMenuItem saveCropZ;
	private JMenuItem cropZ;
	private JMenuItem saveROI;
	private JMenuItem reloadCropZ;
	private JMenuItem setWindowLevel;
	private JMenuItem setHandTool;
	private JMenuItem setRectangleTool;
	private JMenuItem contrastAdjuster;

	private AceUniverse univ;

	private ImagePlus croppedImg;

	private String path;
	private boolean isCrop;

	private JMenuItem testCrop;

	private AceCropExecuter executer;

	/* Constructor sets up menu on universe menuBar */
	public Ace3DCropMenu(JMenuBar menuBar, AceUniverse univ) {
		super("Crop");
		this.menuBar = menuBar;
		this.univ = univ;
		setup();
		this.menuBar.add(this);
		executer = new AceCropExecuter(univ);
	} 

	/* Sets up crop menu */
	public void setup() {
		newCrop = new JMenuItem("New Crop");
		newCrop.addActionListener(this);
		reloadCrop = new JMenuItem("Reset Crop");
		reloadCrop.addActionListener(this);
		saveROI = new JMenuItem("Apply XY Crop");
		saveROI.addActionListener(this);
		cropZ = new JMenuItem("Crop Z");
		cropZ.addActionListener(this);
		setHandTool = new JMenuItem("Set Hand Tool");
		setHandTool.addActionListener(this);
		setRectangleTool = new JMenuItem("Set Rectangle Tool");
		setRectangleTool.addActionListener(this);
		contrastAdjuster = new JMenuItem("Adjust Contrast");
		contrastAdjuster.addActionListener(this);
		this.add(newCrop);
		this.add(reloadCrop);
		this.add(contrastAdjuster);
		this.add(saveROI);
		this.add(cropZ);
		this.add(setHandTool);
		this.add(setRectangleTool);
	}

	public ImagePlus getCroppedImg() {
		return croppedImg;
	}

	public void setCroppedImg(ImagePlus croppedImg) {
		this.croppedImg = croppedImg;
	}

	/* Function: actionPerformed
	 ---
	 * Sets functionality for each of the buttons, running from
	 * the executer, except for cropZ, which runs an ImageJ plugin "SubstackMaker".
	 */	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == newCrop) {
		 	executer.newCrop();
		} else if (src == reloadCrop) {
			executer.reloadCrop();
		} else if (src == saveROI) {
			executer.saveROI();
		} else if (src == cropZ) {
			executer.cropZ();
		}  else if (src == setHandTool) {
			executer.setHandTool();
		} else if (src == setRectangleTool) {
			executer.setRectangleTool();
		} else if (src == contrastAdjuster) {
			executer.contrastAdjuster();
		}
	}
}
