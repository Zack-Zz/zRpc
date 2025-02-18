package com.github.zack.zrpc.core;

import com.github.zack.zrpc.core.logger.Logger;
import com.github.zack.zrpc.core.logger.LoggerFactory;
import com.github.zack.zrpc.core.request.RequestContext;
import com.github.zack.zrpc.core.response.ResponseContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zack
 * @since 2024/12/20
 */
public class ClientHandler extends SimpleChannelInboundHandler<ResponseContext> {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    // 存储等待响应的请求
    private static final ConcurrentHashMap<String, RpcFuture> pendingRequests = new ConcurrentHashMap<>();

    public static RpcFuture sendRequest(RequestContext request, ChannelHandlerContext ctx) {
        RpcFuture rpcFuture = new RpcFuture();
        pendingRequests.put(request.getRequestId(), rpcFuture);
        ctx.writeAndFlush(request);
        return rpcFuture;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseContext response) {
        RpcFuture rpcFuture = pendingRequests.remove(response.getRequestId());
        if (rpcFuture != null) {
            rpcFuture.setResponse(response);
        }
    }


}
