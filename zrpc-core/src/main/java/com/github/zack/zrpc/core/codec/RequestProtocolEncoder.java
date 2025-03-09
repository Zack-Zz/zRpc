package com.github.zack.zrpc.core.codec;

import com.github.zack.zrpc.core.proto.RequestMessage;
import com.github.zack.zrpc.core.request.RequestContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 *
 * @author zack
 * @since 2025/3/7
 */
public class RequestProtocolEncoder extends MessageToMessageEncoder<RequestMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage, List<Object> out) throws Exception {
        ByteBuf byteBuf = channelHandlerContext.alloc().buffer();

        byte[] bytes = requestMessage.toByteArray();

        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        out.add(byteBuf);
    }
}
