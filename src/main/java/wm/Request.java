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
    String get_req_method();


    /**
     * The HTTP version used by the client. Most often {1,1}.
     *
     * @return
     */
    Version get_req_version();


    /**
     * The IP address of the client.
     *
     * @return
     */
    InetAddress get_req_peer();


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
    String get_req_header(String headerName);


    /**
     * The incoming HTTP headers. Generally, get_req_header is more useful.
     *
     * @return
     */
    Map<String, List<String>> get_req_headers();


    /**
     * The incoming request body, if any.
     *
     * @return
     * @throws IOException
     */
    byte[] get_req_body() throws IOException;


    /**
     * The incoming request body in streamed form.
     *
     * @return
     * @throws IOException
     */
    InputStream get_req_body_stream() throws IOException;


    /**
     * Look up the named value in the incoming request cookie header.
     *
     * @param valueName
     * @return
     */
    String get_req_cookie_value(String valueName);


    /**
     * The raw value of the cookie header. Note that get_cookie_value is often
     * more useful.
     *
     * @return
     */
    String get_req_cookie();


    /**
     * Given the name of a key, look up the corresponding value in the query
     * string.
     *
     * @return
     */
    String get_req_qs_value(String paramName);


    /**
     * Given the name of a key and a default value if not present, look up the
     * corresponding value in the query string.
     *
     * @param paramName
     * @param defaultValue
     * @return
     */
    String get_req_qs_value(String paramName, String defaultValue);


    /**
     * The parsed query string, if any. Note that get_qs_value is often more
     * useful.
     *
     * @return
     */
    Map<String, List<String>> get_req_qs();


    /**
     * Look up the current value of an outgoing request header.
     *
     * @param headerName
     * @return
     */
    String get_resp_header(final String headerName);


    /**
     * The last value passed to do_redirect, false otherwise -- if true, then
     * some responses will be 303 instead of 2xx where applicable.
     *
     * @return
     */
    boolean get_resp_redirect();


    /**
     * The outgoing HTTP headers. Generally, get_resp_header is more useful.
     *
     * @return
     */
    Map<String, List<String>> get_resp_headers();


    /**
     * The outgoing response body, if one has been set. Usually,
     * append_to_response_body is the best way to set this.
     *
     * @return
     */
    byte[] get_resp_body();


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
     * Given a header name and value, set an outgoing request header to that
     * value.
     *
     * @param headerName
     * @param headerValue
     * @return
     */
    Request set_resp_header(String headerName, String headerValue);


    /**
     * Append the given value to the body of the outgoing response.
     *
     * @param bytes
     * @return
     * @throws IOException
     */
    Request append_to_response_body(byte[] bytes) throws IOException;


    /**
     * See resp_redirect; this sets that value.
     *
     * @param redirect
     * @return
     */
    Request set_resp_redirect(boolean redirect);


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
    Request set_req_body(byte[] bytes) throws IOException;


    /**
     * Set the outgoing response body to this value.
     *
     * @param bytes
     * @return
     * @throws IOException
     */
    Request set_resp_body(byte[] bytes) throws IOException;


    /**
     * Use this streamed body to produce the outgoing response body on demand.
     *
     * @param stream
     * @return
     * @throws IOException
     */
    Request set_resp_body(InputStream stream) throws IOException;


    /**
     * Given a list of two-tuples of {headername,value}, set those outgoing response headers.
     *
     * @param headers
     * @return
     */
    Request set_resp_headers(Map<String, String[]> headers);


    /**
     * Remove the named outgoing response header.
     *
     * @param headerName
     * @return
     */
    Request remove_resp_header(String headerName);


    /**
     * Look up the current value of an outgoing request header.
     *
     * @param headerName
     * @return The header as a date.
     */
    Date get_req_header_date(String headerName);


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
