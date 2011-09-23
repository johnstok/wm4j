/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import static wm.Header.*;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import wm.headers.DateHeader;
import wm.negotiation.CharsetNegotiator;
import wm.negotiation.ContentNegotiator;
import wm.negotiation.LanguageNegotiator;
import wm.negotiation.MediaTypeNegotiator;


/**
 * Responsibility: implement the HTTP processing logic.
 *
 * @author Keith Webster Johnston.
 */
public class Engine {

    private MediaType accept(final Request request,
                             final Map<MediaType, ? extends BodyWriter> content_types_provided) {
        final List<WeightedValue> clientMediaTypes =
            MediaTypeNegotiator.parse(request.getHeader(Header.ACCEPT));
        return new MediaTypeNegotiator(content_types_provided.keySet()).select(clientMediaTypes);
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
        return negotiator.select(clientEncodings);
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
                                    final Response response) throws HttpException {
        final MediaType mt = MediaType.ANY; // FIXME: Extract media type.
        final BodyReader br = resource.content_types_accepted().get(mt); // TODO: Conneg required?
        try {
            br.read(resource._request.getBody());
        } catch (final IOException e) {
            // TODO Log exception.
            response.setStatus(Status.INTERNAL_SERVER_ERROR);
        }
    }


    private void P11_new_resource(
                     final Resource resource,
                     final Response response) throws HttpException {
        if (null==response.getHeader(Header.LOCATION)) {
            O20_response_includes_an_entity(resource, response);
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
            attachEtag(resource, response); // TODO: Use variant as per current request.
            attachLastModified(resource, response);
            // TODO: Provide an entity if available.
        }
    }


    private void O20_response_includes_an_entity(
                     final Resource resource,
                     final Response response) throws HttpException {
        if (response.hasBody()) {
            O18_multiple_representations(resource, response);
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
            attachEtag(resource, response); // TODO: Use variant as per current request.
            attachLastModified(resource, response);
        }
    }


    private void O18_multiple_representations(
                     final Resource resource,
                     final Response response) throws HttpException {
        // TODO: Set appropriate headers for response.
        if (resource.multiple_choices()) {
            response.setStatus(Status.MULTIPLE_CHOICES);
            // TODO: Write choices in body.
        } else {
            try {
                response.setStatus(Status.OK);

                attachEtag(resource, response);
                attachLastModified(resource, response);

                // TODO: Set 'Expires' header.

                response.write(resource.content_types_provided().get(response.getMediaType()));
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
                                    final Response response) {

        final Date lastModified = resource.last_modified();
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
                            final Response response) {
        // FIXME: ETags depend on variants.
        final ETag eTag = resource.generate_etag();
        if (null!=eTag) { response.setHeader(E_TAG, eTag.getValue()); }
    }


    // FIXME: Remove response param - retrieve via resource instead.
    public final void process(final Resource resource,
                              final Response response) {
        try {
            response.setHeader(Header.SERVER, "wm4j/1.0.0");
            response.setHeader(Header.DATE, response.getOriginationTime());
            B12_service_available(resource, response);
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
                     final Response response) throws HttpException {
        // TODO: Set variances.
        if (resource.exists()) {
            G07a(resource, response);
        } else {
            H07_if_match_is_wildcard(resource, response);
        }
    }


    private void H07_if_match_is_wildcard(final Resource resource,
                     final Response response) throws HttpException {
        if ("*".equals(resource._request.getHeader(Header.IF_MATCH))) {
            response.setStatus(Status.PRECONDITION_FAILED);
        } else {
            I07_is_PUT_method(resource, response);
        }
    }


    private void G07a(final Resource resource,
                      final Response response) throws HttpException { // Added to support redirect for existing resources - confirm logic.
        final URI tempUri = resource.moved_temporarily();
        if (null!=tempUri) {
            response.setStatus(Status.TEMPORARY_REDIRECT);
            response.setHeader(Header.LOCATION, tempUri.toString()); // TODO: Confirm serialisation of URIs
        } else {
            G08_if_match_is_wildcard(resource,response);
        }
    }


    private void G08_if_match_is_wildcard(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.hasHeader(Header.IF_MATCH)) {
            G09_if_match_header_exists(resource, response);
        } else {
            H10_if_unmodified_since_exists(resource, response);
        }
    }


    private void G09_if_match_header_exists(final Resource resource,
                     final Response response) throws HttpException {
        if ("*".equals(resource._request.getHeader(Header.IF_MATCH))) {
            H10_if_unmodified_since_exists(resource, response);
        } else {
            G11_etag_in_if_match(resource, response);
        }
    }


    private void G11_etag_in_if_match(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.getHeader(Header.IF_MATCH).equals(resource.generate_etag().getValue())) {
            H10_if_unmodified_since_exists(resource, response);
        } else {
            response.setStatus(Status.PRECONDITION_FAILED);
        }
    }


    private void L13_if_modified_since_exists(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.hasHeader(Header.IF_MODIFIED_SINCE)) {
            L14_if_modified_since_is_valid(resource, response);
        } else {
            M16_is_DELETE_method(resource, response);
        }

    }


