/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zanghaibo
 * @time 2023-05-22 7:50 下午
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticDataList {
  private List<StatisticData> statisticDataList;
}
