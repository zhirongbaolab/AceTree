/*
 * Created on Sep 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.utils.EUtils;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.process.ImageProcessor;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CellMovementImage extends ImageWindow {

    public CellMovementImage(String title, ImagePlus imgPlus) {
        //System.out.println("CellMovementImage2 constructor: " + title);
        //super(title, imgPlus);
        iIsMainImgWindow = true;
        iTitle = title;
        setTitle(title);
        iImgPlus = imgPlus;
        ImageCanvas ic = new ImageCanvas(imgPlus);
        iImgCanvas = ic;
        Dimension d = ic.getSize();
        d.getHeight();
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(ic, BorderLayout.CENTER);
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //setImageTimeAndPlaneFromTitle();
        iAceTree = AceTree.getAceTree(null);
        updateCurrentInfo(true);
        getContentPane().add(p);
        pack();
        setVisible(true);
        setResizable(false);
        WinEventMgr wem = new WinEventMgr();
        addWindowListener(wem);
        addWindowFocusListener(wem);
        refreshDisplay();
        
    }
    
    @Override
	public void refreshDisplay() {
        String imageName = iAceTree.getImageManager().getCurrentImageName();
        //System.out.println("CellMovementImage2.refreshDisplay called: " + new GregorianCalendar().getTime());
        //new Throwable().printStackTrace();
        if (imageName == null) imageName = iTitle;
        else {
            if (imageName.indexOf(cTifPrefix) == -1) {
                imageName = cTifPrefix +imageName;
            }
            iTitle = imageName;
            setTitle(iTitle);
        }
        //System.out.println("EditImage.refreshDisplay2: " + iImagePlane + C.CS + iPlaneInc);
        updateCurrentInfo(true);
        //updateTextFields();
        //System.out.println("EditImage.refreshDisplay3: " + iImagePlane + C.CS + iPlaneInc);
        ImagePlus ip = null;
        //System.out.println("EditImage.refreshDisplay4: " + imageName);
        //ip = makeImage(imageName);
        switch (iAceTree.getColor()) {
        case 1: 
            ip = makeGreenImagePlus(ip);
            //System.out.println("makeGreen");
            break;
        case 2:
            ip = makeRedImagePlus(ip);
            //System.out.println("makeRed");
            break;
        case 3:
            ip = makePlainImagePlus(ip);
            //System.out.println("makePlain");
            break;
        default:
    }
        //System.out.println("EditImage.refreshDisplay5: " + imageName);
        if (ip != null) {
            iImgPlus.setProcessor(imageName, ip.getProcessor());
            //System.out.println("imageProcesser set");
        }
        //if (iAceTree.isTracking()) iAceTree.addMainAnnotation();
        //if (iAceTree.getShowAnnotations()) showAnnotations();
        //if (iAceTree.getShowCentroids()) showCentroids();
        showCentroids(0);
        showCentroids(1);
        //if (iSpecialEffect != null) showSpecialEffect();
        //iSpecialEffect = null;
        iImgCanvas.repaint();
        //System.out.println("EditImage.refreshDisplay exiting: " + new GregorianCalendar().getTime());
        //return iImgPlus;
    }

    protected void showCentroids(int increment) {
        int t = iAceTree.getImageManager().getCurrImageTime() + iTimeInc - 1 - increment;
        if (t < 0) return;
        Vector v = cNucleiMgr.getNucleiRecord().elementAt(t);
        ImageProcessor iproc = getImagePlus().getProcessor();
        if (increment == 0) {
            iproc.setColor(Color.red);
        } else iproc.setColor(Color.yellow);
        iproc.setLineWidth(cLineWidth);
        Enumeration e = v.elements();
        iAceTree.getCurrentCell().getName();
        while(e.hasMoreElements()) {
            Nucleus n = (Nucleus)e.nextElement();
            if (n.status < 0) continue;
            double x = cNucleiMgr.nucDiameter(n, iAceTree.getImageManager().getCurrImagePlane() + iPlaneInc);
            if (x > 0) {
                iproc.drawPolygon(EUtils.pCircle(n.x, n.y, (int)(x/2.)));
            }
            
        }
    }
    
    private void updateCurrentInfo(boolean detectChange) {
        //System.out.println("EditImage.updateCurrentInfo called: " + new GregorianCalendar().getTime());
        iTimeInc = iAceTree.getTimeInc();
        iPlaneInc = iAceTree.getPlaneInc();
        //iCurrentCell = iAceTree.getCurrentCell();
    }

    private class WinEventMgr extends WindowAdapter {
        @Override
		public void windowGainedFocus(WindowEvent e) {
            //System.out.println("windowGainedFocus: " + getTitle(e));
            refreshDisplay();
        }
        @Override
		public void windowClosing(WindowEvent e) {
            dispose();
            iAceTree.setEditImageNull(4);
        }
    }
    

    public static void main(String[] args) {
    }
}
