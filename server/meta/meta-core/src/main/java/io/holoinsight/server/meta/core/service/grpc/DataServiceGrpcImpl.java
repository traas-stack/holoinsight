/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service.grpc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.reflect.TypeToken;
import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.Pair;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.common.util.ConstModel;
import io.holoinsight.server.meta.common.util.ConstPool;
import io.holoinsight.server.meta.common.util.RetryPolicy;
import io.holoinsight.server.meta.core.service.DBCoreService;
import io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest;
import io.holoinsight.server.meta.proto.data.DataBaseResponse;
import io.holoinsight.server.meta.proto.data.DataHello;
import io.holoinsight.server.meta.proto.data.DataServiceGrpc;
import io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest;
import io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest;
import io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest;
import io.holoinsight.server.meta.proto.data.QueryDataByPksRequest;
import io.holoinsight.server.meta.proto.data.QueryDataByTableRequest;
import io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest;
import io.holoinsight.server.meta.proto.data.QueryDataResponse;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author jsy1001de
 * @version 1.0: DataQueryServiceGrpcImpl.java, v 0.1 2022年03月07日 8:19 下午 jinsong.yjs Exp $
 */
@Service
public class DataServiceGrpcImpl extends DataServiceGrpc.DataServiceImplBase {
  private static final Logger logger = LoggerFactory.getLogger(DataServiceGrpcImpl.class);
  @Autowired
  @Qualifier("mongoDataCoreService")
  private DBCoreService mongoDataCoreService;

  @Autowired
  @Qualifier("sqlDataCoreService")
  private DBCoreService sqlDataCoreService;

  @Autowired
  private SuperCacheService superCacheService;

  private ThreadPoolExecutor writeMysqlExecutor = new ThreadPoolExecutor(3, 3, 0, TimeUnit.MINUTES, //
      new ArrayBlockingQueue<>(65536), //
      new ThreadFactoryBuilder().setNameFormat("meta-mysql-%d").build(), //
      new ThreadPoolExecutor.AbortPolicy());

  @Override
  public void insertOrUpdate(InsertOrUpdateRequest request,
      io.grpc.stub.StreamObserver<DataBaseResponse> o) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    String tableName = request.getTableName();
    String rowsJson = request.getRowsJson();

