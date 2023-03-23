/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.measure;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.service.Measurable;
import io.holoinsight.server.home.biz.access.AccessConfigService;
import io.holoinsight.server.home.biz.access.model.MonitorAccessConfig;
import io.holoinsight.server.home.biz.access.model.MonitorTokenData;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.IdentityType;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.web.controller.model.open.GrafanaJsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static io.holoinsight.server.home.alert.service.task.AlertTaskScheduler.getScheduleProperties;

/**
 * @author masaimu
 * @version 2023-03-03 21:28:00
 */
@Slf4j
@Service
public class MeasureStatistician {

  @Autowired
  private AccessConfigService accessConfigService;

  private Map<StatisticsData, StatisticsData> statisticsDataMap = new ConcurrentHashMap();

  public ApikeyAuthority queryResourceTokenByToken(String accessKey) {
    if (StringUtils.isEmpty(accessKey)) {
      return null;
    }
    Map<String /* accessKey */, MonitorAccessConfig> apikeyMap =
        this.accessConfigService.getAccessConfigDOMap();
    MonitorAccessConfig monitorAccessConfig = apikeyMap.get(accessKey);
    if (monitorAccessConfig == null) {
      return null;
    }
    return ApikeyAuthority.build(monitorAccessConfig);
  }

  public <T> void doStat(MonitorTokenData tokenData, String resourceTenant,
      ResourceType resourceType, ActionType actionType, GrafanaJsonResult<T> result) {
    if (resourceType == ResourceType.unknown || actionType == ActionType.unknown) {
      return;
    }
    if (tokenData == null) {
      MonitorUser mu = RequestContext.getContext().mu;
      if (mu.getIdentityType() == null || mu.getIdentityType() == IdentityType.OUTTOKEN) {
        return;
      }
      tokenData = new MonitorTokenData();
      tokenData.setAccessId("front");
      tokenData.setAccessKey("front");
      resourceTenant = mu.getLoginTenant();
    }
    innerStat(tokenData, resourceTenant, resourceType, actionType, result.getResult());
  }

  public <T> void doStat(MonitorTokenData tokenData, String resourceTenant,
      ResourceType resourceType, ActionType actionType, JsonResult<T> result) {
    if (resourceType == ResourceType.unknown || actionType == ActionType.unknown) {
      return;
    }
    if (tokenData == null) {
      MonitorUser mu = RequestContext.getContext().mu;
      if (mu.getIdentityType() == null || mu.getIdentityType() == IdentityType.OUTTOKEN) {
        return;
      }
      tokenData = new MonitorTokenData();
      tokenData.setAccessId("front");
      tokenData.setAccessKey("front");
      resourceTenant = mu.getLoginTenant();
    }
    innerStat(tokenData, resourceTenant, resourceType, actionType, result.getData());
  }

  private void innerStat(MonitorTokenData tokenData, String resourceTenant,
      ResourceType resourceType, ActionType actionType, Object data) {
    if (data == null) {
      return;
    }
    long size = 0;
    if (data instanceof Collection) {
      Collection collection = (Collection) data;
      for (Object item : collection) {
        if (item instanceof Measurable) {
          size += ((Measurable) item).measure();
        } else {
          size += 1;
        }
      }
    } else if (data instanceof Map) {
      Map map = (Map) data;
      for (Object item : map.values()) {
        if (item instanceof Measurable) {
          size += ((Measurable) item).measure();
        } else {
          size += 1;
        }
      }
    } else if (data instanceof Measurable) {
      size = ((Measurable) data).measure();
    } else {
      size = 1;
    }
    StatisticsData key = StatisticsData.getStatisticsDataKey(resourceType, actionType,
        resourceTenant, tokenData.accessId, tokenData.accessKey);
    StatisticsData statisticsData =
        this.statisticsDataMap.computeIfAbsent(key, k -> new StatisticsData(key));
    statisticsData.add(size);
  }

