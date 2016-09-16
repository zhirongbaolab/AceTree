/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 * Created on Jul 6, 2005
 */
package org.rhwlab.analyze;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.acetree.NucUtils;
import org.rhwlab.snight.Identity3;
import org.rhwlab.snight.Loc;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.C;
import org.rhwlab.utils.ConfigFileList;
import org.rhwlab.utils.Line;
import org.rhwlab.utils.Log;

/**
 * Collects data for developing canonical naming rules.
 * UI offers:
 * Get data
 * Show rules
 * It seems that this version does not take care of normalizing
 * locations according to (slightly) varying image sizes.
 * Such an effect may have been contemplated in Anslysis6
 *
 * @author biowolp
 * @version 1.1 November 30, 2005
 *
 */
public class Analysis4 extends Log {
    AceTree         iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    Identity3        iIdentity;
    Line            iLine;
    private double     iXA; // for angle()
    private double     iYA;
    private double     iZA;
    private JTextField iTextField;
    private Hashtable   iNamingHash;
    private Hashtable   iCompletedCellsHash;
    private char        iTag;
    private int         iSampleIndex;
    private String      iKeyCell;
    private String      iStartingCell;
    private int         iBreakout;

    private Hashtable []    iArrayOfHashes;
    private Hashtable       iCurrentHash;
    private Hashtable       iNucleiMgrHash;
    private ConfigFileList  iConfigFileList;
    private String          iConfigFileName;

    public Analysis4(String title) {
        super(title);
        showMe();
        buildOutToolBar();
        initialize();
        getNamingHash();
        iSampleIndex = 0;
        iText.setFont(new Font("courier", Font.PLAIN, 12));


    }
    private void buildOutToolBar() {
        iToolBar.setMaximumSize(new Dimension(500,20));
        iToolBar.add(new JLabel("prefix:"));
        iTextField = new JTextField();
        iTextField.setColumns(15);
        iTextField.setText("ABal");
        iToolBar.add(iTextField);
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
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        nuclei_record = iNucleiMgr.getNucleiRecord();
        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        iIdentity = iNucleiMgr.getIdentity();
        //iRoot = iNucleiMgr.getRoot();
        iRoot = iAncesTree.getRoot();
        iNucleiMgrHash = iAceTree.getNucleiMgrHash();

    }

