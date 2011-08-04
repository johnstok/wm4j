/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import wm.CharsetNegotiator;
import wm.WeightedValue;



/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public class CharsetNegotiatorTest {

    CharsetNegotiator _negotiator;


    @Before
    public void setUp() {
        _negotiator = new CharsetNegotiator();
    }


    @After
    public void tearDown() {
        _negotiator = null;
    }


    @Test
    public void nullGivesNull() {

        // ACT
        Charset selected  = _negotiator.selectCharset(null);

        // ASSERT
        Assert.assertNull(selected);
    }


    @Test
    public void defaultGivesDefault() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                Collections.singletonList(
                    new WeightedValue(Charset.defaultCharset().name(), 1f)));

        // ASSERT
        Assert.assertEquals(Charset.defaultCharset(), selected);
    }


    @Test
    public void subOptimaldefaultGivesISO_8859_1() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                Collections.singletonList(
                    new WeightedValue(Charset.defaultCharset().name(), 0.5f)));

        // ASSERT
        Assert.assertEquals(Charset.forName("iso-8859-1"), selected);
    }


    @Test
    public void missingCharsetGivesNullIfAnyDisallowed() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                new ArrayList<WeightedValue>() {{
                    add(new WeightedValue("*", 0f));
                    add(new WeightedValue("foo", 1f));
                }}
            );

        // ASSERT
        Assert.assertNull(selected);
    }


    @Test
    public void missingCharsetGivesNullIfISO_8859_1Disallowed() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                new ArrayList<WeightedValue>() {{
                    add(new WeightedValue("iso-8859-1", 0f));
                    add(new WeightedValue("foo", 1f));
                }}
            );

        // ASSERT
        Assert.assertNull(selected);
    }


    @Test
    public void noneAllowedGivesNull() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                new ArrayList<WeightedValue>() {{
                    add(new WeightedValue("*", 0f));
                }}
            );

        // ASSERT
        Assert.assertNull(selected);
    }


    @Test
    public void missingCharsetGivesISO_8859_1() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                new ArrayList<WeightedValue>() {{
                    add(new WeightedValue("foo", 1f));
                    add(new WeightedValue("bar", 0.5f));
                }}
            );

        // ASSERT
        Assert.assertEquals(Charset.forName("iso-8859-1"), selected);
    }


    @Test
    public void wildcardMatchGivesBig5() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                new ArrayList<WeightedValue>() {{
                    add(new WeightedValue("foo", 1f));
                    add(new WeightedValue("*", 0.1f));
                }}
            );

        // ASSERT
        Assert.assertEquals(Charset.forName("Big5"), selected);
    }


    @Test
    public void wildcardMatchRespectsExclusionOnName() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                new ArrayList<WeightedValue>() {{
                    add(new WeightedValue("Big5", 0f));
                    add(new WeightedValue("*",    1f));
                }}
            );

        // ASSERT
        Assert.assertEquals(Charset.forName("Big5-HKSCS"), selected);
    }


    @Test
    public void wildcardMatchRespectsExclusionOnAlias() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                new ArrayList<WeightedValue>() {{
                    add(new WeightedValue("csBig5", 0f));
                    add(new WeightedValue("*",    1f));
                }}
            );

        // ASSERT
        Assert.assertEquals(Charset.forName("Big5-HKSCS"), selected);
    }


    @Test
    public void highestWeightCharsetIsSelected() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                new ArrayList<WeightedValue>() {{
                    add(new WeightedValue("*",   0.001f));
                    add(new WeightedValue("utf-8", 0.5f));
                    add(new WeightedValue("cp1250", 1f));
                }}
            );

        // ASSERT
        Assert.assertEquals(Charset.forName("cp1250"), selected);
    }
}
