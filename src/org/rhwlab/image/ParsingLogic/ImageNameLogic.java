package org.rhwlab.image.ParsingLogic;

import java.io.File;

/**
 * Class of static methods used to look for images and manipulate image names based on known conventions
 * for structuring image series data, and native scope output conventions
 *
 * Public Methods:
 * 1. reconfigureImagePathFrom8bitTo16bit(String _8bitImagePath)
 * 2. findSecondiSIMColorChannel(String iSIM_image_filename)
 * 3. findSecondDiSPIMColorChannel(String diSPIM_image_filename)
 */
public class ImageNameLogic {

    // static variables for dealing with differing system naming conventions
    public static String FORWARDSLASH = "/";
    public static String BACKSLASH = "\\";
    private static char UNDERSCORE = '_';
    private static char DASH = '-';
    private static String PERIOD = ".";
    private static String upstreamDirectoryID_1 = "../";
    private static String upstreamDirectoryID_2 = "..\\";
    private static String downstreamDirectoryID_1 = "./";
    private static String downstreamDirectoryID_2 = ".\\";

    // static variables for iSIM data directory parsing
    private static String endOfChannelTextIdentifier = "_s";

    // static variables for diSPIM data directory parsing
    private static String COLOR = "Color";
    private static String SPIM = "SPIM";
    private static String diSPIM_DIRECTORY_END_SUBSTR = " nm";
    private static int diSPIM_DIRECTORY_NAME_LENGTH = 6;
    private static int IDX_0 = 0;
    private static int IDX_1 = 1;
    private static int IDX_2 = 2;

    private static String t = "t";
    private static char tChar = 't';
    private static String tID_8bitConvention = "-t";
    private static String tID_16bitConvention = "_t";
    private static String TIF_ext = ".TIF";
    private static String tif_ext = ".tif";
    private static String planeStr = "-p";
    private static String planeStrAlt = "_p";
    private static String ZERO_PAD = "0";

    public static String tifDir = "/tif/";
    public static String tifRDir = "/tifR/";
    public static String tifDir_2 = "\\tif\\";
    public static String tifRDir_2 = "\\tifR\\";

    private static String sliceDeleteConventionTIFStr_1 = "/image/tif/";
    private static String sliceDeleteConventionTIFStr_2 = "\\image\\tif\\";
    private static String sliceDeleteConventionTIFRStr_1 = "/image/tifR/";
    private static String sliceDeleteConventionTIFRStr_2 = "\\image\\tifR\\";

    /**
     * @author Braden Katzman
     *
     * Method which given a slice image that follows the ..../tif/image_name.tif convention, returns a second channel,
     * ..../tifR/image_name.tif, if it exists.
     *
     * @param slice_image
     * @return the complementary color channel, if it exists. Otherwise, returns the slice_image parameter
     */
    public static String findSecondColorChannelFromSliceImage(String slice_image) {
        if (slice_image == null || slice_image.isEmpty()) { return slice_image; }

        if (slice_image.contains(tifDir)) {
            String secondChannelAttempt = slice_image.substring(0, slice_image.indexOf(tifDir)) +
                    tifRDir +
                    slice_image.substring(slice_image.indexOf(tifDir) + tifDir.length());

            if (new File(secondChannelAttempt).exists()) {
                //System.out.println(secondChannelAttempt);
                return secondChannelAttempt;
            }
        } else if (slice_image.contains(tifRDir)) {
            String secondChannelAttempt = slice_image.substring(0, slice_image.indexOf(tifRDir)) +
                    tifDir +
                    slice_image.substring(slice_image.indexOf(tifRDir) + tifRDir.length());

            if (new File(secondChannelAttempt).exists()) {
                ///System.out.println(secondChannelAttempt);
                return secondChannelAttempt;
            }
        } else if (slice_image.contains(tifDir_2)) {
            String secondChannelAttempt = slice_image.substring(0, slice_image.indexOf(tifDir_2)) +
                    tifRDir_2 +
                    slice_image.substring(slice_image.indexOf(tifDir_2) + tifDir_2.length());

            if (new File(secondChannelAttempt).exists()) {
                //System.out.println(secondChannelAttempt);
                return secondChannelAttempt;
            }
        } else if (slice_image.contains(tifRDir_2)) {
            String secondChannelAttempt = slice_image.substring(0, slice_image.indexOf(tifRDir_2)) +
                    tifDir_2 +
                    slice_image.substring(slice_image.indexOf(tifRDir_2) + tifRDir_2.length());

            if (new File(secondChannelAttempt).exists()) {
                //System.out.println(secondChannelAttempt);
                return secondChannelAttempt;
            }
        }

        return slice_image;
    }

