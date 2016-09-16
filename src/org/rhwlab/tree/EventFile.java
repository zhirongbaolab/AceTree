package org.rhwlab.tree;

/*
 * Created on Jan 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
//package treetest;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Vector;

public class EventFile {
    Vector iEvt;
    int iNext;
    String iEvtFile;

    /**
     * 
     */
    public EventFile(String fileName) {
        super();
        // TODO Auto-generated constructor stub
        iEvt = new Vector();
        iNext = 0;
        iEvtFile = fileName;
        getEvents();
    }

    @SuppressWarnings("unused")
	public void getEvents() {
        iEvt = new Vector();
        try {
            //System.out.println(iEvtFile);
            InputStream rstream = EventFile.class.getResourceAsStream(iEvtFile);
            //InputStream rstream = new FileInputStream(iEvtFile);
            //System.out.println("InputStream = " + rstream);
            BufferedReader in = new BufferedReader(new InputStreamReader(rstream));
            in.readLine(); // discard header 
            Vector v = new Vector();
            while(in.ready()) {
                String x = in.readLine();
                //System.out.println(x);
                iEvt.add(0,x);
            }
            in.close();
            rstream.close();
        } catch(Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
    
    @SuppressWarnings("unused")
	public String [] getNextEvent() throws ArrayIndexOutOfBoundsException {
        String x = (String)iEvt.elementAt(iNext++);
        StringTokenizer st = new StringTokenizer(x, "\t" );
        StringBuffer sb = new StringBuffer();
        int m = st.countTokens();
        String [] s = new String[m];
        int i = 0;
        while (st.hasMoreTokens()) {
            s[i++] = st.nextToken();
        }
        return s;
    }
    
    public static void main(String[] args) {
    }
}
