/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.crawler;

import io.holoinsight.server.common.MD5Hash;
import io.holoinsight.server.common.Pair;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.service.MetricInfoService;
import io.holoinsight.server.home.task.MonitorTaskJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jsy1001de
 * @version 1.0: MetricCrawlerJob.java, Date: 2023-05-10 Time: 16:11
 */
@Slf4j
public class TenantMetricCrawlerTaskJob extends MonitorTaskJob {

  private TenantMetricCrawlerTaskJobArgs jobArgs;

  private MetricInfoService metricInfoService;

  public TenantMetricCrawlerTaskJob(TenantMetricCrawlerTaskJobArgs jobArgs,
      MetricInfoService metricInfoService) {
    super();
    this.jobArgs = jobArgs;
    this.metricInfoService = metricInfoService;
  }

  @Override
  public boolean run() throws Throwable {
    try {
      startCompare(jobArgs.tenant, jobArgs.workspace, jobArgs.product.getName(),
          jobArgs.metricInfoList);
    } catch (Exception e) {
      log.error("TenantMetricCrawlerTaskJob error, " + jobArgs.product.getName() + ", "
          + jobArgs.tenant + ", " + jobArgs.workspace + ", " + e.getMessage());
      throw new RuntimeException("TenantMetricCrawlerTaskJob error, " + jobArgs.product.getName()
          + ", " + jobArgs.tenant + ", " + jobArgs.workspace + ", " + e.getMessage(), e);
    }
    return true;
  }

  private void startCompare(String tenant, String workspace, String product,
      List<MetricInfo> outerList) {
    log.info("[crawlerTask][{}] [{}], start", product, tenant);
    if (CollectionUtils.isEmpty(outerList)) {
      return;
    }
    log.info("[crawlerTask][{}] [{}], outerList [{}]", product, tenant, outerList.size());
    StopWatch stopWatch = new StopWatch();
    Map<String, Pair<Long, MetricInfo>> fromDbMap = getFromDb(tenant, workspace, product);
    log.info("[crawlerTask][{}] [{}], fromDbMap [{}]", product, tenant, fromDbMap.size());

    List<MetricInfo> createList = new ArrayList<>();
    Set<Long> existed = new HashSet<>();
    List<Long> deleteList = new ArrayList<>();
    List<MetricInfo> updateList = new ArrayList<>();
    for (MetricInfo metricInfo : outerList) {
      if (!fromDbMap.containsKey(metricInfo.getMetricTable())) {
        createList.add(metricInfo);
        continue;
      } else if (compareUpdate(fromDbMap.get(metricInfo.getMetricTable()).right(), metricInfo)) {
        metricInfo.setId(fromDbMap.get(metricInfo.getMetricTable()).left());
        updateList.add(metricInfo);
      }

      existed.add(fromDbMap.get(metricInfo.getMetricTable()).left());
    }

    for (Pair<Long, MetricInfo> pair : fromDbMap.values()) {
      if (!existed.contains(pair.left())) {
        deleteList.add((pair.left()));
      }
    }
    log.info("[crawlerTask][{}][{}], curd detail, need create: {}", product, tenant,
        createList.size());
    log.info("[crawlerTask][{}][{}], curd detail, need delete:{}", product, tenant,
        deleteList.size());
    log.info("[crawlerTask][{}][{}], curd detail, need update:{}", product, tenant,
        updateList.size());
    log.info("[crawlerTask][{}][{}], curd detail, compare cost:{}", product, tenant,
        stopWatch.getTime());

    if (!CollectionUtils.isEmpty(deleteList)) {
      deleteList.forEach(delete -> {
        metricInfoService.removeById(delete);
      });
    }

    if (!CollectionUtils.isEmpty(updateList)) {
      updateList.forEach(update -> {
        metricInfoService.updateById(update);
      });
    }

    if (!CollectionUtils.isEmpty(createList)) {
      createList.forEach(create -> {
        metricInfoService.save(create);
      });
    }
    log.info("[crawlerTask][{}][{}], end", product, tenant);
  }

  private Boolean compareUpdate(MetricInfo db, MetricInfo outer) {
    if (null == db || null == outer)
      return false;
    return !genMd5(db).equalsIgnoreCase(genMd5(outer));
  }

  private String genMd5(MetricInfo metricInfo) {
    String stringBuffer = metricInfo.metric + metricInfo.metricTable + metricInfo.description
        + metricInfo.unit + metricInfo.period + metricInfo.tags;
    return MD5Hash.getMD5(stringBuffer);
  }

  private Map<String, Pair<Long, MetricInfo>> getFromDb(String tenant, String workspace,
      String product) {
    Map<String, Object> map = new HashMap<>();
    map.put("tenant", tenant);
    map.put("workspace", workspace);
    map.put("product", product);
    map.put("deleted", 0);
    List<MetricInfo> metricInfos = metricInfoService.listByMap(map);

    Map<String, Pair<Long, MetricInfo>> metricInfoMap = new HashMap<>();
    if (CollectionUtils.isEmpty(metricInfos)) {
      return metricInfoMap;
    }
    metricInfos.forEach(metricInfo -> {
      metricInfoMap.put(metricInfo.getMetricTable(), new Pair<>(metricInfo.getId(), metricInfo));
    });
    return metricInfoMap;
  }

  @Override
  public String id() {
    return jobArgs.product.getName();
  }
}
