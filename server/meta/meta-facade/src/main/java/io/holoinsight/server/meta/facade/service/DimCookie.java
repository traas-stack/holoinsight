/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.service;

import io.holoinsight.server.meta.common.integration.ClientService;
import io.holoinsight.server.meta.common.integration.impl.ClientServiceImpl;
import io.holoinsight.server.meta.common.util.ConstModel;
import io.holoinsight.server.meta.facade.model.MetaType;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.common.util.ConstPool;
import io.holoinsight.server.common.J;
import io.holoinsight.server.meta.facade.model.ClientException;
import io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest;
import io.holoinsight.server.meta.proto.data.DataBaseResponse;
import io.holoinsight.server.meta.proto.data.DataHello;
import io.holoinsight.server.meta.proto.data.DataServiceGrpc;
import io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest;
import io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest;
import io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest;
import io.holoinsight.server.meta.proto.data.QueryDataByTableRequest;
import io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest;
import io.holoinsight.server.meta.proto.data.QueryDataResponse;
import io.grpc.ManagedChannel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jsy1001de
 * @version 1.0: DimCookie.java, v 0.1 2022年03月07日 5:26 下午 jinsong.yjs Exp $
 */
@Setter
@Getter
public class DimCookie implements DataClientService, AgentHeartBeatService, Serializable {
  private static final Logger log = LoggerFactory.getLogger(DimCookie.class);

  private static final long serialVersionUID = -3873469504228090189L;

  private ClientService clientService = new ClientServiceImpl();

  public static final int QUERY_IDS_BATCH = 2000;

  private String server;
  private int port;
  private ManagedChannel channel;
  private long expireTimeout = 30 * 1000;
  private boolean expired = false;
  private long lastAvailableTime = System.currentTimeMillis();
  private long lastRebase = 0;
  private long rebaseSilence = 10000;

  public DimCookie(String server, int port, ManagedChannel channel) {
    this.server = server;
    this.port = port;
    this.channel = channel;
  }

  public void updateLastAvailableTime() {
    this.lastAvailableTime = System.currentTimeMillis();
  }

  public void destory() {
    try {
      log.info("cookie destroy start, authority={}", channel.authority());
      channel.shutdown();
      channel.awaitTermination(5, TimeUnit.SECONDS);
      log.info("cookie destroy finish, authority={}", channel.authority());
    } catch (Exception e) {
      log.error("cookie destroy fail, msg={}", e.getMessage(), e);
    }
  }


  public boolean dataHeartBeat() {
    try {
      DataHello dataHello =
          DataServiceGrpc.newBlockingStub(channel).withDeadlineAfter(5, TimeUnit.SECONDS)
              .heartBeat(DataHello.newBuilder().setFromApp(clientService.getCurrentApp())
                  .setFromIp(clientService.getLocalIp()).build());
      updateLastAvailableTime();
      log.info("data cookie health check success, server={} : {}.", getServer(),
          dataHello.getCount());
      return true;
    } catch (Exception e) {
      log.error("data cookie health check fail, server={}.", getServer(), e);
      this.setExpired(true);
      return false;
    }
  }

  public boolean isExpired() {
    if (this.expired) {
      return true;
    } else {
      this.expired = (System.currentTimeMillis() - lastAvailableTime) > expireTimeout;
      if (this.expired) {
        log.warn("cookie expired, server={}.", getServer());
      }
    }
    return this.expired;
  }

  @Override
  public void insertOrUpdate(String tableName, List<Map<String, Object>> rows) {
    if (rows.size() > ConstPool.GRPC_INSERT_MAX_SIZE) {
      throw new IllegalArgumentException(
          String.format("insert [%s] records fail, max allow size is [%s] !", rows.size(),
              ConstPool.GRPC_INSERT_MAX_SIZE));
    }
    String rowsJson = J.toJson(rows);
    InsertOrUpdateRequest insertOrUpdateRequest = InsertOrUpdateRequest.newBuilder()
        .setTableName(tableName).setRowsJson(rowsJson).setFromApp(clientService.getCurrentApp())
        .setFromIp(clientService.getLocalIp()).build();
    DataBaseResponse dataBaseResponse = DataServiceGrpc.newBlockingStub(channel)
        .withDeadlineAfter(ConstPool.GRPC_WITH_DEADLINE_AFTER_MS_1, TimeUnit.MILLISECONDS)
        .insertOrUpdate(insertOrUpdateRequest);
    if (!dataBaseResponse.getSuccess()) {
      throw new ClientException("execute insertOrUpdate for dimTable[%s] fail : %s. ", tableName,
          dataBaseResponse.getErrMsg());
    }
  }

