package org.rhwlab.analyze;

import flanagan.math.Matrix;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;

public class RedCorrector2 {
    AceTree         iAceTree;
    NucleiMgr       iNucleiMgr;
    static Hashtable        cCells;
    static Matrix           cM;
    static Hashtable        cNuclei;
    static Vector           cCellNames;


    public RedCorrector2() {
        println("RedCorrector2 entered");
        long start = System.currentTimeMillis();
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        int m = iNucleiMgr.getConfig().iStartingIndex;
        int q = iNucleiMgr.getConfig().iEndingIndex;
        for (int i=m; i <= q; i++) {
            correctNuclei(iNucleiMgr, i);
            if (i % 50 == 0) {
                long elapsed = System.currentTimeMillis() - start;
                println("elapsedTime: " + elapsed + CS  + i);
                System.out.flush();
            }
        }
    }
    
    @SuppressWarnings("unused")
	public static void correctNuclei(NucleiMgr nucMgr, int time) {
        Vector nuclei = nucMgr.getNuclei(time - 1);
        
        Hashtable workingNuclei = new Hashtable();
        for (int i=0; i < nuclei.size(); i++) {
            Nucleus ni = (Nucleus)nuclei.get(i);
            if (ni.status <= 0) continue;
            workingNuclei.put(ni.identity, ni);
        }
        Enumeration keys = workingNuclei.keys();
        Vector cellNames = new Vector();
        while (keys.hasMoreElements()) {
            String name = (String)keys.nextElement();
            cellNames.add(name);
        }
        Collections.sort(cellNames);
        cCellNames = cellNames;
        cCells = new Hashtable();
        for (int i=0; i < cellNames.size(); i++) {
            String name = (String)cellNames.get(i);
            cCells.put(name, new Integer(i));
        }
        
        
        
        
        double zpixres = nucMgr.getZPixRes();
        int m = workingNuclei.size();
        Matrix M = new Matrix(m, m);
        for (int i=0; i < m; i++) {
            String name = (String)cellNames.get(i);
            Nucleus ni = (Nucleus)workingNuclei.get(name);
            for (int j=0; j < m; j++) {
                String name2 = (String)cellNames.get(j);
                Nucleus nj = (Nucleus)workingNuclei.get(name2);
                double infl = influence2D(ni, nj);
                M.setElement(i, j, infl);
            }
        }
        // the rhs of the linear equations
        double [] b = new double[m];
        for (int i=0; i < m; i++) {
            String name = (String)cellNames.get(i);
            Nucleus n = (Nucleus)workingNuclei.get(name);
            //Nucleus n = (Nucleus)workingNuclei.get(i);
            if (n.rwcorr1 < 10) n.rwcorr1 = 25000;
            b[i] = n.rwraw - n.rwcorr1;
        }
        double [] x = M.solveLinearSet(b);
        for (int i=0; i < x.length; i++) {
            String name = (String)cellNames.get(i);
            Nucleus n = (Nucleus)workingNuclei.get(name);
            //Nucleus n = (Nucleus)workingNuclei.get(i);
            n.rwcorr4 = n.rwraw - (int)Math.round(x[i]);
            n.rweight = (int)Math.round(x[i]);
            //n.rwcorr4 = n.rwraw - ((int)Math.round(x[i]) + 25000);
            //n.rwcorr4 = (int)Math.round(x[i]);
        }
        //showResults(nucMgr, time);
        cM = M;
        cNuclei = workingNuclei;
    }
    
