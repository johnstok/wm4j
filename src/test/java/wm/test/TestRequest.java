/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wm.Method;
import wm.Request;
import wm.Version;


/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public class TestRequest
    implements
        Request {

    private String _method = Method.GET;
    private final HashMap<String, List<String>> _headers =
        new HashMap<String, List<String>>();


    /** {@inheritDoc} */
    @Override
    public Request append_to_response_body(final byte[] bytes) throws IOException {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public byte[] get_req_body() throws IOException {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public InputStream get_req_body_stream() throws IOException {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String get_req_cookie() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String get_req_cookie_value(final String valueName) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String get_req_header(final String headerName) {
        final List<String> values = _headers.get(headerName);
        if (null==values || 0==values.size()) {
            return null;
        }
        return values.get(0);
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> get_req_headers() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String get_req_method() {
        return _method;
    }


    /** {@inheritDoc} */
    @Override
    public InetAddress get_req_peer() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> get_req_qs() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String get_req_qs_value(final String paramName) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String get_req_qs_value(final String paramName, final String defaultValue) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Version get_req_version() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public byte[] get_resp_body() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String get_resp_header(final String headerName) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> get_resp_headers() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public boolean get_resp_redirect() {
        return false;
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
    public String path_disp() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Map<Object, String> path_info() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String path_info(final Object atom) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String path_raw() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public String[] path_tokens() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Request remove_resp_header(final String headerName) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Request set_disp_path(final String path) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Request set_req_body(final byte[] bytes) throws IOException {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Request set_resp_body(final byte[] bytes) throws IOException {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Request set_resp_body(final InputStream stream) throws IOException {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Request set_resp_header(final String headerName, final String headerValue) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Request set_resp_headers(final Map<String, String[]> headers) {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    /** {@inheritDoc} */
    @Override
    public Request set_resp_redirect(final boolean redirect) {
        throw new UnsupportedOperationException("Method not implemented.");
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

}
