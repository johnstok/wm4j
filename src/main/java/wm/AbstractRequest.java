/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;


/**
 * Provides default implementations of many {@link Request} API methods.
 *
 * @author Keith Webster Johnston.
 */
public abstract class AbstractRequest
    implements
        Request {

    private   final int     _port;
    private   final String  _host;
    protected final Charset _requestUriCharset;
    private   final URI     _requestUri;


    /**
     * Constructor.
     *
     * @param port       The port on which this request was received.
     * @param host       The host name that received this request.
     * @param uriCharset The character set used to parse the request URI.
     */
    public AbstractRequest(final int port,
                           final String host,
                           final String requestUri,
                           final Charset uriCharset) {
        _port = port; // TODO: Must be greater than 0.
        _host = host; // TODO: Not null.
        try {
            _requestUri = new URI(requestUri);
        } catch (final URISyntaxException e) {
            throw new ClientHttpException(Status.BAD_REQUEST);
        }
        _requestUriCharset = uriCharset;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasQueryValue(final String paramName) {
        return null!=getQueryValue(paramName);
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
    public URI getRequestUri() { return _requestUri; }


    /** {@inheritDoc} */
    @Override
    public final Path getPath(final Charset charset) {
        return new Path(getRequestUri().getRawPath(), charset);
    }
}
