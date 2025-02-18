package com.github.zack.zrpc.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author zack
 * @since 2025/2/18
 */
public class ServiceRegistry {

    // 存储本地服务实例
    private static final Map<Class<?>, Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * 注册服务实例
     */
    public static void registerService(Class<?> serviceClass, Object serviceInstance) {
        serviceMap.put(serviceClass, serviceInstance);
    }

    /**
     * 获取服务实例
     */
    public static Object getService(Class<?> serviceClass) {
        return serviceMap.get(serviceClass);
    }

}
