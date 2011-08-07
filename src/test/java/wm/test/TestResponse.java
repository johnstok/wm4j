/*-----------------------------------------------------------------------------
 * Copyright © 2011 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import wm.BodyWriter;
import wm.Response;
import wm.Status;


/**
 * Responsibility: in-memory representation of a HTTP response.
 *
 * @author Keith Webster Johnston.
 */
public class TestResponse
    implements
        Response {

    private Status                              _status;
    private final SimpleDateFormat              _dateFormatter;
    private final HashMap<String, List<String>> _headers =
        new HashMap<String, List<String>>();
    private byte[]                               _body;
    private final Date                           _originationTime = new Date();



    /**
     * Constructor.
     */
    public TestResponse() {
        _dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        _dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }


    /** {@inheritDoc} */
    @Override
    public Status getStatus() {
        return _status;
    }


    /** {@inheritDoc} */
    @Override
    public void setStatus(final Status status) {
        _status = status;
    }


    /** {@inheritDoc} */
    @Override
    public void setHeader(final String name, final String value) {
        final ArrayList<String> values = new ArrayList<String>();
        values.add(value);
        _headers.put(name, values);
    }


    @Override
    public String getHeader(final String name) {
        final List<String> values = _headers.get(name);
        return (null==values || 0==values.size()) ? null : values.get(0);
    }


    /** {@inheritDoc} */
    @Override
    public String getBodyAsString(final Charset charset) {
        return new String(_body, charset);
    }


    /** {@inheritDoc} */
    @Override
    public void write(final BodyWriter value) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        value.write(outputStream);
        _body = outputStream.toByteArray();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasBody() {
        return null!=_body;
    }


    public void setBody(final byte[] body) {
        _body=body;
    }


    /** {@inheritDoc} */
    @Override
    public Date getOriginationTime() {
        return _originationTime ;
    }


    /** {@inheritDoc} */
    @Override
    public void setHeader(final String name, final Date value) {
        setHeader(name, _dateFormatter.format(value));
    }
}
