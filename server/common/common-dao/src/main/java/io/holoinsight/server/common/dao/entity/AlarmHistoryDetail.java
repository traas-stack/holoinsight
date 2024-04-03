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
@TableName("alarm_history_detail")
public class AlarmHistoryDetail {
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
   * 告警时间
   */
  private Date alarmTime;

  /**
   * 告警id
   */
  private String uniqueId;

  /**
   * 告警历史id
   */
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
  private String envType;

  /**
   * app
   */
  private String app;

}
