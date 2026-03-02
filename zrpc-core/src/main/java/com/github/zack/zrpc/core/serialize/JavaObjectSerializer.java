package com.github.zack.zrpc.core.serialize;

import com.google.protobuf.ByteString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Java native serialization for compatibility only.
 *
 * @author zack
 * @since 2026/3/2
 */
public class JavaObjectSerializer implements Serializer {

    @Override
    public ByteString serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();
        return ByteString.copyFrom(byteArrayOutputStream.toByteArray());
    }

    @Override
    public Object deserialize(ByteString byteString) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteString.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object obj = objectInputStream.readObject();
        objectInputStream.close();
        return obj;
    }
}
