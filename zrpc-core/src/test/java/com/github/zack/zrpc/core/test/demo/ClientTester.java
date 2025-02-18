package com.github.zack.zrpc.core.test.demo;

import com.github.zack.zrpc.core.NettyClient;
import com.github.zack.zrpc.core.RpcClient;
import com.github.zack.zrpc.core.TargetNode;
import com.github.zack.zrpc.core.request.RequestContext;
import com.github.zack.zrpc.core.response.ResponseContext;

/**
 *
 * @author zack
 * @since 2025/2/18
 */
public class ClientTester {

    public static void main(String[] args) throws InterruptedException {
        TargetNode targetNode = new TargetNode();
        targetNode.setHost("127.0.0.1");
        targetNode.setPort(8081);


        NettyClient nettyClient = new NettyClient();
        nettyClient.connect(targetNode);

        RpcClient rpcClient = new RpcClient(nettyClient.getChannel());

        // 构造 RPC 请求
        RequestContext request = new RequestContext();
        request.setClassName("com.github.zack.zrpc.core.test.demo.ServerUserService");
        request.setMethodName("getNameById");
        request.setParameterTypes(new Class[]{Long.class});
        request.setParameters(new Object[]{1});

        // 发送 RPC 请求并获取结果
        ResponseContext response = rpcClient.sendRequest(request);
        System.out.println("RPC 结果：" + response.getResult());

    }
}