  @Override
  public void delete(String tableName, List<String> uks) {
    String primaryKeyValsJson = J.toJson(uks);
    BatchDeleteByPkRequest batchDeleteByPkRequest = BatchDeleteByPkRequest.newBuilder()
        .setTableName(tableName).setPrimaryKeyValsJson(primaryKeyValsJson)
        .setFromApp(clientService.getCurrentApp()).setFromIp(clientService.getLocalIp()).build();
    DataBaseResponse response = DataServiceGrpc.newBlockingStub(channel)
        .withDeadlineAfter(ConstPool.GRPC_WITH_DEADLINE_AFTER_MS_1, TimeUnit.MILLISECONDS)
        .batchDeleteByPk(batchDeleteByPkRequest);
    if (!response.getSuccess()) {
      throw new ClientException("execute batchDeleteByPk for dimTable[%s] fail : %s. ", tableName,
          response.getErrMsg());
    }
  }

  @Override
  public List<Map<String, Object>> queryAll(String tableName) {

    QueryDataByTableRequest queryDataByTableRequest = QueryDataByTableRequest.newBuilder()
        .setTableName(tableName).setFromApp(clientService.getCurrentApp())
        .setFromIp(clientService.getLocalIp()).build();

    List<Map<String, Object>> rows = new ArrayList<>();

    Iterator<QueryDataResponse> responseIterator = DataServiceGrpc.newBlockingStub(channel)
        .withDeadlineAfter(ConstPool.GRPC_WITH_DEADLINE_AFTER_MS_2, TimeUnit.MILLISECONDS)
        .queryDataByTableStream(queryDataByTableRequest);

    while (responseIterator.hasNext()) {
      QueryDataResponse response = responseIterator.next();
      String batchRowsJson = response.getRowsJson();
      List<Map<String, Object>> batchRows = J.toMapList(batchRowsJson);
      rows.addAll(batchRows);
    }

    return rows;
  }

  @Override
  public List<Map<String, Object>> queryAll(String tableName, List<String> rowKeys) {
    QueryDataByTableRowsRequest queryDataByTableRowsRequest = QueryDataByTableRowsRequest
        .newBuilder().setTableName(tableName).setPkRows(J.toJson(rowKeys))
        .setFromApp(clientService.getCurrentApp()).setFromIp(clientService.getLocalIp()).build();

    List<Map<String, Object>> rows = new ArrayList<>();

    Iterator<QueryDataResponse> responseIterator = DataServiceGrpc.newBlockingStub(channel)
        .withDeadlineAfter(ConstPool.GRPC_WITH_DEADLINE_AFTER_MS_2, TimeUnit.MILLISECONDS)
        .queryDataByTableRowsStream(queryDataByTableRowsRequest);

    while (responseIterator.hasNext()) {
      QueryDataResponse response = responseIterator.next();
      String batchRowsJson = response.getRowsJson();
      List<Map<String, Object>> batchRows = J.toMapList(batchRowsJson);
      rows.addAll(batchRows);
    }

    return rows;
  }

  @Override
  public void deleteByExample(String tableName, QueryExample example) {
    String exampleJson = J.toJson(example);
    DeleteDataByExampleRequest deleteDataByExampleRequest = DeleteDataByExampleRequest.newBuilder()
        .setTableName(tableName).setExampleJson(exampleJson)
        .setFromApp(clientService.getCurrentApp()).setFromIp(clientService.getLocalIp()).build();
    DataBaseResponse dataBaseResponse = DataServiceGrpc.newBlockingStub(channel)
        .withDeadlineAfter(ConstPool.GRPC_WITH_DEADLINE_AFTER_MS_1, TimeUnit.MILLISECONDS)
        .deleteByExample(deleteDataByExampleRequest);
    if (!dataBaseResponse.getSuccess()) {
      throw new ClientException("execute deleteByExample for dimTable[%s] fail : %s. ", tableName,
          dataBaseResponse.getErrMsg());
    }
  }

