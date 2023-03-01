/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import io.holoinsight.server.common.service.Measurable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

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
public class Trace implements Serializable, Measurable {

  private static final long serialVersionUID = 3436393920339302223L;

  /**
   * span 列表
   */
  private List<Span> spans = new ArrayList<>();

  @Override
  public long measure() {
    if (CollectionUtils.isEmpty(spans)) {
      return 0;
    }
    return this.spans.size();
  }
}
