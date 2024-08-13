/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.mapper;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/27
 *
 * @author xzchaoo
 */
@TableName("agg_offset_v1")
@Data
public class AggOffsetV1DO {
  @TableId(type = IdType.AUTO)
  private Long id;
  private Date gmtCreate;
  private Date gmtModified;
  @TableField("partition_name")
  private String partitionName;
  private String consumerGroup;
  private long version;
  private byte[] data;
}
