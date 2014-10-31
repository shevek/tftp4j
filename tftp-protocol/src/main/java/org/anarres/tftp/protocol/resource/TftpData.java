/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public interface TftpData extends Closeable {

    /**
     * Returns the data size of this object.
     */
    @Nonnegative
    public int getSize();

    /**
     * Reads data into the given {@link ByteBuffer}.
     * 
     * @return  The number of bytes read, possibly zero. Zero is returned if the
     *          given position is greater than or equal to the data size.
     * @see FileChannel#read(java.nio.ByteBuffer, long)
     */
    @Nonnegative
    public int read(@Nonnull ByteBuffer out, @Nonnegative int offset) throws IOException;
}
