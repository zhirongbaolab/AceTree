package org.rhwlab.volumeview;

import ij3d.Executer;
import ij3d.Image3DUniverse;
import ij3d.Content;
import ij3d.ContentConstants;
import ij3d.ContentInstant;
import ij3d.gui.LUTDialog;
import ij.gui.GenericDialog;
import java.awt.event.AdjustmentListener;
import java.awt.event.TextListener;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.Checkbox;
import java.awt.event.WindowEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.TextEvent;

import javax.media.j3d.Transform3D;

import ij.IJ;
import ij.measure.Calibration;
import ij.process.StackConverter;
import ij.process.StackProcessor;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ij.ImagePlus;

import javax.vecmath.Matrix4f;
import java.awt.Rectangle;
import ij.gui.Roi;

public class AceExecuter extends Executer {
	private AceUniverse univ;

	public AceExecuter(AceUniverse univ) {
		super(univ);
		this.univ = univ;
	}

	/* Function: setCalibration
	 * Usage: executer.setCalibration(pixelDepth);
	 ---
	 * Sets pixel depth of image.
	 */
	public void setCalibration(int pixelDepth) {
		Calibration cal = univ.getImage().getCalibration();
            cal.pixelDepth = pixelDepth;
            univ.getImage().setCalibration(cal);
	}

	/* Function: convertToGray8
	 * Usage: executer.convertToGray8();
	 ---
	 * Converts input image to Gray 8 bit.
	 */
	public void convertToGray8() {
		new StackConverter(univ.getImage()).convertToGray8();
	}

	/* Function: updateTransferFunction
	 * Usage: executer.updateTransferFunction();
	 ---
	 * Allows transfer function settings to persist in time.
	 * Set transfer function to desired value, and it
	 * will be automatically saved.
	 */
	public void updateTransferFunction() {
		int[] r, g, b, a;
		if (AceUniverse.getSettings().containsKey("RedLUT") && AceUniverse.getSettings().containsKey("BlueLUT") &&
				AceUniverse.getSettings().containsKey("GreenLUT") && AceUniverse.getSettings().containsKey("AlphaLUT")) {
			r = (int[])AceUniverse.getSettings().get("RedLUT");
			g = (int[])AceUniverse.getSettings().get("GreenLUT");
			b = (int[])AceUniverse.getSettings().get("BlueLUT");
			a = (int[])AceUniverse.getSettings().get("AlphaLUT");
			univ.getContent().setLUT(r, g, b, a);
		}
	}

	/* Function: updateTranparency
	 * Usage: executer.updateTransparency();
	 ---
	 * Allows transparency settings to persist in time.
	 * Set transparency to desired value, and it will be
	 * automatically saved.
	 */
	public void updateTransparency() {
		if (AceUniverse.getSettings().containsKey("Transparency")) {
			Float f = (Float)AceUniverse.getSettings().get("Transparency");
			univ.getContent().setTransparency(f.floatValue());
		}
	}

	/* Function: updateThreshold
	 * Usage: executer.updateThreshold();
	 ---
	 * Allows threshold settings to persist in time.
	 * Set threshold to desired value, and it
	 * will be automatically saved.
	 */
	public void updateThreshold() {
		if (AceUniverse.getSettings().containsKey("threshold")) {
			Integer i = (Integer)AceUniverse.getSettings().get("threshold");
			univ.getContent().setThreshold(i.intValue());
		}
	}

	/* Function: updateDisplayAs
	 * Usage: executer.updateDisplayAs();
	 ---
	 * Allows "Display As" settings to persist in time.
	 * Set display to desired value, and it will be
	 * automatically saved.
	 */
	public void updateDisplayAs() {
		if (AceUniverse.getSettings().containsKey("DisplayAs")) {
			Integer type = (Integer)AceUniverse.getSettings().get("DisplayAs");
			univ.getContent().displayAs(type.intValue());
		}
	}

	/* Function: updateWindowLevel
	 * Usage: executer.updateWindowLevel();
	 ---
	 * Allows window/level settings to persist in time.
	 */
	public void updateWindowLevel() {
		if (AceUniverse.getSettings().containsKey("RangeMin") && AceUniverse.getSettings().containsKey("RangeMax")) {
			Double min = (Double)AceUniverse.getSettings().get("RangeMin");
			Double max = (Double)AceUniverse.getSettings().get("RangeMax");
			univ.getImage().setDisplayRange(min.doubleValue(), max.doubleValue());
		}
	}

