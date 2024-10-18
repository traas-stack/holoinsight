/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.MD5Hash;
import io.holoinsight.server.common.cache.local.CommonLocalCache;
import io.holoinsight.server.common.dao.entity.AlarmCountable;
import io.holoinsight.server.common.dao.entity.AlarmHistoryDetail;
import io.holoinsight.server.common.dao.entity.AlarmMetric;
import io.holoinsight.server.common.dao.entity.Folder;
import io.holoinsight.server.common.dao.entity.FolderPath;
import io.holoinsight.server.common.dao.entity.FolderPaths;
import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.service.AlarmHistoryDetailService;
import io.holoinsight.server.common.service.AlarmMetricService;
import io.holoinsight.server.common.service.FolderService;
import io.holoinsight.server.common.service.MetricInfoService;
import io.holoinsight.server.home.biz.service.CustomPluginService;
import io.holoinsight.server.home.dal.model.CustomPlugin;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author limengyang
 * @version MonitorAlarmRrdTaskJob.java, v 0.1 2024年09月19日 15:33 limengyang
 */
@Slf4j
public class MonitorAlarmRrdTaskJob extends MonitorTaskJob {


  private static Long MINUTE = 6000l;

  private static final List<String> LOG_METRIC_Type =
      Arrays.asList("logsample", "logdefault", "logspm", "logpattern");

  private static String ALARM_RRD_TASK_GET_CUSTOM_PLUGIN = "ALARM_RRD_TASK_GET_CUSTOM_PLUGIN";

  private long period;

  private AlarmHistoryDetailService alarmHistoryDetailService;

  private CustomPluginService customPluginService;

  private FolderService folderService;

  private MetricInfoService metricInfoService;

  private AlarmMetricService alarmMetricService;


  public MonitorAlarmRrdTaskJob(long period, AlarmHistoryDetailService alarmHistoryDetailService,
      CustomPluginService customPluginService, MetricInfoService metricInfoService,
      AlarmMetricService alarmMetricService, FolderService folderService) {
    super();
    this.period = period;
    this.alarmHistoryDetailService = alarmHistoryDetailService;
    this.customPluginService = customPluginService;
    this.metricInfoService = metricInfoService;
    this.alarmMetricService = alarmMetricService;
    this.folderService = folderService;
  }

  @Override
  public boolean run() throws Throwable {
    // 获取最近5分钟所有的报警记录列表
    List<AlarmHistoryDetail> ahs = alarmHistoryDetailService
        .queryByTime(period - MINUTE - MonitorAlarmRrdTask.PERIOD, period - MINUTE);
    List<AlarmCountable> acs = new ArrayList<>();
    Set<Long> customPluginIds = fillCustomPluginId(ahs, acs);
    if (customPluginIds == null || customPluginIds.size() == 0) {
      return true; // 没有效报警
    }
    Map<Long, CustomPluginDTO> customPlugins = buildCustomPluginMap(customPluginIds);
    Set<Folder> folderIds = fillParentFolderId(acs, customPlugins);

    Map<Long, FolderPaths> folderPathMap = buildFolderMap(folderIds);

    // 产品的父文件夹+父文件夹所在的整个文件路径，做聚合
    Map<Long, Folder> resultFolder = getAllClearFolders(folderPathMap.values());
    Map<Long, CustomPlugin> resultCustomPlugin = getClearCustomPlugins(customPlugins.keySet());

    for (AlarmCountable ac : acs) {
      // 处理一个报警， 先给产品自己弄上
      CustomPlugin cp = resultCustomPlugin.get(ac.customPluginId);
      if (cp == null) {
        continue;
      }
      setAlarmForCustomPlugin(cp, ac.historyId, period);
      if (ac.parentFolderId == null) {
        continue;
      }
      // 给产品向上的父文件搞上
      FolderPaths fps = folderPathMap.get(ac.parentFolderId);
      // to fix npe
      if (fps != null) {
        for (FolderPath fp : fps.paths) {
          Folder folder = resultFolder.get(fp.getId());
          if (folder == null) {
            break;
          }
          setAlarmForFolder(folder, ac.historyId, period);
        }
      }
    }
    // 都搞完了，更新入库
    updateRrdData(resultFolder.values(), resultCustomPlugin.values());
    return true;
  }

  @Override
  public String id() {
    return MonitorAlarmRrdTask.TASK_ID; // 就一个job
  }

