package com.github.zack.zrpc.core.exception;

/**
 * Exception thrown when server-side business execution returns an error.
 *
 * @author zack
 * @since 2026/3/2
 */
public class RpcServerException extends RpcException {

    /**
     * Creates a server exception with details.
     *
     * @param message server-side error details
     */
    public RpcServerException(String message) {
        super(message);
    }
}
