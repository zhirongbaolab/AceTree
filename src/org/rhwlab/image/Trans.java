package org.rhwlab.image;
/*
 * Trans.java
 *
 * separated from Image3D2.java 1.31.2014
 * called in Image3D2 by applyTrans() and handleRotatePanel()
 */

import javax.media.j3d.Transform3D;

public class Trans {
    private Transform3D  iT3d;
    private double       iAngInc;
    private char         iAxis;

    public Trans(Transform3D t, double a, char axis) {
        iT3d = t;
        iAngInc = a;
        iAxis = axis;
    }

    public Transform3D getT3D() {
        return iT3d;
    }

    public double getAngInc() {
        return iAngInc;
    }

    public char getAxis() {
        return iAxis;
    }
}
