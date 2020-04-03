package org.rhwlab.snight;

import java.util.Vector;
import org.rhwlab.utils.C;


public class InitialID {

	NucleiMgr		iNucleiMgr;
	Vector<Vector<Nucleus>> nuclei_record;
	Parameters		iParameters;
	int				iNucCount;
	int				iEndingIndex;
	int		        iDivisor;
	int		        iMinCutoff;
	String			iAxis;
	MeasureCSV		iMeasureCSV;

	double			iAng;
	int				iXC;
	int				iYC;
	int				iX; //transient
	int				iY;
	
	private CanonicalTransform canTrans;

	public InitialID(NucleiMgr nucMgr, Parameters parameters, MeasureCSV measureCSV, CanonicalTransform canTrans) {
		iNucleiMgr = nucMgr;
		nuclei_record = iNucleiMgr.getNucleiRecord();
		iParameters = parameters;
		iNucCount = 1;

		// distinguish between legacy conguration and revised 10/18
		if (nucMgr.isNucConfigNull()) {
			iEndingIndex = iNucleiMgr.iEndingIndex;
			iMeasureCSV = measureCSV;
		} else {
			this.iEndingIndex = nucMgr.getNucConfig().getEndingIndex();
			this.iMeasureCSV = nucMgr.getNucConfig().getMeasureCSV();
		}

		this.canTrans = canTrans;
		getCoordinateParms();

//		this.ang_calculated = false;

		println("InitialID measureCSV: ");
		println("" + iMeasureCSV);

	}

	void getCoordinateParms() {
		String sang = "";
		if (MeasureCSV.isAuxInfoV2()) {
			sang = iMeasureCSV.iMeasureHash_v1.get("ang");
		} else {
			sang = iMeasureCSV.iMeasureHash.get("ang");
		}


		if (sang != null && sang.length() > 0) {
			iAng = Math.toRadians(-Double.parseDouble(sang));
		} else {
			iAng = Math.toRadians(-Double.parseDouble(MeasureCSV.defaultAtt_v1[MeasureCSV.EANG_v1]));
		}


		String x = iMeasureCSV.iMeasureHash.get("xc");
		if (x != null && x.length() > 0) {
			iXC = Integer.parseInt(x);
		} else {
			if (MeasureCSV.isAuxInfoV2()) {
				iXC = Integer.parseInt(MeasureCSV.defaultAtt_v2[MeasureCSV.EXCENTER_v2]);
			} else {
				iXC = Integer.parseInt(MeasureCSV.defaultAtt_v1[MeasureCSV.EXCENTER_v1]);
			}
		}

		String y = iMeasureCSV.iMeasureHash.get("yc");
		if (y != null && y.length() > 0) {
			iYC = Integer.parseInt(x);
		} else {
			if (MeasureCSV.isAuxInfoV2()) {
				iYC = Integer.parseInt(MeasureCSV.defaultAtt_v2[MeasureCSV.EYCENTER_v2]);
			} else {
				iYC = Integer.parseInt(MeasureCSV.defaultAtt_v1[MeasureCSV.EYCENTER_v1]);
			}
		}
	}

	int getNucCount() {
		return iNucCount;
	}

