package com.github.zack.zrpc.core;

import com.github.zack.zrpc.core.request.RequestContext;
import io.netty.channel.Channel;

/**
 *
 * @author zack
 * @since 2025/2/18
 */
public class RpcClient {

    private final Channel channel;

    public RpcClient(Channel channel) {
        this.channel = channel;
    }

    public Object sendRequest(RequestContext request) throws Exception {
        RpcFuture future = ClientHandler.sendRequest(request, channel.pipeline().context(ClientHandler.class));
        return future.get(5000); // 超时 5 秒
    }
}
