/*
 * Created on Mar 29, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.rhwlab.acetree.AceTree;
//import org.rhwlab.analyze.Analysis7.Centroid;
//import org.rhwlab.image.AuxImageWindow2;
import org.rhwlab.image.AuxImageWindowXY;
import org.rhwlab.image.AuxImageWindowXZ;
import org.rhwlab.image.AuxImageWindowYZ;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.image.ZipImage;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.EUtils;
import org.rhwlab.utils.Log;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Analysis10 extends Log {

    private JTextField iTextField;
    AceTree         iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    //Identity        iIdentity;

    public Analysis10(String title) {
        super(title);
        showMe();
        buildOutToolBar();
        //buildOutToolBar2();
        initialize();

    }

//    private void test1() {
//        println("test1 entered");
//        String s = iTextField.getText();
//        String [] sa = s.split(CS);
//        int time = Integer.parseInt(sa[0]);
//        int x = Integer.parseInt(sa[1]);
//        ImagePlus iplus = getOneXZ(time, x);
//        //ColorProcessor cp = new ColorProcessor(iplus.getWidth(), iplus.getHeight());
//        ImageProcessor iproc = iplus.getProcessor();
//
//        //byte [] bpix = (byte [])iproc.getPixels();
//        //byte [] R = new byte[bpix.length];
//        //byte [] G = new byte[bpix.length];
//        //byte [] B = new byte[bpix.length];
//        //ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
//        //iproc3.getRGB(R, G, B);
//        //G = bpix;
//        //iproc3.setRGB(R, G, B);
//        //showCentroids(time, x, iproc3);
//        //iplus = new ImagePlus(iplus.getTitle(), iproc3);
//        String s2 = String.valueOf(time)+"@"+x;
//        new AuxImageWindowYZ(s2, iproc, iAceTree, x);
//    }

//    private void test1(boolean bogus) {
//        println("test1 entered");
//        String s = iTextField.getText();
//        String [] sa = s.split(CS);
//        int time = Integer.parseInt(sa[0]);
//        //int plane = Integer.parseInt(sa[1]);
//        int planeEnd = iNucleiMgr.getPlaneEnd();
//        for (int i=1; i <= planeEnd; i++) {
//            saveOneXY(time, i);
//        }
//    }

//    private void test2() {
//        println("test2 entered");
//        String s = iTextField.getText();
//        String [] sa = s.split(CS);
//        int time = Integer.parseInt(sa[0]);
//        int x = 0; // = Integer.parseInt(sa[1]);
//        //ByteProcessor bp = new ByteProcessor(ImageWindow.cImageWidth, ImageWindow.cImageHeight);
//        ImageProcessor ip = getImageFromSecondPlane(time, 15);
//        ImageProcessor ipG = getImage(time, 15);
//        int y = 0;
//        String ss = time + "_" + y;
//        ImagePlus iplus = new ImagePlus(ss, ip);
//        ImageProcessor iproc = iplus.getProcessor();
//        String s2 = String.valueOf(time)+"@"+x;
//        new AuxImageWindowXY(s2, ipG, iproc, iAceTree, x);
//
//    }

//    private ImageProcessor getImageFromSecondPlane(int time, int plane) {
//        String imageFile = ImageWindow.cZipTifFilePath;
//        imageFile += "/" + ImageWindow.cTifPrefixR;
//        imageFile += iAceTree.makeImageName(time, plane);
//        ImageProcessor ip = getRedData(imageFile);
//        return ip;
//    }


//    private void test2(boolean bogus) {
//        println("test2 entered");
//        String s = iTextField.getText();
//        String [] sa = s.split(CS);
//        int time = Integer.parseInt(sa[0]);
//        int x; // = Integer.parseInt(sa[1]);
//
//        for (x = 100; x < 610; x += 10) {
//            saveOneXZ(time, x);
//        }
//
//    }


//    private void test3() {
//        println("test3 entered");
//        //int time = 30;
//        //int x = 400;
//        String s = iTextField.getText();
//        String [] sa = s.split(CS);
//        int time = Integer.parseInt(sa[0]);
//        int x = Integer.parseInt(sa[1]);
//        ImagePlus iplus = getOneYZ(time, x);
//        //ColorProcessor cp = new ColorProcessor(iplus.getWidth(), iplus.getHeight());
//        ImageProcessor iproc = iplus.getProcessor();
//
//        //byte [] bpix = (byte [])iproc.getPixels();
//        //byte [] R = new byte[bpix.length];
//        //byte [] G = new byte[bpix.length];
//        //byte [] B = new byte[bpix.length];
//        //ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
//        //iproc3.getRGB(R, G, B);
//        //G = bpix;
//        //iproc3.setRGB(R, G, B);
//        //showCentroids(time, x, iproc3);
//        //iplus = new ImagePlus(iplus.getTitle(), iproc3);
//        String s2 = String.valueOf(time)+"@"+x;
//        new AuxImageWindowXZ(s2, iproc, iAceTree, x);
//    }

//    private void test3(boolean bogus) {
//        println("test3 entered");
//        //int time = 30;
//        //int x = 400;
//        String s = iTextField.getText();
//        String [] sa = s.split(CS);
//        int time = Integer.parseInt(sa[0]);
//        int x = Integer.parseInt(sa[1]);
//        ImagePlus iplus = getOneXZ(time, x);
//        //ColorProcessor cp = new ColorProcessor(iplus.getWidth(), iplus.getHeight());
//        ImageProcessor iproc = iplus.getProcessor();
//
//        //byte [] bpix = (byte [])iproc.getPixels();
//        //byte [] R = new byte[bpix.length];
//        //byte [] G = new byte[bpix.length];
//        //byte [] B = new byte[bpix.length];
//        //ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
//        //iproc3.getRGB(R, G, B);
//        //G = bpix;
//        //iproc3.setRGB(R, G, B);
//        //showCentroids(time, x, iproc3);
//        //iplus = new ImagePlus(iplus.getTitle(), iproc3);
//        String s2 = String.valueOf(time)+"@"+x;
//        new AuxImageWindowYZ(s2, iproc, iAceTree, x);
//    }

    @SuppressWarnings("unused")
	protected void showCentroids(int time, int x, ImageProcessor iproc) {
        double zPixRes = 11.;
        Vector v = iNucleiMgr.getNucleiRecord().elementAt(time - 1);
        //ImageProcessor iproc = getImagePlus().getProcessor();
        //iproc.setColor(Color.red);
        iproc.setColor(COLOR[1]);
        iproc.setLineWidth(WIDTHS[1]);
        //iproc.setLineWidth(cLineWidth);
        Polygon p = null;
        Enumeration e = v.elements();
        String currentCellName = iAceTree.getCurrentCell().getName();
        while(e.hasMoreElements()) {
            Nucleus n = (Nucleus)e.nextElement();
            if (n.status < 0) continue;
            //println("n: " + n);
            //if (n.index == 2) System.out.println("showCentroids: " + n);
            double u = nucDiameter(n, x);
            //if (n.index==2) System.out.println("showCentroids: " + x);
            if (u > 0) {
                //if (n.identity.equals(currentCellName) && iAceTree.isTracking()) {
                //if (n.hashKey != null && n.hashKey.equals(iAceTree.getCurrentCell().getHashKey()) && iAceTree.isTracking()) {
                //    iproc.setColor(COLOR[iDispProps[SCENTROID].iLineageNum]);
                    //iproc.setColor(Color.white);
                    //System.out.println("showAnnotations dia, iImagePlane, iPlaneInc: " + x + CS + iImagePlane + CS + iPlaneInc);
                //}
                int xx = (int)(n.z * zPixRes);
                iproc.setColor(COLOR[1]);
                iproc.drawPolygon(EUtils.pCircle(xx, n.y, (int)(u/2.)));
                //iproc.setColor(Color.white);
                //iproc.drawString("test", xx, n.y);
                //iproc.setColor(Color.red);
                //drawRoi(iImagePlane + iPlaneInc, n, iproc);
                //iproc.setColor(COLOR[3]);
                //iproc.moveTo(xx, n.y);
                //iproc.drawString(n.identity);
            }

        }
        //iShowC.setText(HIDEC);
    }

    @SuppressWarnings("unused")
	public double nucDiameter(Nucleus n, double x) {
        if (n == null) return -1; //covers some issues re currentCell and not tracking
        double zPixRes = 11.;
        double r = -0.5;
        double cellPlane = n.x;
        double R = n.size/2.; //pixels
        double y = (cellPlane - x)/R;
        double r2 = 1 - y*y;
        if (r2 >= 0.) r = Math.sqrt(r2)*R;
        return 2*r;
    }

//    private ImagePlus getOneXZ(int time, int x) {
//        int planeEnd = iNucleiMgr.getPlaneEnd();
//        ByteProcessor bp = new ByteProcessor(11 * planeEnd - 1, ImageWindow.cImageHeight);
//        for (int m=1; m <= planeEnd; m++) {
//            ImageProcessor ip = getImage(time, m);
//            //println("test2: " + ip);
//            for (int i=0; i < ip.getHeight(); i++) {
//                int p = ip.getPixel(x, i);
//                for (int k=0; k < 11; k++) {
//                    bp.putPixel(k + m*11, i, p);
//                }
//            }
//        }
//        String s = time + "_" + x;
//        ImagePlus iplus = new ImagePlus(s, bp);
//        return iplus;
//
//    }

//    private ImagePlus getOneYZ(int time, int y) {
//        int planeEnd = iNucleiMgr.getPlaneEnd();
//        ByteProcessor bp = new ByteProcessor(ImageWindow.cImageWidth, 11 * planeEnd - 1);
//        for (int m=1; m <= planeEnd; m++) {
//            ImageProcessor ip = getImage(time, m);
//            //println("test2: " + ip + CS + time + CS + m + CS + planeEnd);
//            for (int i=0; i < ip.getWidth(); i++) {
//                int p = ip.getPixel(i, y);
//                for (int k=0; k < 11; k++) {
//                    bp.putPixel(i, k + m*11, p);
//                    //bp.putPixel(k + m*11, i, p);
//                }
//            }
//        }
//        String s = time + "_" + y;
//        ImagePlus iplus = new ImagePlus(s, bp);
//        return iplus;
//
//    }

//    private void saveOneXY(int time, int plane) {
//        ImageProcessor ip = getImage(time, plane);
//        FileSaver fs = new FileSaver(new ImagePlus("", ip));
//        String s = time + "_" + EUtils.makePaddedInt(plane);
//        fs.saveAsGif("/home/biowolp/0tmp/breadthImages/gif/" + s + ".gif");
//        println("/home/biowolp/0tmp/breadthImages/gif/" + s + ".gif");
//    }

//    private void saveOneXZ(int time, int x) {
//        int planeEnd = iNucleiMgr.getPlaneEnd();
//        ByteProcessor bp = new ByteProcessor(11 * planeEnd - 1, ImageWindow.cImageHeight);
//        for (int m=1; m <= planeEnd; m++) {
//            ImageProcessor ip = getImage(time, m);
//            //println("test2: " + ip);
//            for (int i=0; i < ip.getHeight(); i++) {
//                int p = ip.getPixel(x, i);
//                for (int k=0; k < 11; k++) {
//                    bp.putPixel(k + m*11, i, p);
//                }
//            }
//        }
//        FileSaver fs = new FileSaver(new ImagePlus("", bp));
//        String s = time + "_" + x;
//        fs.saveAsGif("/home/biowolp/0tmp/depthImages/gif/" + s + ".gif");
//        println("/home/biowolp/0tmp/depthImages/" + s + ".gif");
//
//    }


//    private ImageProcessor getImage(int time, int plane) {
//        String imageFile = ImageWindow.cZipTifFilePath;
//        imageFile += "/" + ImageWindow.cTifPrefix;
//        imageFile += iAceTree.makeImageName(time, plane);
//        ImageProcessor ip = getRedData(imageFile);
//        return ip;
//    }

    @SuppressWarnings("unused")
//	private int [] processImage(int time, int plane) {
//        //int time = 10;
//        //int plane = 15;
//        String imageFile = ImageWindow.cZipTifFilePath;
//        imageFile += "/" + ImageWindow.cTifPrefix;
//        imageFile += iAceTree.makeImageName(time, plane);
//        ImageProcessor ipRed = getRedData(imageFile);
//        println("processImage: " + ipRed);
//        ImagePlus iplus = new ImagePlus("testing", ipRed);
//        int height = iplus.getHeight();
//        println("processImage height=" + height);
//        ij.gui.ImageWindow imgWin = new ij.gui.ImageWindow(iplus);
//        //int [] ia = processImage(ipRed, cc);
//        return null;
//    }

//    private ImageProcessor getRedData(String greenName) {
//        //System.out.println("getRedData: " + greenName + C.CS + ImageWindow.cUseZip);
//        if (ImageWindow.cUseZip == 2) return getRedZipData(greenName);
//        FileInputStream fis;
//        ImagePlus ip = null;
//        //String ss = "/home/biowolp/AncesTree/temp2/images/050405-t050-p15.tif";
//        try {
//            fis = new FileInputStream(greenName);
//            byte [] ba = ImageWindow.readByteArray(fis);
//            ip = ImageWindow.openTiff(new ByteArrayInputStream(ba), false);
//            fis.close();
//	    if(ImageWindow.imagewindowUseStack==1) {
//		int markerChannel=1;
//		ip=ImageWindow.splitImage(ip,markerChannel);
//	    }
//            //ip = readData(fis);
//        } catch(IOException ioe) {
//            //System.out.println("ImageWindow.test3 exception ");
//            //System.out.println(ioe);
//        }
//        if (ip != null) return ip.getProcessor();
//        else return null;
//    }


    private ImageProcessor getRedZipData(String redName) {
        ZipImage zipImage = new ZipImage(redName);
        int k1 = redName.lastIndexOf("/") + 1;
        String ss = redName.substring(k1);
        int k2 = ss.indexOf(".");
        ss = ss.substring(0, k2);
        //System.out.println("using: " + ss);
        ZipEntry ze = null;
        if (zipImage != null) ze = zipImage.getZipEntry(ss + ".tif");
        //System.out.println("ZipEntry: " + ze);
        //if (cZipImage == null) cZipImage = new ZipImage(cZipTifFilePath);
        //ZipEntry ze = cZipImage.getZipEntry(s);
        ImagePlus ip = null;
        ip = zipImage.readData(ze, true);
        if (ip != null) return ip.getProcessor();
        else return null;
    }


    public void initialize() {
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        nuclei_record = iNucleiMgr.getNucleiRecord();
        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        //iIdentity = iNucleiMgr.getIdentity();
        //iRoot = iNucleiMgr.getRoot();
        iRoot = iAncesTree.getRoot();
    }



    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals(TEST1)) {
            //test1();
        } else if (s.equals(TEST2)) {
            //test2();
        } else if (s.equals(TEST3)) {
            //test3();
        } else if (s.equals(CLEAR)) {
            append("clear");
            iText.setText("");
        } else super.actionPerformed(e);
    }

    private void buildOutToolBar() {
        iToolBar.setMaximumSize(new Dimension(500,20));
        iToolBar.add(new JLabel("time:"));
        iTextField = new JTextField();
        iTextField.setColumns(15);
        iTextField.setText("1, 260");
        iToolBar.add(iTextField);
        JButton jb = null;
        jb = new JButton(CLEAR);
        addToolBarButton(jb);
        jb = new JButton(TEST1);
        addToolBarButton(jb);
        //jb = new JButton(TEST2);
        //addToolBarButton(jb);
        jb = new JButton(TEST3);
        addToolBarButton(jb);
    }

    private void buildOutToolBar2() {
        JToolBar iToolBar2 = new JToolBar();
        iToolBar2.setMaximumSize(new Dimension(500,20));
        iToolBar2.add(new JLabel("time:"));
        JTextField iTextField2 = new JTextField();
        iTextField2.setColumns(15);
        iTextField2.setText("1, 260");
        iToolBar2.add(iTextField2);
        JButton jb = null;
        jb = new JButton(CLEAR);
        iToolBar2.add(jb);
        jb = new JButton(TEST1);
        iToolBar2.add(jb);
        jb = new JButton(TEST2);
        iToolBar2.add(jb);
        jb = new JButton(TEST3);
        iToolBar2.add(jb);
        JPanel jp = new JPanel();
        jp.add(iToolBar2);
        add(jp);

    }

    private static final String
    CLEAR = "Clear"
   ,TEST1 = "One"
   ,TEST2 = "All"
   ,TEST3 = "Test3"
   ;

    public static final Color [] COLOR = {
            Color.RED
            ,new Color(140,70,255)
            ,Color.GREEN
            ,Color.YELLOW
            ,Color.CYAN
            ,Color.MAGENTA
            ,Color.PINK
            ,Color.LIGHT_GRAY
            ,Color.WHITE
    };

    public static final int [] WIDTHS = {1,2,3};

    public static void main(String[] args) {
    }

    private static void println(String s) {System.out.println(s);}
    private static final String CS = ", ";
}
