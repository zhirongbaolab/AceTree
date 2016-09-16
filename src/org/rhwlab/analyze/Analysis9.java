/*
 * Created on Nov 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.tree.CellData;
import org.rhwlab.utils.C;
import org.rhwlab.utils.HeatMap;
import org.rhwlab.utils.HeatMapFrame;
import org.rhwlab.utils.HeatMapPanel;
import org.rhwlab.utils.Log;

/**
 * Tools for displaying red expression and making it available
 * as a tab delimited file for use in external programs.
 *
 * @author biowolp
 * @version 1.1 November 30, 2005
 *
 */
public class Analysis9 extends Log implements Comparator {

    private JTextField iTextField1;
    private JTextField iTextField2;
    private JTextField iTextField3;
    //private JCheckBox iCheckBox;
    AceTree         iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    //Identity        iIdentity;
    Font []         iFont;
    boolean         iPrintHeader;

    public Analysis9(String title) {
        super(title);
        showMe();
        buildOutToolBar();
        initialize();
        iPrintHeader = true;

    }



    @SuppressWarnings("unused")
	private void test1() {
        initialize();
        int first = Integer.parseInt(iTextField1.getText());
        int last = Integer.parseInt(iTextField2.getText());
        Vector nuclei = (Vector)nuclei_record.elementAt(last - 1);
        Collections.sort(nuclei, this);
        Nucleus n;
        String founder = iTextField3.getText();
        int total = 0;
        Vector all = new Vector();
        Vector allFounders = new Vector();
        for (int j=0; j < nuclei.size(); j++) {
            n = (Nucleus)nuclei.elementAt(j);
            if (n.status == Nucleus.NILLI) continue;
            if (n.identity.indexOf(founder) != 0) continue;
            Vector one = new Vector();
            processLeaf(n.identity, first, last, iPrintHeader, one);
            all.add(one);
            allFounders.add(n.identity);
            iPrintHeader = false;
        }
        showAsHeatMap(all, allFounders, founder);


    }

    @SuppressWarnings("unused")
	private void showAsHeatMap(Vector all, Vector allFounders, String baseFounder) {
        //System.out.println("showAsHeatMap entered");
        int rows = allFounders.size();
        int cols = ((Vector)all.elementAt(0)).size();
        int [][] data = new int[rows][cols];
        String [] labels = new String[rows];
        for (int i=0; i < all.size(); i++) {
            //System.out.println("evaluateAll: " + i);
            Vector one = (Vector)all.elementAt(i);
            for (int j = 0; j < one.size(); j++) {
                CellData cd = (CellData)one.elementAt(j);
                //System.out.println("evaluateAll: " + i + C.CS +j + C.CS +cd.iRweight);
                data[i][j] = cd.iNucleus.rweight;
            }
            labels[i] = (String)allFounders.elementAt(i);
        }
        HeatMap hm = new HeatMap(data, labels, 25000, 100000);
        HeatMapPanel hmp = new HeatMapPanel(hm, 400, 200);
        HeatMapFrame hmf = new HeatMapFrame(baseFounder, hm, 400, 200, 1);
    }

    private void evaluateAll(Vector all) {
        System.out.println("evaluateAll entered");
        for (int i=0; i < all.size(); i++) {
            System.out.println("evaluateAll: " + i);
            Vector one = (Vector)all.elementAt(i);
            for (int j = 0; j < one.size(); j++) {
                CellData cd = (CellData)one.elementAt(j);
                System.out.println("evaluateAll: " + i + C.CS +j + C.CS +cd.iNucleus.rweight);
            }
        }
    }

    private void test2() {
        initialize();
        int first = Integer.parseInt(iTextField1.getText());
        int last = Integer.parseInt(iTextField2.getText());
        Vector nuclei = (Vector)nuclei_record.elementAt(last - 1);
        Collections.sort(nuclei, this);
        Nucleus n;
        int total = 0;
        for (int j=0; j < nuclei.size(); j++) {
            n = (Nucleus)nuclei.elementAt(j);
            if (n.status == Nucleus.NILLI) continue;
            processLeaf(n.identity, first, last, total++ == 0, null);
        }

    }

    private void processLeaf(String founder, int start, int end, boolean showHeader, Vector clone) {
        //System.out.println("processLeaf: " + founder);
        Cell c = (Cell)iCellsByName.get(founder);
        if (c == null) {
            append("BAD ANALYSIS9 PARAMETERS");
            return;
        }
        Vector v = c.getAllCellData(start, end);

        int [] length = new int[1];
        String s = getContents(v, length);
        if (showHeader) {
            String header = founder;
            for (int i=start; i < start + length[0]; i++) {
                header += C.TAB + i;
            }
            append(header);
        }
        append(founder + s);
        clone.addAll(v);

    }

