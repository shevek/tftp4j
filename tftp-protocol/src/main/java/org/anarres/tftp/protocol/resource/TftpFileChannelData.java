/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.annotation.Nonnegative;

/**
 *
 * @author shevek
 */
public class TftpFileChannelData extends AbstractTftpData {

    private final FileChannel channel;
    private final int size;

    public TftpFileChannelData(@Nonnegative FileChannel channel, @Nonnegative int size) {
        this.channel = channel;
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int read(ByteBuffer out, int offset) throws IOException {
        int length = channel.read(out, offset);
        return Math.max(length, 0);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
