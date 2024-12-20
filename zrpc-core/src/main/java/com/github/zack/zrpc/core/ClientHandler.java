package com.github.zack.zrpc.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author zack
 * @since 2024/12/20
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Connected to server");
        ctx.writeAndFlush("Hello, Server!"); // 向服务端发送数据
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 处理服务端返回的数据
        System.out.println("Received from server: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close(); // 关闭连接
    }

}
