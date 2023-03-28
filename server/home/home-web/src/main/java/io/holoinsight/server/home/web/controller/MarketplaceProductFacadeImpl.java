/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.MarketplaceProductService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.dto.MarketplaceProductDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.common.TokenUrls;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jsy1001de
 * @version 1.0: CustomPluginFacadeImpl.java, v 0.1 2022年03月15日 10:25 上午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/marketplace/product")
@TokenUrls("/webapi/marketplace/product/create")
public class MarketplaceProductFacadeImpl extends BaseFacade {

  @Autowired
  private MarketplaceProductService marketplaceProductService;

  @Autowired
  private UserOpLogService userOpLogService;

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Object> update(@RequestBody MarketplaceProductDTO marketplaceProductDTO) {
    final JsonResult<MarketplaceProductDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(marketplaceProductDTO.id, "id");
        ParaCheckUtil.checkParaNotBlank(marketplaceProductDTO.name, "name");
        ParaCheckUtil.checkParaNotNull(marketplaceProductDTO.overview, "overview");
        ParaCheckUtil.checkParaNotBlank(marketplaceProductDTO.configuration, "configuration");
        ParaCheckUtil.checkParaNotNull(marketplaceProductDTO.status, "status");
      }

      @Override
      public void doManage() {

        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          marketplaceProductDTO.setModifier(mu.getLoginName());
        }
        marketplaceProductDTO.setGmtModified(new Date());
        MarketplaceProductDTO update =
            marketplaceProductService.updateByRequest(marketplaceProductDTO);

        assert mu != null;
        userOpLogService.append("marketplace_product", update.getId(), OpType.UPDATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(marketplaceProductDTO),
            J.toJson(update), null, "marketplace_product_update");

      }
    });

    return JsonResult.createSuccessResult(true);
  }

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<MarketplaceProductDTO> save(
      @RequestBody MarketplaceProductDTO marketplaceProductDTO) {
    final JsonResult<MarketplaceProductDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotBlank(marketplaceProductDTO.name, "name");
        ParaCheckUtil.checkParaNotNull(marketplaceProductDTO.overview, "overview");
        ParaCheckUtil.checkParaNotBlank(marketplaceProductDTO.configuration, "configuration");
        ParaCheckUtil.checkParaNotNull(marketplaceProductDTO.status, "status");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu) {
          marketplaceProductDTO.setCreator(mu.getLoginName());
          marketplaceProductDTO.setModifier(mu.getLoginName());
        }
        MarketplaceProductDTO save = marketplaceProductService.create(marketplaceProductDTO);
        JsonResult.createSuccessResult(result, save);

        assert mu != null;
        userOpLogService.append("marketplace_product", save.getId(), OpType.CREATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(marketplaceProductDTO),
            null, null, "marketplace_product_create");

      }
    });

    return result;
  }

  @GetMapping(value = "/queryById/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MarketplaceProductDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<MarketplaceProductDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MarketplaceProductDTO marketplaceProductDTO = marketplaceProductService.findById(id);

        if (null == marketplaceProductDTO) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
              "can not find record:" + id);
        }
        JsonResult.createSuccessResult(result, marketplaceProductDTO);
      }
    });
    return result;
  }

  @GetMapping(value = "/queryByName/{name}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MarketplaceProductDTO>> queryByName(@PathVariable("name") String name) {
    final JsonResult<List<MarketplaceProductDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(name, "name");
      }

      @Override
      public void doManage() {
        List<MarketplaceProductDTO> marketplaceProductDTOs =
            marketplaceProductService.findByMap(Collections.singletonMap("name", name));
        JsonResult.createSuccessResult(result, marketplaceProductDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/listAll")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<MarketplaceProductDTO>> listAll() {
    final JsonResult<List<MarketplaceProductDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        List<MarketplaceProductDTO> marketplaceProductDTOs =
            marketplaceProductService.findByMap(Collections.emptyMap());

        JsonResult.createSuccessResult(result, marketplaceProductDTOs);
      }
    });
    return result;
  }

  @GetMapping(value = "/listAllNames")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<List<String>> listAllNames() {
    final JsonResult<List<String>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        List<MarketplaceProductDTO> marketplaceProductDTOs =
            marketplaceProductService.findByMap(Collections.emptyMap());
        List<String> names = marketplaceProductDTOs == null ? null
            : marketplaceProductDTOs.stream().map(MarketplaceProductDTO::getName)
                .collect(Collectors.toList());
        JsonResult.createSuccessResult(result, names);
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
        MarketplaceProductDTO byId = marketplaceProductService.findById(id);
        if (null == byId) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
              "can not find record:" + id);
        }
        MonitorScope ms = RequestContext.getContext().ms;

        marketplaceProductService.deleteById(id);
        JsonResult.createSuccessResult(result, null);
        userOpLogService.append("marketplace_product", byId.getId(), OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(byId), null, null, "marketplace_product_delete");

      }
    });
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<MarketplaceProductDTO>> pageQuery(
      @RequestBody MonitorPageRequest<MarketplaceProductDTO> customPluginRequest) {
    final JsonResult<MonitorPageResult<MarketplaceProductDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(customPluginRequest.getTarget(), "target");
      }

      @Override
      public void doManage() {
        JsonResult.createSuccessResult(result,
            marketplaceProductService.getListByPage(customPluginRequest));
      }
    });

    return result;
  }
}
