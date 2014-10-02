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
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.anarres.tftp.protocol.resource.TftpDataProvider;

/**
 *
 * @author shevek
 */
public class TftpServer {

    private final TftpDataProvider dataProvider;
    private final int port;
    private Channel channel;

    public TftpServer(TftpDataProvider dataProvider, int port) {
        this.dataProvider = dataProvider;
        this.port = port;
    }

    @PostConstruct
    public void start() throws IOException, InterruptedException {
        ThreadFactory factory = new DefaultThreadFactory("tftp-server");
        NioEventLoopGroup group = new NioEventLoopGroup(1, factory);

        Bootstrap b = new Bootstrap();
        b.group(group);
        b.channel(NioDatagramChannel.class);
        b.handler(new TftpServerHandler(dataProvider));
        channel = b.bind(port).sync().channel();
    }

    @PreDestroy
    public void stop() throws IOException, InterruptedException {
        EventLoop loop = channel.eventLoop();
        channel.close().sync();
        loop.shutdownGracefully();
    }
}
