/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.alert.service.AlertService;
import io.holoinsight.server.home.biz.service.AlertWebhookService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.service.query.WebhookResponse;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.common.SSRFUtils;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.AlarmWebhook;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.AlarmWebhookDTO;
import io.holoinsight.server.home.dal.model.dto.AlarmWebhookTestDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsiyuan
 * @date 2022/4/1 10:27 上午
 */
@Slf4j
@RestController
@RequestMapping("/webapi/alarmWebhook")
public class AlarmWebhookFacadeImpl extends BaseFacade {

  @Autowired
  private AlertWebhookService alarmWebhookService;

  @Autowired
  private AlertService alertService;

  @Autowired
  private UserOpLogService userOpLogService;

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Long> save(@RequestBody AlarmWebhookDTO alarmWebhookDTO) {
    final JsonResult<Long> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(alarmWebhookDTO.getStatus(), "status");
        ParaCheckUtil.checkParaNotBlank(alarmWebhookDTO.getRequestUrl(), "url");
        ParaCheckUtil.checkParaNotBlank(alarmWebhookDTO.getRequestType(), "requestType");
        ParaCheckUtil.checkParaNotBlank(alarmWebhookDTO.getRequestBody(), "requestBody");
        ParaCheckUtil.checkParaNotBlank(alarmWebhookDTO.getWebhookName(), "name");
        ParaCheckUtil.checkParaNotBlank(alarmWebhookDTO.getWebhookTest(), "webhookDebugBody");
        ParaCheckUtil.checkParaId(alarmWebhookDTO.getId());
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          alarmWebhookDTO.setCreator(mu.getLoginName());
        }
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          alarmWebhookDTO.setTenant(ms.tenant);
        }
        alarmWebhookDTO.setGmtCreate(new Date());
        alarmWebhookDTO.setGmtModified(new Date());

        JsonResult<WebhookResponse> test = test(transformTestDTO(alarmWebhookDTO));
        if (!test.isSuccess()) {
          throw new MonitorException(
              "Debugging failed. Please check whether the input parameters are correct");
        }

        Long id = alarmWebhookService.save(alarmWebhookDTO).getId();

        userOpLogService.append("alarm_webhook", id, OpType.CREATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(alarmWebhookDTO), null, null,
            "alarm_webhook_create");

        JsonResult.createSuccessResult(result, id);
      }
    });

    return result;
  }

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> update(@RequestBody AlarmWebhookDTO alarmWebhookDTO) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(alarmWebhookDTO.getId(), "id");
        ParaCheckUtil.checkParaNotNull(alarmWebhookDTO.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(alarmWebhookDTO.getTenant(),
            RequestContext.getContext().ms.getTenant(), "tenant is illegal");

        ParaCheckUtil.checkParaNotBlank(alarmWebhookDTO.getRequestUrl(), "url");
        ParaCheckUtil.checkParaNotBlank(alarmWebhookDTO.getRequestType(), "requestType");
        ParaCheckUtil.checkParaNotBlank(alarmWebhookDTO.getRequestBody(), "requestBody");
        ParaCheckUtil.checkParaNotBlank(alarmWebhookDTO.getWebhookName(), "name");
        ParaCheckUtil.checkParaNotBlank(alarmWebhookDTO.getWebhookTest(), "webhookDebugBody");

      }

      @Override
      public void doManage() {

        AlarmWebhookDTO item = alarmWebhookService.queryById(alarmWebhookDTO.getId(),
            RequestContext.getContext().ms.getTenant());

        if (null == item) {
          throw new MonitorException("cannot find record: " + alarmWebhookDTO.getId());
        }
        if (!item.getTenant().equalsIgnoreCase(alarmWebhookDTO.getTenant())) {
          throw new MonitorException("the tenant parameter is invalid");
        }

        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          alarmWebhookDTO.setModifier(mu.getLoginName());
        }
        alarmWebhookDTO.setGmtModified(new Date());

        JsonResult<WebhookResponse> test = test(transformTestDTO(alarmWebhookDTO));
        if (!test.isSuccess()) {
          throw new MonitorException(
              "Debugging failed. Please check whether the input parameters are correct");
        }

        boolean save = alarmWebhookService.updateById(alarmWebhookDTO);
        userOpLogService.append("alarm_webhook", alarmWebhookDTO.getId(), OpType.UPDATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(item),
            J.toJson(alarmWebhookDTO), null, "alarm_webhook_update");

        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @GetMapping("/query/{id}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlarmWebhookDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<AlarmWebhookDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {

        AlarmWebhookDTO save =
            alarmWebhookService.queryById(id, RequestContext.getContext().ms.getTenant());
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
        MonitorScope ms = RequestContext.getContext().ms;
        boolean rtn = false;
        AlarmWebhookDTO alarmWebhook = alarmWebhookService.queryById(id, ms.getTenant());
        if (alarmWebhook != null) {
          rtn = alarmWebhookService.removeById(id);
          userOpLogService.append("alarm_webhook", alarmWebhook.getId(), OpType.DELETE,
              RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
              J.toJson(alarmWebhook), null, null, "alarm_webhook_delete");

        }
        JsonResult.createSuccessResult(result, rtn);
      }
    });
    return result;
  }

  @DeleteMapping(value = "/deleteByName/{name}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> deleteByName(@PathVariable("name") String name) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(name, "name");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("name", name);
        columnMap.put("tenant", ms.getTenant());
        List<AlarmWebhook> apiKeys = alarmWebhookService.listByMap(columnMap);
        if (CollectionUtils.isEmpty(apiKeys)) {
          return;
        }

        List<Long> ids = apiKeys.stream().map(AlarmWebhook::getId).collect(Collectors.toList());

        alarmWebhookService.removeBatchByIds(ids);
        JsonResult.createSuccessResult(result, null);
        if (!CollectionUtils.isEmpty(ids)) {
          userOpLogService.append("alarmWebhook", ids.get(0), OpType.DELETE,
              RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
              J.toJson(apiKeys), null, null, "alarmWebhook_delete");
        }

      }
    });
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlarmWebhookDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlarmWebhookDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlarmWebhookDTO>> result = new JsonResult<>();
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
        JsonResult.createSuccessResult(result, alarmWebhookService.getListByPage(pageRequest));
      }
    });

    return result;
  }

  @PostMapping("/test")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<WebhookResponse> test(@RequestBody AlarmWebhookTestDTO alarmWebhookDTO) {
    final JsonResult<WebhookResponse> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(alarmWebhookDTO, "alarmWebhookDTO");
        ParaCheckUtil.checkParaNotNull(alarmWebhookDTO.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(alarmWebhookDTO.getTenant(),
            RequestContext.getContext().ms.getTenant(), "tenant is illegal");
      }

      @Override
      public void doManage() {

        try {
          Boolean aBoolean = SSRFUtils.hookCheckUrl(alarmWebhookDTO.getRequestUrl());
          if (!aBoolean) {
            log.error("alarm webhook url check failed");
            JsonResult.fillFailResultTo(result, "alarm webhook url check failed");
            return;
          }
          SSRFUtils.hookStart();
          WebhookResponse response = alertService.webhookTest(alarmWebhookDTO);
          if (response.getCode() == 200) {
            JsonResult.createSuccessResult(result, response);
          } else {
            JsonResult.fillFailResultTo(result, J.toJson(response));
          }
        } catch (Throwable t) {
          log.error("alarm webhook test error", t);
          JsonResult.fillFailResultTo(result, t.getMessage());
        } finally {
          SSRFUtils.hookStop();
        }
      }
    });

    return result;
  }

  private AlarmWebhookTestDTO transformTestDTO(AlarmWebhookDTO alarmWebhookDTO) {
    AlarmWebhookTestDTO alarmWebhookTestDTO = new AlarmWebhookTestDTO();

    alarmWebhookTestDTO.setRequestType(alarmWebhookDTO.getRequestType());
    alarmWebhookTestDTO.setRequestUrl(alarmWebhookDTO.getRequestUrl());
    alarmWebhookTestDTO.setTenant(alarmWebhookDTO.getTenant());
    alarmWebhookTestDTO.setRequestHeaders(alarmWebhookDTO.getRequestHeaders());
    alarmWebhookTestDTO.setRequestBody(alarmWebhookDTO.getRequestBody());
    alarmWebhookTestDTO.setTestBody(alarmWebhookDTO.getWebhookTest());
    alarmWebhookTestDTO.setExtra(alarmWebhookDTO.getExtra());

    return alarmWebhookTestDTO;
  }

}
