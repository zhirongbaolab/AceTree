package org.rhwlab.snight;

import java.util.*;

import org.rhwlab.tree.Cell;

import wormguides.model.LineageData;

/*
 * Adapter to interface AceTree 3D Viewing with WormGUIDES
 * 
 * Created: Oct. 2, 2015
 * Author: Braden Katzman
 */

@SuppressWarnings({"rawtypes", "unchecked"})
public class NucleiMgrAdapter implements LineageData {
	
		private NucleiMgr nucleiMgr;
		private ArrayList<ArrayList<Double[]>> allPositions;
		private Hashtable<String, int[]> cellOccurences;
		private int realTimePoints; /* NucleiMgr's ending index is past last time with cells present */
		private boolean isSulston;
		private double[] xyzScale;
		

		public NucleiMgrAdapter(NucleiMgr nucleiMgr) {
			this.nucleiMgr = nucleiMgr;
			this.cellOccurences = new Hashtable<String, int[]>();
			this.realTimePoints = nucleiMgr.iEndingIndex; // initialize to this to avoid errors
			this.allPositions = new ArrayList<ArrayList<Double[]>>();
			preprocessCellOccurrences();
			preprocessCellPositions();
			setIsSulstonModeFlag(nucleiMgr.iAncesTree.sulstonmode);
			this.xyzScale = new double[3];
			this.xyzScale[0] = this.xyzScale[1] = nucleiMgr.iConfig.iXy_res;
			this.xyzScale[2] = nucleiMgr.iConfig.iZ_res;
		}
		
		private void preprocessCellOccurrences() {
			int timePoints = getTotalTimePoints();
			
			/*
			 * First occurences
			 */
			for (int i = 1; i <= timePoints; i++) {
				String[] names = getNames(i);
				
				if (names.length == 0) {
					this.realTimePoints = i;
					break;
				}
				
				for (int j = 0; j < names.length; j++) {
					String name = names[j];
					
					if (!cellOccurences.containsKey(name)) {
						int[] start_end = new int[2];
						start_end[0] = i;
						start_end[0] = 0; /* to avoid null exceptions if no end time point is found */
						
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
					
					if (cellOccurences.containsKey(name)) {
						cellOccurences.get(name)[1] = i;
					} else {
						System.out.println("no start occurence for: " + name);
					}
				}
			}
			
			
		}
		
		private void preprocessCellPositions() {
			for (int i = 0; i < realTimePoints; i++) {
				ArrayList<Double[]> positions_at_time = new ArrayList<Double[]>();
				
				Double[][] positions = getPositions(i, true);

				for (int j = 0; j < positions.length; j++) {
					Double[] coords = positions[j];
					
					positions_at_time.add(coords);
				}
			
				allPositions.add(positions_at_time);
			}
		}
		
		@Override
		public String[] getNames(int time) {
			if (time > 0) {
				ArrayList<String> namesAL = new ArrayList<String>(); //named to distinguish between return String[] array
				
				//access vector of nuclei at given time frame
				Vector v = (Vector) nucleiMgr.nuclei_record.get(time);
//				Vector v = (Vector) nucleiMgr.nuclei_record.get(time - 1);
				
				//copy nuclei identities to ArrayList names AL
				for (int m = 0; m < v.size(); ++m) {
					Nucleus n = (Nucleus) v.get(m);
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
		public Double[][] getPositions(int time) {
			if (allPositions == null) {
				preprocessCellPositions();
			}
			
			ArrayList<Double[]> positions = allPositions.get(time);
			
			Double[][] positions_array = new Double[positions.size()][3];
			
			for (int i = 0; i < positions.size(); i++) {
				positions_array[i] = positions.get(i);
			}
			
			return positions_array;
		}
		
		private Double[][] getPositions(int time, boolean prvte) {
			ArrayList<ArrayList<Double>> positionsAL = new ArrayList<ArrayList<Double>>();
			
			//access vector of nuclei at given time frame
			Vector v = (Vector) nucleiMgr.nuclei_record.get(time);
			
			//copy nuclei positions to ArrayList positionsAL
			for (int m = 0; m < v.size(); ++m) {
				Nucleus n = (Nucleus) v.get(m);
				if (n.status == 1) {
					ArrayList<Double> position = new ArrayList<Double>(Arrays.asList((double)n.x, (double)n.y, (double)n.z));
					positionsAL.add(position);
				}
			}
			
			//convert ArrayList to Integer[][]
			int size = positionsAL.size(); //numbers of rows i.e. nuclei
			
			Double[][] positions = new Double[size][3]; //nuclei x 3 positions coordinates
			for (int i = 0; i < size; ++i) {
				ArrayList<Double> row = positionsAL.get(i);

				positions[i][0] = row.get(0);
				positions[i][1] = row.get(1);
				positions[i][2] = row.get(2);
			}
			
			return positions;
			
		}

		@Override
		public Double[] getDiameters(int time) {
			ArrayList<Double> diametersAL = new ArrayList<Double>();
			
			//access vector of nuclei at given time frame
			Vector v = (Vector) nucleiMgr.nuclei_record.get(time);
//			Vector v = (Vector) nucleiMgr.nuclei_record.get(time - 1);
			
			for (int m = 0; m < v.size(); ++m) {
				Nucleus n = (Nucleus) v.get(m);
				if (n.status == 1) {
					diametersAL.add((double)n.size);
				}
			}
			
			//convert ArrayList to Integer[]
			int size = diametersAL.size();
			Double[] diameters = new Double[size];
			for (int i = 0; i < size; ++i) {
				diameters[i] = diametersAL.get(i);
			}
			
			return diameters;
		}
		
		@Override
		public ArrayList<String> getAllCellNames() {
			Hashtable allCellNamesHash = nucleiMgr.getCellsByName();
			
			/* construct array list from values of hashtable
			 * each hash value is cell, so we will pull iName out
			 */
			ArrayList<Cell> cells = new ArrayList<Cell>(allCellNamesHash.values());
			ArrayList<String> allCellNames = new ArrayList<String>();
			for (int i = 0; i < cells.size(); ++i) {
				allCellNames.add(cells.get(i).getName());
			}
			
			return allCellNames;
		}
		
		@Override
		public int getTotalTimePoints() {
			return this.realTimePoints;
		}
		
		public int getNumberOfCellsAtTime(int time) {
			return ((Vector) nucleiMgr.nuclei_record.get(time)).size();
//			return ((Vector) nucleiMgr.nuclei_record.get(time - 1)).size();
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
		public void shiftAllPositions(int x, int y, int z) {
			for (int i = 0; i < allPositions.size(); i++) {
				ArrayList<Double[]> positions_at_frame = allPositions.get(i);
				
				for (int j = 0; j < positions_at_frame.size(); j++) {
					Double[] coords = positions_at_frame.get(j);
					
					positions_at_frame.set(j, new Double[] { coords[0] - x, coords[1] - y, coords[2] - z });
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