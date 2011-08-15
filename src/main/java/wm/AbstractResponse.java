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

import java.nio.charset.Charset;
import java.util.Date;


/**
 * TODO: Add a description for this type.
 *
 * @author Keith Webster Johnston.
 */
public abstract class AbstractResponse
    implements
        Response {

    private final Date _originationTime = new Date();
    private Charset _charset;
    private MediaType _mediaType;


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
}
