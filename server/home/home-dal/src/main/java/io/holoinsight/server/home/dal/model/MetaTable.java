/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaTable.java, v 0.1 2022年03月22日 11:34 上午 jinsong.yjs Exp $
 */
@Data
@Table(name = "meta_table")
public class MetaTable {

  @TableId(type = IdType.AUTO)
  public Long id;

  public String name;

  public String tenant;

  public String status;

  public String config;

  public String tableSchema;

  public String creator;

  public String modifier;

  public Date gmtCreate;
  public Date gmtModified;

}
