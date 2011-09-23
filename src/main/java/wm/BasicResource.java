/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public abstract class BasicResource
    extends
        Resource {

    /**
     * Constructor.
     *
     * @param request
     * @param response
     * @param context
     */
    public BasicResource(final Request request,
                         final Response response,
                         final Map<String, Object> context) {
        super(request, response, context);
    }

    /** {@inheritDoc} */
    @Override
    public boolean allow_missing_post() throws HttpException {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> allowed_methods() {
        return
            new HashSet<String>(
                Arrays.asList(new String[] {Method.GET, Method.HEAD}));
    }

    /** {@inheritDoc} */
    @Override
    public Set<Charset> charsets_provided() {
        return new HashSet<Charset>();
    }

    /** {@inheritDoc} */
    @Override
    public URI createPath() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean delete_completed() throws HttpException {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean delete_resource() throws HttpException {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> encodings_provided() {
        return Collections.singleton(ContentEncoding.IDENTITY);
    }

    /** {@inheritDoc} */
    @Override
    public Date expires() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void finish_request() {
        // No Op.
    }

    /** {@inheritDoc} */
    @Override
    public boolean forbidden() throws HttpException {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public ETag generate_etag() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean is_authorized() throws HttpException {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean is_conflict() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean known_content_type() throws HttpException {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Set<LanguageTag> languages_provided() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Date last_modified() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean malformed_request() throws HttpException {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public URI moved_permanently() throws HttpException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public URI moved_temporarily() throws HttpException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean multiple_choices() throws HttpException {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> getOptionsResponseHeaders() {
        return new HashMap<String, Object>();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPostCreate() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean existedPreviously() throws HttpException {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void processPost() throws HttpException {
        throw new ServerHttpException(Status.NOT_IMPLEMENTED);
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists() throws HttpException {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isServiceAvailable() throws HttpException {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isUriTooLong() throws HttpException {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasValidContentHeaders() throws HttpException {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEntityLengthValid() throws HttpException {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Header[] getVariances() {
        return new Header[] {};
    }
}
