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
public interface Configuration {

    /**
     * See http://webmachine.basho.com/streambody.html.
     *
     * @param sizeInBytes
     */
    void set_max_recv_body(int sizeInBytes);
}
