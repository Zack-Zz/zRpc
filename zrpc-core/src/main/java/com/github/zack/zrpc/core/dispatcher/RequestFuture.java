package com.github.zack.zrpc.core.dispatcher;

import com.github.zack.zrpc.core.exception.RpcTimeoutException;
import com.github.zack.zrpc.core.exception.RpcTransportException;
import com.github.zack.zrpc.core.proto.ResponseMessage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 客户端发送请求后不能同步等待，否则会造成性能瓶颈。因此使用 Future 机制，让客户端异步等待结果
 *
 * @author zack
 * @since 2025/2/18
 */
public class RequestFuture {
    private final String requestId;
    private volatile ResponseMessage response;
    private volatile Throwable failure;
    private final CountDownLatch latch = new CountDownLatch(1);

    public RequestFuture(String requestId) {
        this.requestId = requestId;
    }

    public void setResponse(ResponseMessage response) {
        this.response = response;
        latch.countDown(); // 释放等待
    }

    public void setFailure(Throwable throwable) {
        this.failure = throwable;
        latch.countDown();
    }

    public ResponseMessage get(long timeout) throws InterruptedException {
        boolean completed = latch.await(timeout, TimeUnit.MILLISECONDS);
        if (!completed) {
            throw new RpcTimeoutException("RPC request timeout, requestId=" + requestId + ", timeoutMs=" + timeout);
        }
        if (failure != null) {
            if (failure instanceof RuntimeException) {
                throw (RuntimeException) failure;
            }
            throw new RpcTransportException("RPC request failed, requestId=" + requestId, failure);
        }
        if (response == null) {
            throw new RpcTransportException("RPC response is null, requestId=" + requestId);
        }
        return response;
    }
}
