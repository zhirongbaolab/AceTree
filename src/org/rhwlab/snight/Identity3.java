package org.rhwlab.snight;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Vector;

import org.rhwlab.utils.EUtils;


// FROM CURRENT VERSION
public class Identity3 {
    //public static Identity      iIdentity;
    private static NucleiMgr    iNucleiMgr;
    private static Vector<Vector<Nucleus>> nuclei_record;
    int							iNamingMethod;
    int							iStartingIndex;
    int							iNucCount;
    DivisionCaller				iDivisionCaller;
    Parameters					iParameters;
    String						iAxis;
    int							iEndingIndex;
    MeasureCSV					iMeasureCSV;

    PrintWriter					iPrintWriter;
    int							iStartTime;
    
    private MeasureCSV measureCSV;
    private CanonicalTransform canTransform;

    public Identity3(NucleiMgr nucleiMgr) {
        iNucleiMgr = nucleiMgr;
        if (nucleiMgr.isNucConfigNull()) { // the legacy loading pipeline
            iNamingMethod = iNucleiMgr.getConfig().iNamingMethod;
            iStartingIndex = iNucleiMgr.iStartingIndex;
            iEndingIndex = iNucleiMgr.getEndingIndex();
            iMeasureCSV = iNucleiMgr.getMeasureCSV();
        } else { // the revised loading pipeline
            iNamingMethod = iNucleiMgr.getNucConfig().getNamingMethod();
            iStartingIndex = iNucleiMgr.getNucConfig().getStartingIndex();
            iEndingIndex = iNucleiMgr.getNucConfig().getEndingIndex();
            iMeasureCSV = iNucleiMgr.getNucConfig().getMeasureCSV();
            //System.out.println("\n\nparams in Indentity3 queried from NucleiConfig:\n" + iNamingMethod + "\n" + iStartingIndex + "\n" + iEndingIndex + "\n" + iMeasureCSV.toString() + "\n");
        }

        nuclei_record = iNucleiMgr.getNucleiRecord();
        iParameters = iNucleiMgr.getParameters();
    }

    public void setPrintWriter(PrintWriter pw) {
    	iPrintWriter = pw;
    }