    public static void extractTerms(String cellName) {
        double meas = 0;
        int cols = cCellNames.size();
        int row = ((Integer)cCells.get(cellName)).intValue();
        for (int j=0; j < cols; j++) {
            String name = (String)cCellNames.get(j);
            double infl = cM.getElement(row, j);
            Nucleus n = (Nucleus)cNuclei.get(name);
            double t = n.rwraw - n.rwcorr4;
            double c = t * infl;
            meas += c;
            if (infl != 0) println("extractTerms, " + j + CS + name + CS + fmt4(infl) + CS + fmt0(t) + CS + fmt1(c));
        }
        Nucleus n = (Nucleus)cNuclei.get(cellName);
        double t = n.rwraw - n.rwcorr4;
        println("extractTerms, meas=" + fmt0(meas) + CS + fmt0(t) + CS + fmt0(meas - t) + CS + fmt0(n.rwraw));
        
        
    }
    public static void showResults(NucleiMgr nucMgr, int time) {
        //Vector nucRecord = nucMgr.nuclei_record;
        //Vector nuclei = (Vector)nucRecord.get(time - 1);
        Vector nuclei = nucMgr.getNuclei(time - 1);
        Hashtable nucHash = new Hashtable();
        Vector cellNames = new Vector();
        for (int i=0; i < nuclei.size(); i++) {
            Nucleus ni = (Nucleus)nuclei.get(i);
            if (ni.status <= 0) continue;
            nucHash.put(ni.identity, ni);
            cellNames.add(ni.identity);
        }
        Collections.sort(cellNames);
        for (int i=0; i < cellNames.size(); i++) {
            String name = (String)cellNames.get(i);
            Nucleus n = (Nucleus)nucHash.get(name);
            StringBuffer sb = new StringBuffer(name);
            sb.append(CS + DF1.format(n.rwraw - n.rwcorr1));
            sb.append(CS + DF1.format(n.rwraw - n.rwcorr2));
            sb.append(CS + DF1.format(n.rwraw- n.rwcorr3));
            sb.append(CS + DF1.format(n.rwraw - n.rwcorr4));
            sb.append(CS + DF1.format(n.rwraw));
            sb.append(CS + DF1.format(n.rwcorr1));
            sb.append(CS + DF1.format(n.rwcorr2));
            sb.append(CS + DF1.format(n.rwcorr3));
            sb.append(CS + DF1.format(n.rwcorr4));
            println(sb.toString());
        }
        
    }
    
    private void showM(Matrix M) {
        int rows = M.getNrow();
        int cols = M.getNcol();
        for (int i = 0; i < rows; i++) {
            String s = "";
            for (int j = 0; j < cols; j++) {
                double x = M.getElement(i,j);
                s += fmt4(x) + CS;
                
            }
            println(s);
        }
    }
    
    private double correction(double infl, int weight, int background) {
        return infl * (weight - background);
    }
    
    private double influence(double d) {
        if (d >= 90) return 0;
        //double [] pattern = {1, .7, .3, .1, .05, .02, 0};
        double [] pattern = {1, .35, .15, .05, .025, .01, 0};
        int k = (int)Math.floor(d/15);
        double f = (d - 15 * k)/15;
        double infl = f * pattern[k+1] + (1 - f)*pattern[k];
        return infl;
    }
    
    private double influenceN(Nucleus ni, Nucleus nj, double maxDistance) {
        double x = Math.abs(ni.x - nj.x);
        if (x > maxDistance) return 0;
        double y = Math.abs(ni.y - nj.y);
        if (y > maxDistance) return 0;
        double z = Math.abs(ni.z - nj.z) * iNucleiMgr.getZPixRes();
        if (z > maxDistance) return 0;
        double d = Math.sqrt(x*x + y*y + z*z);
        if (d > maxDistance) return 0;
        return influence(d);

    }
    
/* this is the actual matlab screen output
r =
    0.0433    0.0143    0.0236    0.0413    0.0131         0
    0.0428    0.0104    0.0064    0.0494    0.0416    0.0160
    0.0616    0.0612    0.0287    0.0040    0.0170    0.0194
    0.1439    0.0730    0.0553    0.0137    0.0390    0.0137
    0.2013    0.2026    0.1160    0.0277    0.0013    0.0176
    0.4997    0.5145    0.1134    0.0026    0.0009    0.0370
    1.0000    0.8053    0.1727    0.0294    0.0329    0.0471
    0.8143    0.5761    0.2356    0.0410    0.0483    0.0215
    0.5803    0.3716    0.1522    0.0866    0.0188    0.0210
    0.4502    0.3022    0.1837    0.0819    0.0588    0.0425
    0.1726    0.2082    0.0902    0.0473    0.0312    0.0155
    0.1486    0.1491    0.0663    0.0310    0.0082    0.0335
    0.0288    0.0693    0.0473    0.0403    0.0408    0.0690
    0.0372    0.0317    0.0724    0.0534    0.0866    0.0423
>> 
 
 

 */
    
