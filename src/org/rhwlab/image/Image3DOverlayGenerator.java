/* 
 * Image3DOverlayGenerator.java
 *
 * Class to be run as a separate thread by Image3D.java to create overlay 
 * for Image3DGeometryManager when creating 2D overlay on 3D
 * Geometric viewing window.
 *
 * 5.2.2014
 */

package org.rhwlab.image;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.tree.Cell;

import java.util.Vector;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import ij.ImagePlus;
import java.lang.InterruptedException;
import javax.media.j3d.*;
import javax.vecmath.*;



public class Image3DOverlayGenerator extends Thread {
    private     AceTree                 aceTree;
    private     Image3DViewConfig       viewConfig;
    private     ImageWindow             imageWindow;
    private     Image3DGeometryManager  geoMgr;
    private     NucleiMgr               nucleiMgr;
    private     boolean                 needReload;
    private     Image3D                	parent;

    private     Vector<BufferedImage>   images;
    private     Vector<double[]>        coordinates;
    private     Vector<Float>           alphas;
    private     Vector<Color>           colors;

    private     Shape3D                 overlay;
    private     int                     time;
    private     Cell                    overlayROICell;
    private     int                     sliceCount;
    
    private		int						iUseStack;

    public Image3DOverlayGenerator(Image3D p) {
        parent = p;
        geoMgr = parent.getGeoManager();
        viewConfig = parent.getViewConfig();
        aceTree = parent.getAceTree();
        nucleiMgr = aceTree.getNucleiMgr();
        imageWindow = aceTree.iImgWin;

        sliceCount = nucleiMgr.getPlaneEnd();
        overlay = null;
        
        iUseStack = aceTree.getUseStack();
    }

    // An Image3DOverlayGenerator object is first created in Image3D.java, where it is started immediately.
    // It will start by loading the first possible overlay and then will wait. The thread will be interrupted 
    // when Image3D refreshes the display and an overlay reload is needed, so here it
    // will catch the interrupt, reset the need-reload flag and continue to reload.
    @Override
	public void run() {
        while(true) {
            try {
                // we create the overlay, pass it to the geometry manager
                // and tell it the overlay is ready to add to scene graph
                createOverlay();
                geoMgr.setOverlayReady(true);
                
                synchronized(this) {
                	this.wait(1000);
                }
                
                // reload the image3d screen
                parent.insertContent("null");

                // wait until interrupted next ie by Image3D while refreshing display
                synchronized(this) {
                    this.wait();
                }
                
            } catch (InterruptedException e) {
            	// we've been interrupted, so something may have changed
                // so new overlay not yet ready
            	System.out.println("CreateOverlay() interrupted. Overlay not ready.");
                geoMgr.setOverlayReady(false);
                geoMgr.setOverlay(null);
                
            } catch (NoClassDefFoundError ncdfe) {
            	return;
            }
        }
    }

    // Load the buffered images that will be used to create the overlay
    // Plane ends at 60 in the .xml congig file but there are fewer than 60 .tif files
    public void loadImages() throws InterruptedException {
        images = new Vector<BufferedImage>();
        ImagePlus ip = null;
        
        // get current title of Image 3D window
        String title = viewConfig.getTitle();
        // eg ZD_TX1464_20110719_1_s1-t015 (has timepoint)
        if (iUseStack == 1)
        	title = aceTree.getTifPathWithPrefix();
        System.out.println("Image3DOverlayGenerator.loadImages() title: "+title);

        /*
        if(next) {
            title = getNextTitle(title);
            nextTitle = title;
        }
        */
        
        String imageName = null;
        this.time = aceTree.getImageTime() + aceTree.getTimeInc();

        for(int i = 0; i < getSliceCount() && !Thread.currentThread().isInterrupted(); i++) {
            // for each of the slices available for this timepoint, add -p# to the end of the title
            // and pass to ImageWindow's makeImage() function to get the image with that name
            // then cast to buffered image so we can use with pointArray
        	//System.out.println("Overlay slice: "+i);
        	
        	// Works only for 8-bit images
        	if (iUseStack == 0) {
	            imageName = "tif/" + title + "-p";
	            if(i+1 < 10)
	                imageName += "0";
	            imageName += (i+1) + ".tif";
	            ip = ImageWindow.makeImage(imageName);
        	}
        	else if (iUseStack == 1) {
        		imageName = title;
		    	ip = ImageWindow.makeImage2(imageName, i, iUseStack);
        	}
        	//System.out.println("Image3DOverlayGenerator.loadImages() imageName: "+imageName);
            BufferedImage bf=ip.getBufferedImage();
            AffineTransform tx = AffineTransform.getScaleInstance(1,-1);
            tx.translate(0,-bf.getHeight(null));
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            bf = op.filter(bf, null);
            images.add(bf);
            //System.out.println("added image: " + imageName);
        }

        Thread.currentThread();
		if(Thread.interrupted()) {
            System.out.println("Thread interrupted.");
        	throw new InterruptedException();
        }

        System.out.println("generator finished loading images.");
    }

