/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.johnstok.http.MediaType;
import com.johnstok.http.sync.BodyReader;
import com.johnstok.http.sync.BodyWriter;
import wm.BasicResource;
import wm.Resource;


/**
 * Test implementation of the {@link Resource} interface.
 *
 * @author Keith Webster Johnston.
 */
public class TestResource
    extends
        BasicResource<Map<String, Object>> {

    /**
     * Constructor.
     *
     * @param context The server context.
     */
    public TestResource(final Map<String, Object> context) {
        super(context);
    }


    /** {@inheritDoc} */
    @Override
    public Map<MediaType, ? extends BodyReader> getContentTypesAccepted() {
        return new HashMap<MediaType, BodyReader>();
    }


    /** {@inheritDoc} */
    @Override
    public BodyWriter getWriter(final MediaType mediaType) {
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public Set<MediaType> getContentTypesProvided() {
        return null;
    }
}
