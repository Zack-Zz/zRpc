package com.github.zack.zrpc.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local in-process registry that maps service types to implementation instances.
 *
 * @author zack
 * @since 2025/2/18
 */
public class ServiceRegistry {

    // Stores local service implementation instances.
    private static final Map<Class<?>, Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * Registers a service implementation instance.
     *
     * @param serviceClass service contract type
     * @param serviceInstance service implementation instance
     */
    public static void registerService(Class<?> serviceClass, Object serviceInstance) {
        serviceMap.put(serviceClass, serviceInstance);
    }

    /**
     * Returns a registered service implementation by contract type.
     *
     * @param serviceClass service contract type
     * @return registered service instance or {@code null} when not found
     */
    public static Object getService(Class<?> serviceClass) {
        return serviceMap.get(serviceClass);
    }

}