    /*
    // get the title of the image for the next timepoint, in case you want to load it in advance
    public String getNextTitle(String title) {
        String result = null, next = null;

        Pattern p = Pattern.compile("-t(\\d{3})");
        Matcher m = p.matcher(title);

        if(m.find()) {
            int num = Integer.parseInt(m.group(1));
            next = Integer.toString(++num);
            for(int i = (int) Math.log10(num) + 1; i < 3; i++) {
                next = "0" + next;
            }

            result = m.replaceAll("-t" + next);
        }

        System.out.println("match " + next + "\t" + result);
        return result;
    }
    */

    /**
     * Generate a Shape3D object that draws spheres for each of the pixels pulled from each of the 
         * 2D image slices of the embryo
         *
         * @param pointSize
         *          how wide to draw the pixels
         * @return Shape3D
         *          the node to add to the branch group in the geometric 3D window
         */
        public Shape3D generateOverlay(float pointSize) throws InterruptedException { 
            if(this.coordinates.size() > 0) {
                PointArray p = new PointArray(this.coordinates.size(), GeometryArray.COORDINATES | GeometryArray.COLOR_4);
                //QuadArray p = new QuadArray(this.coordinates.size(), QuadArray.COORDINATES | QuadArray.COLOR_4);
                Shape3D points = new Shape3D(p);
                points.setAppearance(new Appearance());
                points.getAppearance().setPointAttributes(new PointAttributes(pointSize, true));

                for (int i = 0; i < this.coordinates.size(); i++) {
                        p.setCoordinate(i, this.coordinates.get(i));
                        float[] col = this.colors.get(i).getColorComponents(null);

                        if(Thread.currentThread().isInterrupted()) {
							Thread.currentThread();
							if(Thread.interrupted())
                                throw new InterruptedException();
						}


                        if(!viewConfig.useOverlayRedChannel()) col[0] = 0; 
                        if(!viewConfig.useOverlayGreenChannel()) col[1] = 0;
                        if(!viewConfig.useOverlayBlueChannel()) col[2] = 0;

                        float alpha = (float) (1-Math.pow(
                            (Math.sqrt(col[0]*col[0]+col[1]*col[1]+col[2]*col[2])/Math.sqrt(3.0)),
                            2.5));

                        p.setColor(i, new Color4f(col[0],col[1],col[2],1-alpha));
                        //p.setColor(i, new Color4f(col[0],col[1],col[2],1-this.alphas.get(i)));
                }

                System.out.println("Finished converting to Shape3D in generateOverlay().");
                return points;
            }
            return null;
        }

    /**
     * Given a vector of BufferedImages, calculates the xyz and alpha values needed to represent
     * said image in a 3D space; specifically, for the 2D overlay on the 3D geometric window.
     *
     * @param double dx, dy, dz
     *          d? for calculating xyz values
     *          colorScale for finding alpha
     *          subSample says whether you'd like to calculate every pixel or only every n pixels
     * @return None.
     *          modifies instance variables images, colors, and alphas
     *          that are then used in a generateOverlay call
     */
    public void generateOverlayInfo(double dx,double dy,double dz, int subSample, double colorScale) 
        throws InterruptedException {
            this.coordinates = new Vector<double[]>();
            this.colors = new Vector<Color>();
            this.alphas = new Vector<Float>();
            BufferedImage img = null;
            float xf, yf, zf;

            int width = ImageWindow.cImageWidth;
            if(width == 0) width = 800;
            float scale = width/2;
            
            // Do not use DEFAULT_OVERLAY_MAX_Z from Image3DViewConfig
            // reset it to the planeEnd value in .xml config file
            viewConfig.setOverlayMaxZ(nucleiMgr.getPlaneEnd());

            for (int z = 0; z < this.images.size(); z++) {
                if(z <= viewConfig.getOverlayMinZ() || z >= viewConfig.getOverlayMaxZ()) continue;
                img = this.images.get(z);

                for (int x = 0; x < img.getWidth(); x++) {


                    if(x <= viewConfig.getOverlayMinX() || x >= viewConfig.getOverlayMaxX()) continue;

                    for (int y = 0; y < img.getHeight(); y++) {

                        if(y <= viewConfig.getOverlayMinY() || y >= viewConfig.getOverlayMaxY()) continue;

                        if(x%subSample==0 && y%subSample==0){  

                            // check if we've been interrupted
                            if(Thread.currentThread().isInterrupted()) {
								Thread.currentThread();
								// if we have been, clear the flag w/ different call 
                                if(Thread.interrupted())
                                    // throw exception and return to run() method to catch
                                    throw new InterruptedException();
							}


                            // find color of pixel at x,y coordinate of given image
                            // and add to colors
                            int colorAsInt = img.getRGB(x, img.getHeight() - y - 1);
                            this.colors.add(new Color(colorAsInt, true));

                            // get array of rgb andalpha value
                            float[] col = new Color(colorAsInt).getColorComponents(null);
                            
                            // calculate new alpha value and add to alphas
                            double alpha = 1-Math.pow(
                                (Math.sqrt(col[0]*col[0]+col[1]*col[1]+col[2]*col[2])/Math.sqrt(3.0)),
                                colorScale);

                            this.alphas.add(new Float( (float) alpha));

                            // calculate xyz coordinates for 3d representation and 
                            // add to coordinates
                            xf = (x-geoMgr.iXA)/scale;
                            yf = (y-geoMgr.iYA)/scale;
                            zf = (float) dz * (z-geoMgr.iZA) / scale;


                            yf = -yf;
                            zf = -zf;
                            this.coordinates.add(new double[]{ xf * dx, yf * dy, zf
                                //-(x-img.getWidth()/2+(subSample*Math.random()))*dx
                                //, -(y-img.getHeight()/2+(subSample*Math.random()))*dy
                                //, (z-this.images.size()/2+(subSample*Math.random()))*dz
                            });

                        }
                    }
                }
            }
            System.out.println("Finished generateOverlayInfo().");
    }

