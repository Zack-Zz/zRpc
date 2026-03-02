package com.github.zack.zrpc.core.handler;

import com.github.zack.zrpc.core.ServiceRegistry;
import com.github.zack.zrpc.core.common.ProtobufSerializer;
import com.github.zack.zrpc.core.logger.Logger;
import com.github.zack.zrpc.core.logger.LoggerFactory;
import com.github.zack.zrpc.core.proto.RequestMessage;
import com.github.zack.zrpc.core.proto.ResponseMessage;
import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * @author zack
 * @since 2024/12/19
 */
public class ServerHandler extends SimpleChannelInboundHandler<RequestMessage> {

    private Logger logger = LoggerFactory.getLogger(ServerHandler.class);

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

        if (channelHandlerContext.channel().isWritable()) {
            channelHandlerContext.writeAndFlush(builder.build());
        } else {
            logger.error("msg dropped. reqId:{}. msg: {}", requestMessage.getRequestId(), builder.build());
        }
    }

    private Object invoke(RequestMessage requestMessage) throws Exception {
        String className = requestMessage.getClassName();
        String methodName = requestMessage.getMethodName();
        Class<?> clazz = Class.forName(className);
        Object service = ServiceRegistry.getService(clazz);
        if (service == null) {
            throw new IllegalStateException("No service registered for class: " + className);
        }

        ProtocolStringList parameterTypesList = requestMessage.getParameterTypesList();
        Class<?>[] clazzArray = new Class[parameterTypesList.size()];
        for (int i = 0; i < parameterTypesList.size(); i++) {
            clazzArray[i] = resolveParameterType(parameterTypesList.get(i));
        }

        List<ByteString> parametersList = requestMessage.getParametersList();
        Object[] parameters = new Object[parametersList.size()];
        for (int i = 0; i < parametersList.size(); i++) {
            Object obj = ProtobufSerializer.deserializeObject(parametersList.get(i));
            parameters[i] = convertParameter(obj, clazzArray[i]);
        }

        return clazz.getMethod(methodName, clazzArray).invoke(service, parameters);
    }

    private Class<?> resolveParameterType(String typeName) throws ClassNotFoundException {
        if ("byte".equals(typeName)) {
            return byte.class;
        }
        if ("short".equals(typeName)) {
            return short.class;
        }
        if ("int".equals(typeName)) {
            return int.class;
        }
        if ("long".equals(typeName)) {
            return long.class;
        }
        if ("float".equals(typeName)) {
            return float.class;
        }
        if ("double".equals(typeName)) {
            return double.class;
        }
        if ("boolean".equals(typeName)) {
            return boolean.class;
        }
        if ("char".equals(typeName)) {
            return char.class;
        }
        return Class.forName(typeName);
    }

    private Object convertParameter(Object value, Class<?> expectedType) {
        if (value == null) {
            if (expectedType.isPrimitive()) {
                throw new IllegalArgumentException("Null value is not allowed for primitive type: " + expectedType.getName());
            }
            return null;
        }

        Class<?> targetType = wrapPrimitive(expectedType);
        if (targetType.isInstance(value) || targetType == Object.class) {
            return value;
        }

        if (Number.class.isAssignableFrom(targetType) && value instanceof Number) {
            Number number = (Number) value;
            if (targetType == Byte.class) {
                return number.byteValue();
            }
            if (targetType == Short.class) {
                return number.shortValue();
            }
            if (targetType == Integer.class) {
                return number.intValue();
            }
            if (targetType == Long.class) {
                return number.longValue();
            }
            if (targetType == Float.class) {
                return number.floatValue();
            }
            if (targetType == Double.class) {
                return number.doubleValue();
            }
        }

        if (targetType == String.class) {
            return String.valueOf(value);
        }
        if (targetType == Character.class && value instanceof String && ((String) value).length() == 1) {
            return ((String) value).charAt(0);
        }

        throw new IllegalArgumentException(
                "Cannot convert parameter value of type " + value.getClass().getName() +
                        " to expected type " + expectedType.getName());
    }

    private Class<?> wrapPrimitive(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (type == byte.class) {
            return Byte.class;
        }
        if (type == short.class) {
            return Short.class;
        }
        if (type == int.class) {
            return Integer.class;
        }
        if (type == long.class) {
            return Long.class;
        }
        if (type == float.class) {
            return Float.class;
        }
        if (type == double.class) {
            return Double.class;
        }
        if (type == boolean.class) {
            return Boolean.class;
        }
        if (type == char.class) {
            return Character.class;
        }
        return type;
    }


}
