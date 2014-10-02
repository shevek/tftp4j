/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.protocol.packet;

/**
 *
 * @author shevek
 */
public class TftpWriteRequestPacket extends TftpRequestPacket {

    @Override
    public TftpOpcode getOpcode() {
        return TftpOpcode.WRQ;
    }
}
