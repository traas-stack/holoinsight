/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.MetaTableService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.MetaTableDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaTableFacadeImpl.java, v 0.1 2022年03月22日 11:54 上午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/metatable")
public class MetaTableFacadeImpl extends BaseFacade {

  @Autowired
  private MetaTableService metaTableService;

  @Autowired
  private UserOpLogService userOpLogService;

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<MetaTableDTO> save(@RequestBody MetaTableDTO metaTable) {
    final JsonResult<MetaTableDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(metaTable.name, "name");
      }

      @Override
      public void doManage() {
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          metaTable.setCreator(mu.getLoginName());
          metaTable.setModifier(mu.getLoginName());
        }
        metaTable.setTenant(MonitorCookieUtil.getTenantOrException());
        metaTable.setGmtCreate(new Date());
        metaTable.setGmtModified(new Date());

        MetaTableDTO save = metaTableService.create(metaTable);
        JsonResult.createSuccessResult(result, save);

        assert mu != null;
        userOpLogService.append("meta_table", save.getId(), OpType.CREATE, mu.getLoginName(),
            RequestContext.getContext().ms.getTenant(), null, J.toJson(save), null, null,
            "meta_table_update");
      }
    });

    return result;
  }

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> update(@RequestBody MetaTableDTO metaTable) {
    final JsonResult<MetaTableDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(metaTable.id, "id");
        ParaCheckUtil.checkParaNotNull(metaTable.name, "name");
        // ParaCheckUtil.checkParaNotNull(metaTable.tableSchema, "tableSchema");

        ParaCheckUtil.checkParaNotNull(metaTable.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(metaTable.getTenant(), RequestContext.getContext().ms.getTenant(),
            "tenant is illegal");

        MetaTableDTO item = metaTableService.queryById(metaTable.getId(),
            RequestContext.getContext().ms.getTenant());

        if (null == item) {
          throw new MonitorException("cannot find record: " + metaTable.getId());
        }
        if (!item.getTenant().equalsIgnoreCase(metaTable.getTenant())) {
          throw new MonitorException("the tenant parameter is invalid");
        }
      }

      @Override
      public void doManage() {

        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          metaTable.setModifier(mu.getLoginName());
        }
        metaTable.setTenant(MonitorCookieUtil.getTenantOrException());
        MetaTableDTO update = metaTableService.update(metaTable);

        assert mu != null;
        userOpLogService.append("meta_table", update.getId(), OpType.UPDATE, mu.getLoginName(),
            RequestContext.getContext().ms.getTenant(), null, J.toJson(metaTable), J.toJson(update),
            null, "meta_table_update");

      }
    });

    return JsonResult.createSuccessResult(true);
  }

  @GetMapping(value = "/query/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MetaTableDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<MetaTableDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MetaTableDTO metaTable =
            metaTableService.queryById(id, RequestContext.getContext().ms.getTenant());

        if (null == metaTable) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, metaTable);
      }
    });
    return result;
  }

  @GetMapping(value = "/queryByTenant")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MetaTableDTO>> queryByTenant() {
    final JsonResult<List<MetaTableDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = MonitorCookieUtil.getTenantOrException();
        List<MetaTableDTO> metaTables = metaTableService.findByTenant(tenant);
        JsonResult.createSuccessResult(result, metaTables);
      }
    });
    return result;
  }
}
