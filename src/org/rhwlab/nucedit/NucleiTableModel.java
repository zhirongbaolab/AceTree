/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */
package org.rhwlab.nucedit;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;

/**
 * specializes the JTable for use with our nucleus objects
 * 
 * @author biowolp
 * @version 1.0 Feb 10, 2005
 */
public class NucleiTableModel extends AbstractTableModel {
    NucleiMgr iNucleiMgr;
    Vector iVdata;
    int iNumAdded;
    int iIndex;
    
    /**
     * default constructor
     *
     */
    public NucleiTableModel() {
        iVdata = new Vector();
        Vector row = new Vector();
        iVdata.add(row);
        iNumAdded = 0;
    }
    
    /**
     * fill in table from nucleus specified by its index position
     * in the NucleiMgr object
     * @param nucleiMgr
     * @param index
     */
    public void updateNucleiTableModel(NucleiMgr nucleiMgr, int index) {
        iVdata.clear();
        Vector va = nucleiMgr.getNucleiRecord();
        //System.out.println("NucleiTableModel.update: " + va.length);
        if (index < va.size()) {
            Vector nuclei = nucleiMgr.getNucleiRecord().elementAt(index);
            for (int i=0; i<nuclei.size(); i++) {
                Nucleus n = (Nucleus)nuclei.elementAt(i);
                Vector row = new Vector();
                row.add(new String(n.identity));
                row.add(new Integer(n.index)); 
                row.add(new Integer(n.status)); 
                row.add(new Integer(n.predecessor)); 
                row.add(new Integer(n.successor1)); 
                row.add(new Integer(n.successor2));
                row.add(new Integer(n.x)); 
                row.add(new Integer(n.y)); 
                row.add(new Float(n.z));
                row.add(new Integer(n.size)); 
                row.add(new Integer(n.weight));
                row.add(new Integer(n.rweight));
                //row.add(new String(n.hashKey));
                iVdata.add(row);
            }
        } else {
            iVdata.add(new Vector());
        }
        fireTableStructureChanged();
        
    }
    
    /**
     * access functions for colnames of the JTable
     */
    @Override
	public String getColumnName(int col) {
        return colnames[col];
    }

    public final static String [] colnames = {
            "identity"
           ,"index"
           ,"status"
           ,"pred"
           ,"succ1"
           ,"succ2"
           ,"x"
           ,"y"
           ,"z"
           ,"size"
           ,"weight"
           ,"rweight"
   };
    /*
    public final static String [] colnames = {
            "index"
           ,"x"
           ,"y"
           ,"z"
           ,"identity"
           ,"size"
           ,"weight"
           ,"status"
           ,"spong1"
           ,"spong2"
           ,"piecer"
           ,"piecer2"
           ,"pred"
           ,"succ1"
           ,"succ2"
   };
   */
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
	public int getColumnCount() {
        if (iVdata.size() == 0) return 0;
        Object o = iVdata.elementAt(0);
        if (o == null) return 0;
        else return ((Vector)iVdata.elementAt(0)).size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
	public int getRowCount() {
        return iVdata.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
	public Object getValueAt(int rowIndex, int columnIndex) {
        return ((Vector)iVdata.elementAt(rowIndex)).elementAt(columnIndex);
    }

    /**
     * access function -- always returns true at this point
     */
    @Override
	public boolean isCellEditable(int row, int col) {
        return true;
    }

    /**
     * updates one element of the table
     */
    @Override
	public void setValueAt(Object value, int row, int col) {
        ((Vector)(iVdata.elementAt(row))).set(col, value);
        fireTableCellUpdated(row, col);
    }
        
    /**
     * adds row to the table and initializes everything to a
     * safe default
     *
     */
    public void addRow() {
        Vector row = new Vector();
        Integer empty = new Integer(Nucleus.NILLI);
        int newRow = getRowCount() + 1;
        row.add(new Integer(newRow)); 
        for (int i = 1; i <= getColumnCount(); i++) row.add(empty);
        iNumAdded++;
        row.setElementAt(new String(ADD + iNumAdded), IDENT);
        row.setElementAt(String.valueOf(newRow), INDEX);
        iVdata.add(row);
        fireTableDataChanged();
    }

    /**
     * access function for the time point corresponding to the
     * top spreadsheet
     * @return time index for top JTable object
     */
    public int getIndex() {
        return iIndex;
    }
    
    /**
     * debugging function
     *
     */
    public void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i=0; i < numRows; i++) {
            for (int j=0; j< numCols; j++) {
                System.out.print("\t" + getValueAt(i, j));
            }
            System.out.println();
            
        }
        fireTableDataChanged();
    }

    final static public int
         IDENT  = 0
        ,INDEX  = 1
        ,STAT   = 2
        ,PRED   = 3
        ,SUCC1  = 4
        ,SUCC2  = 5
        ,X      = 6
        ,Y      = 7
        ,Z      = 8
        ,SIZE   = 9
        ,WT     = 10
        ;
    /*
    final static public int
    INDEX = 0
   ,X = 1
   ,Y = 2
   ,Z = 3
   ,IDENT = 4
   ,SIZE = 5
   ,WT = 6
   ,STAT = 7
   ,SPONG1 = 8
   ,SPONG2 = 9
   ,PR1 = 10
   ,PR2 = 11
   ,PRED = 12
   ,SUCC1 = 13
   ,SUCC2 = 14
   ;
   */

    final static public String
         ADD = "ADD"
        ;
    
    /**
     * main unused here
     * @param args
     */
    public static void main(String[] args) {
    }
}
