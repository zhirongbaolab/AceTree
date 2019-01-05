package org.rhwlab.snight;


import application_src.MainApp;
import application_src.application_model.resources.NucleiMgrAdapterResource;
import org.rhwlab.acetree.AceTree;
import org.rhwlab.image.management.ImageManager;

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
            updateTime(newValue.intValue());
        });

        // set WormGUIDES start time to current image time in AceTree
        externallySetStartTime = this.imageManager.getCurrImageTime();

        timePropertyMainApp.addListener(((observable, oldValue, newValue) -> {
            this.imageManager.setCurrImageTime(newValue.intValue());
            aceTree.updateDisplay();
        }));
	}
}