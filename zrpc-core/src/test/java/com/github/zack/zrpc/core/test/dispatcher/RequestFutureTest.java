package com.github.zack.zrpc.core.test.dispatcher;

import com.github.zack.zrpc.core.dispatcher.RequestFuture;
import com.github.zack.zrpc.core.exception.RpcTimeoutException;
import com.github.zack.zrpc.core.exception.RpcTransportException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RequestFutureTest {

    @Test
    public void shouldThrowTimeoutExceptionWhenAwaitTimedOut() {
        RequestFuture requestFuture = new RequestFuture("request-timeout");
        assertThatThrownBy(() -> requestFuture.get(10))
                .isInstanceOf(RpcTimeoutException.class)
                .hasMessageContaining("request-timeout");
    }

    @Test
    public void shouldThrowTransportExceptionWhenFailureIsSet() {
        RequestFuture requestFuture = new RequestFuture("request-failed");
        requestFuture.setFailure(new RpcTransportException("send failed"));

        assertThatThrownBy(() -> requestFuture.get(100))
                .isInstanceOf(RpcTransportException.class)
                .hasMessageContaining("send failed");
    }
}
