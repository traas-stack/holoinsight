/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

import io.holoinsight.server.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

/**
 * @author jsy1001de
 * @version 1.0: ManageTemplateImpl.java, v 0.1 2022年03月15日 12:23 下午 jinsong.yjs Exp $
 */
@Slf4j
@Service
public class FacadeTemplateImpl implements FacadeTemplate {

  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  @SuppressWarnings("unchecked")
  public void manage(JsonResult result, ManageCallback callback) {
    String requestId = UUID.randomUUID().toString();

    doManagerWithExceptionHandler(result, new ManageInternalCallback() {

      @Override
      public void call() {
        // 检验参数
        callback.checkParameter();
        // 执行管理方法
        callback.doManage();
      }
    }, new ManageInternalCallback() {
      @Override
      public void call() {
        // nothing
      }
    }, requestId);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void manage(JsonResult result, ManageCallback callback, String trace) {
    doManagerWithExceptionHandler(result, new ManageInternalCallback() {

      @Override
      public void call() {
        // 检验参数
        callback.checkParameter();
        // 执行管理方法
        callback.doManage();
      }
    }, new ManageInternalCallback() {
      @Override
      public void call() {
        // nothing
      }
    }, trace);
  }

  @Override
  public void manageWithTransaction(JsonResult result, ManageCallback callback, String trace) {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      public void doInTransactionWithoutResult(final TransactionStatus status) {
        doManagerWithExceptionHandler(result, new ManageInternalCallback() {

          @Override
          public void call() {
            // 检验参数
            callback.checkParameter();
            // 执行管理方法
            callback.doManage();
          }
        }, new ManageInternalCallback() {
          @Override
          public void call() {
            // rollback
            status.setRollbackOnly();
          }
        }, trace);
      }
    });
  }

  private void doManagerWithExceptionHandler(final JsonResult result,
      ManageInternalCallback manageCallback, ManageInternalCallback exceptionCallback,
      String trace) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    try {
      manageCallback.call();
    } catch (MonitorException e) {
      exceptionCallback.call();

      handleBizException(trace, result, e);
    } catch (DataAccessException e) {
      exceptionCallback.call();

      handleDbException(trace, result, e);
    } catch (IllegalArgumentException e) {
      exceptionCallback.call();

      handleIllegalArgumentException(trace, result, e);
    } catch (Exception e) {
      exceptionCallback.call();

      handleUnclassifiedException(trace, result, e);
    } finally {
      stopWatch.stop();
      log.info(trace + ", clientResult=[" + result.isSuccess() + "], clientCost=["
          + stopWatch.getTime() + "]");
    }
  }

  private void handleBizException(String trace, JsonResult result, MonitorException e) {
    log.error(trace + ", MonitorException: " + e.getMessage(), e);
    JsonResult.fillFailResultTo(result, ResultCodeEnum.MONITOR_SYSTEM_ERROR.getResultCode(),
        e.getMessage());
  }

  private void handleDbException(String trace, JsonResult result, DataAccessException e) {
    log.error(trace + ", DataAccessException: " + e.getMessage(), e);
    JsonResult.fillFailResultTo(result, ResultCodeEnum.DATAACCESS_ERROE.getResultCode(),
        e.getMessage());
  }

  private void handleIllegalArgumentException(String trace, JsonResult result,
      IllegalArgumentException e) {
    log.error(trace + ", IllegalAccessException: " + e.getMessage(), e);
    JsonResult.fillFailResultTo(result, ResultCodeEnum.PARAMETER_ILLEGAL.getResultCode(),
        e.getMessage());
  }

  private void handleUnclassifiedException(String trace, JsonResult result, Exception e) {
    log.error(trace + ", Exception: " + e.getMessage(), e);
    JsonResult.fillFailResultTo(result, ResultCodeEnum.SYSTEM_ERROR.getResultCode(),
        e.getMessage());
  }
}
