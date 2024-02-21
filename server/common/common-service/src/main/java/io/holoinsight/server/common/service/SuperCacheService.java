/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.holoinsight.server.common.config.ProdLog;
import io.holoinsight.server.common.config.ScheduleLoadTask;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.dao.mapper.MetricInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: SuperCacheService.java, v 0.1 2022年03月21日 8:24 下午 jinsong.yjs Exp $
 */
@Slf4j
public class SuperCacheService extends ScheduleLoadTask {
  private SuperCache sc;

  @Autowired
  private MetaDictValueService metaDictValueService;

  @Autowired
  private MetricInfoService metricInfoService;

  @Resource
  private MetricInfoMapper metricInfoMapper;

  public SuperCache getSc() {
    return sc;
  }

  @Override
  public void load() throws Exception {
    ProdLog.info("[SuperCache] load start");
    SuperCache sc = new SuperCache();
    sc.metaDataDictValueMap = metaDictValueService.getMetaDictValue();
    ProdLog.info("[SuperCache][metaDataDictValueMap] size: " + sc.metaDataDictValueMap.size());
    sc.expressionMetricList = metricInfoService.querySpmList();
    ProdLog.info("[SuperCache][expressionMetricList] size: " + sc.expressionMetricList.size());

    queryMetricInfoByPage(sc);
    this.sc = sc;
    ProdLog.info("[SuperCache] load end");
  }

  @Override
  public int periodInSeconds() {
    return 60;
  }

  @Override
  public String getTaskName() {
    return "SuperCacheService";
  }


  private void queryMetricInfoByPage(SuperCache sc) {
    QueryWrapper<MetricInfo> queryWrapper = new QueryWrapper<>();

    int current = 1;
    Map<String /* metric table */, MetricInfo> map = new HashMap<>();
    int size = 1000;
    do {
      Page<MetricInfo> page = new Page<>(current++, size);
      Page<MetricInfo> result = this.metricInfoMapper.selectPage(page, queryWrapper);

      if (result == null || CollectionUtils.isEmpty(result.getRecords())) {
        break;
      }

      for (MetricInfo metricInfo : result.getRecords()) {
        if (StringUtils.isEmpty(metricInfo.getMetricTable())) {
          continue;
        }
        map.put(metricInfo.getMetricTable(), metricInfo);
      }
    } while (current < 1000);
    sc.metricInfoMap = map;
    ProdLog.info("[SuperCache][metricInfoMap] size: " + sc.metricInfoMap.size());
  }
}
