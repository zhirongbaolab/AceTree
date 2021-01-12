/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 * Created on Sep 22, 2005
 */
package org.rhwlab.snight;

import org.rhwlab.image.ImageWindow;

/**
 * Loc is used in Identity and Analysis4.
 * It converts the y and z coordinates of the embryo
 * to ADL orientation.
 * If the embryo orientation is ADL nothing changes.
 * If it is AVR then the values are converted.
 * This is necessary for applying the NEWCANONICAL naming rules.
 * The converted values are available as public members of Loc.
 * @author biowolp
 * @version 1.0
 */
public class Loc {

    public int x;
    public int y;
    public int z;
    
    public Loc(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Loc(Nucleus n, NucleiMgr nucMgr) {
        // here this object handles the embryo on left or right side
        // issue but does not handle the possibility of ap axis reversal
        int orientation = nucMgr.getParameters().dvInit;
        if (orientation > 0) {
            x = n.x;
            y = n.y;
            z = (int)(n.z * nucMgr.getZPixRes());
        } else {
            x = n.x;
            y = /* ImageWindow.cImageHeight - */n.y;
            int plane_end = nucMgr.getMovie().plane_end;
            z = (int)(nucMgr.getZPixRes() * (plane_end - 1) - nucMgr.getZPixRes() * n.z);
            //System.out.println("**LOCwidth=" + ImageWindow.cImageWidth);
        }
    }
    
    public Loc(Loc copy) {
        x = copy.x;
        y = copy.y;
        z = copy.z;
    }
    
    public Loc sub(Loc subtrahend) {
        Loc difference = new Loc(this);
        difference.x -= subtrahend.x;
        difference.y -= subtrahend.y;
        difference.z -= subtrahend.z;
        return difference;
        
    }
    
    public Loc add(Loc addend) {
        Loc sum = new Loc(this);
        sum.x += addend.x;
        sum.y += addend.y;
        sum.z += addend.z;
        return sum;
    }
    
    public Loc div(int k) {
        x = x/k;
        y = y/k;
        z = z/k;
        return this;
    }
    
    @Override
	public String toString() {
        String s = x + ", " + y + ", " + z;
        return s;
    }
    
    public static void main(String[] args) {
        Loc a = new Loc(1,2,3);
        System.out.println("a=" + a);
        a = a.add(new Loc(3,2,1));
        System.out.println("a=" + a);
        a = a.div(2);
        System.out.println("a=" + a);
        a = a.sub(new Loc(5,4,3));
        System.out.println("a=" + a);
        Loc b = new Loc(a);
        System.out.println("b=" + b);
        b = b.div(2);
        
        System.out.println("b=" + b);
        System.out.println("a=" + a);
        
    }
}
