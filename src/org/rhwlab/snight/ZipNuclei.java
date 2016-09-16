/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */
package org.rhwlab.snight;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipException;

/**
 * Instantiated in AncesTree.
 * <br>Opens the zip file holding data and analyses files.
 * <br>Reads data line be line from a selected file in the zip.
 * <br>Holds iZipFile, an instance of java.util.zip.ZipFile and makes
 * it available via an access function.
 *
 * @author biowolp
 * @version 1.0 Jan 11, 2005
 */
public class ZipNuclei {
    String iZipName;
    String iEntryName;
    ZipFile iZipFile;
    InputStream iInputStream;
    BufferedReader iBufferedReader;

    /**
     * constructor
     * @param zipName String path to the zip file with nuclei and parameters
     */
    public ZipNuclei(String zipName) {
        super();
        //println("ZipNuclei(" + zipName + ")");
        iZipName = zipName;
        iInputStream = null;
        File zipFile = new File(zipName);
        try {
            //println("ZipNuclei constructor, " + iZipName);
            //iZipFile = new ZipFile(iZipName);
            iZipFile = new ZipFile(zipFile);
            //println(zipName+" has size "+iZipFile.size());
        } catch(IOException ioe) {
            if (ioe instanceof ZipException) {
                System.out.println("got ZipException: " + ioe);
                System.out.println("looking for: " + iZipName);
                //ioe.printStackTrace();
                //System.out.println("exiting -- unable to find zipFile");
                //ioe.printStackTrace();
                //System.exit(0);
            }
            else if(ioe instanceof FileNotFoundException)
            	System.out.println("File "+iZipName+" not found. Cannot open zip file.");
        }
        //System.out.println("using iZipFile: " + iZipName + CS + iZipFile);
    }

    /**
     * returns one entry from the zip file
     * @param entryName String name of entry
     * @return the ZipEntry object for the given name
     */
    public ZipEntry getZipEntry(String entryName) {
        ZipEntry ze = null;
        Enumeration entries = iZipFile.entries();
        // list the contents of each zipped entry
        while (entries.hasMoreElements()) {
          ZipEntry e = (ZipEntry) entries.nextElement();
          if (e.getName().equals(entryName)) {
              ze = e;
              break;
          }
        }
        return ze;
    }

    /**
     * reads one line from a zip entry
     * @param entry the entry in hand
     * @return String or null if end of entry is found
     */
    public String readLine(ZipEntry entry) {
        //System.out.println("ZipNuclei.readLine: " + entry);
        //new Throwable().printStackTrace();
        String s = null;
        try{
            if (iInputStream == null) {
                iInputStream = iZipFile.getInputStream(entry);
                iBufferedReader = new BufferedReader(new InputStreamReader(iInputStream));
            }
            s = iBufferedReader.readLine();
            if (s == null) {
                closeEntry();
            }


        } catch(IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
        return s;
    }

    /**
     * closes the currently open entry and cleans up
     * <br> this is necessary before accessing a different entry
     *
     */
    public void closeEntry() {
        if(iInputStream == null) return;
        try {
            iBufferedReader.close();
            iInputStream.close();
            iInputStream = null;
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void close() {
        try {
            iZipFile.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * accesses the ZipFile object of this ZipNuclei instance
     * @return ZipFile
     */
    //public ZipFile getZipFile() {
    //   return iZipFile;
    //}

    public static final String CS = ", ";

    /**
     * unused main function
     * @param args
     */
    @SuppressWarnings("unused")
	public static void main(String[] args) {
        ZipNuclei zn = new ZipNuclei("/home/biowolp/data/081505/081505-edit.zip");
        //println("ZipNuclei.main: " + zn.iZipFile);
        Enumeration e = zn.iZipFile.entries();
        while (e.hasMoreElements()) {
            ZipEntry ze = (ZipEntry)e.nextElement();
            String [] sa = zn.parseZipEntry(ze);
            if (sa[0].equals("nuclei")) {
                //println("ZipNuclei.main: finds: " + sa[1] + CS + ze.isDirectory() + CS + zn.parseZipEntryName(sa[1]));
                String s = zn.readLine(ze);
                //println("ZipNuclei: string: " + s);
                zn.closeEntry();
            }
        }
    }

    public String [] parseZipEntry(ZipEntry ze) {
        String [] sa = ze.getName().split("/");
        return sa;
    }

    public int parseZipEntryName(String s) {
        //println("parseZipEntryName: " + s);
        int m = s.indexOf("-");
        if (m < 0) return m;
        s = s.substring(1, m);
        return Integer.parseInt(s);
    }

    private static void println(String s) {System.out.println(s);}
    //private static final String CS = ", ";

}
