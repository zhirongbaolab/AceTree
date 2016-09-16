/*
 * SublineageDisplayProperty.java
 *
 * contains a String that is a name of a iName, and an
 * int which is an index of a iLineageNum in String[] Image3D2Config.LINEAGE_COLORS
 */

package org.rhwlab.image;

public class SublineageDisplayProperty {
    public String iName;
    public int    iLineageNum;

    public SublineageDisplayProperty(String iName, int iLineageNum) {
        this.iName = iName;
        this.iLineageNum = iLineageNum;
    }

    public String getName() {
    	return this.iName;
	}

    public int getLineageNum() { 
        return this.iLineageNum;
    } 
    
    public String getColor() {
        return Image3DViewConfig.LINEAGE_COLORS[this.iLineageNum]; 
    }

    public void setName(String iName) {
        this.iName = iName;
    }

    public void setLineageNum(int iLineageNum) {
        this.iLineageNum = iLineageNum;
    }
}


