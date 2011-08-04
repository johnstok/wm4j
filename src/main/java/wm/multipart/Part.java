/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 * Modified by   $Author$
 * Modified on   $Date$
 *
 * Changes: see version control.
 *-----------------------------------------------------------------------------
 */
package wm.multipart;

import java.util.HashMap;
import java.util.Map;


/**
 * A single part of a multipart message.
 *
 * @author Keith Webster Johnston.
 */
public class Part {

    private byte[]              _body    = new byte[0];
    private Map<String, String> _headers = new HashMap<String, String>();


    /**
     * Add a header for this part.
     *
     * @param key   The header's name.
     * @param value The header's value.
     */
    public void addHeader(final String key, final String value) {
        _headers.put(key, value);
    }


    /**
     * Accessor.
     *
     * @return Returns the body.
     */
    public byte[] getBody() {
        return _body;
    }


    /**
     * Accessor.
     *
     * @param key The header's key.
     *
     * @return Returns the header value.
     */
    public String getHeader(final String key) {
        return _headers.get(key);
    }


    /**
     * Accessor.
     *
     * @return Returns the headers.
     */
    public Map<String, String> getHeaders() {
        return _headers;
    }


    /**
     * Mutator.
     *
     * @param body The body to set.
     */
    public void setBody(final byte[] body) {
        _body = body;
    }


    /**
     * Mutator.
     *
     * @param headers The headers to set.
     */
    public void setHeaders(final Map<String, String> headers) {
        _headers = headers;
    }
}
