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
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.johnstok.http.Version;
import com.johnstok.http.sync.AbstractRequest;


/**
 * Implementation of the {@link Request} API using the Simple HTTP library.
 *
 * @author Keith Webster Johnston.
 */
public class SimpleRequest
    extends
        AbstractRequest {

    private final org.simpleframework.http.Request _request;
    private final Version _version;


    /**
     * Constructor.
     *
     * @param request The Simple HTTP request delegated to.
     * @param address The server address at which the request was received.
     */
    public SimpleRequest(final org.simpleframework.http.Request request,
                         final InetSocketAddress serverAddress) {
        super(
            serverAddress,
            // SimpleWeb always uses UTF-8 – see org.simpleframework.http.parse.AddressParser#escape().
            // However, we can call org.simpleframework.http.Request#getTarget and decode the URI ourselves in the AbstractRequest constructor.
            // This would also allow us to remove the query param decoding in the constructor of NettyRequest
            Charset.forName("UTF-8")); //$NON-NLS-1$
        _request = request;     // FIXME: Check for NULL.
        _version = new Version(_request.getMajor(), _request.getMinor());
    }


    /** {@inheritDoc} */
    @Override
    public InputStream getBody() throws IOException {
        return _request.getInputStream();
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> getHeaders() {
        final Map<String, List<String>> headers =
            new HashMap<String, List<String>>();
        for (final String name : _request.getNames()) {
            headers.put(name, _request.getValues(name));
        }
        return headers;
    }


    /** {@inheritDoc} */
    @Override
    public String getMethod() { return _request.getMethod(); }


    /** {@inheritDoc} */
    @Override
    public InetSocketAddress getClientAddress() {
        return _request.getClientAddress();
    }


    /** {@inheritDoc} */
    @Override
    public String getVersion() { return _version.toString(); }


    /** {@inheritDoc} */
    @Override
    public boolean isConfidential() { return _request.isSecure(); }


    /** {@inheritDoc} */
    @Override
    public String getRequestUri() {
        return _request.getTarget();
    }
}
