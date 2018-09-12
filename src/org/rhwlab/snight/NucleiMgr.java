/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 * Created Feb 4, 2005
 */
package org.rhwlab.snight;

import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;


//import org.rhwlab.acetree.*;
//import org.rhwlab.acetree.AceTree;
import org.rhwlab.acetree.NucUtils;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.nucedit.EditLog;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.utils.C;
import org.rhwlab.utils.EUtils;
import org.rhwlab.utils.Log;

/**
 * maintains a structure with all nuclei in the experiment
 * <br>handles naming of nuclei completely using code closely
 * derived from StarryNight
 * <br>provision for reading nuclei files into the structure
 * and for writing files out of the structure
 *
 * @author biowolp
 * @version 1.0
 */
public class NucleiMgr {
    ////   AceTree 		iAceTree;
    ZipNuclei 		iZipNuclei;
    File 					iFile;
    FileOutputStream 		iFOS;
    PrintWriter 			iPWriter;
    Vector<Vector<Nucleus>> nuclei_record;
    Vector<Vector<Nucleus>> nuclei_record_backup;
    Parameters 				Parametersx;
    String 					iParameterEntry;
    int 					iStartingIndex;
    int 					iEndingIndex;
    int 					iImageWidth;
    int 					iImageHeight;
    int 					iIndexOffset;
    int 					iNumNucleiFiles;
    String [] parameterFileData;
    boolean 				iFakeNuclei;
    boolean 		iDebug;

    //private Identity  iIdentity;
    Identity3  		iIdentity;

    AncesTree 		iAncesTree;
    Config  		iConfig;
    MeasureCSV		iMeasureCSV;
    Parameters 		iParameters;
    Movie 			iMovie;
    EditLog     	iEditLog;
    Log         	iDDLog;
    Log         	iDLog;
    boolean     	iEditLogInitialized;
    double      	iZPixRes;


    int         	iPlaneEnd;
    int         	iPlaneStart;
    int         	iLastNucleiFile;

    public  boolean     	iGoodNucleiMgr;
    
    public int iUseStack;
    public int iSplit;

    PrintWriter				iPrintWriter;
    
    int				iStartTime;

    static String p2 = "t";
    static String p3 = "-nuclei";

    
    public NucleiMgr() {

    }
    

    // Method doesn't seem to be used anywhere
    /*
    public void setNucleiRecord(Vector nr) {
    	nuclei_record = nr;
    }
    */


    // Timing commented out -was used for optimization
    @SuppressWarnings("unused")
	public NucleiMgr(String configFileName) {
    	iUseStack = 0;
    	iSplit = 1;
        //System.out.println("NucleiMgr(" + configFileName + ")" + CS + DBAccess.cDBLocation);
        //iAceTree = AceTree.getAceTree(null);
        iEditLog = new EditLog("EditLog");

        int k2 = configFileName.lastIndexOf(".");

        /*
         * Revised 7/19/17 by @author Braden Katzman
         * 
         * With the addition of a second AuxInfo file structure, MeasureCSV.java finishes the
         * creation of the AuxInfo file path by appending AuxInfo.csv or AuxInfo_v2.csv
         * in order to handle both forms of file
         */
        String measureCSVpath = configFileName.substring(0, k2);
        iMeasureCSV = new MeasureCSV(measureCSVpath);
        println(iMeasureCSV.toString());
        String s2 = configFileName.substring(k2 + 1);
        if (s2.equals("xml")) {
            iConfig = Config.createConfigFromXMLFile(configFileName);
        } else {
            iConfig = new Config(configFileName);
        }

        
        iStartingIndex = iConfig.iStartingIndex;
        iEndingIndex = iConfig.iEndingIndex;
        String zipPath = iConfig.iZipFileName;
        //println("NucleiMgr, zipPath=" + zipPath);
        String nucleiDir = iConfig.iZipNucDir + "/"; //"nuclei/";
        //println("NucleiMgr, nucleiDir=" + nucleiDir);
        
        // set use stack flag
        iUseStack = iConfig.iUseStack;
        iSplit = iConfig.iSplit;
        
        // Set start time
        iStartTime = iConfig.getStartTime();

        // parse out the core of the iTifPrefix
        int k = iConfig.iTifPrefix.lastIndexOf("/");
        String s = iConfig.iTifPrefix.substring(k+1);
        //System.out.println("parameter location: " + s);
        setParameterEntry(s);

        iLastNucleiFile = 0;
        // see if this is an image viewing run only
        int m = zipPath.lastIndexOf("NULL");
        //int m = zipPath.lastIndexOf("null");
        m = zipPath.length() - m;
        if (m == 4) {
	    System.out.println(" Direct opening of images is no longer supported");
	    /*
	    //removed this ability in cleaning dependence on  no longer supported from gui -AS 2/14/2013
            fakeNuclei();
            iParameters = fakeParameters(iConfig.iZipTifFilePath, iConfig.iTifPrefix);
            getScopeParameters();
            findImageParameters();
            iGoodNucleiMgr = true;
	    */
	    iGoodNucleiMgr=false;
        } else {
            // the normal case where we have zipped nuclei to use
            //iParameters = readParameterInfo(zipPath);
            iParameters = dummyParameters();
            iZipNuclei = new ZipNuclei(zipPath);
            if (iZipNuclei.iZipFile != null) {
                //20060719 readEditLog(iEditLog);
            	long timeStart = System.nanoTime();
                readNuclei();
                long timeEnd = System.nanoTime();
                double timeDiff = (timeEnd-timeStart)/1e6;
                System.out.println("Time to read nuclei: "+timeDiff+" ms.");
                getScopeParameters();
                findImageParameters();
                iGoodNucleiMgr = true;
            }
        }
        computeRWeights();
        System.gc();
    }

    public NucleiMgr(Config config, PrintWriter printWriter) {
    	this(config);
    	iPrintWriter = printWriter;
    }

