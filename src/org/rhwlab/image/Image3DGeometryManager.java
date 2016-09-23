/*
 * Image3DGeometryManager 
 *
 * used as interface to get nuclear info from AceTree on behalf
 * of Image3D
 *
 * creates nuclear scenegraph to return to Image3D
 *
 * Formerly Nuclei3D (Inner class of Image3D) 
 * 2.21.2014
 */
package org.rhwlab.image;

//import gov.noaa.pmel.sgt.LineAttribute;
import java.awt.*;
import java.util.*;

import javax.media.j3d.*;

import javax.vecmath.*;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.Cell;
import org.rhwlab.acetree.PartsList;

import com.sun.j3d.utils.geometry.Sphere;

public class Image3DGeometryManager {

    private     AceTree         iAceTree;
    private     NucleiMgr       iNucleiMgr;
    private     Image3DViewConfig viewConfig;
    private     BranchGroup     nucBG;
    private     Vector          iSisterList;
    private     Hashtable       iNucleiHash;
    private     boolean         iTransparent;
    private     boolean         iShowIt;
    private     boolean         iEmptyNuclei3D;

    private     Shape3D         overlay;
    private     boolean         overlayReady;
    
    private		boolean			iShowSameSizeSpheres;
    private		double			iSphereScale;

    public      int             iXA;
    public      int             iYA;
    public      float           iZA;
    private     static          String	CS = ",";

    private final String [] SISTERS = {
            "E", "MS", "AB", "P1", "EMS", "P2", "C", "P3", "D", "P4"
    };

    public Image3DGeometryManager(Image3D parent) {
        this.viewConfig = Image3DViewConfig.getInstance(); 
        this.iAceTree = parent.getAceTree();
        this.iNucleiMgr = iAceTree.getNucleiMgr();
        this.overlay = null;
        iShowSameSizeSpheres = false;
        iSphereScale = 1.;
    }
    
    public void setShowSameSizeSpheres(boolean show) {
    	iShowSameSizeSpheres = show;
    }
    
    // Set sphere size for when all spheres are shown with the same radius
    private final float SETSPHERESIZE = (float)0.075;
    
    public BranchGroup createNuclearBranchGroup(int iXA, int iYA, float iZA) {
        //println("Nuclei3D, ");
        //this.nucBG.detach();
        this.nucBG = new BranchGroup();
        this.nucBG.setCapability(BranchGroup.ALLOW_DETACH);

        this.iXA = iXA;
        this.iYA = iYA;
        this.iZA = iZA;

        //add sisters
        if(viewConfig.areSistersVisible()){
            this.iSisterList = prepareSortedList();
            addNucleiViaSisterList();
        } else {
            addNuclei();
        }

        if(viewConfig.isShowingTails()) { 	
            addTracks();
        }

        if(viewConfig.isShowingOverlay() && overlayReady && this.overlay != null) {
        	System.out.println("Calling addOverlay()...");
            addOverlay();
        }

        return this.nucBG;
    }
    
    //////////////////// SISTER CODE START //////////////////// 

    private int inSISTERS(String name) {
        for (int i=0; i < SISTERS.length; i++) {
            if (name.equals(SISTERS[i]))
            	return i;
        }
        return -1;
    }

    private String getSister(String name) {
        String sister = "";
        for (int i=0; i < SISTERS.length; i++) {
            if (name.equals(SISTERS[i])) {
                if (i % 2 == 0) {
                    sister = SISTERS[i + 1];
                } else {
                    sister = SISTERS[i - 1];
                }
                break;
            }

        }
        return sister;
    }

    private void showSisters() {
        Vector sorted = prepareSortedList();
        iSisterList = sorted;
        for (int i=0; i < sorted.size(); i++) {
            if (i == sorted.size() - 1) break;

            String first = (String) sorted.get(i);
            String second = (String) sorted.get(i + 1);

            if (areSisters(first, second)) {
                println("sisters, " + first + CS + second);
                i++;
            }
        }
    }

