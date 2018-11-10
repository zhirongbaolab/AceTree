/*
 * Created on Oct 16, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RedBkgComp extends JDialog implements ActionListener, Runnable {

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

    JTextField      iStartText;
    JTextField      iEndText;
    JTextField      iKLargeText;
    JTextField      iKMediumText;

    JButton         iRun;
    JButton         iStop;

    JTextArea       iText;
    JScrollPane     iPane;

    JCheckBox       iFeedback;

    RedBkgComp2     iRedBkgComp2;

    @SuppressWarnings("unused")
	public RedBkgComp() {
        super(AceTree.getAceTree(null).getMainFrame(), false);
        initialize();
        JFrame owner = iAceTree.getMainFrame();
        setTitle("Background corrected red expression");
        int width = 400;
        int height = 100;
        JPanel all = new JPanel();
        all.setLayout(new BoxLayout(all, BoxLayout.PAGE_AXIS));
        iStartText = new JTextField("205", 10);
        iEndText = new JTextField("207", 10);
        iKLargeText = new JTextField("2.0", 10);
        iKMediumText = new JTextField("1.5", 10);
        Border blackBorder = BorderFactory.createLineBorder(Color.black);
        JPanel lp = new JPanel();
        JPanel mp = new JPanel();
        mp.setPreferredSize(new Dimension(400,height));
        mp.setLayout(new GridLayout(4,2));
        lp.setBorder(blackBorder);
        lp.add(new JLabel("Start time"));
        mp.add(lp);
        mp.add(iStartText);
        lp = new JPanel();
        lp.setBorder(blackBorder);
        lp.add(new JLabel("End time"));
        mp.add(lp);
        mp.add(iEndText);
        lp = new JPanel();
        lp.setBorder(blackBorder);
        lp.add(new JLabel("KLarge"));
        mp.add(lp);
        mp.add(iKLargeText);
        lp = new JPanel();
        lp.setBorder(blackBorder);
        lp.add(new JLabel("KMedium"));
        mp.add(lp);
        mp.add(iKMediumText);
        all.add(mp);

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,0));
        iRun = new JButton("Run");
        iRun.addActionListener(this);
        iStop = new JButton("Stop");
        iStop.addActionListener(this);
        buttons.add(iRun);
        buttons.add(iStop);
        all.add(buttons);

        JPanel fp = new JPanel();
        fp.setBorder(blackBorder);
        iFeedback = new JCheckBox("Send results here ");
        fp.add(iFeedback);
        all.add(fp);

        iText = new JTextArea(4,50);
        iText.setMargin(new Insets(5,5,5,5));
        iText.setEditable(false);
        iText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        iPane = new JScrollPane(iText);
        iPane.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
        all.add(iPane);

        Container cp = getContentPane();
        cp.add(all);
        setLocationRelativeTo(owner);
        pack();
        setVisible(true);
        setModal(false);
        iRedBkgComp2 = new RedBkgComp2(true);
    }

    void getRed() {
        int start = Integer.parseInt(iStartText.getText());
        int end = Integer.parseInt(iEndText.getText());
        double kMedium = Double.parseDouble(iKMediumText.getText());
        double kLarge = Double.parseDouble(iKLargeText.getText());
        boolean sendResults = iFeedback.isSelected();
        iRedBkgComp2.setParameters(start, end, kMedium, kLarge, this, sendResults);
        new Thread(this).start();
    }

    void stopIt() {
        println("actionPerformed, request stop");
        append("requesting stop at end of current time point");
        iRedBkgComp2.stopRequested();
    }


    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
	public void run() {
        //iRedBkgComp2.test1();

    }
    public void append(String s) {
        iText.append(s + NL);
        iText.setCaretPosition( iText.getDocument().getLength() );
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

    public static void main(String[] args) {
        new RedBkgComp();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
	public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == iRun) getRed();
        if (o == iStop) stopIt();


    }
    private static final String NL = "\n";

    private static void println(String s) {System.out.println(s);}
    private static final String CS = ", ";
    private static final DecimalFormat DF1 = new DecimalFormat("####.##");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt1(double x) {return DF1.format(x);}

}
