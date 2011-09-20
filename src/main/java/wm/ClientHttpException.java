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
 * A client HTTP error in the 4xx range.
 *
 * @author Keith Webster Johnston.
 */
public class ClientHttpException extends HttpException {

    /**
     * Constructor.
     *
     * @param status The HTTP status for this error.
     * @param t      The cause of this exception.
     */
    public ClientHttpException(final Status status, final Throwable t) {
        super(status, t);
    }

    /**
     * Constructor.
     *
     * @param status The HTTP status for this error.
     */
    public ClientHttpException(final Status status) {
        super(status);
    }
}
