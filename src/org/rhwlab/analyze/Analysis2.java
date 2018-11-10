/*
 * Created on Jul 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

import ij.gui.PlotWindow;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.C;
import org.rhwlab.utils.Log;

/**
 * Contains a UI and functions for studying the rotation of the
 * embryo. This is the most complete Analysis class and as such
 * can be a template for new data analysis classes.
 * Its Analyze menu item label is:
 * Nuclei rotation
 *
 * @author biowolp
 * @version 1.1 November 30, 2005
 */
public class Analysis2 extends Log {
    AceTree         iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    Hashtable       iNucleiMgrHash;
    //Identity        iIdentity;
    private JFrame  iFrame;
    private JPanel  iPanel;
    private double     iXA; // for angle()
    private double     iYA;
    private double     iZA;


    public Analysis2(String title) {
        super(title);
        showMe();
        JButton jb = null;
        jb = new JButton(CLEAR);
        addToolBarButton(jb);
        jb = new JButton(TEST1);
        addToolBarButton(jb);
        jb = new JButton(TEST2);
        addToolBarButton(jb);
        jb = new JButton(ANGLE);
        addToolBarButton(jb);
        jb = new JButton(TEST3);
        addToolBarButton(jb);
        jb = new JButton(TEST4);
        addToolBarButton(jb);
        //jb = new JButton(TEST5);
        //addToolBarButton(jb);
        initialize();

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

    @SuppressWarnings("deprecation")
	private void test3() {
        append("test3 entered");
        float [] xValues = new float[nuclei_record.size()];
        float [] yValues = new float[nuclei_record.size()];
        String yLabel = "cell count";
        String xLabel = "time";
        String title = "cell count vs time";
        for (int i=0; i < nuclei_record.size(); i++) {
            Vector nuclei = (Vector)nuclei_record.elementAt(i);
            yValues[i] = countNuclei(nuclei);
            xValues[i] = i;
        }


        PlotWindow pw = new PlotWindow(title, xLabel, yLabel, xValues, yValues);
        pw.setLimits(0, 250, 0, 500);
        pw.draw();
    }

    private int countNuclei(Vector nuclei) {
        int count = 0;
        Enumeration e = nuclei.elements();
        while(e.hasMoreElements()) {
            Nucleus n = (Nucleus)e.nextElement();
            if (n.status > 0) count++;
        }
        return count;
    }


    /*private void lineageAngle() {
        initialize();
        PlotData pd = anglecalc(iNucleiMgr);
        String yLabel = "angle";
        String xLabel = "time";
        String title = iNucleiMgr.getConfig().iConfigFileName; //"angle vs time";
        File f = new File(title);
        JPlotLayout plotLayout = plotlayout(f.getName(), 480, 320, xLabel, yLabel, pd.xValues, pd.yValues);
        JFrame frame = new JFrame("lineage angle");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(plotLayout, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }*/

    /*public void angleSeries() {
        append(TEST1);
        initialize();
        createFrame();
        Enumeration e = iNucleiMgrHash.keys();
        while(e.hasMoreElements()) {
            addToFrame(lineageangle((String)e.nextElement()));
        }
        showFrame();
    }*/

    private void createFrame() {
        iFrame = new JFrame("angle series");
        //frame.setSize(new Dimension(200, 100));
        iPanel = new JPanel();
        iPanel.setLayout(new GridLayout(3,4));
        //iFrame.getContentPane().setLayout(new GridLayout(5,4));
    }

  /*  private void addToFrame(JPlotLayout layout) {
        JPanel jp = new JPanel();
        jp.add(layout);
        iPanel.add(jp);
    }*/

    private void showFrame() {
        iFrame.getContentPane().add(iPanel);
        iFrame.pack();
        iFrame.setVisible(true);
    }

    /*private JPlotLayout plotlayout(String title, String xLabel, String yLabel, double [] xValues, double [] yValues) {
        return plotlayout(title, 240, 160, xLabel, yLabel, xValues, yValues);
    }*/

   /* private JPlotLayout plotlayout(String title, int width, int height, String xLabel, String yLabel, double [] xValues, double [] yValues) {
        JPlotLayout layout_ = new JPlotLayout(false, false, false, "bogus", null, false);
        layout_.setSize(new Dimension(width,height));
        layout_.setMouseEventsEnabled(false);
        layout_.setBatch(true);
        layout_.setTitles(title, "", "");
        layout_.setTitleHeightP(0.4, 0.4);
        SimpleLine data = new SimpleLine((double [])xValues, yValues, "CumAngle ");
        SGTMetaData meta = new SGTMetaData(xLabel, "", false, false);
        data.setXMetaData(meta);
        meta = new SGTMetaData(yLabel, "", false, false);
        data.setYMetaData(meta);
        layout_.addData(data, "cum angle");
        Domain d = layout_.getRange();*/
        //System.out.println(title + C.CS  + d.getYRange());
        /*
        d.setYRange(new Range2D(-80, 10));
        try {
            layout_.setRange(d);
        } catch(Exception e) {
            e.printStackTrace();
        }
       

        layout_.setBatch(false);
        return layout_;

    } */

    @SuppressWarnings("unused")
	private PlotData anglecalc(NucleiMgr nucMgr) {
        Vector  nucleirecord = nucMgr.getNucleiRecord();
        //initialize();
        double f = 180./Math.PI;
        double zfac = nucMgr.getZPixRes();
        double planeEnd = nucMgr.getPlaneEnd();
        //int height = ImageWindow.cImageHeight;
        int height = 1;
        //System.out.println("height=" + height);
        int first = 1;
        int last = nuclei_record.size() - 1;
        double [] xValues = new double[last - first];
        double [] yValues = new double[last - first];
        String yLabel = "angle";
        String xLabel = "time";
        String title = nucMgr.getConfig().iConfigFileName; //"angle vs time";
        for (int i=first; i < last; i++) {
            Vector nuclei = (Vector)nucleirecord.elementAt(i);
            Enumeration e = nuclei.elements();
            Nucleus n;
            double ry = 0;
            double rz = 0;
            double ly = 0;
            double lz = 0;
            double countr = 0;
            double countl = 0;
            double y, z;
            while (e.hasMoreElements()) {
                n = (Nucleus)e.nextElement();
                if (n.status == Nucleus.NILLI) continue;
                if (!(n.identity.indexOf("ABpr") == 0 || n.identity.indexOf("ABpl") == 0)) continue;
                //count++;
                y = height - n.y;
                z = (planeEnd - n.z) * zfac;
                if (n.identity.indexOf("ABpr") == 0) {
                    ry += y;
                    rz += z;
                    countr++;
                }
                if (n.identity.indexOf("ABpl") == 0) {
                    ly += y;
                    lz += z;
                    countl++;
                }
                //System.out.println(i + C.CS + y + C.CS + z  + C.CS + n.identity);
            }
            xValues[i - 1] = i;
            if (countr == 0 || countl == 0) {
                yValues[i - 1] = 0;
                continue;
            }
            ry /= countr;
            rz /= countr;
            ly /= countl;
            lz /= countl;
            double m = (ry - ly)/(rz - lz);
            double angdeg = Math.atan(m)*f;
            if (angdeg > 50 ) angdeg -= 180;
            String s = i + C.CS + m + C.CS + angdeg + C.CS ;
            debugShow(Integer.toString(i), m, angdeg, ry, rz, ly, lz,
                    (ry - ly), (rz - lz), 0);
            //append(s);
            //System.out.println(s);
            yValues[i - 1] = angdeg;
        }
        fakeBeginning(yValues);
        return new PlotData(xValues, yValues);
    }

    private double slope(double sumx, double sumy, double sumx2, double sumxy, double n) {
        double m = 0;
        m = (n * sumxy - sumx * sumy)/(n * sumx2 - sumx * sumx);
        return m;
    }

    public class PlotData {
        public double [] yValues;
        public double [] xValues;
        public PlotData(double [] x, double [] y) {
            xValues = x;
            yValues = y;
        }

        public void showMe() {
        //    for (int i=0; i < yValues.length; i++) {
        //        System.out.println("PlotData: " + i + C.CS + xValues[i] + C.CS + yValues[i]);
        //    }
        }
    }

    /*private JPlotLayout lineageangle(String config) {
        //System.out.println("lineageangle: " + config);
        NucleiMgr nucMgr = (NucleiMgr)iNucleiMgrHash.get(config);
        PlotData pd = anglecalc(nucMgr);
        String yLabel = "angle";
        String xLabel = "time";
        String title = config; //"angle vs time";
        return plotlayout(title, xLabel, yLabel, pd.xValues, pd.yValues);

    }*/

    private void fakeBeginning(double [] yValues) {
        double firstNonZeroValue = 0;
        int firstNonZeroIndex = 0;
        for(int i=0; i < yValues.length; i++) {
            //System.out.println("fakeBeginning: " + i + C.CS + yValues[i]);
            if (yValues[i] == 0) continue;
            else {
                firstNonZeroIndex = i;
                firstNonZeroValue = yValues[i];
                break;
            }
        }
        for (int i=0; i < firstNonZeroIndex; i++) {
            yValues[i] = firstNonZeroValue;
        }
        //System.out.println("fakeBeginning: " + firstNonZeroIndex + C.CS + firstNonZeroValue);
    }


    // scheme:
    // work with nuclei at two adjacent time points
    // nuclei1 refers to previous time point
    // nuclei2 refers to this time point
    // find the center of gravity of all nuclei at first time point
    // hold this in local xa, ya, za
    // find the center of gravity of all nuclei at second time point
    // hold this in iXA, iYA, iZA
    // then for each nucleus
    // bypass any that have status -1
    // bypass any that are dividing
    // thus keep nuclei that are present in this and previous time point
    // then, having a nucleus n1 from nuclei1
    // and its matching nucleus n2 from nuclei2
    // find the y and z positions of n1 relative to its center of gravity
    // store these in dy1, dz1
    // find the y and z positions of n2 relative to its center of gravity
    // store these in dy2, dz2
    // find the radius and angle representation of those y,z coordinates
    // using function angrad()


    @SuppressWarnings("unused")
	private PlotData angleByIncrements(NucleiMgr nucMgr) {
        //append("angle");
        initialize();
        double f = 180./Math.PI;
        double zfac = nucMgr.getZPixRes();
        Vector nucleirecord = nucMgr.getNucleiRecord();
        double cumAng = 0;
        double avgAng = 0;
        int first = nucMgr.getConfig().iStartingIndex;
        int last = nucMgr.getConfig().iEndingIndex;
        Vector nuclei1 = (Vector)nucleirecord.elementAt(first);
        double [] xValues = new double[last - first + 1];
        double [] yValues = new double[last - first + 1];
        DecimalFormat fmt = new DecimalFormat("####.##");

        //for (int i=1; i < nuclei_record.length - 1; i++) {
        //System.out.println("angle: " + first + C.CS + last);
        for (int i=first; i <= last; i++) {
            //if (i > 50) break;
            getAvgs(nuclei1);
            double xa = iXA;
            double ya = iYA;
            double za = iZA;
            Vector nuclei2 = (Vector)nucleirecord.elementAt(i);
            getAvgs(nuclei2);
            //Vector pairs = new Vector();
            Nucleus n1 = null;
            Nucleus n2 = null;
            String s = null;
            String CS = C.CS;
            double r = 0;
            double raprod = 0;

            Enumeration e = nuclei1.elements();
            while (e.hasMoreElements()) {
                n1 = (Nucleus)e.nextElement();
                if (n1.status == Nucleus.NILLI) continue;
                //if (!(n1.identity.indexOf("E") == 0 || n1.identity.indexOf("MS") == 0)) continue;
                if (n1.successor1 <= 0 || n1.successor2 > 0) continue;
                //System.out.println("n1: " + n1);
                n2 = (Nucleus)nuclei2.elementAt(n1.successor1 - 1);
                if (!n1.identity.equals(n2.identity)) continue;

                //System.out.println("n2: " + n2);
                double dy1 = n1.y - ya;
                double dz1 = n1.z * nucMgr.getZPixRes() - za;
                double dy2 = n2.y - iYA;
                double dz2 = n2.z * nucMgr.getZPixRes() - iZA;
                double [] c1 = new double[2];
                double [] c2 = new double[2];
                c1 = complex(dy1, dz1);
                c2 = complex(dy2, dz2);
                double a1 = c1[1]; //angrad(dy1, dz1);
                double a2 = c2[1]; //angrad(dy2, dz2);
                double r1 = c1[0];

                double da = a2 - a1;
                if (da > Math.PI * 1.5) da -= 2 * Math.PI;
                else if (da < - 1.5 * Math.PI) da += 2 * Math.PI;
                if (i < 8) debugShow(n1.identity, ya, za, dy1, dz1, dy2, dz2, a1*f, a2*f, da*f);

                //r1 = r1*r1;
                r += r1; //1;
                raprod +=  r1 * da;

                if (!n1.identity.equals(n2.identity)) {
                    System.out.println("bad data");
                }
            }
            //s = String.valueOf(raprod/r * f);
            //System.out.println();

            if (r > 0) avgAng = raprod / r * f;
            cumAng += avgAng;
            //System.out.println("time: " + i + CS + cumAng + CS + avgAng + CS + raprod + CS + r);
            append("time, cumAng, avgAng, " + i + CS + fmt.format(cumAng) + CS + fmt.format(avgAng)
                    + CS + fmt.format(raprod) + CS + fmt.format(r));
            nuclei1 = nuclei2;
            yValues[i - 1] = cumAng;
            xValues[i - 1] = i;
        }

        //return new PlotData(xValues, yValues);
        double [] xx = new double[last - first];
        System.arraycopy(xValues, 0, xx, 0, last - first);
        double [] yy = new double[last - first];
        System.arraycopy(yValues, 0, yy, 0, last - first);
        //testResults(xx, yy);
        return new PlotData(xx, yy);
    }

    private void testResults(double [] xx, double [] yy) {
        for (int i=0; i < xx.length; i++) {
            System.out.println("testResults: " + i + C.CS + xx[i] + C.CS + yy[i]);
        }
    }

    /*private void plotAngleByIncrements() {
        String yLabel = "angle";
        String xLabel = "time";
        File f = new File(iNucleiMgr.getConfig().iConfigFileName);
        String title = f.getName();
        PlotData pd = angleByIncrements(iNucleiMgr);
        pd.showMe();
        JPlotLayout plotLayout = plotlayout(title, 480, 320, xLabel, yLabel, pd.xValues, pd.yValues);
        JFrame frame = new JFrame("incremental angle");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(plotLayout, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }*/

    /*public void plotAngleByIncrementsSeries() {
        append("plotAngleByIncrementsSeries");
        initialize();
        createFrame();
        Enumeration e = iNucleiMgrHash.keys();
        while(e.hasMoreElements()) {
            addToFrame(incrementalLineageAngle((String)e.nextElement()));
        }
        showFrame();
    }*/

/*    private JPlotLayout incrementalLineageAngle(String config) {
        //System.out.println("lineageangle: " + config);
        NucleiMgr nucMgr = (NucleiMgr)iNucleiMgrHash.get(config);
        PlotData pd = angleByIncrements(nucMgr);
        String yLabel = "angle";
        String xLabel = "time";
        String title = "incremental: " + config; //"angle vs time";
        return plotlayout(title, xLabel, yLabel, pd.xValues, pd.yValues);

    }*/



    /*private void plotIt(String title, String xLabel, String yLabel, double [] xValues, double [] yValues) {
        JPlotLayout plotLayout = plotlayout(title, xLabel, yLabel, xValues, yValues);
        JFrame frame = new JFrame("test");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(plotLayout, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }*/

    private double angrad(double y, double z) {
        double angr = Math.atan(y/z);
        if (y < 0.) {
            if (z < 0.) angr += Math.PI;
            else angr += 2. * Math.PI;
        } else {
            if (z < 0.) angr += Math.PI;
        }
        return angr;
    }

    private double [] complex(double y, double z) {
        double [] ra = new double[2];
        ra[1] = angrad(y,z);
        ra[0] = Math.sqrt(y * y + z * z);
        return ra;
    }

    // computes the center of gravity of the nuclei at a time point
    // the center is stored in iXA, iYA, iZA
    private void getAvgs(Vector nuclei) {
        Enumeration e = nuclei.elements();
        Nucleus n = null;
        int count = 0;
        iXA = 0;
        iYA = 0;
        iZA = 0;
        while (e.hasMoreElements()) {
            n = (Nucleus)e.nextElement();
            if (n.status == Nucleus.NILLI) continue;
            count++;
            iXA += n.x;
            iYA += n.y;
            iZA += n.z * iNucleiMgr.getZPixRes();
        }
        if (count > 0) {
            iXA /= count;
            iYA /= count;
            iZA /= count;
        }
    }

    private void debugShow(String cell, double ya, double za, double dy1, double dz1,
            double dy2, double dz2, double a1, double a2, double da) {
        DecimalFormat fmt = new DecimalFormat("####.##");
        String s = cell;
        s += C.CS + fmt.format(ya);
        s += C.CS + fmt.format(za);
        s += C.CS + fmt.format(dy1);
        s += C.CS + fmt.format(dz1);
        s += C.CS + fmt.format(dy2);
        s += C.CS + fmt.format(dz2);
        s += C.CS + fmt.format(a1);
        s += C.CS + fmt.format(a2);
        s += C.CS + fmt.format(da);
        append(s);
    }

/*    SGTLine sampleData2() {
        double [] xArray = {0,1,2,3,4,5};
        double [] yArray = {0,10,20,30,40,50};
        SimpleLine data = new SimpleLine(xArray,
                yArray,
                "Float ");
        //data.setId(file);
        SGTMetaData meta = new SGTMetaData(null,
                null,
                false,
                false);
        data.setXMetaData(meta);
        meta = new SGTMetaData(null,
                null,
                false,
                    false);
        data.setYMetaData(meta);

        return data;

    }*/
   /* SGTLine sampleData1() {
        double [] xArray = {2,3,4,5};
        double [] yArray = {10,15,20,25};
        SimpleLine data = new SimpleLine(xArray,
                yArray,
                "Float ");
        //data.setId(file);
        SGTMetaData meta = new SGTMetaData("Time on X axis",
                "minutes",
                false,
                false);
        data.setXMetaData(meta);
        meta = new SGTMetaData("Angle on Y axis",
                "degrees",
                false,
                    false);
        data.setYMetaData(meta);

        return data;

    }*/


    /*public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals(TEST1)) {
            append(TEST1 + "not supported");
            angleSeries();
        } else if (s.equals(TEST2)) {
            append(TEST2);
            lineageAngle();
        } else if (s.equals(ANGLE)) {
            //append(ANGLE);
            plotAngleByIncrements();
        } else if (s.equals(TEST3)) {
            append(TEST3);
            plotAngleByIncrementsSeries();
        } else if (s.equals(TEST4)) {
            append(TEST4);
            test3();
        } else if (s.equals(CLEAR)) {
            append(TEST5);
            iText.setText("");
        } else super.actionPerformed(e);
    }*/

    private static final String
         CLEAR = "Clear"
        ,LINE  = "                                        "
        ,ANGLE = "incAngle"
        ,TEST1 = "SeriesAng"
        ,TEST2 = "LinAng"
        ,TEST3 = "SeriesIncAng"
        ,TEST4 = "CountCells"
        ,TEST5 = "Test5"
        ;

    public static void main(String[] args) {
    }
}
/*
    private void test4() {
        append("test4 entered");
        CanonicalTree ct = CanonicalTree.getCanonicalTree();
        Hashtable cth = ct.getCanonicalNamesHash();
        Enumeration e = cth.keys();
        while(e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String value = (String)cth.get(key);
            //String suffix = "NOT AVAILABLE";
            char suffix = 'N';
            if (value.length() > key.length()) suffix = getCanonicalAxis(key, cth);
            //if (value.length() > key.length()) suffix = value.substring(key.length());
            append(key + C.CS + value + C.CS + suffix);
            //if ( !suffix.equals("a")) append(key + C.CS + value + C.CS + suffix);
        }
    }

    private char getCanonicalAxis(String parentName, Hashtable canonicalNamesHash) {
        //String parentName = parent.identity;
        String dauName = (String)canonicalNamesHash.get(parentName);
        char rtn = ' ';
        int k = parentName.length();
        if (dauName.length() > k) rtn = dauName.charAt(k);
        return rtn;
    }

    private void test5() {
        append("test5 entered");
        Identity identity = iNucleiMgr.getIdentity();
        int method1 = identity.getNamingMethod();
        Vector [] allNames1 = identity.getIdentities();
        identity.setNamingMethod(Identity3.NEWCANONICAL);
        identity.identityAssignment();
        Vector [] allNames2 = identity.getIdentities();
        identity.setNamingMethod(method1);
        identity.putIdentities(allNames1);
        Hashtable ht = new Hashtable();
        for (int i=0; i < allNames1.length; i++) {
            Vector names1 = allNames1[i];
            Vector names2 = allNames2[i];
            for (int j=0; j < names1.size(); j++) {
                String name1 = (String)names1.elementAt(j);
                String name2 = (String)names2.elementAt(j);
                if (!ht.containsKey(name1)) {
                    ht.put(name1, name2);
                    char c1 = name1.charAt(name1.length() -1);
                    char c2 = name2.charAt(name2.length() -1);
                    if (c1 != c2) {
                        StringBuffer sb = new StringBuffer(LINE);
                        sb.replace(0, name1.length() - 1, name1);
                        sb.replace(20, 20 + name2.length() - 1, name2);
                        append(sb.toString());
                    }
                }
            }

        }

    }

    // try to construct red channel mockup images
    private void test1() {
        append("test1 entered");
        for (int i=127; i < nuclei_record.size(); i++) {
            append(" ");
            append(String.valueOf(i));
            Vector nuclei = (Vector)nuclei_record.elementAt(i);
            Vector circles = new Vector();
            Enumeration e = nuclei.elements();
            while (e.hasMoreElements()) {
                Nucleus n = (Nucleus)e.nextElement();
                String s = n.identity;
                if (s.charAt(0) == 'E' && s.length() >= 4) {
                    //append(s);
                    for (int plane = 1; plane <= 35; plane++) {
                        double d = iNucleiMgr.nucDiameter(n, plane);
                        if (d > 0) {
                            //append(String.valueOf(plane) + C.CS + String.valueOf(d));
                            int [] ia = new int[4];
                            ia[0] = plane;
                            ia[1] = n.x;
                            ia[2] = n.y;
                            ia[3] = (int)d;
                            Vector v = new Vector();
                            v.add(ia);
                            v.add(n.identity);
                            circles.add(v);
                            String ss = String.valueOf(ia[0]);
                            for (int m=1; m < 4; m++) {
                                ss += C.CS + String.valueOf(ia[m]);

                            }
                            ss += C.CS + n.identity;
                            ss += C.CS + n.z;
                            //circles.add(ss);

                            append(ss);
                        }

                    }
                }
            }
            if (circles.size() == 0) continue;

            append(" ");
            Collections.sort(circles, new Comparer());
            printSortedCircles(circles);
            Vector p = (Vector)circles.elementAt(0);
            int plane = ((int [])p.elementAt(0))[0];
            Vector v = new Vector();
            Enumeration ee = circles.elements();
            while (ee.hasMoreElements()) {
                Vector vv = (Vector)ee.nextElement();
                int [] ia = (int [])vv.elementAt(0);
                if (ia[0] != plane) {
                    processVectorOfROIs(v, i + 1, plane);
                    plane = ia[0];
                    v = new Vector();
                }
                v.add(vv);
            }
            //append("end of time point");
        }
    }

    private void printSortedCircles(Vector circles) {
        append("printSortedCircles");
        Enumeration e = circles.elements();
        while(e.hasMoreElements()) {
            Vector v = (Vector)e.nextElement();
            int [] ia = (int []) v.elementAt(0);
            String s = (String)v.elementAt(1);
            String ss = s;
            ss += C.CS + ia[0];
            ss += C.CS + ia[1];
            ss += C.CS + ia[2];
            ss += C.CS + ia[3];
            append(ss);

        }

    }

    private void processVectorOfROIs(Vector v, int time, int plane) {
        String basename = "/home/biowolp/AncesTree/temp2/imagesR/050405-t";
        String basegreenname = "/home/biowolp/AncesTree/temp2/images/050405-t";
        String name = basename + EUtils.makePaddedInt(time, 3) + "-p";
        String greenname = basegreenname + EUtils.makePaddedInt(time, 3) + "-p";
        name += EUtils.makePaddedInt(plane,2) + ".tif";
        greenname += EUtils.makePaddedInt(plane,2) + ".tif";
        ImageProcessor ipGreen = getGreenData(greenname);

        append(name);
        int width = 760;
        int height = 512;
        ByteProcessor bproc = new ByteProcessor(width, height);

        Enumeration e = v.elements();
        while(e.hasMoreElements()) {
            Vector vv = (Vector)e.nextElement();
            int [] ia = (int []) vv.elementAt(0);
            String s = (String)vv.elementAt(1);
            append(s + C.CS + ia[0] + C.CS + ia[1] + C.CS + ia[2] + C.CS + ia[3]);
            addToImage(bproc, ia, ipGreen);
        }
        ImagePlus iplus = new ImagePlus("", bproc);
        FileSaver fs = new FileSaver(iplus);
        fs.saveAsTiff(name);

    }

    private ImageProcessor getGreenData(String greenName) {
        FileInputStream fis;
        ImagePlus ip = null;
        //String ss = "/home/biowolp/AncesTree/temp2/images/050405-t050-p15.tif";
        try {
            fis = new FileInputStream(greenName);
            byte [] ba = ImageWindow.readByteArray(fis);
            ip = ImageWindow.openTiff(new ByteArrayInputStream(ba), false);
            //ip = readData(fis);
        } catch(IOException ioe) {
            System.out.println("ImageWindow.test3 exception ");
            System.out.println(ioe);
        }
        return ip.getProcessor();
    }

    public  void addToImage(ImageProcessor ip, int [] ia, ImageProcessor ipGreen) {
        byte [] pixels = (byte [])ip.getPixels();
        byte [] greenPixels = (byte [])ipGreen.getPixels();
        int width = ip.getWidth();
        int d = ia[3];
        int xx = ia[1] - d/2;
        int yy = ia[2] - d/2;
        OvalRoi oRoi = new OvalRoi(xx, yy, d, d);
        ip.setRoi(oRoi);
        Rectangle r = ip.getRoi();
        append(r.x + C.CS + r.y + C.CS + r.width + C.CS + r.height);
        int offset, i;
        for (int y=r.y; y < (r.y + r.height); y++) {
            offset = y * width;
            for (int x = r.x; x < (r.x + r.width); x++) {
                i = offset + x;
                if (oRoi.contains(x, y)) {
                    pixels[i] = (byte)(greenPixels[i]*Math.random());
                    //pixels[i] = (byte)(256.*Math.random());
                }
            }
        }
    }

    private void makeImage2(int time, int plane, int [] ia) {
        String ss = String.valueOf(time);
        ss += C.CS + String.valueOf(plane);
        for (int m=0; m < 4; m++) {
            ss += C.CS + String.valueOf(ia[m]);

        }
        //String ss = (String) ee.nextElement();
        append(ss);
        //ByteProcessor bproc = new ByteProcessor(width, height);
        //test(bproc);
        //ImagePlus ip2 = new ImagePlus("newtest3", bproc);
        //ij.gui.ImageWindow iImgWin = new ij.gui.ImageWindow(ip2);

        //FileSaver fs = new FileSaver(ip2);
        //fs.saveAsTiff();

    }

    class Comparer implements Comparator {
        public int compare(Object obj1, Object obj2)
        {
            Vector v1 = (Vector)obj1;
            Vector v2 = (Vector)obj2;
            int [] ia1 = (int [])v1.elementAt(0);
            int [] ia2 = (int [])v2.elementAt(0);
            int i1 = ia1[0];
            int i2 = ia2[0];
            return (i1 - i2);
        }
    }

    private void test91() {
        append("test91 entered");
        for (int i=0; i < nuclei_record.size(); i++) {
            Nucleus n = iNucleiMgr.getCurrentCellData("polar1", i + 1);
            CleanString cs = new CleanString();
            cs.insertText(n.identity);
            //cs.addComma();
            cs.insertX(n.x, true);
            cs.insertX(n.y, true);
            cs.insertX(((int)n.z*10), true);
            cs.insertX(n.size, false);
            append(cs.toString());

        }
    }

    private void test2() {
        append("test2 entered");
        double f = 180./Math.PI;
        double sumy = 0;
        double sumz = 0;
        double sumy2 = 0;
        double sumyz = 0;
        double count = 0;
        for (int i=0; i < 10; i++) {
            sumy += i;
            sumz += i;
            sumy2 += i * i;
            sumyz += i * i;
            count++;
        }
        double m = slope(sumy, sumz, sumy2, sumyz, count);
        String s = m + C.CS + Math.atan(m)*f;
        append(s);
    }

    private void plot(String title, String xLabel, String yLabel, double [] xValues, double [] yValues) {
        //PlotWindow pw = new PlotWindow(title, xLabel, yLabel, xValues, yValues);
        //pw.setLimits(0, 200, -90, 30);
        //pw.draw();
        JPlotLayout layout_ = new JPlotLayout(false, false, false, "bogus", null, false);
        layout_.setBatch(true);
        layout_.setTitles(title, "", "");
        layout_.setTitleHeightP(0.2, 0.2);
        SimpleLine data = new SimpleLine((double [])xValues, yValues, "Float ");
        SGTMetaData meta = new SGTMetaData(xLabel, "", false, false);
        data.setXMetaData(meta);
        meta = new SGTMetaData(yLabel, "", false, false);
        data.setYMetaData(meta);
        layout_.addData(data, "angle");



        //SGTData data = sampleData1();
        //layout_.addData(data, "testing");
        //SGTData data2 = sampleData2();
        //layout_.addData(data2, "testing2");

        //Domain d = layout_.getRange();
        //d.setYRange(new Range2D(0, 100));
        //try {
        //    layout_.setRange(d);
        //} catch(Exception e) {
        //    e.printStackTrace();
        //}

        layout_.setBatch(false);
        JFrame frame = new JFrame("test");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(layout_, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }

    private PlotData anglecalc2(NucleiMgr nucMgr) {
        Vector  nucleirecord = nucMgr.getNucleiRecord();
        //initialize();
        double f = 180./Math.PI;
        double zfac = nucMgr.getZPixRes();
        double planeEnd = nucMgr.getPlaneEnd();
        int height = ImageWindow.cImageHeight;
        System.out.println("height=" + height);
        int first = 1;
        int last = nuclei_record.size() - 1;
        double [] xValues = new double[last - first];
        double [] yValues = new double[last - first];
        String yLabel = "angle";
        String xLabel = "time";
        String title = nucMgr.getConfig().iConfigFileName; //"angle vs time";
        for (int i=first; i < last; i++) {
            Vector nuclei = (Vector)nucleirecord.elementAt(i);
            Enumeration e = nuclei.elements();
            Nucleus n;
            double sumy = 0;
            double sumz = 0;
            double sumz2 = 0;
            double sumyz = 0;
            double count = 0;
            double y, z;
            while (e.hasMoreElements()) {
                n = (Nucleus)e.nextElement();
                if (n.status == Nucleus.NILLI) continue;
                if (!(n.identity.indexOf("ABpr") == 0 || n.identity.indexOf("ABpl") == 0)) continue;
                y = height - n.y;
                z = (planeEnd - n.z) * zfac;
                sumy += y;
                sumz += z;
                sumz2 += z * z;
                sumyz += y * z;
                count++;
                System.out.println(i + C.CS + y + C.CS + z  + C.CS + n.identity);
            }
            xValues[i - 1] = (double)i;
            if (count == 0) {
                yValues[i - 1] = 0;
                continue;
            }
            System.out.println(i + C.CS + sumy + C.CS + sumz + C.CS + sumz2 + C.CS + sumyz + C.CS + count);
            double m = slope(sumz, sumy, sumz2, sumyz, count);
            double angdeg = Math.atan(m)*f;
            String s = i + C.CS + m + C.CS + angdeg;
            append(s);
            System.out.println(s);
            yValues[i - 1] = (double)angdeg;
            if (Math.abs(angdeg) > 89.9) yValues[i - 1] = yValues[i - 2];
        }
        fakeBeginning(yValues);
        return new PlotData(xValues, yValues);
    }

    private JPlotLayout lineageangle2(String config) {
        //append("test1 entered");
        System.out.println("lineageangle: " + config);
        NucleiMgr nucMgr = (NucleiMgr)iNucleiMgrHash.get(config);
        Vector  nucleirecord = nucMgr.getNucleiRecord();
        //initialize();
        double f = 180./Math.PI;
        double zfac = nucMgr.getZPixRes();
        int first = 1;
        int last = nuclei_record.size() - 1;
        double [] xValues = new double[last - first];
        double [] yValues = new double[last - first];
        String yLabel = "angle";
        String xLabel = "time";
        String title = config; //"angle vs time";
        for (int i=first; i < last; i++) {
            Vector nuclei = (Vector)nucleirecord.elementAt(i);
            Enumeration e = nuclei.elements();
            Nucleus n;
            double sumy = 0;
            double sumz = 0;
            double sumy2 = 0;
            double sumyz = 0;
            double count = 0;
            while (e.hasMoreElements()) {
                n = (Nucleus)e.nextElement();
                if (n.status == Nucleus.NILLI) continue;
                if (!(n.identity.indexOf("ABpr") == 0 || n.identity.indexOf("ABpl") == 0)) continue;
                sumy += n.y;
                sumz += n.z * zfac;
                sumy2 += n.y * n.y;
                sumyz += n.y * n.z * zfac;
                count++;
            }
            xValues[i - 1] = (double)i;
            if (count == 0) {
                yValues[i - 1] = 0;
                continue;
            }
            double m = slope(sumy, sumz, sumy2, sumyz, count);
            double angdeg = Math.atan(m)*f;
            String s = i + C.CS + m + C.CS + angdeg;
            append(s);
            yValues[i - 1] = (double)angdeg;
            if (Math.abs(angdeg) > 89.9) yValues[i - 1] = yValues[i - 2];
        }
        fakeBeginning(yValues);
        return plotlayout(title, xLabel, yLabel, xValues, yValues);
    }

*/
/*
public void initialize() {
    iAceTree = AceTree.getAceTree(null);
    iNucleiMgr = iAceTree.getNucleiMgr();
    nuclei_record = iNucleiMgr.getNucleiRecord();
    iAncesTree = iNucleiMgr.getAncesTree();
    iCellsByName = iAncesTree.getCellsByName();
    //iIdentity = iNucleiMgr.getIdentity();
    //iRoot = iNucleiMgr.getRoot();
}
*/


