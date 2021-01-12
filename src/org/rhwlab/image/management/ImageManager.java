package org.rhwlab.image.management;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.Opener;
import ij.plugin.ZProjector;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.rhwlab.image.ParsingLogic.ImageNameLogic;
import java.io.File;
import java.util.Hashtable;

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
    private ImagePlus currentImage;
    private String currentImageName;
    private int currentImageTime;
    private int currentImagePlane;
    private int imageHeight;
    private int imageWidth;
    private boolean isCurrImageMIP;
    private boolean timeChange;
    private boolean planeChange;

    private IntegerProperty timeProperty;

    private static boolean setOriginalContrastValues; // not quite sure what this is used for
    private static int contrastMin1, contrastMin2, contrastMax1, contrastMax2, contrastMin3, contrastMax3;
    private static boolean contrastChange;
    private static final int MAX8BIT = 255, MAX16BIT = 65535;
    private static final int GREEN_ID = 1;
    private static final int RED_ID = 2;
    private static final int BLUE_ID = 3;

    public ImageManager(ImageConfig imageConfig) {
        this.imageConfig = imageConfig;

        // Original contrast percentages
        contrastMin1 = contrastMin2 = contrastMin3 = 0;
        if (imageConfig.getUseStack() == 1) {
            contrastMax1 = contrastMax2 = contrastMax3 = MAX16BIT;
        } else {
            contrastMax1 = contrastMax2 = contrastMax3 = MAX8BIT;
        }



        // avoid errors by setting some default values
        this.currentImageName = "";
        this.currentImageTime = 1;
        this.currentImagePlane = 15; // usually about the middle of the stack
        this.setOriginalContrastValues = true;
        this.isCurrImageMIP = false;
        this.timeChange = false;
        this.planeChange = false;
        this.contrastChange = false;

        // timeProperty is a variable that is needed for the 3D Window and harmless when not used
        this.timeProperty = new SimpleIntegerProperty(this.currentImageTime);
    }

    // methods to set runtime parameters
    public void setCurrImageTime(int time) {
        int prevImageTime = currentImageTime;
        this.currentImageTime = time;
        if (currentImageTime != prevImageTime) {
            timeChange = true;
        }
        this.timeProperty.set(this.currentImageTime);
    }
    public int getCurrImageTime() { return this.currentImageTime; }

    public void setCurrImagePlane(int plane) {
        int prevImagePlane = currentImagePlane;
        this.currentImagePlane = plane;
        if (currentImagePlane != prevImagePlane) {
            planeChange = true;
        }
    }
    public int getCurrImagePlane() { return this.currentImagePlane; }

    public void setCurrImage(ImagePlus currImg) { this.currentImage = currImg; }
    public ImagePlus getCurrentImage() { return this.currentImage; }

    public void setCurrImageName(String currImgName) { this.currentImageName = currImgName; }
    public String getCurrentImageName() { return this.currentImageName; }

    public IntegerProperty getTimeProperty() { return this.timeProperty; }

    // methods for runtime updates

    /**
     * Controller for using the arrow keys to move forward and backward in time. Implements an easter
     * egg feature where you can access images outside of the start/end specified range by using the
     * time controls at the bottom of the main AceTree window.
     *
     * @param timeIncrement
     */
    public void incrementImageTimeNumber(int timeIncrement) {
//        if (this.currentImageTime + timeIncrement < this.imageConfig.getStartingIndex() || this.currentImageTime + timeIncrement > this.imageConfig.getEndingIndex()) {
//            // handles the case when the easter egg feature of indexing to a time outside start and end. no movement allowed here
//
//        }
        if (timeIncrement > 0
                && this.currentImageTime != this.imageConfig.getEndingIndex()) {
            setCurrImageTime(this.currentImageTime + timeIncrement);
        } else if (timeIncrement < 0
                    && this.currentImageTime != this.imageConfig.getStartingIndex()) {
            setCurrImageTime(this.currentImageTime + timeIncrement);
        }
    }

    public void incrementImagePlaneNumber(int planeIncrement) {
        if (planeIncrement > 0
                && this.currentImagePlane + planeIncrement <= this.imageConfig.getPlaneEnd()) {
            setCurrImagePlane(this.currentImagePlane + planeIncrement);
        }
        else if (planeIncrement < 0
                    && this.currentImagePlane + planeIncrement >= 1) {
            setCurrImagePlane(this.currentImagePlane + planeIncrement);
        }
    }

    /**
     * Called by AceTree.java to bring up the image series
     *
     * @return the first image in the series (configured by the user - not necessarily time 1)
     */
    public ImagePlus bringUpImageSeries() {
        System.out.println("Bringing up image series");

        // first thing we need to check if whether multiple image files (corresponding to different color channels) were provided in the config file
        // these two conditions are the result of the two conventions for supplying an <image> tag in the XML file. See documentation or ImageConfig.java
        if (!imageConfig.areMultipleImageChannelsGiven()) {
            // only one file was provided --> let's see if it exists
            String imageFile = imageConfig.getProvidedImageFileName();
            //System.out.println("Checking on file: " + imageFile);
            if(!new File(imageFile).exists()) {
                System.out.println("The image listed in the config file does not exist on the system. Checking if it's an 8bit image that no longer exists...");

                // it doesn't exist. It's likely an 8bit image file name that no longer exists, so let's do a check on the
                // file type first (not completely reliable check) and if it's 8bit, we'll try and find a 16bit image. We can't
                // use the normal getImageBitDepth method because it assumes a real file, and we know this one does not exist
                if (ImageNameLogic.doesImageFollow8bitDeletedConvention(imageFile)) {
                    System.out.println("The image has an 8bit file naming convention -> trying to find it's 16bit corollary....");
                    String newFileNameAttempt = ImageNameLogic.reconfigureImagePathFrom8bitTo16bit(imageFile);
                    if (!newFileNameAttempt.equals(imageFile)) {
                        System.out.println("A 16bit file name was generated from the 8bit image file name in the config file. Checking if it exists...");
                        //System.out.println(newFileNameAttempt);
                        if (new File(newFileNameAttempt).exists()) {
                            System.out.println("16bit image file exists. Updating file in ImageConfig to: " + newFileNameAttempt);
                            this.imageConfig.setProvidedImageFileName(newFileNameAttempt);
                            this.imageConfig.setImagePrefixes();

                            // because the image series is now known to be 16bit stacks, we specify the two assumptions about them
                            // that AceTree makes (derived from the confocal microscope): flip and split the stack
                            // ** NOTE: if these assumptions are explicitly given, we don't override them
                            this.imageConfig.setUseStack(1);

                            if (!this.imageConfig.isSplitStackGiven()) {
                                this.imageConfig.setSplitStack(1);
                            }

                            if (!this.imageConfig.isFlipStackGiven()) {
                                this.imageConfig.setFlipStack(1);
                            }


                            // set the starting time if it hasn't been manually specified
                            int timeInSuppliedImage = ImageNameLogic.extractTimeFromImageFileName(this.imageConfig.getProvidedImageFileName());
                            if (this.imageConfig.getStartingIndex() == -1) {
                                // this indicates that a starting index was not supplied in the xml. set the starting index to 1 and bring up the supplied image
                                this.imageConfig.setStartingIndex(1);
                                this.currentImageTime = timeInSuppliedImage;
                                this.currentImageName = this.imageConfig.getProvidedImageFileName();
                            } else if (timeInSuppliedImage > this.imageConfig.getEndingIndex() || timeInSuppliedImage < this.imageConfig.getStartingIndex()) {
                                this.currentImageTime = this.imageConfig.getStartingIndex();
                                this.currentImageName = ImageNameLogic.appendTimeToSingle16BitTIFPrefix(this.imageConfig.getImagePrefixes()[0], this.imageConfig.getStartingIndex());
                            } else if (timeInSuppliedImage >= this.imageConfig.getStartingIndex() && timeInSuppliedImage <= this.imageConfig.getEndingIndex()) {
                                this.currentImageTime = timeInSuppliedImage;
                                this.currentImageName = this.imageConfig.getProvidedImageFileName();
                            }

                            ImagePlus ip = makeImageFromSingle16BitTIF(this.currentImageName);
                            this.currentImage = ip;


                            return ip;
                        } else {
                            System.out.println("16bit image file name generated from 8bit image file name does not exist on the system. Can't bring up image series. Tried image name: " +
                            newFileNameAttempt);
                            return null;
                        }
                    } else {
                        System.out.println("Attempt to generate 16bit image file name from 8bit image file name failed. Can't bring up image series");
                        return null;
                    }
                } else {
                    System.out.println("Provided image file doesn't follow 8bit deleted convention. Can't bring up image series.");
                    return null;
                }
            } else {
                // if we've reached here, either the supplied file exists, or a 16bit corollary was found and we will now proceed with that
                if (getImageBitDepth(imageFile) == _8BIT_ID) {
                    // load this image as the first in the image series
                    this.imageConfig.setUseStack(0); // in case it isn't correctly set
                    this.imageConfig.setFlipStack(0);
                    this.imageConfig.setSplitStack(0);


                    // set the starting time if it hasn't been manually specified
                    int timeInSuppliedImage = ImageNameLogic.extractTimeFromImageFileName(this.imageConfig.getProvidedImageFileName());
                    if (this.imageConfig.getStartingIndex() == -1) {
                        // this indicates that a starting index was not supplied in the xml. set the starting index to 1 and bring up the supplied image
                        this.imageConfig.setStartingIndex(1);
                        this.currentImageTime = timeInSuppliedImage;
                        this.currentImageName = this.imageConfig.getProvidedImageFileName();
                    } else if (timeInSuppliedImage > this.imageConfig.getEndingIndex() || timeInSuppliedImage < this.imageConfig.getStartingIndex()) {
                        this.currentImageTime = this.imageConfig.getStartingIndex();
                        this.currentImageName = ImageNameLogic.appendTimeAndPlaneTo8BittifPrefix(this.imageConfig.getImagePrefixes()[0], this.imageConfig.getStartingIndex(), 1);
                    } else if (timeInSuppliedImage >= this.imageConfig.getStartingIndex() && timeInSuppliedImage <= this.imageConfig.getEndingIndex()) {
                        this.currentImageTime = timeInSuppliedImage;
                        this.currentImageName = this.imageConfig.getProvidedImageFileName();
                    }


                    ImagePlus ip = makeImageFrom8Bittif(this.currentImageName);
                    this.currentImage = ip;

                    return ip;
                } else if (getImageBitDepth(imageFile) == _16BIT_ID || getImageBitDepth(imageFile) == _32BIT_ID) {
                    // we now want to check whether this image file follows the iSIM or diSPIM data hierarchy conventions. If so,
                    // we'll take advantage of that knowledge and look for other files in the series

                    // check if a second color channel can be found if we assume the iSIM data output hierarchy and format
                    String secondColorChannelFromiSIM = ImageNameLogic.findSecondiSIMColorChannel(imageFile);
                    if (!secondColorChannelFromiSIM.isEmpty()) {
                        //System.out.println("ImageManager found second channel stack by assuming iSIM data structure. Loading both channels...");
                        // the assumptions for the iSIM: don't flip, don't split
                        this.imageConfig.setUseStack(1);
                        if (!this.imageConfig.isSplitStackGiven()) {
                            this.imageConfig.setSplitStack(0);
                        }

                        if (!this.imageConfig.isFlipStackGiven()) {
                            this.imageConfig.setFlipStack(0);
                        }

                        // we need to add this second color channel to the image config so that its prefix will be maintained
                        this.imageConfig.addColorChannelImageToConfig(secondColorChannelFromiSIM);

                        // set the starting time if it hasn't been manually specified
                        String[] firstImages = null;
                        int timeInSuppliedImage = ImageNameLogic.extractTimeFromImageFileName(this.imageConfig.getProvidedImageFileName());
                        if (this.imageConfig.getStartingIndex() == -1) {
                            // this indicates that a starting index was not supplied in the xml. set the starting index to 1 and bring up the supplied image
                            this.imageConfig.setStartingIndex(1);
                            this.currentImageTime = timeInSuppliedImage;
                            this.currentImageName = this.imageConfig.getProvidedImageFileName();

                            // because we have the full paths in this instance, we'll call the makeImage method directly with the names. During runtime,
                            // as the user changes images, this will need to first be piped through a method to query the prefixes from ImageConfig and
                            // append the desired time
                            firstImages = new String[]{imageFile, secondColorChannelFromiSIM};
                        } else if (timeInSuppliedImage > this.imageConfig.getEndingIndex() || timeInSuppliedImage < this.imageConfig.getStartingIndex()) { // supplied image out of range, load first in range
                            this.currentImageTime = this.imageConfig.getStartingIndex();
                            firstImages = ImageNameLogic.appendTimeToMultiple16BitTifPrefixes(this.imageConfig.getImagePrefixes(), this.currentImageTime);
                        } else if (timeInSuppliedImage >= this.imageConfig.getStartingIndex() && timeInSuppliedImage <= this.imageConfig.getEndingIndex()) {
                            this.currentImageTime = timeInSuppliedImage;
                            this.currentImageName = this.imageConfig.getProvidedImageFileName();
                            firstImages = new String[]{imageFile, secondColorChannelFromiSIM};
                        }

                        ImagePlus ip = makeImageFromMultiple16BitTIFs(firstImages);
                        this.currentImage = ip;

                        return ip;
                    }

                    // check if a second color channel can be found is we assume the diSPIM data output hierarchy and format
                    String secondColorChannelFromdiSPIM = ImageNameLogic.findSecondDiSPIMColorChannel(imageFile);
                    if (!secondColorChannelFromdiSPIM.isEmpty()) {
                        //System.out.println("ImageManager found second channel stack by assuming diSPIM data structure. Loading both channels...");
                        // the assumptions for the diSIM: don't flip, don't split
                        this.imageConfig.setUseStack(1);

                        if (!this.imageConfig.isSplitStackGiven()) {
                            this.imageConfig.setSplitStack(0);
                        }

                        if (!this.imageConfig.isFlipStackGiven()) {
                            this.imageConfig.setFlipStack(0);
                        }

                        // add the second color channel to the image config so that its prefix will be maintained
                        this.imageConfig.addColorChannelImageToConfig(secondColorChannelFromdiSPIM);

                        // set the starting time if it hasn't been manually specified
                        String[] firstImages = null;
                        int timeInSuppliedImage = ImageNameLogic.extractTimeFromImageFileName(this.imageConfig.getProvidedImageFileName());
                        if (this.imageConfig.getStartingIndex() == -1) {
                            // this indicates that a starting index was not supplied in the xml. set the starting index to 1 and bring up the supplied image
                            this.imageConfig.setStartingIndex(1);
                            this.currentImageTime = timeInSuppliedImage;
                            this.currentImageName = this.imageConfig.getProvidedImageFileName();

                            // because we have the full paths in this instance, we'll call the makeImage method directly with the names. During runtime,
                            // as the user changes images, this will need to first be piped through a method to query the prefixes from ImageConfig and
                            // append the desired time
                            firstImages = new String[]{imageFile, secondColorChannelFromdiSPIM};
                        } else if (timeInSuppliedImage > this.imageConfig.getEndingIndex() || timeInSuppliedImage < this.imageConfig.getStartingIndex()) {
                            this.currentImageTime = this.imageConfig.getStartingIndex();
                            firstImages = ImageNameLogic.appendTimeToMultiple16BitTifPrefixes(this.imageConfig.getImagePrefixes(), this.currentImageTime);
                        } else if (timeInSuppliedImage >= this.imageConfig.getStartingIndex() && timeInSuppliedImage <= this.imageConfig.getEndingIndex()) {
                            this.currentImageTime = timeInSuppliedImage;
                            this.currentImageName = this.imageConfig.getProvidedImageFileName();
                            firstImages = new String[]{imageFile, secondColorChannelFromdiSPIM};
                        }

                        ImagePlus ip = makeImageFromMultiple16BitTIFs(firstImages);
                        this.currentImage = ip;

                        return ip;
                    }

                    // check if this is a rare case of a 16bit slice that needs to be opened as if it was an 8bit image but with higher bit depth
                    if (ImageNameLogic.isSliceImage(imageFile)) {
                        // assumptions for the general 16bit slice: don't flip, don't split
                        this.imageConfig.setUseStack(0);

                        // NOT SURE IF THESE ARE APPLICABLE IN THE SLICE CASE
                        if (!this.imageConfig.isSplitStackGiven()) {
                            this.imageConfig.setSplitStack(0);
                        }

                        if (!this.imageConfig.isFlipStackGiven()) {
                            this.imageConfig.setFlipStack(0);
                        }


                        // set the starting time if it hasn't been manually specified
                        int timeInSuppliedImage = ImageNameLogic.extractTimeFromImageFileName(this.imageConfig.getProvidedImageFileName());
                        if (this.imageConfig.getStartingIndex() == -1) {
                            // this indicates that a starting index was not supplied in the xml. set the starting index to 1 and bring up the supplied image
                            this.imageConfig.setStartingIndex(1);
                            this.currentImageTime = timeInSuppliedImage;
                            this.currentImageName = this.imageConfig.getProvidedImageFileName();
                        } else if (timeInSuppliedImage > this.imageConfig.getEndingIndex() || timeInSuppliedImage < this.imageConfig.getStartingIndex()) {
                            this.currentImageTime = this.imageConfig.getStartingIndex();
                            this.currentImageName = ImageNameLogic.appendTimeToSingle16BitTIFPrefix(this.imageConfig.getImagePrefixes()[0], this.imageConfig.getStartingIndex());
                        } else if (timeInSuppliedImage >= this.imageConfig.getStartingIndex() && timeInSuppliedImage <= this.imageConfig.getEndingIndex()) {
                            this.currentImageTime = timeInSuppliedImage;
                            this.currentImageName = this.imageConfig.getProvidedImageFileName();
                        }

                        ImagePlus ip = makeImageFrom16bitSliceTIF(this.currentImageName);
                        this.currentImage = ip;

                        return ip;
                    }

                    // if none of the above options produced a second image file containing the second color channel or determined that we have a 16bit slide
                    // we'll assume that the supplied image is from a confocal microscope, i.e. the assumptions are: split, flip
                    this.imageConfig.setUseStack(1);

                    if (!this.imageConfig.isSplitStackGiven()) {
                        this.imageConfig.setSplitStack(1);
                    }

                    if (!this.imageConfig.isFlipStackGiven()) {
                        this.imageConfig.setFlipStack(1);
                    }


                    // set the starting time if it hasn't been manually specified
                    int timeInSuppliedImage = ImageNameLogic.extractTimeFromImageFileName(this.imageConfig.getProvidedImageFileName());
                    if (this.imageConfig.getStartingIndex() == -1) {
                        // this indicates that a starting index was not supplied in the xml. set the starting index to 1 and bring up the supplied image
                        this.imageConfig.setStartingIndex(1);
                        this.currentImageTime = timeInSuppliedImage;
                        this.currentImageName = this.imageConfig.getProvidedImageFileName();
                    } else if (timeInSuppliedImage > this.imageConfig.getEndingIndex() || timeInSuppliedImage < this.imageConfig.getStartingIndex()) {
                        this.currentImageTime = this.imageConfig.getStartingIndex();
                        this.currentImageName = ImageNameLogic.appendTimeToSingle16BitTIFPrefix(this.imageConfig.getImagePrefixes()[0], this.imageConfig.getStartingIndex());
                    } else if (timeInSuppliedImage >= this.imageConfig.getStartingIndex() && timeInSuppliedImage <= this.imageConfig.getEndingIndex()) {
                        this.currentImageTime = timeInSuppliedImage;
                        this.currentImageName = this.imageConfig.getProvidedImageFileName();
                    }

                    ImagePlus ip = makeImageFromSingle16BitTIF(this.currentImageName);
                    this.currentImage = ip;

                    return ip;
                }
            }
        } else {
            // assume that the stacks specified are from the confocal microscope, i.e. assume: flip, split
            this.imageConfig.setUseStack(1);

            if (!this.imageConfig.isSplitStackGiven()) {
                this.imageConfig.setSplitStack(1);
            }

            if (!this.imageConfig.isFlipStackGiven()) {
                this.imageConfig.setFlipStack(1);
            }

            if (imageConfig.getNumChannels() > 3) {
                System.out.println("WARNING: More than three image channels were supplied in the .XML file. At this point," +
                        "AceTree only supports viewing 3 channels. All image file names " +
                        "will be loaded, but only the first three will be processed and displayed.");
            }
            // multiple images were provided in the config file. we need to query them slightly differently and then check if they exist
            String[] images = imageConfig.getImageChannels();

            for (String s : images) {
                if (!s.isEmpty()) {
                    this.currentImageName = s;
                    break;
                }
            }


            // set the starting time if it hasn't been manually specified
            String[] firstImages = null;
            int timeInSuppliedImage = ImageNameLogic.extractTimeFromImageFileName(this.imageConfig.getProvidedImageFileName());
            if (this.imageConfig.getStartingIndex() == -1) {
                // this indicates that a starting index was not supplied in the xml. set the starting index to 1 and bring up the supplied image
                this.imageConfig.setStartingIndex(1);
                this.currentImageTime = timeInSuppliedImage;
                this.currentImageName = this.imageConfig.getProvidedImageFileName();

                // because we have the full paths in this instance, we'll call the makeImage method directly with the names. During runtime,
                // as the user changes images, this will need to first be piped through a method to query the prefixes from ImageConfig and
                // append the desired time
                firstImages = images;
            } else if (timeInSuppliedImage > this.imageConfig.getEndingIndex() || timeInSuppliedImage < this.imageConfig.getStartingIndex()) {
                this.currentImageTime = this.imageConfig.getStartingIndex();
                firstImages = ImageNameLogic.appendTimeToMultiple16BitTifPrefixes(this.imageConfig.getImagePrefixes(), this.currentImageTime);
            } else if (timeInSuppliedImage >= this.imageConfig.getStartingIndex() && timeInSuppliedImage <= this.imageConfig.getEndingIndex()) {
                this.currentImageTime = timeInSuppliedImage;
                this.currentImageName = this.imageConfig.getProvidedImageFileName();
                firstImages = images;
            }

            ImagePlus ip = makeImageFromMultiple16BitTIFs(firstImages);
            this.currentImage = ip;

            return ip;
        }

        System.out.println("ImageManager.bringUpImageSeries reached code end. Returning null.");
        return null;
    }

    /**
     * Method to open 8bit tif image. Looks for a second color channel each time in the event that one is present
     * but not at all time points
     *
     * @param tif_8bit
     * @return
     */
    private ImagePlus makeImageFrom8Bittif(String tif_8bit) {
        if (!new File(tif_8bit).exists()) {
            //System.out.println("*** The file: " + tif_8bit + " does NOT exist on this system ***");
            return null;
        }
        ImagePlus ip = new Opener().openImage(tif_8bit); // no need for other arguments, the file is just a single plane at a single timepoint
        if (ip != null) {
            this.imageWidth = ip.getWidth();
            this.imageHeight = ip.getHeight();

            // try and open a second channel according to the tif/ tifR/ convention
            String secondColorChannelAttempt = ImageNameLogic.findSecondColorChannelFromSliceImage(tif_8bit);
            if (!tif_8bit.equals(secondColorChannelAttempt)) {
                // a second color channel was found, so load both images as one layered, RGB image
                ImagePlus ip2 = new Opener().openImage(secondColorChannelAttempt);
                if (ip2 != null) {
                    if (tif_8bit.contains(ImageNameLogic.tifDir) || tif_8bit.contains(ImageNameLogic.tifDir_2)) {
                        return ImageConversionManager.convertMultiple8bittifsToRGB(ip, ip2, this.imageConfig);
                    } else if (tif_8bit.contains(ImageNameLogic.tifRDir) || tif_8bit.contains(ImageNameLogic.tifRDir_2)) {
                        return ImageConversionManager.convertMultiple8bittifsToRGB(ip2, ip, this.imageConfig);
                    }
                } else {
                    System.out.println("System found second color channel image for tif slice series, but could not open the image. Just opening single image. Image not opened: " + secondColorChannelAttempt);
                    return ImageConversionManager.convert8bittifToRGB(ip, this.imageConfig);
                }
            } else {
                // a second color channel was not found, so just load the single image
                return ImageConversionManager.convert8bittifToRGB(ip, this.imageConfig);
            }
        }

        return null;
    }
    private ImagePlus makeImageFrom8Bittif() { return makeImageFrom8Bittif(this.currentImageName); }

    /**
     *
     * @param TIF_slice_16bit
     * @return
     */
    private ImagePlus makeImageFrom16bitSliceTIF(String TIF_slice_16bit) {
        if (!new File(TIF_slice_16bit).exists()) {
            //System.out.println("*** The file: " + TIF_slice_16bit + " does NOT exist on this system ***");
            return null;
        }

        ImagePlus ip = new Opener().openImage(TIF_slice_16bit);
        if (ip != null) {
            this.imageWidth = ip.getWidth();
            this.imageHeight = ip.getHeight();

            return ImageConversionManager.convert16bitSliceTIFToRGB(ip, this.imageConfig);
        }

        return null;
    }
    private ImagePlus makeImageFrom16bitSliceTIF() { return makeImageFrom16bitSliceTIF(this.currentImageName); }

    /**
     *
     * @param TIF_16bit - may include one or more color channels
     * @return
     */
    private ImagePlus makeImageFromSingle16BitTIF(String TIF_16bit) {
        if (!new File(TIF_16bit).exists()) {
            //System.out.println("*** The file: " + TIF_16bit + " does NOT exist on this system ***");
            return null;
        }

        ImagePlus ip = new Opener().openImage(TIF_16bit, this.currentImagePlane);

        if (ip != null) {
            this.imageWidth = ip.getWidth();
            this.imageHeight = ip.getHeight();

            return ImageConversionManager.convertSingle16BitTIFToRGB(ip, this.imageConfig);
        }

        return null;
    }
    private ImagePlus makeImageFromSingle16BitTIF() { return makeImageFromSingle16BitTIF(this.currentImageName); }

    /**
     *
     * @param TIFs_16bit_names it is assumed that each TIF represents a different color channel for the image series
     * @return
     */
    private ImagePlus makeImageFromMultiple16BitTIFs(String[] TIFs_16bit_names) {
        ImagePlus[] TIFs_16bit = new ImagePlus[TIFs_16bit_names.length];

        int i = 0;
        for (; i < TIFs_16bit_names.length; i++) {
            if (!TIFs_16bit_names[i].isEmpty() && new File(TIFs_16bit_names[i]).exists()) {
                TIFs_16bit[i] = new Opener().openImage(TIFs_16bit_names[i], this.currentImagePlane);

                if (TIFs_16bit[i] == null) {
                    System.err.println("Couldn't make image from: " + TIFs_16bit_names[i]);
                    return null;
                }

                this.imageWidth = TIFs_16bit[i].getWidth();
                this.imageHeight = TIFs_16bit[i].getHeight();
            }
        }

        // error check. make sure at least one image is valid
        boolean valid = false;
        for (int k = 0; k < i; k++) {
            if (TIFs_16bit[k] != null) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            return null;
        }

        /** determine the exact configuration among the 6 possible options of multiple channels:
         * 1. RED/GREEN
         * 2. RED/GREEN/BLUE
         * 3. /GREEN
         * 4. /BLUE
         * 5. /GREEN/BLUE
         * 6. RED//BLUE
         */

        boolean red_valid, green_valid, blue_valid;
        red_valid = green_valid = blue_valid = true; // assume RGB

        // check for RED/GREEN image series
        if (TIFs_16bit.length == 2 && TIFs_16bit[0] != null && TIFs_16bit[1] != null) {
            //System.out.println("RED/GREEN mode");
            blue_valid = false;
        }

        // check for RED/GREEN/BLUE is assumed above

        // check for a GREEN image series with a blank red file entry
        if (TIFs_16bit.length == 2 && TIFs_16bit[0] == null && TIFs_16bit[1] != null) {
            red_valid = blue_valid = false;
            //System.out.println("GREEN mode");
        }

        // check for a BLUE image series with blank red, green file entries
        if (TIFs_16bit.length == 3 && TIFs_16bit[0] == null && TIFs_16bit[1] == null && TIFs_16bit[2] != null) {
            //System.out.println("BLUE mode");
            red_valid = green_valid = false;
        }

        // check for a GREEN/BLUE image series with a blank red file entry
        if (TIFs_16bit.length == 3 && TIFs_16bit[0] == null && TIFs_16bit[1] != null && TIFs_16bit[2] != null) {
            //System.out.println("GREEN/BLUE mode");
            red_valid = false;
        }

        // check for a RED/BLUE image series with a blank green file entry
        if (TIFs_16bit.length == 3 && TIFs_16bit[0] != null && TIFs_16bit[1] == null && TIFs_16bit[2] != null) {
            //System.out.println("RED/BLUE mode");
            green_valid = false;
        }

        return ImageConversionManager.convertMultiple16BitTIFsToRGB(TIFs_16bit, this.imageConfig, red_valid, green_valid, blue_valid);
    }

    /**
     * Assumes that the desired time and plane are already set, unless we're in MAX PROJECTION mode, in which case
     * the contrast sliders were probably changed and we just want to return the current image
     * @return
     */
    public ImagePlus makeImage() {
        if (isCurrImageMIP) { return this.currentImage; }

        //if plane & time do not change, simply return currentImage
        if (!planeChange && !timeChange && !contrastChange) { return this.currentImage; }

        //System.out.println("MakeImage() called with: " + this.currentImageTime + ", " + this.currentImagePlane);

        this.currentImage = makeImage(this.currentImageTime, this.currentImagePlane);
        //this.isCurrImageMIP = false;

        //System.gc();

        //reset planeChange, timeChange, contrastChange
        planeChange = false;
        timeChange = false;
        contrastChange = false;
        return this.currentImage;
    }

    /**
     * This is the method called from AceTree during runtime when the UI is triggered to update the images
     * e.g. when the user chan\ges the time/plane
     *
     * @param time
     * @param plane
     * @return
     */
    public ImagePlus makeImage(int time, int plane) {
        // first check if we're dealing with 8 bit or 16 bit images
        if (this.imageConfig.getUseStack() == 0) { // 8bit
            this.currentImageName = ImageNameLogic.appendTimeAndPlaneTo8BittifPrefix(this.imageConfig.getImagePrefixes()[0], time, plane);
            //this.isCurrImageMIP = false;
            return makeImageFrom8Bittif();

        } else if (this.imageConfig.getUseStack() == 1) { //16bit
            // check if there are multiple stacks defining the color channels of the image series, or if all channels are contained in a single stack
            if (this.imageConfig.getNumChannels() == -1) {
                // single stack with one or more color channels
                this.currentImageName = ImageNameLogic.appendTimeToSingle16BitTIFPrefix(this.imageConfig.getImagePrefixes()[0], time);
                //this.isCurrImageMIP = false;
                return makeImageFromSingle16BitTIF();
            } else if (this.imageConfig.getNumChannels() > 1) {
                // multiple stacks containing multiple image channels for an image series
                String[] images = ImageNameLogic.appendTimeToMultiple16BitTifPrefixes(this.imageConfig.getImagePrefixes(), time);
                for (String s : images) {
                    if (!s.isEmpty()) {
                        this.currentImageName = s;
                        break;
                    }
                }
                //this.isCurrImageMIP = false;
                return makeImageFromMultiple16BitTIFs(images);
            }
        }

        System.out.println("Couldn't determine if using 8bit or 16bit images in ImageManager.makeImage(), returning null. useStack: " + this.imageConfig.getUseStack());
        return null;
    }

    /**
     * Build the image name given the current parameters to be used in setting the title in the ImageWindow
     *
     * @return
     */
    public String makeImageNameForTitle() {
        // first check if we're dealing with 8 bit or 16 bit images
        if (this.imageConfig.getUseStack() == 0) { // 8bit
            String fullPath = ImageNameLogic.appendTimeAndPlaneTo8BittifPrefix(this.imageConfig.getImagePrefixes()[0], this.currentImageTime, this.currentImagePlane);
            String directoryDelimiter = ImageNameLogic.getDirectoryDelimiter(fullPath);
            return fullPath.substring(fullPath.lastIndexOf(directoryDelimiter));

        } else if (this.imageConfig.getUseStack() == 1) { //16bit
            // check if there are multiple stacks defining the color channels of the image series, or if all channels are contained in a single stack
            if (this.imageConfig.getNumChannels() == -1) {
                // single stack with one or more color channels
                String fullPath = ImageNameLogic.appendTimeToSingle16BitTIFPrefix(this.imageConfig.getImagePrefixes()[0], this.currentImageTime);
                String directoryDelimiter = ImageNameLogic.getDirectoryDelimiter(fullPath);
                return fullPath.substring(fullPath.lastIndexOf(directoryDelimiter));
            } else if (this.imageConfig.getNumChannels() > 1) {
                // multiple stacks containing multiple image channels for an image series
                for (String s : this.imageConfig.getImagePrefixes()) {
                    if (!s.isEmpty()) {
                        String fullPath = ImageNameLogic.appendTimeToSingle16BitTIFPrefix(s, this.currentImageTime);
                        String directoryDelimiter = ImageNameLogic.getDirectoryDelimiter(fullPath);
                        return fullPath.substring(fullPath.lastIndexOf(directoryDelimiter));
                    }
                }
            }
        }
        return "";
    }


    ////////////////////////////////////////////////
    ////////////////////////////////////////////////
    /////// METHODS FOR EXTRACTING SPECIFIC ////////
    /////// COLOR CHANNELS BASED ON VIEWING ////////
    ///////////// PARAMETERS //////////////////////
    public ImagePlus extractColorChannelFromImagePlus(ImagePlus ip, int colorID) {
        if (ip == null) return null;

        switch(colorID) {
            case 1:
                ip = makeRedImagePlus(ip);
                break;
            case 2:
                ip = makeGreenImagePlus(ip);
                break;
            case 3:
                ip = makeBlueImagePlus(ip);
                break;
            case 4:
                ip = makeGreenAndRedImagePlus(ip);
                break;
            case 5:
                ip = makeGreenAndBlueImagePlus(ip);
                break;
            case 6:
                ip = makeRedAndBlueImagePlus(ip);
                break;
            case 7:
                ip = makeFullImagePlus(ip);
                break;
            default:
        }

        // set the contrast values
        if (this.imageConfig.getUseStack() == 0) {
            // red
            ip.setDisplayRange(contrastMin1, contrastMax1, 4);

            // green
            ip.setDisplayRange(contrastMin2, contrastMax2, 2);
        }

        return ip;
    }

    /**
     *
     * @param ip
     * @return
     */
    private ImagePlus makeRedImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.setRGB(ImageConversionManager.getCurrentRPixelMap(), new byte[ImageConversionManager.getCurrentGPixelMap().length], new byte[ImageConversionManager.getCurrentBPixelMap().length]);
        ip.setProcessor("test", iproc3);
        return ip;
    }

    /**
     *
     * @param ip
     * @return
     */
    private ImagePlus makeGreenImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        //System.out.println("makeGreenImagePlus: " + iproc);
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        //System.out.println("makeGreenImagePlus2: " + iproc + CS + iGpix  + CS + iRpix);
        iproc3.setRGB(new byte[ImageConversionManager.getCurrentRPixelMap().length], ImageConversionManager.getCurrentGPixelMap(), new byte[ImageConversionManager.getCurrentBPixelMap().length]);
        ip.setProcessor("test", iproc3);
        return ip;
    }

    /**
     *
     * @param ip
     * @return
     */
    private ImagePlus makeBlueImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        //System.out.println("makeGreenImagePlus: " + iproc);
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        //System.out.println("makeGreenImagePlus2: " + iproc + CS + iGpix  + CS + iRpix);
        iproc3.setRGB(new byte[ImageConversionManager.getCurrentRPixelMap().length], new byte[ImageConversionManager.getCurrentGPixelMap().length], ImageConversionManager.getCurrentBPixelMap());
        ip.setProcessor("test", iproc3);
        return ip;
    }

    /**
     *
     * @param ip
     * @return
     */
    private ImagePlus makeGreenAndRedImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        //System.out.println("makeGreenImagePlus: " + iproc);
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        //System.out.println("makeGreenImagePlus2: " + iproc + CS + iGpix  + CS + iRpix);
        iproc3.setRGB(ImageConversionManager.getCurrentRPixelMap(), ImageConversionManager.getCurrentGPixelMap(), new byte[ImageConversionManager.getCurrentBPixelMap().length]);
        ip.setProcessor("test", iproc3);
        return ip;
    }

    /**
     *
     * @param ip
     * @return
     */
    private ImagePlus makeGreenAndBlueImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        //System.out.println("makeGreenImagePlus: " + iproc);
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        //System.out.println("makeGreenImagePlus2: " + iproc + CS + iGpix  + CS + iRpix);
        iproc3.setRGB(new byte[ImageConversionManager.getCurrentRPixelMap().length], ImageConversionManager.getCurrentGPixelMap(), ImageConversionManager.getCurrentBPixelMap());
        ip.setProcessor("test", iproc3);
        return ip;
    }

    /**
     *
     * @param ip
     * @return
     */
    private ImagePlus makeRedAndBlueImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        //System.out.println("makeGreenImagePlus: " + iproc);
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        //System.out.println("makeGreenImagePlus2: " + iproc + CS + iGpix  + CS + iRpix);
        iproc3.setRGB(ImageConversionManager.getCurrentRPixelMap(), new byte[ImageConversionManager.getCurrentGPixelMap().length], ImageConversionManager.getCurrentBPixelMap());
        ip.setProcessor("test", iproc3);
        return ip;
    }

    /**
     * Not sure this method is necessary. Can just keep the image as is
     *
     * @param ip
     * @return
     */
    private ImagePlus makeFullImagePlus(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.setRGB(ImageConversionManager.getCurrentRPixelMap(), ImageConversionManager.getCurrentGPixelMap(), ImageConversionManager.getCurrentBPixelMap());
        ip.setProcessor("test", iproc3);
        return ip;
    }

    ///////////////////////////////////////////////////
    /////////// METHODS FOR OTHER ////////////////////
    ////////// IMAGE MANIPULATIONS //////////////////

    /**
     * Makes max projection(s)
     *
     * @return
     */
    public ImagePlus makeMaxProjection() {
        ZProjector zproj = new ZProjector();
        zproj.setMethod(ZProjector.MAX_METHOD);

        if (this.imageConfig.getUseStack() == 0) { // 8bit

            // we'll need to load all of the planes in the stack before making this max projection so hold off on this for now
            //implemented on 2/28/2020

            //combine the single images of current time point into one stack
            ImageStack is = new ImageStack(imageWidth, imageHeight);

            for (int i = 0; i < imageConfig.getPlaneEnd(); i++) {
                this.currentImageName = ImageNameLogic.appendTimeAndPlaneTo8BittifPrefix(this.imageConfig.getImagePrefixes()[0], this.currentImageTime, i + 1);
                //testing
                //System.out.println(this.currentImageName);
                ImagePlus currentIP = new Opener().openImage(this.currentImageName);

                if (currentIP != null) {
                    is.addSlice(Integer.toString(i + 1), currentIP.getProcessor());
                }
            }

            ImagePlus currentStack = new ImagePlus(Integer.toString(this.currentImageTime), is);

            //set the current stack and do the projection
            zproj.setImage(currentStack);
            zproj.doProjection();

            //convert the projection to 8bit RGB (shown in red by default)
            this.currentImage = ImageConversionManager.convertMIPToRGB(zproj.getProjection(), 1, this.imageConfig);

            this.isCurrImageMIP = true;
            return this.currentImage;
        } else if (this.imageConfig.getUseStack() == 1) { //16bit
            // check if there are multiple stacks defining the color channels of the image series, or if all channels are contained in a single stack
            if (this.imageConfig.getNumChannels() == -1 || this.imageConfig.getNumChannels() == 1) { // legacy .XML config
                // single stack with one or more color channels
                this.currentImageName = ImageNameLogic.appendTimeToSingle16BitTIFPrefix(this.imageConfig.getImagePrefixes()[0], this.currentImageTime);

                // set the image and do the projection
                zproj.setImage(new Opener().openImage(this.currentImageName));
                zproj.doProjection();

                // convert the projection to 8bit RGB (shown in red by default)
                this.currentImage = ImageConversionManager.convertMIPToRGB(zproj.getProjection(), 1, this.imageConfig);

                this.isCurrImageMIP = true;
                return this.currentImage;
            } else if (this.imageConfig.getNumChannels() > 1) {
                // multiple stacks containing multiple image channels for an image series
                String[] images = ImageNameLogic.appendTimeToMultiple16BitTifPrefixes(this.imageConfig.getImagePrefixes(), this.currentImageTime);

                // set the current image name as the first non-empty value
                for (String s : images) {
                    if (!s.isEmpty()) {
                        this.currentImageName = images[0];
                        break;
                    }
                }


                ImagePlus[] MIP_ips = new ImagePlus[images.length];
                for (int i = 0; i < images.length; i++) {
                    if (!images[i].isEmpty()) {
                        zproj.setImage(new Opener().openImage(images[i]));
                        zproj.doProjection();
                        MIP_ips[i] = zproj.getProjection();
                    }
                }

                int[] colorChannelIndices = new int[images.length];
                for (int i = 0; i < images.length; i++) {
                    if (!images[i].isEmpty()) {
                        colorChannelIndices[i] = i+1;
                    } else {
                        colorChannelIndices[i] = -1; // this will represent an image that is null, just for safe measure
                    }

                }

                // convert the MIPs into an 8bit RGB image
                this.currentImage = ImageConversionManager.convertMultipleMIPsToRGB(MIP_ips, colorChannelIndices, this.imageConfig);

                this.isCurrImageMIP = true;
                return this.currentImage;
            }
        }
        return null;
    }

    public boolean isCurrImageMIP() { return this.isCurrImageMIP; }
    public void setCurrImageMIP(boolean b) { this.isCurrImageMIP = b; }

    /**
     * This method produce the next valid color toggle index based on the image series that was loaded.
     * The list of all possible options and their corresponding indices is as follows:
     * 1. Red
     * 2. Green
     * 3. Blue
     * 4. Red/Green
     * 5. Green/Blue
     * 6. Red/Blue
     * 7. Red/Green/Blue
     *
     * For example, if a dataset with red and green images is loaded, and it's currently in red mode,
     * this method will return:
     * 2
     *
     *
     * @return
     */
    public int getNextValidColorToggleIndex(int currentToggle) {

        if (this.imageConfig.getNumChannels() == -1) {
            // legacy .XML defition case. Figure out the number of channels first
            if (currentToggle == 1) { return 2; } // if red, return green
            if (currentToggle == 2) { return 4; } // if green, return red/green
            if (currentToggle == 4) { return 1; } // if red/green, return red

            // startup call
            if (currentToggle == -1) { return 4; }
        } else if (this.imageConfig.getNumChannels() == 1) {
            return 1; // only valid color toggle index so it doesn't matter what was passed to this
        } else if (this.imageConfig.getNumChannels() == 2) { // need to check for empty image files which may have been supplied to control color
            // GREEN only case
            if (this.imageConfig.getImageChannels()[0].isEmpty() && !this.imageConfig.getImageChannels()[1].isEmpty()) {
                return 2;
            }

            // otherwise it's a RED/GREEN case
            if (currentToggle == 1) { return 2; }
            if (currentToggle == 2) { return 4; }
            if (currentToggle == 4) { return 1; }

            // startup call
            if (currentToggle == -1) { return 4; };
        } else if (this.imageConfig.getNumChannels() == 3) { // need to check for empty image files which may have been supplied to control color
            // BLUE only case
            if (this.imageConfig.getImageChannels()[0].isEmpty() && this.imageConfig.getImageChannels()[1].isEmpty() && !this.imageConfig.getImageChannels()[2].isEmpty()) {
                return 3; // only blue
            }

            // GREEN/BLUE case
            if (this.imageConfig.getImageChannels()[0].isEmpty() && !this.imageConfig.getImageChannels()[2].isEmpty() && !this.imageConfig.getImageChannels()[2].isEmpty()) {
                if (currentToggle == 2) { return 3; }
                if (currentToggle == 3) { return 5; }
                if (currentToggle == 5) { return 2; }

                // startup call
                if (currentToggle == -1) { return 5; }
            }

            // RED/BLUE case
            if (!this.imageConfig.getImageChannels()[0].isEmpty() && this.imageConfig.getImageChannels()[1].isEmpty() && !this.imageConfig.getImageChannels()[2].isEmpty()) {
                if (currentToggle == 1) { return 3; }
                if (currentToggle == 3) { return 6; }
                if (currentToggle == 6) { return 1; }

                // startup call
                if (currentToggle == -1) { return 6; }
            }

            // otherwise it's a RED/GREEN/BLUE
            // startup call (need to check first because of else condition in this case would return 0 (invalid) on startup
            if (currentToggle == -1) { return 7; }
            if (currentToggle == 7) { return 1; } // go back around
            else { return currentToggle+1; } // all options valid so increment by 1
        }


        return 1;
    }

    // accessors and mutators for static variables
    public static void setOriginContrastValuesFlag(boolean OCVF) { setOriginalContrastValues = OCVF; }
    public static void setContrastMin1(int cMin1) {
        contrastChange = true;
        contrastMin1 = cMin1;
    }
    public static void setContrastMin2(int cMin2) {
        contrastChange = true;
        contrastMin2 = cMin2;
    }
    public static void setContrastMin3(int cMin3) {
        contrastChange = true;
        contrastMin3 = cMin3;
    }
    public static void setContrastMax1(int cMax1) {
        contrastChange = true;
        contrastMax1 = cMax1;
    }
    public static void setContrastMax2(int cMax2) {
        contrastChange = true;
        contrastMax2 = cMax2;
    }
    public static void setContrastMax3(int cMax3) {
        contrastChange = true;
        contrastMax3 = cMax3;
    }
    public static boolean getOriginalContrastValuesFlag() { return setOriginalContrastValues; }
    public static int getContrastMin1() { return contrastMin1; }
    public static int getContrastMin2() { return contrastMin2; }
    public static int getContrastMin3() { return contrastMin3; }
    public static int getContrastMax1() { return contrastMax1; }
    public static int getContrastMax2() { return contrastMax2; }
    public static int getContrastMax3() { return contrastMax3; }

    /**
     * Determines the bit depth of the image specified by filename and returns an int ID which uses the convention
     * 8bit --> returns 8
     * 16bit --> returns 16
     *
     * Also maintains a hash of previously looked up images and saves their results so that files are only opened when necessary
     *
     * @param filename
     * @return 8 on 8bit image, 16 on 16bit image, -1 on failure
     */
    public static int getImageBitDepth(String filename) {
        if (filename == null || filename.isEmpty()) {
            return FAIL;
        }

        if (imagesPreviouslyBitDepthChecked == null) {
            imagesPreviouslyBitDepthChecked = new Hashtable<>();
        }

        // check if a file that shares this image's prefix was already looked up
        if (imagesPreviouslyBitDepthChecked.containsKey(ImageNameLogic.getImagePrefix(filename))) {
            //System.out.println("Found in hash");
            return imagesPreviouslyBitDepthChecked.get(ImageNameLogic.getImagePrefix(filename));
        }

        // open the file, interrogate its metadata, and return its bit depth
        ImagePlus ip = IJ.openImage(filename);
        if (ip != null) {
            // place the image prefix and bit depth in the hashtable
            imagesPreviouslyBitDepthChecked.put(ImageNameLogic.getImagePrefix(filename), ip.getBitDepth());
            return ip.getBitDepth();

        }
        return FAIL;
    }

    public ImageConfig getImageConfig() { return this.imageConfig; }

    private static Hashtable<String, Integer> imagesPreviouslyBitDepthChecked;
    public static int _8BIT_ID = 8;
    public static int _16BIT_ID = 16;
    public static int _32BIT_ID = 32;
    private static int FAIL = -1;

