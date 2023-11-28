/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.AlertmanagerWebhookService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.converter.AlertmanagerWebhookConverter;
import io.holoinsight.server.home.dal.model.AlertmanagerWebhook;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.AlertmanagerWebhookDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/webapi/alertmanager/webhook")
public class AlertmanagerWebhookImpl extends BaseFacade {

  @Autowired
  private AlertmanagerWebhookService alertmanagerWebhookService;

  @Autowired
  private AlertmanagerWebhookConverter alertmanagerWebhookConverter;

  @Autowired
  private UserOpLogService userOpLogService;

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlertmanagerWebhook>> pageQuery(
      @RequestBody MonitorPageRequest<AlertmanagerWebhook> request) {
    final JsonResult<MonitorPageResult<AlertmanagerWebhook>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request.getTarget(), "target");
      }

      @Override
      public void doManage() {
        request.getTarget().setTenant(RequestContext.getContext().ms.getTenant());
        JsonResult.createSuccessResult(result, alertmanagerWebhookService.getListByPage(request));
      }
    });

    return result;
  }


  @GetMapping("/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Object> get(@PathVariable("id") Long id) {
    MonitorScope ms = RequestContext.getContext().ms;

    AlertmanagerWebhook model =
        alertmanagerWebhookService.queryById(id, RequestContext.getContext().ms.getTenant());
    if (model == null || !model.getTenant().equals(ms.getTenant())) {
      return JsonResult.createFailResult("can not find the record");
    }

    return JsonResult.createSuccessResult(alertmanagerWebhookConverter.doToDTO(model));
  }

  @PostMapping
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> create(@RequestBody AlertmanagerWebhookDTO alertmanagerWebhookDTO) {
    MonitorScope ms = RequestContext.getContext().ms;
    MonitorUser mu = RequestContext.getContext().mu;
    AlertmanagerWebhook alertmanagerWebhook =
        alertmanagerWebhookConverter.dtoToDO(alertmanagerWebhookDTO);
    alertmanagerWebhook.setTenant(ms.getTenant());
    alertmanagerWebhook.setModifier(mu.getLoginName());
    alertmanagerWebhook.setCreator(mu.getLoginName());
    alertmanagerWebhook.setGmtCreate(new Date());
    alertmanagerWebhook.setGmtModified(new Date());
    alertmanagerWebhookService.save(alertmanagerWebhook);

    alertmanagerWebhookDTO = alertmanagerWebhookConverter.doToDTO(alertmanagerWebhook);

    userOpLogService.append("alertmanager_webhook", alertmanagerWebhookDTO.getId(), OpType.CREATE,
        mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(alertmanagerWebhookDTO),
        null, null, "alertmanager_webhook_create");

    return JsonResult.createSuccessResult(alertmanagerWebhookDTO);
  }

  @PostMapping("/update/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<AlertmanagerWebhookDTO> update(@PathVariable("id") Long id,
      @RequestBody AlertmanagerWebhookDTO alertmanagerWebhookDTO) {

    final JsonResult<AlertmanagerWebhookDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
        ParaCheckUtil.checkParaNotNull(alertmanagerWebhookDTO.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(alertmanagerWebhookDTO.getTenant(),
            RequestContext.getContext().ms.getTenant(), "tenant is illegal");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;

        AlertmanagerWebhook model =
            alertmanagerWebhookService.queryById(id, RequestContext.getContext().ms.getTenant());
        if (model == null || !model.getTenant().equals(ms.getTenant())) {
          JsonResult.createFailResult("can not find the record");
          return;
        }

        alertmanagerWebhookDTO.setModifier(RequestContext.getContext().mu.getLoginName());
        alertmanagerWebhookDTO.setGmtModified(new Date());
        alertmanagerWebhookDTO.setId(model.getId());
        model = alertmanagerWebhookConverter.dtoToDO(alertmanagerWebhookDTO);
        alertmanagerWebhookService.updateById(model);

        userOpLogService.append("alertmanager_webhook", id, OpType.UPDATE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(model), J.toJson(alertmanagerWebhookDTO), null, "alertmanager_webhook_update");

        JsonResult.createSuccessResult(result, alertmanagerWebhookDTO);
      }
    });

    return result;

  }

  @PostMapping("/delete/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> delete(@PathVariable("id") Long id) {
    MonitorScope ms = RequestContext.getContext().ms;

    AlertmanagerWebhook model = alertmanagerWebhookService.queryById(id, ms.getTenant());
    if (model == null || !model.getTenant().equals(ms.getTenant())) {
      return JsonResult.createFailResult("can not find the record");
    }

    alertmanagerWebhookService.removeById(id);
    userOpLogService.append("alertmanager_webhook", id, OpType.DELETE,
        RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
        J.toJson(id), null, null, "alertmanager_webhook_delete");

    return JsonResult.createSuccessResult(model);
  }

}
