/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import io.holoinsight.server.home.common.util.Debugger;
import io.holoinsight.server.common.J;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.GetLogsRequest;
import com.aliyun.openservices.log.response.GetContextLogsResponse;
import com.aliyun.openservices.log.response.GetLogsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

/**
 * @author zanghaibo
 * @time 2022-08-18 9:58 下午
 */

@Service
@Slf4j
public class SlsLogService {

  public static final int ONCE_SLS_MAX_LINE = 100;

  @Autowired
  private LogClientHolder holder;

  public GetLogsResponse getGetLogsResponse(String project, String logStore, int from, int to,
      String query, int offset, int pageSize, boolean isReverse) throws LogException {
    Client client = holder.getSlsClient();
    GetLogsRequest request =
        new GetLogsRequest(project, logStore, from, to, "", query, offset, pageSize, isReverse);
    GetLogsResponse logsResponse = client.GetLogs(request);
    if (logsResponse != null) {
      Debugger.print("SlsLogService", "Get Log Response:" + J.toJson(logsResponse));
    }
    return logsResponse;
  }

  public GetLogsResponse getGetLogsResponseAll(String project, String logStore, int from, int to,
      String query, int pageSize, int pageNo, long totalCount, boolean isReverse)
      throws LogException {
    // SLS最多只支持返回100条数据，所以需要分批次查询
    int searchNum = Double.valueOf(Math.ceil((double) pageSize / ONCE_SLS_MAX_LINE)).intValue();
    GetLogsResponse logsResponse = null;
    // 只需要查询一次
    if (searchNum == 1) {
      int offset = (pageNo - 1) * pageSize;
      logsResponse =
          getGetLogsResponse(project, logStore, from, to, query, offset, pageSize, isReverse);
      return logsResponse;
    }

    for (int i = 0; i < searchNum; i++) {
      int offset = (pageNo - 1) * pageSize;
      offset = offset + i * ONCE_SLS_MAX_LINE;
      if (offset > totalCount) {
        break;
      }
      if (logsResponse == null) {
        logsResponse = getGetLogsResponse(project, logStore, from, to, query, offset,
            ONCE_SLS_MAX_LINE, isReverse);
      } else {
        GetLogsResponse newLogsResponse = getGetLogsResponse(project, logStore, from, to, query,
            offset, ONCE_SLS_MAX_LINE, isReverse);
        if (newLogsResponse != null && !CollectionUtils.isEmpty(newLogsResponse.GetLogs())) {
          ArrayList<QueriedLog> queriedLogs = logsResponse.GetLogs();
          queriedLogs.addAll(newLogsResponse.GetLogs());
          if (!newLogsResponse.IsCompleted()) {
            // 多个的时候如果有一个未完成 就是整体未完成
            logsResponse.SetProcessStatus(Consts.CONST_RESULT_INCOMPLETE);
          }
        }
      }
    }
    return logsResponse;
  }

  public GetContextLogsResponse getSlsContextLogs(String project, String logstore, String packID,
      String packMeta, int backLines, int forwardLines) throws LogException {
    Client client = holder.getSlsClient();
    GetContextLogsResponse logsResponse =
        client.getContextLogs(project, logstore, packID, packMeta, backLines, forwardLines);
    if (logsResponse != null) {
      log.info("Get Log Context Response:" + J.toJson(logsResponse));
    }
    return logsResponse;
  }

}
