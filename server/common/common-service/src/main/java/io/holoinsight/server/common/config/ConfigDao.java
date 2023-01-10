/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.config;

import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.holoinsight.server.common.dao.entity.GaeaConfigDO;
import io.holoinsight.server.common.dao.entity.GaeaConfigDOExample;
import io.holoinsight.server.common.dao.mapper.GaeaConfigDOMapper;

/**
 * <p>
 * created at 2022/2/25
 *
 * @author xzchaoo
 */
class ConfigDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigDao.class);

  private final GaeaConfigDOMapper mapper;

  ConfigDao(GaeaConfigDOMapper mapper) {
    this.mapper = Objects.requireNonNull(mapper);
  }

  Map<String, String> getConfig() {
    List<GaeaConfigDO> configs =
        mapper.selectByExampleWithBLOBs(GaeaConfigDOExample.newAndCreateCriteria() //
            .andTenantEqualTo(ConfigService.TENANT) //
            .example());
    Map<String, String> map = Maps.newHashMapWithExpectedSize(configs.size());
    for (GaeaConfigDO c : configs) {
      map.put(c.getConfKey(), c.getConfValue());
    }
    return map;
  }
}
