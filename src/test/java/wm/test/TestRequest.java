/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wm.AbstractRequest;
import wm.Method;
import wm.Request;
import wm.Version;


/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public class TestRequest
    extends
        AbstractRequest {

    private String                              _method = Method.GET;
    private final HashMap<String, List<String>> _headers =
        new HashMap<String, List<String>>();
    private byte[]                              _body;


    /**
     * Constructor.
     */
    public TestRequest() {
        super(new HashMap<String, String>(), "/");
    }


    /** {@inheritDoc} */
    @Override
    public byte[] getBody() {
        return _body;
    }


    /** {@inheritDoc} */
    @Override
    public InputStream getBodyAsStream() {
        return new ByteArrayInputStream(_body);
    }


    /** {@inheritDoc} */
    @Override
    public String getHeader(final String headerName) {
        final List<String> values = _headers.get(headerName);
        if (null==values || 0==values.size()) {
            return null;
        }
        return values.get(0);
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> getHeaders() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String getMethod() {
        return _method;
    }


    /** {@inheritDoc} */
    @Override
    public InetAddress getAddress() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> getQueryValues() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String getQueryValue(final String paramName, final String defaultValue) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Version getVersion() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String path() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String path_app_root() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String path_raw() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Request setBody(final byte[] bytes) throws IOException {
        _body = bytes;
        return this;
    }


    /**
     * TODO: Add a description for this method.
     *
     * @param name
     * @param value
     */
    public void setHeader(final String name, final String value) {
        final ArrayList<String> values = new ArrayList<String>();
        values.add(value);
        _headers.put(name, values);
    }


    /**
     * Mutator.
     *
     * @param method The new HTTP method to set.
     */
    public void setMethod(final String method) {
        _method = method;
    }


    /**
     * Mutator.
     *
     * @param headerName
     * @param value
     */
    public void setHeader(final String headerName, final Date value) {
        setHeader(headerName, _dateFormatter.format(value));
    }
}
