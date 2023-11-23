/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.ctl;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.dao.entity.MonitorInstance;
import io.holoinsight.server.common.dao.entity.MonitorInstanceCfg;
import io.holoinsight.server.common.service.MonitorInstanceService;
import io.holoinsight.server.common.service.ResourceKeysHolder;
import io.holoinsight.server.common.service.SuperCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ProductCtlServiceImpl implements ProductCtlService {

  @Autowired
  private SuperCacheService superCacheService;

  @Autowired
  private MonitorInstanceService monitorInstanceService;


  private Map<String, Set<String>> productClosed = new HashMap<>();

  private boolean switchOn = false;

  protected static final String RS_DELIMITER = "_";

  @Autowired
  private ResourceKeysHolder resourceKeysHolder;


  @Scheduled(initialDelay = 10000L, fixedRate = 10000L)
  private void refresh() {
    List<MonitorInstance> monitorInstances = monitorInstanceService.list();
    Map<String, Set<String>> dbProductClosed = new HashMap<>();
    for (MonitorInstance monitorInstance : monitorInstances) {
      MonitorInstanceCfg cfg =
          J.get().fromJson(monitorInstance.getConfig(), MonitorInstanceCfg.class);
      cfg.getClosed().forEach((code, closed) -> {
        if (closed) {
          dbProductClosed.computeIfAbsent(monitorInstance.getInstance(), k -> new HashSet<>())
              .add(code);
        }
      });
    }
    productClosed = dbProductClosed;
    MetaDataDictValue switchOnDictVal = superCacheService.getSc().metaDataDictValueMap
        .getOrDefault("global_config", new HashMap<>()).get("product_closed_switch_on");
    try {
      switchOn = Boolean.parseBoolean(switchOnDictVal.getDictValue());
    } catch (Exception e) {
      switchOn = false;
    }


    log.info("[product_ctl] refresh closed products, switchOn={}, closed={}", switchOn,
        productClosed);
  }

  @Override
  public boolean switchOn() {
    return switchOn;
  }

  public String buildResourceVal(List<String> resourceKeys, Map<String, String> tags) {
    return resourceKeys.stream().map(rk -> {
      if (tags.containsKey(rk)) {
        return tags.get(rk);
      }
      for (String tagk : tags.keySet()) {
        if (StringUtils.endsWith(tagk, rk)) {
          return tags.get(tagk);
        }
      }
      return "NONE";
    }).collect(Collectors.joining(RS_DELIMITER));
  }

  @Override
  public boolean productClosed(MonitorProductCode productCode, Map<String, String> tags) {
    List<String> resourceKeys = resourceKeysHolder.getResourceKeys();
    if (!switchOn || productCode == null || resourceKeys == null || tags == null
        || productClosed == null) {
      return false;
    }
    String code = productCode.getCode();
    if (CollectionUtils.isEmpty(resourceKeys)) {
      return false;
    }
    String rkVal = buildResourceVal(resourceKeys, tags);
    return productClosed.getOrDefault(rkVal, new HashSet<>()).contains(code);
  }

  @Override
  public Map<String, Set<String>> productCtl(MonitorProductCode code, Map<String, String> tags,
      String action) throws Exception {
    List<String> resourceKeys = resourceKeysHolder.getResourceKeys();
    if (CollectionUtils.isEmpty(resourceKeys)) {
      throw new RuntimeException("resource keys not found for code : " + code.getCode());
    }
    String uniqueId = buildResourceVal(resourceKeys, tags);

    List<MonitorInstance> instances = monitorInstanceService.queryByInstance(uniqueId);
    if (CollectionUtils.isNotEmpty(instances)) {
      MonitorInstance instance = instances.get(0);
      MonitorInstanceCfg cfg = J.fromJson(instance.getConfig(), MonitorInstanceCfg.class);
      control(action, uniqueId, code.getCode(), cfg.getClosed(), productClosed);
      log.info("[product_ctl] uniqueId={}, instanceClosed={}, closed={}", uniqueId, cfg.getClosed(),
          productClosed);
      instance.setConfig(J.toJson(cfg));
      monitorInstanceService.updateByInstance(instance);
    }
    return productClosed;
  }

  @Override
  public Map<String, Boolean> productStatus(Map<String, String> tags) throws Exception {
    Map<String, Boolean> closed = new HashMap<>();
    List<String> resourceKeys = resourceKeysHolder.getResourceKeys();
    for (MonitorProductCode code : MonitorProductCode.values()) {
      if (CollectionUtils.isEmpty(resourceKeys)) {
        closed.put(code.getCode(), false);
      } else {
        String uniqueId = buildResourceVal(resourceKeys, tags);
        List<MonitorInstance> instances = monitorInstanceService.queryByInstance(uniqueId);
        if (CollectionUtils.isEmpty(instances)) {
          closed.put(code.getCode(), false);
        } else {
          MonitorInstanceCfg cfg =
              J.fromJson(instances.get(0).getConfig(), MonitorInstanceCfg.class);
          closed = cfg.getClosed();
        }
      }
    }
    return closed;
  }

  public void control(String action, String uniqueId, String productCode,
      Map<String, Boolean> closed, Map<String, Set<String>> productClosed) throws Exception {
    Set<String> closedProduct = productClosed.computeIfAbsent(uniqueId, k -> new HashSet<>());
    if (ProductCtlAction.start.name().equalsIgnoreCase(action)) {
      closedProduct.remove(productCode);
      closed.put(productCode, false);
    } else if (ProductCtlAction.stop.name().equalsIgnoreCase(action)) {
      closedProduct.add(productCode);
      closed.put(productCode, true);
    } else {
      throw new IllegalArgumentException("invalid action : " + action);
    }
  }
}
