package com.github.zack.zrpc.core.codec;

import com.github.zack.zrpc.core.proto.RequestMessage;
import com.github.zack.zrpc.core.request.RequestContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 *
 * @author zack
 * @since 2025/3/6
 */
public class RequestProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        int length = byteBuf.readInt();

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        RequestMessage requestMessage = RequestMessage.parseFrom(bytes);
        out.add(requestMessage);

    }

}
