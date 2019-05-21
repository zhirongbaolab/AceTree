package org.rhwlab.snight;


import application_src.MainApp;
import application_src.application_model.resources.NucleiMgrAdapterResource;
import org.rhwlab.acetree.AceTree;
import org.rhwlab.image.ImageWindow;
import org.rhwlab.image.management.ImageManager;

import java.util.Vector;

import static application_src.application_model.loaders.AceTreeTableLineageDataLoader.setOriginToZero;

/**
 * Class to open WormGUIDES via AceTree
 *
 * Created: Oct. 2, 2015
 * Author: Braden Katzman
 *
 * Revised: December 2018
 */


public class WormGUIDESWindow extends MainApp {
	private NucleiMgrAdapter nucleiMgrAdapter;
	private NucleiMgrAdapterResource nmar;

	private ImageManager imageManager;
	private AceTree aceTree;

	public WormGUIDESWindow(AceTree aceTree) {
		super();
		nucleiMgrAdapter = new NucleiMgrAdapter(aceTree.getNucleiMgr(), aceTree.getConfig());
		nmar = new NucleiMgrAdapterResource(nucleiMgrAdapter);

		this.imageManager = aceTree.getImageManager();
		this.aceTree = aceTree;
	}

	public void initializeWormGUIDES() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Starting WormGUIDES thread...");
				try {
					startProgramatically(new String[0], nmar);
				} catch (IllegalStateException ise) {
					System.out.println("Illegal state exception thrown from starting WormGUIDES programmatically twice on single AceTree thread.");
					System.out.println("Restart AceTree to use WormGUIDES viewer.");
					System.out.println("Error documented 08/2016");
				}
			}
		});
		t.start();
		// set up a change listener on the time property in AceTree and call observe when it's changed
        this.imageManager.getTimeProperty().addListener((observable, oldValue, newValue) -> {
        	// only update if the origin of the change is from AceTree
        	if (newValue.intValue() != timePropertyMainApp.get()) {
				//System.out.println("Updating WG time from AceTree change");
				updateTime(newValue.intValue());
			}

        });

        // set WormGUIDES start time to current image time in AceTree
        externallySetStartTime = this.imageManager.getCurrImageTime();

        timePropertyMainApp.addListener(((observable, oldValue, newValue) -> {
 			//System.out.println("Time property in WormGUIDES has changed and is now: " + newValue.intValue());
            // we need to first rule out changes to the time property that are a result of AceTree's control over WormGUIDES
            // i.e. only those time changes that originate in WormGUIDES should update AceTree
            if (newValue.intValue() == this.imageManager.getCurrImageTime()) {
                return;
            }

        	// we'll route these through some different update styles depending on how the time is being changed in WormGUIDES
			// either by play mode, step forward/backward button, or slider change
        	if (newValue.intValue() == (oldValue.intValue() + 1)) {
        		//System.out.println("Looks like wormguides moved forward an image, so move acetree forward an image");
				aceTree.nextImage();
			} else if (newValue.intValue() == (oldValue.intValue() - 1)) {
        		//System.out.println("Looks like wormguides moved backward an image, so move acetree backward an image");
				aceTree.prevImage();
			} else {
            	// turn off tracking
            	aceTree.setIsTracking(ImageWindow.NONE);
				this.imageManager.setCurrImageTime(newValue.intValue());

				// behavior is modeled off the bottom panel of the main acetree panel where time and cell are inputted
				Vector v = new Vector();
                v.add("InputCtrl1");
                v.add(newValue.toString());
                v.add("");

                aceTree.controlCallback(v);
                aceTree.updateDisplay();
			}
        }));

        seletedEntityLabelMainApp.addListener((observable, oldValue, newValue) -> {
        	if (!seletedEntityLabelMainApp.isEmpty().get() &&
					!aceTree.getCurrentCell().getName().equals(newValue) &&
					!oldValue.equals(newValue)) {
				aceTree.showSelectedCell(newValue);
			}
		});

        // toggle the movie control buttons in the respective apps when either one is in Play mode
        isPlayButtonEnabled.addListener(((observable, oldValue, newValue) -> {
            if (newValue) { // if WormGUIDES is in play mode
                aceTree.getPlayerControl().disableTimeAndPlaneControlButtons();
            } else {
                aceTree.getPlayerControl().enableTimeAndPlaneControlButtons();
            }
        }));
	}

	public void rebuildData() {
		nucleiMgrAdapter.updateCellOccurencesAndPositions();

		// shift the positions to the center
		setOriginToZero(nucleiMgrAdapter, false);
	}

	public void updateData(int time) {
		nucleiMgrAdapter.updateCellOccurencesAndPositions(time, true);
	}

	public void updateData(int startTime, int endTime) {
		nucleiMgrAdapter.updateCellOccurencesAndPositions(startTime, endTime);
	}
}