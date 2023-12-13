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
import io.holoinsight.server.home.facade.utils.ExistCheck;
import io.holoinsight.server.home.facade.utils.FieldCheck;
import io.holoinsight.server.home.facade.utils.ParaCheckUtil;
import io.holoinsight.server.home.facade.utils.ReadCheck;
import io.holoinsight.server.home.facade.utils.SecurityMethodCategory;
import io.holoinsight.server.home.facade.utils.UpdateCheck;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

import static io.holoinsight.server.home.facade.utils.SecurityMethodCategory.create;
import static io.holoinsight.server.home.facade.utils.SecurityMethodCategory.query;
import static io.holoinsight.server.home.facade.utils.SecurityMethodCategory.update;

/**
 * @author masaimu
 * @version 2023-11-21 20:30:00
 */
@Slf4j
@Data
public abstract class ApiSecurity implements FieldCheck {


  public void checkRead(String tenant, String workspace) {

    Field[] fields = this.getClass().getDeclaredFields();
    for (Field field : fields) {
      ReadCheck checkAnnotation = field.getAnnotation(ReadCheck.class);
      if (checkAnnotation != null) {
        processField(field, checkAnnotation.value(), query, tenant, workspace);
      }
      ExistCheck existCheckAnnotation = field.getAnnotation(ExistCheck.class);
      if (existCheckAnnotation != null) {
        BaseMapper existBaseMapper = null;
        Object existMapperObj = SpringContext.getBeanByName(existCheckAnnotation.mapper());
        if (existMapperObj instanceof BaseMapper) {
          existBaseMapper = (BaseMapper) existMapperObj;
        }
        checkFieldExist(field, existCheckAnnotation.column(), tenant, workspace, existBaseMapper);
      }
    }
  }

  private void checkFieldExist(Field field, String[] columns, String tenant, String workspace,
      BaseMapper baseMapper) {
    if (baseMapper == null) {
      throwMonitorException("can not find mapper bean", field);
    }
    try {
      Object v = field.get(this);
      if (v == null) {
        return;
      }
      QueryWrapper<?> wrapper = new QueryWrapper<>();
      RequestContextAdapter requestContextAdapter =
          SpringContext.getBean(RequestContextAdapter.class);
      for (String column : columns) {
        if ("tenant".equals(column)) {
          requestContextAdapter.queryWrapperTenantAdapt(wrapper, tenant);
        } else if ("workspace".equals(column)) {
          requestContextAdapter.queryWrapperWorkspaceAdapt(wrapper, workspace);
        } else {
          wrapper.eq(column, v);
        }
      }
      wrapper.last("LIMIT 1");
      Object objFromDB = baseMapper.selectOne(wrapper);
      if (objFromDB == null) {
        throwMonitorException("should exist.", field);
      }
    } catch (IllegalAccessException e) {
      throwMonitorException(e.getMessage());
    }
  }

  public void checkCreate(String tenant, String workspace) {
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field field : fields) {
      CreateCheck checkAnnotation = field.getAnnotation(CreateCheck.class);
      if (checkAnnotation != null) {
        processField(field, checkAnnotation.value(), create, tenant, workspace);
      }
    }
  }

  public void checkUpdate(String tenant, String workspace) {
    Field[] fields = this.getClass().getDeclaredFields();
    for (Field field : fields) {
      UpdateCheck updateAnnotation = field.getAnnotation(UpdateCheck.class);
      if (updateAnnotation != null) {
        processField(field, updateAnnotation.value(), update, tenant, workspace);
      }
    }
  }

  protected void processField(Field field, CheckCategory[] categories,
      SecurityMethodCategory methodCategory, String tenant, String workspace) {
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
          case SQL_FIELD:
            Object sqlFieldObj = field.get(this);
            if (sqlFieldObj instanceof String) {
              String sqlField = (String) sqlFieldObj;
              if (StringUtils.isNotBlank(sqlField) && !ParaCheckUtil.sqlFieldCheck(sqlField)) {
                throwMonitorException("invalid " + field.getName()
                    + ", please use a-z A-Z 0-9 Chinese - _ , . " + sqlField, field);
              }
            }
            break;
          case SQL_NAME:
            Object sqlNameObj = field.get(this);
            if (sqlNameObj instanceof String) {
              String sqlName = (String) sqlNameObj;
              if (StringUtils.isNotBlank(sqlName) && !ParaCheckUtil.sqlNameCheck(sqlName)) {
                throwMonitorException("invalid " + field.getName()
                    + ", please use a-z A-Z 0-9 Chinese - _ , . spaces " + sqlName, field);
              }
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
              case query:
                customCheckRead(field, tenant, workspace);
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
