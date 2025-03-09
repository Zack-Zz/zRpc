package com.github.zack.zrpc.core.codec;

import com.github.zack.zrpc.core.proto.ResponseMessage;
import com.github.zack.zrpc.core.response.ResponseContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 *
 * @author zack
 * @since 2025/3/7
 */
public class ResponseProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        int length = byteBuf.readInt();

        byte[] data = new byte[length];
        byteBuf.readBytes(data);

        ResponseMessage responseMessage = ResponseMessage.parseFrom(data);
        out.add(responseMessage);

    }

}
