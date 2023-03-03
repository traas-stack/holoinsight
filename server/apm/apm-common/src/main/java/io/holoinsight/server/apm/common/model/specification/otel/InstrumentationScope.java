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
 * @version : InstrumentationScope.java, v 0.1 2022年11月01日 12:01 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentationScope {
  private String name;
  private String version;
  private List<KeyValue> attributes;
  private int droppedAttributesCount;
}
