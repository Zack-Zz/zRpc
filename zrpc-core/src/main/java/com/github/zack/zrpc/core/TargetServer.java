package com.github.zack.zrpc.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author zack
 * @since 2024/12/19
 */
public class TargetServer {

    public void connect(TargetNode targetNode) throws InterruptedException {
        // 创建事件循环组
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // 接收连接
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 处理读写

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 使用NIO的ServerSocketChannel
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 初始化子通道
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 添加自定义处理器
                            pipeline.addLast(new ServerHandler());
                        }
                    });

            // 绑定端口，启动服务
            ChannelFuture future = bootstrap.bind(targetNode.getIp(), targetNode.getPort()).sync();
            System.out.println("Server started, listening on port 8080");
            // 阻塞，直到关闭
            future.channel().closeFuture().sync();
        } finally {
            // 优雅关闭事件循环组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