    // Timing commented out -was used for optimization
    @SuppressWarnings("unused")
	public NucleiMgr(Config config) {
        //System.out.println("NucleiMgr(" + configFileName + ")" + CS + DBAccess.cDBLocation);
        ////iAceTree = AceTree.getAceTree(null);
        iEditLog = new EditLog("EditLog");
        String configFileName = config.iConfigFileName;
        int k2 = configFileName.lastIndexOf(".");

        String measureCSVpath = configFileName.substring(0, k2);
        iMeasureCSV = new MeasureCSV(measureCSVpath);

        iConfig = config;
        
        iStartingIndex = iConfig.iStartingIndex;
        iEndingIndex = iConfig.iEndingIndex;
        String zipPath = iConfig.iZipFileName;
        //println("NucleiMgr, zipPath=" + zipPath);
        String nucleiDir = iConfig.iZipNucDir + "/"; //"nuclei/";
        //println("NucleiMgr, nucleiDir=" + nucleiDir);
        
        // Set use stack flag
        iUseStack = iConfig.iUseStack;
        iSplit = iConfig.iSplit;

        // parse out the core of the iTifPrefix
        int k = iConfig.iTifPrefix.lastIndexOf("/");
        String s = iConfig.iTifPrefix.substring(k+1);
        //System.out.println("parameter location: " + s);
        setParameterEntry(s);

        iLastNucleiFile = 0;
        // see if this is an image viewing run only
        int m = zipPath.lastIndexOf("NULL");
        m = zipPath.length() - m;
        if (m == 4) {
	    /*
			// no longer supported to remove dependency on Acetree as 2/14/2013
            fakeNuclei();
            iParameters = fakeParameters(iConfig.iZipTifFilePath, iConfig.iTifPrefix);
            getScopeParameters();
            findImageParameters();
            iGoodNucleiMgr = true;
	    */
        	iGoodNucleiMgr = false;
        } else {
            // the normal case where we have zipped nuclei to use
            //iParameters = readParameterInfo(zipPath);
            iParameters = dummyParameters();
            iZipNuclei = new ZipNuclei(zipPath);
            if (iZipNuclei.iZipFile != null) {
                //20060719 readEditLog(iEditLog);
            	long timeStart = System.nanoTime();
                readNuclei();
                long timeEnd = System.nanoTime();
                double timeDiff = (timeEnd-timeStart)/1e6;
                System.out.println("Time to read nuclei in constructor(Config c): "+timeDiff+" ms.");
                getScopeParameters();
                findImageParameters();
                iGoodNucleiMgr = true;
            }
        } 
        computeRWeights();
        System.gc();
    }
    
    public int getStartTime() {
    	return iStartTime;
    }

    public int getStartingIndex() {
        return iStartingIndex;
    }

    public void computeRWeights() {
        int k = getWeightMethodIndex();
        try {
	        for (int i = iStartingIndex; i <= iEndingIndex; i++) {
	            //println("computeRWeight, " + i);
	            Vector<Nucleus> v = nuclei_record.get(i - 1);
	            for (int j=0; j < v.size(); j++) {
	                Nucleus n = v.get(j);
	                //println("computeRWeight, " + n.identity);
	                if (n.status >= 0) computeRWeight(n, k);
	            }
	        }
        } catch(NullPointerException npe) {
        	return;
        }
    }

    public void computeRWeight(Nucleus n, int k) {
		if(n.rwraw <= 0) return; // for backward compatibility
        n.rweight = n.rwraw;
        switch(k) {
        case 1:
            n.rweight -= n.rwcorr1; //global
            break;
        case 2:
            n.rweight -= n.rwcorr2; //local
            break;
        case 3:
            n.rweight -= n.rwcorr3; //blot
            break;
        case 4:
            n.rweight -= n.rwcorr4; //cross
            break;
        }
    }

    private int getWeightMethodIndex() {
        String method = iConfig.iExprCorr;
        if (method.equals("global")) return 1;
        if (method.equals("local")) return 2;
        if (method.equals("blot")) return 3;
        if (method.equals("cross")) return 4;
        return 0;
    }

    private void findImageParameters() {
        // save off existing ImageWindow parameters
        String zipTifFilePath = ImageWindow.cZipTifFilePath;
        String tifPrefix = ImageWindow.cTifPrefix;
        String tifPrefixR = ImageWindow.cTifPrefixR;
        int useZip = ImageWindow.cUseZip;
        int width = ImageWindow.cImageHeight;
        int height = ImageWindow.cImageWidth;

        // now feed it my parameters
        sendStaticParametersToImageWindow();
        // make up a sample image name
        int plane = iMovie.plane_start;
        int time = iStartTime;
        String imageName = makeImageName(time, plane);
        // now "make" the image
        //System.out.println("NucleiMgr calling ImageWindow.makeImage...");
        ImageWindow.makeImage(getConfig().iTifPrefix + imageName);
        iImageWidth = ImageWindow.cImageWidth;
        iImageHeight = ImageWindow.cImageHeight;
        // now restore ImageWindow
        ImageWindow.cZipTifFilePath = zipTifFilePath;
        ImageWindow.cTifPrefix = tifPrefix;
        ImageWindow.cTifPrefixR = tifPrefixR;
        ImageWindow.cUseZip = useZip;
        ImageWindow.cImageWidth = width;
        ImageWindow.cImageHeight = height;
        ImageWindow.imagewindowUseStack = iUseStack;
        ImageWindow.iSplit = iSplit;
    }

    private String makeImageName(int time, int plane) {
        // typical name: t001-p15.tif
        // to be augmented later to something like: images/050405-t001-p15.tif
        // which specifies a path and prefix for the set
        StringBuffer name = new StringBuffer("t");
        name.append(EUtils.makePaddedInt(time));
        name.append("-p");
        String p = EUtils.makePaddedInt(plane, 2);
        name.append(p);

        switch(getConfig().iUseZip) {
        case 0:
        case 1:
        case 3:
            name.append(".tif");
            break;
        default:
            name.append(".zip");
        }
        
        newLine();
        System.out.println("NucleiMgr made Image Name: "+ name.toString());
        return(name.toString());
    }


