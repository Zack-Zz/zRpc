package com.github.zack.zrpc.core;

import com.github.zack.zrpc.core.dispatcher.RequestPendingDispatcher;
import com.github.zack.zrpc.core.dispatcher.RequestFuture;
import com.github.zack.zrpc.core.exception.RpcServerException;
import com.github.zack.zrpc.core.exception.RpcTimeoutException;
import com.github.zack.zrpc.core.exception.RpcTransportException;
import com.github.zack.zrpc.core.proto.RequestMessage;
import com.github.zack.zrpc.core.proto.ResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 *
 * @author zack
 * @since 2025/2/18
 */
public class RpcClient {

    private final Channel channel;
    private final long requestTimeoutMs;

    public RpcClient(Channel channel) {
        this(channel, 5000L);
    }

    public RpcClient(Channel channel, long requestTimeoutMs) {
        this.channel = channel;
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public ResponseMessage sendRequest(RequestMessage request) throws InterruptedException {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        String requestId = request.getRequestId();
        if (requestId == null || requestId.isEmpty()) {
            throw new IllegalArgumentException("requestId must not be empty");
        }
        if (channel == null || !channel.isActive()) {
            throw new RpcTransportException("channel is not active");
        }

        RequestPendingDispatcher dispatcher = RequestPendingDispatcher.singleInstance();
        RequestFuture future = dispatcher.register(requestId);

        ChannelFuture channelFuture = channel.writeAndFlush(request);
        channelFuture.addListener(f -> {
            if (!f.isSuccess()) {
                RequestFuture removed = dispatcher.remove(requestId);
                if (removed != null) {
                    removed.setFailure(new RpcTransportException("failed to send request, requestId=" + requestId, f.cause()));
                }
            }
        });

        try {
            ResponseMessage responseMessage = future.get(requestTimeoutMs);
            if (!responseMessage.getError().isEmpty()) {
                throw new RpcServerException("Server error for requestId=" + requestId + ", message=" + responseMessage.getError());
            }
            return responseMessage;
        } catch (RpcTimeoutException timeoutException) {
            dispatcher.remove(requestId, future);
            throw timeoutException;
        }
    }

}
