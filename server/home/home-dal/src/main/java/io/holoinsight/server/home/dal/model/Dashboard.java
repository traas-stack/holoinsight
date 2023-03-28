/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@TableName(autoResultMap = true)
public class Dashboard {

  @TableId(type = IdType.AUTO)
  private Long id;
  private String title;
  @TableField(typeHandler = JacksonTypeHandler.class)
  private Map<String, Object> conf;

  public String tenant;

  public String workspace;

  public String type;

  public Date gmtCreate;

  public Date gmtModified;

  public String creator;

  public String modifier;

}
