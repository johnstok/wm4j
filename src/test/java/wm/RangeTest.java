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
package wm;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import wm.Range;



/**
 * Tests for the {@link Range} class.
 *
 * @author Keith Webster Johnston.
 */
public class RangeTest {


    /**
     * Test.
     */
    @Test
    public void byteRangeBeyondContentLengthIsUnsatisfiable() {

        // ARRANGE
        final Range r = new Range(2L, 2L);

        // ASSERT
        assertEquals(2L, r.getFrom());
        assertEquals(2L, r.getTo());
        assertTrue(r.isValid());
        assertFalse(r.isSatisfiable(1));
    }


    /**
     * Test.
     */
    @Test
    public void parseSingleRangeFinishOnly() {

        // ACT
        final List<Range> ranges = Range.parse("bytes=-2");

        // ASSERT
        assertEquals(1, ranges.size());
        assertEquals(null, ranges.get(0).getFrom());
        assertEquals(2L,  ranges.get(0).getTo());
    }


    /**
     * Test.
     */
    @Test
    public void parseSingleRangeStartAndFinish() {

        // ACT
        final List<Range> ranges = Range.parse("bytes=0-2");

        // ASSERT
        assertEquals(1, ranges.size());
        assertEquals(0L,   ranges.get(0).getFrom());
        assertEquals(2L, ranges.get(0).getTo());
    }


    /**
     * Test.
     */
    @Test
    public void parseSingleRangeStartOnly() {

        // ACT
        final List<Range> ranges = Range.parse("bytes=0-");

        // ASSERT
        assertEquals(1, ranges.size());
        assertEquals(0L,   ranges.get(0).getFrom());
        assertEquals(null, ranges.get(0).getTo());
    }
}
