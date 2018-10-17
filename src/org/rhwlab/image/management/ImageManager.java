package org.rhwlab.image.management;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import org.rhwlab.image.ParsingLogic.ImageFileParser;
import org.rhwlab.image.ParsingLogic.ImageNameLogic;
import org.rhwlab.image.ZipImage;
import org.rhwlab.utils.C;

import java.io.File;
import java.util.zip.ZipEntry;

/**
 * @javadoc
 * ImageManager.java
 * @author Braden Katzman
 * @author bradenkatzman@gmail.com
 * Date created: 10/05/18
 *
 * This class is a top level manager that is responsible for initializing and maintaining
 * an image dataset. Functionality:
 *
 * - Manage image parameters, configurations and variables by maintaining
 *   the ImageConfig class
 *
 * - Delegate file parsing and name logic operations to ImageFileParser and ImageNameLogic
 *
 * - Handle image flipping and splitting
 *
 * - Manage the ImageWindow state by refreshing its display
 */
public class ImageManager {

    // the main configuration data for the image series
    private ImageConfig imageConfig;

    // utilities used be the manager to carry out tasks on the image series: loading, name parsing
    private ImageFileParser imageFileParser;
    private ImageNameLogic imageNameLogic;

    // runtime variables that are used to manage the image series data as it's viewed by the user (time, plane number, image height, etc.)
    private String currentImageFile;
    private ImagePlus currentImage;
    private int currentImageTime;
    private int currentImagePlane;
    private int imageHeight;
    private int imageWidth;

    public ImageManager(ImageConfig imageConfig) {
        this.imageConfig = imageConfig;

        // let's check the vars. First, let's see if the image file listed in the configuration file exists
        String imageFileFromConfig = imageConfig.getTifPrefix();
        if(!new File(imageFileFromConfig).exists()) {
            System.out.println("The image listed in the config file does not exist on the system. Checking if it's an 8bit image that no longer exists");
            // it doesn't exist. It's likely an 8bit image file name that no longer exists, so let's do a check on the
            // file type first (not completely reliable check) and if it's 8bit, we'll try and find a 16bit image
            if (ImageNameLogic.is8bitImage(imageFileFromConfig)) {
                System.out.println("The image has an 8bit file naming convention -> try and find it's 16bit corollary");
                String newFileNameAttempt = ImageNameLogic.reconfigureImagePathFrom8bitTo16bit(imageFileFromConfig);
                if (!newFileNameAttempt.equals(imageFileFromConfig)) {
                    System.out.println("A 16bit file name was generated from the 8bit image file name in the config file. Checking if it exists");
                    if (new File(newFileNameAttempt).exists()) {
                        System.out.println("16bit image file exists. Updating file in ImageConfig to: " + newFileNameAttempt);
                        this.imageConfig.setTifPrefix(newFileNameAttempt);

                        // because the image series is now known to be 16bit stacks, set the use stack flag to 1
                        this.imageConfig.setUseStack(1);
                    } else {
                        System.out.println("16bit image file name generated from 8bit image file name does not exist on the system. Can't bring up image series.");
                    }
                } else {
                    System.out.println("Attempt to generate 16bit image file name from 8bit image file name failed. Can't bring up image series");
                }
            }
        }

        // avoid errors by setting some default values
        this.currentImageTime = 1;
        this.currentImagePlane = 15; // usually about the middle of the stack
    }

    // methods to set runtime parameters
    public void setCurrImageTime(int time) {
        this.currentImageTime = time;
    }
    public int getCurrImageTime() { return this.currentImageTime; }

    public void setCurrImagePlane(int plane) {
        this.currentImagePlane = plane;
    }
    public int getCurrImagePlane() { return this.currentImagePlane; }

