/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 * Created on Jan 31, 2005
 *
 */
package org.rhwlab.snight;

import java.text.DecimalFormat;

/**
 * a class to emulate the Nucleus_t struct in StarryNight
 * <br> all member variables have public access; this makes
 * comparisons with the C code, where needed, easy
 * 
 * @author biowolp
 * @version 1.0
 */
public class Nucleus implements java.util.Comparator {
    public String identity;
    public int index;
    public int status;
    public int predecessor;
    public int successor1;
    public int successor2;
    public int x;
    public int y;
    public float z;
    public int size;
    public int weight;
    public int rweight;
    public int rsum;
    public int rcount;
    public String assignedID;
    public String hashKey;
    public char id_tag;
    public int  rwraw;
    public int  rwcorr1; // a global background correction
    public int  rwcorr2; // a local background correction
    public int  rwcorr3; // local background correction with cookies
    public int  rwcorr4; // crosstalk correction

    public Nucleus copy() {
        Nucleus nc = new Nucleus();
        copyData(nc);
        return nc;
    }
    
    public void copyData(Nucleus nc) {
        nc.identity = identity;
        nc.index = index;
        nc.status = status;
        nc.x = x;
        nc.y = y;
        nc.z = z;
        nc.size = size;
        nc.weight = weight;
        nc.rweight = rweight;
        nc.rsum = rsum;
        nc.rcount = rcount;
        nc.predecessor = predecessor;
        nc.successor1 = successor1;
        nc.successor2 = successor2;
        nc.hashKey = hashKey;
        nc.id_tag = id_tag;
        nc.assignedID = assignedID;
        nc.rwraw = rwraw;
        nc.rwcorr1 = rwcorr1;
        nc.rwcorr2 = rwcorr2;
        nc.rwcorr3 = rwcorr3;
        nc.rwcorr4 = rwcorr4;
    }
    
    /**
     * default constructor needed when the editing process calls
     * for a new nucleus -- it then fills in the data
     * <br>There will be one such object for each line in each
     * nuclei file
     * 
     */
    public Nucleus() {
        super();
        predecessor = successor1 = successor2 = NILLI;//
        assignedID = "";
    }

    /**
     * constructor used when nuclei are read in from files
     * using the new file format
     * @param sa String [] with parsed entries from the line in
     * the file
     */
    public Nucleus(String [] sa) {
        this();
        index = Integer.parseInt(sa[INDEX]);
        x = Integer.parseInt(sa[X]);
        y = Integer.parseInt(sa[Y]);
        z = Float.parseFloat(sa[Z]);
        identity = sa[IDENTITY];
        /*
        if (identity.isEmpty()) {
        	identity = "Nuc" + EUtils.makePaddedInt(index) + "_" + (Math.round(z)) + "_" + x + "_" + y;
        }
        */
        size = Integer.parseInt(sa[SIZE]);
        weight = Integer.parseInt(sa[WT]);
        // try..catch works around series without red data
        int i = 0;
        try {
            for (i = RWT; i < RWCORR4 + 1; i++) { 
                if (sa.length > i) {
                    if (sa[i].length() > 0) {
                        switch(i) {
                            case RWT:
                                rweight = Integer.parseInt(sa[RWT]);
                                break;
                            case RSUM:
                                rsum = Integer.parseInt(sa[RSUM]);
                                break;
                            case RCOUNT:
                                rcount = Integer.parseInt(sa[RCOUNT]);
                                break;
                            case ASSIGNEDID:
                                assignedID = sa[ASSIGNEDID];
                                break;
                            case RWRAW:
                                rwraw = Integer.parseInt(sa[RWRAW]);
                                break;
                            case RWCORR1:
                                rwcorr1 = Integer.parseInt(sa[RWCORR1]);
                                break;
                            case RWCORR2:
                                rwcorr2 = Integer.parseInt(sa[RWCORR2]);
                                break;
                            case RWCORR3:
                                rwcorr3 = Integer.parseInt(sa[RWCORR3]);
                                break;
                            case RWCORR4:
                                rwcorr4 = Integer.parseInt(sa[RWCORR4]);
                                break;
                        }
                    }
                }
            }
        } catch(Exception e) {
            //System.out.println("Nucleus constructor exception: " + i);
        }
        status = -1;
        int xstat = Integer.parseInt(sa[STATUS]);
        if (xstat > 0) 
        	status = xstat;

        if (sa[PRED].equals(NILL) 
            || Integer.parseInt(sa[PRED]) == -1
           ) predecessor = NILLI;
        else predecessor = Integer.parseInt(sa[PRED]);
        if (sa[SUCC1].equals(NILL)) successor1 = NILLI;
        else successor1 = Integer.parseInt(sa[SUCC1]);
        if (sa[SUCC2] == null) successor2 = NILLI;
        else successor2 = Integer.parseInt(sa[SUCC2]);
        
        //assignedID = "XXXX";
    }
    
    public Nucleus(boolean fake) {
        index = 1;
        x = 2;
        y = 2;
        z = 15.f;
        identity = "Px";
        assignedID = identity;
        size = 2;
        weight = 50;
        status = 0;
        predecessor = 1;
        successor1 = 1;
        successor2 = -1;
    }
    
