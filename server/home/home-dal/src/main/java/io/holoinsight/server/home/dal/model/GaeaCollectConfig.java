/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Id;
import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: GaeaCollectConfig.java, v 0.1 2022年03月31日 8:29 下午 jinsong.yjs Exp $
 */
@Data
public class GaeaCollectConfig {

  @Id
  @TableId(type = IdType.AUTO)
  public Long id;

  public String tenant;

  public String workspace;

  public String refId;

  public String tableName;

  public String type;

  public String json;

  public Integer deleted;

  public Long version;

  public String collectRange;

  public String executorSelector;

  public Date gmtCreate;

  public Date gmtModified;
}
