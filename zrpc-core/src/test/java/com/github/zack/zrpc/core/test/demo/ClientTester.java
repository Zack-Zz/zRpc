package com.github.zack.zrpc.core.test.demo;

import com.github.zack.zrpc.core.NettyClient;
import com.github.zack.zrpc.core.RpcClient;
import com.github.zack.zrpc.core.TargetNode;
import com.github.zack.zrpc.core.common.IdUtil;
import com.github.zack.zrpc.core.common.ProtobufSerializer;
import com.github.zack.zrpc.core.logger.Logger;
import com.github.zack.zrpc.core.logger.LoggerFactory;
import com.github.zack.zrpc.core.proto.RequestMessage;
import com.github.zack.zrpc.core.proto.ResponseMessage;

import java.io.IOException;

/**
 *
 * @author zack
 * @since 2025/2/18
 */
public class ClientTester {

    private static final Logger logger = LoggerFactory.getLogger(ClientTester.class);

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {

        NettyClient nettyClient = new NettyClient();
        TargetNode targetNode = new TargetNode();
        targetNode.setHost("127.0.0.1");
        targetNode.setPort(8081);
        nettyClient.connect(targetNode);

        logger.info("connected and preparing...");

        RpcClient rpcClient = new RpcClient(nettyClient.getChannel());

        // 构造 RPC 请求
        RequestMessage.Builder builder = RequestMessage.newBuilder();
        builder.setClassName("com.github.zack.zrpc.core.test.demo.ServerUserService");
        builder.setMethodName("getNameById");

        Class<?>[] paramTypes = {Long.class};
        for (Class<?> paramType : paramTypes) {
            builder.addParameterTypes(paramType.getName());
        }

        Object[] parameters = {1L};
        for (Object param : parameters) {
            try {
                builder.addParameters(ProtobufSerializer.serializeObject(param));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        builder.setRequestId(IdUtil.nextId());
        // 发送 RPC 请求并获取结果
        ResponseMessage response = rpcClient.sendRequest(builder.build());
        System.out.println("RPC 结果：" + ProtobufSerializer.deserializeObject(response.getResult()));
        nettyClient.close();

    }

}
