/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import com.google.common.io.ByteSource;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.codec.TftpPacketDecoder;
import org.anarres.tftp.protocol.codec.TftpPacketEncoder;
import org.anarres.tftp.protocol.engine.TftpReadTransfer;
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
    /* pp */ static final TftpPacketEncoder ENCODER = new TftpPacketEncoder();
    /* pp */ static final TftpPacketDecoder DECODER = new TftpPacketDecoder();
    private final TftpDataProvider provider;

    public TftpServerHandler(@Nonnull TftpDataProvider provider) {
        this.provider = provider;
    }

    public static void send(@Nonnull ChannelHandlerContext ctx, @CheckForNull InetSocketAddress address, @Nonnull TftpPacket packet) {
        ByteBuffer buffer = ENCODER.encode(packet);
        DatagramPacket datagram = new DatagramPacket(Unpooled.wrappedBuffer(buffer), address);
        {
            SocketAddress remoteAddress = address;
            if (remoteAddress == null)
                remoteAddress = ctx.channel().remoteAddress();
            LOG.info(remoteAddress + " <- " + packet);
        }
        ctx.write(datagram);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DatagramPacket datagram = (DatagramPacket) msg;
        TftpPacket packet = DECODER.decode(datagram.content().nioBuffer());

        final InetSocketAddress address = datagram.sender();
        LOG.info(address + " -> " + packet);

        switch (packet.getOpcode()) {
            case RRQ: {
                TftpRequestPacket request = (TftpRequestPacket) packet;
                ByteSource source = provider.open(request.getFilename());
                if (source == null) {
                    send(ctx, address, new TftpErrorPacket(TftpErrorCode.FILE_NOT_FOUND));
                } else {
                    TftpReadTransfer transfer = new TftpReadTransfer(source, request.getBlockSize());
                    Bootstrap bootstrap = new Bootstrap()
                            .group(ctx.channel().eventLoop())
                            .channel(NioDatagramChannel.class)
                            .remoteAddress(address)
                            .handler(new TftpTransferHandler(transfer));
                    bootstrap.connect().addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture future) throws Exception {
                            LOG.info("Connected to " + address);
                        }
                    });
                }
                break;
            }
            case WRQ: {
                send(ctx, address, new TftpErrorPacket(TftpErrorCode.PERMISSION_DENIED));
                break;
            }
            case ACK: {
                break;
            }
            case DATA: {
                LOG.warn("Unexpected TFTP " + packet.getOpcode() + " packet: " + packet);
                send(ctx, address, new TftpErrorPacket(TftpErrorCode.ILLEGAL_OPERATION));
                break;
            }
            case ERROR: {
                LOG.error("Received TFTP error packet: " + packet);
                break;
            }
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
