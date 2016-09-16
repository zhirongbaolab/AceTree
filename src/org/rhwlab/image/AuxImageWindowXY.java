/*
 * Created on Apr 19, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.image;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.utils.C;

/**
 * @author biowolp
 *
 * This version was used to explore handling the DIC image
 * At the moment (May 1, 2006) it is not in use
 */
public class AuxImageWindowXY extends AuxImageWindowXZ {
    
    ImageProcessor iGreen;

    public AuxImageWindowXY(String title, ImageProcessor green, ImageProcessor dataProc, 
            AceTree aceTree, int y) {
        super(title, dataProc, aceTree, y);
        iGreen = green;
        updateImagePlus2();
    }
    
    @Override
	protected void updateImagePlus() {
        ColorProcessor iproc3 = new ColorProcessor(iDataProc.getWidth(), iDataProc.getHeight());
        iImgPlus.setProcessor(iTitle, iproc3);
        
    
    }
    
    protected void updateImagePlus2() {
        byte [] bpix = (byte [])iDataProc.getPixels();
        long x = 0;
        for (int i=0; i < bpix.length; i++) x += bpix[i];
        System.out.println("updateImagePlus: " + x);
        byte [] R = new byte[bpix.length];
        byte [] G = new byte[bpix.length];
        byte [] B = new byte[bpix.length];
        ColorProcessor iproc3 = new ColorProcessor(iDataProc.getWidth(), iDataProc.getHeight());
        iproc3.getRGB(R, G, B);
        System.arraycopy(bpix, 0, R, 0, bpix.length);
        System.arraycopy(bpix, 0, G, 0, bpix.length);
        System.arraycopy(bpix, 0, B, 0, bpix.length);
        //G = bpix;
        //G = (byte [])iGreen.getPixels();
        //B = bpix;
        if (iGreen != null) {
            byte [] gpix = (byte [])iGreen.getPixels();
            println("updateImagePlus loop: " + gpix.length + C.CS + bpix.length);
            int count = 0;
            int u = 0;
            byte k = (byte)u;
            for (int i=0; i < gpix.length; i++) {
                if ((0xff & gpix[i]) >= 80) {
                    
                    G[i] = gpix[i];
                    R[i] = k;
                    B[i] = k;
                    count++;
                    //println("changed: " + i + C.CS + R[i] + C.CS + G[i] + C.CS + B[i]);
                    
                }
                
            }
            println("updateImagePlus changed: "  + count);
        }
        iproc3.setRGB(R, G, B);
        iImgPlus.setProcessor(iTitle, iproc3);
        //return new ImagePlus(iTitle, iproc3);
        if (iImgCanvas != null) iImgCanvas.repaint();
        repaint();

    }


    
    public static void main(String[] args) {
    }
}
