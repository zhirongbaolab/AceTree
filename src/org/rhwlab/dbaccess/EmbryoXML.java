/*
 * Created on Jun 21, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.dbaccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import org.rhwlab.manifest.ManifestX;

import qdxml.DocHandler;
import qdxml.QDParser;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EmbryoXML implements DocHandler {

	public String[] iRecord;
	public Hashtable<String, Integer> iTags;

	public EmbryoXML() {
		iRecord = new String[elem.length];
		iTags = new Hashtable<String, Integer>();
		for (int i = 0; i < elem.length; i++) {
			iTags.put(elem[i], new Integer(i));
			iRecord[i] = "n/a";
		}
		iRecord[elem.length - 1] = statuses[0];

	}

	public EmbryoXML(String embryoName) throws FileNotFoundException {
		this(embryoName, DBAccess.cDBLocation);
	}

	// FileNotFoundException thrown does not seem to affect Orientation window
	public EmbryoXML(String embryoName, String dbPath) throws FileNotFoundException {
		this();
		if (DBAccess.cDBLocation == null) {
			ManifestX.reportAndUpdateManifest();
			DBAccess.cDBLocation = ManifestX.getManifestValue("DBLocation");
		}
		System.out.println("DBAccess.cBDLocation: "+DBAccess.cDBLocation);
		if (dbPath == null)
			dbPath = DBAccess.cDBLocation;
		if (!dbPath.endsWith("/"))
			dbPath += "/";
		
		//String path = dbPath + embryoName + ".xml";
		String path = embryoName;
		boolean OK = true;
		//String path = EmbryoDB.cDBLocation + "/" + embryoName + ".xml";
		try {
			FileReader fr = new FileReader(path);
			QDParser.parse(this, fr);
		} catch (Exception e) {
			println("EmbryoXML: exception: " + e);
			//e.printStackTrace();
			OK = false;
		}
		/*
		catch (FileNotFoundException fnfe) {
			println("EmbryoXML: filenotfoundexception: " + fnfe);
			// fnfe.printStackTrace();
			OK = false;
		}
		*/
		if (!OK) {
			throw (new FileNotFoundException(path));
		}
	}

	public EmbryoXML(String path, int x) {
		this();
		// println("EmbryoXML, " + path);
		try {
			FileReader fr = new FileReader(path);
			QDParser.parse(this, fr);
		} catch (FileNotFoundException fnfe) {
			println("EmbryoXML: filenotfoundexception: " + fnfe);
			fnfe.printStackTrace();
		} catch (Exception e) {
			println("EmbryoXML: exception: " + e);
			e.printStackTrace();
		}

	}

	public static void writeXML(String filePath, String[] data) {
		int k = filePath.lastIndexOf("/");
		String s = "";
		if (k == filePath.length() - 1)
			s = filePath + data[SERIES];
		else
			s = filePath + "/" + data[SERIES];
		// println("writeXML: " + s + CS + data[SERIES]);
		// need provision for not allowing a new series with same
		// name as an existing one
		try {
			FileOutputStream fos = new FileOutputStream(s + XML);
			PrintWriter pw = new PrintWriter(fos);
			pw.println(HEADER);
			pw.println();
			pw.println(EXPERIMENT);
			for (int i = 0; i < elem.length; i++) {
				// println("writeXML: " + i + data[i]);
				if (data[i] == null || data[i].length() == 0)
					continue;
				s = S + elem[i] + SP + att[i] + EQ + data[i] + E;
				pw.println(s);
			}
			pw.println(EEXPERIMENT);
			pw.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}

	}

	public static final String S = "<", SP = " ", E = "\"/>", EQ = "=\"",
			XML = ".xml", HEADER = "<?xml version='1.0' encoding='utf-8'?>",
			EXPERIMENT = "<experiment>", EEXPERIMENT = "</experiment>";

	public static final int SERIES = 0, DATE = 1, PERSON = 2, STRAIN = 3,
			TREATMENTS = 4, REDSIGNAL = 5, IMAGELOC = 6, TIMEPOINTS = 7,
			ANNOTS = 8, ATCONFIG = 9, EDITEDBY = 10, EDITEDTP = 11,
			EDITEDCELLS = 12, CHECKED = 13, COMMENTS = 14, STATUS = 15;

	// 
	public static String[] elem = { "series", "date", "person", "strain",
		"treatments", "redsig", "imageloc", "timepts", "annots", "acetree",
		"editedby", "editedtimepts", "editedcells", "checkedby",
		"comments", "status"
	};

	public static String[] att = { 
		"name" // series
		, "date" // date
		, "name" // person
		, "name" // strain
		, "desc" // treatments
		, "value" // red signal
		, "loc" // image location
		, "num" // time points
		, "loc" // annotation location
		, "config" // acetree
		, "name" // edited by
		, "num" // edited TP
		, "num" // edited cells
		, "name" // checked
		, "text" // comments
		, "case" // status
	};

	public static String[] statuses = { "new", "archived", "deleted", "arc1",
			"arc2", "arc3", "del1"
	};

	public static final int NEWSTATUS = 0, ARCHIVEDSTATUS = 1,
			DELETEDSTATUS = 2, ARC1STATUS = 3, ARC2STATUS = 4, ARC3STATUS = 5,
			DEL1STATUS = 6;

	/*
	 * (non-Javadoc)
	 *
	 * @see qdxml.DocHandler#startElement(java.lang.String, java.util.Hashtable)
	 */
	// AceTree exits when exception is thrown
	@Override
	public void startElement(String tag, Hashtable<?, ?> h) throws Exception {
		System.out.println("Looking in EmbryoXML for startElement for tag: "+tag);
		if (tag.equals("experiment")) {
			;
		} else {
			// println("startElement: " + tag);
			try {
				int m = iTags.get(tag).intValue();
				System.out.println("EmbryoXML got integer: "+m+" from tag: "+tag);
				iRecord[m] = (String) h.get(att[m]);
			} catch (Exception e) {
				System.out.println("Cannot retrieve Integer value for tag: "+tag);
				e.printStackTrace();
				println("startElement: " + tag + CS + h);
				System.exit(1);
				//return;
			}

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see qdxml.DocHandler#endElement(java.lang.String)
	 */
	@Override
	public void endElement(String tag) throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see qdxml.DocHandler#startDocument()
	 */
	@Override
	public void startDocument() throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see qdxml.DocHandler#endDocument()
	 */
	@Override
	public void endDocument() throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see qdxml.DocHandler#text(java.lang.String)
	 */
	@Override
	public void text(String str) throws Exception {
		// TODO Auto-generated method stub

	}

	public static void updateRecord(String[] data) {
		data[EmbryoXML.ANNOTS] = data[EmbryoXML.IMAGELOC];
		data[EmbryoXML.ATCONFIG] = data[EmbryoXML.SERIES] + ".dat";

	}

	public static void updateDatabase(String newLoc) {
		File dir = new File(DBAccess.cDBLocation);
		// File dir = new File(EmbryoDB.cDBLocation);
		String[] list = dir.list();
		for (int i = 0; i < list.length; i++) {
			if (i < 2)
				continue;
			if (i > 4)
				break;
			String s = list[i];
			if (!s.endsWith(".xml"))
				continue;
			int k = s.indexOf(".xml");
			s = s.substring(0, k);
			println("updateDatabase, " + i + CS + s);
			try {
				EmbryoXML exml = new EmbryoXML(s);
				updateRecord(exml.iRecord);
				EmbryoXML.writeXML(newLoc, exml.iRecord);
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			}
		}

	}

	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) {
		String dbloc = "/nfs/waterston/embryoDBnew/";
        // DBAccess.cDBLocation = "/nfs/waterston/embryoDB";
        // EmbryoDB.cDBLocation = "/nfs/waterston/embryoDB";
        // String newLoc = "/nfs/waterston/embryoDB2";
        // updateDatabase(newLoc);
    	String series = "081505";
    	//series = "110805pha4_pop";
    	//series = "20060516_mir_57";
    	
    	String series1 = "20071004cyd1";
    	String series2 = "20071004cyd1";
    	//series = "20070919_pha-4_E3xx";
    	//series = "20090220_egl-5_7E3_12_L2";
    	//series = "20080125_die-1_e12_2";
    	//series = "20080124_pha4b2a_end1d";
    	String seriex = null;
    	seriex = "20080125_ceh-43_1";
    	
    	seriex = "20080125_ceh-43_1";
    	series = "20080125_ceh-43_1";
    	boolean bx = series1.equals(series2);
    	boolean bxx = series.equals(seriex);
    	println("" + bx + CS + bxx);
    	String testFile = dbloc + series + ".xml";

    	try {
    		FileInputStream fis = new FileInputStream(testFile);
    	} catch(IOException ioe) {
    		ioe.printStackTrace();
    	}

    	try {
    		EmbryoXML exml = new EmbryoXML(series, null);
    	} catch(FileNotFoundException fnfe) {
    		fnfe.printStackTrace();
    	}
    	println("A-OK");

    }

	private static void println(String s) {
		System.out.println(s);
	}

	private static final String CS = ", ";

}
