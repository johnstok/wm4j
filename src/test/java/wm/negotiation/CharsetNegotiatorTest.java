/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.negotiation;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import wm.WeightedValue;



/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public class CharsetNegotiatorTest {

    private static final Charset UTF_8      = Charset.forName("UTF-8");
    private static final Charset UTF_16BE   = Charset.forName("UTF-16BE");
    private static final Charset ISO_8859_1 = Charset.forName("iso-8859-1");

    CharsetNegotiator _negotiator;


    @Before
    public void setUp() {
        _negotiator = new CharsetNegotiator(UTF_16BE, UTF_8, ISO_8859_1);
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
                    new WeightedValue("utf-8", 1f)));

        // ASSERT
        Assert.assertEquals(UTF_8, selected);
    }


    @Test
    public void subOptimaldefaultGivesISO_8859_1() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                Collections.singletonList(
                    new WeightedValue("utf-8", 0.5f)));

        // ASSERT
        Assert.assertEquals(ISO_8859_1, selected);
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
                    add(new WeightedValue("*", 0.1f));
                }}
            );

        // ASSERT
        Assert.assertEquals(UTF_8, selected);
    }


    @Test
    public void wildcardMatchRespectsExclusionOnName() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                new ArrayList<WeightedValue>() {{
                    add(new WeightedValue("utf-8", 0f));
                    add(new WeightedValue("*",    1f));
                }}
            );

        // ASSERT
        Assert.assertEquals(UTF_16BE, selected);
    }


    @Test
    public void wildcardMatchRespectsExclusionOnAlias() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                new ArrayList<WeightedValue>() {{
                    add(new WeightedValue("UnicodeBigUnmarked", 0f));
                    add(new WeightedValue("*",    1f));
                }}
            );

        // ASSERT
        Assert.assertEquals(UTF_8, selected);
    }


    @Test
    public void highestWeightCharsetIsSelected() {

        // ACT
        Charset selected  =
            _negotiator.selectCharset(
                new ArrayList<WeightedValue>() {{
                    add(new WeightedValue("*",   0.001f));
                    add(new WeightedValue("utf-8", 0.5f));
                    add(new WeightedValue("utf-16be", 1f));
                }}
            );

        // ASSERT
        Assert.assertEquals(UTF_16BE, selected);
    }
}
