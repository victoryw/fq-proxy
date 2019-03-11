package com.victoryw.fq.proxy.demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeServerHandler extends ChannelInboundHandlerAdapter  {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final ByteBuf time = ctx.alloc().buffer(4);
        log.info(String.format("date is %d", System.currentTimeMillis()));
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
        final ChannelFuture f = ctx.writeAndFlush(time);
        f.addListener(ChannelFutureListener.CLOSE);
    }
}
