package org.rhwlab.nucedit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.acetree.AceTreeNoUI;
import org.rhwlab.acetree.NucUtils;
import org.rhwlab.dbaccess.EmbryoXML;
import org.rhwlab.snight.Config;
import org.rhwlab.snight.DivisionCaller;
import org.rhwlab.snight.Identity3;
import org.rhwlab.snight.MeasureCSV;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;

public class Orientation extends JDialog implements ActionListener, ListSelectionListener, Comparator {

	String 				iTitle;
	JToolBar			iToolBar;
	JFrame				iFrame;
	JTextField			iTextField;
	JTextField			iTextField2;
	JTextField			iTextField3;
	DefaultListModel	iListModel;
	JList				iCellList;
	JScrollPane			iScrollPane;
	AceTree				iAceTree;
	NucleiMgr			iNucleiMgr;
	Vector				nuclei_record;
	AncesTree			iAncesTree;
	Hashtable			iCellsByName;
	Cell				iRoot;
	int					iLastTime;
	Vector				iSortedCellNames;
    boolean             iNamesAvailable;
    int					iTest;
    int					iTime;
    DivisionCaller		iDivisionCaller;
    double				iCutoff;
    String				iSeries;


    public Orientation(AceTreeNoUI acenui) {
    	iNucleiMgr = acenui.getNucleiMgr();
        nuclei_record = iNucleiMgr.getNucleiRecord();
        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        //iIdentity = iNucleiMgr.getIdentity();
        //iRoot = iNucleiMgr.getRoot();
        iRoot = acenui.getRoot();
        makeSortedCellNames();
        iLastTime = estimate350CellStage();

        Identity3 identity = iNucleiMgr.getIdentity();
        String axis = identity.getAxis();
        double zpixres = iAceTree.getConfig().getNucleiConfig().getZPixRes();
        MeasureCSV measureCSV = iNucleiMgr.getMeasureCSV();
//        iDivisionCaller = new DivisionCaller(axis, zpixres, measureCSV);
        iDivisionCaller = new DivisionCaller(measureCSV, axis, zpixres);
        iCutoff = 1;
    }

