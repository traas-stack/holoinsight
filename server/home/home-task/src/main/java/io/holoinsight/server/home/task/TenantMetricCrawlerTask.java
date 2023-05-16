/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task;

import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.service.MetricInfoService;
import io.holoinsight.server.home.biz.service.IntegrationProductService;
import io.holoinsight.server.home.common.util.CommonThreadPool;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.home.task.crawler.TenantMetricCrawlerTaskJob;
import io.holoinsight.server.home.task.crawler.TenantMetricCrawlerTaskJobArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_TENANT;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_WORKSPACE;

/**
 * @author jsy1001de
 * @version 1.0: TenantMetricInfoCrawlerTask.java, Date: 2023-05-10 Time: 10:56
 */
@Slf4j
@Service
@TaskHandler(code = "TENANT_METRIC_CRAWLER")
public class TenantMetricCrawlerTask extends AbstractMonitorTask {
  private static final Executor taskPool =
      CommonThreadPool.createThreadPool(2, 5, 10, "taskPool-sync-pool");

  @Autowired
  private IntegrationProductService integrationProductService;

  @Autowired
  private MetricInfoService metricInfoService;


  public TenantMetricCrawlerTask() {
    super(2, 20, "TENANT_METRIC_CRAWLER");
  }

  @Override
  public boolean needRun() {
    return true;
  }

  @Override
  public long getTaskPeriod() {
    return 10 * MINUTE;
  }

  @Override
  public List<MonitorTaskJob> buildJobs(long period) throws Throwable {
    List<MonitorTaskJob> jobs = new ArrayList<>();
    List<TenantMetricCrawlerTaskJobArgs> args = new ArrayList<>();
    List<IntegrationProductDTO> list = integrationProductService.queryByRows();

    CountDownLatch countDownLatch = new CountDownLatch(list.size());
    for (IntegrationProductDTO integrationProduct : list) {
      log.info("crawler {} start buildJobs", integrationProduct.getName());
      taskPool.execute(() -> {
        try {
          args.addAll(preDoJob(integrationProduct));
        } catch (Throwable e) {
          log.error("build crawler " + integrationProduct.getName() + " error, " + e.getMessage(),
              e);
        } finally {
          countDownLatch.countDown();
          log.info("crawler {} finish buildJobs", integrationProduct.getName());
        }
      });
    }

    try {
      countDownLatch.await(30, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      log.error("Exception: countDownLatch await interrupted", e);
    } finally {
      log.info("countDownLatch: {}", countDownLatch);
    }

    for (TenantMetricCrawlerTaskJobArgs jobArg : args) {
      jobs.add(new TenantMetricCrawlerTaskJob(jobArg, metricInfoService));
    }
    return jobs;
  }

  private List<TenantMetricCrawlerTaskJobArgs> preDoJob(IntegrationProductDTO integrationProduct) {
    List<TenantMetricCrawlerTaskJobArgs> jobs = new ArrayList<>();

    MetricCrawlerBuilder crawlerTask =
        TaskFactoryHolder.getCrawlerTask(integrationProduct.getType());
    if (null == crawlerTask)
      return new ArrayList<>();
    List<MetricInfo> metricInfos = crawlerTask.buildEntity(integrationProduct);
    jobs.add(new TenantMetricCrawlerTaskJobArgs(GLOBAL_TENANT, GLOBAL_WORKSPACE, integrationProduct,
        metricInfos));
    return jobs;
  }
}
