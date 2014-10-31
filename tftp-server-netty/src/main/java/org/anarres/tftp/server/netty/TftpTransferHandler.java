/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.engine.TftpTransfer;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
public class TftpTransferHandler extends ChannelDuplexHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TftpTransferHandler.class);
    private final TftpTransfer<Channel> transfer;

    public TftpTransferHandler(@Nonnull TftpTransfer<Channel> transfer) throws IOException {
        this.transfer = transfer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        transfer.open(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            transfer.handle(ctx.channel(), (TftpPacket) msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        transfer.timeout(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        transfer.close(ctx.channel());
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("Error on channel: " + cause, cause);
        ctx.close();
    }
}