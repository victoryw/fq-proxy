package com.victoryw.fq.proxy.demo;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class HttpChannelFutureListener implements ChannelFutureListener {
    private static final String LOG_PRE = "[http连接建立监听器]通道id:{}";

    /**
     * 客户端要发送给目标主机的消息
     */
    private Object msg;

    /**
     * 通道上下文,如果与目标主机建立连接失败,返回失败响应给客户端,并关闭连接
     */
    private ChannelHandlerContext ctx;

    public HttpChannelFutureListener(Object msg, ChannelHandlerContext ctx) {
        this.msg = msg;
        this.ctx = ctx;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if(future.isSuccess()){
            //将客户端请求报文发送给服务端
            future.channel().writeAndFlush(msg);
            return;
        }
        //并关闭 与客户端的连接
        ctx.close();
    }
}
