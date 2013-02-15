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
        return map(_response.getCode());
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
    public void close() throws IOException {
        _response.close();
    }


    /** {@inheritDoc} */
    @Override
    public OutputStream getBody() throws IOException {
        return _response.getOutputStream();
    }
}
