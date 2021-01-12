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
            System.out.println("saved nuclei zipFileName: " + f);

            // **********************************************

            String file = (String)h.get("file");
            iConfig.iConfigHash.put("zipFileName", file);
        } else if (tag.equals("image")) {
            /** there are two options for the image tag following the revisions in 10/2018
             * 1. Legacy image defintion support: <image file="" />
             * 2. Multi channel defintion: <image numChannels="" channel1="" channel2="" ... channelN="" />*/
            // get the image file name and put it in the hash (previously, there was processing on this name before adding)
            // but for tidyness sake, that should come later
            if (h.keySet().contains("file")) {
                String imageFileName = (String)h.get("file");
                this.xmlConfigData.put("imageFileName", imageFileName);
            } else if (h.keySet().contains("numChannels")) {
                String numChannels = (String)h.get("numChannels");
                this.xmlConfigData.put("numChannels", numChannels);
                for (int i = 1; i <= Integer.parseInt(numChannels); i++) {
                    String tagID = "channel" + Integer.toString(i);
                    if (h.keySet().contains(tagID)) {
                        this.xmlConfigData.put(tagID, (String)h.get(tagID));
                    }
                }
            }


            // **********************************************

//            String typical = (String)h.get("file");
//            System.out.println("");
//
//
//            iConfig.iConfigHash.put("typical image", typical);
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
            iConfig.iConfigHash.put("endIdx", endIndex);
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
        } else if (tag.equals("Split")) {
            String splitMode = (String)h.get("SplitMode");
            this.xmlConfigData.put("split", splitMode);
        } else if (tag.equals("Flip")) {
            String flipMode = (String)h.get("FlipMode");
            this.xmlConfigData.put("flip", flipMode);
        } else if (tag.equals("TimePrefix")) {
            String prefix = (String)h.get("Prefix");
            this.xmlConfigData.put("TimePrefix", prefix);
        }
    }

    public Hashtable<String, String> getXMLConfigDataHash() { return this.xmlConfigData; }

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
        //iConfig.setStartingParms();
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