    private void L14_if_modified_since_is_valid(final Resource resource,
                     final Response response) throws HttpException {
        if (DateHeader.isValidDate(resource._request.getHeader(Header.IF_MODIFIED_SINCE))) {
            L15_if_modified_since_after_now(resource, response);
        } else {
            M16_is_DELETE_method(resource, response);
        }
    }


    private void L15_if_modified_since_after_now(final Resource resource,
                     final Response response) throws HttpException {
        if (DateHeader.parse(resource._request.getHeader(Header.IF_MODIFIED_SINCE)).after(new Date())) { // TODO: Compare with message origination rather than 'now'?
            M16_is_DELETE_method(resource, response);
        } else {
            L17_last_modified_after_if_modified_since(resource, response);
        }
    }


    private void L17_last_modified_after_if_modified_since(final Resource resource,
                     final Response response) throws HttpException {
        if (DateHeader.parse(resource._request.getHeader(Header.IF_MODIFIED_SINCE)).before(resource.last_modified())) {
            M16_is_DELETE_method(resource, response);
        } else {
            response.setStatus(Status.NOT_MODIFIED);
        }
    }


    private void I12_if_none_match_exists(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.hasHeader(Header.IF_NONE_MATCH)) {
            I13_if_none_match_is_wildcard(resource, response);
        } else {
            L13_if_modified_since_exists(resource, response);
        }
    }


    private void I13_if_none_match_is_wildcard(final Resource resource,
                     final Response response) throws HttpException {
        if ("*".equals(resource._request.getHeader(Header.IF_NONE_MATCH))) {
            J18_GET_or_HEAD(resource, response);
        } else {
            K13_etag_in_if_none_match(resource, response);
        }
    }


    private void J18_GET_or_HEAD(final Resource resource,
                     final Response response) {
        if (Method.GET.equals(resource._request.getMethod())
            || Method.HEAD.equals(resource._request.getMethod())) {
            response.setStatus(Status.NOT_MODIFIED);
        } else {
            response.setStatus(Status.PRECONDITION_FAILED);
        }
    }


    private void K13_etag_in_if_none_match(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.getHeader(Header.IF_NONE_MATCH).equals(resource.generate_etag().getValue())) {
            J18_GET_or_HEAD(resource, response);
        } else {
            L13_if_modified_since_exists(resource, response);
        }
    }


    private void H10_if_unmodified_since_exists(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.hasHeader(Header.IF_UNMODIFIED_SINCE)) {
            H11_if_unmodified_since_is_valid(resource, response);
        } else {
            I12_if_none_match_exists(resource, response);
        }
    }


    private void H11_if_unmodified_since_is_valid(final Resource resource,
                     final Response response) throws HttpException {
        if (DateHeader.isValidDate(resource._request.getHeader(Header.IF_UNMODIFIED_SINCE))) {
            H12_last_modified_after_if_unmodified_since(resource, response);
        } else {
            I12_if_none_match_exists(resource, response);
        }
    }


    private void H12_last_modified_after_if_unmodified_since(final Resource resource,
                     final Response response) throws HttpException {
        if (DateHeader.parse(resource._request.getHeader(Header.IF_UNMODIFIED_SINCE)).before(resource.last_modified())) {
            response.setStatus(Status.PRECONDITION_FAILED);
        } else {
            I12_if_none_match_exists(resource, response);
        }
    }


    private void I07_is_PUT_method(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.PUT.equals(resource._request.getMethod())) {
            I04_apply_request_to_another_uri(resource, response);
        } else {
            K07_previously_existed(resource, response);
        }
    }


    private void K07_previously_existed(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.existedPreviously()) {
            K05_moved_permanently(resource, response);
        } else {
            L07_is_POST_method(resource, response);
        }
    }


    private void K05_moved_permanently(final Resource resource,
                     final Response response) throws HttpException {
        final URI permUri = resource.moved_permanently();
        if (null!=permUri) {
            response.setStatus(Status.MOVED_PERMANENTLY);
            response.setHeader(Header.LOCATION, permUri.toString()); // TODO: Confirm serialisation of URIs
        } else {
            L05_moved_temporarily(resource, response);
        }
    }


    private void L07_is_POST_method(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.POST==resource._request.getMethod() && resource.allow_missing_post()) { // L7, M7
            N11_redirect(resource, response);
        } else {
            response.setStatus(Status.NOT_FOUND);
        }
    }


    private void L05_moved_temporarily(final Resource resource,
                     final Response response) throws HttpException {
        final URI tempUri = resource.moved_temporarily();
        if (null!=tempUri) {
            response.setStatus(Status.TEMPORARY_REDIRECT);
            response.setHeader(Header.LOCATION, tempUri.toString()); // TODO: Confirm serialisation of URIs
        } else {
                M05_is_POST_method(resource, response);
        }
    }


    private void M05_is_POST_method(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.POST==resource._request.getMethod() && resource.allow_missing_post()) { // M5, N5
            N11_redirect(resource, response);
        } else {
            response.setStatus(Status.GONE);
        }
    }


    private void P03_conflict(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.is_conflict()) {
            response.setStatus(Status.CONFLICT);
        } else {
            processRequestBody(resource, response);
            P11_new_resource(resource, response);
        }
    }


    private void I04_apply_request_to_another_uri(final Resource resource,
                     final Response response) throws HttpException {
        final URI putUri = resource.moved_permanently();
        if (null!=putUri) {
            response.setStatus(Status.MOVED_PERMANENTLY);
            response.setHeader(Header.LOCATION, putUri.toString()); // TODO: Confirm serialisation of URIs
        } else {
            P03_conflict(resource, response);
        }
    }


    private void M16_is_DELETE_method(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.DELETE==resource._request.getMethod()) {  // M16
            // FIXME: Something fishy here - no O20 decision.
            final boolean accepted = resource.delete_resource();
            if (accepted) {
                final boolean enacted = resource.delete_completed();
                if (!enacted) {                                   // M20
                    response.setStatus(Status.ACCEPTED);
                } else {
                    response.setStatus(Status.NO_CONTENT);
                }
            }
        } else {
            N16_is_POST_method(resource, response);
        }
    }


    private void F06_accept_encoding_header_exists(final Resource resource,
                     final Response response) throws HttpException {
        final String enc = first(resource.encodings_provided());
        if (resource._request.hasHeader(Header.ACCEPT_ENCODING) && null!=enc) {
            F07_acceptable_encoding_available(resource, response);
        } else {
            response.setContentEncoding(enc);
            G07_resource_exists(resource, response);
        }
    }


    private void F07_acceptable_encoding_available(final Resource resource,
                     final Response response) throws HttpException {
        final String encoding =
            acceptEncoding(resource._request, resource.encodings_provided());
        if (null==encoding) {
            response.setStatus(Status.NOT_ACCEPTABLE);
        } else {
            response.setContentEncoding(encoding);
            G07_resource_exists(resource, response);
        }
    }


    private void E05_accept_charset_header_exists(final Resource resource,
                     final Response response) throws HttpException {
        final Charset cs = first(resource.charsets_provided());
        if (resource._request.hasHeader(Header.ACCEPT_CHARSET) && null!=cs) {
            E06_acceptable_charset_available(resource, response);
        } else {
            response.setCharset(cs);
            F06_accept_encoding_header_exists(resource, response);
        }
    }


    private void E06_acceptable_charset_available(final Resource resource,
                     final Response response) throws HttpException {
        final Charset charset =
            acceptCharset(resource._request, resource.charsets_provided());
        if (null==charset) {
            response.setStatus(Status.NOT_ACCEPTABLE);
        } else {
            response.setCharset(charset);
            F06_accept_encoding_header_exists(resource, response);
        }
    }


    private void D04_accept_language_header_exists(final Resource resource,
                     final Response response) throws HttpException {
        final LanguageTag lt = first(resource.languages_provided());
        if (resource._request.hasHeader(Header.ACCEPT_LANGUAGE) && null!=lt) {
            D05_acceptable_language_available(resource, response);
        } else {
            if (null!=lt) {
                response.setHeader(Header.CONTENT_LANGUAGE, lt.toString());
            }
            E05_accept_charset_header_exists(resource, response);
        }
    }


    private void D05_acceptable_language_available(final Resource resource,
                     final Response response) throws HttpException {
        final LanguageTag language =
            acceptLanguage(resource._request, resource.languages_provided());
        if (null==language) {
            response.setStatus(Status.NOT_ACCEPTABLE);
        } else {
            response.setHeader(Header.CONTENT_LANGUAGE, language.toString());
            E05_accept_charset_header_exists(resource, response);
        }
    }


    private void C03_accept_header_present(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.hasHeader(Header.ACCEPT)) {
            C04_acceptable_media_type_available(resource, response);
        } else {
            final MediaType mt = first(resource.content_types_provided().keySet());
            if (null!=mt) {
                response.setMediaType(mt);
            }
            D04_accept_language_header_exists(resource, response);
        }
    }


    private void C04_acceptable_media_type_available(final Resource resource,
                     final Response response) throws HttpException {
        final MediaType mediaType =
            accept(resource._request, resource.content_types_provided());
        if (null==mediaType) {
            response.setStatus(Status.NOT_ACCEPTABLE);
        } else {
            response.setMediaType(mediaType);
            D04_accept_language_header_exists(resource, response);
        }
    }


    private void B11_uri_too_long(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.isUriTooLong()) {
            response.setStatus(Status.REQUEST_URI_TOO_LONG);
        } else {
            B10_malformed_request(resource, response);
        }
    }


    private void B10_malformed_request(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.malformed_request()) {
            response.setStatus(Status.BAD_REQUEST);
        } else {
            B09_authorized(resource, response);
        }
    }


    private void B09_authorized(final Resource resource,
                     final Response response) throws HttpException {
        if (!resource.is_authorized()) {
            response.setStatus(Status.UNAUTHORIZED);
        } else {
            B08_forbidden(resource, response);
        }
    }


    private void B08_forbidden(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.forbidden()) {
            response.setStatus(Status.FORBIDDEN);
        } else {
            B07_unsupported_content_header(resource, response);
        }
    }


    private void B07_unsupported_content_header(final Resource resource,
                        final Response response) throws HttpException {
        if (!resource.hasValidContentHeaders()) {
            response.setStatus(Status.NOT_IMPLEMENTED);
        } else {
            B06_unknown_content_type(resource, response);
        }
    }


    private void B06_unknown_content_type(final Resource resource,
                        final Response response) throws HttpException {
        if (!resource.known_content_type()) {
            response.setStatus(Status.UNSUPPORTED_MEDIA_TYPE);
        } else {
            B05_request_entity_too_large(resource, response);
        }
    }


    private void B05_request_entity_too_large(final Resource resource,
                        final Response response) throws HttpException {
        if (!resource.isEntityLengthValid()) {
            response.setStatus(Status.REQUEST_ENTITY_TOO_LARGE);
        } else {
            B04_options(resource, response);
        }
    }


    private void B04_options(final Resource resource,
                        final Response response) throws HttpException {
        if (Method.OPTIONS==resource._request.getMethod()) {
            response.setStatus(Status.OK);
            response.setHeader(Header.CONTENT_LENGTH, "0");    //$NON-NLS-1$
            response.setHeader(
                Header.ALLOW,
                Utils.join(resource.allowed_methods(), ',').toString());
        } else {
            B01_known_method(resource, response);
        }
    }


    private void B01_known_method(final Resource resource,
                        final Response response) throws HttpException {
        if (!Method.all().contains(resource._request.getMethod())) {
            response.setStatus(Status.NOT_IMPLEMENTED);
        } else {
            C02_method_allowed_on_resource(resource, response);
        }
    }


    private void C02_method_allowed_on_resource(final Resource resource,
                     final Response response) throws HttpException {
        if (!resource.allowed_methods().contains(resource._request.getMethod())) {
            response.setStatus(Status.METHOD_NOT_ALLOWED);
            response.setHeader(
                Header.ALLOW,
                Utils.join(resource.allowed_methods(), ',').toString());
        } else {
            C03_accept_header_present(resource, response);
        }
    }


    private void B12_service_available(final Resource resource,
                     final Response response) throws HttpException {
        if (!resource.isServiceAvailable()) {
            response.setStatus(Status.SERVICE_UNAVAILABLE);
        } else {
            B11_uri_too_long(resource, response);
        }
    }


    private void N16_is_POST_method(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.POST==resource._request.getMethod()) {
            N11_redirect(resource, response);
        } else {
            O16_is_PUT_method(resource, response);
        }
    }


    private void N11_redirect(final Resource resource,
                     final Response response) throws HttpException {
        final boolean postIsCreate = resource.isPostCreate();
        if (postIsCreate) {                                           // M7, N5
            final URI createUri = resource.createPath();
            // FIXME: Dunno, see original source...
        } else {
            resource.processPost();
        }

        if (null!=response.getHeader(Header.LOCATION)) { // TODO: Add method 'requiresRedirection()'.
            response.setStatus(Status.SEE_OTHER);
        } else {
            P11_new_resource(resource, response);
        }
    }


    private void O16_is_PUT_method(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.PUT==resource._request.getMethod()) {
            O14_conflict(resource, response);
        } else {
            O18_multiple_representations(resource, response);
        }
    }


    private void O14_conflict(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.is_conflict()) {
            response.setStatus(Status.CONFLICT);
        } else {
            processRequestBody(resource, response);
            P11_new_resource(resource, response);
        }
    }
}
