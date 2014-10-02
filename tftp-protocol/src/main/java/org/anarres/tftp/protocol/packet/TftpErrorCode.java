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
public enum TftpErrorCode {

    UNDEFINED(0, "Not defined, see error message (if any)."),
    FILE_NOT_FOUND(1, "File not found."),
    PERMISSION_DENIED(2, "Access violation."),
    DISK_FULL(3, "Disk full or allocation exceeded."),
    ILLEGAL_OPERATION(4, "Illegal TFTP operation."),
    UNKNOWN_TRANSFER_ID(5, "Unknown transfer ID."),
    FILE_ALREADY_EXISTS(6, "File already exists."),
    NO_SUCH_USER(7, "No such user.");
    private final short code;
    private final String description;

    /* pp */ TftpErrorCode(int code, String description) {
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
}