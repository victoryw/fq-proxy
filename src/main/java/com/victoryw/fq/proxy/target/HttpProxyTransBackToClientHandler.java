package com.victoryw.fq.proxy.target;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpProxyTransBackToClientHandler extends ChannelInboundHandlerAdapter {
    private final ChannelHandlerContext clientCtx;
    public HttpProxyTransBackToClientHandler(ChannelHandlerContext clientCtx) {
        this.clientCtx = clientCtx;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx0, Object msg) throws Exception {
        if(clientCtx.channel().isActive()){
            clientCtx.writeAndFlush(msg);
            return;
        }

        clientCtx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        clientCtx.close();
    }

}
