/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.common.service.AlarmHistoryService;
import io.holoinsight.server.common.service.AlertGroupService;
import io.holoinsight.server.common.service.AlertRuleService;
import io.holoinsight.server.common.service.AlertSubscribeService;
import io.holoinsight.server.common.service.UserOpLogService;
import io.holoinsight.server.common.ManageCallback;
import io.holoinsight.server.common.MonitorException;
import io.holoinsight.server.common.scope.AuthTargetType;
import io.holoinsight.server.common.scope.MonitorCookieUtil;
import io.holoinsight.server.common.scope.MonitorUser;
import io.holoinsight.server.common.scope.PowerConstants;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.common.dao.converter.AlarmRuleConverter;
import io.holoinsight.server.home.dal.mapper.CustomPluginMapper;
import io.holoinsight.server.common.dao.entity.AlarmRule;
import io.holoinsight.server.common.dao.entity.AlarmSubscribe;
import io.holoinsight.server.home.dal.model.CustomPlugin;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.common.dao.entity.dto.AlarmGroupDTO;
import io.holoinsight.server.common.dao.entity.dto.AlarmSubscribeInfo;
import io.holoinsight.server.common.dao.entity.dto.AlarmHistoryDTO;
import io.holoinsight.server.common.dao.entity.dto.AlarmRuleDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.home.web.security.LevelAuthorizationAccess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.holoinsight.server.common.dao.entity.dto.AlarmRuleDTO.tryParseLink;

/**
 * @author wangsiyuan
 * @date 2022/4/1 10:27 上午
 */
@RestController
@RequestMapping("/webapi/alarmRule")
public class AlarmRuleFacadeImpl extends BaseFacade {

  @Autowired
  protected AlertRuleService alarmRuleService;

  @Autowired
  protected AlertSubscribeService alarmSubscribeService;

  @Autowired
  protected AlertGroupService alarmGroupService;

  @Autowired
  private AlarmHistoryService alarmHistoryService;

  @Autowired
  private UserOpLogService userOpLogService;

  @Resource
  private CustomPluginMapper customPluginMapper;

  @Resource
  protected AlarmRuleConverter alarmRuleConverter;

