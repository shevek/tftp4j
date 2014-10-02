/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.packet;

import com.google.common.base.Objects;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class TftpDataPacket extends TftpPacket {

    public static final int BLOCK_SIZE = 512;
    /** Cheat for an unsigned 2-byte value. */
    private char blockNumber;
    private byte[] data;

    public TftpDataPacket() {
    }

    public TftpDataPacket(char blockNumber, @Nonnull byte[] data) {
        this.blockNumber = blockNumber;
        this.data = data;
    }

    @Override
    public int getWireLength() {
        return data.length + 256;
    }

    @Override
    public TftpOpcode getOpcode() {
        return TftpOpcode.DATA;
    }

    public void setBlockNumber(char blockNumber) {
        this.blockNumber = blockNumber;
    }

    public char getBlockNumber() {
        return blockNumber;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public void toWire(ByteBuffer buffer) {
        super.toWire(buffer);
        buffer.putChar(getBlockNumber());
        buffer.put(getData());
    }

    @Override
    public void fromWire(ByteBuffer buffer) {
        setBlockNumber(buffer.getChar());
        byte[] tmp = new byte[buffer.remaining()];
        buffer.get(tmp);
        setData(tmp);
    }

    @Override
    protected Objects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("blockNumber", (int) getBlockNumber()).add("data", data).add("length", (data != null) ? data.length : 0);
    }
}
