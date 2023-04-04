/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.promql;

import java.util.List;

import io.holoinsight.server.extension.model.PqlParam;
import io.holoinsight.server.extension.model.QueryResult.Result;

/**
 * @author jiwliu
 * @Description PromQL query
 * @date 2023/3/8
 */
public interface PqlQueryService {

  /**
   * instant queries，you can see
   * <a href="https://prometheus.io/docs/prometheus/latest/querying/api/">instant queries</a>
   * 
   * @param pqlParam
   * @return
   */
  List<Result> query(PqlParam pqlParam);

  /**
   * range queries，you can see
   * <a href="https://prometheus.io/docs/prometheus/latest/querying/api/">range queries</a>
   * 
   * @param pqlParam
   * @return
   */
  List<Result> queryRange(PqlParam pqlParam);
}