    /**
     * @author Braden Katzman
     *
     * Part of the configuration and image loading revisions from 10/2018
     *
     * Update the image path from the 8bit one listed in the .xml file
     * to a 16bit one. This is used in the case of 8bit images not existing,
     * or explicit need to use 16bit images in the presence of both types
     *
     * The directory structure that this expects when doing this conversion looks like this
     * ...someDir/
     *      image/
     *          tif/
     *              8bit_image-t#-p#.tif
     *      16bit_image_t#.TIF
     *
     * That is, the expected convention is that the 8bit images generated by StarryNite are stored 2 subdirectories deeper in the
     * dataset directory from the 16bit images. Specifically, those directories are image/ and tif/. This method will only find
     * 16bit images if the 8bit images are stored in directories named exactly as such, and the 16bit .TIF shares the same file
     * prefix as the 8bit, and sits next to the image directory/
     *
     * Note that if 8bit files no longer exist in the image/tif/ directory, an xml file with a properly configured 8bit image name
     * will work because the method will pull the path preceding the name, and then extract the prefix of the name from the time and
     * plane identifiers to find the 16bit corollary.
     *
     * If your situation differs from this convention, convert your directory structure to match this, or alternatively, modify this
     * method and/or add a method alongside it that meets your demands.
     * @return the updated file path or the original path if a failure occurs
     * (Note, the calling class should check if the update was successful, and if so, set the useStack flag to indicate that 16bit images are present)
     */
    public static String reconfigureImagePathFrom8bitTo16bit(String _8bitImagePath) {
        System.out.println("\nReconfiguring image path for an 8bit image to a 16bit TIF, if possible. ImageNameLogic.reconfigureImagePathFrom8BithTo16Bit()");

        // try using layered images two directories up
        String directoryDelimiter = getDirectoryDelimiter(_8bitImagePath);
        int fileNameIdx = _8bitImagePath.lastIndexOf(directoryDelimiter);

        // find the name of the file i.e. cut off everything having to do with either an absolute or relative path in the string passed
        String fileName;
        if (fileNameIdx > 0) {
            fileName = _8bitImagePath.substring(fileNameIdx+1);
        } else {
            System.out.println("Couldn't access filename (i.e. substring after last '/' or '\\') when trying to convert 8bit image name to 16bit. Returning 8bit image filename.");
            return _8bitImagePath;

//            fileNameIdx = _8bitImagePath.lastIndexOf(BACKSLASH);
//            if (fileNameIdx > 0) {
//                fileName = _8bitImagePath.substring(fileNameIdx+1);
//            } else {
//
//            }
        }

        // if this is an 8bit image, it is a slice and should have an identifier for the plane number. Let's look for it
        int planeIdx = fileName.indexOf(planeStr);

        String fileNameNoPlane;
        if (planeIdx > 0) {
            fileNameNoPlane = fileName.substring(0, planeIdx - 1);
        } else {
            System.out.println("Couldn't locate a plane ID in the filename (i.e. 'p#'). This is needed to extract the shared prefix " +
                    "between 8bit and 16bit images in the convention expected. Returning 8bit image filename");
            return _8bitImagePath;
        }

        int extIdx = fileName.lastIndexOf('.');
        String ext;
        if (extIdx > 0) {
            ext = fileName.substring(extIdx).toUpperCase();
        } else {
            System.out.println("No extension in filename. Returning 8bit image filename");
            return _8bitImagePath;
        }


        int lastDashIdx = fileNameNoPlane.lastIndexOf(DASH);
        String filePrefix;
        if (lastDashIdx > 0) {
           filePrefix = fileNameNoPlane.substring(0, lastDashIdx) + UNDERSCORE;
        } else {
            System.out.println("No dash in file name separating shared prefix with 16bit images with 8bit specific naming conventions. Returning 8bit image filename");
            return _8bitImagePath;
        }


        int tIdx = fileName.lastIndexOf(tID_8bitConvention);
        if (tIdx > 0) {
            // move ahead to the first digit of the time string
            tIdx+=2;
            if (Character.isDigit(fileName.charAt(tIdx)) && Character.getNumericValue(fileName.charAt(tIdx)) == 0) {
                while(fileName.charAt(tIdx) == '0') {
                    tIdx++;
                }
            }

        } else {
            System.out.println("No time ID '-t' in filename which is necessary to extract the filename prefix. Returning 8bit image filename");
            return _8bitImagePath;
        }

        String t_ = t + fileName.substring(tIdx, planeIdx);

        String fileNameUpdate = filePrefix + t_ + ext;

        int removeDirsIdx = _8bitImagePath.indexOf("image" + directoryDelimiter + "tif");// this will handle the tif and tifR cases
        if (removeDirsIdx > 0) {
            String filePre = _8bitImagePath.substring(0, removeDirsIdx);

            String finalPath = filePre + fileNameUpdate;
            return finalPath;
        } else {
            System.out.println("8bit image is not contained in .../image/tif/ or ...\\image\\tif\\ subdirectory so 16bit image couldn't be located. Returning 8bit image filename");
            return _8bitImagePath;
        }
    }

