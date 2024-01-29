/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: UserOpLog.java, v 0.1 2022年03月21日 2:30 下午 jinsong.yjs Exp $
 */
@Data
@TableName("user_oplog")
public class UserOpLog {

  @TableId(type = IdType.AUTO)
  public Long id;

  public String tableName;

  @Column(name = "table_entity_id")
  public Long tableEntityId;

  @Column(name = "table_entity_uuid")
  public String tableEntityUuid;

  public String opType;

  public String opBeforeContext;

  public String opAfterContext;

  public String name;

  public String relate;

  public String tenant;

  public String workspace;

  public String creator;

  public Date gmtCreate;

  public Date gmtModified;
}
