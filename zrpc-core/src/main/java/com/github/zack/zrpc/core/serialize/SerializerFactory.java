package com.github.zack.zrpc.core.serialize;

import com.github.zack.zrpc.core.logger.Logger;
import com.github.zack.zrpc.core.logger.LoggerFactory;

import java.util.Locale;

public class SerializerFactory {

    public static final String SERIALIZER_PROPERTY = "zrpc.serializer";
    private static final String JAVA_SERIALIZER = "java";
    private static final Logger LOGGER = LoggerFactory.getLogger(SerializerFactory.class);

    private SerializerFactory() {
    }

    public static Serializer createDefault() {
        String configured = System.getProperty(SERIALIZER_PROPERTY, "safe");
        if (configured == null) {
            return new SafeSerializer();
        }

        String normalized = configured.trim().toLowerCase(Locale.ROOT);
        if (JAVA_SERIALIZER.equals(normalized)) {
            LOGGER.warn(
                    "Using java native serialization. This is not recommended for untrusted input. " +
                            "Set -" + SERIALIZER_PROPERTY + "=safe to use the safe serializer.");
            return new JavaObjectSerializer();
        }

        return new SafeSerializer();
    }
}
