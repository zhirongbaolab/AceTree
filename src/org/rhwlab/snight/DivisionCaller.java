package org.rhwlab.snight;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.rhwlab.acetree.AceTree;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;


/**
 * Called to assign names on cell division
 * Previously: embryos in strict starting orientations
 * Revised: Any starting orientation can be used to calculate names
 * 
 * Original @author 
 * 
 * Revision @author Braden Katzman
 * Date: July 2016 - September 2016
 */

public class DivisionCaller {

	Hashtable<String, Rule>		iRulesHash;
	Hashtable<String, String>		iSulstonHash;
	String			iAxis;
	String			iAxisUse;
	double			iZPixRes;
	MeasureCSV		iMeasureCSV;
	double			iAng;
	double			iEMajor;
	double			iEMinor;
	double			iZSlope;
	Point2D			iAngVec;
	double			iDMajor;
	double			iDMinor;
	double			iDSlope;
	boolean			iDebug;
	double [] 		iDaCorrected;

	private BooleanProperty auxInfoVersion2;
	private CanonicalTransform canTransform;

	/**
	 * The constructor called by datasets that use the AuxInfo v1.0 compressed embryo scheme
	 * Also called by Orientation.java
	 * 
	 * @param measureCSV
	 */
	public DivisionCaller(MeasureCSV measureCSV) {
		System.out.println("Using AuxInfo version 1.0");
		this.iMeasureCSV = measureCSV;
		this.iRulesHash = new Hashtable<String, Rule>();
		this.auxInfoVersion2 = new SimpleBooleanProperty();
		this.auxInfoVersion2.set(false);
		this.iAxis = iMeasureCSV.iMeasureHash.get(MeasureCSV.att_v1[MeasureCSV.AXIS_v1]);
		readNewRules();
		readSulstonRules();

		String zpixres = iMeasureCSV.iMeasureHash.get(MeasureCSV.att_v1[MeasureCSV.ZPIXRES_v1]);
		if (zpixres != null) {
			this.iZPixRes = Double.parseDouble(zpixres);
		} else {
			this.iZPixRes = 1; //default to this
		}

		getScalingParms();
	}

