/*
 * Created on Apr 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.acetree;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.JComponent;
/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PlayerControl extends JPanel implements ActionListener, Runnable {

  protected  AceTree iAceTree;
   protected JToolBar iToolBar;
   protected JButton iPlay;
   protected JButton iPause;
   protected JButton iReverse;
   protected JButton iStepForward;
   protected JButton iStepBack;
   JButton iStepUp;
   JButton iStepDown;
   JButton iStepChannel;
   JButton iProperties;
   JButton iContrast;
   JLabel iZoomLabel;
   protected boolean iRunning;
   protected  boolean iForward;
   protected  int iDwell;
  
  protected JButton iZoomPlus;
  protected JButton iZoomEqual;
  protected JButton iZoomMinus;
  
  
  public PlayerControl(AceTree aceTree) {
	  super(new BorderLayout());
	  iAceTree = aceTree;
	  //Create the toolbar.
	  iToolBar = new JToolBar("");
	  addButtons();
	  setPreferredSize(new Dimension(130, 30));   
	  //setMaximumSize(new Dimension(100, 30));
	  add(iToolBar, BorderLayout.PAGE_START);
	  iRunning = false;
	  iForward = true;
	  iDwell = 200;
  }

  protected  void addButtons() {
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
	  //3 additional buttons
	  iStepUp = makeButton("/images/StepUp16");
	  iStepUp.setToolTipText("Slice Up");
	  iToolBar.add(iStepUp);
	  iStepDown = makeButton("/images/StepDown16");
	  iStepDown.setToolTipText("Slice Down");
	  iToolBar.add(iStepDown);
	  iToolBar.add(new JToolBar.Separator());
	  iStepChannel = makeButton("/images/StepChannel16");
	  iStepChannel.setToolTipText("Channel Visibility");
	  iToolBar.add(iStepChannel);
	  iProperties = makeButton("/images/Properties");
	  iProperties.setToolTipText("Annotation Display");
	  iToolBar.add(iProperties);
	  // Add contrast button
	  iContrast = makeButton("/images/contrast");
	  iContrast.setToolTipText("Adjust Contrast");
	  iToolBar.add(iContrast);
	  iToolBar.add(new JToolBar.Separator());
	  
	  iZoomPlus = makeButton("/images/ZoomPlus16");
	  iZoomPlus.setToolTipText("Zoom In");
	  iToolBar.add(iZoomPlus);
	  iZoomEqual =makeButton("/images/ZoomEqual16");
	  iZoomEqual.setToolTipText("Actual Pixels");
	  iToolBar.add(iZoomEqual);
	  iZoomMinus =makeButton("/images/ZoomMinus16");
	  iZoomMinus.setToolTipText("Zoom Out");
	  iToolBar.add(iZoomMinus);


	  iZoomLabel=new JLabel("100%");
	  iToolBar.add(iZoomLabel);
	  iToolBar.add(new JToolBar.Separator());
	  setEnabledAll(true);
	  iPause.setEnabled(false);
  }
  public JButton getiZoomEqual(){
	  return iZoomEqual;
  }
  public JButton getiZoomPlus(){
	  return iZoomPlus;
  }
  public JButton getiZoomMinus(){
	  return iZoomMinus;
  }

  public JLabel getiZoomLabel(){
	  return iZoomLabel;
  }
 
    protected void setEnabledAll(boolean enabled) {
        int i = 0;
        Component c;
        //boolean more = true;
        while(true) {
            c = iToolBar.getComponentAtIndex(i++);
            if (c == null) break;
            c.setEnabled(enabled);
        }
    }

    protected JButton makeButton(String imageName) {
        JButton b = new JButton();
        String imgLoc = imageName + ".gif";
        URL imageURL = PlayerControl.class.getResource(imgLoc);
        b.setIcon(new ImageIcon(imageURL, "x"));
        b.addActionListener(this);
        return b;
    }

    @Override
	public void run() {
        boolean b; // enables run to exit when movie hits the wall
        while (iRunning) {
            if (iForward) 
            	b = iAceTree.nextImage();
            else 
            	b = iAceTree.prevImage();
            if (b) {
                try {
                    Thread.sleep(iDwell);
                } catch(InterruptedException ie) {
                     ie.printStackTrace();
                 }
            } else {
                iRunning = false;
            }
        }
        setEnabledAll(true);
        iPause.setEnabled(false);
    }


    public void stop() {
    	iRunning = false;
    }

    public void pause() {
        iRunning = false;
        setEnabledAll(true);
        iPause.setEnabled(false);

    }

    public void addToToolbar(JComponent element)
    {
	iToolBar.add(element);
    }
    @Override
	public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == iPause) {
            //iEventPusher.stop();
            pause();
        }
        if (o == iPlay) {
            //iEventPusher = new EventPusher(iAceTree, 30);
            //iEventPusher.start(true);
            iRunning = true;
            iForward = true;
            setEnabledAll(false);
            iPause.setEnabled(true);
            new Thread(this, "TEST").start();

        } else if (o == iReverse) {
            //iEventPusher = new EventPusher(iAceTree, 30);
            //iEventPusher.start(false);
            if (iRunning) return;
            iRunning = true;
            iForward = false;
            setEnabledAll(false);
            iPause.setEnabled(true);
            new Thread(this, "TEST").start();

        } else if (o == iStepForward) {
            if (iRunning)
            	return;
            iAceTree.nextImage();
        } else if (o == iStepBack) {
            if (iRunning)
            	return;
            iAceTree.prevImage();
        } else if (o == iStepUp) {
        	iAceTree.imageUp();
        } else if (o == iStepDown) {
        	iAceTree.imageDown();
		} else if (o == iStepChannel) {
		    iAceTree.toggleColor();
		    iAceTree.updateDisplay();
		} else if (o == iProperties) {
		    iAceTree.getImageWindow().launchImageParamsDialog();
		} else if (o == iContrast) {
			iAceTree.getImageWindow().launchContrastTool();
		}
    }

    public static void main(String[] args) { }
    
}