	/* Function: updateCropZ
	 * Usage: executer.updateCropZ();
	 ---
	 * Allows "Crop Z" settings to persist in time.
	 */
	public void updateCropZ() {
		if (AceUniverse.getSettings().containsKey("stackFirst") && AceUniverse.getSettings().containsKey("stackLast")) {
			Integer first = (Integer)AceUniverse.getSettings().get("stackFirst");
			Integer last = (Integer)AceUniverse.getSettings().get("stackLast");
			int f = first.intValue();
			int l = last.intValue();
			String input = Integer.toString(f) + "-" + Integer.toString(l);
			System.out.println(input);
			SubStackMaker stackMaker = new SubStackMaker();
			ImagePlus imp = univ.getImage();
			univ.setImage(stackMaker.makeSubstack(imp, input));
			imp = null; 
		}
	}

	/* Function: reverseStack
	 * Usage: executer.reverseStack();
	 ---
	 * Flips universe's image stack, left to right.
	 */
	public void reverseStack() {
		ImageStack stack = univ.getImage().getStack();
		for (int i = 1; i <= stack.getSize(); i++) {
			ImageProcessor ip = stack.getProcessor(i);
			ip.flipHorizontal();
			stack.setProcessor(ip, i);
		}
	}

	/* Function: updateZoom
	 * Usage: executer.updateZoom();
	 ---
	 * Allows zoom settings to persist in time.
	 */
	public void updateZoom() {
		if (AceUniverse.getSettings().containsKey("Zoom")) {
			String zoomT;
			if((zoomT = (String)AceUniverse.getSettings().get("Zoom")) != null) {
				univ.getZoomTG().setTransform(t(zoomT));
				univ.getViewPlatformTransformer().updateFrontBackClip();
			}
		}
	}

	/* Function: t
	 * Usage: t(zoomT);
	 ---
	 * Transforms given string into float matrix.
	 */ 
	private static final Transform3D t(String s) {
		String[] sp = s.split(" ");
		float[] f = new float[16];
		for(int i = 0; i < sp.length; i++)
			f[i] = f(sp[i]);
		return new Transform3D(f);
	}

	/* Function: f
	 * Usage: f(sp[i]);
	 ---
	 * Helper method for t; parses string to float.
	 */
	private static final float f(String s) {
		return Float.parseFloat(s);
	}


	/* Function: updateTransform
	 * Usage: executer.updateTransform();
	 ---
	 * Allows rotation, translation settings to persist in time.
	 */
	public void updateTransform() {
		if (AceUniverse.getSettings().containsKey("TransformMatrix")) {
			Matrix4f matrix = new Matrix4f((float[])AceUniverse.getSettings().get("TransformMatrix"));
			univ.getContent().setTransform(new Transform3D(matrix));
		}
	}

	/* Function: setROI
	 * Usage: executer.setROI();
	 ---
	 * Allows crop settings to persist in time.
	 */
	@SuppressWarnings("deprecation")
	public void setROI() {
		if (AceUniverse.getSettings().containsKey("ROI")) {
        	Roi roi = (Roi)AceUniverse.getSettings().get("ROI");
        	ImagePlus imp = univ.getImage(); 
        	ImageStack stack = imp.getStack();
          	Rectangle rect = roi.getBoundingRect(); 
           	int left = rect.x; 
    	   	int top = rect.y; 
           	int width = rect.width; 
           	int height = rect.height;
           	StackProcessor sp = new StackProcessor(stack);
           	univ.getImage().setStack(sp.crop(left, top, width, height)); 
        }
	}

	/* Function: displayAs
	 * Usage: executer.displayAs(getSelected(), Content.VOLUME);
	 ---
	 * Performs "Display As" action... volume, ortho, etc.
	 * Sets global "DisplayAs" in HashMap of universe settings.
	 */
	@Override
	public void displayAs(Content c, int type) {
		if(!checkSel(c))
			return;
		c.displayAs(type);
		Integer integer = new Integer(type);
		AceUniverse.getSettings().put("DisplayAs", integer);
	}

	/* Function: checkSel
	 * Usage: !checkSel(c)
	 ---
	 * Helper method for displayAs. Checks if content is selected.
	 */
	private final boolean checkSel(Content c) {
		if(c == null) {
			IJ.error("Selection required");
			return false;
		}
		return true;
	}