  @Override
  public List<Map<String, Object>> queryByExample(String tableName, QueryExample example) {

    String exampleJson = J.toJson(example);
    QueryDataByExampleRequest queryDataByExampleRequest = QueryDataByExampleRequest.newBuilder()
        .setTableName(tableName).setExampleJson(exampleJson)
        .setFromApp(clientService.getCurrentApp()).setFromIp(clientService.getLocalIp()).build();

    List<Map<String, Object>> rows = new ArrayList<>();

    Iterator<QueryDataResponse> responseIterator = DataServiceGrpc.newBlockingStub(channel)
        .withDeadlineAfter(ConstPool.GRPC_WITH_DEADLINE_AFTER_MS_1, TimeUnit.MILLISECONDS)
        .queryByExampleStream(queryDataByExampleRequest);

    while (responseIterator.hasNext()) {
      QueryDataResponse response = responseIterator.next();
      String batchRowsJson = response.getRowsJson();
      List<Map<String, Object>> batchRows = J.toMapList(batchRowsJson);
      rows.addAll(batchRows);
    }

    return rows;
  }

  @Override
  public List<Map<String, Object>> fuzzyByExample(String tableName, QueryExample example) {
    String exampleJson = J.toJson(example);
    QueryDataByExampleRequest queryDataByExampleRequest = QueryDataByExampleRequest.newBuilder()
        .setTableName(tableName).setExampleJson(exampleJson)
        .setFromApp(clientService.getCurrentApp()).setFromIp(clientService.getLocalIp()).build();

    QueryDataResponse queryDataResponse = DataServiceGrpc.newBlockingStub(channel)
        .withDeadlineAfter(ConstPool.GRPC_WITH_DEADLINE_AFTER_MS_1, TimeUnit.MILLISECONDS)
        .fuzzyByExample(queryDataByExampleRequest);

    if (!queryDataResponse.getSuccess()) {
      throw new ClientException("execute fuzzyByExample for dimTable[%s] fail : %s. ", tableName,
          queryDataResponse.getErrMsg());
    }
    return J.toMapList(queryDataResponse.getRowsJson());
  }

  @Override
  public void agentInsertOrUpdate(String tableName, String ip, String hostname,
      Map<String, Object> row) {

    if (null == row) {
      row = new HashMap<>();
    }
    row.put(ConstModel.default_modifier, "agent");
    row.put(ConstModel.default_modified, System.currentTimeMillis());
    row.put(ConstModel.default_status, "ONLINE");
    row.put(ConstModel.default_type, MetaType.VM.toString());

    row.put("ip", ip);
    row.put("hostname", hostname);
    insertOrUpdate(tableName, Collections.singletonList(row));
  }

  @Override
  public void agentInsertOrUpdate(String tableName, MetaType type, List<Map<String, Object>> rows) {
    if (CollectionUtils.isEmpty(rows)) {
      return;
    }

    rows.forEach(row -> {
      row.put(ConstModel.default_modifier, "agent");
      row.put(ConstModel.default_modified, System.currentTimeMillis());
      row.put(ConstModel.default_status, "ONLINE");
      row.put(ConstModel.default_type, type.toString());
    });

    insertOrUpdate(tableName, rows);
  }

  @Override
  public void agentDelete(String tableName, MetaType type, List<Map<String, Object>> rows) {

    if (CollectionUtils.isEmpty(rows)) {
      return;
    }

    rows.forEach(row -> {
      if (CollectionUtils.isEmpty(row)) {
        return;
      }
      row.put(ConstModel.default_type, type.name());
    });

    String exampleJson = J.toJson(rows);
    DeleteDataByExampleRequest deleteDataByExampleRequest = DeleteDataByExampleRequest.newBuilder()
        .setTableName(tableName).setExampleJson(exampleJson)
        .setFromApp(clientService.getCurrentApp()).setFromIp(clientService.getLocalIp()).build();
    DataBaseResponse dataBaseResponse = DataServiceGrpc.newBlockingStub(channel)
        .withDeadlineAfter(ConstPool.GRPC_WITH_DEADLINE_AFTER_MS_1, TimeUnit.MILLISECONDS)
        .deleteByRowMap(deleteDataByExampleRequest);
    if (!dataBaseResponse.getSuccess()) {
      throw new ClientException("execute agentDelete for dimTable[%s] fail : %s. ", tableName,
          dataBaseResponse.getErrMsg());
    }
  }
}
