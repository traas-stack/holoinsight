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
 * @version 1.0: DisplayMenu.java, v 0.1 2022年12月06日 上午10:27 jinsong.yjs Exp $
 */
@Data
public class DisplayMenu {
  @TableId(type = IdType.AUTO)
  public Long id;
  public Long refId;
  public String tenant;
  public String type;
  public String config;

  public String creator;
  public String modifier;
  public Date gmtCreate;
  public Date gmtModified;
}
