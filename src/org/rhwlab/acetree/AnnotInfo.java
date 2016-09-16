package org.rhwlab.acetree;
import java.io.IOException;

/*
 * Created on Jan 14, 2005
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
public class AnnotInfo {

    /**
     * 
     */
    public String iName;
    public int iX;
    public int iY;
        
    public AnnotInfo(String name, int x, int y) {
    	//System.out.println("Annotation info name: "+name);
    	// Make it so that annotation only shows proper name if proper name is present
    	/*
    	int space = name.indexOf(" ");
    	if (space >= 0)
    		name = name.substring(space+1, name.length());
		*/
        iName = name;
        iX = x;
        iY = y;
    }
    
    @Override
	public String toString() {
        String s = iName + ", " + iX + ", " + iY;
        return s;
    }
    
    @Override
	protected void finalize() throws IOException {
        //System.out.println("AnnotInfo: " + iName);
        //this = null;
    }

}
