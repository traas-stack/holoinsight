/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.extension.model;

import lombok.Data;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: PqlLabelParam.java, Date: 2024-05-21 Time: 20:28
 */
@Data
public class PqlLabelParam {
  String tenant;
  String metric;
  String labelName;
  List<String> match;
  long start;
  long end;
}
