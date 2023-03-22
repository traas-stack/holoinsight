/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.migrate.MigrateService;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.dal.model.Dashboard;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.controller.model.MigrateAlarmRequest;
import io.holoinsight.server.home.web.controller.model.MigrateCustomPluginRequest;
import io.holoinsight.server.home.web.controller.model.MigrateDashBoardRequest;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zanghaibo
 * @time 2022-11-17 4:57 下午
 */
@RestController
@RequestMapping("/webapi/migrate")
@Slf4j
public class MigrateFacadeImpl extends BaseFacade {

  @Autowired
  private MigrateService migrateService;


  public static final Integer MAX_COPY_FOLDER_DEEP = 8;

  /**
   * 迁移自定义日志监控配置到新租户
   * 
   * @param migrateCustomPluginRequest
   * @return
   */
  @PostMapping("/plugin/copy_to_tenant")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<CustomPluginDTO> copyToTenant(
      @RequestBody MigrateCustomPluginRequest migrateCustomPluginRequest) {
    JsonResult<CustomPluginDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {

      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(migrateCustomPluginRequest,
            "source metric description is null");
        ParaCheckUtil.checkParaNotNull(migrateCustomPluginRequest.getPluginId(),
            "source metric id is null");
        ParaCheckUtil.checkParaNotBlank(migrateCustomPluginRequest.getTargetTenant(),
            "migrate target tenant is null");
        ParaCheckUtil.checkParaNotBlank(migrateCustomPluginRequest.getSourceTenant(),
            "migrate source tenant is null");
        ParaCheckUtil.checkParaBoolean(
            !migrateCustomPluginRequest.getTargetTenant()
                .equalsIgnoreCase(migrateCustomPluginRequest.getSourceTenant()),
            "duplicate tenant");
      }

      @Override
      public void doManage() {
        CustomPluginDTO customPluginDTO = migrateService.migrateCustomPlugin(
            migrateCustomPluginRequest.getPluginId(), migrateCustomPluginRequest.getSourceTenant(),
            migrateCustomPluginRequest.getTargetTenant(),
            migrateCustomPluginRequest.getTargetWorkspace());
        if (customPluginDTO != null) {
          JsonResult.createSuccessResult(result, customPluginDTO);
        }
      }
    });

    return result;
  }

  /**
   * 迁移大盘到新租户
   * 
   * @param migrateDashBoardRequest
   * @return
   */
  @PostMapping("/dashboard/copy_to_tenant")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Dashboard> copyToTenant(
      @RequestBody MigrateDashBoardRequest migrateDashBoardRequest) {
    JsonResult<Dashboard> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {

      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(migrateDashBoardRequest, "source dashboard id is null");
        ParaCheckUtil.checkParaNotNull(migrateDashBoardRequest.getSourceTenant(),
            "source dashboard tenant is null");
        ParaCheckUtil.checkParaNotNull(migrateDashBoardRequest.getTargetTenant(),
            "target dashboard tenant is null");
      }

      @Override
      public void doManage() {
        Dashboard dashboard = migrateService.migrateDashboard(
            migrateDashBoardRequest.getDashboardId(), migrateDashBoardRequest.getSourceTenant(),
            migrateDashBoardRequest.getTargetTenant(),
            migrateDashBoardRequest.getTargetWorkspace());
        JsonResult.createSuccessResult(result, dashboard);
      }
    });
    return result;
  }

  /**
   * 迁移告警到新租户下
   * 
   * @param migrateAlarmRequest
   * @return
   */
  @PostMapping("/rule/copy_to_tenant")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<AlarmRuleDTO> copyToTenant(
      @RequestBody MigrateAlarmRequest migrateAlarmRequest) {
    JsonResult<AlarmRuleDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {

      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(migrateAlarmRequest, "source alarm id is null");
        ParaCheckUtil.checkParaNotNull(migrateAlarmRequest.getSourceTenant(),
            "source dashboard tenant is null");
        ParaCheckUtil.checkParaNotNull(migrateAlarmRequest.getTargetTenant(),
            "target dashboard tenant is null");
      }

      @Override
      public void doManage() {
        AlarmRuleDTO alarmRuleDTO = migrateService.migrateAlarmRule(
            migrateAlarmRequest.getAlarmRuleId(), migrateAlarmRequest.getSourceTenant(),
            migrateAlarmRequest.getTargetTenant(), migrateAlarmRequest.getTargetWorkspace());
        if (alarmRuleDTO != null) {
          JsonResult.createSuccessResult(result, alarmRuleDTO);
        }
      }
    });
    return result;
  }

}
