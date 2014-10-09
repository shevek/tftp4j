/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class TftpFileDataProvider extends AbstractTftpDataProvider {

    public static final String PREFIX = "/tftproot";
    private final String prefix;

    public TftpFileDataProvider(@Nonnull String prefix) {
        this.prefix = prefix;
    }

    public TftpFileDataProvider() {
        this(PREFIX);
    }

    @Nonnull
    public String getPrefix() {
        return prefix;
    }

    /**
     * Prepends the prefix to the filename, then returns an ordinary file, or null.
     */
    @CheckForNull
    protected File toFile(@Nonnull String filename) {
        String path = toPath(getPrefix(), filename);
        if (path == null)
            return null;
        File file = new File(path);
        if (!file.isFile())
            return null;
        return file;
    }

    @Override
    public ByteSource open(String filename) throws IOException {
        File file = toFile(filename);
        if (file == null)
            return null;
        return Files.asByteSource(file);
    }
}