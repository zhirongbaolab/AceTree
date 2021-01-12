/*
 * Created on May 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.image;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.EUtils;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ImageAllCentroids extends JPanel implements ActionListener {
    private AceTree iAceTree;
    private NucleiMgr iNucleiMgr;
    private Frame iFrame;
    private String iTitle;
    private Canvas iCanvas;
    private Color [] iSpectrum;
    private JScrollPane iPane;
    private String iMenuItem;
    private boolean iInvertY;
    private boolean iUse200MinNames;
    
    public ImageAllCentroids(AceTree aceTree, String title) {
        //System.out.println("ImageAllCentroids constructor");
        iAceTree = aceTree;
        iNucleiMgr = iAceTree.getNucleiMgr();
        iFrame = new Frame(title);
        iFrame.setLayout(new BorderLayout());
        //iFrame.setMenuBar(createMenuBar());
        iTitle = title;
        iMenuItem = ALL;
        //iFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        WinEventMgr wem = new WinEventMgr();
        iFrame.addWindowListener(wem);
        iCanvas = new CentroidsCanvas();
        //iCanvas.setSize(ImageWindow.cImageWidth, ImageWindow.cImageHeight);
        iFrame.add(iCanvas);
        //iFrame.setSize(ImageWindow.cImageWidth, ImageWindow.cImageHeight);
        iFrame.setMenuBar(createMenuBar());
        //iPane = new JScrollPane(iCanvas);
        //add(iPane);
        //setOpaque(true);
        //iFrame.getContentPane().add(iCanvas);
        //iFrame.setContentPane(this);
        //iCanvas.addMouseListener(this);
        //iFrame.pack();
        iFrame.setVisible(true);
        int N = 35;
        int NUSE = 15;
        int j;
        iSpectrum = new Color[N];
        for (int i=0; i < N; i++) {
            if (i < 10) j = 0;
            else if (i > 25) j = NUSE - 1;
            else j = i - 10;
            iSpectrum[ i ] = new Color((NUSE - j)/(float)NUSE, j/(float)NUSE, 0 );
        }
    }


    public void drawAllNuclei(Graphics2D g, int time) {
        //System.out.println("drawAllNuclei");
        //String s = iTitle;
        //s = s.substring(s.indexOf("-t") + 2);
        //int time = Integer.parseInt(s);
        Vector nuclei = iNucleiMgr.getNuclei(time);
        Nucleus n = null;
        int plane = 0;
        for (int i=0; i < nuclei.size(); i++) {
            n = (Nucleus)nuclei.elementAt(i);
            if (n.status == -1) continue;
            plane = (int)(n.z + 0.5f);
            g.setColor(iSpectrum[plane]);
            g.draw(EUtils.pCircle(n.x, n.y, n.size/2));
            g.drawString(n.identity, n.x, n.y);
        }
    }
    
    @SuppressWarnings("unused")
	public void drawNuclei(Graphics2D g, int time) {
        if (time == 1) {
            if (iMenuItem.equals(ALL)) drawAllNuclei(g, time);
            return;
        }
        //System.out.println("drawNuclei: " + time);
        Vector nuclei = iNucleiMgr.getNuclei(time);
        //System.out.println("drawNuclei2: " + nuclei.size());
        Vector prev = iNucleiMgr.getNuclei(time - 1);
        Nucleus n = null;
        Nucleus p = null;
        int plane = 0;
        for (int i=0; i < nuclei.size(); i++) {
            n = (Nucleus)nuclei.elementAt(i);
            if (n.status == -1) continue;
            drawIt(g, n);
            //p = (Nucleus)prev.elementAt(n.predecessor -1);
            //if (iMenuItem.equals(ALL)) drawIt(g, n);
            //else if (iMenuItem.equals(DIVIDING) && p.successor2 > 0) drawIt(g, n);
            //else if (iMenuItem.equals(NONDIVIDING) && p.successor2 < 0) drawIt(g, n);
            //else if (iMenuItem.equals(ATENDTIME) && isAtEndTime(n.identity, time)) drawIt(g, n);
        }
    }

    private boolean isAtEndTime(String cellName, int time) {
        Cell c = iAceTree.getCellByName(cellName);
        return (c.getEndTime() == time);
    }

    
    private void drawIt(Graphics2D g, Nucleus n) {
        int ny = n.y;
        //if (iInvertY)
        	//ny = ImageWindow.cImageHeight - n.y;
        int plane = (int)(n.z + 0.5f);
        g.setColor(iSpectrum[plane]);
        if (iUse200MinNames && !isInSet(n.identity))
        	return;
        g.draw(EUtils.pCircle(n.x, ny, n.size/2));
        g.drawString(n.identity, n.x, ny);
    }
    
    private boolean isInSet(String name, int x) {
        boolean b = false;
        b = (name.indexOf("MS") == 0);
        b = b || (name.indexOf("E") == 0);
        b = b || (name.indexOf("Z") == 0);
        b = b || (name.indexOf("D") == 0);
        b = b || (name.indexOf("C") == 0);
        
        return b;
    }
    
    private boolean isInSet(String name) {
        boolean b = false;
        for (int i=0; i < sulstonNames.length; i++) {
            b = b || (name.indexOf(sulstonNames[i]) == 0);
            if (b) break;
        }
        
        return b;
    }
    
    private static final String [] sulstonNames = {
             "ABaraaaa"
            ,"ABalpaaa"
            ,"ABalpap"
            ,"ABaraapa"
            ,"ABarapaa"
            ,"ABaraapp"
            ,"ABarapap"
            ,"ABalpappa"
            ,"Capa"
            ,"Cppa"
            ,"Capp"
            ,"Cppp"
            ,"MSaaaa"
            ,"MSpaaa"
            ,"MSaaap"
            ,"MSaapa"
            ,"MSpaap"
            ,"MSpapa"
            ,"MSaapp"
            ,"Ealp"
            ,"Earp"
            ,"Epla"
            ,"Epra"
            ,"Eplp"
            ,"Eprp"
            ,"Da"
            ,"Dp"
            ,"Z"
            
    };
    
    public void drawTestCircles(Graphics2D g) {
        int x = 40;
        int y = 100;
        for (int i=10; i <= 25; i++) {
            g.setColor(iSpectrum[i]);
            //g.draw((Shape)new Rectangle(x, y, 40, 40));
            g.draw(EUtils.pCircle(x, y, 20));
            g.drawString(String.valueOf(i), x + 10, y + 10);
            x += 45;
            
        }
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Show");
        MenuItem test = new MenuItem(STANDARD);
        menu.add(test);
        test.addActionListener(this);
        
        test = new MenuItem(INVERT_Y);
        menu.add(test);
        test.addActionListener(this);
        test = new MenuItem(USE200MIN_NAMES);
        test.addActionListener(this);
        menu.add(test);
        
        menuBar.add(menu);
        
        return menuBar;
    }
    
    /*
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Show");
        JMenuItem test = new JMenuItem("All");
        menu.add(test);
        test.addActionListener(this);
        JMenuItem test2 = new JMenuItem("Dividing only");
        menu.add(test2);
        test2.addActionListener(this);
        JMenuItem test3 = new JMenuItem("Nondividing only");
        menu.add(test3);
        test3.addActionListener(this);
        menuBar.add(menu);
        return menuBar;
    }
    */
    @Override
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        //System.out.println("actionPerformed: " + s);
        iMenuItem = s;
        if (s.equals(STANDARD)) {
            iInvertY = false;
            iUse200MinNames = false;
        } else if (s.equals(INVERT_Y)) {
            iInvertY = !iInvertY;
        } else if (s.equals(USE200MIN_NAMES)) {
            iUse200MinNames = true;
        }
        iCanvas.repaint();
        
    }
    
    private class CentroidsCanvas extends Canvas {
        @Override
		public void paint(Graphics g) {
            //System.out.println("CentroidCanvas.paint called with title: " + iTitle);
            
            String s = iTitle;
            s = s.substring(s.indexOf("-t") + 2);
            int time = Integer.parseInt(s);
            Graphics2D g2d = (Graphics2D)g;
            setBackground(Color.white);
            
            g2d.setStroke(new BasicStroke(3.0f));
            drawNuclei(g2d, time);
            //if (iMenuItem.equals(ALL)) drawAllNuclei(g2d, time);
            //else drawDividingNuclei(g2d, time);
            //drawTestCircles(g2d);
            
            //g.drawOval(100, 100, 50, 50);
            
             
        }
    }
    
    
    
    private class WinEventMgr extends WindowAdapter {
        @Override
		public void windowClosing(WindowEvent e) {
            //System.out.println("ImageAllCentroids windowClosing: ");
            iFrame.dispose();
            //iAceTree.image3DOff();
            
        }
    }
    
    private static final String 
         ALL = "All"
        ,DIVIDING = "Dividing only"
        ,NONDIVIDING = "Nondividing"
        ,ATENDTIME = "At end time"
        ,STANDARD = "Standard view"
        ,INVERT_Y = "Invert y axis"
        ,USE200MIN_NAMES = "Use 200 minute names"
        ;
    
    public static void main(String[] args) {
    }
}
