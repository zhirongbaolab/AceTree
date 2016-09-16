package org.rhwlab.utils;

import java.text.DecimalFormat;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Angle3 {

	public Angle3() {
		Point3d p1 = new Point3d(100, 100, 141);
		Point3d p2 = new Point3d(0, 0, 0);
		Vector3d v = new Vector3d(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
		//v.normalize();
		Vector3d n = new Vector3d(0,0,1);
		double vdotn = v.dot(n);
		double vlength = v.length();
		double cosb = vdotn/vlength;
		double ang = Math.acos(cosb);
		println("Angle3, phi, " + ang + CS + Math.toDegrees(ang) + CS + cosb + CS + vlength + CS + vdotn);

		Vector3d mvdotn = new Vector3d(0, 0, -vdotn);

		v.add(mvdotn);
		Vector3d x = new Vector3d(1,0,0);
		double costheta = v.dot(x)/v.length();
		double theta = Math.acos(costheta);
		println("Angle3, theta, " + theta + CS + Math.toDegrees(theta));
		println("Angle3, " + v);


		//Vector3d vxy = v * Math.sin(ang);


	}

	static public double [] angles(double x1, double y1, double z1, double x2, double y2, double z2) {
		double [] rtn = new double[2];
		Vector3d v = new Vector3d(x1 - x2, y1 - y2, z1 - z2);
		Vector3d n = new Vector3d(0,0,1);
		double vdotn = v.dot(n);
		double vlength = v.length();
		double cosb = vdotn/vlength;
		double phi = Math.acos(cosb);
		rtn[0] = Math.toDegrees(phi);

		Vector3d mvdotn = new Vector3d(0, 0, -vdotn);
		v.add(mvdotn);
		Vector3d x = new Vector3d(1,0,0);
		double costheta = v.dot(x)/v.length();
		double theta = Math.acos(costheta);
		rtn[1] = Math.toDegrees(theta);
		return rtn;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Angle3();
		double [] phiTheta = Angle3.angles(150, 150, 191, 50, 50, 50);
		println("phiTheta, " + phiTheta[0] + CS + phiTheta[1]);

	}

    private static void println(String s) {System.out.println(s);}
    private static void print(String s) {System.out.print(s);}
    private static final String CS = ", ";
    private static final String TAB = "\t";
    private static final DecimalFormat DF0 = new DecimalFormat("####");
    private static final DecimalFormat DF1 = new DecimalFormat("####.#");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt4(double d) {return DF4.format(d);}
    private static String fmt1(double d) {return DF1.format(d);}
    private static String fmt0(double d) {return DF0.format(d);}

}
