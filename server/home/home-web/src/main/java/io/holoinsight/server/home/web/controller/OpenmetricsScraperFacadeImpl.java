/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.OpenmetricsScraperService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.OpenmetricsScraperDTO;
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

@RestController
@RequestMapping("/webapi/openmetrics/scraper")
public class OpenmetricsScraperFacadeImpl extends BaseFacade {

  @Autowired
  private OpenmetricsScraperService openmetricsScraperService;

  @Autowired
  private UserOpLogService userOpLogService;


  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<OpenmetricsScraperDTO>> pageQuery(
      @RequestBody MonitorPageRequest<OpenmetricsScraperDTO> request) {
    final JsonResult<MonitorPageResult<OpenmetricsScraperDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(request.getTarget(), "target");
      }

      @Override
      public void doManage() {
        request.getTarget().setTenant(RequestContext.getContext().ms.getTenant());
        request.getTarget().setWorkspace(RequestContext.getContext().ms.getWorkspace());
        JsonResult.createSuccessResult(result, openmetricsScraperService.getListByPage(request));
      }
    });

    return result;
  }

  @GetMapping("/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<OpenmetricsScraperDTO> get(@PathVariable("id") Long id) {
    MonitorScope ms = RequestContext.getContext().ms;
    JsonResult<OpenmetricsScraperDTO> result = new JsonResult<>();
    OpenmetricsScraperDTO model =
        openmetricsScraperService.queryById(id, ms.getTenant(), ms.getWorkspace());
    if (model == null || !model.getTenant().equals(ms.getTenant())) {
      JsonResult.fillFailResultTo(result, "can not find the record");
      return result;
    }

    return JsonResult.createSuccessResult(model);
  }

  @PostMapping("/create")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<OpenmetricsScraperDTO> create(
      @RequestBody OpenmetricsScraperDTO openmetricsScraperDTO) {

    final JsonResult<OpenmetricsScraperDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(openmetricsScraperDTO.getCollectRanges(), "collectRanges");
        ParaCheckUtil.checkParaNotNull(openmetricsScraperDTO.getPort(), "port");
        ParaCheckUtil.checkParaNotNull(openmetricsScraperDTO.getScrapeInterval(), "interval");
        ParaCheckUtil.checkParaNotNull(openmetricsScraperDTO.getScrapeTimeout(), "timeout");
        ParaCheckUtil.checkParaNotNull(openmetricsScraperDTO.getMetricsPath(), "metricPath");
        ParaCheckUtil.checkParaId(openmetricsScraperDTO.getId());
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;

        openmetricsScraperDTO.setTenant(ms.getTenant());
        openmetricsScraperDTO.setWorkspace(ms.getWorkspace());
        openmetricsScraperService.saveByDTO(openmetricsScraperDTO);

        userOpLogService.append("openmetrics_scraper", openmetricsScraperDTO.getId(), OpType.CREATE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(openmetricsScraperDTO), null, null, "openmetrics_scraper_create");

        JsonResult.createSuccessResult(result, openmetricsScraperDTO);
      }
    });

    return result;
  }

  @PostMapping("/update/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<OpenmetricsScraperDTO> update(@PathVariable("id") Long id,
      @RequestBody OpenmetricsScraperDTO openmetricsScraperDTO) {

    final JsonResult<OpenmetricsScraperDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");

        ParaCheckUtil.checkParaNotNull(openmetricsScraperDTO.getCollectRanges(), "collectRanges");
        ParaCheckUtil.checkParaNotNull(openmetricsScraperDTO.getPort(), "port");
        ParaCheckUtil.checkParaNotNull(openmetricsScraperDTO.getScrapeInterval(), "interval");
        ParaCheckUtil.checkParaNotNull(openmetricsScraperDTO.getScrapeTimeout(), "timeout");
        ParaCheckUtil.checkParaNotNull(openmetricsScraperDTO.getMetricsPath(), "metricPath");

        ParaCheckUtil.checkParaNotNull(openmetricsScraperDTO.getTenant(), "tenant");
        ParaCheckUtil.checkEquals(openmetricsScraperDTO.getTenant(),
            RequestContext.getContext().ms.getTenant(), "tenant is illegal");


      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        OpenmetricsScraperDTO item =
            openmetricsScraperService.queryById(id, ms.getTenant(), ms.getWorkspace());

        if (null == item) {
          throw new MonitorException("cannot find record: " + id);
        }
        if (!item.getTenant().equalsIgnoreCase(openmetricsScraperDTO.getTenant())) {
          throw new MonitorException("the tenant parameter is invalid");
        }

        openmetricsScraperDTO.setTenant(ms.getTenant());
        openmetricsScraperDTO.setWorkspace(ms.getWorkspace());
        openmetricsScraperDTO.setId(id);
        openmetricsScraperService.saveByDTO(openmetricsScraperDTO);

        userOpLogService.append("openmetrics_scraper", openmetricsScraperDTO.getId(), OpType.CREATE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(item), J.toJson(openmetricsScraperDTO), null, "openmetrics_scraper_update");

        JsonResult.createSuccessResult(result, openmetricsScraperDTO);
      }
    });

    return result;

  }

  @PostMapping("/delete/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> delete(@PathVariable("id") Long id) {

    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;

        OpenmetricsScraperDTO model =
            openmetricsScraperService.queryById(id, ms.getTenant(), ms.getWorkspace());
        if (model == null || !model.getTenant().equals(ms.getTenant())) {
          JsonResult.fillFailResultTo(result, "can not find the record");
          return;
        }

        boolean b = openmetricsScraperService.removeById(id);

        userOpLogService.append("openmetrics_scraper", id, OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(model), null, null, "openmetrics_scraper_delete");

        JsonResult.createSuccessResult(result, b);
      }
    });

    return result;
  }

}
