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
 * API for writing the body of a response.
 *
 * @author Keith Webster Johnston.
 */
public interface BodyWriter {

    /**
     * Write the body to the supplied output stream.
     *
     * @param outputStream The output stream to write to.
     */
    void write(OutputStream outputStream) throws IOException;

}
