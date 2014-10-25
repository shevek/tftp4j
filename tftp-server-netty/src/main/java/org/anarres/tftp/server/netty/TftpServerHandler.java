/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import com.google.common.io.ByteSource;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.ReferenceCountUtil;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.engine.TftpTransfer;
import org.anarres.tftp.protocol.packet.TftpErrorCode;
import org.anarres.tftp.protocol.packet.TftpErrorPacket;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.anarres.tftp.protocol.packet.TftpRequestPacket;
import org.anarres.tftp.protocol.resource.TftpDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
public class TftpServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TftpServerHandler.class);
    private final TftpPipelineInitializer.SharedHandlers sharedHandlers;
    private final TftpDataProvider provider;

    public TftpServerHandler(@Nonnull TftpPipelineInitializer.SharedHandlers sharedHandlers, @Nonnull TftpDataProvider provider) {
        this.sharedHandlers = sharedHandlers;
        this.provider = provider;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            TftpPacket packet = (TftpPacket) msg;

            switch (packet.getOpcode()) {
                case RRQ: {
                    TftpRequestPacket request = (TftpRequestPacket) packet;
                    ByteSource source = provider.open(request.getFilename());
                    if (source == null) {
                        ctx.write(new TftpErrorPacket(packet.getRemoteAddress(), TftpErrorCode.FILE_NOT_FOUND), ctx.voidPromise());
                    } else {
                        TftpTransfer<ChannelHandlerContext> transfer = new TftpReadTransfer(packet.getRemoteAddress(), source, request.getBlockSize());
                        Bootstrap bootstrap = new Bootstrap()
                                .group(ctx.channel().eventLoop())
                                .channel(NioDatagramChannel.class)
                                .remoteAddress(packet.getRemoteAddress())
                                .handler(new TftpPipelineInitializer(sharedHandlers, new TftpTransferHandler(transfer)));
                        bootstrap.connect().addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                    }
                    break;
                }
                case WRQ: {
                    ctx.write(new TftpErrorPacket(packet.getRemoteAddress(), TftpErrorCode.PERMISSION_DENIED), ctx.voidPromise());
                    break;
                }
                case ACK: {
                    break;
                }
                case DATA: {
                    LOG.warn("Unexpected TFTP " + packet.getOpcode() + " packet: " + packet);
                    ctx.write(new TftpErrorPacket(packet.getRemoteAddress(), TftpErrorCode.ILLEGAL_OPERATION), ctx.voidPromise());
                    break;
                }
                case ERROR: {
                    LOG.error("Received TFTP error packet: " + packet);
                    break;
                }
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("Error on channel: " + cause, cause);
    }
}
