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

import java.io.IOException;
import java.io.OutputStream;
import com.johnstok.http.sync.AbstractResponse;
import com.johnstok.http.sync.Response;
import com.sun.net.httpserver.HttpExchange;


/**
 * An implementation of the {@link Response} interface backed by a
 * {@link HttpExchange}.
 *
 * @author Keith Webster Johnston.
 */
@SuppressWarnings("restriction")
public class SunHttpResponse
    extends
        AbstractResponse {

    private final HttpExchange _exchange;
    private final int _contentLength = 0; // Implies chunked encoding.
    private int    _statusCode   = 200;
    private String _reasonPhrase = "OK";


    /**
     * Constructor.
     *
     * @param exchange
     */
    public SunHttpResponse(final HttpExchange exchange) {
        _exchange = exchange; // FIXME: Check not null.
    }


    /** {@inheritDoc} */
    @Override
    public void setHeader(final String name, final String value) {
        // FIXME: Intercept setting of Content-Length.
        _exchange.getResponseHeaders().add(name, value);
    }


    /** {@inheritDoc} */
    @Override
    public String getHeader(final String name) {
        return _exchange.getResponseHeaders().getFirst(name);
    }

    /** {@inheritDoc} */
    @Override
    protected void commit() throws IOException {
        super.commit();
        _exchange.sendResponseHeaders(_statusCode, _contentLength);
    }


    /** {@inheritDoc} */
    @Override
    public OutputStream getBody() throws IOException {
        commit(); // Guarantees sendResponseHeaders is called first - should we promote this to be a part of our API contract?
        return _exchange.getResponseBody();
    }


    /** {@inheritDoc} */
    @Override
    public void close() {
        _exchange.close();
    }


    /** {@inheritDoc} */
    @Override
    public void setStatus(final int statusCode, final String reasonPhrase) {
        _statusCode = statusCode;
        _reasonPhrase = reasonPhrase;
    }


    /** {@inheritDoc} */
    @Override
    public int getStatusCode() {
        return _statusCode;
    }


    /** {@inheritDoc} */
    @Override
    public String getReasonPhrase() {
        return _reasonPhrase;
    }
}
