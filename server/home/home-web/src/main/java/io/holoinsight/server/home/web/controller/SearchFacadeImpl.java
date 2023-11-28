/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.service.MetricInfoService;
import io.holoinsight.server.home.biz.service.AlertRuleService;
import io.holoinsight.server.home.biz.service.CustomPluginService;
import io.holoinsight.server.home.biz.service.DashboardService;
import io.holoinsight.server.home.biz.service.FolderService;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.common.util.CommonThreadPool;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.Folder;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author jsy1001de
 * @version 1.0: SearchFacadeImpl.java, v 0.1 2022年05月17日 8:14 下午 jinsong.yjs Exp $
 */
@Slf4j
@RestController
@RequestMapping("/webapi/search")
public class SearchFacadeImpl extends BaseFacade {
  private static ThreadPoolTaskExecutor queryThreadPool =
      CommonThreadPool.createThreadPool(4, 10, 20, "search-query-worker-");

  @Autowired
  private CustomPluginService customPluginService;

  @Autowired
  private FolderService folderService;

  @Autowired
  private DashboardService dashboardService;

  @Autowired
  private AlertRuleService alarmRuleService;

  @Autowired
  private MetricInfoService metricInfoService;

  @Autowired
  private DataClientService dataClientService;

  @Autowired
  private TenantInitService tenantInitService;

