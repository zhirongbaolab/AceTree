package org.rhwlab.snight;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ChannelCopy {

    public static void transferCopy(String src, String dst) throws IOException {
    	//println("ChannelCopy, src, " + src);
    	//println("ChannelCopy, dst, " + dst);
        // get channels
        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(dst);
        FileChannel fcin = fis.getChannel();
        FileChannel fcout = fos.getChannel();

        // do the file copy
        fcin.transferTo(0, fcin.size(), fcout);

        // finish up
        fcin.close();
        fcout.close();
        fis.close();
        fos.close();
    }


    public static void transferCopy(File src, File dst) throws IOException {
    	//println("ChannelCopy, src, " + src);
    	//println("ChannelCopy, dst, " + dst);
        // get channels
        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(dst);
        FileChannel fcin = fis.getChannel();
        FileChannel fcout = fos.getChannel();

        // do the file copy
        fcin.transferTo(0, fcin.size(), fcout);

        // finish up
        fcin.close();
        fcout.close();
        fis.close();
        fos.close();
    }


    public static void main(String[] args) {
    	String src = "/net/waterston/vol1/annots/murray/20090217_eor-1_9G1_13_L2/views/20090217_eor-1_9G1_13_L2.jpg";
    	String dst = "/net/waterston/vol2/home/biowolp/website/htdocs/thumbs/20090217_eor-1_9G1_13_L2.jpg";
        println("ChannelCopy.main, " + src + CS + dst);
        try {
            transferCopy(src, dst);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

    }

    private static void println(String s) {System.out.println(s);}
    private static void print(String s) {System.out.print(s);}
    private static final String CS = ", ", SP = " ";
    private static final DecimalFormat DF0 = new DecimalFormat("####");
    private static final DecimalFormat DF1 = new DecimalFormat("####.#");
    private static final DecimalFormat DF4 = new DecimalFormat("####.####");
    private static String fmt1(double d) {return DF1.format(d);}
    private static String fmt0(double d) {return DF1.format(d);}

}
