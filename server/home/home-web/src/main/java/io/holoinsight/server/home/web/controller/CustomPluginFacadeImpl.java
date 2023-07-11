/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.AlarmMetricService;
import io.holoinsight.server.home.biz.service.CustomPluginService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.AlarmMetric;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric.AfterFilter;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric.Metric;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf.SplitCol;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf.SpmCols;
import io.holoinsight.server.home.dal.model.dto.conf.FilterType;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.controller.model.LogSplitReq;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jsy1001de
 * @version 1.0: CustomPluginFacadeImpl.java, v 0.1 2022年03月15日 10:25 上午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/customPlugin")
public class CustomPluginFacadeImpl extends BaseFacade {

  @Autowired
  private CustomPluginService customPluginService;

  @Autowired
  private UserOpLogService userOpLogService;

  @Autowired
  private AlarmMetricService alarmMetricService;

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<CustomPluginDTO> update(@RequestBody CustomPluginDTO customPluginDTO) {
    final JsonResult<CustomPluginDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(customPluginDTO.id, "id");
        // ParaCheckUtil.checkParaNotBlank(customPluginDTO.tenant);
        ParaCheckUtil.checkParaNotNull(customPluginDTO.parentFolderId, "parentFolderId");
        ParaCheckUtil.checkParaNotBlank(customPluginDTO.name, "name");
        ParaCheckUtil.checkParaNotBlank(customPluginDTO.pluginType, "pluginType");
        ParaCheckUtil.checkParaNotNull(customPluginDTO.status, "status");
        ParaCheckUtil.checkParaNotNull(customPluginDTO.periodType, "periodType");
        ParaCheckUtil.checkParaNotNull(customPluginDTO.conf, "conf");

        ParaCheckUtil.checkParaNotNull(customPluginDTO.getTenant(), "tenant");
        MonitorScope ms = RequestContext.getContext().ms;
        ParaCheckUtil.checkEquals(customPluginDTO.getTenant(), ms.getTenant(), "tenant is illegal");

        CustomPluginDTO item = customPluginService.queryById(customPluginDTO.getId(),
            ms.getTenant(), ms.getWorkspace());

        if (null == item) {
          throw new MonitorException("cannot find record: " + customPluginDTO.getId());
        }
        if (!item.getTenant().equalsIgnoreCase(customPluginDTO.getTenant())) {
          throw new MonitorException("the tenant parameter is invalid");
        }
      }

