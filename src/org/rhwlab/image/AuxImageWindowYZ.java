/*
 * Created on Mar 31, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.image;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputAdapter;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.acetree.AnnotInfo;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.utils.EUtils;


/**
 * @author biowolp
 *
 * Creates a YZ image from a stack of planes at a given time
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AuxImageWindowYZ extends JFrame {

    AceTree                 iAceTree;
    ImageCanvas             iImgCanvas;
    ImagePlus               iImgPlus;
    String                  iTitle;
    MouseHandler            iMouseHandler;
    Hashtable               iAnnotsShown;
    JToolBar                iToolBar;
    JLabel                  iMouseData;
    NucleiMgr               iNucleiMgr;
    double                  iZPixRes;
    int                     iXLoc;
    int                     iTime;
    ImageProcessor          iDataProc;
    boolean                 iShowCentroids;
    boolean                 iShowAnnotations;
    
    public AuxImageWindowYZ(String title, ImageProcessor dataProc, AceTree aceTree, int x) {
        super(title);
        iTitle = title;
        //println("AuxImageWindow: " + iTitle);
        iAceTree = aceTree;
        iXLoc = x;
        iNucleiMgr = iAceTree.getNucleiMgr();
        iZPixRes = iNucleiMgr.getZPixRes();
        //iTime = Integer.parseInt((iTitle.split("@"))[0]);
        String s = iTitle.split(" @")[0];
        int k = s.lastIndexOf("-t");
        s = s.substring(k + 2);
        iTime = Integer.parseInt(s);
        iDataProc = dataProc; 
        iImgPlus = new ImagePlus();
        /*note I have set default behavior to not display circles 
         * as the circle drawing code doesnt look like it was written
         * in a general way and they are hence wrong at least sometimes
         * the feature doesnt seem important enought to fix rather than 
         * disable -AS 2013
        */
        iShowCentroids = false;//iAceTree.getShowCentroids();
        iShowAnnotations = false;//iAceTree.getShowAnnotations();
        iAnnotsShown = new Hashtable();
        updateImagePlus();

        ImageCanvas ic = new ImageCanvas(iImgPlus);
        iImgCanvas = ic;
    
        iToolBar = new JToolBar();
        iToolBar.add(new JLabel("Mouse at: "));
        iMouseData = new JLabel("");
        iToolBar.add(iMouseData);
    
        Container c = getContentPane();
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        jp.add(iToolBar, BorderLayout.NORTH);
        jp.add(iImgCanvas, BorderLayout.CENTER);
        c.add(jp);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        WinEventMgr wem = new WinEventMgr();
        addWindowFocusListener(wem);
        addWindowListener(wem);
        iMouseHandler = new MouseHandler(this);
        iImgCanvas.addMouseMotionListener(iMouseHandler);
        iImgCanvas.addMouseListener(iMouseHandler);
    }
    
    private void updateImagePlus() {
        byte [] bpix = (byte [])iDataProc.getPixels();
        byte [] R = new byte[bpix.length];
        byte [] G = new byte[bpix.length];
        byte [] B = new byte[bpix.length];
        ColorProcessor iproc3 = new ColorProcessor(iDataProc.getWidth(), iDataProc.getHeight());
        iproc3.getRGB(R, G, B);
        G = bpix;
        iproc3.setRGB(R, G, B);
        if (iShowCentroids) showCentroids(iTime, iXLoc, iproc3);
        if (iShowAnnotations) showAnnotations(iproc3);
        iImgPlus.setProcessor(iTitle, iproc3);
        //return new ImagePlus(iTitle, iproc3);
        if (iImgCanvas != null) iImgCanvas.repaint();
        repaint();

    }

    protected void showCentroids(int time, int x, ImageProcessor iproc) {
        double zPixRes = 11.;
        Vector v = (Vector)iNucleiMgr.getNucleiRecord().elementAt(time - 1);
        //ImageProcessor iproc = getImagePlus().getProcessor();
        //iproc.setColor(Color.red);
        iproc.setColor(NUCCOLOR);
        iproc.setLineWidth(WIDTHS[1]);
        Enumeration e = v.elements();
        iAceTree.getCurrentCell().getName();
        while(e.hasMoreElements()) {
            Nucleus n = (Nucleus)e.nextElement();
            if (n.status < 0) continue;
            //println("n: " + n);
            //if (n.index == 2) System.out.println("showCentroids: " + n);
            double u = nucDiameter(n, x);
            //if (n.index==2) System.out.println("showCentroids: " + x);
            if (u > 0) {
                int xx = (int)(n.z * zPixRes);
                iproc.setColor(NUCCOLOR);
                iproc.drawPolygon(EUtils.pCircle(xx, n.y, (int)(u/2.)));
            }
            
        }
    }
    
    private void showAnnotations(ImageProcessor iproc) {
        iproc.setColor(Color.white);
        Enumeration e = iAnnotsShown.elements();
        while (e.hasMoreElements()) {
            AnnotInfo ai = (AnnotInfo)e.nextElement();
            iproc.drawString(ai.iName, ai.iX, ai.iY);
        }
        
    }
    
    public double nucDiameter(Nucleus n, double x) {
        if (n == null) return -1; //covers some issues re currentCell and not tracking
        double r = -0.5;
        double cellPlane = n.x;
        double R = n.size/2.; //pixels
        double y = (cellPlane - x)/R;
        double r2 = 1 - y*y;
        if (r2 >= 0.) r = Math.sqrt(r2)*R;
        return 2*r;
    }
    
    
    private class WinEventMgr extends WindowAdapter {
        @Override
		public void windowGainedFocus(WindowEvent e) {
        }
        @Override
		public void windowClosing(WindowEvent e) {
            dispose();
        }
    }
    
    class MouseHandler extends MouseInputAdapter {

        public MouseHandler(AuxImageWindowYZ iw) {
            super();
        }

        @Override
		public void mouseMoved(MouseEvent e) {
            String s = POSITION + e.getY() + CS + DF1.format(e.getX()/iZPixRes);
            iMouseData.setText(s);
        }
        
        @Override
		public void mouseClicked(MouseEvent e) {
            //println("mouseClicked: " + e.getX() + CS + e.getY());
            int plane = (int)Math.round(e.getX()/iZPixRes);
            Nucleus n = iNucleiMgr.findClosestNucleus(iXLoc, e.getY(), plane, iTime);
            if (n == null) return;
            int x = (int)Math.round(n.z * iZPixRes);
            AnnotInfo ai = new AnnotInfo(n.identity, x, n.y);
            if (iAnnotsShown.get(ai.iName) != null) iAnnotsShown.remove(ai.iName);
            else iAnnotsShown.put(ai.iName, ai);
            updateImagePlus();
        }
        
    }

    private static final Color NUCCOLOR = new Color(140,70,255);
    public static final int [] WIDTHS = {1,2,3};

    
    private static final String 
         CS = ", "
        ,POSITION = "yz = "
        ;
    
    private static DecimalFormat DF1  = new DecimalFormat("###.#");
    private static void println(String s) {System.out.println(s);}
    public static void main(String[] args) {
    }
}
