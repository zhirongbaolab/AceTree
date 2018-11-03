package org.rhwlab.image.management;

import org.rhwlab.image.ParsingLogic.ImageNameLogic;

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

    private int useStack;
    private String useStackKey = "useStack";

    private int splitStack;
    private String splitStackKey = "split";

    /**
     * This is the only runtime variable that the ImageConfig maintains. It contains the prefix(es) for the
     * image series. There are two scenarios for this array:
     * 1. If the legacy <image file="" /> tag is used, this.imagePrefixes will contain a single entry i.e. the prefix of this image file that all other files in the image series share
     * 2. If the new <image numChannels=n
     * */
    private String[] imagePrefixes;

    public ImageConfig(Hashtable<String, String> configData) {
        System.out.println("Configuring ImageConfig using .XML data");
        // prevent errors by initializing everything (default values for optional flags are set here)
        this.providedImageFileName = "";
        this.startingIndex = this.endingIndex = -1;

        this.useStack = 0; // assume 8 bit tif images
        this.splitStack = 1; // assume that if we are working with 16bit images, they contain two channels that should be split

        if (configData == null) return;

        for (String s : configData.keySet()) {
            if (s.toLowerCase().equals(this.imageFileNameKey.toLowerCase())) {
                this.providedImageFileName = configData.get(s);

                // this will indicate that the legacy image tag was given
                this.numChannels = -1;
            } else if (s.toLowerCase().equals(this.numChannelsKey.toLowerCase())) {
                this.numChannels = Integer.parseInt(configData.get(s));

                // initialize the channels array to hold this number of file IDs
                this.imageChannels = new String[this.numChannels];
            } else if (s.toLowerCase().startsWith(this.imageChannelKeyPrefix.toLowerCase())) {
                // extract the channel number from s (the last character)
                int channelNumber = Character.getNumericValue(s.charAt(s.length()-OFFSET));

                // add this file at that location in the array specified by the channel number
                if (this.imageChannels == null) { System.out.println("<image> tag was not correctly defined in XML. Please see documentation for support"); continue; }
                this.imageChannels[channelNumber-1] = configData.get(s);
            } else if (s.toLowerCase().equals(this.startingIndexKey.toLowerCase())) {
                this.startingIndex = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(this.endingIndexKey.toLowerCase())) {
                this.endingIndex = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(this.useStackKey.toLowerCase())) {
                this.useStack = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(this.splitStackKey.toLowerCase())) {
                this.splitStack = Integer.parseInt(configData.get(s));
            }
        }

        if (this.numChannels == -1) {
            System.out.println("Setting single image prefix for supplied image.");
        } else {
            System.out.println("Setting " + this.numChannels + " image prefixes for supplied images.");
        }
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
           // TODO - on second thought, it's not clear yet when this situation would arise (either it's explicitly defined or not) - leaving it here in case we find use in the future

           // the XML file used the new <image numChannels=n ... /> convention, but didn't explicitly list all of the color
           // channel images. AceTree found one, so we increment the numChannels

           // NOTE: AceTree will not properly handle a numChannels number provided in the XML that doesn't match the number
           // of files actually supplied in the tag. E.g. numChannels=2 but only channel1="" is present. The XML file should
           // either use the new convention and list them all, or use the legacy convention and allow AceTree to try and locate
           // any other channels

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
    private void setImagePrefixes() {
        if (numChannels == -1) {
            // there is only one image prefix to set because the legacy tag <image file="" /> was used
            this.imagePrefixes = new String[1];
            this.imagePrefixes[0] = ImageNameLogic.getImagePrefix(this.providedImageFileName);
        } else {
            this.imagePrefixes = new String[numChannels];
            for (int i = 0; i < numChannels; i++) {
                this.imagePrefixes[i] = ImageNameLogic.getImagePrefix(imageChannels[i]);
            }
        }
    }

    // mutator methods
    public void setProvidedImageFileName(String providedImageFileName) { this.providedImageFileName = providedImageFileName; setImagePrefixes(); }
    public void setStartingIndex(String startingIndex) { setStartingIndex(Integer.parseInt(startingIndex)); }
    public void setStartingIndex(int startingIndex) { this.startingIndex = startingIndex; }
    public void setEndingIndex(String endingIndex) { setEndingIndex(Integer.parseInt(endingIndex)); }
    public void setEndingIndex(int endingIndex) { this.endingIndex = endingIndex; }
    public void setUseStack(String useStack) { setUseStack(Integer.parseInt(useStack)); }
    public void setUseStack(int useStack) {this.useStack = useStack; }
    public void setSplitStack(String splitStack) {setSplitStack(Integer.parseInt(splitStack)); }
    public void setSplitStack(int splitStack) { this.splitStack = splitStack; }


    // accessor methods
    public String getProvidedImageFileName() { return this.providedImageFileName; }
    public int getStartingIndex() { return this.startingIndex; }
    public int getEndingIndex() { return this.endingIndex; }
    public int getUseStack() { return this.useStack; }
    public int getSplitStack() { return this.splitStack; }
    public String[] getImageChannels() { return this.imageChannels; }

    /**
     * This indicates whether or not the user supplied multiple image paths in the <image></image> tag
     * relating to the mutliple channels for the image series
     * @return
     */
    public boolean areMultipleImageChannelsGiven() {
        if (numChannels != -1) {
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