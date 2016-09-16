package org.rhwlab.tree;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.event.MouseInputAdapter;


class TreeCanvas extends Canvas {
    Cell        c;
    int         width;
    int         height;
    SulstonTree iSulstonTree;
    Hashtable   iCellXHash;
    int []      iInt;
    boolean     iCanInterrogate;
    int         iLateTime;
    int         iMinRed;
    int         iMaxRed;
    
    public TreeCanvas(Cell c, SulstonTree sulstonTree, boolean canInterrogate) {
    	System.out.println("TreeCanvas constructor called.");
        this.c = c;
        iSulstonTree = sulstonTree;
        iCanInterrogate = canInterrogate;
        iCellXHash = new Hashtable();
        width = XSCALE*c.getLeafCount();
        Cell.setXScale(XSCALE);
        //int frameWidth = iSulstonTree.getWidth();
        //System.out.println("TreeCanvas " + width + CS + frameWidth);
        //if (width < frameWidth) width = frameWidth; 
        //height = YSCALE*c.getDepth();
        //height = YFIXEDSCALE;
        if (canInterrogate) {
            MouseHandler mh = new MouseHandler(this);
            addMouseMotionListener(mh);
            addMouseListener(mh);
        }
        iLateTime = Cell.getEndingIndex();
        setBackground(Color.white);
    }    
    
    public void setLateTime(int time) {
        iLateTime = time;
    }
    
    public void setMinRed(int min) {
        iMinRed = min;
    }
    
    public void setMaxRed(int max) {
        iMaxRed = max;
    }
    
    public void setCell(Cell cSet) {
    	try {
	        c = cSet;
	        width = XSCALE*c.getLeafCount();
	        width = Math.max(width, MINWIDTH);
	        Dimension d = getSize();
	        d.width = width;
	        setSize(d);
    	}
    	catch (NullPointerException npe) {
        	//System.out.println("Cannot create interactive lineage. No cell selected.");
        }
    }
    
    @Override
	public void paint(Graphics g) {
        Dimension d = getSize();
        //System.out.println("TreeCanvas.paint: " + c.getName() + CS + d);
        
        if (c != null) {
            iCellXHash.clear();
            c.setLateTime(iLateTime);
            Cell.setMinRed(iMinRed);
            Cell.setMaxRed(iMaxRed);
            System.out.println("TreeCanvas cell drawing...");
            c.draw(g, d.width, d.height, iSulstonTree.getWidth(), iCellXHash);
        }
        //displayHash();
    }
    
    public void captureImage(String filePath) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        paint(g);
        File f = new File(filePath);
        try {
            ImageIO.write(image, "png", f);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private void displayHash() {
        Enumeration eKeys = iCellXHash.keys();
        while (eKeys.hasMoreElements()) {
            Integer x = (Integer)eKeys.nextElement();
            Cell cname = (Cell)iCellXHash.get(x);
            System.out.println("displayHash: " + cname + CS + x + CS + cname.iEndingIndex +
                    CS + cname.getTime() + CS + cname.getEndTime());
        }
    }
       

    @Override
	public Dimension getPreferredSize() { 
        System.out.println("getPreferredSize: " + width + CS + height);
        return new Dimension(width, height); 
    }

    @Override
	public Dimension getMinimumSize() { 
    	return getPreferredSize(); 
	}

    private static final String CS = ", ";

    private static final int 
         XSCALE = 20
        ,YSCALE = 70
        ,YFIXEDSCALE = 1000
        ,MINWIDTH = 400
    ;

    @SuppressWarnings("unused")
	private Cell findIt(int x, int y) {
        Enumeration ev = iCellXHash.elements();
        int xs = 10000;
        Cell cs = null;
        double timex = (c.getTime() + (y - Cell.START1)/c.ysc);
        int time = (int)(timex + 0.5);
        //System.out.println("findIt: " + c + CS + time + CS + Cell.START1 + CS + c.yStartUse);
        while (ev.hasMoreElements()) {
            Cell c = (Cell)ev.nextElement();
            //System.out.println(c + CS + c.getTime() + CS + c.getEndTime());
            int cystart = c.yStartUse;
            int cyend = cystart + (int)((c.getEndTime() - c.getTime() + 1) * this.c.ysc + 0.5);
            //System.out.println(c + CS + cystart + CS + cyend);
            //if (y < cystart || y > cyend) continue;
            if (time < c.getTime() || time > c.getEndTime()) continue;
            //System.out.println("candidate: " + c);
            int xtest = Math.abs(x - c.xUse);
            if (xtest < xs && xtest < Cell.xsc) {
                xs = xtest;
                cs = c;
            }
        }
        //int time = 0;
        //if (cs != null) time = c.getTime() + (int)((y - c.yStartUse)/this.c.ysc);
        //System.out.println("selected " + cs + " at " + time);
        return cs;
        
    }
    
    public void notifyAceTree(Cell c, int time) {
        Vector v = new Vector();
        v.add("InputCtrl1");
        v.add(String.valueOf(time));
        v.add(c.getName());
        iSulstonTree.iAceTree.controlCallback(v);
    }

    class MouseHandler extends MouseInputAdapter {

        public MouseHandler(Object o) {
            super();
        }

        @Override
		public void mouseMoved(MouseEvent e) {
        }
        
        @Override
		public void mouseClicked(MouseEvent e) {
            double time = (c.getTime() + (e.getY() - Cell.START1)/c.ysc);
            //System.out.println("TreeCanvas2.mouseClicked: " + e.getX() + CS + e.getY() + CS + time);
            Cell cs = findIt(e.getX(), e.getY());
            if (cs != null) {
                int intTime = (int)(time + 0.5);
                int button = e.getButton();
                if (button == MouseEvent.BUTTON1) notifyAceTree(cs, intTime);
                else if (button == MouseEvent.BUTTON3) notifyAceTree(cs, cs.getEndTime());
            }
        }
    }



}
