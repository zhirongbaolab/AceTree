package org.rhwlab.image.management;

import org.rhwlab.image.ParsingLogic.ImageNameLogic;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class holds all of the configuration information for the image dataset
 *
 * It is created and maintained by ImageManager
 */
public class ImageConfig {

    private String providedImageFileName;
    private String imageFileNameKey = "imageFileName";

    private int numChannels;
    private String numChannelsKey = "numChannels";

    private String[] imageChannels;
    private String imageChannelKeyPrefix = "channel";

    private int startingIndex;
    private String startingIndexKey = "startIdx";

    private int endingIndex;
    private String endingIndexKey = "endIdx";

    private int planeEnd;
    private String planeEndKey = "planeEnd";

    private int useStack;
    private String useStackKey = "useStack";

    private int splitStack;
    private String splitStackKey = "split";

    private int flipStack;
    private String flipStackKey = "flip";

    private double xyRes;
    private String xyResKey = "xyRes";

    private double zRes;
    private String zResKey = "zRes";

    private boolean splitStackGiven;
    private boolean flipStackGiven;

    /**
     * This is the only runtime variable that the ImageConfig maintains. It contains the prefix(es) for the
     * image series. There are two scenarios for this array:
     * 1. If the legacy <image file="" /> tag is used, this.imagePrefixes will contain a single entry i.e. the prefix of this image file that all other files in the image series share
     * 2. If the new <image numChannels=n
     * */
    private String[] imagePrefixes;

