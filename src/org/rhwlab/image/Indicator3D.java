/*
 * Created on Nov 3, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.image;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.text.DecimalFormat;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Light;
import javax.media.j3d.LineArray;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Indicator3D extends JPanel {
    private SimpleUniverse  iUniverse;
    private Canvas3D        iCanvas;
    BranchGroup iBGT;
    BranchGroup iBG;

    private Transform3D     iRotate;
    private TransformGroup  iRotGroup;
    private Transform3D     iTranslate;
    private TransformGroup  iTranslateGroup;
    private Vector3d        iTranslateVec;
    private Matrix4d        iMatrix;
    private double          iZViewPos;

    public Indicator3D() {
        super();
        Dimension d = new Dimension(200, 200);
        setPreferredSize(d);
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        iCanvas = new Canvas3D(config);
        iCanvas.setSize(d.width, d.height);
        add(iCanvas);
        //println("Indicator3D, " + iCanvas + CS + getWidth());
        iUniverse = new SimpleUniverse(iCanvas);
        iUniverse.getViewingPlatform().setNominalViewingTransform();
        ViewingPlatform viewingPlatform = iUniverse.getViewingPlatform( );
        iTranslateGroup = viewingPlatform.getViewPlatformTransform( );
        /*
        Transform3D t3d = new Transform3D();
        iTranslateGroup.getTransform(t3d);
        Matrix4d m4d = new Matrix4d();
        t3d.get(m4d);
        println("m4d, " + m4d);
        println("t3d, " + t3d);
        m4d.m23 = .554;
        t3d.set(m4d);
        iTranslateGroup.setTransform(t3d);
        */
        //iTranslate = new Transform3D();
        //iTranslateVec = new Vector3d( 0.0, 0.0, 0.0 );
        //iMatrix  = new Matrix4d( );

        //iTranslateGroup.getTransform(iTranslate);
        //iTranslate.mul(.5);
        //iTranslateGroup.getTransform(iTranslate);
        //iTranslate.get(iMatrix);
        //iZViewPos = iMatrix.m23;
        //iTranslate.set(iMatrix);
        //iTranslateGroup.setTransform(iTranslate);
        insertContent();
        //JFrame frame = new JFrame("Indicator3DTest");
        //frame.getContentPane().add(this);
        //frame.pack();
        //frame.show();
    }

    public static void main(String[] args) {
        println("main, ");
        JFrame frame = new JFrame("Indicator3DTest");
        Indicator3D indicator = new Indicator3D();
        frame.getContentPane().add(indicator);
        frame.pack();
        frame.setVisible(true);

    }

    public void insertContent() {
        //println("insertContent, ");
        if (iBG != null) iBG.detach();
        iBG = createSceneGraph();
        iUniverse.addBranchGraph(iBG);
    }

    private BranchGroup createSceneGraph() {
        //println("createSceneGraph, ");
        BranchGroup root = new BranchGroup();
        root.setCapability(BranchGroup.ALLOW_DETACH);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
        Color3f bgColor   = new Color3f(0.3f, 0.3f, 0.3f); // light gray
        Color3f lColor1   = new Color3f(1f, 1f, 1f);
        Vector3d lPos1 =  new Vector3d(0.0, 0.0, 2.0);
        Vector3f lDirect1 = new Vector3f(lPos1);
        lDirect1.negate();
        Light lgt1 = new DirectionalLight(lColor1, lDirect1);
        lgt1.setInfluencingBounds(bounds);
        root.addChild(lgt1);
        Background bg = new Background(bgColor);
        bg.setApplicationBounds(bounds);
        root.addChild(bg);

        TransformGroup objRotate = new TransformGroup();
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        //objRotate.addChild(new Nuclei3D().getBG());
        //new Nuclei3D();
        //new Talisman();
        iBGT = new Axis3D(0, 0, 0);

        //new Axis();
        iBGT.compile();
        objRotate.addChild(iBGT);
        TransformGroup initRotGroup = new TransformGroup();
        Transform3D initRotate = new Transform3D();
        initRotate.setScale(0.5);
        initRotGroup.setTransform(initRotate);
        initRotGroup.addChild(objRotate);

        if (iRotate == null) iRotate = new Transform3D();
        iRotate.setScale(SCALE);
        iRotGroup = new TransformGroup(iRotate);
        iRotGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        iRotGroup.addChild(initRotGroup);

        root.addChild(iRotGroup);
        root.compile();
        return root;
    }

    public void apply(Transform3D t3d) {
        iRotate.mul(t3d);
        iRotGroup.setTransform(iRotate);
    }

    public void restore() {
        //Transform3D t = new Transform3D();
        iRotate.setIdentity();
        iRotate.setScale(SCALE);
        iRotGroup.setTransform(iRotate);
    }

    private static final double
         SCALE = 4
        ;

    public class Talisman {
        public Talisman() {
            //println("Talisman, ");
            iBGT = new BranchGroup();
            float p = 0.1f;
            float q = 0.06f;
            float r = 0.04f;
            float z = 0f;
            float d = .02f;
            iBGT.addChild(makeNamedSphere("anterior", -p, z, z, d, setColor(ColorConstants.magenta)));
            iBGT.addChild(makeNamedSphere("posterior", p, z, z, d, setColor(ColorConstants.cyan)));
            iBGT.addChild(makeNamedSphere("dorsal",    z, q, z, d, setColor(ColorConstants.red)));
            iBGT.addChild(makeNamedSphere("ventral",   z,-q, z, d, setColor(ColorConstants.green)));
            iBGT.addChild(makeNamedSphere("left",      z, z, r, d, setColor(ColorConstants.orange)));
            iBGT.addChild(makeNamedSphere("right",     z, z,-r, d, setColor(ColorConstants.yellow)));

            //iBGT.addChild(makeNamedSphere("bogus",     z, z, 1.845f, d, setColor(ColorConstants.yellow)));

            LineArray axisXLines = new LineArray(2, GeometryArray.COORDINATES | GeometryArray.COLOR_3   );
            iBGT.addChild(new Shape3D(axisXLines));
            axisXLines.setCoordinate(0, new Point3f(-p, z, z));
            axisXLines.setCoordinate(1, new Point3f( p, z, z));
            axisXLines.setColor(0, ColorConstants.magenta);
            axisXLines.setColor(1, ColorConstants.magenta);
            LineArray axisYLines = new LineArray(2, GeometryArray.COORDINATES | GeometryArray.COLOR_3   );
            iBGT.addChild(new Shape3D(axisYLines));
            axisYLines.setCoordinate(0, new Point3f(z, -q, z));
            axisYLines.setCoordinate(1, new Point3f(z,  q, z));
            axisYLines.setColor(0, ColorConstants.green);
            axisYLines.setColor(1, ColorConstants.green);
            LineArray axisZLines = new LineArray(2, GeometryArray.COORDINATES | GeometryArray.COLOR_3   );
            iBGT.addChild(new Shape3D(axisZLines));
            axisZLines.setCoordinate(0, new Point3f(z, z, -r));
            axisZLines.setCoordinate(1, new Point3f(z, z, r));
            axisZLines.setColor(0, ColorConstants.yellow);
            axisZLines.setColor(1, ColorConstants.yellow);
        }

        private Appearance setColor(Color3f color) {
            Color3f eColor    = new Color3f(0.0f, 0.0f, 0.0f);
            Color3f sColor    = color;
            Material m = new Material(eColor, eColor, sColor, sColor, 100.0f);
            m.setLightingEnable(true);
            Appearance app = new Appearance();
            app.setMaterial(m);
            //app.setColoringAttributes(new ColoringAttributes(new Color3f(1f,0f,0f), ColoringAttributes.SHADE_FLAT));
            return app;
        }

        private TransformGroup makeNamedSphere(String name, float x, float y, float z, float r, Appearance a) {
            //println("makeNamedSphere, " + name);
            Transform3D translate = new Transform3D();
            translate.set(new Vector3f(x, y, z));
            NamedSphere sph = new NamedSphere(name, r, a);
            TransformGroup tg = new TransformGroup(translate);
            tg.addChild(sph);
            return tg;
        }

    }

    public class NamedSphere extends Sphere {
        String iName;

        public NamedSphere(String name, float r, Appearance a) {
            super(r, a);
            iName = name;
        }
    }


    private static void println(String s) {System.out.println(s);}
    private static final String CS = ", ";
    private static final DecimalFormat DF1 = new DecimalFormat("####.##");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");


}
