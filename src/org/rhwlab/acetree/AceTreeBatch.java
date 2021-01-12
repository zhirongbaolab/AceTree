package org.rhwlab.acetree;

import java.io.File;
import java.text.DecimalFormat;

import org.rhwlab.snight.NucZipper;
import org.rhwlab.snight.NucleiMgr;


public class AceTreeBatch {
    
    String      iConfigName;
    NucleiMgr   iNucleiMgr;
    
    @SuppressWarnings("unused")
	public AceTreeBatch(String configName) {
        iConfigName = configName;
        int r = bringUpSeriesData(configName);
        if (r != 0) {
            println("AceTreeBatch, bad nucleiMgr");
            System.exit(0);
        }
        else println("AceTreeBatch, good series");
        String realSave = iNucleiMgr.getConfig().iZipFileName;
        File file = new File(realSave);
        NucZipper nz = new NucZipper(file, iNucleiMgr, null);
        println("AceTreeBatch, " + realSave);
        println("AceTreeBatch, saved");

        
        
    }
    
    @SuppressWarnings("unused")
	public int bringUpSeriesData(String configFileName) {
        System.out.println("bringUpSeriesData: " + configFileName);
        File fx = new File(configFileName);
        
        // this is the only place where we construct a NucleiMgr
        NucleiMgr nucMgr = new NucleiMgr(configFileName);
        if (!nucMgr.iGoodNucleiMgr) {
            return -1;
        }
        nucMgr.processNuclei(true, nucMgr.getConfig().iNamingMethod);
        String config = nucMgr.getConfig().getShortName();
        iNucleiMgr = nucMgr;
        // System.gc();
        return 0;
    }
    


    /**
     * @param args
     */
    public static void main(String[] args) {
        String configName = args[0];
        configName = "/nfs/waterston1/annots/murray/20060716_cnd1_3/dats/20060716_cnd1_3.xml";
        println("AceTreeBatch.main, " + configName);
        new AceTreeBatch(configName);
        //Config config = new Config(configName);

    }
    private static void println(String s) {System.out.println(s);}
    private static void print(String s) {System.out.print(s);}
    private static final String CS = ", ";
    private static final DecimalFormat DF0 = new DecimalFormat("####");
    private static final DecimalFormat DF1 = new DecimalFormat("####.#");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt4(double d) {return DF4.format(d);}
    private static String fmt1(double d) {return DF1.format(d);}
    private static String fmt0(double d) {return DF0.format(d);}

}