    /**
     * Method to locate the second color channel of an iSIM dataset given the location of one color. This
     * method expects the image files follow the native iSIM output format, which lists all image stacks
     * in a single directory, with their filenames distinguishing between the two color channels. In short,
     * this method parses the tokens of the given filename, identifies it's color, and then looks for a
     * corresponding image stack with a different color channel token.
     *
     * Example:
     * iSIM_image_directory/
     *      researcherInitials_datasetIdentifier_w1iSIM - FITC - ###-##_s#_t#.TIF
     *      researcherInitials_datasetIdentifier_w2iSIM - TxRed - ###-##_s#_t#.TIF
     *
     *
     * @param iSIM_image_filename
     * @return
     */
    public static String findSecondiSIMColorChannel(String iSIM_image_filename) {
        if (iSIM_image_filename == null || iSIM_image_filename.isEmpty() || !new File(iSIM_image_filename).exists()) {
            System.out.println("Can't locate second color channel in iSIM dataset. Invalid image file given.");
            return "";
        }

        System.out.println("\nLooking for second iSIM color channel given: " + iSIM_image_filename);

        // extract the prefix before the w# identifier
        int _wIdx = iSIM_image_filename.lastIndexOf("_w");
        if (_wIdx == -1) {
            System.out.println("Couldn't extract channel identifier from iSIM image file name");
            return "";
        }

        String filename_before_channel_text = iSIM_image_filename.substring(0, _wIdx+2);
        char channelNumberIDChar = iSIM_image_filename.charAt(_wIdx+2);

        // make sure the channel number ID is a digit
        if (!Character.isDigit(channelNumberIDChar)) {
            System.out.println("Couldn't extract channel identifier number from iSIM image file name");
            return "";
        }

        // we'll use these vars to distinguish between this channel and others in the directory
        int channelNumberID = Character.getNumericValue(channelNumberIDChar);
        int channelNumberID_idx = _wIdx + 2;

        String filename_after_channel_text = iSIM_image_filename.substring(iSIM_image_filename.lastIndexOf(endOfChannelTextIdentifier));
        // iterate over the other files in the directory, looking for a file that has the same prefix and suffix as the given image and a different channel identifier
        String directoryDelimiter = getDirectoryDelimiter(iSIM_image_filename); // save the directory delimiter for the supplied image to check against any files that are iterated over
        if (directoryDelimiter.isEmpty()) {
            System.out.println("Couldn't extract directory delimiter from config supplied image file: " + iSIM_image_filename);
            return "";
        }
        int containingDirLastCharIdx = iSIM_image_filename.lastIndexOf(directoryDelimiter);
        if (containingDirLastCharIdx == -1) {
            System.out.println("Couldn't extract containing directory path from supplied image name. Make sure an absolute path has been supplied.");
            return "";
        }


        String containingDir = iSIM_image_filename.substring(0, containingDirLastCharIdx);
        File[] fList = new File(containingDir).listFiles();
        if (fList != null) {
            for (File file : fList) {
                String filename = file.getAbsolutePath();

                // check if the directory delimiters differ from that supplied in the config file
                String directoryDelimiter1 = getDirectoryDelimiter(filename);
                if (directoryDelimiter1.isEmpty()) {
                    System.out.println("Couldn't extract directory delimiter from: " + filename);
                    return "";
                }

                if (!directoryDelimiter.equals(directoryDelimiter1)) {
                    // replace the delimiter in the queried file with that used in the supplied file
                    filename = filename.replace(directoryDelimiter1.charAt(0), directoryDelimiter.charAt(0));
                }


                if (filename.startsWith(filename_before_channel_text) && filename.endsWith(filename_after_channel_text)) {
                    // check if the channel number ID is different than the one supplied
                    if (Character.isDigit(filename.charAt(channelNumberID_idx)) &&
                        Character.getNumericValue(filename.charAt(channelNumberID_idx)) != channelNumberID) {
                        System.out.println("Found second iSIM color channel image at: " + filename);
                        return filename;
                    }
                }
            }
        } else {
            System.out.println("Couldn't open containing directory to look for other color channel images. Check the path supplied.");
            return "";
        }

        System.out.println("Second iSIM color channel not found");
        return "";
    }

