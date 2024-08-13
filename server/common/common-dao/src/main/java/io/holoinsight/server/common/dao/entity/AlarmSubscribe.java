/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("alarm_subscribe")
public class AlarmSubscribe {
  /**
   * id
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 创建时间
   */
  private Date gmtCreate;

  /**
   * 修改时间
   */
  private Date gmtModified;

  /**
   * 创建者
   */
  private String creator;

  /**
   * 修改者
   */
  private String modifier;

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
  private String noticeType;

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

  /**
   * 环境类型
   */
  private String envType;

  /**
   * 来源类型
   */
  private String sourceType;

  /**
   * 来源id
   */
  private Long sourceId;

}
