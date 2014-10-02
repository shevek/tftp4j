/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.engine.TftpConnection;
import org.anarres.tftp.protocol.engine.TftpReadTransfer;
import org.anarres.tftp.protocol.packet.TftpPacket;

/**
 *
 * @author shevek
 */
public class TftpTransferHandler extends ChannelDuplexHandler {

    private static class Connection implements TftpConnection {

        private final ChannelHandlerContext ctx;

        public Connection(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void send(TftpPacket packet) throws IOException {
            TftpServerHandler.send(ctx, null, packet);
        }

        public void flush() throws IOException {
            ctx.flush();
        }

        public void close() throws IOException {
            ctx.close();
        }
    }
    private final TftpReadTransfer transfer;

    public TftpTransferHandler(@Nonnull TftpReadTransfer transfer) {
        this.transfer = transfer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        transfer.open(new Connection(ctx));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DatagramPacket datagram = (DatagramPacket) msg;
        TftpPacket packet = TftpServerHandler.DECODER.decode(datagram.content().nioBuffer());
        transfer.handle(new Connection(ctx), packet);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        transfer.timeout(new Connection(ctx));
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        transfer.close();
        super.channelUnregistered(ctx);
    }
}
