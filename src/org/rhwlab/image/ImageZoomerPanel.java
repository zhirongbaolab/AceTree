package org.rhwlab.image;
/*
* This example is from javareference.com
* for more information visit,
* http://www.javareference.com
*/

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import org.rhwlab.acetree.PlayerControl;
import javax.swing.*;

/**
 * This class creates a Image Zoomer
 * @author Rahul Sapkal(rahul@javareference.com)
 * A.S. duplicate of frame extending class
 *created in attempt to replace main image with zoomer
 */
public class ImageZoomerPanel extends JPanel
       implements ActionListener
{
    private ImagePanel m_imagePanel;
    private JScrollPane m_srollPane;
    private JPanel m_imageContainer;
    private JLabel m_zoomedInfo;
    private JButton m_zoomInButton;
    private JButton m_zoomOutButton;
    private JButton m_originalButton;
    private Cursor m_zoomCursor;
    ImageWindow	iImgWin;

    /**
     * Constructor
     * @param image
     * @param zoomPercentage
     * @param imageName
     */
    public ImageZoomerPanel(ImageWindow imgWin, Image image, double zoomPercentage, String imageName, PlayerControl playercontrol)
    {
        //super("Image Zoomer [" + imageName + "]");

        iImgWin = imgWin;
        if(image == null)
        {
            add(new JLabel("Image " + imageName + " not Found"));
        }
        else
        {
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

            m_zoomInButton = playercontrol.getiZoomPlus();//new JButton("Zoom In");
            m_zoomInButton.addActionListener(this);

            m_zoomOutButton = playercontrol.getiZoomMinus();//new JButton("Zoom Out");
            m_zoomOutButton.addActionListener(this);

            m_originalButton = playercontrol.getiZoomEqual();//new JButton("Original");
            m_originalButton.addActionListener(this);

            m_zoomedInfo = playercontrol.getiZoomLabel();

            
            //topPanel.add(new JLabel("Zoom Percentage is " +
	    //                    (int)zoomPercentage + "%"));
            //topPanel.add(m_zoomInButton);
            //topPanel.add(m_originalButton);
           // topPanel.add(m_zoomOutButton);
            //topPanel.add(m_zoomedInfo);

            m_imagePanel = new ImagePanel(image, zoomPercentage);
	    // m_imagePanel.addMouseListener(this);
	    m_imagePanel.addKeyListener(iImgWin); //added to make zoom window respond to key events -AS 11/23/11 didnt work

            m_imageContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
            m_imageContainer.setBackground(Color.BLACK);
            m_imageContainer.add(m_imagePanel);

            m_srollPane = new JScrollPane(m_imageContainer);
            m_srollPane.setAutoscrolls(true);
	    setLayout(new BorderLayout());
            this.add(BorderLayout.SOUTH, topPanel);
            this.add(BorderLayout.CENTER, m_srollPane);
            //getContentPane().add(BorderLayout.SOUTH,
            //             new JLabel("Left Click to Zoom In," +
            //             " Right Click to Zoom Out", JLabel.CENTER));

            m_imagePanel.repaint();
        }

	// pack();
        setVisible(true);
    }

    public void updateImage(Image img) {
    	m_imagePanel.updateImage(img);
    	m_imagePanel.repaint();
    }

    /**
     * Action Listener method taking care of
     * actions on the buttons
     */
    @Override
	public void actionPerformed(ActionEvent ae)
    {
        if(ae.getSource().equals(m_zoomInButton))
        {
            m_imagePanel.zoomIn();
            adjustLayout();
        }
        else if(ae.getSource().equals(m_zoomOutButton))
        {
            m_imagePanel.zoomOut();
            adjustLayout();
        }
        else if(ae.getSource().equals(m_originalButton))
        {
            m_imagePanel.originalSize();
            adjustLayout();
        }
    }

    /**
     * This method takes the Zoom Cursor Image
     * and creates the Zoom Custom Cursor which is
     * shown on the Image Panel on mouse over
     *
     * @param zoomcursorImage
     */
    public void setZoomCursorImage(Image zoomcursorImage)
    {
        m_zoomCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                        zoomcursorImage, new Point(0, 0), "ZoomCursor");
    }

    public int transform(int position){
	return ((int)Math.round(position * 100./ m_imagePanel.getZoomedTo()));
    }

    /**
     * This method adjusts the layout after
     * zooming
     *
     */
    private void adjustLayout()
    {
        m_imageContainer.doLayout();
        m_srollPane.doLayout();

        m_zoomedInfo.setText((int)m_imagePanel.getZoomedTo() + "%");
    }

    void getPositionData(MouseEvent e) {
    	int x = e.getX();
    	int y = e.getY();
    	int x2 = (int)Math.round(x * 100./ m_imagePanel.getZoomedTo());
    	int y2 = (int)Math.round(y * 100./ m_imagePanel.getZoomedTo());
    	MouseEvent e2 = new MouseEvent(iImgWin, 0, 0, 0, x2, y2, 0, false, e.getButton());
    	iImgWin.getMouseHandler().mouseClicked(e2);

    }
    
    public JPanel getImage(){
    	return m_imagePanel;
    }

    /**
     * This class is the Image Panel where the image
     * is drawn and scaled.
     *
     * @author Rahul Sapkal(rahul@javareference.com)
     */
    public class ImagePanel extends JPanel
    {
        private double m_zoom = 1.0;
        private double m_zoomPercentage;
        private Image m_image;

        /**
         * Constructor
         *
         * @param image
         * @param zoomPercentage
         */
        public ImagePanel(Image image, double zoomPercentage)
        {
            m_image = image;
            m_zoomPercentage = zoomPercentage / 100;
        }

        public void updateImage(Image img) {
        	m_image = img;
        	//m_imagePanel.repaint();
        }

        /**
         * This method is overriden to draw the image
         * and scale the graphics accordingly
         */
        @Override
		public void paintComponent(Graphics grp)
        {
            Graphics2D g2D = (Graphics2D)grp;

            //set the background color to white
            g2D.setColor(Color.WHITE);
            //fill the rect
            g2D.fillRect(0, 0, getWidth(), getHeight());

            //scale the graphics to get the zoom effect
            g2D.scale(m_zoom, m_zoom);

            //draw the image
            g2D.drawImage(m_image, 0, 0, this);
        }

        /**
         * This method is overriden to return the preferred size
         * which will be the width and height of the image plus
         * the zoomed width width and height.
         * while zooming out the zoomed width and height is negative
         */
        @Override
		public Dimension getPreferredSize()
        {
            return new Dimension((int)(m_image.getWidth(this) +
                                      (m_image.getWidth(this) * (m_zoom - 1))),
                                 (int)(m_image.getHeight(this) +
                                      (m_image.getHeight(this) * (m_zoom -1 ))));
        }

        /**
         * Sets the new zoomed percentage
         * @param zoomPercentage
         */
        public void setZoomPercentage(int zoomPercentage)
        {
            m_zoomPercentage = ((double)zoomPercentage) / 100;
        }

        /**
         * This method set the image to the original size
         * by setting the zoom factor to 1. i.e. 100%
         */
        public void originalSize()
        {
            m_zoom = 1;
        }

        /**
         * This method increments the zoom factor with
         * the zoom percentage, to create the zoom in effect
         */
        public void zoomIn()
        {
            m_zoom += m_zoomPercentage;
        }

        /**
         * This method decrements the zoom factor with the
         * zoom percentage, to create the zoom out effect
         */
        public void zoomOut()
        {
            m_zoom -= m_zoomPercentage;

            if(m_zoom < m_zoomPercentage)
            {
                if(m_zoomPercentage > 1.0)
                {
                    m_zoom = 1.0;
                }
                else
                {
                    zoomIn();
                }
            }
        }

        /**
         * This method returns the currently
         * zoomed percentage
         *
         * @return
         */
        public double getZoomedTo()
        {
            return m_zoom * 100;
        }
    }
    private static void println(String s) {System.out.println(s);}
    private static void print(String s) {System.out.print(s);}
    private static final String CS = ", ", C = ",";
    private static final String TAB = "\t";
    private static final DecimalFormat DF0 = new DecimalFormat("####");
    private static final DecimalFormat DF1 = new DecimalFormat("####.#");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt4(double d) {return DF4.format(d);}
    private static String fmt1(double d) {return DF1.format(d);}
    private static String fmt0(double d) {return DF0.format(d);}

}
