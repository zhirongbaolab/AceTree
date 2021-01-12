README for XML configuration file: 

Tags:
<embryo>
	<image> points to an 8bit or 16bit image file (the first of a set). If this points to 8bit and it don't exist, it will search for 16bit images two directories back. This tag can also point at a diSPIM or iSIM image in the native directory structure for the microscope and it will make an attempt to find a second color channel image. Lastly, this tag can point to multiple images that correspond to multiple color channel images in a series. The two formats are as follows:
		<image file="IMAGE_FILE_NAME" />
		<image numChannels=n channel1="IMAGE_FILE_NAME_CHANNEL_RED" channel2="IMAGE_FILE_NAME_CHANNEL_GREEN" ... channelN="IMAGE_FILE_NAME_CHANNEL_N" />, where n is a real integer (*Note: currently, only 3 channel imaging is supported, though listing more than three channels will not cause any problems. The first three listed will be loaded)

        * NOTE: Relative path names are supported for the image files (in both tags). The relative path must be defined relative to the XML
        config file name because the images will be opened by prepending the image filename with the
	    * NOTE: The image tag follows a convention of assigning color based on the order which the image files are listed. Specifically, it follows an ordering of
	            RGB such that the first file is assumed to be the red channel, the second file is assumed to be the green channel, and the third file is assumed to be
	            the blue channel. This implies that if the first image tag is used (where only one file is supplied) it is assumed to be red, and any companion files
	            are assumed to be green and then blue.
	            This convention can be used to correctly specify the color channel(s) of an image series that do not have a certain channel, e.g. red. For example, a green
	            image series can be specified by using a blank entry in the usual red channel slot. E.g.:
	            <image numChannels=2 channel1="" channel2="IMAGE_FILE_NAME_CHANNEL_GREEN"/>
	            E.g.: Green and blue image series
	            <image numChannels=3 channel1="" channel2="IMAGE_FILE_NAME_CHANNEL_GREEN" channel3="IMAGE_FILE_NAME_CHANNEL_BLUE"/>
	            It is important to specify 2 and 3 channels for numChannels in the above examples, respectively, because that indicates to AceTree how
	            many tags it needs to process. AceTree will determine that only 1 and 2 valid channels are present, respectively.

	<nuclei> points to a zip with nuclei files
	* NOTE: Relative path names are supported for the nuclei tag. Their absolute path will be built by prepending
	        the absolute path to the supplied XML config file to the filename. Therefore, the XML files must reside in the same directory as
	        the images for AceTree to properly construct an absolute path name
	
	<end> the last timepoint AceTree will show (note that all timepoints will be loaded, this is just the last that will be displayed)
	
	<resolution> contains x,y,z resolution (xy grouped together), and plane end
	
	<exprCorr> the gene expression channel in AceTree files that controls the type of background color for the interface ("blot" is defauly correction)

	<Split SplitMode="1"> (OPTIONAL) indicates that in the presence of image stacks (assumed to be from a confocal)
    	split them into two channels (1) or don't split them (0). The default behavior, if the provided images are stacks and
    	they are not a special case (see iSIM, diSPIM description below), and this flag is not provided, is to split the stack

    <Flip FlipMode="1"> (OPTIONAL) indicates that in the presence of 16bit images (assumed to to be from a confocal scope)
    	flip them horizontally. The default behavior, if the provided images are stacks and they are not a special case (see
    	iSIM, diSPIM description below), and this flag is not provided, is the flip the stack.

	<useStack> (OPTIONAL) indicates slice (0) or stack (1) images. If not listed, default is 0.
	* NOTE: Starting 11/2018, this flag is no longer used. The bit depth of the image series is determine by the program.
	        However, placing it in an .XML file will not cause any problems. It is listed here for legacy support assurance.

	<polar> (May be obsolete) AceTree's attempt to filter out early appearing cells which are actually polar bodies
</embryo>


OVERVIEW OF IMAGE LOGIC:
    The types of data that can be opened by AceTree are: slice image series, confocal image stack series (unlikely type),
    and single TIF stack per time frame. The single TIF stack/time frame format is the most common data format. For images
    of this type to open in AceTree, the filenames need to have the following format: ...###.TIF, where ### signifies the
    time frame (NOTE: time frames less than digits do not need to be zero padded).

    Any data formatted according to the types listed about should open in AceTree. Data that isn't one of these three types
    will not be correctly handled by AceTree. In addition to the general data handling described above, there are a few
    specific cases that are robustly handled by AceTree and are described with accompanying examples below.

    *** NOTE: The slice format is mostly for back compatibility with the historical format used at AceTree's conception.

