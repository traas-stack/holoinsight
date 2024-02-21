/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension;

import java.util.Collections;
import java.util.List;

import io.holoinsight.server.extension.model.DetailResult;
import io.holoinsight.server.extension.model.PqlParam;
import io.holoinsight.server.extension.model.QueryMetricsParam;
import io.holoinsight.server.extension.model.QueryParam;
import io.holoinsight.server.extension.model.QueryResult;
import io.holoinsight.server.extension.model.WriteMetricsParam;
import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2023/1/29
 *
 * @author xzchaoo
 */
public class NoopMetricStorage implements MetricStorage {
  @Override
  public Mono<Void> write(WriteMetricsParam writeMetricsParam) {
    return Mono.empty();
  }

  @Override
  public List<QueryResult.Result> queryData(QueryParam queryParam) {
    return Collections.emptyList();
  }

  @Override
  public List<QueryResult.Result> queryTags(QueryParam queryParam) {
    return Collections.emptyList();
  }

  @Override
  public void deleteKeys(QueryParam queryParam) {

  }

  @Override
  public QueryResult.Result querySchema(QueryParam queryParam) {
    return new QueryResult.Result();
  }

  @Override
  public List<String> queryMetrics(QueryMetricsParam queryParam) {
    return Collections.emptyList();
  }

  @Override
  public List<QueryResult.Result> pqlInstantQuery(PqlParam pqlParam) {
    return Collections.emptyList();
  }

  @Override
  public List<QueryResult.Result> pqlRangeQuery(PqlParam pqlParam) {
    return Collections.emptyList();
  }

  @Override
  public DetailResult queryDetail(QueryParam queryParam) {
    return DetailResult.empty();
  }
}
