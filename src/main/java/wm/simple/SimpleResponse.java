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
package wm.simple;

import java.io.IOException;
import java.nio.charset.Charset;
import wm.BodyWriter;
import wm.Response;
import wm.Status;


/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public class SimpleResponse
    implements
        Response {

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
    public void setStatus(final Status code) {
        _response.setCode(code.getCode());
        _response.setText(code.getDescription());
    }


    /** {@inheritDoc} */
    @Override
    public Status getStatus() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public void setHeader(final String name, final String value) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String getHeader(final String name) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String getBodyAsString(final Charset charset) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public void write(final BodyWriter value) throws IOException {
        value.write(_response.getOutputStream());
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasBody() {
        throw new UnsupportedOperationException("Method not implemented.");
    }

}
