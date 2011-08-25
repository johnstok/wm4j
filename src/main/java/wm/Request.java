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
     * Accessors.
     */


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
    InetAddress getAddress();


    /**
     * The "local" path of the resource URI; the part after any prefix used in
     * dispatch configuration. Of the three path accessors, this is the one you
     * usually want. This is also the one that will change after create_path is
     * called in your resource.
     *
     * @return
     */
    String path_disp();


    /**
     * The path part of the URI -- after the host and port, but not including
     * any query string.
     *
     * @return
     */
    String path();


    /**
     * The entire path part of the URI, including any query string present.
     *
     * @return
     */
    String path_raw();


    /**
     * Looks up a binding as described in dispatch configuration.
     *
     * @param atom
     * @return
     */
    String path_info(Object atom);


    /**
     * The dictionary of bindings as described in dispatch configuration.
     *
     * @return
     */
    Map<Object, String> path_info();


    /**
     * This is a list of string() terms, the disp_path components split by "/".
     *
     * @return
     */
    String[] path_tokens();


    /**
     * Look up the value of an incoming request header.
     *
     * @param headerName
     * @return
     */
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


    /**
     * Indicates the "height" above the requested URI that this resource is
     * dispatched from. Typical values are "." , ".." , "../.." and so on.
     *
     * @return
     */
    String path_app_root();


    /*
     * Mutators.
     */


    /**
     * The disp_path is the only path that can be changed during a request. This
     * function will do so.
     *
     * @param path
     * @return
     */
    Request set_disp_path(String path);


    /**
     * Replace the incoming request body with this for the rest of the
     * processing.
     *
     * @param bytes
     * @return
     */
    // FIXME: Param should be an InputStream
    Request setBody(byte[] bytes) throws IOException;


    /**
     * Look up the current value of an outgoing request header.
     *
     * @param headerName
     * @return The header as a date.
     */
    Date getHeaderDate(String headerName);


    /**
     * Confirm the value of the specified header is a HTTP date.
     *
     * @param headerName
     *
     * @return True if the date is valid; false otherwise.
     */
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
