/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */
package org.rhwlab.nucedit;

import java.util.Vector;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;

/**
 * responds to menu commands on the edit dialog and makes
 * the requested changes in the nuclei vector of NucleiMgr
 * <br>
 * holds an array of NucleiTableModel objects which are potentially
 * editable by the user
 * @author biowolp
 * @version 1.0 Feb 11, 2005
  */
public class NucEditModel {
    NucleiMgr iNucleiMgr;
    NucleiTableModel [] iNucleiTableModel;
    
    /**
     * construct a NucEditModel from an AceTree object
     * only the NucleiMgr object of AceTree is accessed
     * @param nucEdit the AceTree object which is available in
     * the NucEditDialog which calls this constructor
     */
    public NucEditModel(AceTree nucEdit) {
        iNucleiMgr = nucEdit.getNucleiMgr();
        iNucleiTableModel = new NucleiTableModel[2];
        iNucleiTableModel[0] = new NucleiTableModel();
        iNucleiTableModel[1] = new NucleiTableModel();
    }
    
    /**
     * makes changes based on contents of the two spreadsheets of
     * the edit dialog
     * @param time int time of the top nucleus
     */
    public void commitAllChanges(int time) {
        commitChanges(0, time, true);
        commitChanges(1, time + 1, false);
    }
    
    
    @SuppressWarnings("unused")
	private void commitChanges(int tableModel, int i, boolean doSuccessors) {
        if (doSuccessors) {
            if(setSuccessors(i)) return;;
        }
        NucleiTableModel nucleiTableModel = iNucleiTableModel[tableModel];
        Vector nuclei = iNucleiMgr.getNucleiRecord().elementAt(i);
        int m = nuclei.size();
        Nucleus n;
        String s;
        for (int j=0; j<m; j++) {
            n = (Nucleus)nuclei.elementAt(j);
            updateNucleus(nucleiTableModel, n, j, i);
        }
        // added rows are processed here
        int k = nucleiTableModel.getRowCount();
        for (int j = m; j < k; j++) {
            n = new Nucleus();
            System.out.println("new    : " + n);
            updateNucleus(nucleiTableModel, n, j, i);
            System.out.println("updated: " + n);
            nuclei.add(n);
            
        }
        nucleiTableModel.updateNucleiTableModel(iNucleiMgr, i);
    }
    
    private void updateNucleus(NucleiTableModel ntm, Nucleus n, int nucTableModelItem, int timeIndex) {
        int j = nucTableModelItem;
        
        String s;
        s = ntm.getValueAt(j, NucleiTableModel.IDENT).toString();
        if (s.indexOf(NucleiTableModel.ADD) == 0) {
            updateAddedNucleus(ntm, n, j, timeIndex);
            return;
        } else n.identity = s;
        s = ntm.getValueAt(j, NucleiTableModel.INDEX).toString();
        n.index = Integer.parseInt(s);
        s = ntm.getValueAt(j, NucleiTableModel.X).toString();
        n.x = Integer.parseInt(s);
        s = ntm.getValueAt(j, NucleiTableModel.Y).toString();
        n.y = Integer.parseInt(s);
        s = ntm.getValueAt(j, NucleiTableModel.Z).toString();
        n.z = Float.parseFloat(s);
        s = ntm.getValueAt(j, NucleiTableModel.SIZE).toString();
        n.size = Integer.parseInt(s);
        s = ntm.getValueAt(j, NucleiTableModel.WT).toString();
        n.weight = Integer.parseInt(s);
        s = ntm.getValueAt(j, NucleiTableModel.STAT).toString();
        n.status = Integer.parseInt(s);
        //s = ntm.getValueAt(j, NucleiTableModel.SPONG1).toString();
        //n.sponginess1 = Float.parseFloat(s);
        //s = ntm.getValueAt(j, NucleiTableModel.SPONG2).toString();
        //n.sponginess2 = Float.parseFloat(s);
        //s = ntm.getValueAt(j, NucleiTableModel.PR1).toString();
        //n.piece_ratio = Float.parseFloat(s);
        //s = ntm.getValueAt(j, NucleiTableModel.PR2).toString();
        //n.piece_ratio2 = Float.parseFloat(s);
        s = ntm.getValueAt(j, NucleiTableModel.PRED).toString();
        n.predecessor = Integer.parseInt(s);
        s = ntm.getValueAt(j, NucleiTableModel.SUCC1).toString();
        n.successor1 = Integer.parseInt(s);
        s = ntm.getValueAt(j, NucleiTableModel.SUCC2).toString();
        n.successor2 = Integer.parseInt(s);
        n.rwraw = 1;
    }
    
