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
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
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
     * @param configuration
     * @param request
     * @param contex
     */
    public BasicResource(final Properties configuration,
                         final Request request,
                         final Map<String, Object> contex) {
        super(configuration, request, contex);
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
    public URI create_path() {
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
    public Set<Locale> languages_provided() {
        return new HashSet<Locale>();
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
    public Map<String, Object> options() {
        return new HashMap<String, Object>();
    }

    /** {@inheritDoc} */
    @Override
    public boolean post_is_create() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean previously_existed() throws HttpException {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void process_post() throws HttpException {
        throw new HttpException("Unsupported HTTP method: POST.");
    }

    /** {@inheritDoc} */
    @Override
    public boolean resource_exists() throws HttpException {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean service_available() throws HttpException {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean uri_too_long() throws HttpException {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean valid_content_headers() throws HttpException {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean valid_entity_length() throws HttpException {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Header[] variances() {
        return new Header[] {};
    }
}
