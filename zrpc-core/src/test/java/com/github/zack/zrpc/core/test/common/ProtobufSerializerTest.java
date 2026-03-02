package com.github.zack.zrpc.core.test.common;

import com.github.zack.zrpc.core.common.ProtobufSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProtobufSerializerTest {

    @Test
    public void shouldSerializeAndDeserializePrimitiveValues() throws Exception {
        assertThat(ProtobufSerializer.deserializeObject(ProtobufSerializer.serializeObject("zrpc"))).isEqualTo("zrpc");
        assertThat(ProtobufSerializer.deserializeObject(ProtobufSerializer.serializeObject(123L))).isEqualTo(123L);
        assertThat(ProtobufSerializer.deserializeObject(ProtobufSerializer.serializeObject(42))).isEqualTo(42);
        assertThat(ProtobufSerializer.deserializeObject(ProtobufSerializer.serializeObject(true))).isEqualTo(true);
        assertThat(ProtobufSerializer.deserializeObject(ProtobufSerializer.serializeObject(12.5d))).isEqualTo(12.5d);
        assertThat(ProtobufSerializer.deserializeObject(ProtobufSerializer.serializeObject(null))).isNull();
    }

    @Test
    public void shouldRejectUnsupportedObjectByDefaultSafeSerializer() {
        assertThatThrownBy(() -> ProtobufSerializer.serializeObject(new Date()))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Unsupported type");
    }
}
