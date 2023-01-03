/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.web.impl;

import io.holoinsight.server.storage.web.MetricApi;
import io.holoinsight.server.storage.common.model.query.MetricValues;
import io.holoinsight.server.storage.common.model.query.QueryMetricRequest;
import io.holoinsight.server.storage.server.service.MetricService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

/**
 * @author jiwliu
 * @version : TraceApiController.java, v 0.1 2022年09月20日 16:11 wanpeng.xwp Exp $
 */
@Slf4j
public class MetricApiController implements MetricApi {

    @Autowired
    private MetricService metricService;

    @Override
    public ResponseEntity<List<String>> listMetrics() throws IOException {
        List<String> metrics = metricService.listMetrics();
        return ResponseEntity.ok(metrics);
    }

    @Override
    public ResponseEntity<MetricValues> queryMetricData(QueryMetricRequest request) throws IOException {
        MetricValues metricValues = metricService.queryMetric(request.getTenant(), request.getMetric(), request.getDuration(),
                request.getConditions());
        return ResponseEntity.ok(metricValues);
    }

    @Override
    public ResponseEntity<List<String>> queryMetricSchema(QueryMetricRequest request) throws IOException {
        return ResponseEntity.ok(metricService.querySchema(request.getMetric()));
    }
}