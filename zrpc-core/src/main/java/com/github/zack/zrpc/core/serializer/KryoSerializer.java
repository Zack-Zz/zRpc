package com.github.zack.zrpc.core.serializer;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;

import java.io.ByteArrayOutputStream;

/**
 *
 * @author zack
 * @since 2025/2/18
 */
public class KryoSerializer {

    private static final Kryo kryo;

    static {
        kryo = new Kryo();
    }


    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        kryo.register(clazz);

        try (Input input = new Input(data)) {
            return kryo.readObject(input, clazz);
        }
    }

    public static <T> byte[] serialize(T t) {
        ByteArrayOutputStream bass = new ByteArrayOutputStream();

        try (Output output = new Output(bass)) {
            kryo.writeObject(output, t);
        }
        return bass.toByteArray();
    }
}
