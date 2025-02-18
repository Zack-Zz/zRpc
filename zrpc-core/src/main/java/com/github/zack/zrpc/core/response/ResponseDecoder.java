package com.github.zack.zrpc.core.response;

import com.github.zack.zrpc.core.serializer.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 *
 * @author zack
 * @since 2025/2/18
 */
public class ResponseDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 先读取长度
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex(); // 标记当前读指针
        int length = byteBuf.readInt();

        // 确保数据完整
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
            return;
        }

        // 读取数据
        byte[] data = new byte[length];
        byteBuf.readBytes(data);

        // 反序列化 RpcResponse
        ResponseContext response = deserialize(data, ResponseContext.class);
        list.add(response);
    }

    private <T> T deserialize(byte[] data, Class<T> clazz) {
        return KryoSerializer.deserialize(data, clazz); // 可以换成 Protobuf
    }

}
