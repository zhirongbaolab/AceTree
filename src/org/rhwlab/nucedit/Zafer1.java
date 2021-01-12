package org.rhwlab.nucedit;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Collections;
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
import org.rhwlab.acetree.NucUtils;
import org.rhwlab.image.ImageAllCentroids;
import org.rhwlab.snight.Config;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.EUtils;


public class Zafer1  extends JDialog implements ActionListener, ListSelectionListener{

	String 				iTitle;
	JToolBar			iToolBar;
	JFrame				iFrame;
	JTextField			iTextField;
	JTextField			iTextField2;
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
	Hashtable			iZafer1Hash;
	Hashtable			iZafer2Hash;
    boolean             iNamesAvailable;
    int					iTest;
    int					iTime;


	public Zafer1() {
        iTitle = "Zafer1";
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        setContentPane(p);
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,0));
        p.add(iToolBar);
        buildOutToolBar();
        buildList();
        p.add(iScrollPane);
        initialize();
        setVisible(true);
        pack();


        /*
        setPreferredSize(new Dimension(700, 300));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        iToolBar = new JToolBar("");
        iToolBar.setLayout(new GridLayout(1,0));
        add(iToolBar);
        buildOutToolBar();
        buildList();
        initialize();
        iFrame = new JFrame(iTitle);
        showMe();
        */

	}


    double evaluateDivision1(Nucleus parent, Nucleus dau1, Nucleus dau2, int now) {
    	int index = parent.index;
    	String key = makeZaferKey(now, index);
    	Object o1 = iZafer1Hash.get(key);
    	if (o1 == null) 
    		return 999;
    	return ((Double)o1).doubleValue();

    }

    double evaluateDivision2(Nucleus parent, Nucleus dau1, Nucleus dau2, int now) {
    	int index = parent.index;
    	String key = makeZaferKey(now, index);
    	Object o1 = iZafer2Hash.get(key);
    	if (o1 == null) 
    		return 999;
    	return ((Double)o1).doubleValue();

    }

    private String help() {
        return "### DIVISIONS: dividing cell, dau1, dau2, time, divScore";
    }

    private String helpMovements() {
        return "### MOVEMENTS: dividing cell, dau1, dau2, time, moveScore";
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

        iListModel.clear();
        iListModel.addElement(help());
        for (int i = t1; i <= t2; i++) {
            showDivisions(i);
        }
        iNamesAvailable = true;

    }

    public void showDivisions(int now) {
        //println("showDivisions, " + now);
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
                //double score2 = evaluateDivision2(nnow, a, p, now);
                if (score1 == 999 && score1 == 999) continue;
                iListModel.addElement(division(nnow.identity, a.identity, p.identity, now, score1));
            }
        }
        if (!this.isShowing()) this.showMe();
        iNamesAvailable = true;
    }

    public String division(String par, String dau, String dau2, int now, double score1) {
        StringBuffer sb = new StringBuffer(par);
        sb.append(CS + now);
        sb.append(CS + dau);
        sb.append(CS + dau2);
        if (score1 != 999) sb.append(CS + DF4.format(score1));
        else sb.append(CS + "             ");
        return sb.toString();
    }


    @SuppressWarnings("unused")
	void fillMovementsList() {
        initialize();
        iNamesAvailable = false;
        iTest = 1;
        NucUtils.setZPixRes(iAceTree.getConfig().getNucleiConfig().getZPixRes());
        
        String [] sa = iTextField.getText().split(":");
        int maxTime = Integer.parseInt(sa[0]);

        int t1 = Integer.parseInt(iTextField2.getText());
        int t2 = Integer.parseInt(iTextField.getText());

        iListModel.clear();
        iListModel.addElement(helpMovements());
        for (int i = t1; i <= t2; i++) {
            showMovements(i);
        }
        iNamesAvailable = true;

    }

    public void showMovements(int now) {
        //println("showDivisions, " + now);
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
                //double score1 = evaluateDivision1(nnow, a, p, now);
                double score2 = evaluateDivision2(nnow, a, p, now);
                if (score2 == 999) continue;
                iListModel.addElement(movement(nnow.identity, a.identity, p.identity, now, score2));
            }
        }
        if (!this.isShowing()) this.showMe();
        iNamesAvailable = true;
    }

    public String movement(String par, String dau, String dau2, int now, double score2) {
        StringBuffer sb = new StringBuffer(par);
        sb.append(CS + now);
        sb.append(CS + dau);
        sb.append(CS + dau2);
        if (score2 != 999) sb.append(CS + DF4.format(score2));
        else sb.append(CS + "             ");
        return sb.toString();
    }


	void prepareZaferDivisions() {
		iZafer1Hash = new Hashtable();
		iZafer2Hash = new Hashtable();
		Config config = iAceTree.getConfig();
		println("prepareZaferDivisions, " + config.iParent);
		println("prepareZaferDivisions, " + config.iConfigFileName);
		
		File z1 = new File(config.iParent + "/svm_output_correct_division_calls.txt");
		File z2 = new File(config.iParent + "/svm_output_movements_called_as_divisions.txt");
		
		/*
		File z1 = new File("./svm_output_correct_division_calls.txt");
		File z2 = new File("./svm_output_movements_called_as_divisions.txt");
		*/
		
	    boolean bz1 = z1.exists();
	    boolean bz2 = z2.exists();
	    println("prepareZaferDivisions, " + bz1 + CS + bz2);
	    Hashtable h = null;
	    if (bz1) {
	    	h = iZafer1Hash;
	    	readZafer(z1, h);
	    }
	    if (bz2) {
	    	h = iZafer2Hash;
	    	readZafer(z2, h);
	    }
	    fillList();
	}

	void prepareZaferMovements() {
		iZafer2Hash = new Hashtable();
		Config config = iAceTree.getConfig();
		println("prepareZaferMovements, " + config.iParent);
		println("prepareZaferMovements, " + config.getConfigFileName());
		File z2 = new File(config.iParent + "/svm_output_movements_called_as_divisions.txt");
	    boolean bz2 = z2.exists();
	    Hashtable h = null;
	    if (bz2) {
	    	h = iZafer2Hash;
	    	readZafer(z2, h);
	    }
	    fillMovementsList();
	}

	@SuppressWarnings("resource")
	void readZafer(File f, Hashtable h) {
		try {
			FileInputStream fis = new FileInputStream(f);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			while (br.ready()) {
				String s = br.readLine();
				parseZaferLine(s, h);
				//println("readZafer, " + s);
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	void parseZaferLine(String s, Hashtable h) {
		String [] sa = s.split("\\s+");
		//println("parseZaferLine, " + sa.length);
		int time = Integer.parseInt(sa[2]);
		int index = Integer.parseInt(sa[6]);
		double score = Double.parseDouble(sa[10]);
		//println("parseZaferLine, time, index, score, " + time + CS + index + CS + score);
		String key = makeZaferKey(time, index);
		h.put(key, new Double(score));

	}

	String makeZaferKey(int time, int index) {
		String s1 = EUtils.makePaddedInt(time, 4);
		String s2 = EUtils.makePaddedInt(index, 4);
		String r = s1 + s2;
		return r;
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
                    if (n.status > 0 && (!n.identity.startsWith("N"))) 
                    	count++;
                }
            }
            if (count > stage) 
            	break;
        }
        return (time + 1);
    }




    private void buildList() {
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
        //add(iScrollPane);
        iListModel.addElement(help());
    }

    private void buildOutToolBar() {
        //iToolBar.setMaximumSize(new Dimension(700,20));
        iToolBar.add(new JLabel("start/end"));
        iTextField2 = new JTextField();
        iTextField2.setColumns(5);
        iTextField2.setText("1");
        iToolBar.add(iTextField2);
        iTextField = new JTextField();
        iTextField.setColumns(5);
        iTextField.setText("40");
        iToolBar.add(iTextField);
        JButton jb = new JButton(CLEAR);
        addToolBarButton(jb);
        jb = new JButton("Divisions");
        addToolBarButton(jb);
        jb = new JButton("Movements");
        addToolBarButton(jb);
    }

	public void showMe() {
        //iFrame = new JFrame(iLog.iTitle);
        //iFrame.setTitle("Deaths and Adjacencies Dialog");
        iFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        iFrame.setContentPane(this);
        iFrame.pack();
        iFrame.setVisible(true);
    }


	@Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Divisions")) { //first list
        	prepareZaferDivisions();
        } else if (s.equals("Movements")) { //second list
        	prepareZaferMovements();
        } else if (s.equals(CLEAR)) { //Clear
            iNamesAvailable = false;
            iListModel.clear();
            iListModel.addElement(help());
        }


	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
        if (!iNamesAvailable) 
        	return;
        if (e.getValueIsAdjusting() == false) {
            int index = iCellList.getSelectedIndex();
            int k = Math.max(index, 1);
            
            // Setting k to index instead doesn't do anything
            // Retrieved string is null
            //int k = index;
            String s = (String)iListModel.elementAt(k);
            if (s == null) 
            	return;
            if (s.charAt(0) == '#') 
            	return;
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



    protected void addToolBarButton(AbstractButton ab) {
        ab.addActionListener(this);
        iToolBar.add(ab);
    }



    private static final String
    CLEAR = "Clear"
   ,LINE  = "                                        "
   ,ANGLE = "Angle"
   ,TEST1 = "Deaths"
   ,TEST2 = "Adj back"
   ,TEST3 = "Adj forward"
   ,TEST4 = "Jumps"
   ,TEST5 = "Test5"
   ,HELP  = "Help"
   ;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

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
