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
import com.johnstok.http.ETag;
import com.johnstok.http.Header;
import com.johnstok.http.LanguageTag;
import com.johnstok.http.MediaType;
import com.johnstok.http.Specification;
import com.johnstok.http.Specifications;
import com.johnstok.http.sync.BodyReader;
import com.johnstok.http.sync.BodyWriter;


/**
 * API for a HTTP resource.
 *
 * @author Keith Webster Johnston.
 */
public interface Resource {


    /**
     * Determine if the resource accepts POST requests to nonexistent resources.
     *
     * @return True if such requests are allowed; false otherwise.
     */
    boolean allowsPostToMissing();


    /**
     * Get the methods supported by this resource.
     *
     * If a Method not in this list is requested, then a 405 Method Not Allowed
     * will be sent.
     *
     * @return
     */
    // FIXME: Should be strongly typed.
    @Specification(name="rfc-2616", section="10.4.6")
    Set<String> getAllowedMethods();


    /**
     * Get the character sets supported by this resource.
     *
     * @return NULL if negotiation of character set is not supported;
     *  otherwise a set of supported character sets.
     */
    @Specifications({
        @Specification(name="rfc-2616", section="14.2"),
        @Specification(name="rfc-2616", section="3.4"),
        @Specification(name="rfc-2616", section="14.17"),
        @Specification(name="rfc-2616", section="12")
    })
    Set<Charset> getCharsetsProvided();


    /**
     * Determine the content types supported for request bodies.
     *
     * @return A map of media type => body reader.
     */
    public Map<MediaType, ? extends BodyReader> getContentTypesAccepted();

//-- TODO ^

    /**
     * Determine the media-types this resource supports in response entities.
     *
     * Returning NULL indicates language negotiation is not supported.
     *
     * @return A set of {@link MediaType}s.
     */
    @Specifications({
        @Specification(name="rfc-2616", section="14.1"),
        @Specification(name="rfc-2616", section="3.7"),
        @Specification(name="rfc-2616", section="14.17"),
        @Specification(name="rfc-2616", section="12")
    })
    public Set<MediaType> getContentTypesProvided();
    // TODO: Document what happens if an empty set is returned.


    /**
     * Retrieve the body writer for the specified media type.
     *
     * @param The media-type for the response's entity.
     *
     * @return A valid BodyWriter.
     */
    public BodyWriter getWriter(MediaType mediaType);
    // FIXME: Should pass all conneg'ed param's here.


    /**
     * Determine the URI for a new resource created via a POST request.
     *
     * This will be called on a POST request if {@link #isPostCreate()} returns
     * true. It is an error for this method to not produce a Path if
     * {@link #isPostCreate()} returns true.
     *
     * @return A valid path this resource was created by a POST request; false
     *  otherwise.
     */
    URI getCreatePath();


    /**
     * Determine if deletion of this resource has completed.
     *
     * @return True if deletion is complete; false if the deletion was accepted
     * but cannot yet be guaranteed to have finished.
     */
    boolean isDeleted();


    /**
     * Delete this resource.
     *
     * @return True if the resource was started successfully; false if the
     *  delete failed.
     */
    @Specification(name="rfc-2616", section="9.7")
    boolean delete();


    /**
     * Get the content encodings supported by this resource.
     *
     * The content encoding is used to encoded the response body. The 'identity'
     * encoding is always supported.
     *
     * @return NULL if negotiation of content encoding is not supported;
     *  otherwise a set of supported encodings.
     */
    // FIXME: Should be strongly typed.
    @Specifications({
        @Specification(name="rfc-2616", section="14.3"),
        @Specification(name="rfc-2616", section="3.5"),
        @Specification(name="rfc-2616", section="14.11"),
        @Specification(name="rfc-2616", section="12")
    })
    Set<String> getEncodings();


    /**
     * Get the date cached representations of this resource will expire.
     *
     * If this method returns non-NULL, it will be used as the value of the
     * Expires header.
     *
     * @return A date if cached representations can be cached; NULL otherwise.
     */
    @Specifications({
        @Specification(name="rfc-2616", section="14.21"),
        @Specification(name="rfc-2616", section="13.2"),
        @Specification(name="rfc-2616", section="14.9")
    })
    Date getExpiryDate();


    /**
     * Wind up processing of a request.
     *
     * This method will be called by the HTTP engine once the full response has
     * been transmitted.
     */
    void finishRequest();


    /**
     * Determine if access to this resource is forbidden.
     *
     * Returning true will result in a 403 Forbidden response. A resource is
     * forbidden if the request was valid but the resource is refusing to
     * fulfill it. If the client is unauthorized {@link #authorize()} should be
     * used instead.
     *
     * @return True if access to the resource is forbidden; false otherwise.
     */
    @Specification(name="rfc-2616", section="10.4.4")
    boolean isForbidden();


    /**
     * Generate an ETag for this resource.
     *
     * An entity tag MUST be unique across all versions of all entities
     * associated with a particular resource. As such, the value of the base
     * parameter should always be included in a returned value. If this method
     * returns non-NULL,  it will be used as the value of the ETag response
     * header and for comparison in conditional requests.
     *
     * @param base A base ETag value calculated from the dimensions by which
     *  representations of this resource vary. Dimensions are determined from
     *  the values of all headers mentioned in the response 'Vary'  header
     *  including those calculated during server-driven content
     *  negotiation and those specified via the {@link #getVariances()} method.
     *
     * @return An ETag if one is available; null otherwise.
     */
    @Specifications({
        @Specification(name="rfc-2616", section="13.3.3"),
        @Specification(name="rfc-2616", section="14.19"),
        @Specification(name="rfc-2616", section="14.24"),
        @Specification(name="rfc-2616", section="14.26"),
        @Specification(name="rfc-2616", section="14.27")
    })
    ETag generateEtag(String base);


