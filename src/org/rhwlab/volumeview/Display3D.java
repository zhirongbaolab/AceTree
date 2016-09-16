package org.rhwlab.volumeview;

import org.rhwlab.acetree.AceTree;

import ij.gui.GUI;
import ij.IJ;
import java.awt.Frame;

/* Displays an image stack using the ImageJ 3D Viewer Plugin */

public class Display3D {
	private AceUniverse universe3D;
	private Thread thread;
	private Frame mainControl;

	/* Constructor: runs new Display 3D thread */
	public Display3D() {
		universe3D = new AceUniverse();
	}
	
	/* Function: setBaseDir
	 * Usage: setBaseDir(path);
	 ---
	 * Sets base directory of image files.
	 */
	 public void setBaseDir(String path) {
	 	universe3D.setBaseDir(path);
	 }

	/* Function: updateUniverse
	 * Usage: updateUniverse(transformTitle());
	 ---
	 * Sets the image path of the universe to the designated string "title",
	 * and updates the corresponding ImagePlus imp accordingly.
	 */
	public void updateUniverse(String title) {
		universe3D.setPath(title);
		universe3D.updateContent();
	}

	/* Function: addNext
	 * Usage: display3D.addNext();
	 ---
	 * Removes previous contents, and adds a new content corresponding
	 * to ImagePlus imp in the universe.
	 */
	public void addNext() {
		universe3D.setTransform();
		universe3D.removeAllContents();
		universe3D.addContent();
	}

	/* Class: thread3D
	 * Usage: thread = new Thread(new thread3D());
	 ---
	 * Starts a new thread, showing the universe and 
	 * adding the content.
	 */
	public class thread3D implements Runnable {
		@Override
		public void run() {
			try {
			   showContent();
			   AceTree.setWasClosed(1);
			} catch (Exception e) {
				return;
			}
		}	
	}

	/* Class: thread3DClose
	 * Usage: thread = new Thread(new thread3DClose());
	 ---
	 * Starts a new thread, showing the universe and 
	 * adding the content, and then closing it. The
	 * purpose of this is to enable the rectangle/
	 * hand tool.
	 */
	public class thread3DClose implements Runnable {
		@Override
		public void run() {
			try {
			   universe3D.show();
			   universe3D.closeWindow();
			   return;
			} catch (Exception e) {
				return;
			}
		}	
	}

	/* Function: showContent
	 * Usage: showContent();
	 ---
	 * Shows universe contents and adds them, on
	 * thread's first run. Centers unvierse window.
	 */
	public void showContent() {
		universe3D.show();
		GUI.center(universe3D.getWindow());
		universe3D.addContent();  
		System.out.println("content added");
	}

	/* Function: runThread
	 * Usage: display3D.runThread("close");
	 ---
	 * Prepares a new universe, and begins running the thread.
	 */
	public void runThread(String isClose) {
		if (IJ.getInstance() == null) {
           mainControl = new ij.ImageJ(); // Keep this, otherwise memory errors will arise
           IJ.getInstance().setLocation(5000,5000);
           IJ.getInstance().setVisible(false); 
        }
        if (isClose == "close") {
        	thread = new Thread(new thread3DClose());
        } else {
        	thread = new Thread(new thread3D());
        }
		thread.start();
	}

	/* Function: getUniverse
	 * Usage: getUniverse();
	 ---
	 * Returns the display's universe.
	 */
	public AceUniverse getUniverse() {
		return universe3D;
	}
}  
