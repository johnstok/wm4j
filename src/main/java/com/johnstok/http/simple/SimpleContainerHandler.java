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
package com.johnstok.http.simple;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import com.johnstok.http.sync.Handler;


/**
 * A Simple Web container that adapts to a standard handler.
 *
 * @author Keith Webster Johnston.
 */
public class SimpleContainerHandler
    implements
        Container {

    private final Handler _handler;


    /**
     * Constructor.
     *
     */
    public SimpleContainerHandler(final Handler handler) {
        _handler = handler; // TODO: Check for null.
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final Request request, final Response response) {
        _handler.handle(
            new SimpleRequest(
                request,
                request.getAddress().getPort(),
                request.getAddress().getDomain()), // FIXME: This can be NULL!
            new SimpleResponse(response));
    }

}