	///////// code connected to initialID determination below here
	// note modifications made on 20050804 to prevent a nucleus
	// named polar from being renamed Nuc-xxx
	// in three places
	public int initialID(int [] start_p, int [] lineage_ct_p) {
		println("initialID called: " + start_p[0] + CS + lineage_ct_p[0]);
		int startingIndex = start_p[0];
		int rtn = 0;
		int lin_ct = lineage_ct_p[0];
		int first_four = -1, last_four = -1, four_cells;
		Vector<Nucleus> nuclei = nuclei_record.elementAt(startingIndex - 1);
		//Vector nuclei = (Vector)nuclei_record.elementAt(0);
		int nuc_ct = nuclei.size();
		int cell_ct = countCells(nuclei);
		if (cell_ct <= 6) {
			System.out.println("=< 6 cells");
			polarBodies();
			cell_ct = countCells(nuclei);
		}
		if (cell_ct > 4) {
			System.out.println("> 4 cells");
			Nucleus nucleij = null;
			for (int j=0; j < nuc_ct; j++) {
				nucleij = nuclei.elementAt(j);
				if (nucleij.status == -1)
					continue;
				//lin_ct++;
				if (nucleij.identity.indexOf(POLAR) > -1)
					continue; //modification 20050804
				nucleij.identity = NUC + iNucCount++;
			}
			iParameters.axis = 0;
			start_p[0] = 0;
			lineage_ct_p[0] = lin_ct;
			System.out.println("Starting with more than 4 cells.  No canonical ID assigned.");
			return 0;
		} else {
			System.out.println("=< 4 cells in initialID() - InitialID");
			iParameters.axis = 1;
			if (cell_ct == 4)
				first_four = 0;

			//System.out.println("looking for 4 cell stage");
			for (int i=startingIndex - 1; i < iEndingIndex - 1; i++) {
			    //System.out.println("i: " + i);
				nuclei = nuclei_record.elementAt(i);
				nuc_ct = nuclei.size();
				cell_ct = countCells(nuclei);
				//System.out.println("cell count: " + cell_ct);
				if (cell_ct > 4)
					break;
				if (cell_ct == 4) {
					if (first_four < 0) {
						first_four = i;
					}
					last_four = i;
				}
			}

			if (first_four == -1) {
				//nuclei = (Vector)nuclei_record.elementAt(0);
				nuclei = nuclei_record.elementAt(3);
				nuc_ct = nuclei.size();
				Nucleus nucleij = null;
				for (int j=0; j < nuc_ct; j++) {
					nucleij = nuclei.elementAt(j);
					if (nucleij.status == -1)
						continue;
					if (nucleij.identity.indexOf(POLAR) > -1)
						continue;  //modification 20050804
					lin_ct++;
					nucleij.identity = NUC + iNucCount++;
				}
				iParameters.axis = 0;
				start_p[0] = 0;
				//start_p[0] = 4;
				lineage_ct_p[0] = lin_ct;
				System.out.println("Movie too short to see four cells");
				//new Throwable().printStackTrace();
				return 0;
			}
		}

		four_cells = (first_four + last_four)/2;
		start_p[0] = four_cells + 1;


		rtn = fourCellID(four_cells, lineage_ct_p);
		if (rtn != 0) {
			rtn = backAssignment(four_cells, lineage_ct_p);
		} else {
			iParameters.axis = 0;
			return 1;
		}
		return 0;
	}

	/**
	 * Called from initialID() ^^^
	 *
	 * @param four_cells
	 * @param lineage_ct_p
	 * @return
	 */
	private int fourCellID(int four_cells, int [] lineage_ct_p) {
		println("determining fourCellID() in InitialID at time: " + four_cells);
		Vector<Nucleus> nuclei = null, nuclei_next = null;
		Nucleus nucleii = null;
		int nuc_ct;
		//        int ind1, ind2;
		int i;
		int lin_ct = lineage_ct_p[0];

		// access the four cells
		nuclei = nuclei_record.elementAt(four_cells);
		nuc_ct = nuclei.size();

		// attempt to align this diamond with canonical orientation
		int r = alignDiamond(nuclei);

		// return on failure
		if (r == 0) return 0;

		// assign IDs to cells given time point of four cell stage
		r = fourCellIDAssignment(four_cells);

		// return on failure
		if (r == 0) return 0;

		// if the four_cells time point is valid, access the next time point's cells in the record
		if (four_cells < iEndingIndex) {
			nuclei_next = nuclei_record.elementAt(four_cells+1);
		}

		// iterate over the four cell stage
		for (i=0; i<nuc_ct; i++) {
			nucleii = nuclei.elementAt(i);

			// continue on polar bodies
			if (nucleii.identity.indexOf(POLAR) > -1) continue;

			// 20050809 changed sense of the next line -- should now match SN
			if (nucleii.predecessor == Nucleus.NILLI) lin_ct ++;

			if (nucleii.successor2 != Nucleus.NILLI) {
				Nucleus d1 = nuclei_next.elementAt(nucleii.successor1 - 1);
				Nucleus d2 = nuclei_next.elementAt(nucleii.successor2 - 1);
				sisterID(d1, d2, nuc_ct);
			}
		}
		lineage_ct_p[0] = lin_ct;
		return 1;
	}


