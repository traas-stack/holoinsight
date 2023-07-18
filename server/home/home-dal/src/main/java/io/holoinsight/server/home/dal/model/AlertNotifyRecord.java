/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author limengyang
 * @date 2023/7/17 9:52
 */
@Data
@Table(name = "alert_notify_record")
public class AlertNotifyRecord {

  /**
   * id
   */
  @Id
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 创建时间
   */
  @Column(name = "gmt_create")
  private Date gmtCreate;

  /**
   * 修改时间
   */
  @Column(name = "gmt_modified")
  private Date gmtModified;

  /**
   * 历史详情id
   */
  @Column(name = "history_detail_id")
  private Long historyDetailId;

  /**
   * 历史id
   */
  @Column(name = "history_id")
  private Long historyId;

  /**
   * 告警通知异常时间
   */
  @Column(name = "notify_error_time")
  private Date notifyErrorTime;

  /**
   * 告警是否成功
   */
  @Column(name = "is_success")
  private Byte isSuccess;

  /**
   * 通知渠道
   */
  @Column(name = "notify_channel")
  private String notifyChannel;

  /**
   * 通知异常节点
   */
  @Column(name = "notify_error_node")
  private String notifyErrorNode;

  /**
   * 租户
   */
  @Column(name = "tenant")
  private String tenant;

  /**
   * 额外信息
   */
  @Column(name = "extra")
  private String extra;

  /**
   * 运行环境
   */
  @Column(name = "env_type")
  private String envType;


  @Column(name = "workspace")
  private String workspace;

  /**
   * 告警规则名称
   */
  @Column(name = "rule_name")
  private String ruleName;

  @Column(name = "unique_id")
  private String uniqueId;

  @Column(name = "notify_user")
  private String notifyUser;

  @Column(name = "trigger_result")
  private String triggerResult;

}
