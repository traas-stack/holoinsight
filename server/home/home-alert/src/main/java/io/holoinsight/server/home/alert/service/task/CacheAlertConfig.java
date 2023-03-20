/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.config.EnvironmentProperties;
import io.holoinsight.server.common.dao.mapper.MetaDataDictValueMapper;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wangsiyuan
 * @date 2022/2/22 4:34 下午
 */
@Service
public class CacheAlertConfig {

  private static Logger LOGGER = LoggerFactory.getLogger(CacheAlertConfig.class);

  private static final ScheduledExecutorService syncExecutorService =
      Executors.newSingleThreadScheduledExecutor();

  @Autowired
  private EnvironmentProperties environmentProperties;

  private Map<String, Object> configMap = new HashMap<>();

  @Resource
  private MetaDataDictValueMapper metadataDictvalueDOMapper;

  public void start() {
    LOGGER.info("[AlarmConfig] start alarm config syn!");

    getAlarmConfigCache();
    syncExecutorService.scheduleWithFixedDelay(this::getAlarmConfigCache, 5, 10, TimeUnit.SECONDS);
    LOGGER.info("[AlarmConfig] alarm config sync finish!");
  }

  private void getAlarmConfigCache() {
    try {
      QueryWrapper<MetaDataDictValue> condition = new QueryWrapper<>();
      condition.eq("type", "alarm_" + this.environmentProperties.getDeploymentSite());

      List<MetaDataDictValue> metadataDictvalueDOS =
          metadataDictvalueDOMapper.selectList(condition);
      if (!CollectionUtils.isEmpty(metadataDictvalueDOS)) {
        configMap = generateConfigMap(metadataDictvalueDOS);
      }
      LOGGER.info("AlarmGlobalConfig Sync SUCCESS");
    } catch (Exception e) {
      LOGGER.error("InspectCtlParam Sync Exception", e);
    }
  }

  public Object getConfig(String key) {
    return configMap.get(key);
  }

  private static Map<String, Object> generateConfigMap(
      List<MetaDataDictValue> metadataDictvalueDOS) {
    return metadataDictvalueDOS.stream()
        .collect(Collectors.toMap(MetaDataDictValue::getDictKey, MetaDataDictValue::getDictValue));
  }
}
