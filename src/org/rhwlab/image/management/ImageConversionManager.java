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

    private static byte[] currentRPixelMap;
    private static byte[] currentGPixelMap;
    private static byte[] currentBPixelMap;

    /**
     *
     * @param tif_8bit
     * @param imageConfig
     * @return
     */
    public static ImagePlus convert8bittifToRGB(ImagePlus tif_8bit, ImageConfig imageConfig) {
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

        currentRPixelMap = R;
        currentGPixelMap = G;
        currentBPixelMap = B;

        return buildImagePlus(tif_8bit, R, G, B);
    }

    public static ImagePlus convert16bitSliceTIFToRGB(ImagePlus TIF_slice_16bit, ImageConfig imageConfig) {
        ImageConverter ic = new ImageConverter(TIF_slice_16bit);
        ic.convertToGray8();

        ImageProcessor iproc = TIF_slice_16bit.getProcessor();
        byte [] bpix = (byte [])iproc.getPixels();
        byte [] R = new byte[bpix.length];
        byte [] G = new byte[bpix.length];
        byte [] B = new byte[bpix.length];
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.getRGB(R, G, B);

        // special test removal
        G = bpix;
        R = getRedChannelIn16BitImage(R, imageConfig);

        currentRPixelMap = R;
        currentGPixelMap = G;
        currentBPixelMap = B;

        return buildImagePlus(TIF_slice_16bit, R, G, B);
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
                System.out.println("ImageConversionManager set channel 1 contrast minimum, maximum from image: " + ipminred + ", " + ipmaxred);
                ImageManager.setContrastMin1(ipminred);
                ImageManager.setContrastMax1(ipmaxred);

                int ipmingre = (int)(croppedIPG.getDisplayRangeMin());
                int ipmaxgre = (int)(croppedIPG.getDisplayRangeMax());
                System.out.println("ImageConversionManager set channel 2 contrast minimum, maximum from image: " + ipmingre + ", " + ipmaxgre);
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

                System.out.println("ImageConversionManager set color contrast minimum, maximum from image: " + ipmingre + ", " + ipmaxgre);
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

        currentRPixelMap = R;
        currentGPixelMap = G;
        currentBPixelMap = B;

        return TIF_16bit;
    }

    /**
     * This is the method used when multiple images are supplied. The split flag is used as an indication of whether
     * or not there are two channels, and since we know in this case that there are more than 1 channels, we layer them
     * and don't worry about the split stack flag
     *
     * This method assumes that each TIF in the array has only one color channel
     *
     * @param TIFs_16bit
     * @param imageConfig
     * @return
     */
    public static ImagePlus convertMultiple16BitTIFsToRGB(ImagePlus[] TIFs_16bit, ImageConfig imageConfig) {
        ImagePlus ip = new ImagePlus();
        if (TIFs_16bit.length > 3) {
            System.out.println("ImageConversionManager.convertMultiple16BitTIFsToRGB given > 3 image channels to process. " +
                    "AceTree is presently limited to processing three color channels. All channels after the initial three " +
                    "will be ignored");
        }

        ImageProcessor iproc_channel1 = TIFs_16bit[0].getProcessor();
        ImageProcessor iproc_channel2 = TIFs_16bit[1].getProcessor();
        ImageProcessor iproc_channel3;

        iproc_channel1.flipHorizontal();
        iproc_channel2.flipHorizontal();

        int pixelCount_channel1 = iproc_channel1.getPixelCount();
        int ipwidth_channel1 = iproc_channel1.getWidth();
        int ipheight_channel1 = iproc_channel1.getHeight();

        int pixelCount_channel2 = iproc_channel2.getPixelCount();
        int ipwidth_channel2 = iproc_channel2.getWidth();
        int ipheight_channel2 = iproc_channel2.getHeight();

        // error checks
        if (pixelCount_channel1 != pixelCount_channel2) {
            System.err.println("Pixel counts in the first two color channel stacks are mismatched.");
            return ip;
        }
        if (ipwidth_channel1 != ipwidth_channel2) {
            System.err.println("Image widths in the first two color channel stacks are mismatched.");
            return ip;
        }
        if (ipheight_channel1 != ipheight_channel2) {
            System.err.println("Image heights in the first two color channel stacks are mismatched.");
            return ip;
        }

        if (TIFs_16bit.length == 3) {
            iproc_channel3 = TIFs_16bit[2].getProcessor();
            iproc_channel3.flipHorizontal();

            // propogate error checks to third channel
            if (pixelCount_channel1 != iproc_channel3.getPixelCount()
                || ipwidth_channel1 != iproc_channel3.getWidth()
                || ipheight_channel1 != iproc_channel3.getHeight()) {
                System.err.println("Pixel counts in the first three color channel stacks are mismatched.");
                return ip;
            }
        }


        // now these vars will pertain to the single resulting ImagePlus so just doing this for clarity - plenty of memory to go around
        int pixelCount = pixelCount_channel1;
        int ipWidth = ipwidth_channel1;
        int ipHeight = ipheight_channel1;


        byte[] colorChannel1;
        byte[] colorChannel2;
        byte[] colorChannel3 = new byte[pixelCount];


        ColorProcessor iproc3 = new ColorProcessor(ipWidth, ipHeight);

        // set the min and max contrast values if need be
        // This can be lifted out of the splitStack conditional because in the case of multiple TIFs, we know we have two color channels
        if (ImageManager.getOriginalContrastValuesFlag()) {
            int ipMinChannel1 = (int)(TIFs_16bit[0].getDisplayRangeMin());
            int ipMaxChannel1 = (int)(TIFs_16bit[0].getDisplayRangeMax());

            ImageManager.setContrastMin1(ipMinChannel1);
            ImageManager.setContrastMax1(ipMaxChannel1);

            System.out.println("ImageConversionManager set channel contrast min, max for channel1: " + ipMinChannel1 + ", " + ipMaxChannel1);

            int ipMinChannel2 = (int)(TIFs_16bit[1].getDisplayRangeMin());
            int ipMaxChannel2 = (int)(TIFs_16bit[1].getDisplayRangeMax());

            ImageManager.setContrastMin2(ipMinChannel2);
            ImageManager.setContrastMax2(ipMaxChannel2);

            System.out.println("ImageConversionManager set channel contrast min, max for channel2: " + ipMinChannel2 + ", " + ipMaxChannel2);

            if (TIFs_16bit.length == 3) {
                int ipMinChannel3 = (int)TIFs_16bit[2].getDisplayRangeMin();
                int ipMaxChannel3 = (int)TIFs_16bit[2].getDisplayRangeMax();

                ImageManager.setContrastMin3(ipMinChannel3);
                ImageManager.setContrastMax3(ipMaxChannel3);

                System.out.println("ImageConversionManager set channel contrast min, max for channel3: " + ipMinChannel3 + ", " + ipMaxChannel3);
            }

            ImageManager.setOriginContrastValuesFlag(false);
        }

        TIFs_16bit[0].setDisplayRange(ImageManager.getContrastMin1(), ImageManager.getContrastMax1());
        TIFs_16bit[1].setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());

        // convert the images to 8bit
        ImageConverter ic1 = new ImageConverter(TIFs_16bit[0]);
        ImageConverter ic2 = new ImageConverter(TIFs_16bit[1]);
        ic1.convertToGray8();
        ic2.convertToGray8();

        // get the individual color pixels and set them to the color maps
        ImageProcessor convertedChannel1 = TIFs_16bit[0].getProcessor();
        ImageProcessor convertedChannel2 = TIFs_16bit[1].getProcessor();

        colorChannel1 = (byte [])convertedChannel1.getPixels();
        colorChannel2 = (byte [])convertedChannel2.getPixels();

        if (TIFs_16bit.length == 3) {
            TIFs_16bit[2].setDisplayRange(ImageManager.getContrastMin3(), ImageManager.getContrastMax3());

            ImageConverter ic3 = new ImageConverter(TIFs_16bit[2]);
            ic3.convertToGray8();

            ImageProcessor convertedChannel3 = TIFs_16bit[2].getProcessor();

            colorChannel3 = (byte [])convertedChannel3.getPixels();
        }

        iproc3.setRGB(colorChannel1, colorChannel2, colorChannel3);
        ip.setProcessor("test", iproc3);

        currentRPixelMap = colorChannel1;
        currentGPixelMap = colorChannel2;
        currentBPixelMap = colorChannel3;

        return ip;
    }



    private static byte[] getRedChannelIn16BitImage(byte[] R, ImageConfig imageConfig) {
        String fileName = makeRedChannelName(imageConfig);
        File f = new File(fileName);
        if (f.exists()) {
            ImagePlus ip;

            ip = new Opener().openImage(fileName);

            ImageConverter ic = new ImageConverter(ip);
            ic.convertToGray8();

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
     * The image name is something like /......./tif/image_name_t#_p#.tif
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

    public static byte[] getCurrentRPixelMap() { return currentRPixelMap; }
    public static byte[] getCurrentGPixelMap() { return currentGPixelMap; }
    public static byte[] getCurrentBPixelMap() { return currentBPixelMap; }
}
