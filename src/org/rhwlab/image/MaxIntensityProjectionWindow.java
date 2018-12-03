package org.rhwlab.image;

import ij.ImagePlus;
import ij.gui.ImageCanvas;

import javax.swing.*;
import java.awt.*;

public class MaxIntensityProjectionWindow extends JFrame {

    private ImageCanvas imageCanvas;
    private ImagePlus maximumIntensityProjectionIP;

    public MaxIntensityProjectionWindow(ImagePlus MIP_ip) {
        super("Maximum Intensity Projection");
        setSize(MIP_ip.getWidth(), MIP_ip.getHeight());
        this.imageCanvas = new ImageCanvas(MIP_ip);

        Container c = getContentPane();
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());

        c.add(jp);

        pack();

        setVisible(true);
    }

}
