/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */

package org.rhwlab.utils;
import java.awt.Polygon;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.text.MaskFormatter;

/**
 * A class to hold static simple utility functions.
 * <br>At this point, the two version of
 * <code>makePaddedInt</code> are the only things in use.
 *
 * @author biowolp
 * @version 1.0 January 18, 2005
 *
 */
public class EUtils {

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

    public static String padInt(int k) {
        String s = "00" + k;
        return s.substring(s.length() - 2);
    }

    /**
     * create a string version of an int padded with leading zeros
     * @param k int to be converted to string form
     * @return String representation of the int k
     */
    static public String makePaddedInt(int k) {
        int width = 3;
		int fullwidth=String.valueOf(k).length();
		width=Math.max(width,fullwidth);
        String s = "000" + String.valueOf(k);
        int j = s.length();
        s = s.substring(j - width, j);
        return s;
    }

    /**
     * create a String representation of int with leading
     * zeros used to padd it to specified width
     * @param k int to be converted to string form
     * @param width int number of characters in string
     * @return String representation of the int k
     */
    static public String makePaddedInt(int k, int width) {
		int fullwidth=String.valueOf(k).length();
		width=Math.max(width,fullwidth);

        String s = "0000" + String.valueOf(k);
        int j = s.length();
        s = s.substring(j - width, j);
        return s;
    }

    /**
     * Returns a comma separated string of the elements of the string array
     *
     * @param sa a String[]
     * @return a String
     */
    public static String stringArrayToString(String [] sa) {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i< sa.length; i++) {
            sb.append(sa[i]);
            sb.append(", ");
        }
        return sb.toString();
    }

    /**
     * debugging function
     *
     */
    static public void printMs() {
        System.out.println(System.currentTimeMillis());
    }


    /**
     * fast file copy utility function
     * @param inName String name of input file
     * @param otName String name of output file
     * @throws Exception
     */
    @SuppressWarnings("resource")
	static public void copyFile(String inName,  String otName) throws Exception {
        File inFile = null;
        File otFile = null;
        try {
            inFile = new File(inName);
            otFile = new File(otName);
        } catch(Exception e) {
            e.printStackTrace();
        }
        if (inFile == null || otFile == null) return;
        FileChannel sourceChannel = new
             FileInputStream(inFile).getChannel();
        FileChannel destinationChannel = new
             FileOutputStream(otFile).getChannel();
        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
        sourceChannel.close();
        destinationChannel.close();
    }

    /**
     * returns a polygon approximation to a circle of radius r
     * at location x, y
     * @param x
     * @param y
     * @param r
     * @return Polygon object approximation of circle
     */
    static public Polygon pCircle(int x, int y, int r) {
        Polygon p = new Polygon();
        for (int k = -r; k < r; k += 1) {
            int m = x + k;
            int n = (int)Math.sqrt(r*r - k*k);
            p.addPoint(m, y + n);
        }
        for (int k = r; k > -r; k -= 1) {
            int m = x + k;
            int n = (int)Math.sqrt(r*r - k*k);
            p.addPoint(m, y - n);
        }
        return p;
    }


    static public Polygon pPoly(int x, int y, int r) {
        Polygon p = new Polygon();
        double M = 4;
        double R = r;
        for (double m= 0; m < M; m++) {
            double v = R * Math.sqrt(4 * m / M - 4 * m * m /M /M);
            double u = R * (2 * m / M - 1);
            int a = x + (int)Math.round(u);
            int b = y + (int)Math.round(v);
            p.addPoint(a, b);
        }
        for (double m= M; m > 0; m--) {
            double v = R * Math.sqrt(4 * m / M - 4 * m * m /M /M);
            double u = R * (2 * m / M - 1);
            int a = x + (int)Math.round(u);
            int b = y - (int)Math.round(v);
            p.addPoint(a, b);
        }
        //p.npoints = (int)M + 1;
        return p;
    }

    //A convenience method for creating a MaskFormatter.
    public static MaskFormatter createFormatter(String s) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(s);
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
            System.exit(-1);
        }
        return formatter;
    }

    /**
     * test main program
     * @param args String []
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String inPath = "/home/biowolp/wolp/stuff/gamma2/test/4to100cells-1min/tiffs/centroids/";
        String name1 = "4to100cells-";
        String name2 = "t071-p15.tif";
        String otPath = "/home/biowolp/work/temp/4to100cells-1min/centroids/";
        String src = inPath + name1 + name2;
        String dst = otPath + name2;
        System.out.println("src: " + src);
        System.out.println("dst: " + dst);
        printMs();
        copyFile(src, dst);
        printMs();


    }
}