    //show rules
    private void test2() {
        append("Analysis4.test2 entered");
        Enumeration e = iNamingHash.keys();
        Vector v = new Vector();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String rule = (String)iNamingHash.get(key);
            if (rule.length() < 3) continue;
            String s = key + C.CS + rule;
            v.add(s);
        }
        Collections.sort(v);
        e = v.elements();
        while (e.hasMoreElements()) {
            String s = (String)e.nextElement();
            append(s);
        }
    }

    // get identity data
    private void test1() {
        append("Analysis4.test1 entered");
        //System.out.println("\nAnalysis5.test1 entered");
        //String base = "/home/biowolp/data/";
        getNamingHash();
        //Vector configs = iConfigFileList.getConfigFiles();
        //if (configs == null) return;
        iArrayOfHashes = new Hashtable[iNucleiMgrHash.size()];
        initialize();
        Enumeration e = iNucleiMgrHash.keys();
        int i = -1;
        while(e.hasMoreElements()) {
            i++;
            String config = ((String)e.nextElement());
            iConfigFileName = config;
            NucleiMgr nucMgr = (NucleiMgr)iNucleiMgrHash.get(config);
            iAncesTree = nucMgr.getAncesTree();
            iCellsByName = iAncesTree.getCellsByName();

            iCurrentHash = new Hashtable();
            iArrayOfHashes[i] = iCurrentHash;
            //iAceTree.clearTree();
            //String name = (String)configs.elementAt(i);
            append("test2 using: " + config);
            //System.out.println("\n\nloop: " + i + C.CS + config);
            //iAceTree.setConfigFileName(name);
            //iAceTree.bringUpSeriesUI(name);
            /*
            boolean haveConfig = iAceTree.getStartingParms();
            if (haveConfig) {
                iAceTree.readNuclei();
            } else {
                System.out.println("no config file");
                System.exit(10);
            }
            iAceTree.clearTree();
            iAceTree.buildTree(true);
            */
            extendRuleData(nucMgr);
        }
        processArrayOfHashes();
    }


    @SuppressWarnings({ "unused", "resource" })
	private void getNamingHash() {
        File f = new File("namesHash.txt");
        iNamingHash = new Hashtable();
        String s = null;
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedReader  br = new BufferedReader(new InputStreamReader(fis));
            int i = 0;
            while (br.ready()) {
                s = br.readLine();
                System.out.println("getNamingHash: " + s);
                String [] sa = s.split(",");
                iNamingHash.put(sa[0], sa[1]);
                //append(sa[0] + "," + iNamingHash.get(sa[0]));
            }
        } catch(FileNotFoundException fnfe) {
            append("file not found: namesHash.txt");
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /*
    private void fileSplitOnWhiteSpaceTest() {
        File f = new File("ruleData.txt");
        String s = null;
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedReader  br = new BufferedReader(new InputStreamReader(fis));
            int i = 0;
            while (br.ready()) {
                s = br.readLine();
                String [] sa = s.split("\\s+");
                if (sa.length > 1) append(String.valueOf(sa.length) + C.CS + sa[5]);
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }


    }
    */

    @SuppressWarnings("resource")
	private void fileAppend(String s) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("ruleData.txt", true);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return;
        }
        PrintWriter pw = new PrintWriter(fos, true);
        pw.println(s);

    }



    @SuppressWarnings("unused")
	public void extendRuleData(NucleiMgr nucMgr) {
        System.out.println("extendRuleData");
        //initialize();
        Vector  nucleirecord = nucMgr.getNucleiRecord();
        iCompletedCellsHash = new Hashtable();
        //int [] lineage_ct_p = new int[1];
        //lineage_ct_p[0] = 1;
        //int lin_ct = lineage_ct_p[0];
        int start[] = new int[1];
        start[0] = 1;
        int tp_number = nucMgr.getMovie().tp_number;
        int rotate_axis = 1;
        int nuc_ct = 0;
        int t;
        int i;
        Vector nuclei = null;
        Vector nuclei_prev = null;
        Vector nuclei_next = null;
        iBreakout = 0;
        //lin_ct = lineage_ct_p[0];
        int iEndingIndex = nucMgr.getEndingIndex();
        for (i = start[0]; i < iEndingIndex - 1; i++) {
            if (iBreakout > 0) {
                System.out.println("extendRuleData loop exiting on breakout = " + iBreakout);
                break;
            }
            nuclei = (Vector)nucleirecord.elementAt(i);
            nuc_ct = nuclei.size();
            if (i > 0) nuclei_prev = (Vector)nucleirecord.elementAt(i - 1);
            if (i < tp_number - 1) nuclei_next = (Vector)nucleirecord.elementAt(i + 1);

            if (rotate_axis > 0 && nuc_ct > Identity3.EARLY) {
                //iIdentity.rotateAxis();
                rotate_axis = 0;
            }
            Nucleus parent = null;
            Vector nextNuclei = (Vector)nucleirecord.elementAt(i + 1);
            for (int j = 0; j < nuc_ct; j++) {
                if (iBreakout > 0) {
                    System.out.println("extendRuleData loop exiting on breakout");
                    break;
                }
                parent = (Nucleus)nuclei.elementAt(j);
                if (parent.status == Nucleus.NILLI) continue;
                String pname = parent.identity;
                if (!iNamingHash.containsKey(pname)) continue;
                boolean good = (parent.successor1 > 0 && parent.successor2 > 0);
                if (!good) continue;
                // this canonical parent is dividing
                //append("considering: " + pname);
                if (!parentRelevant(pname)) {
                    //System.out.println("not relevantParent: " + pname);
                    continue;
                }
                append(String.valueOf(i) + C.CS + String.valueOf(j)); //##########

                Nucleus dau1 = (Nucleus)nextNuclei.elementAt(parent.successor1 - 1);
                Nucleus dau2 = (Nucleus)nextNuclei.elementAt(parent.successor2 - 1);
                String prule = (String)iNamingHash.get(pname);
                if (prule.length() >= 2) {
                    String line = processDividingCell(i, parent, nucMgr);
                    append(line); //###
                    iCurrentHash.put(pname, line);
                }
            }
        }
        System.out.println("extendRuleData exiting");

    }

    private boolean parentRelevant(String pname) {
        boolean rtn = true;
        rtn = rtn && !pname.equals("AB");
        rtn = rtn && !pname.equals("ABa");
        rtn = rtn && !pname.equals("ABp");
        rtn = rtn && !pname.equals("EMS");
        rtn = rtn && !pname.equals("P2");
        rtn = rtn && !pname.equals("P3");
        rtn = rtn && !pname.equals("P1");
        rtn = rtn && !pname.equals("Z2");
        rtn = rtn && !pname.equals("Z3");
        return rtn;
    }

    private void processArrayOfHashes() {
        Enumeration e = iNamingHash.keys();
        Vector v = new Vector();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String rule = (String)iNamingHash.get(key);
            if (rule.length() < 3) continue;
            String s = key + C.CS + rule;
            v.add(s);
        }
        Collections.sort(v);
        e = v.elements();
        while (e.hasMoreElements()) {
            String s = (String)e.nextElement();
            fileAppend("data for: " + s);
            s = s.substring(0, s.indexOf(','));
            for (int i=0; i < iArrayOfHashes.length; i++) {
                Object o = iArrayOfHashes[i].get(s);
                //System.out.println("hashreadout: " + C.CS + i + C.CS + s + C.CS + o);
                if (o != null) fileAppend((String) o);
            }
            fileAppend("\n");
        }

    }


    private String getConfigFileInfo(String longName) {
        String s = longName.substring(longName.lastIndexOf('/') + 1);
        s = s.substring(0, s.indexOf('.'));
        return s;
    }

    private String processDividingCell(int time, Nucleus n, NucleiMgr nucMgr) {
        //System.out.println("processDividingCell: " + n.identity + C.CS + time);
        //new Throwable().printStackTrace();
        iLine = new Line();
        String s = getConfigFileInfo(iConfigFileName);
        if (s.length() <= 6) iLine.setGap(3);
        iLine.add(s);
        iLine.setGap(0);
        //append(time + C.CS + n.toString());
        Vector nucleirecord = nucMgr.getNucleiRecord();
        int liveCells = NucUtils.countLiveCells((Vector)nucleirecord.elementAt(time));
        //System.out.println("liveCells: " + liveCells);
        iLine.setGap(3);
        iLine.add(liveCells);
        iLine.setGap(0);
        int kd1 = n.successor1 - 1;
        int kd2 = n.successor2 - 1;
        //append("kd1, kd2: " + kd1 + C.CS + kd2);
        Nucleus nd1 = null;
        Nucleus nd2 = null;;
        //for (int i=time+1; i <= time+5 && i < iNucleiMgr.getEndingIndex(); i++) {
        for (int i = 1; i <= 5 && i < nucMgr.getEndingIndex() - time; i++) {
            int k = i + time;
            Vector nuclei = (Vector)nucleirecord.elementAt(k);
            try {
                nd1 = (Nucleus)nuclei.elementAt(kd1);
                nd2 = (Nucleus)nuclei.elementAt(kd2);
                //append(i + C.CS + nd1.toString());
                //append(i + C.CS + nd2.toString());
            } catch(ArrayIndexOutOfBoundsException aiob) {
                System.out.println("ArrayIndexOutOfBounds: " + time + C.CS + i + C.CS + kd1 + C.CS + kd2);
                System.out.println(n);
                System.out.println(nd1);
                System.out.println(nd2);
                break;
            }
            //if (i == 1) locateNextRoundOfDivision(nd1, nd2, nucMgr);
            //if (i != 4) processPair(nd1, nd2, nucMgr); // just show 1,2,3,5
            processPair(nd1, nd2, nucMgr);
            kd1 = nd1.successor1 - 1;
            kd2 = nd2.successor1 - 1;
        }
        iLine.add(n.identity);
        iLine.add(time + 1);
        //iLine.add(nd1.identity);
        //iLine.add(nd2.identity);
        return iLine.iBuf.toString();
    }

    private void processPair(Nucleus nd1, Nucleus nd2, NucleiMgr nucMgr) {
        int avg = (nd1.size + nd2.size)/Identity3.DIVISOR;
        Loc nd1L = new Loc(nd1, nucMgr);
        Loc nd2L = new Loc(nd2, nucMgr);
        int x = 100*(nd1L.x - nd2L.x)/avg;
        int y = 100*(nd1L.y - nd2L.y)/avg;
        int z = 100*(nd1L.z - nd2L.z)/avg;
        iLine.add(x);
        iLine.add(y);
        iLine.setGap(3);
        iLine.add(z);
        iLine.setGap(0);
    }

    private void locateNextRoundOfDivision(Nucleus nd1, Nucleus nd2, NucleiMgr nucMgr) {
        String name1 = nd1.identity;
        String name2 = nd2.identity;
        //System.out.println("locateNextRoundOfDivision: " + name1 + C.CS + name2);
        Cell d1 = (Cell)iCellsByName.get(name1);
        Cell d2 = (Cell)iCellsByName.get(name2);

        int t1 = d1.getEndTime();
        int t2 = d2.getEndTime();
        int k = Math.min(t1, t2) - 1;
        //append(k + C.CS + name1 + C.CS + name2);
        Vector nucleirecord = nucMgr.getNucleiRecord();
        Vector nuclei = (Vector)nucleirecord.elementAt(k);
        Nucleus n1 = null;
        Nucleus n2 = null;
        for (int i=0; i < nuclei.size(); i++) {
            Nucleus n = (Nucleus)nuclei.elementAt(i);
            //System.out.println("locateN: " + n);
            if (n.identity.equals(name1)) n1 = n;
            else if (n.identity.equals(name2)) n2 = n;
        }
        //if (n1 == null || n2 == null) {
        //    System.out.println("both nuclei not found: " + name1 + C.CS + name2);
        //    return;
        //}
        //if (name1.equals("ABpla")) {
        //    System.out.println("locateN n1=" + n1);
        //   System.out.println("locateN n2=" + n2);
         //   System.exit(1);
        //}
        processPair(n1, n2, nucMgr);
    }

    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals(TEST1)) {
            append(TEST1);
            test1();
        } else if (s.equals(TEST2)) {
            append(TEST2);
            test2();
        } else if (s.equals(CLEAR)) {
            append("clear");
            iText.setText("");
        } else super.actionPerformed(e);
    }

    private static final String
         CLEAR = "Clear"
        ,LINE  = "                                        "
        ,ANGLE = "Angle"
        ,TEST1 = "GetData"
        ,TEST2 = "ShowRules"
        ,TEST3 = "Test3"
        ,TEST4 = "Test4"
        ,TEST5 = "Test5"
        ;

    public static void main(String[] args) {
    }

}


