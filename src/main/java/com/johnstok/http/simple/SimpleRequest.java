package com.johnstok.http.simple;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.johnstok.http.Method;
import com.johnstok.http.RequestURI;
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
    public String getHeader(final String headerName,
                            final String defaultValue) {
        final String value = _request.getValue(headerName);
        if (null==value) {
            return defaultValue;
        }
        return value;
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> getHeaders() {
        final Map<String, List<String>> headers = new HashMap<String, List<String>>();
        for (final String name : _request.getNames()) {
            headers.put(name, _request.getValues(name));
        }
        return headers;
    }


    /** {@inheritDoc} */
    @Override
    public Method getMethod() { return Method.parse(_request.getMethod()); }


    /** {@inheritDoc} */
    @Override
    public InetSocketAddress getClientAddress() {
        return _request.getClientAddress();
    }


    /** {@inheritDoc} */
    @Override
    public Version getVersion() { return _version; }


    /** {@inheritDoc} */
    @Override
    public boolean isConfidential() { return _request.isSecure(); }


    /** {@inheritDoc} */
    @Override
    public RequestURI getRequestUri() {
        return RequestURI.parse(_request.getTarget());
    }
}
