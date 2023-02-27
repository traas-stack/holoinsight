/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.service.MetaDictValueService;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.biz.common.SuperCacheService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaDataDictValueFacadeImpl.java, v 0.1 2023年02月27日 下午2:37 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/metadataDictValue")
public class MetaDataDictValueFacadeImpl extends BaseFacade {
  @Autowired
  private MetaDictValueService metaDictValueService;

  @Autowired
  private SuperCacheService superCacheService;

  @GetMapping(value = "/query/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MetaDataDictValue> queryById(@PathVariable("id") Long id) {
    final JsonResult<MetaDataDictValue> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        MetaDataDictValue metaDataDictValue = metaDictValueService.getById(id);

        if (null == metaDataDictValue) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, metaDataDictValue);
      }
    });
    return result;
  }

  @GetMapping(value = "/query/{type}/{key}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MetaDataDictValue> queryByType(@PathVariable("type") String type,
      @PathVariable("key") String key) {
    final JsonResult<MetaDataDictValue> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(type, "type");
        ParaCheckUtil.checkParaNotNull(key, "key");
      }

      @Override
      public void doManage() {
        MetaDataDictValue metaDataDictValue = MetaDictUtil.getMetaData(type, key);

        if (null == metaDataDictValue) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, metaDataDictValue);
      }
    });
    return result;
  }
}
