/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.model;

import io.holoinsight.server.apm.common.model.specification.sw.SlowSql;
import io.holoinsight.server.apm.common.model.storage.annotation.Column;
import io.holoinsight.server.apm.common.model.storage.annotation.ModelAnnotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;

import static io.holoinsight.server.apm.engine.model.SlowSqlDO.INDEX_NAME;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ModelAnnotation(name = INDEX_NAME)
public class SlowSqlDO extends RecordDO {

  public static final String INDEX_NAME = "holoinsight-slow-sql";
  public static final String ADDRESS = "address";
  public static final String SERVICE_NAME = "service_name";
  public static final String STATEMENT = "statement";
  public static final String START_TIME = "start_time";
  public static final String TENANT = "tenant";
  public static final String LATENCY = "latency";
  public static final String TRACE_ID = "trace_id";

  @Id
  private String id;
  @Column(name = TENANT)
  private String tenant;
  @Column(name = ADDRESS)
  private String address;
  @Column(name = SERVICE_NAME)
  private String serviceName;
  @Column(name = STATEMENT)
  private String statement;
  @Column(name = TRACE_ID)
  private String traceId;
  @Column(name = START_TIME)
  private long startTime;
  @Column(name = LATENCY)
  private int latency;

  @Override
  public String indexName() {
    return INDEX_NAME;
  }

  public static SlowSqlDO fromSlowSql(SlowSql slowSql) {
    SlowSqlDO slowSqlEsDO = new SlowSqlDO();
    BeanUtils.copyProperties(slowSql, slowSqlEsDO);
    slowSqlEsDO.setTimestamp(slowSql.getEndTime());
    return slowSqlEsDO;
  }

}