    /**
     * New code as of 10/16
     * As AceTree now handles uncompressed embryos in any 3D orientation, in the presence
     * of such an embryo, we'll start building the CanonicalTransform object here to rotate
     * the embryo into canonical orientation for spatially informed naming
     *
     * Revised 10/18 to handle revised configuration setup
     */
    // Called by NucleiMgr processNuclei method
    @SuppressWarnings("unused")
	public void identityAssignment() {
    	println("Starting identity assingment in Identity3");
    	if (iNamingMethod == MANUAL) {
    		println("identityAssignment, skip naming due to MANUAL naming method");
    		return;
    	}
        //iStartingIndex = iNucleiMgr.getConfig().iStartingIndex;
        clearAllNames();
        //System.out.println("identityAssignment iStartingIndex: " + iStartingIndex);
        
       //this.measureCSV = iNucleiMgr.getMeasureCSV();
       // check for presence of uncompressed embryo --> AuxInfo_v2
        if (MeasureCSV.isAuxInfoV2()) {
        	canTransform = new CanonicalTransform(measureCSV);
        }
        

        iParameters.axis = 0; // a flag telling if we have an axis 0 = no axis
        int start[] = new int[1];
        start[0] = iStartingIndex;
        int[] lineage_ct_p = new int[1];
        lineage_ct_p[0] = 1;
        int lin_ct = lineage_ct_p[0];
        iAxis = tryForAxis(); // sets iParameters.axis to 1 if if finds one
        // if no axis was specified then the initialID code will be run
        // you could still use NEWCANONICAL here if iStartingIndex is greater than one
        // but this should be phased out
        if (iStartingIndex >= 1) {



            InitialID initID = new InitialID(iNucleiMgr, iParameters, iMeasureCSV, canTransform);
            int mm = initID.initialID(start, lineage_ct_p);
        	if (mm > 0) {
        		System.out.println("detected backtrace failure, lineage from start");
        		start[0] = 0; //start from scratch on failure of initialID
            	iNucCount = 1;
        	}
            if (iParameters.axis > 0) {
            	iParameters.ap = iParameters.apInit;
            	iParameters.dv = iParameters.dvInit;
            	iParameters.lr = iParameters.lrInit;
            	iAxis = getOrientation();

            	//System.out.println("initialID returned: " + mm);
            	lin_ct = lineage_ct_p[0];
            	//System.out.println("identityAssignment starting at: " + start[0]);
                iNucCount = initID.getNucCount();
            	if (iNamingMethod == NEWCANONICAL && start[0] > 0) {
            		useCanonicalRules(start, lineage_ct_p);
            		return;
            	}
            }
        }
        println("identityAssignment, reached code end, " + iStartingIndex + CS + start[0]);
        // we are going to assign Nuc names from here on by a simple method
        for (int i = start[0]; i < iEndingIndex; i++) {
            Vector<Nucleus> nuclei = nuclei_record.elementAt(i);
            Vector<Nucleus> nuclei_prev = null;
            int nuc_ct = nuclei.size();
            if (i > 0) nuclei_prev = nuclei_record.elementAt(i - 1);
            Nucleus nucleij = null;
            for (int j = 0; j < nuc_ct; j++) {
                nucleij = nuclei.elementAt(j);
                if (nucleij.status == Nucleus.NILLI) continue;
                if (nuclei_prev != null && nucleij.predecessor != Nucleus.NILLI) {
                    Nucleus pred = nuclei_prev.elementAt(nucleij.predecessor - 1);
                    if (pred.successor2 == Nucleus.NILLI) {
                     	nucleij.identity = pred.identity;
                       	continue;
                    } else {
                       	// case of dividing pred
                       	Nucleus sister = nuclei.get(pred.successor2 - 1);
                       	
                       	// Nucleus doesn't have forced name
                       	if (!nucleij.assignedID.equals(""))
                       		nucleij.identity = nucleij.assignedID;
                       	else {
	                       	nucleij.identity = pred.identity + "a";
	                       	sister.identity = pred.identity + "p";
                       	}
                       	continue;
                    }
                } else {
                 	// this is the first encounter of this nucleus
                   	//nucleij.identity = NUC + iNucCount++;
                	//println("identityAssignment adding nuc, " + nucleij);
                	if (!nucleij.assignedID.equals(""))
                		nucleij.identity = nucleij.assignedID;
                	else {
	                	int z = Math.round(nucleij.z);
	                	nucleij.identity = NUC + EUtils.makePaddedInt(i + 1) + "_" + z + "_" + nucleij.x + "_" + nucleij.y;
	                	//println("identityAssignment, adding nuc, " + nucleij);
                	}
                }
           }
        }
    }
    
    @SuppressWarnings("unused")
	private void clearAllNames() {
        int k = iNucleiMgr.getNucleiRecord().size();
        int endingIndex = iEndingIndex;
        //for (int i = 0; i < iEndingIndex; i++) {
        for (int i = iStartingIndex - 1; i < iEndingIndex; i++) {
            //println("clearAllNames: " + i + CS + iEndingIndex);
            if (!(i < k))
            	break;
            //for (int i = 0; i < iEndingIndex - iNucleiMgr.getConfig().iStartingIndex; i++) {
            if (iStartingIndex > 1 && i == iStartingIndex - 1)
            	continue;
            clearNames(iNucleiMgr.getNucleiRecord().elementAt(i));
        }
    }

    // Clears all non-forced names
    @SuppressWarnings("unused")
	private void clearNames(Vector<Nucleus> nuclei) {
        //println("cleaarNames: " + nuclei.size());
        Nucleus n;
        for (int i=0; i < nuclei.size(); i++) {
            n = nuclei.elementAt(i);
            String id = n.identity;
            if (n.assignedID.length() > 0) {
            	continue;
            }
            n.identity = "";
        }
    }

