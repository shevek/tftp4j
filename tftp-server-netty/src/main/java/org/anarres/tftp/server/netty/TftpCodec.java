/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.nio.ByteBuffer;
import java.util.List;
import org.anarres.tftp.protocol.codec.TftpPacketDecoder;
import org.anarres.tftp.protocol.codec.TftpPacketEncoder;
import org.anarres.tftp.protocol.packet.TftpPacket;

/**
 *
 * @author shevek
 */
// I'm not sure this is actually useful.
public class TftpCodec extends ByteToMessageCodec<TftpPacket> {

    private final TftpPacketEncoder encoder = new TftpPacketEncoder();
    private final TftpPacketDecoder decoder = new TftpPacketDecoder();

    @Override
    protected void encode(ChannelHandlerContext ctx, TftpPacket msg, ByteBuf out) throws Exception {
        // Well, this sucks.
        ByteBuffer buffer = encoder.encode(msg);
        out.writeBytes(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        out.add(decoder.decode(in.nioBuffer()));
    }
}
