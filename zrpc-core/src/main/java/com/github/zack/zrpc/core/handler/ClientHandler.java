package com.github.zack.zrpc.core.handler;

import com.github.zack.zrpc.core.dispatcher.RequestFuture;
import com.github.zack.zrpc.core.dispatcher.RequestPendingDispatcher;
import com.github.zack.zrpc.core.logger.Logger;
import com.github.zack.zrpc.core.logger.LoggerFactory;
import com.github.zack.zrpc.core.proto.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author zack
 * @since 2024/12/20
 */
public class ClientHandler extends SimpleChannelInboundHandler<ResponseMessage> {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage response) {
        RequestFuture requestFuture = RequestPendingDispatcher.singleInstance().remove(response.getRequestId());
        if (requestFuture != null) {
            requestFuture.setResponse(response);
        }
    }


}