    public Parameters dummyParameters() {
        iParameters = new Parameters();
        iMovie = iParameters.getMovie();
        iMovie.xy_res = iConfig.iXy_res; //.09f;
        iMovie.z_res = iConfig.iZ_res; //1;
        iParameters.polar_size = iConfig.iPolar_size; //45;
        iMovie.plane_start = iConfig.iPlaneStart;
        iMovie.plane_end = iConfig.iPlaneEnd;
        
        iUseStack = iConfig.iUseStack;

        return iParameters;

    }

//    private void createDummies(Parameters p) {
//        iMovie = p.getMovie();
//        iMovie.xy_res = iConfig.iXy_res; //.09f;
//        iMovie.z_res = iConfig.iZ_res; //1;
//        p.polar_size = iConfig.iPolar_size; //45;
//        iMovie.plane_start = iConfig.iPlaneStart;
//        iMovie.plane_end = iConfig.iPlaneEnd;
//
//        iUseStack = iConfig.iUseStack;
//    }


    public Parameters readParameterInfo(String zipPath) {
        iZipNuclei = new ZipNuclei(zipPath);
        if (iZipNuclei.iZipFile != null) {
            iParameters =  readParameterInfo(iZipNuclei, iParameterEntry);
        } else {
            iParameters = null;
        }
        iParameters = new Parameters();
        iMovie = iParameters.getMovie();
        iMovie.xy_res = .09f;
        iMovie.z_res = 1;
        iParameters.polar_size = 45;


        return iParameters;
    }

    private Parameters readParameterInfo(ZipNuclei zn, String testParams) {
        iParameters = new Parameters();
        //iParameters.setParameters(zn, testParams);
        // here we allow the config file to override the parameters file
        // this makes it possible to load and view a portion of a data set
        iMovie = iParameters.getMovie();
        //if (iMovie.time_start < iStartingIndex) {
        //    iMovie.time_start = iStartingIndex;
        //}
        //if (iMovie.time_end > iEndingIndex) {
        //    iMovie.time_end = iEndingIndex;
        //}
        //if (iEndingIndex > iMovie.time_end) iEndingIndex = iMovie.time_end;
        iMovie.tp_number = iMovie.time_end - iMovie.time_start + 1;
        println("readParameters: iMovie.tp_number: " + iMovie.tp_number);
        iNumNucleiFiles = iMovie.time_end - iMovie.time_start + 1;
        System.out.println("SN_time_start: " + iMovie.time_start);
        System.out.println("SN_time_end: " + iMovie.time_end);
        System.out.println("SN_plane_start: " + iMovie.plane_start);
        System.out.println("SN_plane_end: " + iMovie.plane_end);
        //System.out.println("readParameters: " + iMovie);
        return iParameters;
    }

    public void readEditLog(Log editLog) {
        ZipEntry ze = iZipNuclei.getZipEntry(PARAMETERS + C.Fileseparator + "EditLog.txt");
        if (ze == null) {
            System.out.println("no edit log found");
            iEditLog.append("\nSTART: " + new GregorianCalendar().getTime().toString());
            iEditLog.append("from config file: " + iConfig.iConfigFileName);
            return;
        }
        //System.out.println("\nREADING EDITLOG");
        String s = null;
        //Log log = iAceTree.getEditLog();
        if (ze != null) {
            //log.append(NL + "READING STORED EDITLOG " + log.getTime());
            while ((s = iZipNuclei.readLine(ze)) != null) {
                //System.out.println("logline: " + s);
                iEditLog.append(s);
            }
            //log.append("END OF STORED EDITLOG " + log.getTime() + NL);

        }
        //System.out.println("readEditLog exiting");

    }

    // CURRENT VERSION
    public void readNuclei() {
        int last = readNuclei(iZipNuclei);
        if (last < iEndingIndex) {
            iEndingIndex = last;
            iConfig.iEndingIndex = iEndingIndex;
        }
        if (last < iConfig.iEndingIndex) {
            iConfig.iEndingIndex = last;
        }
       System.out.println("last, iEndingIndex: " + last + CS + iEndingIndex + CS  + iConfig.iEndingIndex + CS + iMovie);
       iUseStack = iConfig.iUseStack;
       iSplit = iConfig.iSplit;
    }

