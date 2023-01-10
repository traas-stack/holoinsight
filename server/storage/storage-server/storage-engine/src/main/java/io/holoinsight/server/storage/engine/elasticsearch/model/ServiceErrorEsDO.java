/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.model;

import io.holoinsight.server.storage.common.model.specification.sw.ErrorInfo;
import io.holoinsight.server.storage.common.model.storage.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
// @ModelAnnotation(name = INDEX_NAME)
public class ServiceErrorEsDO extends RecordEsDO {

  public static final String INDEX_NAME = "holoinsight-service_error";

  public static final String TENANT = "tenant";

  public static final String TRACE_ID = "trace_id";
  public static final String SERVICE_NAME = "service_name";
  public static final String SERVICE_INSTANCE_NAME = "service_instance_name";
  public static final String START_TIME = "start_time";
  public static final String END_TIME = "end_time";
  public static final String ERROR_KIND = "error_kind";

  @Id
  private String id;
  @Column(name = TENANT)
  private String tenant;
  @Column(name = TRACE_ID)
  private String traceId;
  @Column(name = SERVICE_NAME)
  private String serviceName;
  @Column(name = SERVICE_INSTANCE_NAME)
  private String serviceInstanceName;
  @Column(name = START_TIME)
  private long startTime;
  @Column(name = END_TIME)
  private long endTime;
  @Column(name = ERROR_KIND)
  private String errorKind;

  @Override
  public String indexName() {
    return INDEX_NAME;
  }

  public static ServiceErrorEsDO fromErrorInfo(ErrorInfo errorInfo) {
    ServiceErrorEsDO serviceErrorEsDO = new ServiceErrorEsDO();
    BeanUtils.copyProperties(errorInfo, serviceErrorEsDO);
    return serviceErrorEsDO;
  }

}
