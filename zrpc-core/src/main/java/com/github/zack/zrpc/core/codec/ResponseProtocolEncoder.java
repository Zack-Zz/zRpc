package com.github.zack.zrpc.core.codec;

import com.github.zack.zrpc.core.proto.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 *
 * @author zack
 * @since 2025/3/6
 */
public class ResponseProtocolEncoder extends MessageToMessageEncoder<ResponseMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ResponseMessage responseContext, List<Object> out) throws Exception {
        ByteBuf buffer = channelHandlerContext.alloc().buffer();

        byte[] bytes = responseContext.toByteArray();

        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);

        out.add(buffer);
    }

}
