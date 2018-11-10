/*
 * Created on Oct 12, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.C;
import org.rhwlab.utils.EUtils;
import org.rhwlab.utils.Log;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RedBkgComp2 extends Log {

    JTextField      iTextField;
    AceTree         iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    //Identity        iIdentity;
    //Vector          iAllCircles;
    double          iKMedium;
    double          iKLarge;

    int             iStartTime;
    int             iEndTime;
    RedBkgComp      iRedBkgComp;
    boolean         iStopRequested;
    boolean         iFeedbackRequested;
    Vector          iFeedbackVector;


    public RedBkgComp2() {
        super("Red Background Compensation");
        showMe();
        initialize();
        buildOutToolBar();
        println("RedBkgComp2, ");
        iKMedium = 1.5;
        iKLarge = 2.0;
    }

    public RedBkgComp2(boolean noUI) {
        super("Red Background Compensation");
        initialize();
        iKMedium = 1.5;
        iKLarge = 2.0;
        println("RedBkgComp2, no ui");
    }

    public void setParameters(int start, int end, double kMedium, double kLarge
            , RedBkgComp redBkgComp, boolean feedbackRequested){
        iStartTime = start;
        iEndTime = end;
        iKMedium = kMedium;
        iKLarge = kLarge;
        iRedBkgComp = redBkgComp;
        iStopRequested = false;
        iFeedbackRequested = feedbackRequested;
        if (iFeedbackRequested) iFeedbackVector = new Vector();
    }

    public void stopRequested() {
        iStopRequested = true;
    }


    Vector iKeys;
//    public void test1() {
//        iRedBkgComp.append("beginning..");
//        long startTime = System.currentTimeMillis();
//        for (int time = iStartTime; time <= iEndTime; time++) {
//            Vector hashPlanes = new Vector();
//            for (int plane = 1; plane <= iNucleiMgr.getPlaneEnd(); plane++) {
//                Hashtable h = createHashForPlane(time, plane);
//                // hashtable h has one entry for each cell which has a large diameter
//                // intersection with this plane
//                // the entry is a Centroid object
//                // the plane member has the value of this plane
//                hashPlanes.add(h);
//            }
//            processHashPlanes(time, hashPlanes);
//            if (iFeedbackRequested) {
//                Collections.sort(iFeedbackVector);
//                for (int i=0; i < iFeedbackVector.size(); i++) {
//                    iRedBkgComp.append((String)iFeedbackVector.get(i));
//                }
//            }
//            if (iStopRequested) {
//                iStopRequested = false;
//                iRedBkgComp.append("at time=" + time + ", stopped at user request");
//                break;
//            }
//        }
//        long endTime = System.currentTimeMillis();
//
//        String s = "run elapsed millisec, " + (endTime - startTime);
//        iRedBkgComp.append(s);
//
//    }

    @SuppressWarnings("unused")
	public void processHashPlanes(int time, Vector hashPlanes) {
        Vector nuclei = (Vector)nuclei_record.elementAt(time - 1);
        Vector circles = new Vector();
        iKeys = new Vector();
        Nucleus n = null;
        Hashtable totals = new Hashtable();
        iKeys = new Vector();
        Enumeration e = nuclei.elements();
        for (int j=0; j < nuclei.size(); j++) {
            n = (Nucleus)nuclei.elementAt(j);
            if (n.status == Nucleus.NILLI) continue;
            Totals t = new Totals();
            t.name = n.identity;
            iKeys.add(n.identity);
            for (int i=0; i < hashPlanes.size(); i++) {
                Hashtable h = (Hashtable)hashPlanes.get(i);
                Centroid c = (Centroid)h.get(n.identity);
                if (c == null) continue;
                t.nuclearRed += c.nuclearRed;
                t.nuclearArea += c.nuclearArea;
                t.annulusRed += c.annulusRed;
                t.annulusArea += c.annulusArea;
            }
            double bri = 1000 * t.annulusRed / t.annulusArea;
            double fri = 1000 * t.nuclearRed / t.nuclearArea;
            t.fri = fri;
            t.bri = bri;
            totals.put(n.identity, t);
            n.rweight = (int)Math.round(fri - bri);
            n.rsum = (int)Math.round(t.nuclearArea);
            n.rcount = (int)Math.round(bri);
            //println("processHashPlanes, " + fri + CS + bri + CS + time + CS + n.identity);
            if (iFeedbackRequested) {
                String s = n.identity + CS + n.rweight + CS + n.rcount + CS + n.rsum + CS + time;
                iFeedbackVector.add(s);
            }
        }
        /*
        Collections.sort(iKeys);
        for (int i=0; i < iKeys.size(); i++) {
            Totals t = (Totals)totals.get(iKeys.get(i));

            println("processHashPlanes, " + t);
        }
        */
    }

