package org.rhwlab.image.management;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import org.rhwlab.image.ParsingLogic.ImageFileParser;
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

    private static boolean setOriginalContrastValues; // not quite sure what this is used for
    private static int contrastMin1, contrastMin2, contrastMax1, contrastMax2;
    private static final int MAX8BIT = 255, MAX16BIT = 65535;

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
                        this.currentImageFile = this.imageConfig.getTifPrefix();

                        // because the image series is now known to be 16bit stacks, set the use stack flag to 1
                        this.imageConfig.setUseStack(1);
                    } else {
                        System.out.println("16bit image file name generated from 8bit image file name does not exist on the system. Can't bring up image series.");
                    }
                } else {
                    System.out.println("Attempt to generate 16bit image file name from 8bit image file name failed. Can't bring up image series");
                }
            }
        } else {
            // the supplied path is a real file
            this.currentImageFile = this.imageConfig.getTifPrefix();
        }

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


    /**
     * Called by:
     * - AceTree.bringUpSeriesUI() to bring up the first image in the series
     * @return
     */
    public ImagePlus makeImage() {
        ImagePlus ip;

        ip = doMakeImageFromTif();

        if (ip != null) {
            this.imageWidth = ip.getWidth();
            this.imageHeight = ip.getHeight();
            this.currentImage = ip;
        }

        return ip;
    }

    private ImagePlus doMakeImageFromTif() {
        ImagePlus ip = null;

        if (imageConfig.getUseStack() == 1) { //16bit images are present
            System.out.println("ImageManager doMakeImageFromTif using stack: 1");
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