	/**
	 * The constructor called by datasets that use the AuxInfo v2.0 uncompressed embryo scheme
	 * 
	 * @param measureCSV
	 * @param canTrans - the transform representing the rotations to AP and LR canonical
	 */
	public DivisionCaller(MeasureCSV measureCSV, CanonicalTransform canTransform) {
		System.out.println("Using AuxInfo version 2.0");
		this.iMeasureCSV = measureCSV;
		this.iRulesHash = new Hashtable<String, Rule>();
		this.canTransform = canTransform;
		readNewRules();
		readSulstonRules();

		/*
		 * initialize based on AuxInfo v1.0 or v2.0
		 */
		this.auxInfoVersion2 = new SimpleBooleanProperty();
		this.auxInfoVersion2.set(true);
		this.auxInfoVersion2.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				/*
				 * If AuxInfo v2.0 fails at any point, get the axis and angle from the AuxInfo v1.0
				 */
				if (!newValue) {
					iAxis = iMeasureCSV.iMeasureHash_v1.get(MeasureCSV.att_v1[MeasureCSV.AXIS_v1]);

					String sang = iMeasureCSV.iMeasureHash_v1.get("ang");
					if (sang.length() > 0) {
						iAng = Math.toRadians(-Double.parseDouble(sang));
					}

					iAngVec = new Point2D(Math.cos(iAng), Math.sin(iAng));
				}
			}
		});

		this.iZPixRes = Double.parseDouble(iMeasureCSV.iMeasureHash.get(MeasureCSV.att_v2[MeasureCSV.ZPIXRES_v2]));

		getScalingParms();
	}

	private void getScalingParms() {		
		if (!(auxInfoVersion2.get())) {
			initAng();
		}

		String smaj = iMeasureCSV.iMeasureHash.get("maj");
		if (smaj != null && smaj.length() > 0) {
			iEMajor = Double.parseDouble(smaj);
		} else {
			if (MeasureCSV.isAuxInfoV2()) {
				iEMajor = Double.parseDouble(MeasureCSV.defaultAtt_v2[MeasureCSV.EMAJOR_v2]);
			} else {
				iEMajor = Double.parseDouble(MeasureCSV.defaultAtt_v1[MeasureCSV.EMAJOR_v1]);
			}
		}

		String smin = iMeasureCSV.iMeasureHash.get("min");
		if (smin != null && smin.length() > 0) {
			iEMinor = Double.parseDouble(smin);
		} else {
			if (MeasureCSV.isAuxInfoV2()) {
				iEMinor = Double.parseDouble(MeasureCSV.defaultAtt_v2[MeasureCSV.EMINOR_v2]);
			} else {
				iEMinor = Double.parseDouble(MeasureCSV.defaultAtt_v1[MeasureCSV.EMINOR_v1]);
			}	
		}


		String szslope = iMeasureCSV.iMeasureHash.get("zslope");
		if (szslope != null && szslope.length() > 0) {
			iZSlope = Double.parseDouble(szslope);
		} else {
			if (auxInfoVersion2.get()) {
				iZSlope = Double.parseDouble(MeasureCSV.defaultAtt_v2[MeasureCSV.ZSLOPE_v2]);
			} else {
				iZSlope = Double.parseDouble(MeasureCSV.defaultAtt_v1[MeasureCSV.ZSLOPE_v1]);
			}
		}

		if (MeasureCSV.isAuxInfoV2()) {
			iDMajor = Double.parseDouble(MeasureCSV.defaultAtt_v2[MeasureCSV.EMAJOR_v2]);
			iDMinor = Double.parseDouble(MeasureCSV.defaultAtt_v2[MeasureCSV.EMINOR_v2]);
			iDSlope = Double.parseDouble(MeasureCSV.defaultAtt_v2[MeasureCSV.ZSLOPE_v2]);
		} else {
			iDMajor = Double.parseDouble(MeasureCSV.defaultAtt_v1[MeasureCSV.EMAJOR_v1]);
			iDMinor = Double.parseDouble(MeasureCSV.defaultAtt_v1[MeasureCSV.EMINOR_v1]);
			iDSlope = Double.parseDouble(MeasureCSV.defaultAtt_v1[MeasureCSV.ZSLOPE_v1]);
		}
	}

	/**
	 * Initializes the "ang" field for assigning names if AuxInfo version 1.0 is being used
	 */
	private void initAng() {
		if (!(auxInfoVersion2.get())) {
			String sang = iMeasureCSV.iMeasureHash.get("ang");
			if (sang != null) {
				if (sang.length() > 0) {
					iAng = Math.toRadians(-Double.parseDouble(sang));
				}
			}

			iAngVec = new Point2D(Math.cos(iAng), Math.sin(iAng));
		}
	}

	/**
	 * Only used for debugging purposes
	 */
	public void showMeasureCSV() {
		Enumeration<?> e = iMeasureCSV.iMeasureHash.keys();
		while (e.hasMoreElements()) {
			String key = (String)e.nextElement();
			String value = iMeasureCSV.iMeasureHash.get(key);
			println("showMeasureCSV, " + key + CS + value);
		}
	}

	public String getRuleString(String parent) {
		Rule r = iRulesHash.get(parent);
		if (r == null) return "";
		else return r.toString();
	}

	/**
	 * Given a parent Nucleus, return Rule if already created, create if not
	 * @param parent
	 * @return
	 */
	private Rule getRule(Nucleus parent) {
		Rule r = iRulesHash.get(parent.identity);

		/*
		 * If no rule is found, create rule based on division direction and axis
		 */
		if (r == null) {
			String pname = parent.identity;
			//System.out.println("DivisionCaller.getRule parent identity: "+pname);
			String sulston = iSulstonHash.get(pname);

			//System.out.println("Sulston for: " + pname + " is --> " + sulston);

			//If no name, set default as 'a'
			if (sulston == null || pname.startsWith("Nuc")) {
				sulston = "a";
			} else { //used the first letter of the sulston name
				sulston = sulston.substring(0, 1);
			}

			//System.out.println("Using first letter: '" +  sulston + "' for daughter_1 and: '" + complement(sulston.charAt(0)) + "' for daughter_2");

			// append parent identity with sulston letter for daughter_1 sulston name 
			String sdau1 = parent.identity + sulston;
			//System.out.println("Sulston daughter_1 for parent: " + pname + ", is --> " + sdau1);

			// get the letter representing the division opposite of this division
			char c = complement(sulston.charAt(0));

			// set the daughter cell's sulston name with parent identity and complement of division
			String sdau2 = parent.identity + c;
			//System.out.println("Sulston daughter_2 for parent: " + pname + ", is --> " + sdau2);

			// set the xyz axis of this rule based on sulston first letter
			/*
			 * If the first daughter divides in the anterior direction, the Rule vector is <1,0,0> in AuxInfo1
			 * If the first daughter divides in the left direction, the Rule vector is <0,0,1>
			 * If the first daughter divides in the dorsal direction, the Rule vector is <0,1,0> (else condition always seems to be when sulston equals "d")
			 */
			int x = 0;
			int y = 0;
			int z = 0;
			if (sulston.equals("a")) {
				x = 1;
			} else if (sulston.equals("l")) {
				z = 1;
			} else { //usually "d"
				y = 1;
			}

			// append a 0 to the sulston name
			sulston += "0";

			// create a rule for this parent which contains parent name, the directional suslton letter for the first daughter, and the vector corresponding
			r = new Rule(pname, sulston, sdau1, sdau2, x, y, z);

			if (!(auxInfoVersion2.get())) {
				// assuming dummy rules are late in embryonic development
				// introduce rotation
				if (iAxis.equals("ADL"))
					iAxisUse = "ARD";
				if (iAxis.equals("AVR"))
					iAxisUse = "ALV";
				if (iAxis.equals("PDR"))
					iAxisUse = "PLD";
				if (iAxis.equals("PVL"))
					iAxisUse = "PRV";
				if (iAxis == null)
					iAxisUse = MeasureCSV.defaultAtt_v1[MeasureCSV.AXIS_v1];
			}
		}
		return r;
	}

	public double getDotProduct(Nucleus parent, Nucleus dau1, Nucleus dau2) {
		Rule r = getRule(parent);
		return getDotProduct(parent, dau1, dau2, r);
	}

	public double [] getDaCorrected() {
		return iDaCorrected;
	}


	/**
	 * Determines the dot product between the vector in the Rule and the vector between the daughter cells
	 * The vector between the daughter cells is corrected and rotated before calculating dot
	 * 
	 * @param parent
	 * @param dau1
	 * @param dau2
	 * @param r
	 * @return
	 */
	private double getDotProduct(Nucleus parent, Nucleus dau1, Nucleus dau2, Rule r) {
		if (!(auxInfoVersion2.get())) {
			iAxisUse = iAxis;
		}

		// create a temporary template vector from the xyz coordinates of the rule
		Point3D template = new Point3D(r.iX, r.iY, r.iZ);

		// find the vector between the daughter cells, with corrections and rotations induced
		double [] daCorrected = diffsCorrected(dau1, dau2);

		// update the instance var
		iDaCorrected = daCorrected;

		// create vector from daughter vector coordinates
		Point3D sample = new Point3D(daCorrected[0], daCorrected[1], daCorrected[2]);

		// normalize the vector
		sample = sample.normalize();

		/*
		 * find and return the dot product of the normalized, corrected, and rotated
		 * vector between the two daughter cells
		 */
		double dotCorrected = template.dotProduct(sample);
		double dot = dotCorrected;
		Double Dot = new Double(dot);
		if (Dot.isNaN()) dot = 0;
		
		return dot;
	}

	/**
	 * Assigns names given a parent cell and its daughters based on their locations relative to the canonical axis
	 * 
	 * @param parent
	 * @param dau1
	 * @param dau2
	 * @return
	 */
	public void assignNames(Nucleus parent, Nucleus dau1, Nucleus dau2) {
		//		System.out.println("Assigning names in DivisionCaller.java for parent: " + parent.identity);
		String newd1 = "";
		String newd2 = "";


		// get an existing rule or create a new one based on the division direction of the daughters from the given parent
		Rule r = getRule(parent);

		// find the dot product between the vector in the Rule and the corrected, rotated vector between the daughter cells
		double dot = getDotProduct(parent, dau1, dau2, r);

		if (dot > 0) {
			newd1 = r.iDau1;
			newd2 = r.iDau2;
		}
		else {
			newd1 = r.iDau2;
			newd2 = r.iDau1;
		}
		dau1.identity = newd1;
		dau2.identity = newd2;
	}

	/**
	 * Find the vector between the daughter cells with corrections and rotations
	 * 
	 * @param d1
	 * @param d2
	 * @return the vector between the two daughters, corrected by constants and axis in use
	 */
	private double [] diffsCorrected(Nucleus d1, Nucleus d2) {
		double [] da = new double[3];
	
//		if (auxInfoVersion2.get()) {
//			da[0] = d1.x - d2.x;
//			da[1] = d1.y - d2.y;
//			da[2] = d1.z - d2.z;
//		} else {
//			da[0] = d2.x - d1.x;
//			da[1] = d2.y - d1.y;
//			da[2] = d2.z - d1.z;
//		}
		
		da[0] = d2.x - d1.x;
		da[1] = d2.y - d1.y;
		da[2] = d2.z - d1.z;
		
		// scale the z coordinate difference by the z pixel resolution i.e. the z scale
		da[2] *= iZPixRes;
		//if (iDebug) println("diffs, " + fmt4(da[0]) + CS + fmt4(da[1]) + CS + fmt4(da[2]));

		// induce rotations with corrections and scaling
		measurementCorrection(da);

		if (!(auxInfoVersion2.get())) {
			if (iAxisUse == null)
				iAxisUse = MeasureCSV.defaultAtt_v1[MeasureCSV.AXIS_v1];
			if (iAxisUse.equals("AVR")) {
				da[1] *= -1;
				da[2] *= -1;
			} else if (iAxisUse.equals("PVL")) {
				da[0] *= -1;
				da[1] *= -1;
			} else if (iAxisUse.equals("PDR")) {
				da[0] *= -1;
				da[2] *= -1;
			} else if (iAxisUse.equals("ARD")) {
				da[1] *= -1;
			} else if (iAxisUse.equals("ALV")) {
				da[2] *= -1;
			} else if (iAxisUse.equals("PLD")) {
				da[0] *= -1;
			} else if (iAxisUse.equals("PRV")) {
				da[1] *= -1;
				da[2] *= -1;
			}
		}

		return da;
	}

	/**
	 * Apply the rotations and make corrections
	 * 
	 * @param da
	 */
	private void measurementCorrection(double [] da) {
		// correct for angle
		if (auxInfoVersion2.get()) {
			canTransform.applyProductTransform(da);
		} else {
			double [] dxy = handleRotation_V1(da[0], da[1], iAng);
			da[0] = dxy[0];
			da[1] = dxy[1];	
		}
		
		// correct for x stretch
		da[0] *= (iEMajor/iDMajor);
		// correct for y stretch
		da[1] *= (iEMinor/iDMinor);
		// correct for z stretch
		da[2] *= (iZSlope/iDSlope);
	}


	/**
	 * Rotate the x and y coordinates of the vector between divided cells by the AuxInfo version 1.0 scheme
	 * 
	 * @param x
	 * @param y
	 * @param ang - in radians
	 * @return
	 */
	public static double [] handleRotation_V1(double x, double y, double ang) {
		double cosang = Math.cos(ang);
		double sinang = Math.sin(ang);
		double denom = cosang * cosang + sinang * sinang;
		double xpnum = x * cosang + y * sinang;
		double xp = xpnum / denom;
		double yp = (y - xp * sinang) / cosang;
		double [] da = new double[2];
		da[0] = xp;
		da[1] = yp;
		return da;
	}

	private void readNewRules() {
		URL url = AceTree.class.getResource("/org/rhwlab/snight/NewRules.txt");
		InputStream istream = null;
		try {
			istream = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(istream));
			String s;
			br.readLine(); //toss the header
			while (br.ready()) {
				s = br.readLine();
				if (s.length() == 0) continue;
				//println("readNewRules, " + s);
				String [] sa = s.split(TAB);
				Rule r = new Rule(sa);
				iRulesHash.put(sa[0], r);
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	public void readSulstonRules() {
		Hashtable<String, String> namingHash = new Hashtable<String, String>();
		URL url = AceTree.class.getResource("/org/rhwlab/snight/namesHash.txt");
		InputStream istream = null;
		try {
			istream = url.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(istream));
			String s;
			while (br.ready()) {
				s = br.readLine();
				if (s.length() == 0) continue;
				String [] sa = s.split(",");
				namingHash.put(sa[0], sa[1]);
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		iSulstonHash = namingHash;

	}

	/**
	 * Returns the opposite division given a division letter
	 * @param x - the character representing the direction of the division (A,P,D,V,L,R)
	 * @return - the division direction opposite of x
	 */
	private char complement(char x) {
		switch(x) {
		case 'a':
			return 'p';
		case 'p':
			return 'a';
		case 'd':
			return 'v';
		case 'v':
			return 'd';
		case 'l':
			return 'r';
		case 'r':
			return 'l';

		}
		return 'g';
	}


	public class Rule {
		double  DOTTOL = 0.6;
		String 	iParent;
		String  iRule;
		String	iDau1;
		String  iDau2;
		public double	iX;
		public double	iY;
		public double   iZ;

		public Rule(String [] sa) {
			iParent = sa[0];
			iRule = sa[1];
			iDau1 = sa[2];
			iDau2 = sa[3];
			iX = Double.parseDouble(sa[4]);
			iY = Double.parseDouble(sa[5]);
			iZ = Double.parseDouble(sa[6]);
		}

		// constructor for default rule
		public Rule(String parent, String rule, String dau1, String dau2, double x, double y, double z) {
			iParent = parent;
			iRule = rule;
			iDau1 = dau1;
			iDau2 = dau2;
			iX = x;
			iY = y;
			iZ = z;
			//println("Rule, default rule in use");
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(iParent);
			//sb.append(CS + iRule);
			sb.append(C + iDau1);
			sb.append(C + iDau2);
			sb.append(C + fmt4(iX));
			sb.append(C + fmt4(iY));
			sb.append(C + fmt4(iZ));
			return sb.toString();
		}

	}

	public static void test() {
		double x = 20;
		double y = 10;
		double deg = -15;
		double ang = Math.toRadians(deg);
		double cosang = Math.cos(ang);
		double sinang = Math.sin(ang);
		double denom = cosang * cosang + sinang * sinang;
		double xpnum = x * cosang + y * sinang;
		double xp = xpnum / denom;
		double yp = (y - xp * sinang) / cosang;
		println("test, " + x + CS + y + CS + fmt4(xp) + CS + fmt4(yp));
		double check0 = x * x + y * y;
		double check1 = xp * xp + yp * yp;
		println("test, " + fmt4(check0) + CS + fmt4(check1));
	}

	//	@SuppressWarnings("unused")
	//	public static void test2() {
	//		double hyp = 50;
	//		double deg = 30;
	//		double ang = Math.toRadians(deg);
	//		double y = hyp * Math.sin(ang);
	//		double x = hyp * Math.cos(ang);
	//		double [] da = handleRotation_V1(x, y, ang);
	//		double xp = da[0];
	//		double yp = da[1];
	//	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//DivisionCaller dc = new DivisionCaller("ADL", 11.1);
		//dc.readNewRules();
		//println("main, " + dc.iRulesHash.size());
		//		test2();
	}

	private static void println(String s) {System.out.println(s);}
	private static final String CS = ", ", C = ",", TAB = "\t";
	private static final DecimalFormat DF4 = new DecimalFormat("####.####");
	private static String fmt4(double d) {return DF4.format(d);}   
}