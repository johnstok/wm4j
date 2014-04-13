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
package com.johnstok.http.simple;

import java.io.IOException;
import java.io.OutputStream;
import com.johnstok.http.sync.AbstractResponse;


/**
 * Implementation of the {@link Response} API using the Simple HTTP library.
 *
 * @author Keith Webster Johnston.
 */
public class SimpleResponse
    extends
        AbstractResponse {

    private final org.simpleframework.http.Response _response;


    /**
     * Constructor.
     *
     * @param response
     */
    public SimpleResponse(final org.simpleframework.http.Response response) {
        _response = response;
    }


    /** {@inheritDoc} */
    @Override
    public void setHeader(final String name, final String value) {
        _response.set(name, value);
    }


    /** {@inheritDoc} */
    @Override
    public String getHeader(final String name) {
        return _response.getValue(name);
    }


    /** {@inheritDoc} */
    @Override
    protected void commit() throws IOException {
        super.commit();
        _response.commit();
    }


    /** {@inheritDoc} */
    @Override
    public OutputStream getBody() throws IOException {
        return _response.getOutputStream();
    }


    /** {@inheritDoc} */
    @Override
    public void setStatus(final int statusCode, final String reasonPhrase) {
        _response.setCode(statusCode);
        _response.setText(reasonPhrase);
    }


    /** {@inheritDoc} */
    @Override
    public int getStatusCode() {
        return _response.getCode();
    }


    /** {@inheritDoc} */
    @Override
    public String getReasonPhrase() {
        return _response.getText();
    }
}
