/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 * Created on Jan 10, 2005
 */
package org.rhwlab.image;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.io.TiffDecoder;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.rhwlab.utils.C;


/**
 * Handles reading of the tiff image files in the zip file under study.
 * <br>Called from AceTree when the user has requested a (new) image.
 * 
 * @author biowolp
 * @version 1.0 20050118
 *
 */
public class ZipImage {
    ZipFile iZipFile;

    /**
     * Constructor called with ZipFile which in this app was
     * obtained in ZipNuclei. Only one copy of the ZipFile object
     * is around.
     * <br>All accesses of this are confined to 
     * <code>Our_Tree3.makeImage</code>.
     */
    public ZipImage(ZipFile zipFile) {
        iZipFile = zipFile;
    }
    
    /**
     * Constructor called with string name of the tif zip file. Only one copy of the ZipFile object
     * is around.
     * <br>All accesses of this are confined to 
     * <code>Our_Tree3.makeImage</code>.
     */
    public ZipImage(String zipTifFilePath) {
        //System.out.println("ZipImage: " + zipTifFilePath);
        if (iZipFile == null) {
            try {
                iZipFile = new ZipFile(zipTifFilePath);
            } catch(IOException ioe) {
                //ioe.printStackTrace();
            }
        }
    }
    
    public ZipEntry getZipEntry(String entryName) {
        ZipEntry ze;
        if (iZipFile == null) return null;
        Enumeration entries = iZipFile.entries();
        // list the contents of each zipped entry
        while (entries.hasMoreElements()) {
          ze = (ZipEntry) entries.nextElement();
          //System.out.println(ze.getName());
          if (ze.getName().equals(entryName)) return ze;
        }
        return null;
    }

