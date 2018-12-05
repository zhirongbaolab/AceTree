package org.rhwlab.image;

import ij.ImagePlus;
import ij.gui.ImageCanvas;

import javax.swing.*;
import java.awt.*;

public class MaxIntensityProjectionWindow extends JFrame {

    public MaxIntensityProjectionWindow(String imageTitle, ImagePlus MIP_ip) {
        super("MAX_" + imageTitle); // set the frame title
        System.out.println("Generating Maximum Intensity Projection for: " + imageTitle);

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        MIP_ip.setDisplayRange(0, 50);

        ImageCanvas imageCanvas = new ImageCanvas(MIP_ip);

//        // create components and put them in the frame
        Container c = getContentPane();
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());

        jp.add(imageCanvas);
        c.add(jp);

        // size the frame
        setSize(MIP_ip.getWidth(), MIP_ip.getHeight());

        // bring up the image
        setVisible(true);
    }
}
