package wm.simple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.simpleframework.http.Address;
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


    /**
     * Constructor.
     *
     * @param request The Simple HTTP request delegated to.
     */
    public SimpleRequest(final org.simpleframework.http.Request request) {
        _request     = request;     // FIXME: Check for NULL.
    }


    /** {@inheritDoc} */
    @Override
    public byte[] getBody() throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        copy(_request.getInputStream(), os);
        return os.toByteArray();
    }


    /** {@inheritDoc} */
    @Override
    public InputStream getBodyAsStream() throws IOException {
        return _request.getInputStream();
    }


    /** {@inheritDoc} */
    @Override
    public String getHeader(final String headerName) {
        return _request.getValue(headerName);
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
    public InetAddress getClientAddress() {
        return _request.getClientAddress().getAddress();
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
        return new Version() {

            /** {@inheritDoc} */
            @Override public int major() { return _request.getMajor(); }


            /** {@inheritDoc} */
            @Override public int minor() { return _request.getMinor(); }
        };
    }


    /** {@inheritDoc} */
    @Override
    public URI getPath() {
        try {
            return new URI(_request.getPath().getPath());
        } catch (final URISyntaxException e) {
            // FIXME: Is there a better solution here?
            throw new RuntimeException(e);
        }
    }


    /** {@inheritDoc} */
    @Override
    public URL getUrl() {
        final Address address = _request.getAddress();
        try {
            // FIXME: Doesn't include the query String.
            // FIXME: Does this correctly handle Mismatch between Absolute request URI & Host header?
            // FIXME: Should not normalise the path.
            return new URL(address.getScheme(), address.getDomain(), address.getPort(), address.getPath().getPath());
        } catch (final MalformedURLException e) {
            // FIXME: Is there a better solution here?
            throw new RuntimeException(e);
        }
    }
}