    private String getContents(Vector v, int [] length) {
        int k = v.size();
        length[0] = k;
        String s = "";
        for (int i=0; i < k; i++) {
            Object o = v.elementAt(i);
            s += C.TAB + (((CellData)o).iNucleus.rweight - 35000);
        }
        return s;
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
            test1();
        } else if (s.equals(TEST2)) {
            test2();
        } else if (s.equals(CLEAR)) {
            append("clear");
            iText.setText("");
            iPrintHeader = true;
        } else super.actionPerformed(e);
    }


    private void buildOutToolBar() {
        iToolBar.setPreferredSize(new Dimension(500,40));
        iToolBar.add(new JLabel("time:"));
        iTextField1 = new JTextField();
        iTextField1.setColumns(15);
        iTextField1.setText("1");
        iToolBar.add(iTextField1);
        iTextField2 = new JTextField();
        iTextField2.setColumns(15);
        iTextField2.setText("100");
        iToolBar.add(iTextField2);
        iTextField3 = new JTextField();
        iTextField3.setColumns(15);
        iTextField3.setText("MS");
        iToolBar.add(iTextField3);
        //iCheckBox = new JCheckBox("show header");
        //iToolBar.add(iCheckBox);
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

    private static final String
    CLEAR = "Clear"
   ,TEST1 = "One"
   ,TEST2 = "All"
   ,TEST3 = "Test3"
   ;

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
	public int compare(Object o1, Object o2) {
        Nucleus n1 = (Nucleus)o1;
        Nucleus n2 = (Nucleus)o2;

        return n1.identity.compareTo(n2.identity);
    }


}
/*
private void test3() {
int q = 0;
long start = System.currentTimeMillis();
Vector circles = new Vector();
int k = Integer.parseInt(iTextField2.getText());
Vector nuclei = (Vector)nuclei_record.elementAt(k - 1);
Collections.sort(nuclei, this);
Nucleus n;
String founder = iTextField1.getText();
boolean headerPrinted = false;
//appendx(founder);
//for (int i=k; i >0; i--) appendx(C.TAB + i);
//append("");
//System.out.println("checked: " + iCheckBox.isSelected());
int [] count = new int[1];
int total = 0;
for (int j=0; j < nuclei.size(); j++) {
    n = (Nucleus)nuclei.elementAt(j);
    if (n.status == Nucleus.NILLI) continue;
    if (n.identity.indexOf(founder) != 0) continue;
    String s = n.identity;
    //appendx(s);
    Vector strings = new Vector();
    Vector headers = new Vector();
    //strings.add(s);
    //boolean cellNameAdded = false;
    for (int m=founder.length(); m <= n.identity.length(); m++) {
        String cname = n.identity.substring(0,m);
        //System.out.println("inquiry: " + cname);
        Cell c = (Cell)iCellsByName.get(cname);
        //String ss = c.getReverseRedDataString(0, k, 1, count);
        String ss = c.getRedDataString(0, k, 1, count);
        //if (!cellNameAdded) {
        //    cellNameAdded = true;
        //    ss = n.identity + ss;
        //}
        total += count[0];
        strings.add(ss);
        if (iCheckBox.isSelected()) headers.add(0, c.getReverseRedDataHeaderString());
    }
    if (!headerPrinted) {
        headerPrinted = true;
        System.out.println("total: " + total + C.CS + k);
        appendx(founder);
        //for (int i=k; i > (k-total); i--) appendx(C.TAB + i);
        for (int i=k - total + 1; i <= k; i++) appendx(C.TAB + i);
        append("");

    }
    //Enumeration e = strings.elements();
    for (int i=0; i < strings.size(); i++) {
        String s2 = (String)strings.elementAt(i);
        if (i == 0) s2 = n.identity + s2;
        appendx(s2);
    }
    append("");
    if (iCheckBox.isSelected()) {
        appendx(n.identity);
        Enumeration e = headers.elements();
        while (e.hasMoreElements()) {
            String s2 = (String)e.nextElement();
            appendx(s2);
        }

        append("");
    }
    //System.out.println("q=" + q);
    //iText.setFont(iFont[q]);
    //append(s);

}

}

*/