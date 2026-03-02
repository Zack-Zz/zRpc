package com.github.zack.zrpc.core.exception;

/**
 * Exception representing network transport failures in RPC communication.
 *
 * @author zack
 * @since 2026/3/2
 */
public class RpcTransportException extends RpcException {

    /**
     * Creates a transport exception with a message.
     *
     * @param message transport failure details
     */
    public RpcTransportException(String message) {
        super(message);
    }

    /**
     * Creates a transport exception with a message and root cause.
     *
     * @param message transport failure details
     * @param cause root cause
     */
    public RpcTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
