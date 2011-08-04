/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.simple;


import java.io.IOException;
import java.util.HashMap;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
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
        Container {

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
            Resource r =
                _dispatcher.dispatch(
                    new SimpleRequest(
                        request, response, new HashMap<String, String>(), "/"));
            new Engine().process(r, new SimpleResponse(response));
        } catch (HttpException e) {
            // TODO Auto-generated catch block.
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
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
}
