/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Date;

/**
 * @author wangsiyuan
 * @date 2022/6/15 4:52 下午
 */
@Data
public class AlarmBlockDTO {
  /**
   * id
   */
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
   * 暂停小时
   */
  private int hour;

  /**
   * 暂停分钟
   */
  private int minute;

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
}
