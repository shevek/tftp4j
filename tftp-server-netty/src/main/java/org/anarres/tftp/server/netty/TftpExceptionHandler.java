/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.anarres.tftp.server.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shevek
 */
@ChannelHandler.Sharable
public class TftpExceptionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TftpExceptionHandler.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            ctx.fireChannelActive();
        } catch (Throwable t) {
            ctx.fireExceptionCaught(t);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ctx.fireChannelRead(msg);
        } catch (Throwable t) {
            LOG.error("Catch-fail", t);
            ctx.fireExceptionCaught(t);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        try {
            ctx.fireChannelReadComplete();
        } catch (Throwable t) {
            ctx.fireExceptionCaught(t);
        }
    }
}