    // this was based on my examinateion of data in matlab
    /*
    private static final double [][] INFL = 
    {
        {0.0433,    0.0143,    0.0236,    0.0413,    0.0131,     0.0000, 0}
        ,{0.0428,    0.0104,    0.0064,    0.0494,    0.0416,    0.0160, 0}
        ,{0.0616,    0.0612,    0.0287,    0.0040,    0.0170,    0.0194, 0}
        ,{0.1439,    0.0730,    0.0553,    0.0137,    0.0390,    0.0137, 0}
        ,{0.2013,    0.2026,    0.1160,    0.0277,    0.0013,    0.0176, 0}
        ,{0.4997,    0.5145,    0.1134,    0.0026,    0.0009,    0.0370, 0}
        ,{1.0000,    0.8053,    0.1727,    0.0294,    0.0329,    0.0471, 0} // .4 below center
        ,{0.8143,    0.5761,    0.2356,    0.0410,    0.0483,    0.0215, 0} // .6 above center
        ,{0.5803,    0.3716,    0.1522,    0.0866,    0.0188,    0.0210, 0}
        ,{0.4502,    0.3022,    0.1837,    0.0819,    0.0588,    0.0425, 0}
        ,{0.1726,    0.2082,    0.0902,    0.0473,    0.0312,    0.0155, 0}
        ,{0.1486,    0.1491,    0.0663,    0.0310,    0.0082,    0.0335, 0}
        ,{0.0288,    0.0693,    0.0473,    0.0403,    0.0408,    0.0690, 0}
        ,{0.0372,    0.0317,    0.0724,    0.0534,    0.0866,    0.0423, 0}
        ,{0.0000,    0.0000,    0.0000,    0.0000,    0.0000,    0.0000, 0}
    };
    */
 
    
    // using 3,0,11,5
    private static final double [][] INFL = 
    {
         {0,        0,      0,      0,       0,         0, 0}
        ,{0,        0,      0,      0,       0,         0, 0}
        ,{0.0442,   0.0255, 0.0151, 0.0111,  0.0063,  0, 0}
        ,{0.151,    0.0976, 0.0467, 0.0206,  0.0092,  0, 0}
        ,{0.3765,   0.2219, 0.0699, 0.0185,  0.0067,  0, 0}
        ,{0.7416,   0.3884, 0.0893, 0.0179,  0.0057,    0, 0}
        ,{1,        0.5036, 0.1232, 0.0357,  0.0155,  0, 0}
        ,{0.9505,   0.4869, 0.156,  0.0556,  0.0194,  0, 0}
        ,{0.7092,   0.3918, 0.1695, 0.0703,  0.0216,  0, 0}
        ,{0.4957,   0.298,  0.1492, 0.071,   0.0216,  0, 0}
        ,{ 0.2738,   0.1797, 0.0927, 0.0476,  0.0167, 0, 0}
        ,{ 0.0824,   0.0579, 0.0278, 0.0147,  0.005,  0, 0}
        ,{ 0,        0,      0,      0,       0,        0, 0}
        ,{ 0,        0,      0,      0,       0,        0, 0}
        ,{ 0,        0,      0,      0,       0,     0, 0}
        
    };
    
