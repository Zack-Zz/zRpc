package com.github.zack.zrpc.core.serialize;

import com.google.protobuf.ByteString;

import java.io.IOException;

public interface Serializer {
    ByteString serialize(Object obj) throws IOException;

    Object deserialize(ByteString byteString) throws IOException, ClassNotFoundException;
}
