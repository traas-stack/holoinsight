/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.CustomPluginService;
import io.holoinsight.server.home.biz.service.DashboardService;
import io.holoinsight.server.home.biz.service.FolderService;
import io.holoinsight.server.home.biz.service.IntegrationProductService;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.biz.service.UserFavoriteService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.common.service.SpringContext;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.Dashboard;
import io.holoinsight.server.home.dal.model.Folder;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.UserFavorite;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.controller.model.FavRequest;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.web.security.ParameterSecurityService;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;
import lombok.extern.slf4j.Slf4j;
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

/**
 *
 * @author jsy1001de
 * @version 1.0: UserFavoriteFacadeImpl.java, v 0.1 2022年03月21日 3:43 下午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/userFavorite")
@Slf4j
public class UserFavoriteFacadeImpl extends BaseFacade {

  @Autowired
  private UserFavoriteService userFavoriteService;

  @Autowired
  private DataClientService dataClientService;

  @Autowired
  private TenantInitService tenantInitService;

  @Autowired
  private IntegrationProductService integrationProductService;

  @Autowired
  private CustomPluginService customPluginService;

  @Autowired
  private FolderService folderService;

  @Autowired
  private DashboardService dashboardService;

  @Autowired
  private UserOpLogService userOpLogService;

  @Autowired
  private ParameterSecurityService parameterSecurityService;

  @Autowired
  private RequestContextAdapter requestContextAdapter;

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<UserFavorite> save(@RequestBody UserFavorite userFavorite) {
    final JsonResult<UserFavorite> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(userFavorite.relateId, "relateId");
        ParaCheckUtil.checkParaNotBlank(userFavorite.type, "type");
        MonitorScope ms = RequestContext.getContext().ms;
        switch (userFavorite.type) {
          case "folder":
            Folder folder = folderService.queryById(Long.parseLong(userFavorite.getRelateId()),
                ms.tenant, ms.workspace);
            if (null == folder) {
              throw new MonitorException(String.format("can not find record, %s-%s",
                  userFavorite.type, userFavorite.relateId));
            }
            break;
          case "logmonitor":
            CustomPluginDTO customPluginDTO = customPluginService
                .queryById(Long.parseLong(userFavorite.getRelateId()), ms.tenant, ms.workspace);
            if (null == customPluginDTO) {
              throw new MonitorException(String.format("can not find record, %s-%s",
                  userFavorite.type, userFavorite.relateId));
            }
            break;
          case "dashboard":
            Dashboard dashboard = dashboardService
                .queryById(Long.parseLong(userFavorite.getRelateId()), ms.tenant, ms.workspace);
            if (null == dashboard) {
              throw new MonitorException(String.format("can not find record, %s-%s",
                  userFavorite.type, userFavorite.relateId));
            }
            break;
          case "infra":
          case "app":
            QueryExample queryExample = new QueryExample();
            Map<String, Object> map = new HashMap<>();
            if (userFavorite.type.equalsIgnoreCase("infra")) {
              map.put("hostname", userFavorite.name);
              queryExample.setParams(map);
              Map<String, String> conditions = tenantInitService
                  .getTenantWorkspaceMetaConditions(ms.getTenant(), ms.getWorkspace());
              if (!CollectionUtils.isEmpty(conditions)) {
                queryExample.getParams().putAll(conditions);
              }
              List<Map<String, Object>> list = dataClientService.queryByExample(
                  tenantInitService.getTenantServerTable(ms.getTenant()), queryExample);
              if (CollectionUtils.isEmpty(list)) {
                throw new MonitorException(String.format("can not find record, %s-%s",
                    userFavorite.type, userFavorite.relateId));
              }
            } else {
              map.put("app", userFavorite.name);
              if (StringUtils.isNotBlank(ms.getWorkspace())) {
                queryExample.getParams().put("_workspace", ms.getWorkspace());
              }
              List<Map<String, Object>> list = dataClientService.queryByExample(
                  tenantInitService.getTenantAppTable(ms.getTenant()), queryExample);
              if (CollectionUtils.isEmpty(list)) {
                throw new MonitorException(String.format("can not find record, %s-%s",
                    userFavorite.type, userFavorite.relateId));
              }
            }
            break;
          case "integration":
            IntegrationProductDTO byName =
                integrationProductService.findByName(userFavorite.relateId);
            if (null == byName) {
              throw new MonitorException(String.format("can not find record, %s-%s",
                  userFavorite.type, userFavorite.relateId));
            }
            break;
          case "miniapp":
            if (!parameterSecurityService.checkRelateId(userFavorite.getRelateId(),
                userFavorite.getType(), ms.getTenant(), ms.getWorkspace())) {
              throw new MonitorException(String.format("invalid miniapp appId, %s-%s",
                  userFavorite.type, userFavorite.relateId));
            }
            break;
          default:
            throw new MonitorException(String.format("can not find record, %s-%s",
                userFavorite.type, userFavorite.relateId));
        }
        ParaCheckUtil.checkParaId(userFavorite.getId());
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          userFavorite.setUserLoginName(mu.getLoginName());
        }
        if (null != ms && !StringUtil.isBlank(ms.tenant)) {
          userFavorite.setTenant(ms.tenant);
        }
        if (null != ms && !StringUtil.isBlank(ms.workspace)) {
          userFavorite.setWorkspace(ms.workspace);
        }
        userFavorite.setGmtCreate(new Date());
        userFavorite.setGmtModified(new Date());

        UserFavorite save = userFavoriteService.create(userFavorite);
        JsonResult.createSuccessResult(result, save);

        assert mu != null;
        userOpLogService.append("user_favorite", save.getId(), OpType.CREATE, mu.getLoginName(),
            ms.getTenant(), ms.getWorkspace(), J.toJson(save), null, null, "user_favorite_create");
      }
    });

    return result;
  }

  @GetMapping(value = "/query/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<UserFavorite> queryById(@PathVariable("id") Long id) {
    final JsonResult<UserFavorite> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        UserFavorite userFavorite =
            userFavoriteService.queryById(id, ms.getTenant(), ms.getWorkspace());

        if (null == userFavorite) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, userFavorite);
      }
    });
    return result;
  }

  @GetMapping(value = "/queryByRelateId/{type}/{relateId}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<UserFavorite> queryByRelateId(@PathVariable("type") String type,
      @PathVariable("relateId") String id) {
    final JsonResult<UserFavorite> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        List<UserFavorite> byUserAndTenantAndRelateId =
            userFavoriteService.getByUserAndTenantAndRelateId(mu.getLoginName(), ms.getTenant(),
                ms.getWorkspace(), id, type);

        if (CollectionUtils.isEmpty(byUserAndTenantAndRelateId)) {
          JsonResult.createSuccessResult(result, null);
          return;
        }
        JsonResult.createSuccessResult(result, byUserAndTenantAndRelateId.get(0));
      }
    });
    return result;
  }

  @PostMapping(value = "/queryByCondition")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<UserFavorite>> queryByRelateId(@RequestBody FavRequest favRequest) {
    final JsonResult<List<UserFavorite>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotEmpty(favRequest.getFavRequestCmds(), "favRequestCmds");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;

        if (StringUtil.isBlank(favRequest.getUserLoginName())) {
          favRequest.setUserLoginName(mu.getLoginName());
        }

        favRequest.setTenant(RequestContext.getContext().ms.getTenant());

        List<UserFavorite> userFavorites = new ArrayList<>();
        favRequest.getFavRequestCmds().forEach(favRequestCmd -> {
          List<UserFavorite> favorites =
              userFavoriteService.getByUserAndTenantAndRelateIds(mu.getLoginName(), ms.getTenant(),
                  ms.getWorkspace(), favRequestCmd.getRelateIds(), favRequestCmd.getType());

          if (!CollectionUtils.isEmpty(favorites)) {
            userFavorites.addAll(favorites);
          }
        });

        JsonResult.createSuccessResult(result, userFavorites);
      }
    });
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<UserFavorite>> pageQuery(
      @RequestBody MonitorPageRequest<UserFavorite> userFavoriteRequest) {
    final JsonResult<MonitorPageResult<UserFavorite>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(userFavoriteRequest.getTarget(), "target");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        if (null != ms && !StringUtil.isBlank(ms.tenant)) {
          userFavoriteRequest.getTarget().setTenant(ms.tenant);
        }
        if (null != ms && !StringUtil.isBlank(ms.workspace)) {
          userFavoriteRequest.getTarget().setWorkspace(ms.workspace);
        }
        if (StringUtils.isEmpty(userFavoriteRequest.getTarget().getUserLoginName())) {
          if (StringUtils.isNotEmpty(requestContextAdapter.getLoginName())) {
            userFavoriteRequest.getTarget().setUserLoginName(requestContextAdapter.getLoginName());
          }
        }
        JsonResult.createSuccessResult(result,
            userFavoriteService.getListByPage(userFavoriteRequest));
      }
    });

    return result;
  }

  @GetMapping("/queryAll")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<UserFavorite>> queryAll() {
    final JsonResult<List<UserFavorite>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        MonitorUser mu = RequestContext.getContext().mu;
        MonitorScope ms = RequestContext.getContext().ms;

        List<UserFavorite> byUserAndTenant = userFavoriteService
            .getByUserAndTenant(mu.getLoginName(), ms.getTenant(), ms.getWorkspace());

        if (CollectionUtils.isEmpty(byUserAndTenant)) {
          JsonResult.createSuccessResult(result, byUserAndTenant);
          return;
        }

        List<String> logs = new ArrayList<>();
        List<String> folders = new ArrayList<>();
        List<String> dashboards = new ArrayList<>();

        for (UserFavorite userFavorite : byUserAndTenant) {
          switch (userFavorite.type) {
            case "folder":
              folders.add(userFavorite.getRelateId());
              break;
            case "dashboard":
              dashboards.add(userFavorite.getRelateId());
              break;
            case "logmonitor":
              logs.add(userFavorite.getRelateId());
              break;
          }
        }

        Map<String, String> logMaps = new HashMap<>();
        if (!CollectionUtils.isEmpty(logs)) {
          List<CustomPluginDTO> byIds = customPluginService.findByIds(logs);
          if (!CollectionUtils.isEmpty(byIds)) {
            byIds.forEach(byId -> {
              logMaps.put(String.valueOf(byId.getId()), byId.getName());
            });
          }
        }

        Map<String, String> folderMaps = new HashMap<>();
        if (!CollectionUtils.isEmpty(folders)) {
          List<Folder> byIds = folderService.findByIds(folders);
          if (!CollectionUtils.isEmpty(byIds)) {
            byIds.forEach(byId -> {
              folderMaps.put(String.valueOf(byId.getId()), byId.getName());
            });
          }
        }

        Map<String, String> dashboardMaps = new HashMap<>();
        if (!CollectionUtils.isEmpty(dashboards)) {
          List<Dashboard> byIds = dashboardService.findByIds(dashboards);
          if (!CollectionUtils.isEmpty(byIds)) {
            byIds.forEach(byId -> {
              dashboardMaps.put(String.valueOf(byId.getId()), byId.getTitle());
            });
          }
        }

        for (UserFavorite userFavorite : byUserAndTenant) {
          switch (userFavorite.type) {
            case "folder":
              if (folderMaps.containsKey(userFavorite.getRelateId())) {
                userFavorite.setName(folderMaps.get(userFavorite.getRelateId()));
              }

              break;
            case "dashboard":
              if (dashboardMaps.containsKey(userFavorite.getRelateId())) {
                userFavorite.setName(dashboardMaps.get(userFavorite.getRelateId()));
              }
              break;
            case "logmonitor":
              if (logMaps.containsKey(userFavorite.getRelateId())) {
                userFavorite.setName(logMaps.get(userFavorite.getRelateId()));
              }
              break;
          }
        }

        JsonResult.createSuccessResult(result, byUserAndTenant);
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
        UserFavorite byId = userFavoriteService.queryById(id, ms.getTenant(), ms.getWorkspace());
        if (null == byId) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
              "can not find record:" + id);
        }
        userFavoriteService.deleteById(id);
        JsonResult.createSuccessResult(result, true);
        userOpLogService.append("user_favorite", byId.getId(), OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(byId), null, null, "user_favorite_delete");

      }
    });
    return result;
  }

  @DeleteMapping(value = "/deleteByRelateId/{type}/{relateId}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> deleteByRelateId(@PathVariable("relateId") String relateId,
      @PathVariable("type") String type) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(relateId, "relateId");
      }

      @Override
      public void doManage() {
        MonitorUser mu = RequestContext.getContext().mu;
        MonitorScope ms = RequestContext.getContext().ms;
        List<UserFavorite> byId = userFavoriteService.getByUserAndTenantAndRelateId(
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), relateId, type);

        if (CollectionUtils.isEmpty(byId)) {
          return;
        }

        userFavoriteService.deleteById(byId.get(0).id);
        JsonResult.createSuccessResult(result, true);
        userOpLogService.append("user_favorite", byId.get(0).id, OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(byId), null, null, "user_favorite_delete");

      }
    });
    return result;
  }
}
