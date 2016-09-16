package org.rhwlab.image;

import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import java.awt.Rectangle;
import java.text.DecimalFormat;

public class VistaTest {
    
    public VistaTest() {
        
        ByteProcessor iprocG = new ByteProcessor(200, 200);
        Rectangle rectG = new Rectangle(50, 70, 50, 50);
        iprocG.setRoi(rectG);
        iprocG.setValue(128);
        iprocG.fill();

        ByteProcessor iprocR = new ByteProcessor(200, 200);
        Rectangle rectR = new Rectangle(70, 90, 50, 50);
        iprocR.setRoi(rectR);
        iprocR.setValue(128);
        iprocR.fill();

        byte [] gpix = (byte [])iprocG.getPixels();
        byte [] R = new byte[gpix.length];
        byte [] G = new byte[gpix.length];
        byte [] B = new byte[gpix.length];
        
        G = gpix;
        R = (byte [])iprocR.getPixels();
        ColorProcessor iproc3 = new ColorProcessor(iprocG.getWidth(), iprocG.getHeight());
        iproc3.setRGB(R, G, B);

        
        ImagePlus iplus = new ImagePlus("vistaTest", iproc3);
        new ImageWindow(iplus);
        
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        println("VistaTest.main, ");
        new VistaTest();

    }
    
    private static void println(String s) {System.out.println(s);}
    private static void print(String s) {System.out.print(s);}
    private static final String CS = ", ";
    private static final DecimalFormat DF0 = new DecimalFormat("####");
    private static final DecimalFormat DF1 = new DecimalFormat("####.#");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt1(double d) {return DF1.format(d);}
    private static String fmt0(double d) {return DF1.format(d);}

}