    /**
     * Set the appropriate min/max xyz boundaries for later call to generateOverlayInfo()
     * if user specifies that they want an auto ROI. Only points within 2 cell diameters
     * of the selected cell will be shown. Note that XYZ values are normally fetched from
     * the color display tab of the geometric 3D window, so they must be overwritten here
     * before the next method call.
     *
     */
    public void addOverlayAutoROI() {

        Cell c = aceTree.getCurrentCell();
        if(c != this.overlayROICell) {
            this.overlayROICell = c;
            c = null;
        }
        // not sure how to compare cells but pretty sure this should work anyways
        int x = this.overlayROICell.getX();
        int y = this.overlayROICell.getY();
        double diameter = aceTree.getCurrentCell().getDiam();

        viewConfig.setOverlayMinX( (int) (x - 2 * diameter) );
        viewConfig.setOverlayMaxX( (int) (x + 2 * diameter) );

        viewConfig.setOverlayMinY( (int) (y - 2 * diameter) );
        viewConfig.setOverlayMaxY( (int) (y + 2 * diameter) );

        if(c != this.overlayROICell) viewConfig.setOverlayXYZChanged(true);
    }

    /**
     * Load the buffered image slices into memory, translate them into a set of points that can be
     * used in a 3D image, and create and add a node to the existing branchgroup nucBG 
     *
     * @param None.
     * @return none. 
     */
    // overlay used to be exclusively generated in this class, but a separate thread (Image3DOverlayGenerator) was created
    // so that the window would still be functional while the overlay was loading. Commented out code was part of the 
    // initial version.
    public void createOverlay() throws InterruptedException  {
        // change the max/min x/y if use wants to focus on active cell's region only
        if(viewConfig.useOverlayAutoROI())
            addOverlayAutoROI();

        // only if the overlay has not yet been created or we have changed timepoints should we reload the images
        if(this.overlay == null || this.time != (aceTree.getImageTime() + aceTree.getTimeInc())) {
            loadImages();
            viewConfig.setOverlayXYZChanged(true);
        }

        // if the viewing region has changed, if the user has specified a ROI,
        // if the active cell for the ROI has changed, or the images have been reloaded
        // recreate the overlay
        if(viewConfig.isOverlayXYZChanged()) {
            viewConfig.setOverlayXYZChanged(false);
            viewConfig.setChangeOverlayChannel(false);
            generateOverlayInfo(1,1,nucleiMgr.getZPixRes(), viewConfig.getOverlaySubsample(), 2.5);
            this.overlay = generateOverlay(calculateOverlayPointSize());
        } else if(viewConfig.changeOverlayChannel()) {
            viewConfig.setChangeOverlayChannel(false);
            this.overlay = generateOverlay(calculateOverlayPointSize());
        }

        // if the overlay is not null (would be null eg if user specified range of 0)
        // make a duplicate and add to the branch group
        // by making a duplicate, we can avoid recreating the overlay each time branch group
        // changes eg we don't want to recreate overlay if user adding sisters, tracks
        if(this.overlay != null) {
            //Shape3D duplicateOverlay = new Shape3D();
            //duplicateOverlay.duplicateNode(this.overlay, true);
            geoMgr.setOverlay(this.overlay);
            return;
        }
    }

    public int getSliceCount() {
    	return this.sliceCount;
	}
    public float calculateOverlayPointSize() {
        return viewConfig.getOverlaySubsample() * 5f;
    }
}
