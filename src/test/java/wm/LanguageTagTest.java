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
}