    @SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public void useCanonicalRules(int [] start, int [] lineage_ct_p) {
		Vector report = new Vector();

        double zPixRes;
        int iEndingIndex;

        // legacy vs. revised configuration
        if (iNucleiMgr.isNucConfigNull()) {
            zPixRes = iNucleiMgr.getZPixRes();
            iEndingIndex = iNucleiMgr.getEndingIndex();;
        } else {
            zPixRes = iNucleiMgr.getNucConfig().getZPixRes();
            iEndingIndex = iNucleiMgr.getNucConfig().getEndingIndex();
        }

        // initialize the DivisionCaller class to be used for assigning names after computing the axis of division
        if (MeasureCSV.isAuxInfoV2() && canTransform != null) {
        	iDivisionCaller = new DivisionCaller(iMeasureCSV, canTransform);
        } else {
        	iDivisionCaller = new DivisionCaller(iMeasureCSV, iAxis, zPixRes);
        }

        int k = iNucleiMgr.getNucleiRecord().size();
        int m = Math.min(k, iEndingIndex);
        newLine();
        System.out.println("useCanonicalRules starting at: " + start[0] + CS + iEndingIndex);
        int nuc_ct = 0;
        int i;
        Vector<Nucleus> nuclei = null;
        int breakout = 0;

        /*
         * Iterate over all time points 
         */
        for (i = start[0]; i <= m; i++) {
        	//System.out.println("\n----------------------------\n"+
        						//"usecanonicalrules "+i);
            if (breakout > 0) {
                System.out.println("Identity3.useCanonicalRules exiting, breakout=" + breakout);
                System.exit(0);
                break;
            }
            
            // access nuclei at given time point (0 indexed --> subtract 1)
            nuclei = nuclei_record.elementAt(i - 1);
            nuc_ct = nuclei.size();
            Nucleus parent = null;
            Vector<Nucleus> nextNuclei;
            if (i < m) {
                nextNuclei = nuclei_record.elementAt(i);
            } else {
                nextNuclei = null;
            }
            
            /*
             * Iterate over the nuclei at the current time point
             */
            for (int j = 0; j < nuc_ct; j++) {
                parent = nuclei.elementAt(j);
                //println("useCanonicalRules, " + i + CS + j + CS + parent.identity);
                if (parent.status == Nucleus.NILLI) {
                	//println("useCanonicalRules, XXX, " + i + CS + j + CS + parent.identity + CS + parent.status);
                	continue;
                }
                String pname = parent.identity;
               
                /* NUC NAMEING PROCEDURE MODIFIED 20100630
                 * 
                 * REVISED July 14, 2016 --> @author Braden Katzman
                 */
                if (pname == null || pname.length() == 0) {
                    //pname = NUC + iNucCount++;
                	int z = Math.round(parent.z);
                	
                	// Try to only use the Nuc... name when there is no forced name in assignedID
                	if (parent.assignedID.equals(""))
                		pname = NUC + EUtils.makePaddedInt(i) + "_" + z + "_" + parent.x + "_" + parent.y;
                	else {
                		pname = parent.assignedID;
                	}
                	
                	// set the identity to the Nuc... name or the assignedID
                    parent.identity = pname;
                	//println("useCanoncalRules, adding nuc, " + parent.identity);
                }
               
                
                // check if valid division
                if (nextNuclei != null) {
                    boolean good = (parent.successor1 > 0 && parent.successor2 > 0);
                    if (!good) {
                        // not dividing so just extend the name
                        if (parent.successor1 > 0) {
                            Nucleus n = nextNuclei.elementAt(parent.successor1 - 1);
                            if (n.assignedID.length() <= 0) {
                                //println("useCanonicalRules, XXXXXX, " + i + CS + j + CS + parent.identity + CS + parent.status + CS + n.identity);
                                //println("useCanonicalRules, XXXXXX, "+parent.identity);
                                n.identity = pname;
                            }
                        }
                        continue;
                    }

                    // this canonical parent is dividing
                    Nucleus dau1 = nextNuclei.elementAt(parent.successor1 - 1);
                    Nucleus dau2 = nextNuclei.elementAt(parent.successor2 - 1);
                    //System.out.println("about to assign names to children of: " + parent.identity + " - " + dau1.identity + ", " + dau2.identity);

                    /*
                     * Assign names via DivisionCaller
                     */
                    if (parent != null && dau1 != null && dau2 != null) {
                        iDivisionCaller.assignNames(parent, dau1, dau2);
                        usePreassignedID(dau1, dau2);
                    }
                }
            }
        }
        Collections.sort(report);
    }

    private void usePreassignedID(Nucleus dau1, Nucleus dau2) {
        //println("usePreassignedID: " + dau1.identity + CS + dau2.identity);
        //println("usePreassignedID:2 " + dau1.assignedID + CS + dau2.assignedID);
        if (dau1.assignedID.length() == 0 && dau2.assignedID.length()== 0) {
            return;
        }
        //println("usePreassignedID:3 " + dau1.assignedID + CS + dau2.assignedID);
        if (dau1.assignedID.length() > 0) {
//        	println("Changing dau1 identity from: " + dau1.identity + " to: " + dau1.assignedID);
        	dau1.identity = dau1.assignedID;
        }
        	
        if (dau2.assignedID.length() > 0) {
//        	println("Changing dau2 identity from: " + dau2.identity + " to: " + dau2.assignedID);
        	dau2.identity = dau2.assignedID;
        }

        if (dau1.identity.equals(dau2.identity)) {
            String s = dau2.identity;
            s = s.substring(0, s.length() - 1);
            s = s + "X";
            dau2.identity = s;
        }
    }


