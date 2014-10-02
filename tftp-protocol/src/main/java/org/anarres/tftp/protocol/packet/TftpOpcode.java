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
public enum TftpOpcode {

    // TODO: Implement opcode 6, options-response.
    RRQ(1, "Read request"),
    WRQ(2, "Write request"),
    DATA(3, "Data"),
    ACK(4, "Acknowledgment"),
    ERROR(5, "Error"),
    // http://tools.ietf.org/html/rfc2347
    ACK_WITH_OPTIONS(6, "Ack with options");
    private final short code;
    private final String description;

    /* pp */
    TftpOpcode(int code, String description) {
        this.code = (short) code;
        this.description = description;
    }

    public short getCode() {
        return code;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name() + "(" + getCode() + ", " + getDescription() + ")";
    }

    @Nonnull
    public static TftpOpcode forCode(short code) {
        for (TftpOpcode opcode : TftpOpcode.values())
            if (opcode.getCode() == code)
                return opcode;
        throw new IllegalArgumentException("No such TFTP opcode " + code);
    }
}
