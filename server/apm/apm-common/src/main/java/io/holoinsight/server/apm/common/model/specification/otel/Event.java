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
 * @version : Event.java, v 0.1 2022年10月29日 17:17 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
  private long timeUnixNano;
  private String name;
  private List<KeyValue> attributes;
  private int droppedAttributesCount;
}
