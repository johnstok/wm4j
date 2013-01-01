/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import static com.johnstok.http.Header.*;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import com.johnstok.http.ContentEncoding;
import com.johnstok.http.ETag;
import com.johnstok.http.Header;
import com.johnstok.http.HttpException;
import com.johnstok.http.LanguageTag;
import com.johnstok.http.MediaType;
import com.johnstok.http.Method;
import com.johnstok.http.Status;
import com.johnstok.http.WeightedValue;
import com.johnstok.http.headers.DateHeader;
import com.johnstok.http.negotiation.CharsetNegotiator;
import com.johnstok.http.negotiation.ContentNegotiator;
import com.johnstok.http.negotiation.LanguageNegotiator;
import com.johnstok.http.negotiation.MediaTypeNegotiator;
import com.johnstok.http.sync.BodyReader;
import com.johnstok.http.sync.Request;
import com.johnstok.http.sync.Response;


/**
 * Implements the HTTP processing logic.
 *
 * @author Keith Webster Johnston.
 */
public class Engine {

    private MediaType accept(final Request request,
                             final Set<MediaType> content_types_provided) {
        final List<WeightedValue> clientMediaTypes =
            MediaTypeNegotiator.parse(request.getHeader(Header.ACCEPT));
        return new MediaTypeNegotiator(content_types_provided).select(clientMediaTypes);
    }


    private Charset acceptCharset(final Request request,
                                  final Set<Charset> charsets_provided) {
        final List<WeightedValue> clientCharsets =
            Header.parseAcceptCharset(
                request.getHeader(Header.ACCEPT_CHARSET));
        return new CharsetNegotiator(charsets_provided).select(clientCharsets);
    }


    private String acceptEncoding(final Request request,
                                  final Set<String> encodings_provided) {
        final List<WeightedValue> clientEncodings =
            Header.parseAcceptEncoding(
                request.getHeader(Header.ACCEPT_ENCODING));
        final ContentNegotiator negotiator = new ContentNegotiator(encodings_provided);
        ContentEncoding selected = negotiator.select(clientEncodings);
        return (null==selected) ? null : selected.toString(); // FIXME: Should return ContentEncoding.
    }


    private LanguageTag acceptLanguage(final Request request,
                                       final Set<LanguageTag> languages_provided) {
        final List<WeightedValue> clientLanguages =
            LanguageNegotiator.parse(request.getHeader(Header.ACCEPT_LANGUAGE));
        final LanguageNegotiator negotiator =
            new LanguageNegotiator(languages_provided);
        return negotiator.select(clientLanguages);
    }


    private <T> T first(final Set<T> set) {
        if (null==set) { return null; }
        final List<T> list = new ArrayList<T>(set);
        if (1>list.size()) { return null; }
        return list.get(0);
    }


    private void processRequestBody(final Resource resource,
                     final Request request,
                                    final Response response) throws HttpException {
        final MediaType mt = MediaType.ANY; // FIXME: Extract media type.
        final BodyReader br = resource.getContentTypesAccepted().get(mt); // TODO: Conneg required?
        try {
            br.read(request.getBody());
        } catch (final IOException e) {
            // TODO Log exception.
            response.setStatus(Status.INTERNAL_SERVER_ERROR);
        }
    }


    private void P11_new_resource(
                     final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (null==response.getHeader(Header.LOCATION)) {
            O20_response_includes_an_entity(resource, request, response);
        } else {
            /*
             * The request has been fulfilled and resulted in a new resource being
             * created. The newly created resource can be referenced by the URI(s)
             * returned in the entity of the response, with the most specific URI for
             * the resource given by a Location header field. The response SHOULD
             * include an entity containing a list of resource characteristics and
             * location(s) from which the user or user agent can choose the one most
             * appropriate. The entity format is specified by the media type given in
             * the Content-Type header field. The origin server MUST create the resource
             * before returning the 201 status code. If the action cannot be carried out
             * immediately, the server SHOULD respond with 202 (Accepted) response
             * instead.
             *
             * A 201 response MAY contain an ETag response header field indicating the
             * current value of the entity tag for the requested variant just created,
             * see section 14.19.
             */
            response.setStatus(Status.CREATED);
            attachEtag(resource, request, response); // TODO: Use variant as per current request.
            attachLastModified(resource, request, response);
            // TODO: Provide an entity if available.
        }
    }


