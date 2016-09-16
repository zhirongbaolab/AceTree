/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */
package org.rhwlab.snight;

import java.util.Vector;

/**
 * a class to emulate a Parameters_t struct in StarryNight
 * <br>In the June 2006 modification, the notion of reading the 
 * StarryNite parameters file was eliminated. Instead, the
 * three parameters that we really need in AceTree,
 * xy_res, z_res, and polar_size are now set in Config
 * with defaults that match all series so far and the option
 * of specifying different values in the configuration file
 * The structure of this object was retained however
 * <br>most of the parameters only require package access since they
 * are not used outside of this package
 * <br>Movie and z_res_fudge are exceptions
 * @author biowolp
 * @version 1.1 June 8, 2006
 *
 */
public class Parameters {
    public Movie iMovie;
    int neighborhood_size;
    int cen_ct_limit;
    int nuc_size;
    public int polar_size;
    int max_nuc_size;
    float nuc_size_factor1;
    float nuc_size_factor2;
    float nuc_size_factor3;
    float nuc_size_factor4;
    public float z_res_fudge = 1; //20050805 initialization to 1 in case it is not in parameters file
    float noise_fraction;
    float nuc_weight_cutoff;
    float mask_factor;
    int minimal_cell_cycle;
    float ambiguity_cutoff;
    public int axis;
    public int ap;
    public int lr;
    public int dv;
    public int apInit = 1;
    public int lrInit = 1;
    public int dvInit = 1;
    public int t;
    Vector parameterFileData;

    /**
     * 
     */
    public Parameters() {
        iMovie = new Movie();
    }
    
    public Movie getMovie() {
        return iMovie;
    }

    public Vector getParameterFileInfo() {
        return parameterFileData;
    }

    final static private String 
         TIME_START = "time_start"
        ,TIME_END = "time_end"
        ,PLANE_START = "plane_start"
        ,PLANE_END = "plane_end"
        ,XY_RES = "xy_res"
        ,Z_RES = "z_res"
        ,TIME_INTERVAL = "time_interval"
        ,NEIGHBORHOOD_SIZE = "neighborhood_size"
        ,CEN_CT_LIMIT = "cen_ct_limit"
        ,NUC_SIZE = "nuc_size"
        ,POLAR_SIZE = "polar_size"
        ,MAX_NUC_SIZE = "max_nuc_size"
        ,NUC_SIZE_FACTOR1 = "nuc_size_factor1"
        ,NUC_SIZE_FACTOR2 = "nuc_size_factor2"
        ,NUC_SIZE_FACTOR3 = "nuc_size_factor3"
        ,NUC_SIZE_FACTOR4 = "nuc_size_factor4"
        ,Z_RES_FUDGE = "z_res_fudge"
        ,NOISE_FRACTION = "noise_fraction"
        ,NUC_WEIGHT_CUTOFF = "nuc_weight_cutoff"
        ,MASK_FACTOR = "mask_factor"
        ,MINIMAL_CELL_CYCLE = "minimal_cell_cycle"
        ,AMBIGUITY_CUTOFF = "ambiguity_cutoff"
        ;
    

    final static private int 
         NUM_PARAMS = 22
        ;

    final static private char
         HASH = '#'
        ;
    
    final static private String 
        CS = ", "
       ;
    public static void main(String[] args) {
    }
}
/**
 * fills in the parameters from an entry
 * in the ZipNuclei object 
 * <br> this is essentially a parameters file coming out of 
 * StarryNight
 * @param zn ZipNuclei object representing the zip input file
 * containing nuclei and parameters
 * @param entryName the String entryname corresponding to parameters
 */
/*
public void setParameters(ZipNuclei zn, String entryName) {
    System.out.println("setParameters: " + zn + CS + entryName);
    ZipEntry ze = zn.getZipEntry(entryName);
    int found = 0;
    String s0 = null;
    String s1 = null;
    String [] sa = null;
    String s = null;
    parameterFileData = new Vector();
    while ((s = zn.readLine(ze)) != null && found < NUM_PARAMS) {
        parameterFileData.add(s);
        if (s.length() < 3 || s.charAt(0) == HASH) continue;
        sa = s.split(" ");
        if (sa.length < 2) continue;
        s0 = sa[0];
        s1 = sa[1];
        if (s0.equals(TIME_START)) {iMovie.time_start = Integer.parseInt(s1); found++;}   
        else if (s0.equals(TIME_END)) {iMovie.time_end = Integer.parseInt(s1); found++;}   
        else if (s0.equals(PLANE_START)) {iMovie.plane_start = Integer.parseInt(s1); found++;}   
        else if (s0.equals(PLANE_END)) {iMovie.plane_end = Integer.parseInt(s1); found++;}   
        else if (s0.equals(XY_RES)) {iMovie.xy_res = Float.parseFloat(s1); found++;}   
        else if (s0.equals(Z_RES)) {iMovie.z_res = Float.parseFloat(s1); found++;}   
        else if (s0.equals(TIME_INTERVAL)) {iMovie.t_interval = Integer.parseInt(s1); found++;}   
        
        else if (s0.equals(NEIGHBORHOOD_SIZE)) {neighborhood_size = Integer.parseInt(s1); found++;}   
        else if (s0.equals(CEN_CT_LIMIT)) {cen_ct_limit = Integer.parseInt(s1); found++;}   
        else if (s0.equals(NUC_SIZE)) {nuc_size = Integer.parseInt(s1); found++;}   
        else if (s0.equals(POLAR_SIZE)) {polar_size = Integer.parseInt(s1); found++;}   
        else if (s0.equals(MAX_NUC_SIZE)) {max_nuc_size = Integer.parseInt(s1); found++;}   
        else if (s0.equals(NUC_SIZE_FACTOR1)) {nuc_size_factor1 = Float.parseFloat(s1); found++;}   
        else if (s0.equals(NUC_SIZE_FACTOR2)) {nuc_size_factor2 = Float.parseFloat(s1); found++;}   
        else if (s0.equals(NUC_SIZE_FACTOR3)) {nuc_size_factor3 = Float.parseFloat(s1); found++;}   
        else if (s0.equals(NUC_SIZE_FACTOR4)) {nuc_size_factor4 = Float.parseFloat(s1); found++;}   
        else if (s0.equals(Z_RES_FUDGE)) {z_res_fudge = Float.parseFloat(s1); found++;}   
        else if (s0.equals(NOISE_FRACTION)) {noise_fraction = Float.parseFloat(s1); found++;}   
        else if (s0.equals(NUC_WEIGHT_CUTOFF)) {nuc_weight_cutoff = Float.parseFloat(s1); found++;}   
        else if (s0.equals(MASK_FACTOR)) {mask_factor = Float.parseFloat(s1); found++;}   
        else if (s0.equals(MINIMAL_CELL_CYCLE)) {minimal_cell_cycle = Integer.parseInt(s1); found++;}   
        else if (s0.equals(AMBIGUITY_CUTOFF)) {ambiguity_cutoff = Float.parseFloat(s1); found++;}   
    }
    iMovie.tp_number = iMovie.time_end - iMovie.time_start + 1;
    System.out.println("setParameters tp_number: " + iMovie.tp_number);
    iMovie.framewidth = 495;
    iMovie.frameheight = 380;
    t = 0;
}
*/    