    // here I want to read all the nuclei data in the zip file
    // even if not all of it will be processed based on the
    // iEndTime parameter setting
    // this should allow interative editing of the nuclei "files"
    @SuppressWarnings("unused")
	private int readNuclei(ZipNuclei zn) {
        // the following results in all nuclei files being read
        // regardless of 1StartTime and iEndTime
    	newLine();
        System.out.println("readNuclei:1 " + iMovie.time_end + CS + iMovie.time_start);
        // Initializes vector to array of empty vectors
        
        fakeNuclei();
        
        // Try replacing fakeNuclei() with this:
        // Increment vector size by VEC_INCREMENT_SIZE (20) each time it is resized up
        //nuclei_record = new Vector(??, VEC_INCREMENT_SIZE);
        
        iFakeNuclei = false; //override this param
        Nucleus n = null;
        int debugCount = 0;
        System.out.println("readNuclei:2 " + iMovie.time_end + CS + iMovie.time_start);
        
        // Try this way of iterating through zip entries
        //InputStream is = getInputStream 
        //ZipEntry ze = null;
        
        // Old way of iteration
        Enumeration<? extends ZipEntry> e = zn.iZipFile.entries();
        //int lastNonEmptyIndex = 0;
        while (e.hasMoreElements()) {
        	//System.out.println("More elements...");
            ZipEntry ze = e.nextElement();
            Vector<Nucleus> v = new Vector<>();
            String [] saa = zn.parseZipEntry(ze);
            
            if (saa.length < 2)
            	continue; // maybe a nuclei/ entry

            if (saa[0].equals("nuclei")) {
                int index = zn.parseZipEntryName(saa[1]) - 1;
                //System.out.println("Zip index: "+index);
                if (index < 0)
                	continue; // probably a nuclei/log entry
                String s = zn.readLine(ze);
                if (s == null) {
                    if (nuclei_record.size() > index) {
                        nuclei_record.setElementAt(v, index);
                        //lastNonEmptyIndex = index;
                        if (index > iLastNucleiFile) {
                            System.out.println("update iLastNucleiFile to: " + index);
                            iLastNucleiFile = index;
                        }
                    }
                    continue;
                }
                boolean newFormat = true;
                // detect old format by the absence of commas
                //if (debugCount++ < 10) println("readNuclei: " + s);
                if (s.indexOf(',') == -1) 
                	newFormat = false;
                int j = 1;
                // added code to handle some misformed nuclei files
                // a little squirrely maybe but it is working
                while (s != null && s.length() > 10) {
                    //if (index == 199) println("\n\nREADNUCLEI: " + s);
                    try {
                        String [] sa;
                        if (newFormat)
                        	sa = getTokens(s, 0);
                        else
                        	sa = getTokens(s);
                        if (sa[0] != null
                            && (sa[0].length() > 0
                            && Integer.parseInt(sa[0]) != j++)) {
                            break;
                        }
                        if (newFormat) {
                        	n = new Nucleus(sa);
                        	/*
                        	if (n.identity.isEmpty() || n.identity == null)
                        		System.out.println("No name for nucleus: "+n);
                    		*/
                        	//System.out.println("Created nucleus: ("+n.identity+")");
                        }
                        else
                        	n = new Nucleus(sa, !newFormat);
                        v.add(n);
                        s = zn.readLine(ze);
                    }
                    catch(Exception ee) {
                        System.out.println("readNuclei exception: " + ee);
                        System.out.println(s);
                        System.out.println("time=" + index + ", j = " + j);
                        zn.closeEntry();
                        ee.printStackTrace();
                        System.exit(1);
                        break;
                    }
                }
                
                if (nuclei_record.size() > index) {
                    nuclei_record.setElementAt(v, index);
                    if (index > iLastNucleiFile)
                    	iLastNucleiFile = index;
                }
                zn.closeEntry();
            }
        }
        println("readNuclei: iEndingIndex=" + iEndingIndex + CS + iLastNucleiFile + CS + nuclei_record.size());
        if (iEndingIndex == 1) {
            iEndingIndex = iLastNucleiFile + 1;
            iConfig.iEndingIndex = iEndingIndex;
            for (int i=LAST - 1; i > iLastNucleiFile; i--) {
                nuclei_record.remove(i);
            }
        }
        
        // Cut nuclei_record down to the latrer of the where the last nuclei file is or the iEndingIndex specified in the .xml
        int newSize = iLastNucleiFile+1;
        if (newSize < iEndingIndex)
        	newSize = iEndingIndex+1;
        nuclei_record.setSize(newSize+1); // extra +1 to account for difference in indexing and setup for legacy datasets.
                                            // the adapter will catch time points that are empty later on and not render
                                            // them so this avoids indexing errors
        
        println("readNuclei: at end, nuclei_record.size: " + nuclei_record.size());

        System.out.println("readNuclei:3 " + iMovie.time_end + CS + iMovie.time_start);
        
        return nuclei_record.size();
    }

    // Called by classes trying to access nuclei_record.elementAt(...)
    public Vector<Nucleus> getElementAt(int i) {
    	if (nuclei_record.size()-1 < i) {
    		int oldSize = nuclei_record.size();
    		// Every time we run out of room in the vector, we add on 30 more indices
    		// and initialize them to empty vectors
    		nuclei_record.setSize(i+30);
	    	for (int j = oldSize; j < nuclei_record.size(); j++)
	    		nuclei_record.setElementAt(new Vector<Nucleus>(), j);
    	}
    	return nuclei_record.elementAt(i);
    }

    public void sendStaticParametersToImageWindow() {
        ImageWindow.setStaticParameters(
                iConfig.iZipTifFilePath
                ,iConfig.iTifPrefix
                ,iConfig.iUseZip
                ,iConfig.iSplitChannelImage
                ,iConfig.iSplit);
    }

    //public Identity getIdentity() {
    //    return iIdentity;
    //}
    public int getiEndingIndex(){
    	return iEndingIndex;
	}

    public Identity3 getIdentity() {
        return iIdentity;
    }

    public Movie getMovie() {
        return iMovie;
    }

    public Parameters getParameters() {
        return iParameters;
    }

    public Config getConfig() {
        return iConfig;
    }

    public void setConfig(Config config) {
    	iConfig = config;
    }

    public MeasureCSV getMeasureCSV() {
    	return iMeasureCSV;
    }

    public EditLog getEditLog() {
        return iEditLog;
    }
    public int getiLastNucleiFile() {
        return iLastNucleiFile;
    }


    public double getZPixRes() {
        return iZPixRes;
    }

    public String getIndex(String name, int time) {
        int k = 0;
        
        // Resize if trying to access a time greater than number of files loaded
        Vector<Nucleus> nuclei;
        if (nuclei_record.size() < time) {
        	nuclei_record.setSize(time);
        	for (int i = getiLastNucleiFile()+1; i < time; i++)
        		nuclei_record.setElementAt(new Vector<Nucleus>(), i);
        	nuclei = nuclei_record.elementAt(time - 1);
        }
        else
    		nuclei = nuclei_record.elementAt(time - 1);
        
        Nucleus n = null;
        for (int j=0; j < nuclei.size(); j++) {
            n = nuclei.elementAt(j);
            //System.out.println(n);
            if (n.identity.equals(name)) {
                k = n.index;
                //System.out.println("getCellIndex: " + n);
                break;
            }
        }
        String s = "(" + k + ")";
        return s;
    }

    public Nucleus findClosestNucleusXYZ(int mx, int my, float mz, int time) {
        Vector<Nucleus> nuclei = nuclei_record.elementAt(time - 1);
        double x, y, z, r;
        boolean g;
        Nucleus candidate = null;
        double d = 100000;
        double xyz;
        mz *= iZPixRes;
        for (int j=0; j < nuclei.size(); j++) {
            Nucleus n = nuclei.elementAt(j);
            //System.out.print("findClosest..: " + n);
            if (n.status == -1) continue;
            x = n.x;
            y = n.y;
            z = n.z * iZPixRes;
            r = n.size/2.;
            g = Math.abs(x - mx) < r;
            if (!g) continue;
            g = Math.abs(y - my) < r;
            if (!g) continue;
            g = Math.abs(z - mz) < r;
            if (!g) continue;
            xyz = Math.abs(x - mx) + Math.abs(y - my) + Math.abs(z - mz);
            //System.out.println("findClosest: " + n.identity + CS + xy);
            if (xyz < d) {
                d = xyz;
                candidate = n;
            }
        }
        return candidate;
    }

