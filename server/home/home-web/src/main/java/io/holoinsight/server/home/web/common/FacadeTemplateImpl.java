/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import io.holoinsight.server.home.biz.access.AccessConfigService;
import io.holoinsight.server.home.biz.access.MonitorAccessService;
import io.holoinsight.server.home.biz.access.model.MonitorAccessConfig;
import io.holoinsight.server.home.biz.access.model.MonitorTokenData;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.web.controller.model.open.GrafanaJsonResult;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.web.measure.MeasureStatistician;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jsy1001de
 * @version 1.0: ManageTemplateImpl.java, v 0.1 2022年03月15日 12:23 下午 jinsong.yjs Exp $
 */
@Slf4j
@Service
public class FacadeTemplateImpl implements FacadeTemplate {

  @Autowired
  private MeasureStatistician measureStatistician;

  @Override
  @SuppressWarnings("unchecked")
  public void manage(JsonResult result, ManageCallback callback) {
    // 检测 token 的资源访问权限
    measureStatistician.checkAuth(getToken(), callback.getResourceTenant(),
        callback.getResourceType(), callback.getActionType());
    // 检验参数
    callback.checkParameter();
    // 执行管理方法
    callback.doManage();
    // 计量统计
    measureStatistician.doStat(getToken(), callback.getResourceTenant(), callback.getResourceType(),
        callback.getActionType(), result);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void manage(JsonResult result, ManageCallback callback, String trace) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    try {
      // 检验参数
      callback.checkParameter();
      // 执行管理方法
      callback.doManage();
      // 计量统计
      measureStatistician.doStat(getToken(), callback.getResourceTenant(),
          callback.getResourceType(), callback.getActionType(), result);
    } finally {
      stopWatch.stop();
      log.info(trace + ", clientResult=[" + result.isSuccess() + "], clientCost=["
          + stopWatch.getTime() + "]");
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void manage(GrafanaJsonResult result, ManageCallback callback) {
    // 检验参数
    callback.checkParameter();
    // 执行管理方法
    callback.doManage();
    // 计量统计
    measureStatistician.doStat(getToken(), callback.getResourceTenant(), callback.getResourceType(),
        callback.getActionType(), result);
  }

  @Autowired
  private AccessConfigService accessConfigService;

  @Autowired
  private MonitorAccessService monitorAccessService;

  private MonitorTokenData getToken() {
    try {
      MonitorUser mu = RequestContext.getContext().mu;
      String token = mu == null ? StringUtils.EMPTY : mu.getAuthToken();
      if (StringUtils.isEmpty(token)) {
        return null;
      }
      MonitorAccessConfig monitorAccessConfig =
          accessConfigService.getAccessConfigDOMap().get(token);

      if (monitorAccessConfig != null) {
        MonitorTokenData tokenData = new MonitorTokenData();
        tokenData.setAccessId(monitorAccessConfig.getAccessId());
        tokenData.setAccessKey(monitorAccessConfig.getAccessKey());
        return tokenData;
      }
      return monitorAccessService.check(token);
    } catch (Exception e) {
      log.error("fail to get token", e);
      return null;
    }
  }
}
