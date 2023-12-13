/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jsy1001de
 * @version 1.0: HistogramLogCount.java, Date: 2023-10-25 Time: 10:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogHistogramCount {
  private Long fromTime;
  private Long toTime;
  private Long count;
}
