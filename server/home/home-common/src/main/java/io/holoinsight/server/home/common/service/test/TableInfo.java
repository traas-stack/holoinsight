/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.test;

import lombok.Data;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: TableInfo.java, v 0.1 2022年02月23日 3:31 下午 jinsong.yjs Exp $
 */
@Data
public class TableInfo {

  private List<String> fieldTypes;
  private List<String> fields;
  private String periodType;
  private String table;
  private String tenant;

}