	/**
	 * Given the 4 cell stage nuclei, apply transformations to rotate the diamond to canonical orientation
	 * called from fourCellID() ^^^
	 *
	 * @param nuclei - the 4 cell stage nuclei
	 * @return 1 on success, 0 on failure
	 */
	private int alignDiamond(Vector<Nucleus> nuclei) {
		//println("alignDiamond in InitialID");
		int rtn = 1;
		int xmin, xmax, ymin, ymax;
		Nucleus north=null, south=null, west=null, east=null;
		int i;

		xmin = Integer.MAX_VALUE; //Movie.framewidth;
		xmax = 0;
		ymin = Integer.MAX_VALUE; //Movie.frameheight * Movie.framewidth;
		ymax = 0;

		/* -------------- AUX INFO V2 STUFF ------------- */
		/*
		 * this will be used as reference to set the 4 cell diamond under the AuxInfo_v2 scheme
		 * We want to wait until we have all of the coordinates to set the positions so on each
		 * transformation, we'll store:
		 * - index in nuclei Vector --> by default of the size
		 * - transformed x coordinate
		 * - transformed y coordinate
		 */

        int[][] nuc_coords = new int[nuclei.size()][2];

		/* -------------- AUX INFO V2 STUFF ------------- */

		for (i=0; i<nuclei.size(); i++) {
			Nucleus nucleii = nuclei.elementAt(i);
			if (nucleii.status < 0 || nucleii.identity.indexOf(POLAR) > -1) {
			    /* -------------- AUX INFO V2 STUFF ------------- */
				nuc_coords[i][0] = 0;
				nuc_coords[i][1] = 0;
				/* -------------- AUX INFO V2 STUFF ------------- */
				continue;
			}
			int [] ia = new int[3];
			ia[0] = nucleii.x;
			ia[1] = nucleii.y;
			ia[2] = (int) nucleii.z;
			//println("alignDiamond, BEFORE, " + ia[0] + CS + ia[1] + CS + ia[2] + CS + xmin + CS + xmax + CS + ymin + CS + ymax);
			applyTransformation(ia);
			//println("alignDiamond, AFTER , " + ia[0] + CS + ia[1] + CS + ia[2]);

			/*
			 * if AuxInfo_v2 is present, add the cell to the local list and then we'll decide the directions after
			 */
			if (MeasureCSV.isAuxInfoV2()) {
				nuc_coords[i][0] = ia[0];
				nuc_coords[i][1] = ia[1];
//				System.out.println(ia[0] + ", " + ia[1]);
			} else {
				// depending on the orientation of the current nucleus in space, set it as N, E, S, W
				if (ia[0] < xmin) {
					//println("Setting West Nuc");
					xmin = ia[0];
					west = nucleii;
				}
				if (ia[0] > xmax) {
					//println("Setting East Nuc");
					xmax = ia[0];
					east = nucleii;
				}
				if (ia[1] < ymin) {
					//println("Setting North Nuc");
					ymin = ia[1];
					north = nucleii;
				}
				if (ia[1] > ymax) {
					//println("Setting South Nuc");
					ymax = ia[1];
					south = nucleii;
				}
			}
		}


		// if in AuxInfo_v2 mode, assign the diamond now
		if (MeasureCSV.isAuxInfoV2()) {
			boolean[] assigned = new boolean[nuclei.size()];
			for (int j = 0; j < assigned.length; j++) { assigned[j]=false; }

			xmin = Integer.MAX_VALUE; // east
			xmax = Integer.MIN_VALUE; // west
			ymin = Integer.MAX_VALUE; // north
			ymax = Integer.MIN_VALUE; // south

			// first set east and west because that division axis is greater than north south
			for (i = 0; i < nuclei.size(); i++) {
				if (nuclei.get(i).status < 0 || nuclei.get(i).identity.indexOf(POLAR) > -1) { continue; }

				if (nuc_coords[i][0] < xmin) {
					xmin = nuc_coords[i][0];
				}

				if (nuc_coords[i][0] > xmax) {
					xmax = nuc_coords[i][0];
				}
			}

//			System.out.println("Xmin, xmax = " + xmin + ", " + xmax);

			// figure out which was the east and west
			for (i = 0; i < nuclei.size(); i++) {
				if (nuclei.get(i).status < 0 || nuclei.get(i).identity.indexOf(POLAR) > -1) { continue; }

				if (xmin == nuc_coords[i][0]) {
//					System.out.println("east is: " + (i+1) + " with xmin = " + xmin);
					east = nuclei.get(i);
					assigned[i] = true;
				}

				if (xmax == nuc_coords[i][0]) {
//					System.out.println("west is: " + (i+1) + " with xmax = " + xmax);
					west = nuclei.get(i);
					assigned[i] = true;
				}
			}

			// now set north and south
			for (i = 0; i < nuclei.size(); i++) {
				if (nuclei.get(i).status < 0 || nuclei.get(i).identity.indexOf(POLAR) > -1) { continue; }

				if (!assigned[i]) {
					if (nuc_coords[i][1] < ymin) {
						ymin = nuc_coords[i][1];
					}

					if (nuc_coords[i][1] > ymax) {
						ymax = nuc_coords[i][1];
					}
				}
			}

//			System.out.println("Ymin, ymax = " + ymin + ", " + ymax);

			// figure out which is north and with is south
			for (i = 0; i < nuclei.size(); i++) {
				if (nuclei.get(i).status < 0 || nuclei.get(i).identity.indexOf(POLAR) > -1) { continue; }

				if (!assigned[i]) {
					if (ymin == nuc_coords[i][1]) {
//						System.out.println("south is: " + (i+1) + " with ymin = " + ymin);
						south = nuclei.get(i);
						assigned[i] = true;
					}

					if (ymax == nuc_coords[i][1]) {
//						System.out.println("north is: " + (i+1) + " with ymax = " + ymax);
						north = nuclei.get(i);
						assigned[i] = true;
					}
				}
			}

		}

		// if all four cells weren't properly assigned, return failure
		if (north == null || south == null || west == null || east == null) {
			System.out.println("No diamond four cell stage at time:1 " + iParameters.t);
			return 0;
		}

		//		// if there are nuclei that were tagged as being in two directions, return failure
		if (north==south || north==west || north==east || south==west || south==east || west==east) {
			System.out.println("No diamond four cell stage at time:2 " + iParameters.t);
			return 0;
		}

//		System.out.println("Assigning IDs");
		// set the ID tags for later access of these nuclei
		north.id_tag = N;
		south.id_tag = S;
		east.id_tag = E;
		west.id_tag = W;

		// if reached, this returns 1
		return rtn;
	}

