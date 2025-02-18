package com.github.zack.zrpc.core.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 *
 * @author zack
 * @since 2025/2/17
 */
public class RequestDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        // 检查是否有足够的数据读取
        if (byteBuf.readableBytes() < 4) {
            return;
        }

        // 读取请求长度
        int length = byteBuf.readInt();
        if (byteBuf.readableBytes() < length) {
            return;
        }

        // 读取请求数据
        byte[] data = new byte[length];
        byteBuf.readBytes(data);

        // 反序列化成 RpcRequest 对象
        RequestContext request = deserialize(data, RequestContext.class);
        list.add(request);
    }

    private <T> T deserialize(byte[] data, Class<T> clazz) {
        // 这里可以用 Kryo、Protobuf 或 JSON 反序列化
        return KryoSerializer.deserialize(data, clazz);
    }
}
