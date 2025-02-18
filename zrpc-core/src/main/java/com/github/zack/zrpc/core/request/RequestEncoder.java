package com.github.zack.zrpc.core.request;

import com.github.zack.zrpc.core.serializer.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * @author zack
 * @since 2025/2/18
 */
public class RequestEncoder extends MessageToByteEncoder<RequestContext> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RequestContext requestContext, ByteBuf byteBuf) throws Exception {
        byte[] data = serialize(requestContext);

        byteBuf.writeInt(data.length);

        byteBuf.writeBytes(data);
    }

    private byte[] serialize(RequestContext request) {
        return KryoSerializer.serialize(request);
    }

}