	/**
	 * Used to align the 4 cell stage diamond with the canonical orientation
	 * called by alignDiamond() ^^^
	 *
	 * AuxInfo v1.0 uses the angle field to rotate the AP axis (always in XY plane in compressed embryo) to the canonical AP
	 * AuxInfo v2.0 takes the given AP axis and projects it onto the xy plane, then uses the angle between the projection and canonical AP
	 *
	 * source: https://www.maplesoft.com/support/help/Maple/view.aspx?path=MathApps/ProjectionOfVectorOntoPlane
	 *
	 * @param ia
	 */
	private void applyTransformation(int [] ia) {
		double[] da = {0., 0., 0.};
		if (MeasureCSV.isAuxInfoV2()) {
			if (canTrans == null) return;

			double[] nuc_coords = new double[3];
			nuc_coords[0] = (double) ia[0];
			nuc_coords[1] = (double) ia[1];
			nuc_coords[2] = (double) ia[2];
			boolean success = canTrans.applyProductTransform(nuc_coords);
			if (!success) {
				System.out.println("Failed to rotate with CanonicalTransform");
				return;
			}

			ia[0] = (int)nuc_coords[0];
			ia[1] = (int)nuc_coords[1];
			ia[2] = (int)nuc_coords[2];
		} else {
			int x = ia[0] - iXC;
			int y = ia[1] - iYC;
			da = DivisionCaller.handleRotation_V1(x, y, iAng);
			ia[0] = iXC + (int)Math.round(da[0]);
			ia[1] = iYC + (int)Math.round(da[1]);
		}
	}

	private int countCells(Vector<Nucleus> nuclei) {
		int cell_ct = 0;
		Nucleus n;
		for (int i=0; i < nuclei.size(); i++) {
			n = nuclei.elementAt(i);
			//FIXME HAD TO SWAP THIS NEW if STATEMENT WITH OLD (commented out) STATEMENT TO GET NAMING TO WORK
			//if (n.status > -1 && !n.identity.equals(POLAR)) cell_ct++;
			if (n.status > -1 && n.identity.indexOf(POLAR) == -1) cell_ct++;
		}
		return cell_ct;
	}

