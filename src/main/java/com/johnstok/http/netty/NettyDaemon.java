/*-----------------------------------------------------------------------------
 * Copyright © 2010 Keith Webster Johnston.
 * All rights reserved.
 *
 * Revision      $Rev$
 *---------------------------------------------------------------------------*/
package com.johnstok.http.netty;


import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
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
import com.johnstok.http.HttpException;
import com.johnstok.http.engine.Daemon;
import com.johnstok.http.engine.Dispatcher;
import com.johnstok.http.engine.RESTfulHandler;


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

    private final RESTfulHandler _handler;
    private final Charset        _charset;
    private       Channel        _c;


    /**
     * Constructor.
     *
     * @param handler
     */
    public NettyDaemon(final RESTfulHandler handler, final Charset charset) {
        _handler = handler;
        _charset = charset;
    }


    /** {@inheritDoc} */
    @Override
    public void messageReceived(final ChannelHandlerContext ctx,
                                final MessageEvent me) throws Exception {
        try {
            final HttpRequest request = (HttpRequest) me.getMessage();
            final HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
            final Channel channel = me.getChannel(); // FIXME: This can't be thread safe?

            final NettyResponse resp =
                new NettyResponse(response, channel);
            final NettyRequest req =
                new NettyRequest(request, channel, _charset);

            _handler.handle(req, resp);

            if (!resp.isCommitted()) { resp.commit(); }

        } catch (final HttpException e) {
            e.printStackTrace(); // FIXME: WTF.
            throw new RuntimeException(e);
        } catch (final RuntimeException e) {
            e.printStackTrace(); // FIXME: WTF.
            throw e;
        } finally {
            ctx.getChannel().close();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup(final InetSocketAddress address) {
        final ServerBootstrap bootstrap =
            new ServerBootstrap(
                new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override  public ChannelPipeline getPipeline() {
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
    public void shutdown() {
        _c.close().awaitUninterruptibly();
    }
}