//    @SuppressWarnings("unused")
//	public Hashtable createHashForPlane(int time, int plane) {
//        Vector nuclei = (Vector)nuclei_record.elementAt(time - 1);
//        Vector circles = new Vector();
//        iKeys = new Vector();
//        Nucleus n = null;
//        Enumeration e = nuclei.elements();
//        Hashtable h = new Hashtable(); // vector to hold all centroids for this cell
//        for (int j=0; j < nuclei.size(); j++) {
//            n = (Nucleus)nuclei.elementAt(j);
//            if (n.status == Nucleus.NILLI) continue;
//
//            // hear take steps to guarantee that all cells having a
//            // large circle have a representative in the centroid hashtable
//            double dl = nucDiameter(n, plane, n.size * iKLarge);
//            if (dl > 0) {
//                //append(String.valueOf(plane) + C.CS + String.valueOf(d));
//                Centroid c = new Centroid();
//                c.index = j + 1;
//                c.time = time;
//                c.plane = plane;
//                c.x = n.x;
//                c.y = n.y;
//                c.d = nucDiameter(n, plane, n.size);
//                c.dl = dl;
//                c.dm = nucDiameter(n, plane, n.size * iKMedium);
//                c.name = n.identity;
//                c.weight = n.weight;
//                c.n = n;
//                h.put(n.identity, c);
//                iKeys.add(n.identity);
//                //println("test1, " + c);
//            }
//        }
//        process(h, time, plane);
//        Collections.sort(iKeys);
//        return h;
//
//    }

    /*
     * this function processes the image associated with the plane
     * represented by the Centroids in hashtable h
     * three representations of that image are used
     * ipData deals with the nuclear red information
     * ipCopy gets middle circles zeroed out so it sort of has
     * only background information
     * ipTemplate is a dummy page whose size corresponds to
     * that of ipData. It is used to estimate the usable
     * area of the annuli in which background red will
     * be estimated
     *
     * the first loop is used to zero out the middle sized
     * circles in ipCopy and ipTemplate
     *
     * the second loop adds to the Centroid object
     * the information about total red signal
     * and "area" of the two regions:
     * nucleus and annulus
     */
    //@SuppressWarnings("unused")