	@SuppressWarnings("unused")
	private void polarBodies() {
		Vector<Nucleus> nuclei = nuclei_record.elementAt(0);
		Vector<Nucleus> nuclei_next = null;
		int nuc_ct = nuclei.size();
		int i, t;
		int p_ct = 0;
		Nucleus nucleii;
		for (i = 0; i < nuc_ct; i++) {
			nucleii = nuclei.elementAt(i);
			if (nucleii.status < 0) continue;
			if (nucleii.size < iParameters.polar_size) {
				nucleii.identity = POLAR + (p_ct + 1);
				p_ct++;
			}
		}
		if (p_ct == 0) return;

		for(i = 0; i < iEndingIndex; i++) {
			nuclei = nuclei_record.elementAt(i);
			nuc_ct = nuclei.size();
			try {
				nuclei_next = nuclei_record.elementAt(i + 1);
			} catch(ArrayIndexOutOfBoundsException oob) {
				break;
			}
			Nucleus nucleij = null;
			for (int j = 0; j < nuc_ct; j++) {
				nucleij = nuclei.elementAt(j);
				if (nucleij.identity.indexOf(POLAR) == -1) continue;
				if (nucleij.successor1 == Nucleus.NILLI) p_ct--;
				if (p_ct == 0) break;
				if (nucleij.successor2 != Nucleus.NILLI) {
					System.out.println("Polar body divided: "
							+ i + 1 + ":" + j + 1 + "->"
							+ i + 2 + ":" + nucleij.successor1 + " and "
							+ i + 2 + ":" + nucleij.successor2
							);
				} else {
					if (nucleij.successor1 == -1) continue;
					Nucleus suc = nuclei_next.elementAt(nucleij.successor1 - 1);
					suc.identity = nucleij.identity;
				}
			}
		}

	}

	/**
	 * 
	 * @param four_cells
	 * @param lineage_ct_p
	 * @return
	 */
	private int backAssignment(int four_cells, int [] lineage_ct_p) {
		System.out.println("backAssignment: " + four_cells);
		int i, j;
		Vector<Nucleus> nuclei = null, nuclei_next = null, nuclei_prev = null;
		Nucleus nucleij = null;// nucleijn = null;
		Nucleus suc1 = null, suc2 = null, pred = null;
		int nuc_ct;
		int lin_ct = lineage_ct_p[0];
		int successor1 = Nucleus.NILLI;
		int successor2 = Nucleus.NILLI;
		int badExit = 0;

		for (i=four_cells-1; i>=0; i--) {
			//println("backAssignment: " + i);
			nuclei = nuclei_record.elementAt(i);
			nuc_ct = nuclei.size();
			nuclei_next = nuclei_record.elementAt(i + 1);
			successor1 = Nucleus.NILLI;
			successor2 = Nucleus.NILLI;

			// 20050809 key bug fix here to handle the case where
			// both successors were null implying a NUC cell name
			// in a backAssignment
			for (j = 0; j < nuc_ct; j++) {
				suc1 = null;
				suc2 = null;
				nucleij = nuclei.elementAt(j);
				if (nucleij.identity.indexOf(POLAR) > -1) continue;
				if (nucleij.status == Nucleus.NILLI) continue;
				successor1 = nucleij.successor1;
				successor2 = nucleij.successor2;
				if (successor1 != Nucleus.NILLI) {
					suc1 = nuclei_next.elementAt(successor1 - 1);
				}

				if (successor2 == Nucleus.NILLI) {
					if (suc1 != null)
						nucleij.identity = suc1.identity;
					else
						nucleij.identity = NUC + iNucCount++;
				}
				else {
					suc2 = nuclei_next.elementAt(successor2 - 1);
					String s1 = suc1.identity;
					String s2 = suc2.identity;
					if (s1.equals("P2") || s1.equals("EMS")) {
						if (s2.equals("P2") || s2.equals("EMS")) {
							nucleij.identity = "P1";
						} else {
							System.out.println("bad sister names: " + s1 + ", " + s2);
							badExit = 1;
							break;
						}
					} else if ( s1.equals("ABa") || s1.equals("ABp")) {
						if (s2.equals("ABa") || s2.equals("ABp")) {
							nucleij.identity = "AB";
						} else {
							System.out.println("bad sister names: " + s1 + ", " + s2);
							badExit = 1;
							break;
						}
					} else if ( s1.equals("AB") || s1.equals("P1")) {
						if (s2.equals("AB") || s2.equals("P1")) {
							nucleij.identity = "P0";
						} else {
							System.out.println("bad sister names: " + s1 + ", " + s2);
							badExit = 1;
							break;
						}
					} else {
						System.out.println("bad sister names: " + s1 + ", " + s2);
						badExit = 1;
						break;
					}
				}
			}
			// test here for failure
			if (badExit > 0) {
				System.out.println("backtrace failure: " + i + C.CS + j + C.CS + nuc_ct);
				return 0;
			}
		}

		// process the first set of data only
		nuclei = nuclei_record.elementAt(0);
		nuc_ct = nuclei.size();
		for (j=0; j < nuc_ct; j++) {
			nucleij = nuclei.elementAt(j);
			if (nucleij.identity.indexOf(POLAR) > -1) continue;
			if (nucleij.identity == null) {
				nucleij.identity = NUC + iNucCount++;
			}
		}

		// now process the rest up to four_cells
		for(i = 1; i < four_cells; i++) {
			nuclei = nuclei_record.elementAt(i);
			nuc_ct = nuclei.size();
			nuclei_next = nuclei_record.elementAt(i + 1);
			nuclei_prev = nuclei_record.elementAt(i - 1);
			for (j=0; j<nuc_ct; j++) {
				nucleij = nuclei.elementAt(j);
				if (nucleij.identity.indexOf(POLAR) > -1) continue;
				boolean validId = nucleij.identity != null && !nucleij.identity.equals("");
				if (!validId && nucleij.predecessor == Nucleus.NILLI) {
					lin_ct++;
					nucleij.identity = NUC + iNucCount++;
				} else if (nucleij.identity == null) {
					pred = nuclei_prev.elementAt(nucleij.predecessor - 1);
					successor2 = pred.successor2;
					if (successor2 == Nucleus.NILLI) {
						pred.identity = nucleij.identity;
					} else {
						newBornID(pred,
								nuclei.elementAt(pred.successor1 - 1),
								nuclei.elementAt(successor2 - 1)
								);
					}
				}
				// deliberate change from C code in next line initial reference
				//                if (nucleij.successor2 != Nucleus.NILLI) {
				if (nuclei.elementAt(j).successor2 != Nucleus.NILLI) {
					//iDLog.append("reached deliberate code change");
					sisterID(nuclei_next.elementAt(nucleij.successor1 - 1)
							,nuclei_next.elementAt(nucleij.successor2 - 1)
							, nuc_ct
							);
				}
			}
		}
		lineage_ct_p[0] = lin_ct;
		return 1;
	}


