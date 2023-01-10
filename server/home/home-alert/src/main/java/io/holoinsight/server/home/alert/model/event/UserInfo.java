/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.event;

import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/8/4 8:44 下午
 */
@Data
public class UserInfo {

  /**
   * 用户id
   */
  private String userId;

  /**
   * 电话区域码
   */
  private String region;

  /**
   * 用户电话
   */
  private String mobile;

  /**
   * 用户邮箱
   */
  private String userEmail;

}
