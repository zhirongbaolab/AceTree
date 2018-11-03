package org.rhwlab.image.management;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.Opener;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

import java.awt.*;
import java.io.File;

/**
 * Handles 16bit to 8bit conversion and image splitting
 *
 * Public methods:
 * 1. convert8bitImageToRGB()
 * 2. convertSingle16BitImageToRGB()
 * 3. convertMultiple16BitImagesToRGB()
 */
public class ImageConversionManager {

    /**
     *
     * @param tif_8bit
     * @param imageConfig
     * @return
     */
    public static ImagePlus convert8bittifToRGB(ImagePlus tif_8bit, ImageConfig imageConfig) {
        //original version
        FileInfo fi;
        fi = tif_8bit.getFileInfo();
        if (fi.getBytesPerPixel() != 8) {
            ImageConverter ic = new ImageConverter(tif_8bit);
            ic.convertToGray8();
        }

        ImageProcessor iproc = tif_8bit.getProcessor();
        byte [] bpix = (byte [])iproc.getPixels();
        byte [] R = new byte[bpix.length];
        byte [] G = new byte[bpix.length];
        byte [] B = new byte[bpix.length];
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.getRGB(R, G, B);

        // special test removal
        G = bpix;
        R = getRedChannelIn8BitImage(R, imageConfig);

        return buildImagePlus(tif_8bit, R, G, B);
    }

