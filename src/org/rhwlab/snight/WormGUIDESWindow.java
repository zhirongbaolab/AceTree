package org.rhwlab.snight;


import application_src.MainApp;
import application_src.Observe;
import application_src.application_model.resources.NucleiMgrAdapterResource;
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

	public WormGUIDESWindow(NucleiMgr nucleiMgr, Config config, ImageManager imageManager) {
		super();
		nucleiMgrAdapter = new NucleiMgrAdapter(nucleiMgr, config);
		nmar = new NucleiMgrAdapterResource(nucleiMgrAdapter);

		this.imageManager = imageManager;
	}

	public void initializeWormGUIDES() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Starting WormGUIDES thread...");
				try {
					MainApp.startProgramatically(new String[0], nmar);
				} catch (IllegalStateException ise) {
					System.out.println("Illegal state exception thrown from starting WormGUIDES programatically twice on single AceTree thread.");
					System.out.println("Restart AceTree to use WormGUIDES viewer.");
					System.out.println("Error documented 08/2016");
				}
			}
		});
		t.start();

		// set up a change listener on the time property and call observe when it's changed
        this.imageManager.getTimeProperty().addListener((observable, oldValue, newValue) -> {
            //System.out.println("New time property value: " + newValue.intValue() + ", old value: " + oldValue.intValue());
            updateTime(newValue.intValue());
        });

        // set WormGUIDES start time to current image time in AceTree
        updateTime(this.imageManager.getCurrImageTime());
	}
}