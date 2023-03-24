/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jiwliu
 * @version : Ref.java, v 0.1 2022年09月20日 15:31 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ref implements Serializable {
  private static final long serialVersionUID = -1599839859432306726L;
  private String traceId;
  private String parentSpanId;
  private RefType type;
}
