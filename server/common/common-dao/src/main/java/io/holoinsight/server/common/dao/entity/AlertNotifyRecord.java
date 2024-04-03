/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author limengyang
 * @date 2023/7/17 9:52
 */
@Data
@TableName("alert_notify_record")
public class AlertNotifyRecord {

  /**
   * id
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * gmtCreate
   */
  private Date gmtCreate;

  /**
   * gmtModified
   */
  private Date gmtModified;

  /**
   * alert history detail id
   */
  private Long historyDetailId;

  /**
   * alert history id
   */
  private Long historyId;

  /**
   * alert exception time
   */
  private Date notifyErrorTime;

  /**
   * alert success
   */
  private Byte isSuccess;

  /**
   * notify channel
   */
  private String notifyChannel;

  /**
   * exception node
   */
  private String notifyErrorNode;

  /**
   * tenant
   */
  private String tenant;

  /**
   * alert compute detail
   */
  private String extra;

  /**
   * envType
   */
  private String envType;


  private String workspace;

  /**
   * alert rule name
   */
  private String ruleName;

  private String uniqueId;

  /**
   * notify target detail
   */
  private String notifyUser;

  private String triggerResult;

}