    /**
     * Authorize the client to access the resource.
     *
     * If a non-NULL value is returned the response will be 401 Unauthorized.
     *
     * @return NULL if the client is authorized; the value of the
     *  WWW-Authenticate response header challenge if the client is
     *  unauthorized.
     */
    @Specifications({
        @Specification(name="rfc-2616", section="10.4.2"),
        @Specification(name="rfc-2616", section="10.8"),
        @Specification(name="rfc-2617")
    })
    String authorize();


    /**
     * Has the execution of the request caused a conflict in the state of the
     * current resource.
     *
     * Conflicts are most likely to occur in response to a PUT request. For
     * example, if versioning were being used and the entity being PUT included
     * changes to a resource which conflict with those made by an earlier
     * (third-party) request. If this returns true, the client will receive a
     * 409 Conflict.
     *
     * @return True if the resource is in conflict; false otherwise.
     */
    @Specification(name="rfc-2616", section="10.4.10")
    boolean isInConflict();


    /**
     * Determine if the resources understands the specified Media Type.
     *
     * Returning false will result in 415 Unsupported Media Type.
     *
     * @param mediaType The media-type to check.
     *
     * @return True if the media-type is known; false otherwise.
     */
    @Specification(name="rfc-2616", section="10.4.16")
    boolean isContentTypeKnown(MediaType mediaType);


    /**
     * Query the languages in which this resource is available.
     *
     * Returning NULL indicates language negotiation is not supported.
     *
     * @return A set of available languages.
     */
    @Specifications({
        @Specification(name="rfc-2616", section="14.4"),
        @Specification(name="rfc-2616", section="3.10"),
        @Specification(name="rfc-2616", section="14.12"),
        @Specification(name="rfc-2616", section="12")
    })
    Set<LanguageTag> getLanguages();


    /**
     * Get the date this resource was last modified.
     *
     * @return Last date the resource was modified.
     */
    // TODO: Specification?
    Date getLastModifiedDate();


    /**
     * Determine if the resource was unable to understand the request.
     *
     * Returning true will result in 400 Bad Request.
     *
     * @return True if the request was malformed; false otherwise.
     */
    @Specification(name="rfc-2616", section="10.4.1")
    boolean isMalformed();


    /**
     * Determine if this resource is permanently available at an another URI.
     *
     * @return Returns the alternative URI if so; returns NULL otherwise.
     */
    @Specification(name="rfc-2616", section="10.3.2")
    URI movedPermanentlyTo();


    /**
     * Determine if this resource is temporarily available at another URI.
     *
     * @return Returns the alternative URI if so; returns NULL otherwise.
     */
    @Specification(name="rfc-2616", section="10.3.8")
    URI movedTemporarilyTo();


    /**
     * Does this resource have multiple representations available to the client?
     *
     * This method can be used to implement agent-driven content negotiation. If
     * this returns true, then it is assumed that multiple representations of
     * the response are possible and a single one cannot be automatically
     * chosen, so a 300 Multiple Choices will be sent instead of a 200.
     *
     * FIXME: Describe how the response body is generated!
     *
     * References:
     *  - http://www.amundsen.com/blog/archives/1085
     *
     * @return True if multiple representations are available, false otherwise.
     */
    @Specifications({
        @Specification(name="rfc-2616", section="10.3.1"),
        @Specification(name="rfc-2616", section="12.2")
    })
    boolean hasMultipleChoices();


    /**
     * Get the headers for response to an OPTIONS request.
     *
     * @return A collection of headers, as a map.
     */
    @Specification(name="rfc-2616", section="9.2")
    Map<String,List<String>> getOptionsResponseHeaders();


    /**
     * Determine if POSTs to this resource create / update content.
     *
     * If true is returned the request will be treated similarly to a PUT,
     * inserting content into a (potentially new) resource (as opposed to being
     * a generic submission for processing). The {@link #getCreatePath()} method
     * will be called to determine the path at which content should be
     * created/updated.
     *
     * @return True if the request should be treated as a PUT; false otherwise.
     */
    boolean isPostCreate();


    /**
     * Determine if the resource existed prior to this request.
     *
     * @return True if the resource previously existed; false otherwise.
     */
    boolean existedPreviously();


    /**
     * Perform this resources POST action.
     *
     * This method is only called when postIsCreate returns false.
     */
    void processPost();


    /**
     * Determine if this resource exists.
     *
     * Returning non-true values will result in 404 Not Found.
     *
     * @return True if the resource exists, false otherwise.
     */
    boolean exists();


    /**
     * Determine if this resource is available to serve requests.
     *
     * Returning false will result in a 503 Service Unavailable.
     *
     * @return True if the service is available, false otherwise.
     */
    boolean isServiceAvailable();


    /**
     * Determine if the request URI is under the maximum size.
     *
     * Returning true will result in 414 Request URI Too Long.
     *
     * @return True if the URI length is under the maximum; false otherwise.
     */
    boolean isUriTooLong();


    /**
     * Determine if an unsupported header is included in the request.
     *
     * Returning false will result in 501 Not Implemented.
     *
     * @return True if all headers are supported; false otherwise.
     */
    boolean hasValidContentHeaders();


    /**
     * Determine if the entity length is under the maximum size.
     *
     * Normally this method will check the value of the Content-Length header.
     * Returning false will result in 413 Request Entity Too Large.
     *
     * @return True if the entity length is under the maximum; false otherwise.
     */
    boolean isEntityLengthValid();


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
    Header[] getVariances();
}