SUPPORTED SPECIAL FORMATS:
1. iSIM - Support for reading native iSIM output
    If a single iSIM image file is provided in the image tag, AceTree will find other color channels in the image series if the
    images are contained in the output directory that was generated by the iSIM.
        See the javadocs from ImageNameLogic.findSecondiSIMColorChannel()
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

    Examples:
    1a. Provide a single file and AceTree will locate a second channel. In this example, the <Split> and <Flip> tags
        do not need to be provided because AceTree will recognize that a second channel is present according to the iSIM
        directory format and will set Split and Flip flags to 0 internally.
    <?xml version='1.0' encoding='utf-8'?>
        <embryo>
            <image file="/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_post2018/ForBraden/iSIM-test data/KB_BV591_03192018_w2iSIM - Red - 600-50_s1_t1.TIF"
            <nuclei file="./KB_BV591_03192018_singeChannelSuppliedTest.zip"/>
            <end index="10"/>
            <resolution xyRes="0.254" zRes="1" planeEnd="35"/> <exprCorr type="blot"/>
            <polar size="15"/>
        </embryo>

    1b. Explicitly specify the color channels and their locations. In this example, a RED, GREEN, and BLUE channel are
    specified explicitly. Further, it is explicitly specified that the stack should not be SPLIT nor FLIPPED
    <?xml version='1.0' encoding='utf-8'?>
    <embryo>
        <image numChannels="3" channel1="/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_post2018/ForBraden/iSIM-test data/KB_BV591_03192018_w2iSIM - Red - 600-50_s1_t1.TIF"
                                channel2="/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_post2018/ForBraden/iSIM-test data/KB_BV591_03192018_w1iSIM - Green - 525-50_s1_t1.TIF"
                                channel3="/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_post2018/ForBraden/iSIM-test data/KB_BV591_03192018_w2iSIM - Blue - 600-50_s1_t1.TIF"/>
        <nuclei file="/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_post2018/ForBraden/iSIM-test data/KB_BV591_03192018_threeChannelsSuppliedTest.zip"/>
        <end index="10"/>
        <resolution xyRes="0.254" zRes="1" planeEnd="35"/> <exprCorr type="blot"/>
        <polar size="15"/>
        <Split SplitMode="0"/> <Flip FlipMode="0"/>
    </embryo>

    1c. Explicitly specify the color channels and their locations (in this example, the channel1 entry (red channel) is
        intentionally left blank) and the channel2 entry (green channel) is provided. The Split and Flip flags are set
        explicitly.
    <?xml version='1.0' encoding='utf-8'?>
    <embryo>
        <image numChannels="2" channel1=""
                                channel2="/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_post2018/ForBraden/iSIM-test data/KB_BV591_03192018_w1iSIM - Green - 525-50_s1_t1.TIF"/>
        <nuclei file="/media/braden/24344443-dff2-4bf4-b2c6-b8c551978b83/AceTree_data/data_post2018/ForBraden/iSIM-test data/KB_BV591_03192018_twoChannelsSuppliedTest.zip"/>
        <end index="10"/>
        <resolution xyRes="0.254" zRes="1" planeEnd="35"/> <exprCorr type="blot"/>
        <polar size="15"/>
        <Split SplitMode="0"/> <Flip FlipMode="0"/>
    </embryo>

    *** NOTE: AceTree will not attempt to locate the red channel in this instance because the explicit tag is being used to specify
    that only a green channel is desired.


2. diSPIM - Support for reading native diSPIM output
    If a single diSPIM image file is provided in the image tag, AceTree will find other color channels in the image series if the
    images are contained in the output directory that was generated by the diSPIM.
        See the javadocs from ImageNameLogic.findSecondDiSPIMColorChannel()
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

    Examples:
    2a. Provide a single file and AceTree will locate a second channel. In this example, the <Split> and <Flip> tags
        do not need to be provided because AceTree will recognize that a second channel is present according to the diSIM
        directory format and will set Split and Flip flags to 0 internally.
    <?xml version='1.0' encoding='utf-8'?>
    <embryo>
        <image file="/Users/user/Desktop/ForBraden/diSPIM_fused/Color1/Decon/Decon_0.tif"/>
        <nuclei file="/Users/user/Desktop/ForBraden/diSPIM_fused/Color1/Decon/diSPIM_fused_singleChannelSuppliedTest.zip"/>
        <end index="10"/>
        <resolution xyRes="0.254" zRes="1" planeEnd="35"/> <exprCorr type="blot"/>
        <polar size="15"/>
    </embryo>

    2b. Explicitly specify the color channels and their locations
    <?xml version='1.0' encoding='utf-8'?>
    <embryo>
        <image numChannels="2" channel1="/Users/user/Desktop/ForBraden/diSPIM_fused/Color1/Decon/Decon_0.tif"
                                channel2="/Users/user/Desktop/ForBraden/diSPIM_fused/Color2/Decon/Decon_0.tif"/>
        <nuclei file="/Users/bradenkatzman/Desktop/ForBraden/diSPIM_singleviews/SPIMA/488 nm/SPIMA_diSPIM_singleViews_singleChannelSuppliedTest.zip"/>
        <end index="10"/>
        <resolution xyRes="0.254" zRes="1" planeEnd="35"/> <exprCorr type="blot"/>
        <Split SplitMode="0"/> <Flip FlipMode="0"/>
        <polar size="15"/>
    </embryo>

3. Confocal
    Example: Image tag uses a relative path and points at a stack from the confocal scope. With no Split or Flip
    flag specification, AceTree will default to SplitMode="1" and FlipMode="1".
    <?xml version='1.0' encoding='utf-8'?>
    <embryo>
       <image file="./20140407_JIM113_SiO-0.15_1_s1_t1.tif"/>
       <nuclei file="./20140407_JIM113_SiO-0.15_1_s1_emb_mirroredcorrect_edited.zip"/>
       <end index="475"/>
       <resolution xyRes="0.15" zRes="0.75" planeEnd="60"/> <exprCorr type="blot"/>
       <polar size="15"/>
    </embryo>

4. Slice images (legacy format)
    Example: using relative path names (AceTree will prepend the absolute path of the provided XML file to this filename)
    <?xml version='1.0' encoding='utf-8'?>
    <embryo>
        <image file="./20140407_JIM113_SiO60/image/tif/20140407_JIM113_SiO-0.15_1_s1-t001-p01.tif"/>
        <nuclei file="./20140407_JIM113_SiO-0.15_1_s1_emb_mirroredcorrect_edited.zip"/>
        <end index="475"/>
        <resolution xyRes="0.15" zRes="0.75" planeEnd="60"/> <exprCorr type="blot"/>
        <useStack type="1" /> <Split SplitMode="0"/>
        <polar size="15"/>
    </embryo>