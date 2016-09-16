package org.rhwlab.acetree;

import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class PartsList {

   static Hashtable<String, String> partslistHashTable;
   static Hashtable<String, String> partslistHashTableReverse;
   
   public PartsList() {
	   
	   partslistHashTable = new Hashtable<String, String>();
	   partslistHashTableReverse = new Hashtable<String, String>();
	   
	   /*
	    * parsing from external file
	    * Revised: August 30, 2016
	    * @author Braden Katzman
	    */
	   try {
		   URL url = PartsList.class.getResource("/org/rhwlab/acetree/partslist.txt");
		   InputStream input = url.openStream(); 
		   InputStreamReader isr = new InputStreamReader(input);
		   BufferedReader br = new BufferedReader(isr);
		   
		   String line;
		   while ((line = br.readLine()) != null) {
			   String[] lineArray = line.split("\t");
			   
			   if (lineArray.length == 3) {
				   partslistHashTable.put(lineArray[1].toLowerCase(), lineArray[0].toLowerCase());
				    partslistHashTableReverse.put(lineArray[0].toLowerCase(), lineArray[1]);
			   }
		   }
		   
	   } catch (IOException ioe) {
		   ioe.printStackTrace();
	   }

   }
   
    public static String lookupSulston(String sulstonName){
    	return partslistHashTable.get(sulstonName.toLowerCase());
    }
    
    public static String lookupProper(String properName){
    	return partslistHashTableReverse.get(properName.toLowerCase());
    }
}