/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.io.IOException;
import java.nio.charset.Charset;
import org.simpleframework.http.Status;


/**
 * Responsibility: model the response to a HTTP request.
 *
 * @author Keith Webster Johnston.
 */
public interface Response {

    /**
     * Mutator.
     *
     * @param status The new status to set.
     */
    void setStatus(Status code);


    /**
     * Accessor.
     *
     * @return The current status of the response.
     */
    Status getStatus();


    /**
     * TODO: Add a description for this method.
     *
     * @param value
     * @param name
     */
    void setHeader(String name, String value);


    /**
     * TODO: Add a description for this method.
     *
     * @param string
     */
    String getHeader(final String name);


    /**
     * TODO: Add a description for this method.
     *
     * @param charset
     * @return
     */
    public String getBodyAsString(Charset charset);


    /**
     * TODO: Add a description for this method.
     *
     * @param value
     */
    void write(BodyWriter value) throws IOException;
}
