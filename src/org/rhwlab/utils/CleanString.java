package org.rhwlab.utils;
import java.util.Arrays;

/*
 * Created on Jun 17, 2005
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
public class CleanString {
    
    StringBuffer iSB;
    int iPos;
    
    public CleanString() {
        char[] chars = new char[100];
        Arrays.fill(chars, ' ');
        iSB = new StringBuffer(new String(chars));
        iPos = 0;
    }

    public void insertText(String text) {
        int k = iPos + text.length();
        iSB.replace(iPos, k, text);
        iPos = k;
        k = iPos + CS.length();
        iSB.replace(iPos, k, CS);
        iPos = k;
    }
    
    
    public void insertX(int x) {
        insertX(x, true);
    }
    
    public void insertX(int x, boolean putComma) {
        String s = makePaddedInt(x);
        int k = iPos + s.length();
        iSB.replace(iPos, k, s);
        if (putComma) {
            iPos = k;
            addComma();
        }
    }
    
    public void addComma() {
        int k = iPos + CS.length();
        iSB.replace(iPos, k, CS);
        iPos = k;
    }
    
    public String makePaddedInt(int k) {
        int width = 4;
        String s = "    " + String.valueOf(k);
        int j = s.length();
        s = s.substring(j - width, j);
        return s;
    }
    
    public void setPosition(int k) {
        iPos = k;
    }
    
    @Override
	public String toString() {
        return iSB.toString();
    }

    public static final String 
         CS = ", "
        ;

    public static void main(String[] args) {
    }
}
