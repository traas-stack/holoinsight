/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version : Folder.java, v 0.1 2022年05月23日 8:23 下午 jinsong.yjs Exp $
 */
@Data
public class Folder {
  @TableId(type = IdType.AUTO)
  public Long id;
  public String name;
  public String tenant;
  public String workspace;
  public Long parentFolderId;
  public String creator;
  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;

  public String extInfo;
}
