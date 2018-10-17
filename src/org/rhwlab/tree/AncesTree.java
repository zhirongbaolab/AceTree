/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
  * Created on Jan 11, 2005
*/
package org.rhwlab.tree;

//import org.rhwlab.acetree.AceTree;

import org.rhwlab.snight.Identity3;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;

//import javax.swing.JTree;

/**
 * Creates the abstract tree structure from the analysis "nuclei" files.
 * <br>The contents of the files have been read and ensconced in
 * the iNucleiMgr object
 * <br>Relies on Cell to represent the tree. Cell is derived from
 * java class DefaultMutableTreeNode
 * <p>
 * Constructed in <code>AceTree.buildTree</code>.
 * <br>Construction implies processing data stored in a NucleiMgr object
 * and building the tree where the root node is
 * <code>Cell iRoot</code>, which is essentially a complete representation
 * of the embryo tree as seen in the data.
 * Access functions reach essential information developed
 * when the instance is constructed.
 * <p>
 * <br><code>getRoot</code>returns <code>iRoot</code>
 * <br><code>getCells</code>returns <code>Hashtable iCells</code>
 * which enables any Cell in the tree to be accessed by its name.
 * <p>
 * <p>
 *
 *
 * @author biowolp
 * @version 1.0 January 18, 2005
 * @version 1.1 February 25, 2005
 *
 */
public class AncesTree implements Comparator {
    private Cell iRoot;
    //    private JTree iTree;
    private String iSplitTime;
    private String iNucleiPrefix;
    private int iStartingIndex;
    private int iEndingIndex;
    private Vector iTempV;
    private boolean iRootEstablished;
    private Hashtable iCells;
    private Hashtable iCellsByName;
    private Hashtable iCellsByNameLowerCase;
    private NucleiMgr iNucleiMgr;
    private Vector iNucleiRecord;
    private int iPolarCount;
    private boolean iShowDeathsAndDivisions;
    private int []  iCellCounts;
    private Vector iRootCells;
    
    public boolean sulstonmode;

    /**
     * constructor
     *
     * @param root  Cell to become the root of the tree
     * @param nucleiMgr NucleiMgr object with contents of -nuclei files
     * @param startingIndex int first file index usually 1
     * @param endingIndex   int last file index
     */
    @SuppressWarnings("unused")
	public AncesTree(Cell root, NucleiMgr nucleiMgr, int startingIndex, int endingIndex) {
        iRoot = new Cell(ROOTNAME, endingIndex, startingIndex);
        iRoot.setEndTime(1);
        iNucleiMgr = nucleiMgr;
        iStartingIndex = startingIndex;
        iEndingIndex = endingIndex;
        iRootEstablished = false;
        iTempV = new Vector();
        iCells = new Hashtable();
        iPolarCount = 1;
        iShowDeathsAndDivisions = false;
        iCellCounts = new int[endingIndex + 1];

		//only add dummy early cell nodes to root if sulston names present
		//warning embeds assumption that if not sulston using Nuc names
		//not sure what root is passed in for (its null) so query for nucleus at first timepoint
		//embed assumption any Nuc named cell at tp 1 means not in sulston
        
        // For non-1 starting timepoints
		Vector nv = nucleiMgr.getNuclei(startingIndex);
        
        //System.out.println(nv);
		
		sulstonmode = false;
		
		Iterator it = nv.iterator();
		String s;
		
		while(it.hasNext()){
		    s=((Nucleus)it.next()).identity;
		    if(!(s.isEmpty() || s.startsWith("N")))
		    	sulstonmode = true;
		}
		if (sulstonmode){
		    iRoot.add(createDummyNodes());
		}

        //System.out.println("AncesTree1 iCells.size: " + iCells.size());
        processEntries();
        //System.out.println("AncesTree2 iCells.size: " + iCells.size());
        adjustEarlyStartTimes();
        //adjustEarlyEndTimes();
        extractRootCells();
        //System.out.println("AncesTree3 iCells.size: " + iCells.size());

        Cell PP = (Cell)iCellsByName.get("P");
        int kk = PP.getChildCount();
        //println("AncesTree.constructor, " + kk + CS + PP.getName());
        
        // This can happen at the very end instead of every time a root cell is processed
        if (sulstonmode)
    		checkFirstGeneration();
    }


