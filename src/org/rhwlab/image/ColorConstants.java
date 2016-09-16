/*
 *      ColorConstants.java 1.0 98/11/23
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package org.rhwlab.image;
import java.awt.Color;

import javax.vecmath.*;

public class ColorConstants{
	public static final Color3f red     = new Color3f(0.7f, .10f, .10f);
	public static final Color3f green   = new Color3f(0.0f, 1.0f, 0.0f);
	public static final Color3f blue    = new Color3f(0.0f, 0.0f, 1.0f);
    public static final Color3f yellow  = new Color3f(1.0f, 1.0f, 0.0f);
    public static final Color3f orange  = new Color3f(1.0f, 0.5f, 0.0f);
	public static final Color3f cyan    = new Color3f(0.0f, 1.0f, 1.0f);
	public static final Color3f magenta = new Color3f(1.0f, 0.0f, 1.0f);
	public static final Color3f white   = new Color3f(1.0f, 1.0f, 1.0f);
    public static final Color3f black   = new Color3f(0.0f, 0.0f, 0.0f);
    public static final Color3f gray    = new Color3f(0.3f, 0.3f, 0.3f);
    public static final Color3f pink    = new Color3f(((float)Color.pink.getRed())/256,
                    								((float)Color.pink.getGreen())/256,
                    								((float)Color.pink.getBlue())/256);
    public static final Color3f lightgray = new Color3f(0.65f, 0.65f, 0.65f);
    public static final Color3f darkgray = new Color3f(0.17f, 0.17f, 0.17f);
}

