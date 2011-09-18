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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Provides default implementations of many {@link Request} API methods.
 *
 * @author Keith Webster Johnston.
 */
public abstract class AbstractRequest
    implements
        Request {

    protected final SimpleDateFormat    _dateFormatter;


    /**
     * Constructor.
     */
    public AbstractRequest() {
        _dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        _dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    }


    /** {@inheritDoc} */
    @Override
    public Date getHeaderDate(final String headerName) {
        try {
            return _dateFormatter.parse(getHeader(headerName));
        } catch (final ParseException e) {
            return null;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean isValidDate(final String headerName) {
        try {
            _dateFormatter.parse(getHeader(headerName));
            return true;
        } catch (final ParseException e) {
            return false;
        }
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


    // FIXME: Move elsewhere.
    protected void copy(final InputStream is,
                        final OutputStream os) throws IOException {
        final byte[] buffer = new byte[8*1024];
        final int read = is.read(buffer);
        while (-1!=read) {
            os.write(buffer, 0, read);
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getPort() {
        return getUrl().getPort();
    }


    /** {@inheritDoc} */
    @Override
    public String getDomain() {
        return getUrl().getHost();
    }


    /** {@inheritDoc} */
    @Override
    public Scheme getScheme() {
        // TODO: Should throw 'Unsupported Scheme' exception.
        return Scheme.valueOf(getUrl().getProtocol());
    }


    /** {@inheritDoc} */
    @Override
    public String getFragment() {
        // FIXME: Decode the fragment.
        return getUrl().getRef();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isConfidential() {
        return getScheme().isConfidential();
    }
}