  @Value("${holoinsight.home.domain}")
  private String domain;

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!alarmRuleDTO"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmRuleLevelAuthorizationChecker")
  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Long> create(@RequestBody AlarmRuleDTO alarmRuleDTO) {
    final JsonResult<Long> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          alarmRuleDTO.setCreator(mu.getLoginName());
        }
        if (StringUtils.isNotEmpty(tenant)) {
          alarmRuleDTO.setTenant(tenant);
        }
        if (StringUtils.isNotEmpty(workspace)) {
          alarmRuleDTO.setWorkspace(workspace);
        }
        alarmRuleDTO.setGmtCreate(new Date());
        alarmRuleDTO.setGmtModified(new Date());
        Map<String /* metric */, Map<String /* type */, String /* page */>> systemMetrics =
            getMetricPage();
        String parentId = tryGetParentId(alarmRuleDTO);
        tryParseLink(alarmRuleDTO, domain, systemMetrics, parentId);
        if (StringUtils.isBlank(alarmRuleDTO.getSourceType())) {
          alarmRuleDTO.setSourceType("custom");
        }
        Long id = alarmRuleService.save(alarmRuleDTO);

        userOpLogService.append("alarm_rule", id, OpType.CREATE, mu.getLoginName(), tenant,
            workspace, J.toJson(alarmRuleDTO), null, null, "alarm_rule_create");
        JsonResult.createSuccessResult(result, id);
      }
    });

    return result;
  }

  private String tryGetParentId(AlarmRuleDTO alarmRuleDTO) {
    if (!StringUtils.equals(alarmRuleDTO.getSourceType(), "log")
        || alarmRuleDTO.getSourceId() == null) {
      return null;
    }
    CustomPlugin customPlugin = this.customPluginMapper.selectById(alarmRuleDTO.getSourceId());
    return String.valueOf(customPlugin.parentFolderId);
  }

  public static Map<String, Map<String, String>> getMetricPage() {
    return MetaDictUtil.getValue("notification_config", "metric_page",
        new TypeToken<Map<String, Map<String, String>>>() {});
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!alarmRuleDTO"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmRuleLevelAuthorizationChecker")
  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> update(@RequestBody AlarmRuleDTO alarmRuleDTO) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        AlarmRuleDTO item = alarmRuleService.queryById(alarmRuleDTO.getId(), tenant, workspace);

        if (null == item) {
          throw new MonitorException("cannot find record: " + alarmRuleDTO.getId());
        }

        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu && StringUtils.isBlank(alarmRuleDTO.getModifier())) {
          alarmRuleDTO.setModifier(mu.getLoginName());
        }
        alarmRuleDTO.setGmtModified(new Date());
        getMetricPage();
        boolean save = alarmRuleService.updateById(alarmRuleDTO);

        userOpLogService.append("alarm_rule", alarmRuleDTO.getId(), OpType.UPDATE,
            RequestContext.getContext().mu.getLoginName(), tenant, workspace, J.toJson(item),
            J.toJson(alarmRuleDTO), null, "alarm_rule_update");

        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!id"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmRuleLevelAuthorizationChecker")
  @GetMapping("/query/{id}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlarmRuleDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<AlarmRuleDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        AlarmRuleDTO save = alarmRuleService.queryById(id, tenant, workspace);
        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!ids"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmRuleLevelAuthorizationChecker")
  @GetMapping("/queryByIds/{ids}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<AlarmRuleDTO>> queryByIds(@PathVariable("ids") String ids) {
    final JsonResult<List<AlarmRuleDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        List<AlarmRuleDTO> alarmRuleDTOS = new ArrayList<>();
        String[] idArray = StringUtils.split(ids, ",");
        for (String id : idArray) {
          AlarmRuleDTO alarmRuleDTO =
              alarmRuleService.queryById(Long.parseLong(id), tenant, workspace);
          if (null == alarmRuleDTO)
            continue;
          alarmRuleDTOS.add(alarmRuleDTO);
        }
        JsonResult.createSuccessResult(result, alarmRuleDTOS);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!id"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmRuleLevelAuthorizationChecker")
  @DeleteMapping(value = "/delete/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> deleteById(@PathVariable("id") Long id) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        boolean rtn = false;
        AlarmRuleDTO alarmRule = alarmRuleService.queryById(id, tenant, workspace);
        if (alarmRule != null) {
          rtn = alarmRuleService.deleteById(id);
        }

        userOpLogService.append("alarm_rule", id, OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), tenant, workspace, J.toJson(alarmRule),
            null, null, "alarm_rule_delete");

        JsonResult.createSuccessResult(result, rtn);
      }
    });
    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!pageRequest"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmRuleLevelAuthorizationChecker")
  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlarmRuleDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlarmRuleDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlarmRuleDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        if (StringUtils.isNotEmpty(tenant)) {
          pageRequest.getTarget().setTenant(tenant);
        }
        if (StringUtils.isNotEmpty(workspace)) {
          pageRequest.getTarget().setWorkspace(workspace);
        }
        JsonResult.createSuccessResult(result, alarmRuleService.getListByPage(pageRequest));
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!req"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmRuleLevelAuthorizationChecker")
  @GetMapping(value = "/querySubscribeList")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<AlarmRuleDTO>> querySubscribeList(
      @RequestParam(value = "myself", required = false) String req) {
    final JsonResult<List<AlarmRuleDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        boolean myself = StringUtils.equals(req, "true");
        Map<Long, AlarmRuleDTO> map = new HashMap<>();

        List<AlarmRuleDTO> byIds = getRuleListBySubscribe(myself, tenant, workspace);
        if (!CollectionUtils.isEmpty(byIds)) {
          byIds.forEach(byId -> {
            map.put(byId.getId(), byId);
          });
        }

        List<AlarmRuleDTO> byGroups = getRuleListByGroup(myself, tenant, workspace);
        if (!CollectionUtils.isEmpty(byGroups)) {
          byGroups.forEach(byGroup -> {
            map.put(byGroup.getId(), byGroup);
          });
        }

        JsonResult.createSuccessResult(result, new ArrayList<>(map.values()));

      }
    });
    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!req", "PARAMETER" + ":$!pageRequest"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmRuleLevelAuthorizationChecker")
  @PostMapping(value = "/querySubAlarmHistory")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlarmHistoryDTO>> querySubAlarmHistory(
      @RequestParam(value = "myself", required = false) String req,
      @RequestBody MonitorPageRequest<AlarmHistoryDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlarmHistoryDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        boolean myself = StringUtils.equals(req, "true");
        List<AlarmRuleDTO> alarmRuleDTOS = new ArrayList<>();

        List<AlarmRuleDTO> byIds = getRuleListBySubscribe(myself, tenant, workspace);
        if (!CollectionUtils.isEmpty(byIds)) {
          alarmRuleDTOS.addAll(byIds);
        }

        List<AlarmRuleDTO> byGroups = getRuleListByGroup(myself, tenant, workspace);
        if (!CollectionUtils.isEmpty(byGroups)) {
          alarmRuleDTOS.addAll(byGroups);
        }

        if (CollectionUtils.isEmpty(alarmRuleDTOS)) {
          return;
        }

        if (StringUtils.isNotEmpty(tenant)) {
          pageRequest.getTarget().setTenant(tenant);
        }

        if (StringUtils.isNotEmpty(workspace)) {
          pageRequest.getTarget().setWorkspace(workspace);
        }

        List<String> uniqueIds = alarmRuleDTOS.stream()
            .map(alarmRuleDTO -> alarmRuleDTO.getRuleType() + "_" + alarmRuleDTO.getId())
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(uniqueIds))
          return;
        JsonResult.createSuccessResult(result,
            alarmHistoryService.getListByPage(pageRequest, uniqueIds));

      }
    });
    return result;
  }

  protected List<AlarmRuleDTO> getRuleListByGroup(boolean myself, String tenant, String workspace) {
    String userId = RequestContext.getContext().mu.getUserId();
    List<AlarmGroupDTO> listByUserLike =
        alarmGroupService.getListByUserLike(userId, MonitorCookieUtil.getTenantOrException());

    if (CollectionUtils.isEmpty(listByUserLike))
      return Collections.emptyList();

    List<AlarmRuleDTO> alarmRuleDTOS = new ArrayList<>();
    for (AlarmGroupDTO alarmGroupDTO : listByUserLike) {
      QueryWrapper<AlarmSubscribe> alarmSubscribeQueryWrapper = new QueryWrapper<>();
      alarmSubscribeQueryWrapper.eq("group_id", alarmGroupDTO.getId());

      requestContextAdapter.queryWrapperTenantAdapt(alarmSubscribeQueryWrapper, tenant, workspace);
      List<AlarmSubscribeInfo> alarmSubscribeInfos =
          alarmSubscribeService.queryByMap(alarmSubscribeQueryWrapper);

      if (CollectionUtils.isEmpty(alarmSubscribeInfos)) {
        continue;
      }

      List<String> ruleIds = new ArrayList<>();
      for (AlarmSubscribeInfo alarmSubscribeInfo : alarmSubscribeInfos) {
        if (StringUtils.isBlank(alarmSubscribeInfo.getUniqueId())
            || !alarmSubscribeInfo.getUniqueId().contains("_"))
          continue;

        String[] array = alarmSubscribeInfo.getUniqueId().split("_");

        ruleIds.add(array[1]);
      }

      QueryWrapper<AlarmRule> queryWrapper = new QueryWrapper<>();
      queryWrapper.in("id", ruleIds);
      if (myself) {
        String loginName = RequestContext.getContext().mu.getLoginName();
        queryWrapper
            .and(wrapper -> wrapper.eq("creator", loginName).or().eq("modifier", loginName));
      }
      List<AlarmRule> alarmRules = alarmRuleService.list(queryWrapper);
      List<AlarmRuleDTO> byIds = this.alarmRuleConverter.dosToDTOs(alarmRules);
      if (CollectionUtils.isEmpty(byIds)) {
        continue;
      }
      alarmRuleDTOS.addAll(byIds);
    }

    return alarmRuleDTOS;

  }

  protected List<AlarmRuleDTO> getRuleListBySubscribe(boolean myself, String tenant,
      String workspace) {
    RequestContext.getContext();
    String userId = RequestContext.getContext().mu.getUserId();
    QueryWrapper<AlarmSubscribe> alarmSubscribeQueryWrapper = new QueryWrapper<>();
    alarmSubscribeQueryWrapper.eq("subscriber", userId);

    requestContextAdapter.queryWrapperTenantAdapt(alarmSubscribeQueryWrapper, tenant, workspace);
    List<AlarmSubscribeInfo> alarmSubscribeInfos =
        alarmSubscribeService.queryByMap(alarmSubscribeQueryWrapper);

    if (CollectionUtils.isEmpty(alarmSubscribeInfos))
      return null;

    List<String> ruleIds = new ArrayList<>();
    for (AlarmSubscribeInfo alarmSubscribeInfo : alarmSubscribeInfos) {
      if (StringUtils.isBlank(alarmSubscribeInfo.getUniqueId())
          || !alarmSubscribeInfo.getUniqueId().contains("_"))
        continue;

      String[] array = alarmSubscribeInfo.getUniqueId().split("_");

      ruleIds.add(array[1]);
    }
    QueryWrapper<AlarmRule> queryWrapper = new QueryWrapper<>();
    queryWrapper.in("id", ruleIds);
    if (myself) {
      String loginName = RequestContext.getContext().mu.getLoginName();
      queryWrapper.and(wrapper -> wrapper.eq("creator", loginName).or().eq("modifier", loginName));
    }
    List<AlarmRule> alarmRules = alarmRuleService.list(queryWrapper);
    return this.alarmRuleConverter.dosToDTOs(alarmRules);
  }

}
