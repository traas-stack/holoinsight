/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import io.holoinsight.server.common.dao.entity.GaeaConfigDO;
import io.holoinsight.server.common.dao.entity.GaeaConfigDOExample;
import io.holoinsight.server.common.dao.mapper.GaeaConfigDOMapper;

/**
 * <p>
 * created at 2022/4/29
 *
 * @author xzchaoo
 */
public class ConfigService {
  static final String TENANT = "all";

  @Autowired
  private GaeaConfigDOMapper mapper;

  @Transactional
  public void delete(String key) {
    GaeaConfigDOExample example = GaeaConfigDOExample.newAndCreateCriteria() //
        .andTenantEqualTo(TENANT) //
        .andConfKeyEqualTo(key) //
        .example();
    example.limit(1);
    mapper.deleteByExample(example);
  }

  @Transactional
  public void set(String key, String value) {
    GaeaConfigDOExample example = GaeaConfigDOExample.newAndCreateCriteria() //
        .andTenantEqualTo(TENANT) //
        .andConfKeyEqualTo(key) //
        .example();

    GaeaConfigDO item = mapper.selectOneByExampleWithBLOBs(example);
    Date now = new Date();
    if (item == null) {
      item = new GaeaConfigDO();
      item.setGmtCreate(now);
      item.setGmtModified(now);
      item.setTenant(TENANT);
      item.setConfKey(key);
      item.setConfValue(value);
      mapper.insert(item);
    } else {
      item.setGmtModified(now);
      item.setConfValue(value);
      mapper.updateByExampleSelective(item, example, //
          GaeaConfigDO.Column.gmtModified, //
          GaeaConfigDO.Column.confValue); //
    }
  }
}
