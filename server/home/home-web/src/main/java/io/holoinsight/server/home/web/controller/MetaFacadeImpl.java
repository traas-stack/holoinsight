/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.common.util.scope.RequestContext.Context;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;
import io.holoinsight.server.meta.facade.service.TableClientService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaFacadeImpl.java, v 0.1 2022年03月18日 11:01 上午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/meta")
public class MetaFacadeImpl extends BaseFacade {

  @Autowired
  private DataClientService dataClientService;

  @Autowired
  private TableClientService tableClientService;

  @Autowired
  private TenantInitService tenantInitService;

  @PostMapping("/queryByTenantServer")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<Map<String, Object>>> queryByTenantServer(
      @RequestBody Map<String, Object> condition) {
    final JsonResult<List<Map<String, Object>>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        // ParaCheckUtil.checkParaNotEmpty(condition, "condition");
      }

      @Override
      public void doManage() {
        QueryExample queryExample = new QueryExample();
        queryExample.setParams(new HashMap<>());
        queryExample.getParams().putAll(condition);
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, String> conditions =
            tenantInitService.getTenantWorkspaceMetaConditions(ms.getWorkspace());
        if (!CollectionUtils.isEmpty(conditions)) {
          queryExample.getParams().putAll(conditions);
        }

        List<Map<String, Object>> list = dataClientService
            .queryByExample(tenantInitService.getTenantServerTable(ms.getTenant()), queryExample);
        JsonResult.createSuccessResult(result, list);
      }
    });

    return result;
  }

  @PostMapping("/queryByTenantApp")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<Map<String, Object>>> queryByTenantApp(
      @RequestBody Map<String, Object> condition) {
    final JsonResult<List<Map<String, Object>>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        // ParaCheckUtil.checkParaNotEmpty(condition, "condition");
      }

      @Override
      public void doManage() {
        QueryExample queryExample = new QueryExample();
        queryExample.setParams(new HashMap<>());
        queryExample.getParams().putAll(condition);

        MonitorScope ms = RequestContext.getContext().ms;
        if (StringUtils.isNotBlank(ms.getWorkspace())) {
          queryExample.getParams().put("_workspace", ms.getWorkspace());
        }

        List<Map<String, Object>> list = dataClientService
            .queryByExample(tenantInitService.getTenantAppTable(ms.getTenant()), queryExample);
        JsonResult.createSuccessResult(result, list);
      }
    });

    return result;
  }

  @PostMapping("/fuzzyQueryByTenantServer")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<Map<String, Object>>> fuzzyQueryByTenant(
      @RequestBody Map<String, Object> condition) {
    final JsonResult<List<Map<String, Object>>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotEmpty(condition, "condition");
      }

      @Override
      public void doManage() {
        QueryExample queryExample = new QueryExample();
        for (Map.Entry<String, Object> entry : condition.entrySet()) {
          if (entry.getKey().equalsIgnoreCase("ip")
              || entry.getKey().equalsIgnoreCase("hostname")) {
            Pattern pattern = Pattern.compile(String.format("^.*%s.*$", entry.getValue()),
                Pattern.CASE_INSENSITIVE);
            queryExample.getParams().put(entry.getKey(), pattern);
          } else {
            queryExample.getParams().put(entry.getKey(), entry.getValue());
          }
        }
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, String> conditions =
            tenantInitService.getTenantWorkspaceMetaConditions(ms.getWorkspace());
        if (!CollectionUtils.isEmpty(conditions)) {
          queryExample.getParams().putAll(conditions);
        }
        List<Map<String, Object>> list = dataClientService
            .fuzzyByExample(tenantInitService.getTenantServerTable(ms.getTenant()), queryExample);
        JsonResult.createSuccessResult(result, list);
      }
    });

    return result;
  }

  /**
   * 批量导入
   */
  @PostMapping("/{table}/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> save(@PathVariable("table") String table,
      @RequestBody List<Map<String, Object>> metaList) {
    final JsonResult<Object> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(table, "table");
        ParaCheckUtil.checkParaNotEmpty(metaList, "metaList");

        if (!table.startsWith(RequestContext.getContext().ms.getTenant())) {
          throw new MonitorException("table is illegal");
        }
      }

      @Override
      public void doManage() {
        Context context = RequestContext.getContext();
        MonitorUser mu = context.mu;

        List<Map<String, Object>> list = new ArrayList<>();

        for (Map<String, Object> meta : metaList) {
          if (!meta.containsKey("_type"))
            continue;

          Map<String, Object> map = new HashMap<>();
          map.put("_modifier", mu.getLoginName());
          map.put("_modified", System.currentTimeMillis());
          map.putAll(meta);
          list.add(map);
        }

        dataClientService.insertOrUpdate(table, J.toMapList(J.toJson(list)));
        JsonResult.createSuccessResult(result, true);
      }
    });

    return result;
  }

  @GetMapping("/{table}/queryAll")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<Map<String, Object>>> queryAll(@PathVariable("table") String table) {
    final JsonResult<List<Map<String, Object>>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(table, "table");
        if (!table.startsWith(RequestContext.getContext().ms.getTenant())) {
          throw new MonitorException("table is illegal");
        }
      }

      @Override
      public void doManage() {
        List<Map<String, Object>> list = dataClientService.queryAll(table);
        JsonResult.createSuccessResult(result, list);
      }
    });

    return result;
  }

  @PostMapping("/{table}/queryByCondition")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<Map<String, Object>>> queryByCondition(@PathVariable("table") String table,
      @RequestBody Map<String, Object> condition) {
    final JsonResult<List<Map<String, Object>>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotEmpty(condition, "condition");
        ParaCheckUtil.checkParaNotBlank(table, "table");
        if (!table.startsWith(RequestContext.getContext().ms.getTenant())) {
          throw new MonitorException("table is illegal");
        }
      }

      @Override
      public void doManage() {
        QueryExample queryExample = new QueryExample();
        queryExample.setParams(new HashMap<>());
        queryExample.getParams().putAll(condition);
        List<Map<String, Object>> list = dataClientService.queryByExample(table, queryExample);
        JsonResult.createSuccessResult(result, list);
      }
    });

    return result;
  }

  @PostMapping("/{table}/fuzzyQuery")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<Map<String, Object>>> fuzzyQuery(@PathVariable("table") String table,
      @RequestBody Map<String, Object> condition) {
    final JsonResult<List<Map<String, Object>>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotEmpty(condition, "condition");
        ParaCheckUtil.checkParaNotBlank(table, "table");
        if (!table.startsWith(RequestContext.getContext().ms.getTenant())) {
          throw new MonitorException("table is illegal");
        }
      }

      @Override
      public void doManage() {
        QueryExample queryExample = new QueryExample();
        for (Map.Entry<String, Object> entry : condition.entrySet()) {
          if (entry.getKey().equalsIgnoreCase("_type")) {
            queryExample.getParams().put("_type", entry.getValue());
            continue;
          } else if (entry.getKey().equalsIgnoreCase("_workspace")) {
            queryExample.getParams().put("_workspace", entry.getValue());
            continue;
          }
          Pattern pattern = Pattern.compile(String.format("^.*%s.*$", entry.getValue()),
              Pattern.CASE_INSENSITIVE);
          queryExample.getParams().put(entry.getKey(), pattern);
        }

        List<Map<String, Object>> list = dataClientService.fuzzyByExample(table, queryExample);
        JsonResult.createSuccessResult(result, list);
      }
    });

    return result;
  }

  @DeleteMapping("/{table}/deleteByCondition")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> deleteByCondition(@PathVariable("table") String table,
      @RequestBody Map<String, Object> condition) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(table, "table");
        ParaCheckUtil.checkParaNotEmpty(condition, "condition");
        if (!table.startsWith(RequestContext.getContext().ms.getTenant())) {
          throw new MonitorException("table is illegal");
        }
      }

      @Override
      public void doManage() {

        QueryExample queryExample = new QueryExample();
        queryExample.setParams(new HashMap<>());
        queryExample.getParams().putAll(condition);
        dataClientService.deleteByExample(table, queryExample);
        JsonResult.createSuccessResult(result, true);
      }
    });

    return result;
  }

  @PostMapping("/{table}/deleteIndex")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> deleteIndex(@PathVariable("table") String table,
      @RequestBody MetaTableIndex condition) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(table, "table");
        ParaCheckUtil.checkParaNotNull(condition, "condition");
        ParaCheckUtil.checkParaNotBlank(condition.index, "index");
        if (!table.startsWith(RequestContext.getContext().ms.getTenant())) {
          throw new MonitorException("table is illegal");
        }
      }

      @Override
      public void doManage() {
        tableClientService.deleteIndex(table, condition.index);
        JsonResult.createSuccessResult(result, true);
      }
    });

    return result;
  }

  @PostMapping("/{table}/createIndex")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> createIndex(@PathVariable("table") String table,
      @RequestBody MetaTableIndex condition) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(table, "table");
        ParaCheckUtil.checkParaNotNull(condition, "condition");
        ParaCheckUtil.checkParaNotBlank(condition.index, "index");
        if (!table.startsWith(RequestContext.getContext().ms.getTenant())) {
          throw new MonitorException("table is illegal");
        }
      }

      @Override
      public void doManage() {

        tableClientService.createIndex(table, condition.index, condition.sort);
        JsonResult.createSuccessResult(result, true);
      }
    });

    return result;
  }

  @PostMapping("/{table}/getIndexInfo")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<List<Object>> getIndexInfo(@PathVariable("table") String table,
      @RequestBody MetaTableIndex condition) {
    final JsonResult<List<Object>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(table, "table");
        ParaCheckUtil.checkParaNotNull(condition, "condition");
        if (!table.startsWith(RequestContext.getContext().ms.getTenant())) {
          throw new MonitorException("table is illegal");
        }
      }

      @Override
      public void doManage() {

        List<Object> indexInfo = tableClientService.getIndexInfo(table);
        JsonResult.createSuccessResult(result, indexInfo);
      }
    });

    return result;
  }

  @Data
  public static class MetaTableIndex {
    public String index;

    // true ? Direction.ASC : Direction.DESC
    public Boolean sort;
  }
}
