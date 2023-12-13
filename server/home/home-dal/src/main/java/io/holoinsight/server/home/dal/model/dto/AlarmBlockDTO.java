/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import io.holoinsight.server.home.common.service.SpringContext;
import io.holoinsight.server.home.facade.ApiSecurity;
import io.holoinsight.server.home.facade.utils.ApiSecurityService;
import io.holoinsight.server.home.facade.utils.CreateCheck;
import io.holoinsight.server.home.facade.utils.ExistCheck;
import io.holoinsight.server.home.facade.utils.ParaCheckUtil;
import io.holoinsight.server.home.facade.utils.UpdateCheck;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Date;

import static io.holoinsight.server.home.facade.utils.CheckCategory.CUSTOM;
import static io.holoinsight.server.home.facade.utils.CheckCategory.IS_NULL;
import static io.holoinsight.server.home.facade.utils.CheckCategory.NOT_NULL;

/**
 * @author wangsiyuan
 * @date 2022/6/15 4:52 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AlarmBlockDTO extends ApiSecurity {
  /**
   * id
   */
  @CreateCheck(IS_NULL)
  @UpdateCheck({NOT_NULL, CUSTOM})
  @ExistCheck(column = {"id", "tenant", "workspace"}, mapper = "alarmBlockMapper")
  private Long id;

  /**
   * 创建时间
   */
  private Date gmtCreate;

  /**
   * 修改时间
   */
  private Date gmtModified;

  /**
   * 创建者
   */
  private String creator;

  /**
   * 修改者
   */
  private String modifier;

  /**
   * 暂停小时
   */
  private int hour;

  /**
   * 暂停分钟
   */
  private int minute;

  /**
   * 告警id
   */
  @CreateCheck(CUSTOM)
  @UpdateCheck(CUSTOM)
  private String uniqueId;

  /**
   * 原因
   */
  private String reason;

  /**
   * 租户id
   */
  @UpdateCheck({NOT_NULL, CUSTOM})
  private String tenant;

  /**
   * workspace
   */
  private String workspace;

  /**
   * 屏蔽维度
   */
  private String tags;

  /**
   * 额外信息
   */
  private String extra;

  /**
   * 开始时间
   */
  private Date startTime;

  /**
   * 结束时间
   */
  private Date endTime;

  @Override
  public void customCheckRead(Field field, String tenant, String workspace) {

  }

  @Override
  public void customCheckUpdate(Field field, String tenant, String workspace) {
    ApiSecurityService apiSecurityService = SpringContext.getBean(ApiSecurityService.class);
    String fieldName = field.getName();
    switch (fieldName) {
      case "uniqueId":
        if (StringUtils.isNotEmpty(this.uniqueId)) {
          ParaCheckUtil.checkParaBoolean(
              apiSecurityService.checkRuleTenantAndWorkspace(this.uniqueId, tenant, workspace),
              "uniqueId do not belong to this tenant or workspace");
        }
        break;
      case "tenant":
        if (!StringUtils.equals(this.tenant, tenant)) {
          throwMonitorException("tenant is illegal");
        }
        break;
    }
  }

  @Override
  public void customCheckCreate(Field field, String tenant, String workspace) {
    ApiSecurityService apiSecurityService = SpringContext.getBean(ApiSecurityService.class);
    String fieldName = field.getName();
    switch (fieldName) {
      case "uniqueId":
        if (StringUtils.isNotEmpty(this.uniqueId)) {
          ParaCheckUtil.checkParaBoolean(
              apiSecurityService.checkRuleTenantAndWorkspace(this.uniqueId, tenant, workspace),
              "uniqueId do not belong to this tenant or workspace");
        }
        break;
    }
  }
}
