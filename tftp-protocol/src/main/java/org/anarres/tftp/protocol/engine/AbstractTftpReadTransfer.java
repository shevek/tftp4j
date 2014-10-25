/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.engine;

import com.google.common.io.ByteSource;
import org.anarres.tftp.protocol.packet.TftpDataPacket;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.util.Arrays;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.packet.TftpAckPacket;
import org.anarres.tftp.protocol.packet.TftpErrorCode;
import org.anarres.tftp.protocol.packet.TftpErrorPacket;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
public abstract class AbstractTftpReadTransfer<TftpTransferContext> extends AbstractTftpTransfer<TftpTransferContext> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTftpReadTransfer.class);
    public static final int MAX_WINDOW_SIZE = 16;
    public static final int MAX_RETRIES = 3;
    private final InputStream inputStream;
    private int blockNumber = 0;    // The first block number is 1.
    private final byte[] block;
    private int blockLength = 0;
    private int retryNumber = 0;
    private boolean closed = false;

    public AbstractTftpReadTransfer(@Nonnull SocketAddress remoteAddress, @Nonnull ByteSource source, @Nonnegative int blockSize) throws IOException {
        super(remoteAddress);
        this.inputStream = source.openBufferedStream();
        this.block = new byte[blockSize];
    }

    @Nonnegative
    private int getBlockNumber() {
        return blockNumber;
    }

    @Nonnull
    private TftpDataPacket currentPacket() {
        return new TftpDataPacket((char) blockNumber, Arrays.copyOf(block, blockLength));
    }

    @CheckForNull
    private TftpDataPacket nextPacket() throws IOException {
        if (closed)
            return null;
        blockLength = ByteStreams.read(inputStream, block, 0, block.length);
        // Note that if length is 0, we still construct a "final" zero-length packet.
        if (blockLength < block.length)
            closed = true;
        blockNumber++;
        if (blockNumber > Character.MAX_VALUE)
            throw new IllegalStateException("Block number too large: " + blockNumber);
        retryNumber = 0;
        return currentPacket();
    }

    private boolean nextRetry() {
        return retryNumber++ < MAX_RETRIES;
    }

    private void resendBlock(@Nonnull TftpTransferContext context) throws Exception {
        if (!nextRetry()) {
            LOG.error("Retries exceeded (" + AbstractTftpReadTransfer.MAX_RETRIES + ") on " + this);
            inputStream.close();
            close(context);
        } else {
            send(context, currentPacket());
        }
    }

    private void sendBlock(@Nonnull TftpTransferContext context) throws Exception {
        TftpDataPacket data = nextPacket();
        if (data == null)
            close(context);
        else
            send(context, data);
    }

    @Override
    public void open(@Nonnull TftpTransferContext context) throws Exception {
        sendBlock(context);
        flush(context);
    }

    @Override
    public void handle(@Nonnull TftpTransferContext context, @Nonnull TftpPacket packet) throws Exception {
        switch (packet.getOpcode()) {
            case ACK:
                TftpAckPacket ack = (TftpAckPacket) packet;
                if (ack.getBlockNumber() == getBlockNumber())
                    sendBlock(context);
                else
                    resendBlock(context);
                break;
            case RRQ:
            case WRQ:
            case DATA:
                LOG.warn("Unexpected TFTP " + packet.getOpcode() + " packet: " + packet);
                send(context, new TftpErrorPacket(packet.getRemoteAddress(), TftpErrorCode.ILLEGAL_OPERATION));
                close(context);
                break;
            case ERROR:
                TftpErrorPacket error = (TftpErrorPacket) packet;
                LOG.error("Received TFTP error packet: " + error);
                close(context);
                break;
        }
        flush(context);
    }

    @Override
    public void timeout(@Nonnull TftpTransferContext context) throws Exception {
        resendBlock(context);
    }
}