/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/19 4:10 下午
 */
@Data
public class AlarmSubscribeDTO {

  private String uniqueId;

  private List<AlarmSubscribeInfo> alarmSubscribe;

  /**
   * 环境类型
   */
  private String envType;

}
