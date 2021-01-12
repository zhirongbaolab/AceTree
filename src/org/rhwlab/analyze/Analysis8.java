/*
 * Created on Oct 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.OvalRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.StackWindow;
import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.image.ZipImage;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.C;
import org.rhwlab.utils.Log;


/**
 * Consider this a template for future analyses.
 * Obsolete right now and not called.
 *
 * @author biowolp
 * @version 1.1 November 30, 2005
 *
 */
public class Analysis8 extends Log {
    AceTree         iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    Hashtable       iNucleiMgrHash;
    //Identity        iIdentity;
    private double     iXA; // for angle()
    private double     iYA;
    private double     iZA;
    private JFrame  iFrame;
    private JPanel  iPanel;
    private JTextField iTextField;
    private ImagePlus iStackPlus;
    private StackWindow iStackWindow;
    private ImagePlus iBlankStackPlus;
    private StackWindow iBlankStackWindow;
    private ImageStack iStack;
    private StackWindow iExtraStackWindow;


    public Analysis8(String title) {
        super(title);
        showMe();
        buildOutToolBar();
        initialize();
        iStackWindow = null;

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
        iNucleiMgrHash = iAceTree.getNucleiMgrHash();
    }

    public void test2() {
        System.out.println("test2 StackWindow: " + iStackWindow);
        if (iStackWindow != null) {
            append("closing: " + iStackWindow);
            iStackWindow.close();
            iStackWindow = null;
        }
        if (iBlankStackWindow != null) {
            append("closing: " + iBlankStackWindow);
            iBlankStackWindow.close();
            iBlankStackWindow = null;
        }
        if (iExtraStackWindow != null) {
            append("closing: " + iExtraStackWindow);
            iExtraStackWindow.close();
            iExtraStackWindow = null;
        }
    }

    // builds up an image stack for thinking purposes
    public void test3() {
        append(TEST1);
        ImagePlus stack = createBlankStack("test");
        ImageStack stack2 = stack.getStack();
        addCircles(stack2);
        ImagePlus plusStack2 = createBlankStack("test add");
        computeCrosstalkUsingROI(plusStack2, stack);
        //addStacks(plusStack2, stack);
        iStackWindow = new StackWindow(plusStack2);
        //iStackWindow = new StackWindow(stack);
    }

    private void addStacks(ImagePlus dst, ImagePlus src) {
        for (int i=1; i <= 7; i++) {
            ImageProcessor dstProc = dst.getStack().getProcessor(i);
            ImageProcessor srcProc = src.getStack().getProcessor(i);
            //srcProc.multiply(0.3);
            dstProc.copyBits(srcProc, 0, 0, Blitter.ADD);
            //dstProc.copyBits(srcProc, 0, 0, Blitter.ADD);
        }
    }

    private void computeCrosstalkUsingROI(ImagePlus dst, ImagePlus src) {
        OvalRoi oRoi = makeOvalRoi(50, 50, 40);
        double r1 = 0.5;
        double r2 = 0.25;
        ImageProcessor d, s1, s2;
        // stack position 1 effects
        d = dst.getStack().getProcessor(1);
        s2 = src.getStack().getProcessor(3);
        processOneLevel(d, s2, r2, oRoi);
        //s2.snapshot();
        //s2.multiply(r2);
        //d.copyBits(s2, 0, 0, Blitter.ADD);
        //s2.reset();
        // develop stack position 2 effects

        d = dst.getStack().getProcessor(2);
        s1 = s2;
        s2 = src.getStack().getProcessor(4);
        processOneLevel(d, s1, r1, oRoi);
        processOneLevel(d, s2, r2, oRoi);
        // stack pos 3
        d = dst.getStack().getProcessor(3);
        s1 = s2;
        s2 = src.getStack().getProcessor(5);
        processOneLevel(d, s1, r1, oRoi);
        processOneLevel(d, s2, r2, oRoi);
        // stack pos 4 (somewhat special)
        d = dst.getStack().getProcessor(4);
        s1 = s2;
        s2 = src.getStack().getProcessor(3);
        processOneLevel(d, s1, r1, oRoi);
        processOneLevel(d, s2, r1, oRoi);
        // stack pos 5
        d = dst.getStack().getProcessor(5);
        s1 = src.getStack().getProcessor(4);
        s2 = s2;
        processOneLevel(d, s1, r1, oRoi);
        processOneLevel(d, s2, r2, oRoi);
        // stack pos 6
        d = dst.getStack().getProcessor(6);
        s2 = s1;
        s1 = src.getStack().getProcessor(5);
        processOneLevel(d, s1, r1, oRoi);
        processOneLevel(d, s2, r2, oRoi);
        // stack pos 7
        d = dst.getStack().getProcessor(7);
        s2 = s1;
        processOneLevel(d, s2, r2, oRoi);

    }

