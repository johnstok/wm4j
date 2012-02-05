/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;


/**
 * A client HTTP error in the 4xx range.
 *
 * @author Keith Webster Johnston.
 */
public class ClientHttpException extends HttpException {

    /**
     * Constructor.
     *
     * @param status The HTTP status for this error.
     * @param t      The cause of this exception.
     */
    public ClientHttpException(final Status status, final Throwable t) {
        super(status, t);
    }

    /**
     * Constructor.
     *
     * @param status The HTTP status for this error.
     */
    public ClientHttpException(final Status status) {
        super(status);
    }
}
