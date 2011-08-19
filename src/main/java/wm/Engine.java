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
            MediaTypeNegotiator.parse(request.get_req_header(Header.ACCEPT));
        return new MediaTypeNegotiator(content_types_provided.keySet()).select(clientMediaTypes);
    }


    private Charset acceptCharset(final Request request,
                                  final Set<Charset> charsets_provided) {
        final List<WeightedValue> clientCharsets =
            Header.parseAcceptCharset(
                request.get_req_header(Header.ACCEPT_CHARSET));
        return new CharsetNegotiator(charsets_provided).select(clientCharsets);
    }


    private String acceptEncoding(final Request request,
                                  final Set<String> encodings_provided) {
        final List<WeightedValue> clientEncodings =
            Header.parseAcceptEncoding(
                request.get_req_header(Header.ACCEPT_ENCODING));
        final ContentNegotiator negotiator = new ContentNegotiator(encodings_provided);
        return negotiator.select(clientEncodings);
    }


    private LanguageTag acceptLanguage(final Request request,
                                       final Set<LanguageTag> languages_provided) {
        final List<WeightedValue> clientLanguages =
            LanguageNegotiator.parse(request.get_req_header(Header.ACCEPT_LANGUAGE));
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
            br.read(resource._request.get_req_body_stream());
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
            attachEtag(resource, response); // Use variant as per current request.
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
            response.setStatus(Status.NO_CONTENT);
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


    public final void process(final Resource resource,
                              final Response response) {
        try {
            response.setHeader(Header.SERVER, "wm4j/1.0.0");
            response.setHeader(Header.DATE, response.getOriginationTime());
            B12(resource, response);
        } catch (final HttpException e) {
            // TODO handle committed responses.
            response.setStatus(Status.INTERNAL_SERVER_ERROR);
        } catch (final RuntimeException e) {
            // TODO handle committed responses.
            response.setStatus(Status.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }


    private void G07(final Resource resource,
                     final Response response) throws HttpException {
        // TODO: Set variances.
        if (resource.resource_exists()) {
            G07a(resource, response);
        } else {
            H07(resource, response);
        }
    }


    private void H07(final Resource resource,
                     final Response response) throws HttpException {
        if ("*".equals(resource._request.get_req_header(Header.IF_MATCH))) {
            response.setStatus(Status.PRECONDITION_FAILED);
        } else {
            I07(resource, response);
        }
    }


    private void G07a(final Resource resource,
                      final Response response) throws HttpException { // Added to support redirect for existing resources - confirm logic.
        final URI tempUri = resource.moved_temporarily();
        if (null!=tempUri) {
            response.setStatus(Status.TEMPORARY_REDIRECT);
            response.setHeader(Header.LOCATION, tempUri.toString()); // TODO: Confirm serialisation of URIs
        } else {
            GO8(resource,response);
        }
    }


    private void GO8(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.hasHeader(Header.IF_MATCH)) {
            G09(resource, response);
        } else {
            H10(resource, response);
        }
    }


    private void G09(final Resource resource,
                     final Response response) throws HttpException {
        if ("*".equals(resource._request.get_req_header(Header.IF_MATCH))) {
            H10(resource, response);
        } else {
            G11(resource, response);
        }
    }


    private void G11(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.get_req_header(Header.IF_MATCH).equals(resource.generate_etag().getValue())) {
            H10(resource, response);
        } else {
            response.setStatus(Status.PRECONDITION_FAILED);
        }
    }


    private void L13(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.hasHeader(Header.IF_MODIFIED_SINCE)) {
            L14(resource, response);
        } else {
            M16(resource, response);
        }

    }


    private void L14(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.isValidDate(Header.IF_MODIFIED_SINCE)) {
            L15(resource, response);
        } else {
            M16(resource, response);
        }
    }


    private void L15(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.get_req_header_date(Header.IF_MODIFIED_SINCE).after(new Date())) { // TODO: Compare with message origination rather than 'now'?
            M16(resource, response);
        } else {
            L17(resource, response);
        }
    }


    private void L17(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.get_req_header_date(Header.IF_MODIFIED_SINCE).before(resource.last_modified())) {
            M16(resource, response);
        } else {
            response.setStatus(Status.NOT_MODIFIED);
        }
    }


    private void I12(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.hasHeader(Header.IF_NONE_MATCH)) {
            I13(resource, response);
        } else {
            L13(resource, response);
        }
    }


    private void I13(final Resource resource,
                     final Response response) throws HttpException {
        if ("*".equals(resource._request.get_req_header(Header.IF_NONE_MATCH))) {
            J18(resource, response);
        } else {
            K13(resource, response);
        }
    }


    private void J18(final Resource resource,
                     final Response response) {
        if (Method.GET.equals(resource._request.get_req_method())
            || Method.HEAD.equals(resource._request.get_req_method())) {
            response.setStatus(Status.NOT_MODIFIED);
        } else {
            response.setStatus(Status.PRECONDITION_FAILED);
        }
    }


    private void K13(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.get_req_header(Header.IF_NONE_MATCH).equals(resource.generate_etag().getValue())) {
            J18(resource, response);
        } else {
            L13(resource, response);
        }
    }


    private void H10(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.hasHeader(Header.IF_UNMODIFIED_SINCE)) {
            H11(resource, response);
        } else {
            I12(resource, response);
        }
    }


    private void H11(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.isValidDate(Header.IF_UNMODIFIED_SINCE)) {
            H12(resource, response);
        } else {
            I12(resource, response);
        }
    }


    private void H12(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.get_req_header_date(Header.IF_UNMODIFIED_SINCE).before(resource.last_modified())) {
            response.setStatus(Status.PRECONDITION_FAILED);
        } else {
            I12(resource, response);
        }
    }


    private void I07(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.PUT.equals(resource._request.get_req_method())) {
            I04(resource, response);
        } else {
            K07(resource, response);
        }
    }


    private void K07(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.previously_existed()) {
            K05(resource, response);
        } else {
            L07(resource, response);
        }
    }


    private void K05(final Resource resource,
                     final Response response) throws HttpException {
        final URI permUri = resource.moved_permanently();
        if (null!=permUri) {
            response.setStatus(Status.MOVED_PERMANENTLY);
            response.setHeader(Header.LOCATION, permUri.toString()); // TODO: Confirm serialisation of URIs
        } else {
            L05(resource, response);
        }
    }


    private void L07(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.POST==resource._request.get_req_method() && resource.allow_missing_post()) { // L7, M7
            N11(resource, response);
        } else {
            response.setStatus(Status.NOT_FOUND);
        }
    }


    private void L05(final Resource resource,
                     final Response response) throws HttpException {
        final URI tempUri = resource.moved_temporarily();
        if (null!=tempUri) {
            response.setStatus(Status.TEMPORARY_REDIRECT);
            response.setHeader(Header.LOCATION, tempUri.toString()); // TODO: Confirm serialisation of URIs
        } else {
                M05(resource, response);
        }
    }


    private void M05(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.POST==resource._request.get_req_method() && resource.allow_missing_post()) { // M5, N5
            N11(resource, response);
        } else {
            response.setStatus(Status.GONE);
        }
    }


    private void P03(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.is_conflict()) {
            response.setStatus(Status.CONFLICT);
        } else {
            processRequestBody(resource, response);
            P11_new_resource(resource, response);
        }
    }


    private void I04(final Resource resource,
                     final Response response) throws HttpException {
        final URI putUri = resource.moved_permanently();
        if (null!=putUri) {
            response.setStatus(Status.MOVED_PERMANENTLY);
            response.setHeader(Header.LOCATION, putUri.toString()); // TODO: Confirm serialisation of URIs
        } else {
            P03(resource, response);
        }
    }


    private void M16(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.DELETE==resource._request.get_req_method()) {  // M16
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
            N16(resource, response);
        }
    }


    private void F06(final Resource resource,
                     final Response response) throws HttpException {
        final String enc = first(resource.encodings_provided());
        if (resource._request.hasHeader(Header.ACCEPT_ENCODING) && null!=enc) {
            F07(resource, response);
        } else {
            response.setContentEncoding(enc);
            G07(resource, response);
        }
    }


    private void F07(final Resource resource,
                     final Response response) throws HttpException {
        final String encoding =
            acceptEncoding(resource._request, resource.encodings_provided());
        if (null==encoding) {
            response.setStatus(Status.NOT_ACCEPTABLE);
        } else {
            response.setContentEncoding(encoding);
            G07(resource, response);
        }
    }


    private void E05(final Resource resource,
                     final Response response) throws HttpException {
        final Charset cs = first(resource.charsets_provided());
        if (resource._request.hasHeader(Header.ACCEPT_CHARSET) && null!=cs) {
            E06(resource, response);
        } else {
            response.setCharset(cs);
            F06(resource, response);
        }
    }


    private void E06(final Resource resource,
                     final Response response) throws HttpException {
        final Charset charset =
            acceptCharset(resource._request, resource.charsets_provided());
        if (null==charset) {
            response.setStatus(Status.NOT_ACCEPTABLE);
        } else {
            response.setCharset(charset);
            F06(resource, response);
        }
    }


    private void D04(final Resource resource,
                     final Response response) throws HttpException {
        final LanguageTag lt = first(resource.languages_provided());
        if (resource._request.hasHeader(Header.ACCEPT_LANGUAGE) && null!=lt) {
            D05(resource, response);
        } else {
            if (null!=lt) {
                response.setHeader(Header.CONTENT_LANGUAGE, lt.toString());
            }
            E05(resource, response);
        }
    }


    private void D05(final Resource resource,
                     final Response response) throws HttpException {
        final LanguageTag language =
            acceptLanguage(resource._request, resource.languages_provided());
        if (null==language) {
            response.setStatus(Status.NOT_ACCEPTABLE);
        } else {
            response.setHeader(Header.CONTENT_LANGUAGE, language.toString());
            E05(resource, response);
        }
    }


    private void C03(final Resource resource,
                     final Response response) throws HttpException {
        if (resource._request.hasHeader(Header.ACCEPT)) {
            C04(resource, response);
        } else {
            final MediaType mt = first(resource.content_types_provided().keySet());
            if (null!=mt) {
                response.setMediaType(mt);
            }
            D04(resource, response);
        }
    }


    private void C04(final Resource resource,
                     final Response response) throws HttpException {
        final MediaType mediaType =
            accept(resource._request, resource.content_types_provided());
        if (null==mediaType) {
            response.setStatus(Status.NOT_ACCEPTABLE);
        } else {
            response.setMediaType(mediaType);
            D04(resource, response);
        }
    }


    private void B11(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.uri_too_long()) {
            response.setStatus(Status.REQUEST_URI_TOO_LONG);
        } else {
            B10(resource, response);
        }
    }


    private void B10(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.malformed_request()) {
            response.setStatus(Status.BAD_REQUEST);
        } else {
            B09(resource, response);
        }
    }


    private void B09(final Resource resource,
                     final Response response) throws HttpException {
        if (!resource.is_authorized()) {
            response.setStatus(Status.UNAUTHORIZED);
        } else {
            B08(resource, response);
        }
    }


    private void B08(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.forbidden()) {
            response.setStatus(Status.FORBIDDEN);
        } else {
            B07(resource, response);
        }
    }


    private void B07(final Resource resource,
                        final Response response) throws HttpException {
        if (!resource.valid_content_headers()) {
            response.setStatus(Status.NOT_IMPLEMENTED);
        } else {
            B06(resource, response);
        }
    }


    private void B06(final Resource resource,
                        final Response response) throws HttpException {
        if (!resource.known_content_type()) {
            response.setStatus(Status.UNSUPPORTED_MEDIA_TYPE);
        } else {
            B05(resource, response);
        }
    }


    private void B05(final Resource resource,
                        final Response response) throws HttpException {
        if (!resource.valid_entity_length()) {
            response.setStatus(Status.REQUEST_ENTITY_TOO_LARGE);
        } else {
            B04(resource, response);
        }
    }


    private void B04(final Resource resource,
                        final Response response) throws HttpException {
        if (Method.OPTIONS==resource._request.get_req_method()) {
            response.setStatus(Status.OK);
            response.setHeader(Header.CONTENT_LENGTH, "0");    //$NON-NLS-1$
            response.setHeader(
                Header.ALLOW,
                Utils.join(resource.allowed_methods(), ',').toString());
        } else {
            B01(resource, response);
        }
    }


    private void B01(final Resource resource,
                        final Response response) throws HttpException {
        if (!Method.all().contains(resource._request.get_req_method())) {
            response.setStatus(Status.NOT_IMPLEMENTED);
        } else {
            C02(resource, response);
        }
    }


    private void C02(final Resource resource,
                     final Response response) throws HttpException {
        if (!resource.allowed_methods().contains(resource._request.get_req_method())) {
            response.setStatus(Status.METHOD_NOT_ALLOWED);
            response.setHeader(
                Header.ALLOW,
                Utils.join(resource.allowed_methods(), ',').toString());
        } else {
            C03(resource, response);
        }
    }


    private void B12(final Resource resource,
                     final Response response) throws HttpException {
        if (!resource.service_available()) {
            response.setStatus(Status.SERVICE_UNAVAILABLE);
        } else {
            B11(resource, response);
        }
    }


    private void N16(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.POST==resource._request.get_req_method()) {
            N11(resource, response);
        } else {
            O16(resource, response);
        }
    }


    private void N11(final Resource resource,
                     final Response response) throws HttpException {
        final boolean postIsCreate = resource.post_is_create();
        if (postIsCreate) {                                           // M7, N5
            final URI createUri = resource.create_path();
            // FIXME: Dunno, see original source...
        } else {
            resource.process_post();
        }

        if (resource._request.get_resp_redirect()) {
            // TODO: Confirm Location header has been set.
            response.setStatus(Status.SEE_OTHER);
        } else {
            P11_new_resource(resource, response);
        }
    }


    private void O16(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.PUT==resource._request.get_req_method()) {
            O14(resource, response);
        } else {
            O18_multiple_representations(resource, response);
        }
    }


    private void O14(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.is_conflict()) {
            response.setStatus(Status.CONFLICT);
        } else {
            P11_new_resource(resource, response);
        }
    }
}
