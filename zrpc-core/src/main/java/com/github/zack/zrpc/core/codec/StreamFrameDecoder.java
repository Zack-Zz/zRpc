package com.github.zack.zrpc.core.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 *
 * @author zack
 * @since 2025/3/6
 */
public class StreamFrameDecoder extends LengthFieldBasedFrameDecoder {

    public StreamFrameDecoder() {
        super(10240, 0, 2, 0, 2);
    }
}
