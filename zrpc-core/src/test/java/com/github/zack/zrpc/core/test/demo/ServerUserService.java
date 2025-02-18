package com.github.zack.zrpc.core.test.demo;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zack
 * @since 2025/2/18
 */
public class ServerUserService {

    private final Map<Long, String> userMap;

    public ServerUserService() {
        this.userMap = new HashMap<>();
        userMap.put(1L, "xiao ming");
        userMap.put(2L, "let");
    }


    public String getNameById(Long userId) {
        return userMap.get(userId);
    }
}
