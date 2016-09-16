package org.rhwlab.tree;

/*
 * Created on Jun 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CanonicalTree {

    private Hashtable iCells;
    private Cell iLastDaughter;
    private Cell iRoot;
    private Vector iSortedCellNames;
    private Hashtable iCanonicalNamesHash;
    private Hashtable iCanonicalSimpleNamesHash;
    private static CanonicalTree iCanonicalTree;


    public static synchronized CanonicalTree getCanonicalTree() {
        if (iCanonicalTree == null) {
            iCanonicalTree = new CanonicalTree();
        }
        return iCanonicalTree;
    }

    private CanonicalTree() {
        //System.out.println("\n#####CanonicalTree constructor entered");
        iCells = new Hashtable();
        iCanonicalNamesHash = new Hashtable();
        iCanonicalSimpleNamesHash = new Hashtable();
        iRoot = createNodes();
        makeSortedCellNames();
        makeCanonicalHashes();
        //System.out.println("hash sizes: " + iCanonicalNamesHash.size()
        //        + CS + iCanonicalSimpleNamesHash.size());
        Cell c = (Cell)iCells.get("Ealaa");
        c.showParameters();
    }
    @Override
	public Object clone()
    throws CloneNotSupportedException
  {
    throw new CloneNotSupportedException();
    // that'll teach 'em
  }

    /**
     * @param createNodes
     */
    @SuppressWarnings("unused")
	private Cell createNodes() {
        String evtFile = "lineage2.gtr";
        Cell top;
        EventFile ef = new EventFile(evtFile);
        String [] s = new String[0];
        Cell daughter = new Cell("P0");
        for (int i=0; i<5000; i++) {
            try {
                s = ef.getNextEvent();
            } catch(ArrayIndexOutOfBoundsException aie) {
                //System.out.println("\nlast canonical event: " + i);
                break;
            }
            //System.out.println(s[0] + ", " + s[1] + ", " + s[2] + CS + s[3]);

            //for (int j=0; j < s.length; j++) {
            //    System.out.print(s[j] + CS);
            //}
            //System.out.println();

            if (s.length == 2) {
                processCellDeath(s);
                continue;
            }
            Cell parent = (Cell)iCells.get(s[0]);
            if(parent == null) {
                parent = new Cell(s[0], Cell.LARGEENDTIME);
                iCells.put(s[0], parent);
            }
            int time = Integer.parseInt(s[3]);
            parent.setEndTime(time);
            parent.setEndFate(Cell.DIVIDED);
            //Cell grandParent = (Cell)parent.getParent();
            daughter = new Cell(s[1], Cell.LARGEENDTIME);
            daughter.setStartTime(time);
            parent.add(daughter);
            iCells.put(s[1], daughter);
            daughter = new Cell(s[2], Cell.LARGEENDTIME);
            daughter.setStartTime(time);
            parent.add(daughter);
            iCells.put(s[2], daughter);
        }
        iLastDaughter = daughter;
        //System.out.println("lastDaughter = " + iLastDaughter);
        postProcessTree((Cell)iCells.get("P0"));
        return (Cell)iCells.get("P0");
    }

    private void postProcessTree(Cell root) {
        int maxEndTime = 0;
        Enumeration e = root.breadthFirstEnumeration();
        while(e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            int end = c.getEndTime();
            maxEndTime = Math.max(end, maxEndTime);
        }
        maxEndTime += DIVISIONTIME;
        e = root.breadthFirstEnumeration();
        while(e.hasMoreElements()) {
            Cell c = (Cell)e.nextElement();
            c.setEndingIndex(maxEndTime);
            if (c.getEnd() == 0) c.setEndTime(maxEndTime);
            //System.out.println("ot: " + c.showStuff());
        }
    }

    private void processCellDeath(String [] sa) {
        Cell c = (Cell)iCells.get(sa[0]);
        if (c != null) {
            int dt = Integer.parseInt(sa[1]);
            c.setEndTime(dt);
            c.setEndFate(Cell.DIED);
        } else {
            System.out.println("canonical lineage error -- unknown cell dying: " + sa[0]);
        }

    }


	private void makeSortedCellNames() {
        iSortedCellNames = new Vector();
        Enumeration e = iCells.keys();
        //int i = 0;
        while (e.hasMoreElements()) {
            iSortedCellNames.add (e.nextElement());
        }
        Collections.sort(iSortedCellNames);
    }

    /**
     * where the key is the parent name
     * and the value is the first daughter name
     *
     */
    private void makeCanonicalHashes() {
        Enumeration e = iSortedCellNames.elements();
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            Cell c = (Cell)iCells.get(name);
            Cell p = (Cell)c.getParent();
            if (p == null) continue;
            String pname = p.getName();
            String spname = makeSimpleName(pname);
            Cell d1 = (Cell)p.getChildAt(0);
            String dname = d1.getName();
            iCanonicalNamesHash.put(pname, dname);

            iCanonicalSimpleNamesHash.put(spname, dname);
            //System.out.println("SS: " + spname + CS + dname);
            //System.out.println("put: " + pname + CS + spname + CS + dname);
        }
    }

    public Hashtable getCanonicalNamesHash() {
        return iCanonicalNamesHash;
    }

    public Hashtable getCanonicalSimpleNamesHash() {
        return iCanonicalSimpleNamesHash;
    }

    public static String makeNewName(String name, char tag) {
        int k = name.length();
        StringBuffer sb = new StringBuffer(name);
        sb.setCharAt(k-1, tag);
        if (!name.equals(sb.toString())) System.out.println("makeNewName: " + name + CS + sb.toString() + CS + tag);
        return sb.toString();
    }

    public static String makeSisterName(String s) {
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
            case 'Z':
                if (s.equals("Z2")) sis = "Z3";
                else sis = "Z2";
            case 'P':
                if (s.equals("P2")) sis = "EMS";
                else if (s.equals("P3")) sis = "C";
                else if (s.equals("P4")) sis = "D";
        }
        return sis;
    }

    private static String replaceLastChar(String s) {
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

    public static String makeSimpleName(String name) {
        String s = name;
        s = s.replace(D, A);
        s = s.replace(L, A);
        s = s.replace(V, P);
        s = s.replace(R, P);
        return s;
    }


    public Cell getRoot() {
        return iRoot;
    }

    public Hashtable getCellsHash() {
        return iCells;
    }

    public Vector getSortedCellNames() {
        return iSortedCellNames;
    }

    private static final int
         DIVISIONTIME = 50
        ;

    private static final String
         CS = ", "
        ;

    private static final char
    E = 'e'
   ,W = 'w'
   ,V = 'v'
   ,D = 'd'
   ,B = 'b' //'d' based on identity study 20050614
   ,T = 't' //'v'
   ,A = 'a'
   ,P = 'p'
   ,L = 'l'
   ,R = 'r'
   ,X = 'X'    // a dummy tag used in newBornID
   ;

    public static void main(String[] args) {
    }
}
