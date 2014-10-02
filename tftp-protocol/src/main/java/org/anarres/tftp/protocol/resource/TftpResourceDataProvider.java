/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class TftpResourceDataProvider extends AbstractTftpDataProvider {

    private final String prefix;

    public TftpResourceDataProvider(@Nonnull String prefix) {
        this.prefix = prefix;
    }

    public TftpResourceDataProvider() {
        this(PREFIX);
    }

    @Override
    public ByteSource open(String filename) throws IOException {
        String path = toPath(prefix, filename);
        if (path == null)
            return null;
        URL resource = Resources.getResource(path);
        return Resources.asByteSource(resource);
    }
}