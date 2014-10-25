/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.mina;

import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.anarres.tftp.protocol.engine.TftpTransfer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
// TODO: Re-code this to use a window of (say) 8 packets.
public class TftpTransferProtocolHandler extends IoHandlerAdapter implements IoFutureListener<ConnectFuture> {

    private static final Logger LOG = LoggerFactory.getLogger(TftpTransferProtocolHandler.class);
    private final IoService connector;
    private final TftpTransfer<IoSession> transfer;

    public TftpTransferProtocolHandler(@Nonnull IoService connector, @Nonnull TftpTransfer<IoSession> transfer) {
        this.connector = connector;
        this.transfer = transfer;
    }

    @Override
    public void operationComplete(ConnectFuture future) {
        if (!future.isConnected()) {
            connector.dispose(false);
            return;
        }
        final IoSession session = future.getSession();
        try {
            // This does "real" I/O, so can really throw an exception.
            transfer.open(session);
        } catch (Exception e) {
            session.close(true);
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        TftpPacket packet = (TftpPacket) message;
        transfer.handle(session, packet);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        transfer.timeout(session);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        LOG.error("Exception caught in session " + session + ": " + cause, cause);
        session.close(true);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        connector.dispose(false);
        transfer.close(session);
    }
}