    @SuppressWarnings("unused")
	public ImagePlus readData(ZipEntry ze) {
        if (ze == null) return null;
        int byteCount;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        try {
            InputStream is = iZipFile.getInputStream(ze);
            byte data[] = new byte[DATA_BLOCK_SIZE];

            //  4. read source zipped data and write to uncompressed stream
            while ( (byteCount = is.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
                out.write(data, 0, byteCount);
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return openTiff(new ByteArrayInputStream(out.toByteArray()));

    }
    
    @SuppressWarnings("unused")
	public ImagePlus readData(ZipEntry ze, boolean asEightBit) {
        if (ze == null) return null;
        int byteCount;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        try {
            InputStream is = iZipFile.getInputStream(ze);
            byte data[] = new byte[DATA_BLOCK_SIZE];

            //  4. read source zipped data and write to uncompressed stream
            while ( (byteCount = is.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
                out.write(data, 0, byteCount);
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return openTiff(new ByteArrayInputStream(out.toByteArray()), asEightBit);
    }
    
    private ImagePlus openTiff(InputStream in, boolean asEightBit) {
        if (in == null) return null;
        FileInfo[] info = null;
        try {
            TiffDecoder td = new TiffDecoder(in, null);
            info = td.getTiffInfo();
        } catch (FileNotFoundException e) {
            IJ.error("TiffDecoder", "File not found: "+e.getMessage());
            return null;
        } catch (Exception e) {
            IJ.error("TiffDecoder", ""+e);
            return null;
        }
        ImagePlus imp = null;
        if (IJ.debugMode) // dump tiff tags
            IJ.log(info[0].info);
        FileOpener fo = new FileOpener(info[0]);
        imp = fo.open(false);
        // detect 8 bit or RGB from the FileInfo object info[0]
        //IJ.showStatus("");
        return imp;
    }
    /** Attempts to open the specified inputStream as a
    TIFF, returning an ImagePlus object if successful. */
    private ImagePlus openTiff(InputStream in) {
        if (in == null) return null;
        FileInfo[] info = null;
        try {
            TiffDecoder td = new TiffDecoder(in, null);
            info = td.getTiffInfo();
        } catch (FileNotFoundException e) {
            IJ.error("TiffDecoder", "File not found: "+e.getMessage());
            return null;
        } catch (Exception e) {
            IJ.error("TiffDecoder", ""+e);
            return null;
        }
        ImagePlus imp = null;
        if (IJ.debugMode) // dump tiff tags
            IJ.log(info[0].info);
        FileOpener fo = new FileOpener(info[0]);
        imp = fo.open(false);
        // detect 8 bit or RGB from the FileInfo object info[0]
        if (info[0].getBytesPerPixel() == 1) {
            imp = convertToRGB(imp);
        }
        //IJ.showStatus("");
        return imp;
    }
    
    

    /**
     * If the images in the zip archive are 8 bit tiffs,
     * we use that as the green plane of an RGB image processor
     * so the program is always showing RGB images
     * 
     * @param ip an Image processor obtained from the image file
     * @return
     */
    private ImagePlus convertToRGB(ImagePlus ip) {
        ImageProcessor iproc = ip.getProcessor();
        byte [] bpix = (byte [])iproc.getPixels();
        byte [] R = new byte[bpix.length];
        byte [] G = new byte[bpix.length];
        byte [] B = new byte[bpix.length];
        ColorProcessor iproc3 = new ColorProcessor(iproc.getWidth(), iproc.getHeight());
        iproc3.getRGB(R, G, B);
        G = bpix;
        R = getRedChannel(R);
        iproc3.setRGB(R, G, B);
        ip.setProcessor("test", iproc3);
        return ip;    
    }
    

    @SuppressWarnings("unused")
	private byte[] getRedChannel(byte [] R) {
        String fileName = makeRedChannelName();
        //println("getRedChannel: " + fileName);
        File f = new File(fileName);
        ZipFile zipImage = null;
        if (f.exists()) {
            FileInputStream fis;
            ImagePlus ip = null;
            
            try {
                zipImage = new ZipFile(fileName);
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
            //println("getRedChannel2: ");
            int k1 = fileName.lastIndexOf(C.Fileseparator) + 1;
            String ss = fileName.substring(k1);
            int k2 = ss.indexOf(".");
            ss = ss.substring(0, k2);
            ZipEntry ze = null;
            if (zipImage != null) ze = zipImage.getEntry(ss + ".tif");
            if (ze == null) {
                ip = new ImagePlus();
                //ImageProcessor iproc = new ColorProcessor(ImageWindow.cImageWidth, ImageWindow.cImageHeight);
                ip.setProcessor(fileName, null);
            }
            else {
                ip = readRedData(zipImage, ze);
            }
            if (ip != null) {
                ByteProcessor bproc = (ByteProcessor)ip.getProcessor();
                R = (byte [])bproc.getPixels();
            }
            
        }
        return R;
        
    }
    
    @SuppressWarnings("unused")
	public ImagePlus readRedData(ZipFile zipFile, ZipEntry ze) {
        if (ze == null) return null;
        int byteCount;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        try {
            InputStream is = zipFile.getInputStream(ze);
            byte data[] = new byte[DATA_BLOCK_SIZE];

            //  4. read source zipped data and write to uncompressed stream
            while ( (byteCount = is.read(data, 0, DATA_BLOCK_SIZE)) != -1) {
                out.write(data, 0, byteCount);
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return openRedTiff(new ByteArrayInputStream(out.toByteArray()));

    }
    
    private ImagePlus openRedTiff(InputStream in) {
        if (in == null) return null;
        FileInfo[] info = null;
        try {
            TiffDecoder td = new TiffDecoder(in, null);
            info = td.getTiffInfo();
        } catch (FileNotFoundException e) {
            IJ.error("TiffDecoder", "File not found: "+e.getMessage());
            return null;
        } catch (Exception e) {
            IJ.error("TiffDecoder", ""+e);
            return null;
        }
        ImagePlus imp = null;
        if (IJ.debugMode) // dump tiff tags
            IJ.log(info[0].info);
        FileOpener fo = new FileOpener(info[0]);
        imp = fo.open(false);
        return imp;
    }
    private static String makeRedChannelName() {
        //String s = ImageWindow.cCurrentImageFile;
        String s = "DISABLED";
        int k = s.indexOf(ImageWindow.cTifPrefix) + ImageWindow.cTifPrefix.length();
        
        
        s = ImageWindow.cZipTifFilePath + C.Fileseparator + ImageWindow.cTifPrefixR + s.substring(k);
        return s;
    }
    
    @Override
	protected void finalize() throws IOException {
        iZipFile.close();
    }
    
    public void xdelay(long delayTime) {
        long start = System.currentTimeMillis();
        long end = start + delayTime;
        while(System.currentTimeMillis() < end);
    }
    
    private static final int
         DATA_BLOCK_SIZE  = 2048
        ;
    
    
    public static void main(String[] args) throws Exception {
    }
    private static void println(String s) {System.out.println(s);}
}
