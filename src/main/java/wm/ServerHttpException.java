/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;


/**
 * A server HTTP error in the 5xx range.
 *
 * @author Keith Webster Johnston.
 */
public class ServerHttpException
    extends
        HttpException {

    /**
     * Constructor.
     *
     * @param status The HTTP status for this error.
     * @param t      The cause of this exception.
     */
    public ServerHttpException(final Status status, final Throwable t) {
        super(status, t);
    }

    /**
     * Constructor.
     *
     * @param status The HTTP status for this error.
     */
    public ServerHttpException(final Status status) {
        super(status);
    }
}