/*
private void test1() {
    String base = "/home/biowolp/data/";
    getNamingHash();
    iKeyCell = iTextField.getText();
    String rule = (String)iNamingHash.get(iKeyCell);
    String s = iKeyCell + C.CS + rule;
    fileAppend(s);
    iStartingCell = getStartingCell();
    append("starting at: " + iStartingCell);
    iAceTree.clearTree();
    for (int i=0; i < samples.length; i++) {
    String name = base + samples[i];
    append("test1 using: " + name);
    //System.out.println("Parameteraxes: " + Parameters.apInit + C.CS + Parameters.dvInit + C.CS + Parameters.lrInit);

    iAceTree.setConfigFileName(name);
    iSampleIndex = (iSampleIndex + 1) % samples.length;
    //boolean haveConfig = iAceTree.getStartingParms();
    //if (haveConfig) {
        //iAceTree.readNuclei();
        iAceTree.bringUpSeriesUI(name);
    //} else {
    //    System.out.println("no config file");
    //    System.exit(10);
    //}
    iAceTree.buildTree(true);
    initialize();
    //System.out.println("Parameteraxes: " + Parameters.apInit + C.CS + Parameters.dvInit + C.CS + Parameters.lrInit);
    //append("Parameter axes: " + Parameters.apInit + C.CS + Parameters.dvInit + C.CS + Parameters.lrInit);
    //extendRuleData();
    if (iBreakout == 5) {
        String ss = "exiting due to bad breakout: " + iBreakout;
        System.out.println(ss);
        append(ss);
        break;
    }
    }
}
*/








