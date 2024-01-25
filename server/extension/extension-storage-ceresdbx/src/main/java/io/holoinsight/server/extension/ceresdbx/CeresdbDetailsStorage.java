/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.ceresdbx;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.ceresdb.CeresDBClient;
import io.ceresdb.models.Err;
import io.ceresdb.models.Point;
import io.ceresdb.models.Result;
import io.ceresdb.models.SqlQueryOk;
import io.ceresdb.models.SqlQueryRequest;
import io.ceresdb.models.Value;
import io.ceresdb.models.WriteRequest;
import io.holoinsight.server.extension.DetailsStorage;
import io.holoinsight.server.extension.MetricMeterService;
import io.holoinsight.server.extension.model.Header;
import io.holoinsight.server.extension.model.Row;
import io.holoinsight.server.extension.model.Table;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2023/11/27
 *
 * @author xzchaoo
 */
@Slf4j
public class CeresdbDetailsStorage implements DetailsStorage {
  @Autowired
  private CeresdbxClientManager ceresdbxClientManager;
  @Autowired
  private MetricMeterService metricMeterService;
  private Cache<String, Boolean> cache = //
      CacheBuilder.newBuilder() //
          .expireAfterWrite(Duration.ofHours(1)) //
          .build(); //

  private String forceTenant;

  public CeresdbDetailsStorage() {}

  public CeresdbDetailsStorage(String forceTenant) {
    this.forceTenant = forceTenant;
  }

  @Override
  public Mono<Void> write(String tenant, Table table) {
    metricMeterService.meter(tenant, table);

    String forceTenant = StringUtils.firstNonEmpty(this.forceTenant, tenant);

    CeresDBClient client = ceresdbxClientManager.getClient(forceTenant);

    ensureTableSchema(client, table);

    List<Point> points = new ArrayList<>(table.getRows().size());
    Header header = table.getHeader();
    for (Row row : table.getRows()) {
      Point.PointBuilder pointB =
          Point.newPointBuilder(table.getName()).setTimestamp(row.getTimestamp());
      for (int i = 0; i < header.getTagKeys().size(); i++) {
        pointB.addTag(header.getTagKeys().get(i), row.getTagValues().get(i));
      }
      for (int i = 0; i < header.getFieldKeys().size(); i++) {
        pointB.addField(header.getFieldKeys().get(i),
            Value.withDouble(row.getFieldValues().get(i)));
      }
      points.add(pointB.build());
    }

    return Mono.create(sink -> {
      long begin = System.currentTimeMillis();
      client.write(new WriteRequest(points)).thenAccept(r -> {
        long cost = System.currentTimeMillis() - begin;
        log.info("details write table=[{}] count=[{}] cost=[{}]", table.getName(), points.size(),
            cost);

        if (r.isOk()) {
          sink.success();
        } else {
          sink.error(new RuntimeException("write CeresDB error: " + r.getErr()));
        }
      });
    });
  }

  @SneakyThrows
  private void ensureTableSchema(CeresDBClient client, Table table) {
    if (cache.getIfPresent(table.getName()) == Boolean.TRUE) {
      return;
    }

    SqlQueryRequest req = SqlQueryRequest.newBuilder() //
        .forTables(table.getName()) //
        .sql("SHOW CREATE TABLE " + table.getName()) //
        .build(); //

    // update_mode = 'APPEND'
    Result<SqlQueryOk, Err> r = client.sqlQuery(req).get();

    if (r.isOk()) {
      String createTableSQL =
          r.getOk().getRowList().get(0).getColumn("Create Table").getValue().getString();
      if (createTableSQL.contains("update_mode='APPEND'")) {
        cache.put(table.getName(), Boolean.TRUE);
        return;
      }
      // need to drop and recreate
      SqlQueryRequest dropTableReq = SqlQueryRequest.newBuilder().forTables(table.getName())
          .sql("DROP TABLE IF EXISTS " + table.getName()).build();

      r = client.sqlQuery(dropTableReq).get();

      if (!r.isOk()) {
        throw new IllegalStateException("fail to drop wrong schema table " + table.getName());
      }
    } else {
      log.warn("fail to show create table {}, err=[{}]", table.getName(), r.getErr());
    }

    StringBuilder sb = new StringBuilder("CREATE TABLE `" + table.getName() + "` (");
    sb.append("`period` timestamp NOT NULL,");
    for (String tagKey : table.getHeader().getTagKeys()) {
      sb.append("`" + tagKey + "` string TAG NULL,");
    }
    for (String fieldKey : table.getHeader().getFieldKeys()) {
      sb.append("`" + fieldKey + "` double NULL,");
    }
    sb.append("TIMESTAMP KEY(period)");
    sb.append(") ENGINE=Analytic WITH(update_mode='APPEND')");

    SqlQueryRequest createTableReq = SqlQueryRequest.newBuilder() //
        .forTables(table.getName()) //
        .sql(sb.toString()) //
        .build(); //

    r = client.sqlQuery(createTableReq).get();
    if (r.isOk()) {
      log.info("create table {} successfully", table.getName());
      cache.put(table.getName(), Boolean.TRUE);
      return;
    }
    log.error("fail to create table {}, err=[{}]", table.getName(), r.getErr());
  }
}
