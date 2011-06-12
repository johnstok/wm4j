/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.simpleframework.http.Status;


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


    /**
     * TODO: Add a description for this method.
     *
     * @param resource
     * @param response
     * @throws HttpException
     */
    private void handlePost(final Resource resource, final Response response)
        throws HttpException {
        final boolean postIsCreate = resource.post_is_create();
        if (postIsCreate) {
            final URI createUri = resource.create_path();
            // Handle as a PUT
            // Return 303 if createUri is not NULL, 201 otherwise
            // Check for conflicts
        }
        resource.process_post();
        response.setStatus(Status.OK);
        return;
    }


    protected final void process(final Resource resource,
                                 final Response response) {
        try {
            if (!resource.service_available()) {
                response.setStatus(Status.SERVICE_UNAVAILABLE);
                return;
            }
            if (resource.uri_too_long()) {
                response.setStatus(Status.REQUEST_URI_TOO_LONG);
                return;
            }
            if (resource.malformed_request()) {
                response.setStatus(Status.BAD_REQUEST);
                return;
            }
            if (!resource.is_authorized()) {
                response.setStatus(Status.UNAUTHORIZED);
                return;
            }
            if (resource.forbidden()) {
                response.setStatus(Status.FORBIDDEN);
                return;
            }
            if (!resource.valid_content_headers()) {
                response.setStatus(Status.NOT_IMPLEMENTED);
                return;
            }
            if (!resource.known_content_type()) {
                response.setStatus(Status.UNSUPPORTED_MEDIA_TYPE);
                return;
            }
            if (!resource.valid_entity_length()) {
                response.setStatus(Status.REQUEST_ENTITY_TOO_LARGE);
                return;
            }
            if (Method.OPTIONS==resource._request.get_req_method()) {
                response.setStatus(Status.OK);
                response.setHeader(Header.CONTENT_LENGTH, "0");    //$NON-NLS-1$
                response.setHeader(
                    Header.ALLOW,
                    Utils.join(resource.allowed_methods(), ',').toString());
                return;
            }
            if (!Method.all().contains(resource._request.get_req_method())) {
                response.setStatus(Status.NOT_IMPLEMENTED);
                return;
            }
            if (!resource.allowed_methods().contains(resource._request.get_req_method())) {
                response.setStatus(Status.METHOD_NOT_ALLOWED);
                return;
            }
            if (exists(Header.ACCEPT, resource._request)) {
                final MediaType mediaType =
                    accept(
                        resource._request, resource.content_types_provided());
                if (null==mediaType) {
                    response.setStatus(Status.NOT_ACCEPTABLE);
                    return;
                }
            }
            if (exists(Header.ACCEPT_LANGUAGE, resource._request)) {
                final Locale language =
                    acceptLanguage(resource._request, resource.languages_provided());
                if (null==language) {
                    response.setStatus(Status.NOT_ACCEPTABLE);
                    return;
                }
            }
            if (exists(Header.ACCEPT_CHARSET, resource._request)) {
                final Charset charset =
                    acceptCharset(resource._request, resource.charsets_provided());
                if (null==charset) {
                    response.setStatus(Status.NOT_ACCEPTABLE);
                    return;
                }
            }
            if (exists(Header.ACCEPT_ENCODING, resource._request)) {
                final ContentEncoding encoding =
                    acceptEncoding(resource._request, resource.encodings_provided());
                if (null==encoding) {
                    response.setStatus(Status.NOT_ACCEPTABLE);
                    return;
                }
            }
            if (resource.resource_exists()) {
                if (Method.DELETE==resource._request.get_req_method()) {
                    final boolean accepted = resource.delete_resource();
                    if (accepted) {
                        final boolean enacted = resource.delete_completed();
                        if (!enacted) {
                            response.setStatus(Status.ACCEPTED);
                            return;
                        }
                        response.setStatus(Status.NO_CONTENT);
                        return;
                    }
                }
                if (Method.POST==resource._request.get_req_method()) {
                    handlePost(resource, response);
                    return;
                }
                if (Method.GET==resource._request.get_req_method()) {
                    response.setStatus(Status.OK);
                    response.write(resource.content_types_provided().entrySet().iterator().next().getValue());
                    return;
                }
            } else {
                if (Method.PUT==resource._request.get_req_method()) {
                    final URI putUri = resource.moved_permanently();
                    if (null!=putUri) {
                        response.setStatus(Status.MOVED_PERMANENTLY);
                        response.setHeader(Header.LOCATION, putUri.toString()); // TODO: Confirm serialisation of URIs
                        return;
                    }
                    if (resource.is_conflict()) {
                        response.setStatus(Status.CONFLICT);
                        return;
                    }
                    response.setStatus(Status.CREATED);
                    return;
                }

                if (resource.previously_existed()) {
                    if (Method.POST==resource._request.get_req_method() && resource.allow_missing_post()) {
                        handlePost(resource, response);
                        return;
                    }
                    final URI permUri = resource.moved_permanently();
                    final URI tempUri = resource.moved_temporarily();
                    if (null!=permUri) {
                        response.setStatus(Status.MOVED_PERMANENTLY);
                        response.setHeader(Header.LOCATION, permUri.toString()); // TODO: Confirm serialisation of URIs
                        return;
                    }
                    if (null!=tempUri) {
                        response.setStatus(Status.TEMPORARY_REDIRECT);
                        response.setHeader(Header.LOCATION, tempUri.toString()); // TODO: Confirm serialisation of URIs
                        return;
                    }
                    response.setStatus(Status.GONE);
                    return;
                }
                if (Method.POST==resource._request.get_req_method() && resource.allow_missing_post()) {
                    handlePost(resource, response);
                    return;
                }
                response.setStatus(Status.NOT_FOUND);
                return;
            }

        } catch (final HttpException e) {
            // TODO handle committed responses.
            response.setStatus(Status.INTERNAL_SERVER_ERROR);
        } catch (final IOException e) {
            // TODO handle committed responses.
            response.setStatus(Status.INTERNAL_SERVER_ERROR);
        }
    }
}
