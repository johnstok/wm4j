/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 * Modified by   $Author$
 * Modified on   $Date$
 *
 * Changes: see version control.
 *-----------------------------------------------------------------------------
 */
package wm;


/**
 * Supported content encodings.
 *
 * @author Keith Webster Johnston.
 */
public final class ContentEncoding {

    /** IDENTITY : String. */
    public static final String IDENTITY = "identity";              //$NON-NLS-1$

    /** GZIP : String. */
    public static final String GZIP     = "gzip";                  //$NON-NLS-1$

    /** ANY : String. */
    public static final String ANY      = "*";                     //$NON-NLS-1$


    private ContentEncoding() { super(); }
}
