/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package com.johnstok.http.simple;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import com.johnstok.http.Status;
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
    public boolean hasBody() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public void setHeader(final String name, final Date value) {
        _response.setDate(name, value.getTime());
    }


    /** {@inheritDoc} */
    @Override
    protected void commit() throws IOException {
        super.commit();
        _response.commit();
    }


    /** {@inheritDoc} */
    @Override
    protected void close() throws IOException {
        _response.close();
    }


    /** {@inheritDoc} */
    @Override
    protected OutputStream getOutputStream() throws IOException {
        return _response.getOutputStream();
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
