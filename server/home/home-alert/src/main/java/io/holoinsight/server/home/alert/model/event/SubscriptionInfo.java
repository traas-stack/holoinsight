/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.event;

import lombok.Builder;
import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/8/4 8:32 下午
 */
@Data
@Builder
public class SubscriptionInfo {

  /**
   * 通知类型
   */
  String notifyType;

  /**
   * 用户id
   */
  String userId;

}
