/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.codec;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.packet.TftpAckPacket;
import org.anarres.tftp.protocol.packet.TftpDataPacket;
import org.anarres.tftp.protocol.packet.TftpErrorPacket;
import org.anarres.tftp.protocol.packet.TftpOpcode;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.anarres.tftp.protocol.packet.TftpReadRequestPacket;
import org.anarres.tftp.protocol.packet.TftpWriteRequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
public class TftpPacketDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(TftpPacketDecoder.class);

    @Nonnull
    public TftpPacket decode(@Nonnull SocketAddress remoteAddress, @Nonnull ByteBuffer buf) {
        TftpOpcode opcode = TftpOpcode.forCode(buf.getShort());
        TftpPacket packet;
        switch (opcode) {
            case RRQ:
                packet = new TftpReadRequestPacket();
                break;
            case WRQ:
                packet = new TftpWriteRequestPacket();
                break;
            case DATA:
                packet = new TftpDataPacket();
                break;
            case ACK:
                packet = new TftpAckPacket();
                break;
            case ERROR:
                packet = new TftpErrorPacket();
                break;
            case ACK_WITH_OPTIONS:
            default:
                throw new IllegalStateException("Unknown TftpOpcode in decoder: " + opcode);
        }
        packet.setRemoteAddress(remoteAddress);
        packet.fromWire(buf);
        if (buf.position() < buf.limit()) {
            LOG.warn("Discarded " + (buf.limit() - buf.position()) + " trailing bytes in TFTP packet: " + buf);
            buf.position(buf.limit());
        }
        return packet;
    }
}
