/*
 * Created on Sep 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.utils;

import java.util.Arrays;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Line {

    public StringBuffer iBuf;
    int iLast = 0;
    int gap;
    
    public Line() {
        char [] ca = new char[150];
        Arrays.fill(ca, ' ');
        iBuf = new StringBuffer(new String(ca));
        gap = GAP;
    }
    
    public void add(int x) {
        String s = makePaddedInt(x);
        add(s);
    }
    
    public void add(String s) {
        int len = s.length();
        iBuf.replace(iLast, iLast + len, s);
        iLast += len + gap;
    }
    
    public String makePaddedInt(int k) {
        int width = 4;
        String s = "    " + String.valueOf(k);
        int j = s.length();
        s = s.substring(j - width, j);
        return s;
    }
    
    @Override
	public String toString() {
        return iBuf.toString().trim();
    }
    
    public void setGap(int g) {
        gap = g;
        if (g == 0) gap = GAP;
    }
    
   

    private static final int 
         GAP = 1
        ;
    
    
    public static void main(String[] args) {
        // note gaps are added after an item addition
        Line a = new Line();
        a.setGap(30);
        a.add("test");
        a.add(234);
        a.setGap(0);
        a.add(2);
        a.add(43);
        System.out.println(a);
    }
}
