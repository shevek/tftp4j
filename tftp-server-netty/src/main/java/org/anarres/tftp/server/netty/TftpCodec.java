/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.codec.TftpPacketDecoder;
import org.anarres.tftp.protocol.codec.TftpPacketEncoder;
import org.anarres.tftp.protocol.packet.TftpPacket;

/**
 *
 * @author shevek
 */
@ChannelHandler.Sharable
public class TftpCodec extends ChannelDuplexHandler {

    private final TftpPacketEncoder encoder = new TftpPacketEncoder();
    private final TftpPacketDecoder decoder = new TftpPacketDecoder();

    @Nonnull
    public TftpPacket decode(@Nonnull ChannelHandlerContext ctx, @Nonnull DatagramPacket packet) throws Exception {
        return decoder.decode(packet.sender(), packet.content().nioBuffer());
    }

    @Nonnull
    public DatagramPacket encode(@Nonnull ChannelHandlerContext ctx, @Nonnull TftpPacket packet) throws Exception {
        ByteBuffer buffer = encoder.encode(packet);
        return new DatagramPacket(Unpooled.wrappedBuffer(buffer), (InetSocketAddress) packet.getRemoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ctx.fireChannelRead(decode(ctx, (DatagramPacket) msg));
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            ctx.write(encode(ctx, (TftpPacket) msg), promise);
        } finally {
            // It isn't, but it might become so?
            ReferenceCountUtil.release(msg);
        }
    }
}