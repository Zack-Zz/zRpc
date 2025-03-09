package com.github.zack.zrpc.core.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author zack
 * @since 2025/3/9
 */
public class IdUtil {

    private static final AtomicInteger ID = new AtomicInteger();

    private IdUtil() {
    }

    public static Integer next() {
        return ID.incrementAndGet();
    }

    public static String nextId() {
        return String.valueOf(next());
    }
}
