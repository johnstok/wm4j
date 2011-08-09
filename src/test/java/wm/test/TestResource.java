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
     * @param contex
     */
    public TestResource(final Request request,
                        final Map<String, Object> contex) {
        super(request, contex);
    }


    /** {@inheritDoc} */
    @Override
    public Map<MediaType, BodyWriter> content_types_provided() {
        return new HashMap<MediaType, BodyWriter>();
    }


    /** {@inheritDoc} */
    @Override
    public Map<MediaType, BodyReader> content_types_accepted() {
        return new HashMap<MediaType, BodyReader>();
    }

}
