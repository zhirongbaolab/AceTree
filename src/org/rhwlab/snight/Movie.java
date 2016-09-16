/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 * Created on Jan 31, 2005
 *
 */
package org.rhwlab.snight;

import org.rhwlab.utils.C;

/**
 * a class to emulate a Movie_t struct in StarryNight
 * <br>In the June 2006 modifications the role for this
 * object was reduced, but it was retained to avoid unnecessary
 * changes elsewhere in the code.
 * At this point, only the xy_res and z_res parameters
 * are in use, and they are being handled via the AceTree
 * configuration file
 * 
 * @author biowolp
 * @version 1.1, June 2006
 */
public class Movie {
    public int time_start;
    public int time_end;
    public int plane_start;
    public int plane_end;
    public float xy_res;
    public float z_res;
    public int t_interval;
    public int tp_number;
    public int framewidth;
    public int frameheight;

    /**
     * 
     */
    public Movie() {
        super();
    }
    
    @Override
	public String toString() {
        String s = new String("Movie: ");
        s += String.valueOf(time_start) + C.CS;
        s += time_end + C.CS;
        s += plane_start + C.CS;
        s += plane_end + C.CS;
        s += tp_number;
        return s;
    }

    public static void main(String[] args) {
    }
}
