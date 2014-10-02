/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.codec;

import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.packet.TftpPacket;

/**
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