    private void adjustEarlyStartTimes() {
    	if (this.sulstonmode) {
    		int tEnd = 0;
        	Cell x = (Cell)iCellsByName.get("ABal");
    		//check if cell exists 
    		//if not skip all rest because we're not in sulston naming and they dont exist
    		if (x != null){
    	    	int t_ABal = x.getTime();
    	    	if (t_ABal < 1)
    	    		return; //cannot adjust
    	
    	    	x = (Cell)iCellsByName.get("ABa");
    	    	int t_ABa = 0;
    	    	if (x != null) {
    	    		t_ABa = x.getTime();
    	    		if (t_ABa < 0) {
        		    	t_ABa = t_ABal - 14;
        		    	x.setTime(t_ABal - 14);
        		    	x.setEndTime(t_ABal - 1);
        		    	x = (Cell)iCellsByName.get("ABp");
        		    	x.setTime(t_ABal - 14);
        		    	x.setEndTime(t_ABal - 1);
        	    	}
    	    	}
    	    	
    	    	x = (Cell)iCellsByName.get("AB");
    	    	int t_AB = 0;
    	    	if (x != null) {
    	    		t_AB = x.getTime();
        	    	if (t_AB < 0) {
        	    		t_AB = t_ABal - 28;
        	    		x.setTime(t_AB);
        	    		tEnd = t_ABa - 1;
        	   			x.setEndTime(tEnd);
        	    		x = (Cell)iCellsByName.get("P1");
        	    		x.setTime(t_AB);
        	    		x.setEndTime(tEnd);
        	    	}
    	    	}
    	    	

    	    	x = (Cell)iCellsByName.get("P0");
    	    	int t_P0 = 0;
    	    	if (x != null) {
    	    		t_P0 = x.getTime();
        	    	if (t_P0 < 0) {
        	    		t_P0 = t_ABal - 42;
        	    		x.setTime(t_ABal - 42);
        	    		tEnd = t_AB - 1;
        	    		x.setEndTime(tEnd);
        	    	}
    	    	}
    	
    	        x = (Cell)iCellsByName.get("MS");
    	        int t_MS;
    	        if (x != null) {
    	        	t_MS = x.getTime();
        	        if (t_MS < 1)
        	        	return; //cannot adjust as things stand now

        	        x = (Cell)iCellsByName.get("EMS");
        	        int t_EMS = x.getTime();
        	        if (t_EMS < 0) {
        	            t_EMS = t_MS - 17;
        	            x.setTime(t_EMS);
        	            tEnd = t_MS - 1;
        	            x.setEndTime(tEnd);
        	            x = (Cell)iCellsByName.get("P2");
        	            x.setTime(t_EMS);
        	            x.setEndTime(tEnd);
        	        }
    	        }
    		}
    	}
    }

    private Cell createDummyNodes() {
    	//long timeStart = System.nanoTime();
    	System.out.println("Creating dummy nodes...");

    	Cell c0 = new Cell("P0", Cell.LARGEENDTIME, -120);
    	iCells.put("P0", c0);

    	Cell d1 = addDaughter(c0, "AB", -100);
    	Cell d2 = addDaughter(c0, "P1", -100);

    	Cell c1 = d1;
    	Cell c2 = d2;

    	d1 = addDaughter(c1, "ABa", -86);
    	d2 = addDaughter(c1, "ABp", -86);

    	addDaughter(d1, "ABal", -73);
    	addDaughter(d1, "ABar", -73);
    	addDaughter(d2, "ABpl", -71);
    	addDaughter(d2, "ABpr", -71);

    	d1 = addDaughter(c2, "EMS", -84);
    	d2 = addDaughter(c2, "P2", -84);
    	addDaughter(d1, "MS", -68);
    	addDaughter(d1, "E", -68);
    	addDaughter(d2, "C", -65);
    	addDaughter(d2, "P3", -65);

    	return c0;
    }

