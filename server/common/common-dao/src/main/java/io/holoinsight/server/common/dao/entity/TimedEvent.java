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
 * @author jsy1001de
 * @version 1.0: TimedEvent.java, Date: 2024-03-14 Time: 14:35
 */
@Data
@TableName("timed_event")
public class TimedEvent {
  @TableId(type = IdType.AUTO)
  public Long id;

  public String topic;

  public String status;

  public String data;

  public Integer retryTimes;

  public String guardianServer;

  public Date timeoutAt;

  public Date gmtCreate;

  public Date gmtModified;
}
