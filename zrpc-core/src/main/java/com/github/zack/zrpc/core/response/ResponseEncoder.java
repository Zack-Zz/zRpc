package com.github.zack.zrpc.core.response;

import com.github.zack.zrpc.core.serializer.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * @author zack
 * @since 2025/2/18
 */
public class ResponseEncoder extends MessageToByteEncoder<ResponseContext> {


    private byte[] serialize(ResponseContext response) {
        return KryoSerializer.serialize(response);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ResponseContext responseContext, ByteBuf byteBuf) throws Exception {
        byte[] data = serialize(responseContext);
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }

}
