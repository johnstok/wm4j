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
 * along with wm4j. If not, see <http://www.gnu.org/licenses/>.
 *---------------------------------------------------------------------------*/
package wm;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;


/**
 * Provides default implementations of many {@link Request} API methods.
 *
 * @author Keith Webster Johnston.
 */
public abstract class AbstractRequest
    implements
        Request {

    private   final int    _port;
    private   final String _host;
    protected final String _requestUriCharset;


    /**
     * Constructor.
     *
     * @param port       The port on which this request was received.
     * @param host       The host name that received this request.
     * @param uriCharset The character set used to parse the request URI.
     */
    public AbstractRequest(final int port,
                           final String host,
                           final String uriCharset) {
        _port = port; // TODO: Must be greater than 0.
        _host = host; // TODO: Not null.
        _requestUriCharset = uriCharset;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasHeader(final String headerName) {
        return null!=getHeader(headerName);
    }


    /** {@inheritDoc} */
    @Override
    public String getQueryValue(final String paramName) {
        return getQueryValue(paramName, null);
    }


    /** {@inheritDoc} */
    @Override
    public String getHeader(final String headerName) {
        return getHeader(headerName, null);
    }


    /** {@inheritDoc} */
    @Override
    public int getPort() { return _port; }


    /** {@inheritDoc} */
    @Override
    public String getDomain() { return _host; }


    /** {@inheritDoc} */
    @Override
    public final Scheme getScheme() {
        return (isConfidential()) ? Scheme.https : Scheme.http ;
    }


    /** {@inheritDoc} */
    @Override
    public String getPath() {
        try {
            return URLDecoder.decode(new URI(getRequestUri()).getRawPath(), _requestUriCharset);
        } catch (final URISyntaxException e) {
            // FIXME: Is there a better solution here?
            throw new RuntimeException(e);
        } catch (final UnsupportedEncodingException e) {
            // FIXME: Is there a better solution here?
            throw new RuntimeException(e);
        }
    }
}
