/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.plugin.model.HostingAlertList;
import io.holoinsight.server.home.biz.plugin.model.HostingPlugin;
import io.holoinsight.server.home.biz.service.AlertRuleService;
import io.holoinsight.server.home.biz.service.IntegrationProductService;
import io.holoinsight.server.home.dal.mapper.AlarmSubscribeMapper;
import io.holoinsight.server.home.dal.mapper.AlarmWebhookMapper;
import io.holoinsight.server.home.dal.model.AlarmSubscribe;
import io.holoinsight.server.home.dal.model.AlarmWebhook;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.facade.trigger.Filter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-02-27 21:06:00
 */
public abstract class AbstractHostingAlertPlugin extends HostingPlugin {

  @Autowired
  public AlertRuleService alertRuleService;

  @Resource
  private AlarmWebhookMapper alarmWebhookMapper;

  @Resource
  private AlarmSubscribeMapper alarmSubscribeMapper;

  @Autowired
  private IntegrationProductService productService;

  protected void enableAlertHosting(IntegrationProductDTO product,
      IntegrationPluginDTO integrationPluginDTO) {
    String configuration = product.configuration;
    if (StringUtils.isEmpty(configuration)) {
      return;
    }
    HostingAlertList hostingAlertList =
        J.fromJson(configuration, new TypeToken<HostingAlertList>() {}.getType());

    List<AlarmRuleDTO> alarmRuleDTOList = hostingAlertList.parseAlertRule(product,
        integrationPluginDTO, buildFilter(integrationPluginDTO), getSourceType());
    Map<Long, AlarmRuleDTO> ids = new HashMap<>();
    for (AlarmRuleDTO alarmRuleDTO : alarmRuleDTOList) {
      Long id = this.alertRuleService.save(alarmRuleDTO);
      ids.put(id, alarmRuleDTO);
    }
    List<AlarmWebhook> webhooks = getWebhookConfig(integrationPluginDTO.tenant);
    if (!CollectionUtils.isEmpty(webhooks)) {
      for (AlarmWebhook alarmWebhook : webhooks) {
        enableAlertSubscribe(alarmWebhook, ids);
      }
    }
  }

  private void enableAlertSubscribe(AlarmWebhook alarmWebhook, Map<Long, AlarmRuleDTO> ruleIds) {
    if (CollectionUtils.isEmpty(ruleIds)) {
      return;
    }
    for (Map.Entry<Long, AlarmRuleDTO> entry : ruleIds.entrySet()) {
      AlarmRuleDTO rule = entry.getValue();
      String uniqueId = rule.getRuleType() + "_" + entry.getKey();
      AlarmSubscribe alarmSubscribe = new AlarmSubscribe();
      alarmSubscribe.setCreator(alarmWebhook.getCreator());
      alarmSubscribe.setGroupId(alarmWebhook.getId());
      alarmSubscribe.setUniqueId(uniqueId);
      alarmSubscribe.setNoticeType("[\"webhook\"]");
      alarmSubscribe.setStatus((byte) 1);
      alarmSubscribe.setTenant(alarmWebhook.getTenant());
      alarmSubscribe.setEnvType(rule.getEnvType());
      this.alarmSubscribeMapper.insert(alarmSubscribe);
    }
  }

  private List<AlarmWebhook> getWebhookConfig(String tenant) {
    QueryWrapper<AlarmWebhook> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("role", "hosting");
    queryWrapper.eq("tenant", tenant);
    return this.alarmWebhookMapper.selectList(queryWrapper);
  }

  private List<Filter> buildFilter(IntegrationPluginDTO newPlugin) {
    Map<String, Object> collectRange = newPlugin.getCollectRange();
    if (CollectionUtils.isEmpty(collectRange)) {
      return Collections.emptyList();
    }
    IntegrationPluginDTO.DataRange dataRange =
        J.fromJson(J.toJson(collectRange), IntegrationPluginDTO.DataRange.class);
    if (CollectionUtils.isEmpty(dataRange.getValuesMap())) {
      return Collections.emptyList();
    }
    List<Filter> list = new ArrayList<>();

    for (Map.Entry<String, String> entry : dataRange.getValuesMap().entrySet()) {
      String key = entry.getKey();
      Filter filter = new Filter();
      filter.setName(key);
      filter.setValue(entry.getValue());
      filter.setType("literal_or");
      list.add(filter);
    }
    return list;
  }

  protected boolean disableAlertHosting(IntegrationPluginDTO disableIntegrationPlugin) {
    Long id = disableIntegrationPlugin.id;
    if (id == null) {
      return true;
    }

    AlarmRuleDTO condition = new AlarmRuleDTO();
    condition.setSourceId(id);
    MonitorPageRequest<AlarmRuleDTO> request = new MonitorPageRequest<>();
    request.setTarget(condition);
    request.setPageSize(1000);
    MonitorPageResult<AlarmRuleDTO> result = this.alertRuleService.getListByPage(request);
    if (CollectionUtils.isEmpty(result.getItems())) {
      return true;
    }
    for (AlarmRuleDTO alarmRuleDTO : result.getItems()) {
      if (invalidTenant(alarmRuleDTO, disableIntegrationPlugin)) {
        continue;
      }
      AlarmRuleDTO updateEntity = new AlarmRuleDTO();
      updateEntity.setId(alarmRuleDTO.getId());
      updateEntity.setStatus((byte) 0);
      this.alertRuleService.updateById(updateEntity);
    }
    return true;
  }

  protected abstract boolean invalidTenant(AlarmRuleDTO alarmRuleDTO,
      IntegrationPluginDTO disableIntegrationPlugin);


  @Override
  public IntegrationPluginDTO apply(IntegrationPluginDTO integrationPlugin) {
    if (integrationPlugin == null) {
      return null;
    }

    Map<String, Object> productCondition = new HashMap<>();
    productCondition.put("name", integrationPlugin.product);
    productCondition.put("type", getProductType());
    productCondition.put("version", integrationPlugin.version);
    List<IntegrationProductDTO> existingProductList =
        this.productService.findByMap(productCondition);
    if (CollectionUtils.isEmpty(existingProductList)) {
      return null;
    }

    IntegrationProductDTO product = existingProductList.get(0);

    disableAlertHosting(integrationPlugin);
    enableAlertHosting(product, integrationPlugin);

    return integrationPlugin;
  }

  protected abstract String getProductType();

  public abstract String getSourceType();
}
