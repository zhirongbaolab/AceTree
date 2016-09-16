/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */
package org.rhwlab.tree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.rhwlab.tree.Cell;
import org.rhwlab.utils.C;

//import forester.atv.ATVjframe;
//import forester.tree.Tree;

/**
 * Creates a representation of the tree as a string in the
 * Newick file format and passes the string to the ATV
 * program for rendering as a Sulston style tree
 * 
 * @author biowolp
 * @version 1.0 February 24, 2005
 *
 */
public class Newick extends JPanel {
        
    Cell iRoot;
    Vector iBuffer;
    
    /**
     * Constructor where all the action is initiated
     * 
     * @param root a cell treated as the root of a tree -- typically "start"
     * Our trees may have several "roots" arising from the fact that
     * the processing typically starts at the 4 cell stage. We treat
     * the 4 founder cells as separate trees.
     */
    public Newick(Cell root) {
        iRoot = root;
        iBuffer = new Vector();
        Vector v = null;
        Cell x = iRoot;
        Vector r = new Vector();
        // the loop below treats each child of the "root" as the
        // root of a subtree which is processed separately at first
        // then the several (typically 4) parts are merged
        int m = x.getChildCount();
        for (int i = 0; i < m; i++) {
            Cell c = (Cell)x.getChildAt(i);
            v = processTree(c);
            String s = buildNewick(v) + C.NL;
            r.add(s);
        }
        int rs = r.size();
        String s = null;
        if (rs > 1) {
            StringBuffer sb = new StringBuffer();
            for (int i=0; i < rs; i++) {
                if (i == 0) sb.append("(");
                else if (i < (rs)) {
                    sb.append(",");
                }
                sb.append((String)r.elementAt(i));
                
            }
            sb.append(")");
            s = sb.toString();
            
        } else s = (String)r.elementAt(0);
        //System.out.println("Newick: " + s);
        //showATVtree(s);
        saveNewickFile(s);
    }
    

    /**
     * Create a vector containing the string parts of a newick
     * representation of the tree rooted on Cell x
     * 
     * @param x the cell at the root of this "subtree"
     * @return a vector of strings that are pieces of the newick
     * subtree for x
     */
    private Vector processTree(Cell x) {
        Vector v = new Vector();
        preorder(x, v);
        //showVector(v, "result");
        return v;
        
    }
    
    /**
     * Debugging function to show the Vector of strings created
     * in processTree()
     * 
     * @param stack The Vector returned from preorder() treated as a staco
     * @param s A string representation of the implied newick form
     */
    private void showVector(Vector stack, String s) {
        System.out.println(s);
        int k = stack.size();
        String s2 = null;
        for(int i=0; i<k; i++) {
            s2 = (String)stack.elementAt(i);
            System.out.println(i + "\t" + s2);
        }
        
    
    }
    
    /**
     * recursive function to do a traversal of the tree
     * not sure about pre or post order -- this version
     * was tinkered with to work on the 4to100Cells data
     * where working means that the order of branches
     * was well aligned with the Sulston tree
     * when called from the AceTree tree of that data
     * @param x Cell to visit next
     * @param v Vector of results up to this point
     */
    @SuppressWarnings("unused")
	private void preorder(Cell x, Vector v) {
        Cell p = (Cell)x.getParent();
        String name = x.toString();
        String vname = name;
        String s = null;
        boolean addSpecies = false;
        int count = x.getChildCount();
        if (count > 0) {
            vname = validateName(name);
        }
        s = vname;
        addSpecies = (count > 0) && (vname.length() == 0);
        s += ":" + x.getLifeTime();
        //if (addSpecies) s += ":S=" + name;
        if (addSpecies) s += ":S=" + name + C.NL;
        v.add(0,s);
        if (x.getChildCount() == 0) {
            return;
        }
        Cell c0 = (Cell)x.getChildAt(1);
        if (c0 != null) { 
            v.add(0, ")");
            preorder(c0, v);
        } 
        Cell c1 = (Cell)x.getChildAt(0);
        if (c1 != null) {
            v.add(0, ",");
            preorder(c1, v);
            v.add(0, "(");
        }
    }
    
    /**
     * return either s or a "null" string depending
     * on whether the name s is in a set we are interested in
     * @param name string name of a cell
     * @return
     */
    private String validateName(String name) {
        String s = "";
        boolean found = false;
        if (name.length() < 4) {
            int i;
            for (i=0; i < blastomeres.length; i++) {
                found = name.equals(blastomeres[i]);
                if (found) break;
            }
            if (found) s = blastomeres[i];
        }
        return s;
    }
    
    /**
     * concatinate the elements of the Vector of Strings from preorder()
     * 
     * @param v A vector of strings produced by preorder()
     * @return returns a string representation of Newick form
     */
    private String buildNewick(Vector v) {
        String s;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < v.size(); i++) {
            s = (String)v.elementAt(i);
            //if (s.lastIndexOf(",") == s.length() - 1) s += C.NL;
            sb.append(s);
            //sb.append((String)v.elementAt(i));
        }
        return sb.toString();
    }
    
    /**
     * essentially launches ATV with the string produced here
     * 
     * @param s A newich tree representation as a string
     */
    /*
    private void showATVtree(String s) {
        Tree tree = null;
        try {
            tree = new Tree(s);   
            ATVjframe atvframe = new ATVjframe( tree );
            atvframe.showWhole();
        } catch ( Exception e ) {
            System.out.println( "ATVapp not available: " + e );
            //System.exit( -1 );
        }
        
        //ATVjframe atvframe = new ATVjframe( tree );
        //atvframe.showWhole();

    }
    */
    
    @SuppressWarnings("unused")
	private void saveNewickFile(String s) {
        JFileChooser fc = new JFileChooser(new File("."));
        int returnVal = fc.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String dir = fc.getCurrentDirectory().toString();
            String name = fc.getName(fc.getSelectedFile());
            //append(dir);
            //append(name);
            
            try {
                PrintStream ps = new PrintStream(new FileOutputStream(fc.getSelectedFile()));
                ps.print(s);
                ps.flush();
                ps.close();
            
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        
        }    

    }
    
    /**
     * The set of interior cells that are to be labeled in
     * the newick representation. Other interior cells are only
     * tagged with their lifetimes. 
     */
    private static final String [] blastomeres = {
             "P0"
            ,"P1"
            ,"AB"
            ,"P2"
            ,"EMS"
            ,"ABa"
            ,"ABp"
            ,"MS"
            ,"E"
            ,"P3"
            ,"C"
            ,"P4"
            ,"D"
    };
    
    public static void main(String[] args) {
    }
}
