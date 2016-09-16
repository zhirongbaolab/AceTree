package org.rhwlab.manifest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/*
 * Created on Apr 18, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ManifestX {
    
    static JarFile      cJarFile;
    static Manifest     cManifest;
    static Hashtable    cManifestHash;
    
    public static void reportAndUpdateManifest() {
        String s = "Running from jar";
        if (!amRunningFromJar()) {
            s = "NOT " + s;
            updateManifest();
        }
        showManifest();
    }
    
    public static void showManifest() {
        if (cJarFile != null) showManifestFromJar();
        else showManifestFromHash();
    }
    
    @SuppressWarnings("resource")
	public static void showManifestFromJar() {
        String jarFileName = System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0];
        println("jarFileName, " + jarFileName);
        try {
            JarFile jar = new JarFile(jarFileName);
            Manifest manifest = jar.getManifest();
            Map entries = manifest.getMainAttributes();
            Iterator iter = entries.entrySet().iterator();
            for (int i=0; i < entries.size(); i++) {
                Map.Entry me = (Map.Entry)iter.next();
                Object key = me.getKey();
                String value = (String)me.getValue();
                println("" + i + CS + key + CS + value);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void showManifestFromHash() {
        Enumeration keys = cManifestHash.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)cManifestHash.get(key);
            println(key + CS + value);
        }
    }
    
    public static boolean amRunningFromJar() {
        if (cJarFile != null) return true;
        String pathToJar = System.getProperty("java.class.path");
        System.out.println("pathToJar: " + pathToJar);
        String ps = ":";
        int k = pathToJar.indexOf(ps);
        boolean b =  k < 0;
        String s = "RUNNING FROM JAR";
        if (!b) s = "NOT " + s;
        println(s);
        if (b) {
            String jarFileName = System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0];
            try {
                cJarFile = new JarFile(jarFileName);
                cManifest = cJarFile.getManifest();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return b;
    }
    

    public static void updateManifest() {
        cManifestHash = new Hashtable();
        File f = new File(SRC_MANIFEST_FILE);
        Vector v = new Vector();
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String sr = br.readLine();
            while (sr != null && sr.length() > 2) {
                String [] sa = sr.split(": ");
                cManifestHash.put(sa[0], sa[1]);
                if (sr.indexOf("Manifest-Version") < 0) v.add(sr);
                sr = br.readLine();
            }
            br.close();
            PrintStream ps = new PrintStream(new FileOutputStream(f));
            String version = makeVersion();
            cManifestHash.put("Manifest-Version", version);
            ps.println("Manifest-Version: " + version);
            println("updateManifest: " + version);
            for (int i=0; i < v.size(); i++) {
                String s = (String)v.get(i);
                ps.println(s);
            }
            ps.flush();
            ps.close();
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    static public String makeVersion() {
        Calendar c = new GregorianCalendar();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hr = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        
        String version = String.valueOf(year) + padInt(month)
            + padInt(day) + padInt(hr) + padInt(min);
        return version;

    }
    
    public static String getManifestValue(String property) {
        if (cJarFile != null) {
            Attributes attr = cManifest.getMainAttributes();
            return attr.getValue(property);
        } else {
            return (String)cManifestHash.get(property);
        }
    }
    
    
    public static String padInt(int k) {
        String s = "00" + k;
        return s.substring(s.length() - 2);
    }
    
    public static final String SRC_MANIFEST_FILE = "source/org/rhwlab/manifest/manifest.txt";    
    
    public static void main(String[] args) {
        reportAndUpdateManifest();
    }
    
    private static void println(String s) {
    	System.out.println(s);
	
    }
    private static final String CS = ", ", C = ",";
}