/*
private void getNamingHash() {
    File f = new File("namesHash.txt");
    iNamingHash = new Hashtable();
    String s = null;
    try {
        FileInputStream fis = new FileInputStream(f);
        BufferedReader  br = new BufferedReader(new InputStreamReader(fis));
        int i = 0;
        while (br.ready()) {
            s = br.readLine();
            String [] sa = s.split(",");
            iNamingHash.put(sa[0], sa[1]);
            //append(sa[0] + "," + iNamingHash.get(sa[0]));
        }
    } catch(IOException ioe) {
        ioe.printStackTrace();
    }

}
private void fileSplitOnWhiteSpaceTest() {
    File f = new File("ruleData.txt");
    String s = null;
    try {
        FileInputStream fis = new FileInputStream(f);
        BufferedReader  br = new BufferedReader(new InputStreamReader(fis));
        int i = 0;
        while (br.ready()) {
            s = br.readLine();
            String [] sa = s.split("\\s+");
            if (sa.length > 1) append(String.valueOf(sa.length) + C.CS + sa[5]);
        }
    } catch(IOException ioe) {
        ioe.printStackTrace();
    }


}

private void fileAppend(String s) {
    FileOutputStream fos = null;
    try {
        fos = new FileOutputStream("ruleData.txt", true);
    } catch(FileNotFoundException fnfe) {
        fnfe.printStackTrace();
        return;
    }
    PrintWriter pw = new PrintWriter(fos, true);
    pw.println(s);

}



private void provideRuleData(Nucleus parent, int index) {
    String pname = parent.identity;
    String rule = (String)iNamingHash.get(pname);
    if (rule == null) return;
    if (rule.charAt(1) == '0') {
        append("setting rule flag for " + pname);
    }
    StringBuffer sb = new StringBuffer(rule);
    sb.setCharAt(1, '1');
    append("sb: " + sb.toString());
    iNamingHash.put(pname, sb.toString());
    rule = (String)iNamingHash.get(pname);
    append(pname + "," + rule);
    processDividingCell(index, parent);
}

private void nameDaughters(Nucleus parent, Nucleus dau1, Nucleus dau2) {
    dau1.identity = parent.identity + iTag;
    dau2.identity = iIdentity.replaceLastChar(dau1.identity);
}

private String getStartingCell() {
    String s = "";
    char c = iKeyCell.charAt(0);
    switch(c) {
        case 'A':
            s = iKeyCell.substring(0,4);
            break;
        case 'M':
            s = "MS";
            break;
        case 'E':
            s = iKeyCell.substring(0,2);
            break;
        default:
            s = String.valueOf(c);
    }
    return s;
}
private String [] samples = {
        "090205yy/090205yy.dat"
        ,"083105xx/083105xx.dat"
        ,"082605/082605.dat"
        ,"081905xx/081905xx.dat"
        ,"082005/082005.dat"

        "090105/090105.dat"
        ,"090605xx/090605xx.dat"
        ,"090305xx/090305xx.dat"
        ,"090205/090205.dat"


        "081905/081905.dat"
        ,"083105/083105.dat"
        ,"081305/081305.dat"
        ,"090405/090405.dat"


        "090405/090405.dat"
        ,"090105/090105.dat"
        ,"090605xx/090605xx.dat"
        ,"090305xx/090305xx.dat"
        ,"090205/090205.dat"
        ,"090205yy/090205yy.dat"
        ,"083105/083105.dat"
        ,"083105xx/083105xx.dat"
        ,"082605/082605.dat"
        ,"081905/081905.dat"
        ,"081905xx/081905xx.dat"
        ,"081305/081305.dat"
        ,"082005/082005.dat"

};


private String getConfigFileInfo(String longName) {
    String s = longName.substring(longName.lastIndexOf('/') + 1);
    s = s.substring(0, s.indexOf('.'));
    return s;
}

private void processDividingCell(int time, Nucleus n) {
    iLine = new Line();
    String s = getConfigFileInfo(iAceTree.getConfigFileName());
    if (s.length() <= 6) iLine.setGap(3);
    iLine.add(s);
    iLine.setGap(0);
    //append(time + C.CS + n.toString());
    int liveCells = NucUtils.countLiveCells((Vector)nuclei_record.elementAt(time));
    iLine.add(liveCells);
    int kd1 = n.successor1 - 1;
    int kd2 = n.successor2 - 1;
    //append("kd1, kd2: " + kd1 + C.CS + kd2);
    Nucleus nd1 = null;
    Nucleus nd2 = null;;
    for (int i=time+1; i <= time+5 && i < iNucleiMgr.getEndingIndex(); i++) {

        Vector nuclei = (Vector)nuclei_record.elementAt(i);
        try {
            nd1 = (Nucleus)nuclei.elementAt(kd1);
            nd2 = (Nucleus)nuclei.elementAt(kd2);
            //append(i + C.CS + nd1.toString());
            //append(i + C.CS + nd2.toString());
        } catch(ArrayIndexOutOfBoundsException aiob) {
            System.out.println("ArrayIndexOutOfBounds: " + time + C.CS + i + C.CS + kd1 + C.CS + kd2);
            System.out.println(n);
            System.out.println(nd1);
            System.out.println(nd2);
        }
        processPair(nd1, nd2);
        kd1 = nd1.successor1 - 1;
        kd2 = nd2.successor1 - 1;
    }
    iLine.add(n.identity);
    iLine.add(time);
    //iLine.add(nd1.identity);
    //iLine.add(nd2.identity);
    append(iLine.iBuf.toString());
    fileAppend(iLine.iBuf.toString());
}

private void processPair(Nucleus nd1, Nucleus nd2) {
    int avg = (nd1.size + nd2.size)/Identity3.DIVISOR;
    Loc nd1L = new Loc(nd1, Parameters.dvInit);
    Loc nd2L = new Loc(nd2, Parameters.dvInit);
    int x = 100*(nd1L.x - nd2L.x)/avg;
    int y = 100*(nd1L.y - nd2L.y)/avg;
    int z = 100*(nd1L.z - nd2L.z)/avg;
    iLine.add(x);
    iLine.add(y);
    iLine.add(z);
}
*/

