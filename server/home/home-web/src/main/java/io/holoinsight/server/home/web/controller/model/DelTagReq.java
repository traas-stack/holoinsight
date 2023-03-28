/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 * <p>
 * Date: 2023-03-28 Time: 14:37
 * </p>
 *
 * @author jsy1001de
 */
@Data
public class DelTagReq {
  private String metric;
  private boolean all = false;
  private List<Map<String, String>> keys;
  private Long end;
  private Long start;

  private String tenant;
}
