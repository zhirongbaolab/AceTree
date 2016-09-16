package org.rhwlab.image;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.io.Opener;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Vector;

import org.rhwlab.acetree.AceTreeNoUI;
import org.rhwlab.acetree.AnnotInfo;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.C;
import org.rhwlab.utils.EUtils;

public class ImageWindowNoUI {

    AceTreeNoUI             iAceTree;
	String					iTitle;
    int                     iImageTime;
    int                     iTimeInc;
    int                     iImagePlane;
    int                     iPlaneInc;

    ImageCanvas             iImgCanvas;

    static Object []        iSpecialEffect;
    public static ImagePlus        	iImgPlus;
    public static NucleiMgr     cNucleiMgr;
    public static String		cTifPrefix;
    public static int           cUseZip;
    public static String        cCurrentImageFile;
    public static int           cImageWidth;
    public static int           cImageHeight;
    public static String        cCurrentImagePart;
    public static String        cZipTifFilePath;
    public static String        cTifPrefixR;
    public static int           cLineWidth;

    static boolean         	cAcbTree = false;
    static byte []          iRpix;
    static byte []          iGpix;
    static byte []          iBpix;

    public ImageWindowNoUI(String s, ImagePlus ip) {
    	iTitle = s;
    	iImgPlus = ip;
        ImageCanvas ic = new ImageCanvas(iImgPlus);
        iImgCanvas = ic;
        //cZipTifFilePath = "/nfs/waterston1/images/murray/102405_pha4red/";
    }

    public void test() {
    	new ij.gui.ImageWindow(iImgPlus);

    }

    public ImagePlus refreshDisplay(String imageName) {
        println("refreshDisplay, " + imageName);
    	if (imageName == null) imageName = iTitle;
        else {
            if (imageName.indexOf(cTifPrefix) == -1) {
                imageName = cTifPrefix +imageName;
            }
            iTitle = imageName;
            //setTitle(iTitle);
        }
            iTimeInc = iAceTree.getTimeInc();
            iPlaneInc = iAceTree.getPlaneInc();
            iImageTime = iAceTree.getImageTime();
            iImagePlane = iAceTree.getImagePlane();
        String random = RANDOMT;
        if (cUseZip > 0) random = RANDOMF;
        int k = imageName.indexOf(random);
        if (k > -1) imageName = imageName.substring(0, k + random.length() - 1 );
        ImagePlus ip = null;

        //System.out.println("ImageWindow.refreshDisplay3: " + imageName);
        ip = makeImage(imageName);
        switch (iAceTree.getColor()) {
            case 1:
                ip = makeGreenImagePlus(ip);
                break;
            case 2:
                ip = makeRedImagePlus(ip);
                break;
            case 3:
                ip = makePlainImagePlus(ip);
                break;
            default:
        }
        //ip = makeGreenImagePlus(ip);

        if (ip != null) iImgPlus.setProcessor(imageName, ip.getProcessor());
        if (iAceTree.isTracking()) iAceTree.addMainAnnotation();
        //if (iAceTree.getShowCentroids()) showCentroids();
        //if (iAceTree.getShowAnnotations()) showAnnotations();
        if (iSpecialEffect != null) showSpecialEffect();
        //iSpecialEffect = null;
        iImgCanvas.repaint();
        return iImgPlus;

    }


    public static ImagePlus makeImage(String s) {
        cCurrentImageFile = s;
        //new Throwable().printStackTrace();
        ImagePlus ip = null;
        //iSpecialEffect = null;
        //if (iSpecialEffect != null) iSpecialEffect = null;

        // this version does not handle zipped stuff
        ip = doMakeImageFromTif(s);


        if (ip != null) {
            cImageWidth = ip.getWidth();
            cImageHeight = ip.getHeight();
        }
        if (ip == null) return iImgPlus;
        else return ip;
    }

    @SuppressWarnings("unused")
	public static ImagePlus doMakeImageFromTif(String s) {
        cCurrentImagePart = s;
        FileInputStream fis;
        ImagePlus ip = null;
        String ss = cZipTifFilePath + C.Fileseparator + s;
        ip = new Opener().openImage(ss);
        if (ip != null) {
            cImageWidth = ip.getWidth();
            cImageHeight = ip.getHeight();
            ip = convertToRGB(ip);
        } else {
            ip = new ImagePlus();
            ImageProcessor iproc = new ColorProcessor(cImageWidth, cImageHeight);
            ip.setProcessor(s, iproc);
        }

        return ip;
    }

