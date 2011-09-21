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
import java.nio.charset.Charset;
import org.junit.Test;



/**
 * Tests for the {@link Path} Class.
 *
 * @author Keith Webster Johnston.
 */
public class PathTest {

    @Test
    public void rootPathHasSizeZero() {

        // ARRANGE
        Path p = new Path("/", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(0, size);
    }

    @Test
    public void encodedSegmentsDecoded() {

        // ARRANGE
        Path p = new Path("/%C2%A3/%C2%A3/%C2%A3", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(3, size);
        assertEquals("£", p.getSegment(0));
        assertEquals("£", p.getSegment(1));
        assertEquals("£", p.getSegment(2));
    }

    @Test
    public void encodedSegmentDecoded() {

        // ARRANGE
        Path p = new Path("/foo%2Fbar%2Fbaz", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(1, size);
        assertEquals("foo/bar/baz", p.getSegment(0));
    }

    @Test
    public void singlePeriodNormalised() {

        // ARRANGE
        Path p = new Path("/foo/./bar", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(2, size);
        assertEquals("foo", p.getSegment(0));
        assertEquals("bar", p.getSegment(1));
    }

    @Test
    public void doublePeriodNormalised() {

        // ARRANGE
        Path p = new Path("/foo/../bar", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(1, size);
        assertEquals("bar", p.getSegment(0));
    }

    @Test
    public void normalisationSupportsEncodedPeriods() {

        // ARRANGE
        Path p = new Path("/foo/%2E%2E/bar/%2E/baz", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(2, size);
        assertEquals("bar", p.getSegment(0));
        assertEquals("baz", p.getSegment(1));
    }

    @Test
    public void multipleDoublePeriodNormalised() {

        // ARRANGE
        Path p = new Path("/foo/../bar/../baz", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(1, size);
        assertEquals("baz", p.getSegment(0));
    }

    @Test
    public void leadingDoublePeriodKept() {

        // ARRANGE
        Path p = new Path("/../foo", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(2, size);
        assertEquals("..", p.getSegment(0));
        assertEquals("foo", p.getSegment(1));
    }

    @Test
    public void multipleLeadingDoublePeriodKept() {

        // ARRANGE
        Path p = new Path("/../../foo", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(3, size);
        assertEquals("..", p.getSegment(0));
        assertEquals("..", p.getSegment(1));
        assertEquals("foo", p.getSegment(2));
    }

    @Test
    public void consecutiveDoublePeriodNormalised() {

        // ARRANGE
        Path p = new Path("/foo/bar/../../baz", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(1, size);
        assertEquals("baz", p.getSegment(0));
    }

    @Test
    public void unencodedNormalisedSimplePath() {

        // ARRANGE
        Path p = new Path("/foo", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(1, size);
        assertEquals("foo", p.getSegment(0));
    }

    @Test
    public void unencodedNormalisedLongPath() {

        // ARRANGE
        Path p = new Path("/foo/bar/baz", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(3, size);
        assertEquals("foo", p.getSegment(0));
        assertEquals("bar", p.getSegment(1));
        assertEquals("baz", p.getSegment(2));
    }

    @Test
    public void emptySegmentsDiscarded() {

        // ARRANGE
        Path p = new Path("/foo//baz", Charset.forName("UTF-8"));

        // ACT
        int size = p.getSize();

        // ASSERT
        assertEquals(2, size);
        assertEquals("foo", p.getSegment(0));
        assertEquals("baz", p.getSegment(1));
    }
}
