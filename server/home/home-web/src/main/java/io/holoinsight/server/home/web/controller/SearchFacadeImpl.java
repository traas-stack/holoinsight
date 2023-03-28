/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.AlertRuleService;
import io.holoinsight.server.home.biz.service.CustomPluginService;
import io.holoinsight.server.home.biz.service.DashboardService;
import io.holoinsight.server.home.biz.service.FolderService;
import io.holoinsight.server.home.common.util.CommonThreadPool;
import io.holoinsight.server.home.common.util.TenantMetaUtil;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.Folder;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
  private DataClientService dataClientService;

  @ResponseBody
  @PostMapping(value = "/queryByKeyword")
  public JsonResult<Object> query(@RequestBody QueryKeywordReq req) {

    final JsonResult<Object> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(req, "req");
        ParaCheckUtil.checkParaNotNull(req.keyword, "keyword");
        ParaCheckUtil.checkParaNotNull(req.tenant, "tenant");
        ParaCheckUtil.checkEquals(req.tenant, RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        String tenant = ms.tenant;
        String workspace = ms.workspace;

        List<SearchKeywordRet> ret = new ArrayList<>();
        List<Future<SearchKeywordRet>> futures = new ArrayList<>();
        futures.add(queryThreadPool.submit(() -> {
          return searchLogEntity(req.keyword, tenant, workspace);
        }));

        futures.add(queryThreadPool.submit(() -> {
          return searchFolderEntity(req.keyword, tenant, workspace);
        }));

        futures.add(queryThreadPool.submit(() -> {
          return searchDashboardEntity(req.keyword, tenant, workspace);
        }));

        futures.add(queryThreadPool.submit(() -> {
          return searchInfraEntity(req.keyword, tenant, workspace);
        }));

        futures.add(queryThreadPool.submit(() -> {
          return searchAppEntity(req.keyword, tenant, workspace);
        }));

        futures.add(queryThreadPool.submit(() -> {
          return searchAlarmEntity(req.keyword, tenant, workspace);
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

  @ResponseBody
  @PostMapping(value = "/configSearch")
  public JsonResult<Object> configSearch(@RequestBody QueryKeywordReq req) {

    final JsonResult<Object> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(req, "req");
        ParaCheckUtil.checkParaNotNull(req.keyword, "keyword");
        ParaCheckUtil.checkParaNotNull(req.tenant, "tenant");
        ParaCheckUtil.checkEquals(req.tenant, RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");
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
    queryExample.getParams().put("ip",
        Pattern.compile(String.format("^.*%s.*$", keyword), Pattern.CASE_INSENSITIVE));
    queryExample.getParams().put("hostname",
        Pattern.compile(String.format("^.*%s.*$", keyword), Pattern.CASE_INSENSITIVE));
    if (StringUtils.isNotBlank(workspace)) {
      queryExample.getParams().put("_workspace", workspace);
    }

    List<Map<String, Object>> list = dataClientService
        .fuzzyByExample(TenantMetaUtil.genTenantServerTableName(tenant), queryExample);

    return genSearchResult("infra", list);
  }

  public SearchKeywordRet searchAppEntity(String keyword, String tenant, String workspace) {

    QueryExample queryExample = new QueryExample();
    queryExample.getParams().put("app",
        Pattern.compile(String.format("^.*%s.*$", keyword), Pattern.CASE_INSENSITIVE));
    if (StringUtils.isNotBlank(workspace)) {
      queryExample.getParams().put("_workspace", workspace);
    }
    List<Map<String, Object>> list = dataClientService
        .fuzzyByExample(TenantMetaUtil.genTenantAppTableName(tenant), queryExample);

    return genSearchResult("app", list);
  }

  public SearchKeywordRet searchAlarmEntity(String keyword, String tenant, String workspace) {

    return genSearchResult("alarm", alarmRuleService.getListByKeyword(keyword, tenant, workspace));
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
