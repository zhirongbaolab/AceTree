package org.rhwlab.acetree;
import java.util.Hashtable;
import java.util.Vector;

import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.Cell;



/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 * Created on Jan 19, 2005
 */

/**
 * Holds static utility functions specific to this application.
 * Created from an earlier version called (inappropriately)
 * <code>NucleiTokenizer</code> after its first function.
 * 
 * @author biowolp
 * @version 1.0 January 18, 2005
 *
 */
public class NucUtils {
    
    private static Hashtable anteriorDaughter;
    //private static Hashtable posteriorDaughter;
    private static Hashtable parent;
    private static double cZPixRes;
    
    public static double getZPixRes() {
        return cZPixRes;
    }
    
    public static void setZPixRes(double zpixres) {
        cZPixRes = zpixres;
    }
    
    public static double nucDiameter(Nucleus n, double imgPlane) {
        if (n == null) return -1; //covers some issues re currentCell and not tracking
        double r = -0.5;
        double cellPlane = n.z;
        double R = n.size/2.; //pixels
        double y = (cellPlane - imgPlane)*cZPixRes/R;
        double r2 = 1 - y*y;
        if (r2 >= 0.) r = Math.sqrt(r2)*R;
        return 2*r;
    }
    
    public static boolean hasCircle(Nucleus n, double imgPlane) {
        return (nucDiameter(n, imgPlane) > 0);
    }
    
    public static int distance(Nucleus n1, Nucleus n2) {
        int z1 = (int)Math.round(n1.z * cZPixRes);
        int z2 = (int)Math.round(n2.z * cZPixRes);
        int dz = z1 - z2;
        int dx = n1.x - n2.x;
        int dy = n1.y - n2.y;
        int d = dx*dx + dy*dy + dz*dz;
        double dd = Math.sqrt(d);
        return (int)Math.round(dd);
    }
    