    private void O20_response_includes_an_entity(
                     final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (response.hasBody()) {
            O18_multiple_representations(resource, request, response);
        } else {
            /*
             * The server has fulfilled the request but does not need to return
             * an entity-body, and might want to return updated
             * meta-information. The response MAY include new or updated
             * meta-information in the form of entity-headers, which if present
             * SHOULD be associated with the requested variant.
             *
             * If the client is a user agent, it SHOULD NOT change its document
             * view from that which caused the request to be sent. This response
             * is primarily intended to allow input for actions to take place
             * without causing a change to the user agent's active document
             * view, although any new or updated meta-information SHOULD be
             * applied to the document currently in the user agent's active
             * view.
             *
             * The 204 response MUST NOT include a message-body, and thus is
             * always terminated by the first empty line after the header
             * fields.
             */
            response.setStatus(Status.NO_CONTENT);
            attachEtag(resource, request, response); // TODO: Use variant as per current request.
            attachLastModified(resource, request, response);
        }
    }


    private void O18_multiple_representations(
                     final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        // TODO: Set appropriate headers for response.
        if (resource.hasMultipleChoices()) {
            response.setStatus(Status.MULTIPLE_CHOICES);
            // TODO: Write choices in body.
        } else {
            try {
                response.setStatus(Status.OK);

                attachEtag(resource, request, response);
                attachLastModified(resource, request, response);

                // TODO: Set 'Expires' header.

                response.write(resource.getWriter(response.getMediaType()));
            } catch (final IOException e) {
                // TODO Handle committed responses.
                // TODO Log exception.
                response.setStatus(Status.INTERNAL_SERVER_ERROR);
            }
            response.setStatus(Status.OK);
        }
    }


    /**
     * Attach the last modified date to a response.
     *
     * @param resource The source of the date.
     * @param response The response to which the date will be attached.
     */
    private void attachLastModified(final Resource resource,
                                    final Request request,
                                    final Response response) {

        final Date lastModified = resource.getLastModifiedDate();
        final Date messageOriginationTime = response.getOriginationTime();

        /*
         * An origin server MUST NOT send a Last-Modified date which
         * is later than the server's time of message origination.
         * In such cases, where the resource's last modification
         * would indicate some time in the future, the server MUST
         * replace that date with the message origination date.
         */
        if (null!=lastModified) {
            response.setHeader(
                Header.LAST_MODIFIED,
                (lastModified.after(messageOriginationTime)) ? messageOriginationTime : lastModified);
        }
    }


    /**
     * Attach an ETag to a response.
     *
     * @param resource The source of the ETag.
     * @param response The response to which the ETag will be attached.
     */
    private void attachEtag(final Resource resource,
                            final Request request,
                            final Response response) {
        // FIXME: ETags depend on variants.
        final ETag eTag = resource.generateEtag(calculateEtagBase(response));
        if (null!=eTag) { response.setHeader(E_TAG, eTag.getValue()); }
    }


