/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import java.util.ArrayList;
import java.util.List;

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
        if (pbrow.getValueValuesCount() == 0) {
          continue;
        }
        for (DataNode dn : pbrow.getValueValuesList()) {
          fieldValues.add(dn.getValue());
        }
        row.setFieldValues(fieldValues);
        rows.add(row);
      }
      table.setRows(rows);
      if (rows.size() > 0) {
        monos.add(detailsStorage.write(authInfo.getTenant(), table));
      }
    }

    return Flux.merge(monos).ignoreElements();
  }
}
