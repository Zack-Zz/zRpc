package com.github.zack.zrpc.core.common;

import com.github.zack.zrpc.core.serialize.Serializer;
import com.github.zack.zrpc.core.serialize.SerializerFactory;
import com.google.protobuf.ByteString;

import java.io.IOException;

/**
 *
 * @author zack
 * @since 2025/3/9
 */
public class ProtobufSerializer {
    private static final Serializer DEFAULT_SERIALIZER = SerializerFactory.createDefault();

    private ProtobufSerializer() {
    }

    public static Object deserializeObject(ByteString byteString) throws IOException, ClassNotFoundException {
        return DEFAULT_SERIALIZER.deserialize(byteString);
    }


    public static ByteString serializeObject(Object obj) throws IOException {
        return DEFAULT_SERIALIZER.serialize(obj);
    }
}