	/* Function: changeThreshold
	 * Usage: executer.changeThreshold(getSelected());
	 ---
	 * Sets threshold and stores threshold in global value
	 * in HashMap of settings.
	 */
	@Override
	public void changeThreshold(final Content c) {
		IJ.getInstance().setVisible(true);
		if(!checkSel(c))
			return;
		if(c.getImage() == null) {
			IJ.error("The selected object contains no image data,\n" +
					"therefore the threshold can't be changed");
			return;
		}
		final ContentInstant ci = c.getCurrent();
		final SliderAdjuster thresh_adjuster = new SliderAdjuster() {
			@Override
			public synchronized final void setValue(ContentInstant ci, int v) {
				ci.setThreshold(v);
				AceUniverse.getSettings().put("threshold", new Integer(v));
				univ.fireContentChanged(c);
			}
		};
		final int oldTr = (ci.getThreshold());
		if(c.getType() == ContentConstants.SURFACE) {
			final GenericDialog gd = new GenericDialog(
				"Adjust threshold ...", univ.getWindow());
			final int old = ci.getThreshold();
			gd.addNumericField("Threshold", old, 0);
			gd.addCheckbox("Apply to all timepoints", true);
			gd.showDialog();
			if(gd.wasCanceled())
				return;
			int th = (int)gd.getNextNumber();
			th = Math.max(0, th);
			th = Math.min(th, 255);
			if(gd.getNextBoolean()) {
				c.setThreshold(th);
				AceUniverse.getSettings().put("threshold", new Integer(th));
			} else {
				ci.setThreshold(th);
				AceUniverse.getSettings().put("threshold", new Integer(th));
			}
			univ.fireContentChanged(c);
			record(SET_THRESHOLD, Integer.toString(th));
			return;
		}
		// in case we've not a mesh, change it interactively
		final GenericDialog gd =
				new GenericDialog("Adjust threshold...");
		gd.addSlider("Threshold", 0, 255, oldTr);
		((Scrollbar)gd.getSliders().get(0)).
			addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(final AdjustmentEvent e) {
				// start adjuster and request an action
				if(!thresh_adjuster.go)
					thresh_adjuster.start();
				thresh_adjuster.exec(e.getValue(), ci, univ);
			}
		});
		gd.addCheckbox("Apply to all timepoints", true);
		final Checkbox aBox = (Checkbox)gd.getCheckboxes().get(0);
		gd.setModal(false);
		gd.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				IJ.getInstance().setVisible(false);
				try {
					if(gd.wasCanceled()) {
						ci.setThreshold(oldTr);
						AceUniverse.getSettings().put("threshold", new Integer(oldTr));
						univ.fireContentChanged(c);
						return;
					}
					// apply to other time points
					if(aBox.getState()) {
						c.setThreshold(ci.getThreshold());
						AceUniverse.getSettings().put("threshold", new Integer(ci.getThreshold()));
					}

					record(SET_THRESHOLD,
						Integer.toString(
						c.getThreshold()));
				} finally {
					// [ This code block executes even when
					//   calling return above ]
					//
					// clean up
					if (null != thresh_adjuster)
						thresh_adjuster.quit();
				}
			}
		});
		gd.showDialog();
		IJ.getInstance().setLocation(5000,5000);
	}

	/* Class: FrameListener
	 * Usage: ld.addWindowListener(new FrameListener());
	 ---
	 * WindowListener class for changeThreshold. Hides ImageJ toolbar upon closing.
	 */
	class FrameListener extends WindowAdapter {
   		@Override
		public void windowClosed(WindowEvent e) {
    		IJ.getInstance().setVisible(false);
  		}
	}

	/* Function: adjustLUTs
	 * Usage: executer.adjustLUTs(getSelected());
	 ---
	 * Sets transfer function, and stores global values for
	 * r, g, b, a in HashMap.
	 */
	@Override
	public void adjustLUTs(final Content c) {
		IJ.getInstance().setVisible(true);
		if(!checkSel(c))
			return;
		final int[] r = new int[256]; c.getRedLUT(r);
		final int[] g = new int[256]; c.getGreenLUT(g);
		final int[] b = new int[256]; c.getBlueLUT(b);
		final int[] a = new int[256]; c.getAlphaLUT(a);

		LUTDialog ld = new LUTDialog(r, g, b, a);
		ld.addCtrlHint();
		ld.addListener(new LUTDialog.Listener() {
			@Override
			public void applied() {
				c.setLUT(r, g, b, a);
				AceUniverse.getSettings().put("RedLUT", r);
				AceUniverse.getSettings().put("GreenLUT", g);
				AceUniverse.getSettings().put("BlueLUT", b);
				AceUniverse.getSettings().put("AlphaLUT", a);
				univ.fireContentChanged(c);
			}
		});

		ld.addWindowListener(new FrameListener());
		ld.showDialog();
		IJ.getInstance().setLocation(5000,5000);
	}

	/* Function: changeTransparency
	 * Usage: executer.changeTransparency(getSelected());
	 ---
	 * Sets transparency and stores transparency in global value
	 * in HashMap of settings.
	 */
	@Override
	public void changeTransparency(final Content c) {
		if(!checkSel(c))
			return;
		final ContentInstant ci = c.getCurrent();
		final SliderAdjuster transp_adjuster = new SliderAdjuster() {
			@Override
			public synchronized final void setValue(ContentInstant ci, int v) {
				ci.setTransparency(v / 100f);
				Float f = new Float(v / 100f);
				AceUniverse.getSettings().put("Transparency", f);
				univ.fireContentChanged(c);
			}
		};
		final GenericDialog gd = new GenericDialog(
			"Adjust transparency ...", univ.getWindow());
		final int oldTr = (int)(ci.getTransparency() * 100);
		gd.addSlider("Transparency", 0, 100, oldTr);
		gd.addCheckbox("Apply to all timepoints", true);

		((Scrollbar)gd.getSliders().get(0)).
			addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if(!transp_adjuster.go)
					transp_adjuster.start();
				transp_adjuster.exec(e.getValue(), ci, univ);
			}
		});
		((TextField)gd.getNumericFields().get(0)).
			addTextListener(new TextListener() {
			@Override
			public void textValueChanged(TextEvent e) {
				if(!transp_adjuster.go)
					transp_adjuster.start();
				TextField input = (TextField)e.getSource();
				String text = input.getText();
				try {
					int value = Integer.parseInt(text);
					transp_adjuster.exec(value, ci, univ);
				} catch (Exception exception) {
					// ignore intermediately invalid number
				}
			}
		});
		final Checkbox aBox = (Checkbox)(gd.getCheckboxes().get(0));
		gd.setModal(false);
		gd.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (null != transp_adjuster)
					transp_adjuster.quit();
				if(gd.wasCanceled()) {
					float newTr = oldTr / 100f;
					ci.setTransparency(newTr);
					Float f = new Float(newTr);
					AceUniverse.getSettings().put("Transparency", f);
					univ.fireContentChanged(c);
					return;
				}
				// apply to all instants of the content
				if(aBox.getState()) {
					c.setTransparency(ci.getTransparency());
					Float f = new Float(ci.getTransparency());
					AceUniverse.getSettings().put("Transparency", f);
				}

				record(SET_TRANSPARENCY, Float.
					toString(((Scrollbar)gd.getSliders().
					get(0)).getValue() / 100f));
			}
		});
		gd.showDialog();
	}

	/* Class: sliderAdjuster
	 * Usage: final SliderAdjuster transp_adjuster = new SliderAdjuster() {}
	 ---
	 * Helper class for changeTransparency. Implements a new slider adjuster.
	 */
	private abstract class SliderAdjuster extends Thread {
		boolean go = false;
		int newV;
		ContentInstant content;
		Image3DUniverse univ;
		final Object lock = new Object();

		SliderAdjuster() {
			super("VIB-SliderAdjuster");
			setPriority(Thread.NORM_PRIORITY);
			setDaemon(true);
		}

		/*
		 * Set a new event, overwritting previous if any.
		 */
		void exec(final int newV, final ContentInstant content, final Image3DUniverse univ) {
			synchronized (lock) {
				this.newV = newV;
				this.content = content;
				this.univ = univ;
			}
			synchronized (this) { notify(); }
		}

		public void quit() {
			this.go = false;
			synchronized (this) { notify(); }
		}

		/*
		 * This class has to be implemented by subclasses, to define
		 * the specific updating function.
		 */
		protected abstract void setValue(final ContentInstant c, final int v);

		@SuppressWarnings("unused")
		@Override
		public void run() {
			go = true;
			while (go) {
				try {
					if (null == content) {
						synchronized (this) { wait(); }
					}
					if (!go) return;
					// 1 - cache vars, to free the lock very quickly
					ContentInstant c;
					int transp = 0;
					Image3DUniverse u;
					synchronized (lock) {
						c = this.content;
						transp = this.newV;
						u = this.univ;
					}
					// 2 - exec cached vars
					if (null != c) {
						setValue(c, transp);
					}
					// 3 - done: reset only if no new request was put
					synchronized (lock) {
						if (c == this.content) {
							this.content = null;
							this.univ = null;
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
}