    private String tryForAxis() {
    	if (MeasureCSV.isAuxInfoV2()) return ""; 
    	
    	String axis = "";
    	if (iNucleiMgr.isNucConfigNull()) {
            axis = iNucleiMgr.getConfig().iAxisGiven;
        } else {
            axis = iNucleiMgr.getNucConfig().getAxisGiven();
        }

        if (axis.length() > 0) iParameters.axis = 1;
        
        return axis;
    }


	public String getOrientation() {
    	if (MeasureCSV.isAuxInfoV2()) return "";
    	
    	/*
    	 * only proceed on AuxInfo v1.0
    	 */

        String orientation = "A";
        String late = "A";
        if (iParameters.ap < 0) orientation = "P";
        if (iParameters.dv > 0) orientation += "D";
        else orientation += "V";
        if (iParameters.lr > 0) orientation += "L";
        else orientation += "R";
        if (orientation.equals("ADL")) late = "ARD";
        else if (orientation.equals("AVR")) late = "ALV";
        else if (orientation.equals("PDR")) late = "PLD";
        else if (orientation.equals("PVL")) late = "PRV";
        return orientation;
    }

    public String getAxis() {
    	return iAxis;
    }

    public int getNamingMethod() {
        return iNamingMethod;
    }

    public void setNamingMethod(int method) {
    	newLine();
        System.out.println("Identity3.setNamingMethod called with: " + method + CS + NAMING_METHOD[method]);
        iNamingMethod = method;
    }


    // called from class Analysis
    public String makeSisterName(String s) {
        String sis = null;
        char x = s.charAt(0);
        int n = s.length();
        boolean b = n == 1;
        switch(x) {
            case 'C':
                if (b) return("P3");
            case 'D':
                if (b) return("P4");
                else {
                    sis = replaceLastChar(s);
                    break;
                }
            case 'E':
                if (b) return ("MS");
                else {
                    sis = replaceLastChar(s);
                    break;
                }
            case 'M':
                if (n == 2) return("E");
                else {
                    sis = replaceLastChar(s);
                    break;
                }
            case 'A':
                if (s.equals("ABal")) return("ABar");
                if (s.equals("ABpl")) return ("ABpr");
                sis = replaceLastChar(s);
                break;
            case 'R': /*20050824 was Z but no such special case */
                if (s.equals("Z2")) sis = "Z3";
                else sis = "Z2";
                break;
            case 'P':
                if (s.equals("P2")) sis = "EMS";
                else if (s.equals("P3")) sis = "C";
                else if (s.equals("P4")) sis = "D";
                break;
            default:
                sis = replaceLastChar(s);
        }
        return sis;
    }


    public String replaceLastChar(String s) {
        StringBuffer sb = new StringBuffer(s);
        int n = sb.length() - 1;
        char x = sb.charAt(n);
        switch(x) {
        case 'a':
            sb.setCharAt(n, 'p');
            break;
        case 'l':
            sb.setCharAt(n, 'r');
            break;
        case 'd':
            sb.setCharAt(n, 'v');
            break;
        case 'p':
            sb.setCharAt(n, 'a');
            break;
        case 'r':
            sb.setCharAt(n, 'l');
            break;
        case 'v':
            sb.setCharAt(n, 'd');
            break;

        }
        return sb.toString();
    }



    public static final String [] NAMING_METHOD = {
        "NONE"
        ,"STANDARD"
        ,"MANUAL"
        ,"NEWCANONICAL"
    };
    
    public static final int
	    EARLY = 50
	   ,MID = 450
	   ,DEAD = -1
	   ,DEADZERO = 0
	   ,DIVISOR = 8
	   ,MINCUTOFF = 5
   ;

    public static final int
    	  STANDARD = 1
    	 ,MANUAL = 2
         ,NEWCANONICAL = 3
	 ;


    private static final String
		 NUC = "Nuc"
	;

    private static void println(String s) {System.out.println(s);}
    private void newLine() {System.out.println("");}
    private static final String CS = ", ";// C = ",", TAB = "\t";
}
