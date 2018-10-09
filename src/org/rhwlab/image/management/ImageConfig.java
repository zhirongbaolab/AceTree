package org.rhwlab.image.management;

/**
 * This class holds all of the configuration information for the image dataset
 *
 * It is created and maintained by ImageManager
 */
public class ImageConfig {
    private String tifPrefix;
    private int startingIndex;
    private int endingIndex;
    private int planeStart;
    private int planeEnd;
    private int startTime;
    private int endTime;


    private int useStack;
    public int splitStack;

    public ImageConfig() {
        // avoid null ptr exceptions by setting to functionally 0 values
        this.tifPrefix = "";
        this.startingIndex = this.endingIndex = this.planeStart = this.planeEnd = this.startTime = this.endTime = 0;

        // set use stack and split stack defaults
        this.useStack = 0; // default value indicates that 8bit images are excepted
        this.splitStack = 1; // default value indicates that 16bit images (when present) are expected to contain two channels and should be split
    }

    // mutator methods
    public void setTifPrefix(String tifPrefix) { this.tifPrefix = tifPrefix; }
    public void setStartingIndex(int startingIndex) { this.startingIndex = startingIndex; }
    public void setEndingIndex(int endingIndex) { this.endingIndex = endingIndex; }
    public void setPlaneStart(int planeStart) { this.planeStart = planeStart; }
    public void setPlaneEnd(int planeEnd) { this.planeEnd = planeEnd; }
    public void setStartTime(int startTime) { this.startTime = startTime; }
    public void setEndTime(int endTime) { this.endTime = endTime; }
    public void setUseStack(int useStack) {this.useStack = useStack; }
    public void setSplitStack(int splitStack) { this.splitStack = splitStack; }


    // accessor methods
    public String getTifPrefix() { return this.tifPrefix; }
    public int getStartingIndex() { return this.startingIndex; }
    public int getEndingIndex() { return this.endingIndex; }
    public int getPlaneStart() { return this.planeStart; }
    public int getPlaneEnd() { return this.planeEnd; }
    public int getStartTime() { return this.startTime; }
    public int getEndTime() { return this.endTime; }
    public int getUseStack() { return this.useStack; }
    public int getSplitStack() { return this.splitStack; }
}
