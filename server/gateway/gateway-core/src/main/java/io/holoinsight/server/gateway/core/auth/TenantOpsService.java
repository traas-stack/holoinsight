/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.auth;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import io.holoinsight.server.common.dao.entity.TenantOpsDO;
import io.holoinsight.server.common.dao.mapper.TenantOpsDOMapper;
import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

/**
 * <p>
 * created at 2022/6/9
 *
 * @author sw1136562366
 */
@Service
public class TenantOpsService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TenantOpsService.class);

  @Autowired
  private TenantOpsDOMapper mapper;

  @Getter
  private Map<String, TenantOpsDO> tenantMap = Collections.emptyMap();

  /**
   * <p>
   * init.
   * </p>
   */
  @PostConstruct
  public void init() {
    refresh();
  }

  /**
   * <p>
   * refresh.
   * </p>
   */
  @Scheduled(initialDelay = 60000L, fixedDelay = 60000L)
  public void refresh() {
    long begin = System.currentTimeMillis();
    List<TenantOpsDO> tenants = mapper.selectByExample(null);
    Map<String, TenantOpsDO> tenantMap = Maps.newHashMapWithExpectedSize(tenants.size());
    for (TenantOpsDO t : tenants) {
      tenantMap.put(t.getTenant(), t);
    }
    this.tenantMap = tenantMap;
    long end = System.currentTimeMillis();
    LOGGER.info("[tenant] size=[{}] cost=[{}]", tenants.size(), end - begin);
  }
}