    /**
     * Build an image config given parsed XML attributes
     *
     * @param configData
     * @param configFileName - if a relative image path was supplied, the configFile path is prepended
     */
    public ImageConfig(Hashtable<String, String> configData, String configFileName) {
        System.out.println("Configuring ImageConfig using .XML data");
        // prevent errors by initializing everything (default values for optional flags are set here)
        this.providedImageFileName = "";
        this.startingIndex = this.endingIndex = -1;

        this.useStack = 1; // assume 16 bit tif images (really, the distinction is between slice and stack images, but it has been convention
                            // to use 8 and 16bit, so we maintain that here

        // the following two assumptions are derived from the confocal microscope set up. These can be explicitly set in the XML file
        this.splitStack = 1; // assume that if we are working with 16bit images, they contain two channels that should be split
        this.flipStack = 1; // assume that if we are working with 16bit images, they contain two channels that should be split

        // we'll also maintain two flags that will indicate whether the split and flip stacks were given explicitly to avoid overriding them later
        this.splitStackGiven = false;
        this.flipStackGiven = false;


        if (configData == null) return;

        // we need to check ahead of time if this xml uses the 2nd image tag definition option, where the number of channels are supplied.
        // this is necessary because even if that subtag is defined before the channels in the .xml file, the numChannels key may not
        // come before the channel keys do, and then there won't be an array to place the channels in
        if (configData.keySet().contains(numChannelsKey)) {
            this.numChannels = Integer.parseInt(configData.get(numChannelsKey));

            this.imageChannels = new String[this.numChannels];
        }

        for (String s : configData.keySet()) {
            if (s.toLowerCase().equals(this.imageFileNameKey.toLowerCase())) {
                String imageFile = configData.get(s);
                if (imageFile != null && (ImageNameLogic.isPathRelative(imageFile) || ImageNameLogic.isPathImplicitRelative(imageFile))) {
                    // we need to make sure that we're working with the same directory delimiter in the config file and the image file, because we're going to build
                    // the absolute path to the image from the config file
                    String directoryDelimiter1 = ImageNameLogic.getDirectoryDelimiter(configFileName);
                    if (directoryDelimiter1.isEmpty()) {
                        System.out.println("Couldn't extract directory delimiter from config file to build absolute path for supplied relative image file");
                        return;
                    }


                    // if the image file is implicitly relative, prepend "./" or ".\" to it
                    if (ImageNameLogic.isPathImplicitRelative(imageFile)) {
                        imageFile = "." + directoryDelimiter1 + imageFile;
                        System.out.println("Image file is implicitly relative, updating to: " + imageFile);
                    }

                    String directoryDelimiter = ImageNameLogic.getDirectoryDelimiter(imageFile);
                    if (directoryDelimiter.isEmpty()) {
                        System.out.println("Couldn't update relative image path to absolute because the file separator couldn't be determined. Make " +
                                "sure they are consistent.");
                        return;
                    }

                    // are the delimiters the same?
                    if (!directoryDelimiter1.equals(directoryDelimiter)) {
                        imageFile = imageFile.replace(directoryDelimiter, directoryDelimiter1);
                    }

                    if (ImageNameLogic.isRelativePathDownstream(imageFile)) {
                        // if the image file is relative either in the same directory or downstream, just prepend it with the absolute path in the configFileName
                        imageFile = configFileName.substring(0, configFileName.lastIndexOf(directoryDelimiter1) + 1) + imageFile.substring(imageFile.indexOf(directoryDelimiter1)+1);
                        System.out.println("Updating relative image file path to absolute: " + imageFile);
                    } else if (ImageNameLogic.isRelativePathUpstream(imageFile)) {
                        // if the image file is relative upstream, we'll need to walk back along the config file path according to the relative path specified by the image file
                        int numUpstreamDirectoriesSpecified = ImageNameLogic.getNumberOfUpstreamDirectoriesSpecifiedInRelativePath(imageFile);
                        // make sure that there are more directories in the config file path

                        int numDirectoriesInAbsPath = ImageNameLogic.getNumberOfDirectoriesInAbsolutePath(configFileName);
                        if (numUpstreamDirectoriesSpecified < numDirectoriesInAbsPath) {
                            imageFile = ImageNameLogic.getFirstNDirectoriesInAbsolutePath(configFileName, numDirectoriesInAbsPath - numUpstreamDirectoriesSpecified) + ImageNameLogic.getImagePathAfterUpstreamDirectoryCharacters(imageFile);
                            System.out.println("Updating relative image file path to absolute: " + imageFile);
                        } else {
                            System.out.println("The number of upstream directories specified in the relative image path is greater than the total number of nested directories in the absolute path of the config file");
                        }
                    }
                }
                this.providedImageFileName = imageFile;

                // this will indicate that the legacy image tag was given
                this.numChannels = -1;
            } else if (s.toLowerCase().startsWith(this.imageChannelKeyPrefix.toLowerCase())) {
                // extract the channel number from s (the last character)
                int channelNumber = Character.getNumericValue(s.charAt(s.length()-OFFSET));

                // add this file at that location in the array specified by the channel number
                if (this.imageChannels == null) { System.out.println("<image> tag was not correctly defined in XML. Please see documentation for support"); continue; }

                String imageFile = configData.get(s);

                // check if there is a non-empty string listed for this channel (and check if it's a relative path)
                if (imageFile != null && ImageNameLogic.isPathRelative(imageFile)) {
                    String directoryDelimiter = ImageNameLogic.getDirectoryDelimiter(imageFile);
                    if (directoryDelimiter.isEmpty()) {
                        System.out.println("Couldn't update relative image path to absolute because the file separator couldn't be determined. Make " +
                                "sure they are consistent.");
                        return;
                    }
                    // if the image file name is not absolute, prepend it with the absolute path in the configFileName
                    imageFile = configFileName.substring(0, configFileName.lastIndexOf(directoryDelimiter)+1) + imageFile.substring(imageFile.lastIndexOf(directoryDelimiter)+1);
                    System.out.println("Updating relative image file path to absolute: " + imageFile);

                }
                this.imageChannels[channelNumber-1] = imageFile;
            } else if (s.toLowerCase().equals(this.flipStackKey.toLowerCase())) {
                this.flipStack = Integer.parseInt(configData.get(s));
                this.flipStackGiven = true;
            } else if (s.toLowerCase().equals(this.startingIndexKey.toLowerCase())) {
                this.startingIndex = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(this.endingIndexKey.toLowerCase())) {
                this.endingIndex = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(this.planeEndKey.toLowerCase())) {
                this.planeEnd = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(this.useStackKey.toLowerCase())) {
                this.useStack = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(this.splitStackKey.toLowerCase())) {
                this.splitStack = Integer.parseInt(configData.get(s));
                this.splitStackGiven = true;
            } else if (s.toLowerCase().equals(xyResKey.toLowerCase())) {
            this.xyRes = Double.parseDouble(configData.get(s));
            } else if (s.toLowerCase().equals(zResKey.toLowerCase())) {
            this.zRes = Double.parseDouble(configData.get(s));
            }
        }

        if (this.numChannels == -1) {
            System.out.println("Setting single image prefix for supplied image.");
        } else {
            System.out.println("Setting " + this.numChannels + " image prefixes for supplied images.");
        }

        // set starting index if it wasn't explicitly given or it's unreasonable
//        if (this.startingIndex < 0) {
//            setStartingIndex(ImageNameLogic.extractTimeFromImageFileName(providedImageFileName));
//        }

        setImagePrefixes();
    }

