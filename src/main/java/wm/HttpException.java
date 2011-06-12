/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;


/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public class HttpException extends Exception {

    /**
     * Constructor.
     */
    public HttpException() {
        super();
    }

    /**
     * Constructor.
     *
     * @param arg0
     * @param arg1
     */
    public HttpException(final String arg0, final Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Constructor.
     *
     * @param arg0
     */
    public HttpException(final String arg0) {
        super(arg0);
    }

    /**
     * Constructor.
     *
     * @param arg0
     */
    public HttpException(final Throwable arg0) {
        super(arg0);
    }
}
