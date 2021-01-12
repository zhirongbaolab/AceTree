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
    public int polar_size;
    public float z_res_fudge = 1; //20050805 initialization to 1 in case it is not in parameters file

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

    public static void main(String[] args) {
    }
}