    /**
     * Method to locate the second channel of an diSPIM dataset given the location of one color. This
     * method expects the image files and directories follow the native diSPIM output formats, either
     * fused or single view stacks.
     *
     * Example (fused):
     * diSPIM (fused)
     *       Color1/
     *           Decon/
     *               Decon_#.tif
     *       Color2/
     *           Decon/
     *               Decon_#.tif
     *
     *
     * Example (single views):
     * diSPIM_singleviews/
     *       SPIMA/
     *           ### nm/ the numbers here correspond the the length of the wavelength of light used by the scope (which corresponds to the excitation range of the fluorophore targeted)
     *               SPIMA-#.tif
     *           ### nm/
     *               SPIMA-#.tif (names match that of other directory ^^^)
     *       SPIMB/
     *           ### nm/
     *               SPIMB-#.tif
     *           ### nm/ (note - 2 subfolders have matching name in SPIMA, SPIMB)
     *               SPIMB-#.tif (names match that of other directory ^^^)
     *
     *  *Note: if an image in SPIMA/ is provided, it will return it's counterpart also in SPIMA/. AceTree does
     *  not currently support fusing multiple views
     *
     * @param diSPIM_image_filename
     * @return
     */
    public static String findSecondDiSPIMColorChannel(String diSPIM_image_filename) {

        if (diSPIM_image_filename == null || diSPIM_image_filename.isEmpty()) { return ""; }

        // figure out if this is a fused image (i.e both views fused) or a single view image
        if (diSPIM_image_filename.contains(COLOR)) {
            System.out.println("\nLocating second channel for diSPIM fused images given: " + diSPIM_image_filename);
            // figure out if the image file is in the Color1 or Color2 directory
            char colorNumber = diSPIM_image_filename.charAt(diSPIM_image_filename.lastIndexOf(COLOR) + COLOR.length());

            char colorNumberSwap = '0';
            if (colorNumber == '1') {
                colorNumberSwap = '2';
            } else if (colorNumber == '2') {
                colorNumberSwap = '1';
            } else {
                System.out.println("Color#/ directory containing diSPIM image needs to be either 1 or 2.");
                return "";
            }

            // swap the colors and return
            String secondDiSPIMFusedChannel = diSPIM_image_filename.substring(0, diSPIM_image_filename.lastIndexOf(COLOR) + COLOR.length())
                    + colorNumberSwap
                    + diSPIM_image_filename.substring(diSPIM_image_filename.lastIndexOf(COLOR) + COLOR.length() + 1);
            System.out.println("Second channel for fused diSPIM data found at: " + secondDiSPIMFusedChannel);

            return secondDiSPIMFusedChannel;
        } else if (diSPIM_image_filename.contains(SPIM)) {
            System.out.println("\nLocating second channel for diSPIM single view images (just using one scope view) given: " + diSPIM_image_filename);

            String directoryDelimiter = getDirectoryDelimiter(diSPIM_image_filename);
            if (directoryDelimiter.isEmpty()) {
                System.out.println("Directory delimiter couldn't be found.");
                return "";
            }
            int lastSlashIdx = diSPIM_image_filename.lastIndexOf(directoryDelimiter);
            if (lastSlashIdx == -1) {
                System.out.println("Image path not properly configured. Can't separate path and image name.");
                return "";
            }

            String imageNameNoPath = diSPIM_image_filename.substring(lastSlashIdx + 1);

            // now we want to move up a directory and get the directory name that this image resides in (for the diSPIM, the directory is named
            // by the scope parameters for this view - specifically, the wavelength of light with format ### nm/
            int secondToLastSlashIdx = diSPIM_image_filename.substring(0, lastSlashIdx).lastIndexOf(directoryDelimiter);
            if (secondToLastSlashIdx == -1) {
                System.out.println("Image path not properly configured. Can't identify containing directory.");
                return "";
            }

            // the path up until the second to last slash is the directory which contains both views and their images (of which one has been supplied
            String directoryWithScopeViewSubfolders = diSPIM_image_filename.substring(0, secondToLastSlashIdx);

            // extract the name of the containing directory of the image supplied so that we can use it to find the other directory with the second channel
            String containingDirectoryName = diSPIM_image_filename.substring(secondToLastSlashIdx + 1, lastSlashIdx);

            // iterate over all subfolders and files in the topmost level we have
            File[] fList = new File(directoryWithScopeViewSubfolders).listFiles();
            if (fList != null) {
                for (File file : fList) {
                    if (file.isDirectory()) {
                        //System.out.println("Dir name: " + file.getName() + " -- against supplied dir name: " + containingDirectoryName);

                        // check if we found a data directory which
                        if (!file.getName().toLowerCase().equals(containingDirectoryName)) {
                            // we'll do some basic checks here to make sure we're working with the right folder

                            // make sure the directory name is 6 characters long, contains the substring " nm" at the end, and has digits in the first three positions (i.e. the wavelength of light used by this view to excite the fluorophore
                            if (file.getName().length() == diSPIM_DIRECTORY_NAME_LENGTH &&
                                    file.getName().endsWith(diSPIM_DIRECTORY_END_SUBSTR) &&
                                    Character.isDigit(file.getName().charAt(IDX_0)) &&
                                    Character.isDigit(file.getName().charAt(IDX_1)) &&
                                    Character.isDigit(file.getName().charAt(IDX_2))) {
                                // let's look for the same image name in this parallel folder
                                File[] fList_secondChannelDir = new File(file.getAbsolutePath()).listFiles();
                                if (fList_secondChannelDir != null) {
                                    for (File file1 : fList_secondChannelDir) {
                                        //System.out.println("File name in second channel directory: " + file1.getName() + " -- against supplied name: " + imageNameNoPath);
                                        if (file1.getName().toLowerCase().equals(imageNameNoPath.toLowerCase())) {
                                            System.out.println("Second channel for single views diSPIM data found at: " + file1.getAbsolutePath());
                                            return file1.getAbsolutePath();
                                        }
                                    }
                                } else {
                                    System.out.println("Couldn't open directory and access files - " + file.getAbsolutePath());
                                    return "";
                                }
                            } else {
                                System.out.println("Directory in diSPIM data structure does not match naming criteria of '### nm/'. Please use unmodified structure in future.");
                            }

                        }
                    } else if (file.isFile()) {
                        System.out.println("Native diSPIM output data doesn't contain files in subdirectory with respective directories for each view. Please use unmodified structure in future.");
                    }
                }
            } else {
                System.out.println("Couldn't open directory to access subfolders pertaining to each scope view.");
                return "";
            }

        } else {
            System.out.println("diSPIM image file path doesn't contain 'Color' (fused views) or 'SPIM' (single views) so directory structure can't be inferred");
            return "";
        }

        return "";
    }

