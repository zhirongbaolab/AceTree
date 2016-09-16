

/**
 *
 *    Copyright 2003-2005 Tassy Olivier/Lemaire Patrick CNRS Marseille
 *    3D virtual embryo program is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or any later version.
 *    
 *    3D virtual embryo is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *    
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *    
 */


package org.rhwlab.image;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;
import com.sun.j3d.utils.geometry.*;

public class Axis3D extends BranchGroup {

////////////////////////////////////////////////////////////////////////////////////////////////////
//Constructor    
    public Axis3D(double X, double Y, double Z) {
        
        Transform3D translate = new Transform3D();
        translate.set(new Vector3d(X, Y, Z));
        TransformGroup TG = new TransformGroup(translate);
        TG.addChild(createGeometry());
        this.addChild(TG);

    }
////////////////////////////////////////////////////////////////////////////////////////////////////    
    private Group createGeometry(){

        Group tripod = new Group();

        //origin
        ColorCube origin = new ColorCube(0.03f);
        tripod.addChild(origin);
        
        //Y axis
        Appearance appY = new Appearance();
        Color3f objColorY = new Color3f(Color.green);
        ColoringAttributes caY = new ColoringAttributes();
        caY.setColor(objColorY);
        appY.setColoringAttributes(caY);
        
        Transform3D translate1 = new Transform3D();
        translate1.set(new Vector3f(0f,0.3f,0f));
        TransformGroup TG1 = new TransformGroup(translate1);
        Cone cone1 = new Cone(0.05f, 0.1f, Primitive.ENABLE_APPEARANCE_MODIFY, 4, 2, appY);
        TG1.addChild(cone1);
        tripod.addChild(TG1);

        ///////////////////////////////////////
        Transform3D translate2 = new Transform3D();
        translate2.set(new Vector3f(0f,0.15f,0f));
        TransformGroup TG2 = new TransformGroup(translate2);
        
        Cylinder cyl1 = new Cylinder(0.01f, 0.3f, Primitive.ENABLE_APPEARANCE_MODIFY, 4, 2, appY);
        TG2.addChild(cyl1);

        //Display axis names
        Font3D f3d = new Font3D(new Font("dialog", Font.PLAIN, 1),new FontExtrusion());
        Text3D text3d= new Text3D(
        f3d,"Y",new Point3f(0f,0f,0f),Text3D.ALIGN_CENTER,Text3D.PATH_RIGHT
        );
        //fix the 3d text displayed face
        OrientedShape3D orient = new OrientedShape3D(
        text3d,appY,OrientedShape3D.ROTATE_ABOUT_POINT,new Point3f(0f,0f,0f)
        );
        //TG in order to reduce the text size
        Transform3D t3dtxt = new Transform3D();
        t3dtxt.setScale(0.1f);
        TransformGroup scaletxt = new TransformGroup(t3dtxt);
        scaletxt.addChild(orient);
        
        TG2.addChild(scaletxt);
        tripod.addChild(TG2);
        
        //X axis
        Appearance appX = new Appearance();
        Color3f objColorX = new Color3f(Color.red);
        ColoringAttributes caX = new ColoringAttributes();
        caX.setColor(objColorX);
        appX.setColoringAttributes(caX);
        
        Transform3D translate3 = new Transform3D();
        translate3.set(new Vector3f(0.3f,0f,0f));
        
        Transform3D rotate1 = new Transform3D();
        rotate1.rotZ(-Math.PI/2d);
        
        translate3.mul(rotate1);
        
        TransformGroup TG3 = new TransformGroup(translate3);
        
        Cone cone2 = new Cone(0.05f, 0.1f, Primitive.ENABLE_APPEARANCE_MODIFY, 4, 2, appX);
        
        TG3.addChild(cone2);
        tripod.addChild(TG3);
        ///////////////////////////////////////
        Transform3D translate4 = new Transform3D();
        translate4.set(new Vector3f(0.15f,0f,0f));
        
        Transform3D rotate2 = new Transform3D();
        rotate2.rotZ(Math.PI/2d);
        
        translate4.mul(rotate2);
        
        TransformGroup TG4 = new TransformGroup(translate4);
        
        Cylinder cyl2 = new Cylinder(0.01f, 0.3f, Primitive.GENERATE_NORMALS, 4, 2, appX);

        TG4.addChild(cyl2);

        //Display axis names
        Font3D f3dX = new Font3D(new Font("dialog", Font.PLAIN, 1),new FontExtrusion());
        Text3D text3dX= new Text3D(
        f3dX,"X",new Point3f(0f,0f,0f),Text3D.ALIGN_CENTER,Text3D.PATH_RIGHT
        );
         //fix the 3d text displayed face
        OrientedShape3D orientX = new OrientedShape3D(
        text3dX,appX,OrientedShape3D.ROTATE_ABOUT_POINT,new Point3f(0f,0f,0f)
        );
        //TG to reduce text size
        Transform3D t3dtxtX = new Transform3D();
        t3dtxtX.setScale(0.1f);
        TransformGroup scaletxtX = new TransformGroup(t3dtxtX);
        scaletxtX.addChild(orientX);
        
        TG4.addChild(scaletxtX);
        tripod.addChild(TG4);
        
        //Z axis
        Appearance appZ = new Appearance();
        Color3f objColorZ = new Color3f(Color.blue);
        ColoringAttributes caZ = new ColoringAttributes();
        caZ.setColor(objColorZ);
        appZ.setColoringAttributes(caZ);
        
        Transform3D translate5 = new Transform3D();
        translate5.set(new Vector3f(0f,0f,0.3f));
        
        Transform3D rotate3 = new Transform3D();
        rotate3.rotX(Math.PI/2d);
        
        translate5.mul(rotate3);
        
        TransformGroup TG5 = new TransformGroup(translate5);
        
        Cone cone3 = new Cone(0.05f, 0.1f, Primitive.ENABLE_APPEARANCE_MODIFY, 4, 2, appZ);
        TG5.addChild(cone3);
        tripod.addChild(TG5);
        ///////////////////////////////////////
        Transform3D translate6 = new Transform3D();
        translate6.set(new Vector3f(0f,0f,0.15f));
        
        Transform3D rotate4 = new Transform3D();
        rotate4.rotX(Math.PI/2d);
        
        translate6.mul(rotate4);
        
        TransformGroup TG6 = new TransformGroup(translate6);
        
        Cylinder cyl3 = new Cylinder(0.01f, 0.3f, Primitive.ENABLE_APPEARANCE_MODIFY, 4, 2, appZ);
        TG6.addChild(cyl3);
        
        
        
        //Display axis names
        Font3D f3dZ = new Font3D(new Font("dialog", Font.PLAIN, 1),new FontExtrusion());
        Text3D text3dZ= new Text3D(
        f3dZ,"Z",new Point3f(0f,0f,0f),Text3D.ALIGN_CENTER,Text3D.PATH_RIGHT
        );
         //fix the 3d text displayed face
        OrientedShape3D orientZ = new OrientedShape3D(
        text3dZ,appZ,OrientedShape3D.ROTATE_ABOUT_POINT,new Point3f(0f,0f,0f)
        );
        //TG to reduce text size
        Transform3D t3dtxtZ = new Transform3D();
        t3dtxtZ.setScale(0.1f);
        TransformGroup scaletxtZ = new TransformGroup(t3dtxtZ);
        scaletxtZ.addChild(orientZ);
        
        TG6.addChild(scaletxtZ);
        tripod.addChild(TG6);
        
        
        return tripod;
        
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
} // end of class Axis3D
