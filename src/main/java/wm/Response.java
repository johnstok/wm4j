/*-----------------------------------------------------------------------------

 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;


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
    // TODO: This should return a list of values.
    String getHeader(final String name);


    /**
     * TODO: Add a description for this method.
     *
     * @param value
     */
    void write(BodyWriter value) throws IOException;


    /**
     * TODO: Add a description for this method.
     *
     * @return
     */
    boolean hasBody();


    /**
     * The time at which the server originated the response message.
     *
     * @return
     */
    Date getOriginationTime();


    /**
     * TODO: Add a description for this method.
     *
     * @param name
     * @param value
     */
    void setHeader(String name, Date value);


    /**
     * TODO: Add a description for this method.
     *
     * @param charset
     */
    void setCharset(Charset charset);


    /**
     * TODO: Add a description for this method.
     *
     * @param mediaType
     */
    void setMediaType(MediaType mediaType);


    /**
     * TODO: Add a description for this method.
     *
     * @param encoding
     */
    void setContentEncoding(String encoding);


    /**
     * TODO: Add a description for this method.
     *
     * @return
     */
    MediaType getMediaType();


//  /**
//  * Look up the current value of an outgoing request header.
//  *
//  * @param headerName
//  * @return
//  */
// String get_resp_header(final String headerName);
//
//
// /**
//  * The last value passed to do_redirect, false otherwise -- if true, then
//  * some responses will be 303 instead of 2xx where applicable.
//  *
//  * @return
//  */
// boolean get_resp_redirect();
//
//
// /**
//  * The outgoing HTTP headers. Generally, get_resp_header is more useful.
//  *
//  * @return
//  */
// Map<String, List<String>> get_resp_headers();
//
//
// /**
//  * The outgoing response body, if one has been set. Usually,
//  * append_to_response_body is the best way to set this.
//  *
//  * @return
//  */
// byte[] get_resp_body();
//
//
//  /**
//  * Given a header name and value, set an outgoing request header to that
//  * value.
//  *
//  * @param headerName
//  * @param headerValue
//  * @return
//  */
// Request set_resp_header(String headerName, String headerValue);
//
//
// /**
//  * Append the given value to the body of the outgoing response.
//  *
//  * @param bytes
//  * @return
//  * @throws IOException
//  */
// Request append_to_response_body(byte[] bytes) throws IOException;
//
//
// /**
//  * See resp_redirect; this sets that value.
//  *
//  * @param redirect
//  * @return
//  */
// Request set_resp_redirect(boolean redirect);
//
//
//  /**
//  * Set the outgoing response body to this value.
//  *
//  * @param bytes
//  * @return
//  * @throws IOException
//  */
// Request set_resp_body(byte[] bytes) throws IOException;
//
//
// /**
//  * Use this streamed body to produce the outgoing response body on demand.
//  *
//  * @param stream
//  * @return
//  * @throws IOException
//  */
// Request set_resp_body(InputStream stream) throws IOException;
//
//
// /**
//  * Given a list of two-tuples of {headername,value}, set those outgoing response headers.
//  *
//  * @param headers
//  * @return
//  */
// Request set_resp_headers(Map<String, String[]> headers);
//
//
// /**
//  * Remove the named outgoing response header.
//  *
//  * @param headerName
//  * @return
//  */
// Request remove_resp_header(String headerName);
}
