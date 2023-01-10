/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.AlertGroupService;
import io.holoinsight.server.home.biz.service.AlarmHistoryService;
import io.holoinsight.server.home.biz.service.AlertRuleService;
import io.holoinsight.server.home.biz.service.AlertSubscribeService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.AlarmGroupDTO;
import io.holoinsight.server.home.dal.model.dto.AlarmSubscribeInfo;
import io.holoinsight.server.home.facade.AlarmHistoryDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.common.TokenUrls;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangsiyuan
 * @date 2022/4/1 10:27 上午
 */
@RestController
@RequestMapping("/webapi/alarmRule")
@TokenUrls("/webapi/alarmRule/query")
public class AlarmRuleFacadeImpl extends BaseFacade {

    @Autowired
    private AlertRuleService      alarmRuleService;

    @Autowired
    private AlertSubscribeService alarmSubscribeService;

    @Autowired
    private AlertGroupService     alarmGroupService;

    @Autowired
    private AlarmHistoryService   alarmHistoryService;

    @Autowired
    private UserOpLogService      userOpLogService;

    @PostMapping("/create")
    @ResponseBody
    @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
    public JsonResult<Long> save(@RequestBody AlarmRuleDTO alarmRuleDTO) {
        final JsonResult<Long> result = new JsonResult<>();
        facadeTemplate.manage(result, new ManageCallback() {
            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotBlank(alarmRuleDTO.getRuleName(), "ruleName");
                ParaCheckUtil.checkParaNotBlank(alarmRuleDTO.getAlarmLevel(), "alarmLevel");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getRule(), "rule");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getTimeFilter(), "timeFilter");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getStatus(), "status");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getRecover(), "recover");
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getIsMerge(), "isMerge");
            }

            @Override
            public void doManage() {
                MonitorScope ms = RequestContext.getContext().ms;
                MonitorUser mu = RequestContext.getContext().mu;
                if (null != mu) {
                    alarmRuleDTO.setCreator(mu.getLoginName());
                }
                if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
                    alarmRuleDTO.setTenant(ms.tenant);
                }
                alarmRuleDTO.setGmtCreate(new Date());
                alarmRuleDTO.setGmtModified(new Date());
                Long id = alarmRuleService.save(alarmRuleDTO);

                userOpLogService.append("alarm_rule", String.valueOf(id), OpType.CREATE,
                    mu.getLoginName(), ms.getTenant(), J.toJson(alarmRuleDTO), null, null,
                    "alarm_rule_create");
                JsonResult.createSuccessResult(result, id);
            }
        });

        return result;
    }

    @PostMapping("/update")
    @ResponseBody
    @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
    public JsonResult<Boolean> update(@RequestBody AlarmRuleDTO alarmRuleDTO) {
        final JsonResult<Boolean> result = new JsonResult<>();
        facadeTemplate.manage(result, new ManageCallback() {
            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getId(), "id");

                ParaCheckUtil.checkParaNotNull(alarmRuleDTO.getTenant(), "tenant");
                ParaCheckUtil.checkEquals(alarmRuleDTO.getTenant(),
                    RequestContext.getContext().ms.getTenant(), "tenant is illegal");

            }

            @Override
            public void doManage() {

                AlarmRuleDTO item = alarmRuleService.queryById(alarmRuleDTO.getId(),
                    RequestContext.getContext().ms.getTenant());

                if (null == item) {
                    throw new MonitorException("cannot find record: " + alarmRuleDTO.getId());
                }
                if (!item.getTenant().equalsIgnoreCase(alarmRuleDTO.getTenant())) {
                    throw new MonitorException("the tenant parameter is invalid");
                }

                MonitorUser mu = RequestContext.getContext().mu;
                if (null != mu) {
                    alarmRuleDTO.setModifier(mu.getLoginName());
                }
                alarmRuleDTO.setGmtModified(new Date());
                boolean save = alarmRuleService.updateById(alarmRuleDTO);

                userOpLogService.append("alarm_rule", String.valueOf(alarmRuleDTO.getId()),
                    OpType.UPDATE, RequestContext.getContext().mu.getLoginName(),
                    RequestContext.getContext().ms.getTenant(), J.toJson(item),
                    J.toJson(alarmRuleDTO), null, "alarm_rule_update");

                JsonResult.createSuccessResult(result, save);
            }
        });

        return result;
    }

    @GetMapping("/query/{id}")
    @ResponseBody
    @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
    public JsonResult<AlarmRuleDTO> queryById(@PathVariable("id") Long id) {
        final JsonResult<AlarmRuleDTO> result = new JsonResult<>();
        facadeTemplate.manage(result, new ManageCallback() {
            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotNull(id, "id");
            }

            @Override
            public void doManage() {

                AlarmRuleDTO save = alarmRuleService.queryById(id,
                    RequestContext.getContext().ms.getTenant());
                JsonResult.createSuccessResult(result, save);
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
                boolean rtn = false;
                AlarmRuleDTO alarmRule = alarmRuleService.queryById(id,
                    RequestContext.getContext().ms.getTenant());
                if (alarmRule != null) {
                    rtn = alarmRuleService.removeById(id);
                }

                userOpLogService.append("alarm_rule", String.valueOf(id), OpType.DELETE,
                    RequestContext.getContext().mu.getLoginName(),
                    RequestContext.getContext().ms.getTenant(), J.toJson(alarmRule), null, null,
                    "alarm_rule_delete");

                JsonResult.createSuccessResult(result, rtn);
            }
        });
        return result;
    }

    @PostMapping("/pageQuery")
    @ResponseBody
    @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
    public JsonResult<MonitorPageResult<AlarmRuleDTO>> pageQuery(@RequestBody MonitorPageRequest<AlarmRuleDTO> pageRequest) {
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
                JsonResult.createSuccessResult(result, alarmRuleService.getListByPage(pageRequest));
            }
        });

        return result;
    }

    @GetMapping(value = "/querySubscribeList")
    @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
    public JsonResult<List<AlarmRuleDTO>> querySubscribeList() {
        final JsonResult<List<AlarmRuleDTO>> result = new JsonResult<>();
        facadeTemplate.manage(result, new ManageCallback() {
            @Override
            public void checkParameter() {
            }

            @Override
            public void doManage() {
                List<AlarmRuleDTO> alarmRuleDTOS = new ArrayList<>();

                List<AlarmRuleDTO> byIds = getRuleListBySubscribe();
                if (!CollectionUtils.isEmpty(byIds)) {
                    alarmRuleDTOS.addAll(byIds);
                }

                List<AlarmRuleDTO> byGroups = getRuleListByGroup();
                if (!CollectionUtils.isEmpty(byGroups)) {
                    alarmRuleDTOS.addAll(byGroups);
                }

                JsonResult.createSuccessResult(result, alarmRuleDTOS);

            }
        });
        return result;
    }

    @PostMapping(value = "/querySubAlarmHistory")
    @ResponseBody
    @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
    public JsonResult<MonitorPageResult<AlarmHistoryDTO>> querySubAlarmHistory(@RequestBody MonitorPageRequest<AlarmHistoryDTO> pageRequest) {
        final JsonResult<MonitorPageResult<AlarmHistoryDTO>> result = new JsonResult<>();
        facadeTemplate.manage(result, new ManageCallback() {
            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotNull(pageRequest.getTarget(), "target");
            }

            @Override
            public void doManage() {
                List<AlarmRuleDTO> alarmRuleDTOS = new ArrayList<>();

                List<AlarmRuleDTO> byIds = getRuleListBySubscribe();
                if (!CollectionUtils.isEmpty(byIds)) {
                    alarmRuleDTOS.addAll(byIds);
                }

                List<AlarmRuleDTO> byGroups = getRuleListByGroup();
                if (!CollectionUtils.isEmpty(byGroups)) {
                    alarmRuleDTOS.addAll(byGroups);
                }

                if (CollectionUtils.isEmpty(alarmRuleDTOS))
                    return;

                MonitorScope ms = RequestContext.getContext().ms;
                if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
                    pageRequest.getTarget().setTenant(ms.tenant);
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

    private List<AlarmRuleDTO> getRuleListByGroup() {
        List<AlarmGroupDTO> listByUserLike = alarmGroupService.getListByUserLike(
            RequestContext.getContext().mu.getUserId(), MonitorCookieUtil.getTenantOrException());

        if (CollectionUtils.isEmpty(listByUserLike))
            return null;

        List<AlarmRuleDTO> alarmRuleDTOS = new ArrayList<>();
        for (AlarmGroupDTO alarmGroupDTO : listByUserLike) {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("group_id", alarmGroupDTO.getId());
            conditions.put("tenant", MonitorCookieUtil.getTenantOrException());

            List<AlarmSubscribeInfo> alarmSubscribeInfos = alarmSubscribeService
                .queryByMap(conditions);

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

            List<AlarmRuleDTO> byIds = alarmRuleService.findByIds(ruleIds);
            if (CollectionUtils.isEmpty(byIds)) {
                continue;
            }
            alarmRuleDTOS.addAll(byIds);
        }

        return alarmRuleDTOS;

    }

    private List<AlarmRuleDTO> getRuleListBySubscribe() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("subscriber", RequestContext.getContext().mu.getUserId());
        conditions.put("tenant", MonitorCookieUtil.getTenantOrException());

        List<AlarmSubscribeInfo> alarmSubscribeInfos = alarmSubscribeService.queryByMap(conditions);

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

        return alarmRuleService.findByIds(ruleIds);
    }

}
