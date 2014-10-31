/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import java.io.IOException;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public interface TftpDataProvider {

    /**
     * Returns the resource with the given name.
     */
    @CheckForNull
    public TftpData open(@Nonnull String filename) throws IOException;
}
