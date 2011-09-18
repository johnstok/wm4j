/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * A HTTP request.
 *
 * From http://webmachine.basho.com/reqdata.html
 *
 * @author Keith Webster Johnston.
 */
public interface Request {

    /*
     * FIXME
     *
     * getURL
     *   How do we handle conflict between scheme/host/port in request line & 'Host' header & actual values ie we know server is running on port 1234. See RFC-2616, sec 5.2.
     *
     * getPort: int
     *   Port upon which the server received the request
     * getHost: String or Address?
     *   Hostname the request was directed to.
     * getScheme: Scheme
     *   The scheme used for the request. How do we handle conflict between absolute UriRequest value & actual use of SSL.
     * getPath : URI
     *   Decoded path component of URL - which charset for decoding?
     * getQueryValue : String
     *   Decoded query value - which charset for decoding?
     */


    /*
     * Accessors.
     */

    /**
     * The port specified by the request.
     *
     * @return The port number, as an integer.
     */
    int getPort();


    /**
     * The host name specified in the client request.
     *
     * @return The host name, as a string.
     */
    String getDomain();


    /**
     * Get the protocol used for this request.
     *
     * @return The protocol scheme.
     */
    Scheme getScheme();

    /**
     * Get URL fragment specified in the client request.
     *
     * @return Returns the decoded URL as a string.
     */
    String getFragment();


    /**
     * Does this request use a confidential protocol.
     *
     * @return True if a confidential protocol is in use; false otherwise.
     */
    boolean isConfidential();


    /**
     * The URL requested by the client.
     *
     * No decoding or normalisation/canonicalisation is performed on this value.
     *
     * @return The request URL as a Java URL.
     */
    URL getUrl();


    /**
     * The HTTP method used by the client.
     *
     * @return
     */
    String getMethod();


    /**
     * The HTTP version used by the client.
     *
     * @return A {@link Version} object.
     */
    Version getVersion();


    /**
     * The IP address of the client.
     *
     * @return
     */
    // FIXME: Should be InetSocketAddress.
    InetAddress getClientAddress();


    /**
     * The decoded path from the request URL.
     *
     * @return The request path, as a URI.
     */
    URI getPath();


    /**
     * Look up the value of an incoming request header.
     *
     * @param headerName
     * @return
     */
    // TODO: Add another version of this method that accepts a default value.
    String getHeader(String headerName);


    /**
     * The incoming HTTP headers. Generally, get_req_header is more useful.
     *
     * @return
     */
    Map<String, List<String>> getHeaders();


    /**
     * The incoming request body, if any.
     *
     * @return
     * @throws IOException
     */
    // FIXME: Remove this provide a utility to read a body into memory.
    byte[] getBody() throws IOException;


    /**
     * The incoming request body in streamed form.
     *
     * @return
     * @throws IOException
     */
    InputStream getBodyAsStream() throws IOException;


    /**
     * Given the name of a key, look up the corresponding value in the query
     * string.
     *
     * @return
     */
    String getQueryValue(String paramName);


    /**
     * Given the name of a key and a default value if not present, look up the
     * corresponding value in the query string.
     *
     * @param paramName
     * @param defaultValue
     * @return
     */
    String getQueryValue(String paramName, String defaultValue);


    /**
     * The parsed query string, if any. Note that get_qs_value is often more
     * useful.
     *
     * @return
     */
    Map<String, List<String>> getQueryValues();


    /*
     * Mutators.
     */


    /**
     * Look up the current value of an outgoing request header.
     *
     * @param headerName
     * @return The header as a date.
     */
    // FIXME: Convert to wm.headers.DateHeader#parse(headerValue).
    Date getHeaderDate(String headerName);


    /**
     * Confirm the value of the specified header is a HTTP date.
     *
     * @param headerName
     *
     * @return True if the date is valid; false otherwise.
     */
    // FIXME: Convert to wm.headers.DateHeader#isValid(headerValue).
    boolean isValidDate(String headerName);


    /**
     * Does this request have at least one header with the specified name.
     *
     * @param headerName
     *
     * @return True if the request has such a header; false otherwise.
     */
    boolean hasHeader(String headerName);
}
