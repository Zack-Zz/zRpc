package com.github.zack.zrpc.core.handler;

import com.github.zack.zrpc.core.ServiceRegistry;
import com.github.zack.zrpc.core.common.ProtobufSerializer;
import com.github.zack.zrpc.core.proto.RequestMessage;
import com.github.zack.zrpc.core.proto.ResponseMessage;
import com.github.zack.zrpc.core.response.ResponseContext;
import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * @author zack
 * @since 2024/12/19
 */
public class ServerHandler extends SimpleChannelInboundHandler<RequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage) throws Exception {
        ResponseMessage.Builder builder = ResponseMessage.newBuilder();
        builder.setRequestId(requestMessage.getRequestId());

        try {
            Object result = invoke(requestMessage);
            builder.setResult(ProtobufSerializer.serializeObject(result));
        } catch (Exception e) {
            builder.setError(e.getMessage());
        }

        channelHandlerContext.writeAndFlush(builder.build());
    }

    private Object invoke(RequestMessage requestMessage) throws Exception {
        String className = requestMessage.getClassName();
        String methodName = requestMessage.getMethodName();
        Class<?> clazz = Class.forName(className);
        Object service = ServiceRegistry.getService(clazz);

        ProtocolStringList parameterTypesList = requestMessage.getParameterTypesList();
        Class<?>[] clazzArray = new Class[parameterTypesList.size()];
        for (int i = 0; i < parameterTypesList.size(); i++) {
            try {
                clazzArray[i] = Class.forName(parameterTypesList.get(i));
            } catch (ClassNotFoundException e) {
                clazzArray[i] = Object.class;
            }
        }

        List<ByteString> parametersList = requestMessage.getParametersList();
        Object[] parameters = new Object[parametersList.size()];
        for (int i = 0; i < parametersList.size(); i++) {
            Object obj = ProtobufSerializer.deserializeObject(parametersList.get(i));

            Class<?> aClass = clazzArray[i];
            if (aClass == Long.class) {
                parameters[i] = Long.parseLong(obj.toString());
            } else if (aClass == Double.class) {
                parameters[i] = Double.parseDouble(obj.toString());
            } else if (aClass == Float.class) {
                parameters[i] = Float.parseFloat(obj.toString());
            }else {
                parameters[i] = obj;
            }
        }

        return clazz.getMethod(methodName, clazzArray).invoke(service, parameters);
    }


}
