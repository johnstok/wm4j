/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package com.johnstok.http.netty;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.ssl.SslHandler;
import com.johnstok.http.Version;
import com.johnstok.http.sync.AbstractRequest;


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
    private final Version                  _version;



    /**
     * Constructor.
     *
     * @param request The Netty HTTP request delegated to.
     * @param channel The Netty channel used for IO.
     * @param charset The charset used to interpret the request URI.
     */
    public NettyRequest(final HttpRequest request,
                        final Channel channel,
                        final Charset charset) {
        super((InetSocketAddress) channel.getLocalAddress(),
              charset); // FIXME: Check for NULL.
        _request = request; // FIXME: Check for NULL.
        _channel = channel; // FIXME: Check for NULL.

        _version =
            new Version(
                _request.getProtocolVersion().getMajorVersion(),
                _request.getProtocolVersion().getMinorVersion());
    }


    /** {@inheritDoc} */
    @Override
    public String getMethod() {
        return _request.getMethod().getName();
    }


    /** {@inheritDoc} */
    @Override
    public String getVersion() { return _version.toString(); }


    /** {@inheritDoc} */
    @Override
    public InetSocketAddress getClientAddress() {
        return (InetSocketAddress) _channel.getRemoteAddress();
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
    public InputStream getBody() {
        // FIXME: This reads the whole request body into memory - BAD.
        // Implement a ChannelInputStream.
        return new ByteArrayInputStream(_request.getContent().copy().array());
    }


    /** {@inheritDoc} */
    @Override
    public boolean isConfidential() {
        return null!=_channel.getPipeline().get(SslHandler.class);
    }


    /** {@inheritDoc} */
    @Override
    public String getRequestUri() {
        return _request.getUri();
    }
}
