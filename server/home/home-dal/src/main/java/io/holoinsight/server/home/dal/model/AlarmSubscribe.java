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

@Data
@Table(name = "alarm_subscribe")
public class AlarmSubscribe {
  /**
   * id
   */
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
  @Column(name = "group_id")
  private Long groupId;

  /**
   * 告警id
   */
  @Column(name = "unique_id")
  private String uniqueId;

  /**
   * 通知方式
   */
  @Column(name = "notice_type")
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
  @Column(name = "env_type")
  private String envType;

  /**
   * 来源类型
   */
  @Column(name = "source_type")
  private String sourceType;

  /**
   * 来源id
   */
  @Column(name = "source_id")
  private Long sourceId;

}
