/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.ceresdbx;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import io.ceresdb.HoraeDBClient;
import io.ceresdb.RouteMode;
import io.ceresdb.options.HoraeDBOptions;
import io.ceresdb.rpc.RpcOptions;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.config.EnvironmentProperties;
import io.holoinsight.server.common.dao.entity.TenantOps;
import io.holoinsight.server.common.dao.mapper.TenantOpsMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static io.holoinsight.server.extension.ceresdbx.CeresdbxClientManager.configKey;

/**
 * @author masaimu
 * @version 2024-05-16 19:50:00
 */
public class HoraedbClientManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(HoraedbClientManager.class);
  public static final String PUBLIC_DATABASE = "public";

  private TenantOpsMapper tenantOpsMapper;

  private EnvironmentProperties environmentProperties;

  private Map<String, HoraeDBClientInstance> instances = new ConcurrentHashMap<>();

  public HoraedbClientManager(TenantOpsMapper tenantOpsMapper,
      EnvironmentProperties environmentProperties) {
    this.tenantOpsMapper = tenantOpsMapper;
    this.environmentProperties = environmentProperties;
  }

  @PostConstruct
  @Scheduled(fixedRate = 60000L)
  public void refresh() {
    QueryWrapper<TenantOps> wrapper = new QueryWrapper<>();
    List<TenantOps> tenantOpsList = tenantOpsMapper.selectList(wrapper);
    if (CollectionUtils.isNotEmpty(tenantOpsList)) {
      for (TenantOps tenantOps : tenantOpsList) {
        String tenant = tenantOps.getTenant();
        Map<String, Object> json = J.toMap(tenantOps.getStorage());
        Map<String, Object> metricConfig = (Map<String, Object>) json.get("metric");
        Assert.notNull(metricConfig, String.format("metric conf not found: %s", tenantOps));
        initHoraedb((Map<String, Object>) metricConfig.get("horaedb"), tenant);
      }
    }
  }

  private void initHoraedb(Map<String, Object> horaedbConfig, String tenant) {
    if (Objects.isNull(horaedbConfig) || horaedbConfig.size() == 0) {
      return;
    }

    String accessUser = (String) horaedbConfig.get("accessUser");
    String accessKey = (String) horaedbConfig.get("accessKey");
    String address = (String) horaedbConfig.get("address");
    String database = (String) horaedbConfig.get("database");
    Object portObj = horaedbConfig.get("port");
    int port = Double.valueOf(String.valueOf(portObj)).intValue();
    address = fixAddress(address);
    String newConfigKey = configKey(address, port, accessUser, accessKey);
    HoraeDBClientInstance clientInstance = instances.get(tenant);
    if (clientInstance == null
        || !StringUtils.equals(clientInstance.getConfigKey(), newConfigKey)) {
      RpcOptions rpcOptions = new RpcOptions();
      rpcOptions.setLimitKind(RpcOptions.LimitKind.None);
      HoraeDBOptions opts = HoraeDBOptions.newBuilder(address, port, RouteMode.DIRECT)
          .database(StringUtils.isBlank(database) ? PUBLIC_DATABASE : database).writeMaxRetries(2)
          .readMaxRetries(2).maxWriteSize(512).build();
      HoraeDBClient client = new HoraeDBClient();
      try {
        final boolean ret = client.init(opts);
        LOGGER.info("Start HoraeDBx client {}, with options: {}.", ret ? "success" : "failed",
            opts);
        HoraeDBClientInstance newInstance = new HoraeDBClientInstance(newConfigKey, client);
        instances.put(tenant, newInstance);
      } catch (Throwable e) {
        LOGGER.error("create HoraeDB instance error", e);
      }
    }
  }

  private String fixAddress(String address) {
    if (StringUtils.indexOfIgnoreCase(address, "${zone}") != -1) {
      String zoneAddress =
          StringUtils.replaceIgnoreCase(address, "${zone}", this.environmentProperties.getZone());
      return zoneAddress;
    }
    return address;
  }

  public HoraeDBClient getClient(String tenant) {
    if (StringUtils.isBlank(tenant)) {
      tenant = "dev";
    }
    HoraeDBClientInstance horaeDBClientInstance = instances.get(tenant);

    return horaeDBClientInstance == null ? null : horaeDBClientInstance.getHoraeDBClient();
  }
}
