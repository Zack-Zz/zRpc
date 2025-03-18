package com.github.zack.zrpc.core.handler;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author zack
 * @since 2025/3/18
 */
public class MetricHandler extends ChannelDuplexHandler {

    private AtomicLong totalConnectedConnections = new AtomicLong(0);

    private AtomicLong readDataLength = new AtomicLong();

    private AtomicLong readTimeoutCauses = new AtomicLong();

    {
        MetricRegistry metricRegistry = new MetricRegistry();

        metricRegistry.register("totalConnectedConnections", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return totalConnectedConnections.longValue();
            }
        });

        metricRegistry.register("readDataLength", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return readDataLength.longValue();
            }
        });

        metricRegistry.register("readTimeoutCauses", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return readDataLength.longValue();
            }
        });


        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        consoleReporter.start(10, TimeUnit.SECONDS);

        JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
        jmxReporter.start();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        totalConnectedConnections.incrementAndGet();

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        totalConnectedConnections.decrementAndGet();
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            int length = buf.readableBytes();
            readDataLength.addAndGet(length);
        }

        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof ReadTimeoutException) {
            readTimeoutCauses.incrementAndGet();
        }

        super.exceptionCaught(ctx, cause);
    }
}
