/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.io.IOException;
import java.io.InputStream;


/**
 * API for reading the body of a request.
 *
 * @author Keith Webster Johnston.
 */
public interface BodyReader {


    /**
     * Read the body from the supplied input stream.
     *
     * @param inputStream The input stream to read from.
     */
    void read(InputStream inputStream) throws IOException;
}
