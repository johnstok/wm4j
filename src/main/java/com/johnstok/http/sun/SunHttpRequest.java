/*-----------------------------------------------------------------------------
 * Copyright © 2013 Keith Webster Johnston.
 * All rights reserved.
 *
 * This file is part of wm4j.
 *
 * wm4j is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * wm4j is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with wm4j. If not, see <http://www.gnu.org/licenses/>.
 *---------------------------------------------------------------------------*/
package com.johnstok.http.sun;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import com.johnstok.http.Method;
import com.johnstok.http.RequestURI;
import com.johnstok.http.Version;
import com.johnstok.http.sync.AbstractRequest;
import com.johnstok.http.sync.Request;
import com.sun.net.httpserver.HttpExchange;


/**
 * An implementation of the {@link Request} interface backed by a
 * {@link HttpExchange}.
 *
 * @author Keith Webster Johnston.
 */
@SuppressWarnings("restriction")
public class SunHttpRequest
    extends
        AbstractRequest {

    private final HttpExchange _exchange;

    /**
     * Constructor.
     *
     * @param exchange
     * @param charset
     */
    public SunHttpRequest(final HttpExchange exchange,
                          final Charset charset) {
        super(exchange.getLocalAddress(), charset);
        _exchange = exchange;
    }


    /** {@inheritDoc} */
    @Override
    public RequestURI getRequestUri() {
        return RequestURI.parse(_exchange.getRequestURI().toString()); // TODO: Confirm this correctly handles encoded URLs.
    }


    /** {@inheritDoc} */
    @Override
    public boolean isConfidential() {
        return false; // Assumes we do not use HttpsServer.
    }


    /** {@inheritDoc} */
    @Override
    public Method getMethod() {
        return Method.parse(_exchange.getRequestMethod());
    }


    /** {@inheritDoc} */
    @Override
    public Version getVersion() {
        return Version.parse(_exchange.getProtocol());
    }


    /** {@inheritDoc} */
    @Override
    public InetSocketAddress getClientAddress() {
        return _exchange.getRemoteAddress();
    }


    /** {@inheritDoc} */
    @Override
    public String getHeader(final String headerName, final String defaultValue) {
        String value = _exchange.getRequestHeaders().getFirst(headerName);
        return (null==value) ? defaultValue : value;
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> getHeaders() {
        return _exchange.getRequestHeaders();
    }


    /** {@inheritDoc} */
    @Override
    public InputStream getBody() {
        return _exchange.getRequestBody();
    }
}
