/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: ApiKey.java, v 0.1 2022年05月31日 11:25 上午 jinsong.yjs Exp $
 */
@Data
@TableName("apikey")
public class ApiKey {
  @TableId(type = IdType.AUTO)
  public Long id;
  public String name;
  public String tenant;
  @TableField("apikey")
  public String apiKey;
  public String role;
  public String creator;
  public String modifier;

  @TableField(value = "`status`")
  public Boolean status;

  @TableField(value = "`desc`")
  public String desc;

  public String accessConfig;

  public Date gmtCreate;
  public Date gmtModified;

}
