package org.rhwlab.image.management;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.Opener;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.utils.C;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

/**
 * Handles 16bit to 8bit conversion
 *
 */
public class ImageConversionManager {

    /**
     * If the images in the zip archive are 8 bit tiffs,
     * we use that as the green plane of an RGB image processor
     * so the program is always showing RGB images
     *
     * @param ip an Image processor obtained from the image file
     * @return
     */
    public static ImagePlus convertToRGB(ImagePlus ip, int useStack, int splitStack, boolean setOriginalContrastValues) {
        //System.out.println("Image width, height: "+ip.getWidth()+CS+ip.getHeight());
        //System.out.println("convertToRGB entered");
        // this is where ted put code for splitting which need to test

        // this is a check for whether we are using 8bit (useStack = 0) or 16bit (useStack = 1)
        if(useStack == 1) {
            FileInfo fi = new FileInfo();
            fi = ip.getFileInfo();
            //need this

            ImageProcessor iproc = ip.getProcessor();

            iproc.flipHorizontal();

            int pixelCount = iproc.getPixelCount();
            int ipwidth = iproc.getWidth();
            int ipheight = iproc.getHeight();
            //System.out.println("cSplitChannelImage: "+cSplitChannelImage);
            if (splitStack == 1) {
                pixelCount /= 2;
                ipwidth /= 2;
            }

            byte [] G = new byte[pixelCount];
            byte [] R = new byte[pixelCount];
            byte [] B = new byte[pixelCount];

            ColorProcessor iproc3 = new ColorProcessor(ipwidth, ipheight);

            // this indicates 16bit images are present (because useStack = 1), *and* they should be split into two channels
            if (splitStack == 1) {
                iproc.setRoi(new Rectangle(ip.getWidth()/2, 0, ip.getWidth()/2, ip.getHeight()));
                ImageProcessor croppedR = iproc.crop();
                ImagePlus croppedIPR = new ImagePlus(ip.getTitle(), croppedR);

                iproc.setRoi(new Rectangle(0, 0, ip.getWidth()/2, ip.getHeight()));
                ImageProcessor croppedG = iproc.crop();
                ImagePlus croppedIPG = new ImagePlus(ip.getTitle(), croppedG);

                if (setOriginalContrastValues){
                    // Set contrast values from original image
                    int ipminred = (int)(croppedIPR.getDisplayRangeMin());
                    int ipmaxred = (int)(croppedIPR.getDisplayRangeMax());
                    System.out.println("ImageWindow set Red min, max from image: "+ipminred+CS+ipmaxred);
                    ImageWindow.contrastmin1 = ipminred;
                    ImageWindow.contrastmax1 = ipmaxred;

                    int ipmingre = (int)(croppedIPG.getDisplayRangeMin());
                    int ipmaxgre = (int)(croppedIPG.getDisplayRangeMax());
                    System.out.println("ImageWindow set Green min, max from image: "+ipmingre+CS+ipmaxgre);
                    ImageWindow.contrastmin2 = ipmingre;
                    ImageWindow.contrastmax2 = ipmaxgre;

                    setOriginalContrastValues = false;
                }

                croppedIPR.setDisplayRange(contrastmin1, contrastmax1);
                croppedIPG.setDisplayRange(contrastmin2, contrastmax2);
                ImageConverter ic1 = new ImageConverter(croppedIPR);
                ImageConverter ic2 = new ImageConverter(croppedIPG);
                ic1.convertToGray8();
                ic2.convertToGray8();

                ImageProcessor convertedR = croppedIPR.getProcessor();
                ImageProcessor convertedG = croppedIPG.getProcessor();
                R = (byte [])convertedR.getPixels();
                G = (byte [])convertedG.getPixels();
            } else { // this option identifies the case where 16bit images should *not* be split
                if (setOriginalContrastValues){
                    // Set contrast values from original image
                    int ipmingre = (int)(ip.getDisplayRangeMin());
                    int ipmaxgre = (int)(ip.getDisplayRangeMax());
                    System.out.println("ImageWindow set Green min, max from image: "+ipmingre+CS+ipmaxgre);
                    ImageWindow.contrastmin2 = ipmingre;
                    ImageWindow.contrastmax2 = ipmaxgre;
                    setOriginalContrastValues = false;
                }
                ip.setDisplayRange(contrastmin2, contrastmax2);
                ImageConverter ic = new ImageConverter(ip);
                ic.convertToGray8();
                ImageProcessor converted = ip.getProcessor();
                G = (byte [])converted.getPixels();
            }

            iRpix = R;
            iGpix = G;
            iBpix = B;
            iproc3.setRGB(iRpix, iGpix, iBpix);
            ip.setProcessor("test", iproc3);

            return ip;
        } else { // useStack = 0, so load the 8bit images
            //original version
            FileInfo fi = new FileInfo();
            fi = ip.getFileInfo();
            if (fi.getBytesPerPixel() != 8) {
                //ip = convertTo8Bits(ip);
                ImageConverter ic = new ImageConverter(ip);
                ic.convertToGray8();
            }

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
            // end special
            iRpix = R;
            iGpix = G;
            iBpix = B;
            return buildImagePlus(ip);
        }
    }

    /**
     *
     * @param R
     * @return
     */
    private static byte[] getRedChannel(byte [] R) {
        String fileName = makeRedChannelName();
        //System.out.println("getRedChannel: " + fileName);
        File f = new File(fileName);
        if (f.exists()) {
            FileInputStream fis;
            ImagePlus ip = null;

            if (imagewindowUseStack==1){
                ip = new Opener().openImage(fileName,imagewindowPlaneNumber);
            }
            else{
                ip = new Opener().openImage(fileName);
            }
            FileInfo fi = new FileInfo();
            fi = ip.getFileInfo();
            if (fi.getBytesPerPixel() != 8)
            {
                //ip = convertTo8Bits(ip);
                ImageConverter ic = new ImageConverter(ip);
                ic.convertToGray8();
            }

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

    /**
     *
     * @return
     */
    private static String makeRedChannelName() {
        String s = cCurrentImageFile;
        String ss = cCurrentImagePart;
        ss = ss.substring(3);
        s = cZipTifFilePath + C.Fileseparator + "/tifR/" + ss;

        return s;
    }

    /**
     *
     * @param ip
     * @return
     */
    private static ImagePlus buildImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.setRGB(iRpix, iGpix, iBpix);
        ip.setProcessor("test", iproc3);
        return ip;

    }

}
