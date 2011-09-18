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



/**
 * Provides default implementations of many {@link Request} API methods.
 *
 * @author Keith Webster Johnston.
 */
public abstract class AbstractRequest
    implements
        Request {


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
    public String getHeader(final String headerName) {
        return getHeader(headerName, null);
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
