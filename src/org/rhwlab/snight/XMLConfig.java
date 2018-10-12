/*
 * Created on May 15, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.snight;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Hashtable;

import qdxml.DocHandler;
import qdxml.QDParser;

/**
 * Revised 10/2018
 * @author Braden Katzman
 *
 * This class, in its revised form, functions primarily as a loader for .xml configuration files by populating
 * a local hashtable with the tag data in an .xml file and returning this to Config.java so that it can
 * then be organized into Nuclei (i.e. .zip) specific parameters and Image (i.e. the image series) specific parameters
 */
public class XMLConfig implements DocHandler {

    // TODO - we'll get rid of these when the new pipeline is working
    static Config   iConfig;
    static String   iConfigFileName;
    String   iZipFile;
    String   iImageFile;
    String   iEndIndex;
    boolean pathRefigured;

    private Hashtable<String, String> xmlConfigData;

    // non param constructor called by Config constructor with String parameter
    public XMLConfig() {
        this.xmlConfigData = new Hashtable();
    }

    public Hashtable<String, String> loadConfigDataFromXMLFile(String filename) {
        this.xmlConfigData = new Hashtable<>();

        // populate the xmlConfigData hashtable but using QDParser
        try {
            // try opening the file
            FileReader fr = new FileReader(filename);
            QDParser.parse(this, fr);

            // if we reach here, the file is valid and opened. Let's parse it with QDParser
        } catch (FileNotFoundException fnfe) {
            System.out.println("Error reading .xml file: " + filename + " in XMLConfig.loadConfigDataFromXMLFile()");
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return xmlConfigData;
    }

    public static Config createConfigFromXMLFile(String fileName) {
        iConfigFileName = fileName;
        //println("XMLConfig: reading config file: " + iConfigFileName);
        new XMLConfig(fileName, null);
        // NB Config.setStartingParms was just called
        return iConfig;
    }

    public XMLConfig(String fileName, Config config) {
    	xmlConfigData = new Hashtable<>();
        iConfig = config;
        try {
            FileReader fr = new FileReader(fileName);
            QDParser.parse(this, fr);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see qdxml.DocHandler#startElement(java.lang.String, java.util.Hashtable)
     */
    @Override
	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public void startElement(String tag, Hashtable h) throws Exception {
        pathRefigured = false;
        if (tag.equals("embryo")) {
            // nothing for revised version on this tag

            // ***********************************************
            // FOR EACH OF THESE IF CONDITIONS, THERE IS THE NEW REVISED HANDLER AND THE PREVIOUS, SEPARATED BY A CHUNK OF WHITESPACE and a comment of asterisks
            if (iConfig == null) {
            	iConfig = new Config(iConfigFileName, true);
            }

            // THIS IS EQUIVALENT TO THE SET STARTING PARMS METHOD IN CONFIG.JAVA IS CALLED ON A NON-XML DATASET
            iConfig.iConfigHash = new Hashtable<String, String>();
            for (int i=0; i < Config.configParams.length; i++) {
                iConfig.iConfigHash.put(Config.configParams[i], "");
            }
        } else if (tag.equals("nuclei")) {
            // save the zip file name
            String f = (String)h.get("file");
            this.xmlConfigData.put("zipFileName", f);

            // **********************************************

            String file = (String)h.get("file");
            iConfig.iConfigHash.put("zipFileName", file);
        } else if (tag.equals("image")) {
            // get the image file name and put it in the hash (previously, there was processing on this name before adding)
            // but for tidyness sake, that should come later
            String imageFileName = (String)h.get("file");
            this.xmlConfigData.put("imageFileName", imageFileName);

            // TODO - when specifying two channels in the image tag, add the support here (see resolution tag below for example)

            // **********************************************

            String typical = (String)h.get("file");
            System.out.println("");
            
            //check if the file exists
            if(!new File(typical).exists()) {
                typical = reconfigureImagePath(typical);
                pathRefigured = true;
                System.out.println("Reconfigured Image Path in XMLConfig.java is:" + typical);
            }

            iConfig.iConfigHash.put("typical image", typical);
        } else if (tag.equals("start")) {
            String startIdx = (String)h.get("index");
            this.xmlConfigData.put("startIdx", startIdx);

            // **********************************************

            String startIndex = (String)h.get("index");
            iConfig.iConfigHash.put("starting index", startIndex);
        } else if (tag.equals("end")) {
            String endIdx = (String)h.get("index");
            this.xmlConfigData.put("endIdx", endIdx);

            // **********************************************

            String endIndex = (String)h.get("index");
            iConfig.iConfigHash.put("ending index", endIndex);
        } else if (tag.equals("naming")) {
            String namingMethod = (String)h.get("method");
            this.xmlConfigData.put("namingMethod", namingMethod);

            // **********************************************

            String method = (String)h.get("method");
            iConfig.iConfigHash.put("namingMethod", method);
        } else if (tag.equals("axis")) {
            String axis = (String)h.get("axis");
            this.xmlConfigData.put("axis", axis);

            // **********************************************

            iConfig.iConfigHash.put("axis", axis);
        } else if (tag.equals("polar")) {
            String polarSize = (String)h.get("size");
            this.xmlConfigData.put("polarSize", polarSize);

            // **********************************************

            iConfig.iConfigHash.put("polarSize", polarSize);
        } else if (tag.equals("resolution")) {
            String xyRes = (String)h.get("xyRes");
            String zRes = (String)h.get("zRes");
            String planeEnd = (String)h.get("planeEnd");

            this.xmlConfigData.put("xyRes", xyRes);
            this.xmlConfigData.put("zRes", zRes);
            this.xmlConfigData.put("planeEnd", planeEnd);

            // **********************************************

            iConfig.iConfigHash.put("xyRes", xyRes);
            iConfig.iConfigHash.put("zRes", zRes);
            iConfig.iConfigHash.put("planeEnd", planeEnd);
        } else if (tag.equals("exprCorr")) {
            String exprCorr = (String)h.get("type");

            this.xmlConfigData.put("exprCorr", exprCorr);
            // **********************************************

            iConfig.iConfigHash.put("exprCorr", exprCorr);
        } else if (tag.equals("useZip")) {
            String useZip = (String)h.get("type");
            this.xmlConfigData.put("useZip", useZip);

            // **********************************************

            iConfig.iConfigHash.put("use zip", useZip);
        } else if (tag.equals("useStack")) {
        	String useStack = (String)h.get("type");

        	this.xmlConfigData.put("useStack", useStack);
            // **********************************************

            iConfig.iConfigHash.put("use stack", useStack);   
        } else if (tag.equals("splitChannelImage")) {
            // TODO - redundant, only need the Split flag below
        	String splitChannelImage = (String)h.get("type");

        	this.xmlConfigData.put("splitChannelImage", splitChannelImage);
            // **********************************************

        	iConfig.iConfigHash.put("splitChannelImage", splitChannelImage);
        } else if (tag.equals("angle")) {
        	String degrees = (String)h.get("degrees");
            this.xmlConfigData.put("angle", degrees);

            // **********************************************

        	iConfig.iConfigHash.put("angle", degrees);
        } else if (tag.equals("center")) {
        	String x = (String)h.get("x");
        	String y = (String)h.get("y");

        	this.xmlConfigData.put("x", x);
        	this.xmlConfigData.put("y", y);

            // **********************************************

        	iConfig.iConfigHash.put("x", x);
        	iConfig.iConfigHash.put("y", y);
        } else if (tag.equals("Split")) { // TODO: pavak case has 16 bit and no 8, wants to use SplitMode to say don't split. Si0 case is has 8, 16, wants to use SplitMode so needs recongifuring
            String splitMode = (String)h.get("SplitMode");

            this.xmlConfigData.put("split", splitMode);
            // **********************************************

            // TODO - this will end up happening later on, not appropriate for it to be done in the parser
            System.out.println("THE SPLIT MODE IS: " + splitMode);
            iConfig.iConfigHash.put("splitMode", splitMode);
            if (!pathRefigured && (!new File((String)iConfig.iConfigHash.get("typical image")).exists()))
                iConfig.iConfigHash.put("typical image", reconfigureImagePath(iConfig.iConfigHash.get("typical image")));
        }
    }

    public Hashtable<String, String> getXMLConfigDataHash() { return this.xmlConfigData; }

    /**
     * Update the image path from the 8bit one listed in the .xml file
     * to a 16bit one. This is used in the case of 8bit images not existing,
     * or explicit need to use 16bit images in the presence of both types
     *
     * @return
     */
    private String reconfigureImagePath(Object t_) {
        System.out.println("Reconfiguring Image Path in XMLConfig.java");
        // look for 16 bit .TIFF files
        String typical = (String)t_;

        if (!new File(typical).exists()) {
            System.out.println(typical + " doesn't exist on the system. Building new file path for layered images...");
        }

        if (iConfig.iConfigHash.get("splitMode").equals("0")) {
            System.out.println(typical + " being updated to 16bit images to support non-split mode");
        }


        //try using layered images two directories up
        int fileNameIdx = typical.lastIndexOf('/');
        String fileName = typical.substring(fileNameIdx+1);
        int planeIdx = fileName.indexOf("-p");

        String fileNameNoPlane = fileName.substring(0, planeIdx - 1);

        int extIdx = fileName.lastIndexOf('.');
        String ext = fileName.substring(extIdx);
        ext = ext.toUpperCase();

        int lastDashIdx = fileNameNoPlane.lastIndexOf('-');
        String filePrefix = fileNameNoPlane.substring(0, lastDashIdx) + '_';

        int tIdx = fileName.indexOf("-t");
        if (fileName.charAt(tIdx+2) == '0') {
            tIdx+=2;

            while(fileName.charAt(tIdx) == '0') {
                tIdx++;
            }
        }

        String t = 't' + fileName.substring(tIdx, planeIdx);

        String fileNameUpdate = filePrefix + t + ext;

//            	System.out.println("NEW FILE NAME: " + fileNameUpdate);

        int removeDirsIdx = typical.indexOf("image/tif");
        if (removeDirsIdx > 0) {
            String filePre = typical.substring(0, removeDirsIdx);

            String finalPath = filePre + fileNameUpdate;

//                	System.out.println("Typical: " + typical);
            typical = finalPath;
//                	System.out.println("Typical became: " + typical);

            String useStack = "1";
            iConfig.iConfigHash.put("use stack", useStack);
        }

        return typical;
    }

    /* (non-Javadoc)
     * @see qdxml.DocHandler#endElement(java.lang.String)
     */
    @Override
	public void endElement(String tag) throws Exception {
        //println("endElement: " + tag);

    }

    /* (non-Javadoc)
     * @see qdxml.DocHandler#startDocument()
     */
    @Override
	public void startDocument() throws Exception {
        //println("startDocument: ");

    }

    /* (non-Javadoc)
     * @see qdxml.DocHandler#endDocument()
     */
    @Override
	public void endDocument() throws Exception {
        //println("endDocument: ");
        iConfig.setStartingParms();
    }

    /* (non-Javadoc)
     * @see qdxml.DocHandler#text(java.lang.String)
     */
    @Override
	public void text(String str) throws Exception {
        //println("text: " + str);

    }
    public static void println(String s) {System.out.println(s);}
    public static final String CS = ", ";

}