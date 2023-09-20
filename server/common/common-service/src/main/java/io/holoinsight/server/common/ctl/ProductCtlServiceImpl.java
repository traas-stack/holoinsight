/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.ctl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.service.MetaDataDictValueService;
import io.holoinsight.server.common.service.SuperCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ProductCtlServiceImpl implements ProductCtlService {

  @Autowired
  private SuperCacheService superCacheService;

  @Autowired
  private MetaDataDictValueService metaDataDictValueService;


  private Map<String, Set<String>> productClosed;

  private Map<String, List<String>> resourceKeys;

  private boolean switchOn = false;

  protected static final String RS_DELIMITER = "_";


  @Scheduled(initialDelay = 10000L, fixedDelay = 60000L)
  private void refresh() {

    MetaDataDictValue switchOnDictVal = superCacheService.getSc().metaDataDictValueMap
        .getOrDefault("global_config", new HashMap<>()).get("product_closed_switch_on");
    try {
      switchOn = Boolean.parseBoolean(switchOnDictVal.getDictValue());
    } catch (Exception e) {
      switchOn = false;
    }

    MetaDataDictValue productClosedDictVal = superCacheService.getSc().metaDataDictValueMap
        .getOrDefault("global_config", new HashMap<>()).get("product_closed");
    if (productClosedDictVal != null) {
      productClosed = J.get().fromJson(productClosedDictVal.getDictValue(),
          new TypeToken<Map<String, Set<String>>>() {}.getType());
    }

    MetaDataDictValue resourceKeyDictVal = superCacheService.getSc().metaDataDictValueMap
        .getOrDefault("global_config", new HashMap<>()).get("resource_keys");
    if (resourceKeyDictVal != null) {
      resourceKeys = J.get().fromJson(resourceKeyDictVal.getDictValue(),
          new TypeToken<Map<String, List<String>>>() {}.getType());
    }


    log.info("[product_ctl] refresh closed products, switchOn={}, resourceKeys={}, closed={}",
        switchOn, resourceKeys, productClosed);
  }

  @Override
  public boolean switchOn() {
    return switchOn;
  }

  public String buildResourceVal(List<String> resourceKeys, Map<String, String> tags) {
    return resourceKeys.stream().map(tags::get).collect(Collectors.joining(RS_DELIMITER));
  }

  @Override
  public boolean productClosed(MonitorProductCode productCode, Map<String, String> tags) {
    if (!switchOn || productCode == null || resourceKeys == null || tags == null
        || productClosed == null) {
      return false;
    }
    String code = productCode.getCode();
    List<String> rks = resourceKeys.get(code);
    if (CollectionUtils.isEmpty(rks)) {
      return false;
    }
    String rkVal = buildResourceVal(rks, tags);
    return productClosed.getOrDefault(rkVal, new HashSet<>()).contains(code);
  }

  @Override
  public Map<String, Set<String>> productCtl(MonitorProductCode code, Map<String, String> tags,
      String action) throws Exception {
    MetaDataDictValue metricDefineDictVal = superCacheService.getSc().metaDataDictValueMap
        .getOrDefault("global_config", new HashMap<>()).get("product_closed");
    if (metricDefineDictVal == null) {
      metricDefineDictVal = new MetaDataDictValue();
      metricDefineDictVal.setType("global_config");
      metricDefineDictVal.setDictKey("product_closed");
      metricDefineDictVal.setDictValue("{}");
      metricDefineDictVal.setDictValueType("String");
      metricDefineDictVal.setCreator("admin");
      metricDefineDictVal.setModifier("admin");
      metricDefineDictVal.setGmtModified(new Date());
      metricDefineDictVal.setGmtCreate(new Date());
      metricDefineDictVal.setVersion(1);
      metaDataDictValueService.save(metricDefineDictVal);
    }
    Map<String, Set<String>> productClosed = J.get().fromJson(metricDefineDictVal.getDictValue(),
        new TypeToken<Map<String, Set<String>>>() {}.getType());
    List<String> rks = resourceKeys.get(code.getCode());
    if (CollectionUtils.isEmpty(rks)) {
      throw new RuntimeException("resource keys not found for code : " + code.getCode());
    }
    String uniqueId = buildResourceVal(rks, tags);

    log.info("[product_ctl] uniqueId={}, closed={}", uniqueId, productClosed);
    control(action, uniqueId, code.getCode(), productClosed);
    metricDefineDictVal.setDictValue(J.get().toJson(productClosed));
    UpdateWrapper<MetaDataDictValue> wrapper = new UpdateWrapper<>();
    wrapper.eq("type", "global_config");
    wrapper.eq("dict_key", "product_closed");
    metaDataDictValueService.update(metricDefineDictVal, wrapper);
    return productClosed;
  }

  @Override
  public Map<String, Boolean> productStatus(Map<String, String> tags) throws Exception {
    Map<String, Boolean> status = new HashMap<>();
    Map<String, Set<String>> productClosed = new HashMap<>();
    QueryWrapper<MetaDataDictValue> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("type", "global_config");
    queryWrapper.eq("dict_key", "product_closed");
    MetaDataDictValue metricDefineDictVal = metaDataDictValueService.getOne(queryWrapper);
    if (metricDefineDictVal != null) {
      productClosed = J.get().fromJson(metricDefineDictVal.getDictValue(),
          new TypeToken<Map<String, Set<String>>>() {}.getType());
    }

    for (MonitorProductCode code : MonitorProductCode.values()) {
      List<String> rks = resourceKeys.get(code.getCode());
      if (CollectionUtils.isEmpty(rks)) {
        status.put(code.getCode(), false);
      } else {
        String uniqueId = buildResourceVal(rks, tags);
        Set<String> closed = productClosed.computeIfAbsent(uniqueId, k -> new HashSet<>());
        if (closed.contains(code.getCode())) {
          status.put(code.getCode(), true);
        } else {
          status.put(code.getCode(), false);
        }
      }
    }
    return status;
  }

  public void control(String action, String uniqueId, String productCode,
      Map<String, Set<String>> productClosed) throws Exception {
    Set<String> closed = productClosed.computeIfAbsent(uniqueId, k -> new HashSet<>());
    switch (action) {
      case "start":
        closed.remove(productCode);
        break;
      case "stop":
        closed.add(productCode);
        break;
      default:
        throw new IllegalArgumentException("invalid action : " + action);
    }
  }
}
