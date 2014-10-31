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
    private ByteBuffer data;

    public TftpDataPacket() {
    }

    public TftpDataPacket(char blockNumber, @Nonnull ByteBuffer data) {
        this.blockNumber = blockNumber;
        this.data = data;
    }

    @Override
    public int getWireLength() {
        return data.remaining() + 256;
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

    @Nonnull
    public ByteBuffer getData() {
        return data;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }

    @Override
    public void toWire(ByteBuffer buffer) {
        super.toWire(buffer);
        buffer.putChar(getBlockNumber());
        buffer.put(getData().slice());
    }

    @Override
    public void fromWire(ByteBuffer buffer) {
        setBlockNumber(buffer.getChar());
        byte[] tmp = new byte[buffer.remaining()];
        buffer.get(tmp);
        setData(ByteBuffer.wrap(tmp));
    }

    @Override
    protected Objects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("blockNumber", (int) getBlockNumber())
                .add("data", data);
    }
}
