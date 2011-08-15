package wm.simple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.simpleframework.http.Response;
import wm.Header;
import wm.Request;
import wm.Version;


/**
 * Implementation of the {@link Request} API using the Simple HTTP library.
 *
 * @author Keith Webster Johnston.
 */
public class SimpleRequest
    implements
        Request {

    private final org.simpleframework.http.Request _request;
    private final Response                         _response;
    private       boolean                          _doRedirect = false;
    private final Map<String, String>              _atomMatches;
    private       String                           _dispPath;
    private final SimpleDateFormat                 _dateFormatter;


    /**
     * Constructor.
     *
     * @param request     The Simple HTTP request delegated to.
     * @param response    The Simple HTTP response delegated to.
     * @param atomMatches
     * @param dispPath
     */
    public SimpleRequest(final org.simpleframework.http.Request request,
                         final Response response,
                         final Map<String, String> atomMatches,
                         final String dispPath) {
        _request     = request;     // FIXME: Check for NULL.
        _response    = response;    // FIXME: Check for NULL.
        _atomMatches = atomMatches; // FIXME: Check for NULL.
        _dispPath    = dispPath;    // FIXME: Check for NULL.
        _dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        _dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }


    @Override
    public Request append_to_response_body(final byte[] bytes) throws IOException {
        _response.getOutputStream().write(bytes);
        return this;
    }


    private void copy(final InputStream is,
                      final OutputStream os) throws IOException {
        final byte[] buffer = new byte[8*1024];
        final int read = is.read(buffer);
        while (-1!=read) {
            os.write(buffer, 0, read);
        }
    }


    @Override
    public byte[] get_req_body() throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        copy(_request.getInputStream(), os);
        return os.toByteArray();
    }


    @Override
    public InputStream get_req_body_stream() throws IOException {
        return _request.getInputStream();
    }


    @Override
    public String get_req_cookie() {
        return _request.getValue(Header.COOKIE);
    }


    @Override
    public String get_req_cookie_value(final String valueName) {
        return _request.getCookie(valueName).getValue();
    }


    @Override
    public String get_req_header(final String headerName) {
        return _request.getValue(headerName);
    }


    @Override
    public Map<String, List<String>> get_req_headers() {
        final Map<String, List<String>> headers = new HashMap<String, List<String>>();
        for (final String name : _request.getNames()) {
            headers.put(name, _request.getValues(name));
        }
        return headers;
    }


    /** {@inheritDoc} */
    @Override
    public String get_req_method() {
        return _request.getMethod();
    }


    @Override
    public InetAddress get_req_peer() {
        return _request.getClientAddress().getAddress();
    }


    @Override
    public Map<String, List<String>> get_req_qs() {
        final Map<String, List<String>> params = new HashMap<String, List<String>>();
        for (final String key : _request.getQuery().keySet()) {
            params.put(
                key, new ArrayList<String>(_request.getQuery().getAll(key)));
        }
        return params;
    }


    @Override
    public String get_req_qs_value(final String paramName) {
        return get_req_qs_value(paramName, null);
    }


    @Override
    public String get_req_qs_value(final String paramName, final String defaultValue) {
        final String value = _request.getQuery().get(paramName);
        return (null==value) ? defaultValue : value;
    }


    @Override
    public Version get_req_version() {
        return new Version() {

            /** {@inheritDoc} */
            @Override public int major() { return _request.getMajor(); }


            /** {@inheritDoc} */
            @Override public int minor() { return _request.getMinor(); }
        };
    }


    @Override
    public byte[] get_resp_body() {
        throw new UnsupportedOperationException("Response body can't be read.");
    }


    @Override
    public String get_resp_header(final String headerName) {
        return _response.getValue(headerName);
    }


    @Override
    public Map<String, List<String>> get_resp_headers() {
        final Map<String, List<String>> headers = new HashMap<String, List<String>>();
        for (final String name : _response.getNames()) {
            headers.put(name, _response.getValues(name));
        }
        return headers;
    }


    @Override
    public boolean get_resp_redirect() {
        return _doRedirect;
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
    public Request remove_resp_header(final String headerName) {
        _response.remove(headerName);
        return this;
    }


    @Override
    public Request set_disp_path(final String path) {
        _dispPath = path;
        return this;
    }


    @Override
    public Request set_req_body(final byte[] bytes) {
        throw new UnsupportedOperationException("Request body is immutable.");
    }


    @Override
    public Request set_resp_body(final byte[] bytes) throws IOException {
        _response.reset(); // Should only reset the body.
        _response.getOutputStream().write(bytes);
        return this;
    }


    @Override
    public Request set_resp_body(final InputStream stream) throws IOException {
        copy(stream, _response.getOutputStream());
        return this;
    }


    @Override
    public Request set_resp_header(final String headerName, final String headerValue) {
        _response.set(headerName, headerValue);
        return this;
    }


    @Override
    public Request set_resp_headers(final Map<String, String[]> headers) {
        for (final Map.Entry<String, String[]> header : headers.entrySet()) {
            remove_resp_header(header.getKey());
            for (final String value : header.getValue()) {
                _response.add(header.getKey(), value);
            }
        }
        return this;
    }


    @Override
    public Request set_resp_redirect(final boolean redirect) {
        _doRedirect = redirect;
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public Date get_req_header_date(final String headerName) {
        try {
            return _dateFormatter.parse(get_req_header(headerName));
        } catch (final ParseException e) {
            return null;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean isValidDate(final String headerName) {
        try {
            _dateFormatter.parse(get_req_header(headerName));
            return true;
        } catch (final ParseException e) {
            return false;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasHeader(final String headerName) {
        return null!=get_req_header(headerName);
    }
}
