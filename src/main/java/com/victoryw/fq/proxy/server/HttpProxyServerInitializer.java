package com.victoryw.fq.proxy.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;

public class HttpProxyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast("aggegator", new HttpObjectAggregator(512 * 1024));  //3
        //消息聚合器,注意,需要添加在http编解码器(HttpServerCodec)之后
        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast(new HttpProxyInboundHandler());
    }
}


