package com.github.zack.zrpc.core.logger;

/**
 * @author zack
 * @since 2024/12/25
 */
public interface Logger {

    void debug(String message, Object... params);

    void info(String message, Object... params);

    void warn(String message, Object... params);

    void error(String message, Object... params);

    void error(String message, Throwable e, Object... params);
}
