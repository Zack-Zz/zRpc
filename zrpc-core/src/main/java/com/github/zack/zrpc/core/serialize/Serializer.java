package com.github.zack.zrpc.core.serialize;

import com.google.protobuf.ByteString;

import java.io.IOException;

/**
 * Serializer abstraction used by zRpc to encode request parameters and response values.
 *
 * @author zack
 * @since 2026/3/2
 */
public interface Serializer {

    /**
     * Serializes an object into a protobuf byte payload.
     *
     * @param obj object to serialize
     * @return serialized payload
     * @throws IOException when serialization fails
     */
    ByteString serialize(Object obj) throws IOException;

    /**
     * Deserializes a protobuf byte payload into an object.
     *
     * @param byteString serialized payload
     * @return deserialized object
     * @throws IOException when payload parsing fails
     * @throws ClassNotFoundException when payload references an unavailable class
     */
    Object deserialize(ByteString byteString) throws IOException, ClassNotFoundException;
}
