/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiwliu
 * @version : Span.java, v 0.1 2022年09月20日 15:30 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Span implements Serializable {

  private static final long serialVersionUID = 98205008356521571L;

  private String traceId;

  private String spanId;

  private String parentSpanId;

  private List<Ref> refs = new ArrayList<>();

  private String serviceCode;

  private String serviceInstanceName;

  private long startTime;

  private long endTime;

  private String endpointName;

  private String type;

  private String peer;

  private String component;

  private boolean isError;

  private String layer;
  private List<KeyValue> tags = new ArrayList<>();
  private List<LogEntity> logs = new ArrayList<>();

  private boolean isRoot;
  private boolean isMesh;

}
