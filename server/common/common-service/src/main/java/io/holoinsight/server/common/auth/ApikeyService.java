/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.auth;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import io.holoinsight.server.common.dao.entity.ApikeyDO;
import io.holoinsight.server.common.dao.entity.ApikeyDOExample;
import io.holoinsight.server.common.dao.mapper.ApikeyDOMapper;
import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.google.common.collect.Maps;

/**
 * <p>
 * created at 2022/11/25
 *
 * @author xzchaoo
 */
public class ApikeyService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApikeyService.class);

  @Autowired
  private ApikeyDOMapper mapper;

  @Getter
  private Map<String, ApikeyDO> apikeyMap = Collections.emptyMap();

  @PostConstruct
  public void init() {
    refresh();
  }

  @Scheduled(initialDelay = 60000L, fixedDelay = 60000L)
  public void refresh() {
    long begin = System.currentTimeMillis();
    List<ApikeyDO> apikeys = mapper.selectByExample(ApikeyDOExample.newAndCreateCriteria() //
        .andStatusEqualTo((byte) 1) //
        .example()); //
    Map<String, ApikeyDO> apikeyMap = Maps.newHashMapWithExpectedSize(apikeys.size());
    for (ApikeyDO a : apikeys) {
      apikeyMap.put(a.getApikey(), a);
    }
    this.apikeyMap = apikeyMap;
    long end = System.currentTimeMillis();
    LOGGER.info("[apikey] size=[{}] cost=[{}]", apikeys.size(), end - begin);
  }
}
