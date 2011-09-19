/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;


/**
 * A HTTP request.
 *
 * @author Keith Webster Johnston.
 */
public interface Request {


    /**
     * The URI requested by the client.
     *
     * No decoding or normalisation/canonicalisation is performed on this value.
     *
     * @return The request URI, as a string.
     */
    // TODO: Return a URI instead?
    String getRequestUri();


    /**
     * The port via which this request was received.
     *
     * @return The port number, as an integer.
     */
    int getPort();


    /**
     * The hostname via which this request was received.
     *
     * Virtual hosting is currently unsupported. As such the 'Host' header
     * field and request URI 'domain' are ignored. Each of these values can be
     * accessed directly, if necessary.
     *
     * @return The host name, as a string.
     */
    @Specification(name="RFC-2616", section="5.2")
    String getDomain(); // TODO: Rename to getHost()?


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
     * The network address of the client.
     *
     * @return A network address.
     */
    InetSocketAddress getClientAddress();


    /**
     * The decoded path from the request URI.
     *
     * @return The request path, as a string.
     *
     * @throws ClientHttpException If the request URI is malformed.
     * @throws ServerHttpException If the required URI encoding is unsupported.
     */
    // TODO: Return a URI instead?
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
