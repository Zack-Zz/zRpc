package com.github.zack.zrpc.core.logger;

import com.github.zack.zrpc.core.logger.jdk.JDKLogger;

/**
 *
 * @author zack
 * @since 2024/12/26
 */
public class LoggerFactory {


    public static Logger getLogger(Class<?> clazz) {
        return new JDKLogger(clazz.getName());
    }


}
