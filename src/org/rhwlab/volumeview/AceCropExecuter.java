package org.rhwlab.volumeview;

import ij.ImagePlus;
import ij.WindowManager;
import ij.IJ;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.gui.Toolbar;

import java.awt.Color;
import ij.gui.GenericDialog;

public class AceCropExecuter {
	private AceUniverse univ;
	private ImagePlus currentImage;

	public AceCropExecuter(AceUniverse univ) {
		this.univ = univ;
	}

	/* Function: newCrop
	 * Usage: executer.newCrop();
	 ---
	 * Opens up new crop image, corresponding to 
	 * universe-stored image file.
	 */	
	public void newCrop() {
		try {
			currentImage = null;
			currentImage = IJ.openImage(univ.getPath());
			transformImg(currentImage);
			currentImage.show();
			setRectangleTool();
		} catch(NullPointerException exception) {
			System.out.println("The image path you are trying to open is not available.");
		}		
	}

	/* Function: setWindowLevel
	 * Usage: executer.setWindowLevel();
	 ---
	 * Saves current window and level.
	 */	
	public void setWindowLevel() {
		try {
			double min = IJ.getImage().getDisplayRangeMin();
			double max = IJ.getImage().getDisplayRangeMax();
			Double minimum = new Double(min);
			Double maximum = new Double(max);
			AceUniverse.getSettings().put("RangeMin", minimum);
			AceUniverse.getSettings().put("RangeMax", maximum);
		} catch (RuntimeException f) {
		  System.out.println("No image is open");
		}
	}

	/* Function: setCrop
	 * Usage: setCrop();
	 ---
	 * Sets crop universe to desired crop, according to reset,
	 * crop Z, or Apply XY Crop.
	 */	
	public void setCrop() {
		try {
			univ.updateContent();
			univ.removeAllContents();
			univ.addContent();
		} catch (RuntimeException f) {
		  System.out.println("No image is open");
		}
	}
	/* Function: reverseStack
	 * Usage: reverseStack(img); 
	 ---
	 * Reverses image stack.
	 */
	public void reverseStack(ImagePlus imp) {
		ImageStack stack = imp.getStack();
		for (int i = 1; i <= stack.getSize(); i++) {
			ImageProcessor ip = stack.getProcessor(i);
			ip.flipHorizontal();
			stack.setProcessor(ip, i);
		}
	}

	/* Function: transformImg
	 * Usage: transformImg(img);
	 ---
	 * Transforms image to pixelDepth of 4, and gray 8 bit.
	 */	
	public void transformImg (ImagePlus img) {
		Calibration cal = img.getCalibration();
		cal.pixelDepth = 4;
       	img.setCalibration(cal);
       	reverseStack(img);
	}

	/* Function: reloadCrop
	 * Usage: executer.reloadCrop();
	 ---
	 * Removes global ROI setting, so that crop settings are completely reset.
	 */	
	public void reloadCrop() {
		AceUniverse.getSettings().remove("ROI");
		AceUniverse.getSettings().remove("RangeMin");
		AceUniverse.getSettings().remove("RangeMax");
		AceUniverse.getSettings().remove("TransformMatrix");
		AceUniverse.getSettings().remove("Zoom");
		reloadCropZ();
		setCrop();
		setHandTool();
	}

	/* Function: saveROI
	 * Usage: executer.saveROI();
	 ---
	 * Saves crop region of interest for current window.
	 * Global crop ROI setting updated.
	 */	
	public void saveROI() {
		if (WindowManager.getCurrentWindow() != null) {
				ImagePlus imp = IJ.getImage();
				Roi roi = imp.getRoi();
				if (roi == null) {
					System.out.println("There is no highlighted region of interest.");
					return;
				} 
				AceUniverse.getSettings().put("ROI", roi);
				System.out.println("Crop Roi and Contrast saved");
				setWindowLevel();
				setCrop();
				setHandTool();
		} else {
				System.out.println("No window is open.");
		}		
	}

	/* Function: cropZ
	 * Usage: executer.cropZ();
	 ---
	 * Opens dialog box for inputting desired
	 * stack slices, range in the form of a-b,
	 * and saves the setting.
	 */	
	public void cropZ() {
		if (WindowManager.getCurrentWindow() != null) {
			try {
				String userInput = showDialog();
				userInput = userInput.replaceAll("\\s","");
				String[] range = userInput.split("-");
				int first = Integer.parseInt(range[0]);
				int last = Integer.parseInt(range[1]);
				int maxStackHeight = IJ.openImage(univ.getPath()).getStack().getSize();
				if (first >= last || !(first >= 1 && first <= maxStackHeight) || !(last >= 1 && last <= maxStackHeight)) 
					throw new Exception();
				AceUniverse.getSettings().put("stackFirst", new Integer(first));
				AceUniverse.getSettings().put("stackLast", new Integer(last));
				System.out.println("cropped Z, " + userInput);
				setCrop();
				setHandTool();
			} catch (Exception e) {
				System.out.println("Your input is invalid.");
			}  
		} else {
			System.out.println("no ImageJ image is open");
		}
	}

	/* Function: showDialog
	 * Usage: String userInput = showDialog();
	 ---
	 * Helper method for cropZ. Displays dialog
	 * for user input - stack range.
	 */	
	String showDialog() {
		GenericDialog gd = new GenericDialog("Substack Maker");
		gd.setInsets(10,45,0);
		gd.addMessage("Enter a range (e.g. 2-14).\nThe range spans the original stack, only.", null, Color.darkGray);
		gd.addStringField("Slices:", "", 40);
		gd.showDialog();
		if (gd.wasCanceled())
			return null;
		else {
			return gd.getNextString();
		}
	}

	/* Function: reloadCropZ
	 * Usage: executer.reloadCropZ();
	 ---
	 * Restores image stack to original height.
	 */	
	public void reloadCropZ() {
		AceUniverse.getSettings().put("stackFirst", new Integer(1));
		AceUniverse.getSettings().put("stackLast", new Integer(
		IJ.openImage(univ.getPath()).getStack().getSize()));
		System.out.println("restoring image stack height");
	}

	public void setHandTool() {
		univ.ui.setHandTool();
	}

	public void setRectangleTool() {
		univ.ui.setTool(Toolbar.RECTANGLE);
	}

	public void contrastAdjuster() {
		try{
			IJ.runPlugIn(IJ.getImage(), "ij.plugin.frame.ContrastAdjuster", "wl");
		} catch (RuntimeException e) {
				IJ.getInstance().setVisible(true);
				IJ.getInstance().setVisible(false);
				System.out.println("no ImageJ image is open");
		}			
	}
}
