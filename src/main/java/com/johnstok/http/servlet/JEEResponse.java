/*-----------------------------------------------------------------------------
 * Copyright © 2013 Keith Webster Johnston.
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
package com.johnstok.http.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import com.johnstok.http.sync.AbstractResponse;


/**
 * A HTTP response backed by a Servlet.
 *
 * @author Keith Webster Johnston.
 */
public class JEEResponse
    extends
        AbstractResponse {

    private final HttpServletResponse _response;
    private final Map<String, String> _headers;

    private int    _statusCode   = 200;
    private String _reasonPhrase = "OK";


    /**
     * Constructor.
     *
     * @param response
     */
    public JEEResponse(final HttpServletResponse response) {
        _response = response; // FIXME: Check for null.
        _headers = new HashMap<String, String>();
    }


    /** {@inheritDoc} */
    @Override
    public void setHeader(final String name, final String value) {
        _headers.put(name, value);
        _response.setHeader(name, value);
    }


    /** {@inheritDoc} */
    @Override
    public String getHeader(final String name) {
        return _headers.get(name);
    }


    /** {@inheritDoc} */
    @Override
    protected void commit() throws IOException {
        super.commit();
        _response.flushBuffer();
    }


    /** {@inheritDoc} */
    @Override
    public OutputStream getBody() throws IOException {
        return _response.getOutputStream();
    }


    /** {@inheritDoc} */
    @Override
    public void setStatus(final int statusCode, final String reasonPhrase) {
        _statusCode = statusCode;
        _reasonPhrase = reasonPhrase;
        _response.setStatus(statusCode, reasonPhrase);
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
