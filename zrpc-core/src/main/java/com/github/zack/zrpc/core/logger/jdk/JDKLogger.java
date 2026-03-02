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
        logger.log(Level.CONFIG, format(message, params));
    }

    @Override
    public void info(String message, Object... params) {
        logger.log(Level.INFO, format(message, params));
    }

    @Override
    public void warn(String message, Object... params) {
        logger.log(Level.WARNING, format(message, params));
    }

    @Override
    public void error(String message, Object... params) {
        logger.log(Level.SEVERE, format(message, params));
    }

    @Override
    public void error(String message, Throwable e, Object... params) {
        logger.log(Level.SEVERE, format(message, params), e);
    }

    private String format(String message, Object... params) {
        if (message == null) {
            return null;
        }
        if (params == null || params.length == 0) {
            return message;
        }

        StringBuilder builder = new StringBuilder();
        int cursor = 0;
        int paramIndex = 0;
        while (cursor < message.length()) {
            int holderIndex = message.indexOf("{}", cursor);
            if (holderIndex < 0) {
                builder.append(message.substring(cursor));
                break;
            }
            builder.append(message, cursor, holderIndex);
            if (paramIndex < params.length) {
                builder.append(params[paramIndex++]);
            } else {
                builder.append("{}");
            }
            cursor = holderIndex + 2;
        }
        if (paramIndex < params.length) {
            for (int i = paramIndex; i < params.length; i++) {
                builder.append(" [").append(params[i]).append(']');
            }
        }
        return builder.toString();
    }
}
