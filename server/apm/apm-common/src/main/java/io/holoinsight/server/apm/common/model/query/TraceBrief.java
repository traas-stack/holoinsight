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
 * @version : TraceBrief.java, v 0.1 2022年09月20日 15:48 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraceBrief implements Serializable {

  private static final long serialVersionUID = -5452437207833607799L;
  private List<BasicTrace> traces = new ArrayList<>();
}
