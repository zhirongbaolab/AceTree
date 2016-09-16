package org.rhwlab.acetree;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import org.rhwlab.dbaccess.EmbryoXML;
import org.rhwlab.dbaccess.DBAccess;
import org.rhwlab.snight.Config;

public class OpenFromDB  extends JDialog implements ActionListener {

    AceTree iAceTree;
    JTextField  iSeries;
    JButton     iOpen;
    private JRadioButton    iNewCanonical;
    private JRadioButton    iManual;
    private JRadioButton    iTraditional;
    private JRadioButton    iNew;
    private int             iNamingMethod;
    private JCheckBox		iEditedPts;


    public OpenFromDB(AceTree acetree) {
    	super();
		println("OpenFromDB, DBAccess.cDBLocation=" + DBAccess.cDBLocation);
    	iAceTree = acetree;
    	setTitle("Open from DB");
        //setSize(new Dimension(400, 100));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2,1));
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        setContentPane(p);
        iSeries = new JTextField(30);
        //iSeries.setSize(50, 20);
        p.add(iSeries);
        iOpen = new JButton("open");
        p.add(iOpen);
        iOpen.addActionListener(this);
        iNamingMethod = Config.cDefaultNaming;
        addIdentityChoices(p);
        addDBChoices(p);
        addEditedPointsCheckbox(p);
        setVisible(true);
        pack();
    }

    private void addIdentityChoices(JPanel mp) {
        iNewCanonical = new JRadioButton(NEWCANONICAL);
        iManual = new JRadioButton(MANUAL);
        ButtonGroup bg = new ButtonGroup();
        JPanel rp = new JPanel();
        Border blackline = BorderFactory.createLineBorder(Color.black);
        rp.setBorder(blackline);
        rp.setLayout(new GridLayout(0,1));
        bg.add(iNewCanonical);
        bg.add(iManual);
        iNewCanonical.setSelected(iNamingMethod == Config.NEWCANONICAL);
        iManual.setSelected(iNamingMethod == Config.MANUAL);
        rp.add(new JLabel("Identification mode"));
        rp.add(iNewCanonical);
        rp.add(iManual);
        mp.add(rp);

    }

    private void addDBChoices(JPanel mp) {
        iTraditional = new JRadioButton("traditional");
        iNew = new JRadioButton("new");
        ButtonGroup bg = new ButtonGroup();
        JPanel rp = new JPanel();
        Border blackline = BorderFactory.createLineBorder(Color.black);
        rp.setBorder(blackline);
        rp.setLayout(new GridLayout(0,1));
        bg.add(iTraditional);
        bg.add(iNew);
        iNew.setSelected(true);
        println("addDBChoices, " + DBAccess.cDBLocation);
        //iNew.setSelected(!DBAccess.cDBLocation.equals("/nfs/waterston/embryoDBnew"));
        rp.add(new JLabel("Database"));
        rp.add(iTraditional);
        rp.add(iNew);
        mp.add(rp);
    }

    private void addEditedPointsCheckbox(JPanel mp) {
    	iEditedPts = new JCheckBox("Use edited timepoints only");
    	iEditedPts.setSelected(false);
        JPanel rp = new JPanel();
        Border blackline = BorderFactory.createLineBorder(Color.black);
        rp.setBorder(blackline);
        rp.setLayout(new GridLayout(0,1));
        rp.add(iEditedPts);
        mp.add(rp);
    }

    void handleRadioButtons() {
    	if (iTraditional.isSelected()) {
    		DBAccess.cDBLocation = "/nfs/waterston/embryoDBnew";
    	} else DBAccess.cDBLocation = "/nfs/waterston/embryoDB";
    	if (iManual.isSelected()) {
    		Config.cDefaultNaming = Config.MANUAL;
    	} else Config.cDefaultNaming = Config.NEWCANONICAL;
    }