    // the few processing methods for ImageConfig (all are related to AceTree's ability to located parts of the image series
    // don't need to be explicitly given by the user). See ImageNameLogic.java for more information.

    /**
     *
     * @param newColorChannelImage
     */
    public void addColorChannelImageToConfig(String newColorChannelImage) {
       if (newColorChannelImage == null || newColorChannelImage.isEmpty()) { return; }

       int updateIdx = -1; // this will let updateImagePrefixes() known the index of the newly added image that it needs to process for prefix
       if (numChannels == -1) {
           // in this situation, the XML file used the legacy <image file="" /> convention, but AceTree was able to locate
           // another image file with another color channel for the image series. We'll switch the convention here by
           // updating these vars
           numChannels = 2;

           this.imageChannels = new String[this.numChannels];
           this.imageChannels[0] = this.providedImageFileName;
           this.imageChannels[1] = newColorChannelImage;
           updateIdx = 1;
           updateImagePrefixes(updateIdx);
       } else {
           // we'll assume this is the third channel that's being added because 3 channel image is all that is supported.
           // If this is more than the third, it will replace whatever the current third channel is
           numChannels = 3;

           String[] imageChannelsLocal = this.imageChannels;

           this.imageChannels = new String[this.numChannels];
           this.imageChannels[0] = imageChannelsLocal[0];
           this.imageChannels[1] = imageChannelsLocal[1];
           this.imageChannels[2] = newColorChannelImage;
           updateIdx = 2;
           updateImagePrefixes(updateIdx);
       }
    }


    /**
     * This method is used to update the list of image prefixes if AceTree is able to find other
     * images for the image series that correspond to different color channels. This method is
     * called after the new image is added to the list
     * @param updateIdx
     */
    private void updateImagePrefixes(int updateIdx) {
        if (this.imagePrefixes.length <= updateIdx) {
            // resize the array
            String[] prefixesLocal = this.imagePrefixes;
            this.imagePrefixes = new String[numChannels]; // numChannels was updated in the calling method

            // bring the previously set prefixes over to the new array
            int i = 0;
            for (; i < prefixesLocal.length; i++) {
                this.imagePrefixes[i] = prefixesLocal[i];
            }

            // add the newest one
            this.imagePrefixes[i] = ImageNameLogic.getImagePrefix(this.imageChannels[i]);
        }
    }

    /**
     * This method processes the given image file name(s) and sets the prefix(es) so that other files in the series can be loaded
     */
    public void setImagePrefixes() {
        if (numChannels == -1) {
            // there is only one image prefix to set because the legacy tag <image file="" /> was used
            this.imagePrefixes = new String[1];
            this.imagePrefixes[0] = ImageNameLogic.getImagePrefix(this.providedImageFileName);
        } else {
            this.imagePrefixes = new String[numChannels];
            for (int i = 0; i < numChannels; i++) {
                this.imagePrefixes[i] = ImageNameLogic.getImagePrefix(imageChannels[i]);
            }

            // also set the first file to the providedImageFileName variable so that it can be used to create a title for the ImageWindow
            for (String s : imageChannels) {
                if (!s.isEmpty()) {
                    setProvidedImageFileName(imageChannels[0]);
                    return;
                }
            }
            System.out.println("No non-empty image file provided");
            return;
        }
    }

