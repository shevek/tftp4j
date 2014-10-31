/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public class TftpMemoryDataProvider extends AbstractTftpDataProvider {

    private final Map<String, byte[]> map = new HashMap<String, byte[]>();

    public void setData(@Nonnull String name, @CheckForNull byte[] data) {
        if (data == null)
            map.remove(name);
        else
            map.put(name, data);
    }

    public void setData(@Nonnull String name, @Nonnull String data) {
        setData(name, data.getBytes(Charsets.ISO_8859_1));
    }

    @CheckForNull
    public byte[] getData(String name) {
        return map.get(name);
    }

    @Override
    public TftpData open(String filename) throws IOException {
        byte[] data = getData(filename);
        if (data == null)
            return null;
        return new TftpByteArrayData(data);
    }
}