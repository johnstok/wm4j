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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private Status _status;
    private final HashMap<String, List<String>> _headers =
        new HashMap<String, List<String>>();
    private byte[] _body;


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
}
