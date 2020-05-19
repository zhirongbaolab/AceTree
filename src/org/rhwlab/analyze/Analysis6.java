/*
 * Created on Jul 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.acetree.NucUtils;
import org.rhwlab.analyze.Analysis2.PlotData;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.snight.Identity3;
import org.rhwlab.snight.Loc;
import org.rhwlab.snight.Movie;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.snight.Parameters;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.CanonicalTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.C;
import org.rhwlab.utils.CleanString;
import org.rhwlab.utils.EUtils;
import org.rhwlab.utils.Line;
import org.rhwlab.utils.Log;

import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import gov.noaa.pmel.sgt.swing.JPlotLayout;
import gov.noaa.pmel.util.Domain;
import ij.ImagePlus;
import ij.gui.OvalRoi;
import ij.gui.PlotWindow;
import ij.io.FileSaver;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Consider building this out to extract normalized cell position
 * data for classification studies.
 *
 * @author biowolp
 * @version 1.1 November 30, 2005
 *
 */
public class Analysis6 extends Log {
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
    private JTextField iTextField_prefix; //prefix for sizes
    private JTextField iTextField_endTime; //prefix for sizes
    private Hashtable   iNamingHash;
    private Hashtable   iCompletedCellsHash;
    private char        iTag;
    private int         iSampleIndex;
    private String      iKeyCell;
    private String      iStartingCell;
    private int         iBreakout;
    private Hashtable [] iArrayOfHashes;
    private Hashtable    iCurrentHash;
    private Hashtable    iAvgsHash;


    public Analysis6(String title) {
        //super(title);
        super("Nuclei Counts vs Time");
        showMe();
        buildOutToolBar();
        initialize();
        iSampleIndex = 0;
        iText.setFont(new Font("courier", Font.PLAIN, 12));


    }
    private void buildOutToolBar() {
        //iToolBar.setMaximumSize(new Dimension(500,20));
        iToolBar.setMinimumSize(new Dimension(500,20));

        iToolBar.add(new JLabel("prefix:"));

        iTextField_prefix = new JTextField();
        iTextField_prefix.setColumns(15);
        iTextField_prefix.setText("ABal");
        iToolBar.add(iTextField_prefix);

        iToolBar.add(new JLabel("End Time:"));

        iTextField_endTime = new JTextField();
        iTextField_endTime.setColumns(15);
        iTextField_endTime.setText("240");
        iToolBar.add(iTextField_endTime);

        JButton jb = null;
        jb = new JButton(CLEAR);
        addToolBarButton(jb);
        jb = new JButton(TEST1);
        addToolBarButton(jb);
        jb = new JButton(TEST2);
        addToolBarButton(jb);
        //jb = new JButton(TEST3);
        //addToolBarButton(jb);


    }