    /**
     * This is the method used when one image is supplied that may have more than one color channel
     * and it needs to be either split or left fused and converted to RGB for view
     * @param TIF_16bit an Image processor obtained from the image file
     * @return
     */
    public static ImagePlus convertSingle16BitTIFToRGB(ImagePlus TIF_16bit, ImageConfig imageConfig) {
        ImageProcessor iproc = TIF_16bit.getProcessor();

        iproc.flipHorizontal();

        int pixelCount = iproc.getPixelCount();
        int ipwidth = iproc.getWidth();
        int ipheight = iproc.getHeight();

        if (imageConfig.getSplitStack() == 1) {
            pixelCount /= 2;
            ipwidth /= 2;
        }

        byte [] G = new byte[pixelCount];
        byte [] R = new byte[pixelCount];
        byte [] B = new byte[pixelCount];

        ColorProcessor iproc3 = new ColorProcessor(ipwidth, ipheight);

        /** this indicates 16bit images are present (because useStack = 1), *and* they should be split into two channels */
        if (imageConfig.getSplitStack() == 1) {
            iproc.setRoi(new Rectangle(TIF_16bit.getWidth()/2, 0, TIF_16bit.getWidth()/2, TIF_16bit.getHeight()));
            ImageProcessor croppedR = iproc.crop();
            ImagePlus croppedIPR = new ImagePlus(TIF_16bit.getTitle(), croppedR);

            iproc.setRoi(new Rectangle(0, 0, TIF_16bit.getWidth()/2, TIF_16bit.getHeight()));
            ImageProcessor croppedG = iproc.crop();
            ImagePlus croppedIPG = new ImagePlus(TIF_16bit.getTitle(), croppedG);

            if (ImageManager.getOriginalContrastValuesFlag()) {
                // Set contrast values from original image
                int ipminred = (int)(croppedIPR.getDisplayRangeMin());
                int ipmaxred = (int)(croppedIPR.getDisplayRangeMax());
                System.out.println("ImageConversionManager set Red contrast minimum, maximum from image: " + ipminred + ", " + ipmaxred);
                ImageManager.setContrastMin1(ipminred);
                ImageManager.setContrastMax1(ipmaxred);

                int ipmingre = (int)(croppedIPG.getDisplayRangeMin());
                int ipmaxgre = (int)(croppedIPG.getDisplayRangeMax());
                System.out.println("ImageConversionManager set Green contrast minimum, maximum from image: " + ipmingre + ", " + ipmaxgre);
                ImageManager.setContrastMin2(ipmingre);
                ImageManager.setContrastMax2(ipmaxgre);

                ImageManager.setOriginContrastValuesFlag(false);
            }

            croppedIPR.setDisplayRange(ImageManager.getContrastMin1(), ImageManager.getContrastMax1());
            croppedIPG.setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());

            // convert the images to 8bit
            ImageConverter ic1 = new ImageConverter(croppedIPR);
            ImageConverter ic2 = new ImageConverter(croppedIPG);
            ic1.convertToGray8();
            ic2.convertToGray8();

            // get the individual color pixels and set them to the color maps
            ImageProcessor convertedR = croppedIPR.getProcessor();
            ImageProcessor convertedG = croppedIPG.getProcessor();
            R = (byte [])convertedR.getPixels();
            G = (byte [])convertedG.getPixels();
        } else { /** this option identifies the case where 16bit images should *not* be split */
            if (ImageManager.getOriginalContrastValuesFlag()) {
                // Set contrast values from original image
                int ipmingre = (int)(TIF_16bit.getDisplayRangeMin());
                int ipmaxgre = (int)(TIF_16bit.getDisplayRangeMax());

                System.out.println("ImageConversionManager set Green contrast minimum, maximum from image: " + ipmingre + ", " + ipmaxgre);
                ImageManager.setContrastMin2(ipmingre);
                ImageManager.setContrastMax2(ipmaxgre);

                ImageManager.setOriginContrastValuesFlag(false);
            }

            TIF_16bit.setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());
            ImageConverter ic = new ImageConverter(TIF_16bit);
            ic.convertToGray8();
            ImageProcessor converted = TIF_16bit.getProcessor();
            G = (byte [])converted.getPixels();
        }

        iproc3.setRGB(R, G, B);
        TIF_16bit.setProcessor("test", iproc3);

        return TIF_16bit;
    }

    /**
     * This is the method used when multiple images are supplied
     * and they needs to be either split or left fused and converted to RGB for view
     *
     * @param TIFs_16bit
     * @param imageConfig
     * @return
     */
    public static ImagePlus convertMultiple16BitTIFsToRGB(ImagePlus[] TIFs_16bit, ImageConfig imageConfig) {

        ImageProcessor iproc = ip.getProcessor();

        iproc.flipHorizontal();

        int pixelCount = iproc.getPixelCount();
        int ipwidth = iproc.getWidth();
        int ipheight = iproc.getHeight();

        if (imageConfig.getSplitStack() == 1) {
            pixelCount /= 2;
            ipwidth /= 2;
        }

        byte [] G = new byte[pixelCount];
        byte [] R = new byte[pixelCount];
        byte [] B = new byte[pixelCount];

        ColorProcessor iproc3 = new ColorProcessor(ipwidth, ipheight);

        /** this indicates 16bit images are present (because useStack = 1), *and* they should be split into two channels */
        if (imageConfig.getSplitStack() == 1) {
            iproc.setRoi(new Rectangle(ip.getWidth()/2, 0, ip.getWidth()/2, ip.getHeight()));
            ImageProcessor croppedR = iproc.crop();
            ImagePlus croppedIPR = new ImagePlus(ip.getTitle(), croppedR);

            iproc.setRoi(new Rectangle(0, 0, ip.getWidth()/2, ip.getHeight()));
            ImageProcessor croppedG = iproc.crop();
            ImagePlus croppedIPG = new ImagePlus(ip.getTitle(), croppedG);

            if (ImageManager.getOriginalContrastValuesFlag()) {
                // Set contrast values from original image
                int ipminred = (int)(croppedIPR.getDisplayRangeMin());
                int ipmaxred = (int)(croppedIPR.getDisplayRangeMax());
                System.out.println("ImageConversionManager set Red contrast minimum, maximum from image: " + ipminred + ", " + ipmaxred);
                ImageManager.setContrastMin1(ipminred);
                ImageManager.setContrastMax1(ipmaxred);

                int ipmingre = (int)(croppedIPG.getDisplayRangeMin());
                int ipmaxgre = (int)(croppedIPG.getDisplayRangeMax());
                System.out.println("ImageConversionManager set Green contrast minimum, maximum from image: " + ipmingre + ", " + ipmaxgre);
                ImageManager.setContrastMin2(ipmingre);
                ImageManager.setContrastMax2(ipmaxgre);

                ImageManager.setOriginContrastValuesFlag(false);
            }

            croppedIPR.setDisplayRange(ImageManager.getContrastMin1(), ImageManager.getContrastMax1());
            croppedIPG.setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());

            // convert the images to 8bit
            ImageConverter ic1 = new ImageConverter(croppedIPR);
            ImageConverter ic2 = new ImageConverter(croppedIPG);
            ic1.convertToGray8();
            ic2.convertToGray8();

            // get the individual color pixels and set them to the color maps
            ImageProcessor convertedR = croppedIPR.getProcessor();
            ImageProcessor convertedG = croppedIPG.getProcessor();
            R = (byte [])convertedR.getPixels();
            G = (byte [])convertedG.getPixels();


        } else { /** this option identifies the case where 16bit images should *not* be split */
            if (ImageManager.getOriginalContrastValuesFlag()) {
                // Set contrast values from original image
                int ipmingre = (int)(ip.getDisplayRangeMin());
                int ipmaxgre = (int)(ip.getDisplayRangeMax());

                System.out.println("ImageConversionManager set Green contrast minimum, maximum from image: " + ipmingre + ", " + ipmaxgre);
                ImageManager.setContrastMin2(ipmingre);
                ImageManager.setContrastMax2(ipmaxgre);

                ImageManager.setOriginContrastValuesFlag(false);
            }

            ip.setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());
            ImageConverter ic = new ImageConverter(ip);
            ic.convertToGray8();
            ImageProcessor converted = ip.getProcessor();
            G = (byte [])converted.getPixels();
        }

        iproc3.setRGB(R, G, B);
        ip.setProcessor("test", iproc3);

        return ip;
    }



    private static byte[] getRedChannelInStack(byte[] R, ImageConfig imageConfig, int currentImagePlane) {
        String fileName = makeRedChannelName(imageConfig);
        File f = new File(fileName);
        if (f.exists()) {
            ImagePlus ip;


            ip = new Opener().openImage(fileName, currentImagePlane);

            FileInfo fi;
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
        }
        return R;
    }

    /**
     *
     * @param R
     * @return
     */
    private static byte[] getRedChannelIn8BitImage(byte [] R, ImageConfig imageConfig) {
        String fileName = makeRedChannelName(imageConfig);
        File f = new File(fileName);
        if (f.exists()) {
            ImagePlus ip;

            ip = new Opener().openImage(fileName);

            FileInfo fi;
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
        }
        return R;
    }

    /**
     * The 8bit image name is something like /......./tif/image_name_t#_p#.tif
     *
     * This method replaces /tif/ with /tifR/, the directory which holds the Red channel images
     *
     * @return
     */
    private static String makeRedChannelName(ImageConfig imageConfig) {
        return imageConfig.getProvidedImageFileName().replace("/tif/", "/tifR");
    }

    /**
     *
     * @param ip
     * @return
     */
    private static ImagePlus buildImagePlus(ImagePlus ip, byte[] rPixMap, byte[] gPixMap, byte[] bPixMay) {
        ImageProcessor iproc = ip.getProcessor();
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.setRGB(rPixMap, gPixMap, bPixMay);
        ip.setProcessor("test", iproc3);
        return ip;

    }

}
