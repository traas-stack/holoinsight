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
 * @author wangsiyuan
 * @date 2022/6/15 4:36 下午
 */
@Data
@Table(name = "alarm_block")
public class AlarmBlock {
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
   * 开始时间
   */
  @Column(name = "start_time")
  private Date startTime;

  /**
   * 结束时间
   */
  @Column(name = "end_time")
  private Date endTime;

  /**
   * 告警id
   */
  @Column(name = "unique_id")
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
