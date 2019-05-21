package com.victoryw.fq.proxy.demo;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class HttpConnectChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelHandlerContext ctx;

    public HttpConnectChannelInitializer(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                //作为客户端时的请求编码解码
                .addLast(new HttpClientCodec())
                //数据聚合类,将http报文转为 FullHttpRequest和FullHttpResponse
                .addLast(new HttpObjectAggregator(Integer.MAX_VALUE))
                //自定义处理器
                .addLast(new HttpConnectHandler(ctx));
    }
}


