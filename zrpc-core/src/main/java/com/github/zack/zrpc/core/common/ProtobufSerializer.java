package com.github.zack.zrpc.core.common;

import com.google.protobuf.ByteString;

import java.io.*;

/**
 *
 * @author zack
 * @since 2025/3/9
 */
public class ProtobufSerializer {
    private ProtobufSerializer() {
    }

    public static Object deserializeObject(ByteString byteString) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteString.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        return obj;
    }


    public static ByteString serializeObject(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
        return ByteString.copyFrom(byteArrayOutputStream.toByteArray());
    }
}
