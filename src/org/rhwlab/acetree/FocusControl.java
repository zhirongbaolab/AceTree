/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */

package org.rhwlab.acetree;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

/** forces focus to a button to enable keyboard keys to come thru
 * 
 * @author biowolp
 * @version 1.0 January 27, 2005
 */
public class FocusControl extends FocusTraversalPolicy {
    Component iFocusButton;
    Component iTimeField;
    Component iNameField;
    
    /**
     * 
     * @param button the default button to get focus
     * @param timeField in InputCtrl you want to make work normally.
     * @param nameField in InputCtrl you want to have shift tab work with.
     */
    public FocusControl( Component button
                        ,Component timeField 
                        ,Component nameField
                        ) {
        super();
        iFocusButton = button;
        iTimeField = timeField;
        iNameField = nameField;
    }

    /**
     * Implemented function of the interface
     */
    @Override
	public Component getComponentAfter(Container focusCycleRoot,
            Component aComponent) {
        Component c = iFocusButton;
        if (aComponent == iTimeField) c = iNameField;
        return c;
    }

    /**
     * Implemented function of the interface
     */
    @Override
	public Component getComponentBefore(Container focusCycleRoot,
            Component aComponent) {
        Component c = iFocusButton;
        if (aComponent == iNameField) c = iTimeField;
        return c;
    }

    /**
     * Unused function of the interface
     */
    @Override
	public Component getDefaultComponent(Container focusCycleRoot) {
        return iFocusButton;
    }

    /**
     * Unused function of the interface
     */
    @Override
	public Component getFirstComponent(Container focusCycleRoot) {
        return iFocusButton;
    }

    /**
     * Unused function of the interface
     */
    @Override
	public Component getLastComponent(Container focusCycleRoot) {
        return iFocusButton;
    }


    /**
     * Dummy main not used
     * @param args String []
     */
    public static void main(String[] args) {
    }
}
