package com.github.zack.zrpc.core.exception;

/**
 * Exception thrown when an RPC request exceeds its configured timeout.
 *
 * @author zack
 * @since 2026/3/2
 */
public class RpcTimeoutException extends RpcException {

    /**
     * Creates a timeout exception with details.
     *
     * @param message timeout details
     */
    public RpcTimeoutException(String message) {
        super(message);
    }
}
