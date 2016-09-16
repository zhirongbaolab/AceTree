package org.rhwlab.image;
/*
 * NamedSphere.java
 *
 * separated from Image3D2.java 1.31.2014
 * used in Image3D2 makeNamedSphere() and getPickedNucleusNames() 
 */

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import javax.media.j3d.Appearance;

public class NamedSphere extends Sphere {
    private String iName;

    public NamedSphere(String name, float r, Appearance a) {
        super(r, Primitive.GENERATE_NORMALS, 30, a);
        iName = name;
    }

    @Override
	public String getName() {
        return iName;
    }
}