  private void updateRrdData(Collection<Folder> folders, Collection<CustomPlugin> cps) {
    for (CustomPlugin cp : cps) {
      try {
        customPluginService.updateById(cp);
      } catch (Exception e) {
        log.error("update customplugin alarm rrd failed for " + J.toJson(cp), e);
      }
    }
    for (Folder f : folders) {
      try {
        folderService.updateById(f);
      } catch (Exception e) {
        log.error("update folder alarm rrd failed for " + J.toJson(f), e);
      }
    }
  }

  private void setAlarmForFolder(Folder f, Long historyId, long period) {
    // 报警标记
    f.setAlarmed(true);
    // 报警数量
    Integer recent = f.getRecentAlarm();
    if (recent == null) {
      recent = 1;
      f.setRecentAlarm(recent);
    } else {
      f.setRecentAlarm(recent + 1);
    }
    // 设置归档时间
    f.setAlarmRrdTime(period);

    if (recent != null && recent < 150) {
      String alarmHistoryIdStr = f.getRecentAlarmHistoryId();
      if (StringUtils.isBlank(alarmHistoryIdStr)) {
        // 防止默认的null字符串
        alarmHistoryIdStr = String.valueOf(historyId);
      } else {
        alarmHistoryIdStr += "," + historyId;
      }
      f.setRecentAlarmHistoryId(alarmHistoryIdStr);
    }
  }

  private void setAlarmForCustomPlugin(CustomPlugin cp, Long historyId, long period) {
    // 报警标记
    cp.setAlarmed(true);
    // 报警数量
    Integer recent = cp.getRecentAlarm();
    if (recent == null) {
      recent = 1;
      cp.setRecentAlarm(recent);
    } else {
      cp.setRecentAlarm(recent + 1);
    }
    // 设置归档时间
    cp.setAlarmRrdTime(period);
    // 报警历史
    if (recent != null && recent < 150) {
      String alarmHistoryIdStr = cp.getRecentAlarmHistoryId();
      if (StringUtils.isBlank(alarmHistoryIdStr)) {
        // 防止默认的null字符串
        alarmHistoryIdStr = String.valueOf(historyId);
      } else {
        alarmHistoryIdStr += "," + historyId;
      }
      cp.setRecentAlarmHistoryId(alarmHistoryIdStr);
    }
  }

  private Map<Long, CustomPlugin> getClearCustomPlugins(Collection<Long> customPlugins) {
    Map<Long, CustomPlugin> ret = new HashMap<>();
    for (Long cpId : customPlugins) {
      CustomPlugin cp = new CustomPlugin();
      cp.setId(cpId);
      ret.put(cpId, cp);
    }
    return ret;
  }

  // 包括产品的父目录以及它上面的所有父亲
  private Map<Long, Folder> getAllClearFolders(Collection<FolderPaths> fps) {
    Map<Long, Folder> relatedFolderMap = new HashMap<>();
    for (FolderPaths fp : fps) {
      List<FolderPath> ps = fp.paths;
      for (FolderPath p : ps) {
        // 直接put,如果是同一个文件夹也无所谓
        Folder folder = new Folder();
        folder.setId(p.getId());
        relatedFolderMap.put(p.getId(), folder);
      }
    }
    return relatedFolderMap;
  }

  private Map<Long, FolderPaths> buildFolderMap(Set<Folder> folders) {
    Map<Long, FolderPaths> ret = new HashMap<>();
    for (Folder folder : folders) {
      try {
        if (folder.id == -1) {
          // 非法的直接过
          continue;
        }
        folder = folderService.queryById(folder.id, folder.tenant, folder.workspace);
        if (folder == null) {
          // 不存在了，直接过
          continue;
        }
        FolderPath filePath = new FolderPath(folder.getId(), folder.getName(), FolderPath.FOLDER);
        Long pfId = folder.getParentFolderId();
        // 从pfId 开始，往上一个个挨着撸，直到到达根目录
        FolderPaths fps = getAbsPath(pfId);
        fps.paths.add(0, filePath);
        ret.put(folder.id, fps);
      } catch (Exception e) {
        log.error("get folder abs path error, " + folder.id, e);
      }
    }
    return ret;
  }

  public FolderPaths getAbsPath(Long pfId) throws Exception {
    FolderPaths fps = new FolderPaths();
    return gogo(pfId, fps);
  }


