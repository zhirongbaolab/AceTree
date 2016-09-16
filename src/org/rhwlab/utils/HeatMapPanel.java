package org.rhwlab.utils;
import java.awt.ScrollPane;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HeatMapPanel extends ScrollPane {
    
    int iWidth;
    int iHeight;
    
    public HeatMapPanel(HeatMap heatMap, int width, int height) {
        iWidth = width;
        iHeight = height;
        add(heatMap);
        //ScrollPane scroller = new ScrollPane();
        //scroller.add(heatMap);
        //add(scroller);
        setSize(width, height);
    }
/*    
    public Dimension getSize() {
        System.out.println("getPreferredSize");
        return new Dimension(iWidth, iHeight);
    }

    public Dimension getPreferredSize() {
        System.out.println("getPreferredSize");
        return new Dimension(iWidth, iHeight);
    }

    public Dimension getMinimumSize() {
        System.out.println("getMinimumSize");
        return getPreferredSize(); 
    }
*/
    public static void main(String[] args) {
    }
}
