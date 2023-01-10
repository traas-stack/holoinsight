/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.DisplayMenuService;
import io.holoinsight.server.home.biz.service.IntegrationGeneratedService;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.IntegrationGenerated;
import io.holoinsight.server.home.dal.model.dto.DisplayMenuConfig;
import io.holoinsight.server.home.dal.model.dto.DisplayMenuDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayMenuFacadeImpl.java, v 0.1 2022年12月06日 上午10:34 jinsong.yjs Exp $
 */
@Slf4j
@RestController
@RequestMapping("/webapi/displaymenu")
public class DisplayMenuFacadeImpl extends BaseFacade {

    @Autowired
    private UserOpLogService            userOpLogService;

    @Autowired
    private DisplayMenuService          displayMenuService;

    @Autowired
    private IntegrationGeneratedService integrationGeneratedService;

    //@PostMapping("/update")
    //@ResponseBody
    //@MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
    //public JsonResult<Object> update(@RequestBody DisplayMenu menu) {
    //    final JsonResult<DisplayMenu> result = new JsonResult<>();
    //    facadeTemplate.manage(result, new ManageCallback() {
    //        @Override
    //        public void checkParameter() {
    //            ParaCheckUtil.checkParaNotNull(menu.id, "id");
    //            ParaCheckUtil.checkParaNotBlank(menu.type, "type");
    //            ParaCheckUtil.checkParaNotBlank(menu.config, "config");
    //            ParaCheckUtil.checkParaNotNull(menu.refId, "refId");
    //            ParaCheckUtil.checkParaNotNull(menu.getTenant(), "tenant");
    //            ParaCheckUtil.checkEquals(menu.getTenant(),
    //                RequestContext.getContext().ms.getTenant(), "tenant is illegal");
    //
    //            DisplayMenuDTO item = displayMenuService.queryById(menu.getId(),
    //                RequestContext.getContext().ms.getTenant());
    //
    //            if (null == item) {
    //                throw new MonitorException("cannot find record: " + menu.getId());
    //            }
    //            if (!item.getTenant().equalsIgnoreCase(menu.getTenant())) {
    //                throw new MonitorException("the tenant parameter is invalid");
    //            }
    //        }
    //
    //        @Override
    //        public void doManage() {
    //
    //            MonitorScope ms = RequestContext.getContext().ms;
    //            MonitorUser mu = RequestContext.getContext().mu;
    //
    //            DisplayMenu update = new DisplayMenu();
    //
    //            BeanUtils.copyProperties(menu, update);
    //
    //            if (null != mu) {
    //                update.setModifier(mu.getLoginName());
    //            }
    //            if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
    //                update.setTenant(ms.tenant);
    //            }
    //            update.setGmtModified(new Date());
    //            displayMenuService.updateById(update);
    //
    //            assert mu != null;
    //            userOpLogService.append("display_menu", String.valueOf(menu.getId()), OpType.UPDATE,
    //                mu.getLoginName(), ms.getTenant(), J.toJson(menu), J.toJson(update), null,
    //                "display_menu_update");
    //        }
    //    });
    //
    //    return JsonResult.createSuccessResult(true);
    //}
    //
    //@PostMapping("/create")
    //@ResponseBody
    //@MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
    //public JsonResult<DisplayMenu> save(@RequestBody DisplayMenu menu) {
    //    final JsonResult<DisplayMenu> result = new JsonResult<>();
    //    facadeTemplate.manage(result, new ManageCallback() {
    //        @Override
    //        public void checkParameter() {
    //            ParaCheckUtil.checkParaNotBlank(menu.type, "type");
    //            ParaCheckUtil.checkParaNotBlank(menu.config, "config");
    //            ParaCheckUtil.checkParaNotNull(menu.refId, "refId");
    //        }
    //
    //        @Override
    //        public void doManage() {
    //            MonitorScope ms = RequestContext.getContext().ms;
    //            MonitorUser mu = RequestContext.getContext().mu;
    //            if (null != mu) {
    //                menu.setCreator(mu.getLoginName());
    //                menu.setModifier(mu.getLoginName());
    //            }
    //            if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
    //                menu.setTenant(ms.tenant);
    //            }
    //            menu.setTenant(MonitorCookieUtil.getTenantOrException());
    //            menu.setGmtCreate(new Date());
    //            menu.setGmtModified(new Date());
    //            displayMenuService.save(menu);
    //            JsonResult.createSuccessResult(result, menu);
    //
    //            assert mu != null;
    //            userOpLogService.append("display_menu", String.valueOf(menu.getId()), OpType.CREATE,
    //                mu.getLoginName(), ms.getTenant(), J.toJson(menu), null, null,
    //                "display_menu_create");
    //
    //        }
    //    });
    //
    //    return result;
    //}

