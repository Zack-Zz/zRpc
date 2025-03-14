package com.github.zack.zrpc.core.response;

/**
 *
 * @author zack
 * @since 2025/2/17
 */
public class ResponseContext {
    private String requestId;
    private Object result;
    private String error;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
