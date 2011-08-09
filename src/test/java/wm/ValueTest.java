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
import java.util.Collections;
import org.junit.Test;



/**
 * Tests for the {@link Value} class.
 *
 * @author Keith Webster Johnston.
 */
public class ValueTest {

    @Test
    public void parseSingleValue() {

        // ARRANGE

        // ACT
        Value v = Value.parse("foo");

        // ASSERT
        assertEquals(new Value("foo"), v);
    }

    @Test
    public void parseSingleValueOneProp() {

        // ARRANGE

        // ACT
        Value v = Value.parse("foo;a=b");

        // ASSERT
        assertEquals(
            new Value("foo", Collections.singletonMap("a", "b")),
            v);
    }
}
