/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.simple;


import java.io.IOException;
import java.net.InetSocketAddress;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import wm.Daemon;
import wm.Dispatcher;
import wm.Engine;
import wm.HttpException;
import wm.Resource;


/**
 * Implementation of {@link Container} that leverages a wm4j {@link Dispatcher}.
 *
 * @author Keith Webster Johnston.
 */
public class SimpleDaemon
    implements
        Container, Daemon {

    private       Connection _connection;
    private final Dispatcher _dispatcher;
    private       int        _port;
    private       String     _host;


    /**
     * Constructor.
     *
     * @param dispatcher
     */
    public SimpleDaemon(final Dispatcher dispatcher) {
        _dispatcher = dispatcher;
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final Request request, final Response response) {
        try {
            final wm.Response resp = new SimpleResponse(response);
            final wm.Request  req  = new SimpleRequest(request, _port, _host);
            final Resource r = _dispatcher.dispatch(req, resp);

            new Engine().process(r, req, resp);

        } catch (final HttpException e) {
            // TODO Auto-generated catch block.
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (final RuntimeException e) {
            // TODO Auto-generated catch block.
            e.printStackTrace();
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
        _host = address.getHostName();
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
