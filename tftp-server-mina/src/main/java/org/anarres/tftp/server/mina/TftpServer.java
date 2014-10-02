/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.mina;

import java.io.IOException;
import java.net.InetSocketAddress;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.anarres.tftp.protocol.engine.AbstractTftpServer;
import org.anarres.tftp.protocol.resource.TftpDataProvider;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

/**
 *
 * @author shevek
 */
public class TftpServer extends AbstractTftpServer {

    private NioDatagramAcceptor acceptor;

    public TftpServer(TftpDataProvider dataProvider, int port) {
        super(dataProvider, port);
    }

    @Override
    public void start() throws IOException {
        acceptor = new NioDatagramAcceptor();
        acceptor.setDefaultLocalAddress(new InetSocketAddress(getPort()));
        acceptor.getFilterChain().addLast("logger-data", new LoggingFilter("tftp-server-data"));
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TftpProtocolCodecFactory()));
        acceptor.getFilterChain().addLast("logger-packet", new LoggingFilter("tftp-server-packet"));
        acceptor.setHandler(new TftpServerProtocolHandler(getDataProvider()));
        DatagramSessionConfig dcfg = acceptor.getSessionConfig();
        dcfg.setReuseAddress(true);
        // dcfg.setIdleTime(IdleStatus.BOTH_IDLE, 5);
        acceptor.bind();
    }

    @Override
    public void stop() throws IOException {
        acceptor.dispose();
    }
}