    // using 0,0,15,7
    private static final double [][] INFLf = 
    {
     {0.0271, 0.0228, 0.036 , 0.0325, 0.0134, 0.0026, 0}
    ,{0.0522, 0.0378, 0.0434, 0.0424, 0.0241, 0.0068, 0}
    ,{0.0914, 0.0564, 0.0406, 0.0396, 0.03  , 0.0093, 0}
    ,{0.1772, 0.1144, 0.0536, 0.0296, 0.0248, 0.0091, 0}
    ,{0.3765, 0.2219, 0.0699, 0.0246, 0.024 , 0.0122, 0}
    ,{0.7416, 0.3884, 0.0893, 0.0273, 0.0325, 0.0189, 0}
    ,{1,      0.5036, 0.1232, 0.0457, 0.0442, 0.0203, 0}
    ,{0.9505, 0.4869, 0.156 , 0.0635, 0.042 , 0.0159, 0}
    ,{0.7092, 0.3918, 0.1695, 0.0778, 0.043 , 0.0151, 0}
    ,{0.4957, 0.298 , 0.1492, 0.0787, 0.0437, 0.0157, 0}
    ,{0.337 , 0.2213, 0.1111, 0.0623, 0.0403, 0.0158, 0}
    ,{0.1969, 0.1425, 0.0741, 0.0509, 0.0436, 0.0213, 0}
    ,{0.1168, 0.1034, 0.0778, 0.0717, 0.0644, 0.0281, 0}
    ,{0.0513, 0.0621, 0.064 , 0.0666, 0.056 , 0.0209, 0}
    ,{0.019 , 0.0259, 0.0307, 0.0317, 0.0236, 0.0068, 0}
    };
    
    
    // if INFLf then we are using the full set
    @SuppressWarnings("unused")
	private static double infl(int i, int j) {
        double infl = 0;
        if (i >= 0 && j >= 0 && i < 15 && j < 7) return INFLf[i][j];
        else return 0;
    }
    
    
    
    private static double influence2D(Nucleus nFrom, Nucleus nTo) {
        int r = INFL.length - 1;
        int c = INFL[0].length - 1;
        double nomz = 0.0;
        double nomx = 10;
        int zb = 6;
        double infl = 0;
        double zdiff = nTo.z - nFrom.z - nomz + zb;
        if (zdiff >= r) return 0;
        double dx = nTo.x - nFrom.x;
        double dy = nTo.y - nFrom.y;
        double xdiff = Math.sqrt(dx * dx + dy * dy)/nomx;
        if (xdiff >= c) return 0;
        int r1 = (int)Math.floor(zdiff);
        int c1 = (int)Math.floor(xdiff);
        if (r1 < 0) return 0;
        double zinc = zdiff - r1;
        double xinc = xdiff - c1;
        double d = 0;
        double a = 0;
        double b = 0;
        try {
            //d = INFL[r1][c1];
            //a = INFL[r1 + 1][c1] - d;
            //b = INFL[r1][c1 + 1] - d;
            
            d = infl(r1, c1);
            a = infl(r1 + 1, c1) - d;
            b = infl(r1, c1 + 1) - d;
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        infl = d + a * zinc  + b * xinc;
        if (infl <= 0) infl = 0;
        return infl;
    }
    
    private void testInfluence() {
        Nucleus nFrom = new Nucleus();
        nFrom.x = 100;
        nFrom.y = 150;
        nFrom.z = 15;
        Nucleus nTo = new Nucleus();
        nTo.x = 100 + 0;
        nTo.y = 150 - 30;
        nTo.z = (float)17.5;
        double infl = influence2D(nFrom, nTo);
        println("testInfluence, " + DF4.format(infl));
    }
    
    
    
    public static void main(String [] args) {
        println("RedCorrector2, ");
        /*
        ManifestX.reportAndUpdateManifest();
        DBAccess.cDBLocation = ManifestX.getManifestValue("DBLocation");
        String dataBaseName = args[0];
        String filepathname = args[1];
        int tp = Integer.parseInt(args[2]);
        RedCorrector2 rc2 = new RedCorrector2(dataBaseName, filepathname, tp);
        correctNuclei(rc2.iNucleiMgr, tp);
        //extractTerms("Dppp");
        */
    }
    
    
        private static void println(String s) {System.out.println(s);}
        private static final String CS = ", ";
        private static final DecimalFormat DF0 = new DecimalFormat("####");
        private static final DecimalFormat DF1 = new DecimalFormat("######.#");
        private static final DecimalFormat DF2 = new DecimalFormat("####.##");
        private static final DecimalFormat DF4 = new DecimalFormat("####.####");
        private static String fmt1(double d) {return DF1.format(d);}
        private static String fmt0(double d) {return DF1.format(d);}
        private static String fmt4(double d) {return DF4.format(d);}
        
    

}
