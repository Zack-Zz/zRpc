package com.github.zack.zrpc.core.dispatcher;

import com.github.zack.zrpc.core.proto.RequestMessage;
import com.github.zack.zrpc.core.request.RequestContext;

import java.util.Map;
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

    private Map<String, ResponseDispatcher> pendingSyncRequests = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, RequestFuture> pendingRequests = new ConcurrentHashMap<>();

    public RequestFuture sendRequest(RequestMessage request) {
        RequestFuture requestFuture = new RequestFuture();
        pendingRequests.put(request.getRequestId(), requestFuture);
        return requestFuture;
    }

    public RequestFuture remove(String requestId) {
        return pendingRequests.remove(requestId);
    }
}
