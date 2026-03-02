package com.github.zack.zrpc.core.dispatcher;

import com.github.zack.zrpc.core.exception.RpcTimeoutException;
import com.github.zack.zrpc.core.exception.RpcTransportException;
import com.github.zack.zrpc.core.proto.ResponseMessage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Synchronization primitive for one in-flight request response.
 * It carries either response payload or transport failure.
 *
 * @author zack
 * @since 2025/2/18
 */
public class RequestFuture {
    private final String requestId;
    private volatile ResponseMessage response;
    private volatile Throwable failure;
    private final CountDownLatch latch = new CountDownLatch(1);

    /**
     * Creates a future bound to a request id.
     *
     * @param requestId unique request identifier
     */
    public RequestFuture(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Completes this future with a response.
     *
     * @param response response payload
     */
    public void setResponse(ResponseMessage response) {
        this.response = response;
        latch.countDown();
    }

    /**
     * Completes this future with a failure.
     *
     * @param throwable transport or runtime failure
     */
    public void setFailure(Throwable throwable) {
        this.failure = throwable;
        latch.countDown();
    }

    /**
     * Waits for completion until timeout.
     *
     * @param timeout timeout in milliseconds
     * @return completed response payload
     * @throws InterruptedException when waiting thread is interrupted
     * @throws RpcTimeoutException when timeout is reached
     * @throws RpcTransportException when request fails or response is invalid
     */
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
