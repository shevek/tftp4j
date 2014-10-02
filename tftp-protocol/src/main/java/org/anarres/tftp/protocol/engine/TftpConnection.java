/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.engine;

import java.io.IOException;
import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.packet.TftpPacket;

/**
 *
 * @author shevek
 */
public interface TftpConnection {

    public void send(@Nonnull TftpPacket packet) throws IOException;

    public void flush() throws IOException;

    public void close() throws IOException;
}
