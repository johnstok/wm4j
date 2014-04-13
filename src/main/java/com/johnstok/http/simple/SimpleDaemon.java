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
package com.johnstok.http.simple;


import java.io.IOException;
import java.net.InetSocketAddress;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import com.johnstok.http.engine.Dispatcher;
import com.johnstok.http.sync.Handler;
import com.johnstok.http.sync.Server;


/**
 * Implementation of {@link Container} that leverages a wm4j {@link Dispatcher}.
 *
 * @author Keith Webster Johnston.
 */
public class SimpleDaemon
    implements
        Container, Server {

    private       Connection        _connection;
    private final Handler           _handler;
    private       InetSocketAddress _address;


    /**
     * Constructor.
     *
     * @param handler
     */
    public SimpleDaemon(final Handler handler) {
        _handler = handler;
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final Request request, final Response response) {
        try {
            _handler.handle(
                new SimpleRequest(request, _address),
                new SimpleResponse(response));

        } catch (final IOException e) {
            e.printStackTrace(); // FIXME: WTF.

        } finally {
            try {
                response.close();
            } catch (final IOException e) {
                e.printStackTrace(); // FIXME: WTF.
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup(final InetSocketAddress address) throws IOException {
        // FIXME: Check address is not null.
        if (null==_connection) {
            _address = address;
            _connection = new SocketConnection(this);
            _connection.connect(address);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown() throws IOException {
        if (null!=_connection) {
            _connection.close();
            _connection = null;
            _address = null;
        }
    }
}
