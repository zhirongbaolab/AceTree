package org.rhwlab.acetree;

import ij.ImagePlus;
import ij.Prefs;
import ij.gui.ImageCanvas;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.JDialog;
import org.rhwlab.snight.Config;
import org.rhwlab.snight.MeasureCSV;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.utils.EUtils;

public class EllipseViewer extends JDialog implements ActionListener {

    private AceTree         iAceTree;
    private NucleiMgr       iNucleiMgr;
    MeasureCSV	iMeasureCSV;
    String		iSeries;
    Config		iConfig;
	String		iImages;
	ImagePlus	iPlus;
	int			iTime;
	int			iCenter;


    double		iZCenter;
	int			iXCenter;
	int			iYCenter;
	double		iMajor;
	double		iMinor;
	double		iAngle;
	double		iTheta;

	public int[] xCoordinates;
	public int[] yCoordinates;
	public int nCoordinates = 0;
	public double xCenter;
	public double  yCenter;
	public double major;
	public double minor;
	public double angle;
	public double theta;
	boolean record;

	public EllipseViewer() {
        super(AceTree.getAceTree(null).getMainFrame(), false);
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        iConfig = iNucleiMgr.getConfig();
        iMeasureCSV = iNucleiMgr.getMeasureCSV();
        String image = iConfig.iTypicalImage;
        File f = new File(image);
        image = f.getParent();
        iImages = new File(image).getParent();


        iTime = Integer.parseInt(iMeasureCSV.get("time"));
        iCenter = (int)Double.parseDouble(iMeasureCSV.get("zc"));
        iXCenter = Integer.parseInt(iMeasureCSV.get("xc"));
        iYCenter = Integer.parseInt(iMeasureCSV.get("yc"));
        iMajor = Integer.parseInt(iMeasureCSV.get("maj"));
        iMinor = Integer.parseInt(iMeasureCSV.get("min"));
        iAngle = Double.parseDouble(iMeasureCSV.get("ang"));

        /* special for series 20090916_nhr57_end3d_3_L2
        iTime = 110;
        iCenter = 14;
        iXCenter = 378;
        iYCenter = 248;
        iMajor = 521;
        iMinor = 329;
        iAngle = 54.8;
		*/



        //iTime = 110;
        //iCenter = 15;

        iZCenter = 14.6875;

        /*
        iXCenter = 384;
        iYCenter = 270;
        iMajor = 529;
        iMinor = 344;
        iAngle = 38.56;
        */
        xCenter = iXCenter;
        yCenter = iYCenter;
        major = iMajor;
        minor = iMinor;
        angle = iAngle;
        theta = 2 * Math.PI * angle / 360;


        println("EllipseViewer, " + iImages);
        getImage();


		ImageProcessor imgproc = iPlus.getProcessor();
		makeRoi(imgproc); // this is where the ellipse parms are used to find the coordinates
		PolygonRoi proi = new PolygonRoi(xCoordinates, yCoordinates, nCoordinates, Roi.POLYGON);
		//Roi testRoi = new Roi(200, 200, 200, 200);
		//Roi.setColor(Color.WHITE);
		iPlus.setRoi(proi);



        showImage();
        /*
        JFrame owner = iAceTree.getMainFrame();
        JDialog dialog = this;
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        p.setPreferredSize(new Dimension(200,600));
        p.setOpaque(true); //content panes must be opaque
        dialog.setContentPane(p);
        dialog.pack();

        //dialog.setSize(new Dimension(200, 300));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
        dialog.setModal(false);
		*/

	}


	@SuppressWarnings("unused")
	int getImage() {
		int r = 0;
		String s = iImages + "/tif/";
		//println("getImage, " + s);
		String [] d = new File(s).list();
		if (d == null || d.length < 100) {
			println("Failure, EllipseFit could not open image for, " + iSeries);
			return 1;
		}
		String ss = d[0];
		ss = ss.substring(0, ss.length() - 13);
		String s2 = EUtils.makePaddedInt(iTime);
		ss += "-t" + s2;
		s2 = EUtils.makePaddedInt(iCenter, 2);
		ss += "-p" + s2 + ".tif";
		ss = s + ss;
		Prefs.open100Percent = true;
		ImagePlus iplus = new ImagePlus(ss);
		iPlus = iplus;
		if (iPlus.getProcessor() == null) {
			println("Failure, EllipseFit, bogus ImagePlus could not open image for, " + iSeries);
			return 1;

		}

		//ImageProcessor imgproc = iPlus.getProcessor();
		//Roi testRoi = new Roi(100, 100, 200, 200);
		//Roi.setColor(Color.WHITE);
		//iPlus.setRoi(testRoi);
		showImage();

		return 0;

	}

