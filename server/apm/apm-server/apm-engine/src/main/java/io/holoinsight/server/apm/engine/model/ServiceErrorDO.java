/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.model;

import io.holoinsight.server.apm.common.model.storage.annotation.Column;
import io.holoinsight.server.apm.common.model.storage.annotation.ModelAnnotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import static io.holoinsight.server.apm.engine.model.ServiceErrorDO.INDEX_NAME;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ModelAnnotation(name = INDEX_NAME)
public class ServiceErrorDO extends RecordDO {

  public static final String INDEX_NAME = "holoinsight-service-error";

  public static final String TENANT = "tenant";
  public static final String SERVICE_NAME = "service_name";
  public static final String ENDPOINT_NAME = "endpoint_name";
  public static final String SERVICE_INSTANCE_NAME = "service_instance_name";
  public static final String TRACE_ID = "trace_id";
  public static final String SPAN_ID = "span_id";
  public static final String ERROR_KIND = "error_kind";
  public static final String START_TIME = "start_time";
  public static final String LATENCY = "latency";

  @Id
  private String id;
  @Column(name = TENANT)
  private String tenant;
  @Column(name = SERVICE_NAME)
  private String serviceName;
  @Column(name = ENDPOINT_NAME)
  private String endpointName;
  @Column(name = SERVICE_INSTANCE_NAME)
  private String serviceInstanceName;
  @Column(name = TRACE_ID)
  private String traceId;
  @Column(name = SPAN_ID)
  private String spanId;
  @Column(name = ERROR_KIND)
  private String errorKind;
  @Column(name = START_TIME)
  private long startTime;
  @Column(name = LATENCY)
  private int latency;

  @Override
  public String indexName() {
    return INDEX_NAME;
  }

}
