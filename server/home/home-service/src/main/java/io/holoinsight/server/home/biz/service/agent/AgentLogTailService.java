/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.protobuf.ProtocolStringList;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.RetryUtils;
import io.holoinsight.server.common.UtilMisc;
import io.holoinsight.server.common.grpc.FileNode;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.common.service.RegistryService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;
import io.holoinsight.server.registry.grpc.prod.PreviewFileResponse;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jsy1001de
 * @version 1.0: AgentLogTailService.java, v 0.1 2022年04月24日 12:10 下午 jinsong.yjs Exp $
 */
@Slf4j
@Service
public class AgentLogTailService {

  @Autowired
  private RegistryService registryService;

  @Autowired
  private DataClientService dataClientService;

  @Autowired
  private TenantInitService tenantInitService;

  public FileTailResponse listFiles(AgentParamRequest agentParamRequest, String tenant,
      String workspace) {

    Map<String, Object> dim = getDimByRequest(agentParamRequest, tenant, workspace);

    FileTailResponse response = new FileTailResponse();

    List<FileNode> fileNodes = registryService.listFiles(tenantInitService.getTsdbTenant(tenant),
        dim, agentParamRequest.getLogpath());

    response.addToDatas("dirTrees", convertFiledNodes(fileNodes));
    response.addToDatas("agentId", dim.get("agentId"));
    response.addToDatas("ip", dim.get("ip"));
    response.addToDatas("namespace", dim.get("namespace"));
    response.addToDatas("hostname", dim.get("hostname"));

    return response;
  }

  public FileTailResponse previewFile(AgentParamRequest agentParamRequest, String tenant,
      String workspace) {

    Map<String, Object> dim = getDimByRequest(agentParamRequest, tenant, workspace);
    FileTailResponse response = new FileTailResponse();

    PreviewFileResponse grpcResp = registryService
        .previewFile(tenantInitService.getTsdbTenant(tenant), dim, agentParamRequest.getLogpath());

    response.addToDatas("lines", convertContentLines(grpcResp.getContentList()));
    response.addToDatas("charset", grpcResp.getCharset());
    response.addToDatas("timezone", grpcResp.getTimezone());
    response.addToDatas("agentId", dim.get("agentId"));
    response.addToDatas("ip", dim.get("ip"));
    response.addToDatas("namespace", dim.get("namespace"));
    response.addToDatas("hostname", dim.get("hostname"));

    return response;
  }

  public FileTailResponse inspect(AgentParamRequest agentParamRequest, String tenant,
      String workspace) {

    Map<String, Object> dim = getDimByRequest(agentParamRequest, tenant, workspace);
    FileTailResponse response = new FileTailResponse();
    String result = RetryUtils.invoke(
        () -> registryService.inspect(tenantInitService.getTsdbTenant(tenant), dim), null, 3, 1000,
        new ArrayList<>());

    Map<String, Object> map = J.toMap(result);

    response.addToDatas("variable", map);
    response.addToDatas("agentId", dim.get("agentId"));
    response.addToDatas("ip", dim.get("ip"));
    response.addToDatas("namespace", dim.get("namespace"));
    response.addToDatas("hostname", dim.get("hostname"));

    return response;
  }

  /**
   * 根据请求获取一个对应的元数据
   * 
   * @param agentParamRequest
   * @param tenant
   * @return
   */
  private Map<String, Object> getDimByRequest(AgentParamRequest agentParamRequest, String tenant,
      String workspace) {

    QueryExample queryExample = new QueryExample();

    if (StringUtil.isNotBlank(agentParamRequest.ip)) {
      queryExample.getParams().put("ip", agentParamRequest.ip);
    }
    if (StringUtil.isNotBlank(agentParamRequest.hostname)) {
      queryExample.getParams().put("hostname", agentParamRequest.hostname);
    }
    if (StringUtil.isNotBlank(agentParamRequest.app)) {
      queryExample.getParams().put("app", agentParamRequest.app);
    }

    if (!CollectionUtils.isEmpty(agentParamRequest.label)) {
      for (Map.Entry<String, String> entry : agentParamRequest.label.entrySet()) {
        if (StringUtil.isBlank(entry.getValue()))
          continue;
        queryExample.getParams().put(entry.getKey(), entry.getValue());
      }
    }

    if (queryExample.getParams().isEmpty()) {
      throw new IllegalStateException("miss metadata query params");
    }


    Map<String, String> conditions =
        tenantInitService.getTenantWorkspaceMetaConditions(tenant, workspace);
    if (!CollectionUtils.isEmpty(conditions)) {
      queryExample.getParams().putAll(conditions);
    }

    List<Map<String, Object>> list = dataClientService
        .queryByExample(tenantInitService.getTenantServerTable(tenant), queryExample);

    if (CollectionUtils.isEmpty(list)) {
      log.warn("get meta error，query param [" + queryExample.getParams().toString() + "] [" + tenant
          + "] dims is null");
      throw new MonitorException("get meta empty");
    }

    return list.get(UtilMisc.getRandom(list.size()));
  }

  private List<MonitorFileNode> convertFiledNodes(List<FileNode> fileNodes) {
    List<MonitorFileNode> fileNodeList = new ArrayList<>();
    fileNodes.forEach(fileNode -> fileNodeList.add(convertFileNode(fileNode, "")));
    return fileNodeList;
  }

  private MonitorFileNode convertFileNode(FileNode fileNode, String path) {
    MonitorFileNode monitorFileNode = new MonitorFileNode();
    monitorFileNode.setDir(fileNode.getDir());
    monitorFileNode.setName(fileNode.getName());
    String fullpath = path + "/" + monitorFileNode.getName();
    monitorFileNode.setFullPath(fullpath);

    if (!fileNode.getDir() || CollectionUtils.isEmpty(fileNode.getChildrenList())) {
      return monitorFileNode;
    }
    fileNode.getChildrenList().forEach(child -> {
      MonitorFileNode childNode = convertFileNode(child, fullpath);
      monitorFileNode.getSubs().add(childNode);
    });

    return monitorFileNode;
  }

  private String[] convertContentLines(ProtocolStringList protocolStringList) {
    String[] strings = new String[protocolStringList.size()];

    for (int i = 0; i < protocolStringList.size(); i++) {
      strings[i] = protocolStringList.get(i);
    }

    return strings;
  }

}
