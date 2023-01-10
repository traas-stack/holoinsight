/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.meta;

import lombok.Data;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaTableCol.java, v 0.1 2022年03月22日 3:28 下午 jinsong.yjs Exp $
 */
@Data
public class MetaTableCol {
  public String name;
  public String colType;
  public String desc;
}