    // mutator methods
    public void setProvidedImageFileName(String providedImageFileName) { this.providedImageFileName = providedImageFileName; }
    public void setStartingIndex(String startingIndex) { setStartingIndex(Integer.parseInt(startingIndex)); }
    public void setStartingIndex(int startingIndex) { this.startingIndex = startingIndex; }
    public void setEndingIndex(String endingIndex) { setEndingIndex(Integer.parseInt(endingIndex)); }
    public void setEndingIndex(int endingIndex) { this.endingIndex = endingIndex; }
    public void setUseStack(String useStack) { setUseStack(Integer.parseInt(useStack)); }
    public void setUseStack(int useStack) {this.useStack = useStack; }
    public void setSplitStack(String splitStack) {setSplitStack(Integer.parseInt(splitStack)); }
    public void setSplitStack(int splitStack) { this.splitStack = splitStack; }
    public void setFlipStack(String flipStack) { setFlipStack(Integer.parseInt(flipStack)); }
    public void setFlipStack(int flipStack) { this.flipStack = flipStack; }
    public void setPlaneEnd(String planeEnd) { setPlaneEnd(Integer.parseInt(planeEnd)); }
    public void setPlaneEnd(int planeEnd) { this.planeEnd = planeEnd; }
    public void setXyRes(String xyRes) { setXyRes(Double.parseDouble(xyRes)); }
    public void setXyRes(double xyRes) { this.xyRes = xyRes; }
    public void setZRes(String zRes) { setZRes(Double.parseDouble(zRes)); }
    public void setZRes(double zRes) { this.zRes = zRes; }


    // accessor methods
    public String getProvidedImageFileName() { return this.providedImageFileName; }
    public int getStartingIndex() { return this.startingIndex; }
    public int getEndingIndex() { return this.endingIndex; }
    public int getUseStack() { return this.useStack; }
    public int getSplitStack() { return this.splitStack; }
    public int getFlipStack() { return this.flipStack; }
    public int getPlaneEnd() { return this.planeEnd; }
    public String[] getImageChannels() { return this.imageChannels; }
    public String[] getImagePrefixes() { return this.imagePrefixes; }
    public boolean isSplitStackGiven() { return this.splitStackGiven; }
    public boolean isFlipStackGiven() { return this.flipStackGiven; }
    public double getXyRes() { return this.xyRes; }
    public double getZRes() { return this.zRes; }

    /**
     * This indicates whether or not the user supplied multiple image paths in the <image></image> tag
     * relating to the mutliple channels for the image series
     * @return
     */
    public boolean areMultipleImageChannelsGiven() {
        if (numChannels != -1 || numChannels > 1) {
            return true;
        }
        return false;
    }

    /**
     * This method is listed here as opposed to the accessor methods block to indicate that it should be used in conjunction
     * with areMultipleImageChannelsGiven(). That method should be called first to indicate whether or not there are channels
     * to be queried in this image series before accessing the number of channels variable
     * @return
     */
    public int getNumChannels() {
        return this.numChannels;
    }

    public String getImageChannelFileName(int channelNum) {
        if (channelNum > 0 && channelNum <= this.numChannels) {
            return this.imageChannels[channelNum - OFFSET];
        }
        System.out.println("Can't access image file for channel: " + channelNum);
        return "";
    }

    @Override
    public String toString() {
        String toString = NL
                + "Image Config:" + NL
                + imageFileNameKey + CS + providedImageFileName + NL
                + startingIndexKey + CS + startingIndex + NL
                + endingIndexKey + CS + endingIndex + NL
                + useStackKey + CS + useStack + NL
                + splitStackKey + CS + splitStack + NL;

        return toString;
    }

    private static String CS = ", ";
    private static String NL = "\n";
    private static int OFFSET = 1;
}
