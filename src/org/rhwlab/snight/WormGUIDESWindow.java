package org.rhwlab.snight;


/**
 * Class to open WormGUIDES via AceTree
 * 
 * Created: Oct. 2, 2015
 * Author: Braden Katzman
 */

import wormguides.MainApp;
import wormguides.model.NucleiMgrAdapterResource;

public class WormGUIDESWindow extends wormguides.MainApp {
	private NucleiMgrAdapter nucleiMgrAdapter;
	private NucleiMgrAdapterResource nmar;
	
	public WormGUIDESWindow(NucleiMgr nucleiMgr) {
		super();
		nucleiMgrAdapter = new NucleiMgrAdapter(nucleiMgr);
		nmar = new NucleiMgrAdapterResource(nucleiMgrAdapter);
	}
	
	public void initializeWormGUIDES() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Starting WormGUIDES thread...");
				try {
					MainApp.startProgramatically(new String[0], nmar);
				} catch (IllegalStateException ise) {
					System.out.println("Illegal state exception thrown from starting WormGUIDES programatically.");
					System.out.println("Restart AceTree to use WormGUIDES viewer.");
				}
			}
		});
		t.start();
	}
}