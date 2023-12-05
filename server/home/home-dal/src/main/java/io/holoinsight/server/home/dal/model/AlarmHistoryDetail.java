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
@Table(name = "alarm_history_detail")
public class AlarmHistoryDetail {
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
   * 告警时间
   */
  @Column(name = "alarm_time")
  private Date alarmTime;

  /**
   * 告警id
   */
  @Column(name = "unique_id")
  private String uniqueId;

  /**
   * 告警历史id
   */
  @Column(name = "history_id")
  private Long historyId;

  /**
   * 租户id
   */
  private String tenant;

  /**
   * workspace
   */
  private String workspace;

  /**
   * 报警维度
   */
  private String tags;

  /**
   * 触发方式简述
   */
  @Column(name = "alarm_content")
  private String alarmContent;

  /**
   * 数据源信息
   */
  private String datasource;

  /**
   * 额外信息
   */
  private String extra;

  /**
   * 环境类型
   */
  @Column(name = "env_type")
  private String envType;

  /**
   * app
   */
  @Column(name = "app")
  private String app;

}