/*
 *
public void extendRuleData() {
    System.out.println("extendRuleData entered but gutted");
    initialize();
    //getNamingHash();
    iCompletedCellsHash = new Hashtable();
    int [] lineage_ct_p = new int[1];
    lineage_ct_p[0] = 1;
    int lin_ct = lineage_ct_p[0];
    int start[] = new int[1];
    start[0] = 1;
    int tp_number = Movie.tp_number;
    int rotate_axis = 1;
    int nuc_ct = 0;
    int t;
    int i;
    Vector nuclei = null;
    Vector nuclei_prev = null;
    Vector nuclei_next = null;
    iBreakout = 0;

    //initialID(start, lineage_ct_p);
    Parameters.ap = Parameters.apInit;
    Parameters.dv = Parameters.dvInit;
    Parameters.lr = Parameters.lrInit;
    lin_ct = lineage_ct_p[0];
    int iEndingIndex = iNucleiMgr.getEndingIndex();
    for (i = start[0]; i < iEndingIndex - 1; i++) {
        //System.out.println("extendRuleData, i=" + i);
        if (iBreakout > 0) {
            System.out.println("extendRuleData loop exiting on breakout = " + iBreakout);
            break;
        }
        nuclei = nuclei_record[i];
        nuc_ct = nuclei.size();
        if (i > 0) nuclei_prev = nuclei_record[i - 1];
        if (i < tp_number - 1) nuclei_next = nuclei_record[i + 1];

        if (rotate_axis > 0 && nuc_ct > Identity3.EARLY) {
            iIdentity.rotateAxis();
            rotate_axis = 0;
        }
        Nucleus parent = null;
        Vector nextNuclei = nuclei_record[i + 1];
        for (int j = 0; j < nuc_ct; j++) {
            if (iBreakout > 0) {
                System.out.println("extendRuleData loop exiting on breakout");
                break;
            }
            parent = (Nucleus)nuclei.elementAt(j);
            if (parent.status == Nucleus.NILLI) continue;
            String pname = parent.identity;
            if (pname.indexOf(iStartingCell) != 0) continue;
            if (!iNamingHash.containsKey(pname)) {
                String s = "noncanonical parent name encountered ";
                append(s + pname);
                System.out.println( s + pname);
                iBreakout = 1;
            }
            boolean good = (parent.successor1 > 0 && parent.successor2 > 0);
            if (!good) {
                // not dividing so just extend the name
                if (parent.successor1 > 0) {
                    Nucleus n = (Nucleus)nextNuclei.elementAt(parent.successor1 - 1);
                    n.identity = pname;
                }
                continue;
            }
            // this canonical parent is dividing
            // use the evolving algorithm to try to name it
            // if there is a rule in place then the name will be canonical
            // and we can continue
            // if there is no rule we will try to name based on the
            // canonical axis. If this passes the noise test we can continue
            // if not we must output the ruleData for this cell
            // and flag this cell as needing rule data from the other series
            // then we exit and allow the next series to be processed
            append("considering: " + pname);
            Nucleus dau1 = (Nucleus)nextNuclei.elementAt(parent.successor1 - 1);
            Nucleus dau2 = (Nucleus)nextNuclei.elementAt(parent.successor2 - 1);
            boolean test = newCanonicalSisterID(parent, dau1, dau2, nuc_ct, i);
            //System.out.println("newCanonicalSisterID returning test=" + test);
            String prule = (String)iNamingHash.get(pname);
            if (pname.equals(iKeyCell)) {
                provideRuleData(parent, i);
                iBreakout = 10;
            }
            if (!test && iBreakout != 10) {
                //iBreakout = 5;
                System.out.println("WARNING, RULE FAILURE IGNORED HERE");
            }
        }
    }
    System.out.println("extendRuleData exiting");
}

    *
    *
    *
    *
    private boolean newCanonicalSisterID(Nucleus parent, Nucleus dau1, Nucleus dau2,
            int cellCount, int index) {
        String pname = parent.identity;
        String prule = (String)iNamingHash.get(pname);
        if (prule == null) {
            String x = "no rule for parent: " + pname;
            append(x);
            System.out.println(x);
            return false;
        }
        // we first try the canonical axis
        // if it passes the noise test we are done
        // if not, we look for a rule
        // if there is no rule we have a naming failure
        // if the rule fails the noise test we have a naming failure
        // but if the rule passes the noise test we are done
        // a rule consists of a different axis and a mapping onto the canonical axis
        // so, if we have a function that can handle any axis we can
        // reuse it with the "different axis"
        boolean canonicalTry = false;
        boolean ruleTry = false;

        char caxis = prule.charAt(0);
        canonicalTry = makeAxisDetermination(caxis, cellCount, dau1, dau2);
        if (!canonicalTry) {
            System.out.println("need a rule for: " + pname + " ;available rule: " + prule);
            if (prule.length() > 2) {
                //System.out.println("apply a rule: " + prule);
                caxis = prule.charAt(2);
                ruleTry = makeAxisDetermination(caxis, cellCount, dau1, dau2);
                canonicalTry = ruleTry;
                // iTag must be remapped based on the last char of the rule
                char switchTag = iTag;
                iTag = prule.charAt(3);
                switch(switchTag) {
                    case 'a':
                    case 'd':
                    case 'l':
                        nameDaughters(parent, dau1, dau2);
                        break;
                    default:
                        nameDaughters(parent, dau2, dau1);
                }
                if (!ruleTry) {
                    String x = "rule failure " + pname + C.CS + getConfigFileInfo(iAceTree.getConfigFileName());
                    append(x);
                    System.out.println(x);
                    processDividingCell(index, parent);
                }


            } else {
                String x = "*** RULE MISSING *** " + pname + C.CS + prule;
                System.out.println(x);
                //processDividingCell(index, parent);

            }
        } else {
            //System.out.println("no rule required for parent: " + pname);
        nameDaughters(parent, dau1, dau2);
    }
        return canonicalTry;
    }

    *     */

