/*
 * Created on May 1, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.image;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.image.AuxImageWindowXZ;
import org.rhwlab.image.AuxImageWindowYZ;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.image.ZipImage;
import org.rhwlab.snight.NucleiMgr;
import java.lang.Math;
/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DepthViews extends JPanel implements ActionListener {

    JFrame      iFrame;
    JSpinner    iTime;
    JSpinner    iXYLocation;
    JButton     iXZView;
    JButton     iYZView;

    AceTree     iAceTree;
    NucleiMgr   iNucleiMgr;

    String      iImageName;

    public DepthViews(String s) {
        setPreferredSize(new Dimension(200, 200));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        iFrame = new JFrame();
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        //makeUI();


        showMe();
    }

//    private void makeUI() {
//        setLayout(new GridLayout(0,1));
//        Border blackline = BorderFactory.createLineBorder(Color.black);
//        JPanel p = new JPanel();
//        p.add(new JLabel("time:"));
//        SpinnerModel model = new SpinnerNumberModel(100, 1, iNucleiMgr.getEndingIndex(), 1);
//        iTime = new JSpinner(model);
//        p.add(iTime);
//        p.setBorder(blackline);
//        add(p);
//        p = new JPanel();
//        p.add(new JLabel("x or y loc:"));
//        model = new SpinnerNumberModel(250, 10, ImageWindow.cImageWidth, 10);
//        iXYLocation = new JSpinner(model);
//        p.add(iXYLocation);
//        p.setBorder(blackline);
//        add(p);
//        iYZView = new JButton("YZ View");
//        iYZView.addActionListener(this);
//        add(iYZView);
//        iXZView = new JButton("XZ View");
//        iXZView.addActionListener(this);
//        add(iXZView);
//    }

//    private void yzView(int time, int x) {
//        ImagePlus iplus = getOneYZ(time, x);
//        println("yzView, " + iplus.getWidth() + CS + iplus.getHeight() + CS + ImageWindow.cImageHeight + CS + iNucleiMgr.getPlaneEnd());
//        ImageProcessor iproc = iplus.getProcessor();
//        String s2 = iImageName +" @ x = " + x;
//        new AuxImageWindowYZ(s2, iproc, iAceTree, x);
//    }
//
//    private void xzView(int time, int y) {
//        ImagePlus iplus = getOneXZ(time, y);
//        ImageProcessor iproc = iplus.getProcessor();
//        String s2 = iImageName +" @ y=" + y;
//        new AuxImageWindowXZ(s2, iproc, iAceTree, y);
//
//    }

//    private ImagePlus getOneYZ(int time, int x) {
//	int factor=(int)Math.round(iAceTree.getiZPixRes());
//        int planeEnd = iNucleiMgr.getPlaneEnd();
//        ByteProcessor bp = new ByteProcessor(factor * planeEnd - 1, ImageWindow.cImageHeight);
//        for (int m=1; m <= planeEnd; m++) {
//            ImageProcessor ip = getImage(time, m);
//            if (ip == null) break;
//            //println("test2: " + ip);
//            for (int i=0; i < ip.getHeight(); i++) {
//                int p = ip.getPixel(x, i);
//                for (int k=0; k < factor; k++) {
//                    bp.putPixel(k + m*factor, i, p);
//                }
//            }
//        }
//        String s = time + "_" + x;
//        ImagePlus iplus = new ImagePlus(s, bp);
//        return iplus;
//
//    }

//    private ImagePlus getOneXZ(int time, int y) {
//	int factor=(int)Math.round(iAceTree.getiZPixRes());
//        int planeEnd = iNucleiMgr.getPlaneEnd();
//        ByteProcessor bp = new ByteProcessor(ImageWindow.cImageWidth,factor * planeEnd - 1);
//        for (int m=1; m <= planeEnd; m++) {
//            ImageProcessor ip = getImage(time, m);
//            if (ip == null) break;
//            println("test2: " + ip + CS + time + CS + m + CS + planeEnd);
//
//            for (int i=0; i < ip.getWidth(); i++) {
//                int p = ip.getPixel(i, y);
//                for (int k=0; k < factor; k++) {
//                    bp.putPixel(i, k + m*factor, p);
//                    //bp.putPixel(k + m*11, i, p);
//                }
//            }
//        }
//        String s = time + "_" + y;
//        ImagePlus iplus = new ImagePlus(s, bp);
//        return iplus;
//
//    }

//    private ImageProcessor getImage(int time, int plane) {
//        String imageFile = ImageWindow.cZipTifFilePath;
//        imageFile += "/" + ImageWindow.cTifPrefix;
//        //imageFile += iAceTree.makeImageName(time, plane);
//        int k = imageFile.lastIndexOf("/");
//        String s = imageFile.substring(k + 1);
//        k = s.lastIndexOf("-");
//        s = s.substring(0, k);
//        //println("getImage: " + s);
//        iImageName = s;
//        ImageProcessor ip = getRedData(imageFile);
//        return ip;
//    }

//    private ImageProcessor getRedData(String greenName) {
//        //System.out.println("getRedData: " + greenName + C.CS + ImageWindow.cUseZip);
//        if (ImageWindow.cUseZip == 2) return getRedZipData(greenName);
//        FileInputStream fis;
//        ImagePlus ip = null;
//        //String ss = "/home/biowolp/AncesTree/temp2/images/050405-t050-p15.tif";
//        try {
//            fis = new FileInputStream(greenName);
//            byte [] ba = ImageWindow.readByteArray(fis);
//            ip = ImageWindow.openTiff(new ByteArrayInputStream(ba), false);
//            fis.close();
//	    if(ImageWindow.imagewindowUseStack==1) {
//		int markerChannel=1;
//		ip=ImageWindow.splitImage(ip,markerChannel);
//	    }
//            //ip = readData(fis);
//        } catch(IOException ioe) {
//            //System.out.println("ImageWindow.test3 exception ");
//            //System.out.println(ioe);
//        }
//        if (ip != null) return ip.getProcessor();
//        else return null;
//    }


    private ImageProcessor getRedZipData(String redName) {
        ZipImage zipImage = new ZipImage(redName);
        int k1 = redName.lastIndexOf("/") + 1;
        String ss = redName.substring(k1);
        int k2 = ss.indexOf(".");
        ss = ss.substring(0, k2);
        //System.out.println("using: " + ss);
        ZipEntry ze = null;
        if (zipImage != null) ze = zipImage.getZipEntry(ss + ".tif");
        //System.out.println("ZipEntry: " + ze);
        //if (cZipImage == null) cZipImage = new ZipImage(cZipTifFilePath);
        //ZipEntry ze = cZipImage.getZipEntry(s);
        ImagePlus ip = null;
        ip = zipImage.readData(ze, true);
        if (ip != null) return ip.getProcessor();
        else return null;
    }



    public void showMe() {
        //iFrame = new JFrame(iLog.iTitle);
        iFrame.setTitle("DepthViews");
        iFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        iFrame.setContentPane(this);
        iFrame.pack();
        iFrame.setLocationRelativeTo(iAceTree);
        iFrame.setVisible(true);

    }


    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
	public void actionPerformed(ActionEvent e) {
        int time = ((Integer)(iTime.getValue())).intValue();
        int xy = ((Integer)(iXYLocation.getValue())).intValue();
        println("actionPerformed: " + time + CS + xy);
        Object o = e.getSource();
        if (o == iYZView) {
            System.out.println("DepthViews yzView() disabled.");
            //yzView(time, xy);

        } else if (o == iXZView) {
            System.out.println("DepthViews xzView() disabled.");
            //xzView(time, xy);
        }

    }

    public static void main(String[] args) {
    }
    private static void println(String s) {System.out.println(s);}
    private static final String CS = ", ";

}
