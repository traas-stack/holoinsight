/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.biz.service.AlarmHistoryService;
import io.holoinsight.server.home.biz.service.AlertGroupService;
import io.holoinsight.server.home.biz.service.AlertRuleService;
import io.holoinsight.server.home.biz.service.AlertSubscribeService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.converter.AlarmRuleConverter;
import io.holoinsight.server.home.dal.mapper.CustomPluginMapper;
import io.holoinsight.server.home.dal.model.AlarmRule;
import io.holoinsight.server.home.dal.model.AlarmSubscribe;
import io.holoinsight.server.home.dal.model.CustomPlugin;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.AlarmGroupDTO;
import io.holoinsight.server.home.dal.model.dto.AlarmSubscribeInfo;
import io.holoinsight.server.home.facade.AlarmHistoryDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.common.SecurityResource;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.home.web.security.CheckTypeEnum;
import io.holoinsight.server.home.web.security.LevelAuthorizationAccess;
import io.holoinsight.server.home.web.security.ParameterSecurityService;
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

import static io.holoinsight.server.home.facade.AlarmRuleDTO.tryParseLink;
import static io.holoinsight.server.home.facade.utils.SecurityMethodCategory.create;
import static io.holoinsight.server.home.facade.utils.SecurityMethodCategory.update;

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

  @Autowired
  private RequestContextAdapter requestContextAdapter;

  @Autowired
  private ParameterSecurityService parameterSecurityService;

  @Value("${holoinsight.home.domain}")
  private String domain;

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!alarmRuleDTO"},
      contentContainer = "CUSTOM", checkType = CheckTypeEnum.CUSTOMCHECK,
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmRuleLevelAuthorizationChecker")
  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Long> save(@RequestBody @SecurityResource(create) AlarmRuleDTO alarmRuleDTO) {
    final JsonResult<Long> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        check(alarmRuleDTO);
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu && StringUtils.isBlank(alarmRuleDTO.getCreator())) {
          alarmRuleDTO.setCreator(mu.getLoginName());
        }
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          alarmRuleDTO.setTenant(ms.tenant);
        }
        if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
          alarmRuleDTO.setWorkspace(ms.workspace);
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

        userOpLogService.append("alarm_rule", id, OpType.CREATE, mu.getLoginName(), ms.getTenant(),
            ms.getWorkspace(), J.toJson(alarmRuleDTO), null, null, "alarm_rule_create");
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
      contentContainer = "CUSTOM", checkType = CheckTypeEnum.CUSTOMCHECK,
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmRuleLevelAuthorizationChecker",
      contentTargetCollectClass = "")
  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> update(
      @RequestBody @SecurityResource(update) AlarmRuleDTO alarmRuleDTO) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        check(alarmRuleDTO);
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        AlarmRuleDTO item =
            alarmRuleService.queryById(alarmRuleDTO.getId(), ms.getTenant(), ms.getWorkspace());

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
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(item), J.toJson(alarmRuleDTO), null, "alarm_rule_update");

        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @GetMapping("/query/{id}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlarmRuleDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<AlarmRuleDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        AlarmRuleDTO save = alarmRuleService.queryById(id, ms.getTenant(), ms.getWorkspace());
        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @GetMapping("/queryByIds/{ids}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<AlarmRuleDTO>> queryByIds(@PathVariable("ids") String ids) {
    final JsonResult<List<AlarmRuleDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(ids, "ids");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        List<AlarmRuleDTO> alarmRuleDTOS = new ArrayList<>();
        String[] idArray = StringUtils.split(ids, ",");
        for (String id : idArray) {
          AlarmRuleDTO alarmRuleDTO =
              alarmRuleService.queryById(Long.parseLong(id), ms.getTenant(), ms.getWorkspace());
          if (null == alarmRuleDTO)
            continue;
          alarmRuleDTOS.add(alarmRuleDTO);
        }
        JsonResult.createSuccessResult(result, alarmRuleDTOS);
      }
    });

    return result;
  }

  @DeleteMapping(value = "/delete/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> deleteById(@PathVariable("id") Long id) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        boolean rtn = false;
        AlarmRuleDTO alarmRule = alarmRuleService.queryById(id, ms.getTenant(), ms.getWorkspace());
        if (alarmRule != null) {
          rtn = alarmRuleService.deleteById(id);
        }

        userOpLogService.append("alarm_rule", id, OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(alarmRule), null, null, "alarm_rule_delete");

        JsonResult.createSuccessResult(result, rtn);
      }
    });
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlarmRuleDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlarmRuleDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlarmRuleDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(pageRequest.getTarget(), "target");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          pageRequest.getTarget().setTenant(ms.tenant);
        }
        if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
          pageRequest.getTarget().setWorkspace(ms.workspace);
        }
        JsonResult.createSuccessResult(result, alarmRuleService.getListByPage(pageRequest));
      }
    });

    return result;
  }

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
        boolean myself = StringUtils.equals(req, "true");
        Map<Long, AlarmRuleDTO> map = new HashMap<>();

        List<AlarmRuleDTO> byIds = getRuleListBySubscribe(myself);
        if (!CollectionUtils.isEmpty(byIds)) {
          byIds.forEach(byId -> {
            map.put(byId.getId(), byId);
          });
        }

        List<AlarmRuleDTO> byGroups = getRuleListByGroup(myself);
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

  @PostMapping(value = "/querySubAlarmHistory")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlarmHistoryDTO>> querySubAlarmHistory(
      @RequestParam(value = "myself", required = false) String req,
      @RequestBody MonitorPageRequest<AlarmHistoryDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlarmHistoryDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(pageRequest.getTarget(), "target");
      }

      @Override
      public void doManage() {
        boolean myself = StringUtils.equals(req, "true");
        List<AlarmRuleDTO> alarmRuleDTOS = new ArrayList<>();

        List<AlarmRuleDTO> byIds = getRuleListBySubscribe(myself);
        if (!CollectionUtils.isEmpty(byIds)) {
          alarmRuleDTOS.addAll(byIds);
        }

        List<AlarmRuleDTO> byGroups = getRuleListByGroup(myself);
        if (!CollectionUtils.isEmpty(byGroups)) {
          alarmRuleDTOS.addAll(byGroups);
        }

        if (CollectionUtils.isEmpty(alarmRuleDTOS))
          return;

        MonitorScope ms = RequestContext.getContext().ms;
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          pageRequest.getTarget().setTenant(ms.tenant);
        }

        if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
          pageRequest.getTarget().setWorkspace(ms.workspace);
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

  protected List<AlarmRuleDTO> getRuleListByGroup(boolean myself) {
    RequestContext.getContext();
    String userId = RequestContext.getContext().mu.getUserId();
    List<AlarmGroupDTO> listByUserLike =
        alarmGroupService.getListByUserLike(userId, MonitorCookieUtil.getTenantOrException());

    if (CollectionUtils.isEmpty(listByUserLike))
      return Collections.emptyList();

    List<AlarmRuleDTO> alarmRuleDTOS = new ArrayList<>();
    for (AlarmGroupDTO alarmGroupDTO : listByUserLike) {
      QueryWrapper<AlarmSubscribe> alarmSubscribeQueryWrapper = new QueryWrapper<>();
      alarmSubscribeQueryWrapper.eq("group_id", alarmGroupDTO.getId());

      requestContextAdapter.queryWrapperTenantAdapt(alarmSubscribeQueryWrapper,
          MonitorCookieUtil.getTenantOrException(), requestContextAdapter.getWorkspace(false));
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

  protected List<AlarmRuleDTO> getRuleListBySubscribe(boolean myself) {
    RequestContext.getContext();
    String userId = RequestContext.getContext().mu.getUserId();
    QueryWrapper<AlarmSubscribe> alarmSubscribeQueryWrapper = new QueryWrapper<>();
    alarmSubscribeQueryWrapper.eq("subscriber", userId);

    requestContextAdapter.queryWrapperTenantAdapt(alarmSubscribeQueryWrapper,
        MonitorCookieUtil.getTenantOrException(), requestContextAdapter.getWorkspace(false));
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