    List<Map<String, Object>> rows = J.toMapList(rowsJson);
    DataBaseResponse.Builder builder = DataBaseResponse.newBuilder();
    try {
      Pair<Integer, Integer> insertOrUpdate = tryUntilSuccess(
          () -> mongoDataCoreService.insertOrUpdate(tableName, rows), "insertOrUpdate", 0);
      if (writeMysqlEnable()) {
        writeMysqlExecutor.execute(() -> sqlDataCoreService.insertOrUpdate(tableName, rows));
      }
      builder.setSuccess(true).setRowsJson(String.format("insertCount: %s, updateCount: %s",
          insertOrUpdate.left(), insertOrUpdate.right()));
      logger.info("DimWriterGrpcBackend,insert,Y,{},{},{},{},{},{},", stopWatch.getTime(),
          tableName, rows.size(), 0, request.getFromApp(), request.getFromIp());
    } catch (Exception e) {
      logger.error("DimWriterGrpcBackend,insert,N,{},{},{},{},{},{},{},", stopWatch.getTime(),
          tableName, rows.size(), 0, request.getFromApp(), request.getFromIp(), e.getMessage(), e);
      builder.setSuccess(false);
      builder.setErrMsg(e.getMessage());
    }
    o.onNext(builder.build());
    o.onCompleted();
  }

  @Override
  public void queryDataByPks(QueryDataByPksRequest request,
      io.grpc.stub.StreamObserver<QueryDataResponse> o) {
    StopWatch stopWatch = new StopWatch();
    QueryDataResponse.Builder builder = QueryDataResponse.newBuilder();
    String tableName = request.getTableName();
    List<String> pkVals =
        J.fromJson(request.getPkValsJson(), new TypeToken<List<String>>() {}.getType());
    DBCoreService coreService = getDbCoreService();
    stopWatch.start();
    List<Map<String, Object>> rows =
        tryUntilSuccess(() -> coreService.queryByPks(tableName, pkVals), "queryDataByPks", 0);
    stopWatch.stop();
    try {
      logger.info("DimWriterGrpcBackend,queryDataByPks,Y,{},{},{},{},{},{},", stopWatch.getTime(),
          tableName, pkVals.size(), rows.size(), request.getFromApp(), request.getFromIp());
      builder.setSuccess(true);
      builder.setRowsJson(J.toJson(rows));
    } catch (Exception e) {
      logger.error("DimWriterGrpcBackend,queryDataByPks,N,{},{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, pkVals.size(), 0, request.getFromApp(),
          request.getFromIp(), e.getMessage(), e);
      builder.setSuccess(false);
      builder.setErrMsg(e.getMessage());
    }
    o.onNext(builder.build());
    o.onCompleted();
  }

  private DBCoreService getDbCoreService() {
    DBCoreService coreService;
    if (readMysqlEnable()) {
      coreService = sqlDataCoreService;
    } else {
      coreService = mongoDataCoreService;
    }
    return coreService;
  }

  @Override
  public void queryDataByTableStream(QueryDataByTableRequest request,
      io.grpc.stub.StreamObserver<QueryDataResponse> o) {
    StopWatch stopWatch = StopWatch.createStarted();
    String tableName = request.getTableName();
    List<Map<String, Object>> rows;
    DBCoreService coreService = getDbCoreService();
    try {
      rows = tryUntilSuccess(() -> coreService.queryByTable(tableName), "queryByTable", 0);
      Iterator<Map<String, Object>> it = rows.iterator();
      int total = 0;
      int count = 0;
      List<Map<String, Object>> batchRows = new ArrayList<>();
      while (it.hasNext()) {
        if (count == ConstPool.GRPC_QUERY_RETURN_BATCH_SIZE) {
          o.onNext(QueryDataResponse.newBuilder().setSuccess(true).setRowsJson(J.toJson(batchRows))
              .build());
          batchRows.clear();
          count = 0;
        } else {
          batchRows.add(it.next());
          count++;
          total++;
        }
      }
      if (count != 0) {
        o.onNext(QueryDataResponse.newBuilder().setSuccess(true).setRowsJson(J.toJson(batchRows))
            .build());
      }
      logger.info("DimWriterGrpcBackend,queryDataByTableStream,Y,{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, 0, total, request.getFromApp(), request.getFromIp());
      o.onCompleted();
    } catch (Exception e) {
      logger.error("DimWriterGrpcBackend,queryDataByTableStream,N,{},{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, 0, 0, request.getFromApp(), request.getFromIp(),
          e.getMessage(), e);
      o.onError(e);
    }
  }

  @Override
  public void queryDataByTableRowsStream(QueryDataByTableRowsRequest request,
      io.grpc.stub.StreamObserver<QueryDataResponse> o) {
    StopWatch stopWatch = StopWatch.createStarted();
    String tableName = request.getTableName();
    List<Map<String, Object>> rows;
    String pkRows = request.getPkRows();
    List<String> rowVals = J.toList(pkRows);
    try {
      DBCoreService coreService = getDbCoreService();
      rows = tryUntilSuccess(() -> coreService.queryByTable(tableName, rowVals), "queryByTable", 0);
      Iterator<Map<String, Object>> it = rows.iterator();
      int total = 0;
      int count = 0;
      List<Map<String, Object>> batchRows = new ArrayList<>();
      while (it.hasNext()) {
        if (count == ConstPool.GRPC_QUERY_RETURN_BATCH_SIZE) {
          o.onNext(QueryDataResponse.newBuilder().setSuccess(true).setRowsJson(J.toJson(batchRows))
              .build());
          batchRows.clear();
          count = 0;
        } else {
          batchRows.add(it.next());
          count++;
          total++;
        }
      }
      if (count != 0) {
        o.onNext(QueryDataResponse.newBuilder().setSuccess(true).setRowsJson(J.toJson(batchRows))
            .build());
      }
      logger.info("DimWriterGrpcBackend,queryDataByTableStream,Y,{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, 0, total, request.getFromApp(), request.getFromIp());
      o.onCompleted();
    } catch (Exception e) {
      logger.error("DimWriterGrpcBackend,queryDataByTableStream,N,{},{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, 0, 0, request.getFromApp(), request.getFromIp(),
          e.getMessage(), e);
      o.onError(e);
    }
  }

  @Override
  public void batchDeleteByPk(BatchDeleteByPkRequest request,
      io.grpc.stub.StreamObserver<DataBaseResponse> o) {
    StopWatch stopWatch = StopWatch.createStarted();
    String pkValsJson = request.getPrimaryKeyValsJson();
    List<String> pkVals = J.toList(pkValsJson);
    DataBaseResponse.Builder builder = DataBaseResponse.newBuilder();
    try {
      Long deleteCount = tryUntilSuccess(
          () -> mongoDataCoreService.batchDeleteByPk(request.getTableName(), pkVals),
          "batchDeleteByPk", 0);
      if (writeMysqlEnable()) {
        writeMysqlExecutor
            .execute(() -> sqlDataCoreService.batchDeleteByPk(request.getTableName(), pkVals));
      }
      logger.info("DimWriterGrpcBackend,batchDeleteByPk,Y,{},{},{},{},{},{},{},",
          stopWatch.getTime(), request.getTableName(), pkVals.size(), 0, request.getFromApp(),
          request.getFromIp(), deleteCount);
      builder.setSuccess(true).setRowsJson("成功删除 " + deleteCount + " 条文档信息");
    } catch (Exception e) {
      logger.error("DimWriterGrpcBackend,batchDeleteByPk,N,{},{},{},{},{},{},{},",
          stopWatch.getTime(), request.getTableName(), pkVals.size(), 0, request.getFromApp(),
          request.getFromIp(), e.getMessage(), e);
      builder.setSuccess(false);
      builder.setErrMsg(e.getMessage());
    }
    o.onNext(builder.build());
    o.onCompleted();
  }

  @Override
  public void deleteByExample(DeleteDataByExampleRequest request,
      io.grpc.stub.StreamObserver<DataBaseResponse> o) {
    StopWatch stopWatch = StopWatch.createStarted();
    String tableName = request.getTableName();
    String exampleJson = request.getExampleJson();
    QueryExample example = J.json2Bean(request.getExampleJson(), QueryExample.class);
    DataBaseResponse.Builder builder = DataBaseResponse.newBuilder();
    try {
      Long deleteCount = tryUntilSuccess(
          () -> mongoDataCoreService.deleteByExample(tableName, example), "deleteByExample", 0);
      if (writeMysqlEnable()) {
        writeMysqlExecutor.execute(() -> sqlDataCoreService.deleteByExample(tableName, example));
      }
      logger.info("DimWriterGrpcBackend,deleteByExample,Y,{},{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, 0, 0, request.getFromApp(), request.getFromIp(),
          deleteCount);
      builder.setSuccess(true).setRowsJson("成功删除 " + deleteCount + " 条文档信息");
    } catch (Exception e) {
      logger.error("DimWriterGrpcBackend,deleteByExample,N,{},{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, exampleJson, 0, request.getFromApp(), request.getFromIp(),
          e.getMessage(), e);
      builder.setSuccess(false);
      builder.setErrMsg(e.getMessage());
    }
    o.onNext(builder.build());
    o.onCompleted();
  }

  @Override
  public void deleteByRowMap(DeleteDataByExampleRequest request,
      io.grpc.stub.StreamObserver<DataBaseResponse> o) {
    StopWatch stopWatch = StopWatch.createStarted();
    String tableName = request.getTableName();
    String exampleJson = request.getExampleJson();
    List<Map<String, Object>> example = J.fromJson(request.getExampleJson(),
        (new TypeToken<List<Map<String, Object>>>() {}).getType());
    DataBaseResponse.Builder builder = DataBaseResponse.newBuilder();
    try {
      Long deleteCount = tryUntilSuccess(
          () -> mongoDataCoreService.deleteByRowMap(tableName, example), "deleteByRowMap", 0);
      if (writeMysqlEnable()) {
        writeMysqlExecutor.execute(() -> sqlDataCoreService.deleteByRowMap(tableName, example));
      }
      logger.info("DimWriterGrpcBackend,deleteByRowMap,Y,{},{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, 0, 0, request.getFromApp(), request.getFromIp(),
          deleteCount);
      builder.setSuccess(true).setRowsJson("成功删除 " + deleteCount + " 条文档信息");
    } catch (Exception e) {
      logger.error("DimWriterGrpcBackend,deleteByRowMap,N,{},{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, exampleJson, 0, request.getFromApp(), request.getFromIp(),
          e.getMessage(), e);
      builder.setSuccess(false);
      builder.setErrMsg(e.getMessage());
    }
    o.onNext(builder.build());
    o.onCompleted();
  }

  @Override
  public void queryByExample(QueryDataByExampleRequest request,
      io.grpc.stub.StreamObserver<QueryDataResponse> o) {
    StopWatch stopWatch = StopWatch.createStarted();
    String tableName = request.getTableName();
    String exampleJson = request.getExampleJson();
    QueryExample example = J.json2Bean(exampleJson, QueryExample.class);
    List<Map<String, Object>> rows;
    QueryDataResponse.Builder builder = QueryDataResponse.newBuilder();
    try {
      DBCoreService coreService = getDbCoreService();
      rows = tryUntilSuccess(() -> coreService.queryByExample(tableName, example), "queryByExample",
          0);
      logger.info("DimWriterGrpcBackend,queryByExample,Y,{},{},{},{},{},{},", stopWatch.getTime(),
          tableName, 0, rows.size(), request.getFromApp(), request.getFromIp());
      builder.setSuccess(true);
      builder.setRowsJson(J.toJson(rows));
    } catch (Exception e) {
      logger.error("DimWriterGrpcBackend,queryByExample,N,{},{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, 0, 0, request.getFromApp(), request.getFromIp(),
          e.getMessage(), e);
      builder.setSuccess(false);
      builder.setErrMsg(e.getMessage());
    }
    o.onNext(builder.build());
    o.onCompleted();
  }

  @Override
  public void fuzzyByExample(QueryDataByExampleRequest request,
      io.grpc.stub.StreamObserver<QueryDataResponse> o) {
    StopWatch stopWatch = StopWatch.createStarted();
    String tableName = request.getTableName();
    String exampleJson = request.getExampleJson();
    QueryExample example = J.json2Bean(exampleJson, QueryExample.class);
    List<Map<String, Object>> rows;
    QueryDataResponse.Builder builder = QueryDataResponse.newBuilder();
    try {
      DBCoreService coreService = getDbCoreService();
      rows = tryUntilSuccess(() -> coreService.fuzzyByExample(tableName, example), "fuzzyByExample",
          0);
      logger.info("DimWriterGrpcBackend,fuzzyByExample,Y,{},{},{},{},{},{},", stopWatch.getTime(),
          tableName, 0, rows.size(), request.getFromApp(), request.getFromIp());
      builder.setSuccess(true);
      builder.setRowsJson(J.toJson(rows));
    } catch (Exception e) {
      logger.error("DimWriterGrpcBackend,fuzzyByExample,N,{},{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, 0, 0, request.getFromApp(), request.getFromIp(),
          e.getMessage(), e);
      builder.setSuccess(false);
      builder.setErrMsg(e.getMessage());
    }
    o.onNext(builder.build());
    o.onCompleted();
  }

  @Override
  public void queryByExampleStream(QueryDataByExampleRequest request,
      io.grpc.stub.StreamObserver<QueryDataResponse> o) {
    StopWatch stopWatch = StopWatch.createStarted();
    String exampleJson = request.getExampleJson();
    String tableName = request.getTableName();
    QueryExample example = J.json2Bean(exampleJson, QueryExample.class);
    List<Map<String, Object>> rows;
    try {
      DBCoreService coreService = getDbCoreService();
      rows = tryUntilSuccess(() -> coreService.queryByExample(tableName, example),
          "queryByExampleStream", 0);
      Iterator<Map<String, Object>> it = rows.iterator();
      int count = 0;
      int total = 0;
      List<Map<String, Object>> batchRows = new ArrayList<>();
      while (it.hasNext()) {
        if (count == ConstPool.GRPC_QUERY_RETURN_BATCH_SIZE) {
          o.onNext(QueryDataResponse.newBuilder().setSuccess(true).setRowsJson(J.toJson(batchRows))
              .build());
          batchRows.clear();
          count = 0;
        } else {
          batchRows.add(it.next());
          count++;
          total++;
        }
      }
      if (count != 0) {
        o.onNext(QueryDataResponse.newBuilder().setSuccess(true).setRowsJson(J.toJson(batchRows))
            .build());
      }
      logger.info("DimWriterGrpcBackend,queryByExampleStream,Y,{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, 0, total, request.getFromApp(), request.getFromIp());
      o.onCompleted();
    } catch (Exception e) {
      logger.error("DimWriterGrpcBackend,queryByExampleStream,N,{},{},{},{},{},{},{},",
          stopWatch.getTime(), tableName, 0, 0, request.getFromApp(), request.getFromIp(),
          e.getMessage(), e);
      o.onError(e);
    }
  }

  @Override
  public void heartBeat(DataHello request, StreamObserver<DataHello> o) {
    StopWatch stopWatch = StopWatch.createStarted();
    try {
      o.onNext(DataHello.getDefaultInstance());
      logger.info("DimWriterGrpcBackend,heatBeat,Y,{},{},{},{},{},{},", stopWatch.getTime(), "-", 0,
          0, request.getFromApp(), request.getFromIp());
      o.onCompleted();
    } catch (Exception e) {
      logger.error("DimWriterGrpcBackend,heartBeat,N,{},{},{},{},{},{},{},", stopWatch.getTime(),
          "-", 0, 0, request.getFromApp(), request.getFromIp(), e.getMessage(), e);
      o.onError(e);
    }
  }

  private <T> T tryUntilSuccess(Supplier<T> supplier, String desc, int i) {
    try {
      T t = supplier.get();
      return t;
    } catch (Throwable e) {
      String errMsg = e.getMessage();
      logger.error("DimWriterGrpcBackend,tryUntilSuccess,F,{},{},{},", i + 1, desc, errMsg);
      int maxRetryTimes = RetryPolicy.maxRetryTimes(errMsg);
      if (maxRetryTimes <= 0) {
        String message = String
            .format("DimWriterGrpcBackend,tryUntilSuccess,%s,wont retry,msg=%s. ", desc, errMsg);
        throw new RuntimeException(message, e);
      }
      if (i > maxRetryTimes) {
        String message = String.format(
            "DimWriterGrpcBackend,tryUntilSuccess,%s,reach max retry times %s,msg=%s. ", desc,
            maxRetryTimes, e.getMessage());
        throw new RuntimeException(message, e);
      }
      try {
        Thread.sleep((new Random().nextInt(10) + 1) * 100);
      } catch (InterruptedException ex) {
        logger.error(e.getMessage(), e);
      }
      T t = tryUntilSuccess(supplier, desc, i + 1);
      return t;
    }
  }

  private boolean readMysqlEnable() {
    Map<String, Map<String, MetaDataDictValue>> metaDataDictValueMap =
        superCacheService.getSc().metaDataDictValueMap;
    Map<String, MetaDataDictValue> indexKeyMaps = metaDataDictValueMap.get(ConstModel.META_CONFIG);
    if (CollectionUtils.isEmpty(indexKeyMaps)) {
      return false;
    }
    MetaDataDictValue metaDataDictValue = indexKeyMaps.get(ConstModel.READ_MYSQL_ENABLE);
    if (Objects.isNull(metaDataDictValue)) {
      return false;
    }
    return "true".equalsIgnoreCase(metaDataDictValue.getDictValue());
  }

  private boolean writeMysqlEnable() {
    Map<String, Map<String, MetaDataDictValue>> metaDataDictValueMap =
        superCacheService.getSc().metaDataDictValueMap;
    Map<String, MetaDataDictValue> indexKeyMaps = metaDataDictValueMap.get(ConstModel.META_CONFIG);
    if (CollectionUtils.isEmpty(indexKeyMaps)) {
      return false;
    }
    MetaDataDictValue metaDataDictValue = indexKeyMaps.get(ConstModel.WRITE_MYSQL_ENABLE);
    if (Objects.isNull(metaDataDictValue)) {
      return false;
    }
    return "true".equalsIgnoreCase(metaDataDictValue.getDictValue());
  }

}
