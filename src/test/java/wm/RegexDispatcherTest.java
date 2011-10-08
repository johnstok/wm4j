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
import java.util.Set;
import org.junit.Test;
import wm.test.TestRequest;
import wm.test.TestResource;


/**
 * Tests for the {@link RegexDispatcher} class.
 *
 * @author Keith Webster Johnston.
 */
public class RegexDispatcherTest {


    /**
     * TODO: Add a description for this type.
     *
     * @author Keith Webster Johnston.
     */
    public static class TestResource2
        extends
            BasicResource<Map<String, Object>> {

        /**
         * Constructor.
         *
         * @param context
         */
        public TestResource2(final Map<String, Object> context) {
            super(context);
        }

        /** {@inheritDoc} */
        @Override
        public Map<MediaType, ? extends BodyReader> getContentTypesAccepted() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public Set<MediaType> getContentTypesProvided() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public BodyWriter getWriter(final MediaType mediaType) {
            throw new UnsupportedOperationException("Method not implemented.");
        }
    }


    @Test
    public void matchReturnsResource() throws Exception {

        // ARRANGE
        final RegexDispatcher<Object> d = new RegexDispatcher<Object>(new Object());
        d.bind(".*", TestResource.class);

        // ACT
        final Resource r = d.dispatch(new TestRequest(), null);

        // ASSERT
        assertTrue(r instanceof TestResource);
    }


    @Test
    public void firstMatchReturnsResource() throws Exception {

        // ARRANGE
        final RegexDispatcher<Object> d = new RegexDispatcher<Object>(new Object());
        d.bind("/",  TestResource.class);
        d.bind(".*", TestResource2.class);

        // ACT
        final Resource r = d.dispatch(new TestRequest(), null);

        // ASSERT
        assertTrue("Incorrect dispatch.", r instanceof TestResource);
    }


    @Test
    public void noMatchThrowsNotFound() throws Exception {

        // ARRANGE
        final RegexDispatcher<Object> d = new RegexDispatcher<Object>(new Object());
        d.bind("/test", TestResource.class);

        // ACT
        try {
            final Resource r = d.dispatch(new TestRequest(), null);

        // ASSERT
        } catch (final ClientHttpException e) {
            assertEquals(Status.NOT_FOUND, e.getStatus());
        }
    }
}