    private void updateAddedNucleus(NucleiTableModel ntm, Nucleus n, int nucTableModelItem, int timeIndex) {
        // we copy mostly everything from the predecessor
        // assume that additions are only permitted in the second table
        
        int j = nucTableModelItem;
        System.out.println("nucTableModelItem: " + j);
        String s;
        // the index, identity, predecessor, successor1, and successor2
        // are from the actual table
        s = ntm.getValueAt(j, NucleiTableModel.INDEX).toString();
        System.out.println("updateAddedNucleus1: " + s);
        n.index = Integer.parseInt(s);
        s = ntm.getValueAt(j, NucleiTableModel.IDENT).toString();
        n.identity = s;
        System.out.println("updateAddedNucleus2: " + s);
        s = ntm.getValueAt(j, NucleiTableModel.PRED).toString();
        System.out.println("updateAddedNucleus pred: " + s);
        n.predecessor = Integer.parseInt(s);
        s = ntm.getValueAt(j, NucleiTableModel.SUCC1).toString();
        System.out.println("updateAddedNucleus succ1: " + s);
        n.successor1 = Integer.parseInt(s);
        s = ntm.getValueAt(j, NucleiTableModel.SUCC2).toString();
        System.out.println("updateAddedNucleus succ2: " + s);
        n.successor2 = Integer.parseInt(s);
        n.rwraw = 1;

        // everything else is copied from the predecessor or successor
        //s = ntm.getValueAt(j, NucleiTableModel.PRED).toString();
        Nucleus m = null; // we will copy data from this nucleus when assigned
        int link = n.predecessor;
        int time = timeIndex - 1;

        // 20050627 what did I have in mind here?
        if (link  < 0) {
            //s = ntm.getValueAt(j, NucleiTableModel.SUCC1).toString();
            link = n.successor1;
            time = timeIndex + 1;
        }
        //int time = iNucleiTableModel[0].getIndex() + 1; // this is the 'next' time
        if (link < 1) return;
        m = (Nucleus)(((Vector)iNucleiMgr.getNucleiRecord().elementAt(time)).elementAt(link - 1));
        //System.out.println("updateAdded pred=" + pred + ", time=" + (j + 1));
        n.x = m.x;
        n.y = m.y;
        n.z = m.z;
        n.size = m.size;
        n.weight = m.weight;
        n.status = m.status;
        //n.sponginess1 = m.sponginess1;
        //n.sponginess2 = m.sponginess2;
        //n.piece_ratio = m.piece_ratio;
        //n.piece_ratio2 = m.piece_ratio2;
    }
    
    private boolean setSuccessors(int i) {
        boolean rtn = false;
        NucleiTableModel ntm0 = iNucleiTableModel[0];
        String nil = String.valueOf(Nucleus.NILLI);
        for (int j=0; j < ntm0.getRowCount(); j++) {
            ntm0.setValueAt(nil, j, NucleiTableModel.SUCC1);
            ntm0.setValueAt(nil, j, NucleiTableModel.SUCC2);
        }
        NucleiTableModel ntm1 = iNucleiTableModel[1];
        for (int j=0; j < ntm1.getRowCount(); j++) {
            Object stat = ntm1.getValueAt(j, NucleiTableModel.STAT);
            int nstat = Integer.parseInt(stat.toString());
            if (nstat < 0) continue; // negative status
            Object m = ntm1.getValueAt(j, NucleiTableModel.INDEX);
            Object ns = ntm1.getValueAt(j, NucleiTableModel.PRED);
            int n = Integer.parseInt(ns.toString()) - 1;
            if (n < 0) continue;
            Object s1 = ntm0.getValueAt(n, NucleiTableModel.SUCC1);
            Object s2 = ntm0.getValueAt(n, NucleiTableModel.SUCC2);
            // if no successor1 assignment has been made, make it now
            if (s1.toString().equals(nil)) {
                ntm0.setValueAt(m, n, NucleiTableModel.SUCC1);
            } else if (s2.toString().equals(nil)){
                // else use successor2 position if it is free
                ntm0.setValueAt(m, n, NucleiTableModel.SUCC2);
            } else {
                System.out.println("ERROR CONDITION row " + j + ", i=" + i);
                System.out.println(s1 + "\n" + s2);
                rtn = true;
                //System.exit(1);
            }
        }
        return rtn;
    }
    
    
    /**
     * access function for one of the two NucleiTableModel array elements
     * @param k the one you want (0 or 1)
     * @return NucleiTableModel object
     */
    public NucleiTableModel getNucleiTableModel(int k) {
        return iNucleiTableModel[k];
    }
    
    /**
     * called from menu action; delegates work to the NucleiTableModel
     * objects
     * @param j int time associated with the top spreadsheet
     */
    public void updateNucleiTableModel(int j) {
        iNucleiTableModel[0].updateNucleiTableModel(iNucleiMgr, j);
        iNucleiTableModel[1].updateNucleiTableModel(iNucleiMgr, j + 1);
    }
    
    public NucleiMgr getNucleiMgr() {
        return iNucleiMgr;
    }
    
    public static void main(String[] args) {
    }
}
