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
import java.util.HashMap;
import java.util.Map;
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
    private         String              _dispPath;
    private final   Map<String, String> _atomMatches;


    /**
     * Constructor.
     */
    public AbstractRequest(final Map<String, String> atomMatches,
                           final String dispPath) {
        _dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        _dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        _dispPath = dispPath;       // FIXME: Check for NULL.
        _atomMatches = atomMatches; // FIXME: Check for NULL.
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


    /** {@inheritDoc} */
    @Override
    public String[] path_tokens() {
        return _dispPath.split("/");                               //$NON-NLS-1$
    }


    /** {@inheritDoc} */
    @Override
    public Request set_disp_path(final String path) {
        _dispPath = path;
        return this;
    }


    /** {@inheritDoc} */
    @Override
    public String path_disp() {
        return _dispPath;
    }


    /** {@inheritDoc} */
    @Override
    public Map<Object, String> path_info() {
        return new HashMap<Object, String>(_atomMatches);
    }


    /** {@inheritDoc} */
    @Override
    public String path_info(final Object atom) {
        return _atomMatches.get(atom);
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
}
