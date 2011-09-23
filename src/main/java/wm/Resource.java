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
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * HTTP API for a resource.
 *
 * From http://webmachine.basho.com/resources.html
 *
 * @author Keith Webster Johnston.
 */
public abstract class Resource {

    protected final Request             _request;
    protected final Response            _response;
    protected final Map<String, Object> _context;


    /**
     * Constructor.
     *
     * @param request
     * @param response
     * @param context
     */
    public Resource(final Request request,
                    final Response response,
                    final Map<String, Object> context) {
        _request  = request;
        _response = response;
        _context   = context;
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
    public abstract Map<MediaType, ? extends BodyReader> content_types_accepted();


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
    public abstract Map<MediaType, ? extends BodyWriter> content_types_provided();


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
    abstract URI createPath();


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
     * Query the languages in which this resource is available.
     *
     * Returning NULL indicates language negotiation is not supported.
     *
     * @return A set of available languages.
     */
    abstract Set<LanguageTag> languages_provided();


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

//-- TODO ^

    /**
     * Get the headers for response to an OPTIONS request.
     *
     * @return A collection of headers, as a map.
     */
    abstract Map<String,List<String>> getOptionsResponseHeaders();


    /**
     * Determine if POSTs to this resource create / update content.
     *
     * If true is returned the request will be treated similarly to a PUT,
     * inserting content into a (potentially new) resource (as opposed to being
     * a generic submission for processing). The {@link #createPath()} method
     * will be called to determine the path at which content should be
     * created/updated.
     *
     * @return True if the request should be treated as a PUT; false otherwise.
     */
    abstract boolean isPostCreate();


    /**
     * Determine if the resource existed prior to this request.
     *
     * @return True if the resource previously existed; false otherwise.
     */
    abstract boolean existedPreviously();


    /**
     * Perform this resources POST action.
     *
     * This method is only called when postIsCreate returns false.
     */
    abstract void processPost();


    /**
     * Determine if this resource exists.
     *
     * Returning non-true values will result in 404 Not Found.
     *
     * @return True if the resource exists, false otherwise.
     */
    abstract boolean exists();


    /**
     * Determine if this resource is available to serve requests.
     *
     * Returning false will result in a 503 Service Unavailable.
     *
     * @return True if the service is available, false otherwise.
     */
    abstract boolean isServiceAvailable();


    /**
     * Determine if the request URI is under the maximum size.
     *
     * Returning true will result in 414 Request URI Too Long.
     *
     * @return True if the URI length is under the maximum; false otherwise.
     */
    abstract boolean isUriTooLong();


    /**
     * Determine if an unsupported header is included in the request.
     *
     * Returning false will result in 501 Not Implemented.
     *
     * @return True if all headers are supported; false otherwise.
     */
    abstract boolean hasValidContentHeaders();


    /**
     * Determine if the entity length is under the maximum size.
     *
     * Normally this method will check the value of the Content-Length header.
     * Returning false will result in 413 Request Entity Too Large.
     *
     * @return True if the entity length is under the maximum; false otherwise.
     */
    abstract boolean isEntityLengthValid();


    /**
     * List the headers upon which this resource's representation can vary.
     *
     * If this function is implemented, it should return a list of strings with
     * header names that should be included in a given response's Vary header.
     * The standard conneg headers (Accept, Accept-Encoding, Accept-Charset,
     * Accept-Language) do not need to be specified here as they will be added
     * automatically depending on resource behaviour.
     *
     * @return An array of {@link Header}s.
     */
    abstract Header[] getVariances();
}
