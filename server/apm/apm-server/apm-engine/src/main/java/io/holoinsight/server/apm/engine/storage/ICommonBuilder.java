/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.common.model.query.ResponseMetric;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import java.util.Map;

public interface ICommonBuilder {
  void addTermParams(BoolQueryBuilder queryBuilder, Map<String, String> termParams);

  void addTermParamsWithAttrPrefix(BoolQueryBuilder queryBuilder, Map<String, String> termParams);

  TermsAggregationBuilder buildAgg(String aggField);

  ResponseMetric buildMetric(Terms.Bucket bucket);

  ResponseMetric buildMetricWithDistinct(Terms.Bucket bucket);
}
