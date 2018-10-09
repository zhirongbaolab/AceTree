package org.rhwlab.snight;

import java.util.Hashtable;

public class NucleiConfig {
    private int namingMethod;
    private String namingMethodKey = "";

    private int polarSize;
    private String polarSizeKey = "";

    private String axisGiven; // one of "", "adl" or "avr"
    private String axisGivenKey = "";

    private String exprCorrID; // one of none, gobal, local, blot, cross correlation
    private String exprCorrIDKey = "";

    /**
     * Constructor called by Config constructor which has built a hashmap of configuration values
     * from an XML file
     *
     * This constructor parses that map for relevant nuclei tags and saves them in this object
     * @param configData
     */
    public NucleiConfig(Hashtable<String, String> configData) {
        if (configData == null) return;

        for (String s : configData.keySet()) {

        }
    }

    private void setAxisGiven(String axisGiven) {
        this.axisGiven = axisGiven;
    }
}
