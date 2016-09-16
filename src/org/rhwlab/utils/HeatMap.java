package org.rhwlab.utils;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HeatMap extends Canvas {
    int iNumTimes;
    int iNumCells;
    int iNumColors;
    int [][] iR;
    String [] iLabels;
    int iMin;
    int     iMax;
    double iScale;
    int     iXinc;
    int     iYinc;

    public HeatMap(int [][] data, String [] labels, int min, int max) {
        iNumCells = data.length;
        iNumTimes = data[0].length;
        iR = data;
        iLabels = labels;
        iMin = min;
        iMax = max;
        iScale = CMAP.length/((double)(max - min));
        iXinc = 10;
        iYinc = 10;
        setSize(iNumTimes*iXinc + 100, iNumCells*iYinc);
        
    }
    
    public void setMinMax(int min, int max) {
        iMin = min;
        iScale = CMAP.length/((double)(max - min));
        invalidate();
    }
    
    public void setSpotSize(int width, int height) {
        iXinc = width;
        iYinc = height;
        invalidate();
    }
    
    public int getMin() {
        return iMin;
    }
    
    public int getMax() {
        return iMax;
    }
    
    public int getXinc() {
        return iXinc;
    }
    
    public int getYinc() {
        return iYinc;
    }
    
    
    public HeatMap() {
        //super(new BorderLayout());
        iNumTimes = 100;
        iNumCells = 10;
        iNumColors = 20;
        iMin = 25000;
        iScale = CMAP.length/(50000 - iMin);
        iR = getDataVector();
        setSize(iNumTimes*10 + 100, iNumCells*10);
        
    }
    
    private int [][] getDataVector() {
        int [][] r = new int[iNumCells][iNumTimes];
        for (int i = 0; i < iNumCells; i++) {
            for (int j=0; j < iNumTimes; j++) {
                r[i][j] = 25000 + (int)(25000 * Math.random());
            }
        }
        return r;
    }
    
    private int getDiscrete(int r) {
        int k = 0;
        k = (int)((r - iMin) * iScale);
        //System.out.println("getDiscrete: " + k + ", " + r + ", " + iMin + ", " + iScale);
        
        return k;
    }
    
    @Override
	public void paint(Graphics g) {
        //System.out.println("paint entered");
        Dimension d = getSize();
        g.setColor(Color.white);
        g.fillRect(0, 0, d.width, d.height);
        //int xinc = 10;
        //int yinc = 10;
        int yl = 0;
        for (int j = 0; j < iNumCells; j++) {
            int xl = 0;
            //int [] r = getDataVector();
            for (int i=0; i < iNumTimes; i++) {
                int discrete = getDiscrete(iR[j][i]);
                //System.out.println("paint: " + j + ", " + i + ", " + iR[j][i] + ", " + discrete);
                g.setColor(getTheColor(discrete));
                g.fillRect(xl, yl, iXinc, iYinc);
                xl += iXinc;
            }
            g.setColor(Color.black);
            g.drawString(iLabels[j], xl + 10, yl + iYinc);
            yl += iYinc;
        }
    }
    
    private Color getTheColor(int index) {
        if (index < 0) index = 0;
        if (index >= CMAP.length) index = CMAP.length - 1;
        return CMAP[index];
    }
    
    private static final Color [] CMAP = {
            new Color(000, 255, 0)
           ,new Color(000, 230, 0)
           ,new Color(000, 205, 0)
           ,new Color(000, 180, 0)
           ,new Color(000, 155, 0)
           ,new Color(000, 130, 0)
           ,new Color(000, 105, 0)
           ,new Color(000, 80, 0)
           ,new Color(000, 55, 0)
           ,new Color(000, 30, 0)
           ,new Color(30,  000, 0)
           ,new Color(55,  000, 0)
           ,new Color(85,  000, 0)
           ,new Color(105, 000, 0)
           ,new Color(130, 000, 0)
           ,new Color(155, 000, 0)
           ,new Color(180, 000, 0)
           ,new Color(205, 000, 0)
           ,new Color(230, 000, 0)
           ,new Color(255, 000, 0)
    };
    

    public static void main(String[] args) {
        JFrame f = new JFrame("heat map main");
        f.addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        HeatMap heatmap = new HeatMap();
        f.getContentPane().add("Center", heatmap);
        f.pack();
        f.setSize(new Dimension(550,200));
        f.setVisible(true);
    }

}
