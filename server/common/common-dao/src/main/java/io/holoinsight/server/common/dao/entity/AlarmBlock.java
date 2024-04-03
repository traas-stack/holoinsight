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
 * @author wangsiyuan
 * @date 2022/6/15 4:36 下午
 */
@Data
@TableName("alarm_block")
public class AlarmBlock {
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
   * 开始时间
   */
  private Date startTime;

  /**
   * 结束时间
   */
  private Date endTime;

  /**
   * 告警id
   */
  private String uniqueId;

  /**
   * 原因
   */
  private String reason;

  /**
   * 租户id
   */
  private String tenant;

  /**
   * workspace
   */
  private String workspace;

  /**
   * 屏蔽维度
   */
  private String tags;

  /**
   * 额外信息
   */
  private String extra;

  /**
   * 屏蔽小时
   */
  private Byte hour;

  /**
   * 屏蔽分钟
   */
  private Byte minute;
}