    private void processOneLevel(ImageProcessor d, ImageProcessor s, double r, OvalRoi roi) {
        s.snapshot();
        s.setRoi(roi);
        ImageProcessor s2 = s.crop();
        s2.multiply(r);
        s2.copyBits(roi.getMask(), 0, 0, Blitter.AND);
        d.copyBits(s2, 30, 30, Blitter.ADD);
        s.setRoi((Roi)null);
    }

    private void computeCrosstalk(ImagePlus dst, ImagePlus src) {
        double r1 = 0.5;
        double r2 = 0.25;
        ImageProcessor d, s1, s2;
        d = dst.getStack().getProcessor(1);
        s2 = src.getStack().getProcessor(3);
        s2.snapshot();
        s2.multiply(r2);
        d.copyBits(s2, 0, 0, Blitter.ADD);
        s2.reset();
        // develop stack position 2 effects
        d = dst.getStack().getProcessor(2);
        s1 = s2;
        s2 = src.getStack().getProcessor(4);
        processOneLevel(d, s1, r1);
        processOneLevel(d, s2, r2);
        // stack pos 3
        d = dst.getStack().getProcessor(3);
        s1 = s2;
        s2 = src.getStack().getProcessor(5);
        processOneLevel(d, s1, r1);
        processOneLevel(d, s2, r2);
        // stack pos 4 (somewhat special)
        d = dst.getStack().getProcessor(4);
        s1 = s2;
        s2 = src.getStack().getProcessor(3);
        processOneLevel(d, s1, r1);
        processOneLevel(d, s2, r1);
        // stack pos 5
        d = dst.getStack().getProcessor(5);
        s1 = src.getStack().getProcessor(4);
        s2 = s2;
        processOneLevel(d, s1, r1);
        processOneLevel(d, s2, r2);
        // stack pos 6
        d = dst.getStack().getProcessor(6);
        s2 = s1;
        s1 = src.getStack().getProcessor(5);
        processOneLevel(d, s1, r1);
        processOneLevel(d, s2, r2);
        // stack pos 7
        d = dst.getStack().getProcessor(7);
        s2 = s1;
        processOneLevel(d, s2, r2);
    }

    private void processOneLevel(ImageProcessor d, ImageProcessor s, double r) {
        s.snapshot();
        s.multiply(r);
        d.copyBits(s, 0, 0, Blitter.ADD);
        s.reset();
    }

    private ImagePlus createBlankStack(String title) {
        return createBlankStack(title, 100, 100, 7);
    }

    private ImagePlus createBlankStack(String title, int width, int height, int planes) {
        ImagePlus iplus = null;
        ImageStack stack = null;
        for (int i=1; i <= planes; i++) {
            ImageProcessor iproc = new ByteProcessor(width, height);
            //if (i == 4) addCircle(iproc);
            if (iplus == null) {
                iplus = new ImagePlus(String.valueOf(i), iproc);
                stack = iplus.getStack();
            } else {
                stack.addSlice(String.valueOf(i), iproc);
            }
        }
        //addCircle(stack);
        return new ImagePlus(title, stack);

    }

    @SuppressWarnings("unused")
	private void addCircle(ImageProcessor iproc) {
        //ImageProcessor iproc = stack.getProcessor(4);
        OvalRoi oRoi = makeOvalRoi(50, 50, 40);
        ImageProcessor mask = oRoi.getMask();
        System.out.println("addCircle: " + mask);
        ImagePlus iplus = new ImagePlus("test", mask);
        ij.gui.ImageWindow iwin = new ij.gui.ImageWindow(iplus);
        iproc.snapshot();
        iproc.setRoi(oRoi);
        iproc.add(128);
        iproc.reset(mask);
    }

    private void addCircles(ImageStack stack) {
        for (int i=1; i <= 7; i++) {
            ImageProcessor iproc = stack.getProcessor(i);
            int d = getLocalDiameter(40, 4, i);
            if (i > 2 && i < 6) d = 40;
            else d = 0;
            System.out.println("addCircle: " + d);
            if (d > 0) {
                //Roi oRoi = makeOvalRoi(50, 50, d);
                Roi oRoi = makePolygonRoi(50, 50, d);
                ImageProcessor mask = oRoi.getMask();
                //System.out.println("addCircle: " + mask);
                //ImagePlus iplus = new ImagePlus("test", mask);
                //ij.gui.ImageWindow iwin = new ij.gui.ImageWindow(iplus);
                iproc.snapshot();
                iproc.setRoi(oRoi);
                iproc.add(128);
                iproc.reset(mask);
            }
        }
    }

