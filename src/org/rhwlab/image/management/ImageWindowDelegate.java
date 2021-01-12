package org.rhwlab.image.management;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.Cell;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * ImageWindowDelegate carries out various tasks that are linked to the ImageWindow. These functionality include:
 * - Click handling
 * - Image annotation management
 * - Image saving management
 *
 * These tasks are facilitated by this class for organizational purposes
 *
 * Date: 10/2018
 * @author Braden Katzman
 */
public class ImageWindowDelegate {
    // vars created and maintained by this class
    private ImageAnnotationManager imageAnnotationManager;
    private ImageSavingManager imageSavingManager;

    // vars given to this class to carry out the tasks of its delegates
    private ImageWindow imageWindow;
    private ImageManager imageManager;

    private NucleiMgr nucleiMgr;

    public ImageWindowDelegate(ImageWindow imageWindow, ImageManager imageManager, NucleiMgr nucleiMgr) {
        this.imageWindow = imageWindow;
        this.imageManager = imageManager;

        this.nucleiMgr = nucleiMgr;

        this.imageAnnotationManager = new ImageAnnotationManager(this.imageWindow, this.imageManager, this.nucleiMgr);
        this.imageSavingManager = new ImageSavingManager();
    }

    ///////////////////////// MOUSE HANDLING //////////////////////////////////////
//    private class WinEventMgr extends WindowAdapter {
//        @Override
//        public void windowGainedFocus(WindowEvent e) {
//            //System.out.println("windowGainedFocus, ");
//            //refreshDisplay(null);
//            //iAceTree.requestFocus();
//        }
//
//        @Override
//        public void windowClosing(WindowEvent e) {
//            //System.out.println("windowClosing: " + iIsMainImgWindow);
//            if (iIsMainImgWindow)
//                dispose();
//        }
//    }
//
//    class MouseHandler extends MouseInputAdapter {
//        ImageWindow iw;
//        public MouseHandler(ImageWindow iw) {
//            super();
//            this.iw=iw;
//        }
//
//        @Override
//        public void mouseMoved(MouseEvent e) {
//            //handle zoom view transform to original coordinate system
//            int x = e.getX();
//            int y = e.getY();
//            int x2 = iImageZoomerPanel.transform(x);
//            //(int)Math.round(x * 100./ (double)m_imagePanel.getZoomedTo());
//            int y2 = iImageZoomerPanel.transform(y);
//            // (int)Math.round(y * 100./ (double)m_imagePanel.getZoomedTo());
//            MouseEvent e2 = new MouseEvent(iw, 0, 0, 0, x2, y2, 0, false, e.getButton());
//            try {
//                iAceTree.mouseMoved(e2);
//            }
//            catch (NullPointerException npe) {
//                // If the image is copiedw ith the END hotkey, it will not have a reference to AceTree
//                // AceTree will be null in this case
//                return;
//            }
//        }
//
//        @Override
//        public void mouseClicked(MouseEvent e) {
//            int x = e.getX();
//            int y = e.getY();
//            int x2 = iImageZoomerPanel.transform(x);
//            //(int)Math.round(x * 100./ (double)m_imagePanel.getZoomedTo());
//            int y2 = iImageZoomerPanel.transform(y);
//            //println("ImageWindow.mouseClicked, " + e.getX() + CS + e.getY());
//            int button = e.getButton();
//
//            // e.BUTTON3 ie right click -DT
//            if (button == MouseEvent.BUTTON3|e.isControlDown()) {
//                iIsRightMouseButton = true;
//            } else {
//                iIsRightMouseButton = false;
//            }
//            if (button == MouseEvent.BUTTON3|e.isControlDown()) {
//                Nucleus n = cNucleiMgr.findClosestNucleus(x2,y2, iImagePlane + iPlaneInc, iImageTime + iTimeInc);
//                if (n == null) {
//                    //System.out.println("No nucleus selected to be active, cannot set current cell.");
//                    return;
//                }
//                Cell c = iAceTree.getCellByName(n.identity);
//                if (c != null) {
//                    iAceTree.setCurrentCell(c, iImageTime + iTimeInc, AceTree.RIGHTCLICKONIMAGE);
//                    //System.out.println("Current cell set to "+n.identity);
//                }
//            }
//            else if (button == MouseEvent.BUTTON1){
//                //System.out.println("mouseClicked " + e.getX());
//                addAnnotation(x2, y2, false);
//                refreshDisplay(null);
//            }
//
//            iAceTree.cellAnnotated(getClickedCellName(x2, y2));
//            iAceTree.updateDisplay();
//            //if (cEditImage3 != null) cEditImage3.processEditMouseEvent(e);
//            MouseEvent e2 = new MouseEvent(iw, 0, 0, 0, x2, y2, 0, false, e.getButton());
//            processEditMouseEvent(e2);
//        }
//
//    }

    //////////////////////////////////////// END MOUSE HANDLING ///////////////////////////////////


    ////////////////////////////////////// ANNOTATION HANDLING ///////////////////////////////////
    public void addMainAnnotation(int cellX, int cellY, boolean doNotRemove) { this.imageAnnotationManager.addAnnotation(cellX, cellY, doNotRemove); }

    ///////////////////////////////////// END ANNOTATION HANDLING ///////////////////////////////




    //////////////////////////////////// SAVING HANDLING ////////////////////////////////////////

    /////////////////////////////////// END SAVING HANDLING ////////////////////////////////////
}