    public static Nucleus meanPos(Nucleus n1, Nucleus n2) {
        int x = (n1.x + n2.x)/2;
        int y = (n1.y + n2.y)/2;
        double z = (n1.z + n2.z)/2;
        Nucleus n = new Nucleus();
        n.x = x;
        n.y = y;
        n.z = (float)z;
        return n;
    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    public static String makeHashKey(int index, Nucleus n) {
        return String.valueOf(index * 1000 + n.index);
    }

        
    /**
     * Given a cell name and a vector of nuclei data for this time point
     * return the Nucleus object for this the cell in the list at this time point.
     */
    @SuppressWarnings("unused")
	public static Nucleus getCurrentCellData(Vector nucData, String cellName) {
        Nucleus r = null;
        boolean found = false;
        for (int j = 0; j < nucData.size(); j++) {
            Nucleus n = (Nucleus)nucData.elementAt(j);
            if (n.identity.equals(cellName)) {
                found = true;
                r = n;
                break;
            }
        }
        return r;
    }
    
    

    /**
     * Given a cell and a vector of nuclei data for this time point
     * return the Nucleus object for this the cell in the list at this time point.
     */
    @SuppressWarnings("unused")
	public static Nucleus getCurrentCellNucleus(Vector nucData, Cell cell) {
        //System.out.println("getCurrentCellNucleus: " + cell.getName() + CS + nucData);
        Nucleus r = null;
        if (cell == null) return r;
        boolean found = false;
        String hashKey = cell.getHashKey();
        //System.out.println("getCurrentCellNucleus: " + hashKey);
        if (hashKey == null) return r;
        for (int j = 0; j < nucData.size(); j++) {
            Nucleus n = (Nucleus)nucData.elementAt(j);
            if (n.status < 0) continue;
            if (n.hashKey != null && n.hashKey.equals(hashKey)) {
                found = true;
                r = n;
                break;
            }
        }
        return r;
    }
    
    
    public static Nucleus getParent(Vector nucData0, Vector nucData1, String daughterName) {
        Nucleus n = null;
        Nucleus n2 = null;
        for (int j=0; j < nucData1.size(); j++) {
            n2 = (Nucleus)nucData1.elementAt(j);
            if (n2.identity.equals(daughterName)) break;
            
        }
        try {
            n = (Nucleus)nucData0.elementAt(n2.predecessor - 1);
        } catch(Exception e) {}
        
        return n;
    }
    
    
    public static Nucleus getParent(Vector nucData, String daughterName) {
        Nucleus n = null;
        Nucleus n2 = null;
        for (int j=0; j < nucData.size(); j++) {
            n2 = (Nucleus)nucData.elementAt(j);
            if (n2.identity.length() == 0) {
                continue;
            }
            if (isParent(n2.identity, daughterName)) {
                n = n2;
                break;
            }
        }
        return n;
    }
    
    private static boolean isParent(String parentName, String daughterName) {
        // special case handling required here but...
        if (parent == null) {
            initializeHashtables();
        }
        String s = (String)parent.get(daughterName);
        if (s != null) return s.equals(parentName);
        int k = daughterName.indexOf(parentName);
        if (k == 0) return true;
        return false;    
    }
    
    
    private static boolean isDaughter(String daughterName, String parentName) {
        // this will require extensive special casing for things like EMS
        if (parent == null) initializeHashtables();
        String p = (String)parent.get(daughterName);
        if (p != null) {
            if (p.equals(parentName)) return true;
            else return false;
        }
        if (daughterName.length() != parentName.length() + 1) return false;
        if (daughterName.indexOf(parentName) == -1) return false;
        return true;
    }
    
    private static boolean isAnteriorDaughter(String daughterName) {
        // this will require extensive special casing for things like EMS
        //if (daughterName.length() != parentName.length() + 1) return false;
        //if (daughterName.indexOf(parentName) == -1) return false;
        if (parent == null) initializeHashtables();
        if (anteriorDaughter.containsKey(daughterName)) return true;
        char x = daughterName.charAt(daughterName.length() - 1);
        if (x == 'a' || x == 'd' || x == 'l') return true;
        return false;   
    }

    private static final String [] posteriorD = {
            "P1","P2","P3","P4", "Z3", "E"
    };
    private static final String [] anteriorD = {
            "AB", "EMS", "C", "D", "Z2", "MS"
    };
    private static final String [] parentN = {
            "P0", "P1", "P2", "P3", "P4", "EMS"
    };
    
    private static void initializeHashtables() {
        anteriorDaughter = new Hashtable();
        for (int i=0; i < anteriorD.length; i++) {
            anteriorDaughter.put(anteriorD[i], anteriorD[i]);
        }
        parent = new Hashtable();
        for (int i=0; i < parentN.length; i++) {
            parent.put(anteriorD[i], parentN[i]);
        }
        for (int i=0; i < parentN.length; i++) {
            parent.put(posteriorD[i], parentN[i]);
        }
    }

    
    
    public static int countLiveCells(Vector nucData) {
        int k = 0;
        for (int j=0; j < nucData.size(); j++) {
            Nucleus n = (Nucleus)nucData.elementAt(j);
            if (n.status == DEAD) continue;
            k++;
        }
        return k;
    }
    
    /**
     * Given a cell name and a vector of nuclei data for this time point
     * return the string array of data for this the cell in the list at this time point.
     */
    public static String [] getCurrentCellData(Vector nucData, String cellName, String x) {
        //int k = -1;
        String [] sa = null;
        boolean found = false;
        int j = nucData.size();
        for (int i = 0; i<j; i++) {
            sa = (String [])nucData.elementAt(i);
            if (sa[CELLNAME].equals(cellName)) {
                found = true;
                break;
            }
        }
        if (found) return sa;
        else return null;
    }

    private static final int
    PLANE = 3
   ,DIA = 5
   ,CELLNAME = 4
   ,DEAD = -1;
   ;

    public static final String
    XYRES = "xy_res"
   ,ZRES = "z_res"
   ,TIMEEND = "time_end"
   ,PLANEEND = "plane_end"
   ,CS = ", "
   ;
    
    public static final float
         HALFROUND = 0.5f
        ;
    public static void main(String[] args) {
    }
}

//private static NucleiMgr iNucleiMgr;
//private static Log       iDLog;
//private static String       iOrientation;
//private static String       iConfigFileName;
/*
public static void setNucleiMgr(NucleiMgr nucleiMgr) {
    iNucleiMgr = nucleiMgr;
    new Throwable().printStackTrace();
}
*/

/*
public static NucleiMgr getNucleiMgr() {
    return iNucleiMgr;
}
*/

/*
public static void setDebugLog(Log debugLog) {
    iDLog = debugLog;
}

public static Log getDLog() {
    return iDLog;
}
*/


/*
public static Nucleus getCurrentCellData(String cellName, int time) {
    Vector nucData = iNucleiMgr.getNucleiRecord()[time - 1];
    return getCurrentCellData(nucData, cellName);
}
*/

/*
public static Vector getNuclei(int time) {
    return iNucleiMgr.getNucleiRecord()[time - 1];
}
*/

/*
public static boolean isValidCell(String name, int time) {
    boolean rtn = false;
    Nucleus n = getCurrentCellData(name, time);
    iDLog.append("isValidCell " + name + CS + time);
    iDLog.append("isValidCell " + n);
    return (n != null);
}
*/
/*
public static Nucleus getNucleusFromHashkey(String hashKey, int time) {
    Nucleus r = null;
    boolean found = false;
    Vector nucData = iNucleiMgr.getNucleiRecord()[time - 1];
    for (int j = 0; j < nucData.size(); j++) {
        Nucleus n = (Nucleus)nucData.elementAt(j);
        if (n.status < 0) continue;
        if (n.hashKey.equals(hashKey)) {
            found = true;
            r = n;
            break;
        }
    }
    return r;
    
}
*/




/*
public static double nucDiameter(Nucleus n, 
        double imgPlane) {
    if (n == null) return -1; //covers some issues re currentCell and not tracking
    double r = -0.5;
    double cellPlane = n.z;
    double R = n.size/2.; //pixels
    double y = (cellPlane - imgPlane)*ZPIXRES/R;
    double r2 = 1 - y*y;
    if (r2 >= 0.) r = Math.sqrt(r2)*R;
    return 2*r;
}

public static boolean hasCircle(Nucleus n,
        double imgPlane) {
    return (nucDiameter(n, imgPlane) > 0);
}
*/
/**
 * called by AceTree when it has read the parameters file 
 */
/*
public static void setZPixRes(double zPixRes) {
    ZPIXRES = zPixRes;
}
*/

/*
public static void setOrientation(String orientation) {
    iOrientation = orientation;
}

public static String getOrientation() {
    return iOrientation;
}
*/

/*
public static void setConfigFileName(String configFileName) {
    iConfigFileName = configFileName;
}

public static String getConfigFileName() {
    return iConfigFileName;
}
*/


//public static double 
//ZPIXRES = 10.
//;

/*
public static void getDaughters(Vector nucData, String parentName, Nucleus anterior, Nucleus posterior) {
    if (parent == null) initializeHashtables();
    Nucleus n = null;
    int countDown = 2;
    for (int j=0; j < nucData.size(); j++) {
        n = (Nucleus)nucData.elementAt(j);
        if (isDaughter(n.identity, parentName)) {
            if (isAnteriorDaughter(n.identity)) {
                n.copyData(anterior);
                countDown--;
            } else {
                n.copyData(posterior);
                countDown--;
            }
            if (countDown == 0) break;
        }
    }
}
*/
/**
 * Adds annotation to the active ImageWindow
 * 
 * @param s String the text to draw (nominally a cell name)
 * @param x int the x location
 * @param y int the y location
 * @param imgWin ImageWindow instance where drawing occurs
 * @param c Color in which the text is drawn
 */
/*
static public void drawString(String s, int x, int y, 
        ImageWindow imgWin, Color c) {
    ImagePlus imgPlus = imgWin.getImagePlus();
    ImageProcessor imgProc = imgPlus.getProcessor();
    ImageCanvas imgCan = imgWin.getCanvas();
    imgProc.setColor(c);
    imgProc.setFont(new Font("SansSerif", Font.PLAIN, 9));
    imgProc.moveTo(imgCan.offScreenX(x),imgCan.offScreenY(y));
    imgProc.drawString(s);
    imgPlus.updateAndDraw();
}
*/

/**
 * Adds annotation to the active ImageWindow in white lettering
 * 
 * @param s String the text to draw (nominally a cell name)
 * @param x int the x location
 * @param y int the y location
 * @param imgWin ImageWindow instance where drawing occurs
 */
/*
static public void drawString(String s, int x, int y, 
        ImageWindow imgWin) {
    drawString(s, x, y, imgWin, Color.white);
}
*/    
/**
 * Displays annotations on the current image. The annotations
 * are provided in a Vector of AnnotInfo objects (name and location)
 * 
 * @param annots Vector of AnnotInfo objects
 * @param imgWin ImageWindow to be annotated
 */
/*
static public void drawStrings(Vector annots, ImageWindow imgWin) {
    ImagePlus imgPlus = imgWin.getImagePlus();
    ImageProcessor imgProc = imgPlus.getProcessor();
    ImageCanvas imgCan = imgWin.getCanvas();
    imgProc.setColor(Color.yellow);
    imgProc.setFont(new Font("SansSerif", Font.BOLD, 13));
    Enumeration e = annots.elements();
    while (e.hasMoreElements()) {
        AnnotInfo ai = (AnnotInfo)e.nextElement();
        imgProc.moveTo(imgCan.offScreenX(ai.iX),imgCan.offScreenY(ai.iY));
        imgProc.drawString(ai.iName);
    }
    imgPlus.updateAndDraw();
}
*/


