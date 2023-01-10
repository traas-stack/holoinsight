/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import java.util.function.Consumer;

import io.grpc.stub.StreamObserver;
import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2023/1/10
 *
 * @author xzchaoo
 */
public class GrpcReactorUtils {

  public static <T> Mono<T> observerToMono(Consumer<StreamObserver<T>> c) {
    return Mono.create(sink -> {
      c.accept(new StreamObserver<T>() {
        @Override
        public void onNext(T value) {
          sink.success(value);
        }

        @Override
        public void onError(Throwable t) {
          sink.error(t);
        }

        @Override
        public void onCompleted() {
          sink.success();
        }
      });
    });
  }
}