    private PolygonRoi makePolygonRoi(int x, int y, int d) {
        int [] xa = {x - d/2, x + d/2, x + d/2, x - d/2};
        int [] ya = {y - d/2, y - d/2, y + d/2, y + d/2};
        return new PolygonRoi(xa, ya, 4, Roi.POLYGON);
    }

    private OvalRoi makeOvalRoi(int x, int y, int d) {
        return new OvalRoi(x - d/2, y - d/2, d, d);
    }

    private int getLocalDiameter(double baseDia, double basePlane, int currentPlane) {
        double r = 0;
        double y = (basePlane - currentPlane)*10.0/(baseDia/2);
        double r2 = 1 - y*y;
        if (r2 >= 0) r = Math.sqrt(r2)*(baseDia/2);
        return (int)Math.round(2*r);

    }

    // processes one time point, all planes
    // blanks out the data within nuclei spheres leaving the rest
//    public void test1() {
//        append(TEST1);
//        System.gc();
//        // assume at first that we deal with plane 15 of time given
//        long start = System.currentTimeMillis();
//        int k = Integer.parseInt(iTextField.getText());
//
//        /*
//        String imageFileBase = ImageWindow.cZipTifFilePath;
//        imageFileBase += "/" + ImageWindow.cTifPrefix;
//        //imageFile += "/" + ImageWindow.cTifPrefixR;
//        String imageFile = imageFileBase + iAceTree.makeImageName(k, 1);
//        append(imageFile);
//        ImageProcessor ip = getRedData(imageFile);
//        int width = ip.getWidth();
//        int height = ip.getHeight();
//        ImagePlus iplus = null;
//        iStack = null;
//        for (int plane = 1; plane <= iNucleiMgr.getPlaneEnd(); plane++) {
//            imageFile = imageFileBase + iAceTree.makeImageName(k, plane);
//            ImageProcessor iproc = getRedData(imageFile);
//            if (iplus == null) {
//                iplus = new ImagePlus(String.valueOf(plane), iproc);
//                iStack = iplus.getStack();
//            } else {
//                iStack.addSlice(String.valueOf(plane), iproc);
//            }
//
//        }
//        */
//        makeStack(k);
//        Vector allCircles = makeAllCircles(k);
//        processAllCircles(allCircles);
//        iStackPlus = new ImagePlus("Stack", iStack);
//        iStackWindow = new StackWindow(iStackPlus);
//        iBlankStackWindow = new StackWindow(iBlankStackPlus);
//        subtractShadows();
//        append("new StackWindow: " + iStackWindow);
//        System.out.println("new StackWindow: " + iStackWindow);
//        //iStackPlus.show();
//        long elapsed = System.currentTimeMillis() - start;
//        append("elapsedTime: " + elapsed);
//
//        /*
//        System.out.println("ip=" + ip);
//        ImagePlus iplus = new ImagePlus("test", ip);
//        ij.gui.ImageWindow iwin = new ij.gui.ImageWindow(iplus);
//        processImage(iplus);
//        //iplus.getProcessor().resetRoi();
//        iplus.setRoi((Roi)null);
//        iplus.getProcessor().add(-100);
//        iwin.updateImage(iplus);
//        */
//    }

    private void subtractShadows() {
        ImageStack base = iStackPlus.getStack();
        ImageStack shadow = iBlankStackPlus.getStack();
        ImagePlus iplus = null;
        ImageStack stack = null;
        for (int i=0; i < base.getSize(); i++) {
            ImageProcessor baseP = base.getProcessor(i + 1).crop();
            ImageProcessor shadowP = shadow.getProcessor(i + 1);
            baseP.copyBits(shadowP, 0, 0, Blitter.SUBTRACT);
            if (iplus == null) {
                iplus = new ImagePlus(String.valueOf(i + 1), baseP);
                stack = iplus.getStack();
            } else {
                stack.addSlice(String.valueOf(i + 1), baseP);
            }
        }
        //iStackPlus = new ImagePlus("Stack", iStack);
        iExtraStackWindow = new StackWindow(new ImagePlus("adjusted stack", stack));

    }


