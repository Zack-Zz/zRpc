package com.github.zack.zrpc.core.test.logger.jdk;

import com.github.zack.zrpc.core.logger.Logger;
import com.github.zack.zrpc.core.logger.jdk.JDKLogger;
import org.junit.jupiter.api.Test;

/**
 * @author zack
 * @since 2024/12/25
 */
public class JDKLoggerTest {

    private final Logger logger = new JDKLogger(JDKLoggerTest.class.getName());

    @Test
    public void testDebug() {
        logger.debug("debug message test");
    }

    @Test
    public void testInfo() {
        logger.info("info message test");
    }

    @Test
    public void testWarn() {
        logger.warn("warn message test");
    }

    @Test
    public void testError() {
        logger.error("error message test",new RuntimeException("runtime ex"));
    }


}