//	public void process(Hashtable h, int time, int plane) {
//        //int time = 200;
//        //int plane = 10;
//        String imageFile = ImageWindow.cZipTifFilePath;
//        imageFile += "/" + ImageWindow.cTifPrefixR;
//        //imageFile += iAceTree.makeImageName(time, plane);
//        ImageProcessor ipData = getRedData(imageFile);
//
//        if (ipData == null) return;
//        // implies we are beyond the last plane
//        // even tho the NucleiMgr does not know that
//
//        ImagePlus iplus = new ImagePlus(iTitle, ipData);
//        //iImgCanvas = new ImageCanvas(iplus);
//        //ij.gui.ImageWindow iwin = new ij.gui.ImageWindow(iplus);
//        ImageProcessor ipCopy = getRedData(imageFile);
//        ImagePlus iplus2 = new ImagePlus(iTitle, ipCopy);
//        //iImgCanvas = new ImageCanvas(iplus);
//        //ij.gui.ImageWindow iwin2 = new ij.gui.ImageWindow(iplus2);
//
//        ByteProcessor ipTemplate = new ByteProcessor(ipData.getWidth(), ipData.getHeight());
//        ipTemplate.setValue(255);
//        ipTemplate.fill();
//        ImagePlus iplus3 = new ImagePlus(iTitle, ipTemplate);
//        //ij.gui.ImageWindow iwin3 = new ij.gui.ImageWindow(iplus3);
//
//        // now zero out inlarged nucleus in copy and template
//        Enumeration e = h.keys();
//        while (e.hasMoreElements()) {
//            String key = (String)e.nextElement();
//            Centroid c = (Centroid)h.get(key);
//            int r = (int)Math.round(c.dm / 2);
//            Polygon middle = EUtils.pCircle(c.x, c.y, r);
//            c.middle = middle;
//            ipCopy.setValue(0);
//            ipCopy.fillPolygon(middle);
//            ipTemplate.setValue(0);
//            ipTemplate.fillPolygon(middle);
//        }
//
//        // now obtain totals for nuclear red and "annulus" red
//        for (int i=0; i < iKeys.size(); i++) {
//            String key = (String)iKeys.get(i);
//            Centroid c = (Centroid)h.get(key);
//            getInfo(ipData, ipCopy, ipTemplate, c);
//        }
//
//    }

    /*
     * this function takes a Centroid representing one cell
     * and sets the red and area data into it
     * this is the hard work of the whole process
     * we examine a region corresponding to the large
     * circle and within it we locate background and
     * nuclear information
     */
    @SuppressWarnings("unused")
	private void getInfo(ImageProcessor ipData, ImageProcessor ipCopy, ImageProcessor ipTemplate, Centroid c) {
        int rad = (int)Math.round(c.d / 2);
        Polygon inner = EUtils.pCircle(c.x, c.y, rad);
        rad = (int)Math.round(c.dl / 2);
        Polygon outer = EUtils.pCircle(c.x, c.y, rad);
        Rectangle r = outer.getBounds();
        double templateArea = 0;
        double backgroundRed = 0;
        double backgroundRedCount = 0;
        double nucleusRed = 0;
        double nucleusRedCount = 0;
        for (int y=r.y; y < (r.y + r.height); y++) {
            for (int x = r.x; x <= (r.x + r.width); x++) {

                if (outer.contains(x, y)) {
                    int p = ipTemplate.getPixel(x, y);
                    templateArea += p;
                    p = ipCopy.getPixel(x, y);
                    backgroundRed += p;
                    backgroundRedCount++;
                }

                if (inner.contains(x, y)) {
                    int p = ipData.getPixel(x, y);
                    nucleusRed += p;
                    nucleusRedCount ++;
                }
            }
        }
        c.nuclearRed = nucleusRed;
        c.nuclearArea = nucleusRedCount;
        c.annulusRed = backgroundRed;
        c.annulusArea = templateArea/255;
        c.outer = outer;
        c.inner = inner;
    }

    @SuppressWarnings("unused")
	private void showInfo(ImageProcessor ipCopy, ImageProcessor ipTemplate, Centroid c) {
        ipTemplate.setValue(0);
        ipTemplate.drawPolygon(c.outer);
        ipCopy.setValue(255);
        ipCopy.drawPolygon(c.middle);
        ipCopy.drawPolygon(c.outer);
        double templateArea = c.annulusArea;
    }


    @SuppressWarnings("unused")
	private double [] processStuff(ImageProcessor ipData, ImageProcessor ipCopy, ImageProcessor ipTemplate, Centroid c) {
        double [] rtn = new double[3];
        double kSmall = 1.5;
        double kLarge = 2.;

        int rn = (int)Math.round(c.d / 2);          // radius of nucleus
        int rs = (int)Math.round(kSmall * c.d / 2); // radius of middle boundary
        int rl = (int)Math.round(kLarge * c.d / 2); // radius of outer boundary

        Polygon pn = EUtils.pCircle(c.x, c.y, rn);
        Polygon ps = EUtils.pCircle(c.x, c.y, rs);
        Polygon pl = EUtils.pCircle(c.x, c.y, rl);
        return rtn;
    }

    private void zeroExpandedCentroid(ImageProcessor ip, Centroid c, double factor) {
        int r = (int)Math.round(factor * c.d / 2);
        ip.setValue(0);
        ip.fillPolygon(EUtils.pCircle(c.x, c.y, r));
    }

    private double estimateArea(ImageProcessor ip, int cx, int cy, int rl, String name) {
        double area = 0;
        OvalRoi oRoi = new OvalRoi(cx - rl, cy - rl, 2*rl, 2*rl);

        Rectangle r = oRoi.getBounds();
        for (int y=r.y; y < (r.y + r.height); y++) {
            for (int x = r.x; x <= (r.x + r.width); x++) {
                if (oRoi.contains(x, y)) {
                    int p = ip.getPixel(x, y);
                    if (name.equals("Epla")) println("estimateArea, " + p);
                    area += p;
                }
            }
        }
        return area/100;
    }


    @SuppressWarnings("unused")
	private void zeroCircle(ImageProcessor ip, int cx, int cy, int d) {
        OvalRoi oRoi = new OvalRoi(cx - d/2, cy - d/2, d, d);

        Rectangle r = oRoi.getBounds();
        int width = ip.getWidth();
        for (int y=r.y; y < (r.y + r.height); y++) {
            for (int x = r.x; x <= (r.x + r.width); x++) {
                if (oRoi.contains(x, y)) {
                    ip.putPixel(x, y, 0);
                }
            }
        }
    }

    private void zeroPolygonRoi(ImageProcessor ip, Polygon p) {
        ip.setRoi(new PolygonRoi(p, Roi.POLYGON));
        ip.setValue(0);
        ip.fill();
    }

