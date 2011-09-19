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
import java.util.List;
import java.util.Map;


/**
 * A HTTP request.
 *
 * @author Keith Webster Johnston.
 */
public interface Request {

    /*
     * FIXME
     *
     * getURL
     *   How do we handle conflict between scheme/host/port in request line & 'Host' header & actual values ie we know server is running on port 1234. See RFC-2616, sec 5.2.
     * getScheme
     *   How do we handle conflict between absolute UriRequest value & actual use of SSL.
     */

    /**
     * The URL requested by the client.
     *
     * No decoding or normalisation/canonicalisation is performed on this value.
     *
     * @return The request URL as a Java URL.
     */
    String getRequestUri();

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
     * Does this request use a confidential protocol.
     *
     * @return True if a confidential protocol is in use; false otherwise.
     */
    boolean isConfidential();


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
    // FIXME: Should be InetSocketAddress?
    InetAddress getClientAddress();


    /**
     * The decoded path from the request URL.
     *
     * @return The request path, as a URI.
     */
    String getPath();


    /**
     * Look up the value of an incoming request header.
     *
     * @param headerName The name of the required header value.
     *
     * @return Returns the header value as a string
     */
    String getHeader(String headerName);

    /**
     * Look up the value of an incoming request header.
     *
     * @param headerName The name of the required header value.
     * @param defaultValue The default value to return if no such header
     *  exists.
     *
     * @return Returns the header value as a string.
     */
    String getHeader(String headerName, String defaultValue);


    /**
     * Get all available header values.
     *
     * @return A map containing the header values for this request.
     */
    Map<String, List<String>> getHeaders();


    /**
     * The incoming request body.
     *
     * @return Returns an input stream to read the body data.
     *
     * @throws IOException If creation of the input stream fails.
     */
    InputStream getBody() throws IOException;


    /**
     * Given the name of a key, look up the corresponding value in the query
     * string.
     *
     * @param paramName The name of the required query parameter.
     *
     * @return Returns the decoded query value.
     */
    String getQueryValue(String paramName);


    /**
     * Given the name of a key and a default value if not present, look up the
     * corresponding value in the query string.
     *
     * @param paramName The name of the required query parameter.
     * @param defaultValue The default value to return if no such parameter
     *  exists.
     *
     * @return Returns the decoded query value.
     */
    String getQueryValue(String paramName, String defaultValue);


    /**
     * Get all available query values.
     *
     * @return A map containing all decoded query values.
     */
    Map<String, List<String>> getQueryValues();


    /**
     * Does this request have at least one header with the specified name.
     *
     * @param headerName The name of the required header.
     *
     * @return True if the request has such a header; false otherwise.
     */
    boolean hasHeader(String headerName);
}