  @ResponseBody
  @PostMapping(value = "/queryByKeyword")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Object> query(@RequestBody QueryKeywordReq req) {

    final JsonResult<Object> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(req, "req");
        ParaCheckUtil.checkParaNotNull(req.keyword, "keyword");
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        final String tenant = ms.tenant;
        final String workspace = ms.workspace;
        final String environment = ms.getEnvironment();

        List<SearchKeywordRet> ret = new ArrayList<>();
        List<Future<SearchKeywordRet>> futures = new ArrayList<>();

        futures.add(queryThreadPool.submit(() -> {
          return searchDashboardEntity(req.keyword, tenant, workspace);
        }));

        futures.add(queryThreadPool.submit(() -> {
          return searchAlarmEntity(req.keyword, tenant, workspace);
        }));

        if (environment.equalsIgnoreCase("SERVER")) {
          futures.add(queryThreadPool.submit(() -> {
            return searchLogEntity(req.keyword, tenant, workspace);
          }));

          futures.add(queryThreadPool.submit(() -> {
            return searchFolderEntity(req.keyword, tenant, workspace);
          }));

          futures.add(queryThreadPool.submit(() -> {
            return searchInfraEntity(req.keyword, tenant, workspace);
          }));

          futures.add(queryThreadPool.submit(() -> {
            return searchAppEntity(req.keyword, tenant, workspace);
          }));

          futures.add(queryThreadPool.submit(() -> {
            return searchLogMetricEntity(req.keyword, tenant, workspace);
          }));
        }

        // 多线程
        for (Future<SearchKeywordRet> future : futures) {
          try {
            SearchKeywordRet result = future.get();
            if (result != null) {
              ret.add(result);
            }
          } catch (Exception e) {
            log.info("error in search future, " + J.toJson(future));
          }
        }
        JsonResult.createSuccessResult(result, ret);
      }
    });

    return result;

  }

  @ResponseBody
  @PostMapping(value = "/configSearch")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Object> configSearch(@RequestBody QueryKeywordReq req) {

    final JsonResult<Object> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(req, "req");
        ParaCheckUtil.checkParaNotNull(req.keyword, "keyword");
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        String tenant = ms.tenant;
        String workspace = ms.workspace;

        List<SearchKeywordRet> ret = new ArrayList<>();
        List<Future<SearchKeywordRet>> futures = new ArrayList<>();
        futures.add(queryThreadPool.submit(() -> {
          List<CustomPluginDTO> listByKeyword =
              customPluginService.getListByNameLike(req.keyword, tenant, workspace);
          return genSearchResult("log", listByKeyword);
        }));

        futures.add(queryThreadPool.submit(() -> {
          List<Folder> listByKeyword =
              folderService.getListByNameLike(req.keyword, tenant, workspace);

          return genSearchResult("folder", listByKeyword);
        }));

        // 多线程
        for (Future<SearchKeywordRet> future : futures) {
          try {
            SearchKeywordRet result = future.get();
            if (result != null) {
              ret.add(result);
            }
          } catch (Exception e) {
            log.info("error in search future, " + J.toJson(future));
          }
        }
        JsonResult.createSuccessResult(result, ret);
      }
    });

    return result;

  }

  public SearchKeywordRet searchLogEntity(String keyword, String tenant, String workspace) {

    List<CustomPluginDTO> listByKeyword =
        customPluginService.getListByKeyword(keyword, tenant, workspace);
    return genSearchResult("log", listByKeyword);
  }

  public SearchKeywordRet searchFolderEntity(String keyword, String tenant, String workspace) {

    List<Folder> listByKeyword = folderService.getListByKeyword(keyword, tenant, workspace);
    return genSearchResult("folder", listByKeyword);
  }

  public SearchKeywordRet searchDashboardEntity(String keyword, String tenant, String workspace) {

    return genSearchResult("dashboard",
        dashboardService.getListByKeyword(keyword, tenant, workspace));
  }

  public SearchKeywordRet searchInfraEntity(String keyword, String tenant, String workspace) {

    QueryExample queryExample = new QueryExample();
    queryExample.getParams().put("hostname",
        Pattern.compile(String.format("^.*%s.*$", keyword), Pattern.CASE_INSENSITIVE));
    if (StringUtils.isNotBlank(workspace)) {
      Map<String, String> conditions =
          tenantInitService.getTenantWorkspaceMetaConditions(tenant, workspace);
      if (!CollectionUtils.isEmpty(conditions)) {
        conditions.forEach((k, v) -> queryExample.getParams().put(k, v));
      }
    }

    List<Map<String, Object>> list = dataClientService
        .fuzzyByExample(tenantInitService.getTenantServerTable(tenant), queryExample);

    return genSearchResult("infra", list);
  }

  public SearchKeywordRet searchAppEntity(String keyword, String tenant, String workspace) {

    QueryExample queryExample = new QueryExample();
    queryExample.getParams().put("app",
        Pattern.compile(String.format("^.*%s.*$", keyword), Pattern.CASE_INSENSITIVE));
    if (StringUtils.isNotBlank(workspace)) {
      queryExample.getParams().put("_workspace", workspace);
    }
    List<Map<String, Object>> list =
        dataClientService.fuzzyByExample(tenantInitService.getTenantAppTable(tenant), queryExample);

    return genSearchResult("app", list);
  }

  public SearchKeywordRet searchAlarmEntity(String keyword, String tenant, String workspace) {

    return genSearchResult("alarm", alarmRuleService.getListByKeyword(keyword, tenant, workspace));
  }

  public SearchKeywordRet searchLogMetricEntity(String keyword, String tenant, String workspace) {
    List<MetricInfoDTO> listByKeyword =
        metricInfoService.getListByKeyword(keyword, tenant, workspace);
    List<MetricInfoDTO> logList =
        listByKeyword.stream().filter(item -> item.getProduct().equalsIgnoreCase("logmonitor"))
            .collect(Collectors.toList());
    return genSearchResult("logMetric", logList);
  }

  public SearchKeywordRet genSearchResult(String type, List<?> datas) {
    SearchKeywordRet skr = new SearchKeywordRet();
    skr.count = datas.size();
    skr.datas = datas;
    skr.name = type;

    return skr;
  }

  public static class QueryKeywordReq {
    public String keyword;
    public String tenant;
  }

  public static class SearchKeywordRet {
    public String name;
    public Integer count;
    public List<?> datas;
  }
}
