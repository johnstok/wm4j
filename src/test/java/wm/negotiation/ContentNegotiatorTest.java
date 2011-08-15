/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 * Modified by   $Author$
 * Modified on   $Date$
 *
 * Changes: see version control.
 *-----------------------------------------------------------------------------
 */
package wm.negotiation;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import wm.ContentEncoding;
import wm.WeightedValue;


/**
 * Tests for the {@link ContentNegotiator} class.
 *
 * @author Keith Webster Johnston.
 */
@SuppressWarnings({"nls"})
public class ContentNegotiatorTest {

    /**
     * Tests RFC-2616/14.3/4.
     */
    @Test
    public void anyWeightedZeroButIdentityWeightedOneGivesIdenity() {

        // ARRANGE
        final ContentNegotiator negotiator = new ContentNegotiator();

        // ACT
        final String encoding =
            negotiator.selectEncoding(
                new WeightedValue(ContentEncoding.ANY, 0),
                new WeightedValue(ContentEncoding.IDENTITY, 1));

        // ASSERT
        assertEquals(ContentEncoding.IDENTITY, encoding);
    }


    /**
     * Tests RFC-2616/14.3/4.
     */
    @Test
    public void anyWeightedZeroGivesNull() {

        // ARRANGE
        final ContentNegotiator negotiator = new ContentNegotiator();

        // ACT
        final String encoding =
            negotiator
                .selectEncoding(new WeightedValue(ContentEncoding.ANY, 0));

        // ASSERT
        assertNull(encoding);
    }


    /**
    * Tests RFC-2616/14.3/4.
    */
    @Test
    public void emptyAcceptEncodingGivesIdentity() {

        // ARRANGE
        final ContentNegotiator negotiator =
            new ContentNegotiator(new WeightedValue("foo", 1));

        // ACT
        final String encoding =
            negotiator.selectEncoding(new ArrayList<WeightedValue>());

        // ASSERT
        assertEquals(ContentEncoding.IDENTITY, encoding);
    }


    /**
         * Tests RFC-2616/14.3/3.
         */
    @Test
    public void highestNonZeroValueSelected() {

        // ARRANGE
        final ContentNegotiator negotiator =
            new ContentNegotiator(
                new WeightedValue("foo", 1),
                new WeightedValue("bar", 1));

        // ACT
        final String encoding =
            negotiator.selectEncoding(
                new WeightedValue("foo", 0.001f),
                new WeightedValue("bar", 1));

        // ASSERT
        assertEquals("bar", encoding);
    }


    /**
     * Tests RFC-2616/14.3/4.
     */
    @Test
    public void identityCodingIsAlwaysAcceptable() {

        // ARRANGE
        final ContentNegotiator negotiator =
            new ContentNegotiator(new WeightedValue("foo", 1));

        // ACT
        final String encoding =
            negotiator.selectEncoding(new ArrayList<WeightedValue>());

        // ASSERT
        assertEquals(ContentEncoding.IDENTITY, encoding);
    }


    /**
     * Tests RFC-2616/14.3/4.
     */
    @Test
    public void identityWeightedZeroGivesNull() {

        // ARRANGE
        final ContentNegotiator negotiator = new ContentNegotiator();

        // ACT
        final String encoding =
            negotiator.selectEncoding(new WeightedValue(
                ContentEncoding.IDENTITY,
                0));

        // ASSERT
        assertNull(encoding);
    }


    /**
     * Tests RFC-2616/14.3.
     *
     * "If no Accept-Encoding field is present in a request, the
     * server MAY assume that the client will accept any content
     * coding. In this case, if "identity" is one of the available
     * content-codings, then the server SHOULD use the "identity"
     * content-coding, unless it has additional information that
     * a different content-coding is meaningful to the client."
     */
    @Test
    public void missingAcceptEncodingGivesIdentity() {

        // ARRANGE
        final ContentNegotiator negotiator = new ContentNegotiator();

        // ACT
        final String encoding =
            negotiator.selectEncoding((List<WeightedValue>) null);

        // ASSERT
        assertEquals(ContentEncoding.IDENTITY, encoding);
    }


    /**
     * Tests RFC-2616/14.3/2.
     */
    @Test
    public void starMatchesAnyCoding() {

        // ARRANGE
        final ContentNegotiator negotiator =
            new ContentNegotiator(
                new WeightedValue("foo", 1),
                new WeightedValue("bar", 0.5f));

        // ACT
        final String encoding =
            negotiator.selectEncoding(
                new WeightedValue("foo", 0),
                new WeightedValue("*", 1));

        // ASSERT
        assertEquals("bar", encoding);
    }


    /**
     * Tests RFC-2616/14.3/1.
     */
    @Test
    public void weightOfZeroDisallowsCoding() {

        // ARRANGE
        final ContentNegotiator negotiator =
            new ContentNegotiator(
                new WeightedValue("foo", 1),
                new WeightedValue("bar", 1));

        // ACT
        final String encoding =
            negotiator.selectEncoding(
                new WeightedValue("foo", 0),
                new WeightedValue("bar", 1));

        // ASSERT
        assertEquals("bar", encoding);
    }
}
