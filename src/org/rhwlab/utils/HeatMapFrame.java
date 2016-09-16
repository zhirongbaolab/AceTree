package org.rhwlab.utils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;

//import org.rhwlab.image.Image3D.PropertiesTab;
//import org.rhwlab.image.Image3D.PropertiesTab.SublineageUI;
/*
 * Created on Nov 10, 2005
 *
 */

/**
 * @author biowolp
 *
 */
public class HeatMapFrame extends JFrame {
    
    HeatMap iHeatMap;
    int     iWidth;
    int     iHeight;
    JTabbedPane iTabbedPane;
    JPanel      iControlPanel;
    //int     iNumCells = 5;
    
    public HeatMapFrame(String title, HeatMap heatMap, int width, int height, int numFrames) {
        super(title);
        iHeatMap = heatMap;
        iWidth = width;
        iHeight = height;
        //System.out.println("heatMapPanel size: " + heatMapPanel.getSize());
        //ScrollPane scroller = new ScrollPane();
        iTabbedPane = new JTabbedPane();
        iTabbedPane.addTab("Image", null, new HeatMapPanel(iHeatMap, iWidth, iHeight), "View 3D image");
        PropertiesTab pt = new PropertiesTab();
        iControlPanel = pt.getPanel();
        iTabbedPane.addTab("Properties", null, iControlPanel, "Set color scheme");

        getContentPane().setLayout(new GridLayout(numFrames,1));
        getContentPane().add(iTabbedPane);        
        //scroller.add(heatMapPanel);
        pack();
        setVisible(true);
    }
    
    public void addHeatMap(ScrollPane heatMapPane) {
        getContentPane().add(heatMapPane);
    }

    @Override
	public Dimension getPreferredSize() {
        //System.out.println("HeatMapFrame getPreferredSize()");
        return new Dimension(iWidth, iHeight); 
    }

    @Override
	public Dimension getMinimumSize() {
        //System.out.println("HeatMapFrame getMinimumSize()");
        return getPreferredSize(); 
    }

    
    public static int [][] getDataVector() {
        int iNumCells = 50;
        int iNumTimes = 100;
        int [][] r = new int[iNumCells][iNumTimes];
        for (int i = 0; i < iNumCells; i++) {
            for (int j=0; j < iNumTimes; j++) {
                r[i][j] = 25000 + (int)(25000 * Math.random());
            }
        }
        return r;
    }

    public class PropertiesTab implements ActionListener {
        JPanel                          iPanel;
        JTextField                      iMin;
        JTextField                      iMax;
        JButton                         iSetMinMax;
        JTextField                      iXinc;
        JTextField                      iYinc;
        JButton                         iSetSpotSize;
        
        
        
        public PropertiesTab() {
            Border blackline = BorderFactory.createLineBorder(Color.black);
            Border grayline = BorderFactory.createLineBorder(Color.darkGray);
            iPanel = new JPanel();
            iPanel.setLayout(new GridLayout(2,1));
            iPanel.setBorder(blackline);
            
            // the minmax panel
            JPanel jp = new JPanel();
            jp.setBorder(blackline);
            jp.setPreferredSize(new Dimension(100, 100));
            jp.setLayout(new GridLayout(3,2));
            iPanel.add(jp);

            JLabel jl = new JLabel("Min");
            jl.setBorder(grayline);
            jp.add(jl);
            iMin = new JTextField(10);
            iMin.setBorder(blackline);
            jp.add(iMin);

            jl = new JLabel("Max");
            jl.setBorder(grayline);
            jp.add(jl);
            iMax = new JTextField(10);
            iMax.setBorder(blackline);
            jp.add(iMax);
            iSetMinMax = new JButton("Set");
            jp.add(iSetMinMax);
            
            
            // the spot size panel
            jp = new JPanel();
            jp.setPreferredSize(new Dimension(100, 100));
            jp.setBorder(blackline);
            jp.setLayout(new GridLayout(3,2));
            iPanel.add(jp);
            
            jl = new JLabel("xinc");
            jl.setBorder(grayline);
            jp.add(jl);
            iXinc = new JTextField(10);
            iXinc.setBorder(blackline);
            jp.add(iXinc);

            jl = new JLabel("yinc");
            jl.setBorder(grayline);
            jp.add(jl);
            iYinc = new JTextField(10);
            iYinc.setBorder(blackline);
            jp.add(iYinc);
            iSetSpotSize = new JButton("Set");
            jp.add(iSetSpotSize);
            
            iSetMinMax.addActionListener(this);
            iSetSpotSize.addActionListener(this);
            iMin.setText(Integer.toString(iHeatMap.getMin()));
            iMax.setText(Integer.toString(iHeatMap.getMax()));
            iXinc.setText(Integer.toString(iHeatMap.getXinc()));
            iYinc.setText(Integer.toString(iHeatMap.getYinc()));
            
        }
        
        public JPanel getPanel() {
            return iPanel;
        }

		@Override
		public void actionPerformed(ActionEvent e) {
            //System.out.println("actionPerformed: " + e);
            Object o = e.getSource();
            //String command = e.getActionCommand();
            if (o == iSetMinMax) {
                int min = Integer.parseInt(iMin.getText());
                int max = Integer.parseInt(iMax.getText());
                iHeatMap.setMinMax(min, max);
            } else if (o == iSetSpotSize) {
                int xinc = Integer.parseInt(iXinc.getText());
                int yinc = Integer.parseInt(iYinc.getText());
                iHeatMap.setSpotSize(xinc, yinc);
            }

        }

    }
        
    @SuppressWarnings("unused")
	public static void main(String [] args) {
        //HeatMap heatMap = new HeatMap();
        //new HeatMapFrame("HeatMap Frame", heatMap);
        //int [][] test = {{1,2,3},{1,2,3}};
        int [][] test = getDataVector();
        //String [] labels = {"one", "seven", "three", "four", "six"};
        String [] labels = new String[50];
        for (int i=0; i < labels.length; i++) labels[i] = Integer.toString(i);
        HeatMap heatMap = new HeatMap(test, labels, 25000, 50000);
        HeatMapPanel heatMapPanel = new HeatMapPanel(heatMap, 300, 100);
        HeatMapFrame hmf = new HeatMapFrame("HeatMap Frame", heatMap, 400, 200, 1);
        int [][] test2 = getDataVector();
        //HeatMap heatMap2 = new HeatMap(test2, labels, 15000, 60000);
        //HeatMapPanel heatMapPanel2 = new HeatMapPanel(heatMap2, 300, 100);
        //hmf.addHeatMap(heatMapPanel2);
        System.out.println(test.length);
        int [] one = test[0];
        System.out.println(one.length);
        
    }
}
