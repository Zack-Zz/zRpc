package com.github.zack.zrpc.core;

import com.github.zack.zrpc.core.codec.RequestProtocolEncoder;
import com.github.zack.zrpc.core.codec.ResponseProtocolDecoder;
import com.github.zack.zrpc.core.codec.StreamFrameDecoder;
import com.github.zack.zrpc.core.codec.StreamFrameEncoder;
import com.github.zack.zrpc.core.exception.RpcTransportException;
import com.github.zack.zrpc.core.handler.ClientHandler;
import com.github.zack.zrpc.core.logger.Logger;
import com.github.zack.zrpc.core.logger.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Netty-based client transport responsible for connection lifecycle management.
 *
 * @author zack
 * @since 2024/12/20
 */
public class NettyClient {

    private final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private volatile Channel channel;

    private volatile boolean connectedSuccess = false;
    private volatile EventLoopGroup group;

    /**
     * Establishes a connection to the target node and returns after connection is ready.
     *
     * @param targetNode target host and port
     * @throws InterruptedException when the connect operation is interrupted
     * @throws IllegalArgumentException when target node parameters are invalid
     * @throws RpcTransportException when connection cannot be established
     */
    public synchronized void connect(TargetNode targetNode) throws InterruptedException {
        if (targetNode == null) {
            throw new IllegalArgumentException("targetNode must not be null");
        }
        if (targetNode.getHost() == null || targetNode.getHost().isEmpty()) {
            throw new IllegalArgumentException("target host must not be empty");
        }
        if (targetNode.getPort() == null || targetNode.getPort() <= 0) {
            throw new IllegalArgumentException("target port must be positive");
        }
        if (isConnectedSuccess()) {
            return;
        }

        EventLoopGroup localGroup = group;
        if (localGroup == null) {
            localGroup = new NioEventLoopGroup();
            group = localGroup;
        }

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(localGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new StreamFrameDecoder());
                        ch.pipeline().addLast(new StreamFrameEncoder());
                        ch.pipeline().addLast(new ResponseProtocolDecoder());
                        ch.pipeline().addLast(new RequestProtocolEncoder());
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });

        ChannelFuture future = bootstrap.connect(targetNode.getHost(), targetNode.getPort()).sync();
        if (!future.isSuccess()) {
            close();
            throw new RpcTransportException("Failed to connect to " + targetNode, future.cause());
        }

        this.channel = future.channel();
        this.connectedSuccess = true;
        this.channel.closeFuture().addListener(f -> connectedSuccess = false);
        logger.info("connect success. target={}", targetNode);
    }

    /**
     * Closes the active channel and shuts down the event loop group.
     *
     * @throws InterruptedException when close operation is interrupted
     */
    public synchronized void close() throws InterruptedException {
        Channel activeChannel = this.channel;
        if (activeChannel != null) {
            activeChannel.close().sync();
            this.channel = null;
        }
        EventLoopGroup localGroup = this.group;
        if (localGroup != null) {
            localGroup.shutdownGracefully().sync();
            this.group = null;
        }
        this.connectedSuccess = false;
    }

    /**
     * Returns the active channel instance.
     *
     * @return active channel, or {@code null} before successful connect
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Indicates whether the client currently holds an active connection.
     *
     * @return {@code true} when channel is connected and active
     */
    public boolean isConnectedSuccess() {
        Channel activeChannel = this.channel;
        return connectedSuccess && activeChannel != null && activeChannel.isActive();
    }
}
