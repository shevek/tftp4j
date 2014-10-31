/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.engine;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.IntMath;
import com.google.common.primitives.Chars;
import org.anarres.tftp.protocol.packet.TftpDataPacket;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.GuardedBy;
import org.anarres.tftp.protocol.packet.TftpAckPacket;
import org.anarres.tftp.protocol.packet.TftpErrorCode;
import org.anarres.tftp.protocol.packet.TftpErrorPacket;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.anarres.tftp.protocol.resource.TftpData;
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
    private final TftpData source;
    private final int blockSize;
    private final int blockCount;
    // Runtime
    /** The next block to send, indexed from 0. */
    @GuardedBy("lock")
    private int sendBlock = 0;
    @GuardedBy("lock")
    private int sendWindow = MAX_WINDOW_SIZE;
    /** The last block acked, indexed from 0. */
    // We start at -2, then -1 on open, causing us to send 0.
    @GuardedBy("lock")
    private int recvBlock = -2;
    @GuardedBy("lock")
    private int recvRetry = 0;
    private final Object lock = new Object();

    public AbstractTftpReadTransfer(@Nonnull SocketAddress remoteAddress, @Nonnull TftpData source, @Nonnegative int blockSize) throws IOException {
        super(remoteAddress);
        this.source = source;
        this.blockSize = blockSize;
        this.blockCount = IntMath.divide(source.getSize() + 1, blockSize, RoundingMode.CEILING);
    }

    @Nonnull
    public abstract ByteBuffer allocate(@Nonnull TftpTransferContext context, @Nonnegative int length);

    /**
     * @param blockNumber indexed from 0
     */
    @Nonnull
    @GuardedBy("lock")
    private TftpDataPacket newPacket(@Nonnull TftpTransferContext context, int blockNumber) throws IOException {
        ByteBuffer buf = allocate(context, blockSize);
        // Note that if length is 0, we still construct a "final" zero-length packet.
        source.read(buf, blockNumber * blockSize);
        buf.flip();
        return new TftpDataPacket(Chars.checkedCast(blockNumber + 1), buf);
    }

    /**
     * @param ackBlock indexed from 0
     */
    @VisibleForTesting
    /* pp */ void ack(@Nonnull TftpTransferContext context, @Nonnegative int ackBlock) throws Exception {
        // if (LOG.isDebugEnabled()) LOG.debug("<- Ack protocol-block {} (index {})", (ackBlock + 1), ackBlock);

        synchronized (lock) {
            if (ackBlock < recvBlock) {
                LOG.warn("{}: Out of order ack {} < {} previously received", new Object[]{
                    this, ackBlock, recvBlock
                });
                // An ack got out of order?
            } else if (ackBlock == recvBlock) {
                // 1) The client is re-acking last known block (client timeout)
                if (recvRetry++ >= MAX_RETRIES) {
                    LOG.error("{}: Retries exceeded {} at packet {}", this, AbstractTftpReadTransfer.MAX_RETRIES, ackBlock + 1);
                    close(context);
                    return;
                } else {
                    LOG.warn("{}: Retry {} of packet {}", this, recvRetry, ackBlock + 1);
                    sendBlock = ackBlock + 1;
                    sendWindow = Math.max(1, sendWindow >> 1);
                }
            } else if (ackBlock == blockCount - 1) {
                // We are done.
                close(context);
                return;
            } else {
                // The first "open" ack goes through here.
                // Let's assume that clients don't ack forwards if they're missing a block.
                recvBlock = ackBlock;
                recvRetry = 0;
            }

            // Make sure we don't run off the end of the data.
            int sendLimit = Math.min(recvBlock + sendWindow, blockCount);
            while (sendBlock < sendLimit)
                send(context, newPacket(context, sendBlock++));
        }
    }

    @Override
    public void open(@Nonnull TftpTransferContext context) throws Exception {
        ack(context, -1);
        flush(context);
    }

    @Override
    public void handle(@Nonnull TftpTransferContext context, @Nonnull TftpPacket packet) throws Exception {
        switch (packet.getOpcode()) {
            case ACK:
                TftpAckPacket ack = (TftpAckPacket) packet;
                ack(context, ack.getBlockNumber() - 1);
                break;
            case RRQ:
            case WRQ:
            case DATA:
                LOG.warn("{}: Unexpected TFTP " + packet.getOpcode() + " packet: " + packet, this);
                send(context, new TftpErrorPacket(packet.getRemoteAddress(), TftpErrorCode.ILLEGAL_OPERATION));
                close(context);
                break;
            case ERROR:
                TftpErrorPacket error = (TftpErrorPacket) packet;
                LOG.error("{}: Received TFTP error packet: {}", this, error);
                close(context);
                break;
        }
        flush(context);
    }

    @Override
    public void timeout(@Nonnull TftpTransferContext context) throws Exception {
        ack(context, recvBlock);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void close(@Nonnull TftpTransferContext context) throws Exception {
        source.close();
    }
}