package com.github.zack.zrpc.core;

import com.github.zack.zrpc.core.request.RequestContext;
import com.github.zack.zrpc.core.response.ResponseContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author zack
 * @since 2024/12/19
 */
public class ServerHandler extends SimpleChannelInboundHandler<RequestContext> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestContext requestContext) throws Exception {
        ResponseContext response = new ResponseContext();
        response.setRequestId(requestContext.getRequestId());

        try {
            // 反射调用目标方法
            Object result = invoke(requestContext);
            response.setResult(result);
        } catch (Exception e) {
            response.setError(e.getMessage());
        }

        // 发送响应（交给 RpcResponseEncoder 编码）
        channelHandlerContext.writeAndFlush(response);
    }

    private Object invoke(RequestContext request) throws Exception {
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Class<?> clazz = Class.forName(className);
        Object service = ServiceRegistry.getService(clazz);
        return clazz.getMethod(methodName, request.getParameterTypes()).invoke(service, request.getParameters());
    }
}
