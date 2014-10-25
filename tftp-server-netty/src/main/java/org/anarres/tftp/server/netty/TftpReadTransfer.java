/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import com.google.common.io.ByteSource;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import java.net.SocketAddress;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.engine.AbstractTftpReadTransfer;
import org.anarres.tftp.protocol.packet.TftpPacket;

/**
 *
 * @author shevek
 */
public class TftpReadTransfer extends AbstractTftpReadTransfer<ChannelHandlerContext> {

    public TftpReadTransfer(
            @Nonnull SocketAddress remoteAddress,
            @Nonnull ByteSource source, int blockSize) throws IOException {
        super(remoteAddress, source, blockSize);
    }

    @Override
    public void send(ChannelHandlerContext ctx, TftpPacket packet) throws Exception {
        packet.setRemoteAddress(getRemoteAddress());
        ctx.write(packet, ctx.voidPromise());
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void close(ChannelHandlerContext ctx) throws Exception {
        ctx.close().addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}
