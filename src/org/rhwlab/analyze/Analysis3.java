/*
 * Created on Jul 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.Movie;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.snight.Parameters;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.C;
import org.rhwlab.utils.Log;

/**
 * Treat this as a template for a future Analysis class.
 * Any existing internals are obsolete as of Nov 30, 2005.
 *
 * @author biowolp
 *
 */
public class Analysis3 extends Log {
    AceTree         iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    Hashtable       iNucleiMgrHash;
    //Identity        iIdentity;
    Line            iLine;
    private double     iXA; // for angle()
    private double     iYA;
    private double     iZA;

    private JTextField iTextField;
    private Hashtable   iNamingHash;
    private Hashtable   iCompletedCellsHash;
    private char        iTag;
    private int         iSampleIndex;
    private JMenuItem   iConfigList;
    private JMenuItem   iExit;
    private JMenu       iMenu;
    private Vector      iConfigFiles;
    private int         iItem;
    private int         iItems;


    public Analysis3(String title) {
        super(title);
        showMe();
        createMenu();
        buildOutToolBar();
        initialize();
        iSampleIndex = 0;
        iText.setFont(new Font("courier", Font.PLAIN, 12));


    }
    private void buildOutToolBar() {
        iToolBar.setMaximumSize(new Dimension(500,20));
        iToolBar.add(new JLabel("prefix:"));
        iTextField = new JTextField();
        iTextField.setColumns(15);
        iTextField.setText("55");
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

    private void createMenu() {
        //JMenu menu = new JMenu("File");
        //add(menu);
        iMenu = new JMenu("File");
        iMenuBar.add(iMenu);
        iConfigList = new JMenuItem(CONFIGS);
        iExit = new JMenuItem(EXIT);
        iMenu.add(iConfigList);
        iMenu.add(iExit);
        iConfigList.addActionListener(this);
        iExit.addActionListener(this);

        //add(iMenu);

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

    /*
    private void test22() {
        append("test2 entered: ");
        initialize();
        Enumeration e = iNucleiMgrHash.keys();
        while(e.hasMoreElements()) {
            String config = (String)e.nextElement();
            NucleiMgr nucMgr = (NucleiMgr)iNucleiMgrHash.get(config);
            int ap = nucMgr.getParameters().apInit;
            int dv = nucMgr.getParameters().dvInit;
            int lr = nucMgr.getParameters().lrInit;
            append(config + C.CS + nucMgr.getImageWidth() + C.CS + nucMgr.getImageHeight()+ C.CS + ap + C.CS + dv + C.CS + lr + C.CS + ap*dv);
            //append(config + C.CS + nucMgr.getImageWidth() + C.CS + nucMgr.getImageHeight());
        }
    }
    */

    @SuppressWarnings("unused")
	private void test2() {
        initialize();
        NucleiMgr nucMgr = iNucleiMgr;
        int time = Integer.parseInt(iTextField.getText());
        Vector normNucs = extractNuclei(time, nucMgr);
        Collections.sort(normNucs, new Comparer());
        makeAllADL(normNucs, nucMgr);
        int [] ia = new int[6];
        computeStats(normNucs, ia);
        String stats = "stats: " + String.valueOf(ia[0]);
        for (int i=1; i < 6; i++) {
            stats += C.CS + ia[i];
        }
        //append(stats);
        Vector normNucs2 = new Vector();
        Enumeration ee = normNucs.elements();
        while (ee.hasMoreElements()) {
            NormNuc nn = (NormNuc)ee.nextElement();
            //append("before: " + nn.toString());
            normalize(nn, ia);
            append(nn.toString());
            //normNucs2.add(nn);

        }
        //computeStats(normNucs2, ia);
        //String stats2 = "stats2: " + String.valueOf(ia[0]);
        //for (int i=1; i < 6; i++) {
        //    stats2 += C.CS + ia[i];
        //}
        //append(stats2);
    }

    @SuppressWarnings("resource")
	Hashtable makeTimesHash(String filename) {
        Hashtable h = new Hashtable();
        try {
            FileInputStream fis = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            while (br.ready()) {
                String s = br.readLine();
                if (s.length() < 2) break;
                String [] sa = s.split(" ");
                String c = sa[0];
                int k = c.lastIndexOf("/");
                c = c.substring(k + 1);
                h.put(c, sa[1]);
                println(c + CS + sa[1]);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        return h;
    }


    @SuppressWarnings("unused")
	private void test3() {
        //append("test3 entered: ");
        initialize();
        Hashtable timesHash = makeTimesHash("/home/biowolp/work/workspace/weka/series/test88.txt");
        Enumeration e = iNucleiMgrHash.keys();
        while(e.hasMoreElements()) {
            String config = (String)e.nextElement();
            NucleiMgr nucMgr = (NucleiMgr)iNucleiMgrHash.get(config);
            //int time = Integer.parseInt(iTextField.getText());
            int time = Integer.parseInt((String)timesHash.get(config));
            append("% " + config + CS + time);

            Vector normNucs = extractNuclei(time, nucMgr);
            Collections.sort(normNucs, new Comparer());
            makeAllADL(normNucs, nucMgr);
            int [] ia = new int[6];
            computeStats(normNucs, ia);
            String stats = "stats: " + String.valueOf(ia[0]);
            for (int i=1; i < 6; i++) {
                stats += C.CS + ia[i];
            }
            //append(stats);
            Vector normNucs2 = new Vector();
            int m = 0;
            Enumeration ee = normNucs.elements();
            while (ee.hasMoreElements()) {
                NormNuc nn = (NormNuc)ee.nextElement();
                //append("before: " + nn.toString());
                normalize(nn, ia);
                boolean skip = false;
                String s = nn.identity;
                skip = s.equals("P4") || s.equals("Z2") || s.equals("Z3");
                if (!skip) append(nn.toString());
                normNucs2.add(nn);

            }
            computeStats(normNucs2, ia);
            String stats2 = "stats2: " + String.valueOf(ia[0]);
            for (int i=1; i < 6; i++) {
                stats2 += C.CS + ia[i];
            }
            append("% " + stats2);
            StringBuffer sb = new StringBuffer();
            boolean started = false;
            ee = normNucs.elements();
            while (ee.hasMoreElements()) {
                NormNuc nn = (NormNuc)ee.nextElement();
                if (!started) {
                    sb.append("% " + nn.identity);
                    started = true;
                }
                else sb.append(CNS + nn.identity);
            }
            append(sb.toString());
        }
    }


    /**
     * Here I want to examine a time point
     * and develop for each valid nucleus
     * a structure giving a normalized x, y, z, size, and identity
     * which in the long run will be used to classify the cell
     * using some kind of crazy classifier
     *
     */

    private void test1() {
        //System.out.println("\nAnalysis3.test1 entered");
        initialize();
        int time = Integer.parseInt(iTextField.getText());
        Vector normNucs = extractNuclei(time, iNucleiMgr);
        Enumeration e = normNucs.elements();
        while (e.hasMoreElements()) {
            NormNuc nn = (NormNuc)e.nextElement();
            //append(nn.toString());
            makeADL(nn, iNucleiMgr);
            append(nn.toString());
        }
        int [] ia = new int[6];
        computeStats(normNucs, ia);
        String stats = "stats: " + String.valueOf(ia[0]);
        for (int i=1; i < 6; i++) {
            stats += C.CS + ia[i];
        }
        append(stats);
    }

    @SuppressWarnings("unused")
	private Vector extractNuclei(int time, NucleiMgr nucMgr) {
        Vector nucleiRecord = nucMgr.getNucleiRecord();
        Vector nuclei = (Vector)nucleiRecord.elementAt(time);
        int count = 0;
        Nucleus n;
        Vector normNucVec = new Vector();
        double zfac = nucMgr.getZPixRes();
        for (int i=0; i < nuclei.size(); i++) {
            n = (Nucleus)nuclei.elementAt(i);
            if (n.status == Nucleus.NILLI) continue;
            if (n.identity.indexOf("polar") >= 0) continue;
            if (n.identity.indexOf("Nuc") >= 0) continue;
            count++;
            //append(count + C.CS + n.identity);
            NormNuc normNuc = new NormNuc();
            normNuc.x = n.x;
            normNuc.y = n.y;
            normNuc.z = (int) (n.z * zfac);
            normNuc.size = n.size;
            normNuc.identity = n.identity;
            normNucVec.add(normNuc);
        }
        return normNucVec;
    }

    private void makeAllADL(Vector nnv, NucleiMgr nm) {
        Enumeration e = nnv.elements();
        while (e.hasMoreElements()) {
            NormNuc nn = (NormNuc)e.nextElement();
            makeADL(nn, nm);
        }
    }

    @SuppressWarnings("unused")
	private void makeADL(NormNuc nn, NucleiMgr nm) {
        Parameters p = nm.getParameters();
        Movie m = nm.getMovie();
        double zfac = nm.getZPixRes();
        int height = nm.getImageHeight();
        int width = nm.getImageWidth();
        int depth = (int)((m.plane_end - m.plane_start + 1) * zfac);
        int ap = nm.getParameters().apInit;
        int dv = nm.getParameters().dvInit;

        if (ap < 0) nn.x = width - nn.x;
        if (ap*dv < 0) {
            nn.y = height - nn.y;
            nn.z = depth - nn.z;
        }
    }

    private void computeStats(Vector nnv, int [] ia) {
        int sumX = 0;
        int sumX2 = 0;
        int sumY = 0;
        int sumY2 = 0;
        int sumZ = 0;
        int sumZ2 = 0;
        NormNuc nn = null;
        int k = nnv.size();
        for (int i=0; i < k; i++) {
            nn = (NormNuc)nnv.elementAt(i);
            sumX += nn.x;
            sumX2 += nn.x * nn.x;
            sumY += nn.y;
            sumY2 += nn.y * nn.y;
            sumZ += nn.z;
            sumZ2 += nn.z * nn.z;
        }
        //Standard Deviation = square root of[ (sum of Xsquared -((sum of X)*(sum of X)/N))/ (N-1)) ]
        double dstdx = Math.sqrt((sumX2 - sumX*sumX/k)/(k - 1));
        double dstdy = Math.sqrt((sumY2 - sumY*sumY/k)/(k - 1));
        double dstdz = Math.sqrt((sumZ2 - sumZ*sumZ/k)/(k - 1));
        int avgx = sumX/k;
        int avgy = sumY/k;
        int avgz = sumZ/k;
        int stdx = (int)Math.round(dstdx);
        int stdy = (int)Math.round(dstdy);
        int stdz = (int)Math.round(dstdz);
        ia[0] = avgx;
        ia[1] = avgy;
        ia[2] = avgz;
        ia[3] = stdx;
        ia[4] = stdy;
        ia[5] = stdz;

    }

    private static final int
         XMM = 350
        ,YMM = 250
        ,ZMM = 180
        ,XSDD = 130
        ,YSDD = 80
        ,ZSDD = 60
        ,SIZESCALE = 5
        ;

    private void normalize(NormNuc nn, int [] stats) {
        nn.x = XMM + (nn.x - stats[0])*XSDD/stats[3];
        nn.y = YMM + (nn.y - stats[1])*YSDD/stats[4];
        nn.z = ZMM + (nn.z - stats[2])*ZSDD/stats[5];

    }


    private class NormNuc {
        public String identity;
        public int x;
        public int y;
        public int z;
        public int size;

        @Override
		public String toString() {
            String s = String.valueOf(x);
            //s += C.CS + x;
            s += C.CS + y;
            s += C.CS + z;
            //s += C.CS + size * SIZESCALE;
            s += C.CS + identity;
            return s;
        }
    }

    class Comparer implements Comparator {
        @Override
		public int compare(Object obj1, Object obj2)
        {
            NormNuc n1 = (NormNuc)obj1;
            NormNuc n2 = (NormNuc)obj2;
            return n1.identity.compareTo(n2.identity);
        }
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals(TEST1)) {
            append(TEST1);
            test1();
        } else if (s.equals(TEST2)) {
            //append(TEST2);
            test2();
        } else if (s.equals(TEST3)) {
            //append(TEST3);
            test3();
        } else if (s.equals(CLEAR)) {
            iText.setText("");
        } else if (s.equals(EXIT)) {
            iFrame.dispose();
        } else super.actionPerformed(e);
    }

    private static final String
         CLEAR = "Clear"
        ,LINE  = "                                        "
        ,ANGLE = "Angle"
        ,TEST1 = "Test1"
        ,TEST2 = "Test2"
        ,TEST3 = "Test3"
        ,TEST4 = "Test4"
        ,TEST5 = "Test5"
        ,EXIT  = "Exit"
        ,CONFIGS = "ConfigFiles"
        ;

    public static void main(String[] args) {
    }
    private static void println(String s) {System.out.println(s);}
    private static final String CS = ", ", CNS = ",";

    private class Line {
        public StringBuffer iBuf;
        int iLast = 0;
        public Line() {
            char [] ca = new char[150];
            Arrays.fill(ca, ' ');
            iBuf = new StringBuffer(new String(ca));
        }

        public void add(int x) {
            String s = makePaddedInt(x);
            add(s);
        }

        public void add(String s) {
            int len = s.length();
            iBuf.replace(iLast, iLast + len, s);
            iLast += len + GAP;
        }

        public String makePaddedInt(int k) {
            int width = 4;
            String s = "    " + String.valueOf(k);
            int j = s.length();
            s = s.substring(j - width, j);
            return s;
        }

        private static final int
             GAP = 1
            ;


    }


}
