package org.rhwlab.image.management;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.OvalRoi;
import ij.process.ImageProcessor;
import org.rhwlab.acetree.AnnotInfo;
import org.rhwlab.acetree.PartsList;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.EUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

import static org.rhwlab.image.ImageWindow.iDispProps;

/**
 *
 *
 * This class will need access to the nuclei manager to be able to query the name of an entity of interest
 */
public class ImageAnnotationManager {
    // runtime variables
    private Vector annotationsShown;
    private NucleiMgr nucleiMgr;

    private ImageWindow imageWindow;
    private ImageManager imageManager;

    private DefaultListModel bookmarkListModel;

    // static variables
    public static int lineWidth;

    private String [] COLORS = {
            "red"
            ,"blue"
            ,"green"
            ,"yellow"
            ,"cyan"
            ,"magenta"
            ,"pink"
            ,"gray"
            ,"white"

    };
    private static final Color [] COLOR = {
            Color.RED
            ,new Color(140,70,255)
            ,Color.GREEN
            ,Color.YELLOW
            ,Color.CYAN
            ,Color.MAGENTA
            ,Color.PINK
            ,Color.LIGHT_GRAY
            ,Color.WHITE
    };

    private static final int
            NCENTROID = 0
            ,SCENTROID = 1
            ,ANNOTATIONS = 2
            ,UPPERSIS = 3
            ,LOWERSIS = 4
            ,LINEWIDTH = 5
            ,BMCENTROID = 7
            ;

    public static final int [] WIDTHS = {1,2,3,4,5,6,7,8,9,10};

    public ImageAnnotationManager(ImageWindow imageWindow, ImageManager imageManager, NucleiMgr nucleiMgr) {
        this.imageWindow = imageWindow;
        this.imageManager = imageManager;
        this.nucleiMgr = nucleiMgr;

        this.annotationsShown = new Vector();
    }


    public void addAnnotation(int mx, int my, boolean dontRemove) {
        Nucleus n = this.nucleiMgr.findClosestNucleus(mx, my, this.imageManager.getCurrImagePlane(), this.imageManager.getCurrImageTime());
        if (n != null) {
            if (this.nucleiMgr.hasCircle(n, this.imageManager.getCurrImagePlane())) {
                String propername = PartsList.lookupSulston(n.identity);
                String label = n.identity;
                if (propername != null) {
                    label = label + " " + propername;
                }
                AnnotInfo ai = new AnnotInfo(label, n.x, n.y);
                // now, if this one is not in the vector add it
                // otherwise remove it
                boolean itemRemoved = false;
                boolean itemAlreadyPresent = false;
                String test = label;//n.identity;
                AnnotInfo aiTest = null;
                for (int k = 0; k < annotationsShown.size(); k++) {
                    aiTest = (AnnotInfo) annotationsShown.elementAt(k);
                    if (aiTest.iName.equals(test)) {
                        itemAlreadyPresent = true;
                        if (!dontRemove) {
                            annotationsShown.remove(k);
                            itemRemoved = true;
                        }
                        break;
                    }

                }

                if (!itemRemoved && !itemAlreadyPresent) {
                    annotationsShown.add(ai);
                }

                // if this was a button 3 mouse click
                // and this is the main window
                // we will make this the current cell and makeDisplayText agree
                if (imageWindow.isRightMouseButtonDown() && imageWindow.isMainImgWindow()) {
                    imageWindow.setiIsRightMouseButton(false);
                }
            }
        }
    }

    /**
     *
     * @param currentCell
     * @param isTracking
     */
    public void showCentroids(Cell currentCell, boolean isTracking) {
        Vector v = nucleiMgr.getElementAt(imageManager.getCurrImageTime() - 1);

        ImageProcessor iproc = this.imageManager.getCurrentImage().getProcessor();
        iproc.setColor(COLOR[iDispProps[NCENTROID].iLineageNum]);
        iproc.setLineWidth(WIDTHS[iDispProps[LINEWIDTH].iLineageNum]);
        Polygon p = null;

        Enumeration e = v.elements();
        while(e.hasMoreElements()) {
            Nucleus n = (Nucleus)e.nextElement();
            if (n.status < 0)
                continue;

            double x = nucleiMgr.nucDiameter(n,
                    imageManager.getCurrImagePlane());
            if (x > 0) {
                // Manage bookmarked cells
                if (bookmarkListModel != null && !bookmarkListModel.isEmpty()) {
                    String name = n.identity;
                    if (bookmarkListModel.contains(name))
                        iproc.setColor(COLOR[iDispProps[BMCENTROID].iLineageNum]);
                }
                if (currentCell != null && n.hashKey != null && n.hashKey.equals(currentCell.getHashKey()) && isTracking) {
                    iproc.setColor(COLOR[iDispProps[SCENTROID].iLineageNum]);
                }
                int TOGGLE_OPTION = 6; // toggle option colorscheme display property is 6th element in iDispProp
                if(iDispProps[TOGGLE_OPTION].iLineageNum == 0) { // don't toggle, default to empty circles
                    iproc.drawPolygon(EUtils.pCircle(n.x, n.y, (int)(x/2.)));
                } else {
                    iproc.drawDot(n.x, n.y);
                }
                iproc.setColor(COLOR[iDispProps[NCENTROID].iLineageNum]);
            }
        }
    }