    /**
     * There is a convention of using old XML files for datasets that have since replaced their 8bit images with 16bit images.
     * This structure has the 16bit images sitting two directories above the 8bit images that used to be present. Specifically,
     * these two directories are image/tif/
     *
     *
     * @param filename
     * @return
     */
    public static boolean doesImageFollow8bitDeletedConvention(String filename) {
        if (filename == null || filename.isEmpty()) return false;

        return filename.contains(sliceDeleteConventionTIFStr_1) ||
                filename.contains(sliceDeleteConventionTIFStr_2) ||
                 filename.contains(sliceDeleteConventionTIFRStr_1) ||
                  filename.contains(sliceDeleteConventionTIFRStr_2);
    }

    /**
     * Determines if the image is a slice image by checking whether it has a plane identifier in the filename
     *
     * @param filename
     * @return
     */
    public static boolean isSliceImage(String filename) {
        if (filename == null || filename.isEmpty()) return false;

        String imageName = "";
        // first make sure we can extract just the file name and get rid of the path
        String directoryDelimiter = getDirectoryDelimiter(filename);
        if (directoryDelimiter.isEmpty()) {
            System.out.println("Couldn't determine directory delimiter when checking if slice image.");
            return false;
        }
        imageName = filename.substring(filename.lastIndexOf(directoryDelimiter));

        // if we got the file name, proceed
        if (!imageName.isEmpty()) {
            // "...-p###.EXT case
            if (imageName.lastIndexOf(PERIOD) != -1 && imageName.lastIndexOf(DASH) != -1) {
                // check for presence of "-p" and make sure that there are only enough characters after the dash, before
                // the start of the file extension to fit -p###.ext
                if (imageName.substring(imageName.lastIndexOf(DASH), imageName.lastIndexOf(DASH)+2).equals(planeStr) &&
                        imageName.lastIndexOf(PERIOD) - imageName.lastIndexOf(DASH) <= 5) {
                    return true;
                }
            // "..._p###.EXT case
            } else if (imageName.lastIndexOf(PERIOD) != -1 && imageName.lastIndexOf(UNDERSCORE) != -1) {
                if (imageName.substring(imageName.lastIndexOf(UNDERSCORE), imageName.lastIndexOf(UNDERSCORE)+2).equals(planeStrAlt) &&
                        imageName.lastIndexOf(PERIOD) - imageName.lastIndexOf(UNDERSCORE) <= 5) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Special distinguishing between normal 16bit convention and native diSPIM output
     *
     * @param filename
     * @return
     */
    public static int extractTimeFromImageFileName(String filename) {
       if (filename == null || filename.isEmpty()) return -1;

        String convention = getDirectoryDelimiter(filename);
        if (convention.isEmpty()) return -1;

       // let's cut out the path and just use the filename in the event that some part of the path messes up the extraction algorithm
        filename = filename.substring(filename.lastIndexOf(convention));

       if (filename.contains(tID_8bitConvention) && filename.contains(planeStr)) {
           // extract the number, assuming the format -t###-p
           String numberSequenceStr = filename.substring(filename.lastIndexOf(tID_8bitConvention) + tID_8bitConvention.length(), filename.lastIndexOf(planeStr));

           if (numberSequenceStr.length() < 3) return -1;

           // remove any zero padding
           if (Character.getNumericValue(numberSequenceStr.charAt(0)) == 0) {
               if (Character.getNumericValue(numberSequenceStr.charAt(1)) == 0) {
                   return Integer.parseInt(numberSequenceStr.substring(2));
               } else {
                return Integer.parseInt(numberSequenceStr.substring(1));
               }
           } else {
               return Integer.parseInt(numberSequenceStr);
           }
       } else if (filename.contains(tID_16bitConvention) && filename.contains(TIF_ext)) {
            // extract the number, assuming the format _t###.TIF
           return Integer.parseInt(filename.substring(filename.lastIndexOf(tID_16bitConvention) + tID_16bitConvention.length(), filename.indexOf(TIF_ext)));
       } else if (!filename.contains(tID_8bitConvention) && !filename.contains(tID_16bitConvention) && filename.contains(tif_ext)) {
           // assume it's diSPIM and extract the numbers that immediately proceed the .tif extension
           String timeStr = "";
           filename = filename.substring(0, filename.indexOf(tif_ext)); // cut out the extension so that we can start iterating from the last character in the string
           for (int i = filename.length()-1; i >= 0; i--) {
               if (Character.isDigit(filename.charAt(i))) {
                   timeStr = filename.charAt(i) + timeStr; // append the new character at the front since we're iterating from the back of the string
               } else if (filename.charAt(i) == UNDERSCORE || filename.charAt(i) == DASH) {
                   return Integer.parseInt(timeStr);
               }
           }
       }

       return -1;
    }

    /**
     * Used by ImageConfig to maintain the prefixes for the images series
     *
     * Most 16bit TIFs follow the naming convention of ..._t###.TIF. However,
     * native output diSPIM data does not follow this convention. Instead, it
     * uses ..._###.tif in the cased of fused images, and ...-###.tif in the
     * case of single view images. We'll assume, when finding the prefix for
     * a 16bit TIF, that if the _t convention can't be found, then we're probably
     * dealing with diSPIM data.
     *
     * TODO: make stack checks regex check that can do everything
     *
     * @param imageName
     * @return
     */
    public static String getImagePrefix(String imageName) {
        if (imageName == null || imageName.isEmpty()) { return ""; }

        String convention = getDirectoryDelimiter(imageName);
        if (convention.isEmpty()) return convention;

        if (isSliceImage(imageName)) {
            // assume the 8bit naming convention, even though the image could in fact be 16bit
            return imageName.substring(0, imageName.lastIndexOf(tID_8bitConvention) + tID_8bitConvention.length());
        } else {
            // distinguish between _t###.TIF and diSPIM conventions
            if (imageName.indexOf(tID_16bitConvention) != -1) {
                // more standard case
                return imageName.substring(0, imageName.lastIndexOf(tID_16bitConvention) + tID_16bitConvention.length());
            } else {
                // the most we'll do to check if it's diSPIM is see if the characters between the last _ or - and the extension
                // are numbers

                // see if it's a fused diSPIM image or a single view (restrict the search to only the filename, not the full path)
                char timeAppendCharacterType = '0';
                if ((imageName.substring(imageName.lastIndexOf(convention))).lastIndexOf(UNDERSCORE) != -1) { // fused
                    timeAppendCharacterType = UNDERSCORE;
                } else if ((imageName.substring(imageName.lastIndexOf(convention))).lastIndexOf('-') != -1) { // single view
                    timeAppendCharacterType = DASH;
                } else {
                    System.out.println("Couldn't extract image prefix from: " + imageName + "\nUnable to find character type before time.");
                    return "";
                }

                // now that we know whether it's fused or single view, use that correct character type to extract the prefix
                String potentialTimeStr = imageName.substring(imageName.lastIndexOf(timeAppendCharacterType)+1, imageName.indexOf(tif_ext));

                for (int i = 0; i < potentialTimeStr.length(); i++) {
                    if (!Character.isDigit(potentialTimeStr.charAt(i))) {
                        System.out.println("Couldn't extract image prefix from: " + imageName + "\nExpected " + potentialTimeStr.charAt(i) + " to be a digit.");
                        return "";
                    }
                }

                // if we've reached here, we know that the potentialTimeStr is all digits, so we'll treat this as the time and everything
                // before it will be considered the image prefix
                //System.out.println("Found image prefix: " + imageName.substring(0, imageName.lastIndexOf(timeAppendCharacterType)+1));
                return imageName.substring(0, imageName.lastIndexOf(timeAppendCharacterType)+1);

            }
        }
    }

    public static String getDirectoryDelimiter(String path) {
        //System.out.println(path);
        if (path.lastIndexOf(FORWARDSLASH) != -1)
        {
            //System.out.println("found forwardslash");
            return FORWARDSLASH;
        } else if (path.lastIndexOf(BACKSLASH) != -1) {
            //System.out.println("Found backslash");
            return BACKSLASH;
        } else {
            return "";
        }
    }

    // mutator methods used by ImageManager to bring up different images in the time series
    public static String[] appendTimeToMultiple16BitTifPrefixes(String[] TIFprefixes_16bit, int time) {
        String[] formattedFileNames = new String[TIFprefixes_16bit.length];
        for (int i = 0; i < TIFprefixes_16bit.length; i++) {
            //System.out.println("String to append time to: " + TIFprefixes_16bit[i]);
            if (!TIFprefixes_16bit[i].isEmpty()) {
                formattedFileNames[i] = appendTimeToSingle16BitTIFPrefix(TIFprefixes_16bit[i], time);
            } else {
                formattedFileNames[i] = "";
            }

        }

        return formattedFileNames;
    }

    /**
     *
     * Distinguish between the normal convention for 16bit ..._t###.TIF
     * and the diSPIM convention of ...-###.tif or ..._###.tif by checking
     * whether the last character in the prefix is a t
     *
     * This method should be used cautiously if modifying.
     *
     * @param TIFprefix_16bit
     * @param time
     * @return
     */
    public static String appendTimeToSingle16BitTIFPrefix(String TIFprefix_16bit, int time) {
        if (TIFprefix_16bit.charAt(TIFprefix_16bit.length()-1) == tChar) { // normal case
            return TIFprefix_16bit + Integer.toString(time) + TIF_ext;
        } else { // diSPIM case -- use .tif ext
            return TIFprefix_16bit + Integer.toString(time) + tif_ext;
        }

    }

    public static String appendTimeAndPlaneTo8BittifPrefix(String tifPrefix_8bit, int time, int plane) {
        return tifPrefix_8bit + formatTimeInteger(time) + planeStr + formatPlaneInteger(plane) + tif_ext;
    }

    private static String formatTimeInteger(int integer) {
        if (integer < 1) { return ""; }

        if (integer < 10) {
            return ZERO_PAD + ZERO_PAD + Integer.toString(integer);
        } else if (integer >= 10 && integer < 100) {
            return ZERO_PAD + Integer.toString(integer);
        } else if (integer >= 100) {
            // we'll assume that anything that is 3 or more digits doesn't have any zero padding
            return Integer.toString(integer);
        }

        return "";
    }

    private static String formatPlaneInteger(int integer) {
        if (integer < 1) { return ""; }

        if (integer < 10) {
            return  ZERO_PAD + Integer.toString(integer);
        } else if (integer >= 10) {
            return Integer.toString(integer);
        }

        return "";
    }

    /**
     * Method to determine whether a path is relative. Does not distinguish between
     * relative paths that define files that are upstream or downstream i.e. ../ or ./
     * @param path
     * @return
     */
    public static boolean isPathRelative(String path) {
        if (path == null || path.isEmpty()) return false;

        return (path.charAt(0) == '.');
        //return (!new File(path).isAbsolute() || path.charAt(0) == '.');
    }

    /**
     * Method that provides support for reading files that are implicitly relative
     * i.e. those files that just have a file name listed and are not prepended
     * with any form of './ or ../'
     * @param path
     * @return
     */
    public static boolean isPathImplicitRelative(String path) {
        if (path == null || path.isEmpty()) return false;

        // determine if just a file name is listed by checking whether there is a directory delimiter or not
        return getDirectoryDelimiter(path).equals("");
    }

    /**
     * Determines if a relative path (check for relative should already be made
     * before calling this method, but it checks anyway for safety) is upstream
     * i.e. is the listed file in a containing directory i.e. does the path start
     * with "../"
     * @param path
     * @return
     */
    public static boolean isRelativePathUpstream(String path) {
        if (!isPathRelative(path)) return false;

        return path.startsWith(upstreamDirectoryID_1) || path.startsWith(upstreamDirectoryID_2);
    }

    /**
     * Determines if a relative path is downstream OR contained
     * in the same directory as another file i.e. does the path
     * start with "./"
     * @param path
     * @return
     */
    public static boolean isRelativePathDownstream(String path) {
        if (!isPathRelative(path)) return false;

        return path.startsWith(downstreamDirectoryID_1) || path.startsWith(downstreamDirectoryID_2);
    }

    /**
     * Determines the number of upstream directories that are specified at the start of a relative path
     * e.g.
     * ../../../ = 3
     * ../../ = 2
     * ../ = 1
     * @param path
     * @return
     */
    public static int getNumberOfUpstreamDirectoriesSpecifiedInRelativePath(String path) {
        if (!isPathRelative(path) || isRelativePathDownstream(path)) return 0;

        int upDirCount = 0;

        for (int i = 0; i < path.length() - upstreamDirectoryID_1.length(); i += upstreamDirectoryID_1.length()) {
            if (path.substring(i, i + upstreamDirectoryID_1.length()).equals(upstreamDirectoryID_1) ||
                path.substring(i, i + upstreamDirectoryID_2.length()).equals(upstreamDirectoryID_2)) {
                upDirCount++;
            } else {
                // return if the condition isn't met because these have to be consecutive from the start of the string to be valid
                return upDirCount;
            }
        }

        return upDirCount;
    }

    /**
     * Used to determine the number of nested directories in a config file's path
     * @param path
     * @return
     */
    public static int getNumberOfDirectoriesInAbsolutePath(String path) {
        if (path == null || path.isEmpty() || isPathRelative(path)) return 0;

        String dd = getDirectoryDelimiter(path);
        if (dd.isEmpty()) {
            System.out.println("Couldn't extract directory delimiter from string when parsing relative path. ImageNameLogic.getNumberOfDirectoriesInAbsolutePath().");
            return 0;
        }
        char directoryDelimiter = dd.charAt(0);

        // we need to subtract one because absolute paths will be prepended with a delimiter
        return Math.toIntExact(path.chars().filter(ch -> ch == directoryDelimiter).count()) - 1;
    }

    /**
     * Returns the first N directories of an absolute path in string form. This is used
     * in conjuction with the methods above the append a relative path that specifies
     * upstream directories with an absolute path
     * @param path
     * @param N - the number of directories in path that should be returned in a single string
     * @return
     */
    public static String getFirstNDirectoriesInAbsolutePath(String path, int N) {
        //System.out.println((path == null) + ", " +  path.isEmpty() + ", " +  isPathRelative(path) + ", " +
        //        (N < 1) + ", " +  (getNumberOfDirectoriesInAbsolutePath(path) < N));

        if (path == null || path.isEmpty() || isPathRelative(path) ||
                N < 1 || getNumberOfDirectoriesInAbsolutePath(path) < N) return "";

        String dd = getDirectoryDelimiter(path);
        if (dd.isEmpty()) {
            System.out.println("Couldn't extract directory delimiter from string when parsing relative path. ImageNameLogic.getFirstNDirectoriesInAbsolutePath().");
            return "";
        }
        char directoryDelimiter = dd.charAt(0);

        int numDelimitersParsed = 0;
        int i = path.indexOf(directoryDelimiter)+1;
        for (; i < path.length() && numDelimitersParsed < N; i++) {
            if (path.charAt(i) == directoryDelimiter) {
                numDelimitersParsed++;
            }
        }

        return path.substring(0, i);
    }

    /**
     * Returns a substring in a path which consists of all characters that occur after
     * the upstream directory substrings. This is used to concatenate a relative path with
     * upstream directory substrings with an absolute path
     * e.g.
     * ../../../directory1/file.txt --> directory1/file.txt
     * @param path
     * @return
     */
    public static String getImagePathAfterUpstreamDirectoryCharacters(String path) {
        if (path == null || path.isEmpty()) return "";

        String dd = getDirectoryDelimiter(path);
        if (dd.isEmpty()) {
            System.out.println("Couldn't extract directory delimiter from string when parsing relative path. ImageNameLogic.getImagePathAfterUpstreamDirectoryCharacters().");
            return "";
        }
        char directoryDelimiter = dd.charAt(0);

        int i = 0;
        for (; i < path.length(); i++) {
            // break out of the loop when we find the first characters that's not a directory delimiter or a period
            if (path.charAt(i) != directoryDelimiter && path.charAt(i) != PERIOD.charAt(0)) break;
        }

        return path.substring(i);
    }

    public static void main(String[] args) {
//        String test = "C:\\Users\\brade\\OneDrive\\Desktop\\MSKCC\\AceTree_data\\20141022_JIM113_UNC-86myrGFP\\image\\tif\\20141022_JIM113_UNC-86myrGFP_1_s1-t001-p01.tif";
//        String secondChannelAttempt = findSecondColorChannelFromSliceImage(test);
//
//        String updatedStr = reconfigureImagePathFrom8bitTo16bit(test);
//        System.out.println(updatedStr);
//
//        String iSIM_test = "C:/Users/brade/OneDrive/Desktop/MSKCC/AceTree_data/ForBraden/iSIM-test data/KB_BV591_03192018_w2iSIM - TxRed - 600-50_s1_t3.TIF";
//        String iSIM_test = "L:/shahp2/ForBraden/iSIM_test data/KB_BV591_03192018_w2iSIM - TxRed - 600-50_s1_t3.TIF";
//        String iSIM_result = findSecondiSIMColorChannel(iSIM_test);
//
//        String diSPIM_fused_test = "/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_post2018/09082016_lineage/Color1/Decon/Decon_1.TIF";
//        String diSPIM_fused_result = findSecondDiSPIMColorChannel(diSPIM_fused_test);
//
//        String diSPIM_singleview_test = "/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_post2018/ForBraden/diSPIM_singleviews/SPIMA/488 nm/SPIMA-0.tif";
//        String diSPIM_singleview_result = findSecondDiSPIMColorChannel(diSPIM_singleview_test);

//        String test = "hello" + BACKSLASH + "image" + BACKSLASH + "tif" + BACKSLASH + "myfile.tif";
//        System.out.println(doesImageFollow8bitDeletedConvention(test));

//        String test = "L:\\shahp2\\ForBraden\\diSPIM_singleviews\\SPIMB\\488 nm\\SPIMB-4.tif";
//        System.out.println(getImagePrefix(test));

//        String test = "/Users/bradenkatzman/Desktop/ForBraden/iSIM-test data/KB_BV591_03192018_w1iSIM - FITC - 525-50_s1_t1.TIF";
//        System.out.println(findSecondiSIMColorChannel(test));

//        String test = "./delimiter_test/hello/hi.txt";
//        test = test.replace("/".charAt(0), "\\".charAt(0));
//        System.out.println(test);

//        String path = "..\\..\\..\\hello.txt";
//        System.out.println(getNumberOfUpstreamDirectoriesSpecifiedInRelativePath(path));

//        String path = "C:\\Users\\blah\\blah\\blah\\blah\\blah\\text.txt";
//        System.out.println(getNumberOfDirectoriesInAbsolutePath(path));

//        String path = "C:/Users/blah2/blah3/blah4/file.txt";
//        int N = 2;
//        System.out.println(getFirstNDirectoriesInAbsolutePath(path, 3));

//        String path = "..\\data\\more_data\\image.jpg";
//        System.out.println(getImagePathAfterUpstreamDirectoryCharacters(path));
    }
}
