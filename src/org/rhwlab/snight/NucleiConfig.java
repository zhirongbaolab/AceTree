package org.rhwlab.snight;

public class NucleiConfig {
    private int namingMethod;
    private int polarSize;
    private String axisGiven; // one of "", "adl" or "avr"
    private String exprCorrID; // one of none, gobal, local, blot, cross correlation

    public NucleiConfig() {
        // this will help avoid null pts exceptions
        this.namingMethod = -1;
        this.polarSize = -1;
        this.axisGiven = "";
        this.exprCorrID = "";
    }

    private void setAxisGiven(String axisGiven) {
        this.axisGiven = axisGiven;
    }
}
