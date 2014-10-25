/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.io.IOException;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.engine.AbstractTftpServer;
import org.anarres.tftp.protocol.resource.TftpDataProvider;

/**
 *
 * @author shevek
 */
public class TftpServer extends AbstractTftpServer {

    private Channel channel;
    private final TftpPipelineInitializer.SharedHandlers sharedHandlers = new TftpPipelineInitializer.SharedHandlers();

    public TftpServer(@Nonnull TftpDataProvider dataProvider, @Nonnegative int port) {
        super(dataProvider, port);
    }

    public void setDebug(boolean debug) {
        sharedHandlers.setDebug(debug);
    }

    @Override
    public void start() throws IOException, InterruptedException {
        ThreadFactory factory = new DefaultThreadFactory("tftp-server");
        NioEventLoopGroup group = new NioEventLoopGroup(1, factory);

        Bootstrap b = new Bootstrap();
        b.group(group);
        b.channel(NioDatagramChannel.class);
        b.handler(new TftpPipelineInitializer(sharedHandlers, new TftpServerHandler(sharedHandlers, getDataProvider())));
        channel = b.bind(getPort()).sync().channel();
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        EventLoop loop = channel.eventLoop();
        channel.close().sync();
        loop.shutdownGracefully();
    }
}
