/*
 * Created on Jan 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.acetree;

import ij.ImagePlus;
import ij.ImageJ;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.rhwlab.analyze.Analysis;
import org.rhwlab.analyze.Analysis11;
import org.rhwlab.analyze.Analysis2;
import org.rhwlab.analyze.Analysis3;
import org.rhwlab.analyze.Analysis4;
import org.rhwlab.analyze.Analysis5;
import org.rhwlab.analyze.Analysis6;
import org.rhwlab.analyze.Analysis8;
import org.rhwlab.analyze.Analysis9;
import org.rhwlab.analyze.DeathAndDivisionLog;
import org.rhwlab.analyze.ExtractRed;
import org.rhwlab.analyze.RedBkgComp;
import org.rhwlab.analyze.RedCompSeries;
import org.rhwlab.analyze.RedCorrector2;
import org.rhwlab.analyze.UsePlugin;

import org.rhwlab.image.DepthViews;
import org.rhwlab.nucedit.Overlaps;
import org.rhwlab.nucedit.SkipFalseNegatives;
import org.rhwlab.snight.Config;
import org.rhwlab.snight.WormGUIDESWindow;
import org.rhwlab.tree.SubTrees;
import org.rhwlab.utils.C;
import org.rhwlab.utils.Log;

import org.rhwlab.help.AceTreeHelp;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class AceMenuBar extends JMenuBar implements ActionListener, ItemListener {
    AceTree iAceTree;
    JMenuItem iQuickOpen;
    JMenuItem iOpenFromDB;
    JMenuItem iOpen;
    JMenuItem iOpenSeries;
    JMenuItem iOptions;
    JMenuItem iSave;
    JMenuItem iSaveConfig;
    JMenuItem iJLaunch;
    JMenuItem iExit;
    JMenuItem iBuildTree;
    JMenuItem iReBuildTree;
    JMenuItem iClearTree;
    JMenuItem iExpandTree;
    JMenuItem iExport;
    JMenuItem iNuclei;
    JMenuItem iShowLog;
    JMenuItem iDDLog;
    JMenuItem iAnalyze;
    JMenuItem iAnalyze2;
    JMenuItem iAnalyze3;
    JMenuItem iAnalyze4;
    JMenuItem iAnalyze5;
    JMenuItem iAnalyze6;
    JMenuItem iExtractRed;
    JMenuItem iFixCrosstalk;
    JMenuItem iRedBkgComp;
    JMenuItem iRedCompSeries;
    JMenuItem iAnalyze8;
    JMenuItem iAnalyze9;
    JMenuItem iAnalyze10;
    JMenuItem iEditTraverse;
    JMenuItem iAdjacencies;
    JMenuItem iLazarus;
    JMenuItem iSiamese;
    JMenuItem iJuvenesence;
    JMenuItem iZafer1;
    JMenuItem iOrientation;
    JMenuItem iOverlaps;
    JMenuItem iUsePlugin;
    JMenuItem iEdit;
    JMenuItem iEdit2;
    JMenuItem iViewNuclei;
    JMenuItem iRelink;
    JMenuItem iSkipFalseNegatives;
    JMenuItem iKillCells;
    // JMenuItem iKillDeepNucs;
    JMenuItem iSetEndTime;
    JMenuItem iIncrementEndTime;
    JMenuItem iUndo;
    JMenuItem iATVTree;
    JMenuItem iAncestralTree;
    JMenuItem iSulstonTree;
    JMenuItem iVTree;
    JMenuItem iSubTrees;
    JMenuItem iTest;
    JMenuItem iNew;
    JMenuItem iTestWindow;
    JMenuItem i3D;
    JMenuItem i3D2;
    JMenuItem i3D3; //WormGUIDES
    JMenuItem i3D2Z;
    JMenuItem i3DViewer;
    JMenuItem iZoomView;  
    JMenuItem iAnnotationProperties;
    JCheckBoxMenuItem i3Dsave;
    JCheckBoxMenuItem i3D2save;
    JCheckBoxMenuItem i3D2Zsave;
    JCheckBoxMenuItem i2Dsave;
    JMenuItem   iViewEllipse;
    JMenuItem   iDepthViews;
    JMenuItem iAllCentroids;
    JMenuItem iCellMovementImage;
    JMenuItem iDebugLog;
    int i3DsaveState;
    int i3D2saveState;
    int i3D2ZsaveState;
    JMenu iHelp;
    JMenuItem iAbout;
    JFileChooser iFileChooser;
    Log             iDLog;
    JMenu           iRecent;
    JMenu           iRemoveRecent;
    JMenuItem       iClearAll;
    
    JMenuItem	iBookmark;
    
    JMenuItem	iViewImgWin;

    Vector iConfigsVector;

    /**
     *
     */
    public AceMenuBar(AceTree aceTree) {
        super();
        boolean fullgui=aceTree.getFullGUI();

//        System.out.println("AceMenuBar constructor "+fullgui);
        //new Throwable().printStackTrace();
        iAceTree = aceTree;
        iDLog = aceTree.getDebugLog();

        // the File menu
        JMenu menu = new JMenu(FILE);
        add(menu);

        if(fullgui){
        iQuickOpen = new JMenuItem(QUICKOPEN);
        iQuickOpen.addActionListener(this);
        menu.add(iQuickOpen);
		}

        iOpenFromDB = new JMenuItem("Open from DB");
        iOpenFromDB.addActionListener(this);
        menu.add(iOpenFromDB);

        iOpen = new JMenuItem(OPEN);
        iOpen.addActionListener(this);
        menu.add(iOpen);
        
        // Add Bookmark option to file menu
        iBookmark = new JMenuItem("Bookmarks");
        iBookmark.addActionListener(this);
        menu.add(iBookmark);

		if(fullgui){
	        iOpenSeries = new JMenuItem(OPENSERIES);
	        iOpenSeries.addActionListener(this);
	        menu.add(iOpenSeries);
	
	        iRecent = new JMenu("Open recent");
	        menu.add(iRecent);
	        iRecent.addActionListener(this);
	        menu.add(new JMenuItem("    "));
	        menu.addSeparator();
	        iRemoveRecent = new JMenu("Remove recent");
	        menu.add(iRemoveRecent);
	        iRemoveRecent.addActionListener(this);
	        iClearAll = new JMenuItem(CLEARALL);
	        iClearAll.addActionListener(this);
	        menu.add(iClearAll);
	        menu.addSeparator();
		}

		iOptions = new JMenuItem(OPTIONS);
        iOptions.addActionListener(this);
        menu.add(iOptions);
        menu.addSeparator();
        iFileChooser = new JFileChooser("./*.zip");
        iSave = new JMenuItem(SAVE);
        iSave.addActionListener(this);
        menu.add(iSave);
		if(fullgui){
	        iSaveConfig = new JMenuItem(SAVECONFIG);
	        iSaveConfig.addActionListener(this);
	        menu.add(iSaveConfig);
		}

        menu.addSeparator();

		iJLaunch=new JMenuItem(IJ);
		iJLaunch.addActionListener(this);
		menu.add(iJLaunch);

        menu.add(new JMenuItem("    "));
        iExit = new JMenuItem(EXIT);
        iExit.addActionListener(this);
        menu.add(iExit);

		if(fullgui){
	        // the Action menu
	        JMenu menu2 = new JMenu(ACTION);
	        add(menu2);
	        iBuildTree = new JMenuItem(BUILDTREE);
	        iBuildTree.addActionListener(this);
	        menu2.add(iBuildTree);
	        iReBuildTree = new JMenuItem(REBUILDTREE);
	        iReBuildTree.addActionListener(this);
	        menu2.add(iReBuildTree);
	        iClearTree = new JMenuItem(CLEAR);
	        iClearTree.addActionListener(this);
	        menu2.add(iClearTree);
	        iExpandTree = new JMenuItem(EXPAND);
	        iExpandTree.addActionListener(this);
	        menu2.add(iExpandTree);
		}

        // the Edit menu
        menu = new JMenu(EDIT);
        add(menu);
        iEdit2 = new JMenuItem(EDITTOOLS);
        iEdit2.addActionListener(this);
        menu.add(iEdit2);

		if(fullgui){
	        iRelink = new JMenuItem(RELINK);
	        iRelink.addActionListener(this);
	        menu.add(iRelink);
	        iEdit = new JMenuItem(DOEDIT);
	        iEdit.addActionListener(this);
	        menu.add(iEdit);
	        iKillCells = new JMenuItem(KILLCELLS);
	        iKillCells.addActionListener(this);
	        menu.add(iKillCells);
		//	iKillDeepNucs = new JMenuItem(KILLDEEPNUCS);
		// iKillDeepNucs.addActionListener(this);
		// menu.add(iKillDeepNucs);
		}

    
		if(fullgui){
	        iSkipFalseNegatives = new JMenuItem(SKIPFALSENEGATIVES);
	        iSkipFalseNegatives.addActionListener(this);
	        menu.add(iSkipFalseNegatives);
		}
	        
		menu.addSeparator();
		JMenu qualityGroupmenu=new JMenu("Quality Control");
		qualityGroupmenu.addActionListener(this);
		menu.add(qualityGroupmenu);
	
		menu.addSeparator();
		iUsePlugin = new JMenuItem(USEPLUGIN);
        iUsePlugin.addActionListener(this);
        menu.add(iUsePlugin);


        //add below to quality submenu rather than main
        iEditTraverse = new JMenuItem(EDITTRAVERSE);
        iEditTraverse.addActionListener(this);
        qualityGroupmenu.add(iEditTraverse);
        iAdjacencies = new JMenuItem(ADJACENCIES);
        iAdjacencies.addActionListener(this);
        qualityGroupmenu.add(iAdjacencies);

        iLazarus = new JMenuItem("Lazarus");
        iLazarus.addActionListener(this);
        qualityGroupmenu.add(iLazarus);

        iSiamese = new JMenuItem("Siamese");
        iSiamese.addActionListener(this);
        qualityGroupmenu.add(iSiamese);

        iJuvenesence = new JMenuItem("Juvenesence");
        iJuvenesence.addActionListener(this);
        qualityGroupmenu.add(iJuvenesence);

        iZafer1 = new JMenuItem("Zafer1");
        iZafer1.addActionListener(this);
        qualityGroupmenu.add(iZafer1);

        iOrientation = new JMenuItem("Orientation");
        iOrientation.addActionListener(this);
        qualityGroupmenu.add(iOrientation);

        iOverlaps = new JMenuItem("Overlaps");
        iOverlaps.addActionListener(this);
        qualityGroupmenu.add(iOverlaps);
        //end quality control submenu

		if(fullgui){
	        menu.addSeparator();
	        iUndo = new JMenuItem(UNDO);
	        iUndo.addActionListener(this);
	        menu.add(iUndo);
	        menu.addSeparator();
	        iSetEndTime = new JMenuItem(SETENDTIME);
	        iSetEndTime.addActionListener(this);
	        menu.add(iSetEndTime);
	        iIncrementEndTime = new JMenuItem(INCREMENTENDTIME);
	        iIncrementEndTime.addActionListener(this);
	        menu.add(iIncrementEndTime);
		}

		if(fullgui){
	        // the Analyze menu
	        menu = new JMenu(ANALYZE);
	        add(menu);
	        iShowLog = new JMenuItem(SHOWLOG);
	        iShowLog.addActionListener(this);
	        menu.add(iShowLog);
	        iDDLog = new JMenuItem(DDLOG);
	        iDDLog.addActionListener(this);
	        menu.add(iDDLog);
	        iAnalyze = new JMenuItem(ANALYZE);
	        iAnalyze.addActionListener(this);
	        menu.add(iAnalyze);
	        iAnalyze2 = new JMenuItem(ANALYSISDEVELOPMENT); //Nuclei rotation
	        iAnalyze2.addActionListener(this);
	        menu.add(iAnalyze2);
	        iAnalyze3 = new JMenuItem(ANALYSIS3);
	        iAnalyze3.addActionListener(this);
	        menu.add(iAnalyze3);
	        iAnalyze4 = new JMenuItem(ANALYSIS4); //Identity data
	        iAnalyze4.addActionListener(this);
	        menu.add(iAnalyze4);
	        iAnalyze5 = new JMenuItem(ANALYSIS5);
	        iAnalyze5.addActionListener(this);
	        menu.add(iAnalyze5);
	        iAnalyze6 = new JMenuItem(ANALYSIS6); //Cell count and nuc size vs time
	        iAnalyze6.addActionListener(this);
	        menu.add(iAnalyze6);
	        iExtractRed = new JMenuItem(ANALYSIS7); //Extract red
	        iExtractRed.addActionListener(this);
	        menu.add(iExtractRed);
	        iFixCrosstalk = new JMenuItem("FixCrosstalk"); //Extract red
	        iFixCrosstalk.addActionListener(this);
	        menu.add(iFixCrosstalk);
	        iRedBkgComp = new JMenuItem(REDBKGCOMP); //Extract red
	        iRedBkgComp.addActionListener(this);
	        menu.add(iRedBkgComp);
	        iRedCompSeries = new JMenuItem(REDCOMPSERIES); //Extract red
	        iRedCompSeries.addActionListener(this);
	        menu.add(iRedCompSeries);
	        iAnalyze8 = new JMenuItem(ANALYSIS8); // image processing
	        iAnalyze8.addActionListener(this);
	        menu.add(iAnalyze8);
	        iAnalyze9 = new JMenuItem(ANALYSIS9); //Red expression
	        iAnalyze9.addActionListener(this);
	        menu.add(iAnalyze9);
	        iAnalyze10 = new JMenuItem(ANALYSIS10); //Red expression
	        iAnalyze10.addActionListener(this);
	        menu.add(iAnalyze10);
	      
	
	        iViewNuclei = new JMenuItem(VIEWNUCLEI);
	        iViewNuclei.addActionListener(this);
	        menu.add(iViewNuclei);
		}


        // the trees menu
        menu = new JMenu(TREES);
        add(menu);
        iAncestralTree = new JMenuItem(SULSTON);
        iAncestralTree.addActionListener(this);
        menu.add(iAncestralTree);
		if(fullgui){
	        iSulstonTree = new JMenuItem(CANONICAL);
	        iSulstonTree.addActionListener(this);
	        menu.add(iSulstonTree);
		}
        iVTree = new JMenuItem(VTREE);
        iVTree.addActionListener(this);
        menu.add(iVTree);
		if(fullgui){
	        iSubTrees = new JMenuItem("SubTrees");
	        iSubTrees.addActionListener(this);
	        menu.add(iSubTrees);
	
	        iATVTree = new JMenuItem(ATVTREE);
	        iATVTree.addActionListener(this);
	        menu.add(iATVTree);
		}
		
		// the view menu
		menu = new JMenu("View");
		add(menu);
		iViewImgWin = new JMenuItem("Image Window");
		iViewImgWin.addActionListener(this);
		menu.add(iViewImgWin);
		
		// the 3D menu
		menu = new JMenu("3D Rendering");
		menu.addActionListener(this);
		add(menu);
		
		// add to 3d submenu
		//if(fullgui){
	    //    i3D = new JMenuItem(THREED);
	    //    i3D.addActionListener(this);
	    //    menu.add(i3D);
		//}
		i3D3 = new JMenuItem(THREED4);
        i3D3.addActionListener(this);
        menu.add(i3D3);
		
//        i3D2 = new JMenuItem(THREED2);
//        i3D2.addActionListener(this);
//        menu.add(i3D2);
//
//        i3DViewer =new JMenuItem(NEWTHREEDVIEW);
//        i3DViewer.addActionListener(this);
//        menu.add(i3DViewer);

	//if(fullgui){      
 	// i3Dsave = new JCheckBoxMenuItem(THREEDSAVE);
   //     i3Dsave.addItemListener(this);
   //     menu.add(i3Dsave);
	//}
     //  i3D2save = new JCheckBoxMenuItem(THREEDTWOSAVE);
     //  i3D2save.addItemListener(this);
    //    menu.add(i3D2save);
      //  i3D2Zsave = new JCheckBoxMenuItem(THREEDTWOZSAVE);
      //  i3D2Zsave.addItemListener(this);
      //  menu.add(i3D2Zsave);
	//end 3d submenu




		if(fullgui){ 
	        // the view
	        menu = new JMenu(VIEW);
	        add(menu);
	        i2Dsave = new JCheckBoxMenuItem(TWODSAVE);
	        i2Dsave.addItemListener(this);
	        menu.add(i2Dsave);	   
	        iViewEllipse = new JMenuItem(VIEWELLIPSE);
	        iViewEllipse.addActionListener(this);
	        menu.add(iViewEllipse);
		}
 
	//iZoomView = new JMenuItem(ZOOMVIEW);
        //iZoomView.addActionListener(this);
	// menu.add(iZoomView);
	
	//	iDepthViews = new JMenuItem(DEPTHVIEWS);
        //iDepthViews.addActionListener(this);
	// menu.add(iDepthViews);
	//iAnnotationProperties = new JMenuItem(ANNOTATIONPROPERTIES);
        //iAnnotationProperties.addActionListener(this);
        //menu.add(iAnnotationProperties);

		if(fullgui){
	        iAllCentroids = new JMenuItem(ALLCENTROIDS);
	        iAllCentroids.addActionListener(this);
	        menu.add(iAllCentroids);
	        iCellMovementImage = new JMenuItem(CELLMOVEMENT);
	        iCellMovementImage.addActionListener(this);
	        menu.add(iCellMovementImage);
	
	        // the help menu
	        menu = new JMenu(HELP);
	        add(menu);
	        iHelp = new JMenu(HELP);
	        iHelp.addActionListener(this);
	        menu.add(iHelp);
	        addAllHelpFiles();
	        iAbout = new JMenuItem(ABOUT);
	        iAbout.addActionListener(this);
	        menu.add(iAbout);
	        iNew = new JMenuItem(NEW);
	        iNew.addActionListener(this);
	        menu.add(iNew);
	        iTestWindow = new JMenuItem("TestWindow");
	        iTestWindow.addActionListener(this);
	        menu.add(iTestWindow);
	        //iDebugLog = new JMenuItem(DEBUGLOG);
	        //iDebugLog.addActionListener(this);
	        //menu.add(iDebugLog);
	        setEnabled(false);
	        iClearTree.setEnabled(false);
		}
    }

    private void addAllHelpFiles() {
        addToHelp("AceTreeDemo");
        addToHelp("ConfigHelp");
        addToHelp("Tools");
    }

    public void addToHelp(String helpFileName) {
        JMenuItem item = new JMenuItem(helpFileName);
        item.addActionListener(this);
        iHelp.add(item);
    }

    public void addToRecent(String configName) {
        JMenuItem item = new JMenuItem(configName);
        item.addActionListener(this);
        iRecent.add(item);
        JMenuItem item2 = new JMenuItem(configName);
        item2.addActionListener(this);
        iRemoveRecent.add(item2);
    }

    private void removeFromRecent(String itemString) {
        int k = iRecent.getItemCount();
        for (int i=0; i < k; i++) {
            JMenuItem menuItem = iRecent.getItem(i);
            if (menuItem.getText().equals(itemString)) {
                iRecent.remove(menuItem);
                break;
            }
        }
    }

    private String getManifestVersion() {
        URL url = AceTree.class.getResource("/org/rhwlab/help/messages/manifest.html");
        //System.out.println("url: " + url);
        InputStream istream = null;
        String s = "";
        try {
            istream = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(istream));
            while (br.ready()) {
                s = br.readLine();
                if (s.indexOf("Manifest-Version:") == 0) {
                    s = s.substring(17);
                    break;
                }
                System.out.println("read: " + s);
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "Version: " + s + C.NL;

    }


    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
	@SuppressWarnings("unused")
	public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (iRecent!=null&&iRecent.isMenuComponent((JMenuItem)o)) {
            String item = ((JMenuItem)e.getSource()).getText();
            iAceTree.restoreTree(item);
        }
        else if (iRemoveRecent!=null&&iRemoveRecent.isMenuComponent((JMenuItem)o)) {
            JMenuItem menuItem = (JMenuItem)e.getSource();
            String item = menuItem.getText();
            iRemoveRecent.remove(menuItem);
            removeFromRecent(item);
            iAceTree.removeRecent(item);
        }
        else if (iClearAll == o) {
            iRecent.removeAll();
            iRemoveRecent.removeAll();
            iAceTree.clearAll();
        }
        else if (iBuildTree == o) {
            iAceTree.buildTree(false);
            iClearTree.setEnabled(true);
        }
        else if (iReBuildTree == o) {
            iAceTree.clearTree();
            iAceTree.buildTree(true);
            //iAceTree.buildTree(true);
            iClearTree.setEnabled(true);
        }
        else if (iClearTree == o) {
            iAceTree.clearTree();
        }
        else if (iExpandTree == o) {
            iAceTree.expandTree();
        }else if (iQuickOpen == o) {
            iAceTree.quickOpen();

        }else if (iOpenFromDB == o) {
            new OpenFromDB(iAceTree);

        } else if(iOpen == o) {
		    ExampleFileFilter filter = new ExampleFileFilter();
		    filter.addExtension("xml");
		    filter.addExtension("dat");
		    filter.setDescription("Config Files");

		    iFileChooser.setFileFilter(filter);
            iFileChooser.setSelectedFile(new File("."));
            int returnVal = iFileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = iFileChooser.getSelectedFile();
                String path = file.getPath();

                iAceTree.setConfigFileName(path);
                iAceTree.bringUpSeriesUI(path);
                //iAceTree.setConfigFileName(file.getName());
                //boolean haveConfig = iAceTree.getStartingParms();
                //if (haveConfig) {
                //    iAceTree.readNuclei();
                //    setEnabled(true);
                //}

            } else {
                //System.out.println("Open command cancelled by user.");
            }
        } 
        else if (iBookmark == o) {
        	iAceTree.bookmarkTool();
        } 
        else if (iViewImgWin == o) {
        	iAceTree.showImageWindow();
        }
        else if (iOpenSeries == o) {
            iFileChooser.setSelectedFile(new File(""));
            int returnVal = iFileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = iFileChooser.getSelectedFile();
                String path = file.getPath();
                iAceTree.openSeveralConfigs(path);
                //iAceTree.setConfigFileName(file.getPath());
                //iAceTree.bringUpSeriesUI(path);
            }

        } else if (iOptions == o) {
            options();

        } else if(iSave == o) {
		    ExampleFileFilter filter = new ExampleFileFilter();
		    filter.addExtension("zip");
		    filter.setDescription("Annotation Files");
	
		    iFileChooser.setFileFilter(filter);

            JFileChooser fileChooser = iFileChooser; //new JFileChooser(".");
            Config config = iAceTree.getNucleiMgr().getConfig();
            String s = config.iConfigFileName;
            println("AceMenuBar.actionListener: " + s);
            String ss = new File(s).getParent();
            fileChooser.setCurrentDirectory(new File(ss));
            fileChooser.setSelectedFile(new File(""));
            int returnVal = fileChooser.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
            	// Add .zip extension to file if user hasn't done so already
                File file = new File(fileChooser.getSelectedFile()+"");
                String path = file.getPath();
                if (path.lastIndexOf(".") != -1) {
	                String extension = path.substring(path.lastIndexOf("."),path.length());
	                if (!extension.equals(".zip"))
	                	file = new File(fileChooser.getSelectedFile()+".zip");
                }
                else {
                	file = new File(fileChooser.getSelectedFile()+".zip");
                }
                
                iAceTree.saveNuclei(file);
            } else {
                    System.out.println("Save command cancelled by user.");
            }
            //fileChooser.setFileFilter(new MyFilter("*.dat"));
        } else if (iSaveConfig == o) {
            Config config = iAceTree.getNucleiMgr().getConfig();
            config.saveConfigXMLFile();
	} else if(iJLaunch ==o){
	    //launch imagej
	    ImageJ ijin = new ImageJ();// ij.Main.launch();
	    //if image window exists open the current slice
	    if  (iAceTree.getImageWindow()!=null){ 
		System.out.println("loading image "+iAceTree.getImageWindow().getCurrentImageName());
		ImagePlus imp=ij.IJ.openImage(iAceTree.getImageWindow().getCurrentImageName());
		imp.show();
		//FileInfo fi = new FileInfo();
		//fi.fileName = iAceTree.getImageWindow().getCurrentImageName();
		//FileOpener test=new FileOpener(fi);
		//	test.open();
	    }
        } else if (iExit == o) {
            iAceTree.exit();

        } else if (iEdit == o) {
            iAceTree.editImage3();
        } else if (iEdit2 == o) {
            iAceTree.editTools();
        } else if (iShowLog == o) {
            iAceTree.getEditLog().showMe();
        } else if (iDDLog == o) {
            new DeathAndDivisionLog(iAceTree, "DeathsAndDivisions");
            //iAceTree.showDivisionsAndDeaths();
        } else if (iAnalyze == o) {
            new Analysis(iAceTree, "Overviews");
        } else if (iAnalyze2 == o) {
            try {
                new Analysis2("Analysis Development");
            } catch(NoClassDefFoundError nce) {
                new AceTreeHelp("/org/rhwlab/help/messages/SGTerror.html", 400, 200);
            }
        } else if (iAnalyze3 == o) {
            new Analysis3("Analysis3");
        } else if (iAnalyze4 == o) {
            new Analysis4("Analysis4");
        } else if (iAnalyze5 == o) {
            new Analysis5("Analysis5");
        } else if (iAnalyze6 == o) {
            new Analysis6("Analysis6");
        } else if (iExtractRed == o) {
            new ExtractRed("ExtractRed");
        } else if (iFixCrosstalk == o) {
            new RedCorrector2();
        } else if (iRedBkgComp == o) {
            new RedBkgComp();
        } else if (iRedCompSeries == o) {
            new RedCompSeries("Red comp series");
        } else if (iAnalyze8 == o) {
            new Analysis8("Analysis8");
        } else if (iAnalyze9 == o) {
            new Analysis9("Analysis9");
        } else if (iAnalyze10 == o) {
            new Analysis11("Analysis11");
        } else if (iUsePlugin == o) {
            new UsePlugin();
        } else if (iEditTraverse == o) {
            iAceTree.editTraverse();
        } else if (iAdjacencies == o) {
        	iAceTree.showDeathsAdjacencies();
            //new DeathsAdjacencies("Deaths and Adjacencies Dialog");
        } else if (iLazarus == o) {
        	iAceTree.showLazarus();
            //new Lazarus();
        } else if (iSiamese == o) {
        	iAceTree.showSiamese();
            //new Siamese("Siamese");
        } else if (iJuvenesence == o) {
        	iAceTree.showJuvenesence();
            //new Juvenesence();
        } else if (iZafer1 == o) {
            iAceTree.showZafer1();
        } else if (iOrientation == o) {
            //new Orientation();
            iAceTree.showOrientation();
        } else if (iOverlaps == o) {
            new Overlaps();
        } else if (iViewNuclei == o) {
            iAceTree.viewNuclei();
        } else if (iRelink == o) {
            iAceTree.relinkNucleus();
        } else if (iSkipFalseNegatives == o) {
            new SkipFalseNegatives();
        } else if (iKillCells == o) {
            iAceTree.killCells();
	    // } else if (iKillDeepNucs == o) {
	    //  iAceTree.killDeepNucs();
        } else if (iSetEndTime == o) {
            iAceTree.setEndTime();
        } else if (iIncrementEndTime == o) {
            iAceTree.incrementEndTime();
        } else if (iUndo == o) {
            iAceTree.undo();
        } else if (iAncestralTree == o) {
            iAceTree.ancestral();
        } else if (iSulstonTree == o) {
            iAceTree.canonical();
        } else if (iVTree == o) {
            iAceTree.vtree();
        } else if (iSubTrees == o) {
            new SubTrees();
        } else if (iATVTree == o) {
            iAceTree.exportNewick();
        } else if (iTest == o) {
            iAceTree.test();
        }else if (i3D3 == o) {
        	WormGUIDESWindow view = new WormGUIDESWindow(iAceTree.getNucleiMgr());
        	view.initializeWormGUIDES();
        } else if (i3D2Z == o) {
            
	    //}else if (iAnnotationProperties ==o){
	    //iAceTree.getImageWindow().launchImageParamsDialog();
        } else if (iViewEllipse == o) {
            new EllipseViewer();
        } else if (iDepthViews == o) {
            new DepthViews("");
        } else if (iAllCentroids == o) {
            iAceTree.allCentroidsView();
        } else if (iCellMovementImage == o) {
            iAceTree.cellMovementImage();
            //int time = iAceTree.getImageTime();
            //int timeinc = iAceTree.getTimeInc();
            //int plane = iAceTree.getImagePlane();
            //new CellMovementImage(time + timeinc, plane);
        } else if (iDebugLog == o) {
            iDLog.showMe();
        } else if (iAbout == o) {
            //String message = "Version 0.5\n";
            String message = getManifestVersion();
            message += "Copyright 2005, Genome Sciences\n";
            message += "University of Washington\n";
            message += "biowolp@u.washington.edu";
            JOptionPane pane = new JOptionPane(message);
            //pane.set.Xxxx(...); // Configure
            JDialog dialog = pane.createDialog(iAceTree, "About AceTree");
            dialog.setVisible(true);
        } else if (iHelp!=null&&iHelp.isMenuComponent((JMenuItem)o)) {
            String item = ((JMenuItem)e.getSource()).getText();
            item = "/org/rhwlab/help/html/" + item + ".html";
            new AceTreeHelp(item);
        } else if (iNew == o) {
            new AceTreeHelp("/org/rhwlab/help/html/NewFeatures.html");
        } else if (iTestWindow == o) {
        	iAceTree.testWindow();
        }

        //System.out.println("AceMenuBar.actionPerformed exiting");

    }

    @Override
	public void itemStateChanged(ItemEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        if (source == i3Dsave) {
            //i3DsaveState = e.getStateChange();
            //iAceTree.image3DSave(i3Dsave.getState());
        } else if (source == i2Dsave) {
            iAceTree.image2DSave(i2Dsave.getState());
        }
    }

    public void resetSaveState() {
        println("resetSaveState: RESETTING!");
        i2Dsave.setState(false);
        i3Dsave.setState(false);
        i3D2save.setState(false);
    }


    @Override
	public void setEnabled(boolean enabled) {
        //System.out.println("AceMenuBar.setEnabled: " + enabled);
        //new Throwable().printStackTrace();
	if (iAceTree.getFullGUI()){
		iBuildTree.setEnabled(enabled);
		iEdit.setEnabled(enabled);}

        iSave.setEnabled(enabled);
   
    }

    public void setClearEnabled(boolean enabled) {
	if (iAceTree.getFullGUI()){
        iClearTree.setEnabled(enabled);
	}
    }

    public void setEditEnabled(boolean enabled) {
        //System.out.println("AceMenuBar.setEditEnabled: " + enabled);
	if (iAceTree.getFullGUI()){
        iEdit.setEnabled(enabled);
	}
    }

    private void options() {
        //System.out.println("AceMenuBar.options");
        new Options();
    }


    final private static String
         FILE = "File"
        ,QUICKOPEN = "Quick open (no config file needed)"
        ,OPEN = "Open config file"
        ,OPENSERIES = "Open series"
        ,CLEARALL = "Clear all"
        ,OPTIONS = "Options"
        ,SAVE = "Save nuclei as zip"
        ,SAVECONFIG = "Save config file"
	,IJ = "Launch ImageJ"
        ,EXIT = "Exit"
        ,EXPORT = "Export newick lineage"
        ,ACTION = "Action"
        ,BUILDTREE = "Build Tree"
        ,REBUILDTREE = "Rebuild Tree"
        ,CLEAR = "Clear tree"
        ,EXPAND = "Expand tree"
        ,TEST = "Test"
        ,TEST1 = "test1"
        ,TREES = "Trees"
        ,ATVTREE = "Save tree as Newick file"
        ,SULSTON = "Interactive Lineage"
        ,VTREE = "Lineage Plot"
        ,CANONICAL = "Sulston tree"
        ,SHOW = "Show"
        ,NUCLEI = "nuclei"
        ,EDIT = "Edit"
        ,EDITTOOLS = "Edit tools"

        ,SHOWLOG = "Show log"
        ,DDLOG = "Div/death log"
        ,ANALYZE = "Analyze"
        ,ANALYSISDEVELOPMENT = "Nuclei rotation"
        ,ANALYSIS3 = "Analysis3"
        ,ANALYSIS4 = "Identity data"
        ,ANALYSIS5 = "Analysis5"
        ,ANALYSIS6 = "Analysis6"
        ,ANALYSIS7 = "Extract red"
        ,REDBKGCOMP = "Red Bkg Comp"
        ,REDCOMPSERIES = "Red Comp Series"
        ,ANALYSIS8 = "Analysis8"
        ,ANALYSIS9 = "Red expression"
        ,ANALYSIS10 = "Imaging test"
        ,EDITTRAVERSE = "Traverse Divisions"
        ,ADJACENCIES = "Deaths/Adjacencies"
        ,USEPLUGIN = "Use Plugin"
        ,DOEDIT = "Edit nuclei"
        ,DOEDIT2 = "Edit nuclei test"
        ,VIEWNUCLEI = "View nuclei"
        ,RELINK = "Relink nuclei"
        ,SKIPFALSENEGATIVES = "Skip false negatives"
        ,KILLCELL = "Kill cell"
        ,KILLCELLS = "Kill cells"
        ,KILLDEEPNUCS = "Kill deep nucs"
        ,SETENDTIME = "Set end time"
        ,INCREMENTENDTIME = "Increment end time"
        ,UNDO = "Undo"
        ,VIEW = "View"
        ,THREED = "3D old View"
        ,THREED3 = "3D Sister View"
        ,THREEDSAVE = "Save 3D old"
        ,THREEDTWOSAVE = "Save 3D View"
        ,THREEDTWOZSAVE = "Save 3D Sister View"
        ,TWODSAVE = "Save 2D"
        ,ANNOTATIONPROPERTIES= "Annotation Properties"
        ,VIEWELLIPSE = "View ellipse"
        ,DEPTHVIEWS = "Depth views"
        ,ALLCENTROIDS = "All Centroids"
        ,CELLMOVEMENT = "Cell Movement"
        ,DEBUGLOG = "Debug Log"
        ,HELP = "Help"
        ,ABOUT = "About"
        ,NEW = "New features"
        ,ZOOMVIEW = "Zoom View"
        ,THREED4 = "WormGUIDES"

        ;

    public static void main(String[] args) {
    }
    private static void println(String s) {System.out.println(s);}

}
