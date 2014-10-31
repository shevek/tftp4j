/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class TftpByteBufferData extends AbstractTftpData {

    private final ByteBuffer data;

    public TftpByteBufferData(@Nonnull ByteBuffer data) {
        this.data = data;
    }

    @Override
    public int getSize() {
        return data.remaining();
    }

    @Override
    public int read(ByteBuffer out, int offset) throws IOException {
        Preconditions.checkPositionIndex(offset, getSize(), "Illegal data offset.");
        int length = Math.min(getSize() - offset, out.remaining());
        ByteBuffer slice = data.slice();    // We might not be reading from a full buffer.
        slice.position(offset).limit(offset + length);
        out.put(slice);
        return length;
    }
}