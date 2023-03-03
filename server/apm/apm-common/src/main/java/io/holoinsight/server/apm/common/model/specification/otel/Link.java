/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.otel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jiwliu
 * @version : Link.java, v 0.1 2022年10月29日 17:21 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Link {

  private String traceId;
  private String spanId;
  private String traceState;
  private List<KeyValue> attributes;
  private int droppedAttributesCount;

}
