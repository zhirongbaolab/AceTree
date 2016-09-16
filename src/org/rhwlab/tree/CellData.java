/*
 * Created on Nov 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.tree;

import org.rhwlab.snight.Nucleus;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CellData {
    //public int iRweight;
    public Nucleus iNucleus;
    
    @SuppressWarnings("unused")
	public CellData(Nucleus n) {
        double size = n.size;
        double ratio = NOMINALSIZE/size;
        double ratiocubed = ratio*ratio*ratio;
        //iRweight = (int)(n.rweight * ratiocubed);
        //iRweight = n.rweight;
        iNucleus = n;
    }
    
    @Override
	public String toString() {
        //String s = "CellData: " + iRweight;
        String s = "CellData: " + iNucleus.rweight;
        return s;
    }
    
    private static final double
         NOMINALSIZE = 40;

}
