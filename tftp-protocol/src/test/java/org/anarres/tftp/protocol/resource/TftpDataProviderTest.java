/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

/**
 *
 * @author shevek
 */
public class TftpDataProviderTest {

    private static final Logger LOG = LoggerFactory.getLogger(TftpDataProviderTest.class);

    public void run(@Nonnull TftpDataProvider provider) throws Exception {
        assertNotNull(provider);
        assertNull(provider.open("/nonexistent"));

        // Must exist.
        for (int i = 0; i < 10; i++) {
            TftpData data = provider.open("/foo");
            LOG.info("Opened " + data);
            ByteBuffer buf = ByteBuffer.allocate(1024);
            data.read(buf, 0);
            data.close();
        }

        // Not absolute.
        assertNull(provider.open("foo"));
    }

    @Test
    public void testResourceDataProvider() throws Exception {
        TftpResourceDataProvider provider = new TftpResourceDataProvider();
        run(provider);
    }

    @Test
    public void testFileChannelDataProvider() throws Exception {
        TftpFileChannelDataProvider provider = new TftpFileChannelDataProvider("src/test/resources/tftproot");
        run(provider);
    }

    @Test
    public void testFileCacheDataProvider() throws Exception {
        TftpFileCacheDataProvider provider = new TftpFileCacheDataProvider("src/test/resources/tftproot");
        run(provider);
    }

    @Test
    public void testFileMapDataProvider() throws Exception {
        TftpFileMapDataProvider provider = new TftpFileMapDataProvider("src/test/resources/tftproot");
        run(provider);
    }

    @Test
    public void testMemoryDataProvider() throws Exception {
        TftpMemoryDataProvider provider = new TftpMemoryDataProvider();
        provider.setData("/foo", "Hello, world.");
        run(provider);
    }
}