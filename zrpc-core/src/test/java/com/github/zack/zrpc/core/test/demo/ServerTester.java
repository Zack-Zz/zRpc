package com.github.zack.zrpc.core.test.demo;

import com.github.zack.zrpc.core.ServerNode;
import com.github.zack.zrpc.core.ServiceRegistry;
import com.github.zack.zrpc.core.TargetServer;

/**
 *
 * @author zack
 * @since 2025/2/18
 */
public class ServerTester {

    public static void main(String[] args) throws InterruptedException {
        ServiceRegistry.registerService(ServerUserService.class, new ServerUserService());

        ServerNode serverNode = new ServerNode();
        serverNode.setPort(8081);

        TargetServer targetServer = new TargetServer();
        targetServer.openConnect(serverNode);

    }
}
