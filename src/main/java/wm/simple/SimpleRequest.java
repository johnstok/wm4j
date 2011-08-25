package wm.simple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
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
    private final Map<String, String>              _atomMatches;
    private       String                           _dispPath;


    /**
     * Constructor.
     *
     * @param request     The Simple HTTP request delegated to.
     * @param response    The Simple HTTP response delegated to.
     * @param atomMatches
     * @param dispPath
     */
    public SimpleRequest(final org.simpleframework.http.Request request,
                         final Map<String, String> atomMatches,
                         final String dispPath) {
        _request     = request;     // FIXME: Check for NULL.
        _atomMatches = atomMatches; // FIXME: Check for NULL.
        _dispPath    = dispPath;    // FIXME: Check for NULL.
    }


    @Override
    public byte[] getBody() throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        copy(_request.getInputStream(), os);
        return os.toByteArray();
    }


    @Override
    public InputStream getBodyAsStream() throws IOException {
        return _request.getInputStream();
    }


    @Override
    public String getHeader(final String headerName) {
        return _request.getValue(headerName);
    }


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


    @Override
    public InetAddress getAddress() {
        return _request.getClientAddress().getAddress();
    }


    @Override
    public Map<String, List<String>> getQueryValues() {
        final Map<String, List<String>> params = new HashMap<String, List<String>>();
        for (final String key : _request.getQuery().keySet()) {
            params.put(
                key, new ArrayList<String>(_request.getQuery().getAll(key)));
        }
        return params;
    }


    @Override
    public String getQueryValue(final String paramName) {
        return getQueryValue(paramName, null);
    }


    @Override
    public String getQueryValue(final String paramName, final String defaultValue) {
        final String value = _request.getQuery().get(paramName);
        return (null==value) ? defaultValue : value;
    }


    @Override
    public Version getVersion() {
        return new Version() {

            /** {@inheritDoc} */
            @Override public int major() { return _request.getMajor(); }


            /** {@inheritDoc} */
            @Override public int minor() { return _request.getMinor(); }
        };
    }


    @Override
    public String path() {
        return _request.getPath().getPath();
    }


    @Override
    public String path_app_root() {
        throw new UnsupportedOperationException("Method not implemented.");
    }


    @Override
    public String path_disp() {
        return _dispPath;
    }


    @Override
    public Map<Object, String> path_info() {
        return new HashMap<Object, String>(_atomMatches);
    }


    @Override
    public String path_info(final Object atom) {
        return _atomMatches.get(atom);
    }


    @Override
    public String path_raw() {
        return _request.getTarget();
    }


    @Override
    public String[] path_tokens() {
        return _dispPath.split("/");                               //$NON-NLS-1$
    }


    @Override
    public Request set_disp_path(final String path) {
        _dispPath = path;
        return this;
    }


    @Override
    public Request setBody(final byte[] bytes) {
        throw new UnsupportedOperationException("Request body is immutable.");
    }
}
