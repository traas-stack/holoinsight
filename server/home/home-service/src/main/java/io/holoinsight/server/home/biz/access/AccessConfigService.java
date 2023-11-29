/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.access;

import com.alibaba.fastjson.JSON;
import io.holoinsight.server.home.biz.access.model.AccessConfig;
import io.holoinsight.server.home.biz.access.model.MonitorAccessConfig;
import io.holoinsight.server.home.biz.service.ApiKeyService;
import io.holoinsight.server.home.dal.model.ApiKey;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: AccessConfigService.java, v 0.1 2022年06月10日 10:38 上午 jinsong.yjs Exp $
 */
@Service
@Slf4j
public class AccessConfigService {

  @Autowired
  private ApiKeyService apiKeyService;

  @Getter
  private Map<String /* accessKey */, MonitorAccessConfig> accessConfigDOMap =
      Collections.emptyMap();

  private static final String TOKEN_AES_KEY = "_token_aes_key";

  @Getter
  @Setter
  public String tokenAesKey;

  @PostConstruct
  public void init() {
    refresh();
  }

  @Scheduled(initialDelay = 60000L, fixedDelay = 60000L)
  public void refresh() {
    long begin = System.currentTimeMillis();

    List<ApiKey> apiKeyDOS = apiKeyService.list();

    Map<String, MonitorAccessConfig> monitorAccessConfigMap = new HashMap<>();
    for (ApiKey t : apiKeyDOS) {

      if (t.getName().equalsIgnoreCase(TOKEN_AES_KEY)) {
        setTokenAesKey(t.getApiKey());
      }

      MonitorAccessConfig currentAccess = new MonitorAccessConfig();
      currentAccess.setTenant(t.getTenant());
      currentAccess.setAccessId(t.getName());
      currentAccess.setAccessKey(t.getApiKey());
      currentAccess.setOnline(t.getStatus());

      if (StringUtils.isBlank(t.getAccessConfig())) {
        monitorAccessConfigMap.put(t.getApiKey(), currentAccess);
        continue;
      }
      AccessConfig accessConfig = JSON.parseObject(t.getAccessConfig(), AccessConfig.class);

      currentAccess.setAccessAll(accessConfig.isAccessAll());
      currentAccess.setAccessRange(accessConfig.getAccessRange());
      currentAccess.setAccessUrl(accessConfig.getAccessUrl());
      currentAccess.setMetricQps(accessConfig.getMetricQps());
      currentAccess.setMetaQps(accessConfig.getMetaQps());
      currentAccess.setDpsLimit(accessConfig.getDpsLimit());
      currentAccess.setTagsLimit(accessConfig.getTagsLimit());
      currentAccess.setUserRate(accessConfig.getUserRate());
      currentAccess.setWorkspace(accessConfig.getWorkspace());

      monitorAccessConfigMap.put(t.getApiKey(), currentAccess);

    }

    this.accessConfigDOMap = monitorAccessConfigMap;
    long end = System.currentTimeMillis();
    log.info("[access_config] size=[{}] cost=[{}]", accessConfigDOMap.size(), end - begin);
  }
}
