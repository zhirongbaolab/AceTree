/*
 * Created on Oct 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.snight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JFileChooser;
//import org.rhwlab.acetree.AceTree;

import org.rhwlab.help.FileError;
import org.rhwlab.image.management.ImageConfig;


/**
 * Revised 10/2018
 * @author Braden Katzman
 *
 * The refactored version of this class now acts as a top level manager of the respective NucleiConfig and ImageConfig classes
 */
public class Config {

    //******************************************************************************************************************
    // main vars
    public String       iConfigFileName;
    private NucleiConfig nucConfig;
    private ImageConfig imageConfig;

    // helper vars
    private String XML_ext = ".xml";

    /**
     * Revised constructor
     * @author Braden Katzman
     *
     * This constructor takes a file name, opens and parses it, and separates the contents into the
     * respective NucleiConfig and ImageConfig objects
     *
     * @param configFileName
     */
    public Config(String configFileName) {
        // error check
        if (configFileName == null || configFileName.isEmpty()) return;

        this.iConfigFileName = configFileName;

        // check what kind of file it is
        int test_idx = configFileName.lastIndexOf(".");
        if (test_idx > 0) {
            if (configFileName.substring(test_idx).equals(XML_ext)) {
                XMLConfig xmlConfigLoader = new XMLConfig();
                Hashtable<String, String> xmlConfigData = xmlConfigLoader.loadConfigDataFromXMLFile(configFileName);

                // TESTING *******************************************
//                for (String s : xmlConfigData.keySet()) {
//                    System.out.println("k, v: " + s + ", " + xmlConfigLoader.getXMLConfigDataHash().get(s));
//                }
                // TESTING *******************************************

                // use the data to populate the two config classes
                this.nucConfig = new NucleiConfig(xmlConfigData, configFileName);
                this.imageConfig = new ImageConfig(xmlConfigData, configFileName);

                // WHAT NEEDS TO BE DONE TO MAKE UP FOR SETSTARTINGPARMS ?
            }
        }
    }

    public String getShortName() {
        //System.out.println("Configuration file name: "+iConfigFileName);
        String s = iConfigFileName;
        return getShortName(s);
    }

    public static String getShortName(String longName) {
        int k = longName.lastIndexOf(File.separator);
        return longName.substring(k + 1);
    }

    public NucleiConfig getNucleiConfig() {
        return this.nucConfig;
    }

    public ImageConfig getImageConfig() {
        return this.imageConfig;
    }

    public String configsToString() {
        return nucConfig.toString() + imageConfig.toString();
    }

    public String getConfigFileName() { return iConfigFileName; }
    //******************************************************************************************************************

















    // REVISE ALL OF THESE VARS
    public String       iParent;
    public Hashtable    iConfigHash;
    public String       iZipFileName; // a full path to the zip with nuclei and parameters
    public String       iZipNucDir;   // subdirectory in above zip for nuclei
    public String       iTypicalImage;
    public String       iZipTifFilePath; // a full path to the zip file with tifs in it
    public String       iTifPrefix;      // leading part of image file names also parameters file
    public int          iStartingIndex;
    public int          iEndingIndex;
    public int          iNamingMethod;
    public int          iUseZip;
    public int			iUseStack; //ted change stack type

    /*
     * iSplit - Flag to determine whether or not to split 16-bit images
      *
      * iUseStack is either set to 1, in which case non-8bit images are used, or set to 0
      * or not set at all, in which case 8bit images are used.
      *
      * This flag will only apply to cases in which iUseStack is 1, and it will distinguish
      * between cases when the images should be split (iSplit == 1, or not set),
      * and when the images should not be split (iSplit == 0)
      */
    public int          iSplit;
    public String       iAxisGiven; // must be "", or "adl", or "avr"
    public float        iXy_res;
    public float        iZ_res;
    public int          iPolar_size;
    public int          iPlaneStart;
    public int          iPlaneEnd;
    public double       iZPixRes;
    public String       iExprCorr; // one of none, global, local, blot, cross
    public int			iStartTime;
    public FileError 	iImageFileError;
    public FileError	iZipFileError;
    public int			iSplitChannelImage; // whether to split image into two channels for 16-bit mode





