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
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.johnstok.http.sync.AbstractRequest;


/**
 * A HTTP request backed by a Servlet.
 *
 * <p>Limitations
 * <ul>
 *   <li>The {@code getVersion()} method is not currently supported.</li>
 *   <li>The {@code getRequestUri()} method will only return a path and (optionally a query).</li>
 * </ul>
 *
 * @author Keith Webster Johnston.
 */
public class JEERequest
    extends
        AbstractRequest {

    private final HttpServletRequest _request;

    /**
     * Constructor.
     *
     * @param address
     * @param uriCharset
     * @param request
     */
    public JEERequest(final InetSocketAddress address,
                      final Charset uriCharset,
                      final HttpServletRequest request) {
        super(address, uriCharset);
        _request = request; // FIXME: Check for null.
    }


    /** {@inheritDoc} */
    @Override
    public String getRequestUri() {
        /*
         * There is no way to retrieve the original URI in the request line via
         * the Servlet API - we need to reconstitute it. At present the host &
         * protocol are not included.
         */
        String queryString = _request.getQueryString();
        return
            _request.getRequestURI() // TODO: Is this path decoded?
            + (null==queryString ? "" : "?"+queryString);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isConfidential() {
        return _request.isSecure();
    }


    /** {@inheritDoc} */
    @Override
    public String getMethod() {
        return _request.getMethod();
    }


    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        throw
            new UnsupportedOperationException(
                "Not supported by Servlet specification.");
    }


    /** {@inheritDoc} */
    @Override
    public InetSocketAddress getClientAddress() {
        return
            new InetSocketAddress(
                _request.getRemoteHost(), // TODO: What about the IP address?!
                _request.getRemotePort());
    }


    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getHeaders() {
        // TODO: Move to constructor for efficiency.
        HashMap<String, List<String>> headers =
            new HashMap<String, List<String>>();
        Enumeration<String> headerNames = _request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            ArrayList<String> values = new ArrayList<String>();
            Enumeration<String> headerValues = _request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                values.add(headerValues.nextElement());
            }
            headers.put(headerName, values);
        }
        return headers;
    }


    /** {@inheritDoc} */
    @Override
    public InputStream getBody() throws IOException {
        return _request.getInputStream();
    }
}