	private int fourCellIDAssignment(int four_cells) {
		println("fourCellIDAssignment() in InitialID");

		// initialize 4 cell stage nuclei, start times and nucleus count
		Vector<Nucleus> nuclei;
		int nuc_ct;
		Nucleus north, south, west, east, ABa, ABp, EMS, P2;
		north=south=west=east=ABa=ABp=EMS=P2=null;
		int ntime, stime, etime, wtime;
		int i;

		// access the 4 cell stage nuclei
		nuclei = nuclei_record.elementAt(four_cells);
		nuc_ct = nuclei.size();

		// iterate over the 4 cells (hopefully 4) and check ID tags to match to N, E, S, W
		for (i=0; i<nuc_ct; i++) {
			Nucleus nucleii = nuclei.elementAt(i);
			if (nucleii.id_tag == N) {
				north = nucleii;
			}
			else if (nucleii.id_tag == S) {
				south = nucleii;
			}
			else if (nucleii.id_tag == E) {
				east = nucleii;
			}
			else if (nucleii.id_tag == W){
				west = nucleii;
			}
		}

		/*
		 * find the division time points for the 4 cell stage nuclei
		 * if a -1 is returned, this method fails
		 */
		ntime = timeToDivide(four_cells, north);
		if (ntime < 0) return 0;
		stime = timeToDivide(four_cells, south);
		if (stime < 0) return 0;
		etime = timeToDivide(four_cells, east);
		if (etime < 0) return 0;
		wtime = timeToDivide(four_cells, west);
		if (wtime < 0) return 0;

		System.out.println("Divisions (N, S, E, W): " + ntime + CS + stime + CS + etime + CS + wtime);

		// set the 4 cells according to division time
		// known that ABp-ABa divide before P2-EMS
		if (wtime < etime) {
			ABa=west;
			P2=east;
			iParameters.ap=1;
		} else if (wtime > etime) {
			ABa=east;
			P2=west;
			iParameters.ap=-1;
			iParameters.apInit = -1;
		} else {
			System.out.println("putative ABa and P2 divide simutaneously.");
			return 0;
		}

		if (ntime < stime) {
			ABp=north;
			EMS=south;
			iParameters.dv=1;
			iParameters.dvInit = 1;
		} else if (ntime > stime) {
			ABp=south;
			EMS=north;
			iParameters.dv=-1;
			iParameters.dvInit = -1;
		} else {
			System.out.println("putative ABp and EMS divide simutaneously.");
			return 0;
		}

		iParameters.lr = iParameters.ap * iParameters.dv;
		iParameters.lrInit = iParameters.lr;

		// set the identities of the 4 cells
		ABa.identity = "ABa";
		ABp.identity = "ABp";
		EMS.identity = "EMS";
		P2.identity = "P2";
		//        String o = iNucleiMgr.getOrientation();
		//NucUtils.setOrientation(o);
		//        System.out.println("axis xyz = " + o + C.CS + iParameters.dvInit + C.CS + iParameters.dv);
		return 1;
	}

