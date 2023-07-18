/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author limengyang
 * @date 2023/7/17 9:32
 */
@Data
public class AlertNotifyRecordDTO {
  /**
   * id
   */
  private Long id;

  /**
   * traceId
   */
  private String traceId;

  /**
   * 创建时间
   */
  private Date gmtCreate;

  private Date gmtModified;

  /**
   * 历史详情id
   */
  private Long historyDetailId;

  /**
   * 历史id
   */
  private Long historyId;

  /**
   * 告警通知异常时间
   */
  private Date notifyErrorTime;

  /**
   * 告警是否成功
   */
  private Byte isSuccess;

  /**
   * 通知渠道
   */
  private String notifyChannel;

  /**
   * 通知渠道
   */
  private String notifyUser;

  /**
   * 通知异常节点
   */
  private String notifyErrorNode;

  /**
   * 租户
   */
  private String tenant;

  /**
   * 额外信息
   */
  private String extra;

  /**
   * 运行环境
   */
  private String envType;

  private String workspace;

  private String uniqueId;

  private String ruleName;

  private String triggerResult;

  private List<NotifyStage> notifyStage;

  private List<String> notifyChannelList;

  private List<NotifyErrorMsg> notifyErrorMsgList;

  private List<NotifyUser> notifyUserList;

  private Boolean isRecord;
}
