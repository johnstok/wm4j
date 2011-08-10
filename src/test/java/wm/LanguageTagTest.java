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
import org.junit.Test;



/**
 * Tests for the {@link LanguageTag} class.
 *
 * @author Keith Webster Johnston.
 */
public class LanguageTagTest {


    @Test
    public void sameAreEqual() {

        // ARRANGE
        LanguageTag t = new LanguageTag("en");

        // ACT
        boolean equal = t.equals(t);

        // ASSERT
        assertTrue(equal);
    }


    @Test
    public void equivalentAreEqual() {

        // ARRANGE
        LanguageTag t = new LanguageTag("en");
        LanguageTag u = new LanguageTag("en");

        // ACT
        boolean equal = t.equals(u);

        // ASSERT
        assertTrue(equal);
    }


    @Test
    public void varyingCasesAreEqual() {

        // ARRANGE
        LanguageTag t = new LanguageTag("En");
        LanguageTag u = new LanguageTag("eN");

        // ACT
        boolean equal = t.equals(u);

        // ASSERT
        assertTrue(equal);
    }


    @Test
    public void equivalentMatch() {

        // ARRANGE
        LanguageTag t = new LanguageTag("en");

        // ACT
        boolean matches = t.matchedBy("en");

        // ASSERT
        assertTrue(matches);
    }


    @Test
    public void varyingCasesMatch() {

        // ARRANGE
        LanguageTag t = new LanguageTag("En");

        // ACT
        boolean matches = t.matchedBy("eN");

        // ASSERT
        assertTrue(matches);
    }


    @Test
    public void lessSpecificMatch() {

        // ARRANGE
        LanguageTag t = new LanguageTag("en-gb");

        // ACT
        boolean matches = t.matchedBy("en");

        // ASSERT
        assertTrue(matches);
    }


    @Test
    public void lessSpecificCaseInsensitiveMatch() {

        // ARRANGE
        LanguageTag t = new LanguageTag("eN-gB");

        // ACT
        boolean matches = t.matchedBy("En");

        // ASSERT
        assertTrue(matches);
    }


    @Test
    public void moreSpecificWontMatch() {

        // ARRANGE
        LanguageTag t = new LanguageTag("en");

        // ACT
        boolean matches = t.matchedBy("en-gb");

        // ASSERT
        assertFalse(matches);
    }


    @Test
    public void tooShortWontMatch() {

        // ARRANGE
        LanguageTag t = new LanguageTag("en-gb");

        // ACT
        boolean matches = t.matchedBy("e");

        // ASSERT
        assertFalse(matches);
    }


    @Test
    public void EndsWithDashWontMatch() {

        // ARRANGE
        LanguageTag t = new LanguageTag("en-gb");

        // ACT
        boolean matches = t.matchedBy("en-");

        // ASSERT
        assertFalse(matches);
    }


    @Test
    public void emptyDisallowed() {

        // ARRANGE

        // ACT
        new LanguageTag(" \n\t");

        // ASSERT
    }


    @Test
    public void zlsDisallowed() {

        // ARRANGE

        // ACT
        new LanguageTag("");

        // ASSERT
    }


    @Test
    public void nullDisallowed() {

        // ARRANGE

        // ACT
        new LanguageTag(null);

        // ASSERT
    }


    @Test
    public void whitespaceDisallowed() {

        // ARRANGE

        // ACT
        new LanguageTag("en gb");

        // ASSERT
    }


    @Test
    public void nonAlphaDisallowed() {

        // ARRANGE

        // ACT
        new LanguageTag("e1");

        // ASSERT
    }


    @Test
    public void malformed1Disallowed() {

        // ARRANGE

        // ACT
        new LanguageTag("-en");

        // ASSERT
    }


    @Test
    public void malformed2Disallowed() {

        // ARRANGE

        // ACT
        new LanguageTag("en-");

        // ASSERT
    }


    @Test
    public void malformed3Disallowed() {

        // ARRANGE

        // ACT
        new LanguageTag("en--gb");

        // ASSERT
    }


    @Test
    public void tooLong1Disallowed() {

        // ARRANGE

        // ACT
        new LanguageTag("abcdefghi");

        // ASSERT
    }


    @Test
    public void tooLong2Disallowed() {

        // ARRANGE

        // ACT
        new LanguageTag("en-abcdefghi");

        // ASSERT
    }
}
