package org.rhwlab.image;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.Container;
import java.awt.Rectangle;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class VistaTest2 extends JFrame {
    static byte []          iRpix;
    static byte []          iGpix;
    static byte []          iBpix;
    
    String iTitle;
    ImagePlus   iImgPlus;
    ImageCanvas iImgCanvas;
    
    public VistaTest2(String title, ImagePlus iplus) {
        super(title);
        iTitle = title;
        iImgPlus = iplus;
        ImageCanvas ic = new ImageCanvas(iplus);
        iImgCanvas = ic;
        Container c = getContentPane();
        JPanel jp = new JPanel();
        jp.add(ic);
        c.add(jp);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static ImagePlus makeImage(String s) {
        ImagePlus ip = null;
        ip = doMakeImageFromTif(s);
        return ip;
    }

    public static ImagePlus doMakeImageFromTif(String s) {
        ImagePlus ip = readData();
        ip = convertToRGB(ip);
        return ip;
    }

    private static ImagePlus convertToRGB(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        byte [] bpix = (byte [])iproc.getPixels();
        byte [] R = new byte[bpix.length];
        byte [] G = new byte[bpix.length];
        byte [] B = new byte[bpix.length];
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.getRGB(R, G, B);
        G = bpix;
        R = getRedChannel(R);
        iRpix = R;
        iGpix = G;
        iBpix = B;
        return buildImagePlus(ip);
    }
    
    private static ImagePlus buildImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.setRGB(iRpix, iGpix, iBpix);
        ip.setProcessor("test", iproc3);
        return ip;
        
    }
    
    
    private static ImagePlus readData() {
        return openTiff();
    }
    
    private static ImagePlus openTiff() {
        ByteProcessor iprocG = new ByteProcessor(200, 200);
        Rectangle rectG = new Rectangle(50, 70, 50, 50);
        iprocG.setRoi(rectG);
        iprocG.setValue(128);
        iprocG.fill();
        //return new ImagePlus("vistaTest", iprocG);
        ImagePlus iplus = new ImagePlus("vistaTest", iprocG);
        //new ij.gui.ImageWindow(iplus);
        return iplus;

    }
    

    private static byte [] getRedChannel(byte [] R) {
        ByteProcessor iprocR = new ByteProcessor(200, 200);
        Rectangle rectR = new Rectangle(70, 90, 50, 50);
        iprocR.setRoi(rectR);
        iprocR.setValue(128);
        iprocR.fill();
        R = (byte [])iprocR.getPixels();
        return R;
        
    }


    
    
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        println("VistaTest2.main, ");
        ImagePlus ip = VistaTest2.makeImage("");
        new VistaTest2("vistaTest2, ", ip);

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
