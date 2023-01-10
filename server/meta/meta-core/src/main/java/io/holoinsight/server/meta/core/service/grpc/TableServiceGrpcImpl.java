/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service.grpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.grpc.stub.StreamObserver;
import io.holoinsight.server.meta.dal.service.MongoDbHelper;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest;
import io.holoinsight.server.meta.proto.table.CreateTableRequest;
import io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest;
import io.holoinsight.server.meta.proto.table.DeleteTableRequest;
import io.holoinsight.server.meta.proto.table.TableBaseResponse;
import io.holoinsight.server.meta.proto.table.TableDataResponse;
import io.holoinsight.server.meta.proto.table.TableHello;
import io.holoinsight.server.meta.proto.table.TableServiceGrpc;
import io.holoinsight.server.common.J;

import static io.holoinsight.server.meta.common.util.ConstPool.TABLE_SERVICE_THREAD_SIZE;

/**
 * @author jsy1001de
 * @version 1.0: TableServiceGrpcImpl.java, v 0.1 2022年03月07日 4:07 下午 jinsong.yjs Exp $
 */
@Service
public class TableServiceGrpcImpl extends TableServiceGrpc.TableServiceImplBase {
  private static final Logger logger = LoggerFactory.getLogger(TableServiceGrpcImpl.class);
  private static final ThreadPoolExecutor EXECUTOR =
      new ThreadPoolExecutor(TABLE_SERVICE_THREAD_SIZE, TABLE_SERVICE_THREAD_SIZE, 60,
          TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
          new BasicThreadFactory.Builder().namingPattern("table-service").daemon(true).build());
  @Autowired
  private MongoDbHelper mongoDbHelper;

  @Override
  public void createTable(CreateTableRequest request,
      io.grpc.stub.StreamObserver<TableBaseResponse> responseObserver) {
    EXECUTOR.execute(() -> {
      StopWatch stopWatch = StopWatch.createStarted();
      TableBaseResponse.Builder builder = TableBaseResponse.newBuilder();

      if (mongoDbHelper.checkIsExited(request.getTableName())) {
        logger.error("TableServiceGrpcImpl,createTable,N,{},{},{},{},{}.{},", stopWatch.getTime(),
            request.getTableName(), 0, request.getFromApp(), request.getFromIp(),
            "collection is exited");
        responseObserver.onError(new RuntimeException("collection is exited"));
        return;
      }

      try {
        mongoDbHelper.createCollection(request.getTableName());
        logger.info("TableServiceGrpcImpl,createTable,Y,{},{},{},{},{},", stopWatch.getTime(),
            request.getTableName(), 0, request.getFromApp(), request.getFromIp());
        builder.setSuccess(true);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
      } catch (Exception e) {
        logger.error("TableServiceGrpcImpl,createTable,N,{},{},{},{},{}.{},", stopWatch.getTime(),
            request.getTableName(), 0, request.getFromApp(), request.getFromIp(), e.getMessage(),
            e);
        responseObserver.onError(e);
      }
    });
  }

  @Override
  public void deleteTable(DeleteTableRequest request,
      io.grpc.stub.StreamObserver<TableBaseResponse> responseObserver) {
    EXECUTOR.execute(() -> {
      StopWatch stopWatch = StopWatch.createStarted();
      TableBaseResponse.Builder builder = TableBaseResponse.newBuilder();

      if (!mongoDbHelper.checkIsExited(request.getTableName())) {
        logger.warn("TableServiceGrpcImpl,delete,N,{},{},{},{},{}.{},", stopWatch.getTime(),
            request.getTableName(), 0, request.getFromApp(), request.getFromIp(),
            "collection is not exited");
        builder.setSuccess(true);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
        return;
      }

      try {
        mongoDbHelper.dropCollection(request.getTableName());
        logger.info("TableServiceGrpcImpl,deleteTable,Y,{},{},{},{},{},", stopWatch.getTime(),
            request.getTableName(), 0, request.getFromApp(), request.getFromIp());
        builder.setSuccess(true);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
      } catch (Exception e) {
        logger.error("TableServiceGrpcImpl,deleteTable,N,{},{},{},{},{}.{},", stopWatch.getTime(),
            request.getTableName(), 0, request.getFromApp(), request.getFromIp(), e.getMessage(),
            e);
        responseObserver.onError(e);
      }
    });
  }

