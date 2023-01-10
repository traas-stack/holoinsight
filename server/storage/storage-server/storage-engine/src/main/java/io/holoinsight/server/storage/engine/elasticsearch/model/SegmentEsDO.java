/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.model;

import io.holoinsight.server.storage.common.model.specification.sw.Segment;
import io.holoinsight.server.storage.common.model.specification.sw.Tag;
import io.holoinsight.server.storage.common.model.storage.annotation.Column;
import io.holoinsight.server.storage.common.model.storage.annotation.ModelAnnotation;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Set;

import static io.holoinsight.server.storage.engine.elasticsearch.model.SegmentEsDO.INDEX_NAME;

/**
 * @author jiwliu
 * @version : SegmentEsDO.java, v 0.1 2022年09月18日 00:45 wanpeng.xwp Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ModelAnnotation(name = INDEX_NAME)
public class SegmentEsDO extends RecordEsDO {

  private static final long serialVersionUID = 6774160380930019988L;

  public static final String INDEX_NAME = "holoinsight-segment";

  public static final String ADDITIONAL_TAG_TABLE = "segment_tag";

  public static final String TENANT = "tenant";

  public static final String AGENT_VERSION = "agent_version";
  public static final String SEGMENT_ID = "segment_id";
  public static final String TRACE_ID = "trace_id";
  public static final String SERVICE_NAME = "service_name";
  public static final String SERVICE_INSTANCE_NAME = "service_instance_name";
  public static final String ENDPOINT_NAME = "endpoint_name";
  public static final String START_TIME = "start_time";
  public static final String END_TIME = "end_time";
  public static final String LATENCY = "latency";
  public static final String IS_ERROR = "is_error";
  public static final String DATA_BINARY = "data_binary";
  public static final String TAGS = "tags";
  public static final String ENTRYLAYER = "entry_layer";
  public static final String ENTRYROOTERRORCODE = "entry_root_error_code";
  public static final String ENTRYERRORCODE = "entry_error_code";
  public static final String STAMP = "stamp";
  public static final String HAS_ENTRY = "has_entry";

  public static final Set<String> RESERVED_FIELDS =
      Sets.newHashSet(TENANT, AGENT_VERSION, SEGMENT_ID, TRACE_ID, SERVICE_NAME,
          SERVICE_INSTANCE_NAME, ENDPOINT_NAME, START_TIME, END_TIME, LATENCY, IS_ERROR,
          DATA_BINARY, ENTRYLAYER, ENTRYROOTERRORCODE, ENTRYERRORCODE, STAMP, HAS_ENTRY);

  @Id
  private String id;
  @Column(name = TENANT)
  private String tenant;
  @Column(name = SEGMENT_ID)
  private String segmentId;
  @Column(name = TRACE_ID)
  private String traceId;
  @Column(name = SERVICE_NAME)
  private String serviceName;
  @Column(name = SERVICE_INSTANCE_NAME)
  private String serviceInstanceName;
  @Column(name = ENDPOINT_NAME)
  private String endpointName;
  @Column(name = START_TIME)
  private long startTime;
  @Column(name = END_TIME)
  private long endTime;
  @Column(name = LATENCY)
  private int latency;
  @Column(name = IS_ERROR)
  private int isError;
  @Column(name = DATA_BINARY)
  private byte[] dataBinary;
  @Column(name = TAGS)
  private List<String> tags;
  @Column(name = HAS_ENTRY)
  private int hasEntry;

  // for bizops
  @Column(name = ENTRYLAYER)
  private String entryLayer;
  @Column(name = ENTRYROOTERRORCODE)
  private String entryRootErrorCode;
  @Column(name = ENTRYERRORCODE)
  private String entryErrorCode;
  @Column(name = STAMP)
  private String stamp;

  @Override
  public String indexName() {
    return INDEX_NAME;
  }

  public static SegmentEsDO fromSegment(Segment segment) {
    SegmentEsDO segmentEsDO = new SegmentEsDO();
    BeanUtils.copyProperties(segment, segmentEsDO, SegmentEsDO.TAGS);
    segmentEsDO.setTags(Tag.Util.toStringList(segment.getTags()));
    return segmentEsDO;
  }

}
