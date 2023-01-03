/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.model;

import io.holoinsight.server.storage.common.model.specification.sw.EndpointRelation;
import io.holoinsight.server.storage.common.model.storage.annotation.Column;
import io.holoinsight.server.storage.common.model.storage.annotation.ModelAnnotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;

import static io.holoinsight.server.storage.engine.elasticsearch.model.EndpointRelationEsDO.INDEX_NAME;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ModelAnnotation(name = INDEX_NAME)
public class EndpointRelationEsDO extends RecordEsDO {

    public static final String INDEX_NAME           = "holoinsight-endpoint_relation";
    public static final String TENANT               = "tenant";
    public static final String START_TIME           = "start_time";
    public static final String END_TIME             = "end_time";
    public static final String SOURCE_SERVICE_NAME  = "source_service_name";
    public static final String DEST_SERVICE_NAME    = "dest_service_name";
    public static final String SOURCE_ENDPOINT_NAME = "source_endpoint_name";
    public static final String DEST_ENDPOINT_NAME   = "dest_endpoint_name";
    public static final String COMPONENT         = "component";
    public static final String TRACE_STATUS                 = "trace_status";
    public static final String LATENCY              = "latency";
    public static final String ENTITY_ID            = "entity_id";
    public static final String TRACE_ID             = "trace_id";
    public static final String APPP_ID                = "app_id";
    public static final String ENV_ID                = "env_id";
    public static final String STAMP                 = "stamp";
    public static final String TYPE                = "type";

    @Id
    private String id;
    @Column(name = TENANT)
    private String tenant;
    @Column(name = SOURCE_SERVICE_NAME)
    private String sourceServiceName;
    @Column(name = DEST_SERVICE_NAME)
    private String destServiceName;
    @Column(name = SOURCE_ENDPOINT_NAME)
    private String sourceEndpointName;
    @Column(name = DEST_ENDPOINT_NAME)
    private String destEndpointName;
    @Column(name = COMPONENT)
    private String    component;
    @Column(name = START_TIME)
    private long   startTime;
    @Column(name = END_TIME)
    private long   endTime;
    @Column(name = TRACE_STATUS)
    private int                 traceStatus;
    @Column(name = LATENCY)
    private int    latency;
    @Column(name = ENTITY_ID)
    private String entityId;
    @Column(name = TRACE_ID)
    private String traceId;
    @Column(name = APPP_ID)
    private String      appId;
    @Column(name = ENV_ID)
    private String      envId;
    @Column(name = STAMP)
    private String stamp;
    @Column(name = TYPE)
    private String type;

    @Override
    public String indexName() {
        return INDEX_NAME;
    }

    public static EndpointRelationEsDO fromEndpointRelation(EndpointRelation endpointRelation) {
        EndpointRelationEsDO endpointRelationEsDO = new EndpointRelationEsDO();
        BeanUtils.copyProperties(endpointRelation, endpointRelationEsDO);
        return endpointRelationEsDO;
    }
}
