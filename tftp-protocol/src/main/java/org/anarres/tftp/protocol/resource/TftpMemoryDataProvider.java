/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.CheckForNull;

/**
 *
 * @author shevek
 */
public class TftpMemoryDataProvider extends AbstractTftpDataProvider {

    private final Map<String, byte[]> map = new HashMap<String, byte[]>();

    public void setData(String name, byte[] data) {
        map.put(name, data);
    }

    public void setData(String name, String data) {
        map.put(name, data.getBytes(Charsets.ISO_8859_1));
    }

    @CheckForNull
    public byte[] getData(String name) {
        return map.get(name);
    }

    @Override
    public ByteSource open(String filename) throws IOException {
        byte[] data = map.get(filename);
        if (data == null)
            return null;
        return ByteSource.wrap(data);
    }
}
