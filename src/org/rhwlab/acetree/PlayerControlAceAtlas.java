/*
 * Created on Apr 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.acetree;


import javax.swing.JToolBar;
/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlayerControlAceAtlas extends PlayerControl {

  
    public PlayerControlAceAtlas(AceTree aceTree) {
	super(aceTree);
    }

    @Override
	protected void addButtons() {
        iStepBack = makeButton("/images/StepBack16");
	iStepBack.setToolTipText("Step Backwards");
        iToolBar.add(iStepBack);
        iReverse = makeButton("/images/PlayBack16");
	iReverse.setToolTipText("Play Backwards");
        iToolBar.add(iReverse);
        iToolBar.add(new JToolBar.Separator());
        iPause = makeButton("/images/Pause16");
	iPause.setToolTipText("Pause");
        iToolBar.add(iPause);
        iToolBar.add(new JToolBar.Separator());
        iPlay = makeButton("/images/Play16");
	iPlay.setToolTipText("Play");
        iToolBar.add(iPlay);
        iStepForward = makeButton("/images/StepForward16");
	iStepForward.setToolTipText("Step Forward");
        iToolBar.add(iStepForward);
	iToolBar.add(new JToolBar.Separator());


        setEnabledAll(true);
        iPause.setEnabled(false);
    }

    
}
