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
import java.util.Arrays;
import java.util.HashSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import wm.LanguageTag;
import wm.WeightedValue;



/**
 * Tests for the {@link LanguageNegotiator} class.
 *
 * @author Keith Webster Johnston.
 */
public class LanguageNegotiatorTest {

    LanguageNegotiator _negotiator;


    @Before
    public void setUp() {
        _negotiator = new LanguageNegotiator(new HashSet<LanguageTag>(Arrays.asList(new LanguageTag("en"))));
    }


    @After
    public void tearDown() {
        _negotiator = null;
    }


    @Test
    public void simpleMatchNoSubs() {

        // ACT
        LanguageTag selected  = _negotiator.selectLanguage(new WeightedValue("en", 1f));

        // ASSERT
        assertEquals(new LanguageTag("en"), selected);
    }
}
