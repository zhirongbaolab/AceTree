package org.rhwlab.image.management;

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

                        // because the image series is now known to be 16bit stacks, set the use stack flag to 1
                        this.imageConfig.setUseStack(1);
                    } else {
                        System.out.println("16bit image file name generated from 8bit image file name does not exist on the system. Can't bring up image series.");
                    }
                } else {
                    System.out.println("Attempt to generate 16bit image file name from 8bit image file name failed. Can't bring up image series");
                }
            }
        }
    }
}
