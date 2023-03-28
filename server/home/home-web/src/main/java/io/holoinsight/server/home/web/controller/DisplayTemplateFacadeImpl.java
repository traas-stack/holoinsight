/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.DisplayTemplateService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorCookieUtil;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.DisplayTemplate;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.DisplayTemplateDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayTemplateFacadeImpl.java, v 0.1 2022年12月06日 上午10:34 jinsong.yjs Exp $
 */
@Slf4j
@RestController
@RequestMapping("/webapi/displaytemplate")
public class DisplayTemplateFacadeImpl extends BaseFacade {

  @Autowired
  private UserOpLogService userOpLogService;

  @Autowired
  private DisplayTemplateService displayTemplateService;

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.SUPER_ADMIN)
  public JsonResult<Object> update(@RequestBody DisplayTemplate template) {
    final JsonResult<DisplayTemplateDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(template.id, "id");
        ParaCheckUtil.checkParaNotBlank(template.type, "type");
        ParaCheckUtil.checkParaNotBlank(template.config, "config");
        ParaCheckUtil.checkParaNotNull(template.refId, "refId");
        // ParaCheckUtil.checkParaNotNull(template.getTenant(), "tenant");
        // ParaCheckUtil.checkEquals(template.getTenant(),
        // RequestContext.getContext().ms.getTenant(), "tenant is illegal");

        // DisplayTemplateDTO item = displayTemplateService.queryById(template.getId(),
        // RequestContext.getContext().ms.getTenant());

        // if (null == item) {
        // throw new MonitorException("cannot find record: " + template.getId());
        // }
        // if (!item.getTenant().equalsIgnoreCase(template.getTenant())) {
        // throw new MonitorException("the tenant parameter is invalid");
        // }
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;

        DisplayTemplate update = new DisplayTemplate();

        BeanUtils.copyProperties(template, update);

        if (null != mu) {
          update.setModifier(mu.getLoginName());
        }
        update.setTenant("-1");
        update.setGmtModified(new Date());
        displayTemplateService.updateById(update);

        assert mu != null;
        userOpLogService.append("display_template", template.getId(), OpType.UPDATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(template),
            J.toJson(update), null, "display_template_update");
      }
    });

    return JsonResult.createSuccessResult(true);
  }

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.SUPER_ADMIN)
  public JsonResult<DisplayTemplate> save(@RequestBody DisplayTemplate template) {
    final JsonResult<DisplayTemplate> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(template.type, "type");
        ParaCheckUtil.checkParaNotBlank(template.config, "config");
        ParaCheckUtil.checkParaNotNull(template.refId, "refId");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          template.setCreator(mu.getLoginName());
          template.setModifier(mu.getLoginName());
        }
        // if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
        // template.setTenant(ms.tenant);
        // }
        template.setTenant("-1");
        template.setTenant(MonitorCookieUtil.getTenantOrException());
        template.setGmtCreate(new Date());
        template.setGmtModified(new Date());
        displayTemplateService.save(template);
        JsonResult.createSuccessResult(result, template);

        assert mu != null;
        userOpLogService.append("display_template", template.getId(), OpType.CREATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(template), null, null,
            "display_template_create");

      }
    });

    return result;
  }

  @GetMapping(value = "/query/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<DisplayTemplateDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<DisplayTemplateDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        DisplayTemplateDTO template = displayTemplateService.queryById(id);

        if (null == template) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, template);
      }
    });
    return result;
  }

  @GetMapping(value = "/query/{type}/{refId}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<DisplayTemplateDTO> queryByTypeRefId(@PathVariable("refId") Long refId,
      @PathVariable("type") String type) {
    final JsonResult<DisplayTemplateDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(refId, "refId");
        ParaCheckUtil.checkParaNotNull(type, "type");
      }

      @Override
      public void doManage() {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("ref_id", refId);
        columnMap.put("type", type);
        List<DisplayTemplateDTO> displayTemplates = displayTemplateService.findByMap(columnMap);

        if (CollectionUtils.isEmpty(displayTemplates)) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, displayTemplates.get(0));
      }
    });
    return result;
  }

  // @DeleteMapping(value = "/delete/{id}")
  // @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  // public JsonResult<Object> deleteById(@PathVariable("id") Long id) {
  // final JsonResult<Object> result = new JsonResult<>();
  // facadeTemplate.manage(result, new ManageCallback() {
  // @Override
  // public void checkParameter() {
  // ParaCheckUtil.checkParaNotNull(id, "id");
  // }
  //
  // @Override
  // public void doManage() {
  //
  //
  // DisplayTemplate byId = displayTemplateService.queryById(id,
  // RequestContext.getContext().ms.getTenant());
  // displayTemplateService.removeById(id);
  // JsonResult.createSuccessResult(result, null);
  // userOpLogService.append("display_template", String.valueOf(byId.getId()), OpType.DELETE,
  // RequestContext.getContext().mu.getLoginName(),
  // RequestContext.getContext().ms.getTenant(), J.toJson(byId), null, null,
  // "display_template_delete");
  //
  // }
  // });
  // return result;
  // }
}
