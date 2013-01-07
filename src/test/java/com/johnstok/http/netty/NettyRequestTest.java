/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package com.johnstok.http.netty;

import static org.junit.Assert.*;
import java.nio.charset.Charset;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.johnstok.http.Method;
import com.johnstok.http.Path;
import com.johnstok.http.Scheme;
import com.johnstok.http.Version;
import com.johnstok.http.netty.test.FakeChannel;
import com.johnstok.http.sync.Request;


/**
 * Tests for the {@link NettyRequest} class.
 *
 * @author Keith Webster Johnston.
 */
public class NettyRequestTest {

    private static final String ENC_PUNCT = "%2C%3B%3A%24%26%2B%3D";
    private static final String PUNCT = ",;:$&+=";


    @Test
    public void schemeIsHttpForNonConfidentialRequest() {

        // ARRANGE
        final Request r =
            new NettyRequest(
                new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                    "/"),
                new FakeChannel(),
                Charset.forName("UTF-8"));

        // ACT
        final Scheme s = r.getScheme();

        // ASSERT
        assertEquals(Scheme.http, s);
    }


    @Test
    public void methodQueriedCorrectly() {

        // ARRANGE
        final Request r =
            new NettyRequest(
                new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                "/"),
                new FakeChannel(),
                Charset.forName("UTF-8"));

        // ACT
        final Method m = r.getMethod();

        // ASSERT
        assertEquals(Method.GET, m);
    }


    @Test
    public void portQueriedCorrectly() {

        // ARRANGE
        final Request r =
            new NettyRequest(
                new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                "/"),
                new FakeChannel(),
                Charset.forName("UTF-8"));

        // ACT
        final int port = r.getPort();

        // ASSERT
        assertEquals(80, port);
    }


    @Test
    public void domainQueriedCorrectly() {

        // ARRANGE
        final Request r =
            new NettyRequest(
                new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                "/"),
                new FakeChannel(),
                Charset.forName("UTF-8"));

        // ACT
        final String domain = r.getServerAddress().getHostName();

        // ASSERT
        assertEquals("localhost", domain);
    }


    @Test
    public void versionQueriedCorrectly() {

        // ARRANGE
        final Request r =
            new NettyRequest(
                new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                "/"),
                new FakeChannel(),
                Charset.forName("UTF-8"));

        // ACT
        final Version v = r.getVersion();

        // ASSERT
        assertEquals(1, v.getMajor());
        assertEquals(1, v.getMinor());
    }


    @Test
    public void pathQueriedCorrectly() {

        // ARRANGE
        final Request r =
            new NettyRequest(
                new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                "/"),
                new FakeChannel(),
                Charset.forName("UTF-8"));

        // ACT
        final Path p = r.getPath();

        // ASSERT
        assertEquals(0, p.getSize());
    }


    @Test
    public void getPresentQueryParam() {

        // ARRANGE
        final Request r =
            new NettyRequest(
                new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                "/?foo=bar"),
                new FakeChannel(),
                Charset.forName("UTF-8"));

        // ACT
        final String qpValue = r.getQueryValue("foo");

        // ASSERT
        assertEquals("bar", qpValue);
    }


    @Test
    public void getDefaultForMissingQueryParam() {

        // ARRANGE
        final Request r =
            new NettyRequest(
                new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                "/?foo=bar"),
                new FakeChannel(),
                Charset.forName("UTF-8"));

        // ACT
        final String qpValue = r.getQueryValue("baz", "meh");

        // ASSERT
        assertEquals("meh", qpValue);
    }


    @Test
    public void queryParamKeysAreDecoded() {

        // ARRANGE
        final Request r =
            new NettyRequest(
                new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                    "/?"+ENC_PUNCT+"=bar"),
                    new FakeChannel(),
                    Charset.forName("UTF-8"));

        // ACT
        final String qpValue = r.getQueryValue(PUNCT);

        // ASSERT
        assertEquals("bar", qpValue);
    }


    @Test
    public void queryParamValuesAreDecoded() {

        // ARRANGE
        final Request r =
            new NettyRequest(
                new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                "/?foo="+ENC_PUNCT),
                new FakeChannel(),
                Charset.forName("UTF-8"));

        // ACT
        final String qpValue = r.getQueryValue("foo");

        // ASSERT
        assertEquals(PUNCT, qpValue);
    }


    @Test
    public void defaultQueryParamValuesAreNotDecoded() {

        // ARRANGE
        final Request r =
            new NettyRequest(
                new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                "/?foo=bar"),
                new FakeChannel(),
                Charset.forName("UTF-8"));

        // ACT
        final String qpValue = r.getQueryValue("baz", ENC_PUNCT);

        // ASSERT
        assertEquals(ENC_PUNCT, qpValue);
    }


    @Test
    public void getNullForMissingQueryParam() {

        // ARRANGE
        final Request r =
            new NettyRequest(
                new DefaultHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.GET,
                "/?foo=bar"),
                new FakeChannel(),
                Charset.forName("UTF-8"));

        // ACT
        final String qpValue = r.getQueryValue("baz");

        // ASSERT
        assertNull(qpValue);
    }


    /**
     * Set up the test harness.
     */
    @Before
    public void setUp() {}


    /**
     * Tear down the test harness.
     */
    @After
    public void tearDown() {}
}
