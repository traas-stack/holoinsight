/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.holoinsight.server.common.J;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.holoinsight.server.registry.core.agent.DimDataWriteTask;
import io.holoinsight.server.common.JsonUtils;
import io.holoinsight.server.common.auth.ApikeyService;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;
import io.holoinsight.server.common.dao.entity.ApikeyDO;
import io.holoinsight.server.common.MD5Hash;

/**
 * <p>
 * created at 2022/7/25
 *
 * @author zzhb101
 */
@Service
public class MetaSyncService {
  public static final String DEFAULT_WORKSPACE = "default";
  private static final String DEFAULT_CLUSTER = "default";

  private static final Logger LOGGER = LoggerFactory.getLogger(MetaSyncService.class);

  @Autowired
  private DataClientService dataClientService;

  @Autowired
  private ApikeyService apikeyService;

  @Autowired
  private MetaWriterService metaWriterService;


  @Autowired
  private MetaConfig metaConfig;

  public void handleFull(FullSyncRequest req) {
    if (StringUtils.isEmpty(req.getWorkspace())) {
      req.setWorkspace(DEFAULT_WORKSPACE);
    }
    if (StringUtils.isEmpty(req.getCluster())) {
      req.setCluster(DEFAULT_CLUSTER);
    }

    if (metaConfig.getBasic().isVerbose()) {
      LOGGER.info("fullSync {}", JsonUtils.toJson(req));
    }
    LOGGER.info("curd detail, need [compare]: {}, {}", req.getType(), req.getResources().size());
    List<Resource> upsert = new ArrayList<>();
    List<Map<String, Object>> delete = new ArrayList<>();

    String tableName = genTableName(req.getApikey());
    if (StringUtils.isBlank(tableName)) {
      String msg = String.format("apiKey [%s] is not existed", req.getApikey());
      LOGGER.error(msg);
      throw new IllegalStateException(msg);
    }

    // compare 返回upsert 列表，和 delete 列表
    try {
      compare(tableName, req.getWorkspace(), req.getCluster(), req.getType(), req.getResources(),
          upsert, delete);
    } catch (Exception e) {
      LOGGER.error("curd detail, error, {}", e.getMessage(), e);
      throw new IllegalStateException("compare error, " + e.getMessage());
    }

    LOGGER.info("curd detail, need [upsert]: {}, {}", req.getType(), upsert.size());
    LOGGER.info("curd detail, need [delete]: {}, {}", req.getType(), delete.size());

    // 加入到异步队列中
    if (!CollectionUtils.isEmpty(upsert)) {
      for (Resource resource : upsert) {
        DimDataWriteTask.upsertSubmit(tableName, req.getType(),
            convertOneMeta(req.getType(), req.getWorkspace(), req.getCluster(), resource));
      }
    }

    if (!CollectionUtils.isEmpty(delete)) {
      for (Map<String, Object> resource : delete) {
        DimDataWriteTask.deleteSubmit(tableName, req.getType(), resource);
      }
    }

  }

  public void handleDelta(DeltaSyncRequest req) {
    if (StringUtils.isEmpty(req.getWorkspace())) {
      req.setWorkspace(DEFAULT_WORKSPACE);
    }
    if (StringUtils.isEmpty(req.getCluster())) {
      req.setCluster(DEFAULT_CLUSTER);
    }

    if (metaConfig.getBasic().isVerbose()) {
      LOGGER.info("deltaSync {}", JsonUtils.toJson(req));
    }

    String tableName = genTableName(req.getApikey());
    if (StringUtils.isBlank(tableName)) {
      String msg = String.format("apiKey [%s] is not existed", req.getApikey());
      LOGGER.error(msg);
      throw new IllegalStateException(msg);
    }

    convertAddMeta(tableName, req.getType().toUpperCase(), req.getWorkspace(), req.getCluster(),
        req.getAdd());
    convertDelMeta(tableName, req.getType().toUpperCase(), req.getWorkspace(), req.getCluster(),
        req.getDel());

  }

  private void convertAddMeta(String tableName, String type, String workspace, String cluster,
      List<Resource> resources) {

    if (CollectionUtils.isEmpty(resources)) {
      return;
    }

    for (Resource resource : resources) {
      DimDataWriteTask.upsertSubmit(tableName, type,
          convertOneMeta(type, workspace, cluster, resource));
    }
  }

  private void convertDelMeta(String tableName, String type, String workspace, String cluster,
      List<Resource> resources) {

    if (CollectionUtils.isEmpty(resources)) {
      return;
    }

    for (Resource resource : resources) {
      DimDataWriteTask.deleteSubmit(tableName, type,
          convertOneMeta(type, workspace, cluster, resource));
    }
  }