    private Cell addDaughter(Cell parent, String dName, int startTime) {
    	Cell d = new Cell(dName, Cell.LARGEENDTIME, startTime);
    	//println("addDaughter, adding to iCells, " + dName + CS + d.getName());
    	iCells.put(dName, d);
    	parent.add(d);
    	parent.setEndTime(startTime);
    	return d;
    }

    // Timing commented out -was used for optimization
    private void extractRootCells() {
    	long timeStart = System.nanoTime();
        iRootCells = new Vector();
        
        Enumeration e = iRoot.children();
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            //System.out.println("extractRootCells, 2, " + c.getName());
            iRootCells.add(c);
            //Collections.sort(iRootCells, this);
            //System.out.println("extractRootCells adding: " + c.getName());
        }
        // Sorting the list after loading each element makes loading time extremely long
        // Sort list after all cells are loaded
        Collections.sort(iRootCells, this);
        long timeEnd = System.nanoTime();
        double timeDiff = (timeEnd-timeStart)/1e6;
        System.out.println("Time to extract root cells: "+timeDiff+" ms.");
        System.out.println("");
    }

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
	public int compare(Object o1, Object o2) {
        String s1 = ((Cell)o1).getName();
        String s2 = ((Cell)o2).getName();
        Boolean isNucS1 = (s1.indexOf("Nuc") == 0);
        Boolean isNucS2 = (s2.indexOf("Nuc") == 0);
        if (isNucS1) {
        	// Comparing two Nuc names
        	if (isNucS2)
	        	return s1.compareTo(s2);
        	
        	// s1 is Nuc but s2 is not, s2 goes before s1
        	else
        		return 1;
        }
        else {
        	// Both are not Nucs
        	if (!isNucS2)
        		return s1.compareTo(s2);
        	
        	// s2 is Nuc but s1 is not, s1 goes before s2
        	else
        		return -1;
        }
    }

    // Returns index of first non-digit character
    private int numberEnd(String s) {
        int k=0;
        for (int i=0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) 
            	break;
            else 
            	k++;
        }
        return k;
    }



    public Vector getRootCells() {
        return iRootCells;
    }

    /**
     * loops through -nuclei file data after it has been
     * processed into the NucleiMgr object
     */
    // Called by AncesTree constructor
    @SuppressWarnings("unused")
	private void processEntries() {
    	//long timeStart = System.nanoTime();
    	
        //System.out.println("starting and ending indices: " + iStartingIndex + ", " + iEndingIndex);
        int count = 0;
        for (int i=iStartingIndex; i <= iEndingIndex; i++) {
            int r = processEntry(i);
            if (r != 0) 
            	break;
            r = iCellCounts[i] = countAliveCellsAtIndex(i);
            if (r < 0)
            	break;
            else
            	count = r;
            //println("processEntries, iCells.size. " + i + CS + iCells.size());
        }
        
        //long timeStart2 = System.nanoTime();
        makeCellsByNameHash();
    }

    private int countAliveCellsAtIndex(int k) {
        Vector nuclei;
        try {
            nuclei = iNucleiMgr.getNucleiRecord().elementAt(k - 1);
        } catch(ArrayIndexOutOfBoundsException oob) {
            return -1;
        }
        Nucleus n = null;
        int count = 0;
        for (int j=0; j < nuclei.size(); j++) {
            n = (Nucleus)nuclei.elementAt(j);
            if (n.status == DEAD)
            	continue;
            count++;
        }
        return count;
    }


    /**
     * creates and updates Cell objects found in the current "file"
     * gets the "nuclei" for this time frame from NucleiMgr
     * this is a vector of nucleus things more or less mimicing
     * the StarryNight nucleus structure
     *
     * @param i int essentially the file number being processed here
     */
    @SuppressWarnings("unused")
	private int processEntry(int i) {
        int index = i;

        if (iShowDeathsAndDivisions)
        	System.out.println("processEntry: " + i + " ************");
        i--;
        Vector nuclei;
        Vector nuclei_record = iNucleiMgr.getNucleiRecord();
        try {
            nuclei = (Vector)nuclei_record.elementAt(i);

        } catch(ArrayIndexOutOfBoundsException oob) {
            System.out.println("***** processEntry: " + i);
            return 1;
        }
        
        iNucleiMgr.setSuccessors(i);
        
        if (index == iStartingIndex) {
            for (int j = 0; j < nuclei.size(); j++) {
                Nucleus n = (Nucleus)nuclei.elementAt(j);
                processRootCell(i, n);
            }
        } else {
            Vector prev = iNucleiMgr.getNucleiRecord().elementAt(i - 1);
            for (int j=0; j < nuclei.size(); j++) {
                // use info on this cell from this and previous time frames
                Nucleus n = (Nucleus)nuclei.elementAt(j);
                //System.out.println("processEntry:3 " + i + CS + j + CS + n.identity);
                if (n.status == -1) {
                    continue;
                }
                if (n.predecessor == Nucleus.NILLI) {
                    // StarryNight may locate cells later in the processing
                    // and assign them to the root for lack of better info
                    processRootCell(i, n); // found a new one
                    continue;
                }
                // k is the index of the predecessor of this cell
                int k = n.predecessor - 1;
                Nucleus prevn = null;
                if (k >= 0) {
                	try {
                		prevn = (Nucleus)prev.elementAt(k);
                	} catch(Exception e) {
                		return 1;
                	}
                }
                // do the nominal thing and set the hashKey of the
                // current nucleus equal to that of its predecessor
                // this will be overlayed later if necessary
                if (prevn.status == -1) {
                    // current nucleus points back to a dead nucleus
                    // change predecessor to root and process it as a new cell
                    n.predecessor = Nucleus.NILLI;
                    processRootCell(i, n); // call it a new one
                    continue;

                }
                
                //System.out.println("prevn "+prevn.identity);
                String hashKey = prevn.getHashKey();
                n.setHashKey(hashKey);

                // check for cell death
                if ((n.successor1 == -1 || n.successor1 == 0) && index < iEndingIndex) {
                    if (iShowDeathsAndDivisions)
                    	System.out.println(death(n.identity));
                    Cell c = null;

                    if (hashKey != null)
                    	c = (Cell)iCells.get(hashKey); //####################

                    if (c != null) {
                        c.setEndTime(index);
                        c.setEndFate(Cell.DIED);
                    } else {
                        System.out.println("DYING CELL NOT IN HASH TABLE");
                        continue;
                    }

                }

                //if (prev.size() <= k || k < 0) continue; // an error I have seen
                //Nucleus prevn = (Nucleus)prev.elementAt(n.predecessor - 1);
                String parentName = prevn.identity;
                if (parentName.equals("Nuc199") && i == 179) {
                	int lll = 9;
                }

                if (hashKey == null) {
                    System.out.println("null hashkey");
                    System.out.println("***** processEntry2: " + i + CS + iCells);
                    //return 1;
                }
                Cell parent = (Cell)iCells.get(hashKey); //####################
                //System.out.println("processEntry - parent: " + parent.getName());

                if (parent == null) {
                    System.out.println("null parent: " + parentName);
                    System.out.println("i=" + (i+1) + ", j=" + (j+1));
                    System.out.println(prevn.toString());
                    System.out.println(n.toString());

                }
                // a division can be seen by examining the successor2 variable
                // of this same cell in the previous time frame
                if (prevn.successor2 == Nucleus.NILLI) {
                    parent.updateCellData(n);
                    continue; // no division
                }
                else {
                    //println("processEntries: " + "division encountered");
                    //handle cell division here
                    // note that we catch the other daughter when she
                    // comes up in the current set of nuclei
                    String daughterName = n.identity;
                    //System.out.println("daughterName: " + daughterName);
                    if (daughterName == null) {
                        System.out.println("null daughterName: " + i + CS + j);
                        System.out.println(n);
                    }
                    Cell daughter = new Cell(daughterName);
                    daughter.setParameters(index, iEndingIndex, n);
                    if (n.successor1 == Nucleus.NILLI && index < iEndingIndex) {
                        // special case of birth and death at same time
                        daughter.setEndTime(index);
                        daughter.setEndFate(Cell.DIED);
                        //System.out.println("special case " + daughter + CS + i);
                    }
                    hashKey = makeHashKey(index, n);
                    n.setHashKey(hashKey);
                    daughter.setHashKey(hashKey);

                    // check for pre-provided cell and remove if present
                    Cell x = (Cell)iCells.get(n.identity);
                    if (x != null) {
                        //System.out.println("processRootCell2: " + x.showStuff());

                    	Cell p = (Cell)x.getParent();
                    	x.removeFromParent();
                        //parent = p;
                    	iCells.remove(n.identity);
                    }

                    if (parent == null) {
                        System.out.println("null parent, i = " + (i+1) + "j = " + (j+1));
                        System.out.println("FORCED CONTINUE");
                        continue;
                    }
                    parent.add(daughter);
                    parent.setEndTime(index - 1);
                    parent.setEndFate(Cell.DIVIDED);
                    if (daughterName.equals(POLAR)) {
                        n.identity = daughterName;
                        daughter.setName(daughterName);
                    }
                    if (iShowDeathsAndDivisions)
                    	System.out.println(division(parent.getName(), daughter.getName()));
                    iCells.put(daughter.getHashKey(), daughter);

                    checkDaughters(parent);
                }
            }
        }
        //long timeEnd = System.nanoTime();
        //double timeDiff = (timeEnd-timeStart)/1e6;
        //System.out.println("Time for AncesTree.processEntry(): "+timeDiff+" ms.");
        return 0;
    }

    public String division(String par, String dau) {
        StringBuffer sb = new StringBuffer(DIVISIONTEMPLATE);
        sb.replace(PARLOC, PARLOC + par.length(), par);
        sb.replace(DAULOC, DAULOC + dau.length(), dau);
        return sb.toString();
    }

    private String death(String cellName) {
        StringBuffer sb = new StringBuffer(DEATHTEMPLATE);
        sb.replace(DEATHLOC, DEATHLOC + cellName.length(), cellName);
        return sb.toString();
    }

    private void setSuccessors(int i) {
        Vector nuclei_record = iNucleiMgr.getNucleiRecord();
        Vector now = (Vector)nuclei_record.elementAt(i);
        Nucleus n = null;
        int m1 = Nucleus.NILLI;
        for (int j=0; j < now.size(); j++) {
            n = (Nucleus)now.elementAt(j);
            n.successor1 = m1;
            n.successor2 = m1;
        }
        if (i == nuclei_record.size() - 1) return;
        Vector next = (Vector)nuclei_record.elementAt(i + 1);
        // first set all successors to -1
        for (int j=0; j < next.size(); j++) {
            n = (Nucleus)next.elementAt(j);
            if (n.status == DEAD) continue;
            int pred = n.predecessor;
            if (pred == DEAD) continue;
            Nucleus p = (Nucleus)now.elementAt(pred -1);
            if (p.successor1 == m1) p.successor1 = j + 1;
            else if (p.successor2 == m1) p.successor2 = j + 1;
            else {
                System.out.println("error: MORE THAN 2 SUCCESSORS");
            }
        }
    }

    
    @SuppressWarnings("unused")
	private void makeCellsByNameHash() {
    	System.out.println("\nAncesTree making cells by name hash...");
        int pct = 0;
        String key = null;
        iCellsByName = new Hashtable();
        String rname = iRoot.getName();
        //println("AncesTree.makeCellsByNameHash.put, " + rname);
        iCellsByName.put(iRoot.getName(), iRoot); //20051007

        int namingMethod;
        if (iNucleiMgr.isNucConfigNull()) {
            namingMethod = iNucleiMgr.getConfig().iNamingMethod;
        } else {
            namingMethod = iNucleiMgr.getNucConfig().getNamingMethod();
        }

        boolean b = (namingMethod == Identity3.MANUAL);
        Enumeration e = iCells.keys();
        while (e.hasMoreElements()) {
        	String hashKey = (String)e.nextElement();
        	Cell c = (Cell)iCells.get(hashKey);
            //Cell c = (Cell)e.nextElement();
            key = c.getName();
            if (key.equals(POLAR) && !b) {
                key = POLAR + (++pct);
            }
            iCellsByName.put(key, c);
            //System.out.println("makeCellsByName: " + hashKey + CS + key + CS + c.showStuff());
        }
        
        // Keep lower case version of cell hash
        iCellsByNameLowerCase = new Hashtable();
        Enumeration<String> keys = iCellsByName.keys();
        while (keys.hasMoreElements()) {
        	String currentkey = keys.nextElement();
        	iCellsByNameLowerCase.put(currentkey.toLowerCase(), iCellsByName.get(currentkey));
        }
    }
    
    public void printCellHashLowerCase() {
    	System.out.println("Showing lower case cell hash:");
    	Enumeration<String> e = iCellsByNameLowerCase.keys();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            System.out.println(key+CS+iCellsByNameLowerCase.get(key));
        }
    }
    
    public void printCellHash() {
    	System.out.println("Showing cell hash:");
    	Enumeration<String> e = iCellsByName.keys();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            System.out.println(key+CS+iCellsByName.get(key));
        }
    }
    
    public Hashtable getCellsByNameLowerCase() {
    	return iCellsByNameLowerCase;
    }

    public Hashtable getCellsByName() {
        return iCellsByName;
    }

    /**
     * make sure daughter cells are ordered in the Sulston standard way
     * so the first daughter is an a, l, d
     * do not handle cells without such ending characters
     * @param parent Cell under investigation
     */
    public int checkDaughtersCount = 0;
    private void checkDaughters(Cell parent) {
    	checkDaughtersCount++;
    	//System.out.println("Checking daughters...");
        // if both children are in place we want to arrange to
        // have the first daughter be the anterior (etc) one
    	//System.out.println("checkDaughters: " + parent);
        if (parent.getChildCount() < 2) return;
        if (parent == iRoot) {
        	/*
        	if (sulstonmode)
        		checkFirstGeneration();
        		*/
            return;
        }
        String pn = parent.getName();
        Cell d1 = (Cell)parent.getChildAt(0);
        Cell d2 = (Cell)parent.getChildAt(1);
        String n1 = d1.getName();
        String n2 = d2.getName();
        char c1 = ' ';
        char c2 = ' ';

        try {
            c1 = n1.charAt(n1.length() -1);
            c2 = n2.charAt(n2.length() -1);
        } catch(Exception ioe) {
            System.out.println("checkDaughters: " + parent);
            System.out.println("checkDaughters: " + d1);
            System.out.println("checkDaughters: " + d2);
            new Throwable().printStackTrace();


        }
        if (!Character.isLowerCase(c1)) {
            parent.removeAllChildren();
            if (pn.equals("P0")) {
                add(parent, d1, d2, n1.equals("AB"));
            } else if (pn.equals("P1")) {
                add(parent, d1, d2, n1.equals("EMS"));
            } else if (pn.equals("P2")) {
                add(parent, d1, d2, n1.equals("C"));
            } else if (pn.equals("P3")) {
                add(parent, d1, d2, n1.equals("D"));
            } else if (pn.equals("EMS")) {
                add(parent, d1, d2, n1.equals("MS"));
            } else if (pn.equals("P4")) {
                add(parent, d1, d2, n1.equals("Z2"));

            }
            return;
        }
        if (c1 < c2) return;
        // here is we need to reverse daughters
        //System.out.println("REVERSING DAUGHTERS FROM " + d1 + CS + d2);
        parent.removeAllChildren();
        parent.add(d2);
        parent.add(d1);
    }

    private void add(Cell parent, Cell d1, Cell d2, boolean sense) {
        if (sense) {
            parent.add(d1);
            parent.add(d2);

        } else {
            parent.add(d2);
            parent.add(d1);
        }
    }
    
    public int count = 0;
    
    // In loading a config file with many top level elements, this method is called way too many times
    // Used in checkDaughters() and processRootCell() -DT
    private void checkFirstGeneration() {
    	count++;
    	//System.out.println("AncesTree checking first generation..."+count);
        int cc = iRoot.getChildCount();
        Hashtable ch = new Hashtable();
        for (int i=0; i < cc; i++) {
            Cell x = (Cell)iRoot.getChildAt(i);
            String xn = x.getName();
            if (ch.containsKey(xn)) xn += (int)(100*Math.random());
            ch.put(xn, x);
        }
        iRoot.removeAllChildren();
        
        // here we re add things in the preferred order
        for (int i=0; i < fgo.length; i++) {
            if (ch.containsKey(fgo[i])) {
                Cell c = (Cell)ch.remove(fgo[i]);
                iRoot.add(c);
            }
        }
        // if there are any not on the preferred list put them in
        Enumeration e = ch.elements();
        while (e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            iRoot.add(c);
        }
    }

    public int processRootCount = 0;
    private void processRootCell(int i, Nucleus n) {
    	processRootCount++;
    	//System.out.println("Processing root cell...");
        //System.out.println("processRootCell: " + i + CS + n.identity);
        //if (n.identity.equals(POLAR)) n.identity = POLAR + iPolarCount++;
        if (n.status == Nucleus.NILLI)
        	return;
        Cell c = new Cell(n.identity);
        c.setParameters(i + 1, iEndingIndex, n);
        String hashKey = makeHashKey(i + 1, n);
        n.setHashKey(hashKey);
        c.setHashKey(hashKey);
        //c.showParameters();

        //System.out.println("processRootCell1: " + n.identity + CS + iCells.size());
        Cell x = (Cell)iCells.get(n.identity);
        if (x != null) {
            //System.out.println("processRootCell2: " + x.showStuff());

        	Cell parent = (Cell)x.getParent();
        	x.removeFromParent();
        	//println("processRootCell, removing, " + n.identity);
        	iCells.remove(n.identity);
            //System.out.println("processRootCell3: " + iCells.size() + CS + n.identity);
        	parent.add(c);
        	checkDaughters(parent);
        	//iCells.put(hashKey, c);
        } else {
            iRoot.add(c);
            //System.out.println("processRootCell: adding " + c.getName());
        }

        iCells.put(hashKey, c);

        
        
    	checkForCellDeath(n, i + 1, hashKey);

    }

    private void checkForCellDeath(Nucleus n, int index, String hashKey) {
        if (n.successor1 == -1 && index < iEndingIndex) {
            if (iShowDeathsAndDivisions) System.out.println(death(n.identity));
            Cell c = null;

            if (hashKey != null) 
            	c = (Cell)iCells.get(hashKey); //####################

            if (c != null) {
                c.setEndTime(index);
                c.setEndFate(Cell.DIED);
            } else {
                System.out.println("DYING CELL NOT IN HASH TABLE");
            }

        }

    }

    // Maybe we can use this in force naming -DT
    private String makeHashKey(int index, Nucleus n) {
        return String.valueOf(index * 100000 + n.index);
    }

    /**
     * access function for the root node of this tree
     * @return Cell at the root
     */
    public Cell getRoot() {
        return iRoot;
    }

    //public JTree getTree() {
    //   return iTree;
    //}

    public int getCellCount(int time) {
        return iCellCounts[time];
    }

    /**
     * access function for a hash permitting Cells to be
     * accessed by name
     * @return Hashtable iCells
     */
    public Hashtable getCells() {
        return iCells;
    }

    private static final String [] fgo = {
             "P0"
            ,"AB"
            ,"P1"
            ,"ABa"
            ,"ABp"
            ,"EMS"
            ,"P2"
    };

    private static final String
         POLAR = "polar"
        ,CS = ", "
        ,DIVISIONTEMPLATE = "  Division:                 ->                "
        ,DEATHTEMPLATE = "  Death:                "
	,ROOTNAME = "P"
        ;

    private static final int
         DEAD = -1
        ,PARLOC = 12
        ,DAULOC = 31
        ,DEATHLOC = 9
        ;

    public static void main(String[] args) {
    }
    private static void println(String s) {System.out.println(s);}
}