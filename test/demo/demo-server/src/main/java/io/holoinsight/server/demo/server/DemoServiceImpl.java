package io.holoinsight.server.demo.server;

import io.holoinsight.server.demo.server.grpc.BarRequest;
import io.holoinsight.server.demo.server.grpc.BarResponse;
import io.holoinsight.server.demo.server.grpc.DemoServiceGrpc;
import io.holoinsight.server.demo.server.grpc.FooRequest;
import io.holoinsight.server.demo.server.grpc.FooResponse;

/**
 * <p>
 * created at 2023/4/5
 *
 * @author xzchaoo
 */
public class DemoServiceImpl extends DemoServiceGrpc.DemoServiceImplBase {
  @Override
  public void foo(FooRequest request, io.grpc.stub.StreamObserver<FooResponse> o) {
    o.onNext(FooResponse.getDefaultInstance());
    o.onCompleted();
  }

  @Override
  public void bar(BarRequest request, io.grpc.stub.StreamObserver<BarResponse> o) {
    o.onNext(BarResponse.getDefaultInstance());
    o.onCompleted();
  }
}
