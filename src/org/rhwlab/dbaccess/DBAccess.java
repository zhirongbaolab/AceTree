package org.rhwlab.dbaccess;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Vector;

import org.rhwlab.manifest.ManifestX;



public class DBAccess {

    //public static String cDBLocation = "/nfs/waterston/embryoDBnew";
    public static String cDBLocation;
    Vector      iEmbryos;
    public static Hashtable   iEmbryosHash; //uses SERIES as the hashkey
    long        iStartDate;
    int         iLookedAt;



    public DBAccess(long startdate) {
        iStartDate = startdate;
        getEmbryos();
        //println("DBAccess, " + iLookedAt + CS + iEmbryos.size());
        //for (int i=0; i < iEmbryos.size(); i++) {
        //    EmbryoXML e = (EmbryoXML)iEmbryos.get(i);
        //    println(i + CS + e.iRecord[EmbryoXML.SERIES]);
        //}
    }
    /*
    public static Embryo getEmbryo(String seriesName) {
        Embryo embryo = null;
        File f = new File(cDBLocation);
        File [] files = f.listFiles();
        for (int i=0; i < files.length; i++) {
            String candidateName = files[i].getName();
            //println("getEmbryo, " + candidateName);
            if (candidateName.equals(seriesName)) {
                String absPath = files[i].getAbsolutePath();
                EmbryoXML exml = new EmbryoXML(absPath, 0);
                embryo = new Embryo(exml);

            }
        }
        return embryo;

    }
    */

    public Vector getEmbryosV() {
    	return iEmbryos;
    }


    public void getEmbryos() {
        iEmbryos = new Vector();
        iEmbryosHash = new Hashtable();
        File f = new File(cDBLocation);
        File [] files = f.listFiles();
        for (int i=0; i < files.length; i++) {
            if (!files[i].isFile()) continue;
            iLookedAt++;
            String absPath = files[i].getAbsolutePath();
            EmbryoXML exml = new EmbryoXML(absPath, 0);
            if (isOfInterest(exml)) {
                iEmbryos.add(exml);
                iEmbryosHash.put(exml.iRecord[EmbryoXML.SERIES], exml);
                //println("getEmbryos, " + exml.iRecord[EmbryoXML.SERIES]);
            }
        }
    }




    private boolean isOfInterest(EmbryoXML exml) {
        String status = exml.iRecord[EmbryoXML.STATUS];
        if (status.equals("deleted")) return false;
        String date = exml.iRecord[EmbryoXML.DATE];
        long ldate = Long.parseLong(date);
        if (ldate < iStartDate) return false;
        return true;
    }



    private static long start;
    private static long inter;
    private static long now;
    private static long end;

    @SuppressWarnings("unused")
	public static void main(String [] args) {
        println("DBAccess.main, ");
        start = System.currentTimeMillis();
        inter = start;
        ManifestX.reportAndUpdateManifest();
        cDBLocation = ManifestX.getManifestValue("DBLocation");
        println("DBCheck.main, cDBLocation, " + cDBLocation);
        DBAccess dba = new DBAccess(20050101);
        //dba.processEmbryo("081505");


    }

    private static void println(String s) {System.out.println(s);}
    private static void print(String s) {System.out.print(s);}
    private static final String CS = ", ";
    private static final DecimalFormat DF0 = new DecimalFormat("####");
    private static final DecimalFormat DF1 = new DecimalFormat("####.#");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt1(double d) {return DF1.format(d);}
    private static String fmt0(double d) {return DF1.format(d);}
}
