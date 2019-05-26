package com.victoryw.fq.proxy.target;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequest;

public class HttpProxyToTargetService {
    public void doSend(String address, ChannelHandlerContext ctx, HttpRequest httpRequest) {
        final Bootstrap bootStrap = createTargetClientBootStrap(ctx);
        send(address, ctx, httpRequest, bootStrap);

    }

    private void send(String address, ChannelHandlerContext ctx, HttpRequest httpRequest, Bootstrap bootStrap) {
        final String[] hostAndPort = address.split(":");
        final int port = hostAndPort.length == 2 ? Integer.valueOf(hostAndPort[1]) : 80;
        final String host = hostAndPort[0];

        bootStrap.connect(host, port)
                .addListener((ChannelFutureListener) future -> {
                    if(future.isSuccess()){
                        //将客户端请求报文发送给服务端
                        future.channel().writeAndFlush(httpRequest);
                        return;
                    }
                    ctx.close();
                });
    }

    private Bootstrap createTargetClientBootStrap(ChannelHandlerContext ctx) {
        return new Bootstrap()
                    .group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new HttpTargetConnectChannelInitializer(ctx));
    }
}
