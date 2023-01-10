/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import io.grpc.stub.StreamObserver;
import io.holoinsight.server.registry.core.grpc.streambiz.BizTypes;
import io.holoinsight.server.registry.grpc.prod.DryRunRequest;
import io.holoinsight.server.registry.grpc.prod.DryRunResponse;
import io.holoinsight.server.registry.grpc.prod.InspectRequest;
import io.holoinsight.server.registry.grpc.prod.InspectResponse;
import io.holoinsight.server.registry.grpc.prod.ListFilesRequest;
import io.holoinsight.server.registry.grpc.prod.ListFilesResponse;
import io.holoinsight.server.registry.grpc.prod.PreviewFileRequest;
import io.holoinsight.server.registry.grpc.prod.PreviewFileResponse;
import io.holoinsight.server.registry.grpc.prod.RegistryServiceForProdGrpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * created at 2022/3/11
 *
 * @author zzhb101
 * @author xzchaoo
 */
@Service
@RegistryGrpcForProd
public class RegistryServiceForProdImpl
    extends RegistryServiceForProdGrpc.RegistryServiceForProdImplBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(RegistryServiceForProdImpl.class);

  @Autowired
  private BiStreamService biStreamService;

  @Override
  public void listFiles(ListFilesRequest request, StreamObserver<ListFilesResponse> o) {
    biStreamService.proxy(request.getTarget(), BizTypes.LIST_FILES, request,
        ListFilesResponse.getDefaultInstance(), o);
  }

  @Override
  public void previewFile(PreviewFileRequest request, StreamObserver<PreviewFileResponse> o) {
    biStreamService.proxy(request.getTarget(), BizTypes.PREVIEW_FILE, request,
        PreviewFileResponse.getDefaultInstance(), o);
  }

  @Override
  public void inspect(InspectRequest request, StreamObserver<InspectResponse> o) {
    biStreamService.proxy(request.getTarget(), BizTypes.INSPECT, request,
        InspectResponse.getDefaultInstance(), o);
  }

  @Override
  public void dryRun(DryRunRequest request, StreamObserver<DryRunResponse> o) {
    biStreamService.proxy(request.getTarget(), BizTypes.DRY_RUN, request,
        DryRunResponse.getDefaultInstance(), o);
  }
}
