/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import static org.junit.Assert.*;
import org.junit.Test;



/**
 * Tests for the {@link MediaType} class.
 *
 * @author Keith Webster Johnston.
 */
public class MediaTypeTest {

    @Test
    public void constructAny() {

        // ARRANGE

        // ACT
        final MediaType mt = new MediaType("*", "*");

        // ASSERT
        assertEquals("*", mt.getType());
        assertEquals("*", mt.getSubtype());
    }

    @Test
    public void constructWildcardSubtype() {

        // ARRANGE

        // ACT
        final MediaType mt = new MediaType("text", "*");

        // ASSERT
        assertEquals("text", mt.getType());
        assertEquals("*", mt.getSubtype());
    }

    @Test
    public void constructFullySpecified() {

        // ARRANGE

        // ACT
        final MediaType mt = new MediaType("text", "html");

        // ASSERT
        assertEquals("text", mt.getType());
        assertEquals("html", mt.getSubtype());
    }

    @Test
    public void constructTextAny() {

        // ARRANGE

        // ACT
        final MediaType mt = new MediaType("*/*");

        // ASSERT
        assertEquals("*", mt.getType());
        assertEquals("*", mt.getSubtype());
    }

    @Test
    public void constructTextWildcardSubtype() {

        // ARRANGE

        // ACT
        final MediaType mt = new MediaType("text/*");

        // ASSERT
        assertEquals("text", mt.getType());
        assertEquals("*", mt.getSubtype());
    }

    @Test
    public void constructTextFullySpecified() {

        // ARRANGE

        // ACT
        final MediaType mt = new MediaType("text/html");

        // ASSERT
        assertEquals("text", mt.getType());
        assertEquals("html", mt.getSubtype());
    }

    @Test
    public void sameAreEqual() {

        // ARRANGE
        final MediaType mt = new MediaType("text", "html");

        // ACT
        final boolean equal = mt.equals(mt);

        // ASSERT
        assertTrue(equal);
    }

    @Test
    public void varyingCaseAreEqual() {

        // ARRANGE
        final MediaType mt1 = new MediaType("text", "HTML");
        final MediaType mt2 = new MediaType("TEXT", "html");

        // ACT
        final boolean equal = mt1.equals(mt2);

        // ASSERT
        assertTrue(equal);
    }

    @Test
    public void equivalentAreEqual() {

        // ARRANGE
        final MediaType mt1 = new MediaType("text", "html");
        final MediaType mt2 = new MediaType("text", "html");

        // ACT
        final boolean equal = mt1.equals(mt2);

        // ASSERT
        assertTrue(equal);
    }

    @Test
    public void wildcardMatchNotEqual() {

        // ARRANGE
        final MediaType mt1 = new MediaType("text", "html");
        final MediaType mt2 = new MediaType("text", "*");

        // ACT
        final boolean equal = mt1.equals(mt2);

        // ASSERT
        assertFalse(equal);
    }

    @Test
    public void equivalentMatches() {

        // ARRANGE
        final MediaType mt = new MediaType("text", "html");

        // ACT
        final boolean match = mt.matches("text/html");

        // ASSERT
        assertTrue(match);
    }

    @Test
    public void wildcardSubtypeMatches() {

        // ARRANGE
        final MediaType mt = new MediaType("text", "html");

        // ACT
        final boolean match = mt.matches("text/*");

        // ASSERT
        assertTrue(match);
    }

    @Test
    public void anyMatches() {

        // ARRANGE
        final MediaType mt = new MediaType("text", "html");

        // ACT
        final boolean match = mt.matches("*/*");

        // ASSERT
        assertTrue(match);
    }

    @Test
    public void wildcardTypeDoesNotMatch() {

        // ARRANGE
        final MediaType mt = new MediaType("text", "html");

        // ACT
        final boolean match = mt.matches("*/html");

        // ASSERT
        assertFalse(match);
    }
}
