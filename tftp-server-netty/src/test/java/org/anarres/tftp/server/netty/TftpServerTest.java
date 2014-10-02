/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.util.ResourceLeakDetector;
import org.anarres.tftp.protocol.engine.TftpServerTester;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author shevek
 */
public class TftpServerTest {

    @Before
    public void setUp() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
    }

    @Test
    public void testServer() throws Exception {
        TftpServerTester tester = new TftpServerTester();
        TftpServer server = new TftpServer(tester.getProvider(), tester.getPort());
        try {
            server.start();
            tester.run();
            // Thread.sleep(100000);
        } finally {
            server.stop();
        }
    }
}