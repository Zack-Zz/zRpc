package com.github.zack.zrpc.core.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 *
 * @author zack
 * @since 2025/3/6
 */
public class StreamFrameDecoder extends LengthFieldBasedFrameDecoder {

    public StreamFrameDecoder() {
        // 设置initialBytesToStrip,因为设置了长度字节数，所以真正的数据要跳过长度字节数
        super(10240, 0, 2, 0, 2);
    }
}
