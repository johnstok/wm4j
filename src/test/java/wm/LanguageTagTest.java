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
@SuppressWarnings("unused")
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
        try {
            new LanguageTag(" \n\t");
            fail();

        // ASSERT
        } catch (IllegalArgumentException e) {
            assertEquals("String ' \n\t' does not match regular expression /\\p{Alpha}{1,8}(-\\p{Alpha}{1,8})*/.", e.getMessage());
        }
    }


    @Test
    public void zlsDisallowed() {

        // ARRANGE

        // ACT
        try {
            new LanguageTag("");
            fail();

        // ASSERT
        } catch (IllegalArgumentException e) {
            assertEquals("String '' does not match regular expression /\\p{Alpha}{1,8}(-\\p{Alpha}{1,8})*/.", e.getMessage());
        }
    }


    @Test
    public void nullDisallowed() {

        // ARRANGE

        // ACT
        try {
            new LanguageTag(null);
            fail();

        // ASSERT
        } catch (NullPointerException e) {
            assertNull(e.getMessage());
        }
    }


    @Test
    public void whitespaceDisallowed() {

        // ARRANGE

        // ACT
        try {
            new LanguageTag("en gb");
            fail();

        // ASSERT
        } catch (IllegalArgumentException e) {
            assertEquals("String 'en gb' does not match regular expression /\\p{Alpha}{1,8}(-\\p{Alpha}{1,8})*/.", e.getMessage());
        }
    }


    @Test
    public void nonAlphaDisallowed() {

        // ARRANGE

        // ACT
        try {
            new LanguageTag("e1");
            fail();

        // ASSERT
        } catch (IllegalArgumentException e) {
            assertEquals("String 'e1' does not match regular expression /\\p{Alpha}{1,8}(-\\p{Alpha}{1,8})*/.", e.getMessage());
        }
    }


    @Test
    public void malformed1Disallowed() {

        // ARRANGE

        // ACT
        try {
            new LanguageTag("-en");
            fail();

        // ASSERT
        } catch (IllegalArgumentException e) {
            assertEquals("String '-en' does not match regular expression /\\p{Alpha}{1,8}(-\\p{Alpha}{1,8})*/.", e.getMessage());
        }
    }


    @Test
    public void malformed2Disallowed() {

        // ARRANGE

        // ACT
        try {
            new LanguageTag("en-");
            fail();

        // ASSERT
        } catch (IllegalArgumentException e) {
            assertEquals("String 'en-' does not match regular expression /\\p{Alpha}{1,8}(-\\p{Alpha}{1,8})*/.", e.getMessage());
        }
    }


    @Test
    public void malformed3Disallowed() {

        // ARRANGE

        // ACT
        try {
            new LanguageTag("en--gb");
            fail();

        // ASSERT
        } catch (IllegalArgumentException e) {
            assertEquals("String 'en--gb' does not match regular expression /\\p{Alpha}{1,8}(-\\p{Alpha}{1,8})*/.", e.getMessage());
        }
    }


    @Test
    public void tooLong1Disallowed() {

        // ARRANGE

        // ACT
        try {
            new LanguageTag("abcdefghi");
            fail();

        // ASSERT
        } catch (IllegalArgumentException e) {
            assertEquals("String 'abcdefghi' does not match regular expression /\\p{Alpha}{1,8}(-\\p{Alpha}{1,8})*/.", e.getMessage());
        }
    }


    @Test
    public void tooLong2Disallowed() {

        // ARRANGE

        // ACT
        try {
            new LanguageTag("en-abcdefghi");
            fail();

        // ASSERT
        } catch (IllegalArgumentException e) {
            assertEquals("String 'en-abcdefghi' does not match regular expression /\\p{Alpha}{1,8}(-\\p{Alpha}{1,8})*/.", e.getMessage());
        }
    }
}
