/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * This file is part of wm4j.
 *
 * wm4j is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * wm4j is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with wm4j.  If not, see <http://www.gnu.org/licenses/>.
 *---------------------------------------------------------------------------*/
package wm;

import static org.junit.Assert.*;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import wm.test.TestRequest;


/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public class RegexDispatcherTest {


    /**
     * TODO: Add a description for this type.
     *
     * @author Keith Webster Johnston.
     */
    public static class TestResource extends BasicResource {

        /**
         * Constructor.
         *
         * @param request
         * @param response
         * @param context
         */
        public TestResource(final Object configuration,
                            final Request request,
                            final Response response,
                            final Map<String, Object> context) {
            super(request, response, context);
        }

        /** {@inheritDoc} */
        @Override
        public Map<MediaType, ? extends BodyReader> content_types_accepted() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public Map<MediaType, ? extends BodyWriter> content_types_provided() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

    }


    /**
     * TODO: Add a description for this type.
     *
     * @author Keith Webster Johnston.
     */
    public static class TestResource2 extends BasicResource {

        /**
         * Constructor.
         *
         * @param request
         * @param response
         * @param context
         */
        public TestResource2(final Object configuration,
                             final Request request,
                             final Response response,
                             final Map<String, Object> context) {
            super(request, response, context);
        }

        /** {@inheritDoc} */
        @Override
        public Map<MediaType, ? extends BodyReader> content_types_accepted() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public Map<MediaType, ? extends BodyWriter> content_types_provided() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

    }


    @Test
    public void matchReturnsResource() throws Exception {

        // ARRANGE
        RegexDispatcher<Object> d = new RegexDispatcher<Object>(new Object());
        d.bind(".*", TestResource.class);

        // ACT
        Resource r = d.dispatch(new TestRequest(), null);

        // ASSERT
        assertTrue(r instanceof TestResource);
    }


    @Test
    public void firstMatchReturnsResource() throws Exception {

        // ARRANGE
        RegexDispatcher<Object> d = new RegexDispatcher<Object>(new Object());
        d.bind("/",  TestResource.class);
        d.bind(".*", TestResource2.class);

        // ACT
        Resource r = d.dispatch(new TestRequest(), null);

        // ASSERT
        assertTrue("Incorrect dispatch.", r instanceof TestResource);
    }


    @Test
    public void noMatchThrowsNotFound() throws Exception {

        // ARRANGE
        RegexDispatcher<Object> d = new RegexDispatcher<Object>(new Object());
        d.bind("/test", TestResource.class);

        // ACT
        try {
            Resource r = d.dispatch(new TestRequest(), null);

        // ASSERT
        } catch (ClientHttpException e) {
            assertEquals(Status.NOT_FOUND, e.getStatus());
        }
    }

    /**
     * TODO: Add a description for this method.
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {}


    /**
     * TODO: Add a description for this method.
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {}

}