    @GetMapping(value = "/query/{id}")
    @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
    public JsonResult<DisplayMenuDTO> queryById(@PathVariable("id") Long id) {
        final JsonResult<DisplayMenuDTO> result = new JsonResult<>();
        facadeTemplate.manage(result, new ManageCallback() {
            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotNull(id, "id");
            }

            @Override
            public void doManage() {
                DisplayMenuDTO menu = displayMenuService.queryById(id,
                    RequestContext.getContext().ms.getTenant());

                if (null == menu) {
                    throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
                        "can not find record");
                }
                JsonResult.createSuccessResult(result, menu);
            }
        });
        return result;
    }

    @GetMapping(value = "/query/{type}/{refId}")
    @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
    public JsonResult<List<DisplayMenuDTO>> queryByRefId(@PathVariable("type") String type,
                                                         @PathVariable("refId") Long refId) {
        final JsonResult<List<DisplayMenuDTO>> result = new JsonResult<>();
        facadeTemplate.manage(result, new ManageCallback() {
            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotNull(type, "type");
                ParaCheckUtil.checkParaNotNull(refId, "refId");
            }

            @Override
            public void doManage() {
                Map<String, Object> columnMap = new HashMap<>();
                columnMap.put("type", type);
                columnMap.put("ref_id", refId);

                List<DisplayMenuDTO> byMap = displayMenuService.findByMap(columnMap);

                if (null == byMap) {
                    throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
                        "can not find record");
                }
                JsonResult.createSuccessResult(result, byMap);
            }
        });
        return result;
    }

    @GetMapping(value = "/query/apm/{name}")
    @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
    public JsonResult<List<DisplayMenuConfig>> queryByRefId(@PathVariable("name") String name) {
        final JsonResult<List<DisplayMenuConfig>> result = new JsonResult<>();
        facadeTemplate.manage(result, new ManageCallback() {
            @Override
            public void checkParameter() {
                ParaCheckUtil.checkParaNotNull(name, "name");
            }

            @Override
            public void doManage() {

                Map<String, Object> apmMenuMap = new HashMap<>();
                apmMenuMap.put("type", "apm");
                apmMenuMap.put("ref_id", -1);
                List<DisplayMenuDTO> byMap = displayMenuService.findByMap(apmMenuMap);

                if (null == byMap) {
                    throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD,
                        "can not find record");
                }

                Map<String, Object> columnMap = new HashMap<>();
                columnMap.put("tenant", RequestContext.getContext().ms.getTenant());
                columnMap.put("name", name);
                columnMap.put("deleted", 0);

                List<IntegrationGenerated> generateds = integrationGeneratedService
                    .listByMap(columnMap);

                if (CollectionUtils.isEmpty(generateds)) {
                    JsonResult.createSuccessResult(result, byMap.get(0).getConfig());
                    return;
                }

                Set<String> itemSets = new HashSet<>();

                generateds.forEach(generated -> {
                    itemSets.add(generated.item);
                });

                try {
                    List<DisplayMenuConfig> menuConfigs = convertApmMenu(byMap.get(0).getConfig(),
                        itemSets);
                    JsonResult.createSuccessResult(result, menuConfigs);
                } catch (Exception e) {
                    log.error("convertApmMenu error", e);
                    JsonResult.createSuccessResult(result, byMap.get(0).getConfig());
                }

            }
        });
        return result;
    }

    private List<DisplayMenuConfig> convertApmMenu(List<DisplayMenuConfig> configs,
                                                   Set<String> itemSets) {

        List<DisplayMenuConfig> menuConfigs = new ArrayList<>();

        for (DisplayMenuConfig config : configs) {
            if (StringUtils.isBlank(config.getShowItem())
                && CollectionUtils.isEmpty(config.getChildren())) {
                menuConfigs.add(config);
            } else if (itemSets.contains(config.getShowItem())) {
                menuConfigs.add(config);
            }

            if (!CollectionUtils.isEmpty(config.getChildren())) {

                DisplayMenuConfig update = new DisplayMenuConfig();
                BeanUtils.copyProperties(config, update);
                update.setChildren(new ArrayList<>());

                for (DisplayMenuConfig child : config.getChildren()) {
                    if (StringUtils.isBlank(child.getShowItem())
                        && CollectionUtils.isEmpty(child.getChildren())) {
                        update.getChildren().add(child);
                    } else if (itemSets.contains(child.getShowItem())) {
                        update.getChildren().add(child);
                    }
                }
                if (!CollectionUtils.isEmpty(update.getChildren())) {
                    menuConfigs.add(update);
                }
            }
        }

        return menuConfigs;
    }

    //@DeleteMapping(value = "/delete/{id}")
    //@MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
    //public JsonResult<Object> deleteById(@PathVariable("id") Long id) {
    //    final JsonResult<Object> result = new JsonResult<>();
    //    facadeTemplate.manage(result, new ManageCallback() {
    //        @Override
    //        public void checkParameter() {
    //            ParaCheckUtil.checkParaNotNull(id, "id");
    //        }
    //
    //        @Override
    //        public void doManage() {
    //
    //            DisplayMenuDTO byId = displayMenuService.queryById(id,
    //                RequestContext.getContext().ms.getTenant());
    //            displayMenuService.removeById(id);
    //            JsonResult.createSuccessResult(result, null);
    //            userOpLogService.append("display_menu", String.valueOf(byId.getId()), OpType.DELETE,
    //                RequestContext.getContext().mu.getLoginName(),
    //                RequestContext.getContext().ms.getTenant(), J.toJson(byId), null, null,
    //                "display_menu_delete");
    //
    //        }
    //    });
    //    return result;
    //}
}