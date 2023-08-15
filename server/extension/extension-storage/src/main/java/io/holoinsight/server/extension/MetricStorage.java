/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension;

import java.util.List;

import io.holoinsight.server.extension.model.DetailResult;
import io.holoinsight.server.extension.model.QueryMetricsParam;
import io.holoinsight.server.extension.model.QueryResult.Result;
import io.holoinsight.server.extension.model.WriteMetricsParam;
import io.holoinsight.server.extension.model.PqlParam;
import io.holoinsight.server.extension.model.QueryParam;
import reactor.core.publisher.Mono;

/**
 * @author jiwliu
 * @date 2023/1/04
 */
public interface MetricStorage {

  Mono<Void> write(WriteMetricsParam writeMetricsParam);

  List<Result> queryData(QueryParam queryParam);

  List<Result> queryTags(QueryParam queryParam);

  void deleteKeys(QueryParam queryParam);

  Result querySchema(QueryParam queryParam);

  List<String> queryMetrics(QueryMetricsParam queryParam);

  List<Result> pqlInstantQuery(PqlParam pqlParam);

  List<Result> pqlRangeQuery(PqlParam pqlParam);

  DetailResult queryDetail(QueryParam queryParam);

}