    // METHODS TO MAKE IMAGES
//    public ImagePlus makeImage(String s) {
//        currentImageFile = s;
//        ImagePlus ip;
//
//        switch(this.imageConfig.getUseZip()) {
//            case 0:
//            case 3:
//                ip = doMakeImageFromTif(s);
//                break;
//            case 1:
//                ip = doMakeImageFromZip(s);
//                break;
//            default:
//                ip = doMakeImageFromZip2(s);
//                break;
//
//        }
//
//        if (ip != null) {
//            this.imageWidth = ip.getWidth();
//            this.imageHeight = ip.getHeight();
//        }
////        if (ip == null)
////            return iImgPlus;
//        //else return ip;
//
//        return ip;
//    }

//    public ImagePlus makeImage2(String s, int iplane, int ustack, int splitMode) {
//        //System.out.println("ImageWindow.makeImage2: "+s);
//         = s;
//        ImagePlus ip = null;
//
//        imagewindowPlaneNumber = iplane;
//        imagewindowUseStack = ustack;
//        iSplit = splitMode;
//        switch(cUseZip) {
//            case 0:
//            case 3:
//                ip = doMakeImageFromTif(s);
//                break;
//            case 1:
//                ip = doMakeImageFromZip(s);
//                break;
//            default:
//                ip = doMakeImageFromZip2(s);
//                break;
//        }
//
//        if (ip != null) {
//            cImageWidth = ip.getWidth();
//            cImageHeight = ip.getHeight();
//        }
//
//        if (ip == null) {
//            return iImgPlus;
//        }
//        else {
//            return ip;
//        }
//    }
//
//
//
//    public  ImagePlus doMakeImageFromZip(String s) {
//        if (cZipImage == null) cZipImage = new ZipImage(cZipTifFilePath);
//        ZipEntry ze = cZipImage.getZipEntry(s);
//        ImagePlus ip;
//        if (ze == null) {
//            ip = new ImagePlus();
//            ImageProcessor iproc = new ColorProcessor(cImageWidth, cImageHeight);
//            ip.setProcessor(s, iproc);
//        }
//        else ip = cZipImage.readData(ze);
//        //System.out.println("ImageWindow.makeImage exiting");
//        return ip;
//    }
//
//
//
//    public ImagePlus doMakeImageFromZip2(String s) {
//        cZipImage = new ZipImage(cZipTifFilePath + "/" + s);
//        int k1 = s.indexOf("/") + 1;
//        String ss = s.substring(k1);
//        int k2 = ss.indexOf(".");
//        ss = ss.substring(0, k2);
//        ZipEntry ze = null;
//        if (cZipImage != null) ze = cZipImage.getZipEntry(ss + ".tif");
//        //System.out.println("ZipEntry: " + ze);
//        //if (cZipImage == null) cZipImage = new ZipImage(cZipTifFilePath);
//        //ZipEntry ze = cZipImage.getZipEntry(s);
//        ImagePlus ip;
//        if (ze == null) {
//            ip = new ImagePlus();
//            ImageProcessor iproc = new ColorProcessor(cImageWidth, cImageHeight);
//            ip.setProcessor(s, iproc);
//        }
//        else ip = cZipImage.readData(ze);
//        //System.out.println("ImageWindow.makeImage exiting");
//        //ip = convertToRGB(ip);
//        ColorProcessor iprocColor = (ColorProcessor)ip.getProcessor();
//        int [] all = (int [])iprocColor.getPixels();
//        byte [] R = new byte[all.length];
//        byte [] G = new byte[all.length];
//        byte [] B = new byte[all.length];
//        //ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
//        iprocColor.getRGB(R, G, B);
//        //G = bpix;
//        //R = getRedChannel(R);
//        iRpix = R;
//        iGpix = G;
//        iBpix = B;
//        return ip;
//    }
//
//    public ImagePlus doMakeImageFromTif(String s) {
//        if (cUseZip == 3)
//            s = s.replaceAll("tif", "jpg");
//        //println("ImageWindow.doMakeImageFromTif entered: " + s);
//        cCurrentImagePart = s;
//        //FileInputStream fis;
//        ImagePlus ip = null;
//        String ss = cZipTifFilePath + C.Fileseparator + s;
//        //println("ImageWindow.makeImage entered: " + ss);
//
//        //System.out.println("ImageWindow using stack: "+imagewindowUseStack);
//        if (imagewindowUseStack == 1){
//            System.out.println("ImageWindow doMakeImageFromTif using stack: 1");
//            try {
//                ip = new Opener().openImage(ss, imagewindowPlaneNumber);
//            } catch (IllegalArgumentException iae) {
//                System.out.println("Exception in ImageWindow.doMakeImageFromTif(String)");
//                System.out.println("TIFF file required.");
//            }
//
//        } else{
//            //System.out.println("ImageWindow doMakeImageFromTif using stack: 0");
//            ip = new Opener().openImage(ss);
//        }
//
//        if (ip != null) {
//            cImageWidth = ip.getWidth();
//            cImageHeight = ip.getHeight();
//            //System.out.println("Loaded image width, height: "+cImageWidth+CS+cImageHeight);
//            ip = convertToRGB(ip);
//        } else {
//            ip = new ImagePlus();
//            ImageProcessor iproc = new ColorProcessor(cImageWidth, cImageHeight);
//            ip.setProcessor(s, iproc);
//        }
//
//        return ip;
//    }

}
