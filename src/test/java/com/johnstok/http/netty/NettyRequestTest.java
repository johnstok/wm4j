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
import com.johnstok.http.Version;
import com.johnstok.http.netty.test.FakeChannel;
import com.johnstok.http.sync.Request;


/**
 * Tests for the {@link NettyRequest} class.
 *
 * @author Keith Webster Johnston.
 */
public class NettyRequestTest {


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
        final int port = r.getServerAddress().getPort();

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
