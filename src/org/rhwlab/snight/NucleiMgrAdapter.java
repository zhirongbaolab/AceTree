package org.rhwlab.snight;

import application_src.application_model.data.LineageData;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.rhwlab.image.management.ImageManager;

import java.util.*;
/**
 * Adapter to interface AceTree 3D Viewing with WormGUIDES
 *
 * Created: Oct. 2, 2015
 * Author: Braden Katzman
 */

public class NucleiMgrAdapter implements LineageData {

	private NucleiMgr nucleiMgr;
	private ArrayList<ArrayList<double[]>> allPositions;
	private Hashtable<String, int[]> cellOccurences;
	private int realTimePoints; /* NucleiMgr's ending index is past last time with cells present */
	private boolean isSulston;
	private double[] xyzScale;


	public NucleiMgrAdapter(NucleiMgr nucleiMgr, Config config) {
		this.nucleiMgr = nucleiMgr;
		this.cellOccurences = new Hashtable<>();
		this.realTimePoints = config.getNucleiConfig().getEndingIndex(); // initialize to this to avoid errors
		this.allPositions = new ArrayList<>();
		preprocessCellOccurrences();
		preprocessCellPositions();
		setIsSulstonModeFlag(nucleiMgr.iAncesTree.sulstonmode);
		//System.out.println("NucleiMgrAdapter has isSulstonMode: " + isSulston);
		this.xyzScale = new double[3];
		this.xyzScale[0] = this.xyzScale[1] = config.getNucleiConfig().getXyRes();
		this.xyzScale[2] = config.getNucleiConfig().getZRes();
	}

	private void preprocessCellOccurrences() {
		int timePoints = getNumberOfTimePoints();

		/*
		 * First occurences
		 */
		for (int i = 1; i <= timePoints; i++) {
			String[] names = getNames(i);

			if (names.length == 0) {
				this.realTimePoints = i;
				//System.out.println("Real time points is: " + i);
				break;
			}

			for (int j = 0; j < names.length; j++) {
				String name = names[j];

				if (!cellOccurences.containsKey(name)) {
					int[] start_end = new int[2];
					start_end[0] = i;
					start_end[1] = i; /* to avoid null exceptions if no end time point is found */

					cellOccurences.put(name, start_end);
				}
			}
		}

		/*
		 * Last occurences
		 */
		for (int i = realTimePoints; i > 0; i--) {
			String[] names = getNames(i);

			for (int j = 0; j < names.length; j++) {
				String name = names[j];

				// only update end time if it is greater than the current time
				if (cellOccurences.containsKey(name) && cellOccurences.get(name)[1] < i) {
					cellOccurences.get(name)[1] = i;
				}
			}
		}
	}

	private void preprocessCellPositions() {
		for (int i = 1; i <= realTimePoints; i++) {
			ArrayList<double[]> positions_at_time = new ArrayList<double[]>();

			double[][] positions = getPositions(i, true);

			for (int j = 0; j < positions.length; j++) {
				double[] coords = positions[j];

				positions_at_time.add(coords);
			}

			allPositions.add(positions_at_time);
		}
		//System.out.println("Size of allPositions = " + allPositions.size());
	}

	public void updateCellOccurencesAndPositions() {
		cellOccurences.clear();
		allPositions.clear();

		preprocessCellOccurrences();
		preprocessCellPositions();
	}

	public void updateCellOccurencesAndPositions(int startTime, int endTime) {
		// check that the range is valid

		for (int i = startTime; i <= endTime; i++) {
			updateCellOccurencesAndPositions(i, false);
		}

		preprocessCellOccurrences();
	}

	public void updateCellOccurencesAndPositions(int time, boolean performCellOccurenceUpdate) {
		// check that the time is valid
		ArrayList<double[]> positions_at_time = new ArrayList<>();

		double[][] positions = getPositions(time, true);

		for (int j = 0; j < positions.length; j++) {
			double[] coords = positions[j];

			positions_at_time.add(coords);
		}

		allPositions.set(time, positions_at_time);

		if (performCellOccurenceUpdate) {
			preprocessCellOccurrences();
		}
	}

	@Override
	public String[] getNames(int time) {
		if (time > 0) {
			ArrayList<String> namesAL = new ArrayList<>(); //named to distinguish between return String[] array

			//access vector of nuclei at given time frame
//			Vector<Nucleus> v = nucleiMgr.nuclei_record.get(time);
				Vector<Nucleus> v = nucleiMgr.nuclei_record.get(time - 1);

			//copy nuclei identities to ArrayList names AL
			for (int m = 0; m < v.size(); ++m) {
				Nucleus n = v.get(m);
				if (n.status == 1) {
					namesAL.add(n.identity); //push back identity
				}
			}

			//convert ArrayList to String[]
			int size = namesAL.size();
			String[] names = new String[size];
			for (int i = 0; i < size; ++i) {
				names[i] = namesAL.get(i);
			}

			return names;
		}
		return new String[0];
	}

