/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package com.johnstok.http.netty;

import java.io.IOException;
import java.io.OutputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import com.johnstok.http.sync.AbstractResponse;


/**
 * Implementation of the {@link Response} API using the Netty HTTP library.
 *
 * @author Keith Webster Johnston.
 */
public class NettyResponse
    extends
        AbstractResponse {

    private final HttpResponse _response;
    private final Channel      _channel;
    private final OutputStream _outputStream;


    /**
     * Constructor.
     *
     * @param response The Netty {@link HttpResponse}.
     * @param channel  The Netty channel used for IO.
     */
    public NettyResponse(final HttpResponse response,
                         final Channel channel) {
        _response = response; // FIXME: Check for NULL.
        _channel  = channel;  // FIXME: Check for NULL.
        _outputStream = new ChannelOutputStream(channel);
    }


    /** {@inheritDoc} */
    @Override
    public void setHeader(final String name, final String value) {
        if (null==value) {
            _response.removeHeader(name);
        } else {
            _response.setHeader(name, value);
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getHeader(final String name) {
        return _response.getHeader(name);
    }


    /** {@inheritDoc} */
    @Override
    protected void commit() throws IOException {
        super.commit();
        ChannelFuture f = _channel.write(_response);
        f.awaitUninterruptibly();
        if (!f.isSuccess()) {
            throw new IOException("Error committing response.", f.getCause());
        }
    }


    /** {@inheritDoc} */
    @Override
    public OutputStream getBody() throws IOException {
        return _outputStream;
    }


    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        ChannelFuture f = _channel.close();
        f.awaitUninterruptibly();
        if (!f.isSuccess()) {
            throw new IOException("Error closing response.", f.getCause());
        }

    }


    /** {@inheritDoc} */
    @Override
    public void setStatus(final int statusCode, final String reasonPhrase) {
        _response.setStatus(new HttpResponseStatus(statusCode, reasonPhrase));
    }


    /** {@inheritDoc} */
    @Override
    public int getStatusCode() {
        return _response.getStatus().getCode();
    }


    /** {@inheritDoc} */
    @Override
    public String getReasonPhrase() {
        return _response.getStatus().getReasonPhrase();
    }
}
