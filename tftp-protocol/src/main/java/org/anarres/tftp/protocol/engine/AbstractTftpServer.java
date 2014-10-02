/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.engine;

import java.io.IOException;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.anarres.tftp.protocol.resource.TftpDataProvider;

/**
 *
 * @author shevek
 */
public abstract class AbstractTftpServer {

    private final TftpDataProvider dataProvider;
    private final int port;

    public AbstractTftpServer(@Nonnull TftpDataProvider dataProvider, @Nonnegative int port) {
        this.dataProvider = dataProvider;
        this.port = port;
    }

    @Nonnull
    public TftpDataProvider getDataProvider() {
        return dataProvider;
    }

    @Nonnegative
    public int getPort() {
        return port;
    }

    @PostConstruct
    public abstract void start() throws IOException, InterruptedException;

    @PreDestroy
    public abstract void stop() throws IOException, InterruptedException;
}
