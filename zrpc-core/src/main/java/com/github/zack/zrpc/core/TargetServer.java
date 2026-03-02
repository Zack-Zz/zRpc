package com.github.zack.zrpc.core;

import com.github.zack.zrpc.core.codec.RequestProtocolDecoder;
import com.github.zack.zrpc.core.codec.ResponseProtocolEncoder;
import com.github.zack.zrpc.core.codec.StreamFrameDecoder;
import com.github.zack.zrpc.core.codec.StreamFrameEncoder;
import com.github.zack.zrpc.core.handler.MetricHandler;
import com.github.zack.zrpc.core.handler.ServerHandler;
import com.github.zack.zrpc.core.logger.Logger;
import com.github.zack.zrpc.core.logger.LoggerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;

import java.util.concurrent.TimeUnit;

/**
 * @author zack
 * @since 2024/12/19
 */
public class TargetServer {

    private final Logger logger = LoggerFactory.getLogger(TargetServer.class);
    private volatile EventLoopGroup bossGroup;
    private volatile EventLoopGroup workerGroup;
    private volatile UnorderedThreadPoolEventExecutor businessGroup;
    private volatile Channel serverChannel;

    public synchronized void openConnect(ServerNode serverNode) throws InterruptedException {
        start(serverNode);
        Channel currentServerChannel = this.serverChannel;
        if (currentServerChannel != null) {
            currentServerChannel.closeFuture().sync();
        }
    }

    public synchronized void start(ServerNode serverNode) throws InterruptedException {
        if (serverNode == null) {
            throw new IllegalArgumentException("serverNode must not be null");
        }
        if (serverNode.getPort() <= 0) {
            throw new IllegalArgumentException("server port must be positive");
        }
        if (isRunning()) {
            return;
        }

        EventLoopGroup localBossGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("bossGroup"));
        EventLoopGroup localWorkerGroup = new NioEventLoopGroup(8, new DefaultThreadFactory("workerGroup"));
        UnorderedThreadPoolEventExecutor localBusinessGroup =
                new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("business"));

        LoggingHandler infoHandler = new LoggingHandler(LogLevel.INFO);
        MetricHandler metricHandler = new MetricHandler();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(localBossGroup, localWorkerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(NioChannelOption.TCP_NODELAY, true)
                .option(NioChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new IdleStateHandler(60, 30, 0, TimeUnit.SECONDS));
                        pipeline.addLast("frameDecoder", new StreamFrameDecoder());
                        pipeline.addLast("metricHandler", metricHandler);
                        pipeline.addLast("frameEncoder", new StreamFrameEncoder());
                        pipeline.addLast("protocolDecoder", new RequestProtocolDecoder());
                        pipeline.addLast("protocolEncoder", new ResponseProtocolEncoder());
                        pipeline.addLast("flushEnhance", new FlushConsolidationHandler(5, true));
                        pipeline.addLast(localBusinessGroup, "serverHandler", new ServerHandler());
                        pipeline.addLast("infoHandler", infoHandler);
                    }
                });

        try {
            ChannelFuture future = bootstrap.bind(serverNode.getPort()).sync();
            this.bossGroup = localBossGroup;
            this.workerGroup = localWorkerGroup;
            this.businessGroup = localBusinessGroup;
            this.serverChannel = future.channel();
            this.serverChannel.closeFuture().addListener(f -> logger.info("Server channel closed."));
            logger.info("Server started, listening on port={}", serverNode.getPort());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            shutdownGroups(localBossGroup, localWorkerGroup, localBusinessGroup);
            throw interruptedException;
        } catch (RuntimeException runtimeException) {
            shutdownGroups(localBossGroup, localWorkerGroup, localBusinessGroup);
            throw runtimeException;
        }
    }

    public synchronized void close() throws InterruptedException {
        Channel currentServerChannel = this.serverChannel;
        if (currentServerChannel != null) {
            currentServerChannel.close().sync();
            this.serverChannel = null;
        }

        EventLoopGroup currentBossGroup = this.bossGroup;
        EventLoopGroup currentWorkerGroup = this.workerGroup;
        UnorderedThreadPoolEventExecutor currentBusinessGroup = this.businessGroup;
        this.bossGroup = null;
        this.workerGroup = null;
        this.businessGroup = null;
        shutdownGroups(currentBossGroup, currentWorkerGroup, currentBusinessGroup);
    }

    public boolean isRunning() {
        Channel currentServerChannel = this.serverChannel;
        return currentServerChannel != null && currentServerChannel.isActive();
    }

    private void shutdownGroups(
            EventLoopGroup localBossGroup,
            EventLoopGroup localWorkerGroup,
            UnorderedThreadPoolEventExecutor localBusinessGroup) throws InterruptedException {
        if (localBusinessGroup != null) {
            localBusinessGroup.shutdownGracefully().sync();
        }
        if (localWorkerGroup != null) {
            localWorkerGroup.shutdownGracefully().sync();
        }
        if (localBossGroup != null) {
            localBossGroup.shutdownGracefully().sync();
        }
    }
}