    public void initialize() {
        System.out.println("initializing");
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

    private void test4() {
        append("test4 entered");
    }

    private void test3() {
        append("test3 entered");
    }

    private void sizes() {
        append("test2 entered: " );
        PlotData pd = getSizeVsTime();
        String yLabel = "nucleus size";
        String xLabel = "time";
        String title;
        if (iNucleiMgr.isNucConfigNull()) {
            title = iNucleiMgr.getConfig().iConfigFileName; //"angle vs time";
        } else {
            title = iAceTree.getConfig().getShortName();
        }
        File f = new File(title);
        JPlotLayout plotLayout = plotlayout(f.getName(), 600, 400, xLabel, yLabel, pd.xValues, pd.yValues);
        JFrame frame = new JFrame("nucleus size vs time");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(plotLayout, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    private String iTemplate;

    private PlotData getSizeVsTime() {
        System.out.println("getting cell sizes vs time");
        initialize();
        iTemplate = iTextField_prefix.getText();
        NucleiMgr nucMgr = iNucleiMgr;
        int first = nucMgr.getStartingIndex();
        if (!nucMgr.isNucConfigNull()) {
            first = nucMgr.getNucConfig().getStartingIndex();
        }
        //int last = nucMgr.getConfig().iEndingIndex;
        int last = Integer.parseInt(iTextField_endTime.getText());
        double [] xValues = new double[last - first + 1];
        double [] yValues = new double[last - first + 1];
        Vector nuclei = (Vector)nuclei_record.elementAt(first);
        for (int i=first; i <= last; i++) {
            int k = getSize(nuclei);
            String s = iTemplate + C.CS + i + C.CS + k;
            append(s);
            xValues[i - first] = i;
            yValues[i - first] = k;
            nuclei = (Vector)nuclei_record.elementAt(i);
        }
        return new PlotData(xValues, yValues);
    }

    private int getSize(Vector nuclei) {
        int rtn = -1;
        Nucleus [] dau = new Nucleus[2];
        int k = 0;
        Enumeration e = nuclei.elements();
        while (e.hasMoreElements()) {
            Nucleus n = (Nucleus)e.nextElement();
            String name = n.identity;
            String prefix = iTextField_prefix.getText();
            if (name.indexOf(iTemplate) < 0) continue;
            if (name.equals(iTemplate)) {
                rtn = n.size;
                break;
            }
            dau[k++] = n;
            if (k > 1) break;



        }
        int m = 1;
        if (k > 0) {
            if (dau[0].identity.compareTo(dau[1].identity) < 0) {
                m = 0;
            }
            rtn = dau[m].size;
            iTemplate = dau[m].identity;

        }
        return rtn;

    }

    private void counts() {
        append("Analysis6.test1 entered");
        PlotData pd = getCellCountVsTime();
        String yLabel = "cell count";
        String xLabel = "time";
        String title;
        if (iNucleiMgr.isNucConfigNull()) {
            title = iNucleiMgr.getConfig().iConfigFileName; //"angle vs time";
        } else {
            title = iAceTree.getConfig().getShortName();
        }
        File f = new File(title);
        JPlotLayout plotLayout = plotlayout(f.getName(), 480, 320, xLabel, yLabel, pd.xValues, pd.yValues);
        JFrame frame = new JFrame("cell count vs time");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(plotLayout, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    private JPlotLayout plotlayout(String title, String xLabel, String yLabel, double [] xValues, double [] yValues) {
        return plotlayout(title, 240, 160, xLabel, yLabel, xValues, yValues);
    }

    private JPlotLayout plotlayout(String title, int width, int height, String xLabel, String yLabel, double [] xValues, double [] yValues) {
        JPlotLayout layout_ = new JPlotLayout(false, false, false, "bogus", null, false);
        //layout_.setSize(new Dimension(240,160));
        layout_.setSize(new Dimension(width,height));
        layout_.setMouseEventsEnabled(false);
        layout_.setBatch(true);
        layout_.setTitles(title, "", "");
        layout_.setTitleHeightP(0.4, 0.4);
        SimpleLine data = new SimpleLine((double [])xValues, yValues, "count ");
        SGTMetaData meta = new SGTMetaData(xLabel, "", false, false);
        data.setXMetaData(meta);
        meta = new SGTMetaData(yLabel, "", false, false);
        data.setYMetaData(meta);
        layout_.addData(data, "");
        Domain d = layout_.getRange();
        //System.out.println(title + C.CS  + d.getYRange());
        /*
        d.setYRange(new Range2D(-80, 10));
        try {
            layout_.setRange(d);
        } catch(Exception e) {
            e.printStackTrace();
        }
        */

        layout_.setBatch(false);
        return layout_;

    }

    private PlotData getCellCountVsTime() {
        System.out.println("getting cell counts vs time");
        initialize();
        NucleiMgr nucMgr = iNucleiMgr;
        int first = nucMgr.getStartingIndex();
        if (!nucMgr.isNucConfigNull()) {
            first = nucMgr.getNucConfig().getStartingIndex();
        }
        //int last = nucMgr.getConfig().iEndingIndex;
        int last = Integer.parseInt(iTextField_endTime.getText());
        double [] xValues = new double[last - first + 1];
        double [] yValues = new double[last - first + 1];
        Vector nuclei = (Vector)nuclei_record.elementAt(first);
        for (int i=first; i <= last; i++) {
            int k = countNuclei(nuclei);
            String s = i + C.CS + k;
            append(s);
            xValues[i - first] = i;
            yValues[i - first] = k;
            nuclei = (Vector)nuclei_record.elementAt(i);
        }
        double [] xx = new double[last - first];
        System.arraycopy(xValues, 0, xx, 0, last - first);
        double [] yy = new double[last - first];
        System.arraycopy(yValues, 0, yy, 0, last - first);
        //testResults(xx, yy);
        return new PlotData(xx, yy);

    }

    private class PlotData {
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

    private int countNuclei(Vector nuclei) {
        int k = 0;
        Enumeration e = nuclei.elements();
        while (e.hasMoreElements()) {
            Nucleus n = (Nucleus)e.nextElement();
            if (n.status < 1) continue;
            if (n.identity.indexOf("polar") >= 0) continue;
            k++;
        }
        return k;
    }


    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();

        //check user input
        int ending_index = iNucleiMgr.getNucConfig().getEndingIndex();
        try {
            if(Integer.parseInt(iTextField_endTime.getText()) > ending_index) {
                System.out.println("Integer entered is larger than the Ending Index, automatically reset to Ending Index");
                iTextField_endTime.setText("" + ending_index);
            }
        } catch (Exception err) {
            System.out.println("non integer text is entered, automatically reset to Ending Index");
            iTextField_endTime.setText("" + ending_index);
        }
        //end check user input

        if (s.equals(TEST1)) {
            append(TEST1);
            counts();
        } else if (s.equals(TEST2)) {
            append(TEST2);
            sizes();
        } else if (s.equals(TEST3)) {
            append(TEST3);
            //test3();
        } else if (s.equals(TEST4)) {
            append(TEST4);
            //test4();
        } else if (s.equals(CLEAR)) {
            append(TEST5);
            iText.setText("");
        } else super.actionPerformed(e);
    }

    private static final String
         CLEAR = "Clear"
        ,LINE  = "                                        "
        ,ANGLE = "Angle"
        ,TEST1 = "Counts"
        ,TEST2 = "Sizes"
        ,TEST3 = "Test3"
        ,TEST4 = "Test4"
        ,TEST5 = "Test5"
        ;

    public static void main(String[] args) {
    }


}
