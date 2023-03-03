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
 * @version : ScopeSpans.java, v 0.1 2022年11月01日 12:02 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScopeSpans {
  private InstrumentationScope scope;
  private List<Span> spans;
  private String schemaUrl;
}
