/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.negotiation;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
        _negotiator = new LanguageNegotiator(new HashSet<LanguageTag>(Arrays.asList(
            new LanguageTag("sv"),
            new LanguageTag("sv-se"),
            new LanguageTag("en"),
            new LanguageTag("fr-ca"))));
    }


    @After
    public void tearDown() {
        _negotiator = null;
    }


    @Test
    public void simpleMatchNoSubs() {

        // ACT
        final LanguageTag selected  = _negotiator.selectLanguage(new WeightedValue("en", 1f));

        // ASSERT
        assertEquals(new LanguageTag("en"), selected);
    }


    @Test
    public void matchesMoreSpecific() {

        // ACT
        final LanguageTag selected  = _negotiator.selectLanguage(new WeightedValue("fr", 1f));

        // ASSERT
        assertEquals(new LanguageTag("fr-ca"), selected);
    }


    @Test
    public void noMatchForLessSpecific() {

        // ACT
        final LanguageTag selected  = _negotiator.selectLanguage(new WeightedValue("en-gb", 1f));

        // ASSERT
        assertNull(selected);
    }


    @Test
    public void qualityAssociatesWithMostSpecificMatch() {

        // ACT
        final LanguageTag selected  = _negotiator.selectLanguage(new WeightedValue("sv-se", 0.1f), new WeightedValue("sv", 1f));

        // ASSERT
        assertEquals(new LanguageTag("sv"), selected);
    }


    @Test
    public void wildcardMatchesAnything() {

        // ACT
        final LanguageTag selected  = _negotiator.selectLanguage(new WeightedValue("*", 1f), new WeightedValue("sv", 0f), new WeightedValue("en", 0f));

        // ASSERT
        assertEquals(new LanguageTag("fr-ca"), selected);
    }


    @Test
    public void parseSingleRange() {

        // ACT
        final List<WeightedValue> ranges = LanguageNegotiator.parse("en");

        // ASSERT
        assertEquals(1, ranges.size());
        assertEquals(new WeightedValue("en", 1.0f), ranges.get(0));
    }


    @Test
    public void parseSingleRangeWithQuality() {

        // ACT
        final List<WeightedValue> ranges = LanguageNegotiator.parse("en;q=0.2");

        // ASSERT
        assertEquals(1, ranges.size());
        assertEquals(new WeightedValue("en", 0.2f), ranges.get(0));
    }


    @Test
    public void parseMultipleRangeWithQuality() {

        // ACT
        final List<WeightedValue> ranges = LanguageNegotiator.parse("en;q=0.2,da;g=0.9");

        // ASSERT
        assertEquals(2, ranges.size());
        assertEquals(new WeightedValue("en", 0.2f), ranges.get(0));
        assertEquals(new WeightedValue("da", 0.9f), ranges.get(1));
    }


    @Test
    public void parseMultipleRangeSomeQuality() {

        // ACT
        final List<WeightedValue> ranges = LanguageNegotiator.parse("en;q=0.2,da");

        // ASSERT
        assertEquals(2, ranges.size());
        assertEquals(new WeightedValue("en", 0.2f), ranges.get(0));
        assertEquals(new WeightedValue("da", 1.0f), ranges.get(1));
    }


    @Test
    public void parseMultipleRangeZeroQuality() {

        // ACT
        final LanguageTag lang = _negotiator.selectLanguage(new WeightedValue("en", 0f));

        // ASSERT
        assertNull(lang);
    }
}
