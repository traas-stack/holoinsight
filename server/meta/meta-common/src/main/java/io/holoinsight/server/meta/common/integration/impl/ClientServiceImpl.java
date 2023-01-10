/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.integration.impl;

import io.holoinsight.server.meta.common.integration.ClientService;
import io.holoinsight.server.meta.common.integration.dict.D;
import io.holoinsight.server.meta.common.util.Env;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author jsy1001de
 * @version 1.0: ClientServiceImpl.java, v 0.1 2022年03月07日 5:13 下午 jinsong.yjs Exp $
 */
@Service
public class ClientServiceImpl implements ClientService {
  private static final Random RANDOM = new Random();

  private static final String LOCAL_DOMAIN = "localhost";

  @Override
  public List<String> getCacheServers() {
    if (Env.isDevEnv()) {
      return Collections.singletonList(LOCAL_DOMAIN);
    }
    String serverStr = D.getDict("metaservice.cache.servers");
    String[] serverArr = StringUtils.split(serverStr, ",");
    if (serverArr == null) {
      return null;
    }
    return Arrays.asList(serverArr);
  }

  @Override
  public String getCacheServer() {
    if (Env.isDevEnv()) {
      return LOCAL_DOMAIN;
    }
    List<String> servers = getCacheServers();
    if (CollectionUtils.isEmpty(servers)) {
      return null;
    }
    return servers.get(RANDOM.nextInt(servers.size()));
  }

  @Override
  public String getCurrentApp() {
    return Env.app();
  }

  @Override
  public String getLocalIp() {
    return Env.localIp();
  }

  @Override
  public String getDomain() {
    if (Env.isDevEnv()) {
      return LOCAL_DOMAIN;
    }
    return D.getDict("metaservice.domain");
  }

  @Override
  public boolean isRemote() {
    return Env.isRemote();
  }
}
