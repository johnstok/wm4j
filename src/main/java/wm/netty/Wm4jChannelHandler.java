/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.netty;


import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;
import java.util.HashMap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
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
public class Wm4jChannelHandler
    extends
        SimpleChannelUpstreamHandler {

    private final Dispatcher _dispatcher;


    /**
     * Constructor.
     *
     * @param dispatcher
     */
    public Wm4jChannelHandler(final Dispatcher dispatcher) {
        _dispatcher = dispatcher;
    }


    /** {@inheritDoc} */
    @Override
    public void messageReceived(final ChannelHandlerContext ctx,
                                final MessageEvent me) throws Exception {
        try {
            HttpRequest request = (HttpRequest) me.getMessage();
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

            final wm.Response resp = new NettyResponse(response);
            final Resource r =
                _dispatcher.dispatch(
                    new NettyRequest(
                        request, me.getChannel(), new HashMap<String, String>(), "/"), resp);
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
            ctx.getChannel().close();
        }
    }
}