    @SuppressWarnings("unused")
	private void processAllCircles(Vector allCircles) {
        Collections.sort(allCircles, new Centroid());
        Enumeration e = allCircles.elements();
        int plane = 0;
        ImageProcessor ip = null;
        ImageProcessor ibp = null;
        int count = 0;
        while (e.hasMoreElements()) {
            Centroid c = (Centroid)e.nextElement();
            System.out.println("centroid: " + c);
            if (c.plane > plane) {
                plane = c.plane;
                ip = iStack.getProcessor(plane);
                ibp = iBlankStackPlus.getStack().getProcessor(plane);
            }
            //ImagePlus iplus = new ImagePlus("test", ip);
            OvalRoi oRoi = makeOvalRoi(c, plane);
            ImageProcessor mask = oRoi.getMask();
            //if (count++ < 6) {
            //    ImagePlus iplus = new ImagePlus("test", mask);
            //    ij.gui.ImageWindow iwin = new ij.gui.ImageWindow(iplus);
            //}
            ip.snapshot();
            ip.setRoi(oRoi);
            ImageProcessor p = ip.crop();
            //ibp.copyBits(p, 0, 0, Blitter.ADD);
            copyCentroidShadowsToNewStack(ibp, p, oRoi, c);
            //ip.setMask(mask);
            //ip.and(0);
            ip.reset(mask);
        }
    }

    @SuppressWarnings("unused")
	private void copyCentroidToNewStack(ImageProcessor dst, ImageProcessor src, Roi roi, Centroid c) {
        float fxx = c.x;
        float fyy = c.y;
        fxx -= c.d/2;
        fyy -= c.d/2;
        int xx = Math.round(fxx);
        int yy = Math.round(fyy);
        int d = (int)Math.round(c.d);
        src.copyBits(roi.getMask(), 0, 0, Blitter.AND);
        dst.copyBits(src, xx, yy, Blitter.ADD);

    }

    @SuppressWarnings("unused")
	private void copyCentroidShadowsToNewStack(ImageProcessor dst, ImageProcessor src, Roi roi, Centroid c) {
        float fxx = c.x;
        float fyy = c.y;
        fxx -= c.d/2;
        fyy -= c.d/2;
        int xx = Math.round(fxx);
        int yy = Math.round(fyy);
        int d = (int)Math.round(c.d);
        ImageProcessor mask = roi.getMask();
        src.copyBits(mask, 0, 0, Blitter.AND);
        //dst.copyBits(src, xx, yy, Blitter.ADD);
        int [] ka = {-3, -2, -1, 1, 2, 3};
        double [] r = {0., 0.25, 0.35, 0.35, 0.25, 0.};
        double baseFrac = 1.;
        int numPlanes = iNucleiMgr.getPlaneEnd();
        src.snapshot();
        for (int i=0; i < ka.length; i++) {
            int k = c.plane + ka[i];
            if (k >= 1 && k <= numPlanes) {
                //System.out.println("copyCentroid..: " + k + C.CS + c.name);
                //int dia = getLocalDiameter(c.d, c.plane, k);
                //if (dia > 0) continue;
                //System.out.println("copying for: " + c.name + C.CS + k);
                ImageProcessor p = iBlankStackPlus.getStack().getProcessor(k);
                src.multiply(r[i]);
                p.copyBits(src, xx, yy, Blitter.ADD);
                src.reset();
            }

        }
        // subtract off a fraction of the base data
        ImageProcessor p = iBlankStackPlus.getStack().getProcessor(c.plane);
        src.multiply(baseFrac);
        p.copyBits(src, xx, yy, Blitter.SUBTRACT);
        src.reset();



    }

    private OvalRoi makeOvalRoi(Centroid c, int plane) {
        float fxx = c.x;
        float fyy = c.y;
        fxx -= c.d/2;
        fyy -= c.d/2;
        int xx = Math.round(fxx);
        int yy = Math.round(fyy);
        int d = (int)Math.round(c.d);

        OvalRoi oRoi = new OvalRoi(xx, yy, d, d);
        return oRoi;

    }