    /**
     * constructor used when nuclei are read in from files
     * using the old file format
     * @param sa String [] with parsed entries from the line in
     * the file
     */
    public Nucleus(String [] sa, boolean oldFormat) {
        predecessor = successor1 = successor2 = NILLI;//
        index = Integer.parseInt(sa[OINDEX]);
        x = Integer.parseInt(sa[OX]);
        y = Integer.parseInt(sa[OY]);
        z = Float.parseFloat(sa[OZ]);
        identity = sa[OIDENTITY];
        size = Integer.parseInt(sa[OSIZE]);
        weight = Integer.parseInt(sa[OWT]);
        status = -1;
        int xstat = Integer.parseInt(sa[OSTATUS]);
        if (xstat >= 0) 
        	status = xstat;

        if (sa[OPRED].equals(NILL) 
            || Integer.parseInt(sa[OPRED]) == -1
           ) predecessor = NILLI;
        else predecessor = Integer.parseInt(sa[OPRED]);
        if (sa[OSUCC1].equals(NILL)) successor1 = NILLI;
        else successor1 = Integer.parseInt(sa[OSUCC1]);
        if (sa[OSUCC2] == null) successor2 = NILLI;
        else successor2 = Integer.parseInt(sa[OSUCC2]);
    }
    
    public void setHashKey(String key) {
        hashKey = key;
    }
    
    public String getHashKey() {
        return hashKey;
    }
    
    public int getCorrectedRed(String type) {
        int choice = getRedChoiceNumber(type);
        return computeRed(choice);
    }
    
    private int getRedChoiceNumber(String type) {
        int i = 0;
        for (i=0; i < Config.REDCHOICE.length; i++) {
            if (type.equals(Config.REDCHOICE[i])) break;
        }
        return i;
    }

    private int computeRed(int k) {
        int red = rwraw;
        switch(k) {
        case 1:
            red -= rwcorr1; //global
            break;
        case 2:
            red -= rwcorr2; //local
            break;
        case 3: 
            red -= rwcorr3; //blot
            break;
        case 4:
            red -= rwcorr4; //cross
            break;
        }
        return red;
    }
    
    @Override
	public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf(index) + CS);
        sb.append(String.valueOf(status) + CS);
        sb.append(String.valueOf(predecessor) + CS);
        sb.append(String.valueOf(successor1) + CS);
        sb.append(String.valueOf(successor2) + CS);
        sb.append(String.valueOf(x) + CS);
        sb.append(String.valueOf(y) + CS);
        sb.append(String.valueOf(z) + CS);
        sb.append(String.valueOf(size) + CS);
        sb.append(identity + CS);
        sb.append(assignedID + CS);
        sb.append(String.valueOf(weight) + CS);
        sb.append(String.valueOf(rweight) + CS);
        sb.append(String.valueOf(rwraw) + CS);
        sb.append(String.valueOf(rwcorr1) + CS);
        sb.append(String.valueOf(rwcorr2) + CS);
        sb.append(String.valueOf(rwcorr3) + CS);
        sb.append(String.valueOf(rwcorr4) + CS);
        return sb.toString();
    }
    
    public String toString(int x) {
        String s = "";
        s = new DecimalFormat("000   ").format(index);
        s += new DecimalFormat("000  ").format(x);
        s += new DecimalFormat("000  ").format(y);
        s += new DecimalFormat("00.0  ").format(z);
        s += (identity + SPACE14).substring(0, 14);
        s += new DecimalFormat("00  ").format(size);
        s += new DecimalFormat("000000  ").format(weight);
        s += new DecimalFormat("00  ").format(status);
        if (predecessor == NILLI)
        	s += NILL + "  ";
        else
        	s += new DecimalFormat(" 000 ").format(predecessor);
        if (successor1 == NILLI)
        	s += NILL + "  ";
        else
        	s += new DecimalFormat("000  ").format(successor1);
        if (successor2 == NILLI)
        	s += "";
        else
        	s += new DecimalFormat("000  ").format(successor2);
        return s;
    }
    
    @Override
	public int compare(Object n1, Object n2) {
        String s1 = ((Nucleus)n1).identity;
        String s2 = ((Nucleus)n2).identity;
        return s1.compareTo(s2);
    }
    
    public String getIdentity() {
    	return this.identity;
    }
    
    // location of things in the new file format
    final public static int
	    INDEX = 0
	   ,X = 5
	   ,Y = 6
	   ,Z = 7
	   ,IDENTITY = 9
	   ,SIZE = 8
	   ,WT = 10
	   ,STATUS = 1
	   ,PRED = 2
	   ,SUCC1 = 3
	   ,SUCC2 = 4
	   ,RWT = 11
	   ,RSUM = 12
	   ,RCOUNT = 13
	   ,ASSIGNEDID = 14
	   ,RWRAW = 15
	   ,RWCORR1 = 16
	   ,RWCORR2 = 17
	   ,RWCORR3 = 18
	   ,RWCORR4 = 19
   ;

    //* the locations of things in the old file format
    final public static int
	    OINDEX = 0
	   ,OX = 1
	   ,OY = 2
	   ,OZ = 3
	   ,OIDENTITY = 4
	   ,OSIZE = 5
	   ,OWT = 6
	   ,OSTATUS = 7
	   ,OSPONG1 = 8
	   ,OSPONG2 = 9
	   ,OPIECE1 = 10
	   ,OPIECE2 = 11
	   ,OPRED = 12
	   ,OSUCC1 = 13
	   ,OSUCC2 = 14
   ;
   //*/
    final private static String
         NILL = "nill"
        ,SPACE14 = "              "
    ;
    
    final public static int 
         NILLI = -1
    ;
    
    private static final String
		CS = ", "
    ;
 
    public static void main(String[] args) {
    }
}
