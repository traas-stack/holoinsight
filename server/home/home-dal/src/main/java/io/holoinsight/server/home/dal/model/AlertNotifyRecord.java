/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Column;
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
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * gmtCreate
   */
  @Column(name = "gmt_create")
  private Date gmtCreate;

  /**
   * gmtModified
   */
  @Column(name = "gmt_modified")
  private Date gmtModified;

  /**
   * alert history detail id
   */
  @Column(name = "history_detail_id")
  private Long historyDetailId;

  /**
   * alert history id
   */
  @Column(name = "history_id")
  private Long historyId;

  /**
   * alert exception time
   */
  @Column(name = "notify_error_time")
  private Date notifyErrorTime;

  /**
   * alert success
   */
  @Column(name = "is_success")
  private Byte isSuccess;

  /**
   * notify channel
   */
  @Column(name = "notify_channel")
  private String notifyChannel;

  /**
   * exception node
   */
  @Column(name = "notify_error_node")
  private String notifyErrorNode;

  /**
   * tenant
   */
  @Column(name = "tenant")
  private String tenant;

  /**
   * alert compute detail
   */
  @Column(name = "extra")
  private String extra;

  /**
   * envType
   */
  @Column(name = "env_type")
  private String envType;


  @Column(name = "workspace")
  private String workspace;

  /**
   * alert rule name
   */
  @Column(name = "rule_name")
  private String ruleName;

  @Column(name = "unique_id")
  private String uniqueId;

  /**
   * notify target detail
   */
  @Column(name = "notify_user")
  private String notifyUser;

  @Column(name = "trigger_result")
  private String triggerResult;

}
