/*-----------------------------------------------------------------------------
 * Copyright © 2013 Keith Webster Johnston.
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
package com.johnstok.http.ext;

import java.util.Map;
import java.util.regex.Pattern;
import com.johnstok.http.Status;
import com.johnstok.http.sync.Handler;
import com.johnstok.http.sync.Request;
import com.johnstok.http.sync.Response;


/**
 * Performs simple dispatching based on a regular expression.
 *
 * @author Keith Webster Johnston.
 */
public class DispatchingHandler
    implements
        Handler {

    private final Map<Pattern, Handler> _handlers;


    public DispatchingHandler(final Map<Pattern, Handler> handlers) {
        _handlers = handlers; // FIXME: Check for NULL.
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final Request request, final Response response) {

        final String path = request.getRequestUri().toUri().getRawPath(); // FIXME: What if the request URI is an authority?!

        for (Pattern p : _handlers.keySet()) {
            if (p.matcher(path).matches()) {
                _handlers.get(p).handle(request, response);
                return;
            }
        }

        response.setStatus(Status.NOT_FOUND);
    }
}
