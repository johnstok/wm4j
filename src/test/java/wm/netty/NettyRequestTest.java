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
 * along with wm4j. If not, see <http://www.gnu.org/licenses/>.
 *---------------------------------------------------------------------------*/
package wm.netty;

import static org.junit.Assert.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelConfig;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import wm.Path;
import wm.Request;
import wm.Scheme;
import wm.Version;


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
                new FakeChannel());

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
                new FakeChannel());

        // ACT
        final String m = r.getMethod();

        // ASSERT
        assertEquals("GET", m);
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
                new FakeChannel());

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
                new FakeChannel());

        // ACT
        final String domain = r.getDomain();

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
                new FakeChannel());

        // ACT
        final Version v = r.getVersion();

        // ASSERT
        assertEquals(1, v.major());
        assertEquals(1, v.minor());
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
                new FakeChannel());

        // ACT
        final Path p = r.getPath(Charset.forName("UTF-8"));

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
                new FakeChannel());

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
                new FakeChannel());

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
                    new FakeChannel());

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
                new FakeChannel());

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
                new FakeChannel());

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
                new FakeChannel());

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


    private static class FakeChannel implements Channel {

        InetSocketAddress _localAddress =
            InetSocketAddress.createUnresolved("localhost", 80);

        FakeChannel() { super(); }

        /** {@inheritDoc} */
        @Override
        public int compareTo(final Channel arg0) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public Integer getId() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelFactory getFactory() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public Channel getParent() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelConfig getConfig() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelPipeline getPipeline() {
            return new DefaultChannelPipeline();
        }

        /** {@inheritDoc} */
        @Override
        public boolean isOpen() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public boolean isBound() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public boolean isConnected() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public SocketAddress getLocalAddress() {
            return _localAddress;
        }

        /** {@inheritDoc} */
        @Override
        public SocketAddress getRemoteAddress() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelFuture write(final Object message) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelFuture write(final Object message, final SocketAddress remoteAddress) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelFuture bind(final SocketAddress localAddress) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelFuture connect(final SocketAddress remoteAddress) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelFuture disconnect() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelFuture unbind() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelFuture close() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelFuture getCloseFuture() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public int getInterestOps() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public boolean isReadable() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public boolean isWritable() {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelFuture setInterestOps(final int interestOps) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

        /** {@inheritDoc} */
        @Override
        public ChannelFuture setReadable(final boolean readable) {
            throw new UnsupportedOperationException("Method not implemented.");
        }

    }
}
