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
    public static ImagePlus convertToRGB(ImagePlus ip, ImageConfig imageConfig, int currentImagePlane) {
        /** this is a check for whether we are using 8bit (useStack = 0) or 16bit (useStack = 1) */
        if(imageConfig.getUseStack() == 1) {
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
        } else { /** Just load the 8bit images */
            //original version
            FileInfo fi;
            fi = ip.getFileInfo();
            if (fi.getBytesPerPixel() != 8) {
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
            R = getRedChannel(R, imageConfig, currentImagePlane);

            return buildImagePlus(ip, R, G, B);
        }
    }

    /**
     *
     * @param R
     * @return
     */
    private static byte[] getRedChannel(byte [] R, ImageConfig imageConfig, int currentImagePlane) {
        String fileName = makeRedChannelName(imageConfig);
        File f = new File(fileName);
        if (f.exists()) {
            ImagePlus ip;

            if (imageConfig.getUseStack() == 1){
                ip = new Opener().openImage(fileName, currentImagePlane);
            }
            else{
                ip = new Opener().openImage(fileName);
            }
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
        return imageConfig.getTifPrefix().replace("/tif/", "/tifR");
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