    protected static ImagePlus makeRedImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.setRGB(iRpix, new byte[iRpix.length], new byte[iRpix.length]);
        ip.setProcessor("test", iproc3);
        return ip;
    }

    protected static ImagePlus makeGreenImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.setRGB(new byte[iRpix.length], iGpix, new byte[iRpix.length]);
        ip.setProcessor("test", iproc3);
        return ip;
    }

    protected static ImagePlus makePlainImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        if (cAcbTree) {
            //byte [] added = new byte[iRpix.length];
            //for (int i=0; i < iRpix.length; i++) {
             //   added[i] = (byte)(iRpix[i] + iGpix[i]);
            //}
            iproc3.setRGB(iRpix, iRpix, iRpix);
        } else {
            iproc3.setRGB(new byte[iRpix.length], new byte[iRpix.length], new byte[iRpix.length]);

        }
        ip.setProcessor("test", iproc3);
        return ip;
    }

    @SuppressWarnings("unused")
	private static byte[] getRedChannel(byte [] R) {
        String fileName = makeRedChannelName();
        File f = new File(fileName);
        if (f.exists()) {
            FileInputStream fis;
            ImagePlus ip = null;
            ip = new Opener().openImage(fileName);
            if (ip != null) {
                ByteProcessor bproc = (ByteProcessor)ip.getProcessor();
                R = (byte [])bproc.getPixels();
            } else {
                System.out.println("getRedChannel, Opener returned null ip");
            }
        } else {
            //System.out.println("getRedChannel, file does not exist");

        }
        return R;

    }

    private static String makeRedChannelName() {
        // 20071108 rehacked this because windows vista was very picky
        // and backslashes were plagueing me
        // the green parsing was working so I created cCurrentImagePart
        // to go from there to red by substituting "tifR" for "tif"
        String s = cCurrentImageFile;
        String ss = cCurrentImagePart;
        ss = ss.substring(3);
        s = cZipTifFilePath + C.Fileseparator + "/tifR/" + ss;
        return s;
    }

    /**
     * If the images in the zip archive are 8 bit tiffs,
     * we use that as the green plane of an RGB image processor
     * so the program is always showing RGB images
     *
     * @param ip an Image processor obtained from the image file
     * @return
     */
    private static ImagePlus convertToRGB(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        byte [] bpix = (byte [])iproc.getPixels();
        byte [] R = new byte[bpix.length];
        byte [] G = new byte[bpix.length];
        byte [] B = new byte[bpix.length];
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.getRGB(R, G, B);
        // special test removal
        G = bpix;
        R = getRedChannel(R);
        iRpix = R;
        iGpix = G;
        iBpix = B;
        return buildImagePlus(ip);
    }

    private static ImagePlus buildImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.setRGB(iRpix, iGpix, iBpix);
        ip.setProcessor("test", iproc3);
        return ip;

    }

    public ImagePlus getImagePlus() {
        return iImgPlus;
    }



    @SuppressWarnings("unused")
	public void showCentroids() {
    	Color nucColor = new Color(140,70,255);
    	//*
    	int time = iImageTime + iTimeInc;
        if (time < 0) {
            iImageTime = 1;
            iTimeInc = 0;
        }
        Vector v = (Vector)cNucleiMgr.getNucleiRecord().elementAt(iImageTime + iTimeInc - 1);
        ImageProcessor iproc = getImagePlus().getProcessor();
        iproc.setColor(Color.WHITE);
        iproc.setLineWidth(2);
        Polygon p = null;
        Enumeration e = v.elements();
        Cell currentCell = null; //iAceTree.getCurrentCell();
        iproc.setColor(nucColor);
        while(e.hasMoreElements()) {
            Nucleus n = (Nucleus)e.nextElement();
            if (n.status < 0) continue;
            double x = cNucleiMgr.nucDiameter(n,
                    iImagePlane + iPlaneInc);
            if (x > 0) {
                if (currentCell != null && n.hashKey != null && n.hashKey.equals(currentCell.getHashKey()) && iAceTree.isTracking()) {
                    iproc.setColor(nucColor);
                }
                iproc.drawPolygon(EUtils.pCircle(n.x, n.y, (int)(x/2.)));
                iproc.setColor(nucColor);
            }

        }
        //*/
    }

    public void showSingleCentroid(Nucleus n) {
        ImageProcessor iproc = getImagePlus().getProcessor();
        iproc.setColor(Color.WHITE);
        iproc.setLineWidth(2);
        double x = cNucleiMgr.nucDiameter(n,iImagePlane + iPlaneInc);
        if (x > 0) iproc.drawPolygon(EUtils.pCircle(n.x, n.y, (int)(x/2.)));

    }

    public void showSingleAnnotation(Nucleus n, Graphics g) {
    	if (1 == 2) return;
    	Vector annots = new Vector();
    	AnnotInfo ai = new AnnotInfo(n.identity, n.x, n.y);
        if (cNucleiMgr.hasCircle(n, iImagePlane + iPlaneInc)) {
            annots.add(ai);
        }
        //drawStrings(annots, this);
        //Image img = createBufferedImage();
        //Graphics g = img.getGraphics();
        g.setColor(Color.WHITE);
        g.drawString(n.identity, n.x, n.y);
        //g.dispose();

    }

    @SuppressWarnings("unused")
	public void showAnnotations(Graphics g) {
        //*
    	//showWhichAnnotations();
        Vector v = (Vector)cNucleiMgr.getNucleiRecord().elementAt(iImageTime  + iTimeInc - 1);
        int size = v.size();
        int [] x = new int[size];
        int [] y = new int[size];
        Vector annots = new Vector();
        Enumeration e = v.elements();
        while(e.hasMoreElements()) {
            AnnotInfo ai = null;
            Nucleus n = (Nucleus)e.nextElement();
            //if (n.identity.length() > 0 && isInList(n.identity)) {
            if (n.status >= 0 /*&& (isInList(n.identity) != null)*/) {
                ai = new AnnotInfo(n.identity, n.x, n.y);
                if (cNucleiMgr.hasCircle(n, iImagePlane + iPlaneInc)) {
                    annots.add(ai);
                }
            }
        }
        drawStrings(annots, g);
        //NucUtils.drawStrings(annots, this);
        //iShow.setText(HIDE);
        //*/
    }

    private void drawStrings(Vector annots, Graphics g) {
        g.setColor(Color.WHITE);
        Enumeration e = annots.elements();
        while (e.hasMoreElements()) {
            AnnotInfo ai = (AnnotInfo)e.nextElement();
            g.drawString(ai.iName, ai.iX, ai.iY);
        }
    }



    @SuppressWarnings("unused")
	public void showAnnotations() {
        //*
    	//showWhichAnnotations();
        Vector v = (Vector)cNucleiMgr.getNucleiRecord().elementAt(iImageTime  + iTimeInc - 1);
        int size = v.size();
        int [] x = new int[size];
        int [] y = new int[size];
        Vector annots = new Vector();
        Enumeration e = v.elements();
        while(e.hasMoreElements()) {
            AnnotInfo ai = null;
            Nucleus n = (Nucleus)e.nextElement();
            //if (n.identity.length() > 0 && isInList(n.identity)) {
            if (n.status >= 0 /*&& (isInList(n.identity) != null)*/) {
                ai = new AnnotInfo(n.identity, n.x, n.y);
                if (cNucleiMgr.hasCircle(n, iImagePlane + iPlaneInc)) {
                    annots.add(ai);
                }
            }
        }
        drawStrings(annots, this);
        //NucUtils.drawStrings(annots, this);
        //iShow.setText(HIDE);
        //*/
    }

    private void drawStrings(Vector annots, ImageWindowNoUI imgWin) {
        ImagePlus imgPlus = imgWin.getImagePlus();
        ImageProcessor imgProc = imgPlus.getProcessor();
        ImageCanvas imgCan = imgWin.getCanvas();
        imgProc.setColor(Color.WHITE);
        imgProc.setFont(new Font("SansSerif", Font.BOLD, 13));
        Enumeration e = annots.elements();
        while (e.hasMoreElements()) {
            AnnotInfo ai = (AnnotInfo)e.nextElement();
            imgProc.moveTo(imgCan.offScreenX(ai.iX),imgCan.offScreenY(ai.iY));
            imgProc.drawString(ai.iName);
        }
        imgPlus.updateAndDraw();
    }

    public ImageCanvas getCanvas() {
        return iImgCanvas;
    }



    protected void showSpecialEffect() {
        /*
    	if (!iAceTree.isTracking()) return;
        int x1 = ((Integer)iSpecialEffect[0]).intValue();
        int y1 = ((Integer)iSpecialEffect[1]).intValue();
        int z1 = ((Integer)iSpecialEffect[2]).intValue();
        int x2 = ((Integer)iSpecialEffect[3]).intValue();
        int y2 = ((Integer)iSpecialEffect[4]).intValue();
        int r2 = ((Integer)iSpecialEffect[5]).intValue();
        int z2 = ((Integer)iSpecialEffect[6]).intValue();
        String s = (String)iSpecialEffect[7];
        int offset = r2 + 4;
        if (y2 < y1) offset = -offset;


        ImageProcessor iproc = getImagePlus().getProcessor();
        //iproc.setColor(Color.magenta);
        iproc.setColor(COLOR[iDispProps[LOWERSIS].iLineageNum]);
        if (z2 <= z1) iproc.setColor(COLOR[iDispProps[UPPERSIS].iLineageNum]);
        //if (z2 <= z1) iproc.setColor(Color.cyan);

        iproc.setLineWidth(cLineWidth);
        //iproc.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
        iproc.drawLine(x1, y1, x2, y2);
        iproc.drawPolygon(EUtils.pCircle((int)x2, (int)y2, (int)r2));
        iproc.drawString("    " + s + "(" + z2 + ")", x2, y2 + offset);
        */
    }

    public void addAnnotation(int mx, int my, boolean dontRemove) {
        /*
    	if (iIsMainImgWindow) {
            iTimeInc = iAceTree.getTimeInc();
            iImageTime = iAceTree.getImageTime();
            iPlaneInc = iAceTree.getPlaneInc();
        } else {
            iTimeInc = 0;
            iPlaneInc = 0;
        }
        double x, y, r;
        boolean g;
        Nucleus n = cNucleiMgr.findClosestNucleus(mx, my, iImagePlane + iPlaneInc, iImageTime + iTimeInc);
            if (cNucleiMgr.hasCircle(n, (double)(iImagePlane + iPlaneInc))) {
                AnnotInfo ai = new AnnotInfo(n.identity, n.x, n.y);
                // now, if this one is not in the vector add it
                // otherwise remove it
                boolean itemRemoved = false;
                boolean itemAlreadyPresent = false;
                String test = n.identity;
                AnnotInfo aiTest = null;
                for (int k=0; k < iAnnotsShown.size(); k++) {
                    aiTest =(AnnotInfo)iAnnotsShown.elementAt(k);
                    if (aiTest.iName.equals(test)) {
                        itemAlreadyPresent = true;
                        if (!dontRemove) {
                            iAnnotsShown.remove(k);
                            itemRemoved = true;
                        }
                        break;
                    }

                }

                if (!itemRemoved && !itemAlreadyPresent) {
                    iAnnotsShown.add(ai);
                }
            }
		*/


    }

    public static void setNucleiMgr(NucleiMgr nucleiMgr) {
        cNucleiMgr = nucleiMgr;
    }


    public static void setStaticParameters(String zipTifFilePath, String tifPrefix, int useZip) {
        cZipTifFilePath = zipTifFilePath;
        cTifPrefix = tifPrefix;
        cUseZip = useZip;
        //if (cUseZip == 1) cZipImage = new ZipImage(cZipTifFilePath);
        cLineWidth = 2;//LINEWIDTH;
        String [] sa = cTifPrefix.split("/");
        if(sa.length > 1) cTifPrefixR = sa[0] + "R" + C.Fileseparator + sa[1];
    }

    public void setAceTree(AceTreeNoUI aceTree) {
        iAceTree = aceTree;
    }

    public ImageWindowNoUI getImageWindow() {
    	return this;
    }

    protected static final String
    RANDOMF = ".zip0"
   ,RANDOMT = ".tif0"
   ,DASHT = "-t"
   ,DASHP = "-p"
   ;

	public BufferedImage createBufferedImage() {
		Image image = iImgPlus.getImage();
        BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
		return bimage;
	}
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {return ((BufferedImage)image).getColorModel().hasAlpha();}

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {pg.grabPixels();} catch (InterruptedException e) {}

        // Get the image's color model
        return pg.getColorModel().hasAlpha();
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

    private static void println(String s) {System.out.println(s);}
    private static void print(String s) {System.out.print(s);}
    private static final String CS = ", ";
    private static final String TAB = "\t";
    private static final DecimalFormat DF0 = new DecimalFormat("####");
    private static final DecimalFormat DF1 = new DecimalFormat("####.#");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt4(double d) {return DF4.format(d);}
    private static String fmt1(double d) {return DF1.format(d);}
    private static String fmt0(double d) {return DF0.format(d);}

}