    public Config() {
    	// 20090730 beefed up to support SNLauncher change
    	//                       to this class for writing configs

    	iEndingIndex = 9999;
    	iNamingMethod = Identity3.NEWCANONICAL;
    	iAxisGiven = "";
    	iExprCorr = "none";
    	iPolar_size = Config.POLARSIZENOMINAL;
    	iXy_res = Config.XYRESNOMINAL;
    	iZ_res = Config.ZRESNOMINAL;
    	iPlaneEnd = Config.PLANEENDNOMINAL;

    	//showStartingParms();
    	
    	iImageFileError = null;
    	iZipFileError = null;
    	
    	iSplitChannelImage = 1; //default
    }

    public Config(String configFile, boolean xml){
    	this();
    	iConfigFileName = configFile;

        iConfigFileName = configFile;
        iTypicalImage = "";
        iEndingIndex = 99999;
        iXy_res = XYRESNOMINAL;
        iZ_res = ZRESNOMINAL;
        iPolar_size = POLARSIZENOMINAL;
        iPlaneStart = PLANESTARTNOMINAL;
        iPlaneEnd   = PLANEENDNOMINAL;
        iZipNucDir = "nuclei/";
        iAxisGiven = "";
        iExprCorr = "blot";
        if (!xml) {
            getStartingParms();
            setStartingParms();
        }

        showStartingParms();
    }

    // note that the XMLConfig code called here
    // calls back to setStartingParms as its last official act
    public static Config createConfigFromXMLFile(String configFileName) {
        return XMLConfig.createConfigFromXMLFile(configFileName);
    }

    public void setEndingIndex(int endingIndex) {
    	iEndingIndex = endingIndex;
    }

