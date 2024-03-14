/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.DashboardService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.Dashboard;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.DashboardType;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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

import java.util.Date;

@RestController
@RequestMapping("/webapi/v1/dashboard")
public class DashboardFacadeImpl extends BaseFacade {

  @Autowired
  private DashboardService dashboardService;

  @Autowired
  private UserOpLogService userOpLogService;

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<Dashboard>> pageQuery(
      @RequestBody MonitorPageRequest<Dashboard> request) {
    final JsonResult<MonitorPageResult<Dashboard>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request.getTarget(), "target");
      }

      @Override
      public void doManage() {
        request.getTarget().setTenant(RequestContext.getContext().ms.getTenant());
        request.getTarget().setWorkspace(RequestContext.getContext().ms.getWorkspace());
        JsonResult.createSuccessResult(result, dashboardService.getListByPage(request));
      }
    });

    return result;
  }

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Dashboard> update(@RequestBody Dashboard request) {
    final JsonResult<Dashboard> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        MonitorScope ms = RequestContext.getContext().ms;
        ParaCheckUtil.checkParaNotNull(request.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(request.getTenant(), ms.getTenant(), "tenant is illegal");

        ParaCheckUtil.checkParaNotNull(request.getId(), "id");

        Dashboard item =
            dashboardService.queryById(request.getId(), ms.getTenant(), ms.getWorkspace());

        if (null == item) {
          throw new MonitorException("cannot find record: " + request.getId());
        }
        if (!item.getTenant().equalsIgnoreCase(request.getTenant())) {
          throw new MonitorException("the tenant parameter is invalid");
        }
      }

      @Override
      public void doManage() {

        Dashboard update = new Dashboard();

        BeanUtils.copyProperties(request, update);


        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          update.setModifier(mu.getLoginName());
        }
        if (null != ms && !StringUtil.isBlank(ms.tenant)) {
          update.setTenant(ms.tenant);
        }
        if (null != ms && !StringUtil.isBlank(ms.workspace)) {
          update.setWorkspace(ms.workspace);
        }
        dashboardService.updateById(update);
        JsonResult.createSuccessResult(result, update);

        assert mu != null;
        userOpLogService.append("dashboard", update.getId(), OpType.UPDATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(request), J.toJson(update), null,
            "dashboard_update");

      }
    });

    return result;
  }

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Dashboard> create(@RequestBody Dashboard request) {
    final JsonResult<Dashboard> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaId(request.getId());
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          request.setCreator(mu.getLoginName());
          request.setModifier(mu.getLoginName());
        }
        if (null != ms && !StringUtil.isBlank(ms.tenant)) {
          request.setTenant(ms.tenant);
        }
        if (null != ms && !StringUtil.isBlank(ms.workspace)) {
          request.setWorkspace(ms.workspace);
        }
        request.setGmtModified(new Date());
        request.setGmtCreate(new Date());

        dashboardService.save(request);
        JsonResult.createSuccessResult(result, request);

        assert mu != null;
        userOpLogService.append("dashboard", request.getId(), OpType.CREATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(request), null, null, "dashboard_create");
      }
    });

    return result;
  }

  @GetMapping(value = "/query/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Dashboard> queryById(@PathVariable("id") Long id) {
    final JsonResult<Dashboard> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Dashboard customPluginDTO =
            dashboardService.queryById(id, ms.getTenant(), ms.getWorkspace());

        if (null == customPluginDTO) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, customPluginDTO);
      }
    });
    return result;
  }

  @DeleteMapping("/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> deleteById(@PathVariable Long id) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {

      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Dashboard dashboard = dashboardService.queryById(id, ms.getTenant(), ms.getWorkspace());
        if (null == dashboard) {
          return;
        }

        boolean b = dashboardService.removeById(id);
        userOpLogService.append("dashboard", id, OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(id), null, null, "dashboard_delete");

        JsonResult.createSuccessResult(result, b);
      }
    });

    return result;
  }

  @GetMapping(value = "/queryByType/{type}/{title}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Dashboard> queryByType(@PathVariable("type") String type,
      @PathVariable("title") String title) {
    final JsonResult<Dashboard> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(type, "type");
        ParaCheckUtil.checkParaNotNull(title, "title");
      }

      @Override
      public void doManage() {

        MonitorPageRequest<Dashboard> request = new MonitorPageRequest<>();
        Dashboard target = new Dashboard();
        DashboardType dashboardType = DashboardType.valueOf(type);
        switch (dashboardType) {
          case faas:
          case iot:
          case miniapp:
            target.setType(dashboardType.code());
            if (StringUtils.equals(title, "biz")) {
              target
                  .setTitle(String.join("_", title, RequestContext.getContext().ms.getWorkspace()));
            } else {
              target.setTitle(title);
            }
            break;
          default:
            result.setSuccess(false);
            result.setMessage("unsupported type " + dashboardType.code());
            return;
        }

        target.setTenant("-1");
        target.setWorkspace("-1");
        request.setTarget(target);
        Dashboard resultDashboard = null;
        MonitorPageResult<Dashboard> pageResult = dashboardService.getListByPage(request);
        if (pageResult != null && !CollectionUtils.isEmpty(pageResult.getItems())) {
          resultDashboard = pageResult.getItems().get(0);
        }
        JsonResult.createSuccessResult(result, resultDashboard);
      }
    });
    return result;
  }

}
