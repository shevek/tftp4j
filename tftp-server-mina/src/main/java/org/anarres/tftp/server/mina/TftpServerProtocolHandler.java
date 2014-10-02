/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.mina;

import com.google.common.io.ByteSource;
import java.net.SocketAddress;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.packet.TftpErrorCode;
import org.anarres.tftp.protocol.packet.TftpErrorPacket;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.anarres.tftp.protocol.packet.TftpRequestPacket;
import org.anarres.tftp.protocol.resource.TftpDataProvider;
import org.anarres.tftp.protocol.engine.TftpReadTransfer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
public class TftpServerProtocolHandler extends IoHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TftpServerProtocolHandler.class);
    public static final int SERVER_PORT = 69;
    private final TftpDataProvider provider;

    public TftpServerProtocolHandler(@Nonnull TftpDataProvider provider) {
        this.provider = provider;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        TftpPacket packet = (TftpPacket) message;

        SocketAddress address = session.getRemoteAddress();
        LOG.info("Address is " + address);

        switch (packet.getOpcode()) {
            case RRQ: {
                TftpRequestPacket request = (TftpRequestPacket) packet;
                ByteSource source = provider.open(request.getFilename());
                if (source == null) {
                    session.write(new TftpErrorPacket(TftpErrorCode.FILE_NOT_FOUND), address);
                    session.close(false);
                } else {
                    final NioDatagramConnector connector = new NioDatagramConnector();
                    TftpReadTransfer transfer = new TftpReadTransfer(source, request.getBlockSize());
                    TftpTransferProtocolHandler handler = new TftpTransferProtocolHandler(connector, transfer);
                    connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TftpProtocolCodecFactory()));
                    connector.getFilterChain().addLast("logger-packet", new LoggingFilter("tftp-transfer-packet"));
                    connector.setHandler(handler);
                    ConnectFuture future = connector.connect(address);
                    future.addListener(handler);
                }
                break;
            }
            case WRQ: {
                session.write(new TftpErrorPacket(TftpErrorCode.PERMISSION_DENIED), address);
                session.close(false);
                break;
            }
            case ACK: {
                break;
            }
            case DATA: {
                LOG.warn("Unexpected TFTP " + packet.getOpcode() + " packet: " + packet);
                session.write(new TftpErrorPacket(TftpErrorCode.ILLEGAL_OPERATION), address);
                session.close(false);
                break;
            }
            case ERROR: {
                TftpErrorPacket error = (TftpErrorPacket) packet;
                LOG.error("Received TFTP error packet: " + error);
                session.close(true);
                break;
            }
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        LOG.error("Exception caught in session " + session + ": " + cause, cause);
    }
}