	/**
	 * Iterate forward over time points to find the point when the given nucleus divides
	 * 		--> this occurs when both successors are valid cells
	 * 
	 * @param current_time - the time at which the Nucleus is starting
	 * @param nuc - the nucleus that we are interesting in the dividing time for
	 * 
	 * @return the time when the given Nucleus divides
	 */
	private int timeToDivide(int current_time, Nucleus nuc) {
		// only iterate over valid time points
		while (current_time < iEndingIndex) {
			// if the first successor is null, return failure
			if (nuc.successor1 == Nucleus.NILLI) return -1;

			// increment current time on null second successor
			if (nuc.successor2 == Nucleus.NILLI) {
				current_time++;
			} else { // when both are valid, the current_time variable holds the division time point for the supplied Nucleus
				break;
			}

			// access the nucleus (same) at the next time point in the nuclei record
			nuc = (nuclei_record.elementAt(current_time)).elementAt(nuc.successor1 - 1);
		}

		return current_time;
	}


	public int newBornID(Nucleus mother, Nucleus dau1, Nucleus dau2) {
		//println("newBornID: " + mother.identity + CS + dau1.identity + CS + dau2.identity);
		int rtn = 0; // return 0 if newBornID handles the assignment
		//        -1 otherwise
		float diff;
		int difi;
		char tag1 = X;
		char tag2 = X;
		if (mother.identity.indexOf(POLAR) > -1) {
			System.out.println("Dividing polar body");
		} else if (mother.identity.equals("ABa")) {
			diff = (dau1.z - dau2.z) * iParameters.lr;
			if (diff < 0) {tag1 = L; tag2 = R;}
			else {tag1 = R; tag2 = L;}
			//else if (diff > 0) {tag1 = R; tag2 = L;}
			dau1.identity = mother.identity + tag1;
			dau2.identity = mother.identity + tag2;
			return 0;
		} else if (mother.identity.equals("ABp")) {
			diff = (dau1.z - dau2.z) * iParameters.lr;
			if (diff < 0) {tag1 = L; tag2 = R;}
			else {tag1 = R; tag2 = L;}
			//else if (diff > 0) {tag1 = R; tag2 = L;}
			dau1.identity = mother.identity + tag1;
			dau2.identity = mother.identity + tag2;
			return 0;
		} else if (mother.identity.equals("EMS")) {
			int k = relativePosition(dau1, dau2);
			dau1.id_tag = earlyFirstCellTag(k);
			if (dau1.id_tag == 'a') {
				dau1.identity = "MS"; dau2.identity = "E";
				return 0;
			}
			else if (dau1.id_tag == 'p') {
				dau1.identity = "E"; dau2.identity = "MS";
				return 0;
			}
		} else if (mother.identity.equals("P2")) {
			difi = (dau1.y - dau2.y) * iParameters.dv;
			if (difi < -dau1.size/2) {
				tag1 = D; tag2 = V;
			} else if (difi > dau1.size/2) {
				//} else if (difi > dau1.size/2) {
				tag1 = V; tag2 = D;
			}
			if (tag1 == D) {
				dau1.identity = "C"; dau2.identity = "P3";
				return 0;
			} else if (tag1 == V) {
				dau1.identity = "P3"; dau2.identity = "C";
				return 0;
			}
		} else if (mother.identity.equals("P3")) {
			//System.out.println("newBornID handling P3");
			difi = (dau1.y - dau2.y) * iParameters.dv;
			if (difi < -dau1.size/2) {
				tag1  = D; tag2 = V;
			} else if (difi > dau1.size/2) {
				tag1 = V; tag2 = D;
			}
			if (tag1 == D) {
				dau1.identity = "D"; dau2.identity = "P4";
				return 0;
			} else if (tag1 == V) {
				dau1.identity = "P4"; dau2.identity = "D";
				return 0;
			}
			System.out.println("P3 NOT RESOLVED IN newBornID");

		} else if (mother.identity.equals("P4")) {
			int k = relativePosition(dau1, dau2);
			dau1.id_tag = midFirstCellTag(k);
			if (dau1.id_tag == A || dau1.id_tag == L) {
				dau1.identity = "Z3"; dau2.identity = "Z2";
				return 0;
			} else if (dau1.id_tag == P || dau1.id_tag == R) {
				dau1.identity = "Z2"; dau2.identity = "Z3";
				return 0;
			}
		}

		if (tag1 != X) {
			dau1.id_tag = tag1;
			dau2.id_tag = tag2;
			dau1.identity = mother.identity + tag1;
			dau2.identity = mother.identity + tag2;
		} else {
			rtn = -1;
		}
		return rtn;
	}

