/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;


/**
 * URI schemes supported by this server.
 *
 * @author Keith Webster Johnston.
 */
public enum Scheme {

    /** HTTP : Scheme. */
    http(false),

    /** HTTPS : Scheme. */
    https(true);

    private final boolean _confidential;


    /**
     * Constructor.
     *
     * @param confidential Is confidential data exchange possible.
     */
    private Scheme(final boolean confidential) {
        _confidential = confidential;
    }


    /**
     * Query if this scheme supports confidential exchange of data.
     *
     * @return True if confidential exchange is possible; false otherwise.
     */
    public boolean isConfidential() {
        return _confidential;
    }
}
