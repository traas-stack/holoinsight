/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessageV3;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.BoundedSchedulers;
import io.holoinsight.server.registry.core.collecttarget.CollectTargetService;
import io.holoinsight.server.registry.core.grpc.streambiz.BizTypes;
import io.holoinsight.server.registry.core.template.CollectTemplate;
import io.holoinsight.server.registry.core.template.TemplateService;
import io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest;
import io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse;
import io.holoinsight.server.registry.grpc.prod.CheckConfigTaskDistributionRequest;
import io.holoinsight.server.registry.grpc.prod.CheckConfigTaskDistributionResponse;
import io.holoinsight.server.registry.grpc.prod.DryRunRequest;
import io.holoinsight.server.registry.grpc.prod.DryRunResponse;
import io.holoinsight.server.registry.grpc.prod.InspectRequest;
import io.holoinsight.server.registry.grpc.prod.InspectResponse;
import io.holoinsight.server.registry.grpc.prod.ListFilesRequest;
import io.holoinsight.server.registry.grpc.prod.ListFilesResponse;
import io.holoinsight.server.registry.grpc.prod.PreviewFileRequest;
import io.holoinsight.server.registry.grpc.prod.PreviewFileResponse;
import io.holoinsight.server.registry.grpc.prod.RegistryServiceForProdGrpc;
import io.holoinsight.server.registry.grpc.prod.TargetIdentifier;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2022/3/11
 *
 * @author xzchaoo
 */
@Service
@RegistryGrpcForProd
@Slf4j
public class RegistryServiceForProdImpl
    extends RegistryServiceForProdGrpc.RegistryServiceForProdImplBase {

  @Autowired
  private BiStreamService biStreamService;
  @Autowired
  private TemplateService templateService;
  @Autowired
  private CollectTargetService collectTargetService;

  @Override
  public void listFiles(ListFilesRequest request, StreamObserver<ListFilesResponse> o) {
    proxy(request.getTarget(), BizTypes.LIST_FILES, request, ListFilesResponse.getDefaultInstance(),
        o);
  }

  @Override
  public void previewFile(PreviewFileRequest request, StreamObserver<PreviewFileResponse> o) {
    proxy(request.getTarget(), BizTypes.PREVIEW_FILE, request,
        PreviewFileResponse.getDefaultInstance(), o);
  }

  @Override
  public void inspect(InspectRequest request, StreamObserver<InspectResponse> o) {
    proxy(request.getTarget(), BizTypes.INSPECT, request, InspectResponse.getDefaultInstance(), o);
  }

  @Override
  public void dryRun(DryRunRequest request, StreamObserver<DryRunResponse> o) {
    try {
      biStreamService.proxyForDim(request.getTarget(), BizTypes.DRY_RUN, request,
          DryRunResponse.getDefaultInstance(), o);
    } catch (Throwable e) {
      log.error("dryRun error", e);
      o.onError(Status.INTERNAL.withCause(e).withDescription(e.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void checkConfigDistribution(CheckConfigDistributionRequest request,
      StreamObserver<CheckConfigDistributionResponse> o) {
    MergedResult merged = new MergedResult();

    Flux.defer(() -> {
      // 找到采集配置
      CollectTemplate template = templateService.fuzzyGet(request.getTableName());
      if (template == null) {
        throw new IllegalStateException("no template " + request.getTableName());
      }
      merged.template = template;

      // 计算出所有targets
      return Mono.fromSupplier(() -> collectTargetService.getTargets(template)) //
          .doOnNext(targets -> merged.targets = targets.size()) //
          .subscribeOn(BoundedSchedulers.BOUNDED) //
          .flatMapIterable(Function.identity()); //
    }).flatMap(target -> {

      // 发请求查agent是否有配置
      Map<String, Object> row = target.unwrap();
      TargetIdentifier targetIdentifier = TargetIdentifier.newBuilder() //
          .setTenant(merged.template.getTenant()) //
          .setTargetUk((String) row.get("_uk")) //
          .build(); //

      CheckConfigTaskDistributionRequest pbReq = CheckConfigTaskDistributionRequest.newBuilder()
          .addSubTasks(CheckConfigTaskDistributionRequest.SubTask.newBuilder() //
              .setConfigKey(merged.template.getTableName()) //
              .setConfigVersion(merged.template.getVersion()) //
              .setTargetKey(target.getId()) //
              .setTargetVersion("") // 暂无
              .build()) //
          .build();
      CheckConfigTaskDistributionResponse defaultPbResp =
          CheckConfigTaskDistributionResponse.getDefaultInstance();

      // 异步发出请求, 无论成功或失败, 总是当做完成 (materialize + ignoreElement)
      return GrpcReactorUtils
          .<CheckConfigTaskDistributionResponse>observerToMono(o2 -> biStreamService
              .proxyForDim(targetIdentifier, BizTypes.CHECK_TASK, pbReq, defaultPbResp, o2)) //
          .doOnNext(merged::merge) //
          .doOnError(merged::onError) //
          .materialize() //
          .ignoreElement(); //
    }) //
        .then(Mono.fromSupplier(() -> { //
          return CheckConfigDistributionResponse.newBuilder() //
              .setDims(merged.targets) //
              .setTasks(merged.targets) //
              .setLatestVersion(merged.latestVersion.get()) //
              .setOldVersion(merged.oldVersion.get()) //
              .setUnknown(merged.unknown.get()) //
              .build();
        })) //
        .subscribe(o::onNext, err -> { //
          o.onError(Status.INTERNAL.withCause(err).withDescription(err.getMessage())
              .asRuntimeException());
        }, o::onCompleted); //

  }

  public <RESP extends GeneratedMessageV3> void proxy(TargetIdentifier target, int bizType,
      GeneratedMessageV3 request, RESP defaultResp, StreamObserver<RESP> o) {
    try {
      biStreamService.proxyForDim(target, bizType, request, defaultResp, o);
    } catch (RuntimeException e) {
      log.error("proxyForDim error", e);
      o.onError(Status.INTERNAL.withCause(e).withDescription(e.getMessage()).asRuntimeException());
    }
  }
}


class MergedResult {
  CollectTemplate template;
  int targets;
  AtomicInteger latestVersion = new AtomicInteger(0);
  AtomicInteger oldVersion = new AtomicInteger(0);
  AtomicInteger unknown = new AtomicInteger(0);
  AtomicInteger no = new AtomicInteger(0);

  void merge(CheckConfigTaskDistributionResponse resp) {
    for (Integer integer : resp.getStatusList()) {
      switch (integer) {
        case 0:
          no.incrementAndGet();
          break;
        case 1:
          latestVersion.incrementAndGet();
          break;
        case 2:
          oldVersion.incrementAndGet();
          break;
        default:
          unknown.incrementAndGet();
          break;
      }
    }
  }

  void onError(Throwable ignored) {
    unknown.incrementAndGet();
  }
}
