package com.github.zack.zrpc.core.test;

import com.github.zack.zrpc.core.NettyClient;
import com.github.zack.zrpc.core.ServerNode;
import com.github.zack.zrpc.core.TargetNode;
import com.github.zack.zrpc.core.TargetServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.assertj.core.api.Assertions.assertThat;

public class NettyLifecycleTest {

    @Test
    public void shouldConnectAndCloseWithoutBlockingBusinessThread() throws Exception {
        int port = findAvailablePort();
        TargetServer targetServer = new TargetServer();
        NettyClient nettyClient = new NettyClient();

        try {
            ServerNode serverNode = new ServerNode();
            serverNode.setPort(port);
            targetServer.start(serverNode);

            TargetNode targetNode = new TargetNode();
            targetNode.setHost("127.0.0.1");
            targetNode.setPort(port);

            long start = System.currentTimeMillis();
            nettyClient.connect(targetNode);
            long elapsed = System.currentTimeMillis() - start;

            assertThat(nettyClient.isConnectedSuccess()).isTrue();
            assertThat(elapsed).isLessThan(2000L);
        } finally {
            nettyClient.close();
            targetServer.close();
        }
    }

    private int findAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