	@Override
	public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == iOpen) {
        	handleRadioButtons();
        	String sr = "";
        	String s = iSeries.getText();
        	File f = new File(s);
        	if (f.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(f);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    sr = br.readLine();
                    while (sr != null && sr.length() > 2) {
                    	boolean first = true;
                        if (sr.indexOf("#") != 0) {
                            String [] sa = sr.split(" ");
                            sr = sa[0];
                            System.out.println("\n\n***series: " + sr);
                        	try{
                        		EmbryoXML exml = new EmbryoXML(sr);
                        		String annots = exml.iRecord[EmbryoXML.ANNOTS];
                        		String atconfig = exml.iRecord[EmbryoXML.ATCONFIG];
                        		String editedPts = exml.iRecord[EmbryoXML.EDITEDTP];
                        		String configFile = annots + "/dats/" + atconfig;
                        		//println("actionPerformed, " + configFile);
                                iAceTree.setConfigFileName(configFile);
                    			Config config = Config.createConfigFromXMLFile(configFile);
                    			if (iEditedPts.isSelected()) {
                    				config.setEndingIndex(Integer.parseInt(editedPts));
                    			}
                                iAceTree.bringUpSeriesData(config);
                                if (first) {
                                    iAceTree.bringUpSeriesUI(configFile);
                                    first = false;
                                }
                        	} catch(FileNotFoundException fnfe) {
                        		fnfe.printStackTrace();
                        	}
                        }
                        sr = br.readLine();
                    }
                    br.close();
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }

        	} else {
        	//println("actionPerformed, " + s);
        		try{
        			EmbryoXML exml = new EmbryoXML(s);
        			String annots = exml.iRecord[EmbryoXML.ANNOTS];
        			String atconfig = exml.iRecord[EmbryoXML.ATCONFIG];
        			String configFile = annots + "/dats/" + atconfig;
        			//Config config = Config.createConfigFromXMLFile(configFile);
        			Config config = new Config(configFile);
        			if (iEditedPts.isSelected()) {
                		String editedPts = exml.iRecord[EmbryoXML.EDITEDTP];
        				config.setEndingIndex(Integer.parseInt(editedPts));
        			}
        			//println("actionPerformed, " + configFile);
        			iAceTree.setConfigFileName(configFile);
        			iAceTree.bringUpSeriesUI(config);
        			dispose();

        		} catch(FileNotFoundException fnfe) {
        			fnfe.printStackTrace();
        		}

        	}
        	dispose();
        }

	}


	public void actionPerformed(ActionEvent e, boolean bogus) {
        Object o = e.getSource();
        if (o == iOpen) {
        	handleRadioButtons();
        	String sr = "";
        	String s = iSeries.getText();
        	File f = new File(s);
        	if (f.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(f);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    sr = br.readLine();
                    while (sr != null && sr.length() > 2) {
                    	boolean first = true;
                        if (sr.indexOf("#") != 0) {
                            String [] sa = sr.split(" ");
                            sr = sa[0];
                            System.out.println("\n\n***series: " + sr);
                        	try{
                        		EmbryoXML exml = new EmbryoXML(sr);
                        		String annots = exml.iRecord[EmbryoXML.ANNOTS];
                        		String atconfig = exml.iRecord[EmbryoXML.ATCONFIG];
                        		String configFile = annots + "/dats/" + atconfig;
                        		//println("actionPerformed, " + configFile);
                                iAceTree.setConfigFileName(configFile);
                                iAceTree.bringUpSeriesData(configFile);
                                if (first) {
                                    iAceTree.bringUpSeriesUI(configFile);
                                    first = false;
                                }
                        	} catch(FileNotFoundException fnfe) {
                        		fnfe.printStackTrace();
                        	}
                        }
                        sr = br.readLine();
                    }
                    br.close();
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                }

        	} else {
        	//println("actionPerformed, " + s);
        		try{
        			EmbryoXML exml = new EmbryoXML(s);
        			String annots = exml.iRecord[EmbryoXML.ANNOTS];
        			String atconfig = exml.iRecord[EmbryoXML.ATCONFIG];
        			String configFile = annots + "/dats/" + atconfig;
        			//Config config = new Config(configFile);
        			//println("actionPerformed, " + configFile);
        			iAceTree.setConfigFileName(configFile);
        			iAceTree.bringUpSeriesUI(configFile);
        			dispose();

        		} catch(FileNotFoundException fnfe) {
        			fnfe.printStackTrace();
        		}

        	}
        	dispose();
        }

	}

    private static final String
   STANDARD = "Standard"
   //,CANONICAL = "Canonical"
   ,NEWCANONICAL = "New canonical"
   ,MANUAL = "Manual"
   ;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

    private static void println(String s) {System.out.println(s);}

}