	@SuppressWarnings("unused")
	void showImage() {
		ImageProcessor iproc = iPlus.getProcessor();
		//makeRoi(iproc);
		//drawEllipse(iproc);
		ImageCanvas icanvas = new ImageCanvas(iPlus);
		//icanvas.setMagnification(1.0);
		//icanvas.zoomIn(10, 10);
		ij.gui.ImageWindow iwin = new ij.gui.ImageWindow(iPlus, icanvas);
		iwin.setSize(new Dimension(iPlus.getWidth(), iPlus.getHeight()));
	}



	/** Draws the ellipse on the specified image. */
	@SuppressWarnings("unused")
	public void drawEllipse(ImageProcessor ip) {
		if (major==0.0 && minor==0.0)
			return;
		int xc = (int)Math.round(xCenter);
		int yc = (int)Math.round(yCenter);
		int maxY = ip.getHeight();
		int xmin, xmax;
		double sint, cost, rmajor2, rminor2, g11, g12, g22, k1, k2, k3;
		int x, xsave, ymin, ymax;
		int[] txmin = new int[maxY];
		int[] txmax = new int[maxY];
		double j1, j2, yr;

		sint = Math.sin(theta);
		cost = Math.cos(theta);
		rmajor2 = 1.0 / sqr(major/2);
		rminor2 = 1.0 / sqr(minor/2);
		g11 = rmajor2 * sqr(cost) + rminor2 * sqr(sint);
		g12 = (rmajor2 - rminor2) * sint * cost;
		g22 = rmajor2 * sqr(sint) + rminor2 * sqr(cost);
		k1 = -g12 / g11;
		k2 = (sqr(g12) - g11 * g22) / sqr(g11);
		k3 = 1.0 / g11;
		ymax = (int)Math.floor(Math.sqrt(Math.abs(k3 / k2)));
		if (ymax>maxY)
			ymax = maxY;
		if (ymax<1)
			ymax = 1;
		ymin = -ymax;
 		// Precalculation and use of symmetry speed things up
		for (int y=0; y<=ymax; y++) {
			//GetMinMax(y, aMinMax);
			j2 = Math.sqrt(k2 * sqr(y) + k3);
			j1 = k1 * y;
			txmin[y] = (int)Math.round(j1 - j2);
			txmax[y] = (int)Math.round(j1 + j2);
		}
		if (record) {
			xCoordinates[nCoordinates] = xc + txmin[ymax - 1];
			yCoordinates[nCoordinates] = yc + ymin;
			nCoordinates++;
		} else
			ip.moveTo(xc + txmin[ymax - 1], yc + ymin);
		for (int y=ymin; y<ymax; y++) {
			x = y<0?txmax[-y]:-txmin[y];
			if (record) {
				xCoordinates[nCoordinates] = xc + x;
				yCoordinates[nCoordinates] = yc + y;
				nCoordinates++;
			} else
				ip.lineTo(xc + x, yc + y);
		}
		for (int y=ymax; y>ymin; y--) {
			x = y<0?txmin[-y]:-txmax[y];
			if (record) {
				xCoordinates[nCoordinates] = xc + x;
				yCoordinates[nCoordinates] = yc + y;
				nCoordinates++;
			} else
				ip.lineTo(xc + x, yc + y);
		}
	}

	public void makeRoi(ImageProcessor ip) {
		record = true;
		int size = ip.getHeight()*3;
		size = Math.max(1500, size);
		xCoordinates = new int[size];
		yCoordinates = new int[size];
		nCoordinates = 0;
		drawEllipse(ip);
		//println("makeRoi,");
		record = false;
	}


	private double sqr(double x) {
		return x*x;
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		println("EllipseViewer.main, ");
		new EllipseViewer();

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
