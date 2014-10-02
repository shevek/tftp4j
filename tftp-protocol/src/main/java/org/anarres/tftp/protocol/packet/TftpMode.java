/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.packet;

import javax.annotation.Nonnull;

/**
 *
 * @author shevek
 */
public enum TftpMode {

    NETASCII, OCTET, MAIL;

    @Nonnull
    public static TftpMode forMode(@Nonnull String code) {
        return TftpMode.valueOf(code.toUpperCase());
    }
}
