package com.github.zack.zrpc.core.dispatcher;

import com.github.zack.zrpc.core.proto.ResponseMessage;
import com.github.zack.zrpc.core.response.ResponseContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 客户端发送请求后不能同步等待，否则会造成性能瓶颈。因此使用 Future 机制，让客户端异步等待结果
 *
 * @author zack
 * @since 2025/2/18
 */
public class RequestFuture {
    private ResponseMessage response;
    private final CountDownLatch latch = new CountDownLatch(1);

    public void setResponse(ResponseMessage response) {
        this.response = response;
        latch.countDown(); // 释放等待
    }

    public ResponseMessage get(long timeout) throws InterruptedException {
        latch.await(timeout, TimeUnit.MILLISECONDS);
        return response;
    }
}
