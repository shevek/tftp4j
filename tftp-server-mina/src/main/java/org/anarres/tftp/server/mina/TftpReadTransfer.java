/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.mina;

import com.google.common.io.ByteSource;
import java.io.IOException;
import java.net.SocketAddress;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.engine.AbstractTftpReadTransfer;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author shevek
 */
public class TftpReadTransfer extends AbstractTftpReadTransfer<IoSession> {

    public TftpReadTransfer(@Nonnull SocketAddress remoteAddress, @Nonnull ByteSource source, int blockSize) throws IOException {
        super(remoteAddress, source, blockSize);
    }

    @Override
    public void send(IoSession session, TftpPacket packet) throws Exception {
        session.write(packet, getRemoteAddress());
    }

    @Override
    public void flush(IoSession session) throws Exception {
    }

    @Override
    public void close(IoSession session) throws Exception {
        session.close(false);
    }
}
