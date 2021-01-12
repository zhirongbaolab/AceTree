package org.rhwlab.snight;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.acetree.NucUtils;
import org.rhwlab.dbaccess.DBAccess;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.nucedit.EditLog;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.C;
import org.rhwlab.utils.EUtils;
import org.rhwlab.utils.Log;

public class NucleiMgrHeadless extends NucleiMgr {

	/*
    private AceTree 		iAceTree;
    private ZipNuclei 		iZipNuclei;
    File 					iFile;
    FileOutputStream 		iFOS;
    PrintWriter 			iPWriter;
    Vector 					nuclei_record;
    Vector 					nuclei_record_backup;
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
    private boolean 		iDebug;

    //private Identity  iIdentity;
    private Identity3  		iIdentity;

    private AncesTree 		iAncesTree;
    private Config  		iConfig;
    private MeasureCSV		iMeasureCSV;
    private Parameters 		iParameters;
    private Movie 			iMovie;
    private EditLog     	iEditLog;
    private Log         	iDDLog;
    private Log         	iDLog;
    private boolean     	iEditLogInitialized;
    private double      	iZPixRes;


    private int         	iPlaneEnd;
    private int         	iPlaneStart;
    private int         	iLastNucleiFile;

    public  boolean     	iGoodNucleiMgr;

    PrintWriter				iPrintWriter;

    private static String p2 = "t";
    private static String p3 = "-nuclei";
	*/
	
	
	public NucleiMgrHeadless() {

	}
	

    public void setNucleiRecord(Vector nr) {
    	nuclei_record = nr;
    }


