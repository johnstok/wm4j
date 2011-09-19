/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;


/**
 * Models a HTTP error.
 *
 * @author Keith Webster Johnston.
 */
public abstract class HttpException extends RuntimeException {

    private final Status _status;


    /**
     * Constructor.
     *
     * @param status The HTTP status for this error.
     * @param t      The cause of this exception.
     */
    public HttpException(final Status status, final Throwable t) {
        super(status.toString(), t);
        _status = status;
    }


    /**
     * Constructor.
     *
     * @param status The HTTP status for this error.
     */
    public HttpException(final Status status) {
        super(status.toString());
        _status = status;
    }


    /**
     * Accessor.
     *
     * @return Returns the status for this error.
     */
    public Status getStatus() {
        return _status;
    }
}
