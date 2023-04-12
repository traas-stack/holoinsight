/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.common.model.query.ResponseMetric;
import io.holoinsight.server.apm.engine.model.ServiceRelationDO;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.apm.engine.storage.ICommonBuilder;
import io.opentelemetry.proto.trace.v1.Status;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.elasticsearch.search.aggregations.metrics.Percentile;
import org.elasticsearch.search.aggregations.metrics.Percentiles;
import org.elasticsearch.search.aggregations.metrics.ValueCount;

import java.util.Map;

public class CommonBuilder implements ICommonBuilder {

  @Override
  public void addTermParams(BoolQueryBuilder queryBuilder, Map<String, String> termParams) {
    if (termParams != null && termParams.size() > 0) {
      termParams.keySet().forEach(k -> {
        queryBuilder.must(QueryBuilders.termQuery(k, termParams.get(k)));
      });
    }
  }

  /**
   * The tags of the query need to add 'attributes.' as a prefix when query from holoinsight-span
   * 
   * @param queryBuilder
   * @param termParams
   */
  @Override
  public void addTermParamsWithAttrPrefix(BoolQueryBuilder queryBuilder,
      Map<String, String> termParams) {
    if (termParams != null && termParams.size() > 0) {
      termParams.keySet().forEach(k -> {
        String value = termParams.get(k);
        queryBuilder.must(QueryBuilders.termQuery(SpanDO.attributes(k), value));
      });
    }
  }

  @Override
  public TermsAggregationBuilder buildAgg(String aggField) {
    TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(aggField).field(aggField)
        .subAggregation(AggregationBuilders.terms(ServiceRelationDO.COMPONENT)
            .field(ServiceRelationDO.COMPONENT).executionHint("map")
            .collectMode(Aggregator.SubAggCollectionMode.BREADTH_FIRST))
        .subAggregation(AggregationBuilders.avg("avg_latency").field(ServiceRelationDO.LATENCY))
        .subAggregation(AggregationBuilders.percentiles("percentiles_latency")
            .field(ServiceRelationDO.LATENCY).percentiles(95.0, 99.0))
        .subAggregation(AggregationBuilders.count("total_count").field(ServiceRelationDO.TRACE_ID))
        .subAggregation(AggregationBuilders
            .filter(ServiceRelationDO.TRACE_STATUS,
                QueryBuilders.termQuery(ServiceRelationDO.TRACE_STATUS,
                    Status.StatusCode.STATUS_CODE_ERROR_VALUE))
            .subAggregation(AggregationBuilders.cardinality("error_count").field("trace_id")))
        .executionHint("map").collectMode(Aggregator.SubAggCollectionMode.BREADTH_FIRST).size(1000);

    return aggregationBuilder;
  }

  @Override
  public ResponseMetric buildMetric(Terms.Bucket bucket) {
    Avg avgLatency = bucket.getAggregations().get("avg_latency");
    double latency = Double.valueOf(avgLatency.getValue());

    Percentiles percentiles = bucket.getAggregations().get("percentiles_latency");

    double p95Latency = 0;
    double p99Latency = 0;
    for (Percentile percentile : percentiles) {
      if (percentile.getPercent() == 95) {
        p95Latency = percentile.getValue();
      } else if (percentile.getPercent() == 99) {
        p99Latency = percentile.getValue();
      }
    }

    ValueCount totalTerm = bucket.getAggregations().get("total_count");
    int totalCount = (int) totalTerm.getValue();

    Filter errFilter = bucket.getAggregations().get(ServiceRelationDO.TRACE_STATUS);
    ParsedCardinality errorTerm = errFilter.getAggregations().get("error_count");
    int errorCount = (int) errorTerm.getValue();

    ResponseMetric metric =
        new ResponseMetric(latency, p95Latency, p99Latency, totalCount, errorCount);

    return metric;
  }

  @Override
  public ResponseMetric buildMetricWithDistinct(Terms.Bucket bucket) {
    Avg avgLatency = bucket.getAggregations().get("avg_latency");
    double latency = Double.valueOf(avgLatency.getValue());

    Percentiles p95Percentiles = bucket.getAggregations().get("p95_latency");
    double p95Latency = p95Percentiles.iterator().next().getValue();

    Percentiles p99Percentiles = bucket.getAggregations().get("p99_latency");
    double p99Latency = p99Percentiles.iterator().next().getValue();

    ParsedCardinality totalTerm = bucket.getAggregations().get("total_count");
    int totalCount = (int) totalTerm.getValue();

    Filter errFilter = bucket.getAggregations().get(ServiceRelationDO.TRACE_STATUS);
    ParsedCardinality errorTerm = errFilter.getAggregations().get("error_count");
    int errorCount = (int) errorTerm.getValue();

    ResponseMetric metric =
        new ResponseMetric(latency, p95Latency, p99Latency, totalCount, errorCount);

    return metric;
  }
}

