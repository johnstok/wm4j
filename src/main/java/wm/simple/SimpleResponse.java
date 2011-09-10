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
import java.util.Date;
import wm.AbstractResponse;
import wm.BodyWriter;
import wm.Response;
import wm.Status;


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
        _response.set(name, value);
    }


    /** {@inheritDoc} */
    @Override
    public String getHeader(final String name) {
        return _response.getValue(name);
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


    /** {@inheritDoc} */
    @Override
    public void setHeader(final String name, final Date value) {
        _response.setDate(name, value.getTime());
    }


//  @Override
//  public Request append_to_response_body(final byte[] bytes) throws IOException {
//      _response.getOutputStream().write(bytes);
//      return this;
//  }
//
//
//  @Override
//  public byte[] get_resp_body() {
//      throw new UnsupportedOperationException("Response body can't be read.");
//  }
//
//
//  @Override
//  public String get_resp_header(final String headerName) {
//      return _response.getValue(headerName);
//  }
//
//
//  @Override
//  public Map<String, List<String>> get_resp_headers() {
//      final Map<String, List<String>> headers = new HashMap<String, List<String>>();
//      for (final String name : _response.getNames()) {
//          headers.put(name, _response.getValues(name));
//      }
//      return headers;
//  }
//
//
//  @Override
//  public boolean get_resp_redirect() {
//      return _doRedirect;
//  }
//
//
//  @Override
//  public Request remove_resp_header(final String headerName) {
//      _response.remove(headerName);
//      return this;
//  }
//
//
//  @Override
//  public Request set_resp_body(final byte[] bytes) throws IOException {
//      _response.reset(); // Should only reset the body.
//      _response.getOutputStream().write(bytes);
//      return this;
//  }
//
//
//  @Override
//  public Request set_resp_body(final InputStream stream) throws IOException {
//      copy(stream, _response.getOutputStream());
//      return this;
//  }
//
//
//  @Override
//  public Request set_resp_header(final String headerName, final String headerValue) {
//      _response.set(headerName, headerValue);
//      return this;
//  }
//
//
//  @Override
//  public Request set_resp_headers(final Map<String, String[]> headers) {
//      for (final Map.Entry<String, String[]> header : headers.entrySet()) {
//          remove_resp_header(header.getKey());
//          for (final String value : header.getValue()) {
//              _response.add(header.getKey(), value);
//          }
//      }
//      return this;
//  }
//
//
//  @Override
//  public Request set_resp_redirect(final boolean redirect) {
//      _doRedirect = redirect;
//      return this;
//  }
}
