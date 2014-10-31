/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.resource;

import java.io.IOException;

/**
 *
 * @author shevek
 */
public abstract class AbstractTftpData implements TftpData {

    @Override
    public void close() throws IOException {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + System.identityHashCode(this) + "(" + getSize() + " bytes)";
    }
}