    public Nucleus findClosestNucleus(int mx, int my, int time) {
        //System.out.println("findClosestNucleus: " + mx + CS + my + CS + time);
        Vector<Nucleus> nuclei = nuclei_record.elementAt(time - 1);
        double x, y, r;
        boolean g;
        Nucleus candidate = null;
        double d = 100000;
        double xy;
        for (int j=0; j < nuclei.size(); j++) {
            Nucleus n = nuclei.elementAt(j);
            //System.out.println("findClosest..: " + n);
            //System.out.print(n);
            if (n.status == -1) continue;
            x = n.x;
            y = n.y;
            r = n.size/2.;
            xy = Math.abs(x - mx) + Math.abs(y - my);
            //System.out.print("findClosest: "  + j + CS + n.identity + CS + x + CS + y + CS
            //        + mx + CS + my + CS + r + CS + xy);
            g = Math.abs(x - mx) < r;
            if (!g) continue;
            g = Math.abs(y - my) < r;
            if (!g) continue;
            if (xy < d) {
                d = xy;
                candidate = n;
            }
        }
        //System.out.println("returning candidate: " + candidate.identity + CS + candidate.z);
        return candidate;
    }

    public Nucleus findClosestNucleus(int mx, int my, int mz, int time) {
        //System.out.println("findClosestNucleus: " + mx + CS + my + CS + mz + CS + time);
        if (time < 1) time = 1;
        Vector<Nucleus> nuclei = null;
        try {
            nuclei = nuclei_record.elementAt(time - 1);
        } catch(Exception e) {
            return null;
        }
        double x, y, z, r;
        boolean g;
        Nucleus candidate = null;
        double d = 100000;
        double xy;
        mz *= iZPixRes;
        for (int j=0; j < nuclei.size(); j++) {
            Nucleus n = nuclei.elementAt(j);
            //System.out.print(n);
            if (n.status == -1) continue;
            x = n.x;
            y = n.y;
            z = (int)(n.z * iZPixRes);
            r = n.size/2.;
            xy = Math.abs(x - mx) + Math.abs(y - my) + Math.abs(z - mz);
            //System.out.print("findClosest: "  + j + CS + n.identity + CS + x + CS + y + CS
            //        + mx + CS + my + CS + r + CS + xy);
            g = Math.abs(x - mx) < r;
            if (!g) continue;
            g = Math.abs(y - my) < r;
            if (!g) continue;
            g = Math.abs(z - mz) < r;
            if (!g) continue;
            if (xy < d) {
                d = xy;
                candidate = n;
            }
        }
        //System.out.println("returning candidate: " + candidate.identity + CS + candidate.z);
        return candidate;
    }


    public Nucleus getCurrentCellData(String cellName, int time) {
        Vector<Nucleus> nucData = nuclei_record.elementAt(time - 1);
        return NucUtils.getCurrentCellData(nucData, cellName);
    }

    @SuppressWarnings("unused")
	public boolean isValidCell(String name, int time) {
        boolean rtn = false;
        Nucleus n = getCurrentCellData(name, time);
        iDLog.append("isValidCell " + name + CS + time);
        iDLog.append("isValidCell " + n);
        return (n != null);
    }

    public Vector<Nucleus> getNuclei(int time) {
        return nuclei_record.elementAt(time);
    }

    @SuppressWarnings("unused")
	public Nucleus getNucleusFromHashkey(String hashKey, int time) {
        Nucleus r = null;
        boolean found = false;
        Vector<Nucleus> nucData = nuclei_record.elementAt(time - 1);
        for (int j = 0; j < nucData.size(); j++) {
            Nucleus n = nucData.elementAt(j);
            //println("getNucleusFromHashkey: " + n);
            if (n.status < 0) continue;
            if (n.hashKey.equals(hashKey)) {
                found = true;
                r = n;
                break;
            }
        }
        return r;
    }

    public double nucDiameter(Nucleus n, double imgPlane) {
        if (n == null) return -1; //covers some issues re currentCell and not tracking
        double r = -0.5;
        double cellPlane = n.z;
        double R = n.size/2.; //pixels
        double y = (cellPlane - imgPlane)*iZPixRes/R;
        double r2 = 1 - y*y;
        if (r2 >= 0.) r = Math.sqrt(r2)*R;
        return 2*r;
    }

    public boolean hasCircle(Nucleus n, double imgPlane) {
        return (nucDiameter(n, imgPlane) > 0);
    }

    @SuppressWarnings("unused")
	public String getOrientation() {
        //println("getOrientation: ");
        //new Throwable().printStackTrace();
        String orientation = "A";
        String late = "A";
        if (iParameters.ap < 0) orientation = "P";
        if (iParameters.dv > 0) orientation += "D";
        else orientation += "V";
        if (iParameters.lr > 0) orientation += "L";
        else orientation += "R";
        if (orientation.equals("ADL")) late = "ARD";
        else if (orientation.equals("AVR")) late = "ALV";
        else if (orientation.equals("PDR")) late = "PLD";
        else if (orientation.equals("PVL")) late = "PRV";
        //return orientation + " -> " + late;
        return orientation;
    }

    public String getConfigFileName() {
        return iConfig.iConfigFileName;
    }


