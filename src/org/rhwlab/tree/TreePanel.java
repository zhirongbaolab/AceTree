/*
 * Created on Mar 2, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.tree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import javax.swing.ListModel;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TreePanel extends JPanel {
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
    int         iNoPaint;

    public TreePanel(Cell c, SulstonTree sulstonTree, boolean canInterrogate) {
    	//System.out.println("TreePanel constructor called.");
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
        height = YFIXEDSCALE;
        if (canInterrogate) {
            MouseHandler mh = new MouseHandler(this);
            addMouseMotionListener(mh);
            addMouseListener(mh);
        }
        iLateTime = Cell.getEndingIndex();
        setBackground(Color.white);
        //System.out.println("TreePanel: " + width + CS + height);
        
        //iBookmarkListModel = null;
    }
    
    public void setBookmarkListModel(ListModel listModel) {
    	if (c != null)
    		c.setBookmarkListModel(listModel);
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
	        height = YSCALE*c.getDepth();
	        d.height = height;
	        setSize(d);
	        //System.out.println("setCell: " + d);
    	}
    	catch (NullPointerException npe) {
        	//System.out.println("Cannot create interactive lineage. No cell selected.");
        }
    }

//    public void captureImage(String filePath) {
    public void captureImage(String fileName, String dir) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        //println("captureImage, " + getWidth() + CS + getHeight());
        Graphics g = image.getGraphics();
        paint(g);
        //println("captureImage, " + image.getWidth() + CS + image.getHeight());
        File f = new File(dir + "/" + fileName);
        try {
            ImageIO.write(image, "png", f);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        makeWebPage(fileName, dir);
    }

    //public void makeWebPage(String filePath) {
    @SuppressWarnings("resource")
	public void makeWebPage(String fileName, String dir) {
        String s = fileName.substring(0, fileName.length() - 4);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dir + "/" + s + ".html", false);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return;
        }
        PrintWriter pw = new PrintWriter(fos, true);
        pw.println("<html>");
        pw.println("<head><title>" + s + "</title></head>");
        pw.println("<body>");
        pw.println("<img src=\"" + s + ".png\">");
        pw.println("</body>");
        pw.println("</html>");
    }

    @Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //System.out.println("\nTreePanel.paintComponent: " );
        //if (iNoPaint++ > 10) return;
        //new Throwable().printStackTrace();
        Dimension d = getSize();
        //System.out.println("paintComponent d=" + d + CS + iLateTime);
        //d = getPreferredSize();
        Dimension pd = iSulstonTree.getSize();
        //System.out.println("paintComponent, pd=" + pd);
        //Cell.setHeight(pd.height);

        if (c != null) {
            //System.out.println("TreePanel.paintComponent:\n " + System.currentTimeMillis()
                   //+CS + d + CS + iSulstonTree.getWidth() + CS + c.getName()
                    //+ "\n" + iLateTime + CS + iMinRed + CS + iMaxRed);
            iCellXHash.clear();
            c.setLateTime(iLateTime);
            Cell.setMinRed(iMinRed);
            Cell.setMaxRed(iMaxRed);
            //System.out.println("TreePanel cell drawing...");
            // Give Cell c reference to bookmark list
            /*
            if (iBookmarkListModel != null) {
            	System.out.println("Setting cell bookmark list...");
            	c.setBookmarkListModel(iBookmarkListModel);
            }
            else {
            	c.setBookmarkListModel(null);
            }
            */
            // Draws first yellow segment of root cell
            c.draw(g, d.width, pd.height-89, iSulstonTree.getWidth(), iCellXHash);
        }
        //displayHash();
        //System.out.println("paintComponent2: " + getSize());
    }

    @SuppressWarnings("unused")
	private void displayHash() {
        Enumeration eKeys = iCellXHash.keys();
        while (eKeys.hasMoreElements()) {
            Integer x = (Integer)eKeys.nextElement();
            Cell cname = (Cell)iCellXHash.get(x);
            //System.out.println("displayHash: " + cname + CS + x + CS + cname.iEndingIndex +
            //        CS + cname.getTime() + CS + cname.getEndTime());
        }
    }


    @Override
	public Dimension getPreferredSize() {
        //System.out.println("getPreferredSize: " + width + CS + height);
        //new Throwable().printStackTrace();
        return new Dimension(width, height);
    }

    @Override
	public Dimension getMinimumSize()
    { return getPreferredSize(); }

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
                if (button == MouseEvent.BUTTON1)
                	notifyAceTree(cs, intTime);
                else if (button == MouseEvent.BUTTON3)
                	notifyAceTree(cs, cs.getEndTime());
            }
            iSulstonTree.iAceTree.requestFocus();
        }
    }

    public static void main(String[] args) { }
    
    private static void println(String s) {
    	System.out.println(s);
	}
}
