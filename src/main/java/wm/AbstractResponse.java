/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * Provides default implementations of many {@link Response} API methods.
 *
 * @author Keith Webster Johnston.
 */
public abstract class AbstractResponse
    implements
        Response {

    protected final SimpleDateFormat _dateFormatter;

    private final Date         _originationTime = new Date();
    private final List<String> _variances = new ArrayList<String>();
    private Charset            _charset;
    private MediaType          _mediaType;
    private String             _contentEncoding;


    /**
     * Constructor.
     */
    public AbstractResponse() {
        _dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        _dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }


    /** {@inheritDoc} */
    @Override
    public Date getOriginationTime() {
        return _originationTime; // TODO: Make defensive copy?
    }


    /** {@inheritDoc} */
    @Override
    public void setCharset(final Charset charset) {
        _charset = charset;
        setContentType();
    }


    private void setContentType() {
        // FIXME: Handle setting charset when no media type is set.
        setHeader(
            Header.CONTENT_TYPE,
            _mediaType+((null==_charset) ? "" : "; charset="+_charset));
    }


    /** {@inheritDoc} */
    @Override
    public void setMediaType(final MediaType mediaType) {
        _mediaType = mediaType;
        setContentType();
    }


    /** {@inheritDoc} */
    @Override
    public MediaType getMediaType() {
        return _mediaType;
    }


    /** {@inheritDoc} */
    @Override
    public void setContentEncoding(final String encoding) {
        if (ContentEncoding.IDENTITY.equals(encoding)) {
            // Don't send a header for 'identity'.
            _contentEncoding = null;
            setHeader(Header.CONTENT_ENCODING, (String) null);
        } else {
            _contentEncoding = encoding;
            setHeader(Header.CONTENT_ENCODING, encoding);
        }
    }


    /** {@inheritDoc} */
    @Override
    public String[] getVariances() {
        return _variances.toArray(new String[_variances.size()]);
    }


    /** {@inheritDoc} */
    @Override
    public void addVariance(final String headerName) {
        _variances.add(headerName);
    }
}
