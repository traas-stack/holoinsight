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
 * @version : Trace.java, v 0.1 2022年09月20日 15:35 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trace implements Serializable {

  private static final long serialVersionUID = 3436393920339302223L;

  /**
   * span 列表
   */
  private List<Span> spans = new ArrayList<>();
}
