/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.test;

import io.holoinsight.server.common.JsonResult;

/**
 *
 * @author jsy1001de
 * @version 1.0: TestService.java, v 0.1 2022年02月23日 2:10 下午 jinsong.yjs Exp $
 */
public interface TestApiService {
  JsonResult<TableInfo> createTable(TableInfo table);

  JsonResult<TableInfo> queryTable(String name);
}