    private void getScopeParameters() {
        //iMovie = iParameters.getMovie();
        //iPlaneEnd = iMovie.plane_end;
        //iZPixRes = iMovie.z_res/iMovie.xy_res*iParameters.z_res_fudge;
        ////NucUtils.setZPixRes(iZPixRes);
        //if (iEndingIndex > iMovie.time_end) iEndingIndex = iMovie.time_end;

        if (!iFakeNuclei) {
            iMovie = iParameters.getMovie();
            iPlaneEnd = iMovie.plane_end;
            iZPixRes = iMovie.z_res/iMovie.xy_res*iParameters.z_res_fudge;
            //println("getScopeParameters: iZPixRes: " + iZPixRes);
        } else {
                iPlaneEnd = iConfig.iPlaneEnd;
                iPlaneStart = iConfig.iPlaneStart;
                iZPixRes = iConfig.iZ_res/iConfig.iXy_res;

        }
        NucUtils.setZPixRes(iZPixRes);
        //System.out.println("getScopeParameters: iEndingIndex=" + iEndingIndex);
        //System.out.println("getScopeParameters: xy_res=" + iMovie.xy_res);
        //System.out.println("getScopeParameters: z_res=" + iMovie.z_res);
        //System.out.println("getScopeParameters: z_res_fudge=" + iParameters.z_res_fudge);
        //System.out.println("getScopeParameters: iZPixRes=" + iZPixRes);
    }

    public void setParameterEntry(String parameterLocation) {
        iParameterEntry = PARAMETERS + "/" + parameterLocation + PARAMETERS;
    }

    public String getParameterEntry() {
        return iParameterEntry;
    }

    public Vector<Nucleus> getParameterFileInfo() {
        Vector<Nucleus> v = iParameters.getParameterFileInfo();
        //Vector v = iAceTree.getParameters().getParameterFileInfo();
        return v;
    }


    /**
     * called from AceTree when no nuclei file was specified
     * we will add either iEndingIndex or LAST empty nuclei vectors
     * LAST if iEndingIndex = 1 meaning no ending index was specified
     * if LAST then extras are removed after all nuclei files have been read
     */
    @SuppressWarnings("unused")
	public void fakeNuclei() {
        //println("fakeNuclei: iEndingIndex: " + iEndingIndex);
        iFakeNuclei = true;
        nuclei_record = new Vector<Vector<Nucleus>>(); //[iEndingIndex - iStartingIndex + 1];
        Nucleus n = null;
        int last = LAST;
        //if (iEndingIndex > 1) last = iEndingIndex;
        for (int i=0; i < last; i++) {
            nuclei_record.add(new Vector<Nucleus>());
            //n = new Nucleus(true); // a fake nucleus
            //if (i == 0) n.predecessor = -1;
            //n.setHashKey(NucUtils.makeHashKey(i + 1, n));
            //((Vector)nuclei_record.elementAt(i)).add(n);
        }
    }

//    private void addFakeNuclei(Vector<Vector<Nucleus>> nuclei_record, int iEndingIndex) {
//        //iFakeNuclei = true;
//        //nuclei_record = new Vector(); //[iEndingIndex - iStartingIndex + 1];
//        Nucleus n = null;
//        for (int i=0; i < iEndingIndex; i++) {
//            nuclei_record.add(new Vector<Nucleus>());
//            n = new Nucleus(true); // a fake nucleus
//            if (i == 0) {
//            	//System.out.println("NucleiMgr set predecessor to -1 for: "+n.identity);
//            	n.predecessor = -1;
//            }
//            (nuclei_record.elementAt(i)).add(n);
//        }
//    }

    public boolean isFake() {
        return iFakeNuclei;
    }

    /*
// I removed the image without config file code to escape dependence on acetree class
// the feature is no longer used/supported from gui anyway 2/14/2013 -AS

    public Parameters fakeParameters(String tifPath, String tifPrefix) {
        Parameters p = new Parameters();
        //Movie iMovie = p.getMovie();
        createDummies(p);
  
        String start = tifPath + C.Fileseparator + tifPrefix;
        int i;
        for (i=0; i <= 50; i++) {
            String s = makeImageName(iStartingIndex, i + 1);
            File f = new File(start + s);
            if (!f.exists()) break;
        }
        int planeEnd = i;
        System.out.println("iPlaneEnd: " + i);
        for (i=0; i <= 1000; i++) {
            String s =makeImageName(i + 1, 1);
            File f = new File(start + s);
            if (!f.exists()) break;

        }
        System.out.println("iTimeEnd: " + i);
        int timeEnd = i;
        iMovie.plane_start = 1;
        iMovie.plane_end = planeEnd;
        iMovie.time_start = iStartingIndex;
        iMovie.time_end = timeEnd;
        iMovie.xy_res = 0.09f;
        iMovie.z_res = 1.0f;
        p.z_res_fudge = 1.0f;
        p.parameterFileData = new Vector();
        p.parameterFileData.add("# faked parameters");
        p.parameterFileData.add("time_start " + iMovie.time_start);
        p.parameterFileData.add("time_end " + iMovie.time_end);
        p.parameterFileData.add("plane_start " + iMovie.plane_start);
        p.parameterFileData.add("plane_end " + iMovie.plane_end);
        p.parameterFileData.add("xy_res " + iMovie.xy_res);
        p.parameterFileData.add("z_res " + iMovie.z_res);
        p.parameterFileData.add("z_res_fudge " + p.z_res_fudge);

        return p;
    }
    */

    public void reviewNuclei() {
    	Vector<Vector<Nucleus>> nr = nuclei_record;
    	for (int i=194; i < 195; i++) {
    		Vector<Nucleus> nuclei = nr.get(i);
    		for (int j=0; j < nuclei.size(); j++) {
    			Nucleus n = nuclei.get(j);
    			println("reviewNuclei, " + i + CS + j  + CS + n);
    		}
    	}
    }