	@SuppressWarnings("unused")
	public NucleiMgrHeadless(String configFileName) {
        System.out.println("NucleiMgr(" + configFileName + ")" + CS + DBAccess.cDBLocation);
        //iAceTree = AceTree.getAceTree(null);
        //iEditLog = new EditLog("EditLog");

        int k2 = configFileName.lastIndexOf(".");

        String measureCSVpath = configFileName.substring(0, k2) + "AuxInfo.csv";
        //println("NucleiMgr, " + measureCSVpath + CS + DBAccess.cDBLocation);
        iMeasureCSV = new MeasureCSV(measureCSVpath);

        String s2 = configFileName.substring(k2 + 1);
        if (s2.equals("xml")) {
            iConfig = Config.createConfigFromXMLFile(configFileName);
        } else {
            iConfig = new Config(configFileName, false);
        }
        //if (1 == 1) System.exit(0);


        iStartingIndex = iConfig.iStartingIndex;
        iEndingIndex = iConfig.iEndingIndex;
        String zipPath = iConfig.iZipFileName;
        //println("NucleiMgr, zipPath=" + zipPath);
        String nucleiDir = iConfig.iZipNucDir + "/"; //"nuclei/";
        //println("NucleiMgr, nucleiDir=" + nucleiDir);

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
            fakeNuclei();
            iParameters = fakeParameters(iConfig.iZipTifFilePath, iConfig.iTifPrefix);
            getScopeParameters();
            findImageParameters();
            iGoodNucleiMgr = true;
        } else {
            // the normal case where we have zipped nuclei to use
            //iParameters = readParameterInfo(zipPath);
            iParameters = dummyParameters();
            iZipNuclei = new ZipNuclei(zipPath);
            if (iZipNuclei.iZipFile != null) {

                //20060719 readEditLog(iEditLog);
                readNuclei();
                getScopeParameters();
                findImageParameters();
                iGoodNucleiMgr = true;
            }
        }
        computeRWeights();
        // System.gc();



	}


	public NucleiMgrHeadless(Config config, PrintWriter printWriter) {
    	this(config);
    	iPrintWriter = printWriter;

	}

	@SuppressWarnings("unused")
	public NucleiMgrHeadless(Config config) {
        System.out.println("NucleiMgr, " + CS + DBAccess.cDBLocation);
        //iAceTree = AceTree.getAceTree(null);
        //iEditLog = new EditLog("EditLog");
        String configFileName = config.iConfigFileName;
        int k2 = configFileName.lastIndexOf(".");

        String measureCSVpath = configFileName.substring(0, k2) + "AuxInfo.csv";
        println("NucleiMgr, " + measureCSVpath + CS + DBAccess.cDBLocation);
        iMeasureCSV = new MeasureCSV(measureCSVpath);

        iConfig = config;

        iStartingIndex = iConfig.iStartingIndex;
        iEndingIndex = iConfig.iEndingIndex;
        String zipPath = iConfig.iZipFileName;
        //println("NucleiMgr, zipPath=" + zipPath);
        String nucleiDir = iConfig.iZipNucDir + "/"; //"nuclei/";
        //println("NucleiMgr, nucleiDir=" + nucleiDir);

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
            fakeNuclei();
            iParameters = fakeParameters(iConfig.iZipTifFilePath, iConfig.iTifPrefix);
            getScopeParameters();
            findImageParameters();
            iGoodNucleiMgr = true;
        } else {
            // the normal case where we have zipped nuclei to use
            //iParameters = readParameterInfo(zipPath);
            iParameters = dummyParameters();
            iZipNuclei = new ZipNuclei(zipPath);
            if (iZipNuclei.iZipFile != null) {

                //20060719 readEditLog(iEditLog);
                readNuclei();
                getScopeParameters();
                findImageParameters();
                iGoodNucleiMgr = true;
            }
        }
        computeRWeights();
        // System.gc();


	}




    @Override
	public int getStartingIndex() {
        return iStartingIndex;
    }

    @Override
	public void computeRWeights() {
        int k = getWeightMethodIndex();
        for (int i = iStartingIndex; i <= iEndingIndex; i++) {
            //println("computeRWeight, " + i);
            Vector v = nuclei_record.get(i - 1);
            for (int j=0; j < v.size(); j++) {
                Nucleus n = (Nucleus)v.get(j);
                //println("computeRWeight, " + n.identity);
                if (n.status >= 0) computeRWeight(n, k);
            }
        }
    }

    @Override
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
        // now feed it my parameters
        // make up a sample image name
        int plane = iMovie.plane_start;
        int time = iStartingIndex;
        String imageName = makeImageName(time, plane);
        // now "make" the image
        // now restore ImageWindow
        ImageWindow.cZipTifFilePath = zipTifFilePath;
        ImageWindow.cTifPrefix = tifPrefix;
        ImageWindow.cTifPrefixR = tifPrefixR;
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
        return(name.toString());
    }


    @Override
	public Parameters dummyParameters() {
        iParameters = new Parameters();
        iMovie = iParameters.getMovie();
        iMovie.xy_res = iConfig.iXy_res; //.09f;
        iMovie.z_res = iConfig.iZ_res; //1;
        iParameters.polar_size = iConfig.iPolar_size; //45;
        iMovie.plane_start = iConfig.iPlaneStart;
        iMovie.plane_end = iConfig.iPlaneEnd;

        return iParameters;

    }

    private void createDummies(Parameters p) {
        iMovie = p.getMovie();
        iMovie.xy_res = iConfig.iXy_res; //.09f;
        iMovie.z_res = iConfig.iZ_res; //1;
        p.polar_size = iConfig.iPolar_size; //45;
        iMovie.plane_start = iConfig.iPlaneStart;
        iMovie.plane_end = iConfig.iPlaneEnd;

    }

    @Override
	public void readNuclei() {
        int last = readNuclei(iZipNuclei);
        if (last < iEndingIndex) {
            iEndingIndex = last;
            iConfig.iEndingIndex = iEndingIndex;
        }
        if (last < iConfig.iEndingIndex) {
            iConfig.iEndingIndex = last;
        }
        //System.out.println("last, iEndingIndex: " + last + CS + iEndingIndex + CS  + iConfig.iEndingIndex + CS + iMovie);
    }

    // here I want to read all the nuclei data in the zip file
    // even if not all of it will be processed based on the
    // iEndTime parameter setting
    // this should allow interative editing of the nuclei "files"
    @SuppressWarnings("unused")
	private int readNuclei(ZipNuclei zn) {
        // tthe following results in all nuclei files being read
        // regardless of 1StartTime and iEndTime
        //System.out.println("readNuclei:1 " + iMovie.time_end + CS + iMovie.time_start);
        fakeNuclei();
        iFakeNuclei = false; //override this parm
        Nucleus n = null;
        int debugCount = 0;

        Enumeration e = zn.iZipFile.entries();
        //System.out.println("readNuclei:2 " + iMovie.time_end + CS + iMovie.time_start);
        while (e.hasMoreElements()) {
            ZipEntry ze = (ZipEntry)e.nextElement();

            Vector v = new Vector();
            String [] saa = zn.parseZipEntry(ze);
            if (saa.length < 2) continue; // maybe a nuclei/ entry
            if (saa[0].equals("nuclei")) {
                int index = zn.parseZipEntryName(saa[1]) - 1;
                if (index < 0) continue; // probably a nuclei/log entry
                //if (index > 30) continue;
                //println("ZipNuclei.main: finds: " + saa[1] + CS + ze.isDirectory() + CS + index);
                String s = zn.readLine(ze);
                if (s == null) {
                    if (nuclei_record.size() > index) {
                        nuclei_record.setElementAt(v, index);
                        if (index > iLastNucleiFile) iLastNucleiFile = index;
                    }
                    continue;
                }
                boolean newFormat = true;
                // detect old format by the absence of commas
                //if (debugCount++ < 10) println("readNuclei: " + s);
                if (s.indexOf(',') == -1) newFormat = false;
                int j = 1;
                // added code to handle some misformed nuclei files
                // a little squirrely maybe but it is working
                while (s != null && s.length() > 10) {
                    //if (index == 199) println("\n\nREADNUCLEI: " + s);
                    try {
                        String [] sa;
                        if (newFormat) sa = getTokens(s, 0);
                        else sa = getTokens(s);
                        if (    sa[0] != null
                            && (sa[0].length() > 0
                            && Integer.parseInt(sa[0]) != j++)) {
                            break;
                        }
                        if (newFormat) n = new Nucleus(sa);
                        else n = new Nucleus(sa, !newFormat);
                        v.add(n);
                        s = zn.readLine(ze);
                    } catch(Exception ee) {
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
                    if (index > iLastNucleiFile) iLastNucleiFile = index;
                }
                zn.closeEntry();
            }
        }
        //println("readNuclei: iEndingIndex=" + iEndingIndex + CS + iLastNucleiFile + CS + nuclei_record.size());
        if (iEndingIndex == 1) {
            iEndingIndex = iLastNucleiFile + 1;
            iConfig.iEndingIndex = iEndingIndex;
            for (int i=LAST - 1; i > iLastNucleiFile; i--) {
                nuclei_record.remove(i);
            }
        }
        //println("readNuclei: at end, nuclei_record.size: " + nuclei_record.size());

        //System.out.println("readNuclei:3 " + iMovie.time_end + CS + iMovie.time_start);
        return 9999;



    }

    @Override
	public Identity3 getIdentity() {
        return iIdentity;
    }

    @Override
	public Movie getMovie() {
        return iMovie;
    }

    @Override
	public Parameters getParameters() {
        return iParameters;
    }

    @Override
	public Config getConfig() {
        return iConfig;
    }

    @Override
	public void setConfig(Config config) {
    	iConfig = config;
    }

    @Override
	public MeasureCSV getMeasureCSV() {
    	return iMeasureCSV;
    }

    @Override
	public EditLog getEditLog() {
        return iEditLog;
    }

    @Override
	public double getZPixRes() {
        return iZPixRes;
    }

    @Override
	public String getIndex(String name, int time) {
        int k = 0;
        Vector nuclei = nuclei_record.elementAt(time - 1);
        Nucleus n = null;
        for (int j=0; j < nuclei.size(); j++) {
            n = (Nucleus)nuclei.elementAt(j);
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

    @Override
	public Nucleus findClosestNucleusXYZ(int mx, int my, float mz, int time) {
        Vector nuclei = nuclei_record.elementAt(time - 1);
        double x, y, z, r;
        boolean g;
        Nucleus candidate = null;
        double d = 100000;
        double xyz;
        mz *= iZPixRes;
        for (int j=0; j < nuclei.size(); j++) {
            Nucleus n = (Nucleus)nuclei.elementAt(j);
            System.out.print("findClosest..: " + n);
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

    @Override
	public Nucleus findClosestNucleus(int mx, int my, int time) {
        //System.out.println("findClosestNucleus: " + mx + CS + my + CS + time);
        Vector nuclei = nuclei_record.elementAt(time - 1);
        double x, y, r;
        boolean g;
        Nucleus candidate = null;
        double d = 100000;
        double xy;
        for (int j=0; j < nuclei.size(); j++) {
            Nucleus n = (Nucleus)nuclei.elementAt(j);
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

    @Override
	public Nucleus findClosestNucleus(int mx, int my, int mz, int time) {
        //System.out.println("findClosestNucleus: " + mx + CS + my + CS + mz + CS + time);
        if (time < 1) time = 1;
        Vector nuclei = null;
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
            Nucleus n = (Nucleus)nuclei.elementAt(j);
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


    @Override
	public Nucleus getCurrentCellData(String cellName, int time) {
        Vector nucData = nuclei_record.elementAt(time - 1);
        return NucUtils.getCurrentCellData(nucData, cellName);
    }

    @Override
	@SuppressWarnings("unused")
	public boolean isValidCell(String name, int time) {
        boolean rtn = false;
        Nucleus n = getCurrentCellData(name, time);
        iDLog.append("isValidCell " + name + CS + time);
        iDLog.append("isValidCell " + n);
        return (n != null);
    }

    @Override
	public Vector getNuclei(int time) {
        return nuclei_record.elementAt(time);
    }

    @Override
	@SuppressWarnings("unused")
	public Nucleus getNucleusFromHashkey(String hashKey, int time) {
        Nucleus r = null;
        boolean found = false;
        Vector nucData = nuclei_record.elementAt(time - 1);
        for (int j = 0; j < nucData.size(); j++) {
            Nucleus n = (Nucleus)nucData.elementAt(j);
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

    @Override
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

    @Override
	public boolean hasCircle(Nucleus n, double imgPlane) {
        return (nucDiameter(n, imgPlane) > 0);
    }

    @Override
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


    private void getScopeParameters() {



        if (!iFakeNuclei) {
            iMovie = iParameters.getMovie();
            iPlaneEnd = iMovie.plane_end;
            iZPixRes = iMovie.z_res/iMovie.xy_res*iParameters.z_res_fudge;
        } else {
                iPlaneEnd = iConfig.iPlaneEnd;
                iPlaneStart = iConfig.iPlaneStart;
                iZPixRes = iConfig.iZ_res/iConfig.iXy_res;

        }
        NucUtils.setZPixRes(iZPixRes);
    }

    @Override
	public void setParameterEntry(String parameterLocation) {
        iParameterEntry = PARAMETERS + "/" + parameterLocation + PARAMETERS;
    }

    @Override
	public String getParameterEntry() {
        return iParameterEntry;
    }

    @Override
	public Vector getParameterFileInfo() {
        Vector v = iParameters.getParameterFileInfo();
        //Vector v = iAceTree.getParameters().getParameterFileInfo();
        return v;
    }


    /**
     * called from AceTree when no nuclei file was specified
     * we will add either iEndingIndex or LAST empty nuclei vectors
     * LAST if iEndingIndex = 1 meaning no ending index was specified
     * if LAST then extras are removed after all nuclei files have been read
     */
    @Override
	@SuppressWarnings("unused")
	public void fakeNuclei() {
        //println("fakeNuclei: iEndingIndex: " + iEndingIndex);
        iFakeNuclei = true;
        nuclei_record = new Vector(); //[iEndingIndex - iStartingIndex + 1];
        Nucleus n = null;
        int last = LAST;
        //if (iEndingIndex > 1) last = iEndingIndex;
        for (int i=0; i < last; i++) {
            nuclei_record.add(new Vector());
            //n = new Nucleus(true); // a fake nucleus
            //if (i == 0) n.predecessor = -1;
            //n.setHashKey(NucUtils.makeHashKey(i + 1, n));
            //((Vector)nuclei_record.elementAt(i)).add(n);
        }
    }

    private void addFakeNuclei(Vector nuclei_record, int iEndingIndex) {
        //iFakeNuclei = true;
        //nuclei_record = new Vector(); //[iEndingIndex - iStartingIndex + 1];
        Nucleus n = null;
        for (int i=0; i < iEndingIndex; i++) {
            nuclei_record.add(new Vector());
            n = new Nucleus(true); // a fake nucleus
            if (i == 0) n.predecessor = -1;
            ((Vector)nuclei_record.elementAt(i)).add(n);
        }
    }

    @Override
	public boolean isFake() {
        return iFakeNuclei;
    }


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
            String s = makeImageName(i + 1, 1);
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


    @Override
	public void processNuclei(boolean doIdentity, int namingMethod) {
    	//println("reviewNuclei, 1");
    	//reviewNuclei();
        setAllSuccessors();
        if (iIdentity==null) iIdentity = new Identity3(this);
        iIdentity.setNamingMethod(getConfig().iNamingMethod);
        iIdentity.setPrintWriter(iPrintWriter);
    	//println("reviewNuclei, 2");
    	//reviewNuclei();
        if (doIdentity) {
        	iIdentity.identityAssignment();
        }
    	//println("reviewNuclei, 3");
    	//reviewNuclei();
        iAncesTree = new AncesTree(null, this, iStartingIndex, iEndingIndex);
    	//println("reviewNuclei, 4");
    	//reviewNuclei();

        Cell PP = (Cell)iAncesTree.getCellsByName().get("P");
        int kk = PP.getChildCount();
        println("NucleiMgr, constructor, " + kk + CS + PP.getName());

    }

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

    @Override
	public Hashtable getCellsByName() {
        return iAncesTree.getCellsByName();
    }

    @Override
	public AncesTree getAncesTree() {
        return iAncesTree;
    }



    /**
     * used in rebuild tree action
     *
     */
    @Override
	public void clearAllHashkeys() {
        //System.out.println("clearAllHashkeys: " + nuclei_record);
        Vector v = null;
        for (int i=0; i < nuclei_record.size(); i++) {
            v = nuclei_record.elementAt(i);
            for (int j=0; j < v.size(); j++) {
                ((Nucleus)v.elementAt(j)).hashKey = null;
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
    @Override
	public Vector  getNucleiRecord() {
        return nuclei_record;
    }

    @Override
	public void makeBackupNucleiRecord() {
//        System.out.println("makeBackupNucleiRecord");
//        nuclei_record_backup = new Vector();
//        Vector nuclei = null;
//        Vector nucleiNew = null;
//        Nucleus n = null;
//        Nucleus nNew = null;
//        for (int i=0; i < nuclei_record.size(); i++) {
//            nuclei = nuclei_record.elementAt(i);
//            nucleiNew = new Vector();
//            for (int j=0; j < nuclei.size(); j++) {
//                n = (Nucleus)nuclei.elementAt(j);
//                nNew = n.copy();
//                nucleiNew.add(nNew);
//            }
//            nuclei_record_backup.add(nucleiNew);
//        }
    }

    @Override
	public void restoreNucleiRecord() {
        //nuclei_record = nuclei_record_backup;
    }

    @Override
	public void setEndingIndex(int endTime) {
        iEndingIndex = endTime;
    }

    @Override
	public int getEndingIndex() {
        return iEndingIndex;
    }

    @Override
	public int getPlaneEnd() {
        return iPlaneEnd;
    }

    @Override
	public int getPlaneStart() {
        return iPlaneStart;
    }

    @Override
	public int getImageWidth() {
        return iImageWidth;
    }

    @Override
	public int getImageHeight() {
        return iImageHeight;
    }



    /**
     * access function for ZipNuclei member
     * @return ZipNuclei -- the file used to initialize this object
     */
    @Override
	public ZipNuclei getZipNuclei() {
        return iZipNuclei;
    }

    @Override
	public void setAllSuccessors() {
        //System.out.println("setAllSuccessors: " + iStartingIndex + CS + iEndingIndex + ", " + nuclei_record.size());
        //for (int i=iStartingIndex - 1; i < iEndingIndex; i++) {
        for (int i=iStartingIndex - 1; i < nuclei_record.size(); i++) {
            int r = setSuccessors(i);
            if (r != 0) break;
        }
    }

    @Override
	public int setSuccessors(int i) {
        //Vector [] nuclei_record = iNucleiMgr.getNucleiRecord();
        //if (i == 28) {
        //    System.out.println("setSuccessors: " + i);
        //}
        if (iConfig.iNamingMethod == Identity3.MANUAL) return 0;
        Vector now = nuclei_record.elementAt(i);
        Nucleus n = null;
        int m1 = Nucleus.NILLI;
        for (int j=0; j < now.size(); j++) {
            n = (Nucleus)now.elementAt(j);
            //println("setSuccessors3: " + n);
            n.successor1 = m1;
            n.successor2 = m1;
        }
        if (i == iEndingIndex - 1) return 1;
        Vector next;
        try {
            next = nuclei_record.elementAt(i + 1);
        } catch(ArrayIndexOutOfBoundsException oob) {
            return 1;
        }
        // first set all successors to -1
        for (int j=0; j < next.size(); j++) {
            n = (Nucleus)next.elementAt(j);
            //println("setSuccessors: " + n);
            if (n.status == Identity3.DEAD) 
            	continue;
            int pred = n.predecessor;
            if (pred == Identity3.DEAD) 
            	continue;
            //println("setSuccessors2: " + j + CS + pred);
            Nucleus p = null;
            try {
            	p = (Nucleus)now.elementAt(pred -1);
            } catch(Exception e) {
            	continue;
            }
            if (p.successor1 == m1) p.successor1 = j + 1;
            else if (p.successor2 == m1) p.successor2 = j + 1;
            else {
                System.out.println("error: MORE THAN 2 SUCCESSORS");
            }
        }
        return 0;
    }

    private void open(String s) {
        try {
            iFile = new File(s);
            iFOS = new FileOutputStream(iFile);
            iPWriter = new PrintWriter(iFOS);

        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

    }

    private void close() {
        try {
            iPWriter.close();
            iFOS.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void write(String s) {
        iPWriter.println(s);
    }

    private void printNuclei() {
        for (int i = 0; i< iMovie.tp_number; i++) {
            String name = "t" + EUtils.makePaddedInt(i + 1) + "-nuclei";
            open(name);
            Vector nuclei = nuclei_record.elementAt(i);
            for (int j=0; j<nuclei.size(); j++) {
                Nucleus nucleij = (Nucleus)nuclei.elementAt(j);
                write(nucleij.toString());
            }
            close();
        }
    }

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
         LAST = 599 // more than the most nuclei we ever expect to see
        ;


    final public static String
    PARAMETERS = "parameters"
        ,NL = "\n"
        ,CS = ", "
        ;

    public static void main(String[] args) {
        System.out.println("NucleiMgr test main entered");
        System.out.println("main exiting");
    }

    private void println(String s) {System.out.println(s);}


}
