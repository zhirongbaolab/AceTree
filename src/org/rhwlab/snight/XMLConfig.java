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
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class XMLConfig implements DocHandler {

    static Config   iConfig;
    static String   iConfigFileName;
    String   iZipFile;
    String   iImageFile;
    String   iEndIndex;

    boolean pathRefigured;

    public static Config createConfigFromXMLFile(String fileName) {
        iConfigFileName = fileName;
        //println("XMLConfig: reading config file: " + iConfigFileName);
        new XMLConfig(fileName, null);
        // NB Config.setStartingParms was just called
        return iConfig;
    }

    public XMLConfig(String fileName) {
        try {
            FileReader fr = new FileReader(fileName);
            QDParser.parse(this, fr);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public XMLConfig(String fileName, Config config) {
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




    public static void main(String[] args) {
    }

    /* (non-Javadoc)
     * @see qdxml.DocHandler#startElement(java.lang.String, java.util.Hashtable)
     */
    @Override
	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	public void startElement(String tag, Hashtable h) throws Exception {
        pathRefigured = false;
        if (tag.equals("embryo")) {
            if (iConfig == null) {
            	iConfig = new Config(iConfigFileName, true);
            }

            // THIS IS EQUIVALENT TO THE SET STARTING PARMS METHOD IN CONFIG.JAVA IS CALLED ON A NON-XML DATASET
            iConfig.iConfigHash = new Hashtable<String, String>();
            for (int i=0; i < Config.configParams.length; i++) {
                iConfig.iConfigHash.put(Config.configParams[i], "");
            }
        } else if (tag.equals("nuclei")) {
            String file = (String)h.get("file");
            iConfig.iConfigHash.put("zipFileName", file);
        } else if (tag.equals("image")) {
            String typical = (String)h.get("file");
            
            System.out.println("");
            
            //check if the file exists
            if(!new File(typical).exists()) {
                typical = reconfigureImagePath(typical);
                pathRefigured = true;
            }
            System.out.println("Trying to configure image file: " + typical);
            
            iConfig.iConfigHash.put("typical image", typical);
        } else if (tag.equals("start")) {
            String startIndex = (String)h.get("index");
            iConfig.iConfigHash.put("starting index", startIndex);
        } else if (tag.equals("end")) {
            String endIndex = (String)h.get("index");
            iConfig.iConfigHash.put("ending index", endIndex);
        } else if (tag.equals("naming")) {
            String method = (String)h.get("method");
            iConfig.iConfigHash.put("namingMethod", method);
        } else if (tag.equals("axis")) {
            String axis = (String)h.get("axis");
            iConfig.iConfigHash.put("axis", axis);
        } else if (tag.equals("polar")) {
            String size = (String)h.get("size");
            iConfig.iConfigHash.put("polarSize", size);
//            println("startElement: " + size);
        } else if (tag.equals("resolution")) {
            String xyRes = (String)h.get("xyRes");
            iConfig.iConfigHash.put("xyRes", xyRes);
            String zRes = (String)h.get("zRes");
            iConfig.iConfigHash.put("zRes", zRes);
            String planeEnd = (String)h.get("planeEnd");
            iConfig.iConfigHash.put("planeEnd", planeEnd);
        } else if (tag.equals("exprCorr")) {
            String exprCorr = (String)h.get("type");
            iConfig.iConfigHash.put("exprCorr", exprCorr);
        } else if (tag.equals("useZip")) {
            String useZip = (String)h.get("type");
            iConfig.iConfigHash.put("use zip", useZip);
        } else if (tag.equals("useStack")) {
        	String useStack = (String)h.get("type");
            iConfig.iConfigHash.put("use stack", useStack);   
        } else if (tag.equals("splitChannelImage")) {
        	String splitChannelImage = (String)h.get("type");
        	iConfig.iConfigHash.put("splitChannelImage", splitChannelImage);
        } else if (tag.equals("angle")) {
        	String degrees = (String)h.get("degrees");
        	iConfig.iConfigHash.put("angle", degrees);
        } else if (tag.equals("center")) {
        	String x = (String)h.get("x");
        	String y = (String)h.get("y");
        	iConfig.iConfigHash.put("x", x);
        	iConfig.iConfigHash.put("y", y);
        } else if (tag.equals("Split")) { // TODO: pavak case has 16 bit and no 8, wants to use SplitMode to say don't split. Si0 case is has 8, 16, wants to use SplitMode so needs recongifuring
            String splitMode = (String)h.get("SplitMode");
            System.out.println("THE SPLIT MODE IS: " + splitMode);
            iConfig.iConfigHash.put("splitMode", splitMode);
            if (!pathRefigured && (!new File((String)iConfig.iConfigHash.get("typical image")).exists()))
                iConfig.iConfigHash.put("typical image", reconfigureImagePath(iConfig.iConfigHash.get("typical image")));
        }
    }

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