/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import io.holoinsight.server.home.dal.model.dto.meta.MetaTableCol;
import io.holoinsight.server.home.dal.model.dto.meta.MetaTableConfig;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaTableDTO.java, v 0.1 2022年03月22日 11:42 上午 jinsong.yjs Exp $
 */
@Data
public class MetaTableDTO {
  public Long id;

  public String tenant;

  public String name;

  public MetaTableConfig config;

  public List<MetaTableCol> tableSchema;

  public TableStatus status;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;

  /**
   *
   * @author jsy1001de
   * @version 1.0: TableStatus.java, v 0.1 2022年03月14日 8:05 下午 jinsong.yjs Exp $
   */
  public enum TableStatus {
    ONLINE, OFFLINE
  }
}
