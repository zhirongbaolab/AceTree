package org.rhwlab.acetree;

/*
 * Copyright 2005 University of Washington Genome Sciences
 * All rights reserved
 */
import org.rhwlab.acetree.AceTree;
import java.io.IOException;

/**
 * just a routine at the root level to start things
 * 
 * @author biowolp
 * @version 1.0 January 28, 2005
 * 
 */
public class AceTreeLauncher {

    public static void main(String[] args) throws IOException {
        System.out.println("AceTreeLauncher.main: " + args.length);
        AceTree.main(args);
    }
}
