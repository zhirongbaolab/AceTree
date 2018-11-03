package org.rhwlab.image.management;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import org.rhwlab.image.ParsingLogic.ImageNameLogic;
import java.io.File;

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

    // runtime variables that are used to manage the image series data as it's viewed by the user (time, plane number, image height, etc.)
    private String currentImageFile;
    private ImagePlus currentImage;
    private int currentImageTime;
    private int currentImagePlane;
    private int imageHeight;
    private int imageWidth;

    private static boolean setOriginalContrastValues; // not quite sure what this is used for
    private static int contrastMin1, contrastMin2, contrastMax1, contrastMax2;
    private static final int MAX8BIT = 255, MAX16BIT = 65535;

    public ImageManager(ImageConfig imageConfig) {
        this.imageConfig = imageConfig;

        // Original contrast percentages
        contrastMin1 = contrastMin2 = 0;
        if (imageConfig.getUseStack() == 1) {
            contrastMax1 = contrastMax2 = MAX16BIT;
        } else {
            contrastMax1 = contrastMax2 = MAX8BIT;
        }


        // avoid errors by setting some default values
        this.currentImageTime = 1;
        this.currentImagePlane = 15; // usually about the middle of the stack
        this.setOriginalContrastValues = true;
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

    public void setCurrImage(ImagePlus currImg) { this.currentImage = currImg; }
    public ImagePlus getCurrentImage() { return this.currentImage; }

    /**
     * Called by AceTree.java to bring up the image series
     *
     * @return the first image in the series (configured by the user - not necessarily time 1)
     */
    public ImagePlus bringUpImageSeries() {

        // first thing we need to check if whether multiple image files (corresponding to different color channels) were provided in the config file
        // these two conditions are the result of the two conventions for supplying an <image> tag in the XML file. See documentation or ImageConfig.java
        if (imageConfig.areMultipleImageChannelsGiven()) {
            // only one file was provided --> let's see if it exists
            String imageFileFromConfig = imageConfig.getProvidedImageFileName();
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
                            this.imageConfig.setProvidedImageFileName(newFileNameAttempt);
                            this.currentImageFile = this.imageConfig.getProvidedImageFileName();

                            // because the image series is now known to be 16bit stacks, set the use stack flag to 1
                            this.imageConfig.setUseStack(1);
                        } else {
                            System.out.println("16bit image file name generated from 8bit image file name does not exist on the system. Can't bring up image series.");
                            return null;
                        }
                    } else {
                        System.out.println("Attempt to generate 16bit image file name from 8bit image file name failed. Can't bring up image series");
                        return null;
                    }
                }
            } else {
                // the supplied path is a real file

                if (ImageNameLogic.is8bitImage(imageFileFromConfig)) {
                    this.currentImageFile = imageFileFromConfig;

                    // load this image as the first in the image series

                } else {
                    // we now want to check whether this image file follows the iSIM or diSPIM data hierarchy conventions. If so,
                    // we'll take advantage of that knowledge and look for other files in the series

                    // check if a second color channel can be found if we assume the iSIM data output hierarchy and format
                    String secondColorChannelFromiSIM = ImageNameLogic.findSecondiSIMColorChannel(imageFileFromConfig);
                    if (!secondColorChannelFromiSIM.isEmpty()) {
                        // let's check again that the files exist before we pass them off to be opened
                    }

                    // check if a second color channel can be found is we assume the diSPIM data output hierarchy and format
                    String secondColorChannelFromdiSPIM = ImageNameLogic.findSecondDiSPIMColorChannel(imageFileFromConfig);
                    if (!secondColorChannelFromdiSPIM.isEmpty()) {
                        // let's check again that the files exist before we pass them off to be opened
                    }

                    // if none of the above options produced a second image file containing the second color channel, we'll assume that the supplied image is a
                    // stack that contains both color channels in it

                }

            }
        } else {
            if (imageConfig.getNumChannels() > 2) {
                System.out.println("WARNING: More than three image channels were supplied in the .XML file. At this point," +
                        "AceTree only supports viewing 2 channels in either split or side by side mode. All image file names " +
                        "will be loaded, but only the first two will be processed and displayed.");
            }

            // multiple images were provided in the config file. we need to query them slightly differently and then check if they exist
            String[] images = imageConfig.getImageChannels();

        }

        // if we've reached here, a valid image file was supplied and set to the correct runtime var. let's bring it up
        return makeImage();
    }

    /**
     *
     * @param tif_8bit
     * @return
     */
    public ImagePlus makeImageFrom8Bittif(String tif_8bit) {
        ImagePlus ip = new Opener().openImage(tif_8bit); // no need for other arguments, the file is just a single plane at a single timepoint
        if (ip != null) {
            this.imageWidth = ip.getWidth();
            this.imageHeight = ip.getHeight();

            ip = ImageConversionManager.convert8bittifToRGB(ip, this.imageConfig);
        } else {
            ip = new ImagePlus();
            ImageProcessor iproc = new ColorProcessor(this.imageWidth, this.imageHeight);
            ip.setProcessor(this.currentImageFile, iproc);
        }

        return ip;
    }

    /**
     *
     * @param TIF_16bit - may include one or more color channels
     * @return
     */
    public ImagePlus makeImageFromSingle16BitTIF(String TIF_16bit) {
        ImagePlus ip = new Opener().openImage(TIF_16bit, this.currentImagePlane);

        if (ip != null) {
            this.imageWidth = ip.getWidth();
            this.imageHeight = ip.getHeight();

            ip = ImageConversionManager.convertSingle16BitTIFToRGB(ip, this.imageConfig);
        } else {
            ip = new ImagePlus();
            ImageProcessor iproc = new ColorProcessor(this.imageWidth, this.imageHeight);
            ip.setProcessor(this.currentImageFile, iproc);
        }

        return ip;
    }

    /**
     *
     * @param TIFs_16bit_names it is assumed that each TIF represents a different color channel for the image series
     * @return
     */
    public ImagePlus makeImageFromMultiple16BitTIFs(String[] TIFs_16bit_names) {
        ImagePlus[] TIFs_16bit = new ImagePlus[TIFs_16bit_names.length];

        for (int i = 0; i < TIFs_16bit_names.length; i++) {
            TIFs_16bit[i] = new Opener().openImage(TIFs_16bit_names[i], this.currentImagePlane);
            if (TIFs_16bit[i] == null) {
                System.err.println("Couldn't make image from: " + TIFs_16bit_names[i]);
                return null;
            }
        }


        return ImageConversionManager.convertMultiple16BitTIFsToRGB(TIFs_16bit, this.imageConfig);
    }




    /**
     * Called by:
     * - AceTree.bringUpSeriesUI() to bring up the first image in the series
     * @return
     */
    public ImagePlus makeImage() {
        ImagePlus ip;

        ip = makeImageFromTif();

        if (ip != null) {
            this.imageWidth = ip.getWidth();
            this.imageHeight = ip.getHeight();
            this.currentImage = ip;
        }

        return ip;
    }



    private ImagePlus makeImageFromTif() {
        ImagePlus ip = null;

        if (imageConfig.getUseStack() == 1) { //16bit images are present
            try {
                System.out.println("trying to open: " + this.currentImageFile + " at plane: " + this.currentImagePlane);
                ip = new Opener().openImage(this.currentImageFile, this.currentImagePlane);
            } catch (IllegalArgumentException iae) {
                System.out.println("Exception in ImageWindow.doMakeImageFromTif(String)");
                System.out.println("TIFF file required.");
            }

        } else{ //8bit images
            ip = new Opener().openImage(this.currentImageFile); // no need for other arguments, the file is just a single plane at a single timepoint
        }

        // TODO - ask if this is the kind of thing we want to lift out of this image manager all together

        // if the Image was correctly processed and open, we want to convert it to 8bit RGB for display in the window
        if (ip != null) {
            this.imageWidth = ip.getWidth();
            this.imageHeight = ip.getHeight();

            ip = ImageConversionManager.convertToRGB(ip, this.imageConfig, this.currentImagePlane);
        } else {
            ip = new ImagePlus();
            ImageProcessor iproc = new ColorProcessor(this.imageWidth, this.imageHeight);
            ip.setProcessor(this.currentImageFile, iproc);
        }

        return ip;
    }

    // accessors and mutators for static variables
    public static void setOriginContrastValuesFlag(boolean OCVF) { setOriginalContrastValues = OCVF; }
    public static void setContrastMin1(int cMin1) { contrastMin1 = cMin1; }
    public static void setContrastMin2(int cMin2) { contrastMin2 = cMin2; }
    public static void setContrastMax1(int cMax1) { contrastMax1 = cMax1; }
    public static void setContrastMax2(int cMax2) { contrastMax2 = cMax2; }
    public static boolean getOriginalContrastValuesFlag() { return setOriginalContrastValues; }
    public static int getContrastMin1() { return contrastMin1; }
    public static int getContrastMin2() { return contrastMin2; }
    public static int getContrastMax1() { return contrastMax1; }
    public static int getContrastMax2() { return contrastMax2; }
}
