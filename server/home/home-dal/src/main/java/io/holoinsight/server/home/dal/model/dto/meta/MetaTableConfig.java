/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.meta;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaTableConfig.java, v 0.1 2022年03月22日 3:24 下午 jinsong.yjs Exp $
 */
@Data
public class MetaTableConfig {

  public String source;

  public List<String> metricList;

  public Integer rateSec = 60;

  public Map<String, List<String>> ukMaps = new HashMap<>();
}
