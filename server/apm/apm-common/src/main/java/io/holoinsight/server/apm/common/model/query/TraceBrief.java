/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

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
 * @version : TraceBrief.java, v 0.1 2022年09月20日 15:48 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraceBrief implements Serializable, Measurable {

  private static final long serialVersionUID = -5452437207833607799L;
  private List<BasicTrace> traces = new ArrayList<>();

  @Override
  public long measure() {
    if (CollectionUtils.isEmpty(traces)) {
      return 0;
    }
    long result = 0;
    for (BasicTrace basicTrace : traces) {
      result += basicTrace.measure();
    }
    return result;
  }
}
