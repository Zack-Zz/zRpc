package com.github.zack.zrpc.core;

import java.io.Serializable;

/**
 * @author zack
 * @since 2024/12/19
 */
public class TargetNode implements Serializable {

    /**
     * IP
     */
    private String ip;
    /**
     * PORT
     */
    private Integer port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
