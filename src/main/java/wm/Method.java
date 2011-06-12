/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * HTTP methods.
 *
 * @author Keith Webster Johnston.
 */
public final class Method {

    private static final Set<String> KNOWN_METHODS =
        new HashSet<String>(Arrays.asList(
            Method.GET,
            Method.HEAD,
            Method.OPTIONS,
            Method.DELETE,
            Method.POST,
            Method.PUT
        ));

    /** GET : String. */
    public static final String GET  = "GET";                       //$NON-NLS-1$

    /** HEAD : String. */
    public static final String HEAD = "HEAD";                      //$NON-NLS-1$

    /** OPTIONS : String. */
    public static final String OPTIONS = "OPTIONS";                //$NON-NLS-1$

    /** DELETE : String. */
    public static final String DELETE = "DELETE";                  //$NON-NLS-1$

    /** PUT : boolean. */
    public static final String PUT = "PUT";                        //$NON-NLS-1$

    /** POST : String. */
    public static final String POST = "POST";                      //$NON-NLS-1$


    private Method() { super(); }


    /**
     * All known HTTP methods.
     *
     * @return The set of known HTTP methods.
     */
    public static Set<String> all() { return KNOWN_METHODS; }
}