/*
// function sets iTag for the first daughter
// and returns true if the name passed the noise test
private boolean makeAxisDetermination(char caxis, int cellCount, Nucleus dau1, Nucleus dau2) {
    System.out.println("makeAxisDetermination entered but gutted");
    return false;
    int divisor = (dau1.size + dau2.size)/Identity3.DIVISOR;
    Loc dau1L = new Loc(dau1, Parameters.dvInit);
    Loc dau2L = new Loc(dau2, Parameters.dvInit);
    int value = 0;
    iTag = 'X';
    if (caxis == 'a') {
        value = (dau1L.x - dau2L.x)*100/divisor;
        //value *= Parameters.ap;
        if (value > 0) iTag = 'p';
        else iTag = 'a';
        return (Math.abs(value) > 100);
    }
    //boolean ycase = (caxis == 'd') && (cellCount < Identity3.EARLY);
    //ycase = ycase || ((caxis == 'l') && (cellCount >= Identity3.EARLY));
    boolean ycase = (caxis == 'd');
    if (ycase) {
        value = (dau1L.y - dau2L.y)*100/divisor;
        if (caxis == 'd') {
            //value *= Parameters.dv;
            if (value > 0) iTag = 'v';
            else iTag = 'd';
        } else {
            //value *= Parameters.lr;
            if (value > 0) iTag = 'r';
            else iTag = 'l';
        }
        return (Math.abs(value) > 100);
    }
    // use the z axis
    value = (dau1L.z - dau2L.z)*100/divisor;
    if (caxis == 'd') {
        //value *= Parameters.dv;
        if (value < 0) iTag = 'v';
        else iTag = 'd';
    } else {
        //value *= Parameters.lr;
        if (value > 0) iTag = 'r';
        else iTag = 'l';
    }
    return (Math.abs(value) > 100);
}
   */