    // Timing commented out -was used for optimization
	public void processNuclei(boolean doIdentity, int namingMethod) {
    	println("NucleiMgr processing nuclei including: assigning names, building AncesTree");
    	//println("reviewNuclei, 1");
    	//reviewNuclei();
        setAllSuccessors();
        if (iIdentity == null) 
        	iIdentity = new Identity3(this);
        iIdentity.setNamingMethod(getConfig().iNamingMethod);
        iIdentity.setPrintWriter(iPrintWriter);
    	//println("reviewNuclei, 2");
    	//reviewNuclei();
//        println("about to create names");
        if (doIdentity) {
        	iIdentity.identityAssignment();
        }
//        println("about to create tree data structure");
//    	println("reviewNuclei, 3");
    	// Debug here
        // For debugging to see if all nuclei information was correctly read from zip file
    	/*
        for (int i = 0; i < nuclei_record.size(); i++) {
        	System.out.println(i);
        	Vector v = (Vector)nuclei_record.elementAt(i);
        	Enumeration en = v.elements();
        	while (en.hasMoreElements()) {
        		Nucleus temp = (Nucleus)en.nextElement();
        		System.out.println("("+temp.identity+")");
        	}
        }
        */
    	//reviewNuclei();
        //long timeStart = System.nanoTime();
        int newstart = iStartingIndex;
        if (iStartingIndex < iStartTime)
        	newstart = iStartTime;
        iAncesTree = new AncesTree(null, this, newstart, iEndingIndex);
//        Cell PP = (Cell)iAncesTree.getCellsByName().get("P");
        //long timeEnd = System.nanoTime();
        //double timeDiff = (timeEnd-timeStart)/1e6;
        //System.out.println("Time to load entries: "+timeDiff+" ms");
//        int kk = PP.getChildCount();
        
        // For non-1 starting time, PP has a 0 child count
        //iAncesTree.printCounts();
        //println("NucleiMgr, constructor, " + kk + CS + PP.getName());
    }
    /*
      // removed this to eliminate dependence on AceTree -as 2/14/2013
    //20051116 added this while debugging Analysis.java
    // but I am worried that it could cause problems
    // because of squirrely behavior of the DefaultMutableTreeNode
    // basically I got the code from AceTree.updateRoot()
    public Cell getRoot() {
        Cell root = new Cell(AceTree.ROOTNAME);
        Vector rootCells = iAncesTree.getRootCells();
        Enumeration e = rootCells.elements();
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            root.add(c);
        }

        return root;
    }
    */
    public Hashtable getCellsByName() {
        return iAncesTree.getCellsByName();
    }

    public AncesTree getAncesTree() {
        return iAncesTree;
    }



    /**
     * used in rebuild tree action
     *
     */
    public void clearAllHashkeys() {
        //System.out.println("clearAllHashkeys: " + nuclei_record);
        Vector<Nucleus> v = null;
        for (int i=0; i < nuclei_record.size(); i++) {
            v = nuclei_record.elementAt(i);
            for (int j=0; j < v.size(); j++) {
                v.elementAt(j).hashKey = null;
            }
        }
    }

    private String [] getTokens(String s, int x) {
        String [] sa = new String[30];
        StringTokenizer st = new StringTokenizer(s, ",");
        int k = 0;
        while (st.hasMoreTokens()) {
            //String ss = st.nextToken();
            //System.out.println("getTokens: " + k + CS + ss);
            //sa[k++] = ss.trim();
            sa[k++] = st.nextToken().trim();
        }
        return sa;
    }

    /**
     * major access function to the array of nuclei Vectors
     * stored here -- one Vector for each time point
     * @return Vector [] -- the nuclei_record object of this instance
     */
    public Vector<Vector<Nucleus>>  getNucleiRecord() {
        return nuclei_record;
    }

    public void makeBackupNucleiRecord() {
	
	System.out.println("Refusing to make backup, waste of memory");
	/*
        System.out.println("makeBackupNucleiRecord");
        nuclei_record_backup = new Vector();
        Vector nuclei = null;
        Vector nucleiNew = null;
        Nucleus n = null;
        Nucleus nNew = null;
        for (int i=0; i < nuclei_record.size(); i++) {
            nuclei = (Vector)nuclei_record.elementAt(i);
            nucleiNew = new Vector();
            for (int j=0; j < nuclei.size(); j++) {
                n = (Nucleus)nuclei.elementAt(j);
                nNew = n.copy();
                nucleiNew.add(nNew);
            }
            nuclei_record_backup.add(nucleiNew);
        }
	*/
    }

    public void restoreNucleiRecord() {
		//   nuclei_record = nuclei_record_backup;
		System.out.println("cannot restore no backup made");
	}

    public void setEndingIndex(int endTime) {
        iEndingIndex = endTime;
    }

    public int getEndingIndex() {
        return iEndingIndex;
    }

    public int getPlaneEnd() {
        return iPlaneEnd;
    }

    public int getPlaneStart() {
        return iPlaneStart;
    }

    public int getImageWidth() {
        return iImageWidth;
    }

    public int getImageHeight() {
        return iImageHeight;
    }



    /**
     * access function for ZipNuclei member
     * @return ZipNuclei -- the file used to initialize this object
     */
    public ZipNuclei getZipNuclei() {
        return iZipNuclei;
    }

    public void setAllSuccessors() {
    	newLine();
        System.out.println("setAllSuccessors: " + iStartingIndex + CS + iEndingIndex + ", " + nuclei_record.size());
        //for (int i=iStartingIndex - 1; i < iEndingIndex; i++) {
        for (int i=iStartingIndex - 1; i < nuclei_record.size(); i++) {
            int r = setSuccessors(i);
            if (r != 0)
            	break;
        }
    }

