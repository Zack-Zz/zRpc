package com.github.zack.zrpc.core;

import java.io.Serializable;

/**
 * @author zack
 * @since 2024/12/19
 */
public class TargetNode implements Serializable {

    private static final long serialVersionUID = 7958362374873724453L;

    /**
     * IP
     */
    private String host;
    /**
     * PORT
     */
    private Integer port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "TargetNode{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
