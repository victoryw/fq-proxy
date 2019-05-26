package com.victoryw.fq.proxy.server;

import com.victoryw.fq.proxy.App;
import com.victoryw.fq.proxy.target.HttpProxyToTargetService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;

public class HttpProxyInboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static Logger logger = LoggerFactory.getLogger(HttpProxyInboundHandler.class);

    private static String serverHost = "127.0.0.1";
    static int serverPort = 8080;
    private static final String serverAddress = String.format("%s:%s", serverHost, serverPort);

    private final HttpProxyToTargetService proxyService;

    private String requestHost;

    public HttpProxyInboundHandler() {
        proxyService = new HttpProxyToTargetService();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        requestHost = msg.headers().get(HttpHeaderNames.HOST);
        if (serverAddress.equalsIgnoreCase(requestHost)) {
            logger.debug("Incoming Request Call Proxy Server HTTP Service");
            defaultHttpHandle(ctx, msg);
            return;
        }

        if (HttpMethod.CONNECT == msg.method()) {
            logger.debug("Incoming Request Call Proxy Server HTTPs Service {}", requestHost);
            ///TODO: HTTPS CONNECT
            return;
        }

        logger.debug("Incoming Request Call Proxy Server HTTP Service {}", requestHost);
        proxyService.doSend(requestHost, ctx, msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("some error happened", cause);
        ctx.close();
    }

    private void defaultHttpHandle(ChannelHandlerContext ctx, HttpRequest httpRequest) {
        final byte[] CONTENT = "hello, welcome".getBytes();
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

}
