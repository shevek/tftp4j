/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.packet;

import com.google.common.base.Objects;
import java.nio.ByteBuffer;

/**
 *
 * @author shevek
 */
public class TftpErrorPacket extends TftpPacket {

    private short errorCode;
    private String errorMessage;

    public TftpErrorPacket() {
    }

    public TftpErrorPacket(short errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public TftpErrorPacket(TftpErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getDescription());
    }

    @Override
    public TftpOpcode getOpcode() {
        return TftpOpcode.ERROR;
    }

    public short getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(short errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void toWire(ByteBuffer buffer) {
        super.toWire(buffer);
        buffer.putShort(getErrorCode());
        putString(buffer, getErrorMessage());
    }

    @Override
    public void fromWire(ByteBuffer buffer) {
        setErrorCode(buffer.getShort());
        setErrorMessage(getString(buffer));
    }

    @Override
    protected Objects.ToStringHelper toStringHelper() {
        return super.toStringHelper().add("errorCode", getErrorCode()).add("errorMessage", getErrorMessage());
    }
}
