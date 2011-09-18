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
package wm.netty;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import wm.AbstractRequest;
import wm.Request;
import wm.Version;


/**
 * Implementation of the {@link Request} API using the Netty HTTP library.
 *
 * @author Keith Webster Johnston.
 */
public class NettyRequest
    extends
        AbstractRequest {

    private final HttpRequest              _request;
    private final Channel                  _channel;
    private final String                   _path;
    private final Map<String,List<String>> _qParams;



    /**
     * Constructor.
     *
     * @param request     The Netty HTTP request delegated to.
     * @param channel     The Netty channel used for IO.
     */
    public NettyRequest(final HttpRequest request,
                        final Channel channel) {
        _request = request; // FIXME: Check for NULL.
        _channel = channel; // FIXME: Check for NULL.

        // FIXME: Allow charset to be configured.
        final QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        _path = decoder.getPath();
        _qParams = decoder.getParameters();
    }


    /** {@inheritDoc} */
    @Override
    public String getMethod() {
        return _request.getMethod().getName();
    }


    /** {@inheritDoc} */
    @Override
    public Version getVersion() {
        return new Version() {

            /** {@inheritDoc} */
            @Override public int major() { return _request.getProtocolVersion().getMajorVersion(); }


            /** {@inheritDoc} */
            @Override public int minor() { return _request.getProtocolVersion().getMinorVersion(); }
        };
    }


    /** {@inheritDoc} */
    @Override
    public InetAddress getClientAddress() {
        return ((InetSocketAddress) _channel.getRemoteAddress()).getAddress();
    }


    /** {@inheritDoc} */
    @Override
    public URI getPath() {
        try {
            return new URI(_path);
        } catch (final URISyntaxException e) {
            // FIXME: Is there a better solution here?
            throw new RuntimeException(e);
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getHeader(final String headerName,
                            final String defaultValue) {
        final String value = _request.getHeader(headerName);
        if (null==value) {
            return defaultValue;
        }
        return value;
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> getHeaders() {
        final Map<String, List<String>> headers = new HashMap<String, List<String>>();
        for (final String name : _request.getHeaderNames()) {
            headers.put(name, _request.getHeaders(name));
        }
        return headers;
    }


    /** {@inheritDoc} */
    @Override
    public String getQueryValue(final String paramName, final String defaultValue) {
        final List<String> p = _qParams.get(paramName);
        if (null==p || 0==p.size()) { return defaultValue; }
        return p.get(0);
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> getQueryValues() {
        return _qParams;
    }

    /*
     * BODY.
     */

    /** {@inheritDoc} */
    @Override
    public InputStream getBody() {
        // FIXME: This reads the whole request body into memory - BAD.
        return new ByteArrayInputStream(_request.getContent().copy().array());
    }


    /** {@inheritDoc} */
    @Override
    public URL getUrl() {
        try {
            // FIXME: Scheme is hardcoded.
            // FIXME: Domain is hardcoded.
            // FIXME: Port is hard-coded.
            // FIXME: Doesn't correctly handle Mismatch between Absolute request URI & Host header.
            // FIXME: Confirm the path is not decoded or normalised.
            return new URL("http", "localhost", 80, _request.getUri());
        } catch (final MalformedURLException e) {
            // FIXME: Is there a better solution here?
            throw new RuntimeException(e);
        }
    }
}