  private Map<String, Object> convertOneMeta(String type, String workspace, String cluster,
      Resource resource) {
    Map<String, Object> map = new HashMap<>();

    map.put("_type", StringUtils.upperCase(type));
    map.put("_workspace", workspace);
    map.put("_cluster", cluster);
    map.put("namespace", resource.getNamespace());
    map.put("name", resource.getName());

    if (!CollectionUtils.isEmpty(resource.getLabels())) {
      map.put("labels", resource.getLabels());
    }
    if (!CollectionUtils.isEmpty(resource.getAnnotations())) {
      map.put("annotations", resource.getAnnotations());
    }

    Map<String, Object> extraLabel = metaWriterService.getExtraLabel(resource.getLabels());
    if (!CollectionUtils.isEmpty(extraLabel)) {
      map.putAll(extraLabel);
    }

    map.put("app", resource.getApp());
    map.put("ip", resource.getIp());
    if (StringUtils.isNotEmpty(resource.getHostname())) {
      map.put("hostname", resource.getHostname());
    }
    if (StringUtils.isNotEmpty(resource.getHostIP())) {
      map.put("hostIP", resource.getHostIP());
    }
    String uk = genUk(workspace, cluster, resource);
    map.put("_uk", uk);

    map.put("_modifier", "agent");
    map.put("_modified", System.currentTimeMillis());

    if ("POD".equals(type)) {
      map.put("agentId", "dim2:" + uk);
    }
    return map;
  }

  private void compare(String tableName, String workspace, String cluster, String type,
      List<Resource> resources, List<Resource> upsert, List<Map<String, Object>> delete) {

    Map<String, Map<String, Object>> fromDB = getFromDB(tableName, type, workspace, cluster);

    Set<String> dbUks = fromDB.keySet();

    if (CollectionUtils.isEmpty(resources)) {
      return;
    }

    resources.forEach(resource -> {
      upsert.add(resource);
      String uk = genUk(workspace, cluster, resource);
      dbUks.remove(uk);
    });

    if (CollectionUtils.isEmpty(dbUks)) {
      return;
    }

    dbUks.forEach(uk -> {
      delete.add(fromDB.get(uk));
    });
  }

  private Map<String, Map<String, Object>> getFromDB(String tableName, String type,
      String workspace, String cluster) {
    List<String> rowKeys = new ArrayList<>();
    QueryExample example = new QueryExample();
    if (StringUtils.isNotBlank(workspace)) {
      example.getParams().put("_workspace", workspace);
      rowKeys.add("_workspace");
    }

    if (StringUtils.isNotBlank(cluster)) {
      example.getParams().put("_cluster", cluster);
      rowKeys.add("_cluster");
    }

    example.getParams().put("_type", StringUtils.upperCase(type));
    rowKeys.add("_type");
    rowKeys.add("_uk");
    rowKeys.add("name");
    rowKeys.add("namespace");

    example.setRowKeys(rowKeys);
    List<Map<String, Object>> mapList = dataClientService.queryByExample(tableName, example);
    if (CollectionUtils.isEmpty(mapList)) {
      return new HashMap<>();
    }

    LOGGER.info("curd detail, [getFromDB], size: {}, {}", mapList.size(), J.toJson(example));
    Map<String, Map<String, Object>> uks = new HashMap<>();
    mapList.forEach(row -> {
      if (row.get("_uk") == null) {
        return;
        // throw new IllegalStateException("null uk" + row);
      }
      uks.put(row.get("_uk").toString(), row);
    });

    return uks;
  }

  private String genUk(String workspace, String cluster, Resource resource) {

    StringBuilder ukValue = new StringBuilder();

    if (!StringUtils.isBlank(resource.getName())) {
      ukValue.append(resource.getName());
    }

    if (!StringUtils.isBlank(resource.getNamespace())) {
      ukValue.append(resource.getNamespace());
    }

    if (!StringUtils.isBlank(workspace)) {
      ukValue.append(workspace);
    }

    if (!StringUtils.isBlank(cluster)) {
      ukValue.append(cluster);
    }

    return MD5Hash.getMD5(ukValue.toString());
  }

  private String genTableName(String apiKey) {

    if (StringUtils.isBlank(apiKey)) {
      return null;
    }

    ApikeyDO apikeyDO = apikeyService.getApikeyMap().get(apiKey);
    if (null == apikeyDO) {
      return null;
    }

    return apikeyDO.getTenant() + "_server";
  }

}
