/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: TenantMeta.java, v 0.1 2022年03月18日 3:00 下午 jinsong.yjs Exp $
 */
@Data
public class TenantServerMeta {
  public List<Map<String, Object>> dataList;
}
