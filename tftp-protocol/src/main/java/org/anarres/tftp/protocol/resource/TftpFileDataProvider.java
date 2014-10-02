/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class TftpFileDataProvider extends AbstractTftpDataProvider {

    private final String prefix;

    public TftpFileDataProvider(@Nonnull String prefix) {
        this.prefix = prefix;
    }

    public TftpFileDataProvider() {
        this(PREFIX);
    }

    @Override
    public ByteSource open(String filename) throws IOException {
        String path = toPath(prefix, filename);
        if (path == null)
            return null;
        return Files.asByteSource(new File(path));
    }
}