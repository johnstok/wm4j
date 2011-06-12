/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * HTTP API for a resource.
 *
 * From http://webmachine.basho.com/resources.html
 *
 * @author Keith Webster Johnston.
 */
public abstract class Resource {

    final Properties          _configuration;
    final Request             _request;
    final Map<String, Object> _contex;


    /**
     * Constructor.
     *
     * @param configuration A configuration property list from the dispatcher.
     * @param request
     * @param contex
     */
    public Resource(final Properties configuration,
                    final Request request,
                    final Map<String, Object> contex) {
        _configuration = configuration;
        _request = request;
        _contex = contex;
    }


    /**
     * If the resource accepts POST requests to nonexistent resources, then this
     * should return true.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean allow_missing_post() throws HttpException;


    /**
     * If a Method not in this list is requested, then a 405 Method Not Allowed
     * will be sent. Note that these are all-caps and are atoms. (single-quoted)
     *
     * @return
     */
    // FIXME: Should be strongly typed.
    abstract Set<String> allowed_methods();


    /**
     * If this is anything other than the atom no_charset, it must be a list of
     * pairs where each pair is of the form Charset, Converter where Charset is
     * a string naming a charset and Converter is a callable function in the
     * resource which will be called on the produced body in a GET and ensure
     * that it is in Charset.
     *
     * @return
     */
    abstract Set<Charset> charsets_provided();


    /**
     * This is used similarly to content_types_provided, except that it is for
     * incoming resource representations -- for example, PUT requests. Handler
     * functions usually want to use wrq:req_body(ReqData) to access the
     * incoming request body.
     *
     * @return
     */
    public abstract Map<MediaType, BodyReader> content_types_accepted();


    /**
     * This should return a list of pairs where each pair is of the form
     * {Mediatype, Handler} where Mediatype is a string of content-type format
     * and the Handler is an atom naming the function which can provide a
     * resource representation in that media type. Content negotiation is driven
     * by this return value. For example, if a client request includes an Accept
     * header with a value that does not appear as a first element in any of the
     * return tuples, then a 406 Not Acceptable will be sent.
     *
     * @return
     */
    public abstract Map<MediaType, BodyWriter> content_types_provided();


    /**
     * This will be called on a POST request if post_is_create returns true. It
     * is an error for this function to not produce a Path if post_is_create
     * returns true. The Path returned should be a valid URI part following the
     * dispatcher prefix. That Path will replace the previous one in the return
     * value of wrq:disp_path(ReqData) for all subsequent resource function
     * calls in the course of this request.
     *
     * @return
     */
    abstract URI create_path();


    /**
     * This is only called after a successful delete_resource call, and should
     * return false if the deletion was accepted but cannot yet be guaranteed to
     * have finished.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean delete_completed() throws HttpException;


    /**
     * This is called when a DELETE request should be enacted, and should return
     * true if the deletion succeeded.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean delete_resource() throws HttpException;


    /**
     * This must be a list of pairs where in each pair Encoding is a string
     * naming a valid content encoding and Encoder is a callable function in the
     * resource which will be called on the produced body in a GET and ensure
     * that it is so encoded. One useful setting is to have the function check
     * on method, and on GET requests return [{"identity", fun(X) -> X end},
     * {"gzip", fun(X) -> zlib:gzip(X) end}] as this is all that is needed to
     * support gzip content encoding.
     *
     * @return
     */
    // FIXME: Should be strongly typed.
    abstract Set<String> encodings_provided();


    /**
     * If this method returns non-NULL, it will be used as the value of the
     * Expires header (TODO:confirm).
     *
     * @return
     */
    abstract Date expires();


    /**
     * This function, if exported, is called just before the final response is
     * constructed and sent.
     */
    abstract void finish_request();


    /**
     * Returning true will result in a 403 Forbidden.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean forbidden() throws HttpException;


    /**
     * If this returns a value, it will be used as the value of the ETag header
     * and for comparison in conditional requests.
     *
     * @return
     */
    abstract ETag generate_etag();


    /**
     * If this returns anything other than true, the response will be 401
     * Unauthorized. The AuthHead return value will be used as the value in the
     * WWW-Authenticate header.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean is_authorized() throws HttpException;


    /**
     * If this returns true, the client will receive a 409 Conflict.
     *
     * @return
     */
    abstract boolean is_conflict();


    /**
     * Returning false will result in 415 Unsupported Media Type.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean known_content_type() throws HttpException;


    /**
     * TODO: Add a description for this method.
     *
     * @return
     */
    abstract Set<Locale> languages_provided();


    /**
     * The date this resource was last modified.
     *
     * @return
     */
    abstract Date last_modified();


    /**
     * Returning true will result in 400 Bad Request.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean malformed_request() throws HttpException;


    /**
     * Returns non-NULL if the resource has been permanently moved to another
     * URI.
     *
     * @return
     * @throws HttpException
     */
    abstract URI moved_permanently() throws HttpException;


    /**
     * Returns non-NULL if the resource has been temporarily moved to another
     * URI.
     *
     * @return
     * @throws HttpException
     */
    abstract URI moved_temporarily() throws HttpException;


    /**
     * If this returns true, then it is assumed that multiple representations of
     * the response are possible and a single one cannot be automatically
     * chosen, so a 300 Multiple Choices will be sent instead of a 200.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean multiple_choices() throws HttpException;


    /**
     * If the OPTIONS method is supported and is used, the return value of this
     * function is expected to be a list of pairs representing header names and
     * values that should appear in the response.
     *
     * @return
     */
    abstract Map<String,Object> options();


    /**
     * If POST requests should be treated as a request to put content into a
     * (potentially new) resource as opposed to being a generic submission for
     * processing, then this function should return true. If it does return
     * true, then create_path will be called and the rest of the request will be
     * treated much like a PUT to the Path entry returned by that call.
     *
     * @return
     */
    abstract boolean post_is_create();


    /**
     * Did the resource exist prior to this request.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean previously_existed() throws HttpException;


    /**
     * If post_is_create returns false, then this will be called to process any
     * POST requests. If it succeeds, it should return true.
     *
     * @return
     * @throws HttpException
     */
    abstract void process_post() throws HttpException;


    /**
     * Returning non-true values will result in 404 Not Found.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean resource_exists() throws HttpException;


    /**
     * Returning true will result in a 503 Service Unavailable.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean service_available() throws HttpException;


    /**
     * Returning true will result in 414 Request URI Too Long.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean uri_too_long() throws HttpException;


    /**
     * Returning false will result in 501 Not Implemented.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean valid_content_headers() throws HttpException;


    /**
     * Returning false will result in 413 Request Entity Too Large.
     *
     * @return
     * @throws HttpException
     */
    abstract boolean valid_entity_length() throws HttpException;


    /**
     * If this function is implemented, it should return a list of strings with
     * header names that should be included in a given response's Vary header.
     * The standard conneg headers (Accept, Accept-Encoding, Accept-Charset,
     * Accept-Language) do not need to be specified here as Webmachine will add
     * the correct elements of those automatically depending on resource
     * behavior.
     *
     * @return
     */
    abstract Header[] variances();
}
