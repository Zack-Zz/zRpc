package com.github.zack.zrpc.core.logger.jdk;

import com.github.zack.zrpc.core.logger.Logger;

import java.util.logging.Level;

/**
 * @author zack
 * @since 2024/12/25
 */
public class JDKLogger implements Logger {

    private final java.util.logging.Logger logger;

    public JDKLogger(String name) {
        this.logger = java.util.logging.Logger.getLogger(name);
        this.logger.setLevel(Level.FINE);
    }


    @Override
    public void debug(String message, Object... params) {
        logger.log(Level.CONFIG, message);
    }

    @Override
    public void info(String message, Object... params) {
        logger.log(Level.INFO, message);
    }

    @Override
    public void warn(String message, Object... params) {
        logger.log(Level.WARNING, message);
    }

    @Override
    public void error(String message, Object... params) {
        logger.log(Level.SEVERE, message);
    }

    @Override
    public void error(String message, Throwable e, Object... params) {
        logger.log(Level.SEVERE, message, e);
    }
}
