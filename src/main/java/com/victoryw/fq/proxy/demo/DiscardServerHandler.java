package com.victoryw.fq.proxy.demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(DiscardServerHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.write(msg);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("some error",cause);
        ctx.close();
    }
}
