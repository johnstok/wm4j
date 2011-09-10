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
import java.util.Date;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import wm.AbstractResponse;
import wm.BodyWriter;
import wm.Response;
import wm.Status;


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
    private       boolean      _committed;


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
    }


    /** {@inheritDoc} */
    @Override
    public void setStatus(final Status code) {
        _response.setStatus(map(code));
    }


    /** {@inheritDoc} */
    @Override
    public Status getStatus() {
        return map(_response.getStatus());
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
    public void write(final BodyWriter value) throws IOException {
        // FIXME: The whole body is read into memory.
        commit();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        value.write(baos);
        _channel.write(ChannelBuffers.copiedBuffer(baos.toByteArray()));
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasBody() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public void setHeader(final String name, final Date value) {
        setHeader(name, _dateFormatter.format(value));
    }

    /**
     * TODO: Add a description for this method.
     */
    void commit() {
        _committed = true;
        _channel.write(_response);
    }


    /**
     * TODO: Add a description for this method.
     *
     * @return
     */
    boolean isCommitted() {
        return _committed;
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


    private Status map(final HttpResponseStatus status) {
        switch (status.getCode()) {
            case 202:
                return Status.ACCEPTED;
            case 502:
                return Status.BAD_GATEWAY;
            case 400:
                return Status.BAD_REQUEST;
            case 409:
                return Status.CONFLICT;
            case 201:
                return Status.CREATED;
            case 417:
                return Status.EXPECTATION_FAILED;
            case 403:
                return Status.FORBIDDEN;
            case 302:
                return Status.FOUND;
            case 504:
                return Status.GATEWAY_TIMEOUT;
            case 410:
                return Status.GONE;
            case 500:
                return Status.INTERNAL_SERVER_ERROR;
            case 411:
                return Status.LENGTH_REQUIRED;
            case 405:
                return Status.METHOD_NOT_ALLOWED;
            case 301:
                return Status.MOVED_PERMANENTLY;
            case 300:
                return Status.MULTIPLE_CHOICES;
            case 406:
                return Status.NOT_ACCEPTABLE;
            case 404:
                return Status.NOT_FOUND;
            case 501:
                return Status.NOT_IMPLEMENTED;
            case 304:
                return Status.NOT_MODIFIED;
            case 204:
                return Status.NO_CONTENT;
            case 200:
                return Status.OK;
            case 206:
                return Status.PARTIAL_CONTENT;
            case 402:
                return Status.PAYMENT_REQUIRED;
            case 412:
                return Status.PRECONDITION_FAILED;
            case 407:
                return Status.PROXY_AUTHENTICATION_REQUIRED;
            case 416:
                return Status.REQUESTED_RANGE_NOT_SATISFIABLE;
            case 413:
                return Status.REQUEST_ENTITY_TOO_LARGE;
            case 408:
                return Status.REQUEST_TIMEOUT;
            case 414:
                return Status.REQUEST_URI_TOO_LONG;
            case 205:
                return Status.RESET_CONTENT;
            case 303:
                return Status.SEE_OTHER;
            case 503:
                return Status.SERVICE_UNAVAILABLE;
            case 307:
                return Status.TEMPORARY_REDIRECT;
            case 401:
                return Status.UNAUTHORIZED;
            case 415:
                return Status.UNSUPPORTED_MEDIA_TYPE;
            case 305:
                return Status.USE_PROXY;
            case 505:
                return Status.VERSION_NOT_SUPPORTED;
            default:
                // FIXME: Use a subclass of RuntimeException.
                throw new RuntimeException("Unsupported Status.");
        }
    }
}
