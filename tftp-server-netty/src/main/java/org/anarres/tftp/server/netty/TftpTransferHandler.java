/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.engine.TftpTransfer;
import org.anarres.tftp.protocol.packet.TftpPacket;

/**
 *
 * @author shevek
 */
public class TftpTransferHandler extends ChannelInboundHandlerAdapter {

    private final TftpTransfer<ChannelHandlerContext> transfer;

    public TftpTransferHandler(@Nonnull TftpTransfer<ChannelHandlerContext> transfer) throws IOException {
        this.transfer = transfer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        transfer.open(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            transfer.handle(ctx, (TftpPacket) msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        transfer.timeout(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        transfer.close(ctx);
        super.channelUnregistered(ctx);
    }
}