    public Orientation() {
        iTitle = "Orientation";
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        setContentPane(p);
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,0));
        p.add(iToolBar);
        buildOutToolBar();
        buildList(p);
        initialize();
    	int k = 40;
        try{
        	// EmbryoXML.java takes iSeries as the embryo name and tags on the .xml (if not already there)
    		EmbryoXML exml = new EmbryoXML(iSeries);
    		String editedTP = exml.iRecord[EmbryoXML.EDITEDTP];
    		k = Integer.parseInt(editedTP);
    	} catch (FileNotFoundException fnfe) {
    		System.out.println("Orientation FileNotFoundException: "+fnfe);
    		fnfe.printStackTrace();
    	} catch (NumberFormatException nfe) {

    	} catch (NullPointerException npe) {
    		System.out.println("NullPointerException in Orientation.java<init>.");
    	}
    	iTextField.setText(String.valueOf(k));

        setVisible(true);
        pack();
        System.out.println("Orientation successful.");
	}

    public DivisionCaller getDivisionCaller() {
    	return iDivisionCaller;
    }

	void prepareAndFillList() {
		fillList();
	}

    @SuppressWarnings("unused")
	void fillList() {
        initialize();
        iNamesAvailable = false;
        iTest = 1;
        NucUtils.setZPixRes(iAceTree.getConfig().getNucleiConfig().getZPixRes());

        String [] sa = iTextField.getText().split(":");
        int maxTime = Integer.parseInt(sa[0]);

        int t1 = Integer.parseInt(iTextField2.getText());
        int t2 = Integer.parseInt(iTextField.getText());
        iCutoff = Double.parseDouble(iTextField3.getText());

        iListModel.clear();
        iListModel.addElement(help());
        Vector v = new Vector();
        for (int i = t1; i <= t2; i++) {
            showDivisions(i, v);
        }
        Collections.sort(v, this);
        for (int i=0; i < v.size(); i++) {
            iListModel.addElement(v.get(i));

        }
        if (!this.isShowing()) this.showMe();
        iNamesAvailable = true;
        iNamesAvailable = true;

    }



    public void showDivisions(int now, Vector v) {
        //println("showDivisions, " + now);
    	//Vector v = new Vector();
        iNamesAvailable = false;
        iTest = 1;
        Vector nucleiNow = iNucleiMgr.getNucleiRecord().elementAt(now - 1);
        Nucleus nnow = null;
        for (int j=0; j < nucleiNow.size(); j++) {
            nnow = (Nucleus)nucleiNow.elementAt(j);
            if (nnow.status <= 0) continue;
            if (nnow.successor1 > 0  && nnow.successor2 > 0) {
                Vector nucleiNext = iNucleiMgr.getNucleiRecord().elementAt(now);
                Nucleus a = (Nucleus)nucleiNext.elementAt(nnow.successor1 - 1);
                Nucleus p = (Nucleus)nucleiNext.elementAt(nnow.successor2 - 1);
                double score1 = evaluateDivision1(nnow, a, p, now);
                double score2 = Math.abs(score1);
                //double score2 = evaluateDivision2(nnow, a, p, now);
                if (score1 == 999 && score1 == 999) continue;
                if (score2 > iCutoff) continue;
                v.add(division(nnow.identity, a.identity, p.identity, now, score1));
            }
        }
    }

	@Override
	public int compare(Object arg0, Object arg1) {
		String s0 = (String)arg0;
		String s1 = (String)arg1;
		String [] sa0 = s0.split(CS);
		String [] sa1 = s1.split(CS);
		double d0 = Double.parseDouble(sa0[4]);
		double d1 = Double.parseDouble(sa1[4]);
		int r = 0;
		if (Math.abs(d0) < Math.abs(d1)) r = -1;
		else r = 1;
		return r;
	}



    public String division(String par, String dau, String dau2, int now, double score1) {
        StringBuffer sb = new StringBuffer(par);
        sb.append(CS + now);
        sb.append(CS + dau);
        sb.append(CS + dau2);
        sb.append(CS + DF4.format(score1));
        String r = iDivisionCaller.getRuleString(par);
        if (r.length() > 0) sb.append(CS + "           (" + r + ")");
        //println("division, " + sb.toString());
        return sb.toString();
    }

    double evaluateDivision1(Nucleus parent, Nucleus dau1, Nucleus dau2, int now) {
    	return iDivisionCaller.getDotProduct(parent, dau1, dau2);

    }

    public void initialize() {
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        nuclei_record = iNucleiMgr.getNucleiRecord();
        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        //iIdentity = iNucleiMgr.getIdentity();
        //iRoot = iNucleiMgr.getRoot();
        iRoot = iAceTree.getRoot();
        makeSortedCellNames();
        iLastTime = estimate350CellStage();

        Identity3 identity = iNucleiMgr.getIdentity();
        String axis = identity.getAxis();
        System.out.println("Initializing axis in Orientation.java: " + axis);
        double zpixres = iAceTree.getConfig().getNucleiConfig().getZPixRes();
        MeasureCSV measureCSV = iNucleiMgr.getMeasureCSV();
//        iDivisionCaller = new DivisionCaller(axis, zpixres, measureCSV);
        iDivisionCaller = new DivisionCaller(measureCSV, axis, zpixres);

        Config config = iAceTree.getConfig();
        String shortName = config.getShortName();

        // Not sure what the orientation window is supposed to do but it works with this series name
        String series = shortName.substring(0, shortName.indexOf("."));
        iSeries = series;
        
        println("initialize, " + series + CS + shortName);
    }


    private void buildList(JPanel jp) {
        //Create the list and put it in a scroll pane.
        iListModel = new DefaultListModel();
        iCellList = new JList(iListModel);
        iCellList.setFont(new Font("courier", Font.PLAIN, 16));
        //iCellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        iCellList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        iCellList.setSelectedIndex(0);
        iCellList.addListSelectionListener(this);
        iCellList.setVisibleRowCount(10);

        iScrollPane = new JScrollPane(iCellList);
        jp.add(iScrollPane);
        iListModel.addElement(help());
    }

    private void buildOutToolBar() {
        iToolBar.setMaximumSize(new Dimension(700,20));
        iToolBar.add(new JLabel("start/end/cutoff"));
        iTextField2 = new JTextField();
        iTextField2.setColumns(5);
        iTextField2.setText("1");
        iToolBar.add(iTextField2);
        iTextField = new JTextField();
        iTextField.setColumns(5);
        iTextField.setText("40");
        iToolBar.add(iTextField);
        iTextField3 = new JTextField();
        iTextField3.setColumns(5);
        iTextField3.setText("0.5");
        iToolBar.add(iTextField3);
        JButton jb = null;
        jb = new JButton(CLEAR);
        addToolBarButton(jb);
        jb = new JButton("Divisions");
        addToolBarButton(jb);
        //jb = new JButton("Movements");
        //addToolBarButton(jb);
    }


    protected void addToolBarButton(AbstractButton ab) {
        ab.addActionListener(this);
        iToolBar.add(ab);
    }


	public void showMe() {
        //iFrame = new JFrame(iLog.iTitle);
        //iFrame.setTitle("Deaths and Adjacencies Dialog");
        iFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        iFrame.setContentPane(this);
        iFrame.pack();
        iFrame.setVisible(true);
    }



    @SuppressWarnings("unused")
	private void makeSortedCellNames() {
        iSortedCellNames = new Vector();
        Enumeration e = iCellsByName.keys();
        int i = 0;
        while (e.hasMoreElements()) {
            iSortedCellNames.add (e.nextElement());
        }
        Collections.sort(iSortedCellNames);
    }


    @SuppressWarnings("unused")
	private int estimate350CellStage() {
        int stage = 355;
        int r = 0;
        int size = 0;
        int time = 0;
        int i = 0;
        for (; i < nuclei_record.size(); i++) {
            Vector nuclei = (Vector)nuclei_record.get(i);
            int t = nuclei.size();
            if (t > size) {
                size = t;
                time = i;
            }
            int count = 0;
            if (size > stage) {
                count = 0;
                for (int j=0; j < nuclei.size(); j++) {
                    Nucleus n = (Nucleus)nuclei.get(j);
                    if (n.status > 0 && (!n.identity.startsWith("N"))) count++;

                }
            }
            if (count > stage) break;

        }


        return (time + 1);
    }



	@Override
	public void valueChanged(ListSelectionEvent e) {
        if (!iNamesAvailable) return;
        if (e.getValueIsAdjusting() == false) {
            int index = iCellList.getSelectedIndex();
            int k = Math.max(index, 1);
            String s = (String)iListModel.elementAt(k);
            if (s == null) return;
            if (s.charAt(0) == '#') return;
            String [] sa = s.split(CS);
            String name = "";
            name = sa[0];
            iTime = Integer.parseInt(sa[1]);
            bringUpImageWindow(name);

        }

	}

    private void bringUpImageWindow(String name) {
        Vector v = new Vector();
        v.add("InputCtrl1");
        v.add(String.valueOf(iTime));
        v.add(name);
        iAceTree.forceTrackingOn();
        iAceTree.controlCallback(v);
        iAceTree.setFocusHome();
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Divisions")) { //first list
        	prepareAndFillList();
        } else if (s.equals("Movements")) { //second list
        	//prepareZaferMovements();
        } else if (s.equals(CLEAR)) { //Clear
            iNamesAvailable = false;
            iListModel.clear();
            iListModel.addElement(help());
        }


	}

	private String help() {
        return "### DIVISIONS: dividing cell, time, dau1, dau2, dotProduct";
    }


	private static final String
		 CLEAR = "CLEAR"
		;



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private static void println(String s) {System.out.println(s);}
    private static void print(String s) {System.out.print(s);}
    private static final String CS = ", ", C = ",", SP = " ";
    private static final String TAB = "\t";
    private static final DecimalFormat DF0 = new DecimalFormat("####");
    private static final DecimalFormat DF1 = new DecimalFormat("####.#");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt4(double d) {return DF4.format(d);}
    private static String fmt1(double d) {return DF1.format(d);}
    private static String fmt0(double d) {return DF0.format(d);}


}
