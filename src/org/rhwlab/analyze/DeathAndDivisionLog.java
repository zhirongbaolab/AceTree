/*
 * Created on May 26, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rhwlab.analyze;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;

import org.rhwlab.acetree.AceTree;
import org.rhwlab.snight.NucleiMgr;
import org.rhwlab.snight.Nucleus;
import org.rhwlab.tree.AncesTree;
import org.rhwlab.tree.Cell;
import org.rhwlab.utils.Log;

/**
 * @author biowolp
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeathAndDivisionLog extends Log {
    
    private AceTree iAceTree;
    AncesTree       iAncesTree;
    NucleiMgr       iNucleiMgr;
    Vector          nuclei_record;
    Cell            iRoot;
    Hashtable       iCellsByName;
    int             iImageTime;
    int             iTimeInc;
    int             iEndingIndex;

    public DeathAndDivisionLog(AceTree aceTree, String title) {
        super(title);
        iAceTree = aceTree;
        JButton jb = new JButton(CLEAR);
        addToolBarButton(jb);
        jb = new JButton(UPDATENOW);
        addToolBarButton(jb);
        initialize();
        showDivisionsAndDeaths();
        
    }
    
    public void initialize() {
        iAceTree = AceTree.getAceTree(null);
        iNucleiMgr = iAceTree.getNucleiMgr();
        nuclei_record = iNucleiMgr.getNucleiRecord();
        iAncesTree = iNucleiMgr.getAncesTree();
        iCellsByName = iAncesTree.getCellsByName();
        //iIdentity = iNucleiMgr.getIdentity();
        //iRoot = iNucleiMgr.getRoot();
        //iRoot = iAncesTree.getRootCells();
        iRoot = iAncesTree.getRoot();
    }


    public void showDivisionsAndDeaths() {
        //System.out.println("showDivisionsAndDeaths");
        iImageTime = iAceTree.getImageTime();
        iTimeInc = iAceTree.getTimeInc();
        iEndingIndex = iNucleiMgr.getEndingIndex();
        int now = iImageTime + iTimeInc;
        Vector nucleiNow = (Vector)iNucleiMgr.getNucleiRecord().elementAt(now - 1);
        Nucleus nnow = null;
        //System.out.println("time: " + now);
        this.append("time: " + now);
        for (int j=0; j < nucleiNow.size(); j++) {
            nnow = (Nucleus)nucleiNow.elementAt(j);
            if (nnow.status <= 0) continue;
            if (nnow.successor2 == -1) {
                if (nnow.successor1 == -1 && now != iEndingIndex) {
                    //System.out.println("showDivisions: " + nnow.identity + " death");
                    System.out.println(death(nnow.identity));
                    this.append(death(nnow.identity));
                }
            } else if (nnow.successor1 > 0) {
                //System.out.println("showDivisions: " + nnow.identity + " division " 
                //        + nnow.successor1 + CS + nnow.successor2);
                Vector nucleiNext = (Vector)iNucleiMgr.getNucleiRecord().elementAt(now);
                Nucleus a = (Nucleus)nucleiNext.elementAt(nnow.successor1 - 1);
                Nucleus p = (Nucleus)nucleiNext.elementAt(nnow.successor2 - 1);
                //System.out.println("showDivisions: " + nnow.identity + " division " 
                //        + nnow.successor1 + CS + nnow.successor2 + CS
                //        + a.identity + CS + p.identity);
                //System.out.println(division(nnow.identity, a.identity, p.identity));
                this.append(division(nnow.identity, a.identity, p.identity));
                
            }
            
            
        }
        if (!this.isShowing()) this.showMe();
    }
    
    public String division(String par, String dau, String dau2) {
        StringBuffer sb = new StringBuffer(DIVISIONTEMPLATE);
        sb.replace(PARLOC, PARLOC + par.length(), par);
        sb.replace(DAULOC, DAULOC + dau.length(), dau);
        sb.replace(DAULOC2, DAULOC2 + dau.length(), dau2);
        return sb.toString();
    }
    
    private String death(String cellName) {
        StringBuffer sb = new StringBuffer(DEATHTEMPLATE);
        sb.replace(DEATHLOC, DEATHLOC + cellName.length(), cellName);
        return sb.toString();
    }
    


    @Override
	public void actionPerformed(ActionEvent e) {
        //System.out.println("DeathAndDivisionLog.actionPerformed: " + e);
        String s = e.getActionCommand();
        if (s.equals(UPDATENOW)) {
            showDivisionsAndDeaths();
        } else if (s.equals(CLEAR)) {
            iText.setText("");
        } else {
            super.actionPerformed(e);
        }
        
    }

    private static final String
    DIVISIONTEMPLATE = "  Division:                 ->                  +                 "
   ,DEATHTEMPLATE = "  Death:                "
   ;

    private static final int
         DEAD = -1
        ,PARLOC = 12
        ,DAULOC = 31
        ,DAULOC2 = 50
        ,DEATHLOC = 9
        ;


    
    private static final String 
         UPDATENOW = "Update now"
        ,CLEAR = "Clear"

        ;

    public static void main(String[] args) {
    }
}
