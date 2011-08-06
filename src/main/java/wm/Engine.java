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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;


/**
 * Responsibility: implement the HTTP processing logic.
 *
 * @author Keith Webster Johnston.
 */
public class Engine {

    /**
     * TODO: Add a description for this method.
     *
     * @param request
     * @param content_types_provided
     * @return
     */
    private MediaType accept(final Request request,
                             final Map<MediaType, BodyWriter> content_types_provided) {
        // Order Accept values by q value then specificity
        return null;
    }


    /**
     * TODO: Add a description for this method.
     *
     * @param request
     * @param charsets_provided
     * @return
     */
    private Charset acceptCharset(final Request request,
                                  final Set<Charset> charsets_provided) {
        final List<WeightedValue> clientCharsets =
            Header.parseAcceptCharset(
                request.get_req_header(Header.ACCEPT_CHARSET));
        return new CharsetNegotiator(charsets_provided).selectCharset(clientCharsets);
    }


    /**
     * TODO: Add a description for this method.
     *
     * @param request
     * @param encodings_provided
     * @return
     */
    private ContentEncoding acceptEncoding(final Request request,
                                           final Set<String> encodings_provided) {
        return null;
    }


    /**
     * TODO: Add a description for this method.
     *
     * @param request
     * @param languages_provided
     * @return
     */
    private Locale acceptLanguage(final Request request,
                                  final Set<Locale> languages_provided) {
        return null;
    }


    private boolean exists(final String headerName, final Request request) {
        return null!=request.get_req_header(headerName);
    }


    private void P11(final Resource resource,
                     final Response response) throws HttpException {
        if (null==response.getHeader(Header.LOCATION)) {
            O20(resource, response);
        } else {
            response.setStatus(Status.CREATED);
        }
    }


    private void O20(final Resource resource,
                     final Response response) throws HttpException {
        if (response.hasBody()) {
            O18(resource, response);
        } else {
            response.setStatus(Status.NO_CONTENT);
        }
    }


    private void O18(final Resource resource,
                     final Response response) throws HttpException {
        // TODO: Set appropriate headers for response.
        if (resource.multiple_choices()) {
            response.setStatus(Status.MULTIPLE_CHOICES);
            // TODO: Write choices in body.
        } else {
            try {
                response.setStatus(Status.OK);

                // ETag 14.19
                final String eTag = resource.generate_etag().getValue();
                if (null!=eTag) { response.setHeader(E_TAG, eTag); }

                // Last-Modified 14.29
                // FIXME: Invalid strings not handled well; is max-long an appropriate default?
                try {
                    final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
                    dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                    final String requestDateString = response.getHeader(Header.DATE);
                    final long messageDate =
                        (null==requestDateString) ? Long.MAX_VALUE : dateFormatter.parse(requestDateString).getTime();
                    final long lastModified = resource.last_modified().getTime();
                    response.setHeader(
                        Header.LAST_MODIFIED,
                        dateFormatter.format((lastModified<messageDate) ? new Date(lastModified) : new Date(messageDate)));
                } catch (final ParseException e) {
                    // TODO Auto-generated catch block.
                    throw new HttpException(e);
                }

                response.write(resource.content_types_provided().entrySet().iterator().next().getValue()); // FIXME: Awful.
            } catch (final IOException e) {
                // TODO handle committed responses.
                response.setStatus(Status.INTERNAL_SERVER_ERROR);
            }
            response.setStatus(Status.OK);
        }
    }


    public final void process(final Resource resource,
                                 final Response response) {
        try {
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
        if (resource.resource_exists()) {
            G07a(resource, response);
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
            M16(resource, response);
        }
    }


    private void I07(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.PUT==resource._request.get_req_method()) {
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
            P11(resource, response);
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
        if (exists(Header.ACCEPT_ENCODING, resource._request)) {
            final ContentEncoding encoding =
                acceptEncoding(resource._request, resource.encodings_provided());
            if (null==encoding) {                                     // F7
                response.setStatus(Status.NOT_ACCEPTABLE);
            }
        } else {
            G07(resource, response);
        }
    }


    private void E05(final Resource resource,
                     final Response response) throws HttpException {
        if (exists(Header.ACCEPT_CHARSET, resource._request)) {
            final Charset charset =
                acceptCharset(resource._request, resource.charsets_provided());
            if (null==charset) {                                      // E6
                response.setStatus(Status.NOT_ACCEPTABLE);
            }
        } else {
            F06(resource, response);
        }
    }


    private void D04(final Resource resource,
                     final Response response) throws HttpException {
        if (exists(Header.ACCEPT_LANGUAGE, resource._request)) {
            final Locale language =
                acceptLanguage(resource._request, resource.languages_provided());
            if (null==language) {                                     // D5
                response.setStatus(Status.NOT_ACCEPTABLE);
            }
        } else {
            E05(resource, response);
        }
    }


    private void C03(final Resource resource,
                     final Response response) throws HttpException {
        if (exists(Header.ACCEPT, resource._request)) {
            final MediaType mediaType =
                accept(
                    resource._request, resource.content_types_provided());
            if (null==mediaType) {                                    // C4
                response.setStatus(Status.NOT_ACCEPTABLE);
            }
        } else {
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
            P11(resource, response);
        }
    }


    private void O16(final Resource resource,
                     final Response response) throws HttpException {
        if (Method.PUT==resource._request.get_req_method()) {
            O14(resource, response);
        } else {
            O18(resource, response);
        }
    }


    private void O14(final Resource resource,
                     final Response response) throws HttpException {
        if (resource.is_conflict()) {
            response.setStatus(Status.CONFLICT);
        } else {
            P11(resource, response);
        }
    }
}