//    private ImageProcessor getRedData(String greenName) {
//        //if (ImageWindow.cUseZip == 2) return getRedZipData(greenName);
//        println("getRedData, " + greenName);
//        FileInputStream fis;
//        ImagePlus ip = null;
//        try {
//            fis = new FileInputStream(greenName);
//            byte [] ba = ImageWindow.readByteArray(fis);
//            ip = ImageWindow.openTiff(new ByteArrayInputStream(ba), false);
//            fis.close();
//	     if(ImageWindow.imagewindowUseStack==1) {
//		int markerChannel=1;
//		ip=ImageWindow.splitImage(ip,markerChannel);
//	    }
//        } catch(IOException ioe) {
//            //System.out.println(ioe);
//        }
//        if (ip != null) return ip.getProcessor();
//        else return null;
//    }

    public void test2() {
        println("test2, ");
    }

    public void initialize() {
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        nuclei_record = iNucleiMgr.getNucleiRecord();
        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        //iIdentity = iNucleiMgr.getIdentity();
        iRoot = iAncesTree.getRoot();
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals(TEST1)) {
            //test1();
        } else if (s.equals(TEST2)) {
            test2();
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
        iTextField.setText("175");
        iToolBar.add(iTextField);
        JButton jb = null;
        jb = new JButton(CLEAR);
        addToolBarButton(jb);
        jb = new JButton(TEST1);
        addToolBarButton(jb);
        jb = new JButton(TEST2);
        addToolBarButton(jb);
    }

    // this function added for use in background correction
    public double nucDiameter(Nucleus n, double imgPlane, double dx) {
        if (n == null) return -1; //covers some issues re currentCell and not tracking
        double r = -0.5;
        double cellPlane = n.z;
        double R = dx/2.; //pixels
        double y = (cellPlane - imgPlane)*iNucleiMgr.getZPixRes()/R;
        double r2 = 1 - y*y;
        if (r2 >= 0.) r = Math.sqrt(r2)*R;
        return 2*r;
    }

    private static final String
    CLEAR = "Clear"
   ,TEST1 = "One"
   ,TEST2 = "All"
   ,TEST3 = "Test3"
   ;


    private class Totals {
        String name;
        double nuclearRed;
        double nuclearArea;
        double annulusRed;
        double annulusArea;
        double fri;
        double bri;

        @Override
		public String toString() {
            StringBuffer br = new StringBuffer("Totals, " + name);
            br.append(CS + fmt1(fri - bri));
            br.append(CS + fmt1(fri));
            br.append(CS + fmt1(bri));
            br.append(CS + fmt1(nuclearRed));
            br.append(CS + fmt1(nuclearArea));
            br.append(CS + fmt1(annulusRed));
            br.append(CS + fmt1(annulusArea));

            return br.toString();
        }
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
        public double dl;   //the large diameter in "this" plane
        public double dm;   //the small diameter in "this" plane
        public Polygon inner;
        public Polygon middle;
        public Polygon outer;
        public double nuclearRed;
        public double nuclearArea;
        public double annulusRed;
        public double annulusArea;



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

    public static void main(String[] args) {
    }

    private static void println(String s) {System.out.println(s);}
    private static final String CS = ", ";
    private static final DecimalFormat DF1 = new DecimalFormat("####.##");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt1(double x) {return DF1.format(x);}

}
