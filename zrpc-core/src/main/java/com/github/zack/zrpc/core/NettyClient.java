package com.github.zack.zrpc.core;

import com.github.zack.zrpc.core.request.RequestEncoder;
import com.github.zack.zrpc.core.response.ResponseDecoder;
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

    private Channel channel;

    public void connect(TargetNode targetNode) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {

                            ch.pipeline().addLast(new ResponseDecoder());
                            ch.pipeline().addLast(new RequestEncoder());
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });

            // 连接到服务端
            ChannelFuture future = bootstrap.connect(targetNode.getHost(), targetNode.getPort()).sync();
            NettyClient.this.channel = future.channel();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public Channel getChannel() {
        return channel;
    }
}
