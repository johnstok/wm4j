/*-----------------------------------------------------------------------------
 * Copyright © 2013 Keith Webster Johnston.
 * All rights reserved.
 *
 * This file is part of wm4j.
 *
 * wm4j is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * wm4j is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with wm4j. If not, see <http://www.gnu.org/licenses/>.
 *---------------------------------------------------------------------------*/
package com.johnstok.http.servlet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.johnstok.http.sync.Handler;


/**
 * A Servlet that adapts to a standard handler.
 *
 * @author Keith Webster Johnston.
 */
public abstract class JEEHandler
    extends
        HttpServlet {

    private final Handler _handler;


    /**
     * Constructor.
     */
    public JEEHandler(final Handler handler) {
        _handler = handler; // FIXME: Check not null.
    }


    /** {@inheritDoc} */
    @Override
    protected void service(final HttpServletRequest request,
                           final HttpServletResponse resp) {
        final InetSocketAddress isa =
            new InetSocketAddress(
                request.getLocalName(),  // TODO: What about the IP address?!
                request.getLocalPort());

        try {
            _handler.handle(
                new JEERequest(
                    isa,
                    Charset.forName("UTF-8"), // TODO: This should be configurable.
                    request),
                new JEEResponse(resp));

        } catch (final IOException e) {
            e.printStackTrace(); // FIXME: WTF.

        } finally {
            try {
                resp.getOutputStream().close();
            } catch (final IOException e) {
                e.printStackTrace(); // FIXME: WTF.
            }
        }
    }
}
