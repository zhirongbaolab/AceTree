/*
 * Created on Apr 13, 2006
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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AuxImageWindowXZ extends JFrame {

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
    int                     iYLoc;
    int                     iTime;
    ImageProcessor          iDataProc;
    boolean                 iShowCentroids;
    boolean                 iShowAnnotations;

    public AuxImageWindowXZ(String title, ImageProcessor dataProc, 
            AceTree aceTree, int y) {
        super(title);
        iTitle = title;
        iAceTree = aceTree;
        iYLoc = y;
        iNucleiMgr = iAceTree.getNucleiMgr();
        iZPixRes = iNucleiMgr.getZPixRes();
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
	 iShowCentroids =false;
        iShowAnnotations = false;
	     //  iShowCentroids = iAceTree.getShowCentroids();
        //iShowAnnotations = iAceTree.getShowAnnotations();
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
    
    protected void updateImagePlus() {
        byte [] bpix = (byte [])iDataProc.getPixels();
        byte [] R = new byte[bpix.length];
        byte [] G = new byte[bpix.length];
        byte [] B = new byte[bpix.length];
        ColorProcessor iproc3 = new ColorProcessor(iDataProc.getWidth(), iDataProc.getHeight());
        iproc3.getRGB(R, G, B);
        G = bpix;
        iproc3.setRGB(R, G, B);
        if (iShowCentroids) showCentroids(iTime, iYLoc, iproc3);
        if (iShowAnnotations) showAnnotations(iproc3);
        iImgPlus.setProcessor(iTitle, iproc3);
        if (iImgCanvas != null) iImgCanvas.repaint();
        repaint();

    }

    protected void showCentroids(int time, int y, ImageProcessor iproc) {
        //double zPixRes = 11.;
        Vector v = (Vector)iNucleiMgr.getNucleiRecord().elementAt(time - 1);
        iproc.setColor(NUCCOLOR);
        iproc.setLineWidth(WIDTHS[1]);
        Enumeration e = v.elements();
        iAceTree.getCurrentCell().getName();
        while(e.hasMoreElements()) {
            Nucleus n = (Nucleus)e.nextElement();
            if (n.status < 0) continue;
            double u = nucDiameter(n, y);
            if (u > 0) {
                int yy = (int)(n.z * iZPixRes);
                iproc.setColor(NUCCOLOR);
                iproc.drawPolygon(EUtils.pCircle(n.x, yy, (int)(u/2.)));
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
    
    public double nucDiameter(Nucleus n, double y) {
        if (n == null) return -1; //covers some issues re currentCell and not tracking
        //double zPixRes = 11.;
        double r = -0.5;
        double cellPlane = n.y;
        double R = n.size/2.; //pixels
        double x = (cellPlane - y)/R;
        double r2 = 1 - x*x;
        if (r2 >= 0.) r = Math.sqrt(r2)*R;
        return 2*r;
    }
    

    private class WinEventMgr extends WindowAdapter {
        @Override
		public void windowGainedFocus(WindowEvent e) {
            //System.out.println("windowGainedFocus: " + getTitle(e));
            //refreshDisplay(null);
        }
        @Override
		public void windowClosing(WindowEvent e) {
            dispose();
        }
    }
    
    class MouseHandler extends MouseInputAdapter {

        public MouseHandler(AuxImageWindowXZ windowXZ) {
            super();
        }

        @Override
		public void mouseMoved(MouseEvent e) {
            String s = POSITION + e.getX() + CS + DF1.format(e.getY()/iZPixRes);
            iMouseData.setText(s);
        }
        
        @Override
		public void mouseClicked(MouseEvent e) {
            int plane = (int)Math.round(e.getY()/iZPixRes);
            Nucleus n = iNucleiMgr.findClosestNucleus(e.getX(), iYLoc, plane, iTime);
            if (n == null) return;
            println("mouseClicked: " + n.identity + CS + n.x + CS + n.y + CS + n.z);
            int y = (int)Math.round(n.z * iZPixRes);
            AnnotInfo ai = new AnnotInfo(n.identity, n.x, y);
            if (iAnnotsShown.get(ai.iName) != null) iAnnotsShown.remove(ai.iName);
            else iAnnotsShown.put(ai.iName, ai);
            updateImagePlus();
        }
        
    }


    private static final Color NUCCOLOR = new Color(140,70,255);
    public static final int [] WIDTHS = {1,2,3};

    
    private static final String 
         CS = ", "
        ,POSITION = "xz = "
        ;
    
    private static DecimalFormat DF1  = new DecimalFormat("###.#");
    public static void println(String s) {System.out.println(s);}
    public static void main(String[] args) {
    }
}
