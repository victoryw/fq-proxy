package com.victoryw.fq.proxy.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private static final byte[] CONTENT = {'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'};
    private static final String serverHost = "127.0.0.1";
    static final int serverPort = 8080;
    private static final String serverAddress = String.format("%s:%s", serverHost, serverPort);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) {
        final HttpHeaders headers = httpRequest.headers();
        final String address = headers.get(HttpHeaderNames.HOST);

        if (serverAddress.equals(address)) {
            defaultHttpHandle(ctx, httpRequest);
            return;
        }


        headers.add("Connection", headers.get("Proxy-Connection"));
        headers.remove("Proxy-Connection");
        send(address, ctx, httpRequest);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭
        ctx.close();
    }


    private void defaultHttpHandle(ChannelHandlerContext ctx, HttpRequest httpRequest) {
        boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
        FullHttpResponse response = new DefaultFullHttpResponse(
                httpRequest.protocolVersion(),
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(CONTENT));
        response.headers()
                .set(CONTENT_TYPE, TEXT_PLAIN)
                .setInt(CONTENT_LENGTH,
                        response.content().readableBytes());

        ChannelFuture f = ctx.write(response);
    }

    private void send(String address, ChannelHandlerContext ctx, HttpRequest httpRequest) {
        final Bootstrap bootStrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new HttpConnectChannelInitializer(ctx));
        final String[] hostAndPort = address.split(":");
        final int port = hostAndPort.length == 2 ? Integer.valueOf(hostAndPort[1]) : 80;
        bootStrap.connect(hostAndPort[0], port)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if(future.isSuccess()){
                            //将客户端请求报文发送给服务端
                            future.channel().writeAndFlush(httpRequest);
                            return;
                        }
                        ctx.close();
                    }
                });

    }
}


