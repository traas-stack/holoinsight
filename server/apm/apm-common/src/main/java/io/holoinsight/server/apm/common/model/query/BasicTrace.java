/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiwliu
 * @version : BasicTrace.java, v 0.1 2022年09月20日 15:48 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicTrace implements Serializable {

  private static final long serialVersionUID = -4060937279918402711L;
  private List<String> serviceNames = new ArrayList<>();
  private List<String> serviceInstanceNames = new ArrayList<>();
  private List<String> endpointNames = new ArrayList<>();
  private int duration;
  private long start;
  private boolean isError;
  private List<String> traceIds = new ArrayList<>();
}
