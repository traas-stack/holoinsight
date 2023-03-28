/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.ceresdbx;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import io.ceresdb.CeresDBClient;
import io.ceresdb.RouteMode;
import io.ceresdb.options.CeresDBOptions;
import io.ceresdb.rpc.RpcOptions;
import io.ceresdb.rpc.RpcOptions.LimitKind;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.TenantOps;
import io.holoinsight.server.common.dao.mapper.TenantOpsMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.Assert;

/**
 * <p>
 * created at 2023/1/13
 *
 * @author jiwliu
 */
public class CeresdbxClientManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(CeresdbxClientManager.class);
  public static final String PUBLIC_DATABASE = "public";

  private TenantOpsMapper tenantOpsMapper;

  private Map<String, CeresDBxClientInstance> instances = new ConcurrentHashMap<>();

  public CeresdbxClientManager(TenantOpsMapper tenantOpsMapper) {
    this.tenantOpsMapper = tenantOpsMapper;
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
        Map<String, Object> ceresdbConfig = (Map<String, Object>) metricConfig.get("ceresdbx");
        if (Objects.isNull(ceresdbConfig) || ceresdbConfig.size() == 0) {
          continue;
        }
        String accessUser = (String) ceresdbConfig.get("accessUser");
        String accessKey = (String) ceresdbConfig.get("accessKey");
        String address = (String) ceresdbConfig.get("address");
        String database = (String) ceresdbConfig.get("database");
        Object portObj = ceresdbConfig.get("port");
        int port = Double.valueOf(String.valueOf(portObj)).intValue();
        String newConfigKey = configKey(address, port, accessUser, accessKey);
        CeresDBxClientInstance clientInstance = instances.get(tenant);
        if (clientInstance == null
            || !StringUtils.equals(clientInstance.getConfigKey(), newConfigKey)) {
          RpcOptions rpcOptions = new RpcOptions();
          rpcOptions.setLimitKind(LimitKind.None);
          CeresDBOptions opts = CeresDBOptions.newBuilder(address, port, RouteMode.DIRECT)
              .database(StringUtils.isBlank(database) ? PUBLIC_DATABASE : database)
              .writeMaxRetries(2).readMaxRetries(2).maxWriteSize(512).build();
          CeresDBClient client = new CeresDBClient();
          try {
            final boolean ret = client.init(opts);
            LOGGER.info("Start CeresDBx client {}, with options: {}.", ret ? "success" : "failed",
                opts);
            CeresDBxClientInstance newInstance = new CeresDBxClientInstance(newConfigKey, client);
            instances.put(tenant, newInstance);
          } catch (Throwable e) {
            LOGGER.error("create CeresDBx instance error", e);
          }
        }
      }
    }
  }

  public CeresDBClient getClient(String tenant) {
    if (StringUtils.isBlank(tenant)) {
      tenant = "dev";
    }
    CeresDBxClientInstance ceresDBxClientInstance = instances.get(tenant);
    Assert.notNull(ceresDBxClientInstance, "CeresDBx instance not found for tenant: " + tenant);
    return ceresDBxClientInstance.getCeresDBClient();
  }

  private String configKey(String host, int port, String user, String accessKey) {
    return host + port + user + accessKey;
  }
}


@AllArgsConstructor
@Data
class CeresDBxClientInstance {
  private final String configKey;
  private final CeresDBClient ceresDBClient;
}
