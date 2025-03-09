package com.github.zack.zrpc.core;

import com.github.zack.zrpc.core.dispatcher.RequestPendingDispatcher;
import com.github.zack.zrpc.core.dispatcher.RequestFuture;
import com.github.zack.zrpc.core.proto.RequestMessage;
import com.github.zack.zrpc.core.proto.ResponseMessage;
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

    public ResponseMessage sendRequest(RequestMessage request) throws InterruptedException {

        channel.writeAndFlush(request);

        RequestFuture future = RequestPendingDispatcher.singleInstance().sendRequest(request);
        return future.get(5000); // 超时 5 秒
    }

}
