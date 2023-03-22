/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@TableName(autoResultMap = true)
public class OpenmetricsScraper {

  @TableId(type = IdType.AUTO)
  private Long id;
  private String name;
  private String tenant;

  public String workspace;

  @TableField(value = "`conf`", typeHandler = GsonTypeHandler.class)
  private Map<String, String> conf;

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
}
