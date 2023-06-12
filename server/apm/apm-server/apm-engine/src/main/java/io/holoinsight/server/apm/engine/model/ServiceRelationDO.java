/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.model;

import io.holoinsight.server.apm.common.model.specification.sw.ServiceRelation;
import io.holoinsight.server.apm.common.model.storage.annotation.Column;
import io.holoinsight.server.apm.common.model.storage.annotation.ModelAnnotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;

import static io.holoinsight.server.apm.engine.model.ServiceRelationDO.INDEX_NAME;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ModelAnnotation(name = INDEX_NAME)
public class ServiceRelationDO extends RecordDO {

  public static final String INDEX_NAME = "holoinsight-service_relation";
  public static final String TENANT = "tenant";
  public static final String START_TIME = "start_time";
  public static final String END_TIME = "end_time";
  public static final String SOURCE_SERVICE_NAME = "source_service_name";

  public static final String DEST_SERVICE_NAME = "dest_service_name";
  public static final String COMPONENT = "component";
  public static final String TRACE_STATUS = "trace_status";
  public static final String LATENCY = "latency";
  public static final String ENTITY_ID = "entity_id";
  public static final String TRACE_ID = "trace_id";
  public static final String TYPE = "type";

  @Id
  private String id;
  @Column(name = TENANT)
  private String tenant;
  @Column(name = SOURCE_SERVICE_NAME)
  private String sourceServiceName;
  @Column(name = DEST_SERVICE_NAME)
  private String destServiceName;
  @Column(name = COMPONENT)
  private String component;
  @Column(name = START_TIME)
  private long startTime;
  @Column(name = END_TIME)
  private long endTime;
  @Column(name = TRACE_STATUS)
  private int traceStatus;
  @Column(name = LATENCY)
  private int latency;
  @Column(name = ENTITY_ID)
  private String entityId;
  @Column(name = TRACE_ID)
  private String traceId;
  @Column(name = TYPE)
  private String type;

  @Override
  public String indexName() {
    return INDEX_NAME;
  }

  public static ServiceRelationDO fromServiceRelation(ServiceRelation serviceRelation) {
    ServiceRelationDO serviceRelationEsDO = new ServiceRelationDO();
    BeanUtils.copyProperties(serviceRelation, serviceRelationEsDO);
    serviceRelationEsDO.setTimestamp(serviceRelation.getEndTime());
    return serviceRelationEsDO;
  }
}