    public void showAnnotations() {
        //showWhichAnnotations();
        Vector v = nucleiMgr.getNucleiRecord().elementAt(imageManager.getCurrImageTime() - 1);
        int size = v.size();
        int [] x = new int[size];
        int [] y = new int[size];
        Vector annots = new Vector();
        Enumeration e = v.elements();
        while(e.hasMoreElements()) {
            AnnotInfo ai = null;
            Nucleus n = (Nucleus)e.nextElement();
            String propername = PartsList.lookupSulston(n.identity);
            String label = n.identity;
            //System.out.println("name is "+label+" "+propername);
            if(propername != null){
                label = label + " " + propername;
            }

            if (n.status >= 0 && (isInList(label) != null)) {
                ai = new AnnotInfo(label, n.x, n.y);
                if (nucleiMgr.hasCircle(n, imageManager.getCurrImagePlane())) {
                    annots.add(ai);
                }
            }
        }
        drawStrings(annots);
        //NucUtils.drawStrings(annots, this);
        //iShow.setText(HIDE);
    }

    private void drawStrings(Vector annots) {
        ImagePlus imgPlus = this.imageWindow.getImagePlus();
        ImageProcessor imgProc = imgPlus.getProcessor();
        ImageCanvas imgCan = this.imageWindow.getCanvas();
        //imgProc.setColor(Color.yellow);
        //System.out.println("iDispProps: " + iDispProps);
        imgProc.setColor(COLOR[iDispProps[ANNOTATIONS].iLineageNum]);
        imgProc.setFont(new Font("SansSerif", Font.BOLD, 13));
        Enumeration e = annots.elements();
        while (e.hasMoreElements()) {
            AnnotInfo ai = (AnnotInfo)e.nextElement();
            imgProc.moveTo(imgCan.offScreenX(ai.iX),imgCan.offScreenY(ai.iY));

            // If there is a proper name appended, shwo only the proper name
            String name = ai.iName;
            int i = name.indexOf(" ");
            if (i > 0)
                name = name.substring(i+1, name.length());
            imgProc.drawString(name);
        }
        imgPlus.updateAndDraw();
    }

    public void updateCurrentCellAnnotation(Cell newCell, Cell old, int time) {
        //new Throwable().printStackTrace();
        //println("updateCurrentCellAnnotation: " + newCell + CS + old + CS + time);
        AnnotInfo ai = null;
        if (old != null) ai = isInList(old.getName());
        if (ai != null) annotationsShown.remove(ai);
        if (time == -1) time = newCell.getTime();
        String s = newCell.getHashKey();
        Nucleus n = null;
        //println("updateCurrentCellAnnotation:2 " + s);
        if (s != null) {
            n = nucleiMgr.getNucleusFromHashkey(newCell.getHashKey(), time);
            //println("updateCurrentCellAnnotation:3 " + n);
        }
        if ((n != null) && (isInList(newCell.getName()) == null)) {
            ai = new AnnotInfo(newCell.getName(), n.x, n.y);
            annotationsShown.add(ai);
        }
    }

    public void clearAnnotations() {
        annotationsShown.clear();
    }

    public void addAnnotation(String name, int x, int y) {
        AnnotInfo ai = new AnnotInfo(name, x, y);
        annotationsShown.add(ai);
    }

    protected AnnotInfo isInList(String name) {
        //System.out.println("isInList: " + name + CS + iAnnotsShown.size());
        AnnotInfo aiFound = null;
        Enumeration e = annotationsShown.elements();
        while(e.hasMoreElements()) {
            AnnotInfo ai = (AnnotInfo)e.nextElement();
            boolean is = ai.iName.equals(name);
            if (is) {
                aiFound = ai;
                break;
            }
        }
        return aiFound;
    }

    public void setBookmarkList(ListModel list) {
        this.bookmarkListModel = (DefaultListModel)list;
    }
}
