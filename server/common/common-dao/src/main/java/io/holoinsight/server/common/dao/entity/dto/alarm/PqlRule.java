/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity.dto.alarm;

import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerDataResult;
import lombok.Data;

import java.util.List;

/**
 * @author zanghaibo
 * @time 2022-12-28 6:18 下午
 */

@Data
public class PqlRule {

  /**
   * pql
   */
  private String pql;

  /**
   * 告警pql查询数据
   */
  private List<TriggerDataResult> dataResult;
}
