package com.github.zack.zrpc.core.request;

/**
 *
 * @author zack
 * @since 2025/2/17
 */
public class RequestContext {

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

}
