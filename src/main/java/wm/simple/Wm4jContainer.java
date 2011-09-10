/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.simple;


import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
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
public class Wm4jContainer
    implements
        Container, Daemon {

    private       Connection _connection;
    private final Dispatcher _dispatcher;


    /**
     * Constructor.
     *
     * @param dispatcher
     */
    public Wm4jContainer(final Dispatcher dispatcher) {
        _dispatcher = dispatcher;
    }


    /** {@inheritDoc} */
    @Override
    public void handle(final Request request, final Response response) {
        try {
            final wm.Response resp = new SimpleResponse(response);
            final Resource r =
                _dispatcher.dispatch(
                    new SimpleRequest(
                        request, new HashMap<String, String>(), "/"), resp);
            new Engine().process(r, resp);
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
    public void startup(final SocketAddress address) throws IOException {
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
