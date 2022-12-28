/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import io.grpc.Context;
import io.grpc.ServerStreamTracer;
import lombok.Getter;

/**
 * <p>created at 2022/3/18
 *
 * @author sw1136562366
 */
public class TrafficTracer extends ServerStreamTracer {
    /** Constant <code>KEY</code> */
    public static final Context.Key<TrafficTracer> KEY = Context.key("TrafficTracer");
    @Getter
    private long inboundWireSize;
    @Getter
    private long inboundUncompressedSize;

    /** {@inheritDoc} */
    @Override
    public Context filterContext(Context context) {
        return context.withValue(KEY, this);
    }

    /**
     * {@inheritDoc}
     *
     * TCP 传输入流量
     */
    @Override
    public void inboundWireSize(long bytes) {
        this.inboundWireSize += bytes;
    }

    /**
     * {@inheritDoc}
     *
     * 解压后的入流量
     */
    @Override
    public void inboundUncompressedSize(long bytes) {
        this.inboundUncompressedSize += bytes;
    }
}