//    public static void main(String[] args) {
//        String test16bit = "/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_post2018/20141022_JIM113_UNC-86myrGFP/20141022_JIM113_UNC-86myrGFP_1_s1_t1.TIF";
//        System.out.println(getImageBitDepth(test16bit));
//
//        String test8bit = "/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_pre2018/20141022_JIM113_UNC-86myrGFP/image/tif/20141022_JIM113_UNC-86myrGFP_1_s1-t001-p01.tif";
//        System.out.println(getImageBitDepth(test8bit));
//
//        // now test if they're being stored properly, with the original images themselves and others in the series
//        System.out.println(getImageBitDepth(test16bit));
//        System.out.println(getImageBitDepth(test8bit));
//
//        String test8bit1 = "/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_pre2018/20141022_JIM113_UNC-86myrGFP/image/tif/20141022_JIM113_UNC-86myrGFP_1_s1-t001-p05.tif";
//        String test8bit2 = "/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_pre2018/20141022_JIM113_UNC-86myrGFP/image/tif/20141022_JIM113_UNC-86myrGFP_1_s1-t002-p18.tif";
//
//        System.out.println(getImageBitDepth(test8bit1));
//        System.out.println(getImageBitDepth(test8bit2));
//
//        String test16bit1 = "/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_post2018/20141022_JIM113_UNC-86myrGFP/20141022_JIM113_UNC-86myrGFP_1_s1_t10.TIF";
//        String test16bit2 = "/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_post2018/20141022_JIM113_UNC-86myrGFP/20141022_JIM113_UNC-86myrGFP_1_s2_t1.TIF";
//
//        System.out.println(getImageBitDepth(test16bit1));
//        System.out.println(getImageBitDepth(test16bit2));
//    }
}
