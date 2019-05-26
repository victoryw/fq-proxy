package com.victoryw.fq.proxy.target;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class HttpTargetConnectChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelHandlerContext ctx;

    public HttpTargetConnectChannelInitializer(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new HttpClientCodec())
                .addLast(new HttpObjectAggregator(Integer.MAX_VALUE))
                .addLast(new HttpProxyTransBackToClientHandler(ctx));
    }
}


