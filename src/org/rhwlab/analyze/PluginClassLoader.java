package org.rhwlab.analyze;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;


public class PluginClassLoader extends ClassLoader {
    protected String path;
    protected Hashtable cache = new Hashtable();
    protected Vector jarFiles;

    public PluginClassLoader(String path) {
        this.path = path;
        /*
        jarFiles = new Vector();
        //find all JAR files on the path and subdirectories
        File f = new File(path);
        String[] list = f.list();
        if (list==null)
            return;
        for (int i=0; i<list.length; i++) {
            f=new File(path, list[i]);
            if (f.isDirectory()) {
                String[] innerlist = f.list();
                if (innerlist==null) continue;
                for (int j=0; j<innerlist.length; j++) {
                    File g = new File(f,innerlist[j]);
                    if (g.isFile()) addJAR(g);
                }
            } else
                addJAR(f);
        }
        */
    }
    
    /**
     * Returns a Class from the path or JAR files. Classes are automatically resolved.
     * @param className a class name without the .class extension.
     */
    @Override
	public Class loadClass(String className) throws ClassNotFoundException {
        return (loadClass(className, true));
    }

    /**
     * Returns a Class from the path or JAR files. Classes are resolved if resolveIt is true.
     * @param className a String class name without the .class extension.
     *        resolveIt a boolean (should almost always be true)
     */
    @Override
	public synchronized Class loadClass(String className, boolean resolveIt) throws ClassNotFoundException {

        Class   result;
        byte[]  classBytes;

        // try the local cache of classes
        result = (Class)cache.get(className);
        if (result != null) {
            return result;
        }

        // try the system class loader
        try {
            result = super.findSystemClass(className);
            return result;
        }
        catch (Exception e) {}

        // Try to load it from plugins directory
        System.out.println("loading from plugins directory");
        classBytes = loadClassBytes(className);
		//IJ.log("loadClass: "+ className + "  "+ (classBytes!=null?""+classBytes.length:"null"));
		if (classBytes==null) {
			System.out.println("ClassNotFoundException");
			throw new ClassNotFoundException(className);
		}

        // Define it (parse the class file)
        result = defineClass(className, classBytes, 0, classBytes.length);
        if (result == null) {
            throw new ClassFormatError();
        }

        //Resolve if necessary
        if (resolveIt) resolveClass(result);

        cache.put(className, result);
        return result;
    }

    /**
     * This does the actual work of loading the bytes from the disk. Returns an
     * array of bytes that will be defined as a Class. This should be overloaded to have
     * the Class Loader look in more places.
     * @param name a class name without the .class extension.
     */

    protected byte[] loadClassBytes(String name) {
        byte [] classBytes = null;
        classBytes = loadIt(path, name);
        /*
        if (classBytes == null) {
            classBytes = loadFromSubdirectory(path, name);
            if (classBytes == null) {
                // Attempt to get the class data from the JAR files.
                for (int i=0; i<jarFiles.size(); i++) {
                    try {
                        File jf = (File)jarFiles.elementAt(i);
                        classBytes = loadClassFromJar(jf.getPath(), name);
                        if (classBytes != null)
                            return classBytes;
                    }
                    catch (Exception e) {
                        //no problem, try the next one
                    }
                }
            }
        }
        */
        return classBytes;
    }

    // Loads the bytes from file
    private byte [] loadIt(String path, String classname) {
        String filename = classname.replace('.','/');
        filename += ".class";
        File fullname = new File(path, filename);
        //ij.IJ.write("loadIt: " + fullname);
        try { // read the byte codes
            InputStream is = new FileInputStream(fullname);
            int bufsize = (int)fullname.length();
            byte buf[] = new byte[bufsize];
            is.read(buf, 0, bufsize);
            is.close();
            return buf;
        } catch (Exception e) {
            return null;
        }
    }

}
