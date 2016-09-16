/*
 * Created on Sep 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

//import ij.io.PluginClassLoader;
//import ij.plugin.PlugIn;
import ij.IJ;

import java.io.File;

import javax.swing.JFileChooser;

import org.rhwlab.acetree.AceTree;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UsePlugin {

    //private static com.sun.tools.javac.Main javac; 
    // Removed this 7/28/13, windows compiler errors J.Cai
    
    AceTree         iAceTree;

    
    public UsePlugin() {
        initialize();
        getFile();
    }
    
    public void initialize() {
        iAceTree = AceTree.getAceTree(null);
    }
    
    @SuppressWarnings("unused")
	private void getFile() {
        JFileChooser fileChooser = new JFileChooser(new File("."));
        int returnVal = fileChooser.showOpenDialog(iAceTree);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getPath();
            //System.out.println(path);
            String parent = file.getParent();
            String name = file.getName();
            name = name.substring(0, name.indexOf("."));
            //System.out.println(name);
            //System.out.println(parent);
          
            // remmed out two lines below in effort to change behavior to just run class instead of 
            //int result = compile(path);
            
            //if (result == 0) 
            	runUserPlugin(parent, name);
        }

    }
    
    @SuppressWarnings("unused")
	int compile(String path) {
        //javac = new com.sun.tools.javac.Main();    
        System.out.println("path: " + path);
        String classpath = System.getProperty("java.class.path");
        File f = new File(path);
        if (f!=null)  // add directory containing file to classpath
            classpath += File.pathSeparator + f.getParent();
        //int compiled = Main.compile(new String[] {"-deprecation", "-classpath", classpath, path});
        //int compiled = javac.compile(new String[] {"-deprecation", "-classpath", classpath, path});

  //      boolean errors = (compiled > 0);
  //      if (errors)
            System.out.println("compile failed");
  //      else
  //          System.out.println("compile succeeded");
        return 0;
        // Remmed this out 7/28/13, windows compiler errors J.Cai
     }

    Object runUserPlugin(String pluginsDir, String className) {
        PluginClassLoader loader;
        boolean createNewLoader = true;
        PluginClassLoader classLoader = null;
        if (createNewLoader)
            loader = new PluginClassLoader(pluginsDir);
        else {
            if (classLoader==null)
                classLoader = new PluginClassLoader(pluginsDir);
            loader = classLoader;
        }
        Object thePlugIn = null;
        try {
            thePlugIn = (loader.loadClass(className)).newInstance();
            //if (thePlugIn instanceof PlugIn)
            ((Runnable)thePlugIn).run();
            //else if (thePlugIn instanceof PlugInFilter)
            //    runFilterPlugIn(thePlugIn, commandName, arg);
        }
        catch (ClassNotFoundException e) {
            if (className.indexOf('_')!=-1)
                IJ.error("Plugin not found: "+className);
        }
        catch (InstantiationException e) {IJ.error("Unable to load plugin (ins)");}
        catch (IllegalAccessException e) {IJ.error("Unable to load plugin, possibly \nbecause it is not public.");}
        return thePlugIn;
        
    }
    

}
