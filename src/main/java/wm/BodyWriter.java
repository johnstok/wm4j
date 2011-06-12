/*-----------------------------------------------------------------------------

 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.io.IOException;
import java.io.OutputStream;


/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public interface BodyWriter {

    /**
     * TODO: Add a description for this method.
     *
     * @param outputStream
     */
    void write(OutputStream outputStream) throws IOException;

}
