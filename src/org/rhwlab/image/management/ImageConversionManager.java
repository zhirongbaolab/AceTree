package org.rhwlab.image.management;

import com.sun.org.apache.bcel.internal.generic.MULTIANEWARRAY;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.Opener;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

import java.awt.*;
import java.io.File;
import java.util.Collections;

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

        if (ImageManager.getOriginalContrastValuesFlag()) {
            // Set contrast values from original image
            int ipminred = (int)(tif_8bit.getDisplayRangeMin());
            int ipmaxred = (int)(tif_8bit.getDisplayRangeMax());
            System.out.println("ImageConversionManager set channel 2 contrast minimum, maximum from image: " + ipminred + ", " + ipmaxred);
            ImageManager.setContrastMin2(ipminred);
            ImageManager.setContrastMax2(ipmaxred);

            ImageManager.setOriginContrastValuesFlag(false);
        }

        tif_8bit.setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());

        iproc3.getRGB(R, G, B);

        // set the GREEN
        G = bpix;

        currentRPixelMap = R;
        currentGPixelMap = G;
        currentBPixelMap = B;

        return buildImagePlus(tif_8bit, R, G, B);
    }

    public static ImagePlus convertMultiple8bittifsToRGB(ImagePlus tif1, ImagePlus tif2, ImageConfig imageConfig) {
        ImageProcessor iproc1 = tif2.getProcessor();
        ImageProcessor iproc2 = tif1.getProcessor();

        if (imageConfig.getFlipStack() == 1) {
            iproc1.flipHorizontal();
            iproc2.flipHorizontal();
        }

        byte[] Rpix = (byte [])iproc1.getPixels();
        byte[] Gpix = (byte [])iproc2.getPixels();

        byte [] R = new byte[Rpix.length];
        byte [] G = new byte[Rpix.length];
        byte [] B = new byte[Rpix.length];

        ColorProcessor iproc3 = new ColorProcessor(iproc1.getWidth(), iproc1.getHeight());

        if (ImageManager.getOriginalContrastValuesFlag()) {
            // Set contrast values from original image
            int ipminred = (int)(tif2.getDisplayRangeMin());
            int ipmaxred = (int)(tif2.getDisplayRangeMax());
            System.out.println("ImageConversionManager set channel 1 contrast minimum, maximum from image: " + ipminred + ", " + ipmaxred);
            ImageManager.setContrastMin1(ipminred);
            ImageManager.setContrastMax1(ipmaxred);

            int ipmingreen = (int)(tif1.getDisplayRangeMin());
            int ipmaxgreen = (int)(tif1.getDisplayRangeMax());
            System.out.println("ImageConversionManager set channel 2 contrast minimum, maximum from image: " + ipmingreen + ", " + ipmaxgreen);
            ImageManager.setContrastMin2(ipmingreen);
            ImageManager.setContrastMax2(ipmaxgreen);

            ImageManager.setOriginContrastValuesFlag(false);
        }

        tif2.setDisplayRange(ImageManager.getContrastMin1(), ImageManager.getContrastMax1());
        tif1.setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());

        iproc3.getRGB(R, G, B);

        R = Rpix;
        G = Gpix;

        currentRPixelMap = R;
        currentGPixelMap = G;
        currentBPixelMap = B;

        iproc3.setRGB(R, G, B);
        ImagePlus ip = new ImagePlus();
        ip.setProcessor("test", iproc3);
        return ip;
    }

    public static ImagePlus convert16bitSliceTIFToRGB(ImagePlus TIF_slice_16bit, ImageConfig imageConfig) {
        ImageProcessor iproc = TIF_slice_16bit.getProcessor();

        // TODO - split control for these slices
        if (imageConfig.getFlipStack() == 1) {
            iproc.flipHorizontal();
        }

        ImageConverter ic = new ImageConverter(TIF_slice_16bit);
        ic.convertToGray8();

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

        if (imageConfig.getFlipStack() == 1) {
            iproc.flipHorizontal();
        }

        int pixelCount = iproc.getPixelCount();
        int ipwidth = iproc.getWidth();
        int ipheight = iproc.getHeight();

        if (imageConfig.getSplitStack() == 1) {
            pixelCount /= 2;
            ipwidth /= 2;
        }

        byte [] R = new byte[pixelCount];
        byte [] G = new byte[pixelCount];
        byte [] B = new byte[pixelCount];

        ColorProcessor iproc3 = new ColorProcessor(ipwidth, ipheight);

        /** this indicates 16bit images are present (because useStack = 1), *and* they should be split into two channels */
        if (imageConfig.getSplitStack() == 1) {
            /**
             * Defaults in an unflipped image
             * RIGHT SIDE - GREEN
             * LEFT SIDE - RED
             */
            // check if the image was flipped to make sure the correct half of the image is cropped
            if (imageConfig.getFlipStack() == 1) {
                iproc.setRoi(new Rectangle(TIF_16bit.getWidth()/2, 0, TIF_16bit.getWidth()/2, TIF_16bit.getHeight()));
            } else {
                iproc.setRoi(new Rectangle(0, 0, TIF_16bit.getWidth()/2, TIF_16bit.getHeight()));
            }

            ImageProcessor croppedR = iproc.crop();
            ImagePlus croppedIPR = new ImagePlus(TIF_16bit.getTitle(), croppedR);

            if (imageConfig.getFlipStack() == 1) {
                iproc.setRoi(new Rectangle(0, 0, TIF_16bit.getWidth()/2, TIF_16bit.getHeight()));
            } else {
                iproc.setRoi(new Rectangle(TIF_16bit.getWidth()/2, 0, TIF_16bit.getWidth()/2, TIF_16bit.getHeight()));
            }

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
                int ipminred = (int)(TIF_16bit.getDisplayRangeMin());
                int ipmaxred = (int)(TIF_16bit.getDisplayRangeMax());

                System.out.println("ImageConversionManager set color contrast minimum, maximum from image: " + ipminred + ", " + ipmaxred);
                ImageManager.setContrastMin1(ipminred);
                ImageManager.setContrastMax1(ipmaxred);

                ImageManager.setOriginContrastValuesFlag(false);
            }

            TIF_16bit.setDisplayRange(ImageManager.getContrastMin1(), ImageManager.getContrastMax1());
            ImageConverter ic = new ImageConverter(TIF_16bit);
            ic.convertToGray8();
            ImageProcessor converted = TIF_16bit.getProcessor();
            R = (byte [])converted.getPixels();
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
    public static ImagePlus convertMultiple16BitTIFsToRGB(ImagePlus[] TIFs_16bit, ImageConfig imageConfig, boolean red_valid, boolean green_valid, boolean blue_valid) {
        ImagePlus ip = new ImagePlus();
        if (TIFs_16bit.length > 3) {
            System.out.println("ImageConversionManager.convertMultiple16BitTIFsToRGB given > 3 image channels to process. " +
                    "AceTree is presently limited to processing three color channels. All channels after the initial three " +
                    "will be ignored");
        }

        // pipe the configuration to the correct method
        if (red_valid && green_valid && !blue_valid) {
            return convertMultiple16BitTIFsToRG(TIFs_16bit[0], TIFs_16bit[1], imageConfig);
        } else if (red_valid && green_valid && blue_valid) {
            return convertMultiple16BitTIFstoRGB(TIFs_16bit[0], TIFs_16bit[1], TIFs_16bit[2], imageConfig);
        } else if (!red_valid && green_valid && !blue_valid) {
            return convert16BitTIFtoG(TIFs_16bit[1], imageConfig);
        } else if (!red_valid && !green_valid && blue_valid) {
            return convert16BitTIFtoB(TIFs_16bit[2], imageConfig);
        } else if (!red_valid && green_valid && blue_valid) {
            return convertMultiple16BitTIFstoGB(TIFs_16bit[1], TIFs_16bit[2], imageConfig);
        } else if (red_valid && !green_valid && blue_valid) {
            return convertMultiple16BitTIFstoRB(TIFs_16bit[0], TIFs_16bit[2], imageConfig);
        }
        System.out.println("Invalid configuration in ImageConversionManager.convertMultiple16BitTIFsToRGB()");
        return null;
    }

    ////////////////// SUB METHODS //////////////////////
    //////////////// FOR MULTIPLE 16BIT ////////////////
    /////////////// CONVERSION ////////////////////////

    /**
     * RED/GREEN CHANNEL CASE
     * @param red_ip
     * @param green_ip
     * @return
     */
    private static ImagePlus convertMultiple16BitTIFsToRG(ImagePlus red_ip, ImagePlus green_ip, ImageConfig imageConfig) {
        ImagePlus ip = new ImagePlus();

        ImageProcessor iproc_channel1 = red_ip.getProcessor();
        ImageProcessor iproc_channel2 = green_ip.getProcessor();

        if (imageConfig.getFlipStack() == 1) {
            iproc_channel1.flipHorizontal();
            iproc_channel2.flipHorizontal();
        }

        int pixelCount_channel1 = iproc_channel1.getPixelCount();
        int ipwidth_channel1 = iproc_channel1.getWidth();
        int ipheight_channel1 = iproc_channel1.getHeight();

        int pixelCount_channel2 = iproc_channel2.getPixelCount();
        int ipwidth_channel2 = iproc_channel2.getWidth();
        int ipheight_channel2 = iproc_channel2.getHeight();


        // error checks
        if (pixelCount_channel1 != pixelCount_channel2) {
            System.err.println("Pixel counts in the first two color channel stacks are mismatched.");
            return red_ip;
        }
        if (ipwidth_channel1 != ipwidth_channel2) {
            System.err.println("Image widths in the first two color channel stacks are mismatched.");
            return red_ip;
        }
        if (ipheight_channel1 != ipheight_channel2) {
            System.err.println("Image heights in the first two color channel stacks are mismatched.");
            return red_ip;
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
            int ipMinChannel1 = (int)(red_ip.getDisplayRangeMin());
            int ipMaxChannel1 = (int)(red_ip.getDisplayRangeMax());

            ImageManager.setContrastMin1(ipMinChannel1);
            ImageManager.setContrastMax1(ipMaxChannel1);

            System.out.println("ImageConversionManager set channel contrast min, max for channel1: " + ipMinChannel1 + ", " + ipMaxChannel1);

            int ipMinChannel2 = (int)(green_ip.getDisplayRangeMin());
            int ipMaxChannel2 = (int)(green_ip.getDisplayRangeMax());

            ImageManager.setContrastMin2(ipMinChannel2);
            ImageManager.setContrastMax2(ipMaxChannel2);

            System.out.println("ImageConversionManager set channel contrast min, max for channel2: " + ipMinChannel2 + ", " + ipMaxChannel2);


            ImageManager.setOriginContrastValuesFlag(false);
        }

        red_ip.setDisplayRange(ImageManager.getContrastMin1(), ImageManager.getContrastMax1());
        green_ip.setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());

        // convert the images to 8bit
        ImageConverter ic1 = new ImageConverter(red_ip);
        ImageConverter ic2 = new ImageConverter(green_ip);
        ic1.convertToGray8();
        ic2.convertToGray8();

        // get the individual color pixels and set them to the color maps
        ImageProcessor convertedChannel1 = red_ip.getProcessor();
        ImageProcessor convertedChannel2 = green_ip.getProcessor();

        colorChannel1 = (byte [])convertedChannel1.getPixels();
        colorChannel2 = (byte [])convertedChannel2.getPixels();


        iproc3.setRGB(colorChannel1, colorChannel2, colorChannel3);
        ip.setProcessor("test", iproc3);

        currentRPixelMap = colorChannel1;
        currentGPixelMap = colorChannel2;
        currentBPixelMap = colorChannel3;

        return ip;
    }

    /**
     * RED/GREEN/BLUE CHANNEL CASE
     * @param red_ip
     * @param green_ip
     * @param blue_ip
     * @return
     */
    private static ImagePlus convertMultiple16BitTIFstoRGB(ImagePlus red_ip, ImagePlus green_ip, ImagePlus blue_ip, ImageConfig imageConfig) {
        ImagePlus ip = new ImagePlus();

        ImageProcessor iproc_channel1 = red_ip.getProcessor();
        ImageProcessor iproc_channel2 = green_ip.getProcessor();
        ImageProcessor iproc_channel3 = blue_ip.getProcessor();

        if (imageConfig.getFlipStack() == 1) {
            iproc_channel1.flipHorizontal();
            iproc_channel2.flipHorizontal();
            iproc_channel3.flipHorizontal();
        }

        int pixelCount_channel1 = iproc_channel1.getPixelCount();
        int ipwidth_channel1 = iproc_channel1.getWidth();
        int ipheight_channel1 = iproc_channel1.getHeight();

        int pixelCount_channel2 = iproc_channel2.getPixelCount();
        int ipwidth_channel2 = iproc_channel2.getWidth();
        int ipheight_channel2 = iproc_channel2.getHeight();

        int pixelCount_channel3 = iproc_channel3.getPixelCount();
        int ipwidth_channel3 = iproc_channel3.getWidth();
        int ipheight_channel3 = iproc_channel3.getHeight();

        // error checks
        if (pixelCount_channel1 != pixelCount_channel2) {
            System.err.println("Pixel counts in the first two color channel stacks are mismatched.");
            return red_ip;
        }
        if (ipwidth_channel1 != ipwidth_channel2) {
            System.err.println("Image widths in the first two color channel stacks are mismatched.");
            return red_ip;
        }
        if (ipheight_channel1 != ipheight_channel2) {
            System.err.println("Image heights in the first two color channel stacks are mismatched.");
            return red_ip;
        }

        if (pixelCount_channel1 != pixelCount_channel3
                || ipwidth_channel1 != ipwidth_channel3
                || ipheight_channel1 != ipheight_channel3) {
            System.err.println("Pixel counts in the first three color channel stacks are mismatched.");
            return red_ip;
        }


        // now these vars will pertain to the single resulting ImagePlus so just doing this for clarity - plenty of memory to go around
        int pixelCount = pixelCount_channel1;
        int ipWidth = ipwidth_channel1;
        int ipHeight = ipheight_channel1;


        byte[] colorChannel1;
        byte[] colorChannel2;
        byte[] colorChannel3;


        ColorProcessor iproc3 = new ColorProcessor(ipWidth, ipHeight);

        // set the min and max contrast values if need be
        // This can be lifted out of the splitStack conditional because in the case of multiple TIFs, we know we have two color channels
        if (ImageManager.getOriginalContrastValuesFlag()) {
            int ipMinChannel1 = (int)(red_ip.getDisplayRangeMin());
            int ipMaxChannel1 = (int)(red_ip.getDisplayRangeMax());

            ImageManager.setContrastMin1(ipMinChannel1);
            ImageManager.setContrastMax1(ipMaxChannel1);

            System.out.println("ImageConversionManager set channel contrast min, max for channel1: " + ipMinChannel1 + ", " + ipMaxChannel1);

            int ipMinChannel2 = (int)(green_ip.getDisplayRangeMin());
            int ipMaxChannel2 = (int)(green_ip.getDisplayRangeMax());

            ImageManager.setContrastMin2(ipMinChannel2);
            ImageManager.setContrastMax2(ipMaxChannel2);

            System.out.println("ImageConversionManager set channel contrast min, max for channel2: " + ipMinChannel2 + ", " + ipMaxChannel2);

            int ipMinChannel3 = (int)blue_ip.getDisplayRangeMin();
            int ipMaxChannel3 = (int)blue_ip.getDisplayRangeMax();

            ImageManager.setContrastMin3(ipMinChannel3);
            ImageManager.setContrastMax3(ipMaxChannel3);

            System.out.println("ImageConversionManager set channel contrast min, max for channel3: " + ipMinChannel3 + ", " + ipMaxChannel3);

            ImageManager.setOriginContrastValuesFlag(false);
        }

        red_ip.setDisplayRange(ImageManager.getContrastMin1(), ImageManager.getContrastMax1());
        green_ip.setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());
        blue_ip.setDisplayRange(ImageManager.getContrastMin3(), ImageManager.getContrastMax3());

        // convert the images to 8bit
        ImageConverter ic1 = new ImageConverter(red_ip);
        ImageConverter ic2 = new ImageConverter(green_ip);
        ImageConverter ic3 = new ImageConverter(blue_ip);
        ic1.convertToGray8();
        ic2.convertToGray8();
        ic3.convertToGray8();

        // get the individual color pixels and set them to the color maps
        ImageProcessor convertedChannel1 = red_ip.getProcessor();
        ImageProcessor convertedChannel2 = green_ip.getProcessor();
        ImageProcessor convertedChannel3 = blue_ip.getProcessor();

        colorChannel1 = (byte [])convertedChannel1.getPixels();
        colorChannel2 = (byte [])convertedChannel2.getPixels();
        colorChannel3 = (byte [])convertedChannel3.getPixels();


        iproc3.setRGB(colorChannel1, colorChannel2, colorChannel3);
        ip.setProcessor("test", iproc3);

        currentRPixelMap = colorChannel1;
        currentGPixelMap = colorChannel2;
        currentBPixelMap = colorChannel3;

        return ip;
    }

    /**
     * GREEN CHANNEL CASE
     *
     * @param green_ip
     * @return
     */
    private static ImagePlus convert16BitTIFtoG(ImagePlus green_ip, ImageConfig imageConfig) {
        ImagePlus ip = new ImagePlus();

        ImageProcessor iproc_channel2 = green_ip.getProcessor();

        if (imageConfig.getFlipStack() == 1) {
            iproc_channel2.flipHorizontal();
        }

        int pixelCount_channel2 = iproc_channel2.getPixelCount();
        int ipwidth_channel2 = iproc_channel2.getWidth();
        int ipheight_channel2 = iproc_channel2.getHeight();


        byte[] colorChannel1 = new byte[pixelCount_channel2];
        byte[] colorChannel2;
        byte[] colorChannel3 = new byte[pixelCount_channel2];


        ColorProcessor iproc3 = new ColorProcessor(ipwidth_channel2, ipheight_channel2);

        // set the min and max contrast values if need be
        // This can be lifted out of the splitStack conditional because in the case of multiple TIFs, we know we have two color channels
        if (ImageManager.getOriginalContrastValuesFlag()) {
            int ipMinChannel2 = (int)(green_ip.getDisplayRangeMin());
            int ipMaxChannel2 = (int)(green_ip.getDisplayRangeMax());

            ImageManager.setContrastMin2(ipMinChannel2);
            ImageManager.setContrastMax2(ipMaxChannel2);

            System.out.println("ImageConversionManager set channel contrast min, max for channel2: " + ipMinChannel2 + ", " + ipMaxChannel2);


            ImageManager.setOriginContrastValuesFlag(false);
        }

        green_ip.setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());

        // convert the image to 8bit
        ImageConverter ic2 = new ImageConverter(green_ip);
        ic2.convertToGray8();

        // get the individual color pixels and set them to the color maps
        ImageProcessor convertedChannel2 = green_ip.getProcessor();

        colorChannel2 = (byte [])convertedChannel2.getPixels();


        iproc3.setRGB(colorChannel1, colorChannel2, colorChannel3);
        ip.setProcessor("test", iproc3);

        currentRPixelMap = colorChannel1;
        currentGPixelMap = colorChannel2;
        currentBPixelMap = colorChannel3;

        return ip;
    }

    /**
     * BLUE CHANNEL CASE
     * @param blue_ip
     * @return
     */
    private static ImagePlus convert16BitTIFtoB(ImagePlus blue_ip, ImageConfig imageConfig) {
        ImagePlus ip = new ImagePlus();

        ImageProcessor iproc_channel3 = blue_ip.getProcessor();

        if (imageConfig.getFlipStack() == 1) {
            iproc_channel3.flipHorizontal();
        }

        int pixelCount_channel3 = iproc_channel3.getPixelCount();
        int ipwidth_channel3 = iproc_channel3.getWidth();
        int ipheight_channel3 = iproc_channel3.getHeight();


        byte[] colorChannel1 = new byte[pixelCount_channel3];
        byte[] colorChannel2 = new byte[pixelCount_channel3];
        byte[] colorChannel3;


        ColorProcessor iproc3 = new ColorProcessor(ipwidth_channel3, ipheight_channel3);

        // set the min and max contrast values if need be
        // This can be lifted out of the splitStack conditional because in the case of multiple TIFs, we know we have two color channels
        if (ImageManager.getOriginalContrastValuesFlag()) {
            int ipMinChannel3 = (int)(blue_ip.getDisplayRangeMin());
            int ipMaxChannel3 = (int)(blue_ip.getDisplayRangeMax());

            ImageManager.setContrastMin3(ipMinChannel3);
            ImageManager.setContrastMax3(ipMaxChannel3);

            System.out.println("ImageConversionManager set channel contrast min, max for channel3: " + ipMinChannel3 + ", " + ipMaxChannel3);


            ImageManager.setOriginContrastValuesFlag(false);
        }

        blue_ip.setDisplayRange(ImageManager.getContrastMin3(), ImageManager.getContrastMax3());

        // convert the image to 8bit
        ImageConverter ic3 = new ImageConverter(blue_ip);
        ic3.convertToGray8();

        // get the individual color pixels and set them to the color maps
        ImageProcessor convertedChannel3 = blue_ip.getProcessor();

        colorChannel3 = (byte [])convertedChannel3.getPixels();


        iproc3.setRGB(colorChannel1, colorChannel2, colorChannel3);
        ip.setProcessor("test", iproc3);

        currentRPixelMap = colorChannel1;
        currentGPixelMap = colorChannel2;
        currentBPixelMap = colorChannel3;

        return ip;
    }

    /**
     * GREEN/BLUE CHANNEL CASE
     * @param green_ip
     * @param blue_ip
     * @return
     */
    private static ImagePlus convertMultiple16BitTIFstoGB(ImagePlus green_ip, ImagePlus blue_ip, ImageConfig imageConfig) {
        ImagePlus ip = new ImagePlus();

        ImageProcessor iproc_channel2 = green_ip.getProcessor();
        ImageProcessor iproc_channel3 = blue_ip.getProcessor();

        if (imageConfig.getFlipStack() == 1) {
            iproc_channel2.flipHorizontal();
            iproc_channel3.flipHorizontal();
        }

        int pixelCount_channel2 = iproc_channel2.getPixelCount();
        int ipwidth_channel2 = iproc_channel2.getWidth();
        int ipheight_channel2 = iproc_channel2.getHeight();

        int pixelCount_channel3 = iproc_channel3.getPixelCount();
        int ipwidth_channel3 = iproc_channel3.getWidth();
        int ipheight_channel3 = iproc_channel3.getHeight();


        // error checks
        if (pixelCount_channel2 != pixelCount_channel3) {
            System.err.println("Pixel counts in the first two color channel stacks are mismatched.");
            return green_ip;
        }
        if (ipwidth_channel2 != ipwidth_channel3) {
            System.err.println("Image widths in the first two color channel stacks are mismatched.");
            return green_ip;
        }
        if (ipheight_channel2 != ipheight_channel3) {
            System.err.println("Image heights in the first two color channel stacks are mismatched.");
            return green_ip;
        }


        // now these vars will pertain to the single resulting ImagePlus so just doing this for clarity - plenty of memory to go around
        int pixelCount = pixelCount_channel2;
        int ipWidth = ipwidth_channel2;
        int ipHeight = ipheight_channel2;


        byte[] colorChannel1 = new byte[pixelCount];
        byte[] colorChannel2;
        byte[] colorChannel3;


        ColorProcessor iproc3 = new ColorProcessor(ipWidth, ipHeight);

        // set the min and max contrast values if need be
        // This can be lifted out of the splitStack conditional because in the case of multiple TIFs, we know we have two color channels
        if (ImageManager.getOriginalContrastValuesFlag()) {
            int ipMinChannel2 = (int)(green_ip.getDisplayRangeMin());
            int ipMaxChannel2 = (int)(green_ip.getDisplayRangeMax());

            ImageManager.setContrastMin2(ipMinChannel2);
            ImageManager.setContrastMax2(ipMaxChannel2);

            System.out.println("ImageConversionManager set channel contrast min, max for channel2: " + ipMinChannel2 + ", " + ipMaxChannel2);

            int ipMinChannel3 = (int)(blue_ip.getDisplayRangeMin());
            int ipMaxChannel3 = (int)(blue_ip.getDisplayRangeMax());

            ImageManager.setContrastMin3(ipMinChannel3);
            ImageManager.setContrastMax3(ipMaxChannel3);

            System.out.println("ImageConversionManager set channel contrast min, max for channel3: " + ipMinChannel3 + ", " + ipMaxChannel3);


            ImageManager.setOriginContrastValuesFlag(false);
        }

        green_ip.setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());
        blue_ip.setDisplayRange(ImageManager.getContrastMin3(), ImageManager.getContrastMax3());

        // convert the images to 8bit
        ImageConverter ic2 = new ImageConverter(green_ip);
        ImageConverter ic3 = new ImageConverter(blue_ip);
        ic2.convertToGray8();
        ic3.convertToGray8();

        // get the individual color pixels and set them to the color maps
        ImageProcessor convertedChannel2 = green_ip.getProcessor();
        ImageProcessor convertedChannel3 = blue_ip.getProcessor();

        colorChannel2 = (byte [])convertedChannel2.getPixels();
        colorChannel3 = (byte [])convertedChannel3.getPixels();


        iproc3.setRGB(colorChannel1, colorChannel2, colorChannel3);
        ip.setProcessor("test", iproc3);

        currentRPixelMap = colorChannel1;
        currentGPixelMap = colorChannel2;
        currentBPixelMap = colorChannel3;

        return ip;
    }

    /**
     * RED/BLUE CHANNEL CASE
     * @param red_ip
     * @param blue_ip
     * @return
     */
    private static ImagePlus convertMultiple16BitTIFstoRB(ImagePlus red_ip, ImagePlus blue_ip, ImageConfig imageConfig) {
        ImagePlus ip = new ImagePlus();

        ImageProcessor iproc_channel1 = red_ip.getProcessor();
        ImageProcessor iproc_channel3 = blue_ip.getProcessor();

        if (imageConfig.getFlipStack() == 1) {
            iproc_channel1.flipHorizontal();
            iproc_channel3.flipHorizontal();
        }

        int pixelCount_channel1 = iproc_channel1.getPixelCount();
        int ipwidth_channel1 = iproc_channel1.getWidth();
        int ipheight_channel1 = iproc_channel1.getHeight();

        int pixelCount_channel3 = iproc_channel3.getPixelCount();
        int ipwidth_channel3 = iproc_channel3.getWidth();
        int ipheight_channel3 = iproc_channel3.getHeight();


        // error checks
        if (pixelCount_channel1 != pixelCount_channel3) {
            System.err.println("Pixel counts in the first two color channel stacks are mismatched.");
            return red_ip;
        }
        if (ipwidth_channel1 != ipwidth_channel3) {
            System.err.println("Image widths in the first two color channel stacks are mismatched.");
            return red_ip;
        }
        if (ipheight_channel1 != ipheight_channel3) {
            System.err.println("Image heights in the first two color channel stacks are mismatched.");
            return red_ip;
        }


        // now these vars will pertain to the single resulting ImagePlus so just doing this for clarity - plenty of memory to go around
        int pixelCount = pixelCount_channel1;
        int ipWidth = ipwidth_channel1;
        int ipHeight = ipheight_channel1;


        byte[] colorChannel1;
        byte[] colorChannel2 = new byte[pixelCount];
        byte[] colorChannel3;


        ColorProcessor iproc3 = new ColorProcessor(ipWidth, ipHeight);

        // set the min and max contrast values if need be
        // This can be lifted out of the splitStack conditional because in the case of multiple TIFs, we know we have two color channels
        if (ImageManager.getOriginalContrastValuesFlag()) {
            int ipMinChannel1 = (int)(red_ip.getDisplayRangeMin());
            int ipMaxChannel1 = (int)(red_ip.getDisplayRangeMax());

            ImageManager.setContrastMin1(ipMinChannel1);
            ImageManager.setContrastMax1(ipMaxChannel1);

            System.out.println("ImageConversionManager set channel contrast min, max for channel1: " + ipMinChannel1 + ", " + ipMaxChannel1);

            int ipMinChannel3 = (int)(blue_ip.getDisplayRangeMin());
            int ipMaxChannel3 = (int)(blue_ip.getDisplayRangeMax());

            ImageManager.setContrastMin3(ipMinChannel3);
            ImageManager.setContrastMax3(ipMaxChannel3);

            System.out.println("ImageConversionManager set channel contrast min, max for channel3: " + ipMinChannel3 + ", " + ipMaxChannel3);


            ImageManager.setOriginContrastValuesFlag(false);
        }

        red_ip.setDisplayRange(ImageManager.getContrastMin1(), ImageManager.getContrastMax1());
        blue_ip.setDisplayRange(ImageManager.getContrastMin3(), ImageManager.getContrastMax3());

        // convert the images to 8bit
        ImageConverter ic1 = new ImageConverter(red_ip);
        ImageConverter ic3 = new ImageConverter(blue_ip);
        ic1.convertToGray8();
        ic3.convertToGray8();

        // get the individual color pixels and set them to the color maps
        ImageProcessor convertedChannel1 = red_ip.getProcessor();
        ImageProcessor convertedChannel3 = blue_ip.getProcessor();

        colorChannel1 = (byte [])convertedChannel1.getPixels();
        colorChannel3 = (byte [])convertedChannel3.getPixels();


        iproc3.setRGB(colorChannel1, colorChannel2, colorChannel3);
        ip.setProcessor("test", iproc3);

        currentRPixelMap = colorChannel1;
        currentGPixelMap = colorChannel2;
        currentBPixelMap = colorChannel3;

        return ip;
    }
    //////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////


    /**
     * Maximum Intensity Projections are shown in the ImageWindow
     * @param MIP_ip
     * @param colorChannelIdx - 1 (red), 2 (green), 3 (blue)
     * @return
     */
    public static ImagePlus convertMIPToRGB(ImagePlus MIP_ip, int colorChannelIdx, ImageConfig imageConfig) {
        ImageProcessor iproc = MIP_ip.getProcessor();
        //System.out.println(iproc.getNChannels() + ", " + iproc.getSliceNumber() + ", " + iproc.getBitDepth() + ", " + iproc.getPixelCount() + ", " + iproc.getPixelValue(30, 30));

        if (imageConfig.getFlipStack() == 1) {
            // flip the projection
            iproc.flipHorizontal();
        }

        int ipWidth = iproc.getWidth();
        int ipHeight = iproc.getHeight();
        int pixelCount = iproc.getPixelCount();

        if (imageConfig.getSplitStack() == 1) {
            pixelCount /= 2;
            ipWidth /= 2;
        }


        byte [] R = new byte[pixelCount];
        byte [] G = new byte[pixelCount];
        byte [] B = new byte[pixelCount];

        ColorProcessor iproc3 = new ColorProcessor(ipWidth, iproc.getHeight());

        if (imageConfig.getSplitStack() == 1) {
            // crop the image for the red channel (originally the left, but we flipped the image horizontally)
            if (imageConfig.getFlipStack() == 1) {
                iproc.setRoi(new Rectangle(MIP_ip.getWidth()/2, 0, MIP_ip.getWidth()/2, MIP_ip.getHeight()));
            } else {
                iproc.setRoi(new Rectangle(0,0,MIP_ip.getWidth()/2, MIP_ip.getHeight()));
            }

            ImagePlus croppedIPR = new ImagePlus("R", iproc.crop());

            // crop the other half of the image to get green channel
            if (imageConfig.getFlipStack() == 1) {
                iproc.setRoi(new Rectangle(0, 0, MIP_ip.getWidth()/2, MIP_ip.getHeight()));
            } else {
                iproc.setRoi(new Rectangle(MIP_ip.getWidth()/2,0,MIP_ip.getWidth()/2, MIP_ip.getHeight()));
            }

            ImagePlus croppedIPG = new ImagePlus("G", iproc.crop());

            // set the display range from the sliders
            croppedIPR.setDisplayRange(ImageManager.getContrastMin1(), ImageManager.getContrastMax1());
            croppedIPG.setDisplayRange(ImageManager.getContrastMin2(),ImageManager.getContrastMax2());


            // convert the image to 8 bit
            ImageConverter icr = new ImageConverter(croppedIPR);
            icr.convertToGray8();
            ImageConverter icg = new ImageConverter(croppedIPG);
            icg.convertToGray8();

            R = (byte [])croppedIPR.getProcessor().getPixels();
            G = (byte [])croppedIPG.getProcessor().getPixels();

            //iproc3.getRGB(R, G, B);
        } else {
            // set the display range from the sliders
            if (colorChannelIdx == RED) {
                MIP_ip.setDisplayRange(ImageManager.getContrastMin1(), ImageManager.getContrastMax1());
            } else if (colorChannelIdx == GREEN) {
                MIP_ip.setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());
            } else if (colorChannelIdx == BLUE) {
                MIP_ip.setDisplayRange(ImageManager.getContrastMin3(), ImageManager.getContrastMax3());
            }

            ImageConverter ic = new ImageConverter(MIP_ip);
            ic.convertToGray8();

            ImageProcessor converted = MIP_ip.getProcessor();

            byte[] pix = (byte [])converted.getPixels();

            //iproc3.getRGB(R, G, B);

            if (colorChannelIdx == RED) {
                R = pix;
            } else if (colorChannelIdx == GREEN) {
                G = pix;
            } else if (colorChannelIdx == BLUE) {
                B = pix;
            }
        }


        ImagePlus ip = new ImagePlus();
        iproc3.setRGB(R, G, B);
        ip.setProcessor("test", iproc3);

        currentRPixelMap = R;
        currentGPixelMap = G;
        currentBPixelMap = B;

        //System.out.println("Contrast min, max for MIP 8bit: " + ip.getDisplayRangeMin() + ", " + ip.getDisplayRangeMax());


        return ip;
    }

    /**
     * Similar to convertMIPToRGB, but takes multiple max projections and create a single, multi-color
     * max projection image plus. The indices in colorChannelIndices must be parallel to MIP_ips and
     * indicate the respective color of each max projection in MIP_ips
     * @param MIP_ips
     * @param colorChannelIndices
     * @return
     */
    public static ImagePlus convertMultipleMIPsToRGB(ImagePlus[] MIP_ips, int[] colorChannelIndices, ImageConfig imageConfig) {
        if (MIP_ips.length != colorChannelIndices.length) return null;

        // use the first non-null max projection to set up the variables
        ImageProcessor iproc = null;
        int ipWidth = -1;
        int ipHeight = -1;
        int pixelCount = -1;
        for (ImagePlus MIP : MIP_ips) {
            if (MIP != null) {
                iproc = MIP.getProcessor();
                ipWidth = iproc.getWidth();
                ipHeight = iproc.getHeight();
                pixelCount = iproc.getPixelCount();
            }
        }
        //System.out.println(iproc.getNChannels() + ", " + iproc.getSliceNumber() + ", " + iproc.getBitDepth() + ", " + iproc.getPixelCount() + ", " + iproc.getPixelValue(30, 30));

        if (imageConfig.getSplitStack() == 1) {
            pixelCount /= 2;
            ipWidth /= 2;
        }


        byte [] R = new byte[pixelCount];
        byte [] G = new byte[pixelCount];
        byte [] B = new byte[pixelCount];


        ColorProcessor iproc3 = new ColorProcessor(ipWidth, ipHeight);

        // two inner conditions because more than 3 channel imaging is not currently supported
        for (int i = 0; i < MIP_ips.length && i < 3; i++) {
            if (MIP_ips[i] != null) {
                ImageProcessor iprocN = MIP_ips[i].getProcessor();

                if (imageConfig.getFlipStack() == 1) {
                    // flip the projection
                    iprocN.flipHorizontal();
                }


                if (imageConfig.getSplitStack() == 1) {
                    // crop the image so we just have the right side (originally the left, but we flipped the image horizontally)
                    iprocN.setRoi(MIP_ips[0].getWidth()/2, 0, MIP_ips[0].getWidth()/2, MIP_ips[0].getHeight());
                    ImagePlus croppedIP = new ImagePlus("", iprocN.crop());

                    int colorChannelIdx = colorChannelIndices[i];

                    // set the display ranges
                    if (colorChannelIdx == RED) {
                        croppedIP.setDisplayRange(ImageManager.getContrastMin1(), ImageManager.getContrastMax1());
                    } else if (colorChannelIdx == GREEN) {
                        croppedIP.setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());
                    } else if (colorChannelIdx == BLUE) {
                        croppedIP.setDisplayRange(ImageManager.getContrastMin3(), ImageManager.getContrastMax3());
                    }


                    // convert the image to 8 bit
                    ImageConverter ic = new ImageConverter(croppedIP);
                    ic.convertToGray8();


                    byte[] pix = (byte [])croppedIP.getProcessor().getPixels();


                    if (colorChannelIdx == RED) {
                        R = pix;
                    } else if (colorChannelIdx == GREEN) {
                        G = pix;
                    } else if (colorChannelIdx == BLUE) {
                        B = pix;
                    }
                } else {
                    // set the display ranges
                    int colorChannelIdx = colorChannelIndices[i];
                    if (colorChannelIdx == RED) {
                        MIP_ips[i].setDisplayRange(ImageManager.getContrastMin1(), ImageManager.getContrastMax1());
                    } else if (colorChannelIdx == GREEN) {
                        MIP_ips[i].setDisplayRange(ImageManager.getContrastMin2(), ImageManager.getContrastMax2());
                    } else if (colorChannelIdx == BLUE) {
                        MIP_ips[i].setDisplayRange(ImageManager.getContrastMin3(), ImageManager.getContrastMax3());
                    }

                    ImageConverter ic = new ImageConverter(MIP_ips[i]);
                    ic.convertToGray8();

                    ImageProcessor converted = MIP_ips[i].getProcessor();

                    byte[] pix = (byte [])converted.getPixels();


                    if (colorChannelIdx == RED) {
                        R = pix;
                    } else if (colorChannelIdx == GREEN) {
                        G = pix;
                    } else if (colorChannelIdx == BLUE) {
                        B = pix;
                    }
                }
            }

        }

        ImagePlus ip = new ImagePlus();
        iproc3.setRGB(R, G, B);
        ip.setProcessor("test", iproc3);

        currentRPixelMap = R;
        currentGPixelMap = G;
        currentBPixelMap = B;

        //System.out.println("Contrast min, max for MIP 8bit: " + ip.getDisplayRangeMin() + ", " + ip.getDisplayRangeMax());

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
    private static ImagePlus buildImagePlus(ImagePlus ip, byte[] rPixMap, byte[] gPixMap, byte[] bPixMap) {
        ImageProcessor iproc = ip.getProcessor();
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.setRGB(rPixMap, gPixMap, bPixMap);
        ip.setProcessor("test", iproc3);
        return ip;

    }

    public static byte[] getCurrentRPixelMap() { return currentRPixelMap; }
    public static byte[] getCurrentGPixelMap() { return currentGPixelMap; }
    public static byte[] getCurrentBPixelMap() { return currentBPixelMap; }

    private static int RED = 1;
    private static int GREEN = 2;
    private static int BLUE = 3;
}
