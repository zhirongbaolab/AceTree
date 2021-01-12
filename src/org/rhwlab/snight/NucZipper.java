/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */
package org.rhwlab.snight;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.nio.file.Files;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.rhwlab.utils.EUtils;
import org.rhwlab.help.AceTreeHelp;


/**
 * takes the current contents of the NucleiMgr and prepares a zip
 * file with a structure like the nuclei portion of a normal input file
 * <br> this can be called following an edit
 * @author biowolp
 * @version 1.0 Feb 15, 2005
 */
public class NucZipper {
    /**
     * all the work is done in the constructor
     * @param file File object to store into (only the name is used)
     * @param nucleiMgr NucleiMgr object which has the nuclei data
     */
    public NucZipper(File file, NucleiMgr nucleiMgr, Config config) {
    	File targetFile = file;
    	String parent = file.getParent();
    	File tempFile = new File(parent + "/temp.zip");
    	// if temp.zip already exists, then tag on a numerical extension
    	String filePath = tempFile.getPath();
    	while (tempFile.exists()) {
    		filePath = addExtNumber(filePath);
    		tempFile = new File(filePath);
    	}
    	String nucDir = "";
    	if (config != null) {
            nucDir = config.getNucleiConfig().getZipNucDir();
        } else {
    	    nucDir = "nuclei/";
        }

        try {
        	
            FileOutputStream fos = new FileOutputStream(tempFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            Vector nuclei = nucleiMgr.getNucleiRecord();
            
            // Find first entry of nonempty sub-vector starting from last index
            int lastEmptyIndex = nuclei.size()-1;
            for (int i = lastEmptyIndex; i >= 0; i--) {
            	Vector currentVec = (Vector)nuclei.elementAt(i);
            	if (!currentVec.isEmpty()) {
            		lastEmptyIndex = i+1;
            		break;
            	}
            }
            System.out.println("Last non-empty index in nuclei_record: "+lastEmptyIndex);
            
            // given long save time I'm changing behavior to use actual last loaded nucleus
            // for (int i=0; i < nuclei.size(); i++) {
            
        	for (int i = 0; i < lastEmptyIndex; i++) {
            	// I'm not sure why the above was replaced with the below
            	// but I'm putting it back in an attempt to iteratively edit 
            	//large data files (which requires them to be saved out fully	
        		// for (int i=0; i <nucleiMgr.getiEndingIndex(); i++) {
                String ename = nucDir + "t" + EUtils.makePaddedInt(i + 1) + "-nuclei";
                zos.putNextEntry(new ZipEntry(ename));
                String s;
                for (int j=0; j < ((Vector)nuclei.elementAt(i)).size(); j++) {
                    Nucleus n = (Nucleus)((Vector)nuclei.elementAt(i)).elementAt(j);
                    s = formatNucleus(j, n);
                    byte [] b = s.getBytes();
                    zos.write(b, 0, b.length);
                }
                zos.closeEntry();
            }
            addParameters(zos, nucleiMgr);
            zos.close();
            ChannelCopy.transferCopy(tempFile, targetFile);
            //Files.deleteIfExists(tempFile.toPath());
            try {
            	tempFile.delete();
            } catch (Exception e) {
            	System.out.println("Failed to delete temp file.");
            }
        } catch(IOException ioe) {
            System.out.println("NucZipper exception: " + ioe);
            new AceTreeHelp("/org/rhwlab/help/messages/PermissionError.html", 200, 200);
        }
    }
    
    private String addExtNumber(String path) {
    	// Find indices of parenthesis
    	int left = path.indexOf("(");
    	int right = path.indexOf(").zip");
    	if (left < right) {
    		String between = path.substring(left+1, right);
    		try {
    			// See if extension exists already
    			int ext = Integer.parseInt(between);
    			ext++;
    			path = path.substring(0, left)+"("+ext+").zip";
    		}
    		catch (Exception e) {
    			// The string between the parenthesis is not an extension number
    			// Tag on extension to end of path name
    			path = path.substring(0, path.length()-4)+"(1).zip";
    			return path;
    		}
    	}
    	else
    		path = path.substring(0, path.length()-4)+"(1).zip";
		return path;
    }

    @SuppressWarnings("unused")
	private void addParameters(ZipOutputStream zos, NucleiMgr nucleiMgr)
                    throws IOException {
        ZipNuclei zn = nucleiMgr.getZipNuclei();
        String ename = nucleiMgr.getParameterEntry();
        zos.putNextEntry(new ZipEntry(ename));
        Vector parameterFileInfo = nucleiMgr.getParameterFileInfo();
        String s = null;
        if (parameterFileInfo != null) {
            for (int i=0; i < parameterFileInfo.size(); i++) {
                s = (String)parameterFileInfo.elementAt(i);
                s += "\n";
                byte [] b = s.getBytes();
                zos.write(b, 0, b.length);
            }
        }
        zos.closeEntry();
    }


    private String formatNucleus(int j, Nucleus n) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf(j + 1) + CS);
        int status = 0;
        if (n.status > 0) status = 1;
        sb.append(String.valueOf(status) + CS);
        sb.append(String.valueOf(n.predecessor) + CS);
        sb.append(String.valueOf(n.successor1) + CS);
        sb.append(String.valueOf(n.successor2) + CS);
        sb.append(String.valueOf(n.x) + CS);
        sb.append(String.valueOf(n.y) + CS);
        sb.append(String.valueOf(n.z) + CS);
        sb.append(String.valueOf(n.size) + CS);
        sb.append(n.identity + CS);
        sb.append(String.valueOf(n.weight) + CS);
        sb.append(String.valueOf(n.rweight) + CS);
        sb.append(String.valueOf(n.rsum) + CS);
        sb.append(String.valueOf(n.rcount) + CS);
        sb.append(n.assignedID + CS);
        sb.append(String.valueOf(n.rwraw) + CS);
        sb.append(String.valueOf(n.rwcorr1) + CS);
        sb.append(String.valueOf(n.rwcorr2) + CS);
        sb.append(String.valueOf(n.rwcorr3) + CS);
        sb.append(String.valueOf(n.rwcorr4) + CS);
        sb.append("\n");

        return sb.toString();
    }

     /**
     * unused main function
     * @param args String []
     */
    public static void main(String[] args) {
    }

    private static void println(String s) {System.out.println(s);}
    private static void print(String s) {System.out.print(s);}
    private static final String CS = ", ", C = ",";

}