    @SuppressWarnings({ "unused", "resource" })
	public boolean getStartingParms() {
        iConfigHash = new Hashtable();
        for (int i=0; i < configParams.length; i++) {
            iConfigHash.put(configParams[i], "");
        }
        File f = new File(iConfigFileName);
        String path = f.getPath();
        String name = f.getName();
        iParent = f.getParent();
	
        System.out.println("getStartingparams file is "+iConfigFileName+" parent "+iParent);

        if (iParent == null)
        	iParent = "./";
        boolean isabsolute = f.isAbsolute();
        String s = null;
        String s1 = null;
        String s2 = null;
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedReader  br = new BufferedReader(new InputStreamReader(fis));
            while (br.ready()) {
                s = br.readLine();
                println("getStartingParms: " + s);
                s = s.trim();
                if (s.length() == 0)
                	continue;
                if(s.charAt(0) == '#')
                	continue;
                s1 = s.substring(0, s.indexOf(SEP));
                s2 = s.substring(s.indexOf(SEP) + 2);
                System.out.println("getStartingParms: "+s1+"..."+s2);
                if (iConfigHash.containsKey(s1))
                	iConfigHash.put(s1, s2);
            }
        } catch(IOException ioe) {
            System.out.println(HELPMSG + iConfigFileName);
            return false;
        }
        Enumeration e = iConfigHash.keys();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String value = (String)iConfigHash.get(key);
            System.out.println(key + "\t" + value);
        }
        return true;
    }


    public void setStartingParms() {
        iZipFileName = (String)iConfigHash.get(configParams[ZIPFILENAME]);
        File f = new File(iZipFileName);
        // claims wants absolue paths but try with relative if parent undefined for some reason -as

        if (!f.isAbsolute() & iParent!=null) {
            iZipFileName = iParent + System.getProperty("file.separator") + iZipFileName;
		    System.out.println( "if case");
        }
		else{
		    if(!f.isAbsolute()){
				System.out.println("else   case");
				File fxml = new File(iConfigFileName);
				String xmllocation= fxml.getParent();
				xmllocation = xmllocation + System.getProperty("file.separator");
				iZipFileName = xmllocation + iZipFileName;
		    }
		}
	    System.out.println("\nTrying to open zip file in Config.java located at: " + iZipFileName);
	    
	    f = new File(iZipFileName);
	    if (!f.exists()) {
        	if (iZipFileError == null)
        		iZipFileError = new FileError(iZipFileName, ZIP_TYPE);
        	else
        		iZipFileError.setVisible(true);
        }
        
	    // the typical image must be an absolute file path
        String s = (String)iConfigHash.get(configParams[TYPICALIMAGE]);
        
        File imagefile = new File(s);
        if (!imagefile.isAbsolute() & iParent != null) {
        	s = iParent + System.getProperty("file.separator") + s;
        	System.out.println("Image file path not absolute");
        }
        else if (!imagefile.isAbsolute()) {
        	File fxml = new File(iConfigFileName);
        	String xmllocation = fxml.getParent();
        	xmllocation = xmllocation + System.getProperty("file.separator");
        	s = xmllocation + s;
        }
        imagefile = new File(s);
        if (!imagefile.exists()) {
        	if (iImageFileError == null)
        		iImageFileError = new FileError(s, IMAGE_TYPE);
        	else
        		iImageFileError.setVisible(true);
        }

		//typical image decode
		// on top of >0 length I added exists check if not there -as
        if (s.length() > 0 & new File(s).isFile()) {
            decodeTypicalImage(s);
            iTypicalImage = s;
        }else{
        	s="";
	    }
    
        setOptionalParms();
        if (iTifPrefix == null)
        	setOldStyleParms();
    }

    @SuppressWarnings("unused")
	private void decodeTypicalImage(String s) {
    	System.out.println("Decoding typical image in Config.java...: " + s);
        // if a typical image entry is found,
        // this code will extract:
        // tif directory
        // tifPrefix
        // and maybe useZip (but if it is specified explicitly
        // the specified value will override -- used for old style
        // zip where one zip file had all the images -- use zip = 1)
        // otherwise the tif directory and the tifPrefix can be
        // explicitly entered
        //System.out.println("setStartingParms: typical image=" + s);

        // hack for Windows -- maybe
        StringBuffer sb = new StringBuffer();
        for (int i=0; i < s.length(); i++) {
            char x = s.charAt(i);
            if (x != '\\') sb.append(x);
            else {
                sb.append('/');
                //i++;
            }
        }
        s = sb.toString();
        //System.out.println("setStartingParms: typical image=" + s);
        
        File f = new File(s);
        boolean b = f.exists();
        String name = f.getName();
        String parent = f.getParent();
        // hack for Windows -- maybe
        s = parent;
		if (s!=null){
		    sb = new StringBuffer();
		    for (int i=0; i < s.length(); i++) {
				char x = s.charAt(i);
				if (x != '\\')
					sb.append(x);
				else {
				    sb.append('/');
				    //i++;
				}
		    }	
		    s = sb.toString();
		    parent = s;
		}
        int k1 = name.lastIndexOf('.');
        int k2 = -1;
        
        
        
        s = (String)iConfigHash.get(configParams[USESTACK]);
        if (s.length() > 0) {
        	iUseStack = Integer.parseInt(s);
        }
        else {
        	iUseStack = 0;
        }
        
        if (name.lastIndexOf('-') > name.lastIndexOf('_')) {
            // revised 09/29/16 because we want to support datasets with more than 999 time points
            // work backwards and find the index of the 't' that starts the time plane suffix
            boolean foundT = false;
            int idx = k1; // start at the '.' that begins '.tif'
            while (!foundT) {
                if (name.charAt(idx) == 't') {
                    foundT = true;
                    k2 = idx;
                    } else {
                    idx--;
                }
            }
        } else {
            k2 = name.lastIndexOf('_') + 1;

//        	if (iUseStack == 0) {
//	        	//Is this format ever used?
//	        }
//	        else {
//	        	k2 = name.lastIndexOf('_') + 1;
//	        }
        }

        int k3 = parent.lastIndexOf("/");
        String forepath = parent.substring(k3 + 1);
        println("setStartingParms: forepath: " + forepath);
        System.out.println(name + ", " + k2);
        String forename = name.substring(0, k2);
        println("setStartingParms: forename: " + forename);
        
        // For timepoint debug
        String timePlanePart = name.substring(k2, name.length());
        println("setStartingParms: time-plane suffix: " + timePlanePart);
        
        // Starting timepoint by default
        iStartTime = 1;
        // Extract start time from immage name time-plane suffix
        int tIndex = timePlanePart.indexOf("t");
        int hyphenIndex = timePlanePart.indexOf("-");
        if (tIndex != -1 && hyphenIndex != -1 && tIndex+1 < hyphenIndex) {
        	String timeString = timePlanePart.substring(tIndex+1, hyphenIndex);
        	iStartTime = 0;
        	int place = 1;
        	for (int i = timeString.length(); i > 0; i--) {
        		try {
        			int digit = Integer.parseInt(timeString.substring(i-1, i));
        			iStartTime += place*digit;
        			place *= 10;
        		} catch (NumberFormatException nfe) {
        			// If there is any trouble reading the digital, default the start time back to 1
        			iStartTime = 1;
        			break;
        		}
        	}
        }
        
        String prefix = forepath + "/" + forename;
        String tifDir = parent.substring(0, k3);
        println("setStartingParms: " + k1 + CS + k2 + CS + k3);
        println("setStartingParms: tif directory=" + tifDir);
        println("setStartingParms: prefix=" + prefix);

        String ext = name.substring(k1 + 1);
        int useZip = 0;
        if (ext.equals("zip")) useZip = 2;

        //System.out.println("setStartingParms: useZip=" + useZip);

        iUseZip = useZip;
        iTifPrefix = prefix;
        iZipTifFilePath = tifDir;
    }
    
    public int getStartTime() {
    	return iStartTime;
    }

    private void setOptionalParms() {
        iStartingIndex = 1;
        iEndingIndex = 1;
        String s = (String)iConfigHash.get(configParams[ENDINGINDEX]);
        if (s.length() > 0)
        	iEndingIndex = Integer.parseInt(s);

        s = (String)iConfigHash.get(configParams[STARTINGINDEX]);
        if (s.length() > 0)
        	iStartingIndex = Integer.parseInt(s);

        iAxisGiven = (String)iConfigHash.get(configParams[AXISGIVEN]);

        // handle defaulted parameters
        s = (String)iConfigHash.get(configParams[USEZIP]);
        if (s.length() > 0)
        	iUseZip = Integer.parseInt(s);

        s = (String)iConfigHash.get(configParams[USESTACK]);
        if (s.length() > 0) {
        	iUseStack = Integer.parseInt(s);
        }
        else {
        	iUseStack = 0;
        }
        
        s = (String)iConfigHash.get(configParams[SPLITCHANNELIMAGE]);
        System.out.println("Got from iConfigHash: "+s);
        if (s.length() > 0)
        	iSplitChannelImage = Integer.parseInt(s);

        iNamingMethod = cDefaultNaming; //Identity3.NEWCANONICAL;
        s = (String)iConfigHash.get(configParams[NAMINGMETHOD]);
        if (s.length() > 0) {
            //System.out.println("setOptionalParms found NAMINGMETHOD: " + s);
            if (s.equals("STANDARD")) iNamingMethod = Identity3.STANDARD;
            else if (s.equals("MANUAL")) iNamingMethod = Identity3.MANUAL;
            else if (s.equals("NEWCANONICAL")) iNamingMethod = Identity3.NEWCANONICAL;
        }

        s = (String)iConfigHash.get(configParams[POLARSIZE]);
        if (s.length() > 0) {
            iPolar_size = Integer.parseInt(s);
        }

        s = (String)iConfigHash.get(configParams[XYRES]);
        if (s.length() > 0) {
            iXy_res = Float.parseFloat(s);
        }

        s = (String)iConfigHash.get(configParams[ZRES]);
        if (s.length() > 0) {
            iZ_res = Float.parseFloat(s);
        }

        s = (String)iConfigHash.get(configParams[PLANEEND]);
        if (s.length() > 0) {
            iPlaneEnd = Integer.parseInt(s);
        }

        s = (String)iConfigHash.get(configParams[EXPRCORR]);
        if (s.length() > 0) {
            iExprCorr = s;
        }

        s = (String)iConfigHash.get(configParams[SPLIT]);
        if (s.length() > 0) {
            iSplit = Integer.parseInt(s);
            if (iSplit == 0) {
                iUseStack = 1; // just in case it wasn't picked up elsewhere
            }
        } else {
            iSplit = 1;
        }

    }

    private void setOldStyleParms() {
        iZipTifFilePath = (String)iConfigHash.get(configParams[TIFDIRECTORY]);
        File f = new File(iZipTifFilePath);
        if (!f.isAbsolute()) {
            iZipTifFilePath = iParent + "/" + iZipTifFilePath;
        }
        //println("setOldStyleParms: " + iZipTifFilePath);


        iTifPrefix = (String)iConfigHash.get(configParams[TIFPREFIX]);

        if (iTypicalImage.length() == 0) {
            String s = iZipTifFilePath + "/" + iTifPrefix + "t001-p01.";
            if (iUseZip <= 1) s += "tif";
            else if (iUseZip == 2) s += "zip";
            //println("Config.getOldStyleParms: creating typical image name: "  + s);
            iTypicalImage = s;
        }


    }

    public void saveConfigXMLFile() {
        JFileChooser fileChooser = new JFileChooser(); //new JFileChooser(".");
        String s = iConfigFileName;
        String ss = new File(s).getParent();
        fileChooser.setCurrentDirectory(new File(ss));
        fileChooser.setSelectedFile(new File(""));
        int returnVal = fileChooser.showSaveDialog(null);

        if (returnVal != JFileChooser.APPROVE_OPTION) {

            System.out.println("Save command cancelled by user.");
            return;
        }
        File file = fileChooser.getSelectedFile();
        writeXMLConfig(file);
    }

    public void writeXMLConfig(File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);
            pw.println(BEGIN);

            pw.println("");
            pw.println(EMBRYO);
            pw.println(TYPICALIMAGENAME + iTypicalImage + END);
            pw.println(NUCLEI + iZipFileName + END);
            if (iNamingMethod != Identity3.NEWCANONICAL) {
                String namex = "";
                if (iNamingMethod == Identity3.MANUAL) namex = "MANUAL";
                else if (iNamingMethod == Identity3.STANDARD) namex = "STANDARD";
                pw.println(NAMING + namex + END);
            }
            if (iEndingIndex != 9999) {
                pw.println(ENDING + iEndingIndex + END);
            }
            if (iAxisGiven.length() > 0 && iNamingMethod == Identity3.NEWCANONICAL) {
                pw.println(AXIS + iAxisGiven + END);
            }
            if (Math.abs(iPolar_size - POLARSIZENOMINAL) > 0) {
                pw.println(POLAR + iPolar_size + END);
            }
            boolean needXyres = Math.abs(iXy_res - XYRESNOMINAL) > MARGIN;
            boolean needZres = Math.abs(iZ_res - ZRESNOMINAL) > MARGIN;
            boolean needPlaneEnd = (iPlaneEnd - PLANEENDNOMINAL) != 0;
            if (needXyres || needZres || needPlaneEnd) {
                pw.println(RESOLUTION + iXy_res + ZRESXML + iZ_res + PLANEENDXML + iPlaneEnd + END);
            }

            if (!iExprCorr.equals("none")) {
                pw.println(EXPR_CORR + iExprCorr + END);
            }
            
            if (iSplitChannelImage != 1)
            	pw.println("SPLIT_CHANNEL_IMAGE + 0 + END");

            pw.println(ENDEMBRYO);
            pw.close();

        } catch(FileNotFoundException fnfe) {
            System.out.println("File not found for Config.writeXMLConfig(File)");
        }



    }

    public static final String
     SP = " "
    ,EMBRYO = "<embryo>"
    ,ENDEMBRYO = "</embryo>"
    ,TYPICALIMAGENAME = "<image file=\""
    ,NUCLEI = "<nuclei file=\""
    ,NAMING = "<naming method=\""
    ,STARTING = "<start index=\""
    ,ENDING = "<end index=\""
    ,AXIS = "<axis axis=\""
    ,END = "\"/>"
    ,BEGIN = "<?xml version='1.0' encoding='utf-8'?>"
    ,POLAR = "<polar size=\""
    ,RESOLUTION = "<resolution xyRes=\""
    ,ZRESXML = "\" zRes=\""
    ,PLANEENDXML = "\" planeEnd=\""
    ,EXPR_CORR = "<exprCorr type=\""
    ,USE_ZIP = "<useZip type=\""
	,USE_STACK = "<useStack type=\""
	,i16BITSPLIT = "<Split SplitMode=\""
    ;

    private void showStartingParms() {
        System.out.println("showStartingParms start");
        System.out.println("iZipFileName: " + iZipFileName);
        //System.out.println("iZipNucDir: " + iZipNucDir);
        System.out.println("iZipTifFilePath: " + iZipTifFilePath);
        System.out.println("iTifPrefix: " + iTifPrefix);
        System.out.println("iStartingIndex: " + iStartingIndex);
        System.out.println("iEndingIndex: " + iEndingIndex);
        System.out.println("iNamingMethod: " + iNamingMethod);
        System.out.println("iUseZip: " + iUseZip);
        System.out.println("iUseStack: " + iUseStack);
        System.out.println("iStartTime: " + iStartTime);
        System.out.println("iSplitChannelImage: " + iSplitChannelImage);
        System.out.println("iSplit: " + iSplit);

        System.out.println("showStartingParms end");
    }


    final public static String
         HELPMSG = "you must provide file: "
        ,SEP = ", "
        ,ROOTNAME = "P"
        ;

    public static final String [] configParams = {
            "zipFileName"
           ,"tif directory"
           ,"tifPrefix"
           ,"nuclei directory"
           ,"root name"
           ,"starting index"
           ,"ending index"
           ,"use zip"
           ,"namingMethod"
           ,"axis"
           ,"typical image"
           ,"polarSize"
           ,"xyRes"
           ,"zRes"
           ,"planeEnd"
           ,"exprCorr"
           ,"use stack"
           ,"angle"
           ,"center"
           ,"x"
           ,"y"
           ,"splitChannelImage"
            ,"splitMode"
    };

    private static final int
         ZIPFILENAME = 0
        ,TIFDIRECTORY = 1
        ,TIFPREFIX = 2
        ,NUCLEIDIRECTORY = 3
        ,ROOTNAMEI = 4
        ,STARTINGINDEX = 5
        ,ENDINGINDEX = 6
        ,USEZIP = 7
        ,NAMINGMETHOD = 8
        ,AXISGIVEN = 9
        ,TYPICALIMAGE = 10
        ,POLARSIZE = 11
        ,XYRES = 12
        ,ZRES = 13
        ,PLANEEND = 14
        ,EXPRCORR = 15
		,USESTACK = 16
		,SPLITCHANNELIMAGE = 21
        ,SPLIT = 22;

    public static final float
         XYRESNOMINAL = .09f
        ,ZRESNOMINAL = 1
        ,MARGIN = .001f
        ;

    public static final int
         POLARSIZENOMINAL = 45
         ,PLANESTARTNOMINAL = 1
         ,PLANEENDNOMINAL = 50
        ;

    // 20080304 adding background correction methods to config
    public static final String
         NONE = "none"
        ,GLOBAL = "global"
        ,LOCAL = "local"
        ,BLOT = "blot"
        ,CROSS = "cross"
        ;

    public static final int
	  STANDARD = 2
	 ,MANUAL = 2
	 ,NEWCANONICAL = 3
	 ;
    
    private static final int
    	IMAGE_TYPE = 1,
    	ZIP_TYPE = 2;

    public static int	cDefaultNaming = NEWCANONICAL;


    public int getRedChoiceNumber() {
        int i = 0;
        for (i=0; i < REDCHOICE.length; i++) {
            if (iExprCorr.equals(REDCHOICE[i])) break;
        }

        return i;
    }

    public static final String [] REDCHOICE = {
        "none", "global", "local", "blot", "cross"
    };

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Config");
        sb.append(NL +  "iConfigFileName" + CS + iConfigFileName);
        sb.append(NL + "iParent" + CS + iParent);
        sb.append(NL + "iZipFileName" + CS + iZipFileName);
        sb.append(NL + "iZipNucDir" + CS + iZipNucDir);
        sb.append(NL + "iTypicalImage" + CS + iTypicalImage);
        sb.append(NL + "iZipTifFilePath" + CS + iZipTifFilePath);
        sb.append(NL + "iTifPrefix" + CS + iTifPrefix);
        sb.append(NL + "iStartingIndex" + CS + iStartingIndex);
        sb.append(NL + "iEndingIndex" + CS + iEndingIndex);
        sb.append(NL + "iPlaneStart" + CS + iPlaneStart);
        sb.append(NL + "iPlaneEnd" + CS + iPlaneEnd);
        sb.append(NL + "iNamingMethod" + CS + iNamingMethod);
        sb.append(NL + "iUseZip" + CS + iUseZip);
        sb.append(NL + "iUseStack" + CS + iUseStack);
        sb.append(NL + "iAxisGiven" + CS + iAxisGiven);
        sb.append(NL + "iSplitChannelImage" + CS + iSplitChannelImage);

        return sb.toString();
    }

    public static void main(String [] args) {
        String s = "/nfs/waterston/murray/20090425_ceh-34_4_L1/dats/20090425_ceh-34_4_L1.dat";
        //s = "/nfs/waterston1/annots/bao/081505/dats/081505.xml";
        s = "/nfs/waterston1/annots/zhao/20090317PHA-4_AF16_L1/dats/20090317PHA-4_AF16_L1.xml";
        s = "C:/biowolp/0tmp/acewintest/20090826/AceTreeDemo/081505/dats/081505.xml";
        Config c = new Config(s, true);//,null);
        println("main, " + c);
        //Config cx = Config.createConfigFromXMLFile(s);
        //println("main, " + cx);
    }
    private static void println(String s) {System.out.println(s);}
    private static final String CS = ", ", NL = "\n";
}