    private Vector prepareSortedList() {
        //iSisterList = new Vector();
        iNucleiHash = new Hashtable();
        Vector sortedList = new Vector();
        Vector catsAndDogs = new Vector();
        NucleiMgr nucleiMgr = iAceTree.getNucleiMgr();

        int time = iAceTree.getImageTime() + iAceTree.getTimeInc();
        Vector nuclei = nucleiMgr.getNucleiRecord().elementAt(time - 1);
        for (int i=0; i < nuclei.size(); i++) {
            Nucleus n = (Nucleus)nuclei.get(i);
            //println("prepareSortedList, encountered, " + n.identity);
            if (n.status == Nucleus.NILLI) 
                continue;
            iNucleiHash.put(n.identity, n);
            if (inSISTERS(n.identity) >= 0) {
                catsAndDogs.add(n.identity);
                continue;
            }
            sortedList.add(n.identity);
            //println("prepareSortedList, adding, " + n.identity);
        }
        Collections.sort(sortedList);
        addCatsAndDogs(sortedList, catsAndDogs);

        return sortedList;
    }

    private void addCatsAndDogs(Vector sortedList, Vector catsAndDogs) {
        while (catsAndDogs.size() > 0) {
            String name = (String)catsAndDogs.remove(0);
            sortedList.add(name);
            //println("addCatsAndDogs, adding, " + name);
            String sister = getSister(name);
            if (sister.length() > 0 && catsAndDogs.contains(sister)) {
                sortedList.add(sister);
                //println("addCatsAndDogs, adding, " + sister);
                catsAndDogs.remove(sister);
            }
        }
    }

    private boolean areSisters(String first, String second) {
        boolean [] ans = new boolean[2]; // first true if it is a special case; second if are sisters
        ans[0] = ans[1] = false;
        specialCaseSisters(first, second, ans);
        if (ans[0]) {
            return ans[1];
        }
        String s = "";
        s = first.substring(0, first.length() - 1);
        int k = second.indexOf(s);
        return (k == 0 && first.length() == second.length());
    }
    
    private void specialCaseSisters(String first, String second, boolean [] ans) {
        int firstLoc = inSISTERS(first);
        int secondLoc = inSISTERS(second);
        ans[0] = firstLoc >= 0 || secondLoc >= 0;
        if (firstLoc % 2 == 0) {
            ans[1] = (secondLoc == firstLoc + 1);
        } else ans[1] = (secondLoc == firstLoc - 1);
    }

    private void addNucleiViaSisterList() {
        //println("addNucleiViaSisterList, " + System.currentTimeMillis());
        iShowIt = true;
        iTransparent = false;
        NucleiMgr nucleiMgr = iAceTree.getNucleiMgr();
        int time = iAceTree.getImageTime() + iAceTree.getTimeInc();
        Vector nuclei = nucleiMgr.getNucleiRecord().elementAt(time - 1);
        nuclei = copyNuclei(nuclei);
        getCenter(nuclei);
        while (iSisterList.size() > 0) {
            String name = (String)iSisterList.remove(0);
            if (iSisterList.size() == 0) {
                addOne(name, false);
                break;
            }
            String next = (String)iSisterList.get(0);
            if (areSisters(name, next)) {
                iSisterList.remove(0);
                addSisters(name, next, time);
            } else {
                addOne(name, false);
            }

        }

    }

    private void addSisters(String first, String second, int time) {
        //println("addSisters, " + first + CS + second);
        Nucleus n1 = addOne(first, true);
        boolean showing = iShowIt;
        Nucleus n2 = addOne(second, true);
        iShowIt = iShowIt && showing;
        //println("addSisters, " + iShowIt + CS + first + CS + second);
        if (iShowIt && !iTransparent) {
            addConnector(n1, n2, viewConfig.getSisterColor());
        }

    }

