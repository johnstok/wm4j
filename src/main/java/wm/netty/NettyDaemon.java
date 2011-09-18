/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package wm.netty;


import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.simpleframework.http.core.Container;
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
public class NettyDaemon
    extends
        SimpleChannelUpstreamHandler
    implements
        Daemon {

    private final Dispatcher _dispatcher;
    private Channel _c;


    /**
     * Constructor.
     *
     * @param dispatcher
     */
    public NettyDaemon(final Dispatcher dispatcher) {
        _dispatcher = dispatcher;
    }


    /** {@inheritDoc} */
    @Override
    public void messageReceived(final ChannelHandlerContext ctx,
                                final MessageEvent me) throws Exception {
        try {
            final HttpRequest request = (HttpRequest) me.getMessage();
            final HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
            final Channel channel = me.getChannel();

            final NettyResponse resp = new NettyResponse(response, channel);
            final Resource r =
                _dispatcher.dispatch(
                    new NettyRequest(request, channel), resp);
            new Engine().process(r, resp);
            if (!resp.isCommitted()) {
                resp.commit();
            }

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


    /** {@inheritDoc} */
    @Override
    public void startup(final SocketAddress address) throws IOException {
        final ServerBootstrap bootstrap =
            new ServerBootstrap(
                new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            @Override  public ChannelPipeline getPipeline() throws Exception {
                final ChannelPipeline pipeline = new DefaultChannelPipeline();
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
                pipeline.addLast("handler", NettyDaemon.this);
                return pipeline;
            }});
        _c = bootstrap.bind(address);
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown() throws IOException {
        _c.close().awaitUninterruptibly();
    }
}