	private char earlyFirstCellTag(int k) {
		int parameterslr = iParameters.lr;
		int parametersdv = iParameters.dv;

		int ka = Math.abs(k);
		int m = 1;
		switch(ka) {
		case 1:
			m = k * iParameters.ap;
			if (m < 0) return A;
			else return P;
		case 2:
			m = k * parametersdv;
			if (m < 0) return D;
			else return V;
		default:
			m = k * parameterslr;
			if (m < 0) return L;
			else return R;
		}
	}

	/**
	 * return -1 if x is controlling and cd1 is left of cd2
	 * return +1 if x is controlling and cd1 is right of cd2
	 * return -2 if y is controlling and cd1 is above cd2
	 * return +2 if y is controlling and cd1 is below cd2
	 * return -3 if z is controlling and cd1 is in front of cd2
	 * return +3 if z is controlling and cd1 is behind cd2
	 * decide on control using the existing StarryNite rules
	 *
	 */
	private int relativePosition(Nucleus cd1, Nucleus cd2) {
		int cutoff = (cd1.size + cd2.size)/iDivisor;
		cutoff = Math.max(cutoff, iMinCutoff);
		int xdiff = cd1.x - cd2.x;
		int ydiff = cd1.y - cd2.y;
		int zdiff = (int)((cd1.z - cd2.z) * iNucleiMgr.getZPixRes());
		if (Math.abs(xdiff) > cutoff) {
			if (xdiff < 0) return -1;
			else return 1;
		}
		else if (Math.abs(ydiff) > cutoff) {
			if (ydiff < 0) return -2;
			else return 2;
		}
		else if (Math.abs(zdiff) > cutoff) {
			if (zdiff < 0) return -3;
			else return 3;
		} else {
			int maxThing = 1;
			int maxValue = xdiff;
			int testValue = ydiff;
			if (Math.abs(testValue) > Math.abs(maxValue)) {
				maxValue = testValue;
				maxThing = 2;
			}
			testValue = zdiff;
			if (Math.abs(testValue) > Math.abs(maxValue)) {
				maxValue = testValue;
				maxThing = 3;
			}
			if (maxValue < 0) return -maxThing;
			else return maxThing;
		}
	}


	private char midFirstCellTag(int k) {
		//int parameterslr = -Parameters.lr * Parameters.ap;
		//int parametersdv = Parameters.dv * Parameters.ap;
		int parameterslr = iParameters.lr;
		int parametersdv = iParameters.dv;

		int ka = Math.abs(k);
		int m = 1;
		switch(ka) {
		case 1:
			m = k * iParameters.ap;
			if (m < 0) return A;
			else return P;
		case 2:
			m = k * parameterslr;
			if (m < 0) return L;
			//if (m > 0) return L;
			else return R;
		default:
			m = k * parametersdv;
			if (m < 0) return D;
			else return V;
		}
	}

	// this is a dummy version of sisterID so if it is really needed
	// we are toast
	private void sisterID(Nucleus nuc1, Nucleus nuc2, int nuc_ct) {
		nuc1.id_tag = A;
		nuc2.id_tag = P;
		return;
	}


	private static final String
	NUC = "Nuc"
	,POLAR = "polar"
	;

	public static final int
	DIVISOR = 8
	,MINCUTOFF = 5
	,XCENTER = 350
	,YCENTER = 250
	,EARLY = 50
	,MID = 450
	,DEAD = -1
	,DEADZERO = 0
	;

	private static final char
	//IGNORESULSTON = 'i',
	A = 'a'
	,D = 'd'
	,L = 'l'
	,V = 'v'
	,E = 'e'
	,W = 'w'
	,N = 'n'
	,S = 's'
	//,B = 'b' //'d' based on identity study 20050614
	//,T = 't' //'v'
	,P = 'p'
	,R = 'r'
	,X = 'X'    // a dummy tag used in newBornID
	;

	public static final String [] NAMING_METHOD = {
			"NONE"
			,"STANDARD"
			,"MANUAL"
			,"NEWCANONICAL"
	};

	public static final int
	STANDARD = 1
	,MANUAL = 2
	,NEWCANONICAL = 3
	;

	private static void println(String s) {System.out.println(s);}
	private static final String CS = ", ";
}
