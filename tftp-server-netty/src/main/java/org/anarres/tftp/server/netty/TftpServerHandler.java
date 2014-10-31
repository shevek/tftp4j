/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import java.net.InetSocketAddress;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.engine.TftpTransfer;
import org.anarres.tftp.protocol.packet.TftpErrorCode;
import org.anarres.tftp.protocol.packet.TftpErrorPacket;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.anarres.tftp.protocol.packet.TftpRequestPacket;
import org.anarres.tftp.protocol.resource.TftpData;
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
            final TftpPacket packet = (TftpPacket) msg;
            Channel channel = ctx.channel();

            switch (packet.getOpcode()) {
                case RRQ: {
                    TftpRequestPacket request = (TftpRequestPacket) packet;
                    TftpData source = provider.open(request.getFilename());
                    if (source == null) {
                        ctx.writeAndFlush(new TftpErrorPacket(packet.getRemoteAddress(), TftpErrorCode.FILE_NOT_FOUND), ctx.voidPromise());
                        // ctx.writeAndFlush(new TftpErrorPacket(packet.getRemoteAddress(), TftpErrorCode.FILE_NOT_FOUND)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    } else {
                        TftpTransfer<Channel> transfer = new TftpReadTransfer(packet.getRemoteAddress(), source, request.getBlockSize());
                        Bootstrap bootstrap = new Bootstrap()
                                .group(ctx.channel().eventLoop())
                                .channel(channel.getClass())
                                // .localAddress(new InetSocketAddress(0))
                                // .remoteAddress(packet.getRemoteAddress())
                                .handler(new TftpPipelineInitializer(sharedHandlers, new TftpTransferHandler(transfer)));
                        bootstrap.connect(packet.getRemoteAddress());/*.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                LOG.info("Connected for " + packet);
                            }
                        });*/
                    }
                    break;
                }
                case WRQ: {
                    // LOG.warn("Unexpected TFTP " + packet.getOpcode() + " packet: " + packet);
                    ctx.writeAndFlush(new TftpErrorPacket(packet.getRemoteAddress(), TftpErrorCode.PERMISSION_DENIED), ctx.voidPromise());
                    break;
                }
                case ACK: {
                    break;
                }
                case DATA: {
                    LOG.warn("Unexpected TFTP " + packet.getOpcode() + " packet: " + packet);
                    ctx.writeAndFlush(new TftpErrorPacket(packet.getRemoteAddress(), TftpErrorCode.ILLEGAL_OPERATION), ctx.voidPromise());
                    break;
                }
                case ERROR: {
                    LOG.error("Received TFTP error packet: " + packet);
                    break;
                }
            }

        } catch (Exception e) {
            ctx.fireExceptionCaught(e);
            throw e;
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
        // LOG.error("Reported here: " + cause, new Exception("Here"));
    }
}
