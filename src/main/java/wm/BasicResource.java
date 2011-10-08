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
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Implements defaults for many of the {@link Resource} API methods.
 *
 * @author Keith Webster Johnston.
 */
public abstract class BasicResource<T>
    implements
        Resource {

    protected final T _context;


    /**
     * Constructor.
     *
     * @param context The server context.
     */
    public BasicResource(final T context) {
        _context = context;
    }


    /** {@inheritDoc} */
    @Override
    public boolean allowsPostToMissing() { return false; }


    /** {@inheritDoc} */
    @Override
    public Set<String> getAllowedMethods() {
        return
            new HashSet<String>(
                Arrays.asList(new String[] {Method.GET, Method.HEAD}));
    }


    /** {@inheritDoc} */
    @Override
    public Set<Charset> getCharsetsProvided() { return null; }


    /** {@inheritDoc} */
    @Override
    public URI getCreatePath() { return null; }


    /** {@inheritDoc} */
    @Override
    public boolean isDeleted() { return true; }


    /** {@inheritDoc} */
    @Override
    public boolean delete() { return false; }


    /** {@inheritDoc} */
    @Override
    public Set<String> getEncodings() {
        return Collections.singleton(ContentEncoding.IDENTITY);
    }


    /** {@inheritDoc} */
    @Override
    public Date getExpiryDate() { return null; }


    /** {@inheritDoc} */
    @Override
    public void finishRequest() { /* No Op */ }


    /** {@inheritDoc} */
    @Override
    public boolean isForbidden() { return false; }


    /** {@inheritDoc} */
    @Override
    public ETag generateEtag(final String base) { return null; }


    /** {@inheritDoc} */
    @Override
    public String authorize() { return null; }


    /** {@inheritDoc} */
    @Override
    public boolean isInConflict() { return false; }


    /** {@inheritDoc} */
    @Override
    public boolean isContentTypeKnown(final MediaType mediaType) {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public Set<LanguageTag> getLanguages() { return null; }


    /** {@inheritDoc} */
    @Override
    public Date getLastModifiedDate() { return null; }


    /** {@inheritDoc} */
    @Override
    public boolean isMalformed() { return false; }


    /** {@inheritDoc} */
    @Override
    public URI movedPermanentlyTo() { return null; }


    /** {@inheritDoc} */
    @Override
    public URI movedTemporarilyTo() { return null; }


    /** {@inheritDoc} */
    @Override
    public boolean hasMultipleChoices() { return false; }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> getOptionsResponseHeaders() {
        return new HashMap<String, List<String>>();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPostCreate() { return false; }


    /** {@inheritDoc} */
    @Override
    public boolean existedPreviously() { return false; }


    /** {@inheritDoc} */
    @Override
    public void processPost() {
        throw new ServerHttpException(Status.NOT_IMPLEMENTED);
    }


    /** {@inheritDoc} */
    @Override
    public boolean exists() { return true; }


    /** {@inheritDoc} */
    @Override
    public boolean isServiceAvailable() { return true; }


    /** {@inheritDoc} */
    @Override
    public boolean isUriTooLong() { return false; }


    /** {@inheritDoc} */
    @Override
    public boolean hasValidContentHeaders() { return true; }


    /** {@inheritDoc} */
    @Override
    public boolean isEntityLengthValid() { return true; }


    /** {@inheritDoc} */
    @Override
    public Header[] getVariances() { return new Header[] {}; }
}