    // FIXME: Remove response param - retrieve via resource instead.
    public final void process(final Resource resource,
                     final Request request,
                              final Response response) {
        try {
            response.setHeader(Header.SERVER, "wm4j/1.0.0");
            response.setHeader(Header.DATE, response.getOriginationTime());
            B12_service_available(resource, request, response);
        } catch (final HttpException e) {
            // TODO handle committed responses.
            response.setStatus(Status.INTERNAL_SERVER_ERROR);
        } catch (final RuntimeException e) {
            // TODO handle committed responses.
            response.setStatus(Status.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }


    private void G07_resource_exists(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        // TODO: Set variances.
        if (resource.exists()) {
            G07a(resource, request, response);
        } else {
            H07_if_match_is_wildcard(resource, request, response);
        }
    }


    private void H07_if_match_is_wildcard(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if ("*".equals(request.getHeader(Header.IF_MATCH))) {
            response.setStatus(Status.PRECONDITION_FAILED);
        } else {
            I07_is_PUT_method(resource, request, response);
        }
    }


    private void G07a(final Resource resource,
                     final Request request,
                      final Response response) throws HttpException { // Added to support redirect for existing resources - confirm logic.
        final URI tempUri = resource.movedTemporarilyTo();
        if (null!=tempUri) {
            response.setStatus(Status.TEMPORARY_REDIRECT);
            response.setHeader(Header.LOCATION, tempUri.toString()); // TODO: Confirm serialisation of URIs
        } else {
            G08_if_match_is_wildcard(resource, request, response);
        }
    }


    private void G08_if_match_is_wildcard(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (request.hasHeader(Header.IF_MATCH)) {
            G09_if_match_header_exists(resource, request, response);
        } else {
            H10_if_unmodified_since_exists(resource, request, response);
        }
    }


    private void G09_if_match_header_exists(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if ("*".equals(request.getHeader(Header.IF_MATCH))) {
            H10_if_unmodified_since_exists(resource, request, response);
        } else {
            G11_etag_in_if_match(resource, request, response);
        }
    }


    private void G11_etag_in_if_match(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (request.getHeader(Header.IF_MATCH).equals(resource.generateEtag(calculateEtagBase(response)).getValue())) {
            H10_if_unmodified_since_exists(resource, request, response);
        } else {
            response.setStatus(Status.PRECONDITION_FAILED);
        }
    }


    private String calculateEtagBase(final Response response) {
        return ""; // FIXME: MD5 of values of headers mentioned in `Vary` header.
    }


    private void L13_if_modified_since_exists(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (request.hasHeader(Header.IF_MODIFIED_SINCE)) {
            L14_if_modified_since_is_valid(resource, request, response);
        } else {
            M16_is_DELETE_method(resource, request, response);
        }

    }


    private void L14_if_modified_since_is_valid(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (DateHeader.isValidDate(request.getHeader(Header.IF_MODIFIED_SINCE))) {
            L15_if_modified_since_after_now(resource, request, response);
        } else {
            M16_is_DELETE_method(resource, request, response);
        }
    }


    private void L15_if_modified_since_after_now(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (DateHeader.parse(request.getHeader(Header.IF_MODIFIED_SINCE)).after(new Date())) { // TODO: Compare with message origination rather than 'now'?
            M16_is_DELETE_method(resource, request, response);
        } else {
            L17_last_modified_after_if_modified_since(resource, request, response);
        }
    }


    private void L17_last_modified_after_if_modified_since(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (DateHeader.parse(request.getHeader(Header.IF_MODIFIED_SINCE)).before(resource.getLastModifiedDate())) {
            M16_is_DELETE_method(resource, request, response);
        } else {
            response.setStatus(Status.NOT_MODIFIED);
        }
    }


    private void I12_if_none_match_exists(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (request.hasHeader(Header.IF_NONE_MATCH)) {
            I13_if_none_match_is_wildcard(resource, request, response);
        } else {
            L13_if_modified_since_exists(resource, request, response);
        }
    }


    private void I13_if_none_match_is_wildcard(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if ("*".equals(request.getHeader(Header.IF_NONE_MATCH))) {
            J18_GET_or_HEAD(resource, request, response);
        } else {
            K13_etag_in_if_none_match(resource, request, response);
        }
    }


    private void J18_GET_or_HEAD(final Resource resource,
                     final Request request,
                     final Response response) {
        if (Method.GET.equals(request.getMethod())
            || Method.HEAD.equals(request.getMethod())) {
            response.setStatus(Status.NOT_MODIFIED);
        } else {
            response.setStatus(Status.PRECONDITION_FAILED);
        }
    }


    private void K13_etag_in_if_none_match(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (request.getHeader(Header.IF_NONE_MATCH).equals(resource.generateEtag(calculateEtagBase(response)).getValue())) {
            J18_GET_or_HEAD(resource, request, response);
        } else {
            L13_if_modified_since_exists(resource, request, response);
        }
    }


    private void H10_if_unmodified_since_exists(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (request.hasHeader(Header.IF_UNMODIFIED_SINCE)) {
            H11_if_unmodified_since_is_valid(resource, request, response);
        } else {
            I12_if_none_match_exists(resource, request, response);
        }
    }


    private void H11_if_unmodified_since_is_valid(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (DateHeader.isValidDate(request.getHeader(Header.IF_UNMODIFIED_SINCE))) {
            H12_last_modified_after_if_unmodified_since(resource, request, response);
        } else {
            I12_if_none_match_exists(resource, request, response);
        }
    }


    private void H12_last_modified_after_if_unmodified_since(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (DateHeader.parse(request.getHeader(Header.IF_UNMODIFIED_SINCE)).before(resource.getLastModifiedDate())) {
            response.setStatus(Status.PRECONDITION_FAILED);
        } else {
            I12_if_none_match_exists(resource, request, response);
        }
    }


    private void I07_is_PUT_method(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (Method.PUT.equals(request.getMethod())) {
            I04_apply_request_to_another_uri(resource, request, response);
        } else {
            K07_previously_existed(resource, request, response);
        }
    }


    private void K07_previously_existed(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (resource.existedPreviously()) {
            K05_moved_permanently(resource, request, response);
        } else {
            L07_is_POST_method(resource, request, response);
        }
    }


    private void K05_moved_permanently(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        final URI permUri = resource.movedPermanentlyTo();
        if (null!=permUri) {
            response.setStatus(Status.MOVED_PERMANENTLY);
            response.setHeader(Header.LOCATION, permUri.toString()); // TODO: Confirm serialisation of URIs
        } else {
            L05_moved_temporarily(resource, request, response);
        }
    }


    private void L07_is_POST_method(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if ((Method.POST==request.getMethod()) && resource.allowsPostToMissing()) { // L7, M7
            N11_redirect(resource, request, response);
        } else {
            response.setStatus(Status.NOT_FOUND);
        }
    }


    private void L05_moved_temporarily(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        final URI tempUri = resource.movedTemporarilyTo();
        if (null!=tempUri) {
            response.setStatus(Status.TEMPORARY_REDIRECT);
            response.setHeader(Header.LOCATION, tempUri.toString()); // TODO: Confirm serialisation of URIs
        } else {
                M05_is_POST_method(resource, request, response);
        }
    }


    private void M05_is_POST_method(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if ((Method.POST==request.getMethod()) && resource.allowsPostToMissing()) { // M5, N5
            N11_redirect(resource, request, response);
        } else {
            response.setStatus(Status.GONE);
        }
    }


    private void P03_conflict(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (resource.isInConflict()) {
            response.setStatus(Status.CONFLICT);
        } else {
            processRequestBody(resource, request, response);
            P11_new_resource(resource, request, response);
        }
    }


    private void I04_apply_request_to_another_uri(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        final URI putUri = resource.movedPermanentlyTo();
        if (null!=putUri) {
            response.setStatus(Status.MOVED_PERMANENTLY);
            response.setHeader(Header.LOCATION, putUri.toString()); // TODO: Confirm serialisation of URIs
        } else {
            P03_conflict(resource, request, response);
        }
    }


    private void M16_is_DELETE_method(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (Method.DELETE==request.getMethod()) {  // M16
            // FIXME: Something fishy here - no O20 decision.
            final boolean accepted = resource.delete();
            if (accepted) {
                final boolean enacted = resource.isDeleted();
                if (!enacted) {                                   // M20
                    response.setStatus(Status.ACCEPTED);
                } else {
                    response.setStatus(Status.NO_CONTENT);
                }
            }
        } else {
            N16_is_POST_method(resource, request, response);
        }
    }


    private void F06_accept_encoding_header_exists(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        final Set<String> availableEncodings = resource.getEncodings();

        if (null==availableEncodings) {                       // Disable conneg.
            G07_resource_exists(resource, request, response);

        } else if (request.hasHeader(Header.ACCEPT_ENCODING)) {    // Do conneg.
            F07_acceptable_encoding_available(resource, request, response);

        } else {                                          // Choose an encoding.
            final String encoding = first(availableEncodings);
            if (null!=encoding) { // TODO: Can't we assume encoding is never NULL?
                if (!ContentEncoding.IDENTITY.toString().equals(encoding)) {
                    response.setContentEncoding(encoding);
                }
                response.addVariance(Header.CONTENT_ENCODING);
            }
            G07_resource_exists(resource, request, response);
        }
    }


    private void F07_acceptable_encoding_available(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        final String encoding =
            acceptEncoding(request, resource.getEncodings());

        if (null==encoding) {
            response.setStatus(Status.NOT_ACCEPTABLE);
        } else {
            if (!ContentEncoding.IDENTITY.toString().equals(encoding)) {
                response.setContentEncoding(encoding);
            }
            response.addVariance(Header.CONTENT_ENCODING);
            G07_resource_exists(resource, request, response);
        }
    }


    private void E05_accept_charset_header_exists(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        final Set<Charset> availableCharsets = resource.getCharsetsProvided();

        if (null==availableCharsets) {
            F06_accept_encoding_header_exists(resource, request, response);

        } else if (request.hasHeader(Header.ACCEPT_CHARSET)) {
            E06_acceptable_charset_available(resource, request, response);

        } else {
            final Charset cs = first(resource.getCharsetsProvided());
            if(null!=cs) {
                response.setCharset(cs);
                response.addVariance(Header.CONTENT_TYPE);
            }
            F06_accept_encoding_header_exists(resource, request, response);
        }
    }


    private void E06_acceptable_charset_available(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        final Charset charset =
            acceptCharset(request, resource.getCharsetsProvided());

        if (null==charset) {
            response.setStatus(Status.NOT_ACCEPTABLE);
        } else {
            response.setCharset(charset);
            response.addVariance(Header.CONTENT_TYPE);
            F06_accept_encoding_header_exists(resource, request, response);
        }
    }


    private void D04_accept_language_header_exists(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        final Set<LanguageTag> availableLanguages = resource.getLanguages();

        if(null==availableLanguages) {
            E05_accept_charset_header_exists(resource, request, response);

        } else if (request.hasHeader(Header.ACCEPT_LANGUAGE)) {
            D05_acceptable_language_available(resource, request, response);

        } else {
            final LanguageTag lt = first(resource.getLanguages());
            if (null!=lt) {
                response.setHeader(Header.CONTENT_LANGUAGE, lt.toString());
                response.addVariance(Header.CONTENT_LANGUAGE);
            }
            E05_accept_charset_header_exists(resource, request, response);
        }
    }


    private void D05_acceptable_language_available(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        final LanguageTag language =
            acceptLanguage(request, resource.getLanguages());

        if (null==language) {
            response.setStatus(Status.NOT_ACCEPTABLE);
        } else {
            response.setHeader(Header.CONTENT_LANGUAGE, language.toString());
            response.addVariance(Header.CONTENT_LANGUAGE);
            E05_accept_charset_header_exists(resource, request, response);
        }
    }


    private void C03_accept_header_present(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        final Set<MediaType> availTypes = resource.getContentTypesProvided();

        if (null==availTypes) {
            D04_accept_language_header_exists(resource, request, response);

        } else if (request.hasHeader(Header.ACCEPT)) {
            C04_acceptable_media_type_available(resource, request, response);

        } else {
            final MediaType mt = first(resource.getContentTypesProvided());
            if (null!=mt) {
                response.setMediaType(mt);
                response.addVariance(Header.CONTENT_TYPE);
            }
            D04_accept_language_header_exists(resource, request, response);
        }
    }


    private void C04_acceptable_media_type_available(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        final MediaType mediaType =
            accept(request, resource.getContentTypesProvided());

        if (null==mediaType) {
            response.setStatus(Status.NOT_ACCEPTABLE);
        } else {
            response.setMediaType(mediaType);
            response.addVariance(Header.CONTENT_TYPE);
            D04_accept_language_header_exists(resource, request, response);
        }
    }


    private void B11_uri_too_long(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (resource.isUriTooLong()) {
            response.setStatus(Status.REQUEST_URI_TOO_LONG);
        } else {
            B10_malformed_request(resource, request, response);
        }
    }


    private void B10_malformed_request(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (resource.isMalformed()) {
            response.setStatus(Status.BAD_REQUEST);
        } else {
            B09_authorized(resource, request, response);
        }
    }


    private void B09_authorized(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (null!=resource.authorize()) {
            response.setStatus(Status.UNAUTHORIZED);
            response.setHeader(
                Header.WWW_AUTHENTICATE, resource.authorize());
        } else {
            B08_forbidden(resource, request, response);
        }
    }


    private void B08_forbidden(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (resource.isForbidden()) {
            response.setStatus(Status.FORBIDDEN);
        } else {
            B07_unsupported_content_header(resource, request, response);
        }
    }


    private void B07_unsupported_content_header(final Resource resource,
                     final Request request,
                        final Response response) throws HttpException {
        if (!resource.hasValidContentHeaders()) {
            response.setStatus(Status.NOT_IMPLEMENTED);
        } else {
            B06_unknown_content_type(resource, request, response);
        }
    }


    private void B06_unknown_content_type(final Resource resource,
                     final Request request,
                        final Response response) throws HttpException {
        final String reqContentType = request.getHeader(CONTENT_TYPE);
        if ((null!=reqContentType // TODO: Should we reject if missing or only check for PUT & POST.
)
            && !resource.isContentTypeKnown(MediaType.parse(reqContentType))) {
            response.setStatus(Status.UNSUPPORTED_MEDIA_TYPE);
        } else {
            B05_request_entity_too_large(resource, request, response);
        }
    }


    private void B05_request_entity_too_large(final Resource resource,
                     final Request request,
                        final Response response) throws HttpException {
        if (!resource.isEntityLengthValid()) {
            response.setStatus(Status.REQUEST_ENTITY_TOO_LARGE);
        } else {
            B04_options(resource, request, response);
        }
    }


    private void B04_options(final Resource resource,
                     final Request request,
                        final Response response) throws HttpException {
        if (Method.OPTIONS==request.getMethod()) {
            response.setStatus(Status.OK);
            response.setHeader(Header.CONTENT_LENGTH, "0");    //$NON-NLS-1$
            response.setHeader(
                Header.ALLOW,
                Utils.join(resource.getAllowedMethods(), ',').toString());
        } else {
            B01_known_method(resource, request, response);
        }
    }


    private void B01_known_method(final Resource resource,
                     final Request request,
                        final Response response) throws HttpException {
        if (!Method.all().contains(request.getMethod())) {
            response.setStatus(Status.NOT_IMPLEMENTED);
        } else {
            C02_method_allowed_on_resource(resource, request, response);
        }
    }


    private void C02_method_allowed_on_resource(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (!resource.getAllowedMethods().contains(request.getMethod())) {
            response.setStatus(Status.METHOD_NOT_ALLOWED);
            response.setHeader(
                Header.ALLOW,
                Utils.join(resource.getAllowedMethods(), ',').toString());
        } else {
            C03_accept_header_present(resource, request, response);
        }
    }


    private void B12_service_available(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (!resource.isServiceAvailable()) {
            response.setStatus(Status.SERVICE_UNAVAILABLE);
        } else {
            B11_uri_too_long(resource, request, response);
        }
    }


    private void N16_is_POST_method(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (Method.POST==request.getMethod()) {
            N11_redirect(resource, request, response);
        } else {
            O16_is_PUT_method(resource, request, response);
        }
    }


    private void N11_redirect(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        final boolean postIsCreate = resource.isPostCreate();
        if (postIsCreate) {                                           // M7, N5
            final URI createUri = resource.getCreatePath();
            // FIXME: Dunno, see original source...
        } else {
            resource.processPost();
        }

        if (null!=response.getHeader(Header.LOCATION)) { // TODO: Add method 'requiresRedirection()'.
            response.setStatus(Status.SEE_OTHER);
        } else {
            P11_new_resource(resource, request, response);
        }
    }


    private void O16_is_PUT_method(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (Method.PUT==request.getMethod()) {
            O14_conflict(resource, request, response);
        } else {
            O18_multiple_representations(resource, request, response);
        }
    }


    private void O14_conflict(final Resource resource,
                     final Request request,
                     final Response response) throws HttpException {
        if (resource.isInConflict()) {
            response.setStatus(Status.CONFLICT);
        } else {
            processRequestBody(resource, request, response);
            P11_new_resource(resource, request, response);
        }
    }
}
