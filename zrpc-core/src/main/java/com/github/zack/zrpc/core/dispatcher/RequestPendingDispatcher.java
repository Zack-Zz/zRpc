package com.github.zack.zrpc.core.dispatcher;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author zack
 * @since 2025/3/9
 */
public class RequestPendingDispatcher {

    public static RequestPendingDispatcher singleInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final RequestPendingDispatcher instance = new RequestPendingDispatcher();
    }

    private final ConcurrentHashMap<String, RequestFuture> pendingRequests = new ConcurrentHashMap<>();

    public RequestFuture register(String requestId) {
        RequestFuture requestFuture = new RequestFuture(requestId);
        RequestFuture previous = pendingRequests.putIfAbsent(requestId, requestFuture);
        if (previous != null) {
            throw new IllegalStateException("Duplicate requestId detected: " + requestId);
        }
        return requestFuture;
    }

    public RequestFuture remove(String requestId) {
        return pendingRequests.remove(requestId);
    }

    public boolean remove(String requestId, RequestFuture requestFuture) {
        return pendingRequests.remove(requestId, requestFuture);
    }

    public int size() {
        return pendingRequests.size();
    }

    public void clear() {
        pendingRequests.clear();
    }
}
