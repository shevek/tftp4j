/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.packet;

import com.google.common.base.Objects;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author shevek
 */
public class TftpAckPacket extends TftpPacket {

    /** Cheat for an unsigned 2-byte value. */
    private char blockNumber;

    @Override
    public TftpOpcode getOpcode() {
        return TftpOpcode.ACK;
    }

    public void setBlockNumber(char blockNumber) {
        this.blockNumber = blockNumber;
    }

    public char getBlockNumber() {
        return blockNumber;
    }

    @Override
    public void toWire(ByteBuffer buffer) throws IOException {
        super.toWire(buffer);
        buffer.putChar(getBlockNumber());
    }

    @Override
    public void fromWire(ByteBuffer buffer) throws IOException {
        setBlockNumber(buffer.getChar());
    }

    @Override
    protected Objects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("blockNumber", (int) getBlockNumber());
    }
}
