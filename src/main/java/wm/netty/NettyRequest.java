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
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.ssl.SslHandler;
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
     * @param request The Netty HTTP request delegated to.
     * @param channel The Netty channel used for IO.
     */
    public NettyRequest(final HttpRequest request,
                        final Channel channel) {
        super(
            ((InetSocketAddress) channel.getLocalAddress()).getPort(),
            ((InetSocketAddress) channel.getLocalAddress()).getHostName(),
            "UTF-8");
        _request = request; // FIXME: Check for NULL.
        _channel = channel; // FIXME: Check for NULL.

        final QueryStringDecoder decoder =
            new QueryStringDecoder(request.getUri(), _requestUriCharset);
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
    public InetSocketAddress getClientAddress() {
        return (InetSocketAddress) _channel.getRemoteAddress();
    }


    /** {@inheritDoc} */
    @Override
    public String getPath() { return _path; }


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
    public String getRequestUri() { return _request.getUri(); }


    /** {@inheritDoc} */
    @Override
    public boolean isConfidential() {
        return null!=_channel.getPipeline().get(SslHandler.class);
    }
}
