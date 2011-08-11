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
import org.junit.Test;
import wm.WeightedValue;



/**
 * Tests for the {@link MediaTypeNegotiator} class.
 *
 * @author Keith Webster Johnston.
 */
public class MediaTypeNegotiatorTest {

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
