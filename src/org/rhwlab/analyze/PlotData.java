/*
 * Created on Nov 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

import org.rhwlab.utils.C;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlotData {

    public static void main(String[] args) {
    }
    public double [] yValues;
    public double [] xValues;
    public PlotData(double [] x, double [] y) {
        xValues = x;
        yValues = y;
    }
        
    public void showMe() {
        for (int i=0; i < yValues.length; i++) {
            System.out.println("PlotData: " + i + C.CS + xValues[i] + C.CS + yValues[i]);
        }
    }
}