  /**
   * Check authority of tenant, resource type, action type
   * 
   * @param tokenData
   * @param resourceTenant
   * @param resourceType
   * @param actionType
   */
  public void checkAuth(MonitorTokenData tokenData, String resourceTenant,
      ResourceType resourceType, ActionType actionType) {
    if (tokenData == null) {
      return;
    }
    ApikeyAuthority apikeyAuthority = queryResourceTokenByToken(tokenData.accessKey);

    if (apikeyAuthority == null) {
      throw new MonitorException(
          String.format("Can not find token [%s] authority.", tokenData.accessKey));
    }

    apikeyAuthority.checkResourceType(resourceType, tokenData);
    apikeyAuthority.checkActionType(actionType, tokenData);

    if (StringUtils.isNotEmpty(resourceTenant)) {
      apikeyAuthority.checkTenant(resourceTenant, tokenData);
    }
  }

  public static class StatisticsData {
    ResourceType resourceType;
    ActionType actionType;
    String tenant;
    String accessId;
    String accessKey;

    AtomicLong counter = new AtomicLong(0);

    public StatisticsData(ResourceType resourceType, ActionType actionType, String tenant,
        String accessId, String accessKey) {
      this.resourceType = resourceType;
      this.actionType = actionType;
      this.tenant = tenant;
      this.accessId = accessId;
      this.accessKey = accessKey;
    }

    public StatisticsData(StatisticsData key) {
      if (key != null) {
        this.resourceType = key.resourceType;
        this.actionType = key.actionType;
        this.tenant = key.tenant;
        this.accessId = key.accessId;
        this.accessKey = key.accessKey;
      }
    }

    static StatisticsData getStatisticsDataKey(ResourceType resourceType, ActionType actionType,
        String tenant, String accessId, String accessKey) {
      return new StatisticsData(resourceType, actionType, tenant, accessId, accessKey);
    }

    public long add(long size) {
      return counter.addAndGet(size);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      StatisticsData that = (StatisticsData) o;
      return resourceType == that.resourceType && actionType == that.actionType
          && Objects.equals(tenant, that.tenant) && Objects.equals(accessId, that.accessId)
          && Objects.equals(accessKey, that.accessKey);
    }

    @Override
    public int hashCode() {
      return Objects.hash(resourceType, actionType, tenant, accessId, accessKey);
    }
  }


  public void start() {
    try {
      StdSchedulerFactory stdSchedulerFactory =
          new StdSchedulerFactory(getScheduleProperties("StatisticsPrint"));
      Scheduler scheduler = stdSchedulerFactory.getScheduler();

      // 创建需要执行的任务
      String jobName = "apikey-stat-job";
      JobDetail jobDetail =
          JobBuilder.newJob(StatisticsPrinter.class).withIdentity(jobName).build();
      jobDetail.getJobDataMap().put("statisticsDataMap", this.statisticsDataMap);
      // 创建触发器，指定任务执行时间
      CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("PerMinTrigger")
          .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")).build();

      scheduler.scheduleJob(jobDetail, cronTrigger);
      scheduler.start();
      log.info("[apikey-stat-job] start! ");
    } catch (Exception e) {
      log.error(
          "[HoloinsightHomeInternalException][MeasureStatistician] fail to schedule stat task.", e);
    }
  }

  public static class StatisticsPrinter implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      log.info("measure statistics print once.");
      Map<StatisticsData, StatisticsData> statisticsDataMap =
          (Map<StatisticsData, StatisticsData>) context.getJobDetail().getJobDataMap()
              .get("statisticsDataMap");
      if (CollectionUtils.isEmpty(statisticsDataMap)) {
        log.info("statistics data map is empty");
        return;
      }
      for (StatisticsData statisticsData : statisticsDataMap.values()) {
        log.info(String.join("|", statisticsData.accessId, statisticsData.accessKey,
            statisticsData.tenant, statisticsData.resourceType.code,
            statisticsData.resourceType.type, statisticsData.actionType.name(),
            String.valueOf(statisticsData.counter.getAndSet(0))));
      }
    }
  }
}