    public int setSuccessors(int i) {
    	//System.out.println("\nnucleimgr setsuccessors "+i);
    	//long timeStart = System.nanoTime();
        if (iConfig.iNamingMethod == Identity3.MANUAL)
        	return 0;
        Vector<Nucleus> now = nuclei_record.elementAt(i);
        Nucleus n = null;
        int m1 = Nucleus.NILLI;
        for (int j=0; j < now.size(); j++) {
            n = now.elementAt(j);
            //println("setSuccessors3: " + n.identity);
            n.successor1 = m1;
            n.successor2 = m1;
        }
        if (i == iEndingIndex - 1)
        	return 1;
        Vector<Nucleus> next;
        try {
            next = nuclei_record.elementAt(i + 1);
        } catch(ArrayIndexOutOfBoundsException oob) {
            return 1;
        }
        // first set all successors to -1
        for (int j=0; j < next.size(); j++) {
            n = next.elementAt(j);
            //println("setSuccessors: " + n.identity);
            if (n.status == Identity3.DEAD) 
            	continue;
            int pred = n.predecessor;
            if (pred == Identity3.DEAD) 
            	continue;
            //println("setSuccessors2: " + j + CS + pred);
            Nucleus p = null;
            try {
            	p = now.elementAt(pred -1);
            } catch(Exception e) {
            	continue;
            }
            if (p.successor1 == m1)
            	p.successor1 = j + 1;
            else if (p.successor2 == m1)
            	p.successor2 = j + 1;
            else {
                System.out.println("error: MORE THAN 2 SUCCESSORS");
            }
            
            //System.out.println("nucleimgr succ for "+p.identity+": "+p.successor1+" "+p.successor2);
        }
        //long timeEnd = System.nanoTime();
        //double timeDiff = (timeEnd-timeStart)/1e6;
        //System.out.println("Time for NucleiMgr.setSuccessors(): "+timeDiff+" ms.");
        return 0;
    }

//    private void open(String s) {
//        try {
//            iFile = new File(s);
//            iFOS = new FileOutputStream(iFile);
//            iPWriter = new PrintWriter(iFOS);
//
//        } catch(IOException ioe) {
//            ioe.printStackTrace();
//        }
//
//    }
//
//    private void close() {
//        try {
//            iPWriter.close();
//            iFOS.close();
//        } catch(IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }
//
//    private void write(String s) {
//        iPWriter.println(s);
//    }

//    private void printNuclei() {
//    	System.out.println("Saving printNuclei loop end: "+iMovie.tp_number);
//        for (int i = 0; i< iMovie.tp_number; i++) {
//            String name = "t" + EUtils.makePaddedInt(i + 1) + "-nuclei";
//            open(name);
//            Vector<Nucleus> nuclei = nuclei_record.elementAt(i);
//            for (int j=0; j<nuclei.size(); j++) {
//                Nucleus nucleij = (Nucleus)nuclei.elementAt(j);
//                write(nucleij.toString());
//            }
//            close();
//        }
//    }

    /**
     * Special tokenizing function adapted to the lines in nuclei files
     *
     * a line in such a file consists of 15 fields which were written
     * by a printf statement in a C program
     * The widths of the fields are specified in the static int [] X
     * 1    index -- line number in file starting from 1
     * 2    x location of nucleus in pixels
     * 3    y location of nucleus in pixels
     * 4    z location of nucleus in planes (float)
     * 5    cell name assigned by StarryNight
     * 6    nominal cell diameter in pixels (all cells in a time index have the same value)
     * 7    measure of GFP signal within the circle of this nucleus
     * 8    ? (seems to be a flag)
     * 9    ? (float)
     * 10   ? (float)
     * 11   ? (float)
     * 12   ? (float)
     * 13   index - the index that this cell had in the previous time nuclei file
     * 14   index - the index that this cell has in the next time nuclei file
     * 15   index - (if present indicates a division occurred) index of second daughter in next time nuclei file
     *
     *
     *
     * @param s String representing one line from such a file
     * @return String [] containing the tokens found
     */

    private String [] getTokens(String s) {
        String [] sa = new String[15];
        int k = 0;
        int j = 0;
        int max = s.length();
        for(int i=0; i<15; i++) {
            //if(j > max) break;
            k += XX[i];
            //System.out.println(i + ", " + j + ", " + k);
            if (k > max) break;
            sa[i] = s.substring(j, k).trim();
            j = k;
        }
        return sa;
    }

    /**
     * pointers into the formatted lines of the nuclei files
     */
    private static final int [] XX = {
        6,5,5,6,14,4,8,4,5,6,5,6,5,5,5
    };

    public static final int
         LAST = 10000 // more than the most time points we ever expect to see
    ;


    final public static String
    PARAMETERS = "parameters"
        ,NL = "\n"
        ,CS = ", "
        ;

//    @SuppressWarnings({ "unused", "null" })
//	public static void main(String[] args) {
//        System.out.println("NucleiMgr test main entered");
//        NucleiMgr test = null; //new NucleiMgr(0, 100);
//        String testZip = "t220.zip";
//        ZipNuclei zn = new ZipNuclei(testZip);
//        String testParams = "parameters/t220-parameters";
//        test.readParameterInfo(zn, testParams);;
//        Movie Movie = test.iParameters.getMovie();
//        int timePts = Movie.time_end - Movie.time_start + 1;
//        Vector<Nucleus>[] na = new Vector<Nucleus>[timePts];
//        timePts = 20;
//        test.readNuclei(zn);
//        int start = 7, end = 10;
//        for (int i=start; i < end; i++) {
//            Vector<Vector<Nucleus>> a = test.nuclei_record;
//            Vector<Nucleus> b = a.elementAt(i);
//            int c = b.size();
//            for (int j=0; j < c; j++) {
//                Nucleus n = (Nucleus)b.elementAt(j);
//                n.identity = "";
//            }
//        }
//        int [] lineage_ct_p = new int[1];
//        lineage_ct_p[0] = 0;
//        System.out.println("lineage_ct_p=" + lineage_ct_p[0]);
//        test.printNuclei();
//        System.out.println("main exiting");
//    }

    private void println(String s) {System.out.println(s);}
    private void newLine() {System.out.println("");}

}
/**
 * default constructor
 * <br> this object should be a singleton
 *
 */
/*
public NucleiMgr(AceTree aceTree, int startingIndex, int endingIndex) {
    super();
    // the following are based on the current structure
    // of the nuclei and parameters files
    p1 = "nuclei/";
    //p2 = "t";
    //p3 = "-nuclei";
    iStartingIndex = 1;
    iEndingIndex = endingIndex - startingIndex + 1;
    iIndexOffset = startingIndex - 1;
    //iStartingIndex = startingIndex;
    //iEndingIndex = endingIndex;
    iAceTree = aceTree;
    iFakeNuclei = false;
    iDLog = iAceTree.getDebugLog();
    System.out.println("NucleiMgr: " + iStartingIndex + ", " + iEndingIndex);

}
*/