  @Override
  public void createIndexKey(CreateIndexKeyRequest request,
      io.grpc.stub.StreamObserver<TableBaseResponse> responseObserver) {
    EXECUTOR.execute(() -> {
      StopWatch stopWatch = StopWatch.createStarted();
      Index index = new Index();
      index.on(request.getIndexKey(), request.getAsc() ? Direction.ASC : Direction.DESC);
      String tableName = request.getTableName();
      try {
        mongoDbHelper.createIndex(index, tableName);
        logger.info("TableServiceGrpcImpl,createIndexKey,Y,{},{},{},{},{},", stopWatch.getTime(),
            tableName, 0, request.getFromApp(), request.getFromIp());
        TableBaseResponse.Builder builder = TableBaseResponse.newBuilder();
        builder.setSuccess(true);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
      } catch (Exception e) {
        logger.error("TableServiceGrpcImpl,createIndexKey,N,{},{},{},{},{}.{},",
            stopWatch.getTime(), tableName, 0, request.getFromApp(), request.getFromIp(),
            e.getMessage(), e);
        responseObserver.onError(e);
      }
    });
  }

  @Override
  public void deleteIndexKey(DeleteIndexKeyRequest request,
      io.grpc.stub.StreamObserver<TableBaseResponse> responseObserver) {
    EXECUTOR.execute(() -> {
      StopWatch stopWatch = StopWatch.createStarted();
      String tableName = request.getTableName();
      try {
        mongoDbHelper.dropIndex(request.getIndexKey(), tableName);
        logger.info("TableServiceGrpcBackend,deleteIndexKey,Y,{},{},{},{},{},", stopWatch.getTime(),
            tableName, 0, request.getFromApp(), request.getFromIp());
        TableBaseResponse.Builder builder = TableBaseResponse.newBuilder();
        builder.setSuccess(true);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
      } catch (Exception e) {
        logger.error("TableServiceGrpcBackend,deleteIndexKey,N,{},{},{},{},{}.{},",
            stopWatch.getTime(), tableName, 0, request.getFromApp(), request.getFromIp(),
            e.getMessage(), e);
        responseObserver.onError(e);
      }
    });
  }

  @Override
  public void getIndexInfo(CreateIndexKeyRequest request,
      io.grpc.stub.StreamObserver<TableDataResponse> responseObserver) {
    EXECUTOR.execute(() -> {
      StopWatch stopWatch = StopWatch.createStarted();
      String tableName = request.getTableName();
      TableDataResponse.Builder builder = TableDataResponse.newBuilder();
      List<String> indexInfoStr = new ArrayList<>();
      try {
        List<IndexInfo> indexInfo = mongoDbHelper.getIndexInfo(tableName);
        logger.info("TableServiceGrpcImpl,getIndexInfo,Y,{},{},{},{},{},", stopWatch.getTime(),
            tableName, 0, request.getFromApp(), request.getFromIp());
        builder.setSuccess(true);

        if (!CollectionUtils.isEmpty(indexInfo)) {
          indexInfo.forEach(index -> {
            indexInfoStr.add(index.toString());
          });
        }

        builder.setRowsJson(J.toJson(indexInfoStr));
      } catch (Exception e) {
        logger.error("TableServiceGrpcImpl,getIndexInfo,N,{},{},{},{},{}.{},", stopWatch.getTime(),
            tableName, 0, request.getFromApp(), request.getFromIp(), e.getMessage(), e);
        builder.setSuccess(false);
        builder.setErrMsg(e.getMessage());
      }
      responseObserver.onNext(builder.build());
      responseObserver.onCompleted();
    });
  }

  @Override
  public void heartBeat(TableHello request, StreamObserver<TableHello> responseObserver) {
    EXECUTOR.execute(() -> {
      StopWatch stopWatch = StopWatch.createStarted();
      try {
        responseObserver.onNext(TableHello.getDefaultInstance());
        logger.info("TableServiceGrpcImpl,heatBeat,Y,{},{},{},{},{},", stopWatch.getTime(), "-", 0,
            request.getFromApp(), request.getFromIp());
        responseObserver.onCompleted();
      } catch (Exception e) {
        logger.error("TableServiceGrpcImpl,heatBeat,N,{},{},{},{},{}.{},", stopWatch.getTime(), "-",
            0, request.getFromApp(), request.getFromIp(), e.getMessage(), e);
        responseObserver.onError(e);
      }
    });
  }
}
