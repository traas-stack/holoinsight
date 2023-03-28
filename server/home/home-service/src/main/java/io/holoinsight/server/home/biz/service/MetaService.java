/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.Workspace;
import io.holoinsight.server.common.service.WorkspaceService;
import io.holoinsight.server.home.common.util.Debugger;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.meta.facade.service.DataClientService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 * <p>
 * Date: 2023-03-24 Time: 14:16
 * </p>
 *
 * @author jsy1001de
 */
@Service
public class MetaService {

  private static final String meta_app = "app";
  private static final String meta_type = "_type";
  private static final String meta_workspace = "_workspace";
  private static final String meta_workspace_default = "default";
  @Autowired
  private DataClientService dataClientService;
  @Autowired
  private WorkspaceService workspaceService;


  public List<AppModel> getAppModelFromServerTable(String tenant, String serverTableName) {

    List<Workspace> byTenant = workspaceService.getByTenant(tenant);

    List<Map<String, Object>> mapList = dataClientService.queryAll(serverTableName);
    Debugger.print("TenantAppMetaSyncTask", "query meta list from table={} size={}",
        serverTableName, mapList.size());
    if (CollectionUtils.isEmpty(mapList)) {
      return new ArrayList<>();
    }

    List<AppModel> appModels = new ArrayList<>();
    for (Map<String, Object> map : mapList) {
      if (!map.containsKey(meta_app) || null == map.get(meta_app)) {
        continue;
      }

      String app = map.get(meta_app).toString();
      if (StringUtil.isBlank(app) || "-".equalsIgnoreCase(app)) {
        continue;
      }
      AppModel appModel = new AppModel();

      String workspace = meta_workspace_default;
      if (map.containsKey(meta_workspace) && null != map.get(meta_workspace)
          && !CollectionUtils.isEmpty(byTenant)) {
        workspace = map.get(meta_workspace).toString();
      }
      appModel.setApp(app);
      appModel.setWorkspace(workspace);

      if (map.containsKey(meta_type) && null != map.get(meta_type)) {
        appModel.setMachineType(map.get(meta_type).toString());
      }
      appModels.add(appModel);
    }

    return appModels;
  }

  public List<AppModel> getAppModelFromAppTable(String appTableName) {
    List<Map<String, Object>> dbLists = dataClientService.queryAll(appTableName);

    List<AppModel> appModels = new ArrayList<>();
    if (CollectionUtils.isEmpty(dbLists)) {
      return appModels;
    }

    dbLists.forEach(db -> {
      if (null != db.get(meta_app)) {
        return;
      }
      AppModel appModel = new AppModel();
      appModel.setApp(db.get(meta_app).toString());
      appModel.setWorkspace(db.getOrDefault(meta_workspace, meta_workspace_default).toString());

      if (null != db.get("_label"))
        return;

      Map<String, Object> map = J.toMap(J.toJson(db.get("_label")));
      assert map != null;
      appModel.setMachineType(map.getOrDefault(meta_type, "-").toString());
      appModels.add(appModel);
    });

    return appModels;
  }

  @Data
  public static class AppModel {
    public String app;
    public String machineType;
    public String workspace;
    public String tenant;
  }
}
