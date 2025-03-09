package com.github.zack.zrpc.core.codec;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 *
 * @author zack
 * @since 2025/3/6
 */
public class StreamFrameEncoder extends LengthFieldPrepender {
    public StreamFrameEncoder() {
        super(2);
    }
}
