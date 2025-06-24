package com.github.zack.zrpc.core.request;

/**
 *
 * @author zack
 * @since 2025/2/17
 */
public class RequestContext {

    /**
     * request id
     */
    private String requestId;

    /**
     * server class name
     */
    private String className;
    /**
     * server method name
     */
    private String methodName;
    /**
     * server method parameter types
     */
    private Class<?>[] parameterTypes;
    /**
     * server method parameters
     */
    private Object[] parameters;


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
