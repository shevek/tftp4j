/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import org.anarres.tftp.protocol.resource.TftpFileCacheDataProvider;
import org.junit.Test;

/**
 *
 * @author shevek
 */
public class TftpServerTest {

    @Test
    public void testServer() throws Exception {
        TftpServer server = new TftpServer(new TftpFileCacheDataProvider(), 1067);
        try {
            server.start();
            Thread.sleep(100000);
        } finally {
            server.stop();
        }
    }
}