    @SuppressWarnings("unused")
	private Vector makeAllCircles(int time) {
        Vector allCircles = new Vector();
        int k = Integer.parseInt(iTextField.getText());
            Vector nuclei = (Vector)nuclei_record.elementAt(k - 1);
            //circles = new Vector();
            Nucleus n = null;
            Enumeration e = nuclei.elements();
            for (int j=0; j < nuclei.size(); j++) {
                n = (Nucleus)nuclei.elementAt(j);
                if (n.status == Nucleus.NILLI) continue;
                // create vector to hold all centroids for this cell
                // at this time point
                Vector v = new Vector(); // vector to hold all centroids for this cell
                for (int plane = 1; plane <= iNucleiMgr.getPlaneEnd(); plane++) {
                    double d = iNucleiMgr.nucDiameter(n, plane);
                    if (d > 0) {
                        //append(String.valueOf(plane) + C.CS + String.valueOf(d));
                        Centroid c = new Centroid();
                        c.index = j + 1;
                        c.time = k - 1;
                        c.plane = plane;
                        c.x = n.x;
                        c.y = n.y;
                        c.d = d;
                        c.name = n.identity;
                        c.weight = n.weight;
                        c.n = n;
                        v.add(c);
                        allCircles.add(c);
                    }
                }
            }
            return allCircles;

    }


//    private void makeStack(int time) {
//        String imageFileBase = ImageWindow.cZipTifFilePath;
//        //imageFileBase += "/" + ImageWindow.cTifPrefix;
//        imageFileBase += "/" + ImageWindow.cTifPrefixR;
//        String imageFile = imageFileBase + iAceTree.makeImageName(time, 1);
//        append(imageFile);
//        ImageProcessor ip = getRedData(imageFile);
//        int width = ip.getWidth();
//        int height = ip.getHeight();
//        int numPlanes = iNucleiMgr.getPlaneEnd();
//        iBlankStackPlus = createBlankStack("new stack", width, height, numPlanes);
//        ImagePlus iplus = null;
//        iStack = null;
//        for (int plane = 1; plane <= iNucleiMgr.getPlaneEnd(); plane++) {
//            imageFile = imageFileBase + iAceTree.makeImageName(time, plane);
//            ImageProcessor iproc = getRedData(imageFile);
//            if (iplus == null) {
//                iplus = new ImagePlus(String.valueOf(plane), iproc);
//                iStack = iplus.getStack();
//            } else {
//                iStack.addSlice(String.valueOf(plane), iproc);
//            }
//        }
//    }


    private void processStack() {
        int k = iStack.getSize();
        for (int i=1; i <= k; i++) {
            ImageProcessor ip = iStack.getProcessor(i);
            ip.setRoi(200, 300, 50, 50);
            ip.and(0);
        }
    }




    private void processImage(ImagePlus iplus) {
        iplus.setRoi(makeRoi());
        iplus.getProcessor().add(100);


    }

    private Roi makeRoi() {
        int [] x = {200, 300, 300, 200};
        int [] y = {300, 300, 400, 400};
        return new PolygonRoi(x, y, 4, Roi.POLYGON);

    }

    private Roi makeRoi2() {
        int [] x = {100, 200, 200, 100};
        int [] y = {200, 200, 300, 300};
        return new PolygonRoi(x, y, 4, Roi.POLYGON);

    }

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

    private class Centroid implements Comparator {
        public int time;
        public int plane;
        public int index;
        public int x;
        public int y;
        public double d;
        public String name;
        public int weight;
        public Nucleus n;
        public int rsum;
        public int rcount;


        @Override
		public String toString() {
            String s = String.valueOf(index);
            s += C.CS + time;
            s += C.CS + plane;
            s += C.CS + x;
            s += C.CS + y;
            s += C.CS + d;
            s += C.CS + name;
            s += C.CS + weight;
            s += C.CS + rsum;
            s += C.CS + rcount;

            return s;
        }

        @Override
		public int compare(Object o1, Object o2) {
            if (((Centroid)o1).plane < ((Centroid)o2).plane) return -1;
            else if (((Centroid)o1).plane > ((Centroid)o2).plane) return 1;
            return 0;
        }
    }



    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals(TEST1)) {
            //test1();
        } else if (s.equals(TEST2)) {
            append(TEST2);
            test2();
        } else if (s.equals(TEST3)) {
            append(TEST3);
            test3();
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
        iTextField.setText("1");
        iToolBar.add(iTextField);
        JButton jb = null;
        jb = new JButton(CLEAR);
        addToolBarButton(jb);
        jb = new JButton(TEST1);
        addToolBarButton(jb);
        jb = new JButton(TEST2);
        addToolBarButton(jb);
        jb = new JButton(TEST3);
        addToolBarButton(jb);
    }

    private static final String
    CLEAR = "Clear"
   ,TEST1 = "Test1"
   ,TEST2 = "Test2"
   ,TEST3 = "Test3"
   ;

    public static void main(String[] args) {
    }
}
