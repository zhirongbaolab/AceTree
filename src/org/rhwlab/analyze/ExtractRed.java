/*
 * Created on Sep 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;


import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageStatistics;
import ij.io.Opener;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.image.ZipImage;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.C;
import org.rhwlab.utils.EUtils;
import org.rhwlab.utils.Log;


/**
 * Tool for extracting red data.
 *
 * @author biowolp
 * @version 1.1 November 30, 2005
 *
 */
public class ExtractRed extends Log {

    private JTextField iTextField;
    AceTree         iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    //Identity        iIdentity;
    Vector          iAllCircles;
JCheckBox       redChannelButton;
    public ExtractRed(String title) {
        super(title);
        System.out.println("EXTRACT RED DISABLED");
        //showMe();
        //buildOutToolBar();
        //initialize();

    }

    // get color data for one time point
//    @SuppressWarnings("unused")
//	public void test1() {
//        long start = System.currentTimeMillis();
//        Vector circles = new Vector();
//        Vector allCircles = new Vector();
//        int k = Integer.parseInt(iTextField.getText());
//        for (int i=k; i < k+1; i++) {
//            Vector nuclei = (Vector)nuclei_record.elementAt(i - 1);
//            circles = new Vector();
//            Nucleus n = null;
//            Enumeration e = nuclei.elements();
//            for (int j=0; j < nuclei.size(); j++) {
//                n = (Nucleus)nuclei.elementAt(j);
//                if (n.status == Nucleus.NILLI) continue;
//                // create vector to hold all centroids for this cell
//                // at this time point
//                Vector v = new Vector(); // vector to hold all centroids for this cell
//                for (int plane = 1; plane <= iNucleiMgr.getPlaneEnd(); plane++) {
//                    double d = iNucleiMgr.nucDiameter(n, plane);
//                    if (d > 0) {
//                        //append(String.valueOf(plane) + C.CS + String.valueOf(d));
//                        Centroid c = new Centroid();
//                        c.index = j + 1;
//                        c.time = i;
//                        c.plane = plane;
//                        c.x = n.x;
//                        c.y = n.y;
//                        c.d = d;
//                        c.name = n.identity;
//                        c.weight = n.weight;
//                        c.n = n;
//                        v.add(c);
//                        allCircles.add(c);
//                    }
//                }
//                circles.add(v);
//            }
//            // vector circles visualizes holding a vector of centroids
//            // for each cell at this time point
//            // at this point, though, it has one the one cell at this time point
//
//            processAllCircles(allCircles, circles, true);
//
//        }
//        long elapsed = System.currentTimeMillis() - start;
//        append("elapsedTime: " + elapsed + C.CS  + circles.size());
//
//    }
//
//    private void processAllCircles(Vector allCircles, Vector circles, boolean report) {
//        Collections.sort(allCircles, new Centroid());
//        Enumeration e = allCircles.elements();
//        int plane = 0;
//        ImageProcessor ipRed = null;
//        while (e.hasMoreElements()) {
//            Centroid c = (Centroid)e.nextElement();
//            if (c.plane > plane) {
//                plane = c.plane;
//                String imageFile = ImageWindow.cZipTifFilePath;
//		if(ImageWindow.imagewindowUseStack==1){
//		    imageFile += "/" + ImageWindow.cTifPrefix;
//		    //imageFile += iAceTree.makeImageName(c.time, c.plane);
//		}else{
//                imageFile += "/" + ImageWindow.cTifPrefixR;
//                //imageFile += iAceTree.makeImageName(c.time, c.plane);
//		}
//                ipRed = getRedData(imageFile,c.plane);
//                //System.out.println("YimageFile: " + (new File(imageFile).getName()));
//
//            }
//            c.rsum = 0;
//            c.rcount = 1;
//            if (ipRed != null) {
//                //int [] ia = processImageUsingRoi(ipRed, c);
//                int [] ia = processImageUsingPolygon(ipRed, c);
//                c.rsum = ia[0];
//                c.rcount = ia[1];
//            }
//            //System.out.println("Y: " + c.name + C.CS + c.rsum + C.CS + c.rcount);
//        }
//        // each element of circles has data for one cell
//        // the data consists of several planes where the cell is observable
//        // in each plane we need to count the red stuff
//        // and accumulate it against the cell involved
//        Enumeration ee = circles.elements();
//        while (ee.hasMoreElements()) {
//            Vector vv = (Vector)ee.nextElement();
//            // vv is deals with one of the cells present at this time point
//            // it may have several Centroids in it
//            int redSum = 0;
//            int redCount = 0;
//            Centroid cc = null;
//            Enumeration ec = vv.elements();
//            while(ec.hasMoreElements()) {
//                cc = (Centroid)ec.nextElement();
//                redSum += cc.rsum;
//                redCount += cc.rcount;
//                //System.out.println("Y: " + cc.name + C.CS + redSum + C.CS + redCount);
//            }
//            int redWeight = redSum * STANDARDCOUNT / redCount;
//            cc.n.rweight = redWeight;
//            cc.n.rsum = redSum;
//            cc.n.rcount = redCount;
//            cc.n.rwraw = redWeight;
//            if (report) append(cc.index + C.CS + redCount + C.CS + redWeight + C.CS + cc.n.rweight + C.CS + redSum + C.CS + cc.weight + C.CS + cc.name);
//        }
//
//    }
//
//    // get color data for all time points
//    @SuppressWarnings("unused")
//	public void test2() {
//        long start = System.currentTimeMillis();
//        //int k = Integer.parseInt(iTextField.getText());
//        int m = iNucleiMgr.getConfig().iStartingIndex;
//        int q = iNucleiMgr.getConfig().iEndingIndex;
//        for (int i=m; i <= q; i++) {
//            Vector circles = new Vector();
//            Vector allCircles = new Vector();
//            Vector nuclei = (Vector)nuclei_record.elementAt(i - 1);
//            circles = new Vector();
//            Nucleus n = null;
//            Enumeration e = nuclei.elements();
//            for (int j=0; j < nuclei.size(); j++) {
//                n = (Nucleus)nuclei.elementAt(j);
//                //if (!n.identity.equals("ABplaaaaa")) continue;
//                if (n.status == Nucleus.NILLI) continue;
//                Vector v = new Vector(); // vector to hold all centroids for this cell
//                for (int plane = 1; plane <= iNucleiMgr.getPlaneEnd(); plane++) {
//                    double d = iNucleiMgr.nucDiameter(n, plane);
//                    if (d > 0) {
//                        //append(String.valueOf(plane) + C.CS + String.valueOf(d));
//                        Centroid c = new Centroid();
//                        c.index = j + 1;
//                        c.time = i;
//                        c.plane = plane;
//                        c.x = n.x;
//                        c.y = n.y;
//                        c.d = d;
//                        c.name = n.identity;
//                        c.weight = n.weight;
//                        c.n = n;
//                        v.add(c);
//                        allCircles.add(c);
//                    }
//                }
//                circles.add(v);
//            }
//            processAllCircles(allCircles, circles, false);
//            if (i % 10 == 0) {
//                long elapsed = System.currentTimeMillis() - start;
//                System.out.println("elapsedTime: " + elapsed + C.CS  + circles.size() + C.CS + i);
//                System.out.flush();
//            }
//        }
//        long elapsed = System.currentTimeMillis() - start;
//        append("elapsedTime: " + elapsed);
//    }
//
//    private static final int STANDARDCOUNT = 1000;
//
//    private int [] processCentroid(Centroid cc) {
//        String imageFile = ImageWindow.cZipTifFilePath;
//        imageFile += "/" + ImageWindow.cTifPrefixR;
//        //imageFile += iAceTree.makeImageName(cc.time, cc.plane);
//        ImageProcessor ipRed = getRedData(imageFile,cc.plane);
//        int [] ia = processImage(ipRed, cc);
//        return ia;
//    }
//
//    @SuppressWarnings("unused")
//	public  int [] processImage(ImageProcessor ipRed, Centroid c) {
//        byte [] redPixels = (byte [])ipRed.getPixels();
//        //int d = (int)(c.d + 0.5);
//        float fxx = c.x;
//        float fyy = c.y;
//        fxx -= c.d/2;
//        fyy -= c.d/2;
//        int xx = (int)(fxx + 0.5);
//        int yy = (int)(fyy + 0.5);
//        int d = (int)(c.d + 0.5);
//
//        OvalRoi oRoi = new OvalRoi(xx, yy, d, d);
//        Rectangle r = oRoi.getBounds();
//        int width = ipRed.getWidth();
//        int redSum = 0;
//        int redCount = 0;
//        int offset, i;
//        for (int y=r.y; y < (r.y + r.height); y++) {
//            offset = y * width;
//            for (int x = r.x; x <= (r.x + r.width); x++) {
//                i = offset + x;
//                if (oRoi.contains(x, y)) {
//                    int k = ipRed.getPixel(x, y);
//                    //int k = 0xff & redPixels[i];
//                    redSum += k;
//                    redCount++;
//                }
//            }
//        }
//        int [] ia = new int[2];
//        ia[0] = redSum;
//        ia[1] = redCount;
//        return ia;
//    }
//
//    int convert(int u) {
//        int v = u >> 8;
//        v = v & 255;
//        return v;
//    }
//
//    public  int [] processImageUsingRoi(ImageProcessor ipRed, Centroid c) {
//        //byte [] redPixels = (byte [])ipRed.getPixels();
//        //int d = (int)(c.d + 0.5);
//        float fxx = c.x;
//        float fyy = c.y;
//        fxx -= c.d/2;
//        fyy -= c.d/2;
//        int xx = Math.round(fxx);
//        int yy = Math.round(fyy);
//        int d = (int)Math.round(c.d);
//
//        OvalRoi oRoi = new OvalRoi(xx, yy, d, d);
//        ImagePlus iplus2 = new ImagePlus("test", ipRed);
//        int area = 0;
//        iplus2.setRoi(oRoi);
//        area = (int)(Math.PI*c.d*c.d/4);
//        ImageStatistics istat2 = iplus2.getStatistics();
//        int [] ia = new int[2];
//
//        int redSum = (int)(istat2.mean * area);
//        int redCount = area;
//        ia[0] = redSum;
//        ia[1] = redCount;
//        return ia;
//    }
//
//    public  int [] processImageUsingPolygon(ImageProcessor ipRed, Centroid c) {
//        int rad = (int)Math.round(c.d / 2);
//        Polygon inner = EUtils.pCircle(c.x, c.y, rad);
//        Rectangle r = inner.getBounds();
//        double nucleusRed = 0;
//        double nucleusRedCount = 0;
//        for (int y=r.y; y < (r.y + r.height); y++) {
//            for (int x = r.x; x <= (r.x + r.width); x++) {
//                if (inner.contains(x, y)) {
//                    int p = ipRed.getPixel(x, y);
//                    nucleusRed += p;
//                    nucleusRedCount ++;
//                }
//            }
//        }
//        int [] ia = new int[2];
//        ia[0] = (int)Math.round(nucleusRed);
//        ia[1] = (int)Math.round(nucleusRedCount);
//        return ia;
//    }
//
//
//    @SuppressWarnings("unused")
//	private ImageProcessor getRedData(String greenName,int plane) {
//
//
//
////System.out.println("getRedData: " + greenName + C.CS + ImageWindow.cUseZip);
//        //if (ImageWindow.cUseZip == 2) return getRedZipData(greenName);
//        FileInputStream fis;
//        ImagePlus ip = null;
//        //String ss = "/home/biowolp/AncesTree/temp2/images/050405-t050-p15.tif";
//	// try {
//	    //this was self evidently nonsense in current image context urgh
//            //fis = new FileInputStream(greenName);
//            //byte [] ba = ImageWindow.readByteArray(fis);
//            //ip = ImageWindow.openTiff(new ByteArrayInputStream(ba), false);
//            //fis.close();
//
//	    if (ImageWindow.imagewindowUseStack==1){
//	    ip = new Opener().openImage(greenName,plane);
//	    }else{
//	    ip = new Opener().openImage(greenName);
//	    }
//
//	    if(ImageWindow.imagewindowUseStack==1) {
//
//	int markerChannel;
//	if (redChannelButton.getModel().isSelected()){
//	    //System.out.println("use red channel");
//	    markerChannel=2;}
//	else{
//	    //	    System.out.println("use green channel ");
//		markerChannel=1;
//	}
//
//		ip=ImageWindow.splitImage(ip,markerChannel);
//	    }
//	    System.out.println(ip);
//	    System.out.println(ip.getProcessor() instanceof FloatProcessor);
//	    System.out.println(ip.getProcessor() instanceof ShortProcessor);
//            //ip = readData(fis);
//	    // } catch(IOException ioe) {
//	    // System.out.println("ImageWindow.test3 exception ");
//            //System.out.println(ioe);
//	    //}
//        if (ip != null) return ip.getProcessor();
//        else return null;
//    }
//
//
//    private ImageProcessor getRedZipData(String redName) {
//        ZipImage zipImage = new ZipImage(redName);
//        int k1 = redName.lastIndexOf("/") + 1;
//        String ss = redName.substring(k1);
//        int k2 = ss.indexOf(".");
//        ss = ss.substring(0, k2);
//        //System.out.println("using: " + ss);
//        ZipEntry ze = null;
//        if (zipImage != null) ze = zipImage.getZipEntry(ss + ".tif");
//        //System.out.println("ZipEntry: " + ze);
//        //if (cZipImage == null) cZipImage = new ZipImage(cZipTifFilePath);
//        //ZipEntry ze = cZipImage.getZipEntry(s);
//        ImagePlus ip = null;
//        ip = zipImage.readData(ze, true);
//        if (ip != null) return ip.getProcessor();
//        else return null;
//    }
//
//
//    private class Centroid implements Comparator {
//        public int time;
//        public int plane;
//        public int index;
//        public int x;
//        public int y;
//        public double d;
//        public String name;
//        public int weight;
//        public Nucleus n;
//        public int rsum;
//        public int rcount;
//
//
//        @Override
//		public String toString() {
//            String s = String.valueOf(index);
//            s += C.CS + time;
//            s += C.CS + plane;
//            s += C.CS + x;
//            s += C.CS + y;
//            s += C.CS + d;
//            s += C.CS + name;
//            s += C.CS + weight;
//            s += C.CS + rsum;
//            s += C.CS + rcount;
//
//            return s;
//        }
//
//        @Override
//		public int compare(Object o1, Object o2) {
//            if (((Centroid)o1).plane < ((Centroid)o2).plane) return -1;
//            else if (((Centroid)o1).plane > ((Centroid)o2).plane) return 1;
//            return 0;
//        }
//    }
//
//
//
//
//    private void processVectorOfROIs(Vector v, int time, int plane) {
//        append("processVectorOfROIs " + plane);
//    }
//    public void initialize() {
//        iAceTree = AceTree.getAceTree(null);
//        iNucleiMgr = iAceTree.getNucleiMgr();
//        nuclei_record = iNucleiMgr.getNucleiRecord();
//        iAncesTree = iNucleiMgr.getAncesTree();
//        iCellsByName = iAncesTree.getCellsByName();
//        //iIdentity = iNucleiMgr.getIdentity();
//        //iRoot = iNucleiMgr.getRoot();
//	iRoot = iAncesTree.getRoot();
//    }
//
//
//
//    @Override
//	public void actionPerformed(ActionEvent e) {
//        String s = e.getActionCommand();
//        if (s.equals(TEST1)) {
//            test1();
//        } else if (s.equals(TEST2)) {
//            test2();
//        } else if (s.equals(CLEAR)) {
//            append("clear");
//            iText.setText("");
//        } else super.actionPerformed(e);
//    }
//
//
//
//    private void buildOutToolBar() {
//	// iToolBar.setMaximumSize(new Dimension(500,20));
//        iToolBar.add(new JLabel("time:"));
//        iTextField = new JTextField();
//        iTextField.setColumns(15);
//        iTextField.setText("175");
//        iToolBar.add(iTextField);
//        JButton jb = null;
//        jb = new JButton(CLEAR);
//        addToolBarButton(jb);
//        jb = new JButton(TEST1);
//        addToolBarButton(jb);
//        jb = new JButton(TEST2);
//        addToolBarButton(jb);
//    if(ImageWindow.imagewindowUseStack==1) {
//	redChannelButton=new JCheckBox(REDCHANNEL);
//	addToolBarButton(redChannelButton);
//    }
//    }
//
//
//    private static final String
//    CLEAR = "Clear"
//   ,TEST1 = "One"
//   ,TEST2 = "All"
//   ,TEST3 = "Test3"
// ,REDCHANNEL = "Red?"
//   ;
//
//
//    public static void main(String[] args) {
//    }
//
//    /*
//    private void processAllCirclesJAI(Vector allCircles, Vector circles, boolean report) {
//        Collections.sort(allCircles, new Centroid());
//        Enumeration e = allCircles.elements();
//        int plane = 0;
//        PlanarImage ipRed = null;
//        while (e.hasMoreElements()) {
//            Centroid c = (Centroid)e.nextElement();
//            if (c.plane > plane) {
//                plane = c.plane;
//                String imageFile = ImageWindow.cZipTifFilePath;
//                imageFile += "/" + ImageWindow.cTifPrefixR;
//                imageFile += iAceTree.makeImageName(c.time, c.plane);
//                ipRed = getRedZipDataJAI(imageFile);
//                System.out.println("XimageFile: " + (new File(imageFile).getName()));
//
//            }
//            int [] ia = processImageJAI(ipRed, c);
//            c.rsum = ia[0];
//            c.rcount = ia[1];
//            System.out.println("X: " + c.name + C.CS + c.rsum + C.CS + c.rcount);
//        }
//        // each element of circles has data for one cell
//        // the data consists of several planes where the cell is observable
//        // in each plane we need to count the red stuff
//        // and accumulate it against the cell involved
//        Enumeration ee = circles.elements();
//        while (ee.hasMoreElements()) {
//            Vector vv = (Vector)ee.nextElement();
//            // vv is deals with one of the cells present at this time point
//            // it may have several Centroids in it
//            long redSum = 0;
//            long redCount = 0;
//            Centroid cc = null;
//            Enumeration ec = vv.elements();
//            while(ec.hasMoreElements()) {
//                cc = (Centroid)ec.nextElement();
//                redSum += cc.rsum;
//                redCount += cc.rcount;
//                System.out.println("X: " + cc.name + C.CS + redSum + C.CS + redCount);
//            }
//            int redWeight = (int)(redSum * STANDARDCOUNT / redCount);
//            cc.n.rweight = redWeight;
//            if (report) append(cc.index + C.CS + redCount + C.CS + redWeight + C.CS + cc.n.rweight + C.CS + redSum + C.CS + cc.weight + C.CS + cc.name);
//        }
//
//    }
//
//    private PlanarImage getRedZipDataJAI(String redName) {
//        return getPlanarImage(redName);
//    }
//
//    PlanarImage getPlanarImage(String zipfilename) {
//        PlanarImage source = null;
//        File f = new File(zipfilename);
//
//        if ( f.exists() && f.canRead() ) {
//            String s = f.getName();
//            int k = s.indexOf(".");
//            s = s.substring(0, k + 1);
//            s += "tif";
//            try {
//                ZipFile zf = new ZipFile(f);
//                ZipEntry ze = zf.getEntry(s);
//                InputStream is = zf.getInputStream(ze);
//                SeekableStream stream = SeekableStream.wrapInputStream(is, true);
//                source = JAI.create("stream", stream);
//            } catch(Exception ioe) {
//                ioe.printStackTrace();
//            }
//
//        }
//        return source;
//    }
//
//    public  int [] processImageJAI(PlanarImage ipRed, Centroid c) {
//        //int d = (int)(c.d + 0.5);
//        int x = c.x;
//        int y = c.y;
//        double dia = c.d;
//        double area = Math.PI * dia * dia;
//        double side = Math.sqrt(area);
//        int intSide = (int)Math.round(side);
//        x -= side/2;
//        y -= side/2;
//        double mean = getMean(ipRed, x, y, intSide, intSide);
//
//        int [] ia = new int[2];
//        ia[0] = (int)Math.round(mean * area);
//        ia[1] = (int)Math.round(area);;
//        return ia;
//    }
//
//    private double getMean(PlanarImage img, int x, int y, int width, int height) {
//        ParameterBlock pb = new ParameterBlock();
//        pb.addSource(img); // The source image
//        pb.add(new ROIShape(new Rectangle(x, y, width, height)));
//        pb.add(1); // check every pixel horizontally
//        pb.add(1); // check every pixel vertically
//        RenderedImage meanImage = JAI.create("mean", pb, null);
//        double[] mean = (double[])meanImage.getProperty("mean");
//        return mean[0];
//
//    }
//    */
//
//
}
