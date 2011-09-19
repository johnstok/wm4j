package wm.simple;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wm.AbstractRequest;
import wm.Request;
import wm.Version;


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
     * @param port    The port upon which the request was received.
     * @param host    The hostname upon which the request was received.
     */
    public SimpleRequest(final org.simpleframework.http.Request request,
                         final int port,
                         final String host) {
        super(port, host, "UTF-8");
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
    public String getMethod() {
        return _request.getMethod();
    }


    /** {@inheritDoc} */
    @Override
    public InetSocketAddress getClientAddress() {
        return _request.getClientAddress();
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, List<String>> getQueryValues() {
        final Map<String, List<String>> params =
            new HashMap<String, List<String>>();
        for (final String key : _request.getQuery().keySet()) {
            params.put(
                key, new ArrayList<String>(_request.getQuery().getAll(key)));
        }
        return params;
    }


    /** {@inheritDoc} */
    @Override
    public String getQueryValue(final String paramName,
                                final String defaultValue) {
        final String value = _request.getQuery().get(paramName);
        return (null==value) ? defaultValue : value;
    }


    /** {@inheritDoc} */
    @Override
    public Version getVersion() {
        return _version;
    }


    /** {@inheritDoc} */
    @Override
    public String getRequestUri() { return _request.getTarget(); }


    /** {@inheritDoc} */
    @Override
    public boolean isConfidential() {
        return _request.isSecure();
    }
}
