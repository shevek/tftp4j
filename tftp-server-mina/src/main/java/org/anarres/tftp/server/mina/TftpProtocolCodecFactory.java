/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.mina;

import java.io.IOException;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.codec.TftpPacketDecoder;
import org.anarres.tftp.protocol.codec.TftpPacketEncoder;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 *
 * @author shevek
 */
public class TftpProtocolCodecFactory implements ProtocolCodecFactory {

    private static TftpProtocolCodecFactory INSTANCE = new TftpProtocolCodecFactory();

    /**
     * Returns the singleton instance of {@link TftpProtocolCodecFactory}.
     *
     * @return The singleton instance of {@link TftpProtocolCodecFactory}.
     */
    @Nonnull
    public static TftpProtocolCodecFactory getInstance() {
        return INSTANCE;
    }

    private static class TftpEncoder extends TftpPacketEncoder implements ProtocolEncoder {

        public static final TftpEncoder INSTANCE = new TftpEncoder();

        @Override
        public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws IOException {
            TftpPacket packet = (TftpPacket) message;
            IoBuffer buf = IoBuffer.wrap(super.encode(packet));
            out.write(buf);
        }

        @Override
        public void dispose(IoSession session) throws Exception {
        }
    }

    @Nonnull
    @Override
    public ProtocolEncoder getEncoder(@Nonnull IoSession session) {
        return TftpEncoder.INSTANCE;
    }

    private static class TftpDecoder extends TftpPacketDecoder implements ProtocolDecoder {

        public static final TftpDecoder INSTANCE = new TftpDecoder();

        @Override
        public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
            TftpPacket packet = super.decode(session.getRemoteAddress(), in.buf());
            out.write(packet);
        }

        @Override
        public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        }

        @Override
        public void dispose(IoSession session) throws Exception {
        }
    }

    @Nonnull
    @Override
    public ProtocolDecoder getDecoder(@Nonnull IoSession session) {
        return TftpDecoder.INSTANCE;
    }
}