  private FolderPaths gogo(Long pfId, FolderPaths fps) throws Exception {
    if (pfId == -1L) {
      // 在根目录下, 到达递归终止条件
      return fps;
    }
    Folder f = folderService.getById(pfId);
    if (f == null) {
      throw new Exception("invalid folder id:" + pfId);
    }
    fps.paths.add(new FolderPath(f.getId(), f.getName()));
    return gogo(f.getParentFolderId(), fps);
  }


  private Set<Folder> fillParentFolderId(List<AlarmCountable> acs,
      Map<Long, CustomPluginDTO> customPlugins) {
    Set<Folder> folders = new HashSet<>();
    for (AlarmCountable ac : acs) {
      Folder folder = new Folder();
      Long customPluginId = ac.getCustomPluginId();
      CustomPluginDTO cp = customPlugins.get(customPluginId);
      if (cp == null) {
        continue;
      }
      Long pfId = cp.getParentFolderId();
      if (pfId == null) {
        continue;
      }
      folder.id = pfId;
      folder.tenant = cp.tenant;
      folder.workspace = cp.workspace;
      folders.add(folder);
      // folderIds.add(pfId);
      ac.setParentFolderId(pfId);
    }
    return folders;
  }

  private Map<Long, CustomPluginDTO> buildCustomPluginMap(Set<Long> customPluginIds) {
    Map<Long, CustomPluginDTO> ret = new HashMap<>();
    List<String> ids = new ArrayList<>();
    customPluginIds.forEach(id -> ids.add(id.toString()));
    List<CustomPluginDTO> cps = customPluginService.findByIds(ids);
    for (CustomPluginDTO cp : cps) {
      ret.put(cp.getId(), cp);
    }
    return ret;
  }

  private Set<Long> fillCustomPluginId(List<AlarmHistoryDetail> ahs, List<AlarmCountable> acs) {
    Set<Long> customPluginIds = new HashSet<>();

    for (AlarmHistoryDetail ah : ahs) {
      if (StringUtils.isEmpty(ah.getUniqueId())) {
        continue;
      }
      String cacheKey = ALARM_RRD_TASK_GET_CUSTOM_PLUGIN + "@" + MD5Hash.getMD5(ah.getUniqueId());
      Object o = CommonLocalCache.get(cacheKey);
      List<Long> customPluginIdList = new ArrayList<>();
      if (null != o) {
        customPluginIdList = (List<Long>) o;
        log.info("used cacheKey: " + cacheKey + "value: " + customPluginIdList);
        customPluginIds = new HashSet<>(customPluginIdList);
      } else {
        Long ruleId = null;
        if (ah.getUniqueId().contains("rule_")) {
          ruleId = Long.valueOf(ah.getUniqueId().replace("rule_", ""));
        } else if (ah.getUniqueId().contains("ai_")) {
          ruleId = Long.valueOf(ah.getUniqueId().replace("ai_", ""));
        }
        if (null == ruleId) {
          continue;
        }
        List<AlarmMetric> metrics =
            alarmMetricService.queryByRuleId(ruleId, ah.getTenant(), ah.getWorkspace());
        log.info("metrics size: " + metrics.size());
        for (AlarmMetric alarmMetric : metrics) {
          MetricInfoDTO metricInfoDTO = metricInfoService.queryByMetric(alarmMetric.getTenant(),
              alarmMetric.getWorkspace(), alarmMetric.getMetricTable());
          if (null == metricInfoDTO || StringUtils.isBlank(metricInfoDTO.getRef())
              || !LOG_METRIC_Type.contains(metricInfoDTO.getMetricType())
              || !metricInfoDTO.getRef().matches("-?\\d+")) {
            continue;
          }
          log.info("metricInfoDTO getRef: " + metricInfoDTO.getRef());
          Long customPluginId = Long.valueOf(metricInfoDTO.getRef());
          customPluginIds.add(customPluginId);
          customPluginIdList.add(customPluginId);
          CommonLocalCache.put(cacheKey, customPluginIdList, 1, TimeUnit.HOURS);
        }
      }
      customPluginIdList.forEach(customPluginId -> {
        AlarmCountable ac = new AlarmCountable();
        ac.customPluginId = customPluginId;
        ac.historyId = ah.getHistoryId();
        acs.add(ac);
      });
    }
    return customPluginIds;
  }



}
