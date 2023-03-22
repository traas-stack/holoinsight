/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.migrate;

import io.holoinsight.server.home.biz.service.*;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.dal.model.Dashboard;
import io.holoinsight.server.home.dal.model.Folder;
import io.holoinsight.server.home.dal.model.dto.*;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zanghaibo
 * @time 2022-11-20 8:42 下午
 */

@Service
public class MigrateService {

  @Autowired
  private CustomPluginService customPluginService;

  @Autowired
  private DashboardService dashboardService;

  @Autowired
  private AlertRuleService alarmRuleService;

  @Autowired
  private AlertSubscribeService alarmSubscribeService;

  @Autowired
  private AlertGroupService alarmGroupService;

  @Autowired
  private AlertDingDingRobotService alarmDingDingRobotService;

  @Autowired
  private AlertWebhookService alarmWebhookService;

  @Autowired
  private FolderService folderService;

  public static final Integer MAX_COPY_FOLDER_DEEP = 8;

  /**
   * 迁移日志监控配置以及关联日志目录，并且默认支持目录最大深度
   * 
   * @param id
   * @param sourceTenant
   * @param targetTenant
   * @return
   */
  public CustomPluginDTO migrateCustomPlugin(Long id, String sourceTenant, String targetTenant,
      String targetWorkspace) {
    CustomPluginDTO customPluginDTO = customPluginService.queryById(id, sourceTenant, null);
    customPluginDTO.setId(null);
    customPluginDTO.setTenant(targetTenant);
    if (StringUtils.isNotBlank(targetWorkspace)) {
      customPluginDTO.setWorkspace(targetWorkspace);
    }
    CustomPluginDTO targetCustomPluginDTO = customPluginService.create(customPluginDTO);

    int currentDeep = 0;
    Folder lastFolder = null;
    Long parentFolderId = 0L;

    while (currentDeep < MAX_COPY_FOLDER_DEEP) {

      if (currentDeep == 0) {
        parentFolderId = customPluginDTO.getParentFolderId();
      }

      if (parentFolderId == -1) {
        break;
      }

      Folder folder = folderService.queryById(parentFolderId, sourceTenant, null);
      if (folder == null) {
        throw new MonitorException("invalid folder status [folderId: " + parentFolderId + "]"
            + "[tenant: " + sourceTenant + "]");
      }
      folder.setTenant(targetTenant);
      if (StringUtils.isNotBlank(targetWorkspace)) {
        folder.setWorkspace(targetWorkspace);
      }
      folder.setId(null);
      Long saveId = folderService.create(folder);
      folder.setId(saveId);
      if (currentDeep == 0) {
        targetCustomPluginDTO.setParentFolderId(folder.getId());
        customPluginService.updateById(targetCustomPluginDTO);
        lastFolder = folder;
      } else {
        lastFolder.setParentFolderId(folder.getId());
        // update
        folderService.updateById(lastFolder);
      }
      parentFolderId = folder.getParentFolderId();
      currentDeep++;
    }

    if (currentDeep < MAX_COPY_FOLDER_DEEP) {
      return targetCustomPluginDTO;
    }

    return null;

  }

  /**
   * 迁移仪表盘
   * 
   * @param id
   * @param sourceTenant
   * @param targetTenant
   * @return
   */
  public Dashboard migrateDashboard(Long id, String sourceTenant, String targetTenant,
      String targetWorkspace) {
    Dashboard dashboard = dashboardService.queryById(id, sourceTenant, null);
    dashboard.setId(null);
    dashboard.setTenant(targetTenant);
    if (StringUtils.isNotBlank(targetWorkspace)) {
      dashboard.setWorkspace(targetWorkspace);
    }
    dashboard.setGmtModified(new Date());
    dashboard.setGmtCreate(new Date());
    Long saveId = dashboardService.create(dashboard);
    dashboard.setId(saveId);
    return dashboard;
  }

  /**
   * 迁移告警规则以及告警相关通道
   * 
   * @param id
   * @param sourceTenant
   * @param targetTenant
   * @return
   */
  public AlarmRuleDTO migrateAlarmRule(Long id, String sourceTenant, String targetTenant,
      String targetWorkspace) {
    AlarmRuleDTO alarmRuleDTO = alarmRuleService.queryById(id, sourceTenant, null);
    alarmRuleDTO.setTenant(targetTenant);
    if (StringUtils.isNotBlank(targetWorkspace)) {
      alarmRuleDTO.setWorkspace(targetWorkspace);
    }
    alarmRuleDTO.setId(null);
    alarmRuleDTO.setGmtModified(new Date());
    alarmRuleDTO.setGmtCreate(new Date());
    Long alarmRuleSaveId = alarmRuleService.save(alarmRuleDTO);
    alarmRuleDTO.setId(alarmRuleSaveId);
    batchMigrateAlarmSubscribes("rule_" + id, sourceTenant, targetTenant, targetWorkspace);
    return alarmRuleDTO;
  }

