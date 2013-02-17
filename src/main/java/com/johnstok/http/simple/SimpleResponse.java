/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
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
    public void close() throws IOException {
        _response.close();
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
