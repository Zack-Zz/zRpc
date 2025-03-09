package com.github.zack.zrpc.core;

import com.github.zack.zrpc.core.codec.RequestProtocolEncoder;
import com.github.zack.zrpc.core.codec.ResponseProtocolDecoder;
import com.github.zack.zrpc.core.codec.StreamFrameDecoder;
import com.github.zack.zrpc.core.codec.StreamFrameEncoder;
import com.github.zack.zrpc.core.common.IdUtil;
import com.github.zack.zrpc.core.handler.ClientHandler;
import com.github.zack.zrpc.core.logger.Logger;
import com.github.zack.zrpc.core.logger.LoggerFactory;
import com.github.zack.zrpc.core.request.RequestContext;
import com.github.zack.zrpc.core.response.ResponseContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author zack
 * @since 2024/12/20
 */
public class NettyClient {

    private final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private Channel channel;

    private volatile boolean connectedSuccess = false;

    public void connect(TargetNode targetNode) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
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

            // 连接到服务端
            ChannelFuture future = bootstrap.connect(targetNode.getHost(), targetNode.getPort());
            future.sync();
            future.addListener(f -> {
                if (f.isSuccess()) {
                    logger.info("connect success.");
                    connectedSuccess = true;
                } else {
                    logger.error("connect status:{}", f.isSuccess());
                }
            });
            NettyClient.this.channel = future.channel();

            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isConnectedSuccess() {
        return connectedSuccess;
    }
}
