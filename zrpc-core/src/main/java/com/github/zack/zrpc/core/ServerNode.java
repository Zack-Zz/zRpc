package com.github.zack.zrpc.core;

import java.io.Serializable;

/**
 * @author zack
 * @since 2024/12/20
 */
public class ServerNode implements Serializable {
    private static final long serialVersionUID = -2792404442993288490L;

    /**
     * port
     */
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
