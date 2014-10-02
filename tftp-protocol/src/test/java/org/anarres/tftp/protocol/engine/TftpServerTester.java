/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.engine;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.resource.TftpMemoryDataProvider;
import org.apache.commons.net.tftp.TFTPClient;
import org.apache.commons.net.util.Base64;
import static org.junit.Assert.*;

/**
 *
 * @author shevek
 */
public class TftpServerTester {

    private final TftpMemoryDataProvider provider = new TftpMemoryDataProvider();
    private final TFTPClient client = new TFTPClient();
    private final Random random = new Random();

    @Nonnull
    public TftpMemoryDataProvider getProvider() {
        return provider;
    }

    @Nonnegative
    public int getPort() {
        return 1067;
    }

    private void assertSucceeds(String path, int mode) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        client.receiveFile(path, mode, out, InetAddress.getLoopbackAddress(), getPort());
        assertArrayEquals(provider.getData(path), out.toByteArray());
    }

    private void assertFails(String path, int mode) throws Exception {
        try {
            client.receiveFile(path, mode, ByteStreams.nullOutputStream(), InetAddress.getLoopbackAddress(), getPort());
            fail("No");
        } catch (IOException e) {
        }
    }

    @Nonnull
    private byte[] newRandomBytes(int length) {
        byte[] data = new byte[length];
        random.nextBytes(data);
        return data;
    }

    public void run() throws Exception {
        client.open();

        provider.setData("/hello", "Hello, world.");

        assertFails("/nonexistent", TFTPClient.BINARY_MODE);
        assertFails("/nonexistent", TFTPClient.ASCII_MODE);

        assertSucceeds("/hello", TFTPClient.BINARY_MODE);
        assertSucceeds("/hello", TFTPClient.ASCII_MODE);

        int[] sizes = new int[]{
            0,
            1,
            8,
            511,
            512,
            513,
            1 * 1024 - 1,
            1 * 1024,
            1 * 1024 + 1,
            4 * 1024 - 1,
            4 * 1024,
            4 * 1024 + 1,
            1024 * 1024 - 1,
            1024 * 1024,
            1024 * 1024 + 1
        };

        for (int size : sizes) {
            byte[] data = newRandomBytes(size);
            provider.setData("/binary", data);
            assertSucceeds("/binary", TFTPClient.BINARY_MODE);

            provider.setData("/ascii", Base64.encodeBase64(data));
            assertSucceeds("/ascii", TFTPClient.ASCII_MODE);
        }

        client.close();

    }
}
