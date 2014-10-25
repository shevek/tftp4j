/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.engine;

import javax.annotation.Nonnull;
import org.anarres.tftp.protocol.packet.TftpPacket;

/**
 *
 * @author shevek
 */
public interface TftpTransfer<TftpTransferContext> {

    /** Implemented by the transfer. */
    public void open(@Nonnull TftpTransferContext context) throws Exception;

    /** Implemented by the transfer. */
    public void handle(@Nonnull TftpTransferContext context, @Nonnull TftpPacket packet) throws Exception;

    /** Implemented by the transfer. */
    public void timeout(@Nonnull TftpTransferContext context) throws Exception;

    /** Implemented by the channel. */
    public void send(@Nonnull TftpTransferContext context, @Nonnull TftpPacket packet) throws Exception;

    /** Implemented by the channel. */
    public void flush(@Nonnull TftpTransferContext context) throws Exception;

    /** Implemented by the channel. */
    public void close(@Nonnull TftpTransferContext context) throws Exception;
}