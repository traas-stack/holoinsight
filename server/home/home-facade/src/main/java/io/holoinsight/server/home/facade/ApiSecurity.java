/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.common.service.SpringContext;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.facade.utils.CheckCategory;
import io.holoinsight.server.home.facade.utils.CreateCheck;
import io.holoinsight.server.home.facade.utils.FieldCheck;
import io.holoinsight.server.home.facade.utils.SecurityMethodCategory;
import io.holoinsight.server.home.facade.utils.UpdateCheck;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

import static io.holoinsight.server.home.facade.utils.SecurityMethodCategory.create;
import static io.holoinsight.server.home.facade.utils.SecurityMethodCategory.update;

/**
 * @author masaimu
 * @version 2023-11-21 20:30:00
 */
@Slf4j
@Data
public abstract class ApiSecurity implements FieldCheck {


  public void checkCreate(String tenant, String workspace, String mapper) {
    Object mapperObj = SpringContext.getBeanByName(mapper);
    BaseMapper baseMapper = null;
    if (mapperObj instanceof BaseMapper) {
      baseMapper = (BaseMapper) mapperObj;
    }
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field field : fields) {
      CreateCheck checkAnnotation = field.getAnnotation(CreateCheck.class);
      if (checkAnnotation != null) {
        processField(field, checkAnnotation.value(), create, tenant, workspace, baseMapper);
      }
    }
  }

  public void checkUpdate(String tenant, String workspace, String mapper) {
    Object mapperObj = SpringContext.getBeanByName(mapper);
    BaseMapper baseMapper = null;
    if (mapperObj instanceof BaseMapper) {
      baseMapper = (BaseMapper) mapperObj;
    }
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field field : fields) {
      UpdateCheck updateAnnotation = field.getAnnotation(UpdateCheck.class);
      if (updateAnnotation != null) {
        processField(field, updateAnnotation.value(), update, tenant, workspace, baseMapper);
      }
    }
  }

  protected void processField(Field field, CheckCategory[] categories,
      SecurityMethodCategory methodCategory, String tenant, String workspace,
      BaseMapper baseMapper) {
    if (categories == null) {
      return;
    }
    for (CheckCategory category : categories) {
      try {
        field.setAccessible(true);
        switch (category) {
          case IS_NULL:
            Object nullV = field.get(this);
            if (nullV != null) {
              throwMonitorException("should be empty.", field);
            }
            break;
          case NOT_NULL:
            Object notNullV = field.get(this);
            if (notNullV == null) {
              throwMonitorException("should not be empty.", field);
            }
            break;
          case EXIST:
            if (baseMapper == null) {
              throwMonitorException("can not find mapper bean", field);
            }
            QueryWrapper<?> wrapper = new QueryWrapper<>();
            RequestContextAdapter requestContextAdapter =
                SpringContext.getBean(RequestContextAdapter.class);
            requestContextAdapter.queryWrapperTenantAdapt(wrapper, tenant, workspace);
            Object v = field.get(this);
            wrapper.eq(field.getName(), v);
            wrapper.last("LIMIT 1");
            Object objFromDB = baseMapper.selectOne(wrapper);
            if (objFromDB == null) {
              throwMonitorException("should exist.", field);
            }
            break;
          case CUSTOM:
            switch (methodCategory) {
              case create:
                customCheckCreate(field, tenant, workspace);
                break;
              case update:
                customCheckUpdate(field, tenant, workspace);
                break;
            }
        }
      } catch (IllegalAccessException e) {
        throwMonitorException(e.getMessage());
      }
    }

  }

  protected void throwMonitorException(String cause, Field field) {
    throw new MonitorException("API_SECURITY fail to check " + field.getName() + " for " + cause);
  }

  protected void throwMonitorException(String cause) {
    throw new MonitorException("API_SECURITY fail to check for " + cause);
  }
}