	@Override
	public double[][] getPositions(int time) {
		if (allPositions == null) {
			preprocessCellPositions();
		}

		//System.out.println("Trying to access position data at time: " + time);
		ArrayList<double[]> positions = allPositions.get(time - 1);

		double[][] positions_array = new double[positions.size()][3];

		for (int i = 0; i < positions.size(); i++) {
			positions_array[i] = positions.get(i);
		}

		return positions_array;
	}

	private double[][] getPositions(int time, boolean prvte) {
		ArrayList<ArrayList<Double>> positionsAL = new ArrayList<ArrayList<Double>>();

		//access vector of nuclei at given time frame
		Vector<Nucleus> v = nucleiMgr.nuclei_record.get(time-1);

		//copy nuclei positions to ArrayList positionsAL
		for (int m = 0; m < v.size(); ++m) {
			Nucleus n = v.get(m);
			if (n.status == 1) {
				ArrayList<Double> position = new ArrayList<Double>(Arrays.asList((double)n.x, (double)n.y, (double)n.z));
				positionsAL.add(position);
			}
		}

		//convert ArrayList to Integer[][]
		int size = positionsAL.size(); //numbers of rows i.e. nuclei

		double[][] positions = new double[size][3]; //nuclei x 3 positions coordinates
		for (int i = 0; i < size; ++i) {
			ArrayList<Double> row = positionsAL.get(i);

			positions[i][0] = row.get(0);
			positions[i][1] = row.get(1);
			positions[i][2] = row.get(2);
		}

		return positions;

	}

	@Override
	public double[] getDiameters(int time) {
		ArrayList<Double> diametersAL = new ArrayList<Double>();

		//access vector of nuclei at given time frame
		Vector<Nucleus> v = nucleiMgr.nuclei_record.get(time - 1);

		for (int m = 0; m < v.size(); ++m) {
			Nucleus n = v.get(m);
			if (n.status == 1) {
				diametersAL.add((double)n.size);
			}
		}

		//convert ArrayList to Integer[]
		int size = diametersAL.size();
		double[] diameters = new double[size];
		for (int i = 0; i < size; ++i) {
			diameters[i] = diametersAL.get(i);
		}

		return diameters;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList<String> getAllCellNames() {
		ArrayList<String> allCellNames = new ArrayList<>();

		/*
		//old code
		for (int i = 1; i <= realTimePoints; i++) {
			String[] namesAti = getNames(i);

			for (String name : namesAti) {
				if (!allCellNames.contains(name)) {
					allCellNames.add(name);
				}
			}
		}
		*/

		//replace the old code for faster launch speed
		for (Object cellname:nucleiMgr.getCellsByName().keySet()){
			String cname = (String)cellname;
			allCellNames.add(cname);
		}

		//sort names list by string length, so that parent always appear in front of children
		Collections.sort(allCellNames, Comparator.comparing(String::length));

		return allCellNames;
	}

	@Override
	public int getNumberOfTimePoints() {
		return this.realTimePoints;
	}

	/*
	 * If slow, optimize with preprocessing of cells and their first and last occurrences
	 *
	 * (non-Javadoc)
	 * @see wormguides.model.LineageData#getFirstOccurrenceOf(java.lang.String)
	 */
	@Override
	public int getFirstOccurrenceOf(String name) {
		int[] start_end = cellOccurences.get(name);

		if (start_end != null) {
			return start_end[0];
		}

		return 0;
	}

	@Override
	public int getLastOccurrenceOf(String name) {
		int[] start_end = cellOccurences.get(name);

		if (start_end != null) {
			return start_end[1];
		}

		return 0;
	}

	@Override
	public boolean isCellName(String name) {
		return getAllCellNames().contains(name);
	}

	@Override
	public void shiftAllPositions(double x, double y, double z) {
		for (int i = 0; i < allPositions.size(); i++) {
			ArrayList<double[]> positions_at_frame = allPositions.get(i);

			for (int j = 0; j < positions_at_frame.size(); j++) {
				double[] coords = positions_at_frame.get(j);

				positions_at_frame.set(j, new double[] { coords[0] - x, coords[1] - y, coords[2] - z });
			}
			allPositions.set(i, positions_at_frame);
		}
	}

	@Override
	public boolean isSulstonMode() {
		return isSulston;
	}

	@Override
	public void setIsSulstonModeFlag(boolean isSulston) {
		this.isSulston = isSulston;
	}

	@Override
	public double[] getXYZScale() {
		return this.xyzScale;
	}
}