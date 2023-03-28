/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.util.Date;

@Data
public class CustomPlugin {

  @TableId(type = IdType.AUTO)
  public Long id;
  public String tenant;
  public String workspace;

  public Long parentFolderId;

  public String name;

  public String pluginType;

  public String status;

  public String periodType;

  public String conf;

  public String sampleLog;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;
}
