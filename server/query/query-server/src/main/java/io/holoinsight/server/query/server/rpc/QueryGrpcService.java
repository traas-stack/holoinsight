/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.server.rpc;

import io.holoinsight.server.query.grpc.QueryProto;
import io.holoinsight.server.query.grpc.QueryServiceGrpc;
import io.holoinsight.server.query.service.QueryService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * created at 2022/11/26
 *
 * @author xiangwanpeng
 */
public class QueryGrpcService extends QueryServiceGrpc.QueryServiceImplBase {

  @Autowired
  private QueryService queryService;

  @Override
  public void queryData(QueryProto.QueryRequest request,
      StreamObserver<QueryProto.QueryResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryData(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryDetailData(QueryProto.QueryRequest request,
      StreamObserver<QueryProto.QueryDetailResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryDetailData(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryTags(QueryProto.QueryRequest request,
      StreamObserver<QueryProto.QueryResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryTags(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void deleteKeys(QueryProto.QueryRequest request,
      StreamObserver<QueryProto.QueryResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.deleteKeys(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void querySchema(QueryProto.QueryRequest request,
      StreamObserver<QueryProto.QuerySchemaResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.querySchema(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryMetrics(QueryProto.QueryMetricsRequest request,
      StreamObserver<QueryProto.QueryMetricsResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryMetrics(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void pqlInstantQuery(QueryProto.PqlInstantRequest request,
      StreamObserver<QueryProto.QueryResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.pqlInstantQuery(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void pqlRangeQuery(QueryProto.PqlRangeRequest request,
      StreamObserver<QueryProto.QueryResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.pqlRangeQuery(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryBasicTraces(QueryProto.QueryTraceRequest request,
      StreamObserver<QueryProto.TraceBrief> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryBasicTraces(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryTrace(QueryProto.QueryTraceRequest request,
      StreamObserver<QueryProto.Trace> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryTrace(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryTraceTree(QueryProto.QueryTraceRequest request,
      StreamObserver<QueryProto.TraceTreeList> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryTraceTree(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryServiceList(QueryProto.QueryMetaRequest request,
      StreamObserver<QueryProto.QueryMetaResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryServiceList(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryEndpointList(QueryProto.QueryMetaRequest request,
      StreamObserver<QueryProto.QueryMetaResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryEndpointList(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryServiceInstanceList(QueryProto.QueryMetaRequest request,
      StreamObserver<QueryProto.QueryMetaResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryServiceInstanceList(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryComponentList(QueryProto.QueryMetaRequest request,
      StreamObserver<QueryProto.QueryVirtualComponentResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryComponentList(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryComponentTraceIds(QueryProto.QueryMetaRequest request,
      StreamObserver<QueryProto.TraceIds> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryComponentTraceIds(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryTopology(QueryProto.QueryTopologyRequest request,
      StreamObserver<QueryProto.Topology> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryTopology(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void querySlowSqlList(QueryProto.QueryMetaRequest request,
      StreamObserver<QueryProto.QuerySlowSqlResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.querySlowSqlList(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryServiceErrorList(QueryProto.QueryMetaRequest request,
      StreamObserver<QueryProto.CommonMapTypeDataList> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryServiceErrorList(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryServiceErrorDetail(QueryProto.QueryMetaRequest request,
      StreamObserver<QueryProto.CommonMapTypeDataList> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryServiceErrorDetail(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void statisticTrace(QueryProto.StatisticRequest request,
      StreamObserver<QueryProto.StatisticDataList> responseObserver) {
    try {
      responseObserver.onNext(queryService.statisticTrace(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }

  @Override
  public void queryEvents(QueryProto.QueryEventRequest request,
      StreamObserver<QueryProto.QueryEventResponse> responseObserver) {
    try {
      responseObserver.onNext(queryService.queryEvents(request));
      responseObserver.onCompleted();
    } catch (Throwable t) {
      responseObserver.onError(
          Status.INTERNAL.withCause(t).withDescription(t.getMessage()).asRuntimeException());
    }
  }
}
