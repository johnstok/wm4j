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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.netty.buffer.ChannelBuffer;
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
     * @param atomMatches
     * @param dispPath
     */
    public NettyRequest(final HttpRequest request,
                        final Channel channel,
                        final Map<String, String> atomMatches,
                        final String dispPath) {
        super(atomMatches, dispPath);
        _request = request; // FIXME: Check for NULL.
        _channel = channel; // FIXME: Check for NULL.

        // FIXME: Allow charset to be configured.
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
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
    public InetAddress getAddress() {
        return ((InetSocketAddress) _channel.getRemoteAddress()).getAddress();
    }


    /** {@inheritDoc} */
    @Override
    public String path() {
        return _path;
    }


    /** {@inheritDoc} */
    @Override
    public String path_raw() {
        return path();
    }


    /** {@inheritDoc} */
    @Override
    public String getHeader(final String headerName) {
        return _request.getHeader(headerName);
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
        List<String> p = _qParams.get(paramName);
        if (null==p || 0==p.size()) { return defaultValue; }
        return p.get(0);
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> getQueryValues() {
        return _qParams;
    }


    /** {@inheritDoc} */
    @Override
    public String path_app_root() {
        throw new UnsupportedOperationException("Method not implemented.");
    }

    /*
     * BODY.
     */

    /** {@inheritDoc} */
    @Override
    public byte[] getBody() throws IOException {
        ChannelBuffer buf = _request.getContent();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // FIXME: Consume remaining data.
        return baos.toByteArray();
    }


    /** {@inheritDoc} */
    @Override
    public Request setBody(final byte[] bytes) throws IOException {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public InputStream getBodyAsStream() throws IOException {
        throw new UnsupportedOperationException("Method not implemented.");
    }
}
