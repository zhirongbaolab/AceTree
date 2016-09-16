package org.rhwlab.volumeview;

import org.rhwlab.acetree.AceTree;

import ij.ImagePlus;
import ij.IJ;
import ij3d.Image3DUniverse;
import ij3d.Content;

import javax.swing.JMenuBar;
import java.util.HashMap;
import java.util.Map;

import java.awt.Toolkit;

import java.awt.event.WindowEvent;
import javax.media.j3d.Transform3D;
import javax.swing.ImageIcon;
import java.net.URL;

public class AceUniverse extends Image3DUniverse {
	private Ace3DCropMenu cropMenu = null;
	private AceExecuter executer = null;
	private JMenuBar menuBar;
	private Content content;
	private String path3D = "empty";
	private String basedir = "empty";
    private String title = "empty";
	private ImagePlus imp3D;


	private static Map globalSettings;

        static {
            globalSettings = new HashMap<String, Object>();
        }
    
	public AceUniverse() {
		super();
		executer = new AceExecuter(this);
	}

	@Override
	public AceExecuter getExecuter() {
		if (executer == null) {
			executer = new AceExecuter(this);
		}

		return executer;
	}

        /*
         * Prefixes given title with existing base directory to set path.
         * Also sets this.title in the process.
         */
	public void setPath(String title) {
                this.title = title;
		path3D = basedir + title;
	}

	public String getPath() {
		return path3D;
	}

	public void setBaseDir(String basedir) {
		this.basedir = basedir;
	}

	public void setImage(ImagePlus imp3D) {
		this.imp3D = imp3D;
	}

	public ImagePlus getImage() {
		return imp3D;
	}

	public Content getContent() {
		return content;
	}

	public static Map getSettings() {
	    return globalSettings;
	}

	/* Function: updateContent
	 * Usage: universe3D.updateContent();
	 ---
	 * Sets image stack for universe, and sets initial settings,
	 * including stack range and crop of region of interest.
	 * Flips image stack left to right, and converts to gray 8 bit.
         *
	 */	
	public void updateContent() {
            imp3D = IJ.openImage(path3D);

            // if existing path3D wasn't found, try alternative path 
            // where a t is either removed or inserted into path title
            if(imp3D == null) {
                this.setPath(this.getAlternativeTitle(this.title));
                imp3D = IJ.openImage(path3D);
            }

	    executer.setCalibration(4);
	    executer.updateWindowLevel();

            // only flip the image if 't' is in the path title
            if(this.shouldFlip())
                executer.reverseStack();

	    executer.setROI();
	    executer.updateCropZ();
	    executer.convertToGray8();
	    System.out.println(path3D);
	}

        /* Function: getAlternativeTitle
         * Usage: updateContent
         * --
         * Given the title (ie name w/o path) of a file image with the name layout
         * (some alphanumeric/underscore string)_(optional t)(some number).(some extension) eg Decon_t180.tif, 
         * either removes or inserts the t between the underscore and series of digits and returns
         * the new string.
         *
         * @param String oldTitle
         *      the old name of the image file that could not be opened in updateContent()
         * @return String
         *      the same image file name with a t either removed or inserted
         */
        private String getAlternativeTitle(String title) {
            // if the title has a t in it already, remove it
            if(title.matches("\\w*_[tT]\\d*\\.\\w*")) //"\\p{Alnum}*_[tT]\\d*\\.\\p{Alnum}*"))
                return title.replaceFirst("_[tT]", "_");
            // else insert it
            else
                return title.replaceFirst("_", "_t");
        }

        /* Function: shouldFlip()
         * Usage: updateContent()
         * --
         * Returns a boolean saying whether or not the image should be flipped on updating.
         *
         * If the image name contains a 't' before its number series, the image stack should
         * be flipped on updating. Else, the image stack should not be flipped.
         *
         * Sample image name: Decon_t180.tif (should be flipped).
         */
        private boolean shouldFlip() {
            if(this.title.matches("\\w*_[tT]\\d*\\.\\w*"))
                return true;
            else
                return false;
        }


	/* Function: show
	 * Usage: universe3D.show();
	 ---
	 * Shows universe, with edited menubar.
	 * Makes window native to AceTree, changing icon and title.
	 */
	@Override
	public void show() {
		super.show();
		menuBar = getMenuBar();
		menuBar.remove(0);
		menuBar.remove(3);
		menuBar.remove(3);
		cropMenu = new Ace3DCropMenu(menuBar, this);
		menuBar.add(cropMenu);
		this.win.setTitle("AceTree 3D Viewer");
		URL imageURL = AceUniverse.class.getResource("/images/icon2.gif");
	    ImageIcon test=new ImageIcon(imageURL, "x");	
	    this.win.setIconImage(test.getImage());
	}
	
	/* Function: addContent
	 * Usage: addContent();
	 ---
	 * Adds a new content, and updates settings.
	 */
	public void addContent() {
		content = addVoltex(imp3D, 1);
		updateSettings();
		content.applyTranslation((float)0.01, (float)0.01, (float)0.01);
	}

	/* Function: closeWindow
	 * Usage: closeWindow();
	 ---
	 * Programatticaly forces window to close.
	 */
	public void closeWindow() {
		WindowEvent closingEvent = new WindowEvent(win, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);
	}

	/* Function: updateSettings
	 * Usage: updateSettings();
	 ---
	 * Makes settings persist, including zoom, current rotation and translation
	 * displayAs (volume, ortho, etc...), transfer function, and transparency.
	 */
	public void updateSettings() {
		executer.updateDisplayAs();
		executer.updateZoom();
		executer.updateTransform();
		executer.updateTransferFunction();
		executer.updateTransparency();
		executer.updateThreshold();
	}

	/* Function: setTransform
	 * Usage: executer.setTransform();
	 ---
	 * Adds global transform settings -- translation and rotation.
	 */	
	public void setTransform() {
		Transform3D t3d = new Transform3D();
		getZoomTG().getTransform(t3d);
		AceUniverse.getSettings().put("Zoom", toString(t3d).trim());
		Content c = getContent();
		Transform3D t1 = new Transform3D();
		c.getLocalTranslate().getTransform(t1);
		Transform3D t2 = new Transform3D();
		c.getLocalRotate().getTransform(t2);
		t1.mul(t2);
		float[] matrix = new float[16];
		t1.get(matrix);
		AceUniverse.getSettings().put("TransformMatrix", matrix);
	}

	/* Function: toString
	 * Usage: toString(t3d);
	 ---
	 * Helper method for setTransformCrop, converts
	 * transform to string.
	 */	
	private static final String toString(Transform3D t3d) {
		float[] xf = new float[16];
		t3d.get(xf);
		String ret = "";
		for(int i = 0; i < 16; i++)
			ret += " " + xf[i];
		return ret;
	}


	/* Function: cleanup
	 * Usage: cleanup();
	 ---
	 * If main window is closed, clear any remaining universes.
	 */
	@Override
	public void cleanup() {
		if (AceTree.getWasClosed() != 0) {
			setTransform();
		}
		super.cleanup();
		System.out.println("closing AceUniverse");
	}
}
