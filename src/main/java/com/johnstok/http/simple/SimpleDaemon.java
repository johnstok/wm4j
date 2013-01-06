/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package com.johnstok.http.simple;


import java.io.IOException;
import java.net.InetSocketAddress;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import com.johnstok.http.HttpException;
import com.johnstok.http.engine.Daemon;
import com.johnstok.http.engine.Dispatcher;
import com.johnstok.http.engine.RESTfulHandler;


/**
 * Implementation of {@link Container} that leverages a wm4j {@link Dispatcher}.
 *
 * @author Keith Webster Johnston.
 */
public class SimpleDaemon
    implements
        Container, Daemon {

    private       Connection     _connection;
    private final RESTfulHandler _handler;
    private       int            _port;
    private       String         _host;


    /**
     * Constructor.
     *
     * @param handler
     */
    public SimpleDaemon(final RESTfulHandler handler) {
        _handler = handler;
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final Request request, final Response response) {
        try {
            final com.johnstok.http.sync.Response resp =
                new SimpleResponse(response);
            final com.johnstok.http.sync.Request req  =
                new SimpleRequest(request, _port, _host);

            _handler.handle(req, resp);

        } catch (final HttpException e) {
            e.printStackTrace(); // FIXME: WTF.
            throw new RuntimeException(e);
        } catch (final RuntimeException e) {
            e.printStackTrace(); // FIXME: WTF.
            throw e;
        } finally {
            try {
                response.close();
            } catch (final IOException e) {
                System.err.print("Error closing response:"+e);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup(final InetSocketAddress address) throws IOException {
        _port = address.getPort();
        _host = address.getHostName(); // What if we bound to the wildcard IP?
        if (null==_connection) {
            _connection = new SocketConnection(this);
            _connection.connect(address);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown() throws IOException {
        if (null!=_connection) {
            _connection.close();
        }
    }
}
