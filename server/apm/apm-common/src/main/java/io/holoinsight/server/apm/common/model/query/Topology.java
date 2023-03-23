/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import io.holoinsight.server.common.service.Measurable;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Topology implements Measurable {
  private final List<Node> nodes;
  private final List<Call> calls;

  public Topology() {
    this.nodes = new ArrayList<>();
    this.calls = new ArrayList<>();
  }

  @Override
  public long measure() {
    if (CollectionUtils.isEmpty(calls)) {
      return 0;
    }
    return this.calls.size();
  }
}
