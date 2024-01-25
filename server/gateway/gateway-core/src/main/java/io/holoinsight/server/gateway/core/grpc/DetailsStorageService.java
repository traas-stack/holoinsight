/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.holoinsight.server.common.ctl.MonitorProductCode;
import io.holoinsight.server.common.ctl.ProductCtlService;
import io.holoinsight.server.extension.MetricMeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.extension.DetailsStorage;
import io.holoinsight.server.extension.model.Header;
import io.holoinsight.server.extension.model.Row;
import io.holoinsight.server.extension.model.Table;
import io.holoinsight.server.gateway.grpc.DataNode;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2023/11/27
 *
 * @author xzchaoo
 */
@Component
@Slf4j
public class DetailsStorageService {
  @Autowired
  private DetailsStorage detailsStorage;

  @Autowired
  private ProductCtlService productCtlService;

  @Autowired
  private MetricMeterService metricMeterService;

  public Mono<Void> write(AuthInfo authInfo, WriteMetricsRequestV4 req) {
    List<Mono<Void>> monos = new ArrayList<>();

    for (WriteMetricsRequestV4.TaskResult tr : req.getResultsList()) {
      if (!TaskResultUtils.isDetails(tr)) {
        continue;
      }

      WriteMetricsRequestV4.Table pbtable = tr.getTable();
      WriteMetricsRequestV4.Header pbheader = pbtable.getHeader();

      Table table = new Table();
      table.setName(pbtable.getHeader().getMetricName());
      Header header = new Header();
      header.setTagKeys(pbheader.getTagKeysList());
      header.setFieldKeys(pbheader.getValueKeysList());
      table.setHeader(header);

      List<Row> rows = new ArrayList<>(pbtable.getRowsCount());
      for (WriteMetricsRequestV4.Row pbrow : pbtable.getRowsList()) {
        Row row = new Row();
        row.setTimestamp(pbrow.getTimestamp());
        row.setTagValues(pbrow.getTagValuesList());
        List<Double> fieldValues = new ArrayList<>(pbrow.getValueValuesCount());
        // empty fields are allowed
        // if (pbrow.getValueValuesCount() == 0) {
        // continue;
        // }
        for (DataNode dn : pbrow.getValueValuesList()) {
          fieldValues.add(dn.getValue());
        }
        row.setFieldValues(fieldValues);

        // check
        if (!checkClosed(authInfo.getTenant(), table.getName(), header, pbrow.getTagValuesList())) {
          continue;
        }
        rows.add(row);
      }
      table.setRows(rows);
      if (rows.size() > 0) {
        monos.add(detailsStorage.write(authInfo.getTenant(), table));
      }
    }

    return Flux.merge(monos).ignoreElements();
  }

  private Boolean checkClosed(String tenant, String metric, Header header, List<String> tagValues) {
    try {
      Map<String, String> dataTags = new HashMap<>();
      if (!CollectionUtils.isEmpty(header.getTagKeys())) {
        for (int i = 0; i < header.getTagKeys().size(); i++) {
          dataTags.put(header.getTagKeys().get(i), tagValues.get(i));
        }
        if (CollectionUtils.isEmpty(dataTags)) {
          return Boolean.TRUE;
        }
        Map<String, String> meterTags = metricMeterService.keyGen(tenant, metric, dataTags);
        if (CollectionUtils.isEmpty(meterTags)) {
          return Boolean.TRUE;
        }

        if (!productCtlService.isMetricInWhiteList(metric)
            && productCtlService.productClosed(MonitorProductCode.METRIC, meterTags)) {
          return Boolean.FALSE;
        }
      }
    } catch (Exception e) {
      log.warn("detail metric meter fail" + e.getMessage(), e);
    }
    return Boolean.TRUE;
  }
}