    private void addConnector(Nucleus n1, Nucleus n2, Color color) {
        if (!iShowIt) return;
        LineArray connector = new LineArray(2, GeometryArray.COORDINATES | GeometryArray.COLOR_3   );
        float xf, yf, z;
        int width = ImageWindow.cImageWidth;
        float scale = width/2;
        float xoff = iXA;
        float yoff = iYA;
        float zoff = iZA;
        Nucleus n = n1;
        xf = (n.x - xoff)/scale;
        yf = (n.y - yoff)/scale;
        yf = -yf; // for 3D compatibility
        iNucleiMgr.getZPixRes();
        z = (float)iNucleiMgr.getZPixRes() * (n.z - zoff) / scale;
        z = -z; // for 3D compatibility
        connector.setCoordinate(0, new Point3f(xf, yf, z));
        n = n2;
        xf = (n.x - xoff)/scale;
        yf = (n.y - yoff)/scale;
        yf = -yf; // for 3D compatibility
        z = (float)iNucleiMgr.getZPixRes() * (n.z - zoff) / scale;
        z = -z; // for 3D compatibility
        connector.setCoordinate(1, new Point3f(xf, yf, z));
        LineAttributes la = new LineAttributes();
        la.setLineWidth(5);

        //reportAngle(time, n1.identity, n2.identity, n1.x, n1.y, n1.z * zpixres, n2.x, n2.y, n2.z * zpixres);

        Color3f c = new Color3f(color);
        connector.setColor(0, c);
        connector.setColor(1, c);
        Appearance app = new Appearance();
        app.setLineAttributes(la);
        System.out.println("Getting lineage number in addConnector()...");
        int k = getLineageNumber(n1.identity);
        if (k >= viewConfig.getNumDispProps() - 2) {
            connector.setColor(0, ColorConstants.white);
            connector.setColor(1, ColorConstants.white);
        }
        nucBG.addChild(new Shape3D(connector, app));

    }

    /*
    private void reportAngle(int time, String name1, String name2, double x1, double y1, double z1, double x2, double y2, double z2) {
            //println("reportAngle, " + name1 + CS + name2 + CS + x1 + CS + y1 + CS + z1 + CS + x2 + CS + y2 + CS + z2);
            double [] phiTheta = Angle3.angles(x1, y1, z1, x2, y2, z2);
            println("reportAngle, " + time + CS + name1 + CS + name2 + CS + fmt1(phiTheta[0]) + CS + fmt1(phiTheta[1]));
    }
    */

    private Nucleus addOne(String name, boolean sister) {
        Nucleus n = (Nucleus)iNucleiHash.get(name);
        System.out.println("Nucleus identity: "+n.identity);
        if (n == null) {
            println("addOne, " + name + CS + sister);
        }
        float xf, yf, z, rf;
        int width = ImageWindow.cImageWidth;
        float scale = width/2;
        float xoff = iXA;
        float yoff = iYA;
        float zoff = iZA;
        xf = (n.x - xoff)/scale;
        yf = (n.y - yoff)/scale;
        yf = -yf; // for 3D compatibility
        z = (float)iNucleiMgr.getZPixRes() * (n.z - zoff) / scale;
        z = -z; // for 3D compatibility
        rf = (n.size/2) / scale;
        //System.out.println("Radius: "+rf);
        Appearance app = new Appearance();
        new TransparencyAttributes(TransparencyAttributes.BLENDED, 1.0f);
        new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.5f);
        
        
        int k = getLineageNumber(n.identity);
        System.out.println("Lineage number returned in addOne: "+k);
        iShowIt = true;
        iTransparent = false;
        
