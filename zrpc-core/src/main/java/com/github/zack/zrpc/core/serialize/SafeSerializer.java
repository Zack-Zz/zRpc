package com.github.zack.zrpc.core.serialize;

import com.google.protobuf.ByteString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * Safe serializer supporting a bounded set of value types.
 *
 * @author zack
 * @since 2026/3/2
 */
public class SafeSerializer implements Serializer {

    private static final byte TYPE_NULL = 0;
    private static final byte TYPE_STRING = 1;
    private static final byte TYPE_LONG = 2;
    private static final byte TYPE_INTEGER = 3;
    private static final byte TYPE_DOUBLE = 4;
    private static final byte TYPE_FLOAT = 5;
    private static final byte TYPE_BOOLEAN = 6;
    private static final byte TYPE_SHORT = 7;
    private static final byte TYPE_BYTE = 8;
    private static final byte TYPE_CHARACTER = 9;
    private static final byte TYPE_BIG_DECIMAL = 10;
    private static final byte TYPE_BIG_INTEGER = 11;
    private static final byte TYPE_BYTE_ARRAY = 12;

    @Override
    public ByteString serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(byteArrayOutputStream);
        writeValue(output, obj);
        output.flush();
        return ByteString.copyFrom(byteArrayOutputStream.toByteArray());
    }

    @Override
    public Object deserialize(ByteString byteString) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteString.toByteArray());
        DataInputStream input = new DataInputStream(byteArrayInputStream);
        return readValue(input);
    }

    private void writeValue(DataOutputStream output, Object value) throws IOException {
        if (value == null) {
            output.writeByte(TYPE_NULL);
            return;
        }

        if (value instanceof String) {
            output.writeByte(TYPE_STRING);
            writeUtf8(output, (String) value);
            return;
        }
        if (value instanceof Long) {
            output.writeByte(TYPE_LONG);
            output.writeLong((Long) value);
            return;
        }
        if (value instanceof Integer) {
            output.writeByte(TYPE_INTEGER);
            output.writeInt((Integer) value);
            return;
        }
        if (value instanceof Double) {
            output.writeByte(TYPE_DOUBLE);
            output.writeDouble((Double) value);
            return;
        }
        if (value instanceof Float) {
            output.writeByte(TYPE_FLOAT);
            output.writeFloat((Float) value);
            return;
        }
        if (value instanceof Boolean) {
            output.writeByte(TYPE_BOOLEAN);
            output.writeBoolean((Boolean) value);
            return;
        }
        if (value instanceof Short) {
            output.writeByte(TYPE_SHORT);
            output.writeShort((Short) value);
            return;
        }
        if (value instanceof Byte) {
            output.writeByte(TYPE_BYTE);
            output.writeByte((Byte) value);
            return;
        }
        if (value instanceof Character) {
            output.writeByte(TYPE_CHARACTER);
            output.writeChar((Character) value);
            return;
        }
        if (value instanceof BigDecimal) {
            output.writeByte(TYPE_BIG_DECIMAL);
            writeUtf8(output, value.toString());
            return;
        }
        if (value instanceof BigInteger) {
            output.writeByte(TYPE_BIG_INTEGER);
            writeUtf8(output, value.toString());
            return;
        }
        if (value instanceof byte[]) {
            output.writeByte(TYPE_BYTE_ARRAY);
            byte[] bytes = (byte[]) value;
            output.writeInt(bytes.length);
            output.write(bytes);
            return;
        }

        throw new IOException("Unsupported type for safe serializer: " + value.getClass().getName());
    }

    private Object readValue(DataInputStream input) throws IOException {
        byte type = input.readByte();
        switch (type) {
            case TYPE_NULL:
                return null;
            case TYPE_STRING:
                return readUtf8(input);
            case TYPE_LONG:
                return input.readLong();
            case TYPE_INTEGER:
                return input.readInt();
            case TYPE_DOUBLE:
                return input.readDouble();
            case TYPE_FLOAT:
                return input.readFloat();
            case TYPE_BOOLEAN:
                return input.readBoolean();
            case TYPE_SHORT:
                return input.readShort();
            case TYPE_BYTE:
                return input.readByte();
            case TYPE_CHARACTER:
                return input.readChar();
            case TYPE_BIG_DECIMAL:
                return new BigDecimal(readUtf8(input));
            case TYPE_BIG_INTEGER:
                return new BigInteger(readUtf8(input));
            case TYPE_BYTE_ARRAY:
                int length = input.readInt();
                byte[] bytes = new byte[length];
                input.readFully(bytes);
                return bytes;
            default:
                throw new IOException("Unknown serialized type code: " + type);
        }
    }

    private void writeUtf8(DataOutputStream output, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        output.writeInt(bytes.length);
        output.write(bytes);
    }

    private String readUtf8(DataInputStream input) throws IOException {
        int length = input.readInt();
        byte[] bytes = new byte[length];
        input.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
