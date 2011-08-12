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
package wm.negotiation;

import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import wm.MediaType;
import wm.WeightedValue;



/**
 * Tests for the {@link MediaTypeNegotiator} class.
 *
 * @author Keith Webster Johnston.
 */
public class MediaTypeNegotiatorTest {

    @Test
    public void simpleSelection() {

        // ARRANGE
        MediaTypeNegotiator n = new MediaTypeNegotiator(MediaType.HTML);

        // ACT
        MediaType mt = n.select(new WeightedValue("text/html", 1f));

        // ASSERT
        assertEquals(MediaType.HTML, mt);
    }

    @Test
    public void selectionOnWeight() {

        // ARRANGE
        MediaTypeNegotiator n =
            new MediaTypeNegotiator(MediaType.HTML, MediaType.XML);

        // ACT
        MediaType mt =
            n.select(
                new WeightedValue("text/html", .5f),
                new WeightedValue("application/xml", .6f));

        // ASSERT
        assertEquals(MediaType.XML, mt);
    }

    @Test
    public void selectionWithWildcard() {

        // ARRANGE
        MediaTypeNegotiator n =
            new MediaTypeNegotiator(MediaType.HTML, MediaType.JPEG);

        // ACT
        MediaType mt =
            n.select(
                new WeightedValue("text/html", .5f),
                new WeightedValue("image/*", .6f));

        // ASSERT
        assertEquals(MediaType.JPEG, mt);
    }

    @Test
    public void selectionWithAny() {

        // ARRANGE
        MediaTypeNegotiator n =
            new MediaTypeNegotiator(MediaType.HTML);

        // ACT
        MediaType mt =
            n.select(
                new WeightedValue("application/xml", .5f),
                new WeightedValue("*/*", .6f));

        // ASSERT
        assertEquals(MediaType.HTML, mt);
    }

    @Test
    public void qualityRespectsPrecedence() {

        // ARRANGE
        MediaTypeNegotiator n =
            new MediaTypeNegotiator(
                MediaType.XML, MediaType.JSON, MediaType.JPEG);

        // ACT
        Map<MediaType, Float> weights =
            n.weights(
                new WeightedValue("application/xml", .6f),
                new WeightedValue("application/*",   .5f),
                new WeightedValue("*/*",             .4f));

        // ASSERT
        assertEquals(3, weights.size());
        assertEquals(.6f, weights.get(MediaType.XML));
        assertEquals(.5f, weights.get(MediaType.JSON));
        assertEquals(.4f, weights.get(MediaType.JPEG));
    }

    @Test
    public void selectionRespectsPrecedence() {

        // ARRANGE
        MediaTypeNegotiator n =
            new MediaTypeNegotiator(
                MediaType.XML, MediaType.JSON, MediaType.JPEG);

        // ACT
        MediaType mt =
            n.select(
                new WeightedValue("application/xml", .4f),
                new WeightedValue("application/*",   .5f),
                new WeightedValue("*/*",             .6f));

        // ASSERT
        assertEquals(MediaType.JPEG, mt);
    }

    @Test
    public void parseSingleNoWeight() {

        // ARRANGE

        // ACT
        final List<WeightedValue> ranges = MediaTypeNegotiator.parse("audio/basic");

        // ASSERT
        assertEquals(1, ranges.size());
        assertEquals("audio/basic", ranges.get(0).getValue());
        assertEquals(1f, ranges.get(0).getWeight());
    }

    @Test
    public void parseMultipleSomeWeights() {

        // ARRANGE

        // ACT
        final List<WeightedValue> ranges = MediaTypeNegotiator.parse("audio/* ; q=0.2 , audio/basic");

        // ASSERT
        assertEquals(2, ranges.size());
        assertEquals("audio/*", ranges.get(0).getValue());
        assertEquals(0.2f, ranges.get(0).getWeight());
        assertEquals("audio/basic", ranges.get(1).getValue());
        assertEquals(1f, ranges.get(1).getWeight());
    }

    @Test
    public void parseSingleWithQuality() {
        final List<WeightedValue> ranges = MediaTypeNegotiator.parse("application/xml;q=.5");
        assertEquals(1, ranges.size());
        assertEquals("application/xml", ranges.get(0).getValue());
        assertEquals(.5f, ranges.get(0).getWeight());
    }

    @Test
    public void parseMissingQValue() {

        final List<WeightedValue> ranges = MediaTypeNegotiator.parse("application/xml;q=");
        assertEquals(1, ranges.size());
        assertEquals("application/xml", ranges.get(0).getValue());
        assertEquals(1f, ranges.get(0).getWeight());
    }

    @Test
    public void qValueTooHigh() {

        final List<WeightedValue> ranges = MediaTypeNegotiator.parse("application/xml;q=3");
        assertEquals(1, ranges.size());
        assertEquals("application/xml", ranges.get(0).getValue());
        assertEquals(1f, ranges.get(0).getWeight());
    }

    @Test
    public void qValueTooLow() {

        final List<WeightedValue> ranges = MediaTypeNegotiator.parse("application/xml;q=-1");
        assertEquals(1, ranges.size());
        assertEquals("application/xml", ranges.get(0).getValue());
        assertEquals(0f, ranges.get(0).getWeight());
    }

    @Test
    public void invalidWildcard() {

        final List<WeightedValue> ranges = MediaTypeNegotiator.parse(" *; q=.2");
        assertEquals(1, ranges.size());
        assertEquals("*/*", ranges.get(0).getValue());
        assertEquals(.2f, ranges.get(0).getWeight());
    }

    @Test
    public void invalidQuality() {

        final List<WeightedValue> ranges = MediaTypeNegotiator.parse("application/xml; q=bad");
        assertEquals(0, ranges.size());
    }

    @Test
    public void acceptExtensionsPreserved() {

        final List<WeightedValue> ranges = MediaTypeNegotiator.parse("application/xml ; foo=bar;q=.3;b=other");
        assertEquals(1, ranges.size());
        assertEquals("application/xml;b=other", ranges.get(0).getValue());
        assertEquals(.3f, ranges.get(0).getWeight());
    }
}