        // "other" cell colors checked here
        if (k < viewConfig.getNumDispProps() - 2) 
        	app = getLineageColor(k);
        else {
            int m = viewConfig.getDispProp(viewConfig.getNumDispProps() - 2).getLineageNum();
            switch(m) {
            case 0:
            	// Omit
                iShowIt = false;
                break;
            case 1:
            	// Transparent
                app = getLineageColor(11);
                break;
            default:
            	// White
                app = getLineageColor(10);
                break;
            }
        }
        iShowIt = (app != null);
        //println("addOne, " + name + CS + iShowIt + CS + app);
        if (iShowIt && app != null) {
            if (sister)
            	rf = Math.min(n.size/2, 10)/scale;
            if (iShowSameSizeSpheres)
            	rf = SETSPHERESIZE*(float)iSphereScale;
            nucBG.addChild(makeNamedSphere(n.identity, xf, yf, z, rf, app));
            //if (app.getTransparencyAttributes() != tran) count++;
            //System.out.println("addNuclei: " + n.identity + CS + xf + CS + yf + CS + z + CS + rf);
        }
        return n;
    }
    
    // Set scale for unity sphere size
    public void setSphereScale(double s) {
    	iSphereScale = s;
    	//System.out.println("Sphere scale: "+iSphereScale);
    }

    //////////////////// SISTER CODE END //////////////////// 
    
    public boolean empty() {
        return iEmptyNuclei3D;
    }

    private Appearance setColor(Color3f color) {
        Color3f eColor    = new Color3f(0.0f, 0.0f, 0.0f);
        //Color3f sColor    = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f sColor    = color;
        Material m = new Material(eColor, eColor, sColor, sColor, 100.0f);
        m.setLightingEnable(true);
        Appearance app = new Appearance();
        app.setMaterial(m);
        return app;
    }

    private Appearance getLineageColor(int k) {
        Appearance app = null;
        switch(k) {
        case 0:
            app = setColor(ColorConstants.red); // ABa
            break;
        case 1:
            app = setColor(ColorConstants.blue); // ABp
            break;
        case 2:
            app = setColor(ColorConstants.green); // E
            break;
        case 3:
            app = setColor(ColorConstants.yellow); // P
            break;
        case 4:
            app = setColor(ColorConstants.cyan); // polar
            break;
        case 5:
            app = setColor(ColorConstants.magenta); // C
            break;
        case 6:
            app = setColor(ColorConstants.pink); // C
            break;
        case 7:
            app = setColor(ColorConstants.gray); // C
            break;
        case 8:
            app = setColor(ColorConstants.lightgray); // C
            break;
        case 9:
        	app = setColor(ColorConstants.darkgray);
        	break;
        case 10:
        	app = setColor(ColorConstants.white);
        	break;
        case 11:
            app = setColor(ColorConstants.lightgray); // C
            TransparencyAttributes tran = new TransparencyAttributes(TransparencyAttributes.BLENDED, .750f);
            app.setTransparencyAttributes(tran);
            break;
        default:
            app = null; // C
            break;
        }
        return app;
    }

    private Appearance getExpressionColor(Nucleus n) {
        Cell.setMinRed(viewConfig.getMinRed());
        Cell.setMaxRed(viewConfig.getMaxRed());
        int k = Cell.getDiscrete(n.rweight);
        Color color = Cell.getTheColor(k);
        //color = getColor(n.rweight); // 20070917 hack, see immediately below
        color = getColor(n); // 20070917 hack, see immediately below
        Color3f c3f = new Color3f(color);
        Appearance app = setColor(c3f);
        return app;
    }

    // 20070917 cloned this from VTreeImpl
    private Color getColor(Nucleus n) {
        //private Color getColor(int rweight) {
        //CellData cd = (CellData)cellData.elementAt(k);
        int rweight = n.rweight;
        int red = rweight;

        float frac = ((float)red - (float)Cell.cMin)/((float)Cell.cMax - (float)Cell.cMin);
        frac = Math.min(frac, 1f);
        frac = Math.max(frac, 0f);
        float iHue = 0;
        float hue = iHue;
        float sat = 1;
        int col = Color.HSBtoRGB(hue, sat, frac);
        //println("getColor, " + n.identity + CS + red + CS + frac + CS + col);
        return new Color(col);
    }



    private Vector copyNuclei(Vector nuclei) {
        Vector newNuclei = new Vector();
        Enumeration e = nuclei.elements();
        Nucleus n = null;
        while (e.hasMoreElements()) {
            n = (Nucleus)e.nextElement();
            newNuclei.add(n.copy());
        }
        Collections.sort(newNuclei, n);
        return newNuclei;
    }

    // code for adding simple cell tracking
    // gets a vector of nuclei (nuclei) from AceTree's nuclear manager for times current through current-N
    // for a nucleus at position nuclei[i] for all of the above time points, then calls addConnector to create tail
    private void addTracks() {
        // int representing how many timepoints back to go
        int goBack = 1; 
        // represent different nuclei/nuclei vectors at different timepoints
        // older ones refer to the ones from earlier timepoints 
        Nucleus oldNuc = null;
        Nucleus newNuc = null;
        Vector oldNuclei = null;
        Vector newNuclei = null;

        // get the nuclei manager from AceTree and the current time
        NucleiMgr nucleiMgr = iAceTree.getNucleiMgr();
        int time = iAceTree.getImageTime() + iAceTree.getTimeInc();

        // get the nuclei list at the current time
        newNuclei = nucleiMgr.getNucleiRecord().elementAt(time - goBack);
        getCenter(newNuclei);

        while(goBack < viewConfig.getTailTimePoints()) {
            goBack++;

            try {
                // get the nuclei list at the next oldest time
                oldNuclei = nucleiMgr.getNucleiRecord().elementAt(time - goBack);
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }

            // if nuclei vector at that time is empty, can't go back any further in time
            if (oldNuclei.size() == 0) return;

            // loop over every nucleus in new nuclei and find that same nucleus
            // in oldnuclei (ie one point farther back in time)
            for(int i = 0; i < newNuclei.size(); i++) {

                newNuc = (Nucleus) newNuclei.elementAt(i);
                if(newNuc.status < 0) continue;

                for(int j = 0; j < oldNuclei.size(); j++) {
                    oldNuc = (Nucleus) oldNuclei.elementAt(j);
                    if(oldNuc.status < 0) continue;

                    //if(oldNuc.compare(newNuc, oldNuc) == 0)  // << DOES NOT WORK
                    if(oldNuc.index == newNuc.predecessor)
                        addConnector(newNuc, oldNuc, viewConfig.getCustomTailColor());

                }
            }
            
            // redefine new nuclei as old nuclei
            // and at the beginning of the next iteration of the while loop we'll 
            // increment goBack and find the next oldest nuclei list to put in old nuclei
            newNuclei = oldNuclei;
        }
    }

    private void addNuclei() {
        NucleiMgr nucleiMgr = iAceTree.getNucleiMgr();
        int time = iAceTree.getImageTime() + iAceTree.getTimeInc();
        //Vector nuclei = (Vector)nucleiMgr.getNucleiRecord().elementAt(time - 1);
        Vector nuclei = nucleiMgr.getElementAt(time - 1);
        
        iEmptyNuclei3D = (nuclei.size() == 0);
        if (iEmptyNuclei3D)
        	return;
        
        nuclei = copyNuclei(nuclei);
        getCenter(nuclei);
        Nucleus n = null;
        float xf, yf, z, rf;
        int width = ImageWindow.cImageWidth;
        int height = ImageWindow.cImageHeight;
        if(width==0) width=800;
        if(height==0) height=512;

        float scale = width/2;
        float xoff = iXA;
        float yoff = iYA;
        float zoff = iZA;
        for (int j=0; j < nuclei.size(); j++) {
            n = (Nucleus)nuclei.elementAt(j);
            if (n.status < 0) continue;
            xf = (n.x - xoff)/scale;
            yf = (n.y - yoff)/scale;
            yf = -yf; // for 3D compatibility
            z = (float)iNucleiMgr.getZPixRes() * (n.z - zoff) / scale;
            z = -z; // for 3D compatibility
            rf = (n.size/2) / scale;
            //System.out.println("Radius: "+rf);
            Appearance app = new Appearance();
            
            TransparencyAttributes tran = new TransparencyAttributes(TransparencyAttributes.BLENDED, 1.0f);
            tran.setTransparency(0.8f);
            /*
            TransparencyAttributes tran2 = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.5f);
            TransparencyAttributes semitran = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.25f);
            */
            
            int rule = getLineageRule(n.identity);
            int k = getLineageNumber(rule); //these were assumed same, has to be a bug
            //System.out.println(n.identity+" rule & number in addNuclei: "+rule+CS+k);
            iShowIt = true;
            if (rule < viewConfig.getNumDispProps() - 2){
                app = getLineageColor(k);
            }
            else {
                int m = viewConfig.getDispProp(viewConfig.getNumDispProps() - 2).getLineageNum();
                switch(m) {
                case 0:
                	// Omit
                    iShowIt = false;
                    break;
                case 1:
                	// Transparent
                    app = getLineageColor(11);
                    break;
                default:
                	// White
                    app = getLineageColor(10);
                    break;
                }
            }
            if (viewConfig.isUsingExpression()) {
                if (viewConfig.isUsingExpressionColors()) app = getExpressionColor(n);
                if (n.rweight < viewConfig.getMinRed()) {
                    app = setColor(ColorConstants.white);
                    app.setTransparencyAttributes(tran);
                    iShowIt = viewConfig.isShowingNonExpressing(); 
                    //iShowIt = false;
                }
            } else if(viewConfig.getDispProp(viewConfig.getNumDispProps() - 3).getName()
                    .equals("Special")) {
                app = special(n);
            }
            if (iShowIt && app != null) {
            	if (iShowSameSizeSpheres)
                	rf = SETSPHERESIZE*(float)iSphereScale;
                nucBG.addChild(makeNamedSphere(n.identity, xf,yf,z,rf,app));
                if (app.getTransparencyAttributes() != tran)
				 {
				}
            }
        }
    }

    private boolean inSCAList(Nucleus n) {
        String [] theList = {
            "ABaraaappaa",
            "ABalpaappa",
            "ABaraaappap",
            "ABaraaapaaa",
            "ABaraaappp",
            "MSaaaaaa",
            "ABalpaapppa",
            "ABprpapppp",
            "ABalpaapppp",
        };
        for (int i=0; i < theList.length; i++) {
            if (n.identity.equals(theList[i])) return true;
        }
        return false;
    }

    private Appearance special(Nucleus n) {
        TransparencyAttributes faint = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.8f);
        new TransparencyAttributes(TransparencyAttributes.BLENDED, 1.f);
        TransparencyAttributes solid = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.f);
        Appearance app = null;
        String name = n.identity;
        Appearance appRest = null; //setColor(ColorConstants.white);
        //appRest.setTransparencyAttributes(faint);
        app = appRest;
        iShowIt = true;
        
        // Make name case-insensitive
        name = name.toLowerCase();
        if (name.indexOf("e") == 0) {
            app = setColor(ColorConstants.yellow);
            app.setTransparencyAttributes(solid);
        }
        if (name.indexOf("msaa") == 0) {
            app = setColor(ColorConstants.magenta);
            app.setTransparencyAttributes(solid);
            if (   name.indexOf("msaaaaaa") == 0
                   || name.indexOf("msaappp") == 0) {
                app = appRest;
            }
        }
        if (name.indexOf("mspa") == 0) {
            app = setColor(ColorConstants.cyan);
            app.setTransparencyAttributes(solid);
            if (   name.indexOf("mspapp") == 0) {
                app = appRest;
            }
        }
        if (   name.indexOf("abalpaaa") == 0
               || name.indexOf("abalpaapa") == 0
               || name.indexOf("abalpapp") == 0)
            {
                app = setColor(ColorConstants.pink);;
                app.setTransparencyAttributes(solid);

            }
        if (   name.indexOf("abaraaaa") == 0
               || name.indexOf("abaraaapa") == 0)
            {
                app = setColor(ColorConstants.blue);
                app.setTransparencyAttributes(solid);
                if (name.indexOf("abaraaapaaa") == 0) {
                    app = appRest;
                }

            }
        if (name.indexOf("abaraap") == 0) {
            app = setColor(ColorConstants.blue);
            app.setTransparencyAttributes(solid);
        }
        if (name.indexOf("abarapa") == 0) {
            app = setColor(ColorConstants.blue);
            app.setTransparencyAttributes(solid);
            if (name.indexOf("abarapapapa") == 0) {
                app = appRest;
            }
        }
        if (app == appRest) {
            iShowIt = true;
            app = setColor(ColorConstants.white);
            app.setTransparencyAttributes(faint);
        }
        return app;
    }

    private Appearance special(Nucleus n, boolean bogus) {
        TransparencyAttributes faint = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.8f);
        new TransparencyAttributes(TransparencyAttributes.BLENDED, 1.f);
        TransparencyAttributes solid = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.f);
        Appearance app = null;
        String name = n.identity;
        Appearance appRest = null; //setColor(ColorConstants.white);
        //appRest.setTransparencyAttributes(faint);
        app = appRest;
        iShowIt = true;
        
        // Make case-insensitive
        name = name.toLowerCase();
        if (name.indexOf("e") == 0) {
            app = setColor(ColorConstants.green);
            app.setTransparencyAttributes(solid);
        }
        if (name.indexOf("msaa") == 0) {
            app = setColor(ColorConstants.magenta);
            app.setTransparencyAttributes(solid);
            if (   name.indexOf("msaaaaaa") == 0
                   || name.indexOf("msaappp") == 0) {
                app = appRest;
            }
        }
        if (name.indexOf("mspa") == 0) {
            app = setColor(ColorConstants.cyan);
            app.setTransparencyAttributes(solid);
            if (   name.indexOf("mspapp") == 0) {
                app = appRest;
            }
        }
        if (name.indexOf("abalpaaa") == 0
               || name.indexOf("abalpaapa") == 0
               || name.indexOf("abalpapp") == 0)
            {
                app = setColor(ColorConstants.red);
                app.setTransparencyAttributes(solid);

            }
        if (name.indexOf("abaraaaa") == 0
               || name.indexOf("abaraaapa") == 0)
            {
                app = setColor(ColorConstants.blue);
                app.setTransparencyAttributes(solid);
                if (name.indexOf("abaraaapaaa") == 0) {
                    app = appRest;
                }

            }
        if (name.indexOf("abaraap") == 0) {
            app = setColor(ColorConstants.blue);
            app.setTransparencyAttributes(solid);
        }
        if (name.indexOf("abarapa") == 0) {
            app = setColor(ColorConstants.blue);
            app.setTransparencyAttributes(solid);
            if (name.indexOf("abarapapapa") == 0) {
                app = appRest;
            }
        }
        if (app == appRest) {
            iShowIt = true;
            app = setColor(ColorConstants.white);
            app.setTransparencyAttributes(faint);
        }
        return app;
    }

    private void getCenter(Vector nuclei) {
        iXA = 0;
        iYA = 0;
        iZA = 0.f;
        int count = 0;
        Enumeration e = nuclei.elements();
        while (e.hasMoreElements()) {
            Nucleus n = (Nucleus)e.nextElement();
            if (n.status == Nucleus.NILLI) continue;
            iXA += n.x;
            iYA += n.y;
            iZA += n.z;
            count++;
        }
        iXA /= count;
        iYA /= count;
        iZA /= count;
    }

    private TransformGroup makeNamedSphere(String name, float x, float y, float z, float r, Appearance a) {
        Transform3D translate = new Transform3D();
        translate.set(new Vector3f(x, y, z));
        NamedSphere sph = new NamedSphere(name, r, a);
        TransformGroup tg = new TransformGroup(translate);
        tg.addChild(sph);
        return tg;
    }

    private TransformGroup makeSphere(float x, float y, float z, float r, Appearance a) {
        Transform3D translate = new Transform3D();
        translate.set(new Vector3f(x, y, z));
        Sphere sph = new Sphere(r, a);
        TransformGroup tg = new TransformGroup(translate);
        tg.addChild(sph);
        return tg;
    }

    private void println(String s) {
        System.out.println(s);
    }
    
    // Returns the selected color index of the nucleus with "name"
    private int getLineageNumber(String name) {
        if (name.indexOf("Z") >= 0) 
        	name = "P"; 
        int num = viewConfig.getNumDispProps(); 
        
        String propername = PartsList.lookupSulston(name);
        
        //System.out.println("Sulston lookup getLineageNumber: "+propername);
        for (int i = 0; i < viewConfig.getNumDispProps(); i++) {
        	boolean matchpropername = propername!=null && propername.equals(viewConfig.getDispProp(i).getName());
            if (matchpropername || name.indexOf(viewConfig.getDispProp(i).getName()) >= 0) {
            	//System.out.println("Number received: "+num);
            	num = viewConfig.getDispProp(i).getLineageNum();
                break;
            }
        }
        return num;
    }
    
    // Returns selected color index of the nucleus given its display properties index
    private int getLineageNumber(int index) {
    	if (index >= 0 && index < viewConfig.getNumDispProps())
    		return viewConfig.getDispProp(index).getLineageNum();
    	else
    		return 0;
    }

    
    // Returns index of the SubLineageDisplayProperty in display properties of the nucleus with "name"
    private int getLineageRule(String name) {
        if (name.indexOf("Z") >= 0) 
        	name = "P"; //patch for germ line
        
        int numDispProps = viewConfig.getNumDispProps(); 
        int i;
        Integer propernamei = null;
        Integer namei = null;
        //System.out.println("name: "+name);
        
        // lookupSulston() retrieves four-letter names tagged behind sulston names
        String propername = PartsList.lookupSulston(name);
        //System.out.println("propername: "+propername+"\n");
        
        String target = "";
        name = name.toLowerCase();
        for (i = 0; i < numDispProps; i++) {
        	// Make case-insensitive
        	target = viewConfig.getDispProp(i).getName();
        	target = target.toLowerCase();
        	if (!target.equals("")) {
        		if (propername != null && propername.startsWith(target)) {
        			propernamei = i;
        		}
        		else if (name.startsWith(target)) {
        			namei = i;
        		}
        	}
        }
        
        if (propernamei != null)
    		return propernamei;
    	else if (namei != null)
    		return namei;
        
        // Nothing in the display properties matches name or propername
        int otherIndex = viewConfig.getOtherIndex();
    	if (otherIndex != -1)
    		return otherIndex;
    	else
    		return numDispProps - 2;
    }

    /**
     * Load the buffered image slices into memory, translate them into a set of points that can be
     * used in a 3D image, and create and add a node to the existing branchgroup nucBG 
     *
     * @param None.
     * @return none. 
     */
    // overlay used to be exclusively generated in this class, but a separate thread (Image3DOverlayGenerator) was created
    // so that the window would still be functional while the overlay was loading
    public void addOverlay() {
        if(this.overlay != null) {
            Shape3D duplicateOverlay = new Shape3D();
            duplicateOverlay.duplicateNode(this.overlay, true);

            nucBG.addChild(duplicateOverlay);
            System.out.println("Finished adding to scene graph.");
            return;
        }

        System.out.println("No overlay to add to scene graph.");
        return;
    }

    public synchronized void setOverlay(Shape3D overlay) { this.overlay = overlay; }
    public synchronized void setOverlayReady(boolean ready) { this.overlayReady = ready; }

} 

