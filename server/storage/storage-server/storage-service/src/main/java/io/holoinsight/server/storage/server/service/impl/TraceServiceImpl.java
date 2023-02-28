/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.Pagination;
import io.holoinsight.server.storage.common.model.query.QueryOrder;
import io.holoinsight.server.storage.common.model.query.StatisticData;
import io.holoinsight.server.storage.common.model.query.TraceBrief;
import io.holoinsight.server.storage.common.model.specification.sw.Tag;
import io.holoinsight.server.storage.common.model.specification.sw.Trace;
import io.holoinsight.server.storage.common.model.specification.sw.TraceState;
import io.holoinsight.server.storage.engine.storage.SpanStorage;
import io.holoinsight.server.storage.engine.model.SpanDO;
import io.holoinsight.server.storage.server.service.TraceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jiwliu
 * @version : TraceServiceImpl.java, v 0.1 2022年09月20日 16:48 xiangwanpeng Exp $
 */
@Service
@ConditionalOnFeature("trace")
@Slf4j
public class TraceServiceImpl implements TraceService {

  @Resource
  @Qualifier("spanEsStorage")
  private SpanStorage spanEsStorage;

  @Resource
  @Qualifier("spanTatrisStorage")
  private SpanStorage spanTatrisStorage;

  private static final ThreadPoolExecutor EXECUTOR =
      new ThreadPoolExecutor(4, 4, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
          new BasicThreadFactory.Builder().namingPattern("tatris-%d").build());

  @Override
  public TraceBrief queryBasicTraces(String tenant, String serviceName, String serviceInstanceName,
      String endpointName, List<String> traceIds, int minTraceDuration, int maxTraceDuration,
      TraceState traceState, QueryOrder queryOrder, Pagination paging, long start, long end,
      List<Tag> tags) throws Exception {
    // temporarily double-query to compare performance between tatris and es
    if (spanTatrisStorage != null) {
      EXECUTOR.submit(() -> {
        try {
          spanTatrisStorage.queryBasicTraces(tenant, serviceName, serviceInstanceName, endpointName,
              traceIds, minTraceDuration, maxTraceDuration, traceState, queryOrder, paging, start,
              end, tags);
        } catch (Exception e) {
          log.error("[apm] tatris query_basic_traces failed, msg={}", e.getMessage(), e);
        }
      });
    }
    return spanEsStorage.queryBasicTraces(tenant, serviceName, serviceInstanceName, endpointName,
        traceIds, minTraceDuration, maxTraceDuration, traceState, queryOrder, paging, start, end,
        tags);
  }

  @Override
  public Trace queryTrace(String traceId) throws Exception {
    // temporarily double-query to compare performance between tatris and es
    if (spanTatrisStorage != null) {
      EXECUTOR.submit(() -> {
        try {
          spanTatrisStorage.queryTrace(traceId);
        } catch (Exception e) {
          log.error("[apm] tatris query_trace failed, msg={}", e.getMessage(), e);
        }
      });
    }
    return spanEsStorage.queryTrace(traceId);
  }

  @Override
  public void insertSpans(List<SpanDO> spans) throws Exception {
    // temporarily double-write to compare performance between tatris and es
    if (spanTatrisStorage != null) {
      EXECUTOR.submit(() -> {
        try {
          spanTatrisStorage.batchInsert(spans);
        } catch (Exception e) {
          log.error("[apm] tatris batch_insert failed, msg={}", e.getMessage(), e);
        }
      });
    }
    spanEsStorage.batchInsert(spans);
  }

  @Override
  public List<StatisticData> statisticTrace(long startTime, long endTime) throws Exception {
    return spanTatrisStorage.statisticTrace(startTime, endTime);
  }
}
