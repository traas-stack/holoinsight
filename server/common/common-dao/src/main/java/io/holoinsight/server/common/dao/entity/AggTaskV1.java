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
 * @version 1.0: AggTask.java, Date: 2023-12-06 Time: 15:31
 */
@Data
@TableName("agg_task_v1")
public class AggTaskV1 {
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

  private String aggId;

  private Long version;

  private String json;

  public Integer deleted;

  public String refId;
}
