package org.rhwlab.image.management;

import java.util.Hashtable;

/**
 * This class holds all of the configuration information for the image dataset
 *
 * It is created and maintained by ImageManager
 */
public class ImageConfig {

    private String tifPrefix;
    private String tifPrefixKey = "imageFileName";

    private int startingIndex;
    private String startingIndexKey = "startIdx";

    private int endingIndex;
    private String endingIndexKey = "endIdx";

    private int useStack;
    private String useStackKey = "useStack";

    public int splitStack;
    private String splitStackKey = "split";

    private int startTime;
    private int endTime;

    public ImageConfig(Hashtable<String, String> configData) {
        System.out.println("Configuring ImageConfig using .XML data");
        // prevent errors by initializing everything (default values for optional flags are set here)
        tifPrefix = "";
        startingIndex = endingIndex = startTime = endTime = -1;

        useStack = 0; // assume 8 bit images
        splitStack = 1; // assume that if we are working with 16bit images, they contain two channels that should be split

        if (configData == null) return;

        for (String s : configData.keySet()) {
            if (s.toLowerCase().equals(tifPrefixKey.toLowerCase())) {
                this.tifPrefix = configData.get(s);
            } else if (s.toLowerCase().equals(startingIndexKey.toLowerCase())) {
                this.startingIndex = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(endingIndexKey.toLowerCase())) {
                this.endingIndex = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(useStackKey.toLowerCase())) {
                this.useStack = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(splitStackKey.toLowerCase())) {
                this.splitStack = Integer.parseInt(configData.get(s));
            }
        }
    }

    // mutator methods
    public void setTifPrefix(String tifPrefix) { this.tifPrefix = tifPrefix; }
    public void setStartingIndex(String startingIndex) { setStartingIndex(Integer.parseInt(startingIndex)); }
    public void setStartingIndex(int startingIndex) { this.startingIndex = startingIndex; }
    public void setEndingIndex(String endingIndex) { setEndingIndex(Integer.parseInt(endingIndex)); }
    public void setEndingIndex(int endingIndex) { this.endingIndex = endingIndex; }
    public void setStartTime(String startTime) { setStartTime(Integer.parseInt(startTime)); }
    public void setStartTime(int startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { setEndTime(Integer.parseInt(endTime)); }
    public void setEndTime(int endTime) { this.endTime = endTime; }
    public void setUseStack(String useStack) { setUseStack(Integer.parseInt(useStack)); }
    public void setUseStack(int useStack) {this.useStack = useStack; }
    public void setSplitStack(String splitStack) {setSplitStack(Integer.parseInt(splitStack)); }
    public void setSplitStack(int splitStack) { this.splitStack = splitStack; }


    // accessor methods
    public String getTifPrefix() { return this.tifPrefix; }
    public int getStartingIndex() { return this.startingIndex; }
    public int getEndingIndex() { return this.endingIndex; }
    public int getStartTime() { return this.startTime; }
    public int getEndTime() { return this.endTime; }
    public int getUseStack() { return this.useStack; }
    public int getSplitStack() { return this.splitStack; }

    @Override
    public String toString() {
        String toString = NL
                + "Image Config:" + NL
                + tifPrefixKey + CS + tifPrefix + NL
                + startingIndexKey + CS + startingIndex + NL
                + endingIndexKey + CS + endingIndex + NL
                + useStackKey + CS + useStack + NL
                + splitStackKey + CS + splitStack + NL
                + "startTime" + CS + startTime + NL
                + "endTime" + CS + endTime + NL;

        return toString;
    }

    private static String CS = ", ";
    private static String NL = "\n";
}
