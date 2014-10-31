/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.engine;

import com.google.common.primitives.Chars;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.anarres.tftp.protocol.packet.TftpAckPacket;
import org.anarres.tftp.protocol.packet.TftpDataPacket;
import org.anarres.tftp.protocol.packet.TftpPacket;
import org.anarres.tftp.protocol.resource.TftpByteArrayData;
import org.anarres.tftp.protocol.resource.TftpData;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

/**
 *
 * @author shevek
 */
public class AbstractTftpReadTransferTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTftpReadTransferTest.class);

    private static class TftpReadTransfer extends AbstractTftpReadTransfer<List<TftpPacket>> {

        public TftpReadTransfer(TftpData source, int blockSize) throws IOException {
            super(new InetSocketAddress(1046), source, blockSize);
        }

        @Override
        public ByteBuffer allocate(List<TftpPacket> context, int length) {
            return ByteBuffer.allocate(length);
        }

        @Override
        public void send(List<TftpPacket> context, TftpPacket packet) throws Exception {
            LOG.info("-> " + packet);
            context.add(packet);

            packet.toWire(allocate(context, packet.getWireLength()));
        }

        @Override
        public void flush(List<TftpPacket> context) throws Exception {
        }

        @Override
        public void close(List<TftpPacket> context) throws Exception {
            LOG.info("-> (close)");
            context.add(null);
            super.close(context);
        }
    }

    private void testAck(TftpReadTransfer transfer, int ack, int first, int count) throws Exception {
        List<TftpPacket> packets = new ArrayList<TftpPacket>();
        TftpAckPacket ackPacket = new TftpAckPacket();
        ackPacket.setBlockNumber(Chars.checkedCast(ack + 1));
        LOG.info("<- " + ackPacket);
        transfer.handle(packets, ackPacket);
        assertEquals("Got wrong number of packets.", count, packets.size());
        if (count > 0) {
            if (first < 0)
                assertNull(packets.get(0));
            else
                assertEquals("Got wrong first packet", first, ((TftpDataPacket) packets.get(0)).getBlockNumber());
        }
    }

    @Test
    public void testReadTransfer() throws Exception {
        TftpByteArrayData data = new TftpByteArrayData(TftpServerTester.newRandomBytes(12367));
        TftpReadTransfer transfer = new TftpReadTransfer(data, 128);

        List<TftpPacket> packets = new ArrayList<TftpPacket>();
        transfer.open(packets);
        assertEquals(15, packets.size());   // We actually get window_size - 1

        // Ack packet 0
        testAck(transfer, 0, 16, 1);
        // Ack one packet
        testAck(transfer, 1, 17, 1);
        // Skip some acks
        testAck(transfer, 4, 18, 3);

        // Retry one
        testAck(transfer, 4, 6, 7);
        // Retry two
        testAck(transfer, 4, 6, 3);
        // Retry three
        transfer.timeout(packets);
        // Retry four
        testAck(transfer, 4, -1, 1);

        // Out of order ack
        testAck(transfer, 3, 0, 0);
    }
}