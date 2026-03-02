package com.github.zack.zrpc.core.dispatcher;

import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory registry of pending RPC requests waiting for responses.
 *
 * @author zack
 * @since 2025/3/9
 */
public class RequestPendingDispatcher {

    /**
     * Returns singleton dispatcher instance.
     *
     * @return singleton dispatcher
     */
    public static RequestPendingDispatcher singleInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final RequestPendingDispatcher instance = new RequestPendingDispatcher();
    }

    private final ConcurrentHashMap<String, RequestFuture> pendingRequests = new ConcurrentHashMap<>();

    /**
     * Registers a pending request by id.
     *
     * @param requestId request identifier
     * @return future associated with the request
     * @throws IllegalStateException when duplicate request id is detected
     */
    public RequestFuture register(String requestId) {
        RequestFuture requestFuture = new RequestFuture(requestId);
        RequestFuture previous = pendingRequests.putIfAbsent(requestId, requestFuture);
        if (previous != null) {
            throw new IllegalStateException("Duplicate requestId detected: " + requestId);
        }
        return requestFuture;
    }

    /**
     * Removes and returns the future by request id.
     *
     * @param requestId request identifier
     * @return removed future or {@code null} when absent
     */
    public RequestFuture remove(String requestId) {
        return pendingRequests.remove(requestId);
    }

    /**
     * Removes the given mapping only when key and value both match.
     *
     * @param requestId request identifier
     * @param requestFuture expected future instance
     * @return {@code true} when mapping is removed
     */
    public boolean remove(String requestId, RequestFuture requestFuture) {
        return pendingRequests.remove(requestId, requestFuture);
    }

    /**
     * Returns current number of pending requests.
     *
     * @return pending request count
     */
    public int size() {
        return pendingRequests.size();
    }

    /**
     * Clears all pending request mappings.
     */
    public void clear() {
        pendingRequests.clear();
    }
}