  /**
   * 批量迁移告警订阅
   * 
   * @param uniqueId
   * @param sourceTenant
   * @param targetTenant
   */
  public void batchMigrateAlarmSubscribes(String uniqueId, String sourceTenant, String targetTenant,
      String targetWorkspace) {
    Map<String, Object> params = new HashMap<>();
    params.put("unique_id", uniqueId);
    if (StringUtils.isNotBlank(targetWorkspace)) {
      params.put("workspace", targetWorkspace);
    }
    AlarmSubscribeDTO alarmSubscribeDTO = alarmSubscribeService.queryByUniqueId(params);
    for (AlarmSubscribeInfo info : alarmSubscribeDTO.getAlarmSubscribe()) {
      info.setTenant(targetTenant);
      if (StringUtils.isNotBlank(targetWorkspace)) {
        info.setWorkspace(targetWorkspace);
      }
      info.setUniqueId("rule_" + uniqueId);
      info.setId(null);
      Long alarmSubscribeSaveId = alarmSubscribeService.save(info);
      info.setId(alarmSubscribeSaveId);
      List<String> types = info.getNoticeType();
      for (String type : types) {
        if (type.equals("dingDing")) {
          Long alarmGroupId = info.getGroupId();
          AlarmGroupDTO alarmGroupDTO = migrateAlarmGroup(alarmGroupId, sourceTenant, targetTenant);
          info.setGroupId(alarmGroupDTO.getId());
          alarmSubscribeService.updateById(info);
        }
        if (type.equals("dingDingRobot")) {
          Long dingDingRobotId = Long.valueOf(info.getSubscriber());
          AlarmDingDingRobotDTO alarmDingDingRobotDTO =
              migrateDingDingRobot(dingDingRobotId, sourceTenant, targetTenant);
          info.setSubscriber(String.valueOf(alarmDingDingRobotDTO.getId()));
          alarmSubscribeService.updateById(info);
        }
        if (type.equals("webhook")) {
          Long webhookId = info.getGroupId();
          AlarmWebhookDTO alarmWebhookDTO =
              migrateAlarmWebhook(webhookId, sourceTenant, targetTenant);
          info.setGroupId(alarmWebhookDTO.getId());
          alarmSubscribeService.updateById(info);
        }
      }
    }
  }

  /**
   * 迁移告警通道:告警组
   * 
   * @param id
   * @param sourceTenant
   * @param targetTenant
   * @return
   */
  public AlarmGroupDTO migrateAlarmGroup(Long id, String sourceTenant, String targetTenant) {
    AlarmGroupDTO alarmGroupDTO = alarmGroupService.queryById(id, sourceTenant);
    alarmGroupDTO.setId(null);
    alarmGroupDTO.setTenant(targetTenant);
    alarmGroupDTO.setGmtModified(new Date());
    alarmGroupDTO.setGmtCreate(new Date());
    Long saveId = alarmGroupService.save(alarmGroupDTO);
    alarmGroupDTO.setId(saveId);
    return alarmGroupDTO;
  }

  /**
   * 迁移告警通道:webhook
   * 
   * @param id
   * @param sourceTenant
   * @param targetTenant
   * @return
   */
  public AlarmWebhookDTO migrateAlarmWebhook(Long id, String sourceTenant, String targetTenant) {
    AlarmWebhookDTO alarmWebhookDTO = alarmWebhookService.queryById(id, sourceTenant);
    alarmWebhookDTO.setId(null);
    alarmWebhookDTO.setTenant(targetTenant);
    alarmWebhookDTO.setGmtModified(new Date());
    alarmWebhookDTO.setGmtCreate(new Date());
    AlarmWebhookDTO savedWebhook = alarmWebhookService.save(alarmWebhookDTO);
    return savedWebhook;
  }

  /**
   * 迁移告警通道：钉钉机器人
   * 
   * @param id
   * @param sourceTenant
   * @param targetTenant
   * @return
   */
  public AlarmDingDingRobotDTO migrateDingDingRobot(Long id, String sourceTenant,
      String targetTenant) {
    AlarmDingDingRobotDTO alarmDingDingRobotDTO =
        alarmDingDingRobotService.queryById(id, sourceTenant);
    alarmDingDingRobotDTO.setId(null);
    alarmDingDingRobotDTO.setTenant(targetTenant);
    alarmDingDingRobotDTO.setGmtModified(new Date());
    alarmDingDingRobotDTO.setGmtCreate(new Date());
    Long alarmDingDingRobotId = alarmDingDingRobotService.save(alarmDingDingRobotDTO);
    alarmDingDingRobotDTO.setId(alarmDingDingRobotId);
    return alarmDingDingRobotDTO;
  }
}
