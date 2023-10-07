/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * @author jsy1001de
 * @version 1.0: ManageTemplateImpl.java, v 0.1 2022年03月15日 12:23 下午 jinsong.yjs Exp $
 */
@Slf4j
@Service
public class FacadeTemplateImpl implements FacadeTemplate {

  @Override
  @SuppressWarnings("unchecked")
  public void manage(JsonResult result, ManageCallback callback) {
    try {
      // 检验参数
      callback.checkParameter();
      // 执行管理方法
      callback.doManage();
    } catch (MonitorException e) {
      log.error("MonitorException: " + e.getMessage(), e);
      JsonResult.fillFailResultTo(result, e.getResultCode().getResultCode(), e.getMessage());
    } catch (DuplicateKeyException e) {
      log.error("DuplicateKeyException: " + e.getMessage(), e);
      JsonResult.fillFailResultTo(result, ResultCodeEnum.DUPLICATE_KEY.getResultCode(),
          e.getMessage());
    } catch (DataAccessException e) {
      log.error("DataAccessException: " + e.getMessage(), e);
      JsonResult.fillFailResultTo(result, ResultCodeEnum.DATAACCESS_ERROE.getResultCode(),
          e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("IllegalAccessException: " + e.getMessage(), e);
      JsonResult.fillFailResultTo(result, ResultCodeEnum.PARAMETER_ILLEGAL.getResultCode(),
          e.getMessage());
    } catch (Exception e) {
      log.error("Exception: " + e.getMessage(), e);
      JsonResult.fillFailResultTo(result, ResultCodeEnum.SYSTEM_ERROR.getResultCode(),
          e.getMessage());
    }
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

    } catch (MonitorException e) {
      log.error(trace + ", MonitorException: " + e.getMessage(), e);
      JsonResult.fillFailResultTo(result, ResultCodeEnum.MONITOR_SYSTEM_ERROR.getResultCode(),
          e.getMessage());
    } catch (DataAccessException e) {
      log.error(trace + ", DataAccessException: " + e.getMessage(), e);
      JsonResult.fillFailResultTo(result, ResultCodeEnum.DATAACCESS_ERROE.getResultCode(),
          e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error(trace + ", IllegalAccessException: " + e.getMessage(), e);
      JsonResult.fillFailResultTo(result, ResultCodeEnum.PARAMETER_ILLEGAL.getResultCode(),
          e.getMessage());
    } catch (Exception e) {
      log.error(trace + ", Exception: " + e.getMessage(), e);
      JsonResult.fillFailResultTo(result, ResultCodeEnum.SYSTEM_ERROR.getResultCode(),
          e.getMessage());
    } finally {
      stopWatch.stop();
      log.info(trace + ", clientResult=[" + result.isSuccess() + "], clientCost=["
          + stopWatch.getTime() + "]");
    }
  }
}
