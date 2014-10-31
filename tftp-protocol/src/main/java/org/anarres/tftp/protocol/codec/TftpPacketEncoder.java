/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.codec;

import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.packet.TftpPacket;

/**
 * This is an example which uses {@link ByteBuffer#allocate(int)}.
 * 
 * You may wish to use your own allocator and call
 * {@link TftpPacket#toWire(java.nio.ByteBuffer)} yourself.
 *
 * @author shevek
 */
public class TftpPacketEncoder {

    @Nonnull
    public ByteBuffer encode(@Nonnull TftpPacket packet) {
        ByteBuffer buf = ByteBuffer.allocate(packet.getWireLength());
        packet.toWire(buf);
        buf.flip();
        return buf;
    }
}
