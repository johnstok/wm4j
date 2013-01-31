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
import com.johnstok.http.Status;
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
    public void setStatus(final Status code) {
        _response.setStatus(map(code));
    }


    /** {@inheritDoc} */
    @Override
    public Status getStatus() {
        return map(_response.getStatus().getCode());
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


    private HttpResponseStatus map(final Status code) {
        switch (code) {
            case ACCEPTED:
                return HttpResponseStatus.ACCEPTED;
            case BAD_GATEWAY:
                return HttpResponseStatus.BAD_GATEWAY;
            case BAD_REQUEST:
                return HttpResponseStatus.BAD_REQUEST;
            case CONFLICT:
                return HttpResponseStatus.CONFLICT;
            case CREATED:
                return HttpResponseStatus.CREATED;
            case EXPECTATION_FAILED:
                return HttpResponseStatus.EXPECTATION_FAILED;
            case FORBIDDEN:
                return HttpResponseStatus.FORBIDDEN;
            case FOUND:
                return HttpResponseStatus.FOUND;
            case GATEWAY_TIMEOUT:
                return HttpResponseStatus.GATEWAY_TIMEOUT;
            case GONE:
                return HttpResponseStatus.GONE;
            case INTERNAL_SERVER_ERROR:
                return HttpResponseStatus.INTERNAL_SERVER_ERROR;
            case LENGTH_REQUIRED:
                return HttpResponseStatus.LENGTH_REQUIRED;
            case METHOD_NOT_ALLOWED:
                return HttpResponseStatus.METHOD_NOT_ALLOWED;
            case MOVED_PERMANENTLY:
                return HttpResponseStatus.MOVED_PERMANENTLY;
            case MULTIPLE_CHOICES:
                return HttpResponseStatus.MULTIPLE_CHOICES;
            case NOT_ACCEPTABLE:
                return HttpResponseStatus.NOT_ACCEPTABLE;
            case NOT_FOUND:
                return HttpResponseStatus.NOT_FOUND;
            case NOT_IMPLEMENTED:
                return HttpResponseStatus.NOT_IMPLEMENTED;
            case NOT_MODIFIED:
                return HttpResponseStatus.NOT_MODIFIED;
            case NO_CONTENT:
                return HttpResponseStatus.NO_CONTENT;
            case OK:
                return HttpResponseStatus.OK;
            case PARTIAL_CONTENT:
                return HttpResponseStatus.PARTIAL_CONTENT;
            case PAYMENT_REQUIRED:
                return HttpResponseStatus.PAYMENT_REQUIRED;
            case PRECONDITION_FAILED:
                return HttpResponseStatus.PRECONDITION_FAILED;
            case PROXY_AUTHENTICATION_REQUIRED:
                return HttpResponseStatus.PROXY_AUTHENTICATION_REQUIRED;
            case REQUESTED_RANGE_NOT_SATISFIABLE:
                return HttpResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
            case REQUEST_ENTITY_TOO_LARGE:
                return HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE;
            case REQUEST_TIMEOUT:
                return HttpResponseStatus.REQUEST_TIMEOUT;
            case REQUEST_URI_TOO_LONG:
                return HttpResponseStatus.REQUEST_URI_TOO_LONG;
            case RESET_CONTENT:
                return HttpResponseStatus.RESET_CONTENT;
            case SEE_OTHER:
                return HttpResponseStatus.SEE_OTHER;
            case SERVICE_UNAVAILABLE:
                return HttpResponseStatus.SERVICE_UNAVAILABLE;
            case TEMPORARY_REDIRECT:
                return HttpResponseStatus.TEMPORARY_REDIRECT;
            case UNAUTHORIZED:
                return HttpResponseStatus.UNAUTHORIZED;
            case UNSUPPORTED_MEDIA_TYPE:
                return HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE;
            case USE_PROXY:
                return HttpResponseStatus.USE_PROXY;
            case VERSION_NOT_SUPPORTED:
                return new HttpResponseStatus(code.getCode(), code.getDescription());
            default:
                // FIXME: Use a subclass of RuntimeException.
                throw new RuntimeException("Unsupported Status.");
        }
    }


    /** {@inheritDoc} */
    @Override
    protected OutputStream getOutputStream() throws IOException {
        return _outputStream;
    }


    /** {@inheritDoc} */
    @Override
    protected void close() {
        _channel.close().awaitUninterruptibly();
    }
}
