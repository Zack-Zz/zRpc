package com.github.zack.zrpc.core.test;

import com.github.zack.zrpc.core.RpcClient;
import com.github.zack.zrpc.core.dispatcher.RequestPendingDispatcher;
import com.github.zack.zrpc.core.dispatcher.RequestFuture;
import com.github.zack.zrpc.core.exception.RpcServerException;
import com.github.zack.zrpc.core.exception.RpcTimeoutException;
import com.github.zack.zrpc.core.exception.RpcTransportException;
import com.github.zack.zrpc.core.proto.RequestMessage;
import com.github.zack.zrpc.core.proto.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RpcClientTest {

    @AfterEach
    public void tearDown() {
        RequestPendingDispatcher.singleInstance().clear();
    }

    @Test
    public void shouldCleanupPendingRequestWhenTimeout() {
        EmbeddedChannel channel = new EmbeddedChannel();

        RpcClient rpcClient = new RpcClient(channel, 10);
        RequestMessage requestMessage = RequestMessage.newBuilder().setRequestId("timeout-1").build();

        assertThatThrownBy(() -> rpcClient.sendRequest(requestMessage))
                .isInstanceOf(RpcTimeoutException.class);
        assertThat(RequestPendingDispatcher.singleInstance().size()).isZero();
    }

    @Test
    public void shouldCleanupPendingRequestWhenSendFailed() {
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                promise.setFailure(new RuntimeException("send failed"));
            }
        });

        RpcClient rpcClient = new RpcClient(channel, 1000);
        RequestMessage requestMessage = RequestMessage.newBuilder().setRequestId("failed-1").build();

        assertThatThrownBy(() -> rpcClient.sendRequest(requestMessage))
                .isInstanceOf(RpcTransportException.class)
                .hasMessageContaining("failed to send request");
        assertThat(RequestPendingDispatcher.singleInstance().size()).isZero();
    }

    @Test
    public void shouldThrowServerExceptionWhenResponseContainsError() {
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                RequestMessage request = (RequestMessage) msg;
                RequestFuture requestFuture = RequestPendingDispatcher.singleInstance().remove(request.getRequestId());
                requestFuture.setResponse(ResponseMessage.newBuilder()
                        .setRequestId(request.getRequestId())
                        .setError("service failed")
                        .build());
                promise.setSuccess();
            }
        });

        RpcClient rpcClient = new RpcClient(channel, 1000);
        RequestMessage requestMessage = RequestMessage.newBuilder().setRequestId("server-error-1").build();
        assertThatThrownBy(() -> rpcClient.sendRequest(requestMessage))
                .isInstanceOf(RpcServerException.class)
                .hasMessageContaining("service failed");
    }
}
