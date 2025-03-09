package com.github.zack.zrpc.core;

import com.github.zack.zrpc.core.codec.RequestProtocolDecoder;
import com.github.zack.zrpc.core.codec.ResponseProtocolEncoder;
import com.github.zack.zrpc.core.codec.StreamFrameDecoder;
import com.github.zack.zrpc.core.codec.StreamFrameEncoder;
import com.github.zack.zrpc.core.handler.ServerHandler;
import com.github.zack.zrpc.core.logger.Logger;
import com.github.zack.zrpc.core.logger.LoggerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author zack
 * @since 2024/12/19
 */
public class TargetServer {

    private final Logger logger = LoggerFactory.getLogger(TargetServer.class);

    public void openConnect(ServerNode serverNode) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        LoggingHandler infoHandler = new LoggingHandler(LogLevel.INFO);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new IdleStateHandler(60, 30, 0, TimeUnit.SECONDS));

                            pipeline.addLast("frameDecoder", new StreamFrameDecoder());
                            pipeline.addLast("frameEncoder", new StreamFrameEncoder());
                            pipeline.addLast("protocolDecoder", new RequestProtocolDecoder());
                            pipeline.addLast("protocolEncoder", new ResponseProtocolEncoder());

                            pipeline.addLast("serverHandler", new ServerHandler());

                            pipeline.addLast("infoHandler", infoHandler);

                        }
                    });

            ChannelFuture future = bootstrap.bind(serverNode.getPort()).sync();
            logger.info("Server started, listening on port：" + serverNode.getPort());
            // 阻塞，直到关闭
            future.channel().closeFuture().sync();
        } finally {
            // 优雅关闭事件循环组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
