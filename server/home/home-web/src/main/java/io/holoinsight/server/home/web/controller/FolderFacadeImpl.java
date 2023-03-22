/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.CustomPluginService;
import io.holoinsight.server.home.biz.service.FolderService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.Folder;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.controller.model.FolderPath;
import io.holoinsight.server.home.web.controller.model.FolderPaths;
import io.holoinsight.server.home.web.controller.model.FolderRequest;
import io.holoinsight.server.home.web.controller.model.FolderRequestCmd;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: FolderFacadeImpl.java, v 0.1 2022年05月23日 8:38 下午 jinsong.yjs Exp $
 */
@Slf4j
@RestController
@RequestMapping("/webapi/folder")
public class FolderFacadeImpl extends BaseFacade {

  @Autowired
  private FolderService folderService;

  @Autowired
  private CustomPluginService customPluginService;

  @Autowired
  private UserOpLogService userOpLogService;

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> update(@RequestBody Folder folder) {
    final JsonResult<Folder> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        MonitorScope ms = RequestContext.getContext().ms;
        ParaCheckUtil.checkParaNotNull(folder.id, "id");
        ParaCheckUtil.checkParaNotNull(folder.parentFolderId, "parentFolderId");
        ParaCheckUtil.checkParaNotBlank(folder.name, "name");
        ParaCheckUtil.checkParaNotNull(folder.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(folder.getTenant(), ms.getTenant(), "tenant is illegal");

        Folder item = folderService.queryById(folder.getId(), ms.getTenant(), ms.getWorkspace());

        if (null == item) {
          throw new MonitorException("cannot find record: " + folder.getId());
        }
        if (!item.getTenant().equalsIgnoreCase(folder.getTenant())) {
          throw new MonitorException("the tenant parameter is invalid");
        }
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;

        Folder update = new Folder();

        BeanUtils.copyProperties(folder, update);

        if (null != mu) {
          update.setModifier(mu.getLoginName());
        }
        if (null != ms && !StringUtil.isBlank(ms.tenant)) {
          update.setTenant(ms.tenant);
        }
        if (null != ms && !StringUtil.isBlank(ms.workspace)) {
          update.setWorkspace(ms.workspace);
        }
        update.setGmtModified(new Date());
        folderService.updateById(update);

        assert mu != null;
        userOpLogService.append("folder", folder.getId(), OpType.UPDATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(folder), J.toJson(update), null,
            "folder_update");
      }
    });

    return JsonResult.createSuccessResult(true);
  }

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Folder> save(@RequestBody Folder folder) {
    final JsonResult<Folder> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(folder.parentFolderId, "parentFolderId");
        ParaCheckUtil.checkParaNotBlank(folder.name, "name");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          folder.setCreator(mu.getLoginName());
          folder.setModifier(mu.getLoginName());
        }
        if (null != ms && !StringUtil.isBlank(ms.tenant)) {
          folder.setTenant(ms.tenant);
        }

        if (null != ms && !StringUtil.isBlank(ms.workspace)) {
          folder.setWorkspace(ms.workspace);
        }
        folder.setGmtCreate(new Date());
        folder.setGmtModified(new Date());
        folderService.save(folder);
        JsonResult.createSuccessResult(result, folder);

        assert mu != null;
        userOpLogService.append("folder", folder.getId(), OpType.CREATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(folder), null, null, "folder_create");

      }
    });

    return result;
  }

  @GetMapping(value = "/query/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Folder> queryById(@PathVariable("id") Long id) {
    final JsonResult<Folder> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Folder folder = folderService.queryById(id, ms.getTenant(), ms.getWorkspace());

        if (null == folder) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, folder);
      }
    });
    return result;
  }

  @DeleteMapping(value = "/delete/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> deleteById(@PathVariable("id") Long id) {
    final JsonResult<Object> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("parent_folder_id", id);
        columnMap.put("tenant", ms.getTenant());
        columnMap.put("workspace", ms.getTenant());
        List<CustomPluginDTO> byMap = customPluginService.findByMap(columnMap);
        if (!CollectionUtils.isEmpty(byMap)) {
          JsonResult
              .createFailResult(String.format("there are %s conf under this folder", byMap.size()));
          return;
        }

        Folder byId = folderService.queryById(id, ms.getTenant(), ms.getWorkspace());
        folderService.removeById(id);
        JsonResult.createSuccessResult(result, null);
        userOpLogService.append("folder", byId.getId(), OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(byId), null, null, "folder_delete");

      }
    });
    return result;
  }

  @PostMapping("/updateParentFolderId")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> updateParentFolderId(@RequestBody Folder folder) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(folder.id, "id");
        ParaCheckUtil.checkParaNotNull(folder.parentFolderId, "parentFolderId");
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;

        Folder update = folderService.queryById(folder.id, ms.getTenant(), ms.getWorkspace());
        if (null == update) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        if (null != mu) {
          update.setModifier(mu.getLoginName());
        }
        update.setParentFolderId(folder.getParentFolderId());

        update.setGmtModified(new Date());
        folderService.updateById(update);
        JsonResult.createSuccessResult(result, true);
        assert mu != null;
        userOpLogService.append("folder", folder.getId(), OpType.UPDATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(folder), J.toJson(update), null,
            "folder_update");
      }
    });

    return result;
  }

  @GetMapping(value = "/queryByParentFolderId/{parentFolderId}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<Folder>> queryByParentFolderIdAndTenant(
      @PathVariable("parentFolderId") Long parentFolderId) {
    final JsonResult<List<Folder>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(parentFolderId, "parentFolderId");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("parent_folder_id", parentFolderId);
        conditions.put("tenant", ms.getTenant());
        conditions.put("workspace", ms.getWorkspace());
        JsonResult.createSuccessResult(result, folderService.listByMap(conditions));
      }
    });
    return result;
  }

  @PostMapping("/path")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<FolderPaths>> path(@RequestBody FolderRequest folderReqs) {
    final JsonResult<List<FolderPaths>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotEmpty(folderReqs.requests, "requests");
      }

      @SneakyThrows
      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        if (folderReqs.requests != null && folderReqs.requests.size() > 0) {
          List<FolderPaths> list = new ArrayList<FolderPaths>();
          for (FolderRequestCmd folderReq : folderReqs.requests) {
            list.add(doOne(folderReq, ms.getTenant(), ms.getWorkspace()));
          }
          JsonResult.createSuccessResult(result, list);
        }
      }
    });

    return result;
  }

  private FolderPaths doOne(FolderRequestCmd folderReq, String tenant, String workspace)
      throws Exception {
    Long pfId;
    FolderPath filePath = null;
    if (folderReq.customPluginId != null) {
      if (folderReq.customPluginId == -1) {
        // 非法的直接返回
        return new FolderPaths();
      }
      CustomPluginDTO cp =
          customPluginService.queryById(folderReq.customPluginId, tenant, workspace);
      if (cp == null) {
        return new FolderPaths();
      }
      filePath = new FolderPath(cp.getId(), cp.getName(), FolderPath.FILE);
      pfId = cp.getParentFolderId();
    } else if (folderReq.folderId != null) {
      if (folderReq.folderId == -1) {
        // 非法的直接返回
        return new FolderPaths();
      }
      Folder folder = folderService.queryById(folderReq.folderId, tenant, workspace);
      if (folder == null) {
        // 不存在了，直接返回
        return new FolderPaths();
      }
      filePath = new FolderPath(folder.getId(), folder.getName(), FolderPath.FOLDER);
      pfId = folder.getParentFolderId();
    } else {
      throw new Exception("unknow id? " + J.toJson(folderReq));
    }
    // 从pfId 开始，往上一个个挨着撸，直到到达根目录
    FolderPaths ret = getAbsPath(pfId);
    // 面包屑等特定场景使用
    if (folderReq.includePluginName) {
      if (ret.paths == null) {
        ret.paths = new ArrayList<>();
      }
      ret.paths.add(0, filePath);
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
}
