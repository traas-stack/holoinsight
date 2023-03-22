/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/19 4:12 下午
 */
@Data
public class AlarmSubscribeInfo {

  private Long id;

  /**
   * 订阅者
   */
  private String subscriber;

  /**
   * 订阅组id
   */
  private Long groupId;

  /**
   * 告警id
   */
  private String uniqueId;

  /**
   * 通知方式
   */
  private List<String> noticeType;

  /**
   * 通知是否生效
   */
  private Byte status;

  /**
   * 租户id
   */
  private String tenant;

  /**
   * workspace
   */
  private String workspace;
}
