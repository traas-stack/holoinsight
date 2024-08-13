/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;


import io.holoinsight.server.common.grpc.GenericRpcCommand;

import io.grpc.SynchronizationContext;
import io.grpc.stub.StreamObserver;
import lombok.Setter;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
public class SafeWriter {
  private final SynchronizationContext sc;
  private boolean closed = false;
  @Setter
  private StreamObserver<GenericRpcCommand> writer;

  public SafeWriter(Thread.UncaughtExceptionHandler h) {
    this.sc = new SynchronizationContext(h);
  }

  public void run(Runnable r) {
    sc.execute(r);
  }

  public void send(GenericRpcCommand cmd) {
    sc.execute(() -> {
      if (!closed) {
        try {
          writer.onNext(cmd);
        } catch (Throwable e) {
          closed = true;
          writer.onError(e);
        }
      }
    });
  }

  public void sendError(Throwable error) {
    sc.execute(() -> {
      if (!closed) {
        closed = true;
        try {
          writer.onError(error);
        } catch (Throwable e) { //
        }
      }
    });
  }

  public void sendComplete() {
    sc.execute(() -> {
      if (!closed) {
        closed = true;
        try {
          writer.onCompleted();
        } catch (Throwable e) {
          // This catch statement is intentionally empty
        }
      }
    });
  }
}
