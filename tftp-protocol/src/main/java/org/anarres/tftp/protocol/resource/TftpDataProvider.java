/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import com.google.common.io.ByteSource;
import java.io.IOException;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public interface TftpDataProvider {

    @CheckForNull
    public ByteSource open(@Nonnull String filename) throws IOException;
}