      @Override
      public void doManage() {
        addSpmCols(customPluginDTO.conf);

        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          customPluginDTO.setModifier(mu.getLoginName());
        }
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          customPluginDTO.setTenant(ms.tenant);
        }
        if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
          customPluginDTO.setWorkspace(ms.workspace);
        }
        customPluginDTO.setGmtModified(new Date());
        CustomPluginDTO update = customPluginService.updateByRequest(customPluginDTO);
        JsonResult.createSuccessResult(result, update);
        assert mu != null;
        userOpLogService.append("custom_plugin", update.getId(), OpType.UPDATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(customPluginDTO), J.toJson(update), null,
            "custom_plugin_update");

      }
    });

    return result;
  }

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<CustomPluginDTO> save(@RequestBody CustomPluginDTO customPluginDTO) {
    final JsonResult<CustomPluginDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(customPluginDTO.parentFolderId, "parentFolderId");
        ParaCheckUtil.checkParaNotBlank(customPluginDTO.name, "name");
        ParaCheckUtil.checkParaNotBlank(customPluginDTO.pluginType, "pluginType");
        ParaCheckUtil.checkParaNotNull(customPluginDTO.status, "status");
        ParaCheckUtil.checkParaNotNull(customPluginDTO.periodType, "periodType");
        ParaCheckUtil.checkParaNotNull(customPluginDTO.conf, "conf");
      }

      @Override
      public void doManage() {
        addSpmCols(customPluginDTO.conf);

        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          customPluginDTO.setCreator(mu.getLoginName());
          customPluginDTO.setModifier(mu.getLoginName());
        }
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          customPluginDTO.setTenant(ms.tenant);
        }

        if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
          customPluginDTO.setWorkspace(ms.workspace);
        }
        customPluginDTO.setTenant(MonitorCookieUtil.getTenantOrException());
        CustomPluginDTO save = customPluginService.create(customPluginDTO);
        JsonResult.createSuccessResult(result, save);

        assert mu != null;
        userOpLogService.append("custom_plugin", save.getId(), OpType.CREATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(customPluginDTO), null, null,
            "custom_plugin_create");

      }
    });

    return result;
  }

  @PostMapping("/updateParentFolderId")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> updateParentFolderId(@RequestBody CustomPluginDTO customPluginDTO) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(customPluginDTO.id, "id");
        ParaCheckUtil.checkParaNotNull(customPluginDTO.parentFolderId, "parentFolderId");
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;

        CustomPluginDTO update =
            customPluginService.queryById(customPluginDTO.id, ms.getTenant(), ms.getWorkspace());
        if (null == update) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }

        if (null != mu) {
          update.setModifier(mu.getLoginName());
        }
        if (!StringUtils.isEmpty(ms.tenant)) {
          update.setTenant(ms.tenant);
        }

        if (!StringUtils.isEmpty(ms.workspace)) {
          update.setWorkspace(ms.workspace);
        }
        update.setGmtModified(new Date());
        update.setParentFolderId(customPluginDTO.parentFolderId);
        CustomPluginDTO custom = customPluginService.updateByRequest(update);
        JsonResult.createSuccessResult(result, true);
        assert mu != null;
        userOpLogService.append("custom_plugin", update.getId(), OpType.UPDATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(custom), J.toJson(update), null,
            "custom_plugin_update");

      }
    });

    return result;
  }

  @GetMapping(value = "/query/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<CustomPluginDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<CustomPluginDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        CustomPluginDTO customPluginDTO =
            customPluginService.queryById(id, ms.getTenant(), ms.getWorkspace());

        if (null == customPluginDTO) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, customPluginDTO);
      }
    });
    return result;
  }

  @DeleteMapping(value = "/delete/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> deleteById(@PathVariable("id") Long id) {
    final JsonResult<Object> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        CustomPluginDTO byId = customPluginService.queryById(id, ms.getTenant(), ms.getWorkspace());
        if (null == byId) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        customPluginService.deleteById(id);
        JsonResult.createSuccessResult(result, null);
        userOpLogService.append("custom_plugin", byId.getId(), OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(byId), null, null, "custom_plugin_delete");

      }
    });
    return result;
  }

  @GetMapping(value = "/queryByParentFolderId/{parentFolderId}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<CustomPluginDTO>> queryByParentFolderIdAndTenant(
      @PathVariable("parentFolderId") Long parentFolderId) {
    final JsonResult<List<CustomPluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(parentFolderId, "parentFolderId");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("parent_folder_id", parentFolderId);
        conditions.put("tenant", MonitorCookieUtil.getTenantOrException());
        if (StringUtils.isNotBlank(ms.getWorkspace())) {
          conditions.put("workspace", ms.getWorkspace());
        }
        List<CustomPluginDTO> byMap = customPluginService.findByMap(conditions);

        if (!CollectionUtils.isEmpty(byMap)) {
          for (CustomPluginDTO customPluginDTO : byMap) {
            List<AlarmMetric> alarmMetrics = new ArrayList<>();
            for (CollectMetric collectMetric : customPluginDTO.getConf().collectMetrics) {
              List<AlarmMetric> db = alarmMetricService
                  .queryByMetric(collectMetric.getTargetTable(), ms.getTenant(), ms.getWorkspace());
              if (!CollectionUtils.isEmpty(db)) {
                alarmMetrics.addAll(db);
              }
            }
            customPluginDTO.setAlarmMetrics(alarmMetrics);
          }
        }

        JsonResult.createSuccessResult(result, byMap);
      }
    });
    return result;
  }

  @GetMapping(value = "/queryByNameLikeAndTenant/{name}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<CustomPluginDTO>> queryByNameLikeAndTenant(
      @PathVariable("name") String name) {
    final JsonResult<List<CustomPluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(name, "name");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("name", name);
        conditions.put("tenant", MonitorCookieUtil.getTenantOrException());
        if (StringUtils.isNotBlank(ms.getWorkspace())) {
          conditions.put("workspace", ms.getWorkspace());
        }
        JsonResult.createSuccessResult(result, customPluginService.findByMap(conditions));
      }
    });
    return result;
  }

  @GetMapping(value = "/queryByCreatorAndTenant/{creator}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<CustomPluginDTO>> queryByCreatorAndTenant(
      @PathVariable("creator") String creator) {
    final JsonResult<List<CustomPluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(creator, "creator");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("creator", creator);
        conditions.put("tenant", MonitorCookieUtil.getTenantOrException());
        if (StringUtils.isNotBlank(ms.getWorkspace())) {
          conditions.put("workspace", ms.getWorkspace());
        }
        JsonResult.createSuccessResult(result, customPluginService.findByMap(conditions));
      }
    });
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<CustomPluginDTO>> pageQuery(
      @RequestBody MonitorPageRequest<CustomPluginDTO> customPluginRequest) {
    final JsonResult<MonitorPageResult<CustomPluginDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(customPluginRequest.getTarget(), "target");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        customPluginRequest.getTarget().setTenant(ms.getTenant());
        if (StringUtils.isNotBlank(ms.getWorkspace())) {
          customPluginRequest.getTarget().setWorkspace(ms.getWorkspace());
        }
        JsonResult.createSuccessResult(result,
            customPluginService.getListByPage(customPluginRequest));
      }
    });

    return result;
  }

  @ResponseBody
  @RequestMapping(value = "/log/presplit", method = RequestMethod.POST)
  public JsonResult<Object> presplit(@RequestBody LogSplitReq req) {

    final JsonResult<Object> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(req.getSampleLogs(), "sampleLogs");
      }

      @Override
      public void doManage() {
        Object strings;

        if (req.getSplitType().equalsIgnoreCase("SEP")) {
          strings = presplitSeperators(req.getSampleLogs(), req.getSeperators());
        } else if (req.getSplitType().equalsIgnoreCase("REGEXP")) {
          strings = presplitRegexp(req.getSampleLogs(), req.getSeperators());
        } else {
          throw new RuntimeException(
              String.format("not supported split type %s", req.getSplitType()));
        }

        JsonResult.createSuccessResult(result, strings);
      }
    });

    return result;
  }

  public List<String[]> presplitSeperators(List<String> sampleLogs, String seperators) {
    List<String[]> strings = new ArrayList<>();
    sampleLogs.forEach(log -> {
      String[] string = StringUtils.splitByWholeSeparatorPreserveAllTokens(log, seperators);
      strings.add(string);
    });

    return strings;
  }

  public List<String[]> presplitRegexp(List<String> sampleLogs, String expression) {
    List<String[]> strings = new ArrayList<>();
    sampleLogs.forEach(log -> {
      Pattern pattern = Pattern.compile(expression);

      Matcher matcher = pattern.matcher(log);
      if (!matcher.find()) {
        return;
      }

      int count = matcher.groupCount();

      String[] string = new String[count];

      for (int i = 1; i <= count; i++) {
        string[i - 1] = matcher.group(i);
      }

      strings.add(string);
    });

    return strings;
  }

  private Boolean checkSpmConditions(CustomPluginConf conf) {
    if (null == conf || null == conf.spm) {
      return Boolean.FALSE;
    } else if (Boolean.FALSE == conf.spm) {
      return Boolean.FALSE;
    }

    if (null == conf.spmCols) {
      throw new MonitorException("spmCols is null");
    }

    SpmCols spmCols = conf.spmCols;
    if (StringUtils.isBlank(spmCols.resultKey)) {
      throw new MonitorException("resultCols is null");
    }
    if (CollectionUtils.isEmpty(spmCols.successValue)) {
      throw new MonitorException("successValue is null");
    }
    if (StringUtils.isBlank(spmCols.costKey)) {
      throw new MonitorException("costCols is null");
    }

    return Boolean.TRUE;
  }

  private void addSpmCols(CustomPluginConf conf) {

    Boolean spm = checkSpmConditions(conf);

    List<CollectMetric> newCollectMetrics = new ArrayList<>();
    int cols = 0;
    if (!CollectionUtils.isEmpty(conf.collectMetrics)) {
      for (CollectMetric collectMetric : conf.collectMetrics) {
        if (null == collectMetric.spm || Boolean.FALSE == collectMetric.spm) {
          newCollectMetrics.add(collectMetric);
          continue;
        }

        if (spm == Boolean.FALSE) {
          continue;
        }
        newCollectMetrics.add(collectMetric);
        cols++;
      }
    }

    // add spm metrics
    if (spm == Boolean.TRUE && cols == 0) {
      SpmCols spmCols = conf.spmCols;

      List<String> tags = new ArrayList<>();
      List<SplitCol> splitCols = conf.splitCols;
      if (!CollectionUtils.isEmpty(splitCols)) {
        splitCols.forEach(splitCol -> {
          if (splitCol.colType.equalsIgnoreCase("DIM")) {
            tags.add(splitCol.name);
          }
        });
      }

      newCollectMetrics.add(genTotalCollectMetric(spmCols, tags));
      newCollectMetrics.add(genSuccessCollectMetric(spmCols, tags));
      newCollectMetrics.add(genFailCollectMetric(spmCols, tags));
      newCollectMetrics.add(genCostCollectMetric(spmCols, tags));
      newCollectMetrics.add(genSuccessPercentCollectMetric(tags));
    }

    conf.setCollectMetrics(newCollectMetrics);

  }

  private CollectMetric genTotalCollectMetric(SpmCols spmCols, List<String> tags) {
    CollectMetric collectMetric = new CollectMetric();
    collectMetric.setTableName("total");
    collectMetric.setMetricType("count");
    collectMetric.setTags(tags);
    collectMetric.setSpm(true);
    Metric metric = new Metric();
    metric.setFunc("count");
    metric.setName("value");
    if (StringUtils.isNotBlank(spmCols.countKey)) {
      collectMetric.setMetricType("select");
      metric.setFunc("sum");
      metric.setName(spmCols.countKey);
    }
    collectMetric.setMetrics(Collections.singletonList(metric));
    return collectMetric;
  }

  private CollectMetric genSuccessCollectMetric(SpmCols spmCols, List<String> tags) {
    CollectMetric collectMetric = new CollectMetric();
    collectMetric.setTableName("success");
    collectMetric.setMetricType("count");
    collectMetric.setTags(tags);
    collectMetric.setSpm(true);
    Metric metric = new Metric();
    metric.setFunc("count");
    metric.setName("value");
    if (StringUtils.isNotBlank(spmCols.countKey)) {
      collectMetric.setMetricType("select");
      metric.setFunc("sum");
      metric.setName(spmCols.countKey);
    }
    collectMetric.setMetrics(Collections.singletonList(metric));

    AfterFilter afterFilter = new AfterFilter();
    afterFilter.setName(spmCols.resultKey);
    afterFilter.setFilterType(FilterType.IN);
    afterFilter.setValues(spmCols.successValue);
    collectMetric.setAfterFilters(Collections.singletonList(afterFilter));

    return collectMetric;
  }

  private CollectMetric genFailCollectMetric(SpmCols spmCols, List<String> tags) {
    CollectMetric collectMetric = new CollectMetric();
    collectMetric.setTableName("fail");
    collectMetric.setMetricType("count");
    collectMetric.setTags(tags);
    collectMetric.setSpm(true);
    Metric metric = new Metric();
    metric.setFunc("count");
    metric.setName("value");
    if (StringUtils.isNotBlank(spmCols.countKey)) {
      collectMetric.setMetricType("select");
      metric.setFunc("sum");
      metric.setName(spmCols.countKey);
    }
    collectMetric.setMetrics(Collections.singletonList(metric));

    AfterFilter afterFilter = new AfterFilter();
    afterFilter.setName(spmCols.resultKey);
    afterFilter.setFilterType(FilterType.NOT_IN);
    afterFilter.setValues(spmCols.successValue);
    collectMetric.setAfterFilters(Collections.singletonList(afterFilter));

    return collectMetric;
  }

  private CollectMetric genCostCollectMetric(SpmCols spmCols, List<String> tags) {
    CollectMetric collectMetric = new CollectMetric();
    collectMetric.setTableName("cost");
    collectMetric.setMetricType("select");
    collectMetric.setTags(tags);
    collectMetric.setSpm(true);
    Metric metric = new Metric();
    metric.setFunc("avg");
    metric.setName(spmCols.costKey);
    collectMetric.setMetrics(Collections.singletonList(metric));

    return collectMetric;
  }

  private CollectMetric genSuccessPercentCollectMetric(List<String> tags) {
    CollectMetric collectMetric = new CollectMetric();
    collectMetric.setTableName("successPercent");
    collectMetric.setMetricType("count");
    collectMetric.setTags(tags);
    collectMetric.setSpm(true);
    Metric metric = new Metric();
    metric.setFunc("count");
    metric.setName("value");
    collectMetric.setMetrics(Collections.singletonList(metric));


    return collectMetric;
  }
}
