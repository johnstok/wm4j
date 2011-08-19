/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.test;

import java.util.HashMap;
import java.util.Map;
import wm.BasicResource;
import wm.BodyReader;
import wm.BodyWriter;
import wm.MediaType;
import wm.Request;
import wm.Response;


/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public class TestResource
    extends
        BasicResource {

    /**
     * Constructor.
     *
     * @param request
     * @param response
     * @param context
     */
    public TestResource(final Request request,
                        final Response response,
                        final Map<String, Object> context) {
        super(request, response, context);
    }


    /** {@inheritDoc} */
    @Override
    public Map<MediaType, ? extends BodyWriter> content_types_provided() {
        return new HashMap<MediaType, BodyWriter>();
    }


    /** {@inheritDoc} */
    @Override
    public Map<MediaType, ? extends BodyReader> content_types_accepted() {
        return new HashMap<MediaType, BodyReader>();
    }

}
