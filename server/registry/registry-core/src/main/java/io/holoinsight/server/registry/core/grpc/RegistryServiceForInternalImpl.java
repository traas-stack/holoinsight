/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import java.util.Objects;

import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.GrpcUtils;
import io.holoinsight.server.common.grpc.GenericData;
import io.holoinsight.server.common.grpc.GenericDataBatch;
import io.holoinsight.server.registry.core.cluster.Handler;
import io.holoinsight.server.registry.core.cluster.HandlerRegistry;
import io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest;
import io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse;
import io.holoinsight.server.registry.grpc.internal.RegistryServiceForInternalGrpc;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.protobuf.Empty;

/**
 * <p>
 * created at 2022/3/12
 *
 * @author xzchaoo
 */
public class RegistryServiceForInternalImpl
    extends RegistryServiceForInternalGrpc.RegistryServiceForInternalImplBase {

  // TODO
  private final HandlerRegistry registry;
  @Autowired
  private BiStreamService biStreamService;

  public RegistryServiceForInternalImpl(HandlerRegistry registry) {
    this.registry = Objects.requireNonNull(registry);
  }

  @Override
  public void sendEvents(GenericDataBatch batch, StreamObserver<Empty> o) {
    GrpcUtils.setCompressionGzip(o);
    for (GenericData data : batch.getDataList()) {
      Handler h = registry.get(data.getType());
      if (h != null) {
        h.handle(data);
      }
    }
    o.onNext(Empty.getDefaultInstance());
    o.onCompleted();
  }

  @Override
  public void bistreamProxy(BiStreamProxyRequest request, StreamObserver<BiStreamProxyResponse> o) {
    proxy0(request, o);
  }

  public void proxy0(BiStreamProxyRequest request, StreamObserver<BiStreamProxyResponse> o) {
    biStreamService.handleLocal(request, o);
  }

}
