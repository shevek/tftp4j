/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.engine;

import java.net.SocketAddress;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public abstract class AbstractTftpTransfer<TftpTransferContext> implements TftpTransfer<TftpTransferContext> {

    private final SocketAddress remoteAddress;

    public AbstractTftpTransfer(@Nonnull SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Nonnull
    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }
}
