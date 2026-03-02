package com.github.zack.zrpc.core.exception;

/**
 * Base runtime exception for all RPC errors exposed by zRpc.
 *
 * @author zack
 * @since 2026/3/2
 */
public class RpcException extends RuntimeException {

    /**
     * Creates a new RPC exception with a message.
     *
     * @param message error description
     */
    public RpcException(String message) {
        super(message);
    }

    /**
     * Creates a new RPC exception with a message and root cause.
     *
     * @param message error description
     * @param cause root cause
     */
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
