/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.holoinsight.server.common.grpc.FileNode;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.registry.grpc.prod.InspectRequest;
import io.holoinsight.server.registry.grpc.prod.InspectResponse;
import io.holoinsight.server.registry.grpc.prod.ListFilesRequest;
import io.holoinsight.server.registry.grpc.prod.ListFilesResponse;
import io.holoinsight.server.registry.grpc.prod.PreviewFileRequest;
import io.holoinsight.server.registry.grpc.prod.PreviewFileResponse;
import io.holoinsight.server.registry.grpc.prod.RegistryServiceForProdGrpc;
import io.holoinsight.server.registry.grpc.prod.TargetIdentifier;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jsy1001de
 * @version 1.0: RegistryService.java, v 0.1 2022年04月24日 10:41 上午 jinsong.yjs Exp $
 */
@Slf4j
@Service
public class RegistryService {

  @Value("${holoinsight.registry.domain}")
  private String registryHost;

  private ManagedChannel channel;
  private RegistryServiceForProdGrpc.RegistryServiceForProdBlockingStub c;

  @PostConstruct
  public void init() {
    channel = ManagedChannelBuilder.forAddress(registryHost, 7201).usePlaintext().build();
    c = RegistryServiceForProdGrpc.newBlockingStub(channel);
  }

  public List<FileNode> listFiles(String tenant, Map<String, Object> dim, String logPath) {
    // 会自动忽略 '/' 根目录
    // '.' 开头的隐藏目录

    ListFilesRequest req = ListFilesRequest.newBuilder() //
        .setName(logPath) //
        // 最大深度
        .setMaxDepth(5) //
        // 如果为true, 会显示 home 和 admin 节点
        .setIncludeParents(true) //
        // 支持的扩展名 不传则允许任意
        .addAllIncludeExts(Collections.singletonList("log")).setTarget(buildTarget(tenant, dim))
        .build(); //

    ListFilesResponse listFilesResponse = c.listFiles(req);

    if (listFilesResponse.getHeader().getCode() != 0) {
      log.warn("listFiles failed, " + dim + " from " + logPath + ", errorMsg: "
          + listFilesResponse.getHeader().getMessage());
      throw new MonitorException(
          "listFiles failed, errorMsg: " + listFilesResponse.getHeader().getMessage());
    }

    return listFilesResponse.getNodesList();
  }

  public PreviewFileResponse previewFile(String tenant, Map<String, Object> dim, String logPath) {
    PreviewFileRequest req = PreviewFileRequest.newBuilder() //
        .setPath(logPath) //
        .setMaxBytes(4096) //
        .setMaxLines(100) //
        .setTarget(buildTarget(tenant, dim)).build();

    PreviewFileResponse previewFileResponse = c.previewFile(req);

    if (previewFileResponse.getHeader().getCode() != 0) {
      log.warn("previewFile failed, " + dim.get("ip") + " from " + logPath + ", errorMsg: "
          + previewFileResponse.getHeader().getMessage());
      throw new MonitorException(
          "previewFile failed, errorMsg: " + previewFileResponse.getHeader().getMessage());
    }

    return previewFileResponse;
  }

  public String inspect(String tenant, Map<String, Object> dim) {
    InspectRequest req = InspectRequest.newBuilder() //
        .setTarget(buildTarget(tenant, dim)).build(); //
    InspectResponse resp = c.inspect(req);
    if (resp.getHeader().getCode() != 0) {
      log.warn("inspect failed, " + dim.get("ip") + ", errorMsg: " + resp.getHeader().getMessage());
      throw new MonitorException("inspect failed, errorMsg: " + resp.getHeader().getMessage());
    }

    return resp.getResult();
  }

  private static TargetIdentifier buildTarget(String tenant, Map<String, Object> dim) {
    return TargetIdentifier.newBuilder().setTenant(tenant).setTargetUk((String) dim.get("_uk"))
        .build();
  }
}
