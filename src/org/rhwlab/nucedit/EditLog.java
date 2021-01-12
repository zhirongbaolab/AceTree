/*
 * Created on Jun 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.nucedit;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
// 20060719 I am trying to remove this function
// it is not used for its original purpose
// and we have speed issues that are hard to diagnose
// removing this is just a stab
public class EditLog /*extends Log*/ {
    
    boolean iModifiedAndNotSaved;
    
    public EditLog(String title) {
        //super(title);
    }
    
    public void setModified(boolean modified) {
        iModifiedAndNotSaved = modified;
    }
    
    public boolean getModified() {
        return iModifiedAndNotSaved;
    }
    
    // try dummy functions to make other code feasible
    public void append(String s) {
        s = null;
        // System.gc();
    }
    public void appendx(String s) {
        s = null;
        // System.gc();
    }
    public String getTime() {
        return "";
    }
    
    public void showMe() {
        System.out.println("EditLog removed 20060719");
